function Invoke-Method {
	param(
        [string] $SAMAccountName = $(throw "Please specify a sAMAccountName."),
        [string] $Name = $(throw "Please specify a name."),
        [string] $UserRole = $(throw "Please specify a UserRole."), #STUDENT,EMPLOYEE,EXTERNAL
        [string] $DomainController = $(throw "Please specify a DomainController.")#,
		# if you want to include the user as json, you need to set "UserAsJSON":  true in appsettings.json under PowerShellSettings
		#[string] $UserAsJson = $(throw "Please specify a UserAsJson.")
	)
	
	$result = "Creating " + $SAMAccountName + ", " + $Name + ", " + $UserRole + ", " + $DomainController

	$result | Out-File 'c:\logs\log.txt'
}