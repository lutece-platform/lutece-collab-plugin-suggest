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
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Field objects
 */
public final class ImageResourceDAO implements IImageResourceDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_resource_image ) FROM suggest_image";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT image_content, image_mime_type" + " FROM suggest_image WHERE id_resource_image = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO suggest_image (id_resource_image, image_content, image_mime_type) VALUES (?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM suggest_image WHERE id_resource_image = ? ";

    @Override
    public synchronized int insert( ImageResource imageResource, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            daoUtil.setBytes( 2, imageResource.getImage( ) );
            daoUtil.setString( 3, imageResource.getMimeType( ) );

            int nId = newPrimaryKey( plugin );
            daoUtil.setInt( 1, nId );
            daoUtil.executeUpdate( );

            return nId;
        }
    }

    @Override
    public ImageResource load( int nId, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin ) )
        {
            daoUtil.setInt( 1, nId );
            daoUtil.executeQuery( );

            ImageResource imageResource = null;

            if ( daoUtil.next( ) )
            {
                imageResource = new ImageResource( );
                imageResource.setImage( daoUtil.getBytes( 1 ) );
                imageResource.setMimeType( daoUtil.getString( 2 ) );
            }

            return imageResource;
        }
    }

    @Override
    public void delete( int nId, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nId );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * generate new primary key
     * 
     * @param plugin
     *            plugin
     * @return
     */
    private int newPrimaryKey( Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin ) )
        {
            daoUtil.executeQuery( );
            daoUtil.next( );
            int nKey = daoUtil.getInt( 1 ) + 1;

            return nKey;
        }
    }
}
