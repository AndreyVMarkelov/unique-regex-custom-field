package ru.andreymarkelov.atlas.plugins.ur.field;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigItemType;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.velocity.VelocityManager;
import ru.andreymarkelov.atlas.plugins.ur.manager.RegexSettingsManager;

import java.util.HashMap;
import java.util.Map;

public class RegexCFConfig implements FieldConfigItemType {
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final VelocityManager velocityManager;
    private final RegexSettingsManager regexSettingsManager;

    public RegexCFConfig(
            JiraAuthenticationContext jiraAuthenticationContext,
            VelocityManager velocityManager,
            RegexSettingsManager regexSettingsManager) {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.velocityManager = velocityManager;
        this.regexSettingsManager = regexSettingsManager;
    }

    @Override
    public String getDisplayName() {
        return "Regex Configuration";
    }

    @Override
    public String getDisplayNameKey() {
        return null;
    }

    @Override
    public String getViewHtml(FieldConfig fieldConfig, FieldLayoutItem fieldLayoutItem) {
        return null;
    }

    @Override
    public String getObjectKey() {
        return "RegexCFConfig";
    }

    @Override
    public Object getConfigurationObject(Issue issue, FieldConfig fieldConfig) {
        Map<String, Object> configurationObject = new HashMap<>();
        return configurationObject;
    }

    @Override
    public String getBaseEditUrl() {
        return "UniqueRegexCFRegexConfigAction.jspa";
    }
}
