/*
 * Copyright (c) 2002-2011, Mairie de Paris
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

import fr.paris.lutece.portal.service.html.XmlTransformerService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;


/**
 * This class provides instances management methods (create, find, ...) for ExportFormat objects
 */
public final class ExportFormatHome
{
    // Static variable pointed at the DAO instance
    private static IExportFormatDAO _dao = (IExportFormatDAO) SpringContextService.getPluginBean( "digglike",
            "digglike.exportFormatDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private ExportFormatHome(  )
    {
    }

    /**
     * Creation of an instance of ExportFormat
     *
     * @param exportFormat The instance of the ExportFormat which contains the informations to store
     * @param plugin the Plugin
     *
     */
    public static void create( ExportFormat exportFormat, Plugin plugin )
    {
        _dao.insert( exportFormat, plugin );
    }

    /**
     * Update of the ExportFormat which is specified in parameter
     *
     * @param exportFormat The instance of the ExportFormat which contains the informations to update
     * @param plugin the Plugin
     *
     */
    public static void update( ExportFormat exportFormat, Plugin plugin )
    {
        _dao.store( exportFormat, plugin );
        XmlTransformerService.clearXslCache(  );
    }

    /**
     * Remove the ExportFormat whose identifier is specified in parameter
     *
     * @param nIdExport The exportFormat Id
     * @param plugin the Plugin
     */
    public static void remove( int nIdExport, Plugin plugin )
    {
        _dao.delete( nIdExport, plugin );
        XmlTransformerService.clearXslCache(  );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a ExportFormat whose identifier is specified in parameter
     *
     * @param nKey The exportFormat primary key
     * @param plugin the Plugin
     * @return an instance of ExportFormat
     */
    public static ExportFormat findByPrimaryKey( int nKey, Plugin plugin )
    {
        return _dao.load( nKey, plugin );
    }

    /**
     * Loads the data of all the exportFormat and returns them in a list
     *
     * @param plugin the Plugin
     * @return the list which contains the data of all the ExportFormat
     */
    public static List<ExportFormat> getList( Plugin plugin )
    {
        return _dao.selectListExport( plugin );
    }

    /**
     * Load the data of all the export format returns them in a  reference list
     * @param plugin the plugin
     * @return  a  reference list of export format
     */
    public static ReferenceList getListExport( Plugin plugin )
    {
        return _dao.getListExport( plugin );
    }
}
