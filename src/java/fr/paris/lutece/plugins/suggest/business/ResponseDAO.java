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
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides Data Access methods for Response objects
 */
public final class ResponseDAO implements IResponseDAO
{
    // Constants
    private static final String EMPTY_STRING = "";
    private static final String SQL_QUERY_NEW_PK = "SELECT MAX( id_response ) FROM suggest_response";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT "
            + "resp.id_response,resp.id_suggest_submit,resp.response_value,type.class_name,ent.id_entry,ent.title,ent.id_type,ent.show_in_suggest_submit_lis,res.id_resource_image "
            + "FROM suggest_response resp,suggest_entry ent,suggest_entry_type type  "
            + "WHERE resp.id_response=? and resp.id_entry =ent.id_entry and ent.id_type=type.id_type ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO suggest_response ( "
            + "id_response,id_suggest_submit,response_value,id_entry,id_resource_image) VALUES(?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM suggest_response WHERE id_response = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE  suggest_response SET "
            + "id_response=?,id_suggest_submit=?,response_value=?,id_entry=?,id_resource_image=? WHERE id_response=?";
    private static final String SQL_QUERY_SELECT_RESPONSE_BY_FILTER = "SELECT "
            + "resp.id_response,resp.id_suggest_submit,resp.response_value,type.class_name,ent.id_entry,ent.title,ent.id_type,ent.show_in_suggest_submit_list,resp.id_resource_image "
            + "FROM suggest_response resp,suggest_entry ent,suggest_entry_type type  " + "WHERE resp.id_entry =ent.id_entry and ent.id_type=type.id_type ";
    private static final String SQL_FILTER_ID_SUGGEST_SUBMIT = " AND resp.id_suggest_submit = ? ";
    private static final String SQL_FILTER_ID_ENTRY = " AND resp.id_entry = ? ";
    private static final String SQL_ORDER_BY_ID_RESPONSE = " ORDER BY id_response ";

