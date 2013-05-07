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
 * This class provides Data Access methods for DocumentAttributeType objects
 */
public final class EntryAdditionalAttributeDAO implements IEntryAdditionalAttributeDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT_BY_ENTRY = " SELECT id_entry, attr_name, attr_value FROM digglike_entry_attr_additional WHERE id_entry = ?  ";
    private static final String SQL_QUERY_INSERT = " INSERT INTO digglike_entry_attr_additional ( id_entry, attr_name, attr_value ) VALUES ( ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM digglike_entry_attr_additional WHERE id_entry = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE digglike_entry_attr_additional SET " +
        "attr_value=? WHERE id_entry=? AND attr_name=?";

    /**
     * Insert a new record in the table.
     * @param entryAdditionalAttribute instance of the Entry additional attribute object to insert
     * @param plugin the plugin
     */
    public void insert( EntryAdditionalAttribute entryAdditionalAttribute, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        daoUtil.setInt( 1, entryAdditionalAttribute.getIdEntry(  ) );
        daoUtil.setString( 2, entryAdditionalAttribute.getName(  ) );
        daoUtil.setString( 3, entryAdditionalAttribute.getValue(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Select additional attributes list of an entry
     * @param idEntry the id of the entry
     * @param plugin the plugin
     * @return return the list of additional attribute of an Entry
     */
    public List<EntryAdditionalAttribute> selectEntryAdditionalAttributeList( int idEntry, Plugin plugin )
    {
        List<EntryAdditionalAttribute> entryAdditionalAttributeList = new ArrayList<EntryAdditionalAttribute>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ENTRY, plugin );

        daoUtil.setInt( 1, idEntry );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            EntryAdditionalAttribute entryAdditionalAttribute = new EntryAdditionalAttribute(  );
            entryAdditionalAttribute.setIdEntry( daoUtil.getInt( 1 ) );
            entryAdditionalAttribute.setName( daoUtil.getString( 2 ) );
            entryAdditionalAttribute.setValue( daoUtil.getString( 3 ) );

            entryAdditionalAttributeList.add( entryAdditionalAttribute );
        }

        daoUtil.free(  );

        return entryAdditionalAttributeList;
    }

    /**
     * Delete   the additional entry whose identifier is specified in parameter
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
     * Update the additional entry in the table
     *
     * @param entryAdditionalAttribute instance of the EntryAdditionalAttribute object to update
     * @param plugin the plugin
     */
    public void store( EntryAdditionalAttribute entryAdditionalAttribute, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        daoUtil.setString( 1, entryAdditionalAttribute.getValue(  ) );
        daoUtil.setInt( 2, entryAdditionalAttribute.getIdEntry(  ) );
        daoUtil.setString( 3, entryAdditionalAttribute.getName(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
}
