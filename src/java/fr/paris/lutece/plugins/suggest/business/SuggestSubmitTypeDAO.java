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
package fr.paris.lutece.plugins.suggest.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides Data Access methods for objects SuggestSubmitDAO
 */
public final class SuggestSubmitTypeDAO implements ISuggestSubmitTypeDAO
{
    // Constants
    private static final String SQL_ORDER_BY_NAME = "ORDER BY t.name";
    private static final String SQL_QUERY_NEW_PK = "SELECT MAX( id_type ) FROM suggest_suggest_submit_type";
    private static final String SQL_SELECT_SUGGEST_SUBMIT_TYPE = "SELECT t.id_type, t.name, t.color, t.parameterizable, t.id_xsl,t.id_resource_image FROM suggest_suggest_submit_type t ";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = SQL_SELECT_SUGGEST_SUBMIT_TYPE + "WHERE t.id_type=? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO suggest_suggest_submit_type ( id_type,name,color, "
            + "parameterizable, id_xsl,id_resource_image ) VALUES(?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM suggest_suggest_submit_type WHERE id_type = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE suggest_suggest_submit_type SET "
            + "name=?,color=?,parameterizable=?, id_xsl=?,id_resource_image=? WHERE id_type=? ";
    private static final String SQL_QUERY_FIND_ALL = SQL_SELECT_SUGGEST_SUBMIT_TYPE + SQL_ORDER_BY_NAME;
    private static final String SQL_QUERY_FIND_BY_ID_SUGGEST = SQL_SELECT_SUGGEST_SUBMIT_TYPE
            + ",suggest_suggest_suggest_submit_type dt WHERE t.id_type=dt.id_type AND dt.id_suggest= ? " + SQL_ORDER_BY_NAME;
    private static final String SQL_QUERY_COUNT_NUMBER_OF_SUGGEST_ASSOCIATE_TO_THE_SUGGEST_SUBMIT_TYPE = "select COUNT(id_suggest) "
            + " FROM suggest_suggest_suggest_submit_type WHERE id_type=? ";
    private static final String SQL_QUERY_DELETE_ASSOCIATION_SUGGEST = "DELETE FROM suggest_suggest_suggest_submit_type WHERE id_suggest = ? and id_type= ? ";
    private static final String SQL_QUERY_INSERT_ASSOCIATION_SUGGEST = "INSERT INTO suggest_suggest_suggest_submit_type(id_suggest,id_type) VALUES(?,?) ";

