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

import fr.paris.lutece.plugins.suggest.business.CommentSubmit;
import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestHome;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitHome;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitStateHome;
import fr.paris.lutece.plugins.suggest.business.EntryFilter;
import fr.paris.lutece.plugins.suggest.business.EntryHome;
import fr.paris.lutece.plugins.suggest.business.IEntry;
import fr.paris.lutece.plugins.suggest.business.ImageResourceHome;
import fr.paris.lutece.plugins.suggest.business.Response;
import fr.paris.lutece.plugins.suggest.business.ResponseHome;
import fr.paris.lutece.plugins.suggest.business.SubmitFilter;
import fr.paris.lutece.plugins.suggest.service.search.SuggestIndexer;
import fr.paris.lutece.plugins.suggest.utils.SuggestIndexerUtils;
import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.portal.business.indexeraction.IndexerAction;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;


public class SuggestSubmitService implements ISuggestSubmitService
{
    public static final String BEAN_SERVICE = "suggest.suggestSubmitService";
    private static ISuggestSubmitService _singleton;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "suggest.transactionManager" )
    public int create( SuggestSubmit suggestSubmit, Plugin plugin, Locale locale )
    {
        //update creation date
        suggestSubmit.setDateResponse( SuggestUtils.getCurrentDate(  ) );

        //update suggest submit state
        if ( suggestSubmit.getSuggest(  ).isDisableNewSuggestSubmit(  ) )
        {
            suggestSubmit.setSuggestSubmitState( SuggestSubmitStateHome.findByNumero( SuggestSubmit.STATE_WAITING_FOR_PUBLISH,
                    plugin ) );
        }
        else
        {
            suggestSubmit.setSuggestSubmitState( SuggestSubmitStateHome.findByNumero( SuggestSubmit.STATE_PUBLISH, plugin ) );
        }

        int nIdSuggestSubmit = SuggestSubmitHome.create( suggestSubmit, plugin );
        suggestSubmit.setIdSuggestSubmit( nIdSuggestSubmit );

        if ( suggestSubmit.getSuggestSubmitState(  ).getIdSuggestSubmitState(  ) == SuggestSubmit.STATE_PUBLISH )
        {
            String strIdSuggestSubmit = Integer.toString( nIdSuggestSubmit );
            IndexationService.addIndexerAction( strIdSuggestSubmit,
                AppPropertiesService.getProperty( SuggestIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_CREATE );

            SuggestIndexerUtils.addIndexerAction( strIdSuggestSubmit, IndexerAction.TASK_CREATE );
        }

        //store response
        if ( suggestSubmit.getResponses(  ) != null )
        {
            for ( Response response : suggestSubmit.getResponses(  ) )
            {
                if ( response.getImage(  ) != null )
                {
                    response.setIdImageResource( ImageResourceHome.create( response.getImage(  ), plugin ) );
                    //update Id Image ressource associate to the suggest
                    suggestSubmit.setIdImageResource( response.getIdImageResource(  ) );
                }

                response.setSuggestSubmit( suggestSubmit );
                ResponseHome.create( response, plugin );
            }
        }

        //update SuggestSubmit 
        suggestSubmit.setSuggestSubmitValue( SuggestUtils.getHtmlSuggestSubmitValue( suggestSubmit, locale ) );
        suggestSubmit.setSuggestSubmitValueShowInTheList( SuggestUtils.getHtmlSuggestSubmitValueShowInTheList( suggestSubmit, locale ) );
        suggestSubmit.setSuggestSubmitTitle( SuggestUtils.getSuggestSubmitTitle( suggestSubmit, locale ) );
        SuggestSubmitHome.update( suggestSubmit, plugin );

        return nIdSuggestSubmit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "suggest.transactionManager" )
    public void update( SuggestSubmit suggestSubmit, Plugin plugin )
    {
        SuggestSubmitHome.update( suggestSubmit, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "suggest.transactionManager" )
    public void update( SuggestSubmit suggestSubmit, boolean bUpdateIndex, Plugin plugin )
    {
        SuggestSubmitHome.update( suggestSubmit, bUpdateIndex, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "suggest.transactionManager" )
    public void remove( int nIdSuggestSubmit, Plugin plugin )
    {
        SuggestSubmit suggestSubmit = SuggestSubmitHome.findByPrimaryKey( nIdSuggestSubmit, plugin );

        if ( suggestSubmit != null )
        {
            int nIdSuggest = suggestSubmit.getSuggest(  ).getIdSuggest(  );
            //remove
            SuggestSubmitHome.remove( nIdSuggestSubmit, plugin );
            //update suggest submit order
            updateSuggestSubmitOrder( null, null, nIdSuggest, suggestSubmit.isPinned(  ), plugin );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "suggest.transactionManager" )
    public void updateSuggestSubmitOrder( Integer nPositionElement, Integer nNewPositionElement, int nIdSuggest,
        boolean bListPinned, Plugin plugin )
    {
        SubmitFilter filter = new SubmitFilter(  );
        filter.setIdSuggest( nIdSuggest );

        List<Integer> listSortByManually = new ArrayList<Integer>(  );
        listSortByManually.add( SubmitFilter.SORT_MANUALLY );
        filter.setSortBy( listSortByManually );
        //
        filter.setIdPinned( bListPinned ? SubmitFilter.ID_TRUE : SubmitFilter.ID_FALSE );

        List<Integer> listIdSuggestDubmit = getSuggestSubmitListId( filter, plugin );

        if ( ( listIdSuggestDubmit != null ) && ( listIdSuggestDubmit.size(  ) > 0 ) )
        {
            if ( ( nPositionElement != null ) && ( nNewPositionElement != null )
                    && ( !nPositionElement.equals( nNewPositionElement ) ) )
            {
                if ( ( ( nPositionElement > 0 ) && ( nPositionElement <= ( listIdSuggestDubmit.size(  ) + 1 ) ) ) &&
                        ( ( nNewPositionElement > 0 ) && ( nNewPositionElement <= ( listIdSuggestDubmit.size(  ) + 1 ) ) ) )
                {
                    SuggestUtils.moveElement( nPositionElement, nNewPositionElement, (ArrayList<Integer>) listIdSuggestDubmit );
                }
            }

            int nNewOrder = 1;

            //update all Suggest submit
            for ( Integer nIdSuggestSubmit : listIdSuggestDubmit )
            {
                SuggestSubmitHome.updateSuggestSubmitOrder( nNewOrder++, nIdSuggestSubmit, plugin );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SuggestSubmit findByPrimaryKey( int nKey, boolean bLoadCommentList, Plugin plugin )
    {
        return findByPrimaryKey( nKey, bLoadCommentList, null, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SuggestSubmit findByPrimaryKey( int nKey, boolean bLoadCommentList, Integer numberMaxCommentLoad, Plugin plugin )
    {
        SuggestSubmit suggestSubmit = SuggestSubmitHome.findByPrimaryKey( nKey, plugin );

        if ( ( suggestSubmit != null ) && !suggestSubmit.isDisableComment(  ) && bLoadCommentList )
        {
            SubmitFilter submmitFilterComment = new SubmitFilter(  );
            submmitFilterComment.setIdSuggestSubmit( suggestSubmit.getIdSuggestSubmit(  ) );
            submmitFilterComment.setIdCommentSubmitState( CommentSubmit.STATE_ENABLE );
            suggestSubmit.setComments( CommentSubmitService.getService(  )
                                                        .getCommentSubmitList( submmitFilterComment,
                    numberMaxCommentLoad, plugin ) );
        }

        return suggestSubmit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SuggestSubmit findByPrimaryKey( int nKey, boolean bLoadCommentList, boolean bLoadResponseList, Plugin plugin )
    {
        SuggestSubmit suggestSubmit = findByPrimaryKey( nKey, bLoadCommentList, plugin );

        if ( ( suggestSubmit != null ) && bLoadResponseList )
        {
            SubmitFilter submmitFilter = new SubmitFilter(  );
            submmitFilter.setIdSuggestSubmit( suggestSubmit.getIdSuggestSubmit(  ) );
            suggestSubmit.setResponses( ResponseHome.getResponseList( submmitFilter, plugin ) );
        }

        return suggestSubmit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findNextIdSuggestSubmitInTheList( int nIdCurrentSuggestSubmit, SubmitFilter filter, Plugin plugin )
    {
        List<Integer> suggestSubmitListId = getSuggestSubmitListId( filter, plugin );
        Object[] suggestSubmitArrayId = suggestSubmitListId.toArray(  );
        int nIdSuggestSubmitNext = -1;

        for ( int cpt = 0; cpt < suggestSubmitArrayId.length; cpt++ )
        {
            if ( (Integer) suggestSubmitArrayId[cpt] == nIdCurrentSuggestSubmit )
            {
                if ( cpt < ( suggestSubmitArrayId.length - 1 ) )
                {
                    nIdSuggestSubmitNext = (Integer) suggestSubmitArrayId[cpt + 1];
                }

                break;
            }
        }

        return nIdSuggestSubmitNext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int findPrevIdSuggestSubmitInTheList( int nIdCurrentSuggestSubmit, SubmitFilter filter, Plugin plugin )
    {
        List<Integer> suggestSubmitListId = getSuggestSubmitListId( filter, plugin );
        Object[] suggestSubmitArrayId = suggestSubmitListId.toArray(  );
        int nIdSuggestSubmitPrev = -1;

        for ( int cpt = 0; cpt < suggestSubmitArrayId.length; cpt++ )
        {
            if ( (Integer) suggestSubmitArrayId[cpt] == nIdCurrentSuggestSubmit )
            {
                if ( cpt != 0 )
                {
                    nIdSuggestSubmitPrev = (Integer) suggestSubmitArrayId[cpt - 1];
                }

                break;
            }
        }

        return nIdSuggestSubmitPrev;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCountSuggestSubmit( SubmitFilter filter, Plugin plugin )
    {
        return SuggestSubmitHome.getCountSuggestSubmit( filter, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SuggestSubmit> getSuggestSubmitList( SubmitFilter filter, Plugin plugin )
    {
        if ( !filter.containsSortBy(  ) )
        {
            //use default sort
            SuggestUtils.initSubmitFilterBySort( filter, SuggestUtils.CONSTANT_ID_NULL );
        }
        else if ( filter.containsSortBy( SubmitFilter.SORT_BY_PINNED_FIRST ) )
        {
            SubmitFilter filterPinned = SuggestUtils.createPinnedFilter( filter );
            List<SuggestSubmit> listSuggestSubmitPinned = SuggestSubmitHome.getSuggestSubmitList( filterPinned, plugin );
            filter.setIdPinned( SubmitFilter.ID_FALSE );
            listSuggestSubmitPinned.addAll( SuggestSubmitHome.getSuggestSubmitList( filter, plugin ) );
            filter.setIdPinned( SubmitFilter.ALL_INT );

            return listSuggestSubmitPinned;
        }

        return SuggestSubmitHome.getSuggestSubmitList( filter, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SuggestSubmit> getSuggestSubmitList( SubmitFilter filter, Plugin plugin, int nNumberMaxSuggestSubmit )
    {
        if ( !filter.containsSortBy(  ) )
        {
            //use default sort
            SuggestUtils.initSubmitFilterBySort( filter, SuggestUtils.CONSTANT_ID_NULL );
        }

        else if ( filter.containsSortBy( SubmitFilter.SORT_BY_PINNED_FIRST ) )
        {
            SubmitFilter filterPinned = SuggestUtils.createPinnedFilter( filter );
            List<SuggestSubmit> listSuggestSubmitPinned = SuggestSubmitHome.getSuggestSubmitList( filterPinned, plugin,
                    nNumberMaxSuggestSubmit );
            filter.setIdPinned( SubmitFilter.ID_FALSE );
            listSuggestSubmitPinned.addAll( SuggestSubmitHome.getSuggestSubmitList( filterPinned, plugin,
                    nNumberMaxSuggestSubmit - listSuggestSubmitPinned.size(  ) ) );
            filter.setIdPinned( SubmitFilter.ALL_INT );

            return listSuggestSubmitPinned;
        }

        return SuggestSubmitHome.getSuggestSubmitList( filter, plugin, nNumberMaxSuggestSubmit );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getSuggestSubmitListId( SubmitFilter filter, Plugin plugin )
    {
        if ( !filter.containsSortBy(  ) )
        {
            //use default sort
            SuggestUtils.initSubmitFilterBySort( filter, SuggestUtils.CONSTANT_ID_NULL );
        }
        else if ( filter.containsSortBy( SubmitFilter.SORT_BY_PINNED_FIRST ) )
        {
            SubmitFilter filterPinned = SuggestUtils.createPinnedFilter( filter );
            List<Integer> listSuggestSubmitPinned = SuggestSubmitHome.getSuggestSubmitListId( filterPinned, plugin );
            filter.setIdPinned( SubmitFilter.ID_FALSE );
            listSuggestSubmitPinned.addAll( SuggestSubmitHome.getSuggestSubmitListId( filter, plugin ) );
            filter.setIdPinned( SubmitFilter.ALL_INT );

            return listSuggestSubmitPinned;
        }

        return SuggestSubmitHome.getSuggestSubmitListId( filter, plugin );
    }

    //    @Override
    //    public List<SuggestSubmit> getSuggestSubmitListWithNumberComment( SubmitFilter filter, Plugin plugin )
    //    {
    //        if ( !filter.containsSortBy(  ) )
    //        {
    //            //use default sort
    //            SuggestUtils.initSubmitFilterBySort( filter, SuggestUtils.CONSTANT_ID_NULL );
    //        }
    //
    //        return SuggestSubmitHome.getSuggestSubmitListWithNumberComment( filter, plugin );
    //    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxOrderList( int nIdSuggest, boolean bListPinned, Plugin plugin )
    {
        // TODO Auto-generated method stub
        return SuggestSubmitHome.getMaxOrderList( nIdSuggest, bListPinned, plugin );
    }

    /**
     * Returns the instance of the singleton
     *
     * @return The instance of the singleton
     */
    public static ISuggestSubmitService getService(  )
    {
        if ( _singleton == null )
        {
            _singleton = SpringContextService.getBean( BEAN_SERVICE );
        }

        return _singleton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAllDisplayOfSuggestSubmit( Integer nIdSuggest, Plugin plugin, Locale locale )
    {
        Suggest suggest = SuggestHome.findByPrimaryKey( nIdSuggest, plugin );
        HashMap<Integer, IEntry> mapEntry = new HashMap<Integer, IEntry>(  );
        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setIdSuggest( suggest.getIdSuggest(  ) );

        for ( IEntry entry : EntryHome.getEntryList( entryFilter, plugin ) )
        {
            mapEntry.put( entry.getIdEntry(  ), EntryHome.findByPrimaryKey( entry.getIdEntry(  ), plugin ) );
        }

        SubmitFilter filter = new SubmitFilter(  );
        filter.setIdSuggest( nIdSuggest );

        List<Integer> listIdSuggestSubmit = getSuggestSubmitListId( filter, plugin );

        for ( Integer nIdSuggestSubmit : listIdSuggestSubmit )
        {
            updateDisplaySuggestSubmit( nIdSuggestSubmit, plugin, locale, suggest, mapEntry );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateDisplaySuggestSubmit( Integer nIdSuggestSubmit, Plugin plugin, Locale locale, Suggest suggest,
        Map<Integer, IEntry> mapEntry )
    {
        SuggestSubmit suggestSubmit = findByPrimaryKey( nIdSuggestSubmit, false, plugin );
        suggestSubmit.setSuggest( suggest );

        SubmitFilter filter = new SubmitFilter(  );
        filter.setIdSuggestSubmit( nIdSuggestSubmit );

        // add responses
        List<Response> listResponses = ResponseHome.getResponseList( filter, plugin );

        for ( Response response : listResponses )
        {
            response.setEntry( mapEntry.get( response.getEntry(  ).getIdEntry(  ) ) );
        }

        suggestSubmit.setResponses( listResponses );
        // update Number of comment
        suggestSubmit.setNumberComment( CommentSubmitService.getService(  ).getCountCommentSubmit( filter, plugin ) );
        // update Number of Comment Enable
        filter.setIdCommentSubmitState( CommentSubmit.STATE_ENABLE );
        suggestSubmit.setNumberCommentEnable( CommentSubmitService.getService(  ).getCountCommentSubmit( filter, plugin ) );
        // update SuggestSubmitValue
        suggestSubmit.setSuggestSubmitValue( SuggestUtils.getHtmlSuggestSubmitValue( suggestSubmit, locale ) );
        // update SuggestSubmitValue show in the list
        suggestSubmit.setSuggestSubmitValueShowInTheList( SuggestUtils.getHtmlSuggestSubmitValueShowInTheList( suggestSubmit, locale ) );
        // update SuggestSubmit title
        suggestSubmit.setSuggestSubmitTitle( SuggestUtils.getSuggestSubmitTitle( suggestSubmit, locale ) );
        //update SuggestSubmit
        update( suggestSubmit, plugin );
    }
}
