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
package fr.paris.lutece.plugins.suggest.service.subscription;

import fr.paris.lutece.plugins.suggest.service.ISuggestSubmitService;
import jakarta.enterprise.inject.spi.CDI;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

import fr.paris.lutece.plugins.suggest.business.CommentSubmit;
import fr.paris.lutece.plugins.suggest.business.CommentSubmitHome;
import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestFilter;
import fr.paris.lutece.plugins.suggest.business.SuggestHome;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitHome;
import fr.paris.lutece.plugins.suggest.business.SubmitFilter;
import fr.paris.lutece.plugins.suggest.service.SuggestPlugin;
import fr.paris.lutece.plugins.suggest.service.notification.SuggestNotificationService;
import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.plugins.subscribe.business.Subscription;
import fr.paris.lutece.plugins.subscribe.business.SubscriptionFilter;
import fr.paris.lutece.plugins.subscribe.service.SubscriptionService;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppLogService;

/**
 * Daemon to send notifications to users that subscribed to suggests suggest categories or suggest submits
 */
public class SuggestSubscribersNotificationDaemon extends Daemon
{
    private static final String DATASTORE_DAEMON_LAST_RUN_KEY = "suggest.suggestSubscribersNotificationDaemon.lastRunDate";
    private static final String MARK_COMMENTS = "comments";
    private static final String MARK_SUGGEST_SUBMIT = "suggestSubmits";
    private Map<String, List<CommentSubmit>> _mapCommentNotif;
    private Map<String, List<SuggestSubmit>> _mapSuggestSubmitNotif;

    /**
     * {@inheritDoc}
     */
    @Override
    public void run( )
    {
        synchronized( this )
        {
            Date date = new Date( );
            String strLastRunDate = DatastoreService.getInstanceDataValue( DATASTORE_DAEMON_LAST_RUN_KEY, null );

            if ( StringUtils.isNotEmpty( strLastRunDate ) && StringUtils.isNumeric( strLastRunDate ) )
            {
                try
                {
                    notifySubscribers( new Date( Long.parseLong( strLastRunDate ) ) );
                }
                catch( RuntimeException e )
                {
                    AppLogService.error( "Suggest subscribers not notified: {}", e.getMessage( ), e );
                }
            }

            DatastoreService.setInstanceDataValue( DATASTORE_DAEMON_LAST_RUN_KEY, Long.toString( date.getTime( ) ) );
        }
    }

    /**
     * Notifies the subscribers of the comments and the suggest submits published since the last run.
     *
     * @param dateLastRun
     *            the date of the last run
     */
    private void notifySubscribers( Date dateLastRun )
    {
        resetNotifSend( );

        Plugin plugin = PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME );

        // We get the list of comments posted after the last run of this daemon
        List<CommentSubmit> listComment = CommentSubmitHome.findSuggestCommentByDate( dateLastRun, plugin );
        if ( ( listComment != null ) && ( !listComment.isEmpty( ) ) )
        {
            // We order the list of comments by suggest submit
            Map<Integer, List<CommentSubmit>> mapCommentsBySuggestSubmitId = new HashMap<>( listComment.size( ) );

            for ( CommentSubmit comment : listComment )
            {
                comment.setSuggestSubmit( SuggestSubmitHome.findByPrimaryKey( comment.getSuggestSubmit( ).getIdSuggestSubmit( ), plugin ) );

                if ( comment.isActive( ) && isPublished( comment.getSuggestSubmit( ) ) )
                {
                    addCommentToMap( comment, mapCommentsBySuggestSubmitId );
                }
            }

            // Now that the map contain every comment ordered by suggest submit, we just have to generate notifications

            // We get the list of subscriptions to suggest submits
            SubscriptionFilter subscriptionFilter = new SubscriptionFilter( );
            subscriptionFilter.setSubscriptionKey( SuggestSubscriptionProviderService.SUBSCRIPTION_SUGGEST_SUBMIT );
            subscriptionFilter.setSubscriptionProvider( CDI.current( ).select( SuggestSubscriptionProviderService.class ).get( ).getProviderName( ) );

            List<Subscription> listSubscription = CDI.current( ).select( SubscriptionService.class ).get( ).findByFilter( subscriptionFilter );

            if ( ( listSubscription != null ) && ( !listSubscription.isEmpty( ) ) )
            {
                for ( Subscription subscription : listSubscription )
                {
                    if ( StringUtils.isNotEmpty( subscription.getIdSubscribedResource( ) )
                            && StringUtils.isNumeric( subscription.getIdSubscribedResource( ) ) )
                    {
                        int nIdSuggestSubmit = Integer.parseInt( subscription.getIdSubscribedResource( ) );
                        List<CommentSubmit> listComments = mapCommentsBySuggestSubmitId.get( nIdSuggestSubmit );

                        if ( ( listComments != null ) && ( !listComments.isEmpty( ) ) )
                        {
                            registerCommentNotificationToSend( listComments, subscription.getUserId( ) );
                        }
                    }
                }

                sendRegisteredCommentNotifications( plugin );
                // We clear registered notifications
                _mapCommentNotif = null;
            }
        }

