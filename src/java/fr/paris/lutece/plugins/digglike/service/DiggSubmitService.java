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
package fr.paris.lutece.plugins.digglike.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.transaction.annotation.Transactional;

import com.mysql.jdbc.PacketTooBigException;

import fr.paris.lutece.plugins.digglike.business.CommentSubmit;
import fr.paris.lutece.plugins.digglike.business.DiggSubmit;
import fr.paris.lutece.plugins.digglike.business.DiggSubmitHome;
import fr.paris.lutece.plugins.digglike.business.DiggSubmitStateHome;
import fr.paris.lutece.plugins.digglike.business.Response;
import fr.paris.lutece.plugins.digglike.business.ResponseHome;
import fr.paris.lutece.plugins.digglike.business.SubmitFilter;
import fr.paris.lutece.plugins.digglike.service.search.DigglikeIndexer;
import fr.paris.lutece.plugins.digglike.utils.DiggIndexerUtils;
import fr.paris.lutece.plugins.digglike.utils.DiggUtils;
import fr.paris.lutece.portal.business.indexeraction.IndexerAction;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;


public class DiggSubmitService implements IDiggSubmitService
{
    public static final String BEAN_SERVICE = "digglike.diggSubmitService";
    private static IDiggSubmitService _singleton;

    @Override
    @Transactional( "digglike.transactionManager" )
    public int create( DiggSubmit diggSubmit, Plugin plugin,Locale locale )
    {
    	
    	//update creation date
    	diggSubmit.setDateResponse( DiggUtils.getCurrentDate(  ) );
        
    	//update digg submit state
    	if ( diggSubmit.getDigg().isDisableNewDiggSubmit(  ) )
        {
            diggSubmit.setDiggSubmitState( DiggSubmitStateHome.findByNumero( DiggSubmit.STATE_WAITING_FOR_PUBLISH,
                    plugin ) );
        }
        else
        {
            diggSubmit.setDiggSubmitState( DiggSubmitStateHome.findByNumero( DiggSubmit.STATE_PUBLISH, plugin ) );
        }

    	int nIdDiggSubmit = DiggSubmitHome.create( diggSubmit, plugin );
        diggSubmit.setIdDiggSubmit( nIdDiggSubmit );
   
        
        
        if ( diggSubmit.getDiggSubmitState(  ).getIdDiggSubmitState(  ) == DiggSubmit.STATE_PUBLISH )
        {
            String strIdDiggSubmit = Integer.toString( nIdDiggSubmit );
            IndexationService.addIndexerAction( strIdDiggSubmit,
                AppPropertiesService.getProperty( DigglikeIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_CREATE );

            DiggIndexerUtils.addIndexerAction( strIdDiggSubmit, IndexerAction.TASK_CREATE );
        }

        //store response
        if ( diggSubmit.getResponses(  ) != null )
        {
            for ( Response response : diggSubmit.getResponses(  ) )
            {
                response.setDiggSubmit( diggSubmit );
                ResponseHome.create( response, plugin );
                if(response.getImage()!=null)
                {
                	try {
                			ResponseHome.createImage(response.getIdResponse(), response.getImage(), plugin);
					} catch (PacketTooBigException e) {
						
						AppLogService.error(e);
					}
                	
                }
            }
        }
        
        //update DiggSubmit 
        diggSubmit.setDiggSubmitValue( DiggUtils.getHtmlDiggSubmitValue( diggSubmit, locale ) );
        diggSubmit.setDiggSubmitValueShowInTheList( DiggUtils.getHtmlDiggSubmitValueShowInTheList( diggSubmit, locale ) );
        diggSubmit.setDiggSubmitTitle( DiggUtils.getDiggSubmitTitle( diggSubmit, locale ) );
        DiggSubmitHome.update( diggSubmit, plugin );

        return nIdDiggSubmit;
    	
    }

   

    @Override
    @Transactional( "digglike.transactionManager" )
    public void update( DiggSubmit diggSubmit, Plugin plugin )
    {
        DiggSubmitHome.update( diggSubmit, plugin );
    }

    @Override
    @Transactional( "digglike.transactionManager" )
    public void update( DiggSubmit diggSubmit, boolean bUpdateIndex, Plugin plugin )
    {
        DiggSubmitHome.update( diggSubmit, bUpdateIndex, plugin );
    }

    @Override
    @Transactional( "digglike.transactionManager" )
    public void remove( int nIdDiggSubmit, Plugin plugin )
    {
        DiggSubmit diggSubmit = DiggSubmitHome.findByPrimaryKey( nIdDiggSubmit, plugin );

        if ( diggSubmit != null )
        {
            int nIdDigg = diggSubmit.getDigg(  ).getIdDigg(  );
            //remove
            DiggSubmitHome.remove( nIdDiggSubmit, plugin );
            //update digg submit order
            updateDiggSubmitOrder( null, null, nIdDigg,diggSubmit.isPinned(), plugin );
        }
    }

    @Override
    @Transactional( "digglike.transactionManager" )
    public void updateDiggSubmitOrder( Integer nPositionElement, Integer nNewPositionElement, int nIdDigg,boolean bListPinned, Plugin plugin )
    {
        SubmitFilter filter = new SubmitFilter(  );
        filter.setIdDigg( nIdDigg );

        List<Integer> listSortByManually = new ArrayList<Integer>(  );
        listSortByManually.add( SubmitFilter.SORT_MANUALLY );
        filter.setSortBy( listSortByManually );
        //
        filter.setIdPinned(bListPinned?SubmitFilter.ID_TRUE:SubmitFilter.ID_FALSE);
        
        List<Integer> listIdDiggDubmit = getDiggSubmitListId( filter, plugin );

        if ( ( listIdDiggDubmit != null ) && ( listIdDiggDubmit.size(  ) > 0 ) )
        {
            if ( ( nPositionElement != null ) && ( nNewPositionElement != null ) &&
                    ( nPositionElement != nNewPositionElement ) )
            {
                if ( ( ( nPositionElement > 0 ) && ( nPositionElement <= ( listIdDiggDubmit.size(  ) + 1 ) ) ) &&
                        ( ( nNewPositionElement > 0 ) && ( nNewPositionElement <= ( listIdDiggDubmit.size(  ) + 1 ) ) ) )
                {
                    DiggUtils.moveElement( nPositionElement, nNewPositionElement, (ArrayList<Integer>) listIdDiggDubmit );
                }
            }

            int nNewOrder = 1;

            //update all Digg submit
            for ( Integer nIdDiggSubmit : listIdDiggDubmit )
            {
                DiggSubmitHome.updateDiggSubmitOrder( nNewOrder++, nIdDiggSubmit, plugin );
            }
        }
    }

    @Override
    public DiggSubmit findByPrimaryKey( int nKey, boolean bLoadCommentList, Plugin plugin )
    {
        DiggSubmit diggSubmit = DiggSubmitHome.findByPrimaryKey( nKey, plugin );

        if (  diggSubmit != null  && !diggSubmit.isDisableComment() && bLoadCommentList )
        {
            SubmitFilter submmitFilterComment = new SubmitFilter(  );
            submmitFilterComment.setIdDiggSubmit( diggSubmit.getIdDiggSubmit(  ) );
            submmitFilterComment.setIdCommentSubmitState( CommentSubmit.STATE_ENABLE );
            diggSubmit.setComments( CommentSubmitService.getService(  )
                                                        .getCommentSubmitList( submmitFilterComment, plugin ) );
         
        }

        return diggSubmit;
    }

    @Override
    public int findNextIdDiggSubmitInTheList( int nIdCurrentDiggSubmit, SubmitFilter filter, Plugin plugin )
    {
        List<Integer> diggSubmitListId = getDiggSubmitListId( filter, plugin );
        Object[] diggSubmitArrayId = diggSubmitListId.toArray(  );
        int nIdDiggSubmitNext = -1;

        for ( int cpt = 0; cpt < diggSubmitArrayId.length; cpt++ )
        {
            if ( (Integer) diggSubmitArrayId[cpt] == nIdCurrentDiggSubmit )
            {
                if ( cpt < ( diggSubmitArrayId.length - 1 ) )
                {
                    nIdDiggSubmitNext = (Integer) diggSubmitArrayId[cpt + 1];
                }

                break;
            }
        }

        return nIdDiggSubmitNext;
    }

    @Override
    public int findPrevIdDiggSubmitInTheList( int nIdCurrentDiggSubmit, SubmitFilter filter, Plugin plugin )
    {
        List<Integer> diggSubmitListId = getDiggSubmitListId( filter, plugin );
        Object[] diggSubmitArrayId = diggSubmitListId.toArray(  );
        int nIdDiggSubmitPrev = -1;

        for ( int cpt = 0; cpt < diggSubmitArrayId.length; cpt++ )
        {
            if ( (Integer) diggSubmitArrayId[cpt] == nIdCurrentDiggSubmit )
            {
                if ( cpt != 0 )
                {
                    nIdDiggSubmitPrev = (Integer) diggSubmitArrayId[cpt - 1];
                }

                break;
            }
        }

        return nIdDiggSubmitPrev;
    }

    @Override
    public int getCountDiggSubmit( SubmitFilter filter, Plugin plugin )
    {
        return DiggSubmitHome.getCountDiggSubmit( filter, plugin );
    }



    @Override
    public List<DiggSubmit> getDiggSubmitList( SubmitFilter filter, Plugin plugin )
    {
    	if ( !filter.containsSortBy(  ) )
        {
            //use default sort
            DiggUtils.initSubmitFilterBySort( filter, DiggUtils.CONSTANT_ID_NULL );
        }
    	else if(filter.containsSortBy(SubmitFilter.SORT_BY_PINNED_FIRST))
    		
    	{
    		SubmitFilter filterPinned=DiggUtils.createPinnedFilter(filter);
    		List<DiggSubmit > listDiggSubmitPinned=DiggSubmitHome.getDiggSubmitList( filterPinned, plugin );
    		filter.setIdPinned(SubmitFilter.ID_FALSE);
    		listDiggSubmitPinned.addAll(DiggSubmitHome.getDiggSubmitList( filter, plugin ));
    		filter.setIdPinned(SubmitFilter.ALL_INT);
    		return listDiggSubmitPinned;
    	}
    	
    

    		return DiggSubmitHome.getDiggSubmitList( filter, plugin );
    	
    }

    @Override
    public List<DiggSubmit> getDiggSubmitList( SubmitFilter filter, Plugin plugin, int nNumberMaxDiggSubmit )
    {
        if ( !filter.containsSortBy(  ) )
        {
            //use default sort
            DiggUtils.initSubmitFilterBySort( filter, DiggUtils.CONSTANT_ID_NULL );
        }
        
        
        else if(filter.containsSortBy(SubmitFilter.SORT_BY_PINNED_FIRST))
    		
    	{
	    		SubmitFilter filterPinned=DiggUtils.createPinnedFilter(filter);
	    		List<DiggSubmit > listDiggSubmitPinned=DiggSubmitHome.getDiggSubmitList( filterPinned, plugin, nNumberMaxDiggSubmit );
	    		filter.setIdPinned(SubmitFilter.ID_FALSE);
	    		listDiggSubmitPinned.addAll(DiggSubmitHome.getDiggSubmitList( filterPinned, plugin, nNumberMaxDiggSubmit- listDiggSubmitPinned.size()));
	    		filter.setIdPinned(SubmitFilter.ALL_INT);
	    		return listDiggSubmitPinned;
		 }
		

			return DiggSubmitHome.getDiggSubmitList( filter, plugin, nNumberMaxDiggSubmit );
		
    }

    @Override
    public List<Integer> getDiggSubmitListId( SubmitFilter filter, Plugin plugin )
    {
       
    	
    	if ( !filter.containsSortBy(  ) )
        {
            //use default sort
            DiggUtils.initSubmitFilterBySort( filter, DiggUtils.CONSTANT_ID_NULL );
        }
    	else if(filter.containsSortBy(SubmitFilter.SORT_BY_PINNED_FIRST))
     	{
    		
    	
    		SubmitFilter filterPinned=DiggUtils.createPinnedFilter(filter);
    		List<Integer> listDiggSubmitPinned=DiggSubmitHome.getDiggSubmitListId( filterPinned, plugin );
    		filter.setIdPinned(SubmitFilter.ID_FALSE);
    		listDiggSubmitPinned.addAll(DiggSubmitHome.getDiggSubmitListId( filter, plugin ));
    		filter.setIdPinned(SubmitFilter.ALL_INT);
    		return listDiggSubmitPinned;
    	}
    	
    	

        return DiggSubmitHome.getDiggSubmitListId( filter, plugin );
    }

//    @Override
//    public List<DiggSubmit> getDiggSubmitListWithNumberComment( SubmitFilter filter, Plugin plugin )
//    {
//        if ( !filter.containsSortBy(  ) )
//        {
//            //use default sort
//            DiggUtils.initSubmitFilterBySort( filter, DiggUtils.CONSTANT_ID_NULL );
//        }
//
//        return DiggSubmitHome.getDiggSubmitListWithNumberComment( filter, plugin );
//    }

 

    @Override
    public int getMaxOrderList( int nIdDigg,boolean bListPinned, Plugin plugin )
    {
        // TODO Auto-generated method stub
        return DiggSubmitHome.getMaxOrderList( nIdDigg,bListPinned, plugin );
    }

    /**
     * Returns the instance of the singleton
     *
     * @return The instance of the singleton
     */
    public static IDiggSubmitService getService(  )
    {
        if ( _singleton == null )
        {
            _singleton = SpringContextService.getBean( BEAN_SERVICE );
        }

        return _singleton;
    }
}
