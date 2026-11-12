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

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.suggest.business.SuggestUserInfo;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Counts of suggest submits for the statistics page: by period, by department, most commented.
 */
@ApplicationScoped
public class SuggestStatisticsService
{
    private static final int MAX_WEEKS = 16;
    private static final int MIN_WEEKS = 11;

    /**
     * Returns the first day of the default span: the oldest deposit, at least 11 weeks before the last day
     *
     * @param listSuggestSubmit
     *            the suggest submits
     * @param dateLast
     *            the last day of the span
     * @return the first day of the span
     */
    public LocalDate getPeriodStart( List<SuggestSubmit> listSuggestSubmit, LocalDate dateLast )
    {
        LocalDate dateFloor = dateLast.minusWeeks( MIN_WEEKS );

        return listSuggestSubmit.stream( ).map( SuggestSubmit::getDateResponse ).filter( Objects::nonNull ).map( date -> date.toLocalDateTime( ).toLocalDate( ) )
                .filter( date -> date.isBefore( dateFloor ) ).min( Comparator.naturalOrder( ) ).orElse( dateFloor );
    }

    /**
     * Tells whether a span is counted week by week, or month by month when it holds more than 16 weeks
     *
     * @param dateFirst
     *            the first day of the span
     * @param dateLast
     *            the last day of the span
     * @return true for weeks, false for months
     */
    public boolean isWeekly( LocalDate dateFirst, LocalDate dateLast )
    {
        return ChronoUnit.WEEKS.between( dateFirst, dateLast ) < MAX_WEEKS;
    }

    /**
     * Counts the suggest submits deposited in each week, or each month, of a span, empty periods included
     *
     * @param listSuggestSubmit
     *            the suggest submits
     * @param dateFirst
     *            the first day of the span
     * @param dateLast
     *            the last day of the span
     * @return one count per period, the oldest first, dated by its first day
     */
    public List<StatisticsCount> countByPeriod( List<SuggestSubmit> listSuggestSubmit, LocalDate dateFirst, LocalDate dateLast )
    {
        boolean bWeekly = isWeekly( dateFirst, dateLast );
        Function<LocalDate, LocalDate> periodStart = day -> bWeekly ? day.with( DayOfWeek.MONDAY ) : day.withDayOfMonth( 1 );
        Map<LocalDate, Integer> mapCounts = new TreeMap<>( );

        for ( LocalDate day = periodStart.apply( dateFirst ); !day.isAfter( dateLast ); day = bWeekly ? day.plusWeeks( 1 ) : day.plusMonths( 1 ) )
        {
            mapCounts.put( day, 0 );
        }

        listSuggestSubmit.stream( ).map( SuggestSubmit::getDateResponse ).filter( Objects::nonNull )
                .forEach( date -> mapCounts.computeIfPresent( periodStart.apply( date.toLocalDateTime( ).toLocalDate( ) ), ( day, nCount ) -> nCount + 1 ) );

        return mapCounts.entrySet( ).stream( ).map( entry -> new StatisticsCount( null, Date.valueOf( entry.getKey( ) ), entry.getValue( ) ) ).toList( );
    }

    /**
     * Counts the suggest submits of each department of their authors, the largest first
     *
     * @param listSuggestSubmit
     *            the suggest submits
     * @param mapUsersInfo
     *            the user infos of the authors, by lutece user key
     * @return one count per department, labelled null for the authors whose department is unknown
     */
    public List<StatisticsCount> countByDepartment( List<SuggestSubmit> listSuggestSubmit, Map<String, SuggestUserInfo> mapUsersInfo )
    {
        Map<String, Long> mapCounts = listSuggestSubmit.stream( )
                .collect( Collectors.groupingBy( suggestSubmit -> departmentOf( mapUsersInfo.get( suggestSubmit.getLuteceUserKey( ) ) ), LinkedHashMap::new,
                        Collectors.counting( ) ) );

        return mapCounts.entrySet( ).stream( ).map( entry -> new StatisticsCount( entry.getKey( ).isEmpty( ) ? null : entry.getKey( ), null, entry.getValue( ).intValue( ) ) )
                .sorted( Comparator.comparingInt( StatisticsCount::getCount ).reversed( ) ).toList( );
    }

    /**
     * Returns the most commented suggest submits, those without comment left out
     *
     * @param listSuggestSubmit
     *            the suggest submits
     * @param nMax
     *            the maximum number returned
     * @return the most commented suggest submits, the most commented first
     */
    public List<SuggestSubmit> getMostCommented( List<SuggestSubmit> listSuggestSubmit, int nMax )
    {
        return listSuggestSubmit.stream( ).filter( suggestSubmit -> suggestSubmit.getNumberComment( ) > 0 )
                .sorted( Comparator.comparingInt( SuggestSubmit::getNumberComment ).reversed( ) ).limit( nMax ).toList( );
    }

    /**
     * Returns the department of a user, empty when unknown
     *
     * @param userInfo
     *            the user info, or null
     * @return the department, or an empty string
     */
    private static String departmentOf( SuggestUserInfo userInfo )
    {
        return ( ( userInfo == null ) || ( userInfo.getDepartment( ) == null ) ) ? "" : userInfo.getDepartment( ).trim( );
    }
}
