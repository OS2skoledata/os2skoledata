using System;
using System.Threading;

namespace os2skoledata_google_workspace_sync.Services.GoogleWorkspace;

public abstract class RetryUtil
{
    private const int MaxRetries = 3;
    private const int DelaySeconds = 10;
    public delegate T RetriedMethod<out T>();

    public static T WithRetry<T>(RetriedMethod<T> cb)
    {
        int attempt = 0;

        while (true)
        {
            try
            {
                attempt++;
                return cb();

                break;
            }
            catch (Exception ex)
            {
                if (attempt >= MaxRetries)
                {
                    throw;
                }

                // logger.LogInformation($"Attempt {attempt} failed. Retrying in {DelaySeconds} seconds...");
                Thread.Sleep(DelaySeconds * 1000);
            }
        }
    }

}