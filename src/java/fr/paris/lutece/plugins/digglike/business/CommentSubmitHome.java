/*
 * Copyright (c) 2002-2012, Mairie de Paris
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

import fr.paris.lutece.plugins.digglike.utils.DiggUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;


/**
 *class CommentSubmitHome
 */
public final class CommentSubmitHome
{
    // Static variable pointed at the DAO instance
    private static ICommentSubmitDAO _dao = (ICommentSubmitDAO) SpringContextService.getPluginBean( "digglike",
            "digglike.commentSubmitDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private CommentSubmitHome(  )
    {
    }

    /**
     * Creation of an instance of commentSubmit
     *
     * @param commentSubmit The instance of the commentSubmit which contains the informations to store
     * @param plugin the Plugin
     *
     */
    public static void create( CommentSubmit commentSubmit, Plugin plugin )
    {
        _dao.insert( commentSubmit, plugin );
    }

    /**
     * Update of the commentSubmit which is specified in parameter
     *
     * @param commentSubmit The instance of the commentSubmit which contains the informations to update
     * @param plugin the Plugin
     *
     */
    public static void update( CommentSubmit commentSubmit, Plugin plugin )
    {
        _dao.store( commentSubmit, plugin );
    }

    /**
     * Update of the commentSubmit which is specified in parameter
     *
     * @param commentSubmit The instance of the commentSubmit which contains the informations to update
     * @param plugin the Plugin
     *
     */
    public static void updateDateModify( Timestamp dateModify, int idCommentSubmit, Plugin plugin )
    {
        _dao.storeDateModify( dateModify, idCommentSubmit, plugin );
    }

    /**
     * Remove the commentSubmit whose identifier is specified in parameter
     *
     * @param nIdCommentSubmit The commentSubmitId
     * @param plugin the Plugin
     */
    public static void remove( int nIdCommentSubmit, Plugin plugin )
    {
        _dao.delete( nIdCommentSubmit, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a CommentSubmitwhose identifier is specified in parameter
     *
     * @param nKey The commentSubmit primary key
     * @param plugin the Plugin
     * @return an instance of commentSubmit
     */
    public static CommentSubmit findByPrimaryKey( int nKey, Plugin plugin )
    {
        return _dao.load( nKey, plugin );
    }

    /**
        * Load the data of all the commentSubmit who verify the filter and returns them in a  list
        * @param filter the filter
        * @param plugin the plugin
        * @return  the list of commentSubmit
        */
    public static List<CommentSubmit> getCommentSubmitList( SubmitFilter filter, Plugin plugin )
    {
        return _dao.selectListByFilter( filter, plugin );
    }

    /**
     * Load the number of all the commentSubmit who verify the filter
     * @param filter the filter
     * @param plugin the plugin
     * @return  the number of all the commentSubmit who verify the filter
     */
    public static int getCountCommentSubmit( SubmitFilter filter, Plugin plugin )
    {
        return _dao.selectCountByFilter( filter, plugin );
    }
}
