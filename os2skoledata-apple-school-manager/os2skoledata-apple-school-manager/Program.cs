using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Hosting;
using os2skoledata_apple_school_manager.Config;
using Quartz;
using Serilog;

namespace os2skoledata_apple_school_manager
{
    public class Program
    {
        public static void Main(string[] args)
        {
            CreateHostBuilder(args).Build().Run();
        }

        public static IHostBuilder CreateHostBuilder(string[] args) =>
            Host.CreateDefaultBuilder(args)
                .ConfigureAppConfiguration((hostingContext, config) =>
                {
                    config.AddJsonFile("appsettings.Development.json", optional: true, reloadOnChange: false);
                    config.AddJsonFile("/settings/appsettings.json", optional: true, reloadOnChange: false);
                })
                .UseSerilog()
                .ConfigureServices((hostContext, services) =>
                {
                    // Add dependecies using extension method in Config/DependencyInjection.cs
                    // The CreateDefaultBuilder automatically loads a Configuration based on appsettings.json and appsettings.Development.json into the hostContext.Configuration
                    services.AddDependencies(hostContext.Configuration);

                    // Add quartz jobs using extension method in Config/QuartzConfiguraiton.cs
                    services.AddQuartzJobs();

                    // Quartz has a nice convenience method for hosting the main program
                    services.AddQuartzServer(options =>
                    {
                        // when shutting down we want jobs to complete gracefully
                        options.WaitForJobsToComplete = true;
                    });
                })
                // This last bit adds support for running the program as a Windows Service
                // The final .exe file will be able to be installed as a service using the Windows system program sc.exe
                .UseWindowsService();
    }
}
