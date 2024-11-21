using Microsoft.Extensions.DependencyInjection;
using os2skoledata_ad_sync.Jobs;
using Quartz;

namespace os2skoledata_ad_sync.Config
{
    public static class QuartzConfiguration
    {
        public static IServiceCollection AddQuartzJobs(this IServiceCollection services)
        {
            var settings = services.BuildServiceProvider().GetService<Settings>();

            // We use Quartz to schedule jobs
            services.AddQuartz(q =>
            {
                q.UseMicrosoftDependencyInjectionJobFactory();

                // Add SyncJob
                var syncJobKey = new JobKey("SyncJob");
                q.AddJob<SyncJob>(syncJobKey, j => j.WithDescription(syncJobKey.Name));

                // add initial trigger
                q.AddTrigger(t => t
                    .WithIdentity("Startup Trigger")
                    .ForJob(syncJobKey)
                    .StartNow()
                );

                // Add scheduled trigger
                q.AddTrigger(t => t
                    .WithIdentity("Sync Cron Trigger")
                    .ForJob(syncJobKey)
                    .WithCronSchedule(settings.JobSettings.DeltaSyncCron)
                );

                // Add SetFullSyncJob
                var setFullSyncJobKey = new JobKey("SetFullSyncJob");
                q.AddJob<SetFullSyncJob>(setFullSyncJobKey, j => j.WithDescription(setFullSyncJobKey.Name));

                // Add scheduled trigger
                q.AddTrigger(t => t
                    .WithIdentity("FullSync Cron Trigger")
                    .ForJob(setFullSyncJobKey)
                    .WithCronSchedule(settings.JobSettings.FullSyncCron)
                );

                // job for uploading log if requested
                if (settings.LogUploaderSettings != null && settings.LogUploaderSettings.Enabled)
                {
                    // Add LogUploaderSyncJob
                    var logUploaderJobKey = new JobKey("LogUploaderSyncJob");
                    q.AddJob<LogUploaderSyncJob>(logUploaderJobKey, j => j.WithDescription(logUploaderJobKey.Name));

                    // Add scheduled trigger every five minutes
                    q.AddTrigger(t => t
                        .WithIdentity("LogUploader Cron Trigger")
                        .ForJob(logUploaderJobKey)
                        .WithSimpleSchedule(b => b.WithIntervalInMinutes(5).RepeatForever())
                    );
                }
                
            });

            return services;
        }
    }
}