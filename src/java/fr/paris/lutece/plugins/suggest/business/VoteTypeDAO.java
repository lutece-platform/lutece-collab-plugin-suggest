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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * class GraphTypeDAO
 *
 */
public class VoteTypeDAO implements IVoteTypeDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_vote_type,title,template_file_name FROM suggest_vote_type WHERE id_vote_type=?";
    private static final String SQL_QUERY_SELECT = "SELECT id_vote_type,title,template_file_name FROM suggest_vote_type";
    // private static final String SQL_QUERY_SELECT_VOTE_BUTTON_BY_ID_VOTE_TYPE = "SELECT id_vote_button,title,vote_button_value,icon_content,icon_mime_type " +
    // "FROM suggest_vote_type_vote_button vt,suggest_vote_button vb WHERE vt.id_vote_button=vb.id_vote_button " +
    // "AND vt.id_vote_type=? ORDER BY vote_button_order";
    private static final String SQL_QUERY_DELETE_VOTE_BUTTON_ASSOCIATED_BY_ID_VOTE_TYPE = "DELETE FROM suggest_vote_type_vote_button  WHERE id_vote_type = ? ";
    private static final String SQL_QUERY_INSERT_ASSOCIATED_VOTE_BUTTON = "INSERT INTO "
            + "suggest_vote_type_vote_button(id_vote_type,id_vote_button,vote_button_order) VALUES(?,?,?) ";

    /**
     * Load the data of the vote type from the table
     *
     * @param idKey
     *            The identifier of the vote type
     * @param plugin
     *            the plugin
     * @return the instance of the vote type
     */
    public VoteType load( int idKey, Plugin plugin )
    {
        VoteType voteType = null;
        
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin ) )
        {
            daoUtil.setInt( 1, idKey );
            daoUtil.executeQuery( );
            
            if ( daoUtil.next( ) )
            {
                voteType = new VoteType( );
                voteType.setIdVoteType( daoUtil.getInt( 1 ) );
                voteType.setTitle( daoUtil.getString( 2 ) );
                voteType.setTemplateFileName( daoUtil.getString( 3 ) );
    
                // voteType.setVoteButtons(selectListVoteButton(voteType.getIdVoteType(), plugin ));
            }

        }

        return voteType;
    }

    /**
     * Load the data of all vote type returns them in a list
     * 
     * @param plugin
     *            the plugin
     * @return the list of vote type
     */
    public List<VoteType> select( Plugin plugin )
    {
        List<VoteType> listVoteType = new ArrayList<>( );
        
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.executeQuery( );
    
            VoteType voteType = null;
    
            while ( daoUtil.next( ) )
            {
                voteType = new VoteType( );
                voteType.setIdVoteType( daoUtil.getInt( 1 ) );
                voteType.setTitle( daoUtil.getString( 2 ) );
                voteType.setTemplateFileName( daoUtil.getString( 3 ) );
    
                listVoteType.add( voteType );
            }

        }

        return listVoteType;
    }

    // /**
    // * Load the data of all vote button associated to the vote type returns them in a list
    // * @param nIdVoteType the vote type id
    // * @param plugin the plugin
    // * @return the list of vote button associated to the vote type returns them in a list
    // */
    // private List<VoteButton> selectListVoteButton( int nIdVoteType, Plugin plugin )
    // {
    // DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_VOTE_BUTTON_BY_ID_VOTE_TYPE, plugin );
    // daoUtil.setInt( 1, nIdVoteType );
    // daoUtil.executeQuery( );
    //
    // VoteButton voteButton = null;
    // List<VoteButton> listVoteButton = new ArrayList<VoteButton>( );
    //
    // while ( daoUtil.next( ) )
    // {
    // voteButton = new VoteButton( );
    // voteButton.setIdVoteButton( daoUtil.getInt( 1 ) );
    // voteButton.setTitle( daoUtil.getString( 2 ) );
    // voteButton.setValue( daoUtil.getString( 3 ) );
    // voteButton.setIconContent( daoUtil.getBytes( 4 ) );
    // voteButton.setIconMimeType( daoUtil.getString( 5 ) );
    //
    // listVoteButton.add( voteButton );
    // }
    //
    // daoUtil.free( );
    //
    // return listVoteButton;
    // }

    /**
     * Delete all associations between vote type and vote buttons
     *
     * @param nIdVoteType
     *            The identifier of the vote type
     * @param plugin
     *            the plugin
     */
    public void deleteAllAssociatedVoteButtons( int nIdVoteType, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_VOTE_BUTTON_ASSOCIATED_BY_ID_VOTE_TYPE, plugin ) )
        {
            daoUtil.setInt( 1, nIdVoteType );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * insert an association between vote type and vote button
     *
     * @param nIdVoteType
     *            The identifier of the vote Type
     * @param nIdVoteButton
     *            The identifier of the vote button
     * @param nNumero
     *            the numero
     * @param plugin
     *            the plugin
     */
    public void insertVoteButtonAssociated( int nIdVoteType, int nIdVoteButton, int nNumero, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_ASSOCIATED_VOTE_BUTTON, plugin ) )
        {
            daoUtil.setInt( 1, nIdVoteType );
            daoUtil.setInt( 2, nIdVoteButton );
            daoUtil.setInt( 3, nNumero );
    
            daoUtil.executeUpdate( );
        }
    }
}
