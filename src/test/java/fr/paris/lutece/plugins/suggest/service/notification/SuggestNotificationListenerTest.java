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
package fr.paris.lutece.plugins.suggest.service.notification;

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
 * Tests the users notified at once of a comment.
 */
public class SuggestNotificationListenerTest extends LuteceTestCase
{
    @Inject
    private SuggestNotificationListener _listener;

    /**
     * Checks that an answer in a discussion reaches at once the author of the comment and of the other answers, not its own author.
     */
    @Test
    public void testAnswerNotifiesTheDiscussion( )
    {
        Suggest suggest = new Suggest( );
        suggest.setTitle( "Suggest" );
        suggest.setLibelleContribution( "Idea" );
        suggest.setVoteType( VoteTypeHome.findByPrimaryKey( 1, null ) );
        suggest.setNotifyParticipants( true );
        SuggestHome.create( suggest, null );

        SuggestSubmit suggestSubmit = new SuggestSubmit( );
        suggestSubmit.setSuggest( suggest );
        suggestSubmit.setSuggestSubmitState( SuggestSubmitStateHome.findByNumero( SuggestSubmit.STATE_PUBLISH, null ) );
        suggestSubmit.setSuggestSubmitTitle( "Idea" );
        suggestSubmit.setLuteceUserKey( "author" );
        suggestSubmit.setIdSuggestSubmit( SuggestSubmitHome.create( suggestSubmit, null ) );

        CommentSubmit comment = createComment( suggestSubmit, "alice", 0 );
        createComment( suggestSubmit, "bob", comment.getIdCommentSubmit( ) );
        CommentSubmit answer = createComment( suggestSubmit, "claire", comment.getIdCommentSubmit( ) );

        assertTrue( _listener.isNotifiedAtOnce( suggest, answer, "alice" ) );
        assertTrue( _listener.isNotifiedAtOnce( suggest, answer, "bob" ) );
        assertTrue( _listener.isNotifiedAtOnce( suggest, answer, "author" ) );
        assertFalse( _listener.isNotifiedAtOnce( suggest, answer, "claire" ) );
    }

    /**
     * Creates a published comment.
     *
     * @param suggestSubmit
     *            the commented suggest submit
     * @param strUserKey
     *            the author
     * @param nIdParent
     *            the id of the answered comment, 0 for a comment
     * @return the comment
     */
    private static CommentSubmit createComment( SuggestSubmit suggestSubmit, String strUserKey, int nIdParent )
    {
        CommentSubmit comment = new CommentSubmit( );
        comment.setSuggestSubmit( suggestSubmit );
        comment.setLuteceUserKey( strUserKey );
        comment.setIdParent( nIdParent );
        comment.setActive( true );
        comment.setOfficialAnswer( false );
        comment.setValue( "Comment of " + strUserKey );
        CommentSubmitHome.create( comment, null );

        return comment;
    }
}
