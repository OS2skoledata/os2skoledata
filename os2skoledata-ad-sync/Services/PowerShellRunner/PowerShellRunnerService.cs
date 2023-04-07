using Microsoft.Extensions.Logging;
using System;
using System.Management.Automation;

namespace os2skoledata_ad_sync.Services.PowerShellRunner
{
    internal class PowerShellRunnerService : ServiceBase<PowerShellRunnerService>
    {
        private readonly string createPowerShell;
        public PowerShellRunnerService(IServiceProvider sp) : base(sp)
        {
            createPowerShell = settings.PowerShellSettings.createPowerShellScript;
        }

        // TODO do we need to pass other fields?
        public void Run(string sAMAccountName, string name)
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
                        "$ppArg2=\"" + name + "\"\n";


                    script += "Invoke-Method -SAMAccountName $ppArg1 -Name $ppArg2\n";

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
}
