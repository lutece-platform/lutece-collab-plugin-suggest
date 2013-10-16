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
package fr.paris.lutece.plugins.digglike.business;

import fr.paris.lutece.plugins.digglike.business.attribute.DiggAttributeHome;
import fr.paris.lutece.plugins.digglike.utils.DiggUtils;
import fr.paris.lutece.portal.business.style.Theme;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;


/**
 * This class provides instances management methods (create, find, ...) for Digg
 * objects
 */
public final class DiggHome
{
    // Static variable pointed at the DAO instance
    private static IDiggDAO _dao = SpringContextService.getBean( "digglike.diggDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private DiggHome(  )
    {
    }

    /**
     * Creation of an instance of Digg
     *
     * @param digg The instance of the Digg which contains the informations to
     *            store
     * @param plugin the Plugin
     * @return The primary key of the new digg.
     */
    public static int create( Digg digg, Plugin plugin )
    {
        if ( digg.getImage(  ) != null )
        {
            digg.setIdImageResource( ImageResourceHome.create( digg.getImage(  ), plugin ) );
        }

        int nIdDigg = _dao.insert( digg, plugin );

        // Create directory attributes associated to the directory
        Map<String, Object> mapAttributes = DiggUtils.depopulate( digg );
        DiggAttributeHome.create( digg.getIdDigg(  ), mapAttributes );

        return nIdDigg;
    }

    /**
     * Copy of an instance of Digg
     *
     * @param digg The instance of the digg who must copy
     * @param plugin the Plugin
     *
     */
    public static void copy( Digg digg, Plugin plugin )
    {
        Digg diggCopy = digg;

        List<IEntry> listEntry;
        EntryFilter filter = new EntryFilter(  );
        filter.setIdDigg( digg.getIdDigg(  ) );
        listEntry = EntryHome.getEntryList( filter, plugin );
        diggCopy.setActive( false );
        diggCopy.setDefaultDigg( false );
        diggCopy.setIdDigg( create( digg, plugin ) );

        for ( IEntry entry : listEntry )
        {
            entry = EntryHome.findByPrimaryKey( entry.getIdEntry(  ), plugin );
            entry.setDigg( diggCopy );
            EntryHome.copy( entry, plugin );
        }

        for ( Category category : diggCopy.getCategories(  ) )
        {
            CategoryHome.createDiggAssociation( diggCopy.getIdDigg(  ), category.getIdCategory(  ), plugin );
        }

        for ( DiggSubmitType diggSubmitType : diggCopy.getDiggSubmitTypes(  ) )
        {
            DiggSubmitTypeHome.createDiggAssociation( diggCopy.getIdDigg(  ), diggSubmitType.getIdType(  ), plugin );
        }
    }

    /**
     * Update data of the digg which is specified in parameter
     *
     * @param digg The instance of the digg which contains the informations to
     *            update
     * @param plugin the Plugin
     *
     */
    public static void update( Digg digg, Plugin plugin )
    {
        if ( digg.getImage(  ) != null )
        {
            //remove old image if exist
            if ( ( digg.getIdImageResource(  ) != null ) &&
                    ( digg.getIdImageResource(  ) != DiggUtils.CONSTANT_ID_NULL ) )
            {
                ImageResourceHome.remove( digg.getIdImageResource(  ), plugin );
            }

            if ( digg.getImage(  ).getImage(  ) != null )
            {
                digg.setIdImageResource( ImageResourceHome.create( digg.getImage(  ), plugin ) );
            }
            else
            {
                digg.setIdImageResource( DiggUtils.CONSTANT_ID_NULL );
            }
        }

        // Remove directory attributes associated to the directory
        DiggAttributeHome.remove( digg.getIdDigg(  ) );

        // Add directory Attribute
        Map<String, Object> mapAttributes = DiggUtils.depopulate( digg );
        DiggAttributeHome.create( digg.getIdDigg(  ), mapAttributes );

        _dao.store( digg, plugin );
    }

    /**
     * Remove thedigg whose identifier is specified in parameter
     *
     * @param nIdDigg The digg Id
     * @param plugin the Plugin
     */
    public static void remove( int nIdDigg, Plugin plugin )
    {
        Digg digg = findByPrimaryKey( nIdDigg, plugin );
        if ( digg == null )
        {
            return;
        }
        List<IEntry> listEntry;
        List<Integer> listIdDiggSubmit;

        //delete image resource associate
        if ( ( digg.getIdImageResource( ) != null ) &&
                ( digg.getIdImageResource(  ) != DiggUtils.CONSTANT_ID_NULL ) )
        {
            ImageResourceHome.remove( digg.getIdImageResource(  ), plugin );
        }

        //delete association between digg and categories
        for ( Category category : digg.getCategories(  ) )
        {
            CategoryHome.removeDiggAssociation( digg.getIdDigg(  ), category.getIdCategory(  ), plugin );
        }

        //delete association between digg and digg submit type
        for ( DiggSubmitType diggSubmitType : digg.getDiggSubmitTypes(  ) )
        {
            DiggSubmitTypeHome.removeDiggAssociation( digg.getIdDigg(  ), diggSubmitType.getIdType(  ), plugin );
        }

        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setIdDigg( digg.getIdDigg(  ) );
        listEntry = EntryHome.getEntryList( entryFilter, plugin );

        for ( IEntry entry : listEntry )
        {
            EntryHome.remove( entry.getIdEntry(  ), plugin );
        }

        SubmitFilter responseFilter = new SubmitFilter(  );
        responseFilter.setIdDigg( nIdDigg );
        listIdDiggSubmit = DiggSubmitHome.getDiggSubmitListId( responseFilter, plugin );

        for ( Integer nIdDiggSubmit : listIdDiggSubmit )
        {
            DiggSubmitHome.remove( nIdDiggSubmit, plugin );
        }

        // Remove digg attributes associated to the digg
        DiggAttributeHome.remove( nIdDigg );

        _dao.delete( nIdDigg, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders
    /**
     * Returns an instance of a digg whose identifier is specified in parameter
     *
     * @param nKey The digg primary key
     * @param plugin the Plugin
     * @return an instance of Digg
     */
    public static Digg findByPrimaryKey( int nKey, Plugin plugin )
    {
        Digg digg = _dao.load( nKey, plugin );

        if ( digg != null )
        {
            digg.setCategories( CategoryHome.getListByIdDigg( nKey, plugin ) );
            digg.setDiggSubmiTypes( DiggSubmitTypeHome.getListByIdDigg( nKey, plugin ) );

            Map<String, Object> mapAttributes = DiggAttributeHome.findByPrimaryKey( nKey );

            try
            {
                BeanUtils.populate( digg, mapAttributes );
            }
            catch ( IllegalAccessException e )
            {
                AppLogService.error( e );
            }
            catch ( InvocationTargetException e )
            {
                AppLogService.error( e );
            }
        }

        return digg;
    }

    /**
     * Load the data of all the diggs who verify the filter and returns them in
     * a list
     * @param filter the filter
     * @param plugin the plugin
     * @return the list of diggs, or an empty list if no digg was found
     */
    public static List<Digg> getDiggList( DiggFilter filter, Plugin plugin )
    {
        List<Digg> listDigg = _dao.selectDiggList( filter, plugin );

        for ( Digg digg : listDigg )
        {
            Map<String, Object> mapAttributes = DiggAttributeHome.findByPrimaryKey( digg.getIdDigg(  ) );

            try
            {
                BeanUtils.populate( digg, mapAttributes );
            }
            catch ( IllegalAccessException e )
            {
                AppLogService.error( e );
            }
            catch ( InvocationTargetException e )
            {
                AppLogService.error( e );
            }
        }

        return listDigg;
    }

    /**
     * Update the value of the field which will be use to sort the diggSubmit
     * @param nIdDigg the id of the diggSubmit
     * @param nNewField the new number of order
     * @param plugin The Plugin object
     */
    public static void updateDiggSortField( int nNewField, int nIdDigg, Plugin plugin )
    {
        _dao.storeDiggOrderField( nIdDigg, nNewField, plugin );
    }

    /**
     * Load the xpage themes for all diggs
     * @param plugin the plugin
     * @return the map containing the theme
     *
     */
    public static Map<Integer, Theme> getXPageThemes( Plugin plugin )
    {
        return _dao.getXPageThemesMap( plugin );
    }
}
