/*
 * Created by Andrey Markelov 28-10-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins;

/**
 * This structure contains unique regex custom field data.
 * 
 * @author Andrey Markelov
 */
public class CFData
{
    /**
     * Custom field ID.
     */
    private String cfKey;

    /**
     * Custom field name.
     */
    private String cfName;

    /**
     * JQL.
     */
    private String jql;

    /**
     * Regex.
     */
    private String regex;

    /**
     * Custom field ID.
     */
    private String targetCf;

    /**
     * Constructor.
     */
    public CFData(
        String regex,
        String jql,
        String targetCf)
    {
        this.regex = regex;
        this.jql = jql;
        this.targetCf = targetCf;
        
    }

    public String getCfKey()
    {
        return cfKey;
    }

    public String getCfName()
    {
        return cfName;
    }

    public String getJql()
    {
        return jql;
    }

    public String getRegex()
    {
        return regex;
    }

    public String getTargetCf()
    {
        if (targetCf != null && targetCf.length() > 0)
        {
            int index = targetCf.indexOf(":");
            if (index > 0)
            {
                return targetCf.substring(0, index);
            }
            else
            {
                return "";
            }
        }
        else
        {
            return "";
        }
    }

    public String getTargetCfName()
    {
        if (targetCf != null && targetCf.length() > 0)
        {
            int index = targetCf.indexOf(":");
            if (index > 0)
            {
                return targetCf.substring(index + 1);
            }
            else
            {
                return "";
            }
        }
        else
        {
            return "";
        }
    }

    public void setCfKey(String cfKey)
    {
        this.cfKey = cfKey;
    }

    public void setCfName(String cfName)
    {
        this.cfName = cfName;
    }

    @Override
    public String toString()
    {
        return "CFData[cfKey=" + cfKey + ", cfName=" + cfName + ", jql=" + jql
            + ", regex=" + regex + ", targetCf=" + targetCf + "]";
    }
}
