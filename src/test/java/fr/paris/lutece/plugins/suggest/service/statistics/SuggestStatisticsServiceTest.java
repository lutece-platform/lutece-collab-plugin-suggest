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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.suggest.business.statistics.StatisticsCount;

/**
 * Tests the periods of the statistics page.
 */
public class SuggestStatisticsServiceTest
{
    private final SuggestStatisticsService _service = new SuggestStatisticsService( );

    /**
     * A short span is counted week by week from its Monday, empty weeks included.
     */
    @Test
    public void testCountByWeek( )
    {
        LocalDate wednesday = LocalDate.of( 2026, 11, 11 );
        List<StatisticsCount> list = _service.countByPeriod( Map.of( wednesday, 1, wednesday.minusDays( 1 ), 1, wednesday.minusWeeks( 2 ), 1 ),
                wednesday.minusWeeks( 3 ), wednesday );

        assertTrue( _service.isWeekly( wednesday.minusWeeks( 3 ), wednesday ) );
        assertEquals( List.of( 0, 1, 0, 2 ), list.stream( ).map( StatisticsCount::getCount ).toList( ) );
        assertEquals( LocalDate.of( 2026, 11, 9 ), new java.sql.Date( list.get( 3 ).getDateStart( ).getTime( ) ).toLocalDate( ) );
    }

    /**
     * A span of more than 16 weeks is counted month by month, the days outside the span left out.
     */
    @Test
    public void testCountByMonth( )
    {
        LocalDate last = LocalDate.of( 2026, 11, 11 );
        List<StatisticsCount> list = _service.countByPeriod( Map.of( last, 3, LocalDate.of( 2026, 6, 30 ), 2, LocalDate.of( 2025, 1, 1 ), 9 ),
                LocalDate.of( 2026, 6, 1 ), last );

        assertFalse( _service.isWeekly( LocalDate.of( 2026, 6, 1 ), last ) );
        assertEquals( 6, list.size( ) );
        assertEquals( 2, list.get( 0 ).getCount( ) );
        assertEquals( 3, list.get( 5 ).getCount( ) );
    }

    /**
     * The default span starts at the oldest deposit, and never less than 11 weeks before its last day.
     */
    @Test
    public void testPeriodStart( )
    {
        LocalDate last = LocalDate.of( 2026, 11, 11 );

        assertEquals( last.minusWeeks( 11 ), _service.getPeriodStart( new TreeMap<>( ), last ) );
        assertEquals( last.minusWeeks( 11 ), _service.getPeriodStart( new TreeMap<>( Map.of( last.minusDays( 3 ), 1 ) ), last ) );
        assertEquals( LocalDate.of( 2026, 1, 5 ), _service.getPeriodStart( new TreeMap<>( Map.of( LocalDate.of( 2026, 1, 5 ), 1 ) ), last ) );
    }
}
