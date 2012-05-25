/*
 * Copyright (c) 2002-2012, Mairie de Paris
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

import java.util.ArrayList;
import java.util.List;

import fr.paris.lutece.plugins.digglike.service.search.DigglikeIndexer;
import fr.paris.lutece.plugins.digglike.utils.DiggIndexerUtils;
import fr.paris.lutece.portal.business.indexeraction.IndexerAction;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;


/**
 *class FormSubmitHome
 */
public final class DiggSubmitHome
{
    // Static variable pointed at the DAO instance
    private static IDiggSubmitDAO _dao = (IDiggSubmitDAO) SpringContextService.getPluginBean( "digglike",
            "digglike.diggSubmitDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private DiggSubmitHome(  )
    {
    }

    /**
     * Creation of an instance of diggSubmit
     *
     * @param diggSubmit The instance of the diggSubmit which contains the informations to store
     * @param plugin the Plugin
     * @return the id of the new digg submit
     *
     */
    public static int create( DiggSubmit diggSubmit, Plugin plugin )
    {
        int nIdDiggSubmit = _dao.insert( diggSubmit, plugin );

        if ( diggSubmit.getDiggSubmitState(  ).getIdDiggSubmitState(  ) == DiggSubmit.STATE_PUBLISH )
        {
        	String strIdDiggSubmit = Integer.toString( nIdDiggSubmit );
            IndexationService.addIndexerAction( strIdDiggSubmit,
                AppPropertiesService.getProperty( DigglikeIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_CREATE );
            
            DiggIndexerUtils.addIndexerAction( strIdDiggSubmit, IndexerAction.TASK_CREATE );
        }

        return nIdDiggSubmit;
    }

    /**
     * Return the image resource for the specified image id
     * @param nImageId The identifier of image object
     * @param plugin the plugin
     * @return ImageResource
     */
    public static ImageResource getImageResource( int nImageId, Plugin plugin )
    {
        return _dao.loadImageResource( nImageId, plugin );
    }

    /**
     * Update of the diggSubmit which is specified in parameter
     *
     * @param diggSubmit The instance of the diggSubmit which contains the informations to update
     * @param plugin the Plugin
     *
     */
    public static void update( DiggSubmit diggSubmit, Plugin plugin )
    {	
    	update(diggSubmit, true, plugin);
    }
    
    /**
     * Update of the diggSubmit which is specified in parameter
     * @param diggSubmit
     * @param bUpdateIndex
     * @param plugin
     */
    
    public static void update( DiggSubmit diggSubmit,boolean bUpdateIndex,Plugin plugin )
    {
    	
    	if(bUpdateIndex)
    	{
	    	DiggSubmit diggSubmitStored = findByPrimaryKey( diggSubmit.getIdDiggSubmit(  ), plugin );
	    	
	    	//if state has changed
	        if ( diggSubmit.getDiggSubmitState(  ).getIdDiggSubmitState(  ) != diggSubmitStored.getDiggSubmitState(  )
	                                                                                               .getIdDiggSubmitState(  ) )
	        {
	        	String strIdDiggSubmit = Integer.toString( diggSubmit.getIdDiggSubmit(  ) );
	            if ( diggSubmit.getDiggSubmitState(  ).getIdDiggSubmitState(  ) == DiggSubmit.STATE_PUBLISH )
	            {
	                DiggIndexerUtils.addIndexerAction( strIdDiggSubmit, IndexerAction.TASK_CREATE );
	            }
	            else
	            {
	                DiggIndexerUtils.addIndexerAction( strIdDiggSubmit, IndexerAction.TASK_DELETE );
	            }
	        }
	        
	        IndexationService.addIndexerAction( Integer.toString(diggSubmit.getIdDiggSubmit())
	                        , AppPropertiesService.getProperty( DigglikeIndexer.PROPERTY_INDEXER_NAME)
	                        , IndexerAction.TASK_MODIFY );
    	}
        _dao.store( diggSubmit, plugin );
    	
    }
    

    /**
     * Remove the DiggSubmit whose identifier is specified in parameter
     *
     * @param nIdDiggSubmit The diggSubmitId
     * @param plugin the Plugin
     */
    public static void remove( int nIdDiggSubmit, Plugin plugin )
    {
        SubmitFilter filter = new SubmitFilter(  );
        filter.setIdDiggSubmit( nIdDiggSubmit );

        List<Response> listResponses = ResponseHome.getResponseList( filter, plugin );

        for ( Response response : listResponses )
        {
            ResponseHome.remove( response.getIdResponse(  ), plugin );
        }

        List<CommentSubmit> listComments = CommentSubmitHome.getCommentSubmitList( filter, plugin );

        for ( CommentSubmit comment : listComments )
        {
            CommentSubmitHome.remove( comment.getIdCommentSubmit(  ), plugin );
        }

        List<TagSubmit> listTags = TagSubmitHome.getTagSubmitList( filter, plugin );

        for ( TagSubmit tag : listTags )
        {
            TagSubmitHome.remove( tag.getIdTagSubmit(  ), plugin );
        }

        DiggSubmit digg = DiggSubmitHome.findByPrimaryKey( nIdDiggSubmit, plugin );

        if ( digg.getDiggSubmitState(  ).getIdDiggSubmitState(  ) == DiggSubmit.STATE_PUBLISH )
        {
        	String strIdDiggSubmit = Integer.toString( nIdDiggSubmit );
            IndexationService.addIndexerAction( strIdDiggSubmit + "_" + DigglikeIndexer.SHORT_NAME,
                AppPropertiesService.getProperty( DigglikeIndexer.PROPERTY_INDEXER_NAME ), IndexerAction.TASK_DELETE );
            
            DiggIndexerUtils.addIndexerAction( strIdDiggSubmit, IndexerAction.TASK_DELETE );
        }

        _dao.delete( nIdDiggSubmit, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a DiggSubmit whose identifier is specified in parameter
     *
     * @param nKey The diggSubmit primary key
     * @param plugin the Plugin
     * @return an instance of DiggSubmit
     */
    public static DiggSubmit findByPrimaryKey( int nKey, Plugin plugin )
    {
        DiggSubmit diggSubmit=_dao.load( nKey, plugin );
        
        if(diggSubmit != null)
        {
	        SubmitFilter submmitFilterComment = new SubmitFilter(  );
	        submmitFilterComment.setIdDiggSubmit( diggSubmit.getIdDiggSubmit(  ) );
	        submmitFilterComment.setIdCommentSubmitState( CommentSubmit.STATE_ENABLE );
	        diggSubmit.setComments( CommentSubmitHome.getCommentSubmitList( submmitFilterComment, plugin ) );
        }
        return diggSubmit;
      }

    /**
     * Load the data of all the diggSubmit who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of diggSubmit
     */
    public static List<DiggSubmit> getDiggSubmitList( SubmitFilter filter, Plugin plugin )
    {
       
    	List<DiggSubmit> listDiggSubmit=_dao.selectListByFilter( filter, plugin );
    	
    	if( listDiggSubmit !=null )
    	{
    	 
    		SubmitFilter submmitFilterComment = new SubmitFilter(  );
    		for(DiggSubmit diggSubmit:listDiggSubmit)
    		{
    			submmitFilterComment.setIdDiggSubmit( diggSubmit.getIdDiggSubmit(  ) );
    			submmitFilterComment.setIdCommentSubmitState( CommentSubmit.STATE_ENABLE );
    			diggSubmit.setComments( CommentSubmitHome.getCommentSubmitList( submmitFilterComment, plugin ) );
    		}
	    }
    	
    	return listDiggSubmit;
    }

    /**
     * Load the data of all the diggSubmit with the number of comment by digg submit  who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of diggSubmit
     */
    public static List<DiggSubmit> getDiggSubmitListWithNumberComment( SubmitFilter filter, Plugin plugin )
    {
        List<DiggSubmit> listDiggSubmit = _dao.selectListByFilter( filter, plugin );

        if ( listDiggSubmit != null )
        {
            for ( DiggSubmit diggSubmit : listDiggSubmit )
            {
                filter.setIdDiggSubmit( diggSubmit.getIdDiggSubmit(  ) );
                diggSubmit.setNumberComment( CommentSubmitHome.getCountCommentSubmit( filter, plugin ) );
            }
        }

        return listDiggSubmit;
    }

    /**
     * Load the id of all the diggSubmit who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of diggSubmit id
     */
    public static List<Integer> getDiggSubmitListId( SubmitFilter filter, Plugin plugin )
    {
        return _dao.selectIdListByFilter( filter, plugin );
    }

    /**
     * Load the data of all the diggSubmit who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @param nNumberMaxDiggSubmit Max Number of Diggsubmit return
     * @return  the list of diggSubmit
     */
    public static List<DiggSubmit> getDiggSubmitList( SubmitFilter filter, Plugin plugin, int nNumberMaxDiggSubmit )
    {
        List<Integer> diggSubmitListId = getDiggSubmitListId( filter, plugin );
        List<DiggSubmit> diggSubmitList = new ArrayList<DiggSubmit>(  );
        DiggSubmit diggSubmit = null;
        Object[] diggSubmitArrayId = diggSubmitListId.toArray(  );

        for ( int cpt = 0; cpt < diggSubmitArrayId.length; cpt++ )
        {
            if ( cpt < nNumberMaxDiggSubmit )
            {
                diggSubmit = findByPrimaryKey( (Integer) diggSubmitArrayId[cpt], plugin );

                if ( diggSubmit != null )
                {
                    diggSubmitList.add( diggSubmit );
                }
            }
            else
            {
                break;
            }
        }

        return diggSubmitList;
    }

    /**
     * Load the number of all the diggSubmit who verify the filter
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of diggSubmit
     */
    public static int getCountDiggSubmit( SubmitFilter filter, Plugin plugin )
    {
        return _dao.selectCountByFilter( filter, plugin );
    }

    /**
     * return the id of the next digg submit in the list
     *  @param nIdCurrentDiggSubmit the id of the current digg submit
     * @param filter the filter
     * @param plugin the plugin
     * @return the id of the next digg submit in the list
     */
    public static int findNextIdDiggSubmitInTheList( int nIdCurrentDiggSubmit, SubmitFilter filter, Plugin plugin )
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

    /**
     * return the id of the prev digg submit in the list
     *  @param nIdCurrentDiggSubmit the id of the current digg submit
     * @param filter the filter
     * @param plugin the plugin
     * @return the id of the prev digg submit in the list
     */
    public static int findPrevIdDiggSubmitInTheList( int nIdCurrentDiggSubmit, SubmitFilter filter, Plugin plugin )
    {
        List<Integer> diggSubmitListId = getDiggSubmitListId( filter, plugin );
        Object[] diggSubmitArrayId = diggSubmitListId.toArray(  );
        int nIdDiggSubmitPrev = -1;

        for ( int cpt = 0; cpt < diggSubmitArrayId.length; cpt++ )
        {
            if ( diggSubmitArrayId[cpt] == (Integer) nIdCurrentDiggSubmit )
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

    /**
     * Creation of an image
     * @param nIdDiggSubmit the id of the diggSubmit
     * @param image the image to add to the db
     * @param plugin the Plugin
     * @return the id of the new digg submit
     * @throws com.mysql.jdbc.PacketTooBigException if the image is too big
     *
     */
    public static int createImage( int nIdDiggSubmit, ImageResource image, Plugin plugin )
        throws com.mysql.jdbc.PacketTooBigException
    {
        return _dao.insertImageResource( nIdDiggSubmit, image, plugin );
    }

    /**
     * Search the order number of diggSubmit
     * @return int the id by a given order
     * @param nDiggSubmitOrder the id of the diggSubmit
     * @param plugin The Plugin object
     */
    public static int getDiggSubmitIdByOrder( int nDiggSubmitOrder, Plugin plugin )
    {
        return _dao.selectDiggSubmitIdByOrder( nDiggSubmitOrder, plugin );
    }

    /**
     * returns the order of a diggsubmit in a list using its Id
     * @return int  the id by a given order
     * @param nIdDiggSubmit the id of the contactList
     * @param plugin The Plugin object
     */
    public static int getDiggSubmitOrderById( int nIdDiggSubmit, Plugin plugin )
    {
        return _dao.selectDiggSubmitOrderById( nIdDiggSubmit, plugin );
    }

    /**
     * Update the number order of diggSubmit
     * @param nIdDiggSubmit the id of the diggSubmit
     * @param nNewOrder the new number of order
     * @param plugin The Plugin object
     */
    public static void updateDiggSubmitOrder( int nNewOrder, int nIdDiggSubmit, Plugin plugin )
    {
        _dao.storeDiggSubmitOrder( nNewOrder, nIdDiggSubmit, plugin );
    }

    /**
     * Search the max order number of contacts for one list
     * @param nIdDigg the Id of the Digg
     * @return int the max order
     * @param plugin The Plugin object
     */
    public static int getMaxOrderContactList( int nIdDigg, Plugin plugin )
    {
        return _dao.maxOrderDiggSubmit( nIdDigg, plugin );
    }
}
