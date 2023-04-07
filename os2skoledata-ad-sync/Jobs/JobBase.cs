using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using os2skoledata_ad_sync.Config;
using Quartz;
using System;
using System.Threading.Tasks;

namespace os2skoledata_ad_sync.Jobs
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
