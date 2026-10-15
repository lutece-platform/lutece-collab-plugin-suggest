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
package fr.paris.lutece.plugins.suggest.service.notification;

import fr.paris.lutece.plugins.suggest.business.CommentSubmit;
import fr.paris.lutece.plugins.suggest.business.CommentSubmitHome;
import fr.paris.lutece.plugins.suggest.business.SubmitFilter;
import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestHome;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.suggest.service.ICommentSubmitService;
import fr.paris.lutece.plugins.suggest.service.ISuggestSubmitService;
import fr.paris.lutece.plugins.suggest.service.SuggestPlugin;
import fr.paris.lutece.plugins.suggest.service.event.CommentSubmitEvent;
import fr.paris.lutece.plugins.suggest.service.event.SuggestSubmitEvent;
import fr.paris.lutece.plugins.suggest.service.subscription.SuggestSubscriptionProviderService;
import fr.paris.lutece.portal.service.event.EventAction;
import fr.paris.lutece.portal.service.event.Type;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;

import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

/**
 * Subscribes the participants of a suggest submit and notifies them at once, as the settings of its suggest ask.
 */
@ApplicationScoped
public class SuggestNotificationListener
{
    public static final String REASON_AUTHOR = "author";
    public static final String REASON_REPLY = "reply";
    public static final String REASON_THREAD = "thread";
    public static final String REASON_SUBSCRIBER = "subscriber";
    private static final String TEMPLATE_SUGGEST_SUBMIT = "skin/plugins/suggest/notification_mail_participant_suggest_submit.html";
    private static final String TEMPLATE_COMMENT = "skin/plugins/suggest/notification_mail_participant_comment.html";
    private static final String MESSAGE_SUGGEST_SUBMIT_SUBJECT = "suggest.notificationMailParticipant.submit.subject";
    private static final String MESSAGE_COMMENT_SUBJECT = "suggest.notificationMailParticipant.comment.subject";
    private static final String MESSAGE_REPLY_SUBJECT = "suggest.notificationMailParticipant.reply.subject";
    private static final String MESSAGE_THREAD_SUBJECT = "suggest.notificationMailParticipant.thread.subject";
    private static final String MESSAGE_OFFICIAL_SUBJECT = "suggest.notificationMailParticipant.official.subject";
    private static final String MARK_SUGGEST = "suggest";
    private static final String MARK_SUGGEST_SUBMIT = "suggest_submit";
    private static final String MARK_SUGGEST_SUBMIT_URL = "suggest_submit_url";
    private static final String MARK_AUTHOR = "author";
    private static final String MARK_COMMENT = "comment";
    private static final String MARK_COMMENT_AUTHOR = "comment_author";
    private static final String MARK_PARENT_COMMENT = "parent_comment";
    private static final String MARK_REASON = "reason";
    private static final String URL_SUGGEST_SUBMIT = "jsp/site/Portal.jsp?page=suggest&id_suggest={0}&id_suggest_submit={1}&action=view_suggest_submit";

    @Inject
    private ISuggestSubmitService _suggestSubmitService;

    @Inject
    private ICommentSubmitService _commentSubmitService;

    @Inject
    private SuggestSubscriptionProviderService _subscriptionProvider;

    @Inject
    private SuggestNotificationService _notificationService;

    /**
     * Subscribes the author to a new suggest submit and tells the author it was received.
     *
     * @param event
     *            the creation event
     */
    public void onSuggestSubmitCreated( @ObservesAsync @Type( EventAction.CREATE ) SuggestSubmitEvent event )
    {
        Plugin plugin = PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME );
        SuggestSubmit suggestSubmit = _suggestSubmitService.findByPrimaryKey( event.getIdSuggestSubmit( ), false, plugin );
        Suggest suggest = ( suggestSubmit != null ) ? SuggestHome.findByPrimaryKey( suggestSubmit.getSuggest( ).getIdSuggest( ), plugin ) : null;

        if ( ( suggest == null ) || StringUtils.isBlank( suggestSubmit.getLuteceUserKey( ) ) )
        {
            return;
        }

        String strAuthor = suggestSubmit.getLuteceUserKey( );

        if ( suggest.isAutoSubscribe( ) )
        {
            _subscriptionProvider.createSuggestSubmitSubscription( strAuthor, suggestSubmit.getIdSuggestSubmit( ) );
        }

