package fr.paris.lutece.plugins.digglike.service.subscription;

import fr.paris.lutece.plugins.digglike.business.CommentSubmit;
import fr.paris.lutece.plugins.digglike.business.CommentSubmitHome;
import fr.paris.lutece.plugins.digglike.business.DiggSubmit;
import fr.paris.lutece.plugins.digglike.business.DiggSubmitHome;
import fr.paris.lutece.plugins.digglike.business.SubmitFilter;
import fr.paris.lutece.plugins.digglike.service.DiggSubmitService;
import fr.paris.lutece.plugins.digglike.service.DigglikePlugin;
import fr.paris.lutece.plugins.subscribe.business.Subscription;
import fr.paris.lutece.plugins.subscribe.business.SubscriptionFilter;
import fr.paris.lutece.plugins.subscribe.service.SubscriptionService;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.LuteceUserService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;


/**
 * Daemon to send notifications to users that subscribed to diggs digg
 * categories or digg submits
 */
public class DiggSubscribersNotificationDaemon extends Daemon
{

    private static final String DATASTORE_DAEMON_LAST_RUN_KEY = "digglike.diggSubscribersNotificationDaemon.lastRunDate";

    private static final String MARK_COMMENTS = "comments";
    private static final String MARK_DIGG_SUBMIT = "diggSubmits";
    private static final String MARK_BASE_URL = "base_url";

    private static final String TEMPLATE_NEW_COMMENTS = "skin/plugins/digglike/notifications/new_comments.html";
    private static final String TEMPLATE_NEW_COMMENTS_TITLE = "skin/plugins/digglike/notifications/new_comments_title.html";
    private static final String TEMPLATE_NEW_DIGG_SUBMIT = "skin/plugins/digglike/notifications/new_digg_submit.html";
    private static final String TEMPLATE_NEW_DIGG_SUBMIT_TITLE = "skin/plugins/digglike/notifications/new_digg_submit_title.html";

    private Map<String, List<CommentSubmit>> _mapCommentNotif;
    private Map<String, List<DiggSubmit>> _mapDiggSubmitNotif;

