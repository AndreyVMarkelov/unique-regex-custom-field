package ru.andreymarkelov.atlas.plugins.ur;

import java.util.List;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.sal.api.ApplicationProperties;

public class UniqueRegexFieldConfig extends JiraWebActionSupport {
    private static final long serialVersionUID = -727825112402972172L;

    private final ApplicationProperties applicationProperties;
    private final UniqueRegexMgr urMgr;
    private final CustomFieldManager cfMgr;

    private String customFieldId;
    private String regexclause;
    private String regexerror;
    private String jqlclause;
    private String targetcf;

    public UniqueRegexFieldConfig(UniqueRegexMgr urMgr, ApplicationProperties applicationProperties, CustomFieldManager cfMgr) {
        this.urMgr = urMgr;
        this.applicationProperties = applicationProperties;
        this.cfMgr = cfMgr;
    }

    @Override
    public String doDefault() throws Exception {
        if (!hasAdminPermission()) {
            return PERMISSION_VIOLATION_RESULT;
        }

        if (customFieldId == null || customFieldId.length() == 0) {
            return getRedirect("UniqueRegexConfig!default.jspa");
        }

        CustomField field = cfMgr.getCustomFieldObject(customFieldId);
        if (field == null) {
            return getRedirect("UniqueRegexConfig!default.jspa");
        }

        CFData data = urMgr.getCFData(customFieldId);
        jqlclause = data.getJql();
        regexclause = data.getRegex();
        regexerror = data.getRegexError();
        targetcf = data.getTargetCf();
        return INPUT;
    }

    @Override
    @com.atlassian.jira.security.xsrf.RequiresXsrfCheck
    protected String doExecute() throws Exception {
        if (!hasAdminPermission()) {
            return PERMISSION_VIOLATION_RESULT;
        }

        urMgr.setCfJql(customFieldId, jqlclause);
        urMgr.setCfRegex(customFieldId, regexclause);
        urMgr.setCfRegexError(customFieldId, regexerror);
        urMgr.setCfTarget(customFieldId, targetcf);
        return getRedirect("UniqueRegexConfig!default.jspa?saved=true");
    }

    @Override
    protected void doValidation() {
        if (!UrUtils.checkJQL(jqlclause)) {
            addErrorMessage(getText("uniqueregex.incorrectjql"));
        }

        if (!UrUtils.checkRegex(regexclause)) {
            addErrorMessage(getText("uniqueregex.incorrectregex"));
        }

        super.doValidation();
    }

    public String getBackLink() {
        return getBaseUrl().concat("/secure/UniqueRegexConfig!default.jspa");
    }

    public String getBaseUrl() {
        return applicationProperties.getBaseUrl();
    }

    public String getCustomFieldId() {
        return customFieldId;
    }

    public List<CustomField> getCustomFields() {
        return cfMgr.getCustomFieldObjects();
    }

    public String getDefaultTarget() {
        if (UrUtils.isEmpty(targetcf)) {
            return customFieldId;
        } else {
            return targetcf;
        }
    }

    public String getJqlclause() {
        return jqlclause;
    }

    public String getRegexclause() {
        return regexclause;
    }

    public String getRegexerror() {
        return regexerror;
    }

    public String getTargetcf() {
        return targetcf;
    }

    public String getTitle() {
        return getText("ru.andreymarkelov.atlas.plugins.uniqueregexfield.field.title", UrUtils.getCfName(cfMgr, customFieldId));
    }

    public boolean hasAdminPermission() {
        User user = getLoggedInUser();
        if (user == null) {
            return false;
        }

        if (getPermissionManager().hasPermission(Permissions.ADMINISTER, getLoggedInUser())) {
            return true;
        }

        return false;
    }

    public void setCustomFieldId(String customFieldId) {
        this.customFieldId = customFieldId;
    }

    public void setJqlclause(String jqlclause) {
        this.jqlclause = jqlclause;
    }

    public void setRegexclause(String regexclause) {
        this.regexclause = regexclause;
    }

    public void setRegexerror(String regexerror) {
        this.regexerror = regexerror;
    }

    public void setTargetcf(String targetcf) {
        this.targetcf = targetcf;
    }
}
