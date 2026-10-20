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
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNERS OR CONTRIBUTORS BE
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.plugins.suggest.business.Category;
import fr.paris.lutece.plugins.suggest.business.CommentSubmit;
import fr.paris.lutece.plugins.suggest.business.Entry;
import fr.paris.lutece.plugins.suggest.business.EntryType;
import fr.paris.lutece.plugins.suggest.business.IEntry;
import fr.paris.lutece.plugins.suggest.business.Response;
import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;

/**
 * Tests the JSON export of a suggest
 */
public class SuggestJsonExportServiceTest
{
    private static final LocalDateTime DATE = LocalDateTime.of( 2026, 10, 14, 9, 30, 15 );

    /**
     * Exports a suggest with one answered question, one unanswered question and a nested comment
     *
     * @throws Exception
     *             if the export or the parsing fails
     */
    @Test
    public void testExport( ) throws Exception
    {
        Entry entryTitle = newEntry( 3, "Title", 6 );
        Entry entryMessage = newEntry( 4, "Message", 2 );

        Suggest suggest = new Suggest( );
        suggest.setIdSuggest( 1 );
        suggest.setTitle( "Happiness at work" );
        suggest.setEntries( List.<IEntry> of( entryTitle, entryMessage ) );

        Response response = new Response( );
        response.setEntry( entryTitle );
        response.setValueResponse( "Avoid micromanagement" );

        CommentSubmit reply = newComment( 11, "Agreed", false );
        CommentSubmit comment = newComment( 10, "Good idea", true );
        comment.setComments( List.of( reply ) );

        Category category = new Category( );
        category.setTitle( "Theme 1" );

        SuggestSubmit suggestSubmit = new SuggestSubmit( );
        suggestSubmit.setIdSuggestSubmit( 2 );
        suggestSubmit.setSuggestSubmitTitle( "Avoid micromanagement" );
        suggestSubmit.setDateResponse( Timestamp.valueOf( DATE ) );
        suggestSubmit.setNumberScore( 4 );
        suggestSubmit.setNumberVote( 6 );
        suggestSubmit.setNumberComment( 1 );
        suggestSubmit.setCategory( category );
        suggestSubmit.setResponses( List.of( response ) );
        suggestSubmit.setComments( List.of( comment ) );

        String strJson = new SuggestJsonExportService( ).export( suggest, List.of( suggestSubmit ) );
        JsonNode root = new ObjectMapper( ).readTree( strJson );

        assertEquals( "Happiness at work", root.path( "suggest" ).path( "title" ).asText( ) );
        assertEquals( 2, root.path( "entries" ).size( ) );
        assertEquals( 6, root.path( "entries" ).get( 0 ).path( "type" ).asInt( ) );

        JsonNode submit = root.path( "submits" ).get( 0 );
        assertEquals( 2, submit.path( "id" ).asInt( ) );
        assertEquals( "2026-10-14T09:30:15", submit.path( "date" ).asText( ) );
        assertEquals( 4, submit.path( "score" ).asInt( ) );
        assertEquals( "Theme 1", submit.path( "category" ).asText( ) );
        assertTrue( submit.path( "type" ).isNull( ) );

        assertEquals( 2, submit.path( "responses" ).size( ) );
        assertEquals( "Avoid micromanagement", submit.path( "responses" ).get( 0 ).path( "value" ).asText( ) );
        assertEquals( 4, submit.path( "responses" ).get( 1 ).path( "entryId" ).asInt( ) );
        assertEquals( "", submit.path( "responses" ).get( 1 ).path( "value" ).asText( ) );

        JsonNode exportedComment = submit.path( "comments" ).get( 0 );
        assertTrue( exportedComment.path( "officialAnswer" ).asBoolean( ) );
        assertEquals( "Agreed", exportedComment.path( "comments" ).get( 0 ).path( "value" ).asText( ) );
        assertFalse( exportedComment.path( "comments" ).get( 0 ).path( "officialAnswer" ).asBoolean( ) );
    }

    /**
     * Exports a suggest without entries nor submits
     *
     * @throws Exception
     *             if the export or the parsing fails
     */
    @Test
    public void testExportEmpty( ) throws Exception
    {
        Suggest suggest = new Suggest( );
        suggest.setIdSuggest( 5 );
        suggest.setTitle( "Empty" );

        JsonNode root = new ObjectMapper( ).readTree( new SuggestJsonExportService( ).export( suggest, List.of( ) ) );

        assertEquals( 0, root.path( "entries" ).size( ) );
        assertEquals( 0, root.path( "submits" ).size( ) );
    }

    /**
     * Builds an entry
     *
     * @param nId
     *            the entry id
     * @param strTitle
     *            the entry title
     * @param nIdType
     *            the entry type id
     * @return the entry
     */
    private static Entry newEntry( int nId, String strTitle, int nIdType )
    {
        EntryType entryType = new EntryType( );
        entryType.setIdType( nIdType );
        Entry entry = new Entry( );
        entry.setIdEntry( nId );
        entry.setTitle( strTitle );
        entry.setEntryType( entryType );
        return entry;
    }

    /**
     * Builds a comment
     *
     * @param nId
     *            the comment id
     * @param strValue
     *            the comment text
     * @param bOfficialAnswer
     *            true for an official answer
     * @return the comment
     */
    private static CommentSubmit newComment( int nId, String strValue, boolean bOfficialAnswer )
    {
        CommentSubmit comment = new CommentSubmit( );
        comment.setIdCommentSubmit( nId );
        comment.setValue( strValue );
        comment.setDateComment( Timestamp.valueOf( DATE ) );
        comment.setOfficialAnswer( bOfficialAnswer );
        return comment;
    }
}
