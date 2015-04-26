package ru.andreymarkelov.atlas.plugins.ur.utils;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.user.ApplicationUsers;

public class UrUtils {
    public static boolean checkJQL(String jql) {
        if (isEmpty(jql)) {
            return true;
        }

        SearchService searchService = ComponentAccessor.getOSGiComponentInstanceOfType(SearchService.class);
        SearchService.ParseResult parseResult = searchService.parseQuery(ApplicationUsers.toDirectoryUser(ComponentAccessor.getJiraAuthenticationContext().getUser()), jql);
        return parseResult.isValid();
    }

    public static boolean checkRegex(String regex) {
        if (isEmpty(regex)) {
            return true;
        }

        try {
            Pattern.compile(regex);
            return true;
        } catch (PatternSyntaxException ex) {
            return false;
        }
    }

    public static String getCfName(CustomFieldManager cfMgr, String cfKey) {
        CustomField field = cfMgr.getCustomFieldObject(cfKey);
        if (field != null) {
            return field.getName();
        } else {
            return cfKey;
        }
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    private UrUtils() {
    }
}
