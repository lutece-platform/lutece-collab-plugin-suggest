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

import java.io.IOException;
import java.io.StringReader;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.ElisionFilter;

/**
 * Words of a proposal as typed, lower case and without accents nor French stop words, indexed beside the stemmed contents so that a search
 * tolerates typing errors on whole words.
 */
public final class SuggestSearchWords
{
    public static final String FIELD_WORDS = "suggest_words";
    private static final CharArraySet STOP_WORDS = foldStopWords( );

    /**
     * Private constructor.
     */
    private SuggestSearchWords( )
    {
    }

    /**
     * Splits a text into words: lower case, without elided articles, French stop words nor accents.
     *
     * @param strText
     *            the text
     * @return a new token stream over the text
     */
    public static TokenStream tokenStream( String strText )
    {
        Tokenizer tokenizer = new StandardTokenizer( );
        tokenizer.setReader( new StringReader( strText ) );

        TokenStream stream = new ElisionFilter( tokenizer, FrenchAnalyzer.DEFAULT_ARTICLES );
        stream = new LowerCaseFilter( stream );
        stream = new ASCIIFoldingFilter( stream );

        return new StopFilter( stream, STOP_WORDS );
    }

    /**
     * Returns the French stop words without their accents, so that a word typed with or without accents is removed alike.
     *
     * @return the stop words
     */
    private static CharArraySet foldStopWords( )
    {
        CharArraySet setStopWords = new CharArraySet( FrenchAnalyzer.getDefaultStopSet( ).size( ), false );

        for ( Object word : FrenchAnalyzer.getDefaultStopSet( ) )
        {
            setStopWords.add( Normalizer.normalize( new String( (char [ ]) word ), Normalizer.Form.NFD ).replaceAll( "\\p{M}", "" ) );
        }

        return setStopWords;
    }

    /**
     * Returns the words of a text.
     *
     * @param strText
     *            the text
     * @return the words, in order
     * @throws IOException
     *             if the text cannot be read
     */
    public static List<String> words( String strText ) throws IOException
    {
        return terms( tokenStream( strText ) );
    }

    /**
     * Returns the terms of a token stream, then closes it.
     *
     * @param tokenStream
     *            a new token stream
     * @return the terms, in order
     * @throws IOException
     *             if the stream cannot be read
     */
    public static List<String> terms( TokenStream tokenStream ) throws IOException
    {
        List<String> listTerms = new ArrayList<>( );

        try ( TokenStream stream = tokenStream )
        {
            CharTermAttribute term = stream.addAttribute( CharTermAttribute.class );
            stream.reset( );

            while ( stream.incrementToken( ) )
            {
                listTerms.add( term.toString( ) );
            }

            stream.end( );
        }

        return listTerms;
    }
}
