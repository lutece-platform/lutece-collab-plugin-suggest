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

import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for Response objects
 */
public final class ResponseHome
{
    // Static variable pointed at the DAO instance
    private static IResponseDAO _dao = (IResponseDAO) SpringContextService.getPluginBean( "digglike",
            "digglike.responseDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private ResponseHome(  )
    {
    }

    /**
     * Creation of an instance of response
     *
     * @param response The instance of the response which contains the informations to store
     * @param plugin the Plugin
     *
     */
    public static void create( Response response, Plugin plugin )
    {
        _dao.insert( response, plugin );
    }

    /**
     * Update of the response which is specified in parameter
     *
     * @param response The instance of the Response which contains the informations to update
     * @param plugin the Plugin
     *
     */
    public static void update( Response response, Plugin plugin )
    {
        _dao.store( response, plugin );
    }

    /**
     * Remove  response  associate to the digg submit whose identifier is specified in parameter
     *
     * @param nIdResponse The diggSubmitKey
     * @param plugin the Plugin
     */
    public static void remove( int nIdResponse, Plugin plugin )
    {
        _dao.delete( nIdResponse, plugin );
    }
    
    
    /**
     * Remove  an Image  associate to the response whose identifier is specified in parameter
     *
     * @param nIdResponse The nIdResponse
     * @param plugin the Plugin
     */
    public static void removeImage( int nIdResponse, Plugin plugin )
    {
        _dao.deleteImageResource( nIdResponse, plugin );
    }
    

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a Response whose identifier is specified in parameter
     *
     * @param nKey The entry primary key
     * @param plugin the Plugin
     * @return an instance of Response
     */
    public static Response findByPrimaryKey( int nKey, Plugin plugin )
    {
        return _dao.load( nKey, plugin );
    }

    /**
         * Load the data of all the response who verify the filter and returns them in a  list
         * @param filter the filter
         * @param plugin the plugin
         * @return  the list of response
         */
    public static List<Response> getResponseList( SubmitFilter filter, Plugin plugin )
    {
        return _dao.selectListByFilter( filter, plugin );
    }

    /**
     * Return the image resource for the specified image id
     * @param nImageId The identifier of image object
     * @param plugin the plugin
     * @return ImageResource
     */
    public static ImageResource getImageResource( int nImageId, Plugin plugin )
    {
        return _dao.loadImageResource( nImageId, plugin );
    }
    
    /**
     * Creation of an image
     * @param nIdDiggSubmit the id of the diggSubmit
     * @param image the image to add to the db
     * @param plugin the Plugin
     * @return the id of the new digg submit
     * @throws com.mysql.jdbc.PacketTooBigException if the image is too big
     *
     */
    public static int createImage( int nIdDiggSubmit, ImageResource image, Plugin plugin )
        throws com.mysql.jdbc.PacketTooBigException
    {
        return _dao.insertImageResource( nIdDiggSubmit, image, plugin );
    }
}
