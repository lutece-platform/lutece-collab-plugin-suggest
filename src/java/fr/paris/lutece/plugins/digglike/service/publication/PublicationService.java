/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.digglike.service.publication;

import fr.paris.lutece.plugins.digglike.business.Digg;
import fr.paris.lutece.plugins.digglike.business.DiggFilter;
import fr.paris.lutece.plugins.digglike.business.DiggHome;
import fr.paris.lutece.plugins.digglike.business.DiggSubmit;
import fr.paris.lutece.plugins.digglike.business.DiggSubmitHome;
import fr.paris.lutece.plugins.digglike.business.DiggSubmitState;
import fr.paris.lutece.plugins.digglike.business.DiggSubmitStateHome;
import fr.paris.lutece.plugins.digglike.business.SubmitFilter;
import fr.paris.lutece.plugins.digglike.service.DigglikePlugin;
import fr.paris.lutece.plugins.digglike.utils.DiggUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;

import java.sql.Timestamp;

import java.util.List;


/**
 *
 * PublicationService
 *
 */
public final class PublicationService
{
    /**
    * PublicationService
    *
    */
    private PublicationService(  )
    {
    }

    /**
     * disable all digg submit not voted during the period
     * specified in the digg
     *
     */
    public static void publication(  )
    {
        Plugin plugin = PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME );
        Timestamp dateCreationMin;
        Timestamp currentDate = DiggUtils.getCurrentDate(  );
        List<DiggSubmit> listDiggSubmit;
        DiggSubmitState diggSubmitStateDisable = DiggSubmitStateHome.findByNumero( DiggSubmit.STATE_DISABLE, plugin );
        DiggSubmitState diggSubmitStatePublish = DiggSubmitStateHome.findByNumero( DiggSubmit.STATE_PUBLISH, plugin );

        DiggFilter diggFilter = new DiggFilter(  );

        diggFilter.setIdState( Digg.STATE_ENABLE );

        List<Digg> listDigg = DiggHome.getDiggList( diggFilter, plugin );

        SubmitFilter submitFilter = new SubmitFilter(  );
        submitFilter.setIdDiggSubmitState( diggSubmitStatePublish.getIdDiggSubmitState(  ) );
        submitFilter.setNumberVote( 0 );

        for ( Digg digg : listDigg )
        {
            if ( digg.getNumberDayRequired(  ) > 0 )
            {
                dateCreationMin = DiggUtils.getDateAfterNDay( currentDate, -digg.getNumberDayRequired(  ) );
                submitFilter.setIdDigg( digg.getIdDigg(  ) );
                submitFilter.setDateLast( dateCreationMin );
                listDiggSubmit = DiggSubmitHome.getDiggSubmitList( submitFilter, plugin );

                for ( DiggSubmit diggSubmit : listDiggSubmit )
                {
                    diggSubmit = DiggSubmitHome.findByPrimaryKey( diggSubmit.getIdDiggSubmit(  ), plugin );
                    diggSubmit.setDiggSubmitState( diggSubmitStateDisable );
                    DiggSubmitHome.update( diggSubmit, plugin );
                    DiggUtils.sendNotificationNewDiggSubmitDisable( digg, diggSubmit, I18nService.getDefaultLocale(  ) );
                }
            }
        }
    }
}