    /**
     * Generates a new primary key
     *
     * @param plugin
     *            the plugin
     * @return The new primary key
     */
    private int newPrimaryKey( Plugin plugin )
    {
        int nKey;
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin ) )
        {
            daoUtil.executeQuery( );    
            
            if ( !daoUtil.next( ) )
            {
                // if the table is empty
                nKey = 1;
            }
    
            nKey = daoUtil.getInt( 1 ) + 1;
        }

        return nKey;
    }

    /**
     * Insert a new record in the table.
     *
     * @param suggestSubmitType
     *            instance of the Suggest Submit Type object to insert
     * @param plugin
     *            the plugin
     * @return the id of the new Suggest
     */
    public int insert( SuggestSubmitType suggestSubmitType, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            suggestSubmitType.setIdType( newPrimaryKey( plugin ) );
    
            daoUtil.setInt( 1, suggestSubmitType.getIdType( ) );
            daoUtil.setString( 2, suggestSubmitType.getName( ) );
            daoUtil.setString( 3, suggestSubmitType.getColor( ) );
            daoUtil.setBoolean( 4, suggestSubmitType.getParameterizableInFO( ) );
            daoUtil.setInt( 5, suggestSubmitType.getIdXSLStyleSheet( ) );
    
            if ( suggestSubmitType.getIdImageResource( ) != null )
            {
                daoUtil.setInt( 6, suggestSubmitType.getIdImageResource( ) );
            }
            else
            {
                daoUtil.setIntNull( 6 );
            }
    
            daoUtil.executeUpdate( );
        }

        return suggestSubmitType.getIdType( );
    }

    /**
     * Load the data of the suggestSubmitType from the table
     *
     * @param nIdSuggestSubmitType
     *            The identifier of the formResponse
     * @param plugin
     *            the plugin
     * @return the instance of the suggestSubmitType
     */
    public SuggestSubmitType load( int nIdSuggestSubmitType, Plugin plugin )
    {
        SuggestSubmitType suggestSubmitType = null;
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin ) )
        {
            daoUtil.setInt( 1, nIdSuggestSubmitType );
            daoUtil.executeQuery( );
    
            if ( daoUtil.next( ) )
            {
                suggestSubmitType = getRow( daoUtil );
            }

        }

        return suggestSubmitType;
    }

    /**
     * Load the list of the suggestSubmitType from the table The images are not loaded in the SuggestSubmitType Objects
     * 
     * @param plugin
     *            the plugin
     * @return the instance of the suggestSubmitType
     */
    public List<SuggestSubmitType> selectList( Plugin plugin )
    {
        List<SuggestSubmitType> list = new ArrayList<>( );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_ALL, plugin ) )
        {
            daoUtil.executeQuery( );
    
            while ( daoUtil.next( ) )
            {
                list.add( getRow( daoUtil ) );
            }

        }

        return list;
    }

    /**
     * Delete the suggest submit type whose identifier is specified in parameter
     *
     * @param nIdSuggestSubmitType
     *            The identifier of the suggest submit
     * @param plugin
     *            the plugin
     */
    public void delete( int nIdSuggestSubmitType, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdSuggestSubmitType );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Update the the suggestSubmitType in the table
     *
     * @param suggestSubmitType
     *            instance of the suggestSubmit object to update
     * @param plugin
     *            the plugin
     */
    public void store( SuggestSubmitType suggestSubmitType, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {    
            daoUtil.setString( 1, suggestSubmitType.getName( ) );
            daoUtil.setString( 2, suggestSubmitType.getColor( ) );
            daoUtil.setBoolean( 3, suggestSubmitType.getParameterizableInFO( ) );
            daoUtil.setInt( 4, suggestSubmitType.getIdXSLStyleSheet( ) );
    
            if ( suggestSubmitType.getIdImageResource( ) != null )
            {
                daoUtil.setInt( 5, suggestSubmitType.getIdImageResource( ) );
            }
            else
            {
                daoUtil.setIntNull( 5 );
            }
    
            daoUtil.setInt( 6, suggestSubmitType.getIdType( ) );
    
            daoUtil.executeUpdate( );
        }
    }

    /**
     * return SuggestSubmitType
     * 
     * @param daoUtil
     *            daoUtil
     * @return SuggestSubmitType
     */
    private SuggestSubmitType getRow( DAOUtil daoUtil )
    {
        SuggestSubmitType suggestSubmitType = null;
        suggestSubmitType = new SuggestSubmitType( );
        suggestSubmitType.setIdType( daoUtil.getInt( 1 ) );
        suggestSubmitType.setName( daoUtil.getString( 2 ) );
        suggestSubmitType.setColor( daoUtil.getString( 3 ) );
        suggestSubmitType.setParameterizableInFO( daoUtil.getBoolean( 4 ) );
        suggestSubmitType.setIdXSLStyleSheet( daoUtil.getInt( 5 ) );

        if ( daoUtil.getObject( 6 ) != null )
        {
            suggestSubmitType.setIdImageResource( daoUtil.getInt( 6 ) );
        }

        return suggestSubmitType;
    }

    /**
     * Load the list of the suggestSubmitType from the table The images are not loaded in the SuggestSubmitType Objects
     * 
     * @param plugin
     *            the plugin
     * @return the instance of the suggestSubmitType
     */
    public List<SuggestSubmitType> selectListByIdSuggest( int nIdSuggest, Plugin plugin )
    {
        List<SuggestSubmitType> list = new ArrayList<>( );
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_ID_SUGGEST, plugin ) )
        {
            daoUtil.setInt( 1, nIdSuggest );
            daoUtil.executeQuery( );
    
            while ( daoUtil.next( ) )
            {
                list.add( getRow( daoUtil ) );
            }

        }

        return list;
    }

    /**
     * true if there is a suggest associate to the suggest submit type
     * 
     * @param nIdType
     *            the key of the type
     * @param plugin
     *            the plugin
     * @return true if there is a suggest associate to the type
     */
    public boolean isAssociateToSuggest( int nIdType, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_NUMBER_OF_SUGGEST_ASSOCIATE_TO_THE_SUGGEST_SUBMIT_TYPE, plugin ) )
        {
            daoUtil.setInt( 1, nIdType );
            daoUtil.executeQuery( );
    
            if ( daoUtil.next( ) )
            {
                if ( daoUtil.getInt( 1 ) != 0 )
                {
                    daoUtil.free( );
    
                    return true;
                }
            }

        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteSuggestAssociation( int nIdSuggest, int nIdSuggestSubmitType, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ASSOCIATION_SUGGEST, plugin ) )
        {
            daoUtil.setInt( 1, nIdSuggest );
            daoUtil.setInt( 2, nIdSuggestSubmitType );
    
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertSuggestAssociation( int nIdSuggest, int nIdSuggestSubmitType, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_ASSOCIATION_SUGGEST, plugin ) )
        {
            daoUtil.setInt( 1, nIdSuggest );
            daoUtil.setInt( 2, nIdSuggestSubmitType );
            daoUtil.executeUpdate( );
        }
    }
}
