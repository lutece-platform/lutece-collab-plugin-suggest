/*
 * Copyright (c) 2002-2026, City of Paris
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

import jakarta.inject.Inject;
import jakarta.enterprise.context.ApplicationScoped;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.suggest.business.SubmitFilter;
import fr.paris.lutece.plugins.suggest.service.ISuggestSubmitService;
import fr.paris.lutece.plugins.suggest.service.search.SuggestIndexer;
import fr.paris.lutece.portal.service.plugin.Plugin;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DocumentSearchService
 */
@ApplicationScoped
public class SuggestSearchService
{
    @Inject
    private SuggestSearchEngine _engine;

    @Inject
    private ISuggestSubmitService _suggestSubmitService;

    /**
     * Return search results
     *
     * @param strQuery
     *            The search query
     * @param filter
     *            The filter
     * @param plugin
     *            The plugin
     * @return the ids of the suggest submits matching the filter and, when a query is given, found by the search engine, in the order of the filter
     */
    public List<Integer> getSearchResults( String strQuery, SubmitFilter filter, Plugin plugin )
    {
        List<Integer> listIdSuggestSubmit = _suggestSubmitService.getSuggestSubmitListId( filter, plugin );

        if ( StringUtils.isEmpty( strQuery ) || ( listIdSuggestSubmit == null ) )
        {
            return ( listIdSuggestSubmit == null ) ? new ArrayList<>( ) : listIdSuggestSubmit;
        }

        Set<Integer> setIdFound = _engine.getSearchResults( strQuery, filter ).stream( )
                .filter( item -> SuggestIndexer.INDEX_TYPE_SUGGEST.equals( item.getType( ) ) && ( item.getId( ) != null )
                        && StringUtils.isNumeric( item.getIdSuggestSubmit( ) ) )
                .map( item -> Integer.valueOf( item.getIdSuggestSubmit( ) ) ).collect( Collectors.toSet( ) );

        return listIdSuggestSubmit.stream( ).filter( setIdFound::contains ).collect( Collectors.toList( ) );
    }
}
