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

import fr.paris.lutece.plugins.subscribe.service.ISubscriptionProviderService;
import fr.paris.lutece.plugins.suggest.service.subscription.SuggestSubscriptionProviderService;
import fr.paris.lutece.plugins.suggest.web.action.ISuggestAction;
import fr.paris.lutece.portal.business.rss.IResourceRssType;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.junit.jupiter.api.Test;

/**
 * Tests the CDI wiring of the plugin services.
 */
public class SuggestBeansTest extends LuteceTestCase
{
    @Inject
    private ISuggestSubmitService _suggestSubmitService;

    @Inject
    private ICommentSubmitService _commentSubmitService;

    @Inject
    private SuggestService _suggestService;

    @Inject
    private Instance<ISuggestAction> _suggestActions;

    @Inject
    private Instance<ISubscriptionProviderService> _subscriptionProviders;

    @Inject
    @Named( "suggest.resourceRssTypeSuggest" )
    private IResourceRssType _resourceRssType;

    /**
     * Checks that the services, the plugin actions, the subscription provider and the RSS type are beans.
     */
    @Test
    public void testBeans( )
    {
        assertNotNull( _suggestSubmitService );
        assertNotNull( _commentSubmitService );
        assertNotNull( _suggestService );
        assertEquals( 5, _suggestActions.stream( ).count( ) );
        assertTrue( _subscriptionProviders.stream( ).anyMatch( SuggestSubscriptionProviderService.class::isInstance ) );
        assertEquals( "ResourceRssTypeSuggest", _resourceRssType.getKey( ) );
        assertEquals( "suggest.resource_rss_type.label", _resourceRssType.getTitleI18nKey( ) );
    }
}
