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

import fr.paris.lutece.plugins.suggest.service.SuggestSubmitTypeCacheService;
import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.portal.service.cache.AbstractCacheableService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;

/**
 * class SuggestSubmitTypeHome
 */
public final class SuggestSubmitTypeHome
{
    // Static variable pointed at the DAO instance
    private static ISuggestSubmitTypeDAO _dao = SpringContextService.getBean( "suggest.suggestSubmitTypeDAO" );
    private static AbstractCacheableService _cache = new SuggestSubmitTypeCacheService( );

    /**
     * Private constructor - this class need not be instantiated
     */
    private SuggestSubmitTypeHome( )
    {
    }

    /**
     * Creation of an instance of suggestSubmitType
     *
     * @param suggestSubmitType
     *            The instance of the suggestSubmitType which contains the informations to store
     * @param plugin
     *            the Plugin
     * @return the id of the new suggestSubmitType
     *
     */
    public static int create( SuggestSubmitType suggestSubmitType, Plugin plugin )
    {
        if ( suggestSubmitType.getPictogram( ) != null )
        {
            suggestSubmitType.setIdImageResource( ImageResourceHome.create( suggestSubmitType.getPictogram( ), plugin ) );
        }

        return _dao.insert( suggestSubmitType, plugin );
    }

    /**
     * Update of the suggestSubmitType which is specified in parameter
     *
     * @param suggestSubmitType
     *            The instance of the suggestSubmitType which contains the informations to update
     * @param plugin
     *            the Plugin
     *
     */
    public static void update( SuggestSubmitType suggestSubmitType, Plugin plugin )
    {
        if ( suggestSubmitType.getPictogram( ) != null )
        {
            // remove old image if exist
            if ( suggestSubmitType.getIdImageResource( ) != null )
            {
                ImageResourceHome.remove( suggestSubmitType.getIdImageResource( ), plugin );
            }

            if ( suggestSubmitType.getPictogram( ).getImage( ) != null )
            {
                suggestSubmitType.setIdImageResource( ImageResourceHome.create( suggestSubmitType.getPictogram( ), plugin ) );
            }
            else
            {
                suggestSubmitType.setIdImageResource( null );
            }
        }

        _dao.store( suggestSubmitType, plugin );
        _cache.removeKey( SuggestUtils.EMPTY_STRING + suggestSubmitType.getIdType( ) );
    }

    /**
     * Remove the suggestSubmitType whose identifier is specified in parameter
     *
     * @param nIdSuggestSubmitType
     *            The suggestSubmitType
     * @param plugin
     *            the Plugin
     */
    public static void remove( int nIdSuggestSubmitType, Plugin plugin )
    {
        SuggestSubmitType suggestSubmitType = findByPrimaryKey( nIdSuggestSubmitType, plugin );

        if ( ( suggestSubmitType != null ) && ( suggestSubmitType.getIdImageResource( ) != null ) )
        {
            ImageResourceHome.remove( suggestSubmitType.getIdImageResource( ), plugin );
        }

        _dao.delete( nIdSuggestSubmitType, plugin );
        _cache.removeKey( SuggestUtils.EMPTY_STRING + nIdSuggestSubmitType );
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a suggestSubmitType whose identifier is specified in parameter
     *
     * @param nKey
     *            The suggestSubmitType primary key
     * @param plugin
     *            the Plugin
     * @return an instance of SuggestSubmit
     */
    public static SuggestSubmitType findByPrimaryKey( int nKey, Plugin plugin )
    {
        SuggestSubmitType suggestSubmitType = (SuggestSubmitType) _cache.getFromCache( SuggestUtils.EMPTY_STRING + nKey );

        if ( suggestSubmitType == null )
        {
            suggestSubmitType = _dao.load( nKey, plugin );
            _cache.putInCache( SuggestUtils.EMPTY_STRING + nKey, suggestSubmitType );
        }

        return suggestSubmitType;
    }

    /**
     * Load the data of all the suggestSubmitType who verify the filter and returns them in a list
     * 
     * @param plugin
     *            the plugin
     * @return the list of suggestSubmit
     */
    public static List<SuggestSubmitType> getList( Plugin plugin )
    {
        return _dao.selectList( plugin );
    }

    /**
     * Returns a list of all category associate to the suggest
     * 
     * @param nIdSuggest
     *            the id suggest
     * @param plugin
     *            the plugin
     * @return the list of category
     */
    public static List<SuggestSubmitType> getListByIdSuggest( int nIdSuggest, Plugin plugin )
    {
        return _dao.selectListByIdSuggest( nIdSuggest, plugin );
    }

    /**
     * true if there is a suggest associate to the SuggestSubmitType
     * 
     * @param nIdSuggestSubmitType
     *            the key of the suggest submit type
     * @param plugin
     *            the plugin
     * @return true if there is a suggest associate to the SuggestSubmitType
     */
    public static boolean isAssociateToSuggest( int nIdSuggestSubmitType, Plugin plugin )
    {
        return _dao.isAssociateToSuggest( nIdSuggestSubmitType, plugin );
    }

    /**
     * Delete an association between suggest and a suggest submit type
     *
     * @param nIdSuggest
     *            The identifier of the suggest
     * @param nIdSuggestSubmitType
     *            nIdSuggestSubmitType
     * @param plugin
     *            the plugin
     */
    public static void removeSuggestAssociation( int nIdSuggest, int nIdSuggestSubmitType, Plugin plugin )
    {
        _dao.deleteSuggestAssociation( nIdSuggest, nIdSuggestSubmitType, plugin );
    }

    /**
     * insert an association between suggest and a suggest submit type
     *
     * @param nIdSuggest
     *            The identifier of the suggest
     * @param nIdSuggestSubmitType
     *            nIdSuggestSubmitType
     * @param plugin
     *            the plugin
     */
    public static void createSuggestAssociation( int nIdSuggest, int nIdSuggestSubmitType, Plugin plugin )
    {
        _dao.insertSuggestAssociation( nIdSuggest, nIdSuggestSubmitType, plugin );
    }
}
