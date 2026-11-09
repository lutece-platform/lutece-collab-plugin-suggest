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

import fr.paris.lutece.plugins.suggest.business.SuggestSubmitComplement;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitComplementHome;
import fr.paris.lutece.plugins.suggest.service.event.SuggestEvents;
import fr.paris.lutece.plugins.suggest.service.event.SuggestSubmitComplementEvent;
import fr.paris.lutece.portal.service.event.EventAction;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import java.util.List;

/**
 * Adds, lists and removes the complements the authors write below their suggest submits.
 */
@ApplicationScoped
public class SuggestSubmitComplementService
{
    @Inject
    private Event<SuggestSubmitComplementEvent> _complementEvent;

    /**
     * Adds a complement to a suggest submit and fires its creation event.
     *
     * @param nIdSuggestSubmit
     *            the suggest submit id
     * @param strValue
     *            the text
     * @return the created complement
     */
    public SuggestSubmitComplement addComplement( int nIdSuggestSubmit, String strValue )
    {
        SuggestSubmitComplement complement = new SuggestSubmitComplement( );
        complement.setIdSuggestSubmit( nIdSuggestSubmit );
        complement.setValue( strValue.trim( ) );
        SuggestSubmitComplementHome.create( complement, getPlugin( ) );
        SuggestEvents.fireAsync( _complementEvent, EventAction.CREATE, new SuggestSubmitComplementEvent( complement.getIdComplement( ), nIdSuggestSubmit ) );

        return complement;
    }

    /**
     * Removes a complement and fires its removal event.
     *
     * @param complement
     *            the complement
     */
    public void removeComplement( SuggestSubmitComplement complement )
    {
        SuggestSubmitComplementHome.remove( complement.getIdComplement( ), getPlugin( ) );
        SuggestEvents.fireAsync( _complementEvent, EventAction.REMOVE,
                new SuggestSubmitComplementEvent( complement.getIdComplement( ), complement.getIdSuggestSubmit( ) ) );
    }

    /**
     * Returns a complement.
     *
     * @param nIdComplement
     *            the complement id
     * @return the complement, or null
     */
    public SuggestSubmitComplement findByPrimaryKey( int nIdComplement )
    {
        return SuggestSubmitComplementHome.findByPrimaryKey( nIdComplement, getPlugin( ) );
    }

    /**
     * Lists the complements of a suggest submit, oldest first.
     *
     * @param nIdSuggestSubmit
     *            the suggest submit id
     * @return the complements
     */
    public List<SuggestSubmitComplement> getComplements( int nIdSuggestSubmit )
    {
        return SuggestSubmitComplementHome.findBySuggestSubmit( nIdSuggestSubmit, getPlugin( ) );
    }

    /**
     * Returns the suggest plugin.
     *
     * @return the plugin
     */
    private static Plugin getPlugin( )
    {
        return PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME );
    }
}
