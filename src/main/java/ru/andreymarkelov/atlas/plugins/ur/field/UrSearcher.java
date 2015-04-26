package ru.andreymarkelov.atlas.plugins.ur.field;

import java.util.Comparator;

import com.atlassian.jira.bc.issue.search.QueryContextConverter;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.customfields.searchers.ExactTextSearcher;
import com.atlassian.jira.issue.customfields.searchers.transformer.CustomFieldInputHelper;
import com.atlassian.jira.issue.customfields.statistics.AbstractCustomFieldStatisticsMapper;
import com.atlassian.jira.issue.customfields.statistics.CustomFieldStattable;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.search.ClauseNames;
import com.atlassian.jira.issue.search.LuceneFieldSorter;
import com.atlassian.jira.issue.search.searchers.renderer.SearchRenderer;
import com.atlassian.jira.issue.search.searchers.transformer.SearchInputTransformer;
import com.atlassian.jira.issue.statistics.StatisticsMapper;
import com.atlassian.jira.issue.statistics.TextFieldSorter;
import com.atlassian.jira.jql.operand.JqlOperandResolver;
import com.atlassian.jira.jql.util.JqlSelectOptionsUtil;
import com.atlassian.jira.web.FieldVisibilityManager;

public class UrSearcher extends ExactTextSearcher implements CustomFieldStattable {
    private static class SortStringComparator implements Comparator<String> {
        public int compare(String s, String s1) {
            return s.compareTo(s1);
        }
    }

    private CustomField customField;
    private CustomFieldInputHelper customFieldInputHelper;
    private JqlOperandResolver jqlOperandResolver;
    private SearchInputTransformer searchInputTransformer;
    private SearchRenderer searchRenderer;

    public UrSearcher(JqlOperandResolver jqlOperandResolver, CustomFieldInputHelper customFieldInputHelper, FieldVisibilityManager fieldVisibilityManager) {
        super(jqlOperandResolver, customFieldInputHelper, fieldVisibilityManager);
        this.jqlOperandResolver = jqlOperandResolver;
        this.customFieldInputHelper = customFieldInputHelper;
    }

    @Override
    public SearchInputTransformer getSearchInputTransformer() {
        return searchInputTransformer;
    }

    @Override
    public SearchRenderer getSearchRenderer() {
        return searchRenderer;
    }

    @Override
    public LuceneFieldSorter<String> getSorter(final CustomField customField) {
        return new TextFieldSorter(customField.getId()) {
            @Override
            public Comparator<String> getComparator() {
                return new SortStringComparator();
            }
        };
    }

    public StatisticsMapper<?> getStatisticsMapper(CustomField customField) {
        return new AbstractCustomFieldStatisticsMapper(customField) {
            @Override
            public Comparator<String> getComparator() {
                return new Comparator<String>() {
                    public int compare(String o1, String o2) {
                        if (o1 == null && o2 == null) {
                            return 0;
                        } else if (o1 == null) {
                            return 1;
                        } else if (o2 == null) {
                            return -1;
                        }
                        return (o1).compareTo(o2);
                    }
                };
            }

            @Override
            protected String getSearchValue(Object o) {
                if( o == null ) {
                    return null;
                } else {
                    return o.toString();
                }
            }

            public Object getValueFromLuceneField(String id) {
                if(id != null) {
                    return id;
                } else {
                    return null;
                }
            }
        };
    }

    @Override
    public void init(CustomField field) {
        customField = field;
        ClauseNames clauseNames = customField.getClauseNames();
        JqlSelectOptionsUtil jqlSelectOptionsUtil = ComponentAccessor.getComponent(JqlSelectOptionsUtil.class);
        QueryContextConverter queryContextConverter = new QueryContextConverter();
        FieldVisibilityManager fieldVisibilityManager = ComponentAccessor.getComponent(FieldVisibilityManager.class);

        searchInputTransformer = new UrSearchInputTransformer(
            customField.getId(),
            clauseNames,
            field,
            jqlOperandResolver,
            jqlSelectOptionsUtil,
            queryContextConverter,
            customFieldInputHelper);
        searchRenderer = new UrCustomFieldRenderer(
            clauseNames,
            getDescriptor(),
            customField,
            new UrCustomFieldValueProvider(),
            fieldVisibilityManager);
        super.init(field);
    }
}
