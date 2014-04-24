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
package fr.paris.lutece.plugins.digglike.service;

import fr.paris.lutece.plugins.digglike.business.Digg;
import fr.paris.lutece.plugins.digglike.business.DiggSubmit;
import fr.paris.lutece.plugins.digglike.business.IEntry;
import fr.paris.lutece.plugins.digglike.business.SubmitFilter;
import fr.paris.lutece.portal.business.style.Theme;
import fr.paris.lutece.portal.service.plugin.Plugin;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;


public interface IDiggSubmitService
{
    /**
    * Creation of an instance of record
    *
    * @param diggSubmit The instance of the diggSubmit which contains the informations to store
    * @param plugin the Plugin
    * @return the id of {@link Theme} diggsubmit
    */
    @Transactional( "digglike.transactionManager" )
    int create( DiggSubmit diggSubmit, Plugin plugin, Locale locale );

    /**
     * Remove the record whose identifier is specified in parameter
     *
     * @param nIdDiggSubmit the id of the diggSubmit
     * @param plugin the Plugin
     */
    @Transactional( "digglike.transactionManager" )
    void remove( int nIdDiggSubmit, Plugin plugin );

    /**
     * Update of the diggSubmit which is specified in parameter
     *
     * @param diggSubmit The instance of the diggSubmit which contains the informations to update
     * @param plugin the Plugin
     *
     */
    @Transactional( "digglike.transactionManager" )
    void update( DiggSubmit diggSubmit, Plugin plugin );

    /**
     * Update of the diggSubmit which is specified in parameter
     * @param diggSubmit
     * @param bUpdateIndex
     * @param plugin
     */
    @Transactional( "digglike.transactionManager" )
    void update( DiggSubmit diggSubmit, boolean bUpdateIndex, Plugin plugin );

    /**
     * Returns an instance of a DiggSubmit whose identifier is specified in parameter
     *
     * @param nKey The diggSubmit primary key
     * @param bLoadCommentList true if the comment list must be get
     * @param plugin the Plugin
     * @return an instance of DiggSubmit
     */
    DiggSubmit findByPrimaryKey( int nKey, boolean bLoadCommentList, Plugin plugin );

    /**
     * Returns an instance of a DiggSubmit whose identifier is specified in parameter
     *
     * @param nKey The diggSubmit primary key
     * @param bLoadCommentList true if the comment list must be get
     * @param numberMaxCommentLoad the number max of parent comment Load
     * @param plugin the Plugin
     * @return an instance of DiggSubmit
     */
    DiggSubmit findByPrimaryKey( int nKey, boolean bLoadCommentList, Integer numberMaxCommentLoad, Plugin plugin );

    /**
     * Returns an instance of a DiggSubmit whose identifier is specified in parameter
     *
     * @param nKey The diggSubmit primary key
     * @param bLoadCommentList true if the comment list must be get
     * @param bLoadResponseList true if the response list must be get
     * @param plugin the Plugin
     * @return an instance of DiggSubmit
     */
    DiggSubmit findByPrimaryKey( int nKey, boolean bLoadCommentList, boolean bLoadResponseList, Plugin plugin );

    /**
     * Load the data of all the diggSubmit who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of diggSubmit
     */
    List<DiggSubmit> getDiggSubmitList( SubmitFilter filter, Plugin plugin );

    //    /**
    //     * Load the data of all the diggSubmit with the number of comment by digg submit  who verify the filter and returns them in a  list
    //     * @param filter the filter
    //     * @param plugin the plugin
    //     * @return  the list of diggSubmit
    //     */
    //    List<DiggSubmit> getDiggSubmitListWithNumberComment( SubmitFilter filter, Plugin plugin );

    /**
     * Load the id of all the diggSubmit who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of diggSubmit id
     */
    List<Integer> getDiggSubmitListId( SubmitFilter filter, Plugin plugin );

    /**
     * Load the data of all the diggSubmit who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @param nNumberMaxDiggSubmit Max Number of Diggsubmit return
     * @return  the list of diggSubmit
     */
    List<DiggSubmit> getDiggSubmitList( SubmitFilter filter, Plugin plugin, int nNumberMaxDiggSubmit );

    /**
     * Load the number of all the diggSubmit who verify the filter
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of diggSubmit
     */
    int getCountDiggSubmit( SubmitFilter filter, Plugin plugin );

    /**
     * return the id of the next digg submit in the list
     *  @param nIdCurrentDiggSubmit the id of the current digg submit
     * @param filter the filter
     * @param plugin the plugin
     * @return the id of the next digg submit in the list
     */
    int findNextIdDiggSubmitInTheList( int nIdCurrentDiggSubmit, SubmitFilter filter, Plugin plugin );

    /**
     * return the id of the prev digg submit in the list
     *  @param nIdCurrentDiggSubmit the id of the current digg submit
     * @param filter the filter
     * @param plugin the plugin
     * @return the id of the prev digg submit in the list
     */
    int findPrevIdDiggSubmitInTheList( int nIdCurrentDiggSubmit, SubmitFilter filter, Plugin plugin );

    /**
     * move an element in the list of diggSubmit and update the order
     * @param nPositionElement the position of the element to move
     * @param nNewPositionElement the new position of the element
     * @param nIdDigg the digg id
     * @param bListPinned true if the list to update contains only pinned diggsubmit
     * @param plugin the plugin
     */
    @Transactional( "digglike.transactionManager" )
    void updateDiggSubmitOrder( Integer nPositionElement, Integer nNewPositionElement, int nIdDigg,
        boolean bListPinned, Plugin plugin );

    /**
     * Search the max order number of contacts for one list
     * @param nIdDigg the Id of the Digg
     * @param bListPinned true if the list
     * @return int the max order
     * @param plugin The Plugin object
     */
    int getMaxOrderList( int nIdDigg, boolean bListPinned, Plugin plugin );

    /**
     * Update the display off all digg submit associated to a digg
     *
     * @param nIdDigg
     *            the id digg
     * @param plugin the plugin
     *
     * @locale locale the locale
     */
    @Transactional( "digglike.transactionManager" )
    void updateAllDisplayOfDiggSubmit( Integer nIdDigg, Plugin plugin, Locale locale );

    /**
     * update the display of the diggsubmit
     * @param nIdDiggSubmit the diggSubmit Id
     * @param plugin the plugin
     * @param locale the locale
     * @param digg the digg
     * @param mapEntry a map of entry assocaited to the digg
     */
    @Transactional( "digglike.transactionManager" )
    void updateDisplayDiggSubmit( Integer nIdDiggSubmit, Plugin plugin, Locale locale, Digg digg,
        Map<Integer, IEntry> mapEntry );
}
