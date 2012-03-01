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
package fr.paris.lutece.plugins.digglike.business;

import fr.paris.lutece.portal.business.style.Theme;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;
import java.util.Map;


/**
 * This class provides instances management methods (create, find, ...) for Digg objects
 */
public final class DiggHome
{
    // Static variable pointed at the DAO instance
    private static IDiggDAO _dao = (IDiggDAO) SpringContextService.getPluginBean( "digglike", "digglike.diggDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private DiggHome(  )
    {
    }

    /**
     * Creation of an instance of Digg
     *
     * @param digg The instance of the Digg which contains the informations to store
     * @param plugin the Plugin
     * @return The primary key of the new digg.
     */
    public static int create( Digg digg, Plugin plugin )
    {
        return _dao.insert( digg, plugin );
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
        diggCopy.setIdDigg( create( digg, plugin ) );

        for ( IEntry entry : listEntry )
        {
            entry = EntryHome.findByPrimaryKey( entry.getIdEntry(  ), plugin );
            entry.setDigg( diggCopy );
            EntryHome.copy( entry, plugin );
        }

        for ( Category category : diggCopy.getCategories(  ) )
        {
            insertCategoryAssociated( diggCopy.getIdDigg(  ), category.getIdCategory(  ), plugin );
        }
    }

    /**
     * Update data of the digg which is specified in parameter
     *
     * @param digg  The instance of the digg which contains the informations to update
     * @param plugin the Plugin
     *
     */
    public static void update( Digg digg, Plugin plugin )
    {
        _dao.store( digg, plugin );

        //store association between digg and categories
        for ( Category category : digg.getCategories(  ) )
        {
            deleteCategoryAssociated( digg.getIdDigg(  ), category.getIdCategory(  ), plugin );
        }

        for ( Category category : digg.getCategories(  ) )
        {
            insertCategoryAssociated( digg.getIdDigg(  ), category.getIdCategory(  ), plugin );
        }
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
        List<IEntry> listEntry;
        List<DiggSubmit> listDiggSubmit;

        //delete association between digg and categories
        for ( Category category : digg.getCategories(  ) )
        {
            deleteCategoryAssociated( digg.getIdDigg(  ), category.getIdCategory(  ), plugin );
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
        listDiggSubmit = DiggSubmitHome.getDiggSubmitList( responseFilter, plugin );

        for ( DiggSubmit diggSubmit : listDiggSubmit )
        {
            DiggSubmitHome.remove( diggSubmit.getIdDiggSubmit(  ), plugin );
        }

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
        return _dao.load( nKey, plugin );
    }

    /**
         * Load the data of all the diggs who verify the filter and returns them in a  list
         * @param filter the filter
         * @param plugin the plugin
         * @return  the list of diggs
         */
    public static List<Digg> getDiggList( DiggFilter filter, Plugin plugin )
    {
        return _dao.selectDiggList( filter, plugin );
    }

    /**
     * Delete an association between digg and a category
     *
     * @param nIdDigg The identifier of the digg
     * @param nIdCategory The identifier of the category
     * @param plugin the plugin
     */
    public static void deleteCategoryAssociated( int nIdDigg, int nIdCategory, Plugin plugin )
    {
        _dao.deleteCategoryAssociated( nIdDigg, nIdCategory, plugin );
    }

    /**
     * insert an association between digg and categories
     *
     * @param nIdDigg The identifier of the digg
     * @param nIdCategory The identifier of the category
     * @param plugin the plugin
     */
    public static void insertCategoryAssociated( int nIdDigg, int nIdCategory, Plugin plugin )
    {
        _dao.insertCategoryAssociated( nIdDigg, nIdCategory, plugin );
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
