package ru.mail.jira.plugins;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;

public class UrUtils {
    public static boolean checkJQL(String jql) {
        SearchService searchService = ComponentAccessor.getOSGiComponentInstanceOfType(SearchService.class);
        SearchService.ParseResult parseResult = searchService.parseQuery(ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser(), jql);
        return parseResult.isValid();
    }

    public static boolean checkRegex(String regex) {
        try {
            Pattern.compile(regex);
            return true;
        } catch (PatternSyntaxException ex) {
            return false;
        }
    }

    private UrUtils() {
    }
}
