/*
 * Copyright (c) 2002-2020, Mairie de Paris
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
package fr.paris.lutece.plugins.suggest.service.subscription;

import fr.paris.lutece.plugins.suggest.business.Category;
import fr.paris.lutece.plugins.suggest.business.CategoryHome;
import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestHome;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.suggest.service.SuggestSubmitService;
import fr.paris.lutece.plugins.suggest.service.SuggestPlugin;
import fr.paris.lutece.plugins.subscribe.business.Subscription;
import fr.paris.lutece.plugins.subscribe.business.SubscriptionFilter;
import fr.paris.lutece.plugins.subscribe.service.ISubscriptionProviderService;
import fr.paris.lutece.plugins.subscribe.service.SubscriptionService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Locale;

/**
 * Subscription provider for suggest and suggest submit
 */
public class SuggestSubscriptionProviderService implements ISubscriptionProviderService
{
    /**
     * Name of the bean of the SuggestSubscriptionProviderService
     */
    public static final String BEAN_NAME = "suggest.suggestSubscriptionProviderService";
    public static final String SUBSCRIPTION_SUGGEST = "suggest";
    public static final String SUBSCRIPTION_SUGGEST_CATEGORY = "suggest_category";
    public static final String SUBSCRIPTION_SUGGEST_SUBMIT = "suggest_submit";
    private static final String SUBSCRIPTION_PROVIDER_NAME = "suggest.suggestSubscriptionProviderService";
    private static final String MESSAGE_SUBSCRIBED_SUGGEST = "suggest.message.subscriptions.subscribedSuggest";
    private static final String MESSAGE_SUBSCRIBED_SUGGEST_SUBMIT = "suggest.message.subscriptions.subscribedSuggestSubmit";
    private static final String MESSAGE_SUBSCRIBED_SUGGEST_CATEGORY = "suggest.message.subscriptions.subscribedCategory";
    private static SuggestSubscriptionProviderService _instance;

    /**
     * Returns the instance of the singleton
     *
     * @return The instance of the singleton
     */
    public static SuggestSubscriptionProviderService getService( )
    {
        if ( _instance == null )
        {
            synchronized( SuggestSubscriptionProviderService.class )
            {
                _instance = SpringContextService.getBean( BEAN_NAME );
            }
        }

        return _instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProviderName( )
    {
        return SUBSCRIPTION_PROVIDER_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSubscriptionHtmlDescription( LuteceUser user, String strSubscriptionKey, String strIdSubscribedResource, Locale locale )
    {
        int nId = ( ( strIdSubscribedResource != null ) && StringUtils.isNumeric( strIdSubscribedResource ) ) ? Integer.parseInt( strIdSubscribedResource ) : 0;

        if ( nId > 0 )
        {
            if ( StringUtils.equals( SUBSCRIPTION_SUGGEST, strSubscriptionKey ) )
            {
                Suggest suggest = SuggestHome.findByPrimaryKey( nId, PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME ) );

                if ( suggest != null )
                {
                    Object [ ] params = {
                        suggest.getTitle( )
                    };

                    return I18nService.getLocalizedString( MESSAGE_SUBSCRIBED_SUGGEST, params, locale );
                }
            }
            else
                if ( StringUtils.equals( SUBSCRIPTION_SUGGEST_SUBMIT, strSubscriptionKey ) )
                {
                    SuggestSubmit suggestSubmit = SuggestSubmitService.getService( ).findByPrimaryKey( nId, false,
                            PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME ) );

                    if ( suggestSubmit != null )
                    {
                        Object [ ] params = {
                            suggestSubmit.getSuggestSubmitTitle( )
                        };

                        return I18nService.getLocalizedString( MESSAGE_SUBSCRIBED_SUGGEST_SUBMIT, params, locale );
                    }
                }
                else
                    if ( StringUtils.equals( SUBSCRIPTION_SUGGEST_CATEGORY, strSubscriptionKey ) )
                    {
                        Category category = CategoryHome.findByPrimaryKey( nId, PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME ) );

                        if ( category != null )
                        {
                            Object [ ] params = {
                                category.getTitle( )
                            };

                            return I18nService.getLocalizedString( MESSAGE_SUBSCRIBED_SUGGEST_CATEGORY, params, locale );
                        }
                    }
        }

