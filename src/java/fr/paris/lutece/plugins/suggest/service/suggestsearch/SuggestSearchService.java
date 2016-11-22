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
package fr.paris.lutece.plugins.suggest.service.suggestsearch;

import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.suggest.business.SubmitFilter;
import fr.paris.lutece.plugins.suggest.service.SuggestSubmitService;
import fr.paris.lutece.plugins.suggest.service.search.SuggestIndexer;
import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * DocumentSearchService
 */
public class SuggestSearchService
{
    private static final String BEAN_SEARCH_ENGINE = "suggest.suggestSearchEngine";
    private static final String REGEX_ID = "^[\\d]+$";

    // Constants corresponding to the variables defined in the lutece.properties file
    private static SuggestSearchService _singleton;

    /**
     * @return the instance of the service
     */
    public static SuggestSearchService getInstance(  )
    {
        if ( _singleton == null )
        {
            _singleton = new SuggestSearchService(  );
        }

        return _singleton;
    }

    /**
     * Return search results
     * @param strQuery The search query
     * @param filter The filter
     * @param plugin The plugin
     * @return Results as a collection of {@link SuggestSubmit}
     */
    public List<Integer> getSearchResults( String strQuery, SubmitFilter filter, Plugin plugin )
    {
        List<Integer> listSuggestSubmitResult = new ArrayList<Integer>(  );
        SuggestSearchEngine engine = SpringContextService.getBean( BEAN_SEARCH_ENGINE );
        List<Integer> suggestSubmitListId = SuggestSubmitService.getService(  ).getSuggestSubmitListId( filter, plugin );
        List<SuggestSearchItem> listSearchesults = StringUtils.isEmpty( strQuery ) ? null
                                                                                    : engine.getSearchResults( strQuery,
                filter );

        if ( StringUtils.isEmpty( strQuery ) )
        {
            listSuggestSubmitResult = suggestSubmitListId;
        }
        else if ( suggestSubmitListId != null )
        {
            for ( Integer nSuggestSubmitId : suggestSubmitListId )
            {
                for ( SuggestSearchItem searchResult : listSearchesults )
                {
                    if ( ( searchResult.getType(  ) != null ) && ( searchResult.getId(  ) != null ) &&
                            searchResult.getType(  ).equals( SuggestIndexer.INDEX_TYPE_SUGGEST ) &&
                            searchResult.getIdSuggestSubmit(  ).matches( REGEX_ID ) &&
                            ( Integer.parseInt( searchResult.getIdSuggestSubmit(  ) ) == nSuggestSubmitId ) )
                    {
                        listSuggestSubmitResult.add( SuggestUtils.getIntegerParameter( searchResult.getIdSuggestSubmit(  ) ) );
                    }
                }
            }
        }

        return listSuggestSubmitResult;
    }
}
