/*
 * Copyright (c) 2002-2012, Mairie de Paris
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

import fr.paris.lutece.plugins.digglike.utils.DiggUtils;
import fr.paris.lutece.portal.business.style.Theme;
import fr.paris.lutece.portal.business.style.ThemeHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * class DiggDAO
 */
public final class DiggDAO implements IDiggDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_digg ) FROM digglike_digg";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_digg,title," +
        "unavailability_message,workgroup," +
        "id_vote_type,number_vote_required,number_day_required,active_digg_submit_authentification, " +
        "active_vote_authentification,active_comment_authentification,disable_new_digg_submit, " +
        "authorized_comment, disable_new_comment ,id_mailing_list_digg_submit,id_mailing_list_comment, " +
        "active_captcha,active, date_creation, libelle_validate_button,active_digg_proposition_state,libelle_contribution, " +
        "number_digg_submit_in_top_score,number_digg_submit_in_top_comment,limit_number_vote,number_digg_submit_caracters_shown, " +
        "show_category_block,show_top_score_block,show_top_comment_block,active_digg_submit_paginator,number_digg_submit_per_page,role, " +
        "enable_new_digg_submit_mail,id_mailing_list_new_digg_submit,header,sort_field,code_theme,confirmation_message,active_editor_bbcode, " +
        "default_digg,id_default_sort,active_digg_submit_type " + "FROM digglike_digg WHERE id_digg = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO digglike_digg ( id_digg,title," +
        "unavailability_message,workgroup," +
        "id_vote_type,number_vote_required,number_day_required,active_digg_submit_authentification, " +
        "active_vote_authentification,active_comment_authentification,disable_new_digg_submit, " +
        "authorized_comment, disable_new_comment ,id_mailing_list_digg_submit,id_mailing_list_comment, " +
        "active_captcha,active, date_creation, libelle_validate_button,active_digg_proposition_state, " +
        "libelle_contribution,number_digg_submit_in_top_score,number_digg_submit_in_top_comment,limit_number_vote, " +
        "number_digg_submit_caracters_shown,show_category_block,show_top_score_block,show_top_comment_block ,active_digg_submit_paginator,number_digg_submit_per_page,role," +
        "enable_new_digg_submit_mail,id_mailing_list_new_digg_submit,header,sort_field,code_theme,confirmation_message,active_editor_bbcode,default_digg,id_default_sort,active_digg_submit_type)" +
        "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM digglike_digg WHERE id_digg = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE digglike_digg SET  id_digg=?,title=?," +
        "unavailability_message=?,workgroup=?," +
        "id_vote_type=?,number_vote_required=?,number_day_required=?,active_digg_submit_authentification=?, " +
        "active_vote_authentification=?,active_comment_authentification=?,disable_new_digg_submit=?, " +
        "authorized_comment=?, disable_new_comment=? ,id_mailing_list_digg_submit=?,id_mailing_list_comment=?, " +
        "active_captcha=?,active=?, date_creation=?, libelle_validate_button=? ,active_digg_proposition_state=?," +
        "libelle_contribution=? ,number_digg_submit_in_top_score=?,number_digg_submit_in_top_comment=?," +
        "limit_number_vote=?,number_digg_submit_caracters_shown=?,  " +
        "show_category_block=?,show_top_score_block=?,show_top_comment_block=?  ," +
        "active_digg_submit_paginator=?,number_digg_submit_per_page=? ,role=? ," +
        "enable_new_digg_submit_mail=?,id_mailing_list_new_digg_submit=? ,header=? ,sort_field=? ,code_theme=?, confirmation_message=?,active_editor_bbcode=? ," +
        "default_digg=?,id_default_sort=?,active_digg_submit_type=? " + "WHERE id_digg=?";
    private static final String SQL_QUERY_SELECT_DIGG_BY_FILTER = "SELECT id_digg,title," +
        "unavailability_message,workgroup," +
        "id_vote_type,number_vote_required,number_day_required,active_digg_submit_authentification, " +
        "active_vote_authentification,active_comment_authentification,disable_new_digg_submit, " +
        "authorized_comment, disable_new_comment ,id_mailing_list_digg_submit,id_mailing_list_comment, " +
        "active_captcha,active, date_creation, libelle_validate_button,active_digg_proposition_state,libelle_contribution, " +
        "number_digg_submit_in_top_score,number_digg_submit_in_top_comment,limit_number_vote,number_digg_submit_caracters_shown, " +
        "show_category_block,show_top_score_block,show_top_comment_block, active_digg_submit_paginator,number_digg_submit_per_page,role,  " +
        "enable_new_digg_submit_mail,id_mailing_list_new_digg_submit,header, sort_field, code_theme, confirmation_message,active_editor_bbcode, " +
        "default_digg,id_default_sort,active_digg_submit_type " + " FROM digglike_digg ";
    private static final String SQL_QUERY_SELECT_ALL_THEMES = "SELECT id_digg, code_theme FROM digglike_digg";
    private static final String SQL_QUERY_DELETE_ASSOCIATED_CATEGORIE = "DELETE FROM digglike_digg_category WHERE id_digg = ? and id_category= ? ";
    private static final String SQL_QUERY_INSERT_ASSOCIATED_CATEGORY = "INSERT INTO digglike_digg_category(id_digg,id_category) VALUES(?,?) ";
    private static final String SQL_FILTER_WORKGROUP = " workgroup = ? ";
    private static final String SQL_FILTER_ROLE = " role = ? ";
    private static final String SQL_FILTER_STATE = " active = ? ";
    private static final String SQL_FILTER_DEFAULT_DIGG = " default_digg = ? ";
    private static final String SQL_ORDER_BY_DATE_CREATION = " ORDER BY date_creation  DESC ";
    private static final String SQL_QUERY_UPDATE_DIGG_ORDER = "UPDATE digglike_digg SET sort_field = ? WHERE id_digg = ?";

    /**
     * Generates a new primary key
     *
     * @param plugin the plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
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
     * @param digg instance of the Digg to insert
     * @param plugin the plugin
     * @return the new digg create
     */
    public int insert( Digg digg, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        Timestamp timestamp = new java.sql.Timestamp( new java.util.Date(  ).getTime(  ) );

        digg.setIdDigg( newPrimaryKey( plugin ) );
        daoUtil.setInt( 1, digg.getIdDigg(  ) );
        daoUtil.setString( 2, digg.getTitle(  ) );
        daoUtil.setString( 3, digg.getUnavailabilityMessage(  ) );
        daoUtil.setString( 4, digg.getWorkgroup(  ) );
        daoUtil.setInt( 5, digg.getVoteType(  ).getIdVoteType(  ) );
        daoUtil.setInt( 6, digg.getNumberVoteRequired(  ) );
        daoUtil.setInt( 7, digg.getNumberDayRequired(  ) );
        daoUtil.setBoolean( 8, digg.isActiveDiggSubmitAuthentification(  ) );
        daoUtil.setBoolean( 9, digg.isActiveVoteAuthentification(  ) );
        daoUtil.setBoolean( 10, digg.isActiveCommentAuthentification(  ) );
        daoUtil.setBoolean( 11, digg.isDisableNewDiggSubmit(  ) );
        daoUtil.setBoolean( 12, digg.isAuthorizedComment(  ) );
        daoUtil.setBoolean( 13, digg.isDisableNewComment(  ) );
        daoUtil.setInt( 14, digg.getIdMailingListDiggSubmit(  ) );
        daoUtil.setInt( 15, digg.getIdMailingListComment(  ) );
        daoUtil.setBoolean( 16, digg.isActiveCaptcha(  ) );
        daoUtil.setBoolean( 17, digg.isActive(  ) );
        daoUtil.setTimestamp( 18, timestamp );
        daoUtil.setString( 19, digg.getLibelleValidateButton(  ) );
        daoUtil.setBoolean( 20, digg.isActiveDiggPropositionState(  ) );
        daoUtil.setString( 21, digg.getLibelleContribution(  ) );
        daoUtil.setInt( 22, digg.getNumberDiggSubmitInTopScore(  ) );
        daoUtil.setInt( 23, digg.getNumberDiggSubmitInTopComment(  ) );
        daoUtil.setBoolean( 24, digg.isLimitNumberVote(  ) );
        daoUtil.setInt( 25, digg.getNumberDiggSubmitCaractersShown(  ) );
        daoUtil.setBoolean( 26, digg.isShowCategoryBlock(  ) );
        daoUtil.setBoolean( 27, digg.isShowTopScoreBlock(  ) );
        daoUtil.setBoolean( 28, digg.isShowTopCommentBlock(  ) );
        daoUtil.setBoolean( 29, digg.isActiveDiggSubmitPaginator(  ) );
        daoUtil.setInt( 30, digg.getNumberDiggSubmitPerPage(  ) );
        daoUtil.setString( 31, digg.getRole(  ) );
        daoUtil.setBoolean( 32, digg.isEnableMailNewDiggSubmit(  ) );
        daoUtil.setInt( 33, digg.getIdMailingListNewDiggSubmit(  ) );
        daoUtil.setString( 34, digg.getHeader(  ) );
        daoUtil.setInt( 35, digg.getSortField(  ) );
        daoUtil.setString( 36, digg.getCodeTheme(  ) );
        daoUtil.setString( 37, digg.getConfirmationMessage(  ) );
        daoUtil.setBoolean( 38, digg.isActiveEditorBbcode(  ) );
        daoUtil.setBoolean( 39, digg.isDefaultDigg(  ) );
        daoUtil.setInt( 40, digg.getIdDefaultSort(  ) );
        daoUtil.setBoolean( 41, digg.isActiveDiggSubmitType(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );

        return digg.getIdDigg(  );
    }

    /**
     * Load the data of the digg from the table
     *
     * @param nId The identifier of the digg
     * @param plugin the plugin
     * @return the instance of the Digg
     */
    public Digg load( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery(  );

        VoteType voteType = null;
        Digg digg = null;

        if ( daoUtil.next(  ) )
        {
            digg = new Digg(  );
            digg.setIdDigg( daoUtil.getInt( 1 ) );
            digg.setTitle( daoUtil.getString( 2 ) );
            digg.setUnavailabilityMessage( daoUtil.getString( 3 ) );
            digg.setWorkgroup( daoUtil.getString( 4 ) );

            voteType = new VoteType(  );
            voteType.setIdVoteType( daoUtil.getInt( 5 ) );
            digg.setVoteType( voteType );

            digg.setNumberVoteRequired( daoUtil.getInt( 6 ) );
            digg.setNumberDayRequired( daoUtil.getInt( 7 ) );
            digg.setActiveDiggSubmitAuthentification( daoUtil.getBoolean( 8 ) );
            digg.setActiveVoteAuthentification( daoUtil.getBoolean( 9 ) );
            digg.setActiveCommentAuthentification( daoUtil.getBoolean( 10 ) );
            digg.setDisableNewDiggSubmit( daoUtil.getBoolean( 11 ) );
            digg.setAuthorizedComment( daoUtil.getBoolean( 12 ) );
            digg.setDisableNewComment( daoUtil.getBoolean( 13 ) );
            digg.setIdMailingListDiggSubmit( daoUtil.getInt( 14 ) );
            digg.setIdMailingListComment( daoUtil.getInt( 15 ) );
            digg.setActiveCaptcha( daoUtil.getBoolean( 16 ) );
            digg.setActive( daoUtil.getBoolean( 17 ) );
            digg.setDateCreation( daoUtil.getTimestamp( 18 ) );
            digg.setLibelleValidateButton( daoUtil.getString( 19 ) );
            digg.setActiveDiggPropositionState( daoUtil.getBoolean( 20 ) );
            digg.setLibelleContribution( daoUtil.getString( 21 ) );
            digg.setNumberDiggSubmitInTopScore( daoUtil.getInt( 22 ) );
            digg.setNumberDiggSubmitInTopComment( daoUtil.getInt( 23 ) );
            digg.setLimitNumberVote( daoUtil.getBoolean( 24 ) );
            digg.setNumberDiggSubmitCaractersShown( daoUtil.getInt( 25 ) );
            digg.setShowCategoryBlock( daoUtil.getBoolean( 26 ) );
            digg.setShowTopScoreBlock( daoUtil.getBoolean( 27 ) );
            digg.setShowTopCommentBlock( daoUtil.getBoolean( 28 ) );
            digg.setActiveDiggSubmitPaginator( daoUtil.getBoolean( 29 ) );
            digg.setNumberDiggSubmitPerPage( daoUtil.getInt( 30 ) );
            digg.setRole( daoUtil.getString( 31 ) );
            digg.setEnableMailNewDiggSubmit( daoUtil.getBoolean( 32 ) );
            digg.setIdMailingListNewDiggSubmit( daoUtil.getInt( 33 ) );
            digg.setHeader( daoUtil.getString( 34 ) );
            digg.setSortField( daoUtil.getInt( 35 ) );
            digg.setCodeTheme( daoUtil.getString( 36 ) );
            digg.setConfirmationMessage( daoUtil.getString( 37 ) );
            digg.setActiveEditorBbcode( daoUtil.getBoolean( 38 ) );
            digg.setDefaultDigg( daoUtil.getBoolean( 39 ) );
            digg.setIdDefaultSort( daoUtil.getInt( 40 ) );
            digg.setActiveDiggSubmitType( daoUtil.getBoolean( 41 ) );
        }

        daoUtil.free(  );

        return digg;
    }

    /**
     * Delete a record from the table
     *
     * @param nIdDigg The identifier of the digg
     * @param plugin the plugin
     */
    public void delete( int nIdDigg, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdDigg );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Update the digg in the table
     *
     * @param digg instance of the digg object to update
     * @param plugin the plugin
     */
    public void store( Digg digg, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        digg.setIdDigg( digg.getIdDigg(  ) );
        daoUtil.setInt( 1, digg.getIdDigg(  ) );
        daoUtil.setString( 2, digg.getTitle(  ) );
        daoUtil.setString( 3, digg.getUnavailabilityMessage(  ) );
        daoUtil.setString( 4, digg.getWorkgroup(  ) );
        daoUtil.setInt( 5, digg.getVoteType(  ).getIdVoteType(  ) );
        daoUtil.setInt( 6, digg.getNumberVoteRequired(  ) );
        daoUtil.setInt( 7, digg.getNumberDayRequired(  ) );
        daoUtil.setBoolean( 8, digg.isActiveDiggSubmitAuthentification(  ) );
        daoUtil.setBoolean( 9, digg.isActiveVoteAuthentification(  ) );
        daoUtil.setBoolean( 10, digg.isActiveCommentAuthentification(  ) );
        daoUtil.setBoolean( 11, digg.isDisableNewDiggSubmit(  ) );
        daoUtil.setBoolean( 12, digg.isAuthorizedComment(  ) );
        daoUtil.setBoolean( 13, digg.isDisableNewComment(  ) );
        daoUtil.setInt( 14, digg.getIdMailingListDiggSubmit(  ) );
        daoUtil.setInt( 15, digg.getIdMailingListComment(  ) );
        daoUtil.setBoolean( 16, digg.isActiveCaptcha(  ) );
        daoUtil.setBoolean( 17, digg.isActive(  ) );
        daoUtil.setTimestamp( 18, digg.getDateCreation(  ) );
        daoUtil.setString( 19, digg.getLibelleValidateButton(  ) );
        daoUtil.setBoolean( 20, digg.isActiveDiggPropositionState(  ) );
        daoUtil.setString( 21, digg.getLibelleContribution(  ) );
        daoUtil.setInt( 22, digg.getNumberDiggSubmitInTopScore(  ) );
        daoUtil.setInt( 23, digg.getNumberDiggSubmitInTopComment(  ) );
        daoUtil.setBoolean( 24, digg.isLimitNumberVote(  ) );
        daoUtil.setInt( 25, digg.getNumberDiggSubmitCaractersShown(  ) );
        daoUtil.setBoolean( 26, digg.isShowCategoryBlock(  ) );
        daoUtil.setBoolean( 27, digg.isShowTopScoreBlock(  ) );
        daoUtil.setBoolean( 28, digg.isShowTopCommentBlock(  ) );
        daoUtil.setBoolean( 29, digg.isActiveDiggSubmitPaginator(  ) );
        daoUtil.setInt( 30, digg.getNumberDiggSubmitPerPage(  ) );
        daoUtil.setString( 31, digg.getRole(  ) );
        daoUtil.setBoolean( 32, digg.isEnableMailNewDiggSubmit(  ) );
        daoUtil.setInt( 33, digg.getIdMailingListNewDiggSubmit(  ) );
        daoUtil.setString( 34, digg.getHeader(  ) );
        daoUtil.setInt( 35, digg.getSortField(  ) );
        daoUtil.setString( 36, digg.getCodeTheme(  ) );
        daoUtil.setString( 37, digg.getConfirmationMessage(  ) );
        daoUtil.setBoolean( 38, digg.isActiveEditorBbcode(  ) );
        daoUtil.setBoolean( 39, digg.isDefaultDigg(  ) );
        daoUtil.setInt( 40, digg.getIdDefaultSort(  ) );
        daoUtil.setBoolean( 41, digg.isActiveDiggSubmitType(  ) );
        daoUtil.setInt( 42, digg.getIdDigg(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load the data of all the digg who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of digg
     */
    public List<Digg> selectDiggList( DiggFilter filter, Plugin plugin )
    {
        List<Digg> diggList = new ArrayList<Digg>(  );
        Digg digg = null;
        VoteType voteType = null;
        List<String> listStrFilter = new ArrayList<String>(  );

        if ( filter.containsWorkgroupCriteria(  ) )
        {
            listStrFilter.add( SQL_FILTER_WORKGROUP );
        }

        if ( filter.containsRoleCriteria(  ) )
        {
            listStrFilter.add( SQL_FILTER_ROLE );
        }

        if ( filter.containsIdState(  ) )
        {
            listStrFilter.add( SQL_FILTER_STATE );
        }

        if ( filter.containsIdDefaultDigg(  ) )
        {
            listStrFilter.add( SQL_FILTER_DEFAULT_DIGG );
        }

        String strSQL = DiggUtils.buildRequestWithFilter( SQL_QUERY_SELECT_DIGG_BY_FILTER, listStrFilter,
                SQL_ORDER_BY_DATE_CREATION );
        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.containsWorkgroupCriteria(  ) )
        {
            daoUtil.setString( nIndex, filter.getWorkgroup(  ) );
            nIndex++;
        }

        if ( filter.containsRoleCriteria(  ) )
        {
            daoUtil.setString( nIndex, filter.getRole(  ) );
            nIndex++;
        }

        if ( filter.containsIdState(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdState(  ) );
            nIndex++;
        }

        if ( filter.containsIdDefaultDigg(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdDefaultDigg(  ) );
            nIndex++;
        }

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            digg = new Digg(  );
            digg.setIdDigg( daoUtil.getInt( 1 ) );
            digg.setTitle( daoUtil.getString( 2 ) );
            digg.setUnavailabilityMessage( daoUtil.getString( 3 ) );
            digg.setWorkgroup( daoUtil.getString( 4 ) );

            voteType = new VoteType(  );
            voteType.setIdVoteType( daoUtil.getInt( 5 ) );
            digg.setVoteType( voteType );

            digg.setNumberVoteRequired( daoUtil.getInt( 6 ) );
            digg.setNumberDayRequired( daoUtil.getInt( 7 ) );
            digg.setActiveDiggSubmitAuthentification( daoUtil.getBoolean( 8 ) );
            digg.setActiveVoteAuthentification( daoUtil.getBoolean( 9 ) );
            digg.setActiveCommentAuthentification( daoUtil.getBoolean( 10 ) );
            digg.setDisableNewDiggSubmit( daoUtil.getBoolean( 11 ) );
            digg.setAuthorizedComment( daoUtil.getBoolean( 12 ) );
            digg.setDisableNewComment( daoUtil.getBoolean( 13 ) );
            digg.setIdMailingListDiggSubmit( daoUtil.getInt( 14 ) );
            digg.setIdMailingListComment( daoUtil.getInt( 15 ) );
            digg.setActiveCaptcha( daoUtil.getBoolean( 16 ) );
            digg.setActive( daoUtil.getBoolean( 17 ) );
            digg.setDateCreation( daoUtil.getTimestamp( 18 ) );
            digg.setLibelleValidateButton( daoUtil.getString( 19 ) );
            digg.setActiveDiggPropositionState( daoUtil.getBoolean( 20 ) );
            digg.setLibelleContribution( daoUtil.getString( 21 ) );
            digg.setNumberDiggSubmitInTopScore( daoUtil.getInt( 22 ) );
            digg.setNumberDiggSubmitInTopComment( daoUtil.getInt( 23 ) );
            digg.setLimitNumberVote( daoUtil.getBoolean( 24 ) );
            digg.setNumberDiggSubmitCaractersShown( daoUtil.getInt( 25 ) );
            digg.setShowCategoryBlock( daoUtil.getBoolean( 26 ) );
            digg.setShowTopScoreBlock( daoUtil.getBoolean( 27 ) );
            digg.setShowTopCommentBlock( daoUtil.getBoolean( 28 ) );
            digg.setActiveDiggSubmitPaginator( daoUtil.getBoolean( 29 ) );
            digg.setNumberDiggSubmitPerPage( daoUtil.getInt( 30 ) );
            digg.setRole( daoUtil.getString( 31 ) );
            digg.setEnableMailNewDiggSubmit( daoUtil.getBoolean( 32 ) );
            digg.setIdMailingListNewDiggSubmit( daoUtil.getInt( 33 ) );
            digg.setHeader( daoUtil.getString( 34 ) );
            digg.setSortField( daoUtil.getInt( 35 ) );
            digg.setCodeTheme( daoUtil.getString( 36 ) );
            digg.setConfirmationMessage( daoUtil.getString( 37 ) );
            digg.setActiveEditorBbcode( daoUtil.getBoolean( 38 ) );
            digg.setDefaultDigg( daoUtil.getBoolean( 39 ) );
            digg.setIdDefaultSort( daoUtil.getInt( 40 ) );
            digg.setActiveDiggSubmitType( daoUtil.getBoolean( 41 ) );

            diggList.add( digg );
        }

        daoUtil.free(  );

        return diggList;
    }

    /**
     * Delete an association between digg and categories
     *
     * @param nIdDigg The identifier of the digg
     * @param nIdCategory The identifier of the category
     * @param plugin the plugin
     */
    public void deleteCategoryAssociated( int nIdDigg, int nIdCategory, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ASSOCIATED_CATEGORIE, plugin );
        daoUtil.setInt( 1, nIdDigg );
        daoUtil.setInt( 2, nIdCategory );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * insert an association between  digg and a  category
     *
     * @param nIdDigg The identifier of the category
     * @param nIdCategory The identifier of the category
     * @param plugin the plugin
     */
    public void insertCategoryAssociated( int nIdDigg, int nIdCategory, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_ASSOCIATED_CATEGORY, plugin );
        daoUtil.setInt( 1, nIdDigg );
        daoUtil.setInt( 2, nIdCategory );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Modify the order of a diggsubmit
     * @param nSortField The reference field to sort
     * @param nId The digg identifier
     * @param plugin The plugin
     */
    public void storeDiggOrderField( int nId, int nSortField, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_DIGG_ORDER, plugin );
        daoUtil.setInt( 1, nSortField );
        daoUtil.setInt( 2, nId );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load all the themes for form xpages
     * @param plugin the plugin
     * @return a map containing the themes by form id
     */
    public Map<Integer, Theme> getXPageThemesMap( Plugin plugin )
    {
        Map<Integer, Theme> xPageThemesMap = new HashMap<Integer, Theme>(  );

        String strSQL = SQL_QUERY_SELECT_ALL_THEMES;
        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            int nIndex = 1;
            int nIdForm = daoUtil.getInt( nIndex++ );
            String strCodeTheme = daoUtil.getString( nIndex++ );
            Theme theme = ThemeHome.findByPrimaryKey( strCodeTheme );
            xPageThemesMap.put( nIdForm, theme );
        }

        daoUtil.free(  );

        return xPageThemesMap;
    }
}
