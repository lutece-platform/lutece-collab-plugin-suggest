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
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for Entry objects
 */
public final class EntryDAO implements IEntryDAO
{
    // Constants
    private static final String EMPTY_STRING = "";
    private static final String SQL_QUERY_NEW_PK = "SELECT MAX( id_entry ) FROM digglike_entry";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT ent.id_type,typ.title,typ.class_name," +
        "ent.id_entry,ent.id_digg,digg.title,ent.title,ent.help_message," +
        "ent.entry_comment,ent.mandatory,ent.pos,ent.default_value,ent.height,ent.width,ent.max_size_enter,ent.show_in_digg_submit_list " +
        "FROM digglike_entry ent,digglike_entry_type typ,digglike_digg digg  WHERE ent.id_entry = ? and ent.id_type=typ.id_type and " +
        "ent.id_digg=digg.id_digg";
    private static final String SQL_QUERY_INSERT = "INSERT INTO digglike_entry ( " +
        "id_entry,id_digg,id_type,title,help_message,entry_comment,mandatory," +
        "pos,default_value,height,width,max_size_enter,show_in_digg_submit_list) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM digglike_entry WHERE id_entry = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE  digglike_entry SET " +
        "id_entry=?,id_digg=?,id_type=?,title=?,help_message=?," +
        "entry_comment=?,mandatory=?,pos=?,default_value=?,height=?,width=?,max_size_enter=?,show_in_digg_submit_list=? WHERE id_entry=?";
    private static final String SQL_QUERY_SELECT_ENTRY_BY_FILTER = "SELECT ent.id_type,typ.title,typ.class_name," +
        "ent.id_entry,ent.id_digg,ent.title,ent.help_message," +
        "ent.entry_comment,ent.mandatory,ent.pos,ent.show_in_digg_submit_list " +
        "FROM digglike_entry ent,digglike_entry_type typ WHERE ent.id_type=typ.id_type ";
    private static final String SQL_QUERY_SELECT_NUMBER_ENTRY_BY_FILTER = "SELECT COUNT(ent.id_entry) " +
        "FROM digglike_entry ent,digglike_entry_type typ WHERE ent.id_type=typ.id_type ";
    private static final String SQL_QUERY_NEW_POSITION = "SELECT MAX(pos) " + "FROM digglike_entry ";
    private static final String SQL_FILTER_ID_DIGG = " AND ent.id_digg = ? ";
    private static final String SQL_ORDER_BY_POSITION = " ORDER BY ent.pos ";
    private static final String SQL_QUERY_INSERT_VERIF_BY = "INSERT INTO digglike_entry_verify_by(id_entry,id_expression) VALUES(?,?) ";
    private static final String SQL_QUERY_DELETE_VERIF_BY = "DELETE FROM digglike_entry_verify_by WHERE id_entry = ? and id_expression= ?";
    private static final String SQL_QUERY_SELECT_REGULAR_EXPRESSION_BY_ID_ENTRY = "SELECT id_expression " +
        " FROM digglike_entry_verify_by  where id_entry=?";
    private static final String SQL_QUERY_COUNT_ENTRY_BY_ID_REGULAR_EXPRESSION = "SELECT COUNT(id_entry) " +
        " FROM digglike_entry_verify_by where id_expression = ?";

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
     * Generates a new entry position
     * @param plugin the plugin
     * @return the new entry position
     */
    private int newPosition( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_POSITION, plugin );
        daoUtil.executeQuery(  );

        int nPos;

        if ( !daoUtil.next(  ) )
        {
            // if the table is empty
            nPos = 1;
        }

        nPos = daoUtil.getInt( 1 ) + 1;
        daoUtil.free(  );

