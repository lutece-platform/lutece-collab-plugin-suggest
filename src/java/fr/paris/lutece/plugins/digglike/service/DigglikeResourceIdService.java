/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.digglike.service;

import fr.paris.lutece.plugins.digglike.business.Digg;
import fr.paris.lutece.plugins.digglike.business.DiggFilter;
import fr.paris.lutece.plugins.digglike.business.DiggHome;
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
public class DigglikeResourceIdService extends ResourceIdService
{
    /** Permission for creating a digg */
    public static final String PERMISSION_CREATE = "CREATE";

    /** Permission for deleting a digg */
    public static final String PERMISSION_DELETE = "DELETE";

    /** Permission for modifying a digg */
    public static final String PERMISSION_MODIFY = "MODIFY";

    /** Permission for copying a digg */
    public static final String PERMISSION_COPY = "COPY";

    /** Permission for viewing digg submit */
    public static final String PERMISSION_MANAGE_DIGG_SUBMIT = "MANAGE_DIGG_SUBMIT";

    /** Permission for enable or disable digg */
    public static final String PERMISSION_CHANGE_STATE = "CHANGE_STATE";
    private static final String PROPERTY_LABEL_RESOURCE_TYPE = "digglike.permission.label.resourceType";
    private static final String PROPERTY_LABEL_CREATE = "digglike.permission.label.create";
    private static final String PROPERTY_LABEL_DELETE = "digglike.permission.label.delete";
    private static final String PROPERTY_LABEL_MODIFY = "digglike.permission.label.modify";
    private static final String PROPERTY_LABEL_COPY = "digglike.permission.label.copy";
    private static final String PROPERTY_LABEL_MANAGE_DIGG_SUBMIT = "digglike.permission.label.manageDiggSubmit";
    private static final String PROPERTY_LABEL_CHANGE_STATE = "digglike.permission.label.changeState";

    /** Creates a new instance of DigglikeTypeResourceIdService */
    public DigglikeResourceIdService(  )
    {
        setPluginName( DigglikePlugin.PLUGIN_NAME );
    }

    /**
     * Initializes the service
     */
    public void register(  )
    {
        ResourceType rt = new ResourceType(  );
        rt.setResourceIdServiceClass( DigglikeResourceIdService.class.getName(  ) );
        rt.setPluginName( DigglikePlugin.PLUGIN_NAME );
        rt.setResourceTypeKey( Digg.RESOURCE_TYPE );
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
        p.setPermissionKey( PERMISSION_MANAGE_DIGG_SUBMIT );
        p.setPermissionTitleKey( PROPERTY_LABEL_MANAGE_DIGG_SUBMIT );
        rt.registerPermission( p );

        p = new Permission(  );
        p.setPermissionKey( PERMISSION_DELETE );
        p.setPermissionTitleKey( PROPERTY_LABEL_DELETE );
        rt.registerPermission( p );

        ResourceTypeManager.registerResourceType( rt );
    }

    /**
     * Returns a list of digg resource ids
     * @param locale The current locale
     * @return A list of digg resource ids
     */
    public ReferenceList getResourceIdList( Locale locale )
    {
        ReferenceList referenceList = new ReferenceList(  );
        List<Digg> listDigg = DiggHome.getDiggList( new DiggFilter(  ),
                PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME ) );

        for ( Digg digg : listDigg )
        {
            referenceList.addItem( digg.getIdDigg(  ), digg.getTitle(  ) );
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
        int nIdDigg = -1;

        try
        {
            nIdDigg = Integer.parseInt( strId );
        }
        catch ( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        Digg digg = DiggHome.findByPrimaryKey( nIdDigg, PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME ) );

        return digg.getTitle(  );
    }
}
