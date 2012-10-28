// Created by Andrey Markelov 28-10-2012.
// Copyright Mail.Ru Group 2012. All rights reserved.

jQuery(document).ready(function() {
    jQuery(window).bind('beforeunload', function() {
        return null;
    });
});

//--> initialize dialog
function initConfDialog(baseUrl, cfId)
{
    var res = "";
    jQuery.ajax({
        url: baseUrl + "/rest/uniqueregexfield/1.0/uniqueregexsrv/initconfdialog",
        type: "POST",
        dataType: "json",
        data: {"cfId": cfId},
        async: false,
        error: function(xhr, ajaxOptions, thrownError) {
            try {
                var respObj = eval("(" + xhr.responseText + ")");
                initErrorDlg(respObj.message).show();
            } catch(e) {
                initErrorDlg(xhr.responseText).show();
            }
        },
        success: function(result) {
            res = result.html;
        }
    });

    return res;
}

//--> initialize error dialog
function initErrorDlg(bodyText) {
    var errorDialog = new AJS.Dialog({
        width:420,
        height:250,
        id:"error-dialog",
        closeOnOutsideClick: true
    });

    errorDialog.addHeader(AJS.I18n.getText("uniqueregex.conf.dialog.error"));
    errorDialog.addPanel("ErrorMainPanel", '' +
        '<html><body><div class="error-message errdlg">' +
        bodyText +
        '</div></body></html>',
        "error-panel-body");
    errorDialog.addCancel(AJS.I18n.getText("uniqueregex.btn.close"), function() {
        errorDialog.hide();
    });

    return errorDialog;
}

//--> configure custom field
function configureCf(event, baseUrl, cfId) {
    event.preventDefault();

    var dialogBody = initConfDialog(baseUrl, cfId);
    if (!dialogBody)
    {
        return;
    }

    jQuery("#configureform").remove();
    var md = new AJS.Dialog({
        width:550,
        height:350,
        id:"configure_dialog",
        closeOnOutsideClick: true
    });
    md.addHeader(AJS.I18n.getText("uniqueregex.conf.dialog.title"));
    md.addPanel("load_panel", dialogBody);
    md.addButton(AJS.I18n.getText("uniqueregex.btn.apply"), function() {
        jQuery.ajax({
            url: baseUrl + "/rest/uniqueregexfield/1.0/uniqueregexsrv/configurecf",
            type: "POST",
            dataType: "json",
            data: AJS.$("#configureform").serialize(),
            async: false,
            error: function(xhr, ajaxOptions, thrownError) {
                var errText;
                try {
                    var respObj = eval("(" + xhr.responseText + ")");
                    if (respObj.message) {
                        errText = respObj.message;
                    } else if (respObj.html) {
                        errText = respObj.html;
                    } else {
                        errText = xhr.responseText;
                    }
                } catch(e) {
                    errText = xhr.responseText;
                }
                jQuery("#errorpart").empty();
                jQuery("#errorpart").append("<div class='errdiv'>" + errText + "</div>");
            },
            success: function(result) {
                document.location.reload(true);
            }
        });
    });
    md.addCancel(AJS.I18n.getText("uniqueregex.btn.close"), function() {
        md.hide();
    });
    md.show();
}
