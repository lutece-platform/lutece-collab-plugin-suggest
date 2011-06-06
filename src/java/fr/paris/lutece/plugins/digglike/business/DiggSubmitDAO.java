/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.digglike.business;

import com.mysql.jdbc.PacketTooBigException;

import fr.paris.lutece.plugins.digglike.utils.DiggUtils;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for  objects DiggSubmitDAO
 */
public final class DiggSubmitDAO implements IDiggSubmitDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT MAX( id_digg_submit ) FROM digglike_digg_submit";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT d.id_digg_submit,d.id_digg,s.id_state,s.title,s.number,d.date_response, " +
        "d.vote_number,d.score_number,d.id_category,d.digg_submit_value,d.digg_submit_title,d.comment_enable_number,d.digg_submit_value_show_in_the_list, " +
        "d.reported, d.lutece_user_key, d.digg_submit_list_order, d.digg_submit_type FROM digglike_digg_submit d,digglike_digg_submit_state s WHERE d.id_state=s.id_state AND id_digg_submit=? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO digglike_digg_submit ( id_digg_submit,id_digg,id_state,date_response, " +
        "vote_number,score_number,id_category,digg_submit_value ,digg_submit_title,comment_enable_number,digg_submit_value_show_in_the_list,reported," +
        "lutece_user_key,digg_submit_list_order,digg_submit_type ) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM digglike_digg_submit WHERE id_digg_submit = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE digglike_digg_submit SET " +
        "id_digg_submit=?,id_digg=?,id_state=?" +
        ",vote_number=?,score_number=?,id_category=?,digg_submit_value=?,digg_submit_title=? ,comment_enable_number=? ,digg_submit_value_show_in_the_list=?,reported=?,lutece_user_key= ?,digg_submit_list_order=?,digg_submit_type=? " +
        "WHERE id_digg_submit=? ";
    private static final String SQL_QUERY_SELECT_DIGG_SUBMIT_BY_FILTER = "SELECT d.id_digg_submit,d.id_digg,s.id_state,s.title,s.number,d.date_response,  " +
        "d.vote_number,score_number,d.id_category,d.digg_submit_value,d.digg_submit_title,d.comment_enable_number,digg_submit_value_show_in_the_list,d.reported, d.lutece_user_key, d.digg_submit_list_order, d.digg_submit_type  " +
        "FROM digglike_digg_submit d, digglike_digg_submit_state s ";
    private static final String SQL_QUERY_SELECT_ID_DIGG_SUBMIT_BY_FILTER = "SELECT d.id_digg_submit " +
        "FROM digglike_digg_submit d ";
    private static final String SQL_QUERY_SELECT_COUNT_BY_FILTER = "SELECT COUNT(d.id_digg_submit) " +
        "FROM digglike_digg_submit d ";
    private static final String SQL_FILTER_ID_DIGG_SUBMIT = " d.id_digg_submit = ? ";
    private static final String SQL_FILTER_ID_DIGG = " d.id_digg = ? ";
    private static final String SQL_FILTER_ID_CATEGORY = " d.id_category = ? ";
    private static final String SQL_FILTER_ID_DIGG_SUBMIT_STATE = " d.id_state = ? ";
    private static final String SQL_FILTER_DATE_FIRST_SUBMIT = " d.date_response >= ? ";
    private static final String SQL_FILTER_DATE_LAST_SUBMIT = " d.date_response <= ? ";
    private static final String SQL_FILTER_REPORTED = " d.reported = ? ";
    private static final String SQL_FILTER_NUMBER_VOTE = " d.vote_number = ? ";
    private static final String SQL_FILTER_LUTECE_USER_KEY = " d.lutece_user_key = ? ";
    private static final String SQL_FILTER_SORT_BY_DATE_RESPONSE_ASC = " d.date_response ASC ";
    private static final String SQL_FILTER_SORT_BY_DATE_RESPONSE_DESC = " d.date_response DESC ";
    private static final String SQL_FILTER_SORT_BY_SCORE_ASC = " d.score_number ASC ";
    private static final String SQL_FILTER_SORT_BY_NUMBER_VOTE_ASC = " d.vote_number ASC ";
    private static final String SQL_FILTER_SORT_BY_NUMBER_VOTE_DESC = " d.vote_number DESC ";
    private static final String SQL_FILTER_SORT_BY_SCORE_DESC = " d.score_number DESC ";
    private static final String SQL_FILTER_SORT_BY_NUMBER_COMMENT_ASC = " d.comment_enable_number ASC ";
    private static final String SQL_FILTER_SORT_BY_NUMBER_COMMENT_DESC = " d.comment_enable_number DESC ";
    private static final String SQL_FILTER_SORT_MANUALLY = " d.digg_submit_list_order ASC ";
    private static final String SQL_ORDER_BY = " ORDER BY ";
    private static final String SQL_FILTER_ID_STATE = " d.id_state = s.id_state ";

    // ImageResource queries
    private static final String SQL_QUERY_SELECT_RESOURCE_IMAGE = " SELECT image_content, image_mime_type FROM digglike_image WHERE id_digg_submit = ? ";
    private static final String SQL_QUERY_INSERT_RESOURCE_IMAGE = " INSERT INTO digglike_image (id_digg_submit, image_content, image_mime_type) VALUES (?,?,?)";
    private static final String SQL_QUERY_DELETE_RESOURCE_IMAGE = " DELETE FROM digglike_image WHERE id_digg_submit = ? ";

    //Order
    private static final String SQL_QUERY_SELECT_MAX_DIGG_SUBMIT_LIST_ORDER = "SELECT max(digg_submit_list_order) FROM digglike_digg_submit WHERE id_digg = ?";
    private static final String SQL_QUERY_SELECT_DIGG_SUBMIT_LIST_ID_BY_ORDER = "SELECT id_digg_submit FROM digglike_digg_submit WHERE digg_submit_list_order = ?";
    private static final String SQL_QUERY_SELECT_DIGG_SUBMIT_LIST_ORDER_BY_ID = "SELECT digg_submit_list_order FROM digglike_digg_submit WHERE id_digg_submit = ?";
    private static final String SQL_QUERY_UPDATE_DIGG_SUBMIT_LIST_ORDER = "UPDATE digglike_digg_submit SET digg_submit_list_order = ?  WHERE id_digg_submit = ?";

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
     * @param diggSubmit instance of the Digg Submit object to insert
     * @param plugin the plugin
     * @return the id of the new Digg
     */
    public int insert( DiggSubmit diggSubmit, Plugin plugin )
    {
        Timestamp timestamp = new java.sql.Timestamp( new java.util.Date(  ).getTime(  ) );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        if ( diggSubmit.getIdDiggSubmit(  ) == 0 )
        {
            diggSubmit.setIdDiggSubmit( newPrimaryKey( plugin ) );
        }

        diggSubmit.setDiggSubmitOrder( maxOrderDiggSubmit( diggSubmit.getDigg(  ).getIdDigg(  ), plugin ) + 1 );
        daoUtil.setInt( 1, diggSubmit.getIdDiggSubmit(  ) );
        daoUtil.setInt( 2, diggSubmit.getDigg(  ).getIdDigg(  ) );
        daoUtil.setInt( 3, diggSubmit.getDiggSubmitState(  ).getIdDiggSubmitState(  ) );
        daoUtil.setTimestamp( 4, timestamp );
        daoUtil.setInt( 5, diggSubmit.getNumberVote(  ) );
        daoUtil.setInt( 6, diggSubmit.getNumberScore(  ) );

        if ( diggSubmit.getCategory(  ) != null )
        {
            daoUtil.setInt( 7, diggSubmit.getCategory(  ).getIdCategory(  ) );
        }
        else
        {
            daoUtil.setIntNull( 7 );
        }

        daoUtil.setString( 8, diggSubmit.getDiggSubmitValue(  ) );
        daoUtil.setString( 9, diggSubmit.getDiggSubmitTitle(  ) );
        daoUtil.setInt( 10, diggSubmit.getNumberCommentEnable(  ) );
        daoUtil.setString( 11, diggSubmit.getDiggSubmitValueShowInTheList(  ) );
        daoUtil.setBoolean( 12, diggSubmit.isReported(  ) );
        daoUtil.setString( 13, diggSubmit.getLuteceUserKey(  ) );
        daoUtil.setInt( 14, diggSubmit.getDiggSubmitOrder(  ) );

        if ( diggSubmit.getDiggSubmitType(  ) != null )
        {
            daoUtil.setInt( 15, diggSubmit.getDiggSubmitType(  ).getIdType(  ) );
        }
        else
        {
            daoUtil.setIntNull( 15 );
        }

        daoUtil.executeUpdate(  );
        daoUtil.free(  );

        return diggSubmit.getIdDiggSubmit(  );
    }

    /**
     * Load the data of the diggSubmit from the table
     *
     * @param nIdDiggSubmit The identifier of the formResponse
     * @param plugin the plugin
     * @return the instance of the diggSubmit
     */
    public DiggSubmit load( int nIdDiggSubmit, Plugin plugin )
    {
        DiggSubmit diggSubmit = null;
        Digg digg;
        DiggSubmitState diggSubmitState;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nIdDiggSubmit );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            diggSubmit = new DiggSubmit(  );
            diggSubmit.setIdDiggSubmit( daoUtil.getInt( 1 ) );

            digg = new Digg(  );
            digg.setIdDigg( daoUtil.getInt( 2 ) );
            diggSubmit.setDigg( digg );

            diggSubmitState = new DiggSubmitState(  );
            diggSubmitState.setIdDiggSubmitState( daoUtil.getInt( 3 ) );
            diggSubmitState.setTitle( daoUtil.getString( 4 ) );
            diggSubmitState.setNumber( daoUtil.getInt( 5 ) );

            diggSubmit.setDiggSubmitState( diggSubmitState );

            diggSubmit.setDateResponse( daoUtil.getTimestamp( 6 ) );

            diggSubmit.setNumberVote( daoUtil.getInt( 7 ) );
            diggSubmit.setNumberScore( daoUtil.getInt( 8 ) );

            if ( daoUtil.getObject( 9 ) != null )
            {
                diggSubmit.setCategory( CategoryHome.findByPrimaryKey( daoUtil.getInt( 9 ), plugin ) );
            }

            diggSubmit.setDiggSubmitValue( daoUtil.getString( 10 ) );
            diggSubmit.setDiggSubmitTitle( daoUtil.getString( 11 ) );
            diggSubmit.setNumberCommentEnable( daoUtil.getInt( 12 ) );
            diggSubmit.setDiggSubmitValueShowInTheList( daoUtil.getString( 13 ) );
            diggSubmit.setReported( daoUtil.getBoolean( 14 ) );
            diggSubmit.setLuteceUserKey( daoUtil.getString( 15 ) );
            diggSubmit.setDiggSubmitOrder( daoUtil.getInt( 16 ) );

            if ( daoUtil.getObject( 17 ) != null )
            {
                diggSubmit.setDiggSubmitType( DiggSubmitTypeHome.findByPrimaryKey( daoUtil.getInt( 17 ), plugin ) );
            }

            //import the comments of all the diggs 
            SubmitFilter submmitFilterComment = new SubmitFilter(  );
            submmitFilterComment.setIdDiggSubmit( diggSubmit.getIdDiggSubmit(  ) );
            submmitFilterComment.setIdCommentSubmitState( 1 );
            diggSubmit.setComments( CommentSubmitHome.getCommentSubmitList( submmitFilterComment, plugin ) );
        }

        daoUtil.free(  );

        return diggSubmit;
    }

    /**
     * Delete   the digg submit whose identifier is specified in parameter
     *
     * @param nIdDiggSubmit The identifier of the digg submit
     * @param plugin the plugin
     */
    public void delete( int nIdDiggSubmit, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdDiggSubmit );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Update the the diggSubmit in the table
     *
     * @param diggSubmit instance of the diggSubmit object to update
     * @param plugin the plugin
     */
    public void store( DiggSubmit diggSubmit, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setInt( 1, diggSubmit.getIdDiggSubmit(  ) );
        daoUtil.setInt( 2, diggSubmit.getDigg(  ).getIdDigg(  ) );
        daoUtil.setInt( 3, diggSubmit.getDiggSubmitState(  ).getIdDiggSubmitState(  ) );
        daoUtil.setInt( 4, diggSubmit.getNumberVote(  ) );
        daoUtil.setInt( 5, diggSubmit.getNumberScore(  ) );

        if ( diggSubmit.getCategory(  ) != null )
        {
            daoUtil.setInt( 6, diggSubmit.getCategory(  ).getIdCategory(  ) );
        }
        else
        {
            daoUtil.setIntNull( 6 );
        }

        daoUtil.setString( 7, diggSubmit.getDiggSubmitValue(  ) );
        daoUtil.setString( 8, diggSubmit.getDiggSubmitTitle(  ) );
        daoUtil.setInt( 9, diggSubmit.getNumberCommentEnable(  ) );
        daoUtil.setString( 10, diggSubmit.getDiggSubmitValueShowInTheList(  ) );
        daoUtil.setBoolean( 11, diggSubmit.isReported(  ) );
        daoUtil.setString( 12, diggSubmit.getLuteceUserKey(  ) );
        daoUtil.setInt( 13, diggSubmit.getDiggSubmitOrder(  ) );

        if ( diggSubmit.getDiggSubmitType(  ) != null )
        {
            daoUtil.setInt( 14, diggSubmit.getDiggSubmitType(  ).getIdType(  ) );
        }
        else
        {
            daoUtil.setIntNull( 14 );
        }

        daoUtil.setInt( 15, diggSubmit.getIdDiggSubmit(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load the data of all the diggSubmit who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of diggSubmit
     */
    public List<DiggSubmit> selectListByFilter( SubmitFilter filter, Plugin plugin )
    {
        List<DiggSubmit> diggSubmitList = new ArrayList<DiggSubmit>(  );
        DiggSubmit diggSubmit = null;
        Digg digg;
        DiggSubmitState diggSubmitState;
        List<String> listStrFilter = new ArrayList<String>(  );
        String strOrderBy = null;

        listStrFilter.add( SQL_FILTER_ID_STATE );

        if ( filter.containsIdDiggSubmit(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_DIGG_SUBMIT );
        }

        if ( filter.containsIdDigg(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_DIGG );
        }

        if ( filter.containsIdDiggSubmitState(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_DIGG_SUBMIT_STATE );
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

        if ( filter.containsSortBy(  ) )
        {
            strOrderBy = getOrderBy( filter.getSortBy(  ) );
        }

        String strSQL = DiggUtils.buildRequestWithFilter( SQL_QUERY_SELECT_DIGG_SUBMIT_BY_FILTER, listStrFilter,
                strOrderBy );
        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.containsIdDiggSubmit(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdDiggSubmit(  ) );
            nIndex++;
        }

        if ( filter.containsIdDigg(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdDigg(  ) );
            nIndex++;
        }

        if ( filter.containsIdDiggSubmitState(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdDiggSubmitState(  ) );
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

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            diggSubmit = new DiggSubmit(  );
            diggSubmit.setIdDiggSubmit( daoUtil.getInt( 1 ) );

            digg = new Digg(  );
            digg.setIdDigg( daoUtil.getInt( 2 ) );
            diggSubmit.setDigg( digg );

            diggSubmitState = new DiggSubmitState(  );
            diggSubmitState.setIdDiggSubmitState( daoUtil.getInt( 3 ) );
            diggSubmitState.setTitle( daoUtil.getString( 4 ) );
            diggSubmitState.setNumber( daoUtil.getInt( 5 ) );
            diggSubmit.setDiggSubmitState( diggSubmitState );
            diggSubmit.setDateResponse( daoUtil.getTimestamp( 6 ) );
            diggSubmit.setNumberVote( daoUtil.getInt( 7 ) );
            diggSubmit.setNumberScore( daoUtil.getInt( 8 ) );

            if ( daoUtil.getObject( 9 ) != null )
            {
                diggSubmit.setCategory( CategoryHome.findByPrimaryKey( daoUtil.getInt( 9 ), plugin ) );
            }

            diggSubmit.setDiggSubmitValue( daoUtil.getString( 10 ) );
            diggSubmit.setDiggSubmitTitle( daoUtil.getString( 11 ) );
            diggSubmit.setNumberCommentEnable( daoUtil.getInt( 12 ) );
            diggSubmit.setDiggSubmitValueShowInTheList( daoUtil.getString( 13 ) );
            diggSubmit.setReported( daoUtil.getBoolean( 14 ) );
            diggSubmit.setLuteceUserKey( daoUtil.getString( 15 ) );
            diggSubmit.setDiggSubmitOrder( daoUtil.getInt( 16 ) );

            if ( daoUtil.getObject( 17 ) != null )
            {
                diggSubmit.setDiggSubmitType( DiggSubmitTypeHome.findByPrimaryKey( daoUtil.getInt( 17 ), plugin ) );
            }

            //import the comments of all the diggs 
            SubmitFilter submmitFilterComment = new SubmitFilter(  );
            submmitFilterComment.setIdDiggSubmit( diggSubmit.getIdDiggSubmit(  ) );
            submmitFilterComment.setIdCommentSubmitState( 1 );
            diggSubmit.setComments( CommentSubmitHome.getCommentSubmitList( submmitFilterComment, plugin ) );

            diggSubmitList.add( diggSubmit );
        }

        daoUtil.free(  );

        return diggSubmitList;
    }

    /**
     * Load the id of all the diggSubmit who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of diggSubmit id
     */
    public List<Integer> selectIdListByFilter( SubmitFilter filter, Plugin plugin )
    {
        List<Integer> diggSubmitIdList = new ArrayList<Integer>(  );
        List<String> listStrFilter = new ArrayList<String>(  );
        String strOrderBy = null;

        if ( filter.containsIdDigg(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_DIGG );
        }

        if ( filter.containsIdDiggSubmitState(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_DIGG_SUBMIT_STATE );
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
        String strSQL = DiggUtils.buildRequestWithFilter( SQL_QUERY_SELECT_ID_DIGG_SUBMIT_BY_FILTER, listStrFilter,
                strOrderBy );
        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.containsIdDigg(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdDigg(  ) );
            nIndex++;
        }

        if ( filter.containsIdDiggSubmitState(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdDiggSubmitState(  ) );
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

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            diggSubmitIdList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return diggSubmitIdList;
    }

    /**
     * return the number  of all the diggSubmit who verify the filter
     * @param filter the filter
     * @param plugin the plugin
     * @return  the number  of all the diggSubmit who verify the filter
     */
    public int selectCountByFilter( SubmitFilter filter, Plugin plugin )
    {
        int nIdCount = 0;
        List<String> listStrFilter = new ArrayList<String>(  );

        if ( filter.containsIdDigg(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_DIGG );
        }

        if ( filter.containsIdDiggSubmitState(  ) )
        {
            listStrFilter.add( SQL_FILTER_ID_DIGG_SUBMIT_STATE );
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

        if ( filter.containsLuteceUserKey(  ) )
        {
            listStrFilter.add( SQL_FILTER_LUTECE_USER_KEY );
        }

        String strSQL = DiggUtils.buildRequestWithFilter( SQL_QUERY_SELECT_COUNT_BY_FILTER, listStrFilter, null );
        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.containsIdDigg(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdDigg(  ) );
            nIndex++;
        }

        if ( filter.containsIdDiggSubmitState(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdDiggSubmitState(  ) );
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

        if ( filter.containsLuteceUserKey(  ) )
        {
            daoUtil.setString( nIndex, filter.getLuteceUserKey(  ) );
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
                        strOrderBy.append( SQL_FILTER_SORT_BY_NUMBER_COMMENT_ASC );

                        break;

                    case SubmitFilter.SORT_BY_NUMBER_COMMENT_DESC:
                        strOrderBy.append( SQL_FILTER_SORT_BY_NUMBER_COMMENT_DESC );

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
        }

        return strOrderBy.toString(  );
    }

    /**
     * Return the image resource corresponding to the image id
     * @param nImageId The identifier of image object
     * @param plugin the Plugin
     * @return The image resource
     */
    public ImageResource loadImageResource( int nImageId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_RESOURCE_IMAGE, plugin );
        daoUtil.setInt( 1, nImageId );
        daoUtil.executeQuery(  );

        ImageResource image = null;

        if ( daoUtil.next(  ) )
        {
            image = new ImageResource(  );
            image.setImage( daoUtil.getBytes( 1 ) );
            image.setMimeType( daoUtil.getString( 2 ) );
        }

        daoUtil.free(  );

        return image;
    }

    /**
     * @param nIdDiggSubmit the id of the diggSubmit
     * @param image : the image to add
     * @param plugin : plugin
     * @return return the id of the image/diggsubmit
     * @throws com.mysql.jdbc.PacketTooBigException if the image is too big
     */
    public int insertImageResource( int nIdDiggSubmit, ImageResource image, Plugin plugin )
        throws com.mysql.jdbc.PacketTooBigException
    {
        //drop image if this id exist
        int nId = nIdDiggSubmit;

        if ( loadImageResource( nId, plugin ) != null )
        {
            DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_RESOURCE_IMAGE );
            daoUtil.setInt( 1, nId );
            daoUtil.executeUpdate(  );
            daoUtil.free(  );
        }

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_RESOURCE_IMAGE, plugin );

        daoUtil.setInt( 1, nId );
        daoUtil.setBytes( 2, image.getImage(  ) );
        daoUtil.setString( 3, image.getMimeType(  ) );

        try
        {
            daoUtil.executeUpdate(  );
        }
        catch ( Exception e )
        {
            throw new PacketTooBigException( 0, 0 );
        }

        daoUtil.free(  );

        return nId;
    }

    ////////////////////////////////////////////////////////////////////////////
    // ContactList Order management

    /**
    * Modify the order of a diggsubmit
    * @param nNewOrder The order number
    * @param nId The Diggsubmit identifier
    * @param plugin The plugin
    */
    public void storeDiggSubmitOrder( int nNewOrder, int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_DIGG_SUBMIT_LIST_ORDER, plugin );
        daoUtil.setInt( 1, nNewOrder );
        daoUtil.setInt( 2, nId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Returns a diggsubmit identifier in a distinct order
     * @return The order of the DiggSubmitList
     * @param nDiggSubmitListOrder The order number
     * @param plugin The plugin
     */
    public int selectDiggSubmitIdByOrder( int nDiggSubmitListOrder, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_DIGG_SUBMIT_LIST_ID_BY_ORDER, plugin );
        int nResult = 0;
        daoUtil.setInt( 1, nDiggSubmitListOrder );
        daoUtil.executeQuery(  );

        if ( !daoUtil.next(  ) )
        {
            // If number order doesn't exist
            nResult = 1;
        }
        else
        {
            nResult = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nResult;
    }

    /**
     * Returns the order of a DiggSubmit
     * @param nIdDiggSubmit the id of DiggSubmit
     * @param plugin the plugin contact
     * @return the order of the DiggSubmit
     */
    public int selectDiggSubmitOrderById( int nIdDiggSubmit, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_DIGG_SUBMIT_LIST_ORDER_BY_ID, plugin );
        int nResult = 0;
        daoUtil.setInt( 1, nIdDiggSubmit );
        daoUtil.executeQuery(  );

        if ( !daoUtil.next(  ) )
        {
            // If number order doesn't exist
            nResult = 1;
        }
        else
        {
            nResult = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nResult;
    }

    /**
     * Calculate the new max order in a list
     * @param nIdDigg the id of the digg
     * @return the max order of diggsubmit
     * @param plugin The plugin
     */
    public int maxOrderDiggSubmit( int nIdDigg, Plugin plugin )
    {
        int nOrder = 0;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_MAX_DIGG_SUBMIT_LIST_ORDER, plugin );
        daoUtil.setInt( 1, nIdDigg );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nOrder = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nOrder;
    }
}
