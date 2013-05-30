/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
        "authorized_comment, disable_new_comment ,id_mailing_list_digg_submit, " +
        "active_captcha,active, date_creation, libelle_validate_button,active_digg_proposition_state,libelle_contribution, " +
        "number_digg_submit_in_top_score,number_digg_submit_in_top_comment,limit_number_vote,number_digg_submit_caracters_shown, " +
        "show_category_block,show_top_score_block,show_top_comment_block,active_digg_submit_paginator,number_digg_submit_per_page,role, " +
        "enable_new_digg_submit_mail,header,sort_field,code_theme,confirmation_message,active_editor_bbcode, " +
        "default_digg,id_default_sort,active_digg_submit_type " + "FROM digglike_digg WHERE id_digg = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO digglike_digg ( id_digg,title," +
        "unavailability_message,workgroup," +
        "id_vote_type,number_vote_required,number_day_required,active_digg_submit_authentification, " +
        "active_vote_authentification,active_comment_authentification,disable_new_digg_submit, " +
        "authorized_comment, disable_new_comment ,id_mailing_list_digg_submit, " +
        "active_captcha,active, date_creation, libelle_validate_button,active_digg_proposition_state, " +
        "libelle_contribution,number_digg_submit_in_top_score,number_digg_submit_in_top_comment,limit_number_vote, " +
        "number_digg_submit_caracters_shown,show_category_block,show_top_score_block,show_top_comment_block ,active_digg_submit_paginator,number_digg_submit_per_page,role," +
        "enable_new_digg_submit_mail,header,sort_field,code_theme,confirmation_message,active_editor_bbcode,default_digg,id_default_sort,active_digg_submit_type)" +
        "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM digglike_digg WHERE id_digg = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE digglike_digg SET  id_digg=?,title=?," +
        "unavailability_message=?,workgroup=?," +
        "id_vote_type=?,number_vote_required=?,number_day_required=?,active_digg_submit_authentification=?, " +
        "active_vote_authentification=?,active_comment_authentification=?,disable_new_digg_submit=?, " +
        "authorized_comment=?, disable_new_comment=? ,id_mailing_list_digg_submit=?, " +
        "active_captcha=?,active=?, date_creation=?, libelle_validate_button=? ,active_digg_proposition_state=?," +
        "libelle_contribution=? ,number_digg_submit_in_top_score=?,number_digg_submit_in_top_comment=?," +
        "limit_number_vote=?,number_digg_submit_caracters_shown=?,  " +
        "show_category_block=?,show_top_score_block=?,show_top_comment_block=?  ," +
        "active_digg_submit_paginator=?,number_digg_submit_per_page=? ,role=? ," +
        "enable_new_digg_submit_mail=?,header=? ,sort_field=? ,code_theme=?, confirmation_message=?,active_editor_bbcode=? ," +
        "default_digg=?,id_default_sort=?,active_digg_submit_type=? " + "WHERE id_digg=?";
    private static final String SQL_QUERY_SELECT_DIGG_BY_FILTER = "SELECT id_digg,title," +
        "unavailability_message,workgroup," +
        "id_vote_type,number_vote_required,number_day_required,active_digg_submit_authentification, " +
        "active_vote_authentification,active_comment_authentification,disable_new_digg_submit, " +
        "authorized_comment, disable_new_comment ,id_mailing_list_digg_submit, " +
        "active_captcha,active, date_creation, libelle_validate_button,active_digg_proposition_state,libelle_contribution, " +
        "number_digg_submit_in_top_score,number_digg_submit_in_top_comment,limit_number_vote,number_digg_submit_caracters_shown, " +
        "show_category_block,show_top_score_block,show_top_comment_block, active_digg_submit_paginator,number_digg_submit_per_page,role,  " +
        "enable_new_digg_submit_mail,header, sort_field, code_theme, confirmation_message,active_editor_bbcode, " +
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
        int ncpt=1;
        daoUtil.setInt( ncpt++, digg.getIdDigg(  ) );
        daoUtil.setString( ncpt++, digg.getTitle(  ) );
        daoUtil.setString( ncpt++, digg.getUnavailabilityMessage(  ) );
        daoUtil.setString( ncpt++, digg.getWorkgroup(  ) );
        daoUtil.setInt( ncpt++, digg.getVoteType(  ).getIdVoteType(  ) );
        daoUtil.setInt( ncpt++, digg.getNumberVoteRequired(  ) );
        daoUtil.setInt( ncpt++, digg.getNumberDayRequired(  ) );
        daoUtil.setBoolean( ncpt++, digg.isActiveDiggSubmitAuthentification(  ) );
        daoUtil.setBoolean(ncpt++, digg.isActiveVoteAuthentification(  ) );
        daoUtil.setBoolean( ncpt++, digg.isActiveCommentAuthentification(  ) );
        daoUtil.setBoolean( ncpt++, digg.isDisableNewDiggSubmit(  ) );
        daoUtil.setBoolean( ncpt++, digg.isAuthorizedComment(  ) );
        daoUtil.setBoolean( ncpt++, digg.isDisableNewComment(  ) );
        daoUtil.setInt( ncpt++, digg.getIdMailingListDiggSubmit(  ) );
        daoUtil.setBoolean( ncpt++, digg.isActiveCaptcha(  ) );
        daoUtil.setBoolean( ncpt++, digg.isActive(  ) );
        daoUtil.setTimestamp( ncpt++, timestamp );
        daoUtil.setString( ncpt++, digg.getLibelleValidateButton(  ) );
        daoUtil.setBoolean( ncpt++, digg.isActiveDiggPropositionState(  ) );
        daoUtil.setString( ncpt++, digg.getLibelleContribution(  ) );
        daoUtil.setInt( ncpt++, digg.getNumberDiggSubmitInTopScore(  ) );
        daoUtil.setInt( ncpt++, digg.getNumberDiggSubmitInTopComment(  ) );
        daoUtil.setBoolean( ncpt++, digg.isLimitNumberVote(  ) );
        daoUtil.setInt( ncpt++, digg.getNumberDiggSubmitCaractersShown(  ) );
        daoUtil.setBoolean( ncpt++, digg.isShowCategoryBlock(  ) );
        daoUtil.setBoolean( ncpt++, digg.isShowTopScoreBlock(  ) );
        daoUtil.setBoolean( ncpt++, digg.isShowTopCommentBlock(  ) );
        daoUtil.setBoolean( ncpt++, digg.isActiveDiggSubmitPaginator(  ) );
        daoUtil.setInt( ncpt++, digg.getNumberDiggSubmitPerPage(  ) );
        daoUtil.setString( ncpt++, digg.getRole(  ) );
        daoUtil.setBoolean( ncpt++, digg.isEnableMailNewDiggSubmit(  ) );
        daoUtil.setString( ncpt++, digg.getHeader(  ) );
        daoUtil.setInt( ncpt++, digg.getSortField(  ) );
        daoUtil.setString( ncpt++, digg.getCodeTheme(  ) );
        daoUtil.setString( ncpt++, digg.getConfirmationMessage(  ) );
        daoUtil.setBoolean( ncpt++, digg.isActiveEditorBbcode(  ) );
        daoUtil.setBoolean( ncpt++, digg.isDefaultDigg(  ) );
        daoUtil.setInt( ncpt++, digg.getIdDefaultSort(  ) );
        daoUtil.setBoolean( ncpt++, digg.isActiveDiggSubmitType(  ) );

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
        	int ncpt=1;
            digg = new Digg(  );
            digg.setIdDigg( daoUtil.getInt( ncpt++ ) );
            digg.setTitle( daoUtil.getString( ncpt++ ) );
            digg.setUnavailabilityMessage( daoUtil.getString( ncpt++ ) );
            digg.setWorkgroup( daoUtil.getString( ncpt++ ) );

            voteType = new VoteType(  );
            voteType.setIdVoteType( daoUtil.getInt( ncpt++ ) );
            digg.setVoteType( voteType );
            
            digg.setNumberVoteRequired( daoUtil.getInt( ncpt++) );
            digg.setNumberDayRequired( daoUtil.getInt(ncpt++ ) );
            digg.setActiveDiggSubmitAuthentification( daoUtil.getBoolean(ncpt++ ) );
            digg.setActiveVoteAuthentification( daoUtil.getBoolean( ncpt++ ) );
            digg.setActiveCommentAuthentification( daoUtil.getBoolean( ncpt++ ) );
            digg.setDisableNewDiggSubmit( daoUtil.getBoolean( ncpt++ ) );
            digg.setAuthorizedComment( daoUtil.getBoolean( ncpt++ ) );
            digg.setDisableNewComment( daoUtil.getBoolean( ncpt++ ) );
            digg.setIdMailingListDiggSubmit( daoUtil.getInt( ncpt++ ) );
            digg.setActiveCaptcha( daoUtil.getBoolean( ncpt++ ) );
            digg.setActive( daoUtil.getBoolean(  ncpt++ ) );
            digg.setDateCreation( daoUtil.getTimestamp(  ncpt++ ) );
            digg.setLibelleValidateButton( daoUtil.getString(  ncpt++ ) );
            digg.setActiveDiggPropositionState( daoUtil.getBoolean(  ncpt++ ) );
            digg.setLibelleContribution( daoUtil.getString(  ncpt++ ) );
            digg.setNumberDiggSubmitInTopScore( daoUtil.getInt(  ncpt++ ) );
            digg.setNumberDiggSubmitInTopComment( daoUtil.getInt(  ncpt++ ) );
            digg.setLimitNumberVote( daoUtil.getBoolean(  ncpt++ ) );
            digg.setNumberDiggSubmitCaractersShown( daoUtil.getInt(  ncpt++ ) );
            digg.setShowCategoryBlock( daoUtil.getBoolean(  ncpt++ ) );
            digg.setShowTopScoreBlock( daoUtil.getBoolean(  ncpt++ ) );
            digg.setShowTopCommentBlock( daoUtil.getBoolean(  ncpt++ ) );
            digg.setActiveDiggSubmitPaginator( daoUtil.getBoolean(  ncpt++ ) );
            digg.setNumberDiggSubmitPerPage( daoUtil.getInt(  ncpt++ ) );
            digg.setRole( daoUtil.getString(  ncpt++ ) );
            digg.setEnableMailNewDiggSubmit( daoUtil.getBoolean(  ncpt++ ) );
            digg.setHeader( daoUtil.getString(  ncpt++ ) );
            digg.setSortField( daoUtil.getInt(  ncpt++ ) );
            digg.setCodeTheme( daoUtil.getString(  ncpt++ ) );
            digg.setConfirmationMessage( daoUtil.getString(  ncpt++ ) );
            digg.setActiveEditorBbcode( daoUtil.getBoolean(  ncpt++ ) );
            digg.setDefaultDigg( daoUtil.getBoolean(  ncpt++ ) );
            digg.setIdDefaultSort( daoUtil.getInt(  ncpt++ ) );
            digg.setActiveDiggSubmitType( daoUtil.getBoolean(  ncpt++ ) );
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
        int ncpt=1;
        digg.setIdDigg( digg.getIdDigg(  ) );
        daoUtil.setInt( ncpt++, digg.getIdDigg(  ) );
        daoUtil.setString( ncpt++, digg.getTitle(  ) );
        daoUtil.setString( ncpt++, digg.getUnavailabilityMessage(  ) );
        daoUtil.setString( ncpt++, digg.getWorkgroup(  ) );
        daoUtil.setInt( ncpt++, digg.getVoteType(  ).getIdVoteType(  ) );
        daoUtil.setInt( ncpt++, digg.getNumberVoteRequired(  ) );
        daoUtil.setInt( ncpt++, digg.getNumberDayRequired(  ) );
        daoUtil.setBoolean( ncpt++, digg.isActiveDiggSubmitAuthentification(  ) );
        daoUtil.setBoolean( ncpt++, digg.isActiveVoteAuthentification(  ) );
        daoUtil.setBoolean( ncpt++, digg.isActiveCommentAuthentification(  ) );
        daoUtil.setBoolean( ncpt++, digg.isDisableNewDiggSubmit(  ) );
        daoUtil.setBoolean( ncpt++, digg.isAuthorizedComment(  ) );
        daoUtil.setBoolean( ncpt++, digg.isDisableNewComment(  ) );
        daoUtil.setInt( ncpt++, digg.getIdMailingListDiggSubmit(  ) );
        daoUtil.setBoolean( ncpt++, digg.isActiveCaptcha(  ) );
        daoUtil.setBoolean( ncpt++, digg.isActive(  ) );
        daoUtil.setTimestamp( ncpt++, digg.getDateCreation(  ) );
        daoUtil.setString( ncpt++, digg.getLibelleValidateButton(  ) );
        daoUtil.setBoolean( ncpt++, digg.isActiveDiggPropositionState(  ) );
        daoUtil.setString( ncpt++, digg.getLibelleContribution(  ) );
        daoUtil.setInt( ncpt++, digg.getNumberDiggSubmitInTopScore(  ) );
        daoUtil.setInt( ncpt++, digg.getNumberDiggSubmitInTopComment(  ) );
        daoUtil.setBoolean( ncpt++, digg.isLimitNumberVote(  ) );
        daoUtil.setInt( ncpt++, digg.getNumberDiggSubmitCaractersShown(  ) );
        daoUtil.setBoolean( ncpt++, digg.isShowCategoryBlock(  ) );
        daoUtil.setBoolean( ncpt++, digg.isShowTopScoreBlock(  ) );
        daoUtil.setBoolean( ncpt++, digg.isShowTopCommentBlock(  ) );
        daoUtil.setBoolean( ncpt++, digg.isActiveDiggSubmitPaginator(  ) );
        daoUtil.setInt( ncpt++, digg.getNumberDiggSubmitPerPage(  ) );
        daoUtil.setString( ncpt++, digg.getRole(  ) );
        daoUtil.setBoolean( ncpt++, digg.isEnableMailNewDiggSubmit(  ) );
        daoUtil.setString( ncpt++, digg.getHeader(  ) );
        daoUtil.setInt( ncpt++, digg.getSortField(  ) );
        daoUtil.setString( ncpt++, digg.getCodeTheme(  ) );
        daoUtil.setString( ncpt++, digg.getConfirmationMessage(  ) );
        daoUtil.setBoolean( ncpt++, digg.isActiveEditorBbcode(  ) );
        daoUtil.setBoolean( ncpt++, digg.isDefaultDigg(  ) );
        daoUtil.setInt( ncpt++, digg.getIdDefaultSort(  ) );
        daoUtil.setBoolean( ncpt++, digg.isActiveDiggSubmitType(  ) );
        daoUtil.setInt( ncpt++, digg.getIdDigg(  ) );

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
        int ncpt=1;

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
        	ncpt=1;
            digg = new Digg(  );
            digg.setIdDigg( daoUtil.getInt( ncpt++ ) );
            digg.setTitle( daoUtil.getString( ncpt++ ) );
            digg.setUnavailabilityMessage( daoUtil.getString( ncpt++ ) );
            digg.setWorkgroup( daoUtil.getString( ncpt++ ) );

            voteType = new VoteType(  );
            voteType.setIdVoteType( daoUtil.getInt( ncpt++ ) );
            digg.setVoteType( voteType );

            digg.setNumberVoteRequired( daoUtil.getInt( ncpt++ ) );
            digg.setNumberDayRequired( daoUtil.getInt( ncpt++ ) );
            digg.setActiveDiggSubmitAuthentification( daoUtil.getBoolean( ncpt++ ) );
            digg.setActiveVoteAuthentification( daoUtil.getBoolean( ncpt++ ) );
            digg.setActiveCommentAuthentification( daoUtil.getBoolean( ncpt++ ) );
            digg.setDisableNewDiggSubmit( daoUtil.getBoolean( ncpt++ ) );
            digg.setAuthorizedComment( daoUtil.getBoolean( ncpt++ ) );
            digg.setDisableNewComment( daoUtil.getBoolean( ncpt++ ) );
            digg.setIdMailingListDiggSubmit( daoUtil.getInt( ncpt++ ) );
            digg.setActiveCaptcha( daoUtil.getBoolean( ncpt++ ) );
            digg.setActive( daoUtil.getBoolean( ncpt++ ) );
            digg.setDateCreation( daoUtil.getTimestamp( ncpt++ ) );
            digg.setLibelleValidateButton( daoUtil.getString( ncpt++ ) );
            digg.setActiveDiggPropositionState( daoUtil.getBoolean( ncpt++ ) );
            digg.setLibelleContribution( daoUtil.getString( ncpt++ ) );
            digg.setNumberDiggSubmitInTopScore( daoUtil.getInt( ncpt++ ) );
            digg.setNumberDiggSubmitInTopComment( daoUtil.getInt( ncpt++ ) );
            digg.setLimitNumberVote( daoUtil.getBoolean( ncpt++ ) );
            digg.setNumberDiggSubmitCaractersShown( daoUtil.getInt( ncpt++ ) );
            digg.setShowCategoryBlock( daoUtil.getBoolean( ncpt++ ) );
            digg.setShowTopScoreBlock( daoUtil.getBoolean( ncpt++ ) );
            digg.setShowTopCommentBlock( daoUtil.getBoolean( ncpt++ ) );
            digg.setActiveDiggSubmitPaginator( daoUtil.getBoolean( ncpt++ ) );
            digg.setNumberDiggSubmitPerPage( daoUtil.getInt( ncpt++ ) );
            digg.setRole( daoUtil.getString( ncpt++ ) );
            digg.setEnableMailNewDiggSubmit( daoUtil.getBoolean( ncpt++ ) );
            digg.setHeader( daoUtil.getString( ncpt++ ) );
            digg.setSortField( daoUtil.getInt( ncpt++ ) );
            digg.setCodeTheme( daoUtil.getString( ncpt++ ) );
            digg.setConfirmationMessage( daoUtil.getString( ncpt++ ) );
            digg.setActiveEditorBbcode( daoUtil.getBoolean( ncpt++ ) );
            digg.setDefaultDigg( daoUtil.getBoolean( ncpt++ ) );
            digg.setIdDefaultSort( daoUtil.getInt( ncpt++ ) );
            digg.setActiveDiggSubmitType( daoUtil.getBoolean( ncpt++ ) );

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
