package ru.andreymarkelov.atlas.plugins.ur.field;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.customfields.impl.FieldValidationException;
import com.atlassian.jira.issue.customfields.impl.GenericTextCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;
import com.atlassian.jira.issue.customfields.view.CustomFieldParams;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.TextFieldCharacterLengthValidator;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.web.bean.PagerFilter;

import ru.andreymarkelov.atlas.plugins.ur.manager.UniqueRegexMgr;
import ru.andreymarkelov.atlas.plugins.ur.model.CFData;
import webwork.action.ActionContext;

public class UniqueRegexCF extends GenericTextCFType {
    private static Log log = LogFactory.getLog(UniqueRegexCF.class);

    private final CustomFieldManager cfMgr;
    private final SearchService searchService;
    private final UniqueRegexMgr urMgr;
    private final JiraAuthenticationContext authenticationContext;

    public UniqueRegexCF(
            CustomFieldValuePersister customFieldValuePersister,
            GenericConfigManager genericConfigManager,
            TextFieldCharacterLengthValidator textFieldCharacterLengthValidator,
            JiraAuthenticationContext jiraAuthenticationContext,
            UniqueRegexMgr urMgr,
            SearchService searchService,
            CustomFieldManager cfMgr,
            JiraAuthenticationContext authenticationContext) {
        super(customFieldValuePersister, genericConfigManager, textFieldCharacterLengthValidator, jiraAuthenticationContext);
        this.urMgr = urMgr;
        this.searchService = searchService;
        this.cfMgr = cfMgr;
        this.authenticationContext = authenticationContext;
    }

    private Long getIssueId(CustomFieldParams relevantParams) {
        if (relevantParams.containsKey("com.atlassian.jira.internal.issue_id")) {
            Object obj = relevantParams.getFirstValueForKey("com.atlassian.jira.internal.issue_id");
            if (obj != null) {
                return NumberUtils.toLong(obj.toString(), -1L);
            }
        }

        HttpServletRequest req = ActionContext.getRequest();
        if (req == null) {
            return -1L;
        }

        String[] issueIdStr = req.getParameterValues("id");
        if (issueIdStr == null) {
            issueIdStr = req.getParameterValues("issueId");
        }

        if (issueIdStr == null || issueIdStr.length == 0) {
            return -1L;
        } else {
            try {
                return Long.valueOf(issueIdStr[0]);
            } catch (Exception ex) {
                return -1L;
            }
        }
    }

    public void validateFromParams(CustomFieldParams relevantParams, ErrorCollection errorCollectionToAddTo, FieldConfig config) {
        String cfVal;
        try {
            cfVal = (String) getValueFromCustomFieldParams(relevantParams);
        } catch (FieldValidationException e) {
            errorCollectionToAddTo.addError(config.getCustomField().getId(), e.getMessage());
            return;
        }

        if (cfVal != null && cfVal.length() > 0) {
            CustomField cf = config.getCustomField();
            CFData cfData = urMgr.getCFData(cf.getId());
            if (cfData.getRegex() != null && cfData.getRegex().length() > 0) {
                Pattern pattern = Pattern.compile(cfData.getRegex());
                Matcher m = pattern.matcher(cfVal);
                if (!m.matches()) {
                    if (cfData.getRegexError() != null && cfData.getRegexError().length() > 0) {
                        errorCollectionToAddTo.addError(config.getCustomField().getId(), cfData.getRegexError());
                    } else {
                        errorCollectionToAddTo.addError( config.getCustomField().getId(), getI18nBean().getText("uniqueregex.matcherror", cfVal, cfData.getRegex()));
                    }
                    return;
                }
            }

            if (cfData.getJql() != null && cfData.getJql().length() > 0) {
                ApplicationUser user = authenticationContext.getLoggedInUser();
                SearchService.ParseResult parseResult = searchService.parseQuery(user, cfData.getJql());
                if (parseResult.isValid()) {
                    CustomField tCf = cfMgr.getCustomFieldObject(cfData.getTargetCf());
                    if (tCf != null) {
                        try {
                            SearchResults results = searchService.search(user, parseResult.getQuery(), PagerFilter.getUnlimitedFilter());
                            List<Issue> issues = results.getIssues();
                            for (Issue i : issues) {
                                Object tVal = i.getCustomFieldValue(tCf);
                                boolean isSameIssue = false;
                                Long currIssueId = getIssueId(relevantParams);
                                if (currIssueId >= 0) {
                                    if (i.getId().equals(currIssueId)) {
                                        isSameIssue = true;
                                    }
                                }

                                if (tVal != null && tVal.toString().equals(cfVal) && !isSameIssue) {
                                    errorCollectionToAddTo.addError(config.getCustomField().getId(), getI18nBean().getText("uniqueregex.unique", cfData.getJql()));
                                    return;
                                }
                            }
                        } catch (SearchException e) {
                            log.error("UniqueRegexCF::validateFromParams - Search error");
                        }
                    }
                }
            }
        }
    }
}
