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
package fr.paris.lutece.plugins.digglike.web;

import fr.paris.lutece.plugins.digglike.business.VideoType;
import fr.paris.lutece.plugins.digglike.business.VideoTypeHome;
import fr.paris.lutece.plugins.digglike.service.DigglikePlugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * digglike video resources
 */
public class DigglikeVideoResourceServlet
{
    private static final String PARAMETER_VIDEO_ID = "video_id";
    private static final String DEFAULT_FILENAME = "video_";
    private static final String VIDEO_EXTENSION = ".flv";
    private static final String DEFAULT_EXPIRES_DELAY = "180000";
    private static final String PROPERTY_EXPIRES_DELAY = "digglike.resourceServlet.cacheControl.expires";
    private static final String STRING_DELAY_IN_SECOND = AppPropertiesService.getProperty( PROPERTY_EXPIRES_DELAY,
            DEFAULT_EXPIRES_DELAY );
    private static final Long LONG_DELAY_IN_MILLISECOND = Long.parseLong( STRING_DELAY_IN_SECOND ) * 1000;
    private static final ResourceServletCache _cache = new ResourceServletCache(  );

    /**
     * Put the file in cache
     * @param nIdDiggSubmit The DiggSubmit id
     */
    public static void putInCache( int nIdDiggSubmit )
    {
        String strCacheKey = String.valueOf( nIdDiggSubmit );

        VideoType video;

        byte[] content;
        String strContentType;
        String strFilename;

        video = VideoTypeHome.findByPrimaryKey( nIdDiggSubmit, PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME ) );

        strFilename = DEFAULT_FILENAME + nIdDiggSubmit;

        strContentType = video.getMimeType(  );
        content = video.getVideo(  );

        if ( _cache.isCacheEnable(  ) )
        {
            ResourceValueObject r = new ResourceValueObject(  );
            r.setContent( content );
            r.setContentType( strContentType );
            r.setFilename( strFilename );
            _cache.put( strCacheKey, r );
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     * @param request servlet request
     * @param response servlet response
     * @return the content of the video
     * @throws ServletException the servlet Exception
     * @throws IOException the io exception
     */
    public byte[] processRequest( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        String strDiggSubmitId = request.getParameter( PARAMETER_VIDEO_ID );
        int nIdDiggSubmit = Integer.parseInt( strDiggSubmitId );

        String strCacheKey = String.valueOf( nIdDiggSubmit );
        byte[] content;

        if ( _cache.isCacheEnable(  ) && ( _cache.get( strCacheKey ) != null ) )
        {
            ResourceValueObject resource = _cache.get( strCacheKey );
            content = resource.getContent(  );
        }
        else
        {
            VideoType video;

            video = VideoTypeHome.findByPrimaryKey( nIdDiggSubmit, PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME ) );

            if ( video == null )
            { //nothing to do if the data is no longer in DB

                return null;
            }

            content = video.getVideo(  );

            if ( _cache.isCacheEnable(  ) )
            {
                ResourceValueObject r = new ResourceValueObject(  );
                r.setContent( content );
                r.setContentType( video.getMimeType( ) );
                r.setFilename( DEFAULT_FILENAME + nIdDiggSubmit );
                _cache.put( strCacheKey, r );
            }
        }

        // Add Cache Control HTTP header
        response.setHeader( "Content-Disposition",
            "attachment;filename=\"" + DEFAULT_FILENAME + nIdDiggSubmit + VIDEO_EXTENSION + "\"" );
        response.setHeader( "Cache-Control", "max-age=" + STRING_DELAY_IN_SECOND ); // HTTP 1.1
        response.setDateHeader( "Expires", System.currentTimeMillis(  ) + LONG_DELAY_IN_MILLISECOND ); // HTTP 1.0
        response.setContentLength( content.length ); // Keep Alive connexion

        return content;
    }
}
