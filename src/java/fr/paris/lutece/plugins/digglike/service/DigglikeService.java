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
package fr.paris.lutece.plugins.digglike.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.digglike.business.Digg;
import fr.paris.lutece.plugins.digglike.business.DiggFilter;
import fr.paris.lutece.plugins.digglike.business.DiggHome;
import fr.paris.lutece.plugins.digglike.utils.DiggUtils;
import fr.paris.lutece.portal.business.style.Theme;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.portal.ThemesService;


/**
 *
 * DigglikeService
 *
 */
public class DigglikeService
{
    private static DigglikeService _singleton = new DigglikeService(  );
    private static final String PARAMETER_ID_DIGG = "id_digg";

    /**
     * Initialize the Digg service
     *
     */
    public void init(  )
    {
        Digg.init(  );
    }

    /**
     * Returns the instance of the singleton
     *
     * @return The instance of the singleton
     */
    public static DigglikeService getInstance(  )
    {
        return _singleton;
    }
    
    
   /**
    * get the XpageTheme associate
    * @param request HttpServletRequest
    * @return Theme
    */
    public Theme getXPageTheme( HttpServletRequest request)
    {
    	
    	String strIdDigg = request.getParameter( PARAMETER_ID_DIGG );
    	Digg digg=null;;
    	int nIdDigg=DiggUtils.getIntegerParameter( strIdDigg );
        if(nIdDigg == DiggUtils.CONSTANT_ID_NULL)
        {	
         	nIdDigg=getIdDefaultDigg();
         }
         if(nIdDigg != DiggUtils.CONSTANT_ID_NULL)
         {	
        	 digg= DiggHome.findByPrimaryKey( nIdDigg, PluginService.getPlugin(DigglikePlugin.PLUGIN_NAME ));
        	 
         }
    	
        if ( digg != null )
        {
            return ThemesService.getGlobalTheme( digg.getCodeTheme( ) );
        }

        return ThemesService.getGlobalThemeObject( );
    }

    
  
    
    
    /**
    *
    * @return the Id of the default Digg
    */
   public int getIdDefaultDigg(  )
   {
       int nIdDefaultDigg;
       DiggFilter filterDefaultDigg = new DiggFilter(  );
       filterDefaultDigg.setIdDefaultDigg( DiggFilter.ID_TRUE );

       List listDefaultDigg = DiggHome.getDiggList( filterDefaultDigg, PluginService.getPlugin(DigglikePlugin.PLUGIN_NAME ) );
       nIdDefaultDigg = ( ( listDefaultDigg != null ) && ( listDefaultDigg.size(  ) > 0 ) )
           ? ( (Digg) listDefaultDigg.get( 0 ) ).getIdDigg(  ) : DiggUtils.CONSTANT_ID_NULL;

       return nIdDefaultDigg;
   }

}
