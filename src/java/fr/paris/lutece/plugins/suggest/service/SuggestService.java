/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.suggest.service;

import java.util.List;



import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestFilter;
import fr.paris.lutece.plugins.suggest.business.SuggestHome;
import fr.paris.lutece.plugins.suggest.business.SuggestRegularExpressionRemovalListener;
import fr.paris.lutece.plugins.suggest.business.SuggestRoleRemovalListener;
import fr.paris.lutece.plugins.suggest.business.SuggestWorkgroupRemovalListener;
import fr.paris.lutece.plugins.suggest.service.workflow.SuggestWorkflowService;
import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.util.BeanUtils;
import fr.paris.lutece.portal.service.util.RemovalListenerService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.ServletContext;

/**
 *
 * SuggestService
 *
 */
@ApplicationScoped
public class SuggestService
{
    private static final String PARAMETER_ID_SUGGEST = "id_suggest";

    @Inject
    @Named( BeanUtils.BEAN_WORKGROUP_REMOVAL_SERVICE )
    private RemovalListenerService _workgroupRemovalService;

    @Inject
    @Named( BeanUtils.BEAN_REGULAR_EXPRESSION_REMOVAL_SERVICE )
    private RemovalListenerService _regularExpressionRemovalService;

    @Inject
    @Named( BeanUtils.BEAN_ROLE_REMOVAL_SERVICE )
    private RemovalListenerService _roleRemovalService;

    /**
     * Registers the removal listeners when the application starts.
     *
     * @param context
     *            the servlet context
     */
    public void initializedService( @Observes @Initialized( ApplicationScoped.class ) ServletContext context )
    {
        _workgroupRemovalService.registerListener( new SuggestWorkgroupRemovalListener( ) );
        _regularExpressionRemovalService.registerListener( new SuggestRegularExpressionRemovalListener( ) );
        _roleRemovalService.registerListener( new SuggestRoleRemovalListener( ) );
    }

    /**
     *
     * @return the Id of the default Suggest
     */
    public int getIdDefaultSuggest( )
    {
        int nIdDefaultSuggest;
        SuggestFilter filterDefaultSuggest = new SuggestFilter( );
        filterDefaultSuggest.setIdDefaultSuggest( SuggestFilter.ID_TRUE );

        List<Suggest> listDefaultSuggest = SuggestHome.getSuggestList( filterDefaultSuggest, PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME ) );
        nIdDefaultSuggest = ( ( listDefaultSuggest != null ) && ( !listDefaultSuggest.isEmpty( ) ) ) ? ( listDefaultSuggest.get( 0 ) ).getIdSuggest( )
                : SuggestUtils.CONSTANT_ID_NULL;

        return nIdDefaultSuggest;
    }

    /**
     * Tells whether the user holds the permission on the suggest and belongs to its workgroup
     * 
     * @param nIdSuggest
     *            The suggest id
     * @param strPermission
     *            The permission
     * @param user
     *            The user
     * @return true if the suggest exists and the user may act on it
     */
    public static boolean isAuthorized( int nIdSuggest, String strPermission, User user )
    {
        Suggest suggest = SuggestHome.findByPrimaryKey( nIdSuggest, PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME ) );

        return ( suggest != null ) && RBACService.isAuthorized( Suggest.RESOURCE_TYPE, Integer.toString( nIdSuggest ), strPermission, user )
                && AdminWorkgroupService.isAuthorized( suggest, user );
    }

    /**
     * Remove the specified Suggest and all its associated elements ( SuggestSubmit, workflow resources... )
     * 
     * @param nIdSuggest
     *            The ID of the Suggest to remove
     * @param user
     *            The user
     */
    public void removeSuggest( int nIdSuggest, User user )
    {
        SuggestSubmitService.inTransaction( PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME ), ( ) -> {
            // Remove the Suggest's workflow related resources
            removeSuggestWorkflowResources( nIdSuggest, user );
            // Remove the Suggest and its content
            SuggestHome.remove( nIdSuggest, PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME ) );
        } );
    }

    /**
     * Remove the workflow resources from a Suggest (consultation)
     * 
     * @param nIdSuggest
     *            The Suggest's identifier
     * @param user
     *            The user
     */
    public static void removeSuggestWorkflowResources( int nIdSuggest, User user )
    {
        Suggest suggest = SuggestHome.findByPrimaryKey( nIdSuggest, PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME ) );
        int nIdWorkflow = suggest.getIdWorkflow( );
        // Remove the workflow resources linked to the Suggest element
        SuggestWorkflowService.removeResources( nIdWorkflow, nIdSuggest, user );
    }
}
