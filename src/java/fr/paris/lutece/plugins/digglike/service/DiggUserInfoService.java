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
package fr.paris.lutece.plugins.digglike.service;

import fr.paris.lutece.plugins.digglike.business.DiggUserInfo;
import fr.paris.lutece.plugins.digglike.business.DiggUserInfoHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * 
 * service DiggUserInfoService
 *
 */
public class DiggUserInfoService implements IDiggUserInfoService
{
    public static final String BEAN_SERVICE = "digglike.diggUserInfoService";
    private static IDiggUserInfoService _singleton;


   
    /**
     * Returns the instance of the singleton
     *
     * @return The instance of the singleton
     */
    public static IDiggUserInfoService getService(  )
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
    public void updateDiggUserInfoByLuteceUser(LuteceUser luteceUserConnected,Plugin plugin)
    {
    	//test if digg user info exist
    	if( luteceUserConnected !=null && luteceUserConnected.getName() !=null )
    	{
    		DiggUserInfo diggUserInfo=null;
    		diggUserInfo=DiggUserInfoHome.findByKey(luteceUserConnected.getName(), plugin); 
    		if(diggUserInfo == null)
    		{
	    		diggUserInfo=new DiggUserInfo();
	    		diggUserInfo.setLuteceUserKey(luteceUserConnected.getName());
	    		diggUserInfo.setLastName(luteceUserConnected.getUserInfos().get(LuteceUser.NAME_FAMILY));
	    		diggUserInfo.setFirstName(luteceUserConnected.getUserInfos().get(LuteceUser.NAME_GIVEN));
	    		diggUserInfo.setLogin(luteceUserConnected.getName());
	    		diggUserInfo.setBusinesMail(luteceUserConnected.getUserInfos().get(LuteceUser.BUSINESS_INFO_ONLINE_EMAIL));
	    		diggUserInfo.setHomeMail(luteceUserConnected.getUserInfos().get(LuteceUser.HOME_INFO_ONLINE_EMAIL));
	    		DiggUserInfoHome.create(diggUserInfo, plugin);
    		}	
    	}
    	
   
    
    }
    		
    		
    	
    	
    	
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public DiggUserInfo findDiggUserInfoByKey(String strLuteceUserKey,Plugin plugin)
    {
    	return DiggUserInfoHome.findByKey(strLuteceUserKey, plugin);
    
    }
    	
    
    
    
    
    
}
