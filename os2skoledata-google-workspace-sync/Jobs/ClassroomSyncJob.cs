using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using os2skoledata_google_workspace_sync.Services.OS2skoledata;
using Quartz;
using System;
using System.Threading.Tasks;

namespace os2skoledata_google_workspace_sync.Jobs
{
    internal class ClassroomSyncJob : JobBase<SyncJob>
    {
        private readonly SyncService syncService;

        public ClassroomSyncJob(IServiceProvider sp) : base(sp)
        {
            syncService = sp.GetService<SyncService>();
        }

        public override Task Execute(IJobExecutionContext context)
        {
            try
            {
                if (settings.WorkspaceSettings.ClassroomSettings.Enabled)
                {
                    logger.LogInformation("Executing ClassroomSyncJob");
                    syncService.SyncClassrooms();
                    logger.LogInformation("Finished executing ClassroomSyncJob");
                }
                return Task.CompletedTask;
            }
            catch (Exception e)
            {
                logger.LogError(e, "Failed to execute ClassroomSyncJob");
                return Task.FromException(e);
            }
        }

    }
}
