package fr.paris.lutece.plugins.digglike.business;

import fr.paris.lutece.portal.service.security.LuteceUser;

public class DiggUserInfo {

	
	private String _strLuteceUserKey;
	private String _strFirstName;
	private String _strLastName;
	private String _strBusinnessMail;
	private String _strHomeMail;
	private String _strLogin;
	

	public String getLuteceUserKey() {
		return _strLuteceUserKey;
	}
	public void setLuteceUserKey(String _strKey) {
		this._strLuteceUserKey = _strKey;
	}
	public String getFirstName() {
		return _strFirstName;
	}
	public void setFirstName(String _strFirstName) {
		this._strFirstName = _strFirstName;
	}
	public String getLastName() {
		return _strLastName;
	}
	public void setLastName(String strLastName) {
		this._strLastName = strLastName;
	}
	public String getBusinessMail() {
		return _strBusinnessMail;
	}
	public void setBusinesMail(String _strMail) {
		this._strBusinnessMail = _strMail;
	}
	
	public String getHomeMail() {
		return _strHomeMail;
	}
	public void setHomeMail(String _strMail) {
		this._strHomeMail = _strMail;
	}
	
	public String getLogin() {
		return _strLogin;
	}
	public void setLogin(String _strLogin) {
		this._strLogin = _strLogin;
	}
	
	
    /**
     * Gets a user's info
     * @param key The info key
     * @return The info value
     */
    public String getUserInfo( String key )
    {
        
    	String strReturn=null;
    	if( key!=null )
        {
        	if(key.equals(LuteceUser.NAME_GIVEN))
        	{
        		strReturn=getFirstName();
        		
        	}
        	else if(key.equals(LuteceUser.NAME_FAMILY))
        	{
        		strReturn=getLastName();
        		
        	}
        	else if(key.equals(LuteceUser.BUSINESS_INFO_ONLINE_EMAIL))
        	{
        		strReturn=getBusinessMail();
        		
        	}
        	
        	else if(key.equals(LuteceUser.HOME_INFO_ONLINE_EMAIL))
        	{
        		strReturn=getHomeMail();
        		
        	}
        	
        	
        
        }
        return strReturn;
       
    }
        
    /**
     * toString implementation
     * @return The login
     */
    public String toString(  )
    {
        return getLogin();
    }
    
        


}
