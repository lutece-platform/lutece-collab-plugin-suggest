/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for  objects DiggSubmitDAO
 */
public final class DiggSubmitTypeDAO implements IDiggSubmitTypeDAO
{
    // Constants
    private static final String SQL_ORDER_BY_NAME = "ORDER BY t.name";
    private static final String SQL_QUERY_NEW_PK = "SELECT MAX( id_type ) FROM digglike_digg_submit_type";
    private static final String SQL_SELECT_DIGG_SUBMIT_TYPE = "SELECT t.id_type, t.name, t.color, t.parameterizable, t.id_xsl,t.id_resource_image FROM digglike_digg_submit_type t ";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = SQL_SELECT_DIGG_SUBMIT_TYPE + "WHERE t.id_type=? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO digglike_digg_submit_type ( id_type,name,color, " +
        "parameterizable, id_xsl,id_resource_image ) VALUES(?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM digglike_digg_submit_type WHERE id_type = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE digglike_digg_submit_type SET " +
        "name=?,color=?,parameterizable=?, id_xsl=?,id_resource_image=? WHERE id_type=? ";
    private static final String SQL_QUERY_FIND_ALL = SQL_SELECT_DIGG_SUBMIT_TYPE + SQL_ORDER_BY_NAME;
    private static final String SQL_QUERY_FIND_BY_ID_DIGG = SQL_SELECT_DIGG_SUBMIT_TYPE +
        ",digglike_digg_digg_submit_type dt WHERE t.id_type=dt.id_type AND dt.id_digg= ? " + SQL_ORDER_BY_NAME;
    private static final String SQL_QUERY_COUNT_NUMBER_OF_DIGG_ASSOCIATE_TO_THE_DIGG_SUBMIT_TYPE = "select COUNT(id_digg) " +
        " FROM digglike_digg_digg_submit_type WHERE id_type=? ";
    private static final String SQL_QUERY_DELETE_ASSOCIATION_DIGG = "DELETE FROM digglike_digg_digg_submit_type WHERE id_digg = ? and id_type= ? ";
    private static final String SQL_QUERY_INSERT_ASSOCIATION_DIGG = "INSERT INTO digglike_digg_digg_submit_type(id_digg,id_type) VALUES(?,?) ";

    /**
    * Generates a new primary key
    *
    * @param plugin the plugin
    * @return The new primary key
    */
    private int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey;

        if ( !daoUtil.next(  ) )
        {
            // if the table is empty
            nKey = 1;
        }

        nKey = daoUtil.getInt( 1 ) + 1;
        daoUtil.free(  );

