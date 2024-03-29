/*
 * Copyright (c) 2002-2020, Mairie de Paris
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
import java.util.Date;
import java.util.List;

/**
 * This class provides Data Access methods for objects CommentSubmitDAO
 */
public final class CommentSubmitDAO implements ICommentSubmitDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT MAX( id_comment_submit ) FROM suggest_comment_submit";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_comment_submit,id_suggest_submit,date_comment,comment_value,active, lutece_user_key, official_answer, id_parent_comment,date_modify  "
            + "FROM suggest_comment_submit WHERE id_comment_submit=? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO suggest_comment_submit ( id_comment_submit,id_suggest_submit,date_comment,comment_value,active,lutece_user_key,official_answer,id_parent_comment,date_modify ) "
            + "VALUES(?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM suggest_comment_submit WHERE id_comment_submit = ? ";
    private static final String SQL_QUERY_DELETE_BY_ID_PARENT = "DELETE FROM suggest_comment_submit WHERE id_parent_comment = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE suggest_comment_submit SET "
            + "id_comment_submit=?,id_suggest_submit=?,date_comment=?,comment_value=?,active=? ,lutece_user_key=? ,official_answer=? ,id_parent_comment=?"
            + " WHERE id_comment_submit=? ";
    private static final String SQL_QUERY_SELECT_COMMENT_SUBMIT_BY_FILTER = "SELECT dc.id_comment_submit,dc.id_suggest_submit,date_comment,dc.comment_value,dc.active,dc.lutece_user_key,dc.official_answer,dc.id_parent_comment,dc.date_modify "
            + "FROM suggest_comment_submit dc ";
    private static final String SQL_QUERY_SELECT_COUNT_BY_FILTER = "SELECT COUNT(dc.id_comment_submit) "
            + "FROM suggest_comment_submit dc INNER JOIN suggest_suggest_submit ds ON dc.id_suggest_submit = ds.id_suggest_submit ";
    private static final String SQL_FILTER_ID_SUGGEST_SUBMIT = " dc.id_suggest_submit = ? ";
    private static final String SQL_FILTER_ID_PARENT_COMMENT = "dc.id_parent_comment = ? ";
    private static final String SQL_FILTER_ID_SUGGEST = " ds.id_suggest = ? ";
    private static final String SQL_FILTER_COMMENT_SUBMIT_STATE = " dc.active = ? ";
    private static final String SQL_FILTER_CONTAINS_SUB_COMMENT_DISABLE = " dc.id_suggest_submit IN ( SELECT id_parent_comment FROM suggest_comment_submit WHERE id_parent_comment = dc.id_comment_submit and active=0  ) ";
    private static final String SQL_FILTER_NOT_CONTAINS_SUB_COMMENT_DISABLE = " dc.id_suggest_submit NOT IN ( SELECT id_parent_comment FROM suggest_comment_submit WHERE id_parent_comment = dc.id_comment_submit and active=0  )  ";
    private static final String SQL_QUERY_SELECT_COMMENTED_SUGGEST_SUBMIT = " select id_comment_submit,id_suggest_submit,date_comment,comment_value,active, lutece_user_key, official_answer, id_parent_comment,date_modify FROM suggest_comment_submit WHERE date_comment > ? ";
    private static final String SQL_FILTER_SORT_BY_DATE_COMMENT_DESC = " dc.date_comment DESC";
    private static final String SQL_FILTER_SORT_BY_DATE_COMMENT_ASC = " dc.date_comment ASC";
    private static final String SQL_FILTER_SORT_BY_DATE_MODIFY_COMMENT_DESC = " dc.date_modify DESC";
    private static final String SQL_FILTER_SORT_BY_DATE_MODIFY_COMMENT_ASC = " dc.date_modify ASC";
    private static final String SQL_QUERY_UPDATE_DATE_MODIFY = "UPDATE suggest_comment_submit SET date_modify=? WHERE id_comment_submit=? ";
    private static final String SQL_ORDER_BY = " ORDER BY ";
    private static final String SQL_LIMIT = " LIMIT ";

    /**
     * Generates a new primary key
     *
     * @param plugin
     *            the plugin
     * @return The new primary key
     */
    private int newPrimaryKey( Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin ) )
        {
            daoUtil.executeQuery( );

            daoUtil.next( );
            return daoUtil.getInt( 1 ) + 1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert( CommentSubmit commentSubmit, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            commentSubmit.setIdCommentSubmit( newPrimaryKey( plugin ) );
            daoUtil.setInt( 1, commentSubmit.getIdCommentSubmit( ) );
            daoUtil.setInt( 2, commentSubmit.getSuggestSubmit( ).getIdSuggestSubmit( ) );
            daoUtil.setTimestamp( 3, commentSubmit.getDateComment( ) );
            daoUtil.setString( 4, commentSubmit.getValue( ) );
            daoUtil.setBoolean( 5, commentSubmit.isActive( ) );
            daoUtil.setString( 6, commentSubmit.getLuteceUserKey( ) );
            daoUtil.setBoolean( 7, commentSubmit.isOfficialAnswer( ) );
            daoUtil.setInt( 8, commentSubmit.getIdParent( ) );
            daoUtil.setTimestamp( 9, commentSubmit.getDateModify( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentSubmit load( int nIdCommentSubmit, Plugin plugin )
    {
        CommentSubmit commentSubmit = null;
        SuggestSubmit suggestSubmit;

        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin ) )
        {
            daoUtil.setInt( 1, nIdCommentSubmit );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                commentSubmit = new CommentSubmit( );
                commentSubmit.setIdCommentSubmit( daoUtil.getInt( 1 ) );

                suggestSubmit = new SuggestSubmit( );
                suggestSubmit.setIdSuggestSubmit( daoUtil.getInt( 2 ) );
                commentSubmit.setSuggestSubmit( suggestSubmit );

                commentSubmit.setDateComment( daoUtil.getTimestamp( 3 ) );
                commentSubmit.setValue( daoUtil.getString( 4 ) );
                commentSubmit.setActive( daoUtil.getBoolean( 5 ) );
                commentSubmit.setLuteceUserKey( daoUtil.getString( 6 ) );
                commentSubmit.setOfficialAnswer( daoUtil.getBoolean( 7 ) );
                commentSubmit.setIdParent( daoUtil.getInt( 8 ) );
                commentSubmit.setDateModify( daoUtil.getTimestamp( 9 ) );
            }

            return commentSubmit;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdTagSubmit, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdTagSubmit );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store( CommentSubmit commentSubmit, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            daoUtil.setInt( 1, commentSubmit.getIdCommentSubmit( ) );
            daoUtil.setInt( 2, commentSubmit.getSuggestSubmit( ).getIdSuggestSubmit( ) );
            daoUtil.setTimestamp( 3, commentSubmit.getDateComment( ) );
            daoUtil.setString( 4, commentSubmit.getValue( ) );
            daoUtil.setBoolean( 5, commentSubmit.isActive( ) );
            daoUtil.setString( 6, commentSubmit.getLuteceUserKey( ) );
            daoUtil.setBoolean( 7, commentSubmit.isOfficialAnswer( ) );
            daoUtil.setInt( 8, commentSubmit.getIdParent( ) );
            daoUtil.setInt( 9, commentSubmit.getIdCommentSubmit( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeDateModify( Timestamp dateModify, int idCommentSubmit, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_DATE_MODIFY, plugin ) )
        {
            daoUtil.setTimestamp( 1, dateModify );
            daoUtil.setInt( 2, idCommentSubmit );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CommentSubmit> selectListByFilter( SubmitFilter filter, Integer nLimit, Plugin plugin )
    {
        List<CommentSubmit> commentSubmitList = new ArrayList<>( );
        CommentSubmit commentSubmit = null;
        SuggestSubmit suggestSubmit = null;
        List<String> listStrFilter = new ArrayList<>( );
        String strOrderBy = null;

        if ( filter.containsIdSuggestSubmit( ) )
        {
            listStrFilter.add( SQL_FILTER_ID_SUGGEST_SUBMIT );
        }

        if ( filter.containsIdCommentSubmitState( ) )
        {
            listStrFilter.add( SQL_FILTER_COMMENT_SUBMIT_STATE );
        }

        if ( filter.containsIdParent( ) )
        {
            listStrFilter.add( SQL_FILTER_ID_PARENT_COMMENT );
        }

        if ( filter.containsIdContainsCommentDisable( ) )
        {
            listStrFilter.add( ( filter.getIdContainsCommentDisable( ) == SubmitFilter.ID_TRUE ) ? SQL_FILTER_CONTAINS_SUB_COMMENT_DISABLE
                    : SQL_FILTER_NOT_CONTAINS_SUB_COMMENT_DISABLE );
        }

        if ( filter.containsSortBy( ) )
        {
            strOrderBy = getOrderBy( filter.getSortBy( ) );
        }

        String strSQL = SuggestUtils.buildRequestWithFilter( SQL_QUERY_SELECT_COMMENT_SUBMIT_BY_FILTER, listStrFilter, strOrderBy );

        if ( ( nLimit != null ) && ( nLimit != SuggestUtils.CONSTANT_ID_NULL ) )
        {
            strSQL = strSQL + SQL_LIMIT + nLimit;
        }

        try( DAOUtil daoUtil = new DAOUtil( strSQL, plugin ) )
        {
            int nIndex = 1;

            if ( filter.containsIdSuggestSubmit( ) )
            {
                daoUtil.setInt( nIndex, filter.getIdSuggestSubmit( ) );
                nIndex++;
            }

            if ( filter.containsIdCommentSubmitState( ) )
            {
                daoUtil.setBoolean( nIndex, filter.convertIdBoolean( filter.getIdCommentSubmitState( ) ) );
                nIndex++;
            }

            if ( filter.containsIdParent( ) )
            {
                daoUtil.setInt( nIndex, filter.getIdParent( ) );
                nIndex++;
            }

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                commentSubmit = new CommentSubmit( );
                commentSubmit.setIdCommentSubmit( daoUtil.getInt( 1 ) );

                suggestSubmit = new SuggestSubmit( );
                suggestSubmit.setIdSuggestSubmit( daoUtil.getInt( 2 ) );
                commentSubmit.setSuggestSubmit( suggestSubmit );

                commentSubmit.setDateComment( daoUtil.getTimestamp( 3 ) );
                commentSubmit.setValue( daoUtil.getString( 4 ) );
                commentSubmit.setActive( daoUtil.getBoolean( 5 ) );
                commentSubmit.setLuteceUserKey( daoUtil.getString( 6 ) );
                commentSubmit.setOfficialAnswer( daoUtil.getBoolean( 7 ) );
                commentSubmit.setIdParent( daoUtil.getInt( 8 ) );
                commentSubmit.setDateModify( daoUtil.getTimestamp( 9 ) );
                commentSubmitList.add( commentSubmit );
            }

        }

        return commentSubmitList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CommentSubmit> findSuggestCommentByDate( Date dateCreation, Plugin plugin )
    {
        List<CommentSubmit> listComment = new ArrayList<>( );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_COMMENTED_SUGGEST_SUBMIT, plugin ) )
        {
            daoUtil.setTimestamp( 1, new Timestamp( dateCreation.getTime( ) ) );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                CommentSubmit commentSubmit = new CommentSubmit( );
                commentSubmit.setIdCommentSubmit( daoUtil.getInt( 1 ) );

                SuggestSubmit suggestSubmit = new SuggestSubmit( );
                suggestSubmit.setIdSuggestSubmit( daoUtil.getInt( 2 ) );
                commentSubmit.setSuggestSubmit( suggestSubmit );

                commentSubmit.setDateComment( daoUtil.getTimestamp( 3 ) );
                commentSubmit.setValue( daoUtil.getString( 4 ) );
                commentSubmit.setActive( daoUtil.getBoolean( 5 ) );
                commentSubmit.setLuteceUserKey( daoUtil.getString( 6 ) );
                commentSubmit.setOfficialAnswer( daoUtil.getBoolean( 7 ) );
                commentSubmit.setIdParent( daoUtil.getInt( 8 ) );
                commentSubmit.setDateModify( daoUtil.getTimestamp( 9 ) );
                listComment.add( commentSubmit );
            }

            return listComment;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int selectCountByFilter( SubmitFilter filter, Plugin plugin )
    {
        int nIdCount = 0;
        List<String> listStrFilter = new ArrayList<>( );

        if ( filter.containsIdSuggest( ) )
        {
            listStrFilter.add( SQL_FILTER_ID_SUGGEST );
        }

        if ( filter.containsIdSuggestSubmit( ) )
        {
            listStrFilter.add( SQL_FILTER_ID_SUGGEST_SUBMIT );
        }

        if ( filter.containsIdCommentSubmitState( ) )
        {
            listStrFilter.add( SQL_FILTER_COMMENT_SUBMIT_STATE );
        }

        if ( filter.containsIdContainsCommentDisable( ) )
        {
            listStrFilter.add( ( filter.getIdContainsCommentDisable( ) == SubmitFilter.ID_TRUE ) ? SQL_FILTER_CONTAINS_SUB_COMMENT_DISABLE
                    : SQL_FILTER_NOT_CONTAINS_SUB_COMMENT_DISABLE );
        }

        String strSQL = SuggestUtils.buildRequestWithFilter( SQL_QUERY_SELECT_COUNT_BY_FILTER, listStrFilter, null );
        try( DAOUtil daoUtil = new DAOUtil( strSQL, plugin ) )
        {
            int nIndex = 1;

            if ( filter.containsIdSuggest( ) )
            {
                daoUtil.setInt( nIndex, filter.getIdSuggest( ) );
                nIndex++;
            }

            if ( filter.containsIdSuggestSubmit( ) )
            {
                daoUtil.setInt( nIndex, filter.getIdSuggestSubmit( ) );
                nIndex++;
            }

            if ( filter.containsIdCommentSubmitState( ) )
            {
                daoUtil.setBoolean( nIndex, filter.convertIdBoolean( filter.getIdCommentSubmitState( ) ) );
                nIndex++;
            }

            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                nIdCount = daoUtil.getInt( 1 );
            }

        }

        return nIdCount;
    }

    /**
     * build the order by clause
     * 
     * @param listSortBy
     *            list of sort by
     * @return the sql order by clause
     */
    private String getOrderBy( List<Integer> listSortBy )
    {
        StringBuilder strOrderBy = new StringBuilder( );
        int ncpt = 0;

        if ( ( listSortBy != null ) && ( !listSortBy.isEmpty( ) ) )
        {
            strOrderBy.append( SQL_ORDER_BY );

            for ( Integer sort : listSortBy )
            {
                ncpt++;

                switch( sort )
                {
                    case SubmitFilter.SORT_BY_DATE_RESPONSE_ASC:
                        strOrderBy.append( SQL_FILTER_SORT_BY_DATE_COMMENT_ASC );

                        break;

                    case SubmitFilter.SORT_BY_DATE_RESPONSE_DESC:
                        strOrderBy.append( SQL_FILTER_SORT_BY_DATE_COMMENT_DESC );

                        break;

                    case SubmitFilter.SORT_BY_DATE_MODIFY_ASC:
                        strOrderBy.append( SQL_FILTER_SORT_BY_DATE_MODIFY_COMMENT_ASC );

                        break;

                    case SubmitFilter.SORT_BY_DATE_MODIFY_DESC:
                        strOrderBy.append( SQL_FILTER_SORT_BY_DATE_MODIFY_COMMENT_DESC );

                        break;

                    default:
                        strOrderBy.append( SQL_FILTER_SORT_BY_DATE_MODIFY_COMMENT_DESC );

                        break;
                }

                if ( ncpt < listSortBy.size( ) )
                {
                    strOrderBy.append( "," );
                }
            }
        }

        return strOrderBy.toString( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByIdParent( int nIdParentCommentSubmit, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ID_PARENT, plugin ) )
        {
            daoUtil.setInt( 1, nIdParentCommentSubmit );
            daoUtil.executeUpdate( );
        }
    }
}
