package ru.andreymarkelov.atlas.plugins.ur.action;

import com.atlassian.jira.security.xsrf.RequiresXsrfCheck;
import com.atlassian.jira.web.action.JiraWebActionSupport;

import static com.atlassian.jira.permission.GlobalPermissionKey.ADMINISTER;

public class UniqueRegexTester extends JiraWebActionSupport {
    private String regex;
    private String expression;

    @Override
    public String doDefault() {
        if (!hasAdminPermission()) {
            return PERMISSION_VIOLATION_RESULT;
        }
        return INPUT;
    }

    @Override
    @RequiresXsrfCheck
    protected String doExecute() {
        if (!hasAdminPermission()) {
            return PERMISSION_VIOLATION_RESULT;
        }

        return SUCCESS;
    }

    @Override
    protected void doValidation() {
        super.doValidation();
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

    public boolean hasAdminPermission() {
        return getGlobalPermissionManager().hasPermission(ADMINISTER, getLoggedInUser());
    }
}
