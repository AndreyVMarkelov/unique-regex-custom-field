/*
 * Created by Andrey Markelov 28-10-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins;

/**
 * Manager of unique regex fields.
 * 
 * @author Andrey Markelov
 */
public interface UniqueRegexMgr
{
    /**
     * Get custom field data.
     */
    CFData getCFData(String cfKey);

    /**
     * Set custom field JQL.
     */
    void setCfJql(String cfKey, String jql);

    /**
     * Set custom field regex.
     */
    void setCfRegex(String cfKey, String regex);

    /**
     * Set target compare field.
     */
    void setCfTarget(String cfKey, String target);
}
