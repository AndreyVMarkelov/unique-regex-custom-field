package ru.andreymarkelov.atlas.plugins.ur.manager;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import ru.andreymarkelov.atlas.plugins.ur.model.CFData;

public class UniqueRegexMgrImpl implements UniqueRegexMgr {
    private static final String PLUGIN_KEY = "UNIQUE_REGEX_CF";

    private final PluginSettingsFactory pluginSettingsFactory;

    public UniqueRegexMgrImpl(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public CFData getCFData(String cfKey) {
        return new CFData(
                getStringProperty(cfKey + ".regex"),
                getStringProperty(cfKey + ".regexerror"),
                getStringProperty(cfKey + ".jql"),
                getStringProperty(cfKey + ".uniqueerror"),
                getStringProperty(cfKey + ".target")
        );
    }

    private synchronized String getStringProperty(String key) {
        return (String) pluginSettingsFactory.createSettingsForKey(PLUGIN_KEY).get(key);
    }

    @Override
    public void setCfJql(String cfKey, String jql) {
        setStringProperty(cfKey + ".jql", jql);
    }

    @Override
    public void setCfRegex(String cfKey, String regex) {
        setStringProperty(cfKey + ".regex", regex);
    }

    @Override
    public void setCfRegexError(String cfKey, String regexError) {
        setStringProperty(cfKey + ".regexerror", regexError);
    }

    @Override
    public void setUniqueError(String cfKey, String uniqueError) {
        setStringProperty(cfKey + ".uniqueerror", uniqueError);
    }

    @Override
    public void setCfTarget(String cfKey, String target) {
        setStringProperty(cfKey + ".target", target);
    }

    private synchronized void setStringProperty(String key, String value) {
        pluginSettingsFactory.createSettingsForKey(PLUGIN_KEY).put(key, value);
    }
}
