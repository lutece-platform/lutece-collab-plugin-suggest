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

import fr.paris.lutece.plugins.suggest.service.CommentSubmitService;
import fr.paris.lutece.plugins.suggest.service.search.SuggestIndexer;
import fr.paris.lutece.plugins.suggest.utils.SuggestIndexerUtils;
import fr.paris.lutece.portal.business.indexeraction.IndexerAction;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.util.ArrayList;
import java.util.List;

/**
 * class FormSubmitHome
 */
public final class SuggestSubmitHome
{
    // Static variable pointed at the DAO instance
    private static ISuggestSubmitDAO _dao = SpringContextService.getBean( "suggest.suggestSubmitDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private SuggestSubmitHome( )
    {
    }

    /**
     * Creation of an instance of suggestSubmit
     *
     * @param suggestSubmit
     *            The instance of the suggestSubmit which contains the informations to store
     * @param plugin
     *            the Plugin
     * @return the id of the new suggest submit
     *
     */
    public static int create( SuggestSubmit suggestSubmit, Plugin plugin )
    {
        return _dao.insert( suggestSubmit, plugin );
    }

    /**
     * Update of the suggestSubmit which is specified in parameter
     *
     * @param suggestSubmit
     *            The instance of the suggestSubmit which contains the informations to update
     * @param plugin
     *            the Plugin
     *
     */
    public static void update( SuggestSubmit suggestSubmit, Plugin plugin )
    {
        update( suggestSubmit, true, plugin );
    }

    /**
     * Update of the suggestSubmit which is specified in parameter
     * 
     * @param suggestSubmit
     *            The suggest submit
     * @param bUpdateIndex
     *            The update index
     * @param plugin
     *            The plugin
     */
    public static void update( SuggestSubmit suggestSubmit, boolean bUpdateIndex, Plugin plugin )
    {
        if ( bUpdateIndex )
        {
            SuggestSubmit suggestSubmitStored = findByPrimaryKey( suggestSubmit.getIdSuggestSubmit( ), plugin );

            // if state has changed
            if ( suggestSubmit.getSuggestSubmitState( ).getIdSuggestSubmitState( ) != suggestSubmitStored.getSuggestSubmitState( ).getIdSuggestSubmitState( ) )
            {
                String strIdSuggestSubmit = Integer.toString( suggestSubmit.getIdSuggestSubmit( ) );

                if ( suggestSubmit.getSuggestSubmitState( ).getIdSuggestSubmitState( ) == SuggestSubmit.STATE_PUBLISH )
                {
                    SuggestIndexerUtils.addIndexerAction( strIdSuggestSubmit, IndexerAction.TASK_CREATE );
                }
                else
                {
                    SuggestIndexerUtils.addIndexerAction( strIdSuggestSubmit, IndexerAction.TASK_DELETE );
                }
            }

            IndexationService.addIndexerAction( Integer.toString( suggestSubmit.getIdSuggestSubmit( ) ),
                    AppPropertiesService.getProperty( SuggestIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_MODIFY );
        }

        _dao.store( suggestSubmit, plugin );
    }

    /**
     * Remove the SuggestSubmit whose identifier is specified in parameter
     *
     * @param nIdSuggestSubmit
     *            The suggestSubmitId
     * @param plugin
     *            the Plugin
     */
    public static void remove( int nIdSuggestSubmit, Plugin plugin )
    {
        SubmitFilter filter = new SubmitFilter( );
        filter.setIdSuggestSubmit( nIdSuggestSubmit );

        List<Response> listResponses = ResponseHome.getResponseList( filter, plugin );

        for ( Response response : listResponses )
        {
            if ( response.getIdImageResource( ) != null )
            {
                ImageResourceHome.remove( response.getIdImageResource( ), plugin );
            }

            ResponseHome.remove( response.getIdResponse( ), plugin );
        }

        List<CommentSubmit> listComments = CommentSubmitService.getService( ).getCommentSubmitList( filter, plugin );

        for ( CommentSubmit comment : listComments )
        {
            CommentSubmitService.getService( ).remove( comment.getIdCommentSubmit( ), plugin );
        }

        // remove all reported associated to the suggest submit
        ReportedMessageHome.removeBySuggestSubmit( nIdSuggestSubmit, plugin );

        SuggestSubmit suggestSubmit = SuggestSubmitHome.findByPrimaryKey( nIdSuggestSubmit, plugin );

        if ( suggestSubmit.getSuggestSubmitState( ).getIdSuggestSubmitState( ) == SuggestSubmit.STATE_PUBLISH )
        {
            String strIdSuggestSubmit = Integer.toString( nIdSuggestSubmit );
            IndexationService.addIndexerAction( strIdSuggestSubmit + "_" + SuggestIndexer.SHORT_NAME,
                    AppPropertiesService.getProperty( SuggestIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_DELETE );

            SuggestIndexerUtils.addIndexerAction( strIdSuggestSubmit, IndexerAction.TASK_DELETE );
        }

        _dao.delete( nIdSuggestSubmit, plugin );
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a SuggestSubmit whose identifier is specified in parameter
     *
     * @param nKey
     *            The suggestSubmit primary key
     * @param bLoadCommentList
     *            true if the comment list must be get
     * @param plugin
     *            the Plugin
     * @return an instance of SuggestSubmit
     */
    public static SuggestSubmit findByPrimaryKey( int nKey, Plugin plugin )
    {
        SuggestSubmit suggestSubmit = _dao.load( nKey, plugin );

        if ( suggestSubmit != null )
        {
            if ( suggestSubmit.getSuggestSubmitType( ) != null )
            {
                suggestSubmit.setSuggestSubmitType( SuggestSubmitTypeHome.findByPrimaryKey( suggestSubmit.getSuggestSubmitType( ).getIdType( ), plugin ) );
            }

            if ( suggestSubmit.getCategory( ) != null )
            {
                suggestSubmit.setCategory( CategoryHome.findByPrimaryKey( suggestSubmit.getCategory( ).getIdCategory( ), plugin ) );
            }
        }

        return suggestSubmit;
    }

    /**
     * Load the data of all the suggestSubmit who verify the filter and returns them in a list
     * 
     * @param filter
     *            the filter
     * @param plugin
     *            the plugin
     * @return the list of suggestSubmit
     */
    public static List<SuggestSubmit> getSuggestSubmitList( SubmitFilter filter, Plugin plugin )
    {
        List<SuggestSubmit> listSuggestSubmit = _dao.selectListByFilter( filter, plugin );

        if ( listSuggestSubmit != null )
        {
            SubmitFilter submmitFilterComment = new SubmitFilter( );

            for ( SuggestSubmit suggestSubmit : listSuggestSubmit )
            {
                submmitFilterComment.setIdSuggestSubmit( suggestSubmit.getIdSuggestSubmit( ) );
                submmitFilterComment.setIdCommentSubmitState( CommentSubmit.STATE_ENABLE );
                suggestSubmit.setComments( CommentSubmitService.getService( ).getCommentSubmitList( submmitFilterComment, plugin ) );

                if ( suggestSubmit.getSuggestSubmitType( ) != null )
                {
                    suggestSubmit.setSuggestSubmitType( SuggestSubmitTypeHome.findByPrimaryKey( suggestSubmit.getSuggestSubmitType( ).getIdType( ), plugin ) );
                }

                if ( suggestSubmit.getCategory( ) != null )
                {
                    suggestSubmit.setCategory( CategoryHome.findByPrimaryKey( suggestSubmit.getCategory( ).getIdCategory( ), plugin ) );
                }
            }
        }

        return listSuggestSubmit;
    }

    /**
     * Load the id of all the suggestSubmit who verify the filter and returns them in a list
     * 
     * @param filter
     *            the filter
     * @param plugin
     *            the plugin
     * @return the list of suggestSubmit id
     */
    public static List<Integer> getSuggestSubmitListId( SubmitFilter filter, Plugin plugin )
    {
        return _dao.selectIdListByFilter( filter, plugin );
    }

    /**
     * Load the data of all the suggestSubmit who verify the filter and returns them in a list
     * 
     * @param filter
     *            the filter
     * @param plugin
     *            the plugin
     * @param nNumberMaxSuggestSubmit
     *            Max Number of Suggestsubmit return
     * @return the list of suggestSubmit
     */
    public static List<SuggestSubmit> getSuggestSubmitList( SubmitFilter filter, Plugin plugin, int nNumberMaxSuggestSubmit )
    {
        List<Integer> suggestSubmitListId = getSuggestSubmitListId( filter, plugin );
        List<SuggestSubmit> suggestSubmitList = new ArrayList<SuggestSubmit>( );
        SuggestSubmit suggestSubmit = null;
        Object [ ] suggestSubmitArrayId = suggestSubmitListId.toArray( );

        for ( int cpt = 0; cpt < suggestSubmitArrayId.length; cpt++ )
        {
            if ( cpt < nNumberMaxSuggestSubmit )
            {
                suggestSubmit = findByPrimaryKey( (Integer) suggestSubmitArrayId [cpt], plugin );

                if ( suggestSubmit != null )
                {
                    suggestSubmitList.add( suggestSubmit );
                }
            }
            else
            {
                break;
            }
        }

        return suggestSubmitList;
    }

    /**
     * Load the number of all the suggestSubmit who verify the filter
     * 
     * @param filter
     *            the filter
     * @param plugin
     *            the plugin
     * @return the list of suggestSubmit
     */
    public static int getCountSuggestSubmit( SubmitFilter filter, Plugin plugin )
    {
        return _dao.selectCountByFilter( filter, plugin );
    }

    /**
     * Update the number order of suggestSubmit
     * 
     * @param nIdSuggestSubmit
     *            the id of the suggestSubmit
     * @param nNewOrder
     *            the new number of order
     * @param plugin
     *            The Plugin object
     */
    public static void updateSuggestSubmitOrder( int nNewOrder, int nIdSuggestSubmit, Plugin plugin )
    {
        _dao.storeSuggestSubmitOrder( nNewOrder, nIdSuggestSubmit, plugin );
    }

    /**
     * Search the max order number of contacts for one list
     * 
     * @param nIdSuggest
     *            the Id of the Suggest
     * @return int the max order
     * @param plugin
     *            The Plugin object
     */
    public static int getMaxOrderList( int nIdSuggest, boolean bListPinned, Plugin plugin )
    {
        return _dao.maxOrderSuggestSubmit( nIdSuggest, bListPinned, plugin );
    }
}
