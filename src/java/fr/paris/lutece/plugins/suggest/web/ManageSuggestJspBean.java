/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.suggest.web;

import fr.paris.lutece.plugins.suggest.business.Category;
import fr.paris.lutece.plugins.suggest.business.DefaultMessage;
import fr.paris.lutece.plugins.suggest.business.ExportFormat;
import fr.paris.lutece.plugins.suggest.service.CategoryResourceIdService;
import fr.paris.lutece.plugins.suggest.service.DefaultMessageResourceIdService;
import fr.paris.lutece.plugins.suggest.service.ExportFormatResourceIdService;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * class ManageFormJspBean
 */
public class ManageSuggestJspBean extends PluginAdminPageJspBean
{
    public static final String RIGHT_MANAGE_SUGGEST_LIKE = "SUGGEST_MANAGEMENT";
    private static final long serialVersionUID = -4559529824052770950L;

    //templates
    private static final String TEMPLATE_MANAGE_SUGGEST = "admin/plugins/suggest/accueil.html";

    // other constants
    private static final String EMPTY_STRING = "";

    //Markers
    private static final String MARK_PERMISSION_MANAGE_DEFAULT_MESSAGE = "permission_manage_default_message";
    private static final String MARK_PERMISSION_MANAGE_CATEGORY = "permission_manage_category";
    private static final String MARK_PERMISSION_MANAGE_EXPORT_FORMAT = "permission_manage_export_format";

    /*-------------------------------MANAGEMENT  FORM-----------------------------*/

    /**
     * Return management page of plugin form
     * @param request The Http request
     * @return Html management page of plugin form
     */
    public String getManageSuggest( HttpServletRequest request )
    {
        AdminUser adminUser = getUser(  );
        setPageTitleProperty( EMPTY_STRING );

        Map<String, Object> model = new HashMap<String, Object>(  );

        if ( RBACService.isAuthorized( ExportFormat.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    ExportFormatResourceIdService.PERMISSION_MANAGE, adminUser ) )
        {
            model.put( MARK_PERMISSION_MANAGE_EXPORT_FORMAT, true );
        }
        else
        {
            model.put( MARK_PERMISSION_MANAGE_EXPORT_FORMAT, false );
        }

        if ( RBACService.isAuthorized( Category.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    CategoryResourceIdService.PERMISSION_MANAGE, adminUser ) )
        {
            model.put( MARK_PERMISSION_MANAGE_CATEGORY, true );
        }
        else
        {
            model.put( MARK_PERMISSION_MANAGE_CATEGORY, false );
        }

        if ( RBACService.isAuthorized( DefaultMessage.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                    DefaultMessageResourceIdService.PERMISSION_MANAGE, getUser(  ) ) )
        {
            model.put( MARK_PERMISSION_MANAGE_DEFAULT_MESSAGE, true );
        }
        else
        {
            model.put( MARK_PERMISSION_MANAGE_DEFAULT_MESSAGE, false );
        }

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_SUGGEST, getLocale(  ), model );

        return getAdminPage( templateList.getHtml(  ) );
    }
}
