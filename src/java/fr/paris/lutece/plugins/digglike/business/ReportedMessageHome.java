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

import java.util.List;

import fr.paris.lutece.plugins.digglike.utils.DiggUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;


/**
 *class ReportedMessageHome
 */
public final class ReportedMessageHome
{
    // Static variable pointed at the DAO instance
    private static IReportedMessageDAO _dao = (IReportedMessageDAO) SpringContextService.getBean(
            "digglike.reportedMessageDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private ReportedMessageHome(  )
    {
    }

    /**
     * Creation of an instance of reported Message
     *
     * @param reportedMessage The instance of the reported Message which contains the informations to store
     * @param plugin the Plugin
     *
     */
    public static void create( ReportedMessage reportedMessage, Plugin plugin )
    {
    	reportedMessage.setDateReported(DiggUtils.getCurrentDate(  ) );
        _dao.insert( reportedMessage, plugin );
    }

  

  
    /**
     * Remove the reportedMessage whose identifier is specified in parameter
     *
     * @param nIdDiggSubmit The nIdDiggSubmit
     * @param plugin the Plugin
     */
    public static void removeByDiggSubmit( int nIdDiggSubmit, Plugin plugin )
    {
        _dao.deleteByDiggSubmit( nIdDiggSubmit, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a reportedMessage whose  identifier is specified in parameter
     *
     * @param nKey The reportedMessage primary key
     * @param plugin the Plugin
     * @return an instance of commentSubmit
     */
    public static ReportedMessage findByPrimaryKey( int nKey, Plugin plugin )
    {
        return _dao.load( nKey, plugin );
    }

    /**
        * Load the data of all the reportedMessage who is associated to the nIdDiggSubmit
        * @param nIdDiggSubmit the nIdDiggSubmit
        * @param plugin the plugin
        * @return  the list of reportedMessage
        */
    public static List<ReportedMessage> getReportedMessageByDiggSubmit( int nIdDiggSubmit, Plugin plugin )
    {
        return _dao.selectListByDiggSubmit( nIdDiggSubmit, plugin );
    }

}
