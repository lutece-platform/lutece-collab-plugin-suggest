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
 * class SuggestSubmitStateDAO
 *
 */
public class SuggestSubmitStateDAO implements ISuggestSubmitStateDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_state,title,number FROM suggest_suggest_submit_state WHERE id_state=?";
    private static final String SQL_QUERY_SELECT = "SELECT id_state,title,number FROM suggest_suggest_submit_state ";
    private static final String SQL_QUERY_FIND_BY_NUMERO = "SELECT id_state,title,number FROM suggest_suggest_submit_state WHERE number=? ";

    /**
     * Load the data of the suggest submit state from the table
     *
     * @param idKey
     *            The identifier of the state
     * @param plugin
     *            the plugin
     * @return the instance of the state
     */
    public SuggestSubmitState load( int idKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, idKey );
        daoUtil.executeQuery( );

        SuggestSubmitState suggestSubmitState = null;

        if ( daoUtil.next( ) )
        {
            suggestSubmitState = new SuggestSubmitState( );
            suggestSubmitState.setIdSuggestSubmitState( daoUtil.getInt( 1 ) );
            suggestSubmitState.setTitle( daoUtil.getString( 2 ) );
            suggestSubmitState.setNumber( daoUtil.getInt( 3 ) );
        }

        daoUtil.free( );

        return suggestSubmitState;
    }

    /**
     * Load the data of the suggest submit state from the table by numero
     *
     * @param numero
     *            The numero of the state
     * @param plugin
     *            the plugin
     * @return the instance of the state
     */
    public SuggestSubmitState loadByNumero( int numero, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_NUMERO, plugin );
        daoUtil.setInt( 1, numero );
        daoUtil.executeQuery( );

        SuggestSubmitState suggestSubmitState = null;

        if ( daoUtil.next( ) )
        {
            suggestSubmitState = new SuggestSubmitState( );
            suggestSubmitState.setIdSuggestSubmitState( daoUtil.getInt( 1 ) );
            suggestSubmitState.setTitle( daoUtil.getString( 2 ) );
            suggestSubmitState.setNumber( daoUtil.getInt( 3 ) );
        }

        daoUtil.free( );

        return suggestSubmitState;
    }

    /**
     * Load the data of all suggest submit state returns them in a list
     * 
     * @param plugin
     *            the plugin
     * @return the list of suggest submit state
     */
    public List<SuggestSubmitState> select( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.executeQuery( );

        SuggestSubmitState suggestSubmitState = null;
        List<SuggestSubmitState> listSuggestSubmitState = new ArrayList<SuggestSubmitState>( );

        while ( daoUtil.next( ) )
        {
            suggestSubmitState = new SuggestSubmitState( );
            suggestSubmitState.setIdSuggestSubmitState( daoUtil.getInt( 1 ) );
            suggestSubmitState.setTitle( daoUtil.getString( 2 ) );
            suggestSubmitState.setNumber( daoUtil.getInt( 3 ) );
            listSuggestSubmitState.add( suggestSubmitState );
        }

        daoUtil.free( );

        return listSuggestSubmitState;
    }
}
