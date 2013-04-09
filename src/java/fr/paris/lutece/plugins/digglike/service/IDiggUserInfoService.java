package fr.paris.lutece.plugins.digglike.service;

import fr.paris.lutece.plugins.digglike.business.DiggUserInfo;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
/**
 * 
 * IDiggUserInfoService
 *
 */
public interface IDiggUserInfoService {
	
	
	
	
	/**
	 *  update user information associate to the luteceUserConnected
	 * @param luteceUserConnected {@link LuteceUser}
	 * @param plugin {@link Plugin}
	 *
	 * @throws UserNotSignedException {@link UserNotSignedException}
	 */
    void updateDiggUserInfoByLuteceUser(LuteceUser luteceUserConnected,Plugin plugin);
    		
    
    /**
     * return user information associate to the lutece user  key
     * @param strLuteceUserKey the lutece user
     * @param plugin {@link Plugin}
     * @return return user information associate to the lutece user  key
     */
    DiggUserInfo findDiggUserInfoByKey(String strLuteceUserKey,Plugin plugin);

}
