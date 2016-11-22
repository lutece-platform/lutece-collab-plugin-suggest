/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.suggest.service.publication;

import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestFilter;
import fr.paris.lutece.plugins.suggest.business.SuggestHome;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitState;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitStateHome;
import fr.paris.lutece.plugins.suggest.business.SubmitFilter;
import fr.paris.lutece.plugins.suggest.service.SuggestSubmitService;
import fr.paris.lutece.plugins.suggest.service.SuggestPlugin;
import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
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
     * disable all suggest submit not voted during the period
     * specified in the suggest
     *
     */
    public static void publication(  )
    {
        Plugin plugin = PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME );
        Timestamp dateCreationMin;
        Timestamp currentDate = SuggestUtils.getCurrentDate(  );
        List<SuggestSubmit> listSuggestSubmit;
        SuggestSubmitState suggestSubmitStateDisable = SuggestSubmitStateHome.findByNumero( SuggestSubmit.STATE_DISABLE, plugin );
        SuggestSubmitState suggestSubmitStatePublish = SuggestSubmitStateHome.findByNumero( SuggestSubmit.STATE_PUBLISH, plugin );

        SuggestFilter suggestFilter = new SuggestFilter(  );

        suggestFilter.setIdState( Suggest.STATE_ENABLE );

        List<Suggest> listSuggest = SuggestHome.getSuggestList( suggestFilter, plugin );

        SubmitFilter submitFilter = new SubmitFilter(  );
        submitFilter.setIdSuggestSubmitState( suggestSubmitStatePublish.getIdSuggestSubmitState(  ) );
        submitFilter.setNumberVote( 0 );

        for ( Suggest suggest : listSuggest )
        {
            if ( suggest.getNumberDayRequired(  ) > 0 )
            {
                dateCreationMin = SuggestUtils.getDateAfterNDay( currentDate, -suggest.getNumberDayRequired(  ) );
                submitFilter.setIdSuggest( suggest.getIdSuggest(  ) );
                submitFilter.setDateLast( dateCreationMin );
                listSuggestSubmit = SuggestSubmitService.getService(  ).getSuggestSubmitList( submitFilter, plugin );

                for ( SuggestSubmit suggestSubmit : listSuggestSubmit )
                {
                    suggestSubmit = SuggestSubmitService.getService(  )
                                                  .findByPrimaryKey( suggestSubmit.getIdSuggestSubmit(  ), false, plugin );
                    suggestSubmit.setSuggestSubmitState( suggestSubmitStateDisable );
                    SuggestSubmitService.getService(  ).update( suggestSubmit, plugin );
                    SuggestUtils.sendNotificationNewSuggestSubmitDisable( suggest, suggestSubmit, I18nService.getDefaultLocale(  ) );
                }
            }
        }
    }
}
