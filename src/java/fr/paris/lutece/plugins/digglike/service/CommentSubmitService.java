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
package fr.paris.lutece.plugins.digglike.service;

import fr.paris.lutece.plugins.digglike.business.CommentSubmit;
import fr.paris.lutece.plugins.digglike.business.CommentSubmitHome;
import fr.paris.lutece.plugins.digglike.business.SubmitFilter;
import fr.paris.lutece.plugins.digglike.utils.DiggUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


public class CommentSubmitService implements ICommentSubmitService
{
    public static final String BEAN_SERVICE = "digglike.commentSubmitService";
    private static ICommentSubmitService _singleton;

    /**
    * {@inheritDoc}
    */
    @Override
    public void create( CommentSubmit commentSubmit, Plugin plugin )
    {
        commentSubmit.setDateModify( commentSubmit.getDateComment(  ) );
        CommentSubmitHome.create( commentSubmit, plugin );

        if ( commentSubmit.getIdParent(  ) != DiggUtils.CONSTANT_ID_NULL )
        {
            //update parent modification date
            CommentSubmitHome.updateDateModify( commentSubmit.getDateComment(  ), commentSubmit.getIdParent(  ), plugin );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update( CommentSubmit commentSubmit, Plugin plugin )
    {
        CommentSubmitHome.update( commentSubmit, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove( int nIdCommentSubmit, Plugin plugin )
    {
        CommentSubmitHome.remove( nIdCommentSubmit, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
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
        if ( !filter.containsSortBy(  ) )
        {
            //use default sort
            DiggUtils.initCommentFilterBySort( filter, DiggUtils.CONSTANT_ID_NULL );
        }

        //get All parent
        filter.setIdParent( SubmitFilter.ID_PARENT_NULL );

        List<CommentSubmit> commentSubmitList = CommentSubmitHome.getCommentSubmitList( filter, plugin );

        if ( commentSubmitList != null )
        {
            SubmitFilter subCommentFilter;

            for ( CommentSubmit c : commentSubmitList )
            {
                subCommentFilter = new SubmitFilter(  );
                //in this method we gonna get the list of children of a comment
                subCommentFilter.setIdParent( c.getIdCommentSubmit(  ) );
                subCommentFilter.setIdCommentSubmitState( filter.getIdCommentSubmitState(  ) );
                subCommentFilter.getSortBy(  ).add( SubmitFilter.SORT_BY_DATE_RESPONSE_DESC );
                c.setComments( CommentSubmitHome.getCommentSubmitList( subCommentFilter, plugin ) );
            }
        }

        filter.setIdParent( DiggUtils.CONSTANT_ID_NULL );

        return commentSubmitList;
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
     * Returns the instance of the singleton
     *
     * @return The instance of the singleton
     */
    public static ICommentSubmitService getService(  )
    {
        if ( _singleton == null )
        {
            _singleton = SpringContextService.getBean( BEAN_SERVICE );
        }

        return _singleton;
    }
}