        SubmitFilter submitFilter = new SubmitFilter( );
        submitFilter.setDateFirst( new Timestamp( dateLastRun.getTime( ) ) );
        submitFilter.setIdSuggestSubmitState( SuggestSubmit.STATE_PUBLISH );

        List<SuggestSubmit> listCreatedSuggestSubmit = CDI.current( ).select( ISuggestSubmitService.class ).get( ).getSuggestSubmitList( submitFilter, plugin );

        if ( ( listCreatedSuggestSubmit != null ) && ( !listCreatedSuggestSubmit.isEmpty( ) ) )
        {
            // We get the list of subscriptions to suggest categories
            SubscriptionFilter subscriptionFilter = new SubscriptionFilter( );
            subscriptionFilter.setSubscriptionKey( SuggestSubscriptionProviderService.SUBSCRIPTION_SUGGEST_CATEGORY );
            subscriptionFilter.setSubscriptionProvider( CDI.current( ).select( SuggestSubscriptionProviderService.class ).get( ).getProviderName( ) );

            List<Subscription> listSubscription = CDI.current( ).select( SubscriptionService.class ).get( ).findByFilter( subscriptionFilter );

            if ( ( listSubscription != null ) && ( !listSubscription.isEmpty( ) ) )
            {
                for ( Subscription subscription : listSubscription )
                {
                    if ( StringUtils.isNotEmpty( subscription.getIdSubscribedResource( ) )
                            && StringUtils.isNumeric( subscription.getIdSubscribedResource( ) ) )
                    {
                        int nIdCategory = SuggestUtils.getIntegerParameter( subscription.getIdSubscribedResource( ) );

                        for ( SuggestSubmit suggestSubmit : listCreatedSuggestSubmit )
                        {
                            if ( nIdCategory != SuggestUtils.CONSTANT_ID_NULL && suggestSubmit.getCategory( ) != null
                                    && nIdCategory == suggestSubmit.getCategory( ).getIdCategory( ) )
                            {
                                registerSuggestSubmitNotificationToSend( suggestSubmit, subscription.getUserId( ) );
                            }
                        }
                    }
                }
            }

            subscriptionFilter = new SubscriptionFilter( );
            subscriptionFilter.setSubscriptionKey( SuggestSubscriptionProviderService.SUBSCRIPTION_SUGGEST );
            subscriptionFilter.setSubscriptionProvider( CDI.current( ).select( SuggestSubscriptionProviderService.class ).get( ).getProviderName( ) );
            listSubscription = CDI.current( ).select( SubscriptionService.class ).get( ).findByFilter( subscriptionFilter );

            if ( ( listSubscription != null ) && ( !listSubscription.isEmpty( ) ) )
            {
                for ( Subscription subscription : listSubscription )
                {
                    if ( StringUtils.isNotEmpty( subscription.getIdSubscribedResource( ) )
                            && StringUtils.isNumeric( subscription.getIdSubscribedResource( ) ) )
                    {
                        int nIdSuggest = Integer.parseInt( subscription.getIdSubscribedResource( ) );

                        for ( SuggestSubmit suggestSubmit : listCreatedSuggestSubmit )
                        {
                            if ( nIdSuggest == suggestSubmit.getSuggest( ).getIdSuggest( ) )
                            {
                                registerSuggestSubmitNotificationToSend( suggestSubmit, subscription.getUserId( ) );
                            }
                        }
                    }
                }
            }

            sendRegisteredSuggestSubmitNotifications( plugin );
        }
    }

    /**
     * Tells whether a suggest submit is published.
     *
     * @param suggestSubmit
     *            the suggest submit
     * @return true if the suggest submit is published
     */
    private static boolean isPublished( SuggestSubmit suggestSubmit )
    {
        return ( suggestSubmit != null ) && ( suggestSubmit.getSuggestSubmitState( ) != null )
                && ( suggestSubmit.getSuggestSubmitState( ).getNumber( ) == SuggestSubmit.STATE_PUBLISH );
    }

    /**
     * Tells whether a comment goes in the digest of a subscriber: never the subscriber's own comments.
     *
     * @param comment
     *            the comment
     * @param strUserId
     *            the subscriber
     * @return true if the comment goes in the digest
     */
    private static boolean isForDigest( CommentSubmit comment, String strUserId )
    {
        return !Strings.CS.equals( comment.getLuteceUserKey( ), strUserId );
    }

    /**
     * Add a comment to a map of suggest submit ids and list of comments
     * 
     * @param comment
     *            The comment to add
     * @param mapCommentsBySuggestSubmitId
     *            The map to add the comment in
     */
    private void addCommentToMap( CommentSubmit comment, Map<Integer, List<CommentSubmit>> mapCommentsBySuggestSubmitId )
    {
        List<CommentSubmit> listComments = mapCommentsBySuggestSubmitId.get( comment.getSuggestSubmit( ).getIdSuggestSubmit( ) );

        if ( listComments == null )
        {
            listComments = new ArrayList<>( );
            mapCommentsBySuggestSubmitId.put( comment.getSuggestSubmit( ).getIdSuggestSubmit( ), listComments );
        }

        listComments.add( comment );
    }

    /**
     * Reset notifications to send
     */
    private void resetNotifSend( )
    {
        this._mapCommentNotif = new HashMap<>( );
        this._mapSuggestSubmitNotif = new HashMap<>( );
    }

    /**
     * Register a list of comments to send to a user. If a comment has already been registered for the user, then it is ignored
     * 
     * @param listComments
     *            The list of comments to send
     * @param strUserId
     *            The user to send the notification to
     */
    private void registerCommentNotificationToSend( List<CommentSubmit> listAllComments, String strUserId )
    {
        List<CommentSubmit> listComments = listAllComments.stream( ).filter( comment -> isForDigest( comment, strUserId ) ).toList( );

        if ( listComments.isEmpty( ) )
        {
            return;
        }

        List<CommentSubmit> listRegisteredCopmments = _mapCommentNotif.get( strUserId );

        if ( listRegisteredCopmments == null )
        {
            listRegisteredCopmments = new ArrayList<>( listComments );
            _mapCommentNotif.put( strUserId, listRegisteredCopmments );
        }
        else
        {
            List<CommentSubmit> listCommentsToAdd = new ArrayList<>( );

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
     * 
     * @param plugin
     *            the plugin
     */
    private void sendRegisteredCommentNotifications( Plugin plugin )
    {

        SuggestFilter filter = new SuggestFilter( );
        filter.setIdState( Suggest.STATE_ENABLE );
        List<Suggest> listSuggest = SuggestHome.getSuggestList( filter, plugin );
        for ( Suggest suggest : listSuggest )
        {

            for ( Entry<String, List<CommentSubmit>> entry : _mapCommentNotif.entrySet( ) )
            {
                List<CommentSubmit> listCommentSubmitsTmp = new ArrayList<>( );

                for ( CommentSubmit commentSubmitTmp : entry.getValue( ) )
                {

                    if ( suggest.getIdSuggest( ) == commentSubmitTmp.getSuggestSubmit( ).getSuggest( ).getIdSuggest( ) )
                    {

                        listCommentSubmitsTmp.add( commentSubmitTmp );
                    }

                }

                if ( !listCommentSubmitsTmp.isEmpty( ) )
                {
                    sendCommentNotification( suggest, listCommentSubmitsTmp, entry.getKey( ) );
                }
            }

        }
    }

    /**
     * Send a single comment notification
     * 
     * @param suggest
     *            the Suggest link to the notification
     * @param listComments
     *            The list of comments to include into the notification
     * @param strUserName
     *            The name of the lutece user to send the notification to
     */
    private void sendCommentNotification( Suggest suggest, List<CommentSubmit> listComments, String strUserName )
    {
        SuggestNotificationService notificationService = CDI.current( ).select( SuggestNotificationService.class ).get( );
        Map<String, Object> model = notificationService.newModel( strUserName );
        model.put( MARK_COMMENTS, listComments );
        notificationService.sendFromStrings( strUserName, StringUtils.defaultIfBlank( suggest.getNotificationNewCommentSenderName( ), suggest.getTitle( ) ),
                suggest.getNotificationNewCommentTitle( ), suggest.getNotificationNewCommentBody( ), model );
    }

    /**
     * Register a suggest submit to send to a user
     * 
     * @param suggestSubmit
     *            The suggest submit to register
     * @param strUserName
     *            The name of the lutece user to send the notification to
     */
    private void registerSuggestSubmitNotificationToSend( SuggestSubmit suggestSubmit, String strUserName )
    {
        if ( Strings.CS.equals( suggestSubmit.getLuteceUserKey( ), strUserName ) )
        {
            return;
        }

        List<SuggestSubmit> listRegisteredSuggestSubmit = _mapSuggestSubmitNotif.get( strUserName );

        if ( listRegisteredSuggestSubmit == null )
        {
            listRegisteredSuggestSubmit = new ArrayList<>( );
            listRegisteredSuggestSubmit.add( suggestSubmit );
            _mapSuggestSubmitNotif.put( strUserName, listRegisteredSuggestSubmit );
        }
        else
        {
            boolean bAddSuggestSubmit = true;
            int nIdSuggestsubmit = suggestSubmit.getIdSuggestSubmit( );

            for ( SuggestSubmit registeredSuggestSubmit : listRegisteredSuggestSubmit )
            {
                if ( registeredSuggestSubmit.getIdSuggestSubmit( ) == nIdSuggestsubmit )
                {
                    bAddSuggestSubmit = false;

                    break;
                }
            }

            if ( bAddSuggestSubmit )
            {
                listRegisteredSuggestSubmit.add( suggestSubmit );
            }
        }
    }

    /**
     * Send all registered comment notifications
     * 
     * @param plugin
     *            plugin
     */
    private void sendRegisteredSuggestSubmitNotifications( Plugin plugin )
    {
        SuggestFilter filter = new SuggestFilter( );
        filter.setIdState( Suggest.STATE_ENABLE );
        List<Suggest> listSuggest = SuggestHome.getSuggestList( filter, plugin );
        for ( Suggest suggest : listSuggest )
        {

            for ( Entry<String, List<SuggestSubmit>> entry : _mapSuggestSubmitNotif.entrySet( ) )
            {
                List<SuggestSubmit> listSuggestSubmitTmp = new ArrayList<>( );

                for ( SuggestSubmit suggestSubmitTmp : entry.getValue( ) )
                {

                    if ( suggest.getIdSuggest( ) == suggestSubmitTmp.getSuggest( ).getIdSuggest( ) )
                    {

                        listSuggestSubmitTmp.add( suggestSubmitTmp );
                    }

                }

                if ( !listSuggestSubmitTmp.isEmpty( ) )
                {
                    sendSuggestSubmitNotification( suggest, listSuggestSubmitTmp, entry.getKey( ) );
                }
            }

        }
    }

    /**
     * Send a single suggest submit notification
     * 
     * @param suggest
     *            the Suggest link to the notification
     * @param listSuggestSubmit
     *            The list of suggest submit to include into the notification
     * @param strUserName
     *            The name of the lutece user to send the notification to
     */
    private void sendSuggestSubmitNotification( Suggest suggest, List<SuggestSubmit> listSuggestSubmit, String strUserName )
    {
        SuggestNotificationService notificationService = CDI.current( ).select( SuggestNotificationService.class ).get( );
        Map<String, Object> model = notificationService.newModel( strUserName );
        model.put( MARK_SUGGEST_SUBMIT, listSuggestSubmit );
        notificationService.sendFromStrings( strUserName,
                StringUtils.defaultIfBlank( suggest.getNotificationNewSuggestSubmitSenderName( ), suggest.getTitle( ) ),
                suggest.getNotificationNewSuggestSubmitTitle( ), suggest.getNotificationNewSuggestSubmitBody( ), model );
    }
}
