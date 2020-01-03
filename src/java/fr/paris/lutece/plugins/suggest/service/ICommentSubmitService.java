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
package fr.paris.lutece.plugins.suggest.service;

import fr.paris.lutece.plugins.suggest.business.CommentSubmit;
import fr.paris.lutece.plugins.suggest.business.SubmitFilter;
import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

public interface ICommentSubmitService
{
    /**
     * Creation of an instance of commentSubmit
     *
     * @param commentSubmit
     *            The instance of the commentSubmit which contains the informations to store
     * @param plugin
     *            the Plugin
     *
     */
    @Transactional( "suggest.transactionManager" )
    void create( CommentSubmit commentSubmit, Plugin plugin );

    /**
     * Update of the commentSubmit which is specified in parameter
     *
     * @param commentSubmit
     *            The instance of the commentSubmit which contains the informations to update
     * @param plugin
     *            the Plugin
     *
     */
    @Transactional( "suggest.transactionManager" )
    void update( CommentSubmit commentSubmit, Plugin plugin );

    /**
     * Remove the commentSubmit whose identifier is specified in parameter
     *
     * @param nIdCommentSubmit
     *            The commentSubmitId
     * @param plugin
     *            the Plugin
     */
    @Transactional( "suggest.transactionManager" )
    void remove( int nIdCommentSubmit, Plugin plugin );

    /**
     * Returns an instance of a CommentSubmitwhose identifier is specified in parameter
     *
     * @param nKey
     *            The commentSubmit primary key
     * @param plugin
     *            the Plugin
     * @return an instance of commentSubmit
     */
    @Transactional( "suggest.transactionManager" )
    CommentSubmit findByPrimaryKey( int nKey, Plugin plugin );

    /**
     * Load the data of all the commentSubmit who verify the filter and returns them in a list
     * 
     * @param filter
     *            the filter
     * @param plugin
     *            the plugin
     * @return the list of commentSubmit
     */
    @Transactional( "suggest.transactionManager" )
    List<CommentSubmit> getCommentSubmitList( SubmitFilter filter, Plugin plugin );

    /**
     * Load the data of all the commentSubmit who verify the filter and returns them in a list
     * 
     * @param filter
     *            the filter
     * @param nLimitParentNumber
     *            limit the number of parent comment return
     * @param plugin
     *            the plugin
     * @return the list of commentSubmit
     */
    @Transactional( "suggest.transactionManager" )
    List<CommentSubmit> getCommentSubmitList( SubmitFilter filter, Integer nLimitParentNumber, Plugin plugin );

    /**
     * Load the number of all the commentSubmit who verify the filter
     * 
     * @param filter
     *            the filter
     * @param plugin
     *            the plugin
     * @return the number of all the commentSubmit who verify the filter
     */
    @Transactional( "suggest.transactionManager" )
    int getCountCommentSubmit( SubmitFilter filter, Plugin plugin );
}
