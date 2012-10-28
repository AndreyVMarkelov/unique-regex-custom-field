/*
 * Created by Andrey Markelov 28-10-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.impl.TextCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.view.CustomFieldParams;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.web.bean.PagerFilter;

/**
 * Unique regex custon field.
 * 
 * @author Andrey Markelov
 */
public class UniqueRegexCF
    extends TextCFType
{
    /**
     * Logger.
     */
    private static Log log = LogFactory.getLog(UniqueRegexCF.class);

    /**
     * Search service.
     */
    private final SearchService searchService;

    /**
     * Unique regex fields manager.
     */
    private final UniqueRegexMgr urMgr;

    /**
     * Custom field manager.
     */
    private final CustomFieldManager cfMgr;

    /**
     * Constructor.
     */
    public UniqueRegexCF(
        CustomFieldValuePersister customFieldValuePersister,
        GenericConfigManager genericConfigManager,
        UniqueRegexMgr urMgr,
        SearchService searchService,
        CustomFieldManager cfMgr)
    {
        super(customFieldValuePersister, genericConfigManager);
        this.urMgr = urMgr;
        this.searchService = searchService;
        this.cfMgr = cfMgr;
    }

    /**
     * Check parameters.
     */
    public void validateFromParams(
        CustomFieldParams relevantParams,
        ErrorCollection errorCollectionToAddTo,
        FieldConfig config)
    {
        String cfVal;
        try
        {
            cfVal = (String) getValueFromCustomFieldParams(relevantParams);
        }
        catch (FieldValidationException e)
        {
            errorCollectionToAddTo.addError(config.getCustomField().getId(), e.getMessage());
            return;
        }

        if (cfVal != null && cfVal.length() > 0)
        {
            CustomField cf = config.getCustomField();
            CFData cfData = urMgr.getCFData(cf.getId());
            if (cfData.getRegex() != null && cfData.getRegex().length() > 0)
            {
                Pattern pattern = Pattern.compile(cfData.getRegex());
                Matcher m = pattern.matcher(cfVal);
                if (!m.matches())
                {
                    errorCollectionToAddTo.addError(
                        config.getCustomField().getId(),
                        getI18nBean().getText("uniqueregex.matcherror", cfVal, cfData.getRegex()));
                    return;
                }
            }

            if (cfData.getJql() != null && cfData.getJql().length() > 0)
            {
                User user = ComponentManager.getInstance().getJiraAuthenticationContext().getLoggedInUser();
                SearchService.ParseResult parseResult = searchService.parseQuery(user, cfData.getJql());
                if (parseResult.isValid())
                {
                    CustomField tCf = cfMgr.getCustomFieldObject(cfData.getTargetCf());
                    if (tCf != null)
                    {
                        try
                        {
                            SearchResults results = searchService.search(
                                user,
                                parseResult.getQuery(),
                                PagerFilter.getUnlimitedFilter());
                            List<Issue> issues = results.getIssues();
                            for (Issue i : issues)
                            {
                                Object tVal = i.getCustomFieldValue(tCf);
                                if (tVal != null && tVal.toString().equals(cfVal))
                                {
                                    errorCollectionToAddTo.addError(
                                        config.getCustomField().getId(),
                                        getI18nBean().getText("uniqueregex.unique"));
                                    return;
                                }
                            }
                        }
                        catch (SearchException e)
                        {
                            log.error("UniqueRegexCF::validateFromParams - Search error");
                        }
                    }
                }
            }
        }
    }
}
