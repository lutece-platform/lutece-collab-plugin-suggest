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
package fr.paris.lutece.plugins.suggest.service.statistics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.suggest.business.SuggestUserInfo;

/**
 * Tests the counts of the statistics page.
 */
public class SuggestStatisticsServiceTest
{
    private final SuggestStatisticsService _service = new SuggestStatisticsService( );

    /**
     * Returns a suggest submit deposited on a day by an author, with a number of comments
     *
     * @param day
     *            the day of the deposit
     * @param strAuthor
     *            the lutece user key of the author
     * @param nComments
     *            the number of comments
     * @return the suggest submit
     */
    private static SuggestSubmit submit( LocalDate day, String strAuthor, int nComments )
    {
        SuggestSubmit suggestSubmit = new SuggestSubmit( );
        suggestSubmit.setDateResponse( Timestamp.valueOf( day.atTime( 10, 0 ) ) );
        suggestSubmit.setLuteceUserKey( strAuthor );
        suggestSubmit.setNumberComment( nComments );

        return suggestSubmit;
    }

    /**
     * A short span is counted week by week from its Monday, empty weeks included.
     */
    @Test
    public void testCountByWeek( )
    {
        LocalDate wednesday = LocalDate.of( 2026, 11, 11 );
        List<StatisticsCount> list = _service.countByPeriod( List.of( submit( wednesday, "a", 0 ), submit( wednesday.minusDays( 1 ), "a", 0 ),
                submit( wednesday.minusWeeks( 2 ), "b", 0 ) ), wednesday.minusWeeks( 3 ), wednesday );

        assertTrue( _service.isWeekly( wednesday.minusWeeks( 3 ), wednesday ) );
        assertEquals( List.of( 0, 1, 0, 2 ), list.stream( ).map( StatisticsCount::getCount ).toList( ) );
        assertEquals( LocalDate.of( 2026, 11, 9 ), new java.sql.Date( list.get( 3 ).getDateStart( ).getTime( ) ).toLocalDate( ) );
    }

    /**
     * The default span starts on the oldest deposit, and no later than 11 weeks before its last day.
     */
    @Test
    public void testPeriodStart( )
    {
        LocalDate last = LocalDate.of( 2026, 11, 11 );

        assertEquals( last.minusWeeks( 11 ), _service.getPeriodStart( List.of( submit( last.minusWeeks( 2 ), "a", 0 ) ), last ) );
        assertEquals( last.minusWeeks( 20 ), _service.getPeriodStart( List.of( submit( last.minusWeeks( 20 ), "a", 0 ), submit( last, "a", 0 ) ), last ) );
    }

    /**
     * A span of more than 16 weeks is counted month by month.
     */
    @Test
    public void testCountByMonth( )
    {
        LocalDate last = LocalDate.of( 2026, 11, 11 );
        List<StatisticsCount> list = _service.countByPeriod( List.of( submit( last, "a", 0 ), submit( LocalDate.of( 2026, 6, 30 ), "a", 0 ) ),
                LocalDate.of( 2026, 6, 1 ), last );

        assertFalse( _service.isWeekly( LocalDate.of( 2026, 6, 1 ), last ) );
        assertEquals( 6, list.size( ) );
        assertEquals( 1, list.get( 0 ).getCount( ) );
        assertEquals( 1, list.get( 5 ).getCount( ) );
    }

    /**
     * The departments are counted the largest first, the unknown ones under a null label.
     */
    @Test
    public void testCountByDepartment( )
    {
        SuggestUserInfo dsin = new SuggestUserInfo( );
        dsin.setDepartment( "DSIN" );
        LocalDate day = LocalDate.of( 2026, 11, 11 );
        List<StatisticsCount> list = _service.countByDepartment( List.of( submit( day, "x", 0 ), submit( day, "a", 0 ), submit( day, "a", 0 ) ),
                Map.of( "a", dsin ) );

        assertEquals( "DSIN", list.get( 0 ).getLabel( ) );
        assertEquals( 2, list.get( 0 ).getCount( ) );
        assertNull( list.get( 1 ).getLabel( ) );
    }

    /**
     * The most commented come first, the ideas without comment left out.
     */
    @Test
    public void testMostCommented( )
    {
        LocalDate day = LocalDate.of( 2026, 11, 11 );
        SuggestSubmit two = submit( day, "a", 2 );
        SuggestSubmit five = submit( day, "a", 5 );

        assertEquals( List.of( five, two ), _service.getMostCommented( List.of( two, submit( day, "a", 0 ), five ), 10 ) );
    }
}
