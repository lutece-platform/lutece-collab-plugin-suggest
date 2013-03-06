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
import fr.paris.lutece.plugins.digglike.business.Digg;
import fr.paris.lutece.plugins.digglike.business.DiggHome;
import fr.paris.lutece.plugins.digglike.business.DiggSubmit;
import fr.paris.lutece.plugins.digglike.business.DiggSubmitHome;
import fr.paris.lutece.plugins.digglike.business.ResponseHome;
import fr.paris.lutece.plugins.digglike.business.SubmitFilter;
import fr.paris.lutece.plugins.digglike.utils.DiggUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;
import java.util.Locale;


/**
 *
 * DigglikeService
 *
 */
public class DigglikeService
{
    private static DigglikeService _singleton = new DigglikeService(  );
    private IDiggSubmitService _diggSubmitService = SpringContextService.getBean( DiggSubmitService.BEAN_SERVICE );

    /**
     * Initialize the Digg service
     *
     */
    public void init(  )
    {
        Digg.init(  );
    }

    /**
     * Returns the instance of the singleton
     *
     * @return The instance of the singleton
     */
    public static DigglikeService getInstance(  )
    {
        return _singleton;
    }

    /**
     * Update the display off all digg submit associated to a digg
     *
     * @param nIdDigg
     *            the id digg
     */
    public void updateAllDisplayOfDiggSubmit( Integer nIdDigg, Plugin plugin, Locale locale )
    {
        Digg digg = DiggHome.findByPrimaryKey( nIdDigg, plugin );
        SubmitFilter filter = new SubmitFilter(  );
        filter.setIdDigg( nIdDigg );

        List<Integer> listIdDiggSubmit = DiggSubmitHome.getDiggSubmitListId( filter, plugin );

        for ( Integer nIdDiggSubmit : listIdDiggSubmit )
        {
            updateDisplayDiggSubmit( nIdDiggSubmit, plugin, locale, digg );
        }
    }

    /**
     * update the display of a diggS
     *
     * @param diggSubmit
     */
    public void updateDisplayDiggSubmit( Integer nIdDiggSubmit, Plugin plugin, Locale locale, Digg digg )
    {
        DiggSubmit diggSubmit = DiggSubmitHome.findByPrimaryKey( nIdDiggSubmit, plugin );
        diggSubmit.setDigg( digg );

        SubmitFilter filter = new SubmitFilter(  );
        filter.setIdDiggSubmit( nIdDiggSubmit );
        // add responses
        diggSubmit.setResponses( ResponseHome.getResponseList( filter, plugin ) );
        // update Number of comment
        diggSubmit.setNumberComment( CommentSubmitHome.getCountCommentSubmit( filter, plugin ) );
        // update Number of Comment Enable
        filter.setIdCommentSubmitState( CommentSubmit.STATE_ENABLE );
        diggSubmit.setNumberCommentEnable( CommentSubmitHome.getCountCommentSubmit( filter, plugin ) );
        // update DiggSubmitValue
        diggSubmit.setDiggSubmitValue( DiggUtils.getHtmlDiggSubmitValue( diggSubmit, locale ) );
        // update DiggSubmitValue show in the list
        diggSubmit.setDiggSubmitValueShowInTheList( DiggUtils.getHtmlDiggSubmitValueShowInTheList( diggSubmit, locale ) );
        // update DiggSubmit title
        diggSubmit.setDiggSubmitTitle( DiggUtils.getDiggSubmitTitle( diggSubmit, locale ) );
        //update DiggSubmit
        _diggSubmitService.update( diggSubmit, plugin );
    }
}
