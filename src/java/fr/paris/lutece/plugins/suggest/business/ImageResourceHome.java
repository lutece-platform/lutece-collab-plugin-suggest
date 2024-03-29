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

import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods (create, find, ...) for ImageResource objects
 */
public final class ImageResourceHome
{
    // Static variable pointed at the DAO instance
    private static IImageResourceDAO _dao = SpringContextService.getBean( "suggest.imageResourceDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private ImageResourceHome( )
    {
    }

    /**
     * Creation of an instance of ImageResource
     *
     * @param imageResource
     *            The instance of the ImageResource file which contains the informations to store
     * @param plugin
     *            the plugin
     *
     * @return the id of the file after creation
     *
     */
    public static int create( ImageResource imageResource, Plugin plugin )
    {
        return _dao.insert( imageResource, plugin );
    }

    /**
     * Delete the ImageResource file whose identifier is specified in parameter
     *
     * @param nIdImageResource
     *            The identifier of the record physical file
     * @param plugin
     *            the Plugin
     */
    public static void remove( int nIdImageResource, Plugin plugin )
    {
        _dao.delete( nIdImageResource, plugin );
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a ImageResource whose identifier is specified in parameter
     *
     * @param nKey
     *            The file primary key
     * @param plugin
     *            the Plugin
     * @return an instance of ImageResource
     */
    public static ImageResource findByPrimaryKey( int nKey, Plugin plugin )
    {
        return _dao.load( nKey, plugin );
    }
}
