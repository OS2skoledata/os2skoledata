package dk.digitalidentity.config.modules;

public enum UsernameStandard {
	FROM_STIL_OR_AS_UNILOGIN,
	FROM_STIL_OR_AS_UNILOGIN_RANDOM,
	AS_UNILOGIN,
	PREFIX_NAME_FIRST,
	PREFIX_NAME_LAST,
	// numbers from 2-9 (0 and 1 is excluded) e.g. 222mad
	THREE_NUMBERS_THREE_CHARS_FROM_NAME
}