        return nKey;
    }

    /**
     * Insert a new record in the table.
     *
     * @param diggSubmitType instance of the Digg Submit Type object to insert
     * @param plugin the plugin
     * @return the id of the new Digg
     */
    public int insert( DiggSubmitType diggSubmitType, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        diggSubmitType.setIdType( newPrimaryKey( plugin ) );

        daoUtil.setInt( 1, diggSubmitType.getIdType(  ) );
        daoUtil.setString( 2, diggSubmitType.getName(  ) );
        daoUtil.setString( 3, diggSubmitType.getColor(  ) );
        daoUtil.setBoolean( 4, diggSubmitType.getParameterizableInFO(  ) );
        daoUtil.setInt( 5, diggSubmitType.getIdXSLStyleSheet(  ) );

        if ( diggSubmitType.getIdImageResource(  ) != null )
        {
            daoUtil.setInt( 6, diggSubmitType.getIdImageResource(  ) );
        }
        else
        {
            daoUtil.setIntNull( 6 );
        }

        daoUtil.executeUpdate(  );
        daoUtil.free(  );

        return diggSubmitType.getIdType(  );
    }

    /**
     * Load the data of the diggSubmitType from the table
     *
     * @param nIdDiggSubmitType The identifier of the formResponse
     * @param plugin the plugin
     * @return the instance of the diggSubmitType
     */
    public DiggSubmitType load( int nIdDiggSubmitType, Plugin plugin )
    {
        DiggSubmitType diggSubmitType = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nIdDiggSubmitType );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            diggSubmitType = getRow( daoUtil );
        }

        daoUtil.free(  );

        return diggSubmitType;
    }

    /**
     * Load the list of the diggSubmitType from the table
     *The images are not loaded in the DiggSubmitType Objects
     * @param plugin the plugin
     * @return the instance of the diggSubmitType
     */
    public List<DiggSubmitType> selectList( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_ALL, plugin );
        daoUtil.executeQuery(  );

        List<DiggSubmitType> list = new ArrayList<DiggSubmitType>(  );

        while ( daoUtil.next(  ) )
        {
            list.add( getRow( daoUtil ) );
        }

        daoUtil.free(  );

        return list;
    }

    /**
     * Delete   the digg submit type whose identifier is specified in parameter
     *
     * @param nIdDiggSubmitType The identifier of the digg submit
     * @param plugin the plugin
     */
    public void delete( int nIdDiggSubmitType, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdDiggSubmitType );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Update the the diggSubmitType in the table
     *
     * @param diggSubmitType instance of the diggSubmit object to update
     * @param plugin the plugin
     */
    public void store( DiggSubmitType diggSubmitType, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setString( 1, diggSubmitType.getName(  ) );
        daoUtil.setString( 2, diggSubmitType.getColor(  ) );
        daoUtil.setBoolean( 3, diggSubmitType.getParameterizableInFO(  ) );
        daoUtil.setInt( 4, diggSubmitType.getIdXSLStyleSheet(  ) );

        if ( diggSubmitType.getIdImageResource(  ) != null )
        {
            daoUtil.setInt( 5, diggSubmitType.getIdImageResource(  ) );
        }
        else
        {
            daoUtil.setIntNull( 5 );
        }

        daoUtil.setInt( 6, diggSubmitType.getIdType(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * return DiggSubmitType
     * @param daoUtil daoUtil
     * @return DiggSubmitType
     */
    private DiggSubmitType getRow( DAOUtil daoUtil )
    {
        DiggSubmitType diggSubmitType = null;
        diggSubmitType = new DiggSubmitType(  );
        diggSubmitType.setIdType( daoUtil.getInt( 1 ) );
        diggSubmitType.setName( daoUtil.getString( 2 ) );
        diggSubmitType.setColor( daoUtil.getString( 3 ) );
        diggSubmitType.setParameterizableInFO( daoUtil.getBoolean( 4 ) );
        diggSubmitType.setIdXSLStyleSheet( daoUtil.getInt( 5 ) );

        if ( daoUtil.getObject( 6 ) != null )
        {
            diggSubmitType.setIdImageResource( daoUtil.getInt( 6 ) );
        }

        return diggSubmitType;
    }

    /**
     * Load the list of the diggSubmitType from the table
     *The images are not loaded in the DiggSubmitType Objects
     * @param plugin the plugin
     * @return the instance of the diggSubmitType
     */
    public List<DiggSubmitType> selectListByIdDigg( int nIdDigg, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_ID_DIGG, plugin );
        daoUtil.setInt( 1, nIdDigg );
        daoUtil.executeQuery(  );

        List<DiggSubmitType> list = new ArrayList<DiggSubmitType>(  );

        while ( daoUtil.next(  ) )
        {
            list.add( getRow( daoUtil ) );
        }

        daoUtil.free(  );

        return list;
    }

    /**
     * true if there is a  digg associate to the digg submit type
     * @param nIdType the key of the type
     * @param plugin the plugin
     * @return true if there is a digg associate to the type
     */
    public boolean isAssociateToDigg( int nIdType, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_NUMBER_OF_DIGG_ASSOCIATE_TO_THE_DIGG_SUBMIT_TYPE, plugin );
        daoUtil.setInt( 1, nIdType );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            if ( daoUtil.getInt( 1 ) != 0 )
            {
                daoUtil.free(  );

                return true;
            }
        }

        daoUtil.free(  );

        return false;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void deleteDiggAssociation( int nIdDigg, int nIdDiggSubmitType, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ASSOCIATION_DIGG, plugin );
        daoUtil.setInt( 1, nIdDigg );
        daoUtil.setInt( 2, nIdDiggSubmitType );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void insertDiggAssociation( int nIdDigg, int nIdDiggSubmitType, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_ASSOCIATION_DIGG, plugin );
        daoUtil.setInt( 1, nIdDigg );
        daoUtil.setInt( 2, nIdDiggSubmitType );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
}
