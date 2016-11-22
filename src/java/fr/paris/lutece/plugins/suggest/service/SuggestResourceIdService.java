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
package fr.paris.lutece.plugins.suggest.service;

import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestFilter;
import fr.paris.lutece.plugins.suggest.business.SuggestHome;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;
import java.util.Locale;


/**
 *
 * class FormResourceIdService
 *
 */
public class SuggestResourceIdService extends ResourceIdService
{
    /** Permission for creating a suggest */
    public static final String PERMISSION_CREATE = "CREATE";

    /** Permission for deleting a suggest */
    public static final String PERMISSION_DELETE = "DELETE";

    /** Permission for modifying a suggest */
    public static final String PERMISSION_MODIFY = "MODIFY";

    /** Permission for copying a suggest */
    public static final String PERMISSION_COPY = "COPY";

    /** Permission for managing advanced parameters */
    public static final String PERMISSION_MANAGE_ADVANCED_PARAMETERS = "MANAGE_ADVANCED_PARAMETERS";

    /** Permission for viewing suggest submit */
    public static final String PERMISSION_MANAGE_SUGGEST_SUBMIT = "MANAGE_SUGGEST_SUBMIT";

    /** Permission for update All suggest submit */
    public static final String PERMISSION_UPDATE_ALL_SUGGEST_SUBMIT = "UPDATE_ALL_SUGGEST_SUBMIT";

    /** Permission for enable or disable suggest */
    public static final String PERMISSION_CHANGE_STATE = "CHANGE_STATE";
    private static final String PROPERTY_LABEL_RESOURCE_TYPE = "suggest.permission.label.resourceType";
    private static final String PROPERTY_LABEL_CREATE = "suggest.permission.label.create";
    private static final String PROPERTY_LABEL_DELETE = "suggest.permission.label.delete";
    private static final String PROPERTY_LABEL_MODIFY = "suggest.permission.label.modify";
    private static final String PROPERTY_LABEL_COPY = "suggest.permission.label.copy";
    private static final String PROPERTY_LABEL_MANAGE_SUGGEST_SUBMIT = "suggest.permission.label.manageSuggestSubmit";
    private static final String PROPERTY_LABEL_CHANGE_STATE = "suggest.permission.label.changeState";
    private static final String PROPERTY_LABEL_MANAGE_ADVANCED_PARAMETERS = "suggest.permission.label.manageAdvancedParameters";

    /** Creates a new instance of SuggestTypeResourceIdService */
    public SuggestResourceIdService(  )
    {
        setPluginName( SuggestPlugin.PLUGIN_NAME );
    }

    /**
     * Initializes the service
     */
    public void register(  )
    {
        ResourceType rt = new ResourceType(  );
        rt.setResourceIdServiceClass( SuggestResourceIdService.class.getName(  ) );
        rt.setPluginName( SuggestPlugin.PLUGIN_NAME );
        rt.setResourceTypeKey( Suggest.RESOURCE_TYPE );
        rt.setResourceTypeLabelKey( PROPERTY_LABEL_RESOURCE_TYPE );

        Permission p = new Permission(  );
        p.setPermissionKey( PERMISSION_CREATE );
        p.setPermissionTitleKey( PROPERTY_LABEL_CREATE );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_MODIFY );
        p.setPermissionTitleKey( PROPERTY_LABEL_MODIFY );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_COPY );
        p.setPermissionTitleKey( PROPERTY_LABEL_COPY );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_CHANGE_STATE );
        p.setPermissionTitleKey( PROPERTY_LABEL_CHANGE_STATE );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_MANAGE_SUGGEST_SUBMIT );
        p.setPermissionTitleKey( PROPERTY_LABEL_MANAGE_SUGGEST_SUBMIT );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_DELETE );
        p.setPermissionTitleKey( PROPERTY_LABEL_DELETE );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_MANAGE_ADVANCED_PARAMETERS );
        p.setPermissionTitleKey( PROPERTY_LABEL_MANAGE_ADVANCED_PARAMETERS );
        rt.registerPermission( p );

        ResourceTypeManager.registerResourceType( rt );
    }

    /**
     * Returns a list of suggest resource ids
     * @param locale The current locale
     * @return A list of suggest resource ids
     */
    public ReferenceList getResourceIdList( Locale locale )
    {
        ReferenceList referenceList = new ReferenceList(  );
        List<Suggest> listSuggest = SuggestHome.getSuggestList( new SuggestFilter(  ),
                PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME ) );

        for ( Suggest suggest : listSuggest )
        {
            referenceList.addItem( suggest.getIdSuggest(  ), suggest.getTitle(  ) );
        }

        return referenceList;
    }

    /**
     * Returns the Title of a given resource
     * @param strId The Id of the resource
     * @param locale The current locale
     * @return The Title of a given resource
     */
    public String getTitle( String strId, Locale locale )
    {
        int nIdSuggest = -1;

        try
        {
            nIdSuggest = Integer.parseInt( strId );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        Suggest suggest = SuggestHome.findByPrimaryKey( nIdSuggest, PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME ) );

        return suggest.getTitle(  );
    }
}
