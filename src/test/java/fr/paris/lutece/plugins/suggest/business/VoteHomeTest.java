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
package fr.paris.lutece.plugins.suggest.business;

import java.util.List;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.test.LuteceTestCase;

/**
 * Tests the lookup of the votes of a user.
 */
public class VoteHomeTest extends LuteceTestCase
{
    /**
     * Finds, in one query, the suggest submits a user has voted for among a list.
     */
    @Test
    public void testFindIdSuggestSubmitVotedByLuteceUserKey( )
    {
        String strUser = "voter" + System.nanoTime( );
        String strOther = "other" + System.nanoTime( );
        createVote( 900001, strUser );
        createVote( 900003, strUser );
        createVote( 900002, strOther );
        createVote( 900004, strUser );

        List<Integer> listVoted = VoteHome.findIdSuggestSubmitVotedByLuteceUserKey( List.of( 900001, 900002, 900003 ), strUser, null );

        assertEquals( 2, listVoted.size( ) );
        assertTrue( listVoted.containsAll( List.of( 900001, 900003 ) ) );
        assertTrue( VoteHome.findIdSuggestSubmitVotedByLuteceUserKey( List.of( ), strUser, null ).isEmpty( ) );

        for ( int nId : List.of( 900001, 900003, 900004 ) )
        {
            VoteHome.remove( nId, strUser, null );
        }
        VoteHome.remove( 900002, strOther, null );
    }

    /**
     * Records a vote of a user on a suggest submit.
     *
     * @param nIdSuggestSubmit
     *            the id of the suggest submit
     * @param strUser
     *            the key of the user
     */
    private static void createVote( int nIdSuggestSubmit, String strUser )
    {
        Vote vote = new Vote( );
        vote.setIdSuggestSubmit( nIdSuggestSubmit );
        vote.setLuteceUserKey( strUser );
        vote.setVoteValue( 1 );
        VoteHome.create( vote, null );
    }
}
