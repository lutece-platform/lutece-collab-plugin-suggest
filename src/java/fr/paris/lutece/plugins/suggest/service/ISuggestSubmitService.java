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
package fr.paris.lutece.plugins.suggest.service;

import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.suggest.business.IEntry;
import fr.paris.lutece.plugins.suggest.business.SubmitFilter;
import fr.paris.lutece.portal.business.style.Theme;
import fr.paris.lutece.portal.service.plugin.Plugin;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;


public interface ISuggestSubmitService
{
    /**
    * Creation of an instance of record
    *
    * @param suggestSubmit The instance of the suggestSubmit which contains the informations to store
    * @param plugin the Plugin
    * @return the id of {@link Theme} suggestsubmit
    */
    @Transactional( "suggest.transactionManager" )
    int create( SuggestSubmit suggestSubmit, Plugin plugin, Locale locale );

    /**
     * Remove the record whose identifier is specified in parameter
     *
     * @param nIdSuggestSubmit the id of the suggestSubmit
     * @param plugin the Plugin
     */
    @Transactional( "suggest.transactionManager" )
    void remove( int nIdSuggestSubmit, Plugin plugin );

    /**
     * Update of the suggestSubmit which is specified in parameter
     *
     * @param suggestSubmit The instance of the suggestSubmit which contains the informations to update
     * @param plugin the Plugin
     *
     */
    @Transactional( "suggest.transactionManager" )
    void update( SuggestSubmit suggestSubmit, Plugin plugin );

    /**
     * Update of the suggestSubmit which is specified in parameter
     * @param suggestSubmit
     * @param bUpdateIndex
     * @param plugin
     */
    @Transactional( "suggest.transactionManager" )
    void update( SuggestSubmit suggestSubmit, boolean bUpdateIndex, Plugin plugin );

    /**
     * Returns an instance of a SuggestSubmit whose identifier is specified in parameter
     *
     * @param nKey The suggestSubmit primary key
     * @param bLoadCommentList true if the comment list must be get
     * @param plugin the Plugin
     * @return an instance of SuggestSubmit
     */
    SuggestSubmit findByPrimaryKey( int nKey, boolean bLoadCommentList, Plugin plugin );

    /**
     * Returns an instance of a SuggestSubmit whose identifier is specified in parameter
     *
     * @param nKey The suggestSubmit primary key
     * @param bLoadCommentList true if the comment list must be get
     * @param numberMaxCommentLoad the number max of parent comment Load
     * @param plugin the Plugin
     * @return an instance of SuggestSubmit
     */
    SuggestSubmit findByPrimaryKey( int nKey, boolean bLoadCommentList, Integer numberMaxCommentLoad, Plugin plugin );

    /**
     * Returns an instance of a SuggestSubmit whose identifier is specified in parameter
     *
     * @param nKey The suggestSubmit primary key
     * @param bLoadCommentList true if the comment list must be get
     * @param bLoadResponseList true if the response list must be get
     * @param plugin the Plugin
     * @return an instance of SuggestSubmit
     */
    SuggestSubmit findByPrimaryKey( int nKey, boolean bLoadCommentList, boolean bLoadResponseList, Plugin plugin );

    /**
     * Load the data of all the suggestSubmit who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of suggestSubmit
     */
    List<SuggestSubmit> getSuggestSubmitList( SubmitFilter filter, Plugin plugin );

    //    /**
    //     * Load the data of all the suggestSubmit with the number of comment by suggest submit  who verify the filter and returns them in a  list
    //     * @param filter the filter
    //     * @param plugin the plugin
    //     * @return  the list of suggestSubmit
    //     */
    //    List<SuggestSubmit> getSuggestSubmitListWithNumberComment( SubmitFilter filter, Plugin plugin );

    /**
     * Load the id of all the suggestSubmit who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of suggestSubmit id
     */
    List<Integer> getSuggestSubmitListId( SubmitFilter filter, Plugin plugin );

    /**
     * Load the data of all the suggestSubmit who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @param nNumberMaxSuggestSubmit Max Number of Suggestsubmit return
     * @return  the list of suggestSubmit
     */
    List<SuggestSubmit> getSuggestSubmitList( SubmitFilter filter, Plugin plugin, int nNumberMaxSuggestSubmit );

    /**
     * Load the number of all the suggestSubmit who verify the filter
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of suggestSubmit
     */
    int getCountSuggestSubmit( SubmitFilter filter, Plugin plugin );

    /**
     * return the id of the next suggest submit in the list
     *  @param nIdCurrentSuggestSubmit the id of the current suggest submit
     * @param filter the filter
     * @param plugin the plugin
     * @return the id of the next suggest submit in the list
     */
    int findNextIdSuggestSubmitInTheList( int nIdCurrentSuggestSubmit, SubmitFilter filter, Plugin plugin );

    /**
     * return the id of the prev suggest submit in the list
     *  @param nIdCurrentSuggestSubmit the id of the current suggest submit
     * @param filter the filter
     * @param plugin the plugin
     * @return the id of the prev suggest submit in the list
     */
    int findPrevIdSuggestSubmitInTheList( int nIdCurrentSuggestSubmit, SubmitFilter filter, Plugin plugin );

    /**
     * move an element in the list of suggestSubmit and update the order
     * @param nPositionElement the position of the element to move
     * @param nNewPositionElement the new position of the element
     * @param nIdSuggest the suggest id
     * @param bListPinned true if the list to update contains only pinned suggestsubmit
     * @param plugin the plugin
     */
    @Transactional( "suggest.transactionManager" )
    void updateSuggestSubmitOrder( Integer nPositionElement, Integer nNewPositionElement, int nIdSuggest,
        boolean bListPinned, Plugin plugin );

    /**
     * Search the max order number of contacts for one list
     * @param nIdSuggest the Id of the Suggest
     * @param bListPinned true if the list
     * @return int the max order
     * @param plugin The Plugin object
     */
    int getMaxOrderList( int nIdSuggest, boolean bListPinned, Plugin plugin );

    /**
     * Update the display off all suggest submit associated to a suggest
     *
     * @param nIdSuggest
     *            the id suggest
     * @param plugin the plugin
     *
     * @locale locale the locale
     */
    @Transactional( "suggest.transactionManager" )
    void updateAllDisplayOfSuggestSubmit( Integer nIdSuggest, Plugin plugin, Locale locale );

    /**
     * update the display of the suggestsubmit
     * @param nIdSuggestSubmit the suggestSubmit Id
     * @param plugin the plugin
     * @param locale the locale
     * @param suggest the suggest
     * @param mapEntry a map of entry assocaited to the suggest
     */
    @Transactional( "suggest.transactionManager" )
    void updateDisplaySuggestSubmit( Integer nIdSuggestSubmit, Plugin plugin, Locale locale, Suggest suggest,
        Map<Integer, IEntry> mapEntry );
}
