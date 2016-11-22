/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
*  interface IEntryAdditionalAttributeDAO
*/
public interface IEntryAdditionalAttributeDAO
{
    /**
    * Insert a new record in the table.
    * @param entryAdditionalAttribute instance of the Entry additional attribute object to insert
    * @param plugin the plugin
    */
    void insert( EntryAdditionalAttribute entryAdditionalAttribute, Plugin plugin );

    /**
     * Load the additional attribute of the entry returns them in a list
     * @param idEntry the id of the Entry
     * @param plugin the plugin
     * @return  the list of entry additional attribute
     */
    List<EntryAdditionalAttribute> selectEntryAdditionalAttributeList( int idEntry, Plugin plugin );

    /**
     * Delete   the additional entry whose identifier is specified in parameter
     *
     * @param nIdEntry The identifier of the entry
     * @param plugin the plugin
     */
    void delete( int nIdEntry, Plugin plugin );

    /**
     * Update the additional entry in the table
     *
     * @param entryAdditionalAttribute instance of the EntryAdditionalAttribute object to update
     * @param plugin the plugin
     */
    void store( EntryAdditionalAttribute entryAdditionalAttribute, Plugin plugin );
}
