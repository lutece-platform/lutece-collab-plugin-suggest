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

import fr.paris.lutece.plugins.suggest.business.SuggestUserInfo;
import fr.paris.lutece.plugins.suggest.service.ISuggestUserInfoService;
import fr.paris.lutece.plugins.suggest.service.SuggestPlugin;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.LuteceUserService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;

/**
 * Sends the notification mails of the plugin to the front office users.
 */
@ApplicationScoped
public class SuggestNotificationService
{
    public static final String MARK_BASE_URL = "base_url";
    public static final String MARK_SUBSCRIPTIONS_URL = "subscriptions_url";
    public static final String MARK_RECIPIENT = "recipient";
    private static final String URL_SUBSCRIPTIONS = "jsp/site/Portal.jsp?page=subscribe";

    @Inject
    private ISuggestUserInfoService _suggestUserInfoService;

    /**
     * Returns the email of a user: the business email, else the home email, read in the user info the plugin stored, else in the user directory.
     *
     * @param strUserKey
     *            the Lutece user key
     * @return the email, or null if the user has none
     */
    public String getEmail( String strUserKey )
    {
        if ( StringUtils.isBlank( strUserKey ) )
        {
            return null;
        }

        SuggestUserInfo userInfo = getUserInfo( strUserKey );

        if ( ( userInfo != null ) && StringUtils.isNotBlank( StringUtils.defaultIfBlank( userInfo.getBusinessMail( ), userInfo.getHomeMail( ) ) ) )
        {
            return StringUtils.defaultIfBlank( userInfo.getBusinessMail( ), userInfo.getHomeMail( ) );
        }

        LuteceUser user = LuteceUserService.getLuteceUserFromName( strUserKey );

        if ( user == null )
        {
            return null;
        }

        return StringUtils.defaultIfBlank( user.getUserInfo( LuteceUser.BUSINESS_INFO_ONLINE_EMAIL ), user.getUserInfo( LuteceUser.HOME_INFO_ONLINE_EMAIL ) );
    }

    /**
     * Returns the user info the plugin stored for a user.
     *
     * @param strUserKey
     *            the Lutece user key
     * @return the user info, or null if none was stored
     */
    public SuggestUserInfo getUserInfo( String strUserKey )
    {
        return StringUtils.isBlank( strUserKey ) ? null
                : _suggestUserInfoService.findSuggestUserInfoByKey( strUserKey, PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME ) );
    }

    /**
     * Returns the model every notification gets: the site URL, the URL of the user's subscriptions and the recipient's user info.
     *
     * @param strUserKey
     *            the Lutece user key of the recipient
     * @return the model
     */
    public Map<String, Object> newModel( String strUserKey )
    {
        String strBaseUrl = StringUtils.defaultString( AppPathService.getProdUrl( (String) null ) );
        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_BASE_URL, strBaseUrl );
        model.put( MARK_SUBSCRIPTIONS_URL, strBaseUrl + URL_SUBSCRIPTIONS );
        model.put( MARK_RECIPIENT, getUserInfo( strUserKey ) );

        return model;
    }

    /**
     * Renders a notification whose subject and body are FreeMarker strings, and queues it to a user. A user without email is skipped; a rendering error
     * is logged and skips this mail only.
     *
     * @param strUserKey
     *            the Lutece user key of the recipient
     * @param strSenderName
     *            the sender name
     * @param strSubjectTemplate
     *            the FreeMarker string of the subject, HTML allowed
     * @param strBodyTemplate
     *            the FreeMarker string of the body
     * @param model
     *            the model
     * @return true if the mail was queued
     */
    public boolean sendFromStrings( String strUserKey, String strSenderName, String strSubjectTemplate, String strBodyTemplate, Map<String, Object> model )
    {
        String strEmail = getEmail( strUserKey );

        if ( StringUtils.isBlank( strEmail ) )
        {
            return false;
        }

        try
        {
            Locale locale = Locale.getDefault( );
            String strSubject = AppTemplateService.getTemplateFromStringFtl( strSubjectTemplate, locale, model ).getHtml( );
            String strBody = AppTemplateService.getTemplateFromStringFtl( strBodyTemplate, locale, model ).getHtml( );
            MailService.sendMailHtml( strEmail, strSenderName, MailService.getNoReplyEmail( ), Jsoup.parse( strSubject ).text( ), strBody );

            return true;
        }
        catch( RuntimeException e )
        {
            AppLogService.error( "Suggest notification not sent to {}: {}", strUserKey, e.getMessage( ), e );

            return false;
        }
    }

    /**
     * Renders a notification whose body is a template file, and queues it to a user. A user without email is skipped; a rendering error is logged and
     * skips this mail only.
     *
     * @param strUserKey
     *            the Lutece user key of the recipient
     * @param strSenderName
     *            the sender name
     * @param strSubject
     *            the subject
     * @param strTemplate
     *            the path of the body template
     * @param model
     *            the model
     * @return true if the mail was queued
     */
    public boolean sendFromTemplate( String strUserKey, String strSenderName, String strSubject, String strTemplate, Map<String, Object> model )
    {
        String strEmail = getEmail( strUserKey );

        if ( StringUtils.isBlank( strEmail ) )
        {
            return false;
        }

        try
        {
            String strBody = AppTemplateService.getTemplate( strTemplate, Locale.getDefault( ), model ).getHtml( );
            MailService.sendMailHtml( strEmail, strSenderName, MailService.getNoReplyEmail( ), strSubject, strBody );

            return true;
        }
        catch( RuntimeException e )
        {
            AppLogService.error( "Suggest notification not sent to {}: {}", strUserKey, e.getMessage( ), e );

            return false;
        }
    }
}
