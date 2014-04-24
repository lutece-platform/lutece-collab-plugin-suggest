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
package fr.paris.lutece.plugins.digglike.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;


/**
 *
 * class voteTypeHome
 *
 */
public final class VideoTypeHome
{
    // Static variable pointed at the DAO instance
    private static IVideoTypeDAO _dao = SpringContextService.getBean( "digglike.videoTypeDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private VideoTypeHome(  )
    {
    }

    /**
     * Returns an instance of a video Type whose identifier is specified in
     * parameter
     *
     * @param idKey The video type primary key
     * @param plugin the Plugin
     * @return an instance of voteType
     */
    public static VideoType findByPrimaryKey( int idKey, Plugin plugin )
    {
        return _dao.load( idKey, plugin );
    }

    /**
     * Creation of an instance of diggSubmit
     *
     * @param videoType The instance of the videoType which contains the
     *            informations to store
     * @param plugin the Plugin
     * @return the id of the new videoType
     * @throws com.mysql.jdbc.PacketTooBigException Exception
     */
    public static int create( VideoType videoType, Plugin plugin )
        throws com.mysql.jdbc.PacketTooBigException
    {
        return _dao.insert( videoType, plugin );
    }

    /**
     * Update of the diggSubmit which is specified in parameter
     *
     * @param video The instance of the videoType which contains the
     *            informations to update
     * @param plugin the Plugin
     *
     */
    public static void update( VideoType video, Plugin plugin )
    {
        _dao.store( video, plugin );
    }

    /**
     * Remove the video whose identifier is specified in parameter
     *
     * @param nIdDiggSubmit The identifier of the nIdDiggSubmit / Video
     * @param plugin the Plugin
     */
    public static void remove( int nIdDiggSubmit, Plugin plugin )
    {
        _dao.delete( nIdDiggSubmit, plugin );
    }
}
