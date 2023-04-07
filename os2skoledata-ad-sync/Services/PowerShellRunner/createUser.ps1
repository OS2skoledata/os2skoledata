function Invoke-Method {
	param(
        [string] $SAMAccountName = $(throw "Please specify a sAMAccountName."),
        [string] $Name = $(throw "Please specify a name.")
	)
	
	$result = "Creating " + $SAMAccountName + ", " + $Name

	$result | Out-File 'c:\logs\log.txt'
}