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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.portal.business.style.Theme;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.portal.ThemesService;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * class SuggestDAO
 */
public final class SuggestDAO implements ISuggestDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_suggest ) FROM suggest_suggest";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_suggest,title,"
            + "unavailability_message,workgroup,"
            + "id_vote_type,number_vote_required,number_day_required,active_suggest_submit_authentification, "
            + "active_vote_authentification,active_comment_authentification,disable_new_suggest_submit, "
            + "authorized_comment, disable_new_comment ,id_mailing_list_suggest_submit, "
            + "active_captcha,active, date_creation, libelle_validate_button,active_suggest_proposition_state,libelle_contribution, "
            + "number_suggest_submit_in_top_score,number_suggest_submit_in_top_comment,limit_number_vote,number_suggest_submit_caracters_shown, "
            + "show_category_block,show_top_score_block,show_top_comment_block,active_suggest_submit_paginator,number_suggest_submit_per_page,role, "
            + "enable_new_suggest_submit_mail,header,sort_field,code_theme,confirmation_message,active_editor_bbcode, "
            + "default_suggest,id_default_sort,notification_new_comment_sender,notification_new_comment_title,notification_new_comment_body,notification_new_suggest_submit_sender,notification_new_suggest_submit_title,notification_new_suggest_submit_body "
            + "FROM suggest_suggest WHERE id_suggest = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO suggest_suggest ( id_suggest,title,"
            + "unavailability_message,workgroup,"
            + "id_vote_type,number_vote_required,number_day_required,active_suggest_submit_authentification, "
            + "active_vote_authentification,active_comment_authentification,disable_new_suggest_submit, "
            + "authorized_comment, disable_new_comment ,id_mailing_list_suggest_submit, "
            + "active_captcha,active, date_creation, libelle_validate_button,active_suggest_proposition_state, "
            + "libelle_contribution,number_suggest_submit_in_top_score,number_suggest_submit_in_top_comment,limit_number_vote, "
            + "number_suggest_submit_caracters_shown,show_category_block,show_top_score_block,show_top_comment_block ,active_suggest_submit_paginator,number_suggest_submit_per_page,role,"
            + "enable_new_suggest_submit_mail,header,sort_field,code_theme,confirmation_message,active_editor_bbcode,default_suggest,id_default_sort,notification_new_comment_sender,notification_new_comment_title,notification_new_comment_body,notification_new_suggest_submit_sender,notification_new_suggest_submit_title,notification_new_suggest_submit_body)"
            + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM suggest_suggest WHERE id_suggest = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE suggest_suggest SET  id_suggest=?,title=?," + "unavailability_message=?,workgroup=?,"
            + "id_vote_type=?,number_vote_required=?,number_day_required=?,active_suggest_submit_authentification=?, "
            + "active_vote_authentification=?,active_comment_authentification=?,disable_new_suggest_submit=?, "
            + "authorized_comment=?, disable_new_comment=? ,id_mailing_list_suggest_submit=?, "
            + "active_captcha=?,active=?, date_creation=?, libelle_validate_button=? ,active_suggest_proposition_state=?,"
            + "libelle_contribution=? ,number_suggest_submit_in_top_score=?,number_suggest_submit_in_top_comment=?,"
            + "limit_number_vote=?,number_suggest_submit_caracters_shown=?,  " + "show_category_block=?,show_top_score_block=?,show_top_comment_block=?  ,"
            + "active_suggest_submit_paginator=?,number_suggest_submit_per_page=? ,role=? ,"
            + "enable_new_suggest_submit_mail=?,header=? ,sort_field=? ,code_theme=?, confirmation_message=?,active_editor_bbcode=? ,"
            + "default_suggest=?,id_default_sort=?,notification_new_comment_sender=?,notification_new_comment_title=?,"
            + "notification_new_comment_body=?,notification_new_suggest_submit_sender=?,notification_new_suggest_submit_title=?"
            + ",notification_new_suggest_submit_body=? " + "WHERE id_suggest=?";
    private static final String SQL_QUERY_SELECT_SUGGEST_BY_FILTER = "SELECT id_suggest,title,"
            + "unavailability_message,workgroup,"
            + "id_vote_type,number_vote_required,number_day_required,active_suggest_submit_authentification, "
            + "active_vote_authentification,active_comment_authentification,disable_new_suggest_submit, "
            + "authorized_comment, disable_new_comment ,id_mailing_list_suggest_submit, "
            + "active_captcha,active, date_creation, libelle_validate_button,active_suggest_proposition_state,libelle_contribution, "
            + "number_suggest_submit_in_top_score,number_suggest_submit_in_top_comment,limit_number_vote,number_suggest_submit_caracters_shown, "
            + "show_category_block,show_top_score_block,show_top_comment_block, active_suggest_submit_paginator,number_suggest_submit_per_page,role,  "
            + "enable_new_suggest_submit_mail,header, sort_field, code_theme, confirmation_message,active_editor_bbcode, "
            + "default_suggest,id_default_sort,notification_new_comment_sender,notification_new_comment_title,notification_new_comment_body,notification_new_suggest_submit_sender,notification_new_suggest_submit_title,notification_new_suggest_submit_body "
            + " FROM suggest_suggest ";
    private static final String SQL_QUERY_SELECT_ALL_THEMES = "SELECT id_suggest, code_theme FROM suggest_suggest";
    private static final String SQL_FILTER_WORKGROUP = " workgroup = ? ";
    private static final String SQL_FILTER_ROLE = " role = ? ";
    private static final String SQL_FILTER_STATE = " active = ? ";
    private static final String SQL_FILTER_DEFAULT_SUGGEST = " default_suggest = ? ";
    private static final String SQL_ORDER_BY_DATE_CREATION = " ORDER BY date_creation  DESC ";
    private static final String SQL_QUERY_UPDATE_SUGGEST_ORDER = "UPDATE suggest_suggest SET sort_field = ? WHERE id_suggest = ?";

    /**
     * Generates a new primary key
     *
     * @param plugin
     *            the plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery( );

        int nKey;

        daoUtil.next( );
        nKey = daoUtil.getInt( 1 ) + 1;
        daoUtil.free( );

        return nKey;
    }

    /**
     * Insert a new record in the table.
     *
     * @param suggest
     *            instance of the Suggest to insert
     * @param plugin
     *            the plugin
     * @return the new suggest create
     */
    @Override
    public int insert( Suggest suggest, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        Timestamp timestamp = new java.sql.Timestamp( new java.util.Date( ).getTime( ) );

        suggest.setIdSuggest( newPrimaryKey( plugin ) );

        int ncpt = 1;
        daoUtil.setInt( ncpt++, suggest.getIdSuggest( ) );
        daoUtil.setString( ncpt++, suggest.getTitle( ) );
        daoUtil.setString( ncpt++, suggest.getUnavailabilityMessage( ) );
        daoUtil.setString( ncpt++, suggest.getWorkgroup( ) );
        daoUtil.setInt( ncpt++, suggest.getVoteType( ).getIdVoteType( ) );
        daoUtil.setInt( ncpt++, suggest.getNumberVoteRequired( ) );
        daoUtil.setInt( ncpt++, suggest.getNumberDayRequired( ) );
        daoUtil.setBoolean( ncpt++, suggest.isActiveSuggestSubmitAuthentification( ) );
        daoUtil.setBoolean( ncpt++, suggest.isActiveVoteAuthentification( ) );
        daoUtil.setBoolean( ncpt++, suggest.isActiveCommentAuthentification( ) );
        daoUtil.setBoolean( ncpt++, suggest.isDisableNewSuggestSubmit( ) );
        daoUtil.setBoolean( ncpt++, suggest.isAuthorizedComment( ) );
        daoUtil.setBoolean( ncpt++, suggest.isDisableNewComment( ) );
        daoUtil.setInt( ncpt++, suggest.getIdMailingListSuggestSubmit( ) );
        daoUtil.setBoolean( ncpt++, suggest.isActiveCaptcha( ) );
        daoUtil.setBoolean( ncpt++, suggest.isActive( ) );
        daoUtil.setTimestamp( ncpt++, timestamp );
        daoUtil.setString( ncpt++, suggest.getLibelleValidateButton( ) );
        daoUtil.setBoolean( ncpt++, suggest.isActiveSuggestPropositionState( ) );
        daoUtil.setString( ncpt++, suggest.getLibelleContribution( ) );
        daoUtil.setInt( ncpt++, suggest.getNumberSuggestSubmitInTopScore( ) );
        daoUtil.setInt( ncpt++, suggest.getNumberSuggestSubmitInTopComment( ) );
        daoUtil.setBoolean( ncpt++, suggest.isLimitNumberVote( ) );
        daoUtil.setInt( ncpt++, suggest.getNumberSuggestSubmitCaractersShown( ) );
        daoUtil.setBoolean( ncpt++, suggest.isShowCategoryBlock( ) );
        daoUtil.setBoolean( ncpt++, suggest.isShowTopScoreBlock( ) );
        daoUtil.setBoolean( ncpt++, suggest.isShowTopCommentBlock( ) );
        daoUtil.setBoolean( ncpt++, suggest.isActiveSuggestSubmitPaginator( ) );
        daoUtil.setInt( ncpt++, suggest.getNumberSuggestSubmitPerPage( ) );
        daoUtil.setString( ncpt++, suggest.getRole( ) );
        daoUtil.setBoolean( ncpt++, suggest.isEnableMailNewSuggestSubmit( ) );
        daoUtil.setString( ncpt++, suggest.getHeader( ) );
        daoUtil.setInt( ncpt++, suggest.getSortField( ) );
        daoUtil.setString( ncpt++, suggest.getCodeTheme( ) );
        daoUtil.setString( ncpt++, suggest.getConfirmationMessage( ) );
        daoUtil.setBoolean( ncpt++, suggest.isActiveEditorBbcode( ) );
        daoUtil.setBoolean( ncpt++, suggest.isDefaultSuggest( ) );
        daoUtil.setInt( ncpt++, suggest.getIdDefaultSort( ) );
        daoUtil.setString( ncpt++, suggest.getNotificationNewCommentSenderName( ) );
        daoUtil.setString( ncpt++, suggest.getNotificationNewCommentTitle( ) );
        daoUtil.setString( ncpt++, suggest.getNotificationNewCommentBody( ) );
        daoUtil.setString( ncpt++, suggest.getNotificationNewSuggestSubmitSenderName( ) );
        daoUtil.setString( ncpt++, suggest.getNotificationNewSuggestSubmitTitle( ) );
        daoUtil.setString( ncpt++, suggest.getNotificationNewSuggestSubmitBody( ) );
        daoUtil.executeUpdate( );
        daoUtil.free( );

        return suggest.getIdSuggest( );
    }

    /**
     * Load the data of the suggest from the table
     *
     * @param nId
     *            The identifier of the suggest
     * @param plugin
     *            the plugin
     * @return the instance of the Suggest
     */
    @Override
    public Suggest load( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery( );

        VoteType voteType;
        Suggest suggest = null;

        if ( daoUtil.next( ) )
        {
            int ncpt = 1;
            suggest = new Suggest( );
            suggest.setIdSuggest( daoUtil.getInt( ncpt++ ) );
            suggest.setTitle( daoUtil.getString( ncpt++ ) );
            suggest.setUnavailabilityMessage( daoUtil.getString( ncpt++ ) );
            suggest.setWorkgroup( daoUtil.getString( ncpt++ ) );

            voteType = new VoteType( );
            voteType.setIdVoteType( daoUtil.getInt( ncpt++ ) );
            suggest.setVoteType( voteType );

            suggest.setNumberVoteRequired( daoUtil.getInt( ncpt++ ) );
            suggest.setNumberDayRequired( daoUtil.getInt( ncpt++ ) );
            suggest.setActiveSuggestSubmitAuthentification( daoUtil.getBoolean( ncpt++ ) );
            suggest.setActiveVoteAuthentification( daoUtil.getBoolean( ncpt++ ) );
            suggest.setActiveCommentAuthentification( daoUtil.getBoolean( ncpt++ ) );
            suggest.setDisableNewSuggestSubmit( daoUtil.getBoolean( ncpt++ ) );
            suggest.setAuthorizedComment( daoUtil.getBoolean( ncpt++ ) );
            suggest.setDisableNewComment( daoUtil.getBoolean( ncpt++ ) );
            suggest.setIdMailingListSuggestSubmit( daoUtil.getInt( ncpt++ ) );
            suggest.setActiveCaptcha( daoUtil.getBoolean( ncpt++ ) );
            suggest.setActive( daoUtil.getBoolean( ncpt++ ) );
            suggest.setDateCreation( daoUtil.getTimestamp( ncpt++ ) );
            suggest.setLibelleValidateButton( daoUtil.getString( ncpt++ ) );
            suggest.setActiveSuggestPropositionState( daoUtil.getBoolean( ncpt++ ) );
            suggest.setLibelleContribution( daoUtil.getString( ncpt++ ) );
            suggest.setNumberSuggestSubmitInTopScore( daoUtil.getInt( ncpt++ ) );
            suggest.setNumberSuggestSubmitInTopComment( daoUtil.getInt( ncpt++ ) );
            suggest.setLimitNumberVote( daoUtil.getBoolean( ncpt++ ) );
            suggest.setNumberSuggestSubmitCaractersShown( daoUtil.getInt( ncpt++ ) );
            suggest.setShowCategoryBlock( daoUtil.getBoolean( ncpt++ ) );
            suggest.setShowTopScoreBlock( daoUtil.getBoolean( ncpt++ ) );
            suggest.setShowTopCommentBlock( daoUtil.getBoolean( ncpt++ ) );
            suggest.setActiveSuggestSubmitPaginator( daoUtil.getBoolean( ncpt++ ) );
            suggest.setNumberSuggestSubmitPerPage( daoUtil.getInt( ncpt++ ) );
            suggest.setRole( daoUtil.getString( ncpt++ ) );
            suggest.setEnableMailNewSuggestSubmit( daoUtil.getBoolean( ncpt++ ) );
            suggest.setHeader( daoUtil.getString( ncpt++ ) );
            suggest.setSortField( daoUtil.getInt( ncpt++ ) );
            suggest.setCodeTheme( daoUtil.getString( ncpt++ ) );
            suggest.setConfirmationMessage( daoUtil.getString( ncpt++ ) );
            suggest.setActiveEditorBbcode( daoUtil.getBoolean( ncpt++ ) );
            suggest.setDefaultSuggest( daoUtil.getBoolean( ncpt++ ) );
            suggest.setIdDefaultSort( daoUtil.getInt( ncpt++ ) );
            suggest.setNotificationNewCommentSenderName( daoUtil.getString( ncpt++ ) );
            suggest.setNotificationNewCommentTitle( daoUtil.getString( ncpt++ ) );
            suggest.setNotificationNewCommentBody( daoUtil.getString( ncpt++ ) );
            suggest.setNotificationNewSuggestSubmitSenderName( daoUtil.getString( ncpt++ ) );
            suggest.setNotificationNewSuggestSubmitTitle( daoUtil.getString( ncpt++ ) );
            suggest.setNotificationNewSuggestSubmitBody( daoUtil.getString( ncpt++ ) );
        }

        daoUtil.free( );

        return suggest;
    }

    /**
     * Delete a record from the table
     *
     * @param nIdSuggest
     *            The identifier of the suggest
     * @param plugin
     *            the plugin
     */
    @Override
    public void delete( int nIdSuggest, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdSuggest );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Update the suggest in the table
     *
     * @param suggest
     *            instance of the suggest object to update
     * @param plugin
     *            the plugin
     */
    @Override
    public void store( Suggest suggest, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        int ncpt = 1;
        suggest.setIdSuggest( suggest.getIdSuggest( ) );
        daoUtil.setInt( ncpt++, suggest.getIdSuggest( ) );
        daoUtil.setString( ncpt++, suggest.getTitle( ) );
        daoUtil.setString( ncpt++, suggest.getUnavailabilityMessage( ) );
        daoUtil.setString( ncpt++, suggest.getWorkgroup( ) );
        daoUtil.setInt( ncpt++, suggest.getVoteType( ).getIdVoteType( ) );
        daoUtil.setInt( ncpt++, suggest.getNumberVoteRequired( ) );
        daoUtil.setInt( ncpt++, suggest.getNumberDayRequired( ) );
        daoUtil.setBoolean( ncpt++, suggest.isActiveSuggestSubmitAuthentification( ) );
        daoUtil.setBoolean( ncpt++, suggest.isActiveVoteAuthentification( ) );
        daoUtil.setBoolean( ncpt++, suggest.isActiveCommentAuthentification( ) );
        daoUtil.setBoolean( ncpt++, suggest.isDisableNewSuggestSubmit( ) );
        daoUtil.setBoolean( ncpt++, suggest.isAuthorizedComment( ) );
        daoUtil.setBoolean( ncpt++, suggest.isDisableNewComment( ) );
        daoUtil.setInt( ncpt++, suggest.getIdMailingListSuggestSubmit( ) );
        daoUtil.setBoolean( ncpt++, suggest.isActiveCaptcha( ) );
        daoUtil.setBoolean( ncpt++, suggest.isActive( ) );
        daoUtil.setTimestamp( ncpt++, suggest.getDateCreation( ) );
        daoUtil.setString( ncpt++, suggest.getLibelleValidateButton( ) );
        daoUtil.setBoolean( ncpt++, suggest.isActiveSuggestPropositionState( ) );
        daoUtil.setString( ncpt++, suggest.getLibelleContribution( ) );
        daoUtil.setInt( ncpt++, suggest.getNumberSuggestSubmitInTopScore( ) );
        daoUtil.setInt( ncpt++, suggest.getNumberSuggestSubmitInTopComment( ) );
        daoUtil.setBoolean( ncpt++, suggest.isLimitNumberVote( ) );
        daoUtil.setInt( ncpt++, suggest.getNumberSuggestSubmitCaractersShown( ) );
        daoUtil.setBoolean( ncpt++, suggest.isShowCategoryBlock( ) );
        daoUtil.setBoolean( ncpt++, suggest.isShowTopScoreBlock( ) );
        daoUtil.setBoolean( ncpt++, suggest.isShowTopCommentBlock( ) );
        daoUtil.setBoolean( ncpt++, suggest.isActiveSuggestSubmitPaginator( ) );
        daoUtil.setInt( ncpt++, suggest.getNumberSuggestSubmitPerPage( ) );
        daoUtil.setString( ncpt++, suggest.getRole( ) );
        daoUtil.setBoolean( ncpt++, suggest.isEnableMailNewSuggestSubmit( ) );
        daoUtil.setString( ncpt++, suggest.getHeader( ) );
        daoUtil.setInt( ncpt++, suggest.getSortField( ) );
        daoUtil.setString( ncpt++, suggest.getCodeTheme( ) );
        daoUtil.setString( ncpt++, suggest.getConfirmationMessage( ) );
        daoUtil.setBoolean( ncpt++, suggest.isActiveEditorBbcode( ) );
        daoUtil.setBoolean( ncpt++, suggest.isDefaultSuggest( ) );
        daoUtil.setInt( ncpt++, suggest.getIdDefaultSort( ) );
        daoUtil.setString( ncpt++, suggest.getNotificationNewCommentSenderName( ) );
        daoUtil.setString( ncpt++, suggest.getNotificationNewCommentTitle( ) );
        daoUtil.setString( ncpt++, suggest.getNotificationNewCommentBody( ) );
        daoUtil.setString( ncpt++, suggest.getNotificationNewSuggestSubmitSenderName( ) );
        daoUtil.setString( ncpt++, suggest.getNotificationNewSuggestSubmitTitle( ) );
        daoUtil.setString( ncpt++, suggest.getNotificationNewSuggestSubmitBody( ) );

        daoUtil.setInt( ncpt++, suggest.getIdSuggest( ) );

        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Load the data of all the suggest who verify the filter and returns them in a list
     * 
     * @param filter
     *            the filter
     * @param plugin
     *            the plugin
     * @return the list of suggest
     */
    @Override
    public List<Suggest> selectSuggestList( SuggestFilter filter, Plugin plugin )
    {
        List<Suggest> suggestList = new ArrayList<Suggest>( );
        Suggest suggest;
        VoteType voteType;
        List<String> listStrFilter = new ArrayList<String>( );
        int ncpt;

        if ( filter.containsWorkgroupCriteria( ) )
        {
            listStrFilter.add( SQL_FILTER_WORKGROUP );
        }

        if ( filter.containsRoleCriteria( ) )
        {
            listStrFilter.add( SQL_FILTER_ROLE );
        }

        if ( filter.containsIdState( ) )
        {
            listStrFilter.add( SQL_FILTER_STATE );
        }

        if ( filter.containsIdDefaultSuggest( ) )
        {
            listStrFilter.add( SQL_FILTER_DEFAULT_SUGGEST );
        }

        String strSQL = SuggestUtils.buildRequestWithFilter( SQL_QUERY_SELECT_SUGGEST_BY_FILTER, listStrFilter, SQL_ORDER_BY_DATE_CREATION );
        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.containsWorkgroupCriteria( ) )
        {
            daoUtil.setString( nIndex, filter.getWorkgroup( ) );
            nIndex++;
        }

        if ( filter.containsRoleCriteria( ) )
        {
            daoUtil.setString( nIndex, filter.getRole( ) );
            nIndex++;
        }

        if ( filter.containsIdState( ) )
        {
            daoUtil.setInt( nIndex, filter.getIdState( ) );
            nIndex++;
        }

        if ( filter.containsIdDefaultSuggest( ) )
        {
            daoUtil.setInt( nIndex, filter.getIdDefaultSuggest( ) );
            nIndex++;
        }

        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            ncpt = 1;
            suggest = new Suggest( );
            suggest.setIdSuggest( daoUtil.getInt( ncpt++ ) );
            suggest.setTitle( daoUtil.getString( ncpt++ ) );
            suggest.setUnavailabilityMessage( daoUtil.getString( ncpt++ ) );
            suggest.setWorkgroup( daoUtil.getString( ncpt++ ) );

            voteType = new VoteType( );
            voteType.setIdVoteType( daoUtil.getInt( ncpt++ ) );
            suggest.setVoteType( voteType );

            suggest.setNumberVoteRequired( daoUtil.getInt( ncpt++ ) );
            suggest.setNumberDayRequired( daoUtil.getInt( ncpt++ ) );
            suggest.setActiveSuggestSubmitAuthentification( daoUtil.getBoolean( ncpt++ ) );
            suggest.setActiveVoteAuthentification( daoUtil.getBoolean( ncpt++ ) );
            suggest.setActiveCommentAuthentification( daoUtil.getBoolean( ncpt++ ) );
            suggest.setDisableNewSuggestSubmit( daoUtil.getBoolean( ncpt++ ) );
            suggest.setAuthorizedComment( daoUtil.getBoolean( ncpt++ ) );
            suggest.setDisableNewComment( daoUtil.getBoolean( ncpt++ ) );
            suggest.setIdMailingListSuggestSubmit( daoUtil.getInt( ncpt++ ) );
            suggest.setActiveCaptcha( daoUtil.getBoolean( ncpt++ ) );
            suggest.setActive( daoUtil.getBoolean( ncpt++ ) );
            suggest.setDateCreation( daoUtil.getTimestamp( ncpt++ ) );
            suggest.setLibelleValidateButton( daoUtil.getString( ncpt++ ) );
            suggest.setActiveSuggestPropositionState( daoUtil.getBoolean( ncpt++ ) );
            suggest.setLibelleContribution( daoUtil.getString( ncpt++ ) );
            suggest.setNumberSuggestSubmitInTopScore( daoUtil.getInt( ncpt++ ) );
            suggest.setNumberSuggestSubmitInTopComment( daoUtil.getInt( ncpt++ ) );
            suggest.setLimitNumberVote( daoUtil.getBoolean( ncpt++ ) );
            suggest.setNumberSuggestSubmitCaractersShown( daoUtil.getInt( ncpt++ ) );
            suggest.setShowCategoryBlock( daoUtil.getBoolean( ncpt++ ) );
            suggest.setShowTopScoreBlock( daoUtil.getBoolean( ncpt++ ) );
            suggest.setShowTopCommentBlock( daoUtil.getBoolean( ncpt++ ) );
            suggest.setActiveSuggestSubmitPaginator( daoUtil.getBoolean( ncpt++ ) );
            suggest.setNumberSuggestSubmitPerPage( daoUtil.getInt( ncpt++ ) );
            suggest.setRole( daoUtil.getString( ncpt++ ) );
            suggest.setEnableMailNewSuggestSubmit( daoUtil.getBoolean( ncpt++ ) );
            suggest.setHeader( daoUtil.getString( ncpt++ ) );
            suggest.setSortField( daoUtil.getInt( ncpt++ ) );
            suggest.setCodeTheme( daoUtil.getString( ncpt++ ) );
            suggest.setConfirmationMessage( daoUtil.getString( ncpt++ ) );
            suggest.setActiveEditorBbcode( daoUtil.getBoolean( ncpt++ ) );
            suggest.setDefaultSuggest( daoUtil.getBoolean( ncpt++ ) );
            suggest.setIdDefaultSort( daoUtil.getInt( ncpt++ ) );
            suggest.setNotificationNewCommentSenderName( daoUtil.getString( ncpt++ ) );
            suggest.setNotificationNewCommentTitle( daoUtil.getString( ncpt++ ) );
            suggest.setNotificationNewCommentBody( daoUtil.getString( ncpt++ ) );
            suggest.setNotificationNewSuggestSubmitSenderName( daoUtil.getString( ncpt++ ) );
            suggest.setNotificationNewSuggestSubmitTitle( daoUtil.getString( ncpt++ ) );
            suggest.setNotificationNewSuggestSubmitBody( daoUtil.getString( ncpt++ ) );

            suggestList.add( suggest );
        }

        daoUtil.free( );

        return suggestList;
    }

    /**
     * Modify the order of a suggestsubmit
     * 
     * @param nSortField
     *            The reference field to sort
     * @param nId
     *            The suggest identifier
     * @param plugin
     *            The plugin
     */
    @Override
    public void storeSuggestOrderField( int nId, int nSortField, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_SUGGEST_ORDER, plugin );
        daoUtil.setInt( 1, nSortField );
        daoUtil.setInt( 2, nId );
        daoUtil.executeUpdate( );
        daoUtil.free( );
    }

    /**
     * Load all the themes for form xpages
     * 
     * @param plugin
     *            the plugin
     * @return a map containing the themes by form id
     */
    @Override
    public Map<Integer, Theme> getXPageThemesMap( Plugin plugin )
    {
        Map<Integer, Theme> xPageThemesMap = new HashMap<Integer, Theme>( );

        String strSQL = SQL_QUERY_SELECT_ALL_THEMES;
        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            int nIndex = 1;
            int nIdForm = daoUtil.getInt( nIndex++ );
            String strCodeTheme = daoUtil.getString( nIndex++ );
            Theme theme = ThemesService.getGlobalTheme( strCodeTheme );
            xPageThemesMap.put( nIdForm, theme );
        }

        daoUtil.free( );

        return xPageThemesMap;
    }
}
