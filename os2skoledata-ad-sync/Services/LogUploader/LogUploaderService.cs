using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Microsoft.Win32.SafeHandles;
using os2skoledata_ad_sync.Services.OS2skoledata;
using System;
using System.IO;
using System.Net.Http;

namespace os2skoledata_ad_sync.Services.LogUploader
{
    public class LogUploaderService : ServiceBase<LogUploaderService>
    {
        private readonly OS2skoledataService oS2skoledataService;
        private readonly bool enabled;
        private readonly string fileShareUrl;
        private readonly string fileShareApiKey;
        private readonly string domain;

        // has to be the same as the path specified in the log section in appsettings.json minus the file name
        private readonly string logFilePathWithoutFileName = "c:/logs/OS2skoledata Active Directory Sync/";
        private readonly string fileNameWithoutDate = "os2skoledata_ad_sync-";

        public LogUploaderService(IServiceProvider sp) : base(sp)
        {
            oS2skoledataService = sp.GetService<OS2skoledataService>();

            enabled = settings.LogUploaderSettings == null ? false : settings.LogUploaderSettings.Enabled;
            fileShareUrl = settings.LogUploaderSettings == null ? null : settings.LogUploaderSettings.FileShareUrl;
            fileShareApiKey = settings.LogUploaderSettings == null ? null : settings.LogUploaderSettings.FileShareApiKey;
            domain = settings.ActiveDirectorySettings.EmailDomain;

            // Check if url ends with "/" if yes then remove it
            if ('/'.Equals(fileShareUrl[fileShareUrl.Length - 1]))
            {
                fileShareUrl = fileShareUrl.Substring(0, fileShareUrl.Length - 1);
            }
        }

        public void CheckForLogRequest()
        {
            if (enabled)
            {
                try
                {
                    bool shouldUploadLog = oS2skoledataService.GetShouldUploadLog();
                    if (shouldUploadLog)
                    {
                        logger.LogInformation("Logfile has been requested. Will attempt to upload logfile for today.");
                        string dateForLogFile = FindDateForLogFile();
                        string fullLogFilePath = logFilePathWithoutFileName + fileNameWithoutDate + dateForLogFile + ".log";
                        FileStream stream = File.Open(fullLogFilePath, FileMode.Open, FileAccess.Read, FileShare.ReadWrite);
                        UploadFile(domain + "_log_" + dateForLogFile + ".log", stream);
                    }
                } catch (Exception ex)
                {
                    logger.LogError(ex, "Failed to upload requested logfile");
                    oS2skoledataService.ReportError("Failed to upload requested logfile.\n" + ex.Message + "\n" + ex.StackTrace);
                }
            }
        }

        private static string FindDateForLogFile()
        {
            DateTime today = DateTime.Today;
            string formattedDate = today.ToString("yyyyMMdd");
            return formattedDate;
        }

        public void UploadFile(string fileName, Stream fileStream)
        {

            byte[] payload = null;
            using (var memoryStream = new MemoryStream())
            {
                fileStream.CopyTo(memoryStream);
                payload = memoryStream.ToArray();
            }

            var client = new HttpClient();
            HttpRequestMessage request = new HttpRequestMessage(HttpMethod.Post, $"{fileShareUrl}/files?name={fileName}");
            request.Headers.Add("ApiKey", fileShareApiKey);
            request.Content = new ByteArrayContent(payload);

            var response = client.SendAsync(request).Result;
            if (response.IsSuccessStatusCode)
            {
                logger.LogInformation("Upload file " + fileName + " with HTTP status: " + response.StatusCode);
            }
            else
            {
                logger.LogWarning("Upload failed for " + fileName + " with HTTP status: " + response.StatusCode);
            }
        }
    }
}
