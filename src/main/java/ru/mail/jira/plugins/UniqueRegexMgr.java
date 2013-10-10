package ru.mail.jira.plugins;

public interface UniqueRegexMgr {
    CFData getCFData(String cfKey);

    void setCfJql(String cfKey, String jql);

    void setCfRegex(String cfKey, String regex);

    void setCfRegexError(String cfKey, String regexError);

    void setCfTarget(String cfKey, String target);
}
