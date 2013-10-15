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
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


/**
 * class TagSubmitHome
 */
public final class TagSubmitHome
{
    // Static variable pointed at the DAO instance
    private static ITagSubmitDAO _dao = SpringContextService.getBean( "digglike.tagSubmitDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private TagSubmitHome(  )
    {
    }

    /**
     * Creation of an instance of tagSubmit
     *
     * @param tagSubmit The instance of the tagSubmit which contains the
     *            informations to store
     * @param plugin the Plugin
     *
     */
    public static void create( TagSubmit tagSubmit, Plugin plugin )
    {
        _dao.insert( tagSubmit, plugin );
    }

    /**
     * Update of the tagSubmit which is specified in parameter
     *
     * @param tagSubmit The instance of the tagSubmit which contains the
     *            informations to update
     * @param plugin the Plugin
     *
     */
    public static void update( TagSubmit tagSubmit, Plugin plugin )
    {
        _dao.store( tagSubmit, plugin );
    }

    /**
     * Remove the tagSubmit whose identifier is specified in parameter
     *
     * @param nIdTagSubmit The tagSubmitId
     * @param plugin the Plugin
     */
    public static void remove( int nIdTagSubmit, Plugin plugin )
    {
        _dao.delete( nIdTagSubmit, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a TagSubmitwhose identifier is specified in
     * parameter
     *
     * @param nKey The tagSubmit primary key
     * @param plugin the Plugin
     * @return an instance of TagSubmit
     */
    public static TagSubmit findByPrimaryKey( int nKey, Plugin plugin )
    {
        return _dao.load( nKey, plugin );
    }

    /**
     * Load the data of all the tagSubmit who verify the filter and returns them
     * in a list
     * @param filter the filter
     * @param plugin the plugin
     * @return the list of tagSubmit
     */
    public static List<TagSubmit> getTagSubmitList( SubmitFilter filter, Plugin plugin )
    {
        return _dao.selectListByFilter( filter, plugin );
    }

    /**
     * Load the number of all the tagSubmit who verify the filter
     * @param filter the filter
     * @param plugin the plugin
     * @return the number of all the tagSubmit who verify the filter
     */
    public static int getCountTagSubmit( SubmitFilter filter, Plugin plugin )
    {
        return _dao.selectCountByFilter( filter, plugin );
    }
}
