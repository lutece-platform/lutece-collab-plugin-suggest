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

import java.util.List;

/**
 * The Vote DAO interface
 *
 */
public interface IVoteDAO
{
    /**
     * Insert a new record in the table.
     *
     * @param vote
     *            instance of the Vote object to insert
     * @param plugin
     *            the plugin
     */
    void insert( Vote vote, Plugin plugin );

    /**
     * Load the data of all vote associate to the suggest submit
     * 
     * @param nIdSuggestSubmit
     *            the suggestsubmit id
     * @param plugin
     *            the plugin
     * @return the list of vote button associated to the vote type returns them in a list
     */
    List<Vote> selectVoteByIdSuggestSubmit( int nIdSuggestSubmit, Plugin plugin );

    /**
     * return the number of vote for a lutece user on suggestSubmit
     * 
     * @param nIdSuggestSubmit
     *            the id of the suggest submit
     * @param strLuteceUserKey
     *            the LuteceUserKey
     * @param plugin
     *            the plugin
     * @return the number of vote for a lutece user on suggestSubmit
     */
    int selectCountVoteByIdSuggestSubmitAndLuteceUserKey( int nIdSuggestSubmit, String strLuteceUserKey, Plugin plugin );

    /**
     * return the number of vote for a lutece user on suggestSubmit
     * 
     * @param nIdSuggestSubmit
     *            the id of the suggest submit
     * @param plugin
     *            the plugin
     * @return the number of vote for a lutece user on suggestSubmit
     */
    int selectCountVoteByIdSuggestSubmit( int nIdSuggestSubmit, Plugin plugin );
}
