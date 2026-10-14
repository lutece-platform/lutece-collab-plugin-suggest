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
package fr.paris.lutece.plugins.suggest.business.rss;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.paris.lutece.portal.business.rss.IResourceRssType;
import fr.paris.lutece.portal.business.rss.ResourceRssType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

/**
 * Produces the RSS resource type of the suggest submits.
 */
@ApplicationScoped
public class SuggestResourceRssTypeProducer
{
    /**
     * Produces the suggest RSS resource type.
     *
     * @param strKey
     *            the resource type key
     * @param strTitleI18nKey
     *            the i18n key of the resource type title
     * @param strClassName
     *            the class of the RSS resource
     * @return the RSS resource type
     */
    @Produces
    @ApplicationScoped
    @Named( "suggest.resourceRssTypeSuggest" )
    public IResourceRssType produceResourceRssType( @ConfigProperty( name = "suggest.resourceRssTypeSuggest.key" ) String strKey,
            @ConfigProperty( name = "suggest.resourceRssTypeSuggest.titleI18nKey" ) String strTitleI18nKey,
            @ConfigProperty( name = "suggest.resourceRssTypeSuggest.className" ) String strClassName )
    {
        IResourceRssType resourceRssType = new ResourceRssType( );
        resourceRssType.setKey( strKey );
        resourceRssType.setTitleI18nKey( strTitleI18nKey );
        resourceRssType.setClassName( strClassName );

        return resourceRssType;
    }
}
