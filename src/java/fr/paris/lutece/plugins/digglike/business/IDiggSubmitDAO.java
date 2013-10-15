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
 *
 *  Interface IFormSubmitDAO
 *
 */
public interface IDiggSubmitDAO
{
    /**
    * Insert a new record in the table.
    * @param diggSubmit instance of the Digg Submit object to insert
    * @param plugin the plugin
    * @return the id of the new Digg
    */
    int insert( DiggSubmit diggSubmit, Plugin plugin );

    /**
     * Load the data of the diggSubmit from the table
     *
     * @param nIdFormSubmit The identifier of the formResponse
     * @param plugin the plugin
     * @return the instance of the diggSubmit
     */
    DiggSubmit load( int nIdFormSubmit, Plugin plugin );

    /**
     * Delete   the digg submit whose identifier is specified in parameter
     *
     * @param nIdDiggSubmit The identifier of the digg submit
     * @param plugin the plugin
     */
    void delete( int nIdDiggSubmit, Plugin plugin );

    /**
     * Update the the diggSubmit in the table
     *
     * @param diggSubmit instance of the diggSubmit object to update
     * @param plugin the plugin
     */
    void store( DiggSubmit diggSubmit, Plugin plugin );

    /**
     * Load the data of all the diggSubmit who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of diggSubmit
     */
    List<DiggSubmit> selectListByFilter( SubmitFilter filter, Plugin plugin );

    /**
     * Load the id of all the diggSubmit who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of diggSubmit id
     */
    List<Integer> selectIdListByFilter( SubmitFilter filter, Plugin plugin );

    /**
    *
    *
    * return the number  of all the diggSubmit who verify the filter
    * @param filter the filter
    * @param plugin the plugin
    * @return  the number  of all the diggSubmit who verify the filter
    */
    int selectCountByFilter( SubmitFilter filter, Plugin plugin );

    /**
    * Modify the order of a diggsubmit
    * @param nNewOrder The order number
    * @param nId The diggsubmit identifier
    * @param plugin The plugin
    */
    void storeDiggSubmitOrder( int nNewOrder, int nId, Plugin plugin );

    /**
     * Calculate the new max order in a list
     * @param nIdDigg the id of the digg
     * @return the max order of diggsubmit
     * @param plugin The plugin
     */
    int maxOrderDiggSubmit( int nIdDigg, boolean bListPinned, Plugin plugin );
}
