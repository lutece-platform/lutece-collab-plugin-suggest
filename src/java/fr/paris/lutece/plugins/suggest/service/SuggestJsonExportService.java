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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.paris.lutece.plugins.suggest.business.CommentSubmit;
import fr.paris.lutece.plugins.suggest.business.IEntry;
import fr.paris.lutece.plugins.suggest.business.Response;
import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Builds the JSON export of a suggest and of its suggest submits
 */
@ApplicationScoped
public class SuggestJsonExportService
{
    private final ObjectMapper _mapper = new ObjectMapper( ).enable( SerializationFeature.INDENT_OUTPUT );

    /**
     * The exported suggest
     *
     * @param suggest
     *            the suggest identity
     * @param entries
     *            the questions of the suggest
     * @param submits
     *            the exported suggest submits
     */
    public record SuggestExport( SuggestIdentity suggest, List<EntryExport> entries, List<SubmitExport> submits )
    {
    }

    /**
     * The identity of the exported suggest
     *
     * @param id
     *            the suggest id
     * @param title
     *            the suggest title
     */
    public record SuggestIdentity( int id, String title )
    {
    }

    /**
     * A question of the suggest
     *
     * @param id
     *            the entry id
     * @param title
     *            the entry title
     * @param type
     *            the entry type id, null when the entry has no type
     */
    public record EntryExport( int id, String title, Integer type )
    {
    }

    /**
     * An exported suggest submit
     *
     * @param id
     *            the suggest submit id
     * @param title
     *            the suggest submit title
     * @param date
     *            the submission date, ISO-8601
     * @param score
     *            the score
     * @param votes
     *            the number of votes
     * @param commentCount
     *            the number of comments
     * @param category
     *            the category title, null when none
     * @param type
     *            the suggest submit type name, null when none
     * @param responses
     *            one response per question of the suggest
     * @param comments
     *            the comments
     */
    public record SubmitExport( int id, String title, String date, int score, int votes, int commentCount, String category, String type,
            List<ResponseExport> responses, List<CommentExport> comments )
    {
    }

    /**
     * The response of a suggest submit to a question
     *
     * @param entryId
     *            the question id
     * @param value
     *            the response value, empty when the question was not answered
     */
    public record ResponseExport( int entryId, String value )
    {
    }

    /**
     * An exported comment
     *
     * @param id
     *            the comment id
     * @param date
     *            the comment date, ISO-8601
     * @param officialAnswer
     *            true when the comment is an official answer
     * @param value
     *            the comment text
     * @param comments
     *            the replies to this comment
     */
    public record CommentExport( int id, String date, boolean officialAnswer, String value, List<CommentExport> comments )
    {
    }

    /**
     * Serializes a suggest and its suggest submits as JSON
     *
     * @param suggest
     *            the suggest, with its entries loaded
     * @param listSuggestSubmit
     *            the suggest submits, with their responses and comments loaded
     * @return the JSON document
     * @throws JsonProcessingException
     *             if the serialization fails
     */
    public String export( Suggest suggest, List<SuggestSubmit> listSuggestSubmit ) throws JsonProcessingException
    {
        List<IEntry> listEntry = ( suggest.getEntries( ) != null ) ? suggest.getEntries( ) : new ArrayList<>( );

        List<EntryExport> listEntryExport = new ArrayList<>( );
        for ( IEntry entry : listEntry )
        {
            listEntryExport.add( new EntryExport( entry.getIdEntry( ), entry.getTitle( ),
                    ( entry.getEntryType( ) != null ) ? entry.getEntryType( ).getIdType( ) : null ) );
        }

        List<SubmitExport> listSubmitExport = new ArrayList<>( );
        for ( SuggestSubmit suggestSubmit : listSuggestSubmit )
        {
            listSubmitExport.add( toSubmitExport( suggestSubmit, listEntry ) );
        }

        return _mapper.writeValueAsString(
                new SuggestExport( new SuggestIdentity( suggest.getIdSuggest( ), suggest.getTitle( ) ), listEntryExport, listSubmitExport ) );
    }

    /**
     * Builds the export of a suggest submit
     *
     * @param suggestSubmit
     *            the suggest submit
     * @param listEntry
     *            the questions of the suggest
     * @return the export of the suggest submit
     */
    private SubmitExport toSubmitExport( SuggestSubmit suggestSubmit, List<IEntry> listEntry )
    {
        Map<Integer, String> mapResponseByEntry = new HashMap<>( );
        if ( suggestSubmit.getResponses( ) != null )
        {
            for ( Response response : suggestSubmit.getResponses( ) )
            {
                if ( response.getEntry( ) != null )
                {
                    mapResponseByEntry.put( response.getEntry( ).getIdEntry( ), response.getValueResponse( ) );
                }
            }
        }

        List<ResponseExport> listResponseExport = new ArrayList<>( );
        for ( IEntry entry : listEntry )
        {
            listResponseExport.add( new ResponseExport( entry.getIdEntry( ), mapResponseByEntry.getOrDefault( entry.getIdEntry( ), "" ) ) );
        }

        return new SubmitExport( suggestSubmit.getIdSuggestSubmit( ), suggestSubmit.getSuggestSubmitTitle( ), toIsoDate( suggestSubmit.getDateResponse( ) ),
                suggestSubmit.getNumberScore( ), suggestSubmit.getNumberVote( ), suggestSubmit.getNumberComment( ),
                ( suggestSubmit.getCategory( ) != null ) ? suggestSubmit.getCategory( ).getTitle( ) : null,
                ( suggestSubmit.getSuggestSubmitType( ) != null ) ? suggestSubmit.getSuggestSubmitType( ).getName( ) : null, listResponseExport,
                toCommentExports( suggestSubmit.getComments( ) ) );
    }

    /**
     * Builds the export of a list of comments and of their replies
     *
     * @param listComment
     *            the comments, may be null
     * @return the export of the comments
     */
    private List<CommentExport> toCommentExports( List<CommentSubmit> listComment )
    {
        List<CommentExport> listCommentExport = new ArrayList<>( );
        if ( listComment != null )
        {
            for ( CommentSubmit comment : listComment )
            {
                listCommentExport.add( new CommentExport( comment.getIdCommentSubmit( ), toIsoDate( comment.getDateComment( ) ), comment.isOfficialAnswer( ),
                        comment.getValue( ), toCommentExports( comment.getComments( ) ) ) );
            }
        }
        return listCommentExport;
    }

    /**
     * Formats a timestamp as an ISO-8601 local date-time
     *
     * @param timestamp
     *            the timestamp, may be null
     * @return the ISO-8601 date-time, or null
     */
    private static String toIsoDate( Timestamp timestamp )
    {
        return ( timestamp != null ) ? timestamp.toLocalDateTime( ).toString( ) : null;
    }
}
