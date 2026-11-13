/*
 * Copyright (c) 2002-2026, City of Paris
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
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access to the attachments of the suggest submits.
 */
@ApplicationScoped
public class SuggestSubmitAttachmentDAO implements ISuggestSubmitAttachmentDAO
{
    private static final String SQL_QUERY_INSERT = "INSERT INTO suggest_suggest_submit_attachment ( id_suggest_submit, id_entry, file_key, file_name, mime_type, file_size ) VALUES ( ?, ?, ?, ?, ?, ? )";
    private static final String SQL_QUERY_SELECT = "SELECT id_attachment, id_suggest_submit, id_entry, file_key, file_name, mime_type, file_size FROM suggest_suggest_submit_attachment ";
    private static final String SQL_QUERY_SELECT_BY_PRIMARY_KEY = SQL_QUERY_SELECT + "WHERE id_attachment = ?";
    private static final String SQL_QUERY_SELECT_BY_SUGGEST_SUBMIT = SQL_QUERY_SELECT + "WHERE id_suggest_submit = ? ORDER BY id_attachment";
    private static final String SQL_QUERY_DELETE = "DELETE FROM suggest_suggest_submit_attachment WHERE id_attachment = ?";

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert( SuggestSubmitAttachment attachment, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, attachment.getIdSuggestSubmit( ) );
            daoUtil.setInt( nIndex++, attachment.getIdEntry( ) );
            daoUtil.setString( nIndex++, attachment.getFileKey( ) );
            daoUtil.setString( nIndex++, attachment.getFileName( ) );
            daoUtil.setString( nIndex++, attachment.getMimeType( ) );
            daoUtil.setLong( nIndex, attachment.getSize( ) );
            daoUtil.executeUpdate( );

            if ( daoUtil.nextGeneratedKey( ) )
            {
                attachment.setIdAttachment( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SuggestSubmitAttachment load( int nIdAttachment, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_PRIMARY_KEY, plugin ) )
        {
            daoUtil.setInt( 1, nIdAttachment );
            daoUtil.executeQuery( );

            return daoUtil.next( ) ? getRow( daoUtil ) : null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdAttachment, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdAttachment );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SuggestSubmitAttachment> selectBySuggestSubmit( int nIdSuggestSubmit, Plugin plugin )
    {
        List<SuggestSubmitAttachment> listAttachments = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_SUGGEST_SUBMIT, plugin ) )
        {
            daoUtil.setInt( 1, nIdSuggestSubmit );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listAttachments.add( getRow( daoUtil ) );
            }
        }

        return listAttachments;
    }

    /**
     * Reads the current row.
     *
     * @param daoUtil
     *            the DAOUtil on a row
     * @return the attachment
     */
    private static SuggestSubmitAttachment getRow( DAOUtil daoUtil )
    {
        int nIndex = 1;
        SuggestSubmitAttachment attachment = new SuggestSubmitAttachment( );
        attachment.setIdAttachment( daoUtil.getInt( nIndex++ ) );
        attachment.setIdSuggestSubmit( daoUtil.getInt( nIndex++ ) );
        attachment.setIdEntry( daoUtil.getInt( nIndex++ ) );
        attachment.setFileKey( daoUtil.getString( nIndex++ ) );
        attachment.setFileName( daoUtil.getString( nIndex++ ) );
        attachment.setMimeType( daoUtil.getString( nIndex++ ) );
        attachment.setSize( daoUtil.getLong( nIndex ) );

        return attachment;
    }
}
