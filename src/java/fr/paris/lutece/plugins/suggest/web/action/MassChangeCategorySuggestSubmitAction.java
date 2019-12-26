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
package fr.paris.lutece.plugins.suggest.web.action;

import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.suggest.service.SuggestSubmitService;
import fr.paris.lutece.plugins.suggest.service.SuggestPlugin;
import fr.paris.lutece.plugins.suggest.service.SuggestResourceIdService;
import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.pluginaction.AbstractPluginAction;
import fr.paris.lutece.portal.web.pluginaction.DefaultPluginActionResult;
import fr.paris.lutece.portal.web.pluginaction.IPluginActionResult;
import fr.paris.lutece.util.url.UrlItem;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;


/**
 *
 * MassChangeCategorySuggestSubmitAction
 *
 */
public class MassChangeCategorySuggestSubmitAction extends AbstractPluginAction<SuggestAdminSearchFields>
    implements ISuggestAction
{
    private static final String ACTION_NAME = "Mass Change Category SuggestSubmit ";
    private static final String JSP_CONFIRM_CHANGE_SUGGEST_SUBMIT_CATEGORY = "jsp/admin/plugins/suggest/ConfirmMassChangeSuggestSubmitCategory.jsp";
    private static final String MESSAGE_YOU_MUST_SELECT_SUGGEST_SUBMIT = "suggest.message.youMustSelectSuggestSubmit";

    private static final String PARAMETER_ID_CATEGORY = "id_category";
    private static final String PARAMETER_MASS_CHANGE_CATEGORY = "mass_change_category";
    private static final String PARAMETER_SELECTED_SUGGEST_SUBMIT = "selected_suggest_submit";

    /**
     * {@inheritDoc}
     */
    @Override
    public void fillModel( HttpServletRequest request, AdminUser adminUser, Map<String, Object> model )
    {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName(  )
    {
        return ACTION_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getButtonTemplate(  )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInvoked( HttpServletRequest request )
    {
        return ( request.getParameter( PARAMETER_MASS_CHANGE_CATEGORY ) != null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPluginActionResult process( HttpServletRequest request, HttpServletResponse response, AdminUser adminUser,
        SuggestAdminSearchFields searchFields ) throws AccessDeniedException
    {
        IPluginActionResult result = new DefaultPluginActionResult(  );

        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );

        Plugin plugin = getPlugin(  );
        int nIdCategory = SuggestUtils.getIntegerParameter( strIdCategory );
        int nIdSuggestSubmit;
        String strRedirect;

        if ( ( searchFields.getSelectedSuggestSubmit(  ) != null ) && !searchFields.getSelectedSuggestSubmit(  ).isEmpty(  ) )
        {
            UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_CONFIRM_CHANGE_SUGGEST_SUBMIT_CATEGORY );
            url.addParameter( PARAMETER_ID_CATEGORY, nIdCategory );

            //test All ressource selected before update
            for ( String strIdSuggestSubmit : searchFields.getSelectedSuggestSubmit(  ) )
            {
                if ( StringUtils.isNotBlank( strIdSuggestSubmit ) && StringUtils.isNumeric( strIdSuggestSubmit ) )
                {
                    nIdSuggestSubmit = SuggestUtils.getIntegerParameter( strIdSuggestSubmit );

                    SuggestSubmit suggestSubmit = SuggestSubmitService.getService(  )
                                                             .findByPrimaryKey( nIdSuggestSubmit, false, plugin );

                    if ( ( suggestSubmit == null ) ||
                            !RBACService.isAuthorized( Suggest.RESOURCE_TYPE,
                                SuggestUtils.EMPTY_STRING + suggestSubmit.getSuggest(  ).getIdSuggest(  ),
                                SuggestResourceIdService.PERMISSION_MANAGE_SUGGEST_SUBMIT, adminUser ) )
                    {
                        throw new AccessDeniedException( "Access denied" );
                    }

                    url.addParameter( PARAMETER_SELECTED_SUGGEST_SUBMIT, nIdSuggestSubmit );
                }
            }

            strRedirect = url.getUrl(  );
        }
        else
        {
            strRedirect = AdminMessageService.getMessageUrl( request, MESSAGE_YOU_MUST_SELECT_SUGGEST_SUBMIT,
                    AdminMessage.TYPE_INFO );
        }

        result.setRedirect( strRedirect );

        return result;
    }

    /**
     * Gets the plugin
     * @return the plugin
     */
    private Plugin getPlugin(  )
    {
        return PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME );
    }
}
