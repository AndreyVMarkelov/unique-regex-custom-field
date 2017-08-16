package ru.andreymarkelov.atlas.plugins.ur.utils;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class UrUtils {
    public static boolean checkJQL(String jql) {
        if (isBlank(jql)) {
            return true;
        }
        SearchService searchService = ComponentAccessor.getOSGiComponentInstanceOfType(SearchService.class);
        SearchService.ParseResult parseResult = searchService.parseQuery(ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser(), jql);
        return parseResult.isValid();
    }

    public static boolean checkRegex(String regex) {
        if (isBlank(regex)) {
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

    private UrUtils() {
    }
}
