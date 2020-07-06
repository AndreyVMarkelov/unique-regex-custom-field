package ru.andreymarkelov.atlas.plugins.ur.manager.impl;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import ru.andreymarkelov.atlas.plugins.ur.manager.UniqueSettingsManager;

public class UniqueSettingsManagerImpl implements UniqueSettingsManager {
    private static final String PLUGIN_KEY = "UniqueRegexCFUnique";

    private final PluginSettings pluginSettings;

    public UniqueSettingsManagerImpl(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettings = pluginSettingsFactory.createSettingsForKey(PLUGIN_KEY);
    }
}
