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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.UnaryOperator;

import fr.paris.lutece.plugins.suggest.business.SubmitFilter;
import fr.paris.lutece.plugins.suggest.business.statistics.StatisticsCount;
import fr.paris.lutece.plugins.suggest.business.statistics.StatisticsTotals;
import fr.paris.lutece.plugins.suggest.business.statistics.SuggestStatisticsDAO;
import fr.paris.lutece.portal.service.plugin.Plugin;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Counts of suggest submits for the statistics page, computed by the database whatever the number of suggest submits.
 */
@ApplicationScoped
public class SuggestStatisticsService
{
    private static final int MAX_WEEKS = 16;
    private static final int MIN_WEEKS = 11;
    @Inject
    private SuggestStatisticsDAO _dao;

    /**
     * Counts the suggest submits of each state
     *
     * @param filter
     *            the suggest, period, category, type and department counted
     * @param plugin
     *            the plugin
     * @return the number of suggest submits by state, the states without any left out
     */
    public Map<Integer, Integer> countByState( SubmitFilter filter, Plugin plugin )
    {
        return _dao.countByState( filter, plugin );
    }

    /**
     * Sums the votes, the authors and the published comments of the published suggest submits
     *
     * @param filter
     *            the suggest, period, category, type and department counted
     * @param plugin
     *            the plugin
     * @return the totals
     */
    public StatisticsTotals getPublishedTotals( SubmitFilter filter, Plugin plugin )
    {
        return _dao.getPublishedTotals( filter, plugin );
    }

    /**
     * Counts the published suggest submits of each department of their authors, the largest first
     *
     * @param filter
     *            the suggest, period, category, type and department counted
     * @param plugin
     *            the plugin
     * @return one count per department, labelled null for the authors whose department is unknown
     */
    public List<StatisticsCount> countPublishedByDepartment( SubmitFilter filter, Plugin plugin )
    {
        return _dao.countPublishedByDepartment( filter, plugin );
    }

    /**
     * Counts the published suggest submits of each category
     *
     * @param filter
     *            the suggest, period, category, type and department counted
     * @param plugin
     *            the plugin
     * @return the number of published suggest submits by category key, the categories without any left out
     */
    public Map<Integer, Integer> countPublishedByCategory( SubmitFilter filter, Plugin plugin )
    {
        return _dao.countPublishedByCategory( filter, plugin );
    }

    /**
     * Counts the suggest submits deposited each day, whatever their state
     *
     * @param filter
     *            the suggest, period, category, type and department counted
     * @param plugin
     *            the plugin
     * @return the number of suggest submits by day, the oldest first, the days without any left out
     */
    public TreeMap<LocalDate, Integer> countByDay( SubmitFilter filter, Plugin plugin )
    {
        return _dao.countByDay( filter, plugin );
    }

    /**
     * Returns the first day of the default span: the oldest deposit, at least 11 weeks before the last day
     *
     * @param mapDayCounts
     *            the number of suggest submits by day
     * @param dateLast
     *            the last day of the span
     * @return the first day of the span
     */
    public LocalDate getPeriodStart( TreeMap<LocalDate, Integer> mapDayCounts, LocalDate dateLast )
    {
        LocalDate dateFloor = dateLast.minusWeeks( MIN_WEEKS );

        return ( !mapDayCounts.isEmpty( ) && mapDayCounts.firstKey( ).isBefore( dateFloor ) ) ? mapDayCounts.firstKey( ) : dateFloor;
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
     * Groups daily counts by week, or by month, over a span, empty periods included
     *
     * @param mapDayCounts
     *            the number of suggest submits by day
     * @param dateFirst
     *            the first day of the span
     * @param dateLast
     *            the last day of the span
     * @return one count per period, the oldest first, dated by its first day
     */
    public List<StatisticsCount> countByPeriod( Map<LocalDate, Integer> mapDayCounts, LocalDate dateFirst, LocalDate dateLast )
    {
        boolean bWeekly = isWeekly( dateFirst, dateLast );
        UnaryOperator<LocalDate> periodStart = day -> bWeekly ? day.with( DayOfWeek.MONDAY ) : day.withDayOfMonth( 1 );
        Map<LocalDate, Integer> mapCounts = new TreeMap<>( );

        for ( LocalDate day = periodStart.apply( dateFirst ); !day.isAfter( dateLast ); day = bWeekly ? day.plusWeeks( 1 ) : day.plusMonths( 1 ) )
        {
            mapCounts.put( day, 0 );
        }

        mapDayCounts.forEach( ( day, nCount ) -> mapCounts.computeIfPresent( periodStart.apply( day ), ( start, nTotal ) -> nTotal + nCount ) );

        return mapCounts.entrySet( ).stream( ).map( entry -> new StatisticsCount( null, Date.valueOf( entry.getKey( ) ), entry.getValue( ) ) ).toList( );
    }
}
