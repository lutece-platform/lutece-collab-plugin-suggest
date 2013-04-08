package fr.paris.lutece.plugins.digglike.service;

import java.util.List;

import fr.paris.lutece.plugins.digglike.business.CommentSubmit;
import fr.paris.lutece.plugins.digglike.business.CommentSubmitHome;
import fr.paris.lutece.plugins.digglike.business.SubmitFilter;
import fr.paris.lutece.plugins.digglike.utils.DiggUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class CommentSubmitService implements ICommentSubmitService {
	
	 public static final String BEAN_SERVICE = "digglike.commentSubmitService";
	 private static ICommentSubmitService _singleton;

	/**
     * {@inheritDoc}
     */
    @Override
    public  void create( CommentSubmit commentSubmit, Plugin plugin )
    {
    	commentSubmit.setDateModify(commentSubmit.getDateComment());
    	CommentSubmitHome.create(commentSubmit, plugin);
    	if( commentSubmit.getIdParent( ) != DiggUtils.CONSTANT_ID_NULL )
    	{	
    		//update parent modification date
    		CommentSubmitHome.updateDateModify(commentSubmit.getDateComment(), commentSubmit.getIdParent(), plugin);
    	}
    	
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update( CommentSubmit commentSubmit, Plugin plugin )
    {
    	CommentSubmitHome.update(commentSubmit, plugin);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void remove( int nIdCommentSubmit, Plugin plugin )
    {
    	CommentSubmitHome.remove(nIdCommentSubmit, plugin);
    	
    }
    
    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentSubmit findByPrimaryKey( int nKey, Plugin plugin )
    {
    	return CommentSubmitHome.findByPrimaryKey(nKey, plugin);
    	
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public  List<CommentSubmit> getCommentSubmitList( SubmitFilter filter, Plugin plugin )
    {
    	
    	if(!filter.containsSortBy())
    	{
    		//use default sort
    		DiggUtils.initCommentFilterBySort(filter,DiggUtils.CONSTANT_ID_NULL);
    		
    	}
    	
     	//get All parent
    	filter.setIdParent(SubmitFilter.ID_PARENT_NULL);
        List<CommentSubmit> commentSubmitList=CommentSubmitHome.getCommentSubmitList(filter, plugin);
        if(commentSubmitList!=null)
    	{
        	SubmitFilter subCommentFilter;
        	for ( CommentSubmit c : commentSubmitList )
	         {
	        	 subCommentFilter=new SubmitFilter();
	        	 //in this method we gonna get the list of children of a comment
	        	 subCommentFilter.setIdParent(c.getIdCommentSubmit());
	        	 subCommentFilter.setIdCommentSubmitState(filter.getIdCommentSubmitState());
	        	 subCommentFilter.getSortBy().add(SubmitFilter.SORT_BY_DATE_RESPONSE_DESC);
	        	 c.setComments(  CommentSubmitHome.getCommentSubmitList( subCommentFilter, plugin ));
	        
	         }
    	}
        filter.setIdParent(DiggUtils.CONSTANT_ID_NULL);
        return commentSubmitList;
    	
    	
    	
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int getCountCommentSubmit( SubmitFilter filter, Plugin plugin )
    {
    	
    	return CommentSubmitHome.getCountCommentSubmit(filter, plugin);
    	
    }
    
    
    /**
     * Returns the instance of the singleton
     * 
     * @return The instance of the singleton
     */
    public static ICommentSubmitService getService( )
    {
        if ( _singleton == null )
        {
        	_singleton = SpringContextService.getBean( BEAN_SERVICE);
        }

        return _singleton;
    }
    
    
    

}
