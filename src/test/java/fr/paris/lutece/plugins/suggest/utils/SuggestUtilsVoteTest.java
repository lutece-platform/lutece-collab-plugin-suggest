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
package fr.paris.lutece.plugins.suggest.utils;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestHome;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitHome;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitStateHome;
import fr.paris.lutece.plugins.suggest.business.Vote;
import fr.paris.lutece.plugins.suggest.business.VoteHome;
import fr.paris.lutece.plugins.suggest.business.VoteTypeHome;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Tests the vote and its withdrawal.
 */
public class SuggestUtilsVoteTest extends LuteceTestCase
{
    private static final String USER = "voter";

    /**
     * Checks that a withdrawn vote removes the vote and the score it gave.
     */
    @Test
    public void testWithdrawVoteRemovesItsScore( )
    {
        int nIdSuggestSubmit = createSuggestSubmit( );

        SuggestUtils.doVoteSuggestSubmit( nIdSuggestSubmit, -1, USER, null );

        assertEquals( Integer.valueOf( -1 ), VoteHome.findByIdSuggestSubmitAndLuteceUserKey( nIdSuggestSubmit, USER, null ).getVoteValue( ) );
        assertEquals( 1, SuggestSubmitHome.findByPrimaryKey( nIdSuggestSubmit, null ).getNumberVote( ) );
        assertEquals( -1, SuggestSubmitHome.findByPrimaryKey( nIdSuggestSubmit, null ).getNumberScore( ) );

        SuggestUtils.doWithdrawVoteSuggestSubmit( nIdSuggestSubmit, 1, USER, null );

        assertNull( VoteHome.findByIdSuggestSubmitAndLuteceUserKey( nIdSuggestSubmit, USER, null ) );
        assertEquals( 0, SuggestSubmitHome.findByPrimaryKey( nIdSuggestSubmit, null ).getNumberVote( ) );
        assertEquals( 0, SuggestSubmitHome.findByPrimaryKey( nIdSuggestSubmit, null ).getNumberScore( ) );
    }

    /**
     * Checks that a vote stored without its score withdraws the score sent.
     */
    @Test
    public void testWithdrawVoteWithoutStoredScore( )
    {
        int nIdSuggestSubmit = createSuggestSubmit( );
        SuggestSubmit suggestSubmit = SuggestSubmitHome.findByPrimaryKey( nIdSuggestSubmit, null );
        suggestSubmit.setNumberVote( 1 );
        suggestSubmit.setNumberScore( 1 );
        SuggestSubmitHome.update( suggestSubmit, null );

        Vote vote = new Vote( );
        vote.setIdSuggestSubmit( nIdSuggestSubmit );
        vote.setLuteceUserKey( USER );
        VoteHome.create( vote, null );

        SuggestUtils.doWithdrawVoteSuggestSubmit( nIdSuggestSubmit, 1, USER, null );

        assertNull( VoteHome.findByIdSuggestSubmitAndLuteceUserKey( nIdSuggestSubmit, USER, null ) );
        assertEquals( 0, SuggestSubmitHome.findByPrimaryKey( nIdSuggestSubmit, null ).getNumberVote( ) );
        assertEquals( 0, SuggestSubmitHome.findByPrimaryKey( nIdSuggestSubmit, null ).getNumberScore( ) );
    }

    /**
     * Creates a published suggest submit in a new suggest.
     *
     * @return the id of the suggest submit
     */
    private static int createSuggestSubmit( )
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

        return SuggestSubmitHome.create( suggestSubmit, null );
    }
}
