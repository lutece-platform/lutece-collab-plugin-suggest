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
package fr.paris.lutece.plugins.digglike.business.rss;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * class DirectoryResourceRssConfigDAO
 *
 */
public class DiggResourceRssConfigDAO implements IDiggResourceRssConfigDAO
{
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_rss,id_digg, is_submit_rss, id_digg_submit " +
        "FROM digglike_rss_cf  WHERE id_rss=?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO digglike_rss_cf( " +
        "id_rss,id_digg, is_submit_rss, id_digg_submit)" + "VALUES (?,?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE digglike_rss_cf " +
        "SET id_rss=?,id_digg=?,is_submit_rss=?,id_digg_submit=?" + " WHERE id_rss=?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM digglike_rss_cf WHERE id_rss=? ";
    private static final String SQL_QUERY_FIND_ALL = "SELECT id_rss,id_digg, is_submit_rss, id_digg_submit " +
        "FROM digglike_rss_cf";

    /**
     * Insert a new record in the table.
     * @param config The Instance of the object config
     * @param plugin the plugin
     */
    public synchronized void insert( DiggResourceRssConfig config, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        int nPos = 0;

        daoUtil.setInt( ++nPos, config.getIdRss(  ) );
        daoUtil.setInt( ++nPos, config.getIdDigg(  ) );
        daoUtil.setBoolean( ++nPos, config.isSubmitRss(  ) );
        daoUtil.setInt( ++nPos, config.getIdDiggSubmit(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Update the record in the table
     *
     * @param  config instance of config object to update
     * @param plugin the plugin
     */
    public void store( DiggResourceRssConfig config, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

        int nPos = 0;

        daoUtil.setInt( ++nPos, config.getIdRss(  ) );
        daoUtil.setInt( ++nPos, config.getIdDigg(  ) );
        daoUtil.setBoolean( ++nPos, config.isSubmitRss(  ) );
        daoUtil.setInt( ++nPos, config.getIdDiggSubmit(  ) );

        daoUtil.setInt( ++nPos, config.getIdRss(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * load the data of DiggResourceRssConfig from the table
     * @param nIdRss the Rss id
     * @param plugin the plugin
     * @return The Instance of the object DiggResourceRssConfig
     *
     */
    public DiggResourceRssConfig load( int nIdRss, Plugin plugin )
    {
        DiggResourceRssConfig config = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );

        daoUtil.setInt( 1, nIdRss );

        daoUtil.executeQuery(  );

        int nPos = 0;

        if ( daoUtil.next(  ) )
        {
            config = new DiggResourceRssConfig(  );
            config.setIdRss( daoUtil.getInt( ++nPos ) );
            config.setIdDigg( daoUtil.getInt( ++nPos ) );
            config.setSubmitRss( daoUtil.getBoolean( ++nPos ) );
            config.setIdDiggSubmit( daoUtil.getInt( ++nPos ) );
        }

        daoUtil.free(  );

        return config;
    }

    /**
     * Delete a record from the table
     * @param nIdRss The id of object DiggResourceRssConfig
     * @param plugin le plugin
     */
    public void delete( int nIdRss, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );

        daoUtil.setInt( 1, nIdRss );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * Return all record
     * @param plugin le plugin
     * @return List of DiggResourceRssConfig
     */
    public List<DiggResourceRssConfig> loadAll( Plugin plugin )
    {
        List<DiggResourceRssConfig> configList = new ArrayList<DiggResourceRssConfig>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_ALL, plugin );

        daoUtil.executeQuery(  );

        int nPos = 0;

        if ( daoUtil.next(  ) )
        {
            DiggResourceRssConfig config = new DiggResourceRssConfig(  );
            config.setIdRss( daoUtil.getInt( ++nPos ) );
            config.setIdDigg( daoUtil.getInt( ++nPos ) );
            config.setSubmitRss( daoUtil.getBoolean( ++nPos ) );
            config.setIdDiggSubmit( daoUtil.getInt( ++nPos ) );

            configList.add( config );
        }

        daoUtil.free(  );

        return configList;
    }
}
