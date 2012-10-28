/*
 * Created by Andrey Markelov 28-10-2012.
 * Copyright Mail.Ru Group 2012. All rights reserved.
 */
package ru.mail.jira.plugins;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.exception.VelocityException;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.xsrf.XsrfTokenGenerator;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.util.MessageSet;

/**
 * Unique regex custom fields plugin service.
 * 
 * @author Andrey Markelov
 */
@Path("/uniqueregexsrv")
public class UniqueRegexService
{
    /**
     * Logger.
     */
    private static Log log = LogFactory.getLog(UniqueRegexService.class);

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
    public UniqueRegexService(
        UniqueRegexMgr urMgr,
        SearchService searchService,
        CustomFieldManager cfMgr)
    {
        this.urMgr = urMgr;
        this.searchService = searchService;
        this.cfMgr = cfMgr;
    }

    @POST
    @Produces ({ MediaType.APPLICATION_JSON})
    @Path("/configurecf")
    public Response configureCf(@Context HttpServletRequest request)
    throws Exception
    {
        JiraAuthenticationContext authCtx = ComponentManager.getInstance().getJiraAuthenticationContext();
        I18nHelper i18n = authCtx.getI18nHelper();
        User user = authCtx.getLoggedInUser();
        if (user == null)
        {
            log.error("UniqueRegexService::initJclDialog - User is not logged");
            return Response.ok(i18n.getText("uniqueregex.internalerror")).status(401).build();
        }

        XsrfTokenGenerator xsrfTokenGenerator = ComponentManager.getComponentInstanceOfType(XsrfTokenGenerator.class);
        String token = xsrfTokenGenerator.getToken(request);
        if (!xsrfTokenGenerator.generatedByAuthenticatedUser(token))
        {
            log.error("UniqueRegexService::configureCf - There is no token");
            return Response.ok(i18n.getText("uniqueregex.internalerror")).status(500).build();
        }
        else
        {
            String atl_token = request.getParameter("atl_token");
            if (!atl_token.equals(token))
            {
                log.error("UniqueRegexService::configureCf - Token is invalid");
                return Response.ok(i18n.getText("uniqueregex.internalerror")).status(500).build();
            }
        }

        String cfKey = request.getParameter("cfKey");
        String regexclause = request.getParameter("regexclause");
        String jqlclause = request.getParameter("jqlclause");
        String targetcf = request.getParameter("targetcf");

        if (cfKey == null || cfKey.length() < 1)
        {
            log.error("UniqueRegexService::configureCf - Required parameters are not set");
            return Response.ok(i18n.getText("uniqueregex.internalerror")).status(500).build();
        }

        String storeCf = "";
        CustomField tCf = cfMgr.getCustomFieldObject(targetcf);
        if (tCf != null)
        {
            storeCf = tCf.getId() + ":" + tCf.getName();
        }

        if (jqlclause != null && jqlclause.length() > 0)
        {
            SearchService.ParseResult parseResult = searchService.parseQuery(user, jqlclause);
            if (parseResult.isValid())
            {
                urMgr.setCfRegex(cfKey, regexclause);
                urMgr.setCfJql(cfKey, jqlclause);
                urMgr.setCfTarget(cfKey, storeCf);
            }
            else
            {
                MessageSet ms = parseResult.getErrors();
                Set<String> errs = ms.getErrorMessages();
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("errs", errs);
                params.put("i18n", i18n);

                try
                {
                    String body = ComponentAccessor.getVelocityManager().getBody("templates/", "conferr.vm", params);
                    return Response.ok(new HtmlEntity(body)).status(500).build();
                }
                catch (VelocityException vex)
                {
                    log.error("UniqueRegexService::configureCf - Velocity parsing error", vex);
                    return Response.ok(i18n.getText("uniqueregex.internalerror")).status(500).build();
                }
            }
        }
        else
        {
            urMgr.setCfRegex(cfKey, regexclause);
            urMgr.setCfJql(cfKey, "");
            urMgr.setCfTarget(cfKey, storeCf);
        }

        return Response.ok().build();
    }

    /**
     * Get base URL from HTTP request.
     */
    public String getBaseUrl(HttpServletRequest req)
    {
        return (req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath());
    }

    @POST
    @Path("/initconfdialog")
    @Produces({MediaType.APPLICATION_JSON})
    public Response initConfDialog(@Context HttpServletRequest req)
    {
        JiraAuthenticationContext authCtx = ComponentManager.getInstance().getJiraAuthenticationContext();
        I18nHelper i18n = authCtx.getI18nHelper();
        User user = authCtx.getLoggedInUser();
        if (user == null)
        {
            log.error("UniqueRegexService::initConfDialog - User is not logged");
            return Response.ok(i18n.getText("uniqueregex.internalerror")).status(401).build();
        }

        String cfKey = req.getParameter("cfId");
        if (cfKey == null || cfKey.length() < 1)
        {
            log.error("UniqueRegexService::initConfDialog - Required parameters are not set");
            return Response.ok(i18n.getText("uniqueregex.internalerror")).status(500).build();
        }

        XsrfTokenGenerator xsrfTokenGenerator = ComponentManager.getComponentInstanceOfType(XsrfTokenGenerator.class);
        String atl_token = xsrfTokenGenerator.generateToken(req);

        CFData cfData = urMgr.getCFData(cfKey);

        Map<String, String> cfs = new TreeMap<String, String>();
        List<CustomField> cgList = cfMgr.getCustomFieldObjects();
        for (CustomField cf : cgList)
        {
            cfs.put(cf.getId(), cf.getName());
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("i18n", authCtx.getI18nHelper());
        params.put("baseUrl", getBaseUrl(req));
        params.put("atl_token", atl_token);
        params.put("cfKey", cfKey);
        params.put("cfData", cfData);
        params.put("cfs", cfs);

        try
        {
            String body = ComponentAccessor.getVelocityManager().getBody("templates/", "configuredlg.vm", params);
            return Response.ok(new HtmlEntity(body)).build();
        }
        catch (VelocityException vex)
        {
            log.error("UniqueRegexService::initConfDialog - Velocity parsing error", vex);
            return Response.ok(i18n.getText("uniqueregex.internalerror")).status(500).build();
        }
    }
}
