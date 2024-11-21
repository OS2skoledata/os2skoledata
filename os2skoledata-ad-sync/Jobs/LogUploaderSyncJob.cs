using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using os2skoledata_ad_sync.Services.LogUploader;
using Quartz;
using System;
using System.Threading.Tasks;

namespace os2skoledata_ad_sync.Jobs
{
    class LogUploaderSyncJob : JobBase<LogUploaderSyncJob>
    {
        private readonly LogUploaderService logUploaderService;

        public LogUploaderSyncJob(IServiceProvider sp) : base(sp)
        {
            logUploaderService = sp.GetService<LogUploaderService>();
        }

        public override Task Execute(IJobExecutionContext context)
        {
            try
            {
                logger.LogInformation("Executing LogUploaderSyncJob");
                logUploaderService.CheckForLogRequest();
                logger.LogInformation("Finished executing LogUploaderSyncJob");
                return Task.CompletedTask;
            }
            catch (Exception e)
            {
                logger.LogError(e, "Failed to execute LogUploaderSyncJob");
                return Task.FromException(e);
            }
        }

    }
}