    /**
     * Generates a new primary key
     *
     * @param plugin
     *            the plugin
     * @return The new primary key
     */
    private int newPrimaryKey( Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin ) )
        {
            daoUtil.executeQuery( );
            daoUtil.next( );

            return daoUtil.getInt( 1 ) + 1;
        }
    }

    /**
     * Insert a new record in the table.
     *
     * @param response
     *            instance of the Response object to insert
     * @param plugin
     *            the plugin
     */
    @Override
    public void insert( Response response, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            response.setIdResponse( newPrimaryKey( plugin ) );
            daoUtil.setInt( 1, response.getIdResponse( ) );
            daoUtil.setInt( 2, response.getSuggestSubmit( ).getIdSuggestSubmit( ) );
            daoUtil.setString( 3, response.getValueResponse( ) );
            daoUtil.setInt( 4, response.getEntry( ).getIdEntry( ) );

            if ( response.getIdImageResource( ) != null )
            {
                daoUtil.setInt( 5, response.getIdImageResource( ) );
            }
            else
            {
                daoUtil.setIntNull( 5 );
            }

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Load the data of the response from the table
     *
     * @param nIdResponse
     *            The identifier of the response
     * @param plugin
     *            the plugin
     * @return the instance of the response
     */
    @Override
    public Response load( int nIdResponse, Plugin plugin )
    {
        boolean bException = false;
        Response response = null;
        IEntry entry = null;
        EntryType entryType;
        SuggestSubmit suggestSubmit;
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin ) )
        {
            daoUtil.setInt( 1, nIdResponse );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                response = new Response( );
                response.setIdResponse( daoUtil.getInt( 1 ) );

                suggestSubmit = new SuggestSubmit( );
                suggestSubmit.setIdSuggestSubmit( daoUtil.getInt( 2 ) );
                response.setSuggestSubmit( suggestSubmit );

                response.setValueResponse( daoUtil.getString( 3 ) );
                entryType = new EntryType( );
                entryType.setClassName( daoUtil.getString( 4 ) );
                entryType.setIdType( daoUtil.getInt( 7 ) );

                try
                {
                    entry = (IEntry) Class.forName( entryType.getClassName( ) ).newInstance( );
                }
                catch( ClassNotFoundException | InstantiationException | IllegalAccessException e )
                {
                    // class doesn't exist or class is abstract or is an interface or haven't accessible builder or can't access to rhe class
                    AppLogService.error( e.getMessage( ), e );
                    bException = true;
                }

                if ( bException )
                {
                    return null;
                }

                entry.setEntryType( entryType );
                entry.setIdEntry( daoUtil.getInt( 5 ) );
                entry.setTitle( daoUtil.getString( 6 ) );
                entry.setShowInSuggestSubmitList( daoUtil.getBoolean( 8 ) );
                response.setEntry( entry );

                if ( daoUtil.getObject( 9 ) != null )
                {
                    response.setIdImageResource( daoUtil.getInt( 9 ) );
                }
            }

            return response;
        }
    }

    /**
     * Delete response whose identifier is specified in parameter
     *
     * @param nIdResponse
     *            The identifier of the response
     * @param plugin
     *            the plugin
     */
    @Override
    public void delete( int nIdResponse, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdResponse );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Update the the response in the table
     *
     * @param response
     *            instance of the response object to update
     * @param plugin
     *            the plugin
     */
    @Override
    public void store( Response response, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            daoUtil.setInt( 1, response.getIdResponse( ) );
            daoUtil.setInt( 2, response.getSuggestSubmit( ).getIdSuggestSubmit( ) );
            daoUtil.setString( 3, response.getValueResponse( ) );
            daoUtil.setInt( 4, response.getEntry( ).getIdEntry( ) );

            if ( response.getIdImageResource( ) != null )
            {
                daoUtil.setInt( 5, response.getIdImageResource( ) );
            }
            else
            {
                daoUtil.setIntNull( 5 );
            }

            daoUtil.setInt( 6, response.getIdResponse( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Load the data of all the response who verify the filter and returns them in a list
     * 
     * @param filter
     *            the filter
     * @param plugin
     *            the plugin
     * @return the list of response
     */
    @Override
    public List<Response> selectListByFilter( SubmitFilter filter, Plugin plugin )
    {
        boolean bException = false;
        List<Response> responseList = new ArrayList<>( );
        Response response;
        IEntry entry = null;
        EntryType entryType;

        SuggestSubmit suggestSubmit;

        String strSQL = SQL_QUERY_SELECT_RESPONSE_BY_FILTER;
        strSQL += ( ( filter.containsIdSuggestSubmit( ) ) ? SQL_FILTER_ID_SUGGEST_SUBMIT : EMPTY_STRING );
        strSQL += ( ( filter.containsIdEntry( ) ) ? SQL_FILTER_ID_ENTRY : EMPTY_STRING );
        strSQL += SQL_ORDER_BY_ID_RESPONSE;

        try( DAOUtil daoUtil = new DAOUtil( strSQL, plugin ) )
        {
            int nIndex = 1;

            if ( filter.containsIdSuggestSubmit( ) )
            {
                daoUtil.setInt( nIndex, filter.getIdSuggestSubmit( ) );
                nIndex++;
            }

            if ( filter.containsIdEntry( ) )
            {
                daoUtil.setInt( nIndex, filter.getIdEntry( ) );
                nIndex++;
            }

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                response = new Response( );
                response.setIdResponse( daoUtil.getInt( 1 ) );

                suggestSubmit = new SuggestSubmit( );
                suggestSubmit.setIdSuggestSubmit( daoUtil.getInt( 2 ) );
                response.setSuggestSubmit( suggestSubmit );

                response.setValueResponse( daoUtil.getString( 3 ) );
                entryType = new EntryType( );
                entryType.setClassName( daoUtil.getString( 4 ) );
                entryType.setIdType( daoUtil.getInt( 7 ) );

                try
                {
                    entry = (IEntry) Class.forName( entryType.getClassName( ) ).newInstance( );
                }
                catch( ClassNotFoundException | InstantiationException | IllegalAccessException e )
                {
                    // class doesn't exist or class is abstract or is an interface or haven't accessible builder or can't access to rhe class
                    AppLogService.error( e.getMessage( ), e );
                    bException = true;
                }

                if ( bException )
                {
                    return new ArrayList<>( );
                }

                entry.setEntryType( entryType );
                entry.setIdEntry( daoUtil.getInt( 5 ) );
                entry.setTitle( daoUtil.getString( 6 ) );

                entry.setShowInSuggestSubmitList( daoUtil.getBoolean( 8 ) );

                response.setEntry( entry );

                if ( daoUtil.getObject( 9 ) != null )
                {
                    response.setIdImageResource( daoUtil.getInt( 9 ) );
                }

                responseList.add( response );
            }

            return responseList;
        }
    }
}
