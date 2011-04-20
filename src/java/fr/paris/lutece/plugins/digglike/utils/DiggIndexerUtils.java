package fr.paris.lutece.plugins.digglike.utils;

import fr.paris.lutece.portal.business.event.ResourceEvent;
import fr.paris.lutece.portal.business.indexeraction.IndexerAction;
import fr.paris.lutece.portal.service.event.ResourceEventManager;

/**
 * 
 * DiggIndexerUtils
 *
 */
public class DiggIndexerUtils
{
	// Indexed resource type name
	public static final String CONSTANT_TYPE_RESOURCE = "DIGGLIKE_DIGG";
	
	/**
     * Warn that a action has been done.
     * @param strIdDocument the document id
     * @param nIdTask the key of the action to do
     */
    public static void addIndexerAction( String strIdDocument, int nIdTask )
    {
        ResourceEvent event = new ResourceEvent();
        event.setIdResource( strIdDocument );
        event.setTypeResource( CONSTANT_TYPE_RESOURCE );
        switch (nIdTask)
        {
        case IndexerAction.TASK_CREATE:
        	ResourceEventManager.fireAddedResource( event );
        	break;
        case IndexerAction.TASK_MODIFY:
        	ResourceEventManager.fireUpdatedResource( event );
        	break;
        case IndexerAction.TASK_DELETE:
        	ResourceEventManager.fireDeletedResource( event );
        	break;
        default:
        	break;
        }
    }
}
