/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.suggest.business;

import fr.paris.lutece.portal.service.security.LuteceUser;


public class SuggestUserInfo
{
    private String _strLuteceUserKey;
    private String _strFirstName;
    private String _strLastName;
    private String _strBusinnessMail;
    private String _strHomeMail;
    private String _strLogin;

    public String getLuteceUserKey(  )
    {
        return _strLuteceUserKey;
    }

    public void setLuteceUserKey( String strKey )
    {
        this._strLuteceUserKey = strKey;
    }

    public String getFirstName(  )
    {
        return _strFirstName;
    }

    public void setFirstName( String strFirstName )
    {
        this._strFirstName = strFirstName;
    }

    public String getLastName(  )
    {
        return _strLastName;
    }

    public void setLastName( String strLastName )
    {
        this._strLastName = strLastName;
    }

    public String getBusinessMail(  )
    {
        return _strBusinnessMail;
    }

    public void setBusinesMail( String strMail )
    {
        this._strBusinnessMail = strMail;
    }

    public String getHomeMail(  )
    {
        return _strHomeMail;
    }

    public void setHomeMail( String strMail )
    {
        this._strHomeMail = strMail;
    }

    public String getLogin(  )
    {
        return _strLogin;
    }

    public void setLogin( String strLogin )
    {
        this._strLogin = strLogin;
    }

    /**
     * Gets a user's info
     * @param key The info key
     * @return The info value
     */
    public String getUserInfo( String key )
    {
        String strReturn = null;

        if ( key != null )
        {
            if ( key.equals( LuteceUser.NAME_GIVEN ) )
            {
                strReturn = getFirstName(  );
            }
            else if ( key.equals( LuteceUser.NAME_FAMILY ) )
            {
                strReturn = getLastName(  );
            }
            else if ( key.equals( LuteceUser.BUSINESS_INFO_ONLINE_EMAIL ) )
            {
                strReturn = getBusinessMail(  );
            }

            else if ( key.equals( LuteceUser.HOME_INFO_ONLINE_EMAIL ) )
            {
                strReturn = getHomeMail(  );
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
        return getLogin(  );
    }
}
