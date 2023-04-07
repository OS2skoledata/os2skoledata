using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using os2skoledata_ad_sync.Services.OS2skoledata;
using Quartz;
using System;
using System.Threading.Tasks;

namespace os2skoledata_ad_sync.Jobs
{
    class SetFullSyncJob : JobBase<SyncJob>
    {
        private readonly SyncService syncService;

        public SetFullSyncJob(IServiceProvider sp) : base(sp)
        {
            syncService = sp.GetService<SyncService>();
        }

        public override Task Execute(IJobExecutionContext context)
        {
            try
            {
                logger.LogInformation("Executing ResetFullSyncJob");
                syncService.SetFullSync();
                logger.LogInformation("Finished executing ResetFullSyncJob");
                return Task.CompletedTask;
            }
            catch (Exception e)
            {
                logger.LogError(e, "Failed to execute ResetFullSyncJob");
                return Task.FromException(e);
            }
        }

    }
}
