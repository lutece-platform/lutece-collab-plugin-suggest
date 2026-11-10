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
package fr.paris.lutece.plugins.suggest.web;

import java.util.Map;

import fr.paris.lutece.plugins.suggest.service.SuggestService;
import fr.paris.lutece.plugins.suggest.service.subscription.SuggestSubscriptionProviderService;
import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.portal.service.security.ISecurityTokenService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Holds, for one request, the subscription of the connected user to the new proposals of the default suggest.
 */
@RequestScoped
public class SuggestHeaderSubscription
{
    @Inject
    private SuggestService _suggestService;

    @Inject
    private SuggestSubscriptionProviderService _subscriptionProviderService;

    @Inject
    private ISecurityTokenService _securityTokenService;

    private Map<String, Object> _subscription;

    /**
     * Returns the subscription, read once per request.
     *
     * @param request
     *            the request
     * @return the id of the suggest, whether the user subscribed and the security token, or an empty map when nobody is connected or no suggest is the
     *         default one
     */
    public Map<String, Object> getSubscription( HttpServletRequest request )
    {
        if ( _subscription == null )
        {
            _subscription = load( request );
        }

        return _subscription;
    }

    /**
     * Reads the subscription of the connected user to the new proposals of the default suggest.
     *
     * @param request
     *            the request
     * @return the subscription, or an empty map when nobody is connected or no suggest is the default one
     */
    private Map<String, Object> load( HttpServletRequest request )
    {
        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );

        if ( user == null )
        {
            return Map.of( );
        }

        int nIdSuggest = _suggestService.getIdDefaultSuggest( );

        if ( nIdSuggest == SuggestUtils.CONSTANT_ID_NULL )
        {
            return Map.of( );
        }

        boolean bSubscribed = _subscriptionProviderService.hasUserSubscribedToSuggest( user, nIdSuggest );

        return Map.of( "idSuggest", nIdSuggest, "subscribed", bSubscribed, "token", _securityTokenService.getToken( request, SuggestApp.TOKEN_SUGGEST ) );
    }
}
