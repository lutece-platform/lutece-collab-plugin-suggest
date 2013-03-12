package fr.paris.lutece.plugins.digglike.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import fr.paris.lutece.plugins.digglike.business.CommentSubmit;
import fr.paris.lutece.plugins.digglike.business.SubmitFilter;
import fr.paris.lutece.portal.service.plugin.Plugin;

public interface ICommentSubmitService {

	/**
	 * Creation of an instance of commentSubmit
	 *
	 * @param commentSubmit The instance of the commentSubmit which contains the informations to store
	 * @param plugin the Plugin
	 *
	 */
	@Transactional( "digglike.transactionManager" )
	void create(CommentSubmit commentSubmit, Plugin plugin);

	/**
	 * Update of the commentSubmit which is specified in parameter
	 *
	 * @param commentSubmit The instance of the commentSubmit which contains the informations to update
	 * @param plugin the Plugin
	 *
	 */
	@Transactional( "digglike.transactionManager" )
	void update(CommentSubmit commentSubmit, Plugin plugin);

	/**
	 * Remove the commentSubmit whose identifier is specified in parameter
	 *
	 * @param nIdCommentSubmit The commentSubmitId
	 * @param plugin the Plugin
	 */
	@Transactional( "digglike.transactionManager" )
	void remove(int nIdCommentSubmit, Plugin plugin);

	/**
	 * Returns an instance of a CommentSubmitwhose identifier is specified in parameter
	 *
	 * @param nKey The commentSubmit primary key
	 * @param plugin the Plugin
	 * @return an instance of commentSubmit
	 */
	@Transactional( "digglike.transactionManager" )
	CommentSubmit findByPrimaryKey(int nKey, Plugin plugin);

	/**
	 * Load the data of all the commentSubmit who verify the filter and returns them in a  list
	 * @param filter the filter
	 * @param plugin the plugin
	 * @return  the list of commentSubmit
	 */
	@Transactional( "digglike.transactionManager" )
	List<CommentSubmit> getCommentSubmitList(SubmitFilter filter, Plugin plugin);

	/**
	 * Load the data of all the commentSubmit which verify the filter and returns them in a  list
	 * @param filter the filter
	 * @param plugin the plugin
	 * @return  the list of commentSubmit
	 */
	@Transactional( "digglike.transactionManager" )
	List<CommentSubmit> getCommentSubmitListBackOffice(SubmitFilter filter,
			Plugin plugin);

	/**
	 * Load the number of all the commentSubmit who verify the filter
	 * @param filter the filter
	 * @param plugin the plugin
	 * @return  the number of all the commentSubmit who verify the filter
	 */
	@Transactional( "digglike.transactionManager" )
	int getCountCommentSubmit(SubmitFilter filter, Plugin plugin);

}