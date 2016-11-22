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
package fr.paris.lutece.plugins.suggest.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestFilter;
import fr.paris.lutece.plugins.suggest.business.SuggestHome;
import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.portal.business.style.Theme;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.portal.ThemesService;


/**
 *
 * SuggestService
 *
 */
public class SuggestService
{
    private static SuggestService _singleton = new SuggestService(  );
    private static final String PARAMETER_ID_SUGGEST = "id_suggest";

    /**
     * Initialize the Suggest service
     *
     */
    public void init(  )
    {
        Suggest.init(  );
    }

    /**
     * Returns the instance of the singleton
     *
     * @return The instance of the singleton
     */
    public static SuggestService getInstance(  )
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
    	
    	String strIdSuggest = request.getParameter( PARAMETER_ID_SUGGEST );
    	Suggest suggest=null;;
    	int nIdSuggest=SuggestUtils.getIntegerParameter( strIdSuggest );
        if(nIdSuggest == SuggestUtils.CONSTANT_ID_NULL)
        {	
         	nIdSuggest=getIdDefaultSuggest();
         }
         if(nIdSuggest != SuggestUtils.CONSTANT_ID_NULL)
         {	
        	 suggest= SuggestHome.findByPrimaryKey( nIdSuggest, PluginService.getPlugin(SuggestPlugin.PLUGIN_NAME ));
        	 
         }
    	
        if ( suggest != null )
        {
            return ThemesService.getGlobalTheme( suggest.getCodeTheme( ) );
        }

        return ThemesService.getGlobalThemeObject( );
    }

    
  
    
    
    /**
    *
    * @return the Id of the default Suggest
    */
   public int getIdDefaultSuggest(  )
   {
       int nIdDefaultSuggest;
       SuggestFilter filterDefaultSuggest = new SuggestFilter(  );
       filterDefaultSuggest.setIdDefaultSuggest( SuggestFilter.ID_TRUE );

       List listDefaultSuggest = SuggestHome.getSuggestList( filterDefaultSuggest, PluginService.getPlugin(SuggestPlugin.PLUGIN_NAME ) );
       nIdDefaultSuggest = ( ( listDefaultSuggest != null ) && ( listDefaultSuggest.size(  ) > 0 ) )
           ? ( (Suggest) listDefaultSuggest.get( 0 ) ).getIdSuggest(  ) : SuggestUtils.CONSTANT_ID_NULL;

       return nIdDefaultSuggest;
   }

}
