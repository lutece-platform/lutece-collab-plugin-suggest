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
package fr.paris.lutece.plugins.digglike.service.digglikesearch;

import fr.paris.lutece.plugins.digglike.business.SubmitFilter;
import fr.paris.lutece.plugins.digglike.service.search.DigglikeIndexer;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.LuceneSearchEngine;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * LuceneSearchEngine
 */
public class DigglikeLuceneSearchEngine implements DigglikeSearchEngine
{
    /**
     * Return search results
     *
     * @param strQuery The search query
     * @param filter the filter
     * @return Results as a collection of SearchResult
     */
    public List<DigglikeSearchItem> getSearchResults( String strQuery, SubmitFilter filter )
    {
        List<DigglikeSearchItem> listResults = new ArrayList<DigglikeSearchItem>(  );
        Searcher searcher = null;

        try
        {
            searcher = new IndexSearcher( IndexationService.getDirectoryIndex(  ), true );

            Collection<String> queries = new ArrayList<String>(  );
            Collection<String> fields = new ArrayList<String>(  );
            Collection<BooleanClause.Occur> flags = new ArrayList<BooleanClause.Occur>(  );

            //filter on content
            if ( ( strQuery != null ) && !strQuery.equals( "" ) )
            {
                Query queryContent = new TermQuery( new Term( DigglikeSearchItem.FIELD_CONTENTS, strQuery ) );
                queries.add( queryContent.toString(  ) );
                fields.add( DigglikeSearchItem.FIELD_CONTENTS );
                flags.add( BooleanClause.Occur.MUST );
            }

            //filter on content id digg
            if ( filter.containsIdDigg(  ) )
            {
                Query queryIdDigg = new TermQuery( new Term( DigglikeSearchItem.FIELD_ID_DIGG,
                            String.valueOf( filter.getIdDigg(  ) ) ) );
                queries.add( queryIdDigg.toString(  ) );
                fields.add( DigglikeSearchItem.FIELD_ID_DIGG );
                flags.add( BooleanClause.Occur.MUST );
            }

            //filter on digg submit state
            if ( filter.containsIdDiggSubmitState(  ) )
            {
                Query queryState = new TermQuery( new Term( DigglikeSearchItem.FIELD_STATE,
                            String.valueOf( filter.getIdDiggSubmitState(  ) ) ) );
                queries.add( queryState.toString(  ) );
                fields.add( DigglikeSearchItem.FIELD_STATE );
                flags.add( BooleanClause.Occur.MUST );
            }

            //filter on digg type
            Query queryTypeDigg = new TermQuery( new Term( DigglikeSearchItem.FIELD_TYPE,
                        DigglikeIndexer.INDEX_TYPE_DIGG ) );
            queries.add( queryTypeDigg.toString(  ) );
            fields.add( DigglikeSearchItem.FIELD_TYPE );
            flags.add( BooleanClause.Occur.MUST );

            Query queryMulti = MultiFieldQueryParser.parse( IndexationService.LUCENE_INDEX_VERSION,
                    (String[]) queries.toArray( new String[queries.size(  )] ),
                    (String[]) fields.toArray( new String[fields.size(  )] ),
                    (BooleanClause.Occur[]) flags.toArray( new BooleanClause.Occur[flags.size(  )] ),
                    IndexationService.getAnalyser(  ) );

            TopDocs topDocs = searcher.search( queryMulti, LuceneSearchEngine.MAX_RESPONSES );
            ScoreDoc[] hits = topDocs.scoreDocs;

            for ( int i = 0; i < hits.length; i++ )
            {
                int docId = hits[i].doc;
                Document document = searcher.doc( docId );
                DigglikeSearchItem si = new DigglikeSearchItem( document );
                listResults.add( si );
            }

            searcher.close(  );
        }
        catch ( Exception e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }

        return listResults;
    }
}
