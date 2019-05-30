package ru.andreymarkelov.atlas.plugins.ur.field;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.imports.project.customfield.ProjectImportableCustomField;
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
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.andreymarkelov.atlas.plugins.ur.manager.UniqueRegexMgr;
import ru.andreymarkelov.atlas.plugins.ur.model.CFData;
import webwork.action.ActionContext;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trim;

public class UniqueRegexCF extends GenericTextCFType implements ProjectImportableCustomField {
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

    private boolean isInIssueContext(CustomFieldParams relevantParams) {
        if (relevantParams.containsKey("com.atlassian.jira.internal.issue_id")
                || relevantParams.containsKey("com.atlassian.jira.internal.project_id")) {
            return true;
        }
        return false;
    }

    private long getIssueId(CustomFieldParams relevantParams) {
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
        if (!isInIssueContext(relevantParams)) {
            return;
        }

        String cfVal;
        try {
            cfVal = getValueFromCustomFieldParams(relevantParams);
        } catch (FieldValidationException e) {
            errorCollectionToAddTo.addError(config.getCustomField().getId(), e.getMessage());
            return;
        }

        if (isNotBlank(cfVal)) {
            CustomField cf = config.getCustomField();
            CFData cfData = urMgr.getCFData(cf.getId());
            if (isNotBlank(cfData.getRegex())) {
                Pattern pattern = Pattern.compile(cfData.getRegex());
                Matcher m = pattern.matcher(trim(cfVal));
                if (!m.matches()) {
                    if (isNotBlank(cfData.getRegexError())) {
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
                    CustomField customField = cfMgr.getCustomFieldObject(cfData.getTargetCf());
                    if (customField != null) {
                        try {
                            SearchResults results = searchService.search(user, parseResult.getQuery(), PagerFilter.getUnlimitedFilter());
                            List<Issue> issues = results.getIssues();
                            for (Issue i : issues) {
                                Object tVal = i.getCustomFieldValue(customField);
                                boolean isSameIssue = false;
                                Long currIssueId = getIssueId(relevantParams);
                                if (currIssueId >= 0) {
                                    if (i.getId().equals(currIssueId)) {
                                        isSameIssue = true;
                                    }
                                }

                                if (tVal != null && tVal.toString().equals(cfVal) && !isSameIssue) {
                                    if (isNotBlank(cfData.getUniqueError())) {
                                        errorCollectionToAddTo.addError(config.getCustomField().getId(), cfData.getUniqueError());
                                    } else {
                                        errorCollectionToAddTo.addError(config.getCustomField().getId(), getI18nBean().getText("uniqueregex.unique", cfData.getJql()));
                                    }
                                    return;
                                }
                            }
                        } catch (SearchException e) {
                            log.error("UniqueRegexCF::validateFromParams - Search error", e);
                        }
                    }
                }
            }
        }
    }
}
