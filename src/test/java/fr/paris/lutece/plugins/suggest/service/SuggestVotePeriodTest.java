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
package fr.paris.lutece.plugins.suggest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.suggest.business.Suggest;

/**
 * Tests the vote period of a suggest.
 */
public class SuggestVotePeriodTest
{
    private static final LocalDate DAY = LocalDate.of( 2026, 12, 16 );

    /**
     * Builds a suggest with the given vote settings.
     *
     * @param bDisableVote
     *            true to disable the vote
     * @param strBegin
     *            the first vote day, or null
     * @param strEnd
     *            the last vote day, or null
     * @return the suggest
     */
    private static Suggest suggest( boolean bDisableVote, String strBegin, String strEnd )
    {
        Suggest suggest = new Suggest( );
        suggest.setDisableVote( bDisableVote );
        suggest.setDateBeginVote( strBegin );
        suggest.setDateEndVote( strEnd );

        return suggest;
    }

    /**
     * Without dates, the manual switch alone decides.
     */
    @Test
    public void testManualSwitchWithoutDates( )
    {
        assertTrue( SuggestService.isVoteOpen( suggest( false, null, null ), DAY ) );
        assertFalse( SuggestService.isVoteOpen( suggest( true, null, null ), DAY ) );
    }

    /**
     * The vote is open from the first day to the last day, both included.
     */
    @Test
    public void testPeriodBoundsAreInclusive( )
    {
        Suggest suggest = suggest( false, "2026-12-16", "2027-01-30" );

        assertFalse( SuggestService.isVoteOpen( suggest, DAY.minusDays( 1 ) ) );
        assertTrue( SuggestService.isVoteOpen( suggest, DAY ) );
        assertTrue( SuggestService.isVoteOpen( suggest, LocalDate.of( 2027, 1, 30 ) ) );
        assertFalse( SuggestService.isVoteOpen( suggest, LocalDate.of( 2027, 1, 31 ) ) );
    }

    /**
     * The manual switch closes the vote even within the period.
     */
    @Test
    public void testManualSwitchWinsOverPeriod( )
    {
        assertFalse( SuggestService.isVoteOpen( suggest( true, "2026-12-01", "2027-01-30" ), DAY ) );
    }

    /**
     * A single bound leaves the other side open.
     */
    @Test
    public void testSingleBound( )
    {
        assertFalse( SuggestService.isVoteOpen( suggest( false, "2026-12-17", null ), DAY ) );
        assertTrue( SuggestService.isVoteOpen( suggest( false, null, "2026-12-16" ), DAY ) );
    }

    /**
     * The vote has not started before its first day, and stays started after its last day.
     */
    @Test
    public void testVoteStarted( )
    {
        Suggest suggest = suggest( false, "2026-12-16", "2027-01-30" );

        assertFalse( SuggestService.isVoteStarted( suggest, DAY.minusDays( 1 ) ) );
        assertTrue( SuggestService.isVoteStarted( suggest, DAY ) );
        assertTrue( SuggestService.isVoteStarted( suggest, LocalDate.of( 2027, 2, 1 ) ) );
        assertFalse( SuggestService.isVoteStarted( suggest( true, null, null ), DAY ) );
        assertTrue( SuggestService.isVoteStarted( suggest( false, null, null ), DAY ) );
    }

    /**
     * A blank or malformed date reads as no date.
     */
    @Test
    public void testParseDate( )
    {
        assertEquals( DAY, SuggestService.parseDate( " 2026-12-16 " ) );
        assertNull( SuggestService.parseDate( "" ) );
        assertNull( SuggestService.parseDate( "16/12/2026" ) );
    }
}
