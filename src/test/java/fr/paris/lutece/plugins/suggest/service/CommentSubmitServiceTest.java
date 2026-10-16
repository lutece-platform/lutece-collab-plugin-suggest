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
package fr.paris.lutece.plugins.suggest.service;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.suggest.business.CommentSubmit;
import fr.paris.lutece.plugins.suggest.business.CommentSubmitHome;
import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestHome;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitHome;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitStateHome;
import fr.paris.lutece.plugins.suggest.business.VoteTypeHome;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;

/**
 * Tests the discussions of the comment service.
 */
public class CommentSubmitServiceTest extends LuteceTestCase
{
    @Inject
    private ICommentSubmitService _commentSubmitService;

    /**
     * Checks that an answer is stored under the answered comment, or beside it when the comment is at the deepest level of the discussions.
     */
    @Test
    public void testAnswerParentFollowsTheMaximumDepth( )
    {
        SuggestSubmit suggestSubmit = createSuggestSubmit( );
        CommentSubmit comment = createComment( suggestSubmit, 0 );
        CommentSubmit answer = createComment( suggestSubmit, comment.getIdCommentSubmit( ) );
        CommentSubmit nested = createComment( suggestSubmit, answer.getIdCommentSubmit( ) );
        CommentSubmitService service = new CommentSubmitService( );

        assertEquals( comment.getIdCommentSubmit( ), service.getAnswerParent( comment, 2, null ) );
        assertEquals( comment.getIdCommentSubmit( ), service.getAnswerParent( answer, 2, null ) );
        assertEquals( answer.getIdCommentSubmit( ), service.getAnswerParent( answer, 3, null ) );
        assertEquals( answer.getIdCommentSubmit( ), service.getAnswerParent( nested, 3, null ) );
    }

    /**
     * Checks that the answers of a discussion are listed at every depth and lead back to its first comment.
     */
    @Test
    public void testDiscussionAnswersAndRoot( )
    {
        SuggestSubmit suggestSubmit = createSuggestSubmit( );
        CommentSubmit comment = createComment( suggestSubmit, 0 );
        CommentSubmit answer = createComment( suggestSubmit, comment.getIdCommentSubmit( ) );
        CommentSubmit nested = createComment( suggestSubmit, answer.getIdCommentSubmit( ) );

        List<Integer> listIds = _commentSubmitService.getAnswers( comment.getIdCommentSubmit( ), null ).stream( ).map( CommentSubmit::getIdCommentSubmit )
                .collect( Collectors.toList( ) );

        assertEquals( List.of( answer.getIdCommentSubmit( ), nested.getIdCommentSubmit( ) ), listIds );
        assertEquals( comment.getIdCommentSubmit( ), _commentSubmitService.getDiscussionRoot( nested, null ).getIdCommentSubmit( ) );
    }

    /**
     * Checks that removing a comment removes its answers at every depth.
     */
    @Test
    public void testRemoveDeletesTheWholeDiscussion( )
    {
        SuggestSubmit suggestSubmit = createSuggestSubmit( );
        CommentSubmit comment = createComment( suggestSubmit, 0 );
        CommentSubmit answer = createComment( suggestSubmit, comment.getIdCommentSubmit( ) );
        CommentSubmit nested = createComment( suggestSubmit, answer.getIdCommentSubmit( ) );

        _commentSubmitService.remove( comment.getIdCommentSubmit( ), null );

        assertNull( CommentSubmitHome.findByPrimaryKey( comment.getIdCommentSubmit( ), null ) );
        assertNull( CommentSubmitHome.findByPrimaryKey( answer.getIdCommentSubmit( ), null ) );
        assertNull( CommentSubmitHome.findByPrimaryKey( nested.getIdCommentSubmit( ), null ) );
    }

    /**
     * Creates a published suggest submit in a new suggest.
     *
     * @return the suggest submit
     */
    private static SuggestSubmit createSuggestSubmit( )
    {
        Suggest suggest = new Suggest( );
        suggest.setTitle( "Suggest" );
        suggest.setLibelleContribution( "Idea" );
        suggest.setVoteType( VoteTypeHome.findByPrimaryKey( 1, null ) );
        SuggestHome.create( suggest, null );

        SuggestSubmit suggestSubmit = new SuggestSubmit( );
        suggestSubmit.setSuggest( suggest );
        suggestSubmit.setSuggestSubmitState( SuggestSubmitStateHome.findByNumero( SuggestSubmit.STATE_PUBLISH, null ) );
        suggestSubmit.setSuggestSubmitTitle( "Idea" );
        suggestSubmit.setIdSuggestSubmit( SuggestSubmitHome.create( suggestSubmit, null ) );

        return suggestSubmit;
    }

    /**
     * Creates a published comment.
     *
     * @param suggestSubmit
     *            the commented suggest submit
     * @param nIdParent
     *            the id of the answered comment, 0 for a comment
     * @return the comment
     */
    private static CommentSubmit createComment( SuggestSubmit suggestSubmit, int nIdParent )
    {
        CommentSubmit comment = new CommentSubmit( );
        comment.setSuggestSubmit( suggestSubmit );
        comment.setLuteceUserKey( "user" );
        comment.setIdParent( nIdParent );
        comment.setActive( true );
        comment.setValue( "Comment" );
        CommentSubmitHome.create( comment, null );

        return comment;
    }
}