        return StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSubscriptionRemovable( LuteceUser user, String strSubscriptionKey, String strIdSubscribedResource )
    {
        // Subscriptions for suggests or suggest submits are always removable
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrlModifySubscription( LuteceUser user, String strSubscriptionKey, String strIdSubscribedResource )
    {
        // No subscription modification for suggest nor suggest submits
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifySubscriptionRemoval( Subscription subscription )
    {
        // Do nothing
    }

    /**
     * Remove a subscription of a user to a suggest
     * 
     * @param user
     *            The user to remove the subscription of
     * @param nIdSuggest
     *            The id of the suggest
     */
    public void removeSuggestSubscription( LuteceUser user, int nIdSuggest )
    {
        removeSuggestSubmitSubscription( user, nIdSuggest, SUBSCRIPTION_SUGGEST );
    }

    /**
     * Remove a subscription of a user to a suggest category
     * 
     * @param user
     *            The user to remove the subscription of
     * @param nIdCategory
     *            The id of the suggest category
     */
    public void removeSuggestCategorySubscription( LuteceUser user, int nIdCategory )
    {
        removeSuggestSubmitSubscription( user, nIdCategory, SUBSCRIPTION_SUGGEST_CATEGORY );
    }

    /**
     * Remove a subscription of a user to a suggest submit
     * 
     * @param user
     *            The user to remove the subscription of
     * @param nIdSuggestSubmit
     *            The id of the suggest submit
     */
    public void removeSuggestSubmitSubscription( LuteceUser user, int nIdSuggestSubmit )
    {
        removeSuggestSubmitSubscription( user, nIdSuggestSubmit, SUBSCRIPTION_SUGGEST_SUBMIT );
    }

    /**
     * Remove a subscription to a suggest resource
     * 
     * @param user
     *            The user to remove the subscription of
     * @param nId
     *            The id of the subscribed resource
     * @param strSubscriptionKey
     *            The subscription key
     */
    private void removeSuggestSubmitSubscription( LuteceUser user, int nId, String strSubscriptionKey )
    {
        SubscriptionFilter filter = new SubscriptionFilter( user.getName( ), getProviderName( ), strSubscriptionKey, Integer.toString( nId ) );

        List<Subscription> listSubscription = SubscriptionService.getInstance( ).findByFilter( filter );

        if ( ( listSubscription != null ) && ( listSubscription.size( ) > 0 ) )
        {
            for ( Subscription subscription : listSubscription )
            {
                SubscriptionService.getInstance( ).removeSubscription( subscription, false );
            }
        }
    }

    /**
     * Create a new subscription to a suggest
     * 
     * @param user
     *            The user to create a subscription of
     * @param nIdSuggest
     *            The id of the suggest to subscribe to
     */
    public void createSuggestSubscription( LuteceUser user, int nIdSuggest )
    {
        if ( !hasUserSubscribedToResource( user, nIdSuggest, SUBSCRIPTION_SUGGEST ) )
        {
            createSubscription( user, nIdSuggest, SUBSCRIPTION_SUGGEST );
        }
    }

    /**
     * Create a new subscription to a suggest
     * 
     * @param user
     *            The user to create a subscription of
     * @param nIdSuggest
     *            The id of the suggest to subscribe to
     */
    public void createSuggestCategorySubscription( LuteceUser user, int nIdSuggest )
    {
        if ( !hasUserSubscribedToResource( user, nIdSuggest, SUBSCRIPTION_SUGGEST_CATEGORY ) )
        {
            createSubscription( user, nIdSuggest, SUBSCRIPTION_SUGGEST_CATEGORY );
        }
    }

    /**
     * Create a new subscription to a suggest submit
     * 
     * @param user
     *            The user to create a subscription of
     * @param nIdSuggestSubmit
     *            The id of the suggest submit to subscribe to
     */
    public void createSuggestSubmitSubscription( LuteceUser user, int nIdSuggestSubmit )
    {
        if ( !hasUserSubscribedToResource( user, nIdSuggestSubmit, SUBSCRIPTION_SUGGEST_SUBMIT ) )
        {
            createSubscription( user, nIdSuggestSubmit, SUBSCRIPTION_SUGGEST_SUBMIT );
        }
    }

    private void createSubscription( LuteceUser user, int nId, String strSubscriptionKey )
    {
        Subscription subscription = new Subscription( );
        subscription.setIdSubscribedResource( Integer.toString( nId ) );
        subscription.setUserId( user.getName( ) );
        subscription.setSubscriptionKey( strSubscriptionKey );
        subscription.setSubscriptionProvider( getProviderName( ) );
        SubscriptionService.getInstance( ).createSubscription( subscription );
    }

    /**
     * Check if a user has subscribed to a suggest category
     * 
     * @param user
     *            The user
     * @param nIdSuggestCategory
     *            The id of the suggest category to check the subscription to
     * @return True if the user has subscribed to the given suggest category, false otherwise
     */
    public boolean hasUserSubscribedToSuggestCategory( LuteceUser user, int nIdSuggestCategory )
    {
        return hasUserSubscribedToResource( user, nIdSuggestCategory, SUBSCRIPTION_SUGGEST_CATEGORY );
    }

    /**
     * Check if a user has subscribed to a suggest submit
     * 
     * @param user
     *            The user
     * @param nIdSuggestSubmit
     *            The id of the suggest submit to check the subscription to
     * @return True if the user has subscribed to the given suggest submit, false otherwise
     */
    public boolean hasUserSubscribedToSuggestSubmit( LuteceUser user, int nIdSuggestSubmit )
    {
        return hasUserSubscribedToResource( user, nIdSuggestSubmit, SUBSCRIPTION_SUGGEST_SUBMIT );
    }

    /**
     * Check if a user has subscribed to a suggest
     * 
     * @param user
     *            The user
     * @param nIdSuggest
     *            The id of the suggest to check the subscription to
     * @return True if the user has subscribed to the given suggest, false otherwise
     */
    public boolean hasUserSubscribedToSuggest( LuteceUser user, int nIdSuggest )
    {
        return hasUserSubscribedToResource( user, nIdSuggest, SUBSCRIPTION_SUGGEST );
    }

    /**
     * Check if a user has subscribed to a resource
     * 
     * @param user
     *            The user
     * @param nId
     *            The id of the subscribed resource
     * @param strSubscriptionKey
     *            The subscription key
     * @return True if the user has subscribed to the resource, false otherwise
     */
    private boolean hasUserSubscribedToResource( LuteceUser user, int nId, String strSubscriptionKey )
    {
        SubscriptionFilter filter = new SubscriptionFilter( user.getName( ), getProviderName( ), strSubscriptionKey, Integer.toString( nId ) );
        List<Subscription> listSubscription = SubscriptionService.getInstance( ).findByFilter( filter );

        if ( ( listSubscription != null ) && ( listSubscription.size( ) > 0 ) )
        {
            return true;
        }

        return false;
    }

    @Override
    public String getSubscriptionHtmlDescriptionBis( LuteceUser user, String strSubscriptionKey, String strIdSubscribedResource, Locale locale, String userSub )
    {
        // TODO is there a difference between getSubscriptionHtmlDescription and getSubscriptionHtmlDescriptionBis ?
        return getSubscriptionHtmlDescriptionBis( user, strSubscriptionKey, strIdSubscribedResource, locale, userSub );
    }
}
