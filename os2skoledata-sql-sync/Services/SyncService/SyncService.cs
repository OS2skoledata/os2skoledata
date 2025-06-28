using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using os2skoledata_sql_sync.Config;
using os2skoledata_sql_sync.Model;
using os2skoledata_sql_sync.Services.OS2skoledata;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;

namespace os2skoledata_ad_sync.Services.OS2skoledata
{
    internal class SyncService : ServiceBase<SyncService>
    {
        private readonly OS2skoledataService oS2skoledataService;
        private readonly EFContext _context;

        public SyncService(IServiceProvider sp) : base(sp)
        {
            oS2skoledataService = sp.GetService<OS2skoledataService>();
            _context = sp.GetService<EFContext>();
        }

        public void Synchronize()
        {
            try
            {
                logger.LogDebug("Executing FullSyncJob");
                Stopwatch stopWatch = new Stopwatch();
                stopWatch.Start();

                List<InstitutionDTO> institutions = oS2skoledataService.GetEverything();
                foreach (var institutionDTO in institutions)
                {
                    // find institution
                    var dbInstitution = _context.Institutions.Where(i => i.InstitutionNumber.Equals(institutionDTO.institutionNumber)).FirstOrDefault();
                    
                    if (dbInstitution == null)
                    {
                        // add scenario
                        dbInstitution = new Institution();
                        dbInstitution.CopyFields(institutionDTO);

                        logger.LogInformation("Creating new institution: " + institutionDTO.institutionName + " / " + institutionDTO.institutionNumber);

                        _context.Institutions.Add(dbInstitution);
                    } else
                    {
                        // update
                        bool changes = false;

                        if (!dbInstitution.ApiEquals(institutionDTO))
                        {
                            dbInstitution.CopyFields(institutionDTO);
                            changes = true;
                        }

                        if (changes)
                        {
                            logger.LogInformation("Updating core data on institution: " + institutionDTO.institutionName + " / " + institutionDTO.institutionNumber);
                        }
                    }
                }

                // toBedeleted
                foreach (Institution existingInstitution in _context.Institutions)
                {
                    if (institutions.Any(i => i.institutionNumber == existingInstitution.InstitutionNumber) == false)
                    {
                        _context.Institutions.Remove(existingInstitution);
                    }
                }

                _context.SaveChanges();

                oS2skoledataService.SetLastFullSync();

                stopWatch.Stop();
                logger.LogDebug($"Finsihed executing FullSyncJob in {stopWatch.ElapsedMilliseconds / 1000} seconds");
            }
            catch (Exception e)
            {
                oS2skoledataService.ReportError("SQL sync error:\n" + e.Message + "\n" + e.StackTrace);
                logger.LogError(e, "Failed to execute FullSyncJob");
            }
        }
    }
}
