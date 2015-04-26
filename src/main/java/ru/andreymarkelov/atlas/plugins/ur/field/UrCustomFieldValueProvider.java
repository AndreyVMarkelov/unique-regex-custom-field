package ru.andreymarkelov.atlas.plugins.ur.field;

import com.atlassian.jira.issue.customfields.CustomFieldValueProvider;
import com.atlassian.jira.issue.customfields.MultiSelectCustomFieldValueProvider;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.transport.FieldValuesHolder;

public class UrCustomFieldValueProvider implements CustomFieldValueProvider {
    private final CustomFieldValueProvider customFieldValueProvider = new MultiSelectCustomFieldValueProvider();

    public Object getStringValue(CustomField customField, FieldValuesHolder fieldValuesHolder) {
        return customFieldValueProvider.getStringValue(customField, fieldValuesHolder);
    }

    public Object getValue(CustomField customField, FieldValuesHolder fieldValuesHolder) {
        return getStringValue(customField, fieldValuesHolder);
    }
}
