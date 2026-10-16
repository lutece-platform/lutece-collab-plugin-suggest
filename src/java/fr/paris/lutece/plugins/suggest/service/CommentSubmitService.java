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
package fr.paris.lutece.plugins.suggest.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.paris.lutece.plugins.suggest.business.CommentSubmit;
import fr.paris.lutece.plugins.suggest.business.CommentSubmitHome;
import fr.paris.lutece.plugins.suggest.business.SubmitFilter;
import fr.paris.lutece.plugins.suggest.service.event.CommentSubmitEvent;
import fr.paris.lutece.plugins.suggest.service.event.SuggestEvents;
import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.portal.service.event.EventAction;
import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class CommentSubmitService implements ICommentSubmitService
{
    private static final int MAX_ANCESTORS = 32;

    @Inject
    private Event<CommentSubmitEvent> _commentSubmitEvent;

    @Inject
    @ConfigProperty( name = "suggest.comments.maxDepth", defaultValue = "2" )
    private int _nMaxDepth;

    /**
     * {@inheritDoc}
     */
    @Override
    public void create( CommentSubmit commentSubmit, Plugin plugin )
    {
        commentSubmit.setDateModify( commentSubmit.getDateComment( ) );
        CommentSubmitHome.create( commentSubmit, plugin );

        if ( commentSubmit.getIdParent( ) != SuggestUtils.CONSTANT_ID_NULL )
        {
            // update parent modification date
            CommentSubmitHome.updateDateModify( commentSubmit.getDateComment( ), commentSubmit.getIdParent( ), plugin );
        }

        fireEvent( EventAction.CREATE, commentSubmit );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update( CommentSubmit commentSubmit, Plugin plugin )
    {
        CommentSubmitHome.update( commentSubmit, plugin );
        fireEvent( EventAction.UPDATE, commentSubmit );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove( int nIdCommentSubmit, Plugin plugin )
    {
        CommentSubmit commentSubmit = CommentSubmitHome.findByPrimaryKey( nIdCommentSubmit, plugin );
        List<CommentSubmit> listAnswers = getAnswers( nIdCommentSubmit, plugin );

        for ( int i = listAnswers.size( ) - 1; i >= 0; i-- )
        {
            CommentSubmitHome.remove( listAnswers.get( i ).getIdCommentSubmit( ), plugin );
        }

        CommentSubmitHome.remove( nIdCommentSubmit, plugin );

        if ( commentSubmit != null )
        {
            fireEvent( EventAction.REMOVE, commentSubmit );
        }
    }

    /**
     * Fires a comment event to the asynchronous observers of its action.
     *
     * @param action
     *            the event action
     * @param commentSubmit
     *            the comment
     */
    private void fireEvent( EventAction action, CommentSubmit commentSubmit )
    {
        int nIdSuggestSubmit = ( commentSubmit.getSuggestSubmit( ) != null ) ? commentSubmit.getSuggestSubmit( ).getIdSuggestSubmit( )
                : SuggestUtils.CONSTANT_ID_NULL;
        SuggestEvents.fireAsync( _commentSubmitEvent, action, new CommentSubmitEvent( commentSubmit.getIdCommentSubmit( ), nIdSuggestSubmit ) );
    }

    // /////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentSubmit findByPrimaryKey( int nKey, Plugin plugin )
    {
        return CommentSubmitHome.findByPrimaryKey( nKey, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CommentSubmit> getCommentSubmitList( SubmitFilter filter, Plugin plugin )
    {
        if ( !filter.containsSortBy( ) )
        {
            // use default sort
            SuggestUtils.initCommentFilterBySort( filter, SuggestUtils.CONSTANT_ID_NULL );
        }

        // get All parent
        filter.setIdParent( SubmitFilter.ID_PARENT_NULL );

        List<CommentSubmit> commentSubmitList = CommentSubmitHome.getCommentSubmitList( filter, null, plugin );

        if ( commentSubmitList != null )
        {
            for ( CommentSubmit c : commentSubmitList )
            {
                loadAnswers( c, filter.getIdCommentSubmitState( ), 2, plugin );
            }
        }

        filter.setIdParent( SuggestUtils.CONSTANT_ID_NULL );

        return commentSubmitList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CommentSubmit> getCommentSubmitList( SubmitFilter filter, Integer nLimitParentNumber, Plugin plugin )
    {
        if ( ( nLimitParentNumber == null ) || ( nLimitParentNumber == SuggestUtils.CONSTANT_ID_NULL ) )
        {
            // if the number of comment parent are not limited used getCommentSubmitList(filter, plugin)
            return getCommentSubmitList( filter, plugin );
        }

        if ( !filter.containsSortBy( ) )
        {
            // use default sort
            SuggestUtils.initCommentFilterBySort( filter, SuggestUtils.CONSTANT_ID_NULL );
        }

        // get All parent
        filter.setIdParent( SubmitFilter.ID_PARENT_NULL );

        return CommentSubmitHome.getCommentSubmitList( filter, nLimitParentNumber, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCountCommentSubmit( SubmitFilter filter, Plugin plugin )
    {
        return CommentSubmitHome.getCountCommentSubmit( filter, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CommentSubmit> getAnswers( int nIdCommentSubmit, Plugin plugin )
    {
        List<CommentSubmit> listAnswers = new ArrayList<>( );
        Set<Integer> setVisited = new HashSet<>( );
        setVisited.add( nIdCommentSubmit );
        addAnswers( nIdCommentSubmit, listAnswers, setVisited, plugin );

        return listAnswers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommentSubmit getDiscussionRoot( CommentSubmit commentSubmit, Plugin plugin )
    {
        CommentSubmit root = commentSubmit;

        for ( int i = 0; ( i < MAX_ANCESTORS ) && ( root.getIdParent( ) > SubmitFilter.ID_PARENT_NULL ); i++ )
        {
            CommentSubmit parent = CommentSubmitHome.findByPrimaryKey( root.getIdParent( ), plugin );

            if ( parent == null )
            {
                break;
            }

            root = parent;
        }

        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAnswerParent( CommentSubmit commentSubmit, Plugin plugin )
    {
        return getAnswerParent( commentSubmit, _nMaxDepth, plugin );
    }

    /**
     * Returns the comment an answer to a comment is stored under, for discussions of a given depth.
     *
     * @param commentSubmit
     *            the answered comment
     * @param nMaxDepth
     *            the number of levels of a discussion, its first comment included
     * @param plugin
     *            the plugin
     * @return the id of the parent of the answer
     */
    int getAnswerParent( CommentSubmit commentSubmit, int nMaxDepth, Plugin plugin )
    {
        return ( getDepth( commentSubmit, plugin ) < nMaxDepth ) ? commentSubmit.getIdCommentSubmit( ) : commentSubmit.getIdParent( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxDepth( )
    {
        return _nMaxDepth;
    }

    /**
     * Returns the level of a comment in its discussion, 1 for a comment that answers no other comment.
     *
     * @param commentSubmit
     *            the comment
     * @param plugin
     *            the plugin
     * @return the level of the comment
     */
    private static int getDepth( CommentSubmit commentSubmit, Plugin plugin )
    {
        int nDepth = 1;
        int nIdParent = commentSubmit.getIdParent( );

        while ( ( nIdParent > SubmitFilter.ID_PARENT_NULL ) && ( nDepth <= MAX_ANCESTORS ) )
        {
            CommentSubmit parent = CommentSubmitHome.findByPrimaryKey( nIdParent, plugin );

            if ( parent == null )
            {
                break;
            }

            nDepth++;
            nIdParent = parent.getIdParent( );
        }

        return nDepth;
    }

    /**
     * Loads the answers to a comment, then their own answers down to the deepest level of the discussions.
     *
     * @param commentSubmit
     *            the comment
     * @param nIdCommentSubmitState
     *            the state of the answers to load
     * @param nDepth
     *            the level of the answers
     * @param plugin
     *            the plugin
     */
    private void loadAnswers( CommentSubmit commentSubmit, int nIdCommentSubmitState, int nDepth, Plugin plugin )
    {
        SubmitFilter filter = new SubmitFilter( );
        filter.setIdParent( commentSubmit.getIdCommentSubmit( ) );
        filter.setIdCommentSubmitState( nIdCommentSubmitState );
        filter.getSortBy( ).add( SubmitFilter.SORT_BY_DATE_RESPONSE_DESC );

        List<CommentSubmit> listAnswers = CommentSubmitHome.getCommentSubmitList( filter, null, plugin );
        commentSubmit.setComments( listAnswers );

        if ( ( listAnswers != null ) && ( nDepth < _nMaxDepth ) )
        {
            for ( CommentSubmit answer : listAnswers )
            {
                loadAnswers( answer, nIdCommentSubmitState, nDepth + 1, plugin );
            }
        }
    }

    /**
     * Adds the answers to a comment, then their own answers, to a list.
     *
     * @param nIdCommentSubmit
     *            the id of the comment
     * @param listAnswers
     *            the list of the answers
     * @param setVisited
     *            the ids of the comments already listed
     * @param plugin
     *            the plugin
     */
    private static void addAnswers( int nIdCommentSubmit, List<CommentSubmit> listAnswers, Set<Integer> setVisited, Plugin plugin )
    {
        SubmitFilter filter = new SubmitFilter( );
        filter.setIdParent( nIdCommentSubmit );
        filter.getSortBy( ).add( SubmitFilter.SORT_BY_DATE_RESPONSE_ASC );

        for ( CommentSubmit answer : CommentSubmitHome.getCommentSubmitList( filter, null, plugin ) )
        {
            if ( setVisited.add( answer.getIdCommentSubmit( ) ) )
            {
                listAnswers.add( answer );
                addAnswers( answer.getIdCommentSubmit( ), listAnswers, setVisited, plugin );
            }
        }
    }
}
