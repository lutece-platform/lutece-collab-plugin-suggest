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
package fr.paris.lutece.plugins.suggest.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


public class ReportedMessageDAO implements IReportedMessageDAO
{
    // Constants
    private static final String SQL_FILTER_ID_SUGGEST_SUBMIT = " id_suggest_submit = ? ";
    private static final String SQL_FILTER_ID_REPORTED_MESSAGE = " id_reported_message = ? ";
    private static final String SQL_ORDER_BY_DATE = " ORDER BY date_reported DESC ";
    private static final String SQL_QUERY_NEW_PK = "SELECT MAX( id_reported_message ) FROM suggest_reported_message";
    private static final String SQL_QUERY_INSERT = "INSERT INTO suggest_reported_message ( id_reported_message,id_suggest_submit,date_reported,reported_value)" +
        " VALUES(?,?,?,?)";
    private static final String SQL_QUERY_FIND = "SELECT id_reported_message,id_suggest_submit,date_reported,reported_value " +
        "FROM suggest_reported_message WHERE ";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = SQL_QUERY_FIND + SQL_FILTER_ID_REPORTED_MESSAGE;
    private static final String SQL_QUERY_FIND_BY_SUGGEST_SUBMIT = SQL_QUERY_FIND + SQL_FILTER_ID_SUGGEST_SUBMIT +
        SQL_ORDER_BY_DATE;
    private static final String SQL_QUERY_DELETE_BY_SUGGEST_SUBMIT = "DELETE FROM suggest_reported_message WHERE " +
        SQL_FILTER_ID_SUGGEST_SUBMIT;

    /**
    * {@inheritDoc}
    */
    @Override
    public void insert( ReportedMessage reportedMessage, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        reportedMessage.setIdReported( newPrimaryKey( plugin ) );
        daoUtil.setInt( 1, reportedMessage.getIdReported(  ) );
        daoUtil.setInt( 2, reportedMessage.getSuggestSubmit(  ).getIdSuggestSubmit(  ) );
        daoUtil.setTimestamp( 3, reportedMessage.getDateReported(  ) );
        daoUtil.setString( 4, reportedMessage.getValue(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public ReportedMessage load( int nKey, Plugin plugin )
    {
        // TODO Auto-generated method stub
        ReportedMessage reportedMessage = null;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        daoUtil.setInt( 1, nKey );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            reportedMessage = getRow( daoUtil );
        }

        daoUtil.free(  );

        return reportedMessage;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void deleteBySuggestSubmit( int nIdSuggestSubmit, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_SUGGEST_SUBMIT, plugin );
        daoUtil.setInt( 1, nIdSuggestSubmit );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public List<ReportedMessage> selectListBySuggestSubmit( int nIdSuggestSubmit, Plugin plugin )
    {
        List<ReportedMessage> reportedMessageList = new ArrayList<ReportedMessage>(  );

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_SUGGEST_SUBMIT, plugin );
        daoUtil.setInt( 1, nIdSuggestSubmit );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            reportedMessageList.add( getRow( daoUtil ) );
        }

        daoUtil.free(  );

        return reportedMessageList;
    }

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
     * return  ReportedMessage
     * @param daoUtil ReportedMessage
     * @return ReportedMessage
     */
    private ReportedMessage getRow( DAOUtil daoUtil )
    {
        ReportedMessage reportedMessage = null;
        SuggestSubmit suggestSubmit = null;

        reportedMessage = new ReportedMessage(  );
        reportedMessage.setIdReported( daoUtil.getInt( 1 ) );

        suggestSubmit = new SuggestSubmit(  );
        suggestSubmit.setIdSuggestSubmit( daoUtil.getInt( 2 ) );
        reportedMessage.setSuggestSubmit( suggestSubmit );

        reportedMessage.setDateReported( daoUtil.getTimestamp( 3 ) );
        reportedMessage.setValue( daoUtil.getString( 4 ) );

        return reportedMessage;
    }
}
