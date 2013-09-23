package ru.mail.jira.plugins;

import java.util.ArrayList;
import java.util.List;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.sal.api.ApplicationProperties;

public class UniqueRegexConfig extends JiraWebActionSupport {
    private static final long serialVersionUID = 1381842671050861762L;

    private final ApplicationProperties applicationProperties;

    private List<CFData> datas;
    private String customFieldId;

    public UniqueRegexConfig(UniqueRegexMgr urMgr, ApplicationProperties applicationProperties, CustomFieldManager cfMgr) {
        this.applicationProperties = applicationProperties;
        this.datas = new ArrayList<CFData>();

        List<CustomField> cgList = cfMgr.getCustomFieldObjects();
        for (CustomField cf : cgList) {
            if (cf.getCustomFieldType().getKey().equals("ru.mail.jira.plugins.uniqueregexfield:unique-regex-cf")) {
                CFData cFData = urMgr.getCFData(cf.getId());
                cFData.setCfKey(cf.getId());
                cFData.setCfName(cf.getName());
                datas.add(cFData);
            }
        }
    }

    @Override
    public String doDefault() throws Exception {
        if (!hasAdminPermission()) {
            return PERMISSION_VIOLATION_RESULT;
        }
        return INPUT;
    }

    public String doConfigure() throws Exception {
        if (!hasAdminPermission()) {
            return PERMISSION_VIOLATION_RESULT;
        }

        if (customFieldId == null || customFieldId.length() == 0) {
            return INPUT;
        } else {
            
            return "configure";
        }
    }

    @Override
    @com.atlassian.jira.security.xsrf.RequiresXsrfCheck
    protected String doExecute() throws Exception {
        return getRedirect("UniqueRegexConfig!default.jspa?saved=true");
    }

    @Override
    protected void doValidation() {
        super.doValidation();
    }

    public String getBaseUrl() {
        return applicationProperties.getBaseUrl();
    }

    public String getCustomFieldId() {
        return customFieldId;
    }

    public List<CFData> getDatas() {
        return datas;
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
}
