using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using os2skoledata_affiliation_checker.Services.ActiveDirectory;
using os2skoledata_affiliation_checker.Services.OS2skoledata;
using os2skoledata_affiliation_checker.Services.OS2skoledata.Model;
using os2skoledata_affiliation_checker.Services.Sofd;
using System;
using System.Collections.Generic;
using System.Data.Common;
using System.Data.SqlClient;
using System.Diagnostics;
using System.Linq;
using System.Xml.Linq;

namespace os2skoledata_affiliation_checker.Services.Sync
{
    internal class SyncService : ServiceBase<SyncService>
    {
        private readonly ActiveDirectoryService activeDirectoryService;
        private readonly OS2skoledataService oS2skoledataService;
        private readonly SofdService sofdService;
        private readonly string activeEmployeesSecurityGroupDN;
        private readonly string inactiveEmployeesSecurityGroupDN;

        public SyncService(IServiceProvider sp) : base(sp)
        {
            activeDirectoryService = sp.GetService<ActiveDirectoryService>();
            oS2skoledataService = sp.GetService<OS2skoledataService>();
            sofdService = sp.GetService<SofdService>();

            activeEmployeesSecurityGroupDN = settings.ActiveDirectorySettings.ActiveEmployeesSecurityGroupDN;
            inactiveEmployeesSecurityGroupDN = settings.ActiveDirectorySettings.InactiveEmployeesSecurityGroupDN;
        }

        public void Synchronize()
        {
            try
            {
                logger.LogDebug("Executing FullSyncJob");
                Stopwatch stopWatch = new Stopwatch();
                stopWatch.Start();

                List<ActiveAffiliationsRequest> activeAffiliationsRequests = new List<ActiveAffiliationsRequest>();

                if (settings.SyncSettings.FetchDataFrom == FetchFrom.OPUS)
                {
                    // parse XML file - extract CPR numbers
                    activeAffiliationsRequests = GetPeopleFromOpus();
                    

                } else if (settings.SyncSettings.FetchDataFrom == FetchFrom.SQL)
                {
                    activeAffiliationsRequests = GetPeopleFromSQL();
                } else if (settings.SyncSettings.FetchDataFrom == FetchFrom.SOFD)
                {
                    activeAffiliationsRequests = GetPeopleFromSOFD();
                }

                if (activeAffiliationsRequests.Count == 0)
                {
                    throw new Exception($"Could not find any active affiliations via {settings.SyncSettings.FetchDataFrom}!");
                }

                // fetch all employees from AD
                var allEmployees = activeDirectoryService.FetchAllEmployeesFromAD();
                if (allEmployees.Count == 0)
                {
                    throw new Exception("Found 0 employees in Active Directory");
                }

                // the XML file from OPUS might contain CPRs on non-employees (like students),
                // so we filter the set of validCPRs with information from AD about employee AD accounts
                HashSet<string> toRemove = new HashSet<string>();
                foreach (string cpr in activeAffiliationsRequests.Select(a => a.Cpr).ToList())
                {
                    bool found = false;

                    foreach (string key in allEmployees.Keys)
                    {
                        if (string.Equals(key, cpr))
                        {
                            found = true;
                            break;
                        }
                    }

                    if (!found)
                    {
                        toRemove.Add(cpr);
                    }
                }

                activeAffiliationsRequests = activeAffiliationsRequests.Where(a => !toRemove.Contains(a.Cpr)).ToList();
                List<string> activeCprs = activeAffiliationsRequests.Select(a => a.Cpr).ToList();
                List<string> inactiveCprs = new List<string>();

                // find inactive users from employee group
                foreach (string key in allEmployees.Keys)
                {
                    if (!activeCprs.Contains(key))
                    {
                        inactiveCprs.Add(key);
                    }
                }

                // send active affiliations to OS2skoledata
                oS2skoledataService.SendAffiliations(activeAffiliationsRequests);

                // update groups if configured
                activeDirectoryService.handleGroup(activeCprs, activeEmployeesSecurityGroupDN, "activeEmployeesSecurityGroup");
                activeDirectoryService.handleGroup(inactiveCprs, inactiveEmployeesSecurityGroupDN, "inactiveEmployeesSecurityGroupDN");

                stopWatch.Stop();
                logger.LogDebug($"Finsihed executing FullSyncJob in {stopWatch.ElapsedMilliseconds / 1000} seconds");
            }
            catch (Exception e)
            {
                oS2skoledataService.ReportError(e.Message);
                logger.LogError(e, "Failed to execute FullSyncJob");
            }
        }

