package fr.paris.lutece.plugins.digglike.service.subscription;

import fr.paris.lutece.plugins.digglike.business.Category;
import fr.paris.lutece.plugins.digglike.business.CategoryHome;
import fr.paris.lutece.plugins.digglike.business.Digg;
import fr.paris.lutece.plugins.digglike.business.DiggHome;
import fr.paris.lutece.plugins.digglike.business.DiggSubmit;
import fr.paris.lutece.plugins.digglike.service.DiggSubmitService;
import fr.paris.lutece.plugins.digglike.service.DigglikePlugin;
import fr.paris.lutece.plugins.subscribe.business.Subscription;
import fr.paris.lutece.plugins.subscribe.business.SubscriptionFilter;
import fr.paris.lutece.plugins.subscribe.service.ISubscriptionProviderService;
import fr.paris.lutece.plugins.subscribe.service.SubscriptionService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;


/**
 * Subscription provider for digg and digg submit
 */
public class DigglikeSubscriptionProviderService implements ISubscriptionProviderService
{
    /**
     * Name of the bean of the DigglikeSubscriptionProviderService
     */
    public static final String BEAN_NAME = "digglike.digglikeSubscriptionProviderService";

    public static final String SUBSCRIPTION_DIGG = "digg";
    public static final String SUBSCRIPTION_DIGG_CATEGORY = "digg_category";
    public static final String SUBSCRIPTION_DIGG_SUBMIT = "digg_submit";

    private static final String SUBSCRIPTION_PROVIDER_NAME = "digglike.digglikeSubscriptionProviderService";

    private static final String MESSAGE_SUBSCRIBED_DIGG = "digglike.message.subscriptions.subscribedDigg";
    private static final String MESSAGE_SUBSCRIBED_DIGG_SUBMIT = "digglike.message.subscriptions.subscribedDiggSubmit";
    private static final String MESSAGE_SUBSCRIBED_DIGG_CATEGORY = "digglike.message.subscriptions.subscribedCategory";

    private static DigglikeSubscriptionProviderService _instance;

