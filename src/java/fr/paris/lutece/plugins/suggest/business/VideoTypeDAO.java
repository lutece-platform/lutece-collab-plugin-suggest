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

import com.mysql.cj.jdbc.exceptions.PacketTooBigException;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 *
 * class GraphTypeDAO
 *
 */
public class VideoTypeDAO implements IVideoTypeDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_suggest_submit,video_content,video_mime_type,credits FROM suggest_video WHERE id_suggest_submit=?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO suggest_video ( " + "id_suggest_submit,video_content,video_mime_type,credits) VALUES(?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM suggest_video WHERE id_suggest_submit = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE  suggest_video SET " + "video_content=?,video_mime_type=?,credits=? WHERE id_suggest_submit=?";

    /**
     * Insert a new record in the table.
     *
     * @param videoType
     *            instance of the VideoType object to insert
     * @param plugin
     *            the plugin
     * @return the id of the new videoType
     * @throws com.mysql.jdbc.PacketTooBigException
     *             exception
     */
    public int insert( VideoType videoType, Plugin plugin ) throws PacketTooBigException
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {    
            daoUtil.setInt( 1, videoType.getIdSuggestSubmit( ) );
            daoUtil.setBytes( 2, videoType.getVideo( ) );
            daoUtil.setString( 3, videoType.getMimeType( ) );
            daoUtil.setString( 4, videoType.getCredits( ) );
    
            try
            {
                daoUtil.executeUpdate( );
            }
            catch( Exception e )
            {
                throw new PacketTooBigException( 0, 0 );
            }

        }

        return videoType.getIdSuggestSubmit( );
    }

    /**
     * Load the data of the video from the table
     *
     * @param nId
     *            The identifier of the video
     * @param plugin
     *            the plugin
     * @return the instance of the VideoType
     */
    public VideoType load( int nId, Plugin plugin )
    {
        VideoType videoType = null;
        
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin ) )
        {
            daoUtil.setInt( 1, nId );
            daoUtil.executeQuery( );
    
            if ( daoUtil.next( ) )
            {
                videoType = new VideoType( );
                videoType.setIdSuggestSubmit( daoUtil.getInt( 1 ) );
                videoType.setVideo( daoUtil.getBytes( 2 ) );
                videoType.setMimeType( daoUtil.getString( 3 ) );
                videoType.setCredits( daoUtil.getString( 4 ) );
            }

        }

        return videoType;
    }

    /**
     * Delete a record from the table
     *
     * @param nIdSuggestSubmit
     *            The identifier of the nIdSuggestSubmit / Video
     * @param plugin
     *            the plugin
     */
    public void delete( int nIdSuggestSubmit, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdSuggestSubmit );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Update the Video in the table
     *
     * @param video
     *            instance of the VideoType object to update
     * @param plugin
     *            the plugin
     */
    public void store( VideoType video, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {    
            daoUtil.setBytes( 1, video.getVideo( ) );
            daoUtil.setString( 2, video.getMimeType( ) );
            daoUtil.setString( 3, video.getCredits( ) );
            daoUtil.setInt( 4, video.getIdSuggestSubmit( ) );
    
            daoUtil.executeUpdate( );
        }
    }
}
