package ru.andreymarkelov.atlas.plugins.ur.action.field;

import com.atlassian.jira.config.managedconfiguration.ManagedConfigurationItemService;
import com.atlassian.jira.web.action.admin.customfields.AbstractEditConfigurationItemAction;

public class UniqueRegexCFRegexConfigAction extends AbstractEditConfigurationItemAction {
    public UniqueRegexCFRegexConfigAction(ManagedConfigurationItemService managedConfigurationItemService) {
        super(managedConfigurationItemService);
    }
}
