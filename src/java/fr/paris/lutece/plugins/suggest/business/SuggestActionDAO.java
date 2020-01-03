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
 * SuggestActionDAO
 */
public class SuggestActionDAO implements ISuggestActionDAO
{
    private static final String SQL_QUERY_SELECT_ACTIONS = "SELECT a.name_key, a.description_key, a.action_url, a.icon_url, a.action_permission ,a.suggest_state"
            + " FROM suggest_action a  where a.suggest_state=?  order by id_action ASC";

    /**
     * Load the list of actions for a all suggest by suggest state
     * 
     * @param nState
     *            the state of the suggest
     * @param plugin
     *            the plugin
     * @return The Collection of actions
     */
    public List<SuggestAction> selectActionsBySuggestState( int nState, Plugin plugin )
    {
        List<SuggestAction> listActions = new ArrayList<SuggestAction>( );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ACTIONS, plugin );
        daoUtil.setInt( 1, nState );
        daoUtil.executeQuery( );

        while ( daoUtil.next( ) )
        {
            SuggestAction action = new SuggestAction( );
            action.setNameKey( daoUtil.getString( 1 ) );
            action.setDescriptionKey( daoUtil.getString( 2 ) );
            action.setUrl( daoUtil.getString( 3 ) );
            action.setIconUrl( daoUtil.getString( 4 ) );
            action.setPermission( daoUtil.getString( 5 ) );
            action.setSuggestState( daoUtil.getInt( 6 ) );
            listActions.add( action );
        }

        daoUtil.free( );

        return listActions;
    }
}
