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

import fr.paris.lutece.plugins.digglike.service.DiggSubmitTypeCacheService;
import fr.paris.lutece.plugins.digglike.utils.DiggUtils;
import fr.paris.lutece.portal.service.cache.AbstractCacheableService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


/**
 * class DiggSubmitTypeHome
 */
public final class DiggSubmitTypeHome
{
    // Static variable pointed at the DAO instance
    private static IDiggSubmitTypeDAO _dao = SpringContextService.getBean( "digglike.diggSubmitTypeDAO" );
    private static AbstractCacheableService _cache = new DiggSubmitTypeCacheService( );

    /**
     * Private constructor - this class need not be instantiated
     */
    private DiggSubmitTypeHome( )
    {
    }

    /**
     * Creation of an instance of diggSubmitType
     * 
     * @param diggSubmitType The instance of the diggSubmitType which contains
     *            the informations to store
     * @param plugin the Plugin
     * @return the id of the new diggSubmitType
     * 
     */
    public static int create( DiggSubmitType diggSubmitType, Plugin plugin )
    {

        if ( diggSubmitType.getPictogram( ) != null )
        {
            diggSubmitType.setIdImageResource( ImageResourceHome.create( diggSubmitType.getPictogram( ), plugin ) );
        }
        return _dao.insert( diggSubmitType, plugin );
    }

    /**
     * Update of the diggSubmitType which is specified in parameter
     * 
     * @param diggSubmitType The instance of the diggSubmitType which contains
     *            the informations to update
     * @param plugin the Plugin
     * 
     */
    public static void update( DiggSubmitType diggSubmitType, Plugin plugin )
    {

        if ( diggSubmitType.getPictogram( ) != null )
        {
            //remove old image if exist
            if ( diggSubmitType.getIdImageResource( ) != null )
            {
                ImageResourceHome.remove( diggSubmitType.getIdImageResource( ), plugin );
            }
            if ( diggSubmitType.getPictogram( ).getImage( ) != null )
            {
                diggSubmitType.setIdImageResource( ImageResourceHome.create( diggSubmitType.getPictogram( ), plugin ) );
            }
            else
            {
                diggSubmitType.setIdImageResource( null );
            }
        }

        _dao.store( diggSubmitType, plugin );
        _cache.removeKey( DiggUtils.EMPTY_STRING + diggSubmitType.getIdType( ) );
    }

    /**
     * Remove the diggSubmitType whose identifier is specified in parameter
     * 
     * @param nIdDiggSubmitType The diggSubmitType
     * @param plugin the Plugin
     */
    public static void remove( int nIdDiggSubmitType, Plugin plugin )
    {

        DiggSubmitType diggSubmitType = findByPrimaryKey( nIdDiggSubmitType, plugin );
        if ( diggSubmitType != null && diggSubmitType.getIdImageResource( ) != null )
        {
            ImageResourceHome.remove( diggSubmitType.getIdImageResource( ), plugin );
        }
        _dao.delete( nIdDiggSubmitType, plugin );
        _cache.removeKey( DiggUtils.EMPTY_STRING + nIdDiggSubmitType );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a diggSubmitType whose identifier is specified in
     * parameter
     * 
     * @param nKey The diggSubmitType primary key
     * @param plugin the Plugin
     * @return an instance of DiggSubmit
     */
    public static DiggSubmitType findByPrimaryKey( int nKey, Plugin plugin )
    {
        DiggSubmitType diggSubmitType = (DiggSubmitType) _cache.getFromCache( DiggUtils.EMPTY_STRING + nKey );
        if ( diggSubmitType == null )
        {
            diggSubmitType = _dao.load( nKey, plugin );
            _cache.putInCache( DiggUtils.EMPTY_STRING + nKey, diggSubmitType );
        }
        return diggSubmitType;
    }

    /**
     * Load the data of all the diggSubmitType who verify the filter and returns
     * them in a list
     * @param plugin the plugin
     * @return the list of diggSubmit
     */
    public static List<DiggSubmitType> getList( Plugin plugin )
    {
        return _dao.selectList( plugin );
    }

    /**
     * Returns a list of all category associate to the digg
     * @param nIdDigg the id digg
     * @param plugin the plugin
     * @return the list of category
     */
    public static List<DiggSubmitType> getListByIdDigg( int nIdDigg, Plugin plugin )
    {
        return _dao.selectListByIdDigg( nIdDigg, plugin );
    }

    /**
     * true if there is a digg associate to the DiggSubmitType
     * @param nIdDiggSubmitType the key of the digg submit type
     * @param plugin the plugin
     * @return true if there is a digg associate to the DiggSubmitType
     */
    public static boolean isAssociateToDigg( int nIdDiggSubmitType, Plugin plugin )
    {
        return _dao.isAssociateToDigg( nIdDiggSubmitType, plugin );
    }

    /**
     * Delete an association between digg and a digg submit type
     * 
     * @param nIdDigg The identifier of the digg
     * @param nIdDiggSubmitType nIdDiggSubmitType
     * @param plugin the plugin
     */
    public static void removeDiggAssociation( int nIdDigg, int nIdDiggSubmitType, Plugin plugin )
    {
        _dao.deleteDiggAssociation( nIdDigg, nIdDiggSubmitType, plugin );
    }

    /**
     * insert an association between digg and a digg submit type
     * 
     * @param nIdDigg The identifier of the digg
     * @param nIdDiggSubmitType nIdDiggSubmitType
     * @param plugin the plugin
     */
    public static void createDiggAssociation( int nIdDigg, int nIdDiggSubmitType, Plugin plugin )
    {
        _dao.insertDiggAssociation( nIdDigg, nIdDiggSubmitType, plugin );
    }
}
