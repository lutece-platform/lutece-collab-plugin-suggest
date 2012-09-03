package fr.paris.lutece.plugins.digglike.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import fr.paris.lutece.plugins.digglike.business.DiggSubmit;
import fr.paris.lutece.plugins.digglike.business.SubmitFilter;
import fr.paris.lutece.portal.business.style.Theme;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.plugin.Plugin;

public interface IDiggSubmitService {
	
	 
	/**
     * Creation of an instance of record
     *
     * @param diggSubmit The instance of the diggSubmit which contains the informations to store
     * @param plugin the Plugin
     * @return the id of {@link Theme} diggsubmit
     */
    @Transactional( "digglike.transactionManager" )
    int create( DiggSubmit diggSubmit, Plugin plugin );
    
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
    void  update( DiggSubmit diggSubmit,boolean bUpdateIndex,Plugin plugin );
    
    /**
     * Returns an instance of a DiggSubmit whose identifier is specified in parameter
     *
     * @param nKey The diggSubmit primary key
     * @param plugin the Plugin
     * @return an instance of DiggSubmit
     */
    DiggSubmit findByPrimaryKey( int nKey, Plugin plugin );
    
    /**
     * Load the data of all the diggSubmit who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of diggSubmit
     */
    List<DiggSubmit> getDiggSubmitList( SubmitFilter filter, Plugin plugin );
    
    /**
     * Load the data of all the diggSubmit with the number of comment by digg submit  who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of diggSubmit
     */
    List<DiggSubmit> getDiggSubmitListWithNumberComment( SubmitFilter filter, Plugin plugin );
    
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
      * Creation of an image
      * @param nIdDiggSubmit the id of the diggSubmit
      * @param image the image to add to the db
      * @param plugin the Plugin
      * @return the id of the new digg submit
      * @throws com.mysql.jdbc.PacketTooBigException if the image is too big
      *
      */
     int createImage( int nIdDiggSubmit, ImageResource image, Plugin plugin )
         throws com.mysql.jdbc.PacketTooBigException;
     

     /**
      * Search the order number of diggSubmit
      * @return int the id by a given order
      * @param nDiggSubmitOrder the id of the diggSubmit
      * @param plugin The Plugin object
      */
     int getDiggSubmitIdByOrder( int nDiggSubmitOrder, Plugin plugin );
    

     /**
      * returns the order of a diggsubmit in a list using its Id
      * @return int  the id by a given order
      * @param nIdDiggSubmit the id of the contactList
      * @param plugin The Plugin object
      */
     int getDiggSubmitOrderById( int nIdDiggSubmit, Plugin plugin );
     

     /**
      * Update the number order of diggSubmit
      * @param nIdDiggSubmit the id of the diggSubmit
      * @param nNewOrder the new number of order
      * @param plugin The Plugin object
      */
      void updateDiggSubmitOrder( int nNewOrder, int nIdDiggSubmit, Plugin plugin );
    

     /**
      * Search the max order number of contacts for one list
      * @param nIdDigg the Id of the Digg
      * @return int the max order
      * @param plugin The Plugin object
      */
    int getMaxOrderContactList( int nIdDigg, Plugin plugin );
    

}