    /**
     * Returns the instance of the singleton
     * 
     * @return The instance of the singleton
     */
    public static DigglikeSubscriptionProviderService getService( )
    {
        if ( _instance == null )
        {
            synchronized ( DigglikeSubscriptionProviderService.class )
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
    public String getSubscriptionHtmlDescription( LuteceUser user, String strSubscriptionKey,
            String strIdSubscribedResource, Locale locale )
    {
        int nId = strIdSubscribedResource != null && StringUtils.isNumeric( strIdSubscribedResource ) ? Integer
                .parseInt( strIdSubscribedResource ) : 0;
        if ( nId > 0 )
        {
            if ( StringUtils.equals( SUBSCRIPTION_DIGG, strSubscriptionKey ) )
            {
                Digg digg = DiggHome.findByPrimaryKey( nId, PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME ) );
                if ( digg != null )
                {
                    Object[] params = { digg.getTitle( ) };
                    return I18nService.getLocalizedString( MESSAGE_SUBSCRIBED_DIGG, params, locale );
                }
            }
            else if ( StringUtils.equals( SUBSCRIPTION_DIGG_SUBMIT, strSubscriptionKey ) )
            {
                DiggSubmit diggSubmit = DiggSubmitService.getService( ).findByPrimaryKey( nId, false,
                        PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME ) );
                if ( diggSubmit != null )
                {
                    Object[] params = { diggSubmit.getDiggSubmitTitle( ) };
                    return I18nService.getLocalizedString( MESSAGE_SUBSCRIBED_DIGG_SUBMIT, params, locale );
                }
            }
            else if ( StringUtils.equals( SUBSCRIPTION_DIGG_CATEGORY, strSubscriptionKey ) )
            {
                Category category = CategoryHome.findByPrimaryKey( nId,
                        PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME ) );
                if ( category != null )
                {
                    Object[] params = { category.getTitle( ) };
                    return I18nService.getLocalizedString( MESSAGE_SUBSCRIBED_DIGG_CATEGORY, params, locale );
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
        // Subscriptions for diggs or digg submits are always removable
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrlModifySubscription( LuteceUser user, String strSubscriptionKey, String strIdSubscribedResource )
    {
        //No subscription modification for digg nor digg submits
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
     * Remove a subscription of a user to a digg
     * @param user The user to remove the subscription of
     * @param nIdDigg The id of the digg
     */
    public void removeDiggSubscription( LuteceUser user, int nIdDigg )
    {
        removeDiggSubmitSubscription( user, nIdDigg, SUBSCRIPTION_DIGG );
    }

    /**
     * Remove a subscription of a user to a digg category
     * @param user The user to remove the subscription of
     * @param nIdCategory The id of the digg category
     */
    public void removeDiggCategorySubscription( LuteceUser user, int nIdCategory )
    {
        removeDiggSubmitSubscription( user, nIdCategory, SUBSCRIPTION_DIGG_CATEGORY );
    }

    /**
     * Remove a subscription of a user to a digg submit
     * @param user The user to remove the subscription of
     * @param nIdDiggSubmit The id of the digg submit
     */
    public void removeDiggSubmitSubscription( LuteceUser user, int nIdDiggSubmit )
    {
        removeDiggSubmitSubscription( user, nIdDiggSubmit, SUBSCRIPTION_DIGG_SUBMIT );
    }

    /**
     * Remove a subscription to a digg resource
     * @param user The user to remove the subscription of
     * @param nId The id of the subscribed resource
     * @param strSubscriptionKey The subscription key
     */
    private void removeDiggSubmitSubscription( LuteceUser user, int nId, String strSubscriptionKey )
    {
        SubscriptionFilter filter = new SubscriptionFilter( SubscriptionService.getInstance( )
                .getIdSubscriberFromLuteceUser( user ), getProviderName( ), strSubscriptionKey, Integer.toString( nId ) );

        List<Subscription> listSubscription = SubscriptionService.getInstance( ).findByFilter( filter );
        if ( listSubscription != null && listSubscription.size( ) > 0 )
        {
            for ( Subscription subscription : listSubscription )
            {
                SubscriptionService.getInstance( ).removeSubscription( subscription, false );
            }
        }
    }

    /**
     * Create a new subscription to a digg
     * @param user The user to create a subscription of
     * @param nIdDigg The id of the digg to subscribe to
     */
    public void createDiggSubscription( LuteceUser user, int nIdDigg )
    {
        if ( !hasUserSubscribedToResource( user, nIdDigg, SUBSCRIPTION_DIGG ) )
        {
            createSubscription( user, nIdDigg, SUBSCRIPTION_DIGG );
        }
    }

    /**
     * Create a new subscription to a digg
     * @param user The user to create a subscription of
     * @param nIdDigg The id of the digg to subscribe to
     */
    public void createDiggCategorySubscription( LuteceUser user, int nIdDigg )
    {
        if ( !hasUserSubscribedToResource( user, nIdDigg, SUBSCRIPTION_DIGG_CATEGORY ) )
        {
            createSubscription( user, nIdDigg, SUBSCRIPTION_DIGG_CATEGORY );
        }
    }

    /**
     * Create a new subscription to a digg submit
     * @param user The user to create a subscription of
     * @param nIdDiggSubmit The id of the digg submit to subscribe to
     */
    public void createDiggSubmitSubscription( LuteceUser user, int nIdDiggSubmit )
    {
        if ( !hasUserSubscribedToResource( user, nIdDiggSubmit, SUBSCRIPTION_DIGG_SUBMIT ) )
        {
            createSubscription( user, nIdDiggSubmit, SUBSCRIPTION_DIGG_SUBMIT );
        }
    }

    private void createSubscription( LuteceUser user, int nId, String strSubscriptionKey )
    {
        Subscription subscription = new Subscription( );
        subscription.setIdSubscribedResource( Integer.toString( nId ) );
        subscription.setIdSubscriber( SubscriptionService.getInstance( ).getIdSubscriberFromLuteceUser( user ) );
        subscription.setSubscriptionKey( strSubscriptionKey );
        subscription.setSubscriptionProvider( getProviderName( ) );
        SubscriptionService.getInstance( ).createSubscription( subscription );
    }

    /**
     * Check if a user has subscribed to a digg category
     * @param user The user
     * @param nIdDiggCategory The id of the digg category to check the
     *            subscription to
     * @return True if the user has subscribed to the given digg category, false
     *         otherwise
     */
    public boolean hasUserSubscribedToDiggCategory( LuteceUser user, int nIdDiggCategory )
    {
        return hasUserSubscribedToResource( user, nIdDiggCategory, SUBSCRIPTION_DIGG_CATEGORY );
    }

    /**
     * Check if a user has subscribed to a digg submit
     * @param user The user
     * @param nIdDiggSubmit The id of the digg submit to check the subscription
     *            to
     * @return True if the user has subscribed to the given digg submit, false
     *         otherwise
     */
    public boolean hasUserSubscribedToDiggSubmit( LuteceUser user, int nIdDiggSubmit )
    {
        return hasUserSubscribedToResource( user, nIdDiggSubmit, SUBSCRIPTION_DIGG_SUBMIT );
    }

    /**
     * Check if a user has subscribed to a digg
     * @param user The user
     * @param nIdDigg The id of the digg to check the subscription to
     * @return True if the user has subscribed to the given digg, false
     *         otherwise
     */
    public boolean hasUserSubscribedToDigg( LuteceUser user, int nIdDigg )
    {
        return hasUserSubscribedToResource( user, nIdDigg, SUBSCRIPTION_DIGG );
    }

    /**
     * Check if a user has subscribed to a resource
     * @param user The user
     * @param nId The id of the subscribed resource
     * @param strSubscriptionKey The subscription key
     * @return True if the user has subscribed to the resource, false otherwise
     */
    private boolean hasUserSubscribedToResource( LuteceUser user, int nId, String strSubscriptionKey )
    {

        SubscriptionFilter filter = new SubscriptionFilter( SubscriptionService.getInstance( )
                .getIdSubscriberFromLuteceUser( user ), getProviderName( ), strSubscriptionKey, Integer.toString( nId ) );
        List<Subscription> listSubscription = SubscriptionService.getInstance( ).findByFilter( filter );
        if ( listSubscription != null && listSubscription.size( ) > 0 )
        {
            return true;
        }
        return false;
    }
}
