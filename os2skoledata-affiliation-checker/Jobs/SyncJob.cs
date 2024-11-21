using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using os2skoledata_affiliation_checker.Services.Sync;
using Quartz;
using System;
using System.Diagnostics;
using System.Threading.Tasks;

namespace os2skoledata_affiliation_checker.Jobs
{
    [DisallowConcurrentExecution]
    internal class SyncJob : JobBase<SyncJob>
    {
        private readonly SyncService syncService;

        public SyncJob(IServiceProvider sp) : base(sp)
        {
            syncService = sp.GetService<SyncService>();
        }

        public override Task Execute(IJobExecutionContext context)
        {
            try
            {
                logger.LogDebug("Executing SyncJob");
                Stopwatch stopWatch = new Stopwatch();
                stopWatch.Start();
                syncService.Synchronize();
                stopWatch.Stop();
                logger.LogDebug($"Finsihed executing SyncJob in {stopWatch.ElapsedMilliseconds / 1000} seconds");
                return Task.CompletedTask;
            }
            catch (Exception e)
            {
                logger.LogError(e, "Failed to execute SyncJob");
                return Task.FromException(e);
            }
        }
    }
}