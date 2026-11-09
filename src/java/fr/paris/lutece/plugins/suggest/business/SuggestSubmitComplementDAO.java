/*
 * Copyright (c) 2002-2025, City of Paris
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
 * Data access to the complements of the suggest submits.
 */
@ApplicationScoped
public class SuggestSubmitComplementDAO implements ISuggestSubmitComplementDAO
{
    private static final String SQL_QUERY_INSERT = "INSERT INTO suggest_suggest_submit_complement ( id_suggest_submit, date_complement, complement_value ) VALUES ( ?, ?, ? )";
    private static final String SQL_QUERY_SELECT = "SELECT id_complement, id_suggest_submit, date_complement, complement_value FROM suggest_suggest_submit_complement ";
    private static final String SQL_QUERY_SELECT_BY_PRIMARY_KEY = SQL_QUERY_SELECT + "WHERE id_complement = ?";
    private static final String SQL_QUERY_SELECT_BY_SUGGEST_SUBMIT = SQL_QUERY_SELECT + "WHERE id_suggest_submit = ? ORDER BY date_complement, id_complement";
    private static final String SQL_QUERY_DELETE = "DELETE FROM suggest_suggest_submit_complement WHERE id_complement = ?";
    private static final String SQL_QUERY_DELETE_BY_SUGGEST_SUBMIT = "DELETE FROM suggest_suggest_submit_complement WHERE id_suggest_submit = ?";

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert( SuggestSubmitComplement complement, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;
            daoUtil.setInt( nIndex++, complement.getIdSuggestSubmit( ) );
            daoUtil.setTimestamp( nIndex++, complement.getDateComplement( ) );
            daoUtil.setString( nIndex, complement.getValue( ) );
            daoUtil.executeUpdate( );

            if ( daoUtil.nextGeneratedKey( ) )
            {
                complement.setIdComplement( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SuggestSubmitComplement load( int nIdComplement, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_PRIMARY_KEY, plugin ) )
        {
            daoUtil.setInt( 1, nIdComplement );
            daoUtil.executeQuery( );

            return daoUtil.next( ) ? getRow( daoUtil ) : null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int nIdComplement, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdComplement );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteBySuggestSubmit( int nIdSuggestSubmit, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_SUGGEST_SUBMIT, plugin ) )
        {
            daoUtil.setInt( 1, nIdSuggestSubmit );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SuggestSubmitComplement> selectBySuggestSubmit( int nIdSuggestSubmit, Plugin plugin )
    {
        List<SuggestSubmitComplement> listComplements = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_SUGGEST_SUBMIT, plugin ) )
        {
            daoUtil.setInt( 1, nIdSuggestSubmit );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listComplements.add( getRow( daoUtil ) );
            }
        }

        return listComplements;
    }

    /**
     * Reads the current row.
     *
     * @param daoUtil
     *            the DAOUtil on a row
     * @return the complement
     */
    private static SuggestSubmitComplement getRow( DAOUtil daoUtil )
    {
        int nIndex = 1;
        SuggestSubmitComplement complement = new SuggestSubmitComplement( );
        complement.setIdComplement( daoUtil.getInt( nIndex++ ) );
        complement.setIdSuggestSubmit( daoUtil.getInt( nIndex++ ) );
        complement.setDateComplement( daoUtil.getTimestamp( nIndex++ ) );
        complement.setValue( daoUtil.getString( nIndex ) );

        return complement;
    }
}
