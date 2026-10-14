/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.suggest.web;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import fr.paris.lutece.plugins.suggest.business.VideoType;
import fr.paris.lutece.plugins.suggest.business.VideoTypeHome;
import fr.paris.lutece.plugins.suggest.service.SuggestPlugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.io.IOException;

import org.apache.commons.lang3.math.NumberUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * suggest video resources
 */
@RequestScoped
@Named( "suggestVideoResourceServlet" )
public class SuggestVideoResourceServlet
{
    private static final String PARAMETER_VIDEO_ID = "video_id";
    private static final String DEFAULT_FILENAME = "video_";
    private static final String VIDEO_EXTENSION = ".flv";
    private static final String DEFAULT_EXPIRES_DELAY = "180000";
    private static final String PROPERTY_EXPIRES_DELAY = "suggest.resourceServlet.cacheControl.expires";
    private static final String STRING_DELAY_IN_SECOND = AppPropertiesService.getProperty( PROPERTY_EXPIRES_DELAY, DEFAULT_EXPIRES_DELAY );
    private static final Long LONG_DELAY_IN_MILLISECOND = Long.parseLong( STRING_DELAY_IN_SECOND ) * 1000;

    @Inject
    private ResourceServletCache _cache;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * 
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * @throws IOException
     *             the io exception
     */
    public void processRequest( HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        String strSuggestSubmitId = request.getParameter( PARAMETER_VIDEO_ID );
        int nIdSuggestSubmit = NumberUtils.toInt( strSuggestSubmitId, -1 );

        if ( nIdSuggestSubmit == -1 )
        {
            response.sendError( HttpServletResponse.SC_NOT_FOUND );

            return;
        }

        String strCacheKey = String.valueOf( nIdSuggestSubmit );
        byte [ ] content;
        String strContentType;

        if ( _cache.isCacheEnable( ) && ( _cache.get( strCacheKey ) != null ) )
        {
            ResourceValueObject resource = _cache.get( strCacheKey );
            content = resource.getContent( );
            strContentType = resource.getContentType( );
        }
        else
        {
            VideoType video;

            video = VideoTypeHome.findByPrimaryKey( nIdSuggestSubmit, PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME ) );

            if ( video == null )
            {
                response.sendError( HttpServletResponse.SC_NOT_FOUND );

                return;
            }

            content = video.getVideo( );
            strContentType = video.getMimeType( );

            if ( _cache.isCacheEnable( ) )
            {
                ResourceValueObject r = new ResourceValueObject( );
                r.setContent( content );
                r.setContentType( video.getMimeType( ) );
                r.setFilename( DEFAULT_FILENAME + nIdSuggestSubmit );
                _cache.put( strCacheKey, r );
            }
        }

        if ( strContentType != null )
        {
            response.setContentType( strContentType );
        }

        response.setHeader( "Content-Disposition", "attachment;filename=\"" + DEFAULT_FILENAME + nIdSuggestSubmit + VIDEO_EXTENSION + "\"" );
        response.setHeader( "Cache-Control", "max-age=" + STRING_DELAY_IN_SECOND );
        response.setDateHeader( "Expires", System.currentTimeMillis( ) + LONG_DELAY_IN_MILLISECOND );
        response.setContentLength( content.length );
        response.getOutputStream( ).write( content );
        response.getOutputStream( ).flush( );
    }
}
