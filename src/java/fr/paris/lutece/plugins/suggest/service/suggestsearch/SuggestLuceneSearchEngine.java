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

import fr.paris.lutece.plugins.suggest.business.SubmitFilter;
import fr.paris.lutece.plugins.suggest.service.search.SuggestIndexer;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.LuceneSearchEngine;
import fr.paris.lutece.portal.service.util.AppLogService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;


/**
 * LuceneSearchEngine
 */
public class SuggestLuceneSearchEngine implements SuggestSearchEngine
{
    /**
     * Return search results
     * 
     * @param strQuery The search query
     * @param filter the filter
     * @return Results as a collection of SearchResult
     */
    public List<SuggestSearchItem> getSearchResults( String strQuery, SubmitFilter filter )
    {
        List<SuggestSearchItem> listResults = new ArrayList<SuggestSearchItem>( );
        IndexSearcher searcher = null;

        try
        {
            IndexReader ir = DirectoryReader.open( IndexationService.getDirectoryIndex( ) );
            searcher = new IndexSearcher( ir );

            Collection<String> queries = new ArrayList<String>( );
            Collection<String> fields = new ArrayList<String>( );
            Collection<BooleanClause.Occur> flags = new ArrayList<BooleanClause.Occur>( );

            //filter on content
            if ( ( strQuery != null ) && !strQuery.equals( "" ) )
            {
                Query queryContent = new TermQuery( new Term( SuggestSearchItem.FIELD_CONTENTS, strQuery ) );
                queries.add( queryContent.toString( ) );
                fields.add( SuggestSearchItem.FIELD_CONTENTS );
                flags.add( BooleanClause.Occur.MUST );
            }

            //filter on content id suggest
            if ( filter.containsIdSuggest( ) )
            {
                Query queryIdSuggest = new TermQuery( new Term( SuggestSearchItem.FIELD_ID_SUGGEST, String.valueOf( filter
                        .getIdSuggest( ) ) ) );
                queries.add( queryIdSuggest.toString( ) );
                fields.add( SuggestSearchItem.FIELD_ID_SUGGEST );
                flags.add( BooleanClause.Occur.MUST );
            }

            //filter on suggest submit state
            if ( filter.containsIdSuggestSubmitState( ) )
            {
                Query queryState = new TermQuery( new Term( SuggestSearchItem.FIELD_STATE, String.valueOf( filter
                        .getIdSuggestSubmitState( ) ) ) );
                queries.add( queryState.toString( ) );
                fields.add( SuggestSearchItem.FIELD_STATE );
                flags.add( BooleanClause.Occur.MUST );
            }

            //filter on suggest type
            Query queryTypeSuggest = new TermQuery( new Term( SuggestSearchItem.FIELD_TYPE,
                    SuggestIndexer.INDEX_TYPE_SUGGEST ) );
            queries.add( queryTypeSuggest.toString( ) );
            fields.add( SuggestSearchItem.FIELD_TYPE );
            flags.add( BooleanClause.Occur.MUST );

            Query queryMulti = MultiFieldQueryParser.parse(
                    (String[]) queries.toArray( new String[queries.size( )] ),
                    (String[]) fields.toArray( new String[fields.size( )] ),
                    (BooleanClause.Occur[]) flags.toArray( new BooleanClause.Occur[flags.size( )] ),
                    IndexationService.getAnalyser( ) );

            TopDocs topDocs = searcher.search( queryMulti, LuceneSearchEngine.MAX_RESPONSES );
            ScoreDoc[] hits = topDocs.scoreDocs;

            for ( int i = 0; i < hits.length; i++ )
            {
                int docId = hits[i].doc;
                Document document = searcher.doc( docId );
                SuggestSearchItem si = new SuggestSearchItem( document );
                listResults.add( si );
            }
        }
        catch ( Exception e )
        {
            AppLogService.error( e.getMessage( ), e );
        }

        return listResults;
    }
}
