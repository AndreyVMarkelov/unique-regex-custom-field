package ru.andreymarkelov.atlas.plugins.ur.action.admin;

import com.atlassian.jira.security.request.RequestMethod;
import com.atlassian.jira.security.request.SupportedMethods;
import com.atlassian.jira.security.xsrf.RequiresXsrfCheck;
import com.atlassian.jira.web.action.JiraWebActionSupport;

import java.util.regex.Pattern;

import static com.atlassian.jira.permission.GlobalPermissionKey.ADMINISTER;

public class RegexTesterAction extends JiraWebActionSupport {
    private static final String NOT_SET = "NOTSET";

    private String regex;
    private String expression;
    private String matched;

    @Override
    @SupportedMethods({RequestMethod.GET})
    public String doDefault() {
        if (!hasAdminPermission()) {
            return PERMISSION_VIOLATION_RESULT;
        }
        matched = NOT_SET;
        return INPUT;
    }

    @Override
    @SupportedMethods({RequestMethod.POST})
    @RequiresXsrfCheck
    protected String doExecute() {
        if (!hasAdminPermission()) {
            return PERMISSION_VIOLATION_RESULT;
        }

        if (Pattern.compile(regex).matcher(expression).matches()) {
            matched = "OK";
        } else {
            matched = "FAIL";
        }

        return SUCCESS;
    }

    @Override
    protected void doValidation() {
        matched = NOT_SET;

        if (regex == null || regex.isEmpty()) {
            addError("regex", getText("ru.andreymarkelov.atlas.plugins.uniqueregexfield.tester.regex.error.empty"));
        } else {
            try {
                Pattern.compile(regex);
            } catch (Exception ex) {
                addError("regex", getText("ru.andreymarkelov.atlas.plugins.uniqueregexfield.tester.regex.error.invalid"));
            }
        }

        super.doValidation();
    }

    private boolean hasAdminPermission() {
        return getGlobalPermissionManager().hasPermission(ADMINISTER, getLoggedInUser());
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getMatched() {
        return matched;
    }

    public void setMatched(String matched) {
        this.matched = matched;
    }
}
