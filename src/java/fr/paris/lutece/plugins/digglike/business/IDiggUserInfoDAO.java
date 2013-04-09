package fr.paris.lutece.plugins.digglike.business;

import fr.paris.lutece.portal.service.plugin.Plugin;

public interface IDiggUserInfoDAO {

	/**
	 * Update DiggUserInfo
	 * @param diggUserInfo diggUserInfo
	 * @param plugin the plugin
	 */
	public abstract void update(DiggUserInfo diggUserInfo, Plugin plugin);

	/**
	 * insert DiggUserInfo
	 * @param diggUserInfo diggUserInfo
	 * @param plugin the plugin
	 */
	public abstract void insert(DiggUserInfo diggUserInfo, Plugin plugin);

	/**
	 * Load user information by lutece user key
	 * @param strLuteceUserKey the lutece userKey
	 * @param plugin the plugin
	 * @return
	 */
	public abstract DiggUserInfo load(String strLuteceUserKey, Plugin plugin);

	/**
	 * Delete digg user info
	 * @param strLuteceUserKey the lutece user key
	 * @param plugin the plugin
	 */
	public abstract void delete(String strLuteceUserKey, Plugin plugin);

}