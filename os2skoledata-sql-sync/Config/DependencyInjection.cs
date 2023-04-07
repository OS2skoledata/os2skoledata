using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using os2skoledata_ad_sync.Services.OS2skoledata;
using os2skoledata_sql_sync.Config;
using Serilog;

namespace os2skoledata_ad_sync.Config
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
            services.AddSingleton<OS2skoledataService>();
            services.AddSingleton<SyncService>();
            services.AddSingleton<EFContext>();

            return services;
        }

    }
}
