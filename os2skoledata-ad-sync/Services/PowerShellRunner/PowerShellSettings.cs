namespace os2skoledata_ad_sync.Services.PowerShellRunner
{
    public class PowerShellSettings
    {
        public string createPowerShellScript { get; set; }
        public string DisablePowerShellScript { get; set; }
        public bool DryRun { get; set; }
        public bool UserAsJSON { get; set; }
    }
}
