package fr.paris.lutece.plugins.digglike.service;

import java.util.List;

import fr.paris.lutece.plugins.digglike.business.CommentSubmit;
import fr.paris.lutece.plugins.digglike.business.CommentSubmitHome;
import fr.paris.lutece.plugins.digglike.business.SubmitFilter;
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
    	CommentSubmitHome.create(commentSubmit, plugin);
    	
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
    	return CommentSubmitHome.getCommentSubmitList(filter, plugin);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public List<CommentSubmit> getCommentSubmitListBackOffice( SubmitFilter filter, Plugin plugin )
    {
    	return CommentSubmitHome.getCommentSubmitListBackOffice(filter, plugin);
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
