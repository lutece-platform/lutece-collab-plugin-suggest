/*
 * Copyright (c) 2002-2020, Mairie de Paris
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
package fr.paris.lutece.plugins.suggest.business;

import fr.paris.lutece.plugins.suggest.business.attribute.SuggestAttributeHome;
import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.portal.business.style.Theme;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

/**
 * This class provides instances management methods (create, find, ...) for Suggest objects
 */
public final class SuggestHome
{
    // Static variable pointed at the DAO instance
    private static ISuggestDAO _dao = SpringContextService.getBean( "suggest.suggestDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private SuggestHome( )
    {
    }

    /**
     * Creation of an instance of Suggest
     *
     * @param suggest
     *            The instance of the Suggest which contains the informations to store
     * @param plugin
     *            the Plugin
     * @return The primary key of the new suggest.
     */
    public static int create( Suggest suggest, Plugin plugin )
    {
        if ( suggest.getImage( ) != null )
        {
            suggest.setIdImageResource( ImageResourceHome.create( suggest.getImage( ), plugin ) );
        }

        int nIdSuggest = _dao.insert( suggest, plugin );

        // Create directory attributes associated to the directory
        Map<String, Object> mapAttributes = SuggestUtils.depopulate( suggest );
        SuggestAttributeHome.create( suggest.getIdSuggest( ), mapAttributes );

        return nIdSuggest;
    }

    /**
     * Copy of an instance of Suggest
     *
     * @param suggest
     *            The instance of the suggest who must copy
     * @param plugin
     *            the Plugin
     *
     */
    public static void copy( Suggest suggest, Plugin plugin )
    {
        Suggest suggestCopy = suggest;

        List<IEntry> listEntry;
        EntryFilter filter = new EntryFilter( );
        filter.setIdSuggest( suggest.getIdSuggest( ) );
        listEntry = EntryHome.getEntryList( filter, plugin );
        suggestCopy.setActive( false );
        suggestCopy.setDefaultSuggest( false );
        suggestCopy.setIdSuggest( create( suggest, plugin ) );

        for ( IEntry entry : listEntry )
        {
            entry = EntryHome.findByPrimaryKey( entry.getIdEntry( ), plugin );
            entry.setSuggest( suggestCopy );
            EntryHome.copy( entry, plugin );
        }

        for ( Category category : suggestCopy.getCategories( ) )
        {
            CategoryHome.createSuggestAssociation( suggestCopy.getIdSuggest( ), category.getIdCategory( ), plugin );
        }

        for ( SuggestSubmitType suggestSubmitType : suggestCopy.getSuggestSubmitTypes( ) )
        {
            SuggestSubmitTypeHome.createSuggestAssociation( suggestCopy.getIdSuggest( ), suggestSubmitType.getIdType( ), plugin );
        }
    }

    /**
     * Update data of the suggest which is specified in parameter
     *
     * @param suggest
     *            The instance of the suggest which contains the informations to update
     * @param plugin
     *            the Plugin
     *
     */
    public static void update( Suggest suggest, Plugin plugin )
    {
        if ( suggest.getImage( ) != null )
        {
            // remove old image if exist
            if ( ( suggest.getIdImageResource( ) != null ) && ( suggest.getIdImageResource( ) != SuggestUtils.CONSTANT_ID_NULL ) )
            {
                ImageResourceHome.remove( suggest.getIdImageResource( ), plugin );
            }

            if ( suggest.getImage( ).getImage( ) != null )
            {
                suggest.setIdImageResource( ImageResourceHome.create( suggest.getImage( ), plugin ) );
            }
            else
            {
                suggest.setIdImageResource( SuggestUtils.CONSTANT_ID_NULL );
            }
        }

        // Remove directory attributes associated to the directory
        SuggestAttributeHome.remove( suggest.getIdSuggest( ) );

        // Add directory Attribute
        Map<String, Object> mapAttributes = SuggestUtils.depopulate( suggest );
        SuggestAttributeHome.create( suggest.getIdSuggest( ), mapAttributes );

        _dao.store( suggest, plugin );
    }

    /**
     * Remove thesuggest whose identifier is specified in parameter
     *
     * @param nIdSuggest
     *            The suggest Id
     * @param plugin
     *            the Plugin
     */
    public static void remove( int nIdSuggest, Plugin plugin )
    {
        Suggest suggest = findByPrimaryKey( nIdSuggest, plugin );
        if ( suggest == null )
        {
            return;
        }
        List<IEntry> listEntry;
        List<Integer> listIdSuggestSubmit;

        // delete image resource associate
        if ( ( suggest.getIdImageResource( ) != null ) && ( suggest.getIdImageResource( ) != SuggestUtils.CONSTANT_ID_NULL ) )
        {
            ImageResourceHome.remove( suggest.getIdImageResource( ), plugin );
        }

        // delete association between suggest and categories
        for ( Category category : suggest.getCategories( ) )
        {
            CategoryHome.removeSuggestAssociation( suggest.getIdSuggest( ), category.getIdCategory( ), plugin );
        }

        // delete association between suggest and suggest submit type
        for ( SuggestSubmitType suggestSubmitType : suggest.getSuggestSubmitTypes( ) )
        {
            SuggestSubmitTypeHome.removeSuggestAssociation( suggest.getIdSuggest( ), suggestSubmitType.getIdType( ), plugin );
        }

        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdSuggest( suggest.getIdSuggest( ) );
        listEntry = EntryHome.getEntryList( entryFilter, plugin );

        for ( IEntry entry : listEntry )
        {
            EntryHome.remove( entry.getIdEntry( ), plugin );
        }

        SubmitFilter responseFilter = new SubmitFilter( );
        responseFilter.setIdSuggest( nIdSuggest );
        listIdSuggestSubmit = SuggestSubmitHome.getSuggestSubmitListId( responseFilter, plugin );

        for ( Integer nIdSuggestSubmit : listIdSuggestSubmit )
        {
            SuggestSubmitHome.remove( nIdSuggestSubmit, plugin );
        }

        // Remove suggest attributes associated to the suggest
        SuggestAttributeHome.remove( nIdSuggest );

        _dao.delete( nIdSuggest, plugin );
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders
    /**
     * Returns an instance of a suggest whose identifier is specified in parameter
     *
     * @param nKey
     *            The suggest primary key
     * @param plugin
     *            the Plugin
     * @return an instance of Suggest
     */
    public static Suggest findByPrimaryKey( int nKey, Plugin plugin )
    {
        Suggest suggest = _dao.load( nKey, plugin );

        if ( suggest != null )
        {
            suggest.setCategories( CategoryHome.getListByIdSuggest( nKey, plugin ) );
            suggest.setSuggestSubmiTypes( SuggestSubmitTypeHome.getListByIdSuggest( nKey, plugin ) );

            Map<String, Object> mapAttributes = SuggestAttributeHome.findByPrimaryKey( nKey );

            try
            {
                BeanUtils.populate( suggest, mapAttributes );
            }
            catch( IllegalAccessException | InvocationTargetException e )
            {
                AppLogService.error( e );
            }
        }

        return suggest;
    }

    /**
     * Load the data of all the suggests who verify the filter and returns them in a list
     * 
     * @param filter
     *            the filter
     * @param plugin
     *            the plugin
     * @return the list of suggests, or an empty list if no suggest was found
     */
    public static List<Suggest> getSuggestList( SuggestFilter filter, Plugin plugin )
    {
        List<Suggest> listSuggest = _dao.selectSuggestList( filter, plugin );

        for ( Suggest suggest : listSuggest )
        {
            Map<String, Object> mapAttributes = SuggestAttributeHome.findByPrimaryKey( suggest.getIdSuggest( ) );

            try
            {
                BeanUtils.populate( suggest, mapAttributes );
            }
            catch( IllegalAccessException | InvocationTargetException e )
            {
                AppLogService.error( e );
            }
        }

        return listSuggest;
    }

    /**
     * Update the value of the field which will be use to sort the suggestSubmit
     * 
     * @param nIdSuggest
     *            the id of the suggestSubmit
     * @param nNewField
     *            the new number of order
     * @param plugin
     *            The Plugin object
     */
    public static void updateSuggestSortField( int nNewField, int nIdSuggest, Plugin plugin )
    {
        _dao.storeSuggestOrderField( nIdSuggest, nNewField, plugin );
    }

    /**
     * Load the xpage themes for all suggests
     * 
     * @param plugin
     *            the plugin
     * @return the map containing the theme
     *
     */
    public static Map<Integer, Theme> getXPageThemes( Plugin plugin )
    {
        return _dao.getXPageThemesMap( plugin );
    }
}
