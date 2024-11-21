function Invoke-Method {
	param(
        [string] $SAMAccountName = $(throw "Please specify a sAMAccountName.")
	)
	
	$result = "Disabling " + $SAMAccountName

	$result | Out-File 'c:\logs\log.txt'
}