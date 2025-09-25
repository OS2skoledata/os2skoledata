using System;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using os2skoledata_apple_school_manager.Config;

namespace os2skoledata_apple_school_manager.Services
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
