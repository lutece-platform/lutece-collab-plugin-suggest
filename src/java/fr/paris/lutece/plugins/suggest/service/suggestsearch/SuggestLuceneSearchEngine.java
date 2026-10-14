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
package fr.paris.lutece.plugins.suggest.service.suggestsearch;

import jakarta.enterprise.context.ApplicationScoped;
import fr.paris.lutece.plugins.suggest.business.SubmitFilter;
import fr.paris.lutece.plugins.suggest.service.search.SuggestIndexer;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.LuceneSearchEngine;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.util.AppLogService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.jsoup.parser.Parser;

/**
 * LuceneSearchEngine
 */
@ApplicationScoped
public class SuggestLuceneSearchEngine implements SuggestSearchEngine
{
    /**
     * Return search results
     * 
     * @param strQuery
     *            The search query, every word of which must match
     * @param filter
     *            the filter
     * @return Results as a collection of SearchResult
     */
    public List<SuggestSearchItem> getSearchResults( String strQuery, SubmitFilter filter )
    {
        List<SuggestSearchItem> listResults = new ArrayList<>( );

        try ( DirectoryReader reader = DirectoryReader.open( IndexationService.getDirectoryIndex( ) ) )
        {
            BooleanQuery.Builder query = new BooleanQuery.Builder( );

            if ( StringUtils.isNotBlank( strQuery ) )
            {
                query.add( buildTextQuery( strQuery ), BooleanClause.Occur.MUST );
            }

            if ( filter.containsIdSuggest( ) )
            {
                query.add( new TermQuery( new Term( SuggestSearchItem.FIELD_ID_SUGGEST, String.valueOf( filter.getIdSuggest( ) ) ) ), BooleanClause.Occur.FILTER );
            }

            if ( filter.containsIdSuggestSubmitState( ) )
            {
                query.add( new TermQuery( new Term( SearchItem.FIELD_STATE, String.valueOf( filter.getIdSuggestSubmitState( ) ) ) ), BooleanClause.Occur.FILTER );
            }

            query.add( new TermQuery( new Term( SearchItem.FIELD_TYPE, SuggestIndexer.INDEX_TYPE_SUGGEST ) ), BooleanClause.Occur.FILTER );

            IndexSearcher searcher = new IndexSearcher( reader );
            TopDocs topDocs = searcher.search( query.build( ), LuceneSearchEngine.MAX_RESPONSES );

            for ( ScoreDoc hit : topDocs.scoreDocs )
            {
                Document document = searcher.doc( hit.doc );
                listResults.add( new SuggestSearchItem( document ) );
            }
        }
        catch( IOException e )
        {
            AppLogService.error( e.getMessage( ), e );
        }

        return listResults;
    }

    /**
     * Builds the query matching the documents whose contents hold every word of a text, analyzed as the indexed contents are.
     *
     * @param strText
     *            the text typed by the user, possibly entity-encoded by the request filter
     * @return the query, matching nothing when the text holds no searchable word
     * @throws IOException
     *             if the analysis fails
     */
    private static Query buildTextQuery( String strText ) throws IOException
    {
        BooleanQuery.Builder builder = new BooleanQuery.Builder( );

        try ( TokenStream stream = IndexationService.getAnalyser( ).tokenStream( SearchItem.FIELD_CONTENTS, Parser.unescapeEntities( strText, false ) ) )
        {
            CharTermAttribute term = stream.addAttribute( CharTermAttribute.class );
            stream.reset( );

            while ( stream.incrementToken( ) )
            {
                builder.add( new TermQuery( new Term( SearchItem.FIELD_CONTENTS, term.toString( ) ) ), BooleanClause.Occur.MUST );
            }

            stream.end( );
        }

        return builder.build( );
    }
}
