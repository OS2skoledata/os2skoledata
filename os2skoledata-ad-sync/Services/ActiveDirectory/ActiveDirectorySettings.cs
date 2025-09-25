using System.Collections.Generic;

namespace os2skoledata_ad_sync.Services.ActiveDirectory
{
    public class ActiveDirectorySettings
    {
        public string EmailDomain { get; set; }
        public string DisabledUsersOU { get; set; }
        public string RootOU { get; set; }
        public string RootDeletedOusOu { get; set; }
        public string KeepAliveOU { get; set; }
        public bool UseUsernameAsKey { get; set; }
        public string UsernameKeyField { get; set; } = "sAMAccountName";
        public UsernameKeyType UsernameKeyType { get; set; } = UsernameKeyType.SAM_ACCOUNT_NAME;
        public bool UseDanishCharacters { get; set; }
        public bool DryRun { get; set; }
        public bool CreateOUHierarchy { get; set; } = true;
        public Dictionary<string, string> InstitutionUserOUPath { get; set; }
        public string AllSecurityGroupsOU { get; set; }
        public string ADFieldForOS2skoledataMark { get; set; }
        public string[] OUsToAlwaysCreate { get; set; }
        public List<string> UsersToIgnore { get; set; }
        public List<string> UsersToInclude { get; set; }
        public RequiredUserFields requiredUserFields { get; set; }
        public OptionalUserFields optionalUserFields { get; set; }
        public RequiredOUFields requiredOUFields { get; set; }
        public RequiredSecurityGroupFields requiredSecurityGroupFields { get; set; }
        public OptionalSecurityGroupFields optionalSecurityGroupFields { get; set; }
        public UsernameSettings usernameSettings { get; set; }
        public FilteringSettings filteringSettings { get; set; }
        public NamingSettings namingSettings { get; set; }
        public bool MoveUsersEnabled { get; set; }
        public string MultipleCprExcludedGroupDn { get; set; }
        public bool DeleteDisabledUsersFully { get; set; } = false;
        public int DaysBeforeDeletionStudent { get; set; } = 60;
        public int DaysBeforeDeletionEmployee { get; set; } = 60;
        public int DaysBeforeDeletionExternal { get; set; } = 60;
        public bool StudentAndClassGroupsSchoolsOnly { get; set; } = false;
    }

    public enum UsernameStandardType
    {
        FROM_STIL_OR_AS_UNILOGIN,
        AS_UNILOGIN,
        PREFIX_NAME_FIRST,
        PREFIX_NAME_LAST,
        // numbers from 2-9 (0 and 1 is excluded) e.g. 222mad
        THREE_NUMBERS_THREE_CHARS_FROM_NAME,
        FROM_STIL_OR_AS_UNILOGIN_RANDOM,
        // x random chars and y random numbers (0 and 1 and chars; L, l, I, i, o, O are excluded) e.g. yazg4593
        RANDOM
    }

    public enum UsernameKeyType
    {
        SAM_ACCOUNT_NAME,
        UNI_ID
    }
}
