/*
 * Copyright (c) 2002-2010, Mairie de Paris
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
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for ExportFormat objects
 */
public final class ExportFormatDAO implements IExportFormatDAO
{
    // Constants
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_export ) FROM digglike_export_format";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_export,title,description,extension,xsl_file" +
        " FROM digglike_export_format WHERE id_export = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO digglike_export_format( id_export,title,description,extension,xsl_file)" +
        "VALUES(?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM digglike_export_format WHERE id_export = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE digglike_export_format SET id_export=?,title=?,description=?,extension=?,xsl_file=? WHERE id_export = ? ";
    private static final String SQL_QUERY_SELECT = "SELECT id_export,title,description,extension" +
        " FROM digglike_export_format";

    /**
     * Generates a new primary key
     *
     * @param plugin the plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
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
     * @param exportFormat instance of the ExportFormat object to insert
     * @param plugin the plugin
     */
    public synchronized void insert( ExportFormat exportFormat, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        exportFormat.setIdExport( newPrimaryKey( plugin ) );
        daoUtil.setInt( 1, exportFormat.getIdExport(  ) );
        daoUtil.setString( 2, exportFormat.getTitle(  ) );
        daoUtil.setString( 3, exportFormat.getDescription(  ) );
        daoUtil.setString( 4, exportFormat.getExtension(  ) );
        daoUtil.setBytes( 5, exportFormat.getXsl(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load the data of the export format from the table
     *
     * @param nId The identifier of the export format
     * @param plugin the plugin
     * @return the instance of the ExportFormat
     */
    public ExportFormat load( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery(  );

        ExportFormat exportFormat = null;

        if ( daoUtil.next(  ) )
        {
            exportFormat = new ExportFormat(  );
            exportFormat.setIdExport( daoUtil.getInt( 1 ) );
            exportFormat.setTitle( daoUtil.getString( 2 ) );
            exportFormat.setDescription( daoUtil.getString( 3 ) );
            exportFormat.setExtension( daoUtil.getString( 4 ) );
            exportFormat.setXsl( daoUtil.getBytes( 5 ) );
        }

        daoUtil.free(  );

        return exportFormat;
    }

    /**
     * Delete a record from the table
     *
     * @param nIdExport The identifier of the ExportFormat
     * @param plugin the plugin
     */
    public void delete( int nIdExport, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdExport );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Update the exportFormat in the table
     *
     * @param exportFormat instance of the ExportFormat object to update
     * @param plugin the plugin
     */
    public void store( ExportFormat exportFormat, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setInt( 1, exportFormat.getIdExport(  ) );
        daoUtil.setString( 2, exportFormat.getTitle(  ) );
        daoUtil.setString( 3, exportFormat.getDescription(  ) );
        daoUtil.setString( 4, exportFormat.getExtension(  ) );
        daoUtil.setBytes( 5, exportFormat.getXsl(  ) );
        daoUtil.setInt( 6, exportFormat.getIdExport(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load the data of all the ExportFormat and returns them in form of a list
     *
     * @param plugin the plugin
     * @return The List which contains the data of all the ExportFormat
     */
    public List<ExportFormat> selectListExport( Plugin plugin )
    {
        List<ExportFormat> exportFormatList = new ArrayList<ExportFormat>(  );
        ExportFormat exportFormat = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            exportFormat = new ExportFormat(  );
            exportFormat.setIdExport( daoUtil.getInt( 1 ) );
            exportFormat.setTitle( daoUtil.getString( 2 ) );
            exportFormat.setDescription( daoUtil.getString( 3 ) );
            exportFormat.setExtension( daoUtil.getString( 4 ) );
            exportFormatList.add( exportFormat );
        }

        daoUtil.free(  );

        return exportFormatList;
    }

    /**
     * Load the data of all the export format returns them in a  reference list
     * @param plugin the plugin
     * @return  a  reference list of export format
     */
    public ReferenceList getListExport( Plugin plugin )
    {
        ReferenceList listExport = new ReferenceList(  );
        ExportFormat exportFormat = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            exportFormat = new ExportFormat(  );
            exportFormat.setIdExport( daoUtil.getInt( 1 ) );
            exportFormat.setTitle( daoUtil.getString( 2 ) );
            listExport.addItem( exportFormat.getIdExport(  ), exportFormat.getTitle(  ) );
        }

        daoUtil.free(  );

        return listExport;
    }
}
