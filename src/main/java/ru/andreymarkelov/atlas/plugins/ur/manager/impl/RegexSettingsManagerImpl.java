package ru.andreymarkelov.atlas.plugins.ur.manager.impl;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import ru.andreymarkelov.atlas.plugins.ur.manager.RegexSettingsManager;

public class RegexSettingsManagerImpl implements RegexSettingsManager {
    private static final String PLUGIN_KEY = "UniqueRegexCFRegex";

    private final PluginSettings pluginSettings;

    public RegexSettingsManagerImpl(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettings = pluginSettingsFactory.createSettingsForKey(PLUGIN_KEY);
    }
}
