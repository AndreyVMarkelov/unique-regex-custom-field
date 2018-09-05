package ru.andreymarkelov.atlas.plugins.ur.action;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.xsrf.RequiresXsrfCheck;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import ru.andreymarkelov.atlas.plugins.ur.field.UniqueRegexCF;
import ru.andreymarkelov.atlas.plugins.ur.manager.UniqueRegexMgr;
import ru.andreymarkelov.atlas.plugins.ur.model.CFData;
import ru.andreymarkelov.atlas.plugins.ur.utils.UrUtils;

import static com.atlassian.jira.permission.GlobalPermissionKey.ADMINISTER;

public class UniqueRegexConfig extends JiraWebActionSupport {
    private static final long serialVersionUID = 1381842671050861762L;

    private final UniqueRegexMgr uniqueRegexMgr;
    private final JiraAuthenticationContext authenticationContext;
    private final CustomFieldManager customFieldManager;
    private final GlobalPermissionManager globalPermissionManager;

    private List<CFData> datas;
    private String customFieldId;
    private String regexClause;
    private String regexError;
    private String jqlClause;
    private String targetCf;

    public UniqueRegexConfig(
            UniqueRegexMgr uniqueRegexMgr,
            JiraAuthenticationContext authenticationContext,
            CustomFieldManager customFieldManager,
            GlobalPermissionManager globalPermissionManager) {
        this.uniqueRegexMgr = uniqueRegexMgr;
        this.authenticationContext = authenticationContext;
        this.customFieldManager = customFieldManager;
        this.globalPermissionManager = globalPermissionManager;
    }

    @Override
    public String doDefault() {
        if (!hasAdminPermission()) {
            return PERMISSION_VIOLATION_RESULT;
        }

        datas = new ArrayList<>();
        List<CustomField> cgList = customFieldManager.getCustomFieldObjects();
        for (CustomField cf : cgList) {
            if (cf.getCustomFieldType().getClass().equals(UniqueRegexCF.class)) {
                CFData cFData = uniqueRegexMgr.getCFData(cf.getId());
                cFData.setCfKey(cf.getId());
                cFData.setCfName(cf.getName());
                cFData.setTargetCfName(UrUtils.getCfName(customFieldManager, cFData.getTargetCf()));
                datas.add(cFData);
            }
        }
        return SUCCESS;
    }

    public String doEdit() {
        if (!hasAdminPermission()) {
            return PERMISSION_VIOLATION_RESULT;
        }

        CFData cfData = uniqueRegexMgr.getCFData(customFieldId);
        if (cfData != null) {
            regexClause = cfData.getRegex();
            regexError = cfData.getRegexError();
            jqlClause = cfData.getJql();
            targetCf = cfData.getTargetCf();
        }
        return INPUT;
    }

    @Override
    @RequiresXsrfCheck
    protected String doExecute() {
        if (!hasAdminPermission()) {
            return PERMISSION_VIOLATION_RESULT;
        }

        uniqueRegexMgr.setCfJql(customFieldId, jqlClause);
        uniqueRegexMgr.setCfRegex(customFieldId, regexClause);
        uniqueRegexMgr.setCfRegexError(customFieldId, regexError);
        uniqueRegexMgr.setCfTarget(customFieldId, targetCf);
        return getRedirect("UniqueRegexConfig!default.jspa?saved=true");
    }

    @Override
    protected void doValidation() {
        super.doValidation();
        if (!UrUtils.checkJQL(jqlClause)) {
            addError("jqlClause", authenticationContext.getI18nHelper().getText("ru.andreymarkelov.atlas.plugins.ur.admin.configuration.edit.jqlClause.error.invalid"));
        }
        if (!UrUtils.checkRegex(regexClause)) {
            addError("regexClause", authenticationContext.getI18nHelper().getText("ru.andreymarkelov.atlas.plugins.ur.admin.configuration.edit.regexClause.error.invalid"));
        }
    }

    public String getCustomFieldId() {
        return customFieldId;
    }

    public List<CustomField> getCustomFields() {
        return customFieldManager.getCustomFieldObjects();
    }

    public List<CFData> getDatas() {
        return datas;
    }

    public String getFieldName() {
        CustomField customField = customFieldManager.getCustomFieldObject(customFieldId);
        if (customField != null) {
            return customField.getName();
        }
        return customFieldId;
    }

    public String getJqlClause() {
        return jqlClause;
    }

    public String getRegexClause() {
        return regexClause;
    }

    public String getRegexError() {
        return regexError;
    }

    public String getTargetCf() {
        return targetCf;
    }

    public boolean hasAdminPermission() {
        return globalPermissionManager.hasPermission(ADMINISTER, getLoggedInUser());
    }

    public void setCustomFieldId(String customFieldId) {
        this.customFieldId = customFieldId;
    }

    public void setJqlClause(String jqlClause) {
        this.jqlClause = jqlClause;
    }

    public void setRegexClause(String regexClause) {
        this.regexClause = regexClause;
    }

    public void setRegexError(String regexError) {
        this.regexError = regexError;
    }

    public void setTargetCf(String targetCf) {
        this.targetCf = targetCf;
    }
}
