using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using os2skoledata_apple_school_manager.Services.AppleSchoolManager;
using System;
using System.Diagnostics;
using System.Linq;

namespace os2skoledata_apple_school_manager.Services.OS2skoledata
{
    internal class SyncService : ServiceBase<SyncService>
    {
        private readonly AppleSchoolManagerService appleSchoolManagerService;
        private readonly OS2skoledataService os2skoledataService;
        private bool dryRun;
        private string dryRunFilePath;

        public SyncService(IServiceProvider sp) : base(sp)
        {
            appleSchoolManagerService = sp.GetService<AppleSchoolManagerService>();
            os2skoledataService = sp.GetService<OS2skoledataService>();
            dryRun = settings.DryRun;
            dryRunFilePath = settings.DryRunFilePath;
        }

        public void Synchronize()
        {
            try
            {
                logger.LogInformation("Mainflow - Executing Apple School Manager sync");
                Stopwatch stopWatch = new Stopwatch();
                stopWatch.Start();
                var fullLoad = os2skoledataService.GetFullLoad();
                
                if (dryRun)
                {
                    appleSchoolManagerService.SaveZipToLocal(fullLoad, dryRunFilePath);
                }
                else
                {
                    if (fullLoad.Institutions.Where(i => i.Locked).Any())
                    {
                        logger.LogInformation("Mainflow - One or more institutions are locked. Will not send csv files to Apple School Manager");
                        return;
                    }

                    appleSchoolManagerService.ProcessAndUpload(fullLoad);
                }

                os2skoledataService.SetLastFullSync();

                stopWatch.Stop();
                logger.LogInformation($"Mainflow - Finished executing Apple School Manager sync in {stopWatch.ElapsedMilliseconds / 1000} seconds");
            }
            catch (Exception e)
            {
                os2skoledataService.ReportError(e.Message);
                logger.LogError(e, "Failed to execute Apple School Manager sync");
            }
        }
    }
}
