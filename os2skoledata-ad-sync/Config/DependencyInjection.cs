using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using os2skoledata_ad_sync.Services.ActiveDirectory;
using os2skoledata_ad_sync.Services.LogUploader;
using os2skoledata_ad_sync.Services.OS2skoledata;
using os2skoledata_ad_sync.Services.PowerShellRunner;
using Serilog;
using System;
using System.Net.Http;

namespace os2skoledata_ad_sync.Config
{
    public static class DependencyInjection
    {
        public static IServiceCollection AddDependencies(this IServiceCollection services, IConfiguration configuration)
        {
            Log.Logger = new LoggerConfiguration()
                .ReadFrom.Configuration(configuration)
                .CreateLogger();

            // bind settings
            var settings = new Settings();
            configuration.Bind(settings);
            services.AddSingleton(settings);

            // add other required services
            services.AddSingleton<ActiveDirectoryService>();
            services.AddSingleton<SyncService>();
            services.AddSingleton<PowerShellRunnerService>();
            services.AddSingleton<PAMService>();
            services.AddSingleton<LogUploaderService>();

            services.AddTransient<ApiKeyHandler>();

            services.AddHttpClient<OS2skoledataService>()
            .ConfigurePrimaryHttpMessageHandler(() =>
            {
                var handler = new HttpClientHandler
                {
                    ClientCertificateOptions = ClientCertificateOption.Manual
                };
                handler.ServerCertificateCustomValidationCallback =
                    (httpRequestMessage, cert, certChain, policyErrors) => true;
                return handler;
            })
            .AddHttpMessageHandler<ApiKeyHandler>();


            return services;
        }

    }
}
