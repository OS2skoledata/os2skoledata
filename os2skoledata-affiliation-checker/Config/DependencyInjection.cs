using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using os2skoledata_affiliation_checker.Services.ActiveDirectory;
using os2skoledata_affiliation_checker.Services.OS2skoledata;
using os2skoledata_affiliation_checker.Services.Sofd;
using os2skoledata_affiliation_checker.Services.Sync;
using Serilog;

namespace os2skoledata_affiliation_checker.Config
{
    public static class DependencyInjection
    {
        public static IServiceCollection AddDependencies(this IServiceCollection services, IConfiguration configuration)
        {
            Log.Logger = new LoggerConfiguration()
                .ReadFrom.Configuration(configuration)
                .CreateLogger();

            // Bind settings
            var settings = new Settings();
            configuration.Bind(settings);
            services.AddSingleton(settings);

            // Add other required services
            services.AddSingleton<ActiveDirectoryService>();
            services.AddSingleton<OS2skoledataService>();
            services.AddSingleton<SofdService>();
            services.AddSingleton<SyncService>();

            return services;
        }

    }
}
