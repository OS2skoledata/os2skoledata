using System;
using System.Threading.Tasks;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using os2skoledata_apple_school_manager.Config;
using Quartz;

namespace os2skoledata_apple_school_manager.Jobs
{
    public abstract class JobBase<T> : IJob
    {
        protected readonly ILogger<T> logger;
        protected readonly Settings settings;

        public JobBase(IServiceProvider sp)
        {
            logger = sp.GetService<ILogger<T>>();
            settings = sp.GetService<Settings>();
        }

        public abstract Task Execute(IJobExecutionContext context);
    }
}