    /**
     * {@inheritDoc}
     */
    @Override
    public void run( )
    {
        synchronized ( this )
        {
            Date date = new Date( );
            String strLastRunDate = DatastoreService.getInstanceDataValue( DATASTORE_DAEMON_LAST_RUN_KEY, null );
            if ( StringUtils.isNotEmpty( strLastRunDate ) && StringUtils.isNumeric( strLastRunDate ) )
            {
                resetNotifSend( );

                Plugin plugin = PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME );
                Date dateLastRun = new Date( Long.parseLong( strLastRunDate ) );

                // We get the list of comments posted after the last run of this daemon
                List<CommentSubmit> listComment = CommentSubmitHome.findDiggCommentByDate( dateLastRun, plugin );

                if ( listComment != null && listComment.size( ) > 0 )
                {
                    // We order the list of comments by digg submit
                    Map<Integer, List<CommentSubmit>> mapCommentsByDiggSubmitId = new HashMap<Integer, List<CommentSubmit>>(
                            listComment.size( ) );

                    for ( CommentSubmit comment : listComment )
                    {
                        // We set the digg submit to have access to the underlying digg
                        comment.setDiggSubmit( DiggSubmitHome.findByPrimaryKey( comment.getDiggSubmit( )
                                .getIdDiggSubmit( ), plugin ) );
                        addCommentToMap( comment, mapCommentsByDiggSubmitId );
                    }
                    // Now that the map contain every comment ordered by digg submit, we just have to generate notifications

                    // We get the list of subscriptions to digg submits
                    SubscriptionFilter subscriptionFilter = new SubscriptionFilter( );
                    subscriptionFilter
                            .setSubscriptionKey( DigglikeSubscriptionProviderService.SUBSCRIPTION_DIGG_SUBMIT );
                    subscriptionFilter.setSubscriptionProvider( DigglikeSubscriptionProviderService.getService( )
                            .getProviderName( ) );
                    List<Subscription> listSubscription = SubscriptionService.getInstance( ).findByFilter(
                            subscriptionFilter );

                    if ( listSubscription != null && listSubscription.size( ) > 0 )
                    {
                        for ( Subscription subscription : listSubscription )
                        {
                            if ( StringUtils.isNotEmpty( subscription.getIdSubscribedResource( ) )
                                    && StringUtils.isNumeric( subscription.getIdSubscribedResource( ) ) )
                            {
                                int nIdDiggSubmit = Integer.parseInt( subscription.getIdSubscribedResource( ) );
                                List<CommentSubmit> listComments = mapCommentsByDiggSubmitId.get( nIdDiggSubmit );
                                if ( listComments != null && listComments.size( ) > 0 )
                                {
                                    registerCommentNotificationToSend( listComments, subscription.getUserId( ) );
                                }
                            }
                        }
                        sendRegisteredCommentNotifications( );
                        // We clear registered notifications
                        _mapCommentNotif = null;
                    }
                }

                SubmitFilter submitFilter = new SubmitFilter( );
                submitFilter.setDateFirst( new Timestamp( dateLastRun.getTime( ) ) );
                List<DiggSubmit> listCreatedDiggSubmit = DiggSubmitService.getService( ).getDiggSubmitList(
                        submitFilter, plugin );
                if ( listCreatedDiggSubmit != null && listCreatedDiggSubmit.size( ) > 0 )
                {
                    // We get the list of subscriptions to digg categories
                    SubscriptionFilter subscriptionFilter = new SubscriptionFilter( );
                    subscriptionFilter
                            .setSubscriptionKey( DigglikeSubscriptionProviderService.SUBSCRIPTION_DIGG_CATEGORY );
                    subscriptionFilter.setSubscriptionProvider( DigglikeSubscriptionProviderService.getService( )
                            .getProviderName( ) );
                    List<Subscription> listSubscription = SubscriptionService.getInstance( ).findByFilter(
                            subscriptionFilter );

                    if ( listSubscription != null && listSubscription.size( ) > 0 )
                    {
                        for ( Subscription subscription : listSubscription )
                        {
                            if ( StringUtils.isNotEmpty( subscription.getIdSubscribedResource( ) )
                                    && StringUtils.isNumeric( subscription.getIdSubscribedResource( ) ) )
                            {
                                int nIdCategory = Integer.parseInt( subscription.getIdSubscribedResource( ) );
                                for ( DiggSubmit diggSubmit : listCreatedDiggSubmit )
                                {
                                    if ( nIdCategory == diggSubmit.getCategory( ).getIdCategory( ) )
                                    {
                                        registerDiggSubmitNotificationToSend( diggSubmit, subscription.getUserId( ) );
                                    }
                                }
                            }
                        }
                    }

                    subscriptionFilter = new SubscriptionFilter( );
                    subscriptionFilter.setSubscriptionKey( DigglikeSubscriptionProviderService.SUBSCRIPTION_DIGG );
                    subscriptionFilter.setSubscriptionProvider( DigglikeSubscriptionProviderService.getService( )
                            .getProviderName( ) );
                    listSubscription = SubscriptionService.getInstance( ).findByFilter( subscriptionFilter );
                    if ( listSubscription != null && listSubscription.size( ) > 0 )
                    {
                        for ( Subscription subscription : listSubscription )
                        {
                            if ( StringUtils.isNotEmpty( subscription.getIdSubscribedResource( ) )
                                    && StringUtils.isNumeric( subscription.getIdSubscribedResource( ) ) )
                            {
                                int nIdDigg = Integer.parseInt( subscription.getIdSubscribedResource( ) );
                                for ( DiggSubmit diggSubmit : listCreatedDiggSubmit )
                                {
                                    if ( nIdDigg == diggSubmit.getDigg( ).getIdDigg( ) )
                                    {
                                        registerDiggSubmitNotificationToSend( diggSubmit, subscription.getUserId( ) );
                                    }
                                }
                            }
                        }
                    }

                    sendRegisteredDiggSubmitNotifications( );
                }
            }
            DatastoreService.setInstanceDataValue( DATASTORE_DAEMON_LAST_RUN_KEY, Long.toString( date.getTime( ) ) );
        }
    }

    /**
     * Add a comment to a map of digg submit ids and list of comments
     * @param comment The comment to add
     * @param mapCommentsByDiggSubmitId The map to add the comment in
     */
    private void addCommentToMap( CommentSubmit comment, Map<Integer, List<CommentSubmit>> mapCommentsByDiggSubmitId )
    {
        List<CommentSubmit> listComments = mapCommentsByDiggSubmitId.get( comment.getDiggSubmit( ).getIdDiggSubmit( ) );
        if ( listComments == null )
        {
            listComments = new ArrayList<CommentSubmit>( );
            mapCommentsByDiggSubmitId.put( comment.getDiggSubmit( ).getIdDiggSubmit( ), listComments );
        }
        listComments.add( comment );
    }

    /**
     * Reset notifications to send
     */
    private void resetNotifSend( )
    {
        this._mapCommentNotif = new HashMap<String, List<CommentSubmit>>( );
        this._mapDiggSubmitNotif = new HashMap<String, List<DiggSubmit>>( );
    }

    /**
     * Register a list of comments to send to a user. If a comment has already
     * been registered for the user, then it is ignored
     * @param listComments The list of comments to send
     * @param nSubscriberId The user to send the notification to
     */
    private void registerCommentNotificationToSend( List<CommentSubmit> listComments, String strUserId )
    {
        List<CommentSubmit> listRegisteredCopmments = _mapCommentNotif.get( strUserId );
        if ( listRegisteredCopmments == null )
        {
            listRegisteredCopmments = new ArrayList<CommentSubmit>( listComments );
            _mapCommentNotif.put( strUserId, listRegisteredCopmments );
        }
        else
        {
            List<CommentSubmit> listCommentsToAdd = new ArrayList<CommentSubmit>( );
            for ( CommentSubmit comment : listComments )
            {
                boolean bAddComment = true;
                for ( CommentSubmit registeredComment : listRegisteredCopmments )
                {
                    if ( registeredComment.getIdCommentSubmit( ) == comment.getIdCommentSubmit( ) )
                    {
                        bAddComment = false;
                        break;
                    }
                }
                if ( bAddComment )
                {
                    listCommentsToAdd.add( comment );
                }
            }
            listRegisteredCopmments.addAll( listCommentsToAdd );
        }
    }

    /**
     * Send all registered comment notifications
     */
    private void sendRegisteredCommentNotifications( )
    {
        for ( Entry<String, List<CommentSubmit>> entry : _mapCommentNotif.entrySet( ) )
        {
            sendCommentNotification( entry.getValue( ), entry.getKey( ) );
        }
    }

    /**
     * Send a single comment notification
     * @param listComments The list of comments to include into the notification
     * @param strUserName The name of the lutece user to send the notification
     *            to
     */
    private void sendCommentNotification( List<CommentSubmit> listComments, String strUserName )
    {
        LuteceUser user = LuteceUserService.getLuteceUserFromName( strUserName );
        String strEmail = getEmailFromLuteceUser( user );
        if ( StringUtils.isNotEmpty( strEmail ) )
        {
            Locale locale = Locale.getDefault( );

            Map<String, Object> model = new HashMap<String, Object>( );
            model.put( MARK_COMMENTS, listComments );
            model.put( MARK_BASE_URL, AppPathService.getProdUrl( ) );

            HtmlTemplate templateBody = AppTemplateService.getTemplate( TEMPLATE_NEW_COMMENTS, locale, model );
            HtmlTemplate templateTitle = AppTemplateService.getTemplate( TEMPLATE_NEW_COMMENTS_TITLE, locale, model );
            MailService.sendMailHtml( strEmail, MailService.getNoReplyEmail( ), MailService.getNoReplyEmail( ),
                    templateTitle.getHtml( ), templateBody.getHtml( ) );
        }
    }

    /**
     * Register a digg submit to send to a user
     * @param diggSubmit The digg submit to register
     * @param strUserName The name of the lutece user to send the notification
     *            to
     */
    private void registerDiggSubmitNotificationToSend( DiggSubmit diggSubmit, String strUserName )
    {
        List<DiggSubmit> listRegisteredDiggSubmit = _mapDiggSubmitNotif.get( strUserName );
        if ( listRegisteredDiggSubmit == null )
        {
            listRegisteredDiggSubmit = new ArrayList<DiggSubmit>( );
            listRegisteredDiggSubmit.add( diggSubmit );
            _mapDiggSubmitNotif.put( strUserName, listRegisteredDiggSubmit );
        }
        else
        {
            boolean bAddDiggSubmit = true;
            int nIdDiggsubmit = diggSubmit.getIdDiggSubmit( );
            for ( DiggSubmit registeredDiggSubmit : listRegisteredDiggSubmit )
            {
                if ( registeredDiggSubmit.getIdDiggSubmit( ) == nIdDiggsubmit )
                {
                    bAddDiggSubmit = false;
                    break;
                }
            }
            if ( bAddDiggSubmit )
            {
                listRegisteredDiggSubmit.add( diggSubmit );
            }
        }
    }

    /**
     * Send all registered comment notifications
     */
    private void sendRegisteredDiggSubmitNotifications( )
    {
        for ( Entry<String, List<DiggSubmit>> entry : _mapDiggSubmitNotif.entrySet( ) )
        {
            sendDiggSubmitNotification( entry.getValue( ), entry.getKey( ) );
        }
    }

    /**
     * Send a single digg submit notification
     * @param listDiggSubmit The list of digg submit to include into the
     *            notification
     * @param nSubscriberId The name of the lutece user to send the notification
     *            to
     */
    private void sendDiggSubmitNotification( List<DiggSubmit> listDiggSubmit, String strUserName )
    {
        LuteceUser user = LuteceUserService.getLuteceUserFromName( strUserName );
        String strEmail = getEmailFromLuteceUser( user );
        if ( StringUtils.isNotEmpty( strEmail ) )
        {
            Locale locale = Locale.getDefault( );

            Map<String, Object> model = new HashMap<String, Object>( );
            model.put( MARK_DIGG_SUBMIT, listDiggSubmit );
            model.put( MARK_BASE_URL, AppPathService.getProdUrl( ) );

            HtmlTemplate templateBody = AppTemplateService.getTemplate( TEMPLATE_NEW_DIGG_SUBMIT, locale, model );
            HtmlTemplate templateTitle = AppTemplateService.getTemplate( TEMPLATE_NEW_DIGG_SUBMIT_TITLE, locale, model );
            MailService.sendMailHtml( strEmail, MailService.getNoReplyEmail( ), MailService.getNoReplyEmail( ),
                    templateTitle.getHtml( ), templateBody.getHtml( ) );
        }
    }

    /**
     * Get the email from the lutece user
     * @param user The user to get the email of
     * @return The email of the user, or null if he has none
     */
    private String getEmailFromLuteceUser( LuteceUser user )
    {
        String strEmail = user.getUserInfo( LuteceUser.BUSINESS_INFO_ONLINE_EMAIL );
        if ( strEmail != null )
        {
            strEmail = user.getUserInfo( LuteceUser.HOME_INFO_ONLINE_EMAIL );
        }
        return strEmail;
    }
}
