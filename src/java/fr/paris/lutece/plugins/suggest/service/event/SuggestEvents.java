/*
 * Copyright (c) 2002-2026, City of Paris
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
package fr.paris.lutece.plugins.suggest.service.event;

import fr.paris.lutece.portal.service.event.EventAction;
import fr.paris.lutece.portal.service.event.Type.TypeQualifier;
import fr.paris.lutece.portal.service.util.AppLogService;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.NotificationOptions;

import java.util.concurrent.RejectedExecutionException;

/**
 * Fires the events of the plugin to their asynchronous observers.
 */
public final class SuggestEvents
{
    /**
     * Private constructor.
     */
    private SuggestEvents( )
    {
    }

    /**
     * Fires an event to the asynchronous observers of its action. While the server stops, its executor refuses new tasks: the observers are then notified
     * in the current thread.
     *
     * @param <T>
     *            the event type
     * @param event
     *            the injected event
     * @param action
     *            the event action
     * @param payload
     *            the event payload
     */
    public static <T> void fireAsync( Event<T> event, EventAction action, T payload )
    {
        Event<T> qualifiedEvent = event.select( new TypeQualifier( action ) );

        try
        {
            qualifiedEvent.fireAsync( payload );
        }
        catch( RejectedExecutionException e )
        {
            AppLogService.info( "Executor unavailable (server stopping): {} notified in the current thread", payload.getClass( ).getSimpleName( ) );
            qualifiedEvent.fireAsync( payload, NotificationOptions.ofExecutor( Runnable::run ) );
        }
    }
}
