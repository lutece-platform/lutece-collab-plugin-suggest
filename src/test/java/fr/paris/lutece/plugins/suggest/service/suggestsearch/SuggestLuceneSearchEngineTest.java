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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.junit.jupiter.api.Test;

/**
 * Tests the tolerance of the search to typing errors.
 */
public class SuggestLuceneSearchEngineTest
{
    /**
     * Tolerates no error up to 2 characters, one up to 5, two beyond.
     */
    @Test
    public void testGetMaxEdits( )
    {
        assertEquals( 0, SuggestLuceneSearchEngine.getMaxEdits( "ia" ) );
        assertEquals( 1, SuggestLuceneSearchEngine.getMaxEdits( "mail" ) );
        assertEquals( 1, SuggestLuceneSearchEngine.getMaxEdits( "trier" ) );
        assertEquals( 2, SuggestLuceneSearchEngine.getMaxEdits( "priorit" ) );
        assertEquals( 1, SuggestLuceneSearchEngine.getMaxEdits( "élève" ) );
    }

    /**
     * Splits a text into lower-case words without accents, elided articles nor stop words.
     *
     * @throws IOException
     *             if the text cannot be read
     */
    @Test
    public void testWords( ) throws IOException
    {
        assertEquals( List.of( "idee", "trier", "mails", "boite" ), SuggestSearchWords.words( "L’idée de trier les mails de la boîte" ) );
        assertEquals( List.of( "idee" ), SuggestSearchWords.words( "même idée" ) );
        assertEquals( List.of( "idee" ), SuggestSearchWords.words( "meme idee" ) );
    }

    /**
     * Finds a proposal whose words are typed with errors, and not one whose words are absent.
     *
     * @throws IOException
     *             if the index cannot be written or read
     */
    @Test
    public void testWordsQueryToleratesTypingErrors( ) throws IOException
    {
        try ( Directory directory = new ByteBuffersDirectory( ) )
        {
            try ( IndexWriter writer = new IndexWriter( directory, new IndexWriterConfig( new StandardAnalyzer( ) ) ) )
            {
                Document document = new Document( );
                document.add( new TextField( SuggestSearchWords.FIELD_WORDS,
                        SuggestSearchWords.tokenStream( "Analyser les mails et les trier par priorité de traitement" ) ) );
                writer.addDocument( document );
            }

            try ( DirectoryReader reader = DirectoryReader.open( directory ) )
            {
                IndexSearcher searcher = new IndexSearcher( reader );

                assertEquals( 1, searcher.count( SuggestLuceneSearchEngine.buildWordsQuery( "traitment" ) ) );
                assertEquals( 1, searcher.count( SuggestLuceneSearchEngine.buildWordsQuery( "mials priorite" ) ) );
                assertEquals( 1, searcher.count( SuggestLuceneSearchEngine.buildWordsQuery( "analizer les mails" ) ) );
                assertEquals( 0, searcher.count( SuggestLuceneSearchEngine.buildWordsQuery( "facture" ) ) );
            }
        }
    }
}