        return nPos;
    }

    /**
     * Insert a new record in the table.
     *
     * @param entry instance of the Entry object to insert
     * @param plugin the plugin
     * @return the id of the new entry
     */
    public int insert( IEntry entry, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        entry.setIdEntry( newPrimaryKey( plugin ) );

        daoUtil.setInt( 1, entry.getIdEntry(  ) );
        daoUtil.setInt( 2, entry.getDigg(  ).getIdDigg(  ) );
        daoUtil.setInt( 3, entry.getEntryType(  ).getIdType(  ) );
        daoUtil.setString( 4, entry.getTitle(  ) );
        daoUtil.setString( 5, entry.getHelpMessage(  ) );
        daoUtil.setString( 6, entry.getComment(  ) );
        daoUtil.setBoolean( 7, entry.isMandatory(  ) );
        daoUtil.setInt( 8, newPosition( plugin ) );
        daoUtil.setString( 9, entry.getDefaultValue(  ) );
        daoUtil.setInt( 10, entry.getHeight(  ) );
        daoUtil.setInt( 11, entry.getWidth(  ) );
        daoUtil.setInt( 12, entry.getMaxSizeEnter(  ) );
        daoUtil.setBoolean( 13, entry.isShowInDiggSubmitList(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );

        return entry.getIdEntry(  );
    }

    /**
     * Load the data of the entry from the table
     *
     * @param nId The identifier of the entry
     * @param plugin the plugin
     * @return the instance of the Entry
     */
    public IEntry load( int nId, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nId );
        daoUtil.executeQuery(  );

        IEntry entry = null;
        EntryType entryType = null;
        Digg digg = null;

        if ( daoUtil.next(  ) )
        {
            entryType = new EntryType(  );
            entryType.setIdType( daoUtil.getInt( 1 ) );
            entryType.setTitle( daoUtil.getString( 2 ) );
            entryType.setClassName( daoUtil.getString( 3 ) );

            try
            {
                entry = (IEntry) Class.forName( entryType.getClassName(  ) ).newInstance(  );
            }
            catch ( ClassNotFoundException e )
            {
                //  class doesn't exist
                AppLogService.error( e );

                return null;
            }
            catch ( InstantiationException e )
            {
                // Class is abstract or is an  interface or haven't accessible builder
                AppLogService.error( e );

                return null;
            }
            catch ( IllegalAccessException e )
            {
                // can't access to rhe class
                AppLogService.error( e );

                return null;
            }

            entry.setEntryType( entryType );
            entry.setIdEntry( daoUtil.getInt( 4 ) );
            // insert form
            digg = new Digg(  );
            digg.setIdDigg( daoUtil.getInt( 5 ) );
            digg.setTitle( daoUtil.getString( 6 ) );
            entry.setDigg( digg );

            entry.setTitle( daoUtil.getString( 7 ) );
            entry.setHelpMessage( daoUtil.getString( 8 ) );
            entry.setComment( daoUtil.getString( 9 ) );
            entry.setMandatory( daoUtil.getBoolean( 10 ) );
            entry.setPosition( daoUtil.getInt( 11 ) );
            entry.setDefaultValue( daoUtil.getString( 12 ) );
            entry.setHeight( daoUtil.getInt( 13 ) );
            entry.setWidth( daoUtil.getInt( 14 ) );
            entry.setMaxSizeEnter( daoUtil.getInt( 15 ) );
            entry.setShowInDiggSubmitList( daoUtil.getBoolean( 16 ) );
        }

        daoUtil.free(  );

        return entry;
    }

    /**
     * Delete a record from the table
     *
     * @param nIdEntry The identifier of the entry
     * @param plugin the plugin
     */
    public void delete( int nIdEntry, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdEntry );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Update the entry in the table
     *
     * @param entry instance of the Entry object to update
     * @param plugin the plugin
     */
    public void store( IEntry entry, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setInt( 1, entry.getIdEntry(  ) );
        daoUtil.setInt( 2, entry.getDigg(  ).getIdDigg(  ) );
        daoUtil.setInt( 3, entry.getEntryType(  ).getIdType(  ) );
        daoUtil.setString( 4, entry.getTitle(  ) );
        daoUtil.setString( 5, entry.getHelpMessage(  ) );
        daoUtil.setString( 6, entry.getComment(  ) );
        daoUtil.setBoolean( 7, entry.isMandatory(  ) );
        daoUtil.setInt( 8, entry.getPosition(  ) );
        daoUtil.setString( 9, entry.getDefaultValue(  ) );
        daoUtil.setInt( 10, entry.getHeight(  ) );
        daoUtil.setInt( 11, entry.getWidth(  ) );
        daoUtil.setInt( 12, entry.getMaxSizeEnter(  ) );
        daoUtil.setBoolean( 13, entry.isShowInDiggSubmitList(  ) );
        daoUtil.setInt( 14, entry.getIdEntry(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load the data of all the entry who verify the filter and returns them in a  list
     * @param filter the filter
     * @param plugin the plugin
     * @return  the list of entry
     */
    public List<IEntry> selectEntryListByFilter( EntryFilter filter, Plugin plugin )
    {
        List<IEntry> entryList = new ArrayList<IEntry>(  );
        IEntry entry = null;
        EntryType entryType = null;
        Digg digg = null;

        String strSQL = SQL_QUERY_SELECT_ENTRY_BY_FILTER;
        strSQL += ( ( filter.containsIdDigg(  ) ) ? SQL_FILTER_ID_DIGG : EMPTY_STRING );

        strSQL += SQL_ORDER_BY_POSITION;

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.containsIdDigg(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdDigg(  ) );
            nIndex++;
        }

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            entryType = new EntryType(  );
            entryType.setIdType( daoUtil.getInt( 1 ) );
            entryType.setTitle( daoUtil.getString( 2 ) );
            entryType.setClassName( daoUtil.getString( 3 ) );

            try
            {
                entry = (IEntry) Class.forName( entryType.getClassName(  ) ).newInstance(  );
            }
            catch ( ClassNotFoundException e )
            {
                //  class doesn't exist
                AppLogService.error( e );

                return null;
            }
            catch ( InstantiationException e )
            {
                // Class is abstract or is an  interface or haven't accessible builder
                AppLogService.error( e );

                return null;
            }
            catch ( IllegalAccessException e )
            {
                // can't access to rhe class
                AppLogService.error( e );

                return null;
            }

            entry.setEntryType( entryType );
            entry.setIdEntry( daoUtil.getInt( 4 ) );
            // insert form
            digg = new Digg(  );
            digg.setIdDigg( daoUtil.getInt( 5 ) );
            entry.setDigg( digg );

            entry.setTitle( daoUtil.getString( 6 ) );
            entry.setHelpMessage( daoUtil.getString( 7 ) );
            entry.setComment( daoUtil.getString( 8 ) );
            entry.setMandatory( daoUtil.getBoolean( 9 ) );
            entry.setPosition( daoUtil.getInt( 10 ) );
            entry.setShowInDiggSubmitList( daoUtil.getBoolean( 11 ) );

            entryList.add( entry );
        }

        daoUtil.free(  );

        return entryList;
    }

    /**
     * Return  the number of entry who verify the filter
     * @param filter the filter
     * @param plugin the plugin
     * @return   the number of entry who verify the filter
     */
    public int selectNumberEntryByFilter( EntryFilter filter, Plugin plugin )
    {
        int nNumberEntry = 0;
        String strSQL = SQL_QUERY_SELECT_NUMBER_ENTRY_BY_FILTER;
        strSQL += ( ( filter.containsIdDigg(  ) ) ? SQL_FILTER_ID_DIGG : EMPTY_STRING );

        strSQL += SQL_ORDER_BY_POSITION;

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.containsIdDigg(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdDigg(  ) );
            nIndex++;
        }

        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nNumberEntry = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nNumberEntry;
    }

    /**
     * Delete an association between  entry and a regular expression
     *
     * @param nIdEntry The identifier of the field
     *  @param nIdExpression The identifier of the regular expression
     * @param plugin the plugin
     */
    public void deleteVerifyBy( int nIdEntry, int nIdExpression, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_VERIF_BY, plugin );
        daoUtil.setInt( 1, nIdEntry );
        daoUtil.setInt( 2, nIdExpression );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * insert an association between  entry and a regular expression
     *
     * @param nIdEntry The identifier of the entry
     * @param nIdExpression The identifier of the regular expression
     * @param plugin the plugin
     */
    public void insertVerifyBy( int nIdEntry, int nIdExpression, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_VERIF_BY, plugin );
        daoUtil.setInt( 1, nIdEntry );
        daoUtil.setInt( 2, nIdExpression );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Load the key of all the regularExpression associate to the entry and returns them in a  list
     * @param nIdEntry the id of entry
     * @param plugin the plugin
     * @return  the list of regular expression key
     */
    public List<Integer> selectListRegularExpressionKeyByIdEntry( int nIdEntry, Plugin plugin )
    {
        List<Integer> regularExpressionList = new ArrayList<Integer>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_REGULAR_EXPRESSION_BY_ID_ENTRY, plugin );
        daoUtil.setInt( 1, nIdEntry );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            regularExpressionList.add( daoUtil.getInt( 1 ) );
        }

        daoUtil.free(  );

        return regularExpressionList;
    }

    /**
          * verify if the regular expresssion is use
          *
          * @param nIdEntry The identifier of the entry
          * @param plugin the plugin
          * @return true if the regular expression is use
          */
    public boolean isRegularExpressionIsUse( int nIdExpression, Plugin plugin )
    {
        int nNumberEntry = 0;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_ENTRY_BY_ID_REGULAR_EXPRESSION, plugin );
        daoUtil.setInt( 1, nIdExpression );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nNumberEntry = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nNumberEntry != 0;
    }
}
