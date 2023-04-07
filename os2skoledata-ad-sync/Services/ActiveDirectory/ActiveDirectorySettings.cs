namespace os2skoledata_ad_sync.Services.ActiveDirectory
{
    public class ActiveDirectorySettings
    {
        public string EmailDomain { get; set; }
        public string DisabledUsersOU { get; set; }
        public string RootOU { get; set; }
        public string RootDeletedOusOu { get; set; }
        public string[] OUsToAlwaysCreate { get; set; }
        public RequiredUserFields requiredUserFields { get; set; }
        public OptionalUserFields optionalUserFields { get; set; }
        public RequiredOUFields requiredOUFields { get; set; }
        public RequiredSecurityGroupFields requiredSecurityGroupFields { get; set; }
        public UsernameSettings usernameSettings { get; set; }
        public FilteringSettings filteringSettings { get; set; }
        public NamingSettings namingSettings { get; set; }
    }

    public enum UsernameStandardType
    {
        AS_UNILOGIN,
        PREFIX_NAME_FIRST,
        PREFIX_NAME_LAST
    }
}
