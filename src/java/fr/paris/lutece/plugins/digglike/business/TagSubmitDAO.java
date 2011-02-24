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
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * This class provides Data Access methods for  objects DiggSubmitDAO
 */
public final class TagSubmitDAO implements ITagSubmitDAO
{
    // Constants
    private static final String EMPTY_STRING = "";
    private static final String SQL_QUERY_NEW_PK = "SELECT MAX( id_tag_submit ) FROM digglike_tag_submit";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_tag_submit,id_digg_submit,tag_value " +
        "FROM digglike_tag_submit WHERE id_tag_submit=? ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO digglike_tag_submit ( id_tag_submit,id_digg_submit,tag_value)" +
        "VALUES(?,?,?)";
    private static final String SQL_QUERY_DELETE = "DELETE FROM digglike_tag_submit WHERE id_tag_submit = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE digglike_tag_submit SET " +
        "id_tag_submit=?,id_digg_submit=?,tag_value=?" + "FROM digglike_tag_submit WHERE id_tag_submit=? ";
    private static final String SQL_QUERY_SELECT_TAG_SUBMIT_BY_FILTER = "SELECT tag.id_tag_submit,tag.id_digg_submit,tag.tag_value " +
        "FROM digglike_tag_submit tag,digglike_digg_submit dig  WHERE tag.id_digg_submit=dig.id_digg_submit ";
    private static final String SQL_QUERY_SELECT_COUNT_BY_FILTER = "SELECT COUNT(id_tag_submit) " +
        "FROM digglike_tag_submit tag,digglike_digg_submit dig  WHERE  tag.id_digg_submit=dig.id_digg_submit ";
    private static final String SQL_FILTER_ID_DIGG = "AND dig.id_digg = ? ";
    private static final String SQL_FILTER_ID_DIGG_SUBMIT = "AND dig.id_digg_submit = ? ";
    private static final String SQL_FILTER_DATE_FIRST_SUBMIT = "AND dig.date_response >= ? ";
    private static final String SQL_FILTER_DATE_LAST_SUBMIT = "AND dig.date_response <= ? ";
    private static final String SQL_ORDER_BY_TAG_VALUE = " ORDER BY tag.tag_value";

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
         * @param tagSubmit instance of the Tag Submit object to insert
         * @param plugin the plugin
         *          */
    public void insert( TagSubmit tagSubmit, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        tagSubmit.setIdTagSubmit( newPrimaryKey( plugin ) );
        daoUtil.setInt( 1, tagSubmit.getIdTagSubmit(  ) );
        daoUtil.setInt( 2, tagSubmit.getDiggSubmit(  ).getIdDiggSubmit(  ) );
        daoUtil.setString( 3, tagSubmit.getValue(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
         * Load the data of the tagSubmit from the table
         *
         * @param nIdTagSubmit The identifier of the tag submit
         * @param plugin the plugin
         * @return the instance of the tagSubmit
         */
    public TagSubmit load( int nIdTagSubmit, Plugin plugin )
    {
        TagSubmit tagSubmit = null;
        DiggSubmit diggSubmit = null;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nIdTagSubmit );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            tagSubmit.setIdTagSubmit( daoUtil.getInt( 1 ) );

            diggSubmit = new DiggSubmit(  );
            diggSubmit.setIdDiggSubmit( daoUtil.getInt( 2 ) );
            tagSubmit.setDiggSubmit( diggSubmit );

            tagSubmit.setValue( daoUtil.getString( 3 ) );
        }

        daoUtil.free(  );

        return tagSubmit;
    }

    /**
         * Delete   the tag submit whose identifier is specified in parameter
         *
         * @param nIdTagSubmit The identifier of the tag submit
         * @param plugin the plugin
         */
    public void delete( int nIdTagSubmit, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdTagSubmit );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
         * Update the the tagSubmit in the table
         *
         * @param tagSubmit instance of the tagSubmit object to update
         * @param plugin the plugin
         */
    public void store( TagSubmit tagSubmit, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setInt( 1, tagSubmit.getIdTagSubmit(  ) );
        daoUtil.setInt( 2, tagSubmit.getDiggSubmit(  ).getIdDiggSubmit(  ) );
        daoUtil.setString( 3, tagSubmit.getValue(  ) );
        daoUtil.setInt( 4, tagSubmit.getIdTagSubmit(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
         * Load the data of all the tagSubmit who verify the filter and returns them in a  list
         * @param filter the filter
         * @param plugin the plugin
         * @return  the list of tagSubmit
         */
    public List<TagSubmit> selectListByFilter( SubmitFilter filter, Plugin plugin )
    {
        List<TagSubmit> tagSubmitList = new ArrayList<TagSubmit>(  );
        TagSubmit tagSubmit = null;

        DiggSubmit diggSubmit = null;

        String strSQL = SQL_QUERY_SELECT_TAG_SUBMIT_BY_FILTER;
        strSQL += ( ( filter.containsIdDigg(  ) ) ? SQL_FILTER_ID_DIGG : EMPTY_STRING );
        strSQL += ( ( filter.containsIdDiggSubmit(  ) ) ? SQL_FILTER_ID_DIGG_SUBMIT : EMPTY_STRING );
        strSQL += ( ( filter.containsDateFirst(  ) ) ? SQL_FILTER_DATE_FIRST_SUBMIT : EMPTY_STRING );
        strSQL += ( ( filter.containsDateLast(  ) ) ? SQL_FILTER_DATE_LAST_SUBMIT : EMPTY_STRING );
        strSQL += SQL_ORDER_BY_TAG_VALUE;

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.containsIdDigg(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdDigg(  ) );
            nIndex++;
        }

        if ( filter.containsIdDiggSubmit(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdDiggSubmit(  ) );
            nIndex++;
        }

        if ( filter.containsDateFirst(  ) )
        {
            daoUtil.setTimestamp( nIndex, filter.getDateFirst(  ) );
            nIndex++;
        }

        if ( filter.containsDateLast(  ) )
        {
            daoUtil.setTimestamp( nIndex, filter.getDateLast(  ) );
            nIndex++;
        }

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            tagSubmit.setIdTagSubmit( daoUtil.getInt( 1 ) );

            diggSubmit = new DiggSubmit(  );
            diggSubmit.setIdDiggSubmit( daoUtil.getInt( 2 ) );
            tagSubmit.setDiggSubmit( diggSubmit );

            tagSubmit.setValue( daoUtil.getString( 3 ) );

            tagSubmitList.add( tagSubmit );
        }

        daoUtil.free(  );

        return tagSubmitList;
    }

    /**
     * return the number  of all the tagSubmit who verify the filter
     * @param filter the filter
     * @param plugin the plugin
     * @return  the number  of all the tagSubmit who verify the filter
     */
    public int selectCountByFilter( SubmitFilter filter, Plugin plugin )
    {
        int nIdCount = 0;
        String strSQL = SQL_QUERY_SELECT_COUNT_BY_FILTER;
        strSQL += ( ( filter.containsIdDigg(  ) ) ? SQL_FILTER_ID_DIGG : EMPTY_STRING );
        strSQL += ( ( filter.containsIdDiggSubmit(  ) ) ? SQL_FILTER_ID_DIGG_SUBMIT : EMPTY_STRING );
        strSQL += ( ( filter.containsDateFirst(  ) ) ? SQL_FILTER_DATE_FIRST_SUBMIT : EMPTY_STRING );
        strSQL += ( ( filter.containsDateLast(  ) ) ? SQL_FILTER_DATE_LAST_SUBMIT : EMPTY_STRING );

        DAOUtil daoUtil = new DAOUtil( strSQL, plugin );
        int nIndex = 1;

        if ( filter.containsIdDigg(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdDigg(  ) );
            nIndex++;
        }

        if ( filter.containsIdDiggSubmit(  ) )
        {
            daoUtil.setInt( nIndex, filter.getIdDiggSubmit(  ) );
            nIndex++;
        }

        if ( filter.containsDateFirst(  ) )
        {
            daoUtil.setTimestamp( nIndex, filter.getDateFirst(  ) );
            nIndex++;
        }

        if ( filter.containsDateLast(  ) )
        {
            daoUtil.setTimestamp( nIndex, filter.getDateLast(  ) );
            nIndex++;
        }

        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            nIdCount = daoUtil.getInt( 1 );
        }

        daoUtil.free(  );

        return nIdCount;
    }
}
