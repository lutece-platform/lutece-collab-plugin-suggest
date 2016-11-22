/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for  objects SuggestSubmitDAO
 */
public final class SuggestSubmitDAO implements ISuggestSubmitDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT MAX( id_suggest_submit ) FROM suggest_suggest_submit";
    private static final String SQL_QUERY_SELECT_SUGGEST_SUBMIT_BY_FILTER = "SELECT d.id_suggest_submit,d.id_suggest,s.id_state,s.title,s.number,d.date_response, " +
        "d.vote_number,d.score_number,d.id_category,d.suggest_submit_value,d.suggest_submit_title,d.comment_enable_number,d.suggest_submit_value_show_in_the_list, " +
        "d.reported, d.lutece_user_key, d.suggest_submit_list_order, d.suggest_submit_type,d.number_view,d.disable_vote,d.is_pinned,d.disable_comment,d.id_image_resource,d.comment_number FROM suggest_suggest_submit d,suggest_suggest_submit_state s ";
    private static final String SQL_FILTER_ID_SUGGEST_SUBMIT = " d.id_suggest_submit = ? ";
    private static final String SQL_FILTER_ID_SUGGEST = " d.id_suggest = ? ";
    private static final String SQL_FILTER_ID_CATEGORY = " d.id_category = ? ";
    private static final String SQL_FILTER_ID_TYPE = " d.suggest_submit_type = ? ";
    private static final String SQL_FILTER_ID_SUGGEST_SUBMIT_STATE = " d.id_state = ? ";
    private static final String SQL_FILTER_DATE_FIRST_SUBMIT = " d.date_response >= ? ";
    private static final String SQL_FILTER_DATE_LAST_SUBMIT = " d.date_response <= ? ";
    private static final String SQL_FILTER_REPORTED = " d.reported = ? ";
    private static final String SQL_FILTER_IS_PINNED = " d.is_pinned = ? ";
    private static final String SQL_FILTER_NUMBER_VOTE = " d.vote_number = ? ";
    private static final String SQL_FILTER_CONTAINS_COMMENT_DISABLE = " d.comment_number <> d.comment_enable_number ";
    private static final String SQL_FILTER_NOT_CONTAINS_COMMENT_DISABLE = " d.comment_number = d.comment_enable_number ";
    private static final String SQL_FILTER_LUTECE_USER_KEY = " d.lutece_user_key = ? ";
    private static final String SQL_FILTER_SORT_BY_DATE_RESPONSE_ASC = " d.date_response ASC ";
    private static final String SQL_FILTER_SORT_BY_DATE_RESPONSE_DESC = " d.date_response DESC ";
    private static final String SQL_FILTER_SORT_BY_SCORE_ASC = " d.score_number ASC ";
    private static final String SQL_FILTER_SORT_BY_NUMBER_VOTE_ASC = " d.vote_number ASC ";
    private static final String SQL_FILTER_SORT_BY_NUMBER_VOTE_DESC = " d.vote_number DESC ";
    private static final String SQL_FILTER_SORT_BY_SCORE_DESC = " d.score_number DESC ";
    private static final String SQL_FILTER_SORT_BY_NUMBER_COMMENT_ENABLE_ASC = " d.comment_enable_number ASC ";
    private static final String SQL_FILTER_SORT_BY_NUMBER_COMMENT_ENABLE_DESC = " d.comment_enable_number DESC ";
    private static final String SQL_FILTER_SORT_BY_NUMBER_VIEW_ASC = " d.number_view ASC ";
    private static final String SQL_FILTER_SORT_BY_NUMBER_VIEW_DESC = " d.number_view DESC ";
    private static final String SQL_FILTER_SORT_MANUALLY = " d.suggest_submit_list_order ASC ";
    private static final String SQL_ORDER_BY = " ORDER BY ";
    private static final String SQL_JOIN_STATE = " d.id_state = s.id_state ";
    private static final String SQL_CONSTANTE_WHERE = " WHERE ";
    private static final String SQL_CONSTANT_AND = " AND ";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = SQL_QUERY_SELECT_SUGGEST_SUBMIT_BY_FILTER +
        SQL_CONSTANTE_WHERE + SQL_JOIN_STATE + SQL_CONSTANT_AND + SQL_FILTER_ID_SUGGEST_SUBMIT;
    private static final String SQL_QUERY_INSERT = "INSERT INTO suggest_suggest_submit ( id_suggest_submit,id_suggest,id_state,date_response, " +
        "vote_number,score_number,id_category,suggest_submit_value ,suggest_submit_title,comment_enable_number,suggest_submit_value_show_in_the_list,reported," +
        "lutece_user_key,suggest_submit_list_order,suggest_submit_type,number_view,disable_vote,is_pinned,disable_comment,id_image_resource,comment_number ) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM suggest_suggest_submit WHERE id_suggest_submit = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE suggest_suggest_submit SET " +
        "id_suggest_submit=?,id_suggest=?,id_state=?" +
        ",vote_number=?,score_number=?,id_category=?,suggest_submit_value=?,suggest_submit_title=? ,comment_enable_number=? ,suggest_submit_value_show_in_the_list=?,reported=?,lutece_user_key= ?,suggest_submit_list_order=?,suggest_submit_type=?,number_view=?,disable_vote=?,is_pinned= ?,disable_comment=?,id_image_resource= ?,comment_number = ? " +
        "WHERE id_suggest_submit=? ";
    private static final String SQL_QUERY_SELECT_ID_SUGGEST_SUBMIT_BY_FILTER = "SELECT d.id_suggest_submit " +
        "FROM suggest_suggest_submit d ";
    private static final String SQL_QUERY_SELECT_COUNT_BY_FILTER = "SELECT COUNT(d.id_suggest_submit) " +
        "FROM suggest_suggest_submit d ";

    //Order
    private static final String SQL_QUERY_SELECT_MAX_SUGGEST_SUBMIT_LIST_ORDER = "SELECT max(d.suggest_submit_list_order) FROM suggest_suggest_submit d WHERE d.id_suggest = ? AND " +
        SQL_FILTER_IS_PINNED;
    private static final String SQL_QUERY_UPDATE_SUGGEST_SUBMIT_LIST_ORDER = "UPDATE suggest_suggest_submit SET suggest_submit_list_order = ?  WHERE id_suggest_submit = ?";

    /**
     * Generates a new primary key
     *
     * @param plugin the plugin
     * @return The new primary key
     */
    private int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey;

        if ( !daoUtil.next(  ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;
        daoUtil.free(  );

        return nKey;
    }

    /**
     * Insert a new record in the table.
     *
     * @param suggestSubmit instance of the Suggest Submit object to insert
     * @param plugin the plugin
     * @return the id of the new Suggest
     */
    public int insert( SuggestSubmit suggestSubmit, Plugin plugin )
    {
        Timestamp timestamp = new java.sql.Timestamp( new java.util.Date(  ).getTime(  ) );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        if ( suggestSubmit.getIdSuggestSubmit(  ) == 0 )
        {
            suggestSubmit.setIdSuggestSubmit( newPrimaryKey( plugin ) );
        }

        suggestSubmit.setSuggestSubmitOrder( maxOrderSuggestSubmit( suggestSubmit.getSuggest(  ).getIdSuggest(  ), false, plugin ) + 1 );
        daoUtil.setInt( 1, suggestSubmit.getIdSuggestSubmit(  ) );
        daoUtil.setInt( 2, suggestSubmit.getSuggest(  ).getIdSuggest(  ) );
        daoUtil.setInt( 3, suggestSubmit.getSuggestSubmitState(  ).getIdSuggestSubmitState(  ) );
        daoUtil.setTimestamp( 4, timestamp );
        daoUtil.setInt( 5, suggestSubmit.getNumberVote(  ) );
        daoUtil.setInt( 6, suggestSubmit.getNumberScore(  ) );

        if ( suggestSubmit.getCategory(  ) != null )
        {
            daoUtil.setInt( 7, suggestSubmit.getCategory(  ).getIdCategory(  ) );
        }
        else
        {
            daoUtil.setIntNull( 7 );
        }

        daoUtil.setString( 8, suggestSubmit.getSuggestSubmitValue(  ) );
        daoUtil.setString( 9, suggestSubmit.getSuggestSubmitTitle(  ) );
        daoUtil.setInt( 10, suggestSubmit.getNumberCommentEnable(  ) );
        daoUtil.setString( 11, suggestSubmit.getSuggestSubmitValueShowInTheList(  ) );
        daoUtil.setBoolean( 12, suggestSubmit.isReported(  ) );
        daoUtil.setString( 13, suggestSubmit.getLuteceUserKey(  ) );
        daoUtil.setInt( 14, suggestSubmit.getSuggestSubmitOrder(  ) );

        if ( suggestSubmit.getSuggestSubmitType(  ) != null )
        {
            daoUtil.setInt( 15, suggestSubmit.getSuggestSubmitType(  ).getIdType(  ) );
        }
        else
        {
            daoUtil.setIntNull( 15 );
        }

        daoUtil.setInt( 16, suggestSubmit.getNumberView(  ) );
        daoUtil.setBoolean( 17, suggestSubmit.isDisableVote(  ) );
        daoUtil.setBoolean( 18, suggestSubmit.isPinned(  ) );
        daoUtil.setBoolean( 19, suggestSubmit.isDisableComment(  ) );

        if ( suggestSubmit.getIdImageResource(  ) != null )
        {
            daoUtil.setInt( 20, suggestSubmit.getIdImageResource(  ) );
        }
        else
        {
            daoUtil.setIntNull( 20 );
        }

        daoUtil.setInt( 21, suggestSubmit.getNumberComment(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );

        return suggestSubmit.getIdSuggestSubmit(  );
    }

    /**
     * Load the data of the suggestSubmit from the table
     *
     * @param nIdSuggestSubmit The identifier of the formResponse
     * @param plugin the plugin
     * @return the instance of the suggestSubmit
     */
    public SuggestSubmit load( int nIdSuggestSubmit, Plugin plugin )
    {
        SuggestSubmit suggestSubmit = null;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nIdSuggestSubmit );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            suggestSubmit = getRow( daoUtil );
        }

        daoUtil.free(  );

        return suggestSubmit;
    }

    /**
     * Delete   the suggest submit whose identifier is specified in parameter
     *
     * @param nIdSuggestSubmit The identifier of the suggest submit
     * @param plugin the plugin
     */
    public void delete( int nIdSuggestSubmit, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdSuggestSubmit );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Update the the suggestSubmit in the table
     *
     * @param suggestSubmit instance of the suggestSubmit object to update
     * @param plugin the plugin
     */
    public void store( SuggestSubmit suggestSubmit, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setInt( 1, suggestSubmit.getIdSuggestSubmit(  ) );
        daoUtil.setInt( 2, suggestSubmit.getSuggest(  ).getIdSuggest(  ) );
        daoUtil.setInt( 3, suggestSubmit.getSuggestSubmitState(  ).getIdSuggestSubmitState(  ) );
        daoUtil.setInt( 4, suggestSubmit.getNumberVote(  ) );
        daoUtil.setInt( 5, suggestSubmit.getNumberScore(  ) );

        if ( suggestSubmit.getCategory(  ) != null )
        {
            daoUtil.setInt( 6, suggestSubmit.getCategory(  ).getIdCategory(  ) );
        }
        else
        {
            daoUtil.setIntNull( 6 );
        }

        daoUtil.setString( 7, suggestSubmit.getSuggestSubmitValue(  ) );
        daoUtil.setString( 8, suggestSubmit.getSuggestSubmitTitle(  ) );
        daoUtil.setInt( 9, suggestSubmit.getNumberCommentEnable(  ) );
        daoUtil.setString( 10, suggestSubmit.getSuggestSubmitValueShowInTheList(  ) );
        daoUtil.setBoolean( 11, suggestSubmit.isReported(  ) );
        daoUtil.setString( 12, suggestSubmit.getLuteceUserKey(  ) );
        daoUtil.setInt( 13, suggestSubmit.getSuggestSubmitOrder(  ) );

        if ( suggestSubmit.getSuggestSubmitType(  ) != null )
        {
            daoUtil.setInt( 14, suggestSubmit.getSuggestSubmitType(  ).getIdType(  ) );
        }
        else
        {
            daoUtil.setIntNull( 14 );
        }

        daoUtil.setInt( 15, suggestSubmit.getNumberView(  ) );
        daoUtil.setBoolean( 16, suggestSubmit.isDisableVote(  ) );
        daoUtil.setBoolean( 17, suggestSubmit.isPinned(  ) );
        daoUtil.setBoolean( 18, suggestSubmit.isDisableComment(  ) );

        if ( suggestSubmit.getIdImageResource(  ) != null )
        {
            daoUtil.setInt( 19, suggestSubmit.getIdImageResource(  ) );
        }
        else
        {
            daoUtil.setIntNull( 19 );
        }

        daoUtil.setInt( 20, suggestSubmit.getNumberComment(  ) );
        daoUtil.setInt( 21, suggestSubmit.getIdSuggestSubmit(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load the data of all the suggestSubmit who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of suggestSubmit
     */
    public List<SuggestSubmit> selectListByFilter( SubmitFilter filter, Plugin plugin )
    {
        List<SuggestSubmit> suggestSubmitList = new ArrayList<SuggestSubmit>(  );

        List<String> listStrFilter = new ArrayList<String>(  );
        String strOrderBy = null;

        listStrFilter.add( SQL_JOIN_STATE );

        if ( filter.containsIdSuggestSubmit(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_SUGGEST_SUBMIT );
        }

        if ( filter.containsIdSuggest(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_SUGGEST );
        }

        if ( filter.containsIdSuggestSubmitState(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_SUGGEST_SUBMIT_STATE );
        }

        if ( filter.containsIdCategory(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_CATEGORY );
        }

        if ( filter.containsDateFirst(  ) )
        {
            listStrFilter.add( SQL_FILTER_DATE_FIRST_SUBMIT );
        }

        if ( filter.containsDateLast(  ) )
        {
            listStrFilter.add( SQL_FILTER_DATE_LAST_SUBMIT );
        }

        if ( filter.containsIdReported(  ) )
        {
            listStrFilter.add( SQL_FILTER_REPORTED );
        }

        if ( filter.containsNumberVote(  ) )
        {
            listStrFilter.add( SQL_FILTER_NUMBER_VOTE );
        }

        if ( filter.containsIdPinned(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_PINNED );
        }

        if ( filter.containsIdType(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_TYPE );
        }

        if ( filter.containsIdContainsCommentDisable(  ) )
        {
            listStrFilter.add( ( filter.getIdContainsCommentDisable(  ) == SubmitFilter.ID_TRUE )
                ? SQL_FILTER_CONTAINS_COMMENT_DISABLE : SQL_FILTER_NOT_CONTAINS_COMMENT_DISABLE );
        }

        if ( filter.containsSortBy(  ) )
        {
            strOrderBy = getOrderBy( filter.getSortBy(  ) );
        }

        String strSQL = SuggestUtils.buildRequestWithFilter( SQL_QUERY_SELECT_SUGGEST_SUBMIT_BY_FILTER, listStrFilter,
                strOrderBy );
        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.containsIdSuggestSubmit(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdSuggestSubmit(  ) );
            nIndex++;
        }

        if ( filter.containsIdSuggest(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdSuggest(  ) );
            nIndex++;
        }

        if ( filter.containsIdSuggestSubmitState(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdSuggestSubmitState(  ) );
            nIndex++;
        }

        if ( filter.containsIdCategory(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdCategory(  ) );
            nIndex++;
        }

        if ( filter.containsDateFirst(  ) )
        {
            daoUtil.setTimestamp( nIndex, filter.getDateFirst(  ) );
            nIndex++;
        }

        if ( filter.containsDateLast(  ) )
        {
            daoUtil.setTimestamp( nIndex, filter.getDateLast(  ) );
            nIndex++;
        }

        if ( filter.containsIdReported(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.convertIdBoolean( filter.getIdReported(  ) ) );
            nIndex++;
        }

        if ( filter.containsNumberVote(  ) )
        {
            daoUtil.setInt( nIndex, filter.getNumberVote(  ) );
            nIndex++;
        }

        if ( filter.containsIdPinned(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdPinned(  ) );
            nIndex++;
        }

        if ( filter.containsIdType(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdType(  ) );
            nIndex++;
        }

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            suggestSubmitList.add( getRow( daoUtil ) );
        }

        daoUtil.free(  );

        return suggestSubmitList;
    }

    /**
     * Load the id of all the suggestSubmit who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of suggestSubmit id
     */
    public List<Integer> selectIdListByFilter( SubmitFilter filter, Plugin plugin )
    {
        List<Integer> suggestSubmitIdList = new ArrayList<Integer>(  );
        List<String> listStrFilter = new ArrayList<String>(  );
        String strOrderBy = null;

        if ( filter.containsIdSuggest(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_SUGGEST );
        }

        if ( filter.containsIdSuggestSubmit(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_SUGGEST_SUBMIT );
        }

        if ( filter.containsIdSuggestSubmitState(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_SUGGEST_SUBMIT_STATE );
        }

        if ( filter.containsIdCategory(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_CATEGORY );
        }

        if ( filter.containsDateFirst(  ) )
        {
            listStrFilter.add( SQL_FILTER_DATE_FIRST_SUBMIT );
        }

        if ( filter.containsDateLast(  ) )
        {
            listStrFilter.add( SQL_FILTER_DATE_LAST_SUBMIT );
        }

        if ( filter.containsIdReported(  ) )
        {
            listStrFilter.add( SQL_FILTER_REPORTED );
        }

        if ( filter.containsNumberVote(  ) )
        {
            listStrFilter.add( SQL_FILTER_NUMBER_VOTE );
        }

        if ( filter.containsIdPinned(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_PINNED );
        }
        if ( filter.containsLuteceUserKey())
        {
            listStrFilter.add( SQL_FILTER_LUTECE_USER_KEY);
        }

        if ( filter.containsIdType(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_TYPE );
        }
       
        
        

        if ( filter.containsIdContainsCommentDisable(  ) )
        {
            listStrFilter.add( ( filter.getIdContainsCommentDisable(  ) == SubmitFilter.ID_TRUE )
                ? SQL_FILTER_CONTAINS_COMMENT_DISABLE : SQL_FILTER_NOT_CONTAINS_COMMENT_DISABLE );
        }

        if ( filter.containsSortBy(  ) )
        {
            strOrderBy = getOrderBy( filter.getSortBy(  ) );
        }

        //		if(filter.isOrderByScore())
        //		{
        //			strSQL += SQL_ORDER_BY_SCORE;
        //		}
        //		else if(filter.isOrderByCommentNumber())
        //		{
        //			strSQL += SQL_ORDER_BY_COMMENT_NUMBER_ASC;
        //		}
        //		else
        //		{
        //			strSQL += SQL_ORDER_BY_DATE_RESPONSE;
        //		}
        String strSQL = SuggestUtils.buildRequestWithFilter( SQL_QUERY_SELECT_ID_SUGGEST_SUBMIT_BY_FILTER, listStrFilter,
                strOrderBy );
        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.containsIdSuggest(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdSuggest(  ) );
            nIndex++;
        }

        if ( filter.containsIdSuggestSubmit(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdSuggestSubmit(  ) );
            nIndex++;
        }

        if ( filter.containsIdSuggestSubmitState(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdSuggestSubmitState(  ) );
            nIndex++;
        }

        if ( filter.containsIdCategory(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdCategory(  ) );
            nIndex++;
        }

        if ( filter.containsDateFirst(  ) )
        {
            daoUtil.setTimestamp( nIndex, filter.getDateFirst(  ) );
            nIndex++;
        }

        if ( filter.containsDateLast(  ) )
        {
            daoUtil.setTimestamp( nIndex, filter.getDateLast(  ) );
            nIndex++;
        }

        if ( filter.containsIdReported(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.convertIdBoolean( filter.getIdReported(  ) ) );
            nIndex++;
        }

        if ( filter.containsNumberVote(  ) )
        {
            daoUtil.setInt( nIndex, filter.getNumberVote(  ) );
            nIndex++;
        }

        if ( filter.containsIdPinned(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdPinned(  ) );
            nIndex++;
        }
        
        if ( filter.containsLuteceUserKey())
        {
        	  daoUtil.setString( nIndex, filter.getLuteceUserKey());
        	  nIndex++;
        }

        if ( filter.containsIdType(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdType(  ) );
            nIndex++;
        }
       

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            suggestSubmitIdList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return suggestSubmitIdList;
    }

    /**
     * return the number  of all the suggestSubmit who verify the filter
     * @param filter the filter
     * @param plugin the plugin
     * @return  the number  of all the suggestSubmit who verify the filter
     */
    public int selectCountByFilter( SubmitFilter filter, Plugin plugin )
    {
        int nIdCount = 0;
        List<String> listStrFilter = new ArrayList<String>(  );

        if ( filter.containsIdSuggest(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_SUGGEST );
        }

        if ( filter.containsIdSuggestSubmit(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_SUGGEST_SUBMIT );
        }

        if ( filter.containsIdSuggestSubmitState(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_SUGGEST_SUBMIT_STATE );
        }

        if ( filter.containsIdCategory(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_CATEGORY );
        }

        if ( filter.containsDateFirst(  ) )
        {
            listStrFilter.add( SQL_FILTER_DATE_FIRST_SUBMIT );
        }

        if ( filter.containsDateLast(  ) )
        {
            listStrFilter.add( SQL_FILTER_DATE_LAST_SUBMIT );
        }

        if ( filter.containsIdReported(  ) )
        {
            listStrFilter.add( SQL_FILTER_REPORTED );
        }

        if ( filter.containsNumberVote(  ) )
        {
            listStrFilter.add( SQL_FILTER_NUMBER_VOTE );
        }

        if ( filter.containsIdPinned(  ) )
        {
            listStrFilter.add( SQL_FILTER_IS_PINNED );
        }

        if ( filter.containsLuteceUserKey(  ) )
        {
            listStrFilter.add( SQL_FILTER_LUTECE_USER_KEY );
        }

        if ( filter.containsIdType(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_TYPE );
        }

        if ( filter.containsIdContainsCommentDisable(  ) )
        {
            listStrFilter.add( ( filter.getIdContainsCommentDisable(  ) == SubmitFilter.ID_TRUE )
                ? SQL_FILTER_CONTAINS_COMMENT_DISABLE : SQL_FILTER_NOT_CONTAINS_COMMENT_DISABLE );
        }

        String strSQL = SuggestUtils.buildRequestWithFilter( SQL_QUERY_SELECT_COUNT_BY_FILTER, listStrFilter, null );
        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.containsIdSuggest(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdSuggest(  ) );
            nIndex++;
        }

        if ( filter.containsIdSuggestSubmit(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdSuggestSubmit(  ) );
            nIndex++;
        }

        if ( filter.containsIdSuggestSubmitState(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdSuggestSubmitState(  ) );
            nIndex++;
        }

        if ( filter.containsIdCategory(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdCategory(  ) );
            nIndex++;
        }

        if ( filter.containsDateFirst(  ) )
        {
            daoUtil.setTimestamp( nIndex, filter.getDateFirst(  ) );
            nIndex++;
        }

        if ( filter.containsDateLast(  ) )
        {
            daoUtil.setTimestamp( nIndex, filter.getDateLast(  ) );
            nIndex++;
        }

        if ( filter.containsIdReported(  ) )
        {
            daoUtil.setBoolean( nIndex, filter.convertIdBoolean( filter.getIdReported(  ) ) );
            nIndex++;
        }

        if ( filter.containsNumberVote(  ) )
        {
            daoUtil.setInt( nIndex, filter.getNumberVote(  ) );
            nIndex++;
        }

        if ( filter.containsIdPinned(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdPinned(  ) );
            nIndex++;
        }

        if ( filter.containsLuteceUserKey(  ) )
        {
            daoUtil.setString( nIndex, filter.getLuteceUserKey(  ) );
            nIndex++;
        }

        if ( filter.containsIdType(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdType(  ) );
            nIndex++;
        }

        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nIdCount = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nIdCount;
    }

    /**
     * build the order by clause
     * @param listSortBy list of sort by
     * @return the sql order by clause
     */
    private String getOrderBy( List<Integer> listSortBy )
    {
        StringBuffer strOrderBy = new StringBuffer(  );
        String strReturn = SuggestUtils.EMPTY_STRING;
        int ncpt = 0;

        if ( ( listSortBy != null ) && ( listSortBy.size(  ) != 0 ) )
        {
            strOrderBy.append( SQL_ORDER_BY );

            for ( Integer sort : listSortBy )
            {
                ncpt++;

                switch ( sort )
                {
                    case SubmitFilter.SORT_BY_DATE_RESPONSE_ASC:
                        strOrderBy.append( SQL_FILTER_SORT_BY_DATE_RESPONSE_ASC );

                        break;

                    case SubmitFilter.SORT_BY_DATE_RESPONSE_DESC:
                        strOrderBy.append( SQL_FILTER_SORT_BY_DATE_RESPONSE_DESC );

                        break;

                    case SubmitFilter.SORT_BY_SCORE_ASC:
                        strOrderBy.append( SQL_FILTER_SORT_BY_SCORE_ASC );

                        break;

                    case SubmitFilter.SORT_BY_SCORE_DESC:
                        strOrderBy.append( SQL_FILTER_SORT_BY_SCORE_DESC );

                        break;

                    case SubmitFilter.SORT_BY_NUMBER_COMMENT_ASC:
                        strOrderBy.append( SQL_FILTER_SORT_BY_NUMBER_COMMENT_ENABLE_ASC );

                        break;

                    case SubmitFilter.SORT_BY_NUMBER_COMMENT_DESC:
                        strOrderBy.append( SQL_FILTER_SORT_BY_NUMBER_COMMENT_ENABLE_DESC );

                        break;

                    case SubmitFilter.SORT_BY_NUMBER_VIEW_ASC:
                        strOrderBy.append( SQL_FILTER_SORT_BY_NUMBER_VIEW_ASC );

                        break;

                    case SubmitFilter.SORT_BY_NUMBER_VIEW_DESC:
                        strOrderBy.append( SQL_FILTER_SORT_BY_NUMBER_VIEW_DESC );

                        break;

                    case SubmitFilter.SORT_BY_NUMBER_VOTE_ASC:
                        strOrderBy.append( SQL_FILTER_SORT_BY_NUMBER_VOTE_ASC );

                        break;

                    case SubmitFilter.SORT_BY_NUMBER_VOTE_DESC:
                        strOrderBy.append( SQL_FILTER_SORT_BY_NUMBER_VOTE_DESC );

                        break;

                    case SubmitFilter.SORT_MANUALLY:
                        strOrderBy.append( SQL_FILTER_SORT_MANUALLY );

                        break;

                    default:
                        break;
                }

                if ( ncpt < listSortBy.size(  ) )
                {
                    strOrderBy.append( "," );
                }
            }

            strReturn = strOrderBy.toString(  );

            if ( strReturn.endsWith( "," ) )
            {
                strReturn = strReturn.substring( 0, strReturn.length(  ) - 1 );
            }
        }

        return strReturn;
    }

    ////////////////////////////////////////////////////////////////////////////
    // ContactList Order management

    /**
    * Modify the order of a suggestsubmit
    * @param nNewOrder The order number
    * @param nId The Suggestsubmit identifier
    * @param plugin The plugin
    */
    public void storeSuggestSubmitOrder( int nNewOrder, int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_SUGGEST_SUBMIT_LIST_ORDER, plugin );
        daoUtil.setInt( 1, nNewOrder );
        daoUtil.setInt( 2, nId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Calculate the new max order in a list
     * @param nIdSuggest the id of the suggest
     * @return the max order of suggestsubmit
     * @param plugin The plugin
     */
    public int maxOrderSuggestSubmit( int nIdSuggest, boolean bListPinned, Plugin plugin )
    {
        int nOrder = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_MAX_SUGGEST_SUBMIT_LIST_ORDER, plugin );
        daoUtil.setInt( 1, nIdSuggest );
        daoUtil.setBoolean( 2, bListPinned );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nOrder = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nOrder;
    }

    /**
     *
     * @param daoUtil {@link DAOUtil}
     * @return {@link SuggestSubmit} SuggestSubmit
     */
    private SuggestSubmit getRow( DAOUtil daoUtil )
    {
        SuggestSubmit suggestSubmit = new SuggestSubmit(  );
        suggestSubmit.setIdSuggestSubmit( daoUtil.getInt( 1 ) );

        Suggest suggest = new Suggest(  );
        suggest.setIdSuggest( daoUtil.getInt( 2 ) );
        suggestSubmit.setSuggest( suggest );

        SuggestSubmitState suggestSubmitState = new SuggestSubmitState(  );
        suggestSubmitState.setIdSuggestSubmitState( daoUtil.getInt( 3 ) );
        suggestSubmitState.setTitle( daoUtil.getString( 4 ) );
        suggestSubmitState.setNumber( daoUtil.getInt( 5 ) );
        suggestSubmit.setSuggestSubmitState( suggestSubmitState );
        suggestSubmit.setDateResponse( daoUtil.getTimestamp( 6 ) );
        suggestSubmit.setNumberVote( daoUtil.getInt( 7 ) );
        suggestSubmit.setNumberScore( daoUtil.getInt( 8 ) );

        if ( daoUtil.getObject( 9 ) != null )
        {
            Category category = new Category(  );
            category.setIdCategory( daoUtil.getInt( 9 ) );
            suggestSubmit.setCategory( category );
        }

        suggestSubmit.setSuggestSubmitValue( daoUtil.getString( 10 ) );
        suggestSubmit.setSuggestSubmitTitle( daoUtil.getString( 11 ) );
        suggestSubmit.setNumberCommentEnable( daoUtil.getInt( 12 ) );
        suggestSubmit.setSuggestSubmitValueShowInTheList( daoUtil.getString( 13 ) );
        suggestSubmit.setReported( daoUtil.getBoolean( 14 ) );
        suggestSubmit.setLuteceUserKey( daoUtil.getString( 15 ) );
        suggestSubmit.setSuggestSubmitOrder( daoUtil.getInt( 16 ) );

        if ( daoUtil.getObject( 17 ) != null )
        {
            SuggestSubmitType suggestSubmitType = new SuggestSubmitType(  );
            suggestSubmitType.setIdType( daoUtil.getInt( 17 ) );
            suggestSubmit.setSuggestSubmitType( suggestSubmitType );
        }

        suggestSubmit.setNumberView( daoUtil.getInt( 18 ) );
        suggestSubmit.setDisableVote( daoUtil.getBoolean( 19 ) );
        suggestSubmit.setPinned( daoUtil.getBoolean( 20 ) );
        suggestSubmit.setDisableComment( daoUtil.getBoolean( 21 ) );

        if ( daoUtil.getObject( 22 ) != null )
        {
            suggestSubmit.setIdImageResource( daoUtil.getInt( 22 ) );
        }

        suggestSubmit.setNumberComment( daoUtil.getInt( 23 ) );

        return suggestSubmit;
    }
}
