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

import fr.paris.lutece.portal.business.style.Theme;
import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.List;
import java.util.Map;

/**
 * ISuggestDAO Interface
 */
public interface ISuggestDAO
{
    /**
     * Insert a new record in the table.
     *
     * @param suggest
     *            instance of the Suggest to insert
     * @param plugin
     *            the plugin
     * @return the new suggest create
     */
    int insert( Suggest suggest, Plugin plugin );

    /**
     * Load the data of the suggest from the table
     *
     * @param nId
     *            The identifier of the suggest
     * @param plugin
     *            the plugin
     * @return the instance of the Suggest
     */
    Suggest load( int nId, Plugin plugin );

    /**
     * Delete a record from the table
     *
     * @param nIdSuggest
     *            The identifier of the suggest
     * @param plugin
     *            the plugin
     */
    void delete( int nIdSuggest, Plugin plugin );

    /**
     * Update the suggest in the table
     *
     * @param suggest
     *            instance of the suggest object to update
     * @param plugin
     *            the plugin
     */
    void store( Suggest suggest, Plugin plugin );

    /**
     * Load the data of all the suggest who verify the filter and returns them in a list
     * 
     * @param filter
     *            the filter
     * @param plugin
     *            the plugin
     * @return the list of suggest
     */
    List<Suggest> selectSuggestList( SuggestFilter filter, Plugin plugin );

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
    void storeSuggestOrderField( int nId, int nSortField, Plugin plugin );

    /**
     * Load all the themes for form xpages
     * 
     * @param plugin
     *            the plugin
     * @return a map containing the themes by form id
     */
    Map<Integer, Theme> getXPageThemesMap( Plugin plugin );
}