        if ( suggest.isNotifyParticipants( ) )
        {
            Map<String, Object> model = newModel( strAuthor, suggest, suggestSubmit );
            send( suggest, strAuthor, MESSAGE_SUGGEST_SUBMIT_SUBJECT, suggestSubmit, TEMPLATE_SUGGEST_SUBMIT, model );
        }
    }

    /**
     * Subscribes the author of a new comment to the suggest submit, then notifies the author of the suggest submit, the author of the answered comment
     * and, for an official answer, the subscribers.
     *
     * @param event
     *            the creation event
     */
    public void onCommentCreated( @ObservesAsync @Type( EventAction.CREATE ) CommentSubmitEvent event )
    {
        Plugin plugin = PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME );
        CommentSubmit comment = _commentSubmitService.findByPrimaryKey( event.getIdCommentSubmit( ), plugin );
        SuggestSubmit suggestSubmit = _suggestSubmitService.findByPrimaryKey( event.getIdSuggestSubmit( ), false, plugin );
        Suggest suggest = ( suggestSubmit != null ) ? SuggestHome.findByPrimaryKey( suggestSubmit.getSuggest( ).getIdSuggest( ), plugin ) : null;

        if ( ( comment == null ) || ( suggest == null ) )
        {
            return;
        }

        String strCommenter = comment.getLuteceUserKey( );

        if ( suggest.isAutoSubscribe( ) && StringUtils.isNotBlank( strCommenter ) )
        {
            _subscriptionProvider.createSuggestSubmitSubscription( strCommenter, suggestSubmit.getIdSuggestSubmit( ) );
        }

        if ( !comment.isActive( ) )
        {
            return;
        }

        CommentSubmit parentComment = ( comment.getIdParent( ) > SubmitFilter.ID_PARENT_NULL ) ? _commentSubmitService.findByPrimaryKey( comment.getIdParent( ), plugin )
                : null;
        Map<String, String> mapReasons = getRecipients( suggest, suggestSubmit, comment, parentComment );

        for ( Map.Entry<String, String> recipient : mapReasons.entrySet( ) )
        {
            Map<String, Object> model = newModel( recipient.getKey( ), suggest, suggestSubmit );
            model.put( MARK_COMMENT, comment );
            model.put( MARK_COMMENT_AUTHOR, _notificationService.getUserInfo( strCommenter ) );
            model.put( MARK_PARENT_COMMENT, parentComment );
            model.put( MARK_REASON, recipient.getValue( ) );
            send( suggest, recipient.getKey( ), getCommentSubject( comment, recipient.getValue( ) ), suggestSubmit, TEMPLATE_COMMENT, model );
        }
    }

    /**
     * Tells whether a subscriber received a comment at once, so that the daily digest leaves it out.
     *
     * @param suggest
     *            the suggest
     * @param comment
     *            the comment, with its suggest submit
     * @param strUserKey
     *            the subscriber
     * @return true if the subscriber was notified of the comment at once
     */
    public boolean isNotifiedAtOnce( Suggest suggest, CommentSubmit comment, String strUserKey )
    {
        CommentSubmit parentComment = ( comment.getIdParent( ) > SubmitFilter.ID_PARENT_NULL )
                ? _commentSubmitService.findByPrimaryKey( comment.getIdParent( ), PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME ) )
                : null;

        return comment.isActive( ) && getRecipients( suggest, comment.getSuggestSubmit( ), comment, parentComment ).containsKey( strUserKey );
    }

    /**
     * Returns the users notified at once of a comment, with the reason of each: the author of the answered comment, the authors of its other answers, the
     * author of the suggest submit, then for an official answer the subscribers. Nobody is notified of their own comment.
     *
     * @param suggest
     *            the suggest
     * @param suggestSubmit
     *            the commented suggest submit
     * @param comment
     *            the comment
     * @param parentComment
     *            the answered comment, or null
     * @return the reason of each recipient, by Lutece user key
     */
    private Map<String, String> getRecipients( Suggest suggest, SuggestSubmit suggestSubmit, CommentSubmit comment, CommentSubmit parentComment )
    {
        Map<String, String> mapReasons = new LinkedHashMap<>( );
        String strCommenter = comment.getLuteceUserKey( );

        if ( suggest.isNotifyParticipants( ) )
        {
            if ( parentComment != null )
            {
                addRecipient( mapReasons, parentComment.getLuteceUserKey( ), REASON_REPLY, strCommenter );
                addThreadRecipients( mapReasons, parentComment, strCommenter );
            }

            addRecipient( mapReasons, suggestSubmit.getLuteceUserKey( ), REASON_AUTHOR, strCommenter );
        }

        if ( suggest.isNotifySubscribers( ) && comment.isOfficialAnswer( ) )
        {
            for ( String strSubscriber : _subscriptionProvider.getSuggestSubmitSubscribers( suggestSubmit.getIdSuggestSubmit( ) ) )
            {
                addRecipient( mapReasons, strSubscriber, REASON_SUBSCRIBER, strCommenter );
            }
        }

        return mapReasons;
    }

    /**
     * Adds a recipient with its reason, unless it is blank, the author of the action or already a recipient.
     *
     * @param mapReasons
     *            the reason of each recipient
     * @param strUserKey
     *            the recipient
     * @param strReason
     *            the reason
     * @param strActor
     *            the author of the action
     */
    private static void addRecipient( Map<String, String> mapReasons, String strUserKey, String strReason, String strActor )
    {
        if ( StringUtils.isNotBlank( strUserKey ) && !Strings.CS.equals( strUserKey, strActor ) )
        {
            mapReasons.putIfAbsent( strUserKey, strReason );
        }
    }

    /**
     * Returns the subject key of a comment notification.
     *
     * @param comment
     *            the comment
     * @param strReason
     *            the reason of the recipient
     * @return the i18n key of the subject
     */
    private static String getCommentSubject( CommentSubmit comment, String strReason )
    {
        if ( comment.isOfficialAnswer( ) )
        {
            return MESSAGE_OFFICIAL_SUBJECT;
        }

        if ( REASON_THREAD.equals( strReason ) )
        {
            return MESSAGE_THREAD_SUBJECT;
        }

        return REASON_REPLY.equals( strReason ) ? MESSAGE_REPLY_SUBJECT : MESSAGE_COMMENT_SUBJECT;
    }

    /**
     * Adds the authors of the published answers to a comment, who take part in its discussion.
     *
     * @param mapReasons
     *            the reason of each recipient, by Lutece user key
     * @param parentComment
     *            the answered comment
     * @param strCommenter
     *            the author of the new answer, never notified
     */
    private static void addThreadRecipients( Map<String, String> mapReasons, CommentSubmit parentComment, String strCommenter )
    {
        SubmitFilter filter = new SubmitFilter( );
        filter.setIdParent( parentComment.getIdCommentSubmit( ) );
        filter.setIdCommentSubmitState( CommentSubmit.STATE_ENABLE );

        for ( CommentSubmit answer : CommentSubmitHome.getCommentSubmitList( filter, null, PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME ) ) )
        {
            addRecipient( mapReasons, answer.getLuteceUserKey( ), REASON_THREAD, strCommenter );
        }
    }

    /**
     * Returns the model of a notification about a suggest submit.
     *
     * @param strUserKey
     *            the recipient
     * @param suggest
     *            the suggest
     * @param suggestSubmit
     *            the suggest submit
     * @return the model
     */
    private Map<String, Object> newModel( String strUserKey, Suggest suggest, SuggestSubmit suggestSubmit )
    {
        Map<String, Object> model = _notificationService.newModel( strUserKey );
        model.put( MARK_SUGGEST, suggest );
        model.put( MARK_SUGGEST_SUBMIT, suggestSubmit );
        model.put( MARK_AUTHOR, _notificationService.getUserInfo( suggestSubmit.getLuteceUserKey( ) ) );
        model.put( MARK_SUGGEST_SUBMIT_URL, model.get( SuggestNotificationService.MARK_BASE_URL ) + MessageFormat.format( URL_SUGGEST_SUBMIT,
                Integer.toString( suggest.getIdSuggest( ) ), Integer.toString( suggestSubmit.getIdSuggestSubmit( ) ) ) );

        return model;
    }

    /**
     * Sends a notification about a suggest submit.
     *
     * @param suggest
     *            the suggest
     * @param strUserKey
     *            the recipient
     * @param strSubjectKey
     *            the i18n key of the subject, given the suggest submit title
     * @param suggestSubmit
     *            the suggest submit
     * @param strTemplate
     *            the body template
     * @param model
     *            the model
     */
    private void send( Suggest suggest, String strUserKey, String strSubjectKey, SuggestSubmit suggestSubmit, String strTemplate,
            Map<String, Object> model )
    {
        String strSubject = I18nService.getLocalizedString( strSubjectKey, new Object [ ] {
                suggestSubmit.getSuggestSubmitTitle( )
        }, I18nService.getDefaultLocale( ) );
        _notificationService.sendFromTemplate( strUserKey, StringUtils.defaultIfBlank( suggest.getNotificationNewCommentSenderName( ), suggest.getTitle( ) ),
                strSubject, strTemplate, model );
    }
}
