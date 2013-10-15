/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.digglike.web.action;

import fr.paris.lutece.plugins.digglike.business.Category;
import fr.paris.lutece.plugins.digglike.business.CategoryHome;
import fr.paris.lutece.plugins.digglike.business.Digg;
import fr.paris.lutece.plugins.digglike.business.DiggSubmit;
import fr.paris.lutece.plugins.digglike.service.DiggSubmitService;
import fr.paris.lutece.plugins.digglike.service.DigglikePlugin;
import fr.paris.lutece.plugins.digglike.service.DigglikeResourceIdService;
import fr.paris.lutece.plugins.digglike.utils.DiggUtils;
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
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * MassChangeCategoryDiggSubmitAction
 *
 */
public class MassChangeCategoryDiggSubmitAction extends AbstractPluginAction<DigglikeAdminSearchFields>
    implements IDigglikeAction
{
    private static final String ACTION_NAME = "Mass Change Category DiggSubmit ";
    private static final String JSP_CONFIRM_CHANGE_DIGG_SUBMIT_CATEGORY = "jsp/admin/plugins/digglike/ConfirmMassChangeDiggSubmitCategory.jsp";
    private static final String MESSAGE_YOU_MUST_SELECT_DIGG_SUBMIT = "digglike.message.youMustSelectDiggSubmit";
    private static final String MARK_CATEGORY_LIST = "category_list";
    private static final String MARK_DIGG = "digg";
    private static final String PARAMETER_ID_CATEGORY = "id_category";
    private static final String PARAMETER_MASS_CHANGE_CATEGORY = "mass_change_category";
    private static final String PARAMETER_SELECTED_DIGG_SUBMIT = "selected_digg_submit";

    /**
     * {@inheritDoc}
     */
    @Override
    public void fillModel( HttpServletRequest request, AdminUser adminUser, Map<String, Object> model )
    {
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
        DigglikeAdminSearchFields searchFields ) throws AccessDeniedException
    {
        IPluginActionResult result = new DefaultPluginActionResult(  );

        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );

        Plugin plugin = getPlugin(  );
        int nIdCategory = DiggUtils.getIntegerParameter( strIdCategory );
        int nIdDiggSubmit;
        String strRedirect = StringUtils.EMPTY;

        if ( ( searchFields.getSelectedDiggSubmit(  ) != null ) && !searchFields.getSelectedDiggSubmit(  ).isEmpty(  ) )
        {
            UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_CONFIRM_CHANGE_DIGG_SUBMIT_CATEGORY );
            url.addParameter( PARAMETER_ID_CATEGORY, nIdCategory );

            //test All ressource selected before update
            for ( String strIdDiggSubmit : searchFields.getSelectedDiggSubmit(  ) )
            {
                if ( StringUtils.isNotBlank( strIdDiggSubmit ) && StringUtils.isNumeric( strIdDiggSubmit ) )
                {
                    nIdDiggSubmit = DiggUtils.getIntegerParameter( strIdDiggSubmit );

                    DiggSubmit diggSubmit = DiggSubmitService.getService(  )
                                                             .findByPrimaryKey( nIdDiggSubmit, false, plugin );

                    if ( ( diggSubmit == null ) ||
                            !RBACService.isAuthorized( Digg.RESOURCE_TYPE,
                                DiggUtils.EMPTY_STRING + diggSubmit.getDigg(  ).getIdDigg(  ),
                                DigglikeResourceIdService.PERMISSION_MANAGE_DIGG_SUBMIT, adminUser ) )
                    {
                        throw new AccessDeniedException(  );
                    }

                    url.addParameter( PARAMETER_SELECTED_DIGG_SUBMIT, nIdDiggSubmit );
                }
            }

            strRedirect = url.getUrl(  );
        }
        else
        {
            strRedirect = AdminMessageService.getMessageUrl( request, MESSAGE_YOU_MUST_SELECT_DIGG_SUBMIT,
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
        return PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME );
    }
}
