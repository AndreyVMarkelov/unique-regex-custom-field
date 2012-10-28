/*
 * Created by Andrey Markelov 28-10-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

/**
 * Implementation of <code>UniqueRegexMgr</code>.
 * 
 * @author Andrey Markelov
 */
public class UniqueRegexMgrImpl
    implements UniqueRegexMgr
{
    /**
     * PlugIn key.
     */
    private static final String PLUGIN_KEY = "UNIQUE_REGEX_CF";

    /**
     * Plugin settings factory.
     */
    private final PluginSettingsFactory pluginSettingsFactory;

    /**
     * Constructor.
     */
    public UniqueRegexMgrImpl(
        PluginSettingsFactory pluginSettingsFactory)
    {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public CFData getCFData(String cfKey)
    {
        return new CFData(
            getStringProperty(cfKey + ".regex"),
            getStringProperty(cfKey + ".jql"),
            getStringProperty(cfKey + ".target"));
    }

    private synchronized String getStringProperty(String key)
    {
        return (String) pluginSettingsFactory.createSettingsForKey(PLUGIN_KEY).get(key);
    }

    @Override
    public void setCfJql(String cfKey, String jql)
    {
        setStringProperty(cfKey + ".jql", jql);
    }

    @Override
    public void setCfRegex(String cfKey, String regex)
    {
        setStringProperty(cfKey + ".regex", regex);
    }

    @Override
    public void setCfTarget(String cfKey, String target)
    {
        setStringProperty(cfKey + ".target", target);
    }

    private synchronized void setStringProperty(String key, String value)
    {
        pluginSettingsFactory.createSettingsForKey(PLUGIN_KEY).put(key, value);
    }
}
