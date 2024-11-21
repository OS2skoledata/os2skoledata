using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using os2skoledata_affiliation_checker.Config;
using System;

namespace os2skoledata_affiliation_checker.Services
{
    public abstract class ServiceBase<T>
    {
        protected readonly ILogger<T> logger;
        protected readonly Settings settings;

        public ServiceBase(IServiceProvider sp)
        {
            logger = sp.GetService<ILogger<T>>();
            settings = sp.GetService<Settings>();
        }
    }
}
