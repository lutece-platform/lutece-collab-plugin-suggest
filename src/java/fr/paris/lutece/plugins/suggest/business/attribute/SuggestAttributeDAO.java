/*
 * Copyright (c) 2002-2020, Mairie de Paris
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
package fr.paris.lutece.plugins.suggest.business.attribute;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * SuggestAttributeDAO
 *
 */
public class SuggestAttributeDAO implements ISuggestAttributeDAO
{
    private static final String SQL_QUERY_SELECT = " SELECT attribute_key, attribute_value FROM suggest_suggest_attribute WHERE id_suggest = ? ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO suggest_suggest_attribute ( id_suggest, attribute_key, attribute_value ) VALUES ( ?,?,? ) ";
    private static final String SQL_QUERY_DELETE = " DELETE FROM suggest_suggest_attribute WHERE id_suggest = ? ";

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> load( int nIdDirectory, Plugin plugin )
    {
        Map<String, Object> mapAttributes = new HashMap<>( );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nIdDirectory );
            daoUtil.executeQuery( );
    
            while ( daoUtil.next( ) )
            {
                int nIndex = 1;
                String strAttributeKey = daoUtil.getString( nIndex++ );
                Object attributeValue = daoUtil.getObject( nIndex++ );
                mapAttributes.put( strAttributeKey, attributeValue );
            }

        }

        return mapAttributes;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void insert( int nIdDirectory, String strAttributeKey, Object attributeValue, Plugin plugin )
    {
        int nIndex = 1;
        
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            daoUtil.setInt( nIndex++, nIdDirectory );
            daoUtil.setString( nIndex++, strAttributeKey );
            daoUtil.setString( nIndex++, ( attributeValue != null ) ? attributeValue.toString( ) : null );
    
            daoUtil.executeUpdate( );

        }
    }

    /**
     * {@inheritDoc}
     */
    public void remove( int nIdDirectory, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdDirectory );
            daoUtil.executeUpdate( );
        }
    }
}
