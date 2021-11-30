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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.suggest.business.CommentSubmit;
import fr.paris.lutece.plugins.suggest.business.CommentSubmitHome;
import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestFilter;
import fr.paris.lutece.plugins.suggest.business.SuggestHome;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitHome;
import fr.paris.lutece.plugins.suggest.business.SubmitFilter;
import fr.paris.lutece.plugins.suggest.service.SuggestSubmitService;
import fr.paris.lutece.plugins.suggest.service.SuggestPlugin;
import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
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

/**
 * Daemon to send notifications to users that subscribed to suggests suggest categories or suggest submits
 */
public class SuggestSubscribersNotificationDaemon extends Daemon
{
    private static final String DATASTORE_DAEMON_LAST_RUN_KEY = "suggest.suggestSubscribersNotificationDaemon.lastRunDate";
    private static final String MARK_COMMENTS = "comments";
    private static final String MARK_SUGGEST_SUBMIT = "suggestSubmits";
    private static final String MARK_BASE_URL = "base_url";
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
                resetNotifSend( );

                Plugin plugin = PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME );
                Date dateLastRun = new Date( Long.parseLong( strLastRunDate ) );

                // We get the list of comments posted after the last run of this daemon
                List<CommentSubmit> listComment = CommentSubmitHome.findSuggestCommentByDate( dateLastRun, plugin );
                if ( ( listComment != null ) && ( !listComment.isEmpty( ) ) )
                {
                    // We order the list of comments by suggest submit
                    Map<Integer, List<CommentSubmit>> mapCommentsBySuggestSubmitId = new HashMap<>( listComment.size( ) );

                    for ( CommentSubmit comment : listComment )
                    {
                        comment.setSuggestSubmit( SuggestSubmitHome.findByPrimaryKey( comment.getSuggestSubmit( ).getIdSuggestSubmit( ), plugin ) );
                        addCommentToMap( comment, mapCommentsBySuggestSubmitId );
                    }

                    // Now that the map contain every comment ordered by suggest submit, we just have to generate notifications

                    // We get the list of subscriptions to suggest submits
                    SubscriptionFilter subscriptionFilter = new SubscriptionFilter( );
                    subscriptionFilter.setSubscriptionKey( SuggestSubscriptionProviderService.SUBSCRIPTION_SUGGEST_SUBMIT );
                    subscriptionFilter.setSubscriptionProvider( SuggestSubscriptionProviderService.getService( ).getProviderName( ) );

                    List<Subscription> listSubscription = SubscriptionService.getInstance( ).findByFilter( subscriptionFilter );

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

                List<SuggestSubmit> listCreatedSuggestSubmit = SuggestSubmitService.getService( ).getSuggestSubmitList( submitFilter, plugin );

                if ( ( listCreatedSuggestSubmit != null ) && ( !listCreatedSuggestSubmit.isEmpty( ) ) )
                {
                    // We get the list of subscriptions to suggest categories
                    SubscriptionFilter subscriptionFilter = new SubscriptionFilter( );
                    subscriptionFilter.setSubscriptionKey( SuggestSubscriptionProviderService.SUBSCRIPTION_SUGGEST_CATEGORY );
                    subscriptionFilter.setSubscriptionProvider( SuggestSubscriptionProviderService.getService( ).getProviderName( ) );

                    List<Subscription> listSubscription = SubscriptionService.getInstance( ).findByFilter( subscriptionFilter );

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
                    subscriptionFilter.setSubscriptionProvider( SuggestSubscriptionProviderService.getService( ).getProviderName( ) );
                    listSubscription = SubscriptionService.getInstance( ).findByFilter( subscriptionFilter );

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

            DatastoreService.setInstanceDataValue( DATASTORE_DAEMON_LAST_RUN_KEY, Long.toString( date.getTime( ) ) );
        }
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
    private void registerCommentNotificationToSend( List<CommentSubmit> listComments, String strUserId )
    {
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
        LuteceUser user = LuteceUserService.getLuteceUserFromName( strUserName );
        String strEmail = getEmailFromLuteceUser( user );

        if ( StringUtils.isNotEmpty( strEmail ) )
        {
            Locale locale = Locale.getDefault( );

            Map<String, Object> model = new HashMap<>( );
            model.put( MARK_COMMENTS, listComments );
            model.put( MARK_BASE_URL, AppPathService.getProdUrl( (String) null ) );

            HtmlTemplate templateBody = AppTemplateService.getTemplateFromStringFtl( suggest.getNotificationNewCommentBody( ), locale, model );
            HtmlTemplate templateTitle = AppTemplateService.getTemplateFromStringFtl( suggest.getNotificationNewCommentTitle( ), locale, model );
            MailService.sendMailHtml(
                    strEmail,
                    !StringUtils.isBlank( suggest.getNotificationNewCommentSenderName( ) ) ? suggest.getNotificationNewCommentSenderName( ) : suggest
                            .getTitle( ), MailService.getNoReplyEmail( ), templateTitle.getHtml( ), templateBody.getHtml( ) );
        }
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
        LuteceUser user = LuteceUserService.getLuteceUserFromName( strUserName );
        String strEmail = getEmailFromLuteceUser( user );

        if ( StringUtils.isNotEmpty( strEmail ) )
        {
            Locale locale = Locale.getDefault( );

            Map<String, Object> model = new HashMap<>( );
            model.put( MARK_SUGGEST_SUBMIT, listSuggestSubmit );
            model.put( MARK_BASE_URL, AppPathService.getProdUrl( (String) null ) );

            HtmlTemplate templateBody = AppTemplateService.getTemplateFromStringFtl( suggest.getNotificationNewSuggestSubmitBody( ), locale, model );
            HtmlTemplate templateTitle = AppTemplateService.getTemplateFromStringFtl( suggest.getNotificationNewSuggestSubmitTitle( ), locale, model );
            MailService.sendMailHtml( strEmail,
                    !StringUtils.isBlank( suggest.getNotificationNewSuggestSubmitSenderName( ) ) ? suggest.getNotificationNewSuggestSubmitSenderName( )
                            : suggest.getTitle( ), MailService.getNoReplyEmail( ), templateTitle.getHtml( ), templateBody.getHtml( ) );
        }
    }

    /**
     * Get the email from the lutece user
     * 
     * @param user
     *            The user to get the email of
     * @return The email of the user, or null if he has none
     */
    private String getEmailFromLuteceUser( LuteceUser user )
    {
        String strEmail = user.getUserInfo( LuteceUser.BUSINESS_INFO_ONLINE_EMAIL );

        if ( strEmail == null )
        {
            strEmail = user.getUserInfo( LuteceUser.HOME_INFO_ONLINE_EMAIL );
        }

        return strEmail;
    }
}
