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
package fr.paris.lutece.plugins.digglike.web.action;

import fr.paris.lutece.plugins.digglike.business.Digg;
import fr.paris.lutece.plugins.digglike.business.DiggSubmit;
import fr.paris.lutece.plugins.digglike.service.DiggSubmitService;
import fr.paris.lutece.plugins.digglike.service.DigglikePlugin;
import fr.paris.lutece.plugins.digglike.service.DigglikeResourceIdService;
import fr.paris.lutece.plugins.digglike.utils.DiggUtils;
import fr.paris.lutece.plugins.digglike.web.DiggJspBean;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.web.pluginaction.AbstractPluginAction;
import fr.paris.lutece.portal.web.pluginaction.DefaultPluginActionResult;
import fr.paris.lutece.portal.web.pluginaction.IPluginActionResult;

import org.apache.commons.lang.StringUtils;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * MassChangeCommentDiggSubmitAction
 *
 */
public class MassChangeCommentDiggSubmitAction extends AbstractPluginAction<DigglikeAdminSearchFields>
    implements IDigglikeAction
{
    private static final String ACTION_NAME = "Mass Change Comment DiggSubmit ";
    private static final String MESSAGE_YOU_MUST_SELECT_DIGG_SUBMIT = "digglike.message.youMustSelectDiggSubmit";
    private static final String PARAMETER_MASS_DISABLE_ACTION = "mass_disable_comment_action";
    private static final String PARAMETER_MASS_ENABLE_ACTION = "mass_enable_comment_action";

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
        return ( ( request.getParameter( PARAMETER_MASS_DISABLE_ACTION ) != null ) ||
        ( request.getParameter( PARAMETER_MASS_ENABLE_ACTION ) != null ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPluginActionResult process( HttpServletRequest request, HttpServletResponse response, AdminUser adminUser,
        DigglikeAdminSearchFields searchFields ) throws AccessDeniedException
    {
        IPluginActionResult result = new DefaultPluginActionResult(  );

        int nIdDiggSubmit;
        String strRedirect = DiggJspBean.getJspManageDiggSubmit( request );

        if ( ( searchFields.getSelectedDiggSubmit(  ) != null ) && !searchFields.getSelectedDiggSubmit(  ).isEmpty(  ) )
        {
            //test All ressource selected before update
            for ( String strIdDiggSubmit : searchFields.getSelectedDiggSubmit(  ) )
            {
                if ( StringUtils.isNotBlank( strIdDiggSubmit ) && StringUtils.isNumeric( strIdDiggSubmit ) )
                {
                    nIdDiggSubmit = DiggUtils.getIntegerParameter( strIdDiggSubmit );

                    DiggSubmit diggSubmit = DiggSubmitService.getService(  )
                                                             .findByPrimaryKey( nIdDiggSubmit, false, getPlugin(  ) );

                    if ( ( diggSubmit == null ) ||
                            !RBACService.isAuthorized( Digg.RESOURCE_TYPE,
                                DiggUtils.EMPTY_STRING + diggSubmit.getDigg(  ).getIdDigg(  ),
                                DigglikeResourceIdService.PERMISSION_MANAGE_DIGG_SUBMIT, adminUser ) )
                    {
                        throw new AccessDeniedException(  );
                    }
                }
            }

            boolean isDisabledVote = ( request.getParameter( PARAMETER_MASS_DISABLE_ACTION ) != null );

            //update all digg submit selected
            for ( String strIdDiggSubmittoUpdate : searchFields.getSelectedDiggSubmit(  ) )
            {
                if ( StringUtils.isNotBlank( strIdDiggSubmittoUpdate ) &&
                        StringUtils.isNumeric( strIdDiggSubmittoUpdate ) )
                {
                    nIdDiggSubmit = DiggUtils.getIntegerParameter( strIdDiggSubmittoUpdate );

                    DiggSubmit diggSubmit = DiggSubmitService.getService(  )
                                                             .findByPrimaryKey( nIdDiggSubmit, false, getPlugin(  ) );
                    diggSubmit.setDisableComment( isDisabledVote );
                    DiggSubmitService.getService(  ).update( diggSubmit, getPlugin(  ) );
                }
            }
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
