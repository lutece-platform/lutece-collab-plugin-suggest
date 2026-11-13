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
package fr.paris.lutece.plugins.suggest.business.statistics;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.paris.lutece.plugins.suggest.business.SubmitFilter;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Aggregate queries of the statistics page: every count is computed by the database, no suggest submit is loaded.
 */
@ApplicationScoped
public class SuggestStatisticsDAO
{
    private static final String SQL_FROM = " FROM suggest_suggest_submit d ";
    private static final String SQL_COUNT_BY_STATE = "SELECT d.id_state, COUNT(*)" + SQL_FROM;
    private static final String SQL_GROUP_BY_STATE = " GROUP BY d.id_state";
    private static final String SQL_TOTALS = "SELECT COALESCE( SUM( d.vote_number ), 0 ), COUNT( DISTINCT d.lutece_user_key ), COALESCE( SUM( d.comment_enable_number ), 0 )"
            + SQL_FROM;
    private static final String SQL_COUNT_BY_DAY = "SELECT DATE( d.date_response ), COUNT(*)" + SQL_FROM;
    private static final String SQL_GROUP_BY_DAY = " GROUP BY DATE( d.date_response )";
    private static final String SQL_COUNT_BY_DEPARTMENT = "SELECT u.department, COUNT(*)" + SQL_FROM
            + "LEFT JOIN suggest_suggest_user_info u ON u.lutece_user_key = d.lutece_user_key ";
    private static final String SQL_GROUP_BY_DEPARTMENT = " GROUP BY u.department ORDER BY COUNT(*) DESC";
    private static final String SQL_COUNT_BY_CATEGORY = "SELECT d.id_category, COUNT(*)" + SQL_FROM;
    private static final String SQL_GROUP_BY_CATEGORY = " GROUP BY d.id_category";

    /**
     * Counts the suggest submits of each state
     *
     * @param filter
     *            the suggest, period, category, type and department counted; its state is ignored
     * @param plugin
     *            the plugin
     * @return the number of suggest submits by state, the states without any left out
     */
    public Map<Integer, Integer> countByState( SubmitFilter filter, Plugin plugin )
    {
        return selectCounts( SQL_COUNT_BY_STATE, SQL_GROUP_BY_STATE, filter, false, plugin );
    }

    /**
     * Sums the votes, the authors and the published comments of the published suggest submits
     *
     * @param filter
     *            the suggest, period, category, type and department counted; its state is ignored
     * @param plugin
     *            the plugin
     * @return the totals
     */
    public StatisticsTotals getPublishedTotals( SubmitFilter filter, Plugin plugin )
    {
        try ( DAOUtil daoUtil = prepare( SQL_TOTALS, "", filter, true, plugin ) )
        {
            daoUtil.executeQuery( );

            return daoUtil.next( ) ? new StatisticsTotals( daoUtil.getInt( 1 ), daoUtil.getInt( 2 ), daoUtil.getInt( 3 ) ) : new StatisticsTotals( 0, 0, 0 );
        }
    }

    /**
     * Counts the suggest submits deposited each day, whatever their state
     *
     * @param filter
     *            the suggest, period, category, type and department counted; its state is ignored
     * @param plugin
     *            the plugin
     * @return the number of suggest submits by day, the oldest first, the days without any left out
     */
    public TreeMap<LocalDate, Integer> countByDay( SubmitFilter filter, Plugin plugin )
    {
        TreeMap<LocalDate, Integer> mapCounts = new TreeMap<>( );

        try ( DAOUtil daoUtil = prepare( SQL_COUNT_BY_DAY, SQL_GROUP_BY_DAY, filter, false, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                if ( daoUtil.getDate( 1 ) != null )
                {
                    mapCounts.put( daoUtil.getDate( 1 ).toLocalDate( ), daoUtil.getInt( 2 ) );
                }
            }
        }

        return mapCounts;
    }

    /**
     * Counts the published suggest submits of each department of their authors, the largest first
     *
     * @param filter
     *            the suggest, period, category, type and department counted; its state is ignored
     * @param plugin
     *            the plugin
     * @return one count per department whatever its case, labelled null for the authors whose department is unknown
     */
    public List<StatisticsCount> countPublishedByDepartment( SubmitFilter filter, Plugin plugin )
    {
        Map<String, Integer> mapCounts = new TreeMap<>( String.CASE_INSENSITIVE_ORDER );

        try ( DAOUtil daoUtil = prepare( SQL_COUNT_BY_DEPARTMENT, SQL_GROUP_BY_DEPARTMENT, filter, true, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                String strDepartment = daoUtil.getString( 1 );
                mapCounts.merge( ( strDepartment == null ) ? "" : strDepartment.trim( ), daoUtil.getInt( 2 ), Integer::sum );
            }
        }

        return mapCounts.entrySet( ).stream( )
                .map( entry -> new StatisticsCount( entry.getKey( ).isEmpty( ) ? null : entry.getKey( ), null, entry.getValue( ) ) )
                .sorted( Comparator.comparingInt( StatisticsCount::getCount ).reversed( ) ).toList( );
    }

