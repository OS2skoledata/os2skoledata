using Microsoft.Extensions.DependencyInjection;
using os2skoledata_apple_school_manager.Jobs;
using Quartz;

namespace os2skoledata_apple_school_manager.Config
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
                    .WithIdentity("FullSync Cron Trigger")
                    .ForJob(syncJobKey)
                    .WithCronSchedule(settings.JobSettings.FullSyncCron)
                );
            });

            return services;
        }
    }
}