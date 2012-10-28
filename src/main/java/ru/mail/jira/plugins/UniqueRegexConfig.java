/*
 * Created by Andrey Markelov 28-10-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins;

import java.util.ArrayList;
import java.util.List;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.security.Permissions;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.sal.api.ApplicationProperties;

/**
 * Configuration action.
 * 
 * @author Andrey Markelov
 */
public class UniqueRegexConfig
    extends JiraWebActionSupport
{
    /**
     * Unique ID.
     */
    private static final long serialVersionUID = 1381842671050861762L;

    /**
     * Application properties.
     */
    private final ApplicationProperties applicationProperties;

    /**
     * Datas.
     */
    private List<CFData> datas;

    /**
     * Constructor.
     */
    public UniqueRegexConfig(
        UniqueRegexMgr urMgr,
        ApplicationProperties applicationProperties,
        CustomFieldManager cfMgr)
    {
        this.applicationProperties = applicationProperties;
        this.datas = new ArrayList<CFData>();

        List<CustomField> cgList = cfMgr.getCustomFieldObjects();
        for (CustomField cf : cgList)
        {
            if (cf.getCustomFieldType().getKey().equals("ru.mail.jira.plugins.uniqueregexfield:unique-regex-cf"))
            {
                CFData cFData = urMgr.getCFData(cf.getId());
                cFData.setCfKey(cf.getId());
                cFData.setCfName(cf.getName());
                datas.add(cFData);
            }
        }
    }

    /**
     * Get context path.
     */
    public String getBaseUrl()
    {
        return applicationProperties.getBaseUrl();
    }

    public List<CFData> getDatas()
    {
        return datas;
    }

    /**
     * Check administer permissions.
     */
    public boolean hasAdminPermission()
    {
        User user = getLoggedInUser();
        if (user == null)
        {
            return false;
        }

        if (getPermissionManager().hasPermission(Permissions.ADMINISTER, getLoggedInUser()))
        {
            return true;
        }

        return false;
    }
}
