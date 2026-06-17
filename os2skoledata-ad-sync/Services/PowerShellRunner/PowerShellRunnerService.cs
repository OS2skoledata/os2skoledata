using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using os2skoledata_ad_sync.Services.OS2skoledata.Model;
using System;
using System.Management.Automation;

namespace os2skoledata_ad_sync.Services.PowerShellRunner
{
    internal class PowerShellRunnerService : ServiceBase<PowerShellRunnerService>
    {
        private readonly string createPowerShell;
        private readonly string disablePowerShell;
        private readonly string updatePowerShell;
        private readonly bool dryRun;
        private readonly bool userAsJSON;
        public PowerShellRunnerService(IServiceProvider sp) : base(sp)
        {
            createPowerShell = settings.PowerShellSettings.createPowerShellScript;
            disablePowerShell = settings.PowerShellSettings.DisablePowerShellScript;
            updatePowerShell = settings.PowerShellSettings.UpdatePowerShellScript;
            dryRun = settings.PowerShellSettings.DryRun;
            userAsJSON = settings.PowerShellSettings.UserAsJSON;
        }

        public void RunCreateScript(string sAMAccountName, string name, string userRole, string domainController, User user)
        {
            if (dryRun)
            {
                logger.LogInformation($"DryRun: would have executed script after creating or reactivating user");
            }
            else
            {
                logger.LogInformation("Running script after creating or reactivating user");
                try
                {
                    string script = System.IO.File.ReadAllText(createPowerShell);

                    if (!string.IsNullOrEmpty(script))
                    {
                        using PowerShell powershell = PowerShell.Create();
                        script = script + "\n\n" +
                            "$ppArg1=\"" + sAMAccountName + "\"\n" +
                            "$ppArg2=\"" + name + "\"\n" +
                            "$ppArg3=\"" + userRole + "\"\n" +
                            "$ppArg4=\"" + domainController + "\"\n";

                        if (userAsJSON)
                        {
                            string userAsJSON = JsonConvert.SerializeObject(user);
                            script += "$ppArg5= '" + userAsJSON + "'\n";
                            script += "Invoke-Method -SAMAccountName $ppArg1 -Name $ppArg2 -UserRole $ppArg3 -DomainController $ppArg4 -UserAsJson $ppArg5\n";
                        }
                        else
                        {
                            script += "Invoke-Method -SAMAccountName $ppArg1 -Name $ppArg2 -UserRole $ppArg3 -DomainController $ppArg4\n";
                        }

                        powershell.AddScript(script);
                        powershell.Invoke();
                    }
                }
                catch (Exception ex)
                {
                    logger.LogError(ex, "Failed to run powershell script: " + createPowerShell);
                }
            }
        }

        public void RunUpdateScript(string sAMAccountName, string name, string userRole, string domainController, User user, string oldDN, string newDN)
        {
            if (dryRun)
            {
                logger.LogInformation($"DryRun: would have executed script after updating or moving user");
            }
            else
            {
                logger.LogInformation("Running script after updating or moving user");
                try
                {
                    string script = System.IO.File.ReadAllText(updatePowerShell);

                    if (!string.IsNullOrEmpty(script))
                    {
                        using PowerShell powershell = PowerShell.Create();
                        script = script + "\n\n" +
                            "$ppArg1=\"" + sAMAccountName + "\"\n" +
                            "$ppArg2=\"" + name + "\"\n" +
                            "$ppArg3=\"" + userRole + "\"\n" +
                            "$ppArg4=\"" + domainController + "\"\n" +
                            "$ppArg5=\"" + oldDN + "\"\n" +
                            "$ppArg6=\"" + newDN + "\"\n";

                        if (userAsJSON)
                        {
                            string userAsJSON = JsonConvert.SerializeObject(user);
                            script += "$ppArg7= '" + userAsJSON + "'\n";
                            script += "Invoke-Method -SAMAccountName $ppArg1 -Name $ppArg2 -UserRole $ppArg3 -DomainController $ppArg4 -OldDN $ppArg5 -NewDN $ppArg6 -UserAsJson $ppArg7\n";
                        }
                        else
                        {
                            script += "Invoke-Method -SAMAccountName $ppArg1 -Name $ppArg2 -UserRole $ppArg3 -DomainController $ppArg4 -OldDN $ppArg5 -NewDN $ppArg6\n";
                        }

                        powershell.AddScript(script);
                        powershell.Invoke();
                    }
                }
                catch (Exception ex)
                {
                    logger.LogError(ex, "Failed to run powershell script: " + updatePowerShell);
                }
            }
        }

        public void DisableScript(string sAMAccountName)
        {
            if (dryRun)
            {
                logger.LogInformation($"DryRun: would have executed script after disabling user");
            }
            else
            {
                logger.LogInformation("Running script after disabling user");
                try
                {
                    string script = System.IO.File.ReadAllText(disablePowerShell);

                    if (!string.IsNullOrEmpty(script))
                    {
                        using PowerShell powershell = PowerShell.Create();
                        script = script + "\n\n" +
                            "$ppArg1=\"" + sAMAccountName + "\"\n";


                        script += "Invoke-Method -SAMAccountName $ppArg1\n";

                        powershell.AddScript(script);
                        powershell.Invoke();
                    }
                }
                catch (Exception ex)
                {
                    logger.LogError(ex, "Failed to run powershell script: " + disablePowerShell);
                }
            }
        }
    }
}
