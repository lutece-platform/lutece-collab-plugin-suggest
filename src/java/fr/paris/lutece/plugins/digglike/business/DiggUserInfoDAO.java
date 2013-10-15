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
package fr.paris.lutece.plugins.digglike.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;


public class DiggUserInfoDAO implements IDiggUserInfoDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT first_name,last_name,business_mail,home_mail,login " +
        "FROM digglike_digg_user_info WHERE lutece_user_key = ?  ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO digglike_digg_user_info ( lutece_user_key,first_name,last_name,business_mail,home_mail,login ) " +
        "VALUES(?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM digglike_digg_user_info WHERE lutece_user_key= ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE digglike_digg_user_info SET " +
        "first_name= ?,last_name= ?,business_mail=?,home_mail=?,login= ?" + " WHERE lutece_user_key= ? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert( DiggUserInfo diggUserInfo, Plugin plugin )
    {
        int ncpt = 1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        daoUtil.setString( ncpt++, diggUserInfo.getLuteceUserKey(  ) );
        daoUtil.setString( ncpt++, diggUserInfo.getFirstName(  ) );
        daoUtil.setString( ncpt++, diggUserInfo.getLastName(  ) );
        daoUtil.setString( ncpt++, diggUserInfo.getBusinessMail(  ) );
        daoUtil.setString( ncpt++, diggUserInfo.getHomeMail(  ) );
        daoUtil.setString( ncpt++, diggUserInfo.getLogin(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update( DiggUserInfo diggUserInfo, Plugin plugin )
    {
        int ncpt = 1;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setString( ncpt++, diggUserInfo.getFirstName(  ) );
        daoUtil.setString( ncpt++, diggUserInfo.getLastName(  ) );
        daoUtil.setString( ncpt++, diggUserInfo.getBusinessMail(  ) );
        daoUtil.setString( ncpt++, diggUserInfo.getHomeMail(  ) );
        daoUtil.setString( ncpt++, diggUserInfo.getLogin(  ) );
        daoUtil.setString( ncpt++, diggUserInfo.getLuteceUserKey(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DiggUserInfo load( String strLuteceUserKey, Plugin plugin )
    {
        DiggUserInfo submitUserInfo = null;

        int ncpt = 1;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );

        daoUtil.setString( 1, strLuteceUserKey );

        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            submitUserInfo = new DiggUserInfo(  );

            submitUserInfo.setLuteceUserKey( strLuteceUserKey );
            submitUserInfo.setFirstName( daoUtil.getString( ncpt++ ) );
            submitUserInfo.setLastName( daoUtil.getString( ncpt++ ) );
            submitUserInfo.setBusinesMail( daoUtil.getString( ncpt++ ) );
            submitUserInfo.setHomeMail( daoUtil.getString( ncpt++ ) );
            submitUserInfo.setLogin( daoUtil.getString( ncpt++ ) );
        }

        daoUtil.free(  );

        return submitUserInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( String strLuteceUserKey, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setString( 1, strLuteceUserKey );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
}
