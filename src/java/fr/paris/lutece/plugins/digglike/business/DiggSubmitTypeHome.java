/*
 * Copyright (c) 2002-2011, Mairie de Paris
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

import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


/**
 *class DiggSubmitTypeHome
 */
public final class DiggSubmitTypeHome
{
    // Static variable pointed at the DAO instance
    private static IDiggSubmitTypeDAO _dao = (IDiggSubmitTypeDAO) SpringContextService.getPluginBean( "digglike",
            "digglike.diggSubmitTypeDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private DiggSubmitTypeHome(  )
    {
    }

    /**
     * Creation of an instance of diggSubmitType
     *
     * @param diggSubmitType The instance of the diggSubmitType which contains the informations to store
     * @param plugin the Plugin
     * @return the id of the new diggSubmitType
     *
     */
    public static int create( DiggSubmitType diggSubmitType, Plugin plugin )
    {
        return _dao.insert( diggSubmitType, plugin );
    }

    /**
    * Update of the diggSubmitType which is specified in parameter
    *
    * @param diggSubmitType The instance of the diggSubmitType which contains the informations to update
    * @param plugin the Plugin
    *
    */
    public static void update( DiggSubmitType diggSubmitType, Plugin plugin )
    {
        _dao.store( diggSubmitType, plugin );
    }

    /**
     * Remove the diggSubmitType whose identifier is specified in parameter
     *
     * @param nIdDiggSubmitType The diggSubmitType
     * @param plugin the Plugin
     */
    public static void remove( int nIdDiggSubmitType, Plugin plugin )
    {
        _dao.delete( nIdDiggSubmitType, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a diggSubmitType whose identifier is specified in parameter
     *
     * @param nKey The diggSubmitType primary key
     * @param plugin the Plugin
     * @return an instance of DiggSubmit
     */
    public static DiggSubmitType findByPrimaryKey( int nKey, Plugin plugin )
    {
        return _dao.load( nKey, plugin );
    }

    /**
     * Returns an instance of a diggSubmitType whose identifier is specified in parameter
     *
     * @param nKey The diggSubmitType primary key
     * @param plugin the Plugin
     * @return an instance of DiggSubmit
     */
    public static ImageResource getImageResource( int nKey, Plugin plugin )
    {
        return _dao.loadImage( nKey, plugin );
    }

    /**
     * Load the data of all the diggSubmitType who verify the filter and returns them in a  list
     * @param plugin the plugin
     * @return  the list of diggSubmit
     */
    public static List<DiggSubmitType> getList( Plugin plugin )
    {
        return _dao.loadList( plugin );
    }
}
