package ru.andreymarkelov.atlas.plugins.ur.model;

public class RegexSettings {
    /**
     * Regex for custom field value.
     */
    private final String regex;

    /**
     * Custom error if field value is not satisfied Regex.
     */
    private final String regexError;

    public RegexSettings(String regex, String regexError) {
        this.regex = regex;
        this.regexError = regexError;
    }

    public String getRegex() {
        return regex;
    }

    public String getRegexError() {
        return regexError;
    }

    @Override
    public String toString() {
        return "RegexSettings{" +
                "regex='" + regex + '\'' +
                ", regexError='" + regexError + '\'' +
                '}';
    }
}