    /**
     * Counts the published suggest submits of each category
     *
     * @param filter
     *            the suggest, period, category, type and department counted; its state is ignored
     * @param plugin
     *            the plugin
     * @return the number of published suggest submits by category key, the categories without any left out
     */
    public Map<Integer, Integer> countPublishedByCategory( SubmitFilter filter, Plugin plugin )
    {
        return selectCounts( SQL_COUNT_BY_CATEGORY, SQL_GROUP_BY_CATEGORY, filter, true, plugin );
    }

    /**
     * Reads the counts of a query grouped by an integer key
     *
     * @param strSelect
     *            the select and from clauses
     * @param strEnd
     *            the group by clause
     * @param filter
     *            the filter
     * @param bPublished
     *            true to keep the published suggest submits only
     * @param plugin
     *            the plugin
     * @return the count of each key
     */
    private Map<Integer, Integer> selectCounts( String strSelect, String strEnd, SubmitFilter filter, boolean bPublished, Plugin plugin )
    {
        Map<Integer, Integer> mapCounts = new HashMap<>( );

        try ( DAOUtil daoUtil = prepare( strSelect, strEnd, filter, bPublished, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                mapCounts.put( daoUtil.getInt( 1 ), daoUtil.getInt( 2 ) );
            }
        }

        return mapCounts;
    }

    /**
     * Builds a statement restricted to the suggest, period, category, type and department of a filter
     *
     * @param strSelect
     *            the select and from clauses
     * @param strEnd
     *            the group by and order by clauses
     * @param filter
     *            the filter
     * @param bPublished
     *            true to keep the published suggest submits only
     * @param plugin
     *            the plugin
     * @return the statement, its parameters set
     */
    private DAOUtil prepare( String strSelect, String strEnd, SubmitFilter filter, boolean bPublished, Plugin plugin )
    {
        StringBuilder sbSql = new StringBuilder( strSelect ).append( "WHERE d.id_suggest = ?" );
        List<Object> listParameters = new ArrayList<>( );
        listParameters.add( filter.getIdSuggest( ) );

        if ( filter.getDateFirst( ) != null )
        {
            sbSql.append( " AND d.date_response >= ?" );
            listParameters.add( filter.getDateFirst( ) );
        }

        if ( filter.getDateLast( ) != null )
        {
            sbSql.append( " AND d.date_response <= ?" );
            listParameters.add( filter.getDateLast( ) );
        }

        if ( filter.getIdCategory( ) != SubmitFilter.ALL_INT )
        {
            sbSql.append( " AND d.id_category = ?" );
            listParameters.add( filter.getIdCategory( ) );
        }

        if ( filter.getIdType( ) != SubmitFilter.ALL_INT )
        {
            sbSql.append( " AND d.suggest_submit_type = ?" );
            listParameters.add( filter.getIdType( ) );
        }

        if ( filter.containsDepartment( ) )
        {
            sbSql.append( " AND d.lutece_user_key IN ( SELECT f.lutece_user_key FROM suggest_suggest_user_info f WHERE f.department = ? )" );
            listParameters.add( filter.getDepartment( ) );
        }

        if ( bPublished )
        {
            sbSql.append( " AND d.id_state = ?" );
            listParameters.add( SuggestSubmit.STATE_PUBLISH );
        }

        DAOUtil daoUtil = new DAOUtil( sbSql.append( strEnd ).toString( ), plugin );

        for ( int i = 0; i < listParameters.size( ); i++ )
        {
            if ( listParameters.get( i ) instanceof Timestamp timestamp )
            {
                daoUtil.setTimestamp( i + 1, timestamp );
            }
            else if ( listParameters.get( i ) instanceof String strValue )
            {
                daoUtil.setString( i + 1, strValue );
            }
            else
            {
                daoUtil.setInt( i + 1, (Integer) listParameters.get( i ) );
            }
        }

        return daoUtil;
    }
}
