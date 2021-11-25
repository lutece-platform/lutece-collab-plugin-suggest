/*
 * Copyright (c) 2002-2020, Mairie de Paris
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

import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for Entry objects
 */
public final class EntryHome
{
    // Static variable pointed at the DAO instance
    private static IEntryDAO _dao = SpringContextService.getBean( "suggest.entryDAO" );
    private static IEntryAdditionalAttributeDAO _daoAA = SpringContextService.getBean( "suggest.entryAdditionalAttributeDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private EntryHome( )
    {
    }

    /**
     * Creation of an instance of Entry
     *
     * @param entry
     *            The instance of the Entry which contains the informations to store
     * @param plugin
     *            the Plugin
     * @return The primary key of the new entry.
     */
    public static int create( IEntry entry, Plugin plugin )
    {
        int nPK;
        nPK = _dao.insert( entry, plugin );

        if ( entry.getEntryAdditionalAttributeList( ) != null )
        {
            for ( EntryAdditionalAttribute entryAdditionalAttribute : entry.getEntryAdditionalAttributeList( ) )
            {
                entryAdditionalAttribute.setIdEntry( nPK );
                _daoAA.insert( entryAdditionalAttribute, plugin );
            }
        }

        return nPK;
    }

    /**
     * Copy of an instance of Entry
     *
     * @param entry
     *            The instance of the Entry who must copy
     * @param plugin
     *            the Plugin
     *
     */
    public static void copy( IEntry entry, Plugin plugin )
    {
        IEntry entryCopy = entry;
        entryCopy.setIdEntry( create( entry, plugin ) );

        for ( RegularExpression regularExpression : entry.getRegularExpressionList( ) )
        {
            insertVerifyBy( entryCopy.getIdEntry( ), regularExpression.getIdExpression( ), plugin );
        }
    }

    /**
     * Update of the entry which is specified in parameter
     *
     * @param entry
     *            The instance of the Entry which contains the informations to update
     * @param plugin
     *            the Plugin
     *
     */
    public static void update( IEntry entry, Plugin plugin )
    {
        _dao.store( entry, plugin );

        if ( entry.getEntryAdditionalAttributeList( ) != null )
        {
            for ( EntryAdditionalAttribute entryAdditionalAttribute : entry.getEntryAdditionalAttributeList( ) )
            {
                entryAdditionalAttribute.setIdEntry( entry.getIdEntry( ) );
                _daoAA.store( entryAdditionalAttribute, plugin );
            }
        }
    }

    /**
     * Remove the entry whose identifier is specified in parameter
     *
     * @param nIdEntry
     *            The entry Id
     * @param plugin
     *            the Plugin
     */
    public static void remove( int nIdEntry, Plugin plugin )
    {
        _dao.delete( nIdEntry, plugin );
        _daoAA.delete( nIdEntry, plugin );
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a Entry whose identifier is specified in parameter
     *
     * @param nKey
     *            The entry primary key
     * @param plugin
     *            the Plugin
     * @return an instance of Entry
     */
    public static IEntry findByPrimaryKey( int nKey, Plugin plugin )
    {
        IEntry entry = _dao.load( nKey, plugin );
        List<RegularExpression> listRegularExpression = new ArrayList<>( );

        if ( RegularExpressionService.getInstance( ).isAvailable( ) )
        {
            List<Integer> listRegularExpressionKeyEntry = getListRegularExpressionKeyByIdEntry( nKey, plugin );

            if ( ( listRegularExpressionKeyEntry != null ) && ( !listRegularExpressionKeyEntry.isEmpty( ) ) )
            {
                RegularExpression regularExpression = null;

                for ( Integer regularExpressionKey : listRegularExpressionKeyEntry )
                {
                    regularExpression = RegularExpressionService.getInstance( ).getRegularExpressionByKey( regularExpressionKey );

                    if ( regularExpression != null )
                    {
                        listRegularExpression.add( regularExpression );
                    }
                }
            }
        }

        entry.setRegularExpressionList( listRegularExpression );

        List<EntryAdditionalAttribute> additionalAttribute = _daoAA.selectEntryAdditionalAttributeList( nKey, plugin );
        entry.setEntryAdditionalAttributeList( additionalAttribute );

        return entry;
    }

    /**
     * Load the data of all the entry who verify the filter and returns them in a list
     * 
     * @param filter
     *            the filter
     * @param plugin
     *            the plugin
     * @return the list of entry
     */
    public static List<IEntry> getEntryList( EntryFilter filter, Plugin plugin )
    {
        List<IEntry> entries = _dao.selectEntryListByFilter( filter, plugin );

        for ( IEntry e : entries )
        {
            e.setEntryAdditionalAttributeList( _daoAA.selectEntryAdditionalAttributeList( e.getIdEntry( ), plugin ) );
        }

        return entries;
    }

    /**
     * Return the number of entry who verify the filter
     * 
     * @param filter
     *            the filter
     * @param plugin
     *            the plugin
     * @return the number of entry who verify the filter
     */
    public static int getNumberEntryByFilter( EntryFilter filter, Plugin plugin )
    {
        return _dao.selectNumberEntryByFilter( filter, plugin );
    }

    /**
     * remove a regular expression in the entry
     *
     * @param nIdEntry
     *            The identifier of the entry
     * @param nIdExpression
     *            The identifier of the regular expression
     * @param plugin
     *            the plugin
     */
    public static void deleteVerifyBy( int nIdEntry, int nIdExpression, Plugin plugin )
    {
        _dao.deleteVerifyBy( nIdEntry, nIdExpression, plugin );
    }

    /**
     * insert a regular expression in the entry
     *
     * @param nIdEntry
     *            The identifier of the entry
     * @param nIdExpression
     *            The identifier of the regular expression
     * @param plugin
     *            the plugin
     */
    public static void insertVerifyBy( int nIdEntry, int nIdExpression, Plugin plugin )
    {
        _dao.insertVerifyBy( nIdEntry, nIdExpression, plugin );
    }

    /**
     * verify if the regular expresssion is use
     *
     * @param nIdExpression
     *            The identifier of the regular expression
     * @param plugin
     *            the plugin
     * @return true if the regular expression is use
     */
    public static boolean isRegularExpressionIsUse( int nIdExpression, Plugin plugin )
    {
        return _dao.isRegularExpressionIsUse( nIdExpression, plugin );
    }

    /**
     * Load the key of all the regularExpression associate to the entry and returns them in a list
     * 
     * @param nIdEntry
     *            the id of entry
     * @param plugin
     *            the plugin
     * @return the list of regular expression key
     */
    public static List<Integer> getListRegularExpressionKeyByIdEntry( int nIdEntry, Plugin plugin )
    {
        return _dao.selectListRegularExpressionKeyByIdEntry( nIdEntry, plugin );
    }
}
