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
 *
 *  Interface ISuggestSubmitTypeDAO
 *
 */
public interface ISuggestSubmitTypeDAO
{
    /**
    * Insert a new record in the table.
    *
    * @param suggestSubmitType instance of the Suggest Submit Type object to insert
    * @param plugin the plugin
    * @return the id of the new Suggest
    */
    int insert( SuggestSubmitType suggestSubmitType, Plugin plugin );

    /**
     * Load the data of the suggestSubmitType from the table
     *
     * @param nIdSuggestSubmitType The identifier of the formResponse
     * @param plugin the plugin
     * @return the instance of the suggestSubmitType
     */
    SuggestSubmitType load( int nIdSuggestSubmitType, Plugin plugin );

    /**
     * Delete   the suggest submit type whose identifier is specified in parameter
     *
     * @param nIdSuggestSubmitType The identifier of the suggest submit
     * @param plugin the plugin
     */
    void delete( int nIdSuggestSubmitType, Plugin plugin );

    /**
     * Update the the suggestSubmitType in the table
     *
     * @param suggestSubmitType instance of the suggestSubmit object to update
     * @param plugin the plugin
     */
    void store( SuggestSubmitType suggestSubmitType, Plugin plugin );

    /**
     * Load the list of the suggestSubmitType from the table
     *
     * @param plugin the plugin
     * @return the instance of the suggestSubmitType
     */
    List<SuggestSubmitType> selectList( Plugin plugin );

    /**
     * Load the list of the suggestSubmitType from the table
     * @param nIdSuggest the id Suggest
     * @param plugin the plugin
     * @return the instance of the suggestSubmitType
     */
    List<SuggestSubmitType> selectListByIdSuggest( int nIdSuggest, Plugin plugin );

    /**
     * true if there is a  suggest associate to the suggest submit type
     * @param nIdType the key of the type
     * @param plugin the plugin
     * @return true if there is a suggest associate to the type
     */
    boolean isAssociateToSuggest( int nIdType, Plugin plugin );

    /**
    * Delete an association between suggest and a suggest submit type
    *
    * @param nIdSuggest The identifier of the suggest
    * @param nIdSuggestSubmitType nIdSuggestSubmitType
    * @param plugin the plugin
    */
    void deleteSuggestAssociation( int nIdSuggest, int nIdSuggestSubmitType, Plugin plugin );

    /**
     * insert an association between suggest and a suggest submit type
     *
     * @param nIdSuggest The identifier of the suggest
     * @param nIdSuggestSubmitType nIdSuggestSubmitType
     * @param plugin the plugin
     */
    void insertSuggestAssociation( int nIdSuggest, int nIdSuggestSubmitType, Plugin plugin );
}
