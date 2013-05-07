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

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.List;


/**
* IEntryDAO Interface
*/
public interface IEntryDAO
{
    /**
     * Insert a new record in the table.
     *
     * @param entry instance of the Entry object to insert
     * @param plugin the plugin
     * @return the id of the new entry
     */
    int insert( IEntry entry, Plugin plugin );

    /**
         * Update the entry in the table
         *
         * @param entry instance of the Entry object to update
         * @param plugin the plugin
         */
    void store( IEntry entry, Plugin plugin );

    /**
         * Delete a record from the table
         *
         * @param nIdEntry The identifier of the entry
         * @param plugin the plugin
         */
    void delete( int nIdEntry, Plugin plugin );

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
         * Load the data of the entry from the table
         *
         * @param nIdEntry The identifier of the entry
         * @param plugin the plugin
         * @return the instance of the Entry
         */
    IEntry load( int nIdEntry, Plugin plugin );

    /**
         * Load the data of all the entry who verify the filter and returns them in a  list
         *
         * @param filter the filter
         * @param plugin the plugin
         * @return  the list of entry
         */
    List<IEntry> selectEntryListByFilter( EntryFilter filter, Plugin plugin );

    /**
         * Return  the number of entry who verify the filter
         *
         * @param filter the filter
         * @param plugin the plugin
         * @return   the number of entry who verify the filter
         */
    int selectNumberEntryByFilter( EntryFilter filter, Plugin plugin );

    /**
     * Delete an association between  entry and a regular expression
     *
     * @param nIdEntry The identifier of the field
     *  @param nIdExpression The identifier of the regular expression
     * @param plugin the plugin
     */
    void deleteVerifyBy( int nIdEntry, int nIdExpression, Plugin plugin );

    /**
     * insert an association between  entry and a regular expression
     *
     * @param nIdEntry The identifier of the entry
     * @param nIdExpression The identifier of the regular expression
     * @param plugin the plugin
     */
    public void insertVerifyBy( int nIdEntry, int nIdExpression, Plugin plugin );

    /**
     * Load the key of all the regularExpression  associate to the entry and returns them in a  list
     * @param nIdEntry the id of entry
     * @param plugin the plugin
     * @return  the list of regular expression key
     */
    List<Integer> selectListRegularExpressionKeyByIdEntry( int nIdEntry, Plugin plugin );

    /**
     * verify if the regular expresssion is use
     *
     * @param nIdExpression The identifier of the entry
     * @param plugin the plugin
     * @return true if the regular expression is use
     */
    boolean isRegularExpressionIsUse( int nIdExpression, Plugin plugin );
}