        private List<ActiveAffiliationsRequest> GetPeopleFromSOFD()
        {
            var affiliations = new List<ActiveAffiliationsRequest>();
            var employees = sofdService.GetPersons();

            foreach (var employee in employees)
            {
                if (employee.Affiliations != null && employee.Affiliations.Count != 0)
                {
                    foreach (var affiliation in employee.Affiliations)
                    {
                        if (IsAffiliationActive(affiliation.EmployeeId, affiliation.StartDate, affiliation.StopDate, employee.Cpr))
                        {
                            ActiveAffiliationsRequest activeAffiliationsRequest = new ActiveAffiliationsRequest();
                            activeAffiliationsRequest.EmployeeNumber = affiliation.EmployeeId;
                            activeAffiliationsRequest.Cpr = employee.Cpr;
                            activeAffiliationsRequest.StartDate = affiliation.StartDate;
                            activeAffiliationsRequest.StopDate = affiliation.StopDate;
                            affiliations.Add(activeAffiliationsRequest);
                        }
                    }
                }
            }

            return affiliations;
        }

        private List<ActiveAffiliationsRequest> GetPeopleFromSQL()
        {
            var affiliations = new List<ActiveAffiliationsRequest>();
            using DbConnection connection = new SqlConnection(settings.SyncSettings.SQLConnectionString);
            connection.Open();

            using DbCommand command = new SqlCommand(settings.SyncSettings.SQLStatement, (SqlConnection)connection);
            using (DbDataReader reader = command.ExecuteReader())
            {
                while (reader.Read())
                {
                    string employeeId = (string)reader["employee_id"];
                    DateTime? entryDate = reader["start_date"] == DBNull.Value ? (DateTime?)null : (DateTime?)reader["start_date"];
                    DateTime? leaveDate = reader["stop_date"] == DBNull.Value ? (DateTime?)null : (DateTime?)reader["stop_date"];
                    string cpr = (string)reader["cpr"];

                    if (IsAffiliationActive(employeeId, entryDate, leaveDate, cpr))
                    {
                        ActiveAffiliationsRequest affiliation = new ActiveAffiliationsRequest();
                        affiliation.EmployeeNumber = employeeId;
                        affiliation.Cpr = cpr;
                        affiliation.StartDate = entryDate;
                        affiliation.StopDate = leaveDate;
                        affiliations.Add(affiliation);
                    }
                }
            }

            connection.Close();

            return affiliations;
        }

        private List<ActiveAffiliationsRequest> GetPeopleFromOpus()
        {
            var affiliations = new List<ActiveAffiliationsRequest>();

            string filePath = settings.SyncSettings.OpusWagesFile;
            XDocument doc = XDocument.Load(filePath);
            var employees = doc.Descendants("employee");

            foreach (var employee in employees)
            {
                // avoid self-closing <employee/> elements because they are no longer employed
                if (employee.HasElements)
                {
                    string employeeId = employee.Attribute("id").Value;
                    string entryDateStr = employee.Element("entryDate")?.Value;
                    string leaveDateStr = employee.Element("leaveDate")?.Value;
                    string cpr = employee.Element("cpr").Value;

                    DateTime? entryDate = string.IsNullOrEmpty(entryDateStr) ? null : DateTime.Parse(entryDateStr);
                    DateTime? leaveDate = string.IsNullOrEmpty(leaveDateStr) ? null : DateTime.Parse(leaveDateStr);


                    if (IsAffiliationActive(employeeId, entryDate, leaveDate, cpr))
                    {
                        ActiveAffiliationsRequest affiliation = new ActiveAffiliationsRequest();
                        affiliation.EmployeeNumber = employeeId;
                        affiliation.Cpr = cpr;
                        affiliation.StartDate = entryDate;
                        affiliation.StopDate = leaveDate;
                        affiliations.Add(affiliation);
                    }
                }
            }

            return affiliations;
        }

        private bool IsAffiliationActive(string employeeId, DateTime? entryDate, DateTime? leaveDate, string cpr)
        {
            if (string.IsNullOrEmpty(employeeId) || string.IsNullOrEmpty(cpr)) {
                return false;
            }

            if (entryDate == null && leaveDate == null)
            {
                return true;
            }

            DateTime now = DateTime.Now;

            if (entryDate != null && leaveDate == null)
            {
                if (entryDate <= now)
                {
                    return true;
                } else
                {
                    return false;
                }
            }  
            else if (entryDate == null && leaveDate != null)
            {
                if (leaveDate >= now)
                {
                    return true;
                } else 
                { 
                    return false; 
                }
            }
            else
            {
                if (entryDate <= now && leaveDate >= now)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }

        }
    }
}
