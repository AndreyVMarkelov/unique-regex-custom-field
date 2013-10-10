package ru.andreymarkelov.atlas.plugins.ur;

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
    private final UniqueRegexMgr urMgr;
    private final CustomFieldManager cfMgr;

    private List<CFData> datas;

    public UniqueRegexConfig(UniqueRegexMgr urMgr, ApplicationProperties applicationProperties, CustomFieldManager cfMgr) {
        this.urMgr = urMgr;
        this.applicationProperties = applicationProperties;
        this.cfMgr = cfMgr;
    }

    @Override
    public String doDefault() throws Exception {
        if (!hasAdminPermission()) {
            return PERMISSION_VIOLATION_RESULT;
        }

        datas = new ArrayList<CFData>();
        List<CustomField> cgList = cfMgr.getCustomFieldObjects();
        for (CustomField cf : cgList) {
            if (cf.getCustomFieldType().getClass().equals(UniqueRegexCF.class)) {
                CFData cFData = urMgr.getCFData(cf.getId());
                cFData.setCfKey(cf.getId());
                cFData.setCfName(cf.getName());
                cFData.setTargetCfName(UrUtils.getCfName(cfMgr, cFData.getTargetCf()));
                datas.add(cFData);
            }
        }

        return INPUT;
    }

    public String getBaseUrl() {
        return applicationProperties.getBaseUrl();
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
}
