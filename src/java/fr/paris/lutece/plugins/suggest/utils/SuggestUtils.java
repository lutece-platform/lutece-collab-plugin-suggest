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
package fr.paris.lutece.plugins.suggest.utils;

import fr.paris.lutece.plugins.avatar.service.AvatarService;
import fr.paris.lutece.plugins.suggest.business.Category;
import fr.paris.lutece.plugins.suggest.business.CommentSubmit;
import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitType;
import fr.paris.lutece.plugins.suggest.business.SuggestUserInfo;
import fr.paris.lutece.plugins.suggest.business.EntryFilter;
import fr.paris.lutece.plugins.suggest.business.EntryHome;
import fr.paris.lutece.plugins.suggest.business.EntryType;
import fr.paris.lutece.plugins.suggest.business.EntryTypeHome;
import fr.paris.lutece.plugins.suggest.business.FormError;
import fr.paris.lutece.plugins.suggest.business.IEntry;
import fr.paris.lutece.plugins.suggest.business.ReportedMessage;
import fr.paris.lutece.plugins.suggest.business.Response;
import fr.paris.lutece.plugins.suggest.business.SubmitFilter;
import fr.paris.lutece.plugins.suggest.business.Vote;
import fr.paris.lutece.plugins.suggest.business.VoteHome;
import fr.paris.lutece.plugins.suggest.business.attribute.SuggestAttribute;
import fr.paris.lutece.plugins.suggest.service.SuggestSubmitService;
import fr.paris.lutece.plugins.suggest.web.action.SuggestAdminSearchFields;
import fr.paris.lutece.portal.business.mailinglist.Recipient;
import fr.paris.lutece.portal.service.captcha.CaptchaSecurityService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.string.StringUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.ReflectionUtils;

import java.sql.Timestamp;

import java.text.DateFormat;
import java.text.ParseException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * class FormUtils
 *
 */
public final class SuggestUtils
{
    public static final int CONSTANT_ID_NULL = -1;
    public static final int CONSTANT_SUBMIT_FILTER_TO_DAY = 1;
    public static final int CONSTANT_SUBMIT_FILTER_WEEK = 2;
    public static final int CONSTANT_SUBMIT_FILTER_MONTH = 3;
    public static final int CONSTANT_SUBMIT_FILTER_YESTERDAY = 4;
    public static final String SERVLET_IMAGE_PATH = "image?resource_type=image_suggest&id=";
    public static final String EMPTY_STRING = "";
    public static final String PROPERTY_FILTER_ALL = "suggest.suggestFrame.labelFilterAll";
    public static final String PROPERTY_FILTER_TO_DAY = "suggest.suggestFrame.labelFilterTopDay";
    public static final String PROPERTY_FILTER_WEEK = "suggest.suggestFrame.labelFilterWeek";
    public static final String PROPERTY_FILTER_MONTH = "suggest.suggestFrame.labelFilterMonth";
    public static final String PROPERTY_FILTER_YESTERDAY = "suggest.suggestFrame.labelFilterYesterday";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_ENTRY = "entry";
    private static final String MARK_RESPONSE = "response";
    private static final String MARK_CATEGORY_LIST = "category_list";
    private static final String MARK_TYPE_LIST = "types_list";
    private static final String MARK_SUGGEST = "suggest";
    private static final String MARK_SUGGEST_SUBMIT = "suggest_submit";
    private static final String MARK_COMMENT_SUBMIT = "comment_submit";
    private static final String MARK_REPORTED_MESSAGE = "reported_message";
    private static final String MARK_BASE_URL = "base_url";
    private static final String MARK_JCAPTCHA = "jcaptcha";
    private static final String MARK_STR_ENTRY = "str_entry";
    private static final String MARK_IS_TITLE = "is_title";
    private static final String PARAMETER_ID_ENTRY_TYPE = "id_type";
    private static final String JCAPTCHA_PLUGIN = "jcaptcha";
    private static final String CONSTANT_WHERE = " WHERE ";
    private static final String CONSTANT_AND = " AND ";
    private static final String MARK_ID_DEFAULT_CATEGORY = "id_default_category";
    private static final String CONSTANT_CHARACTER_DOUBLE_QUOTE = "\"";
    private static final String CONSTANT_CHARACTER_SIMPLE_QUOTE = "'";
    private static final String CONSTANTE_CHARACTERNEW_LINE = "\n";
    private static final String CONSTANTE_CHARACTER_RETURN = "\r";
    private static final String MARK_AVATAR = "avatar";

    // Xml Tags

    // TEMPLATE
    private static final String TEMPLATE_NOTIFICATION_MAIL_NEW_SUGGEST_SUBMIT = "skin/plugins/suggest/notification_mail_new_suggest_submit.html";
    private static final String TEMPLATE_NOTIFICATION_MAIL_NEW_SUGGEST_SUBMIT_DISABLE = "skin/plugins/suggest/notification_mail_new_suggest_submit_disable.html";
    private static final String TEMPLATE_NOTIFICATION_MAIL_NEW_COMMENT_SUBMIT = "skin/plugins/suggest/notification_mail_new_comment_submit.html";
    private static final String TEMPLATE_NOTIFICATION_MAIL_NEW_REPORTED_MESSAGE = "skin/plugins/suggest/notification_mail_new_reported_message.html";

    // property
    private static final String PROPERTY_NOTIFICATION_MAIL_NEW_SUGGEST_SUBMIT_SUBJECT = "suggest.notificationMailNewSuggestSubmit.subject";
    private static final String PROPERTY_NOTIFICATION_MAIL_NEW_SUGGEST_SUBMIT_SENDER_NAME = "suggest.notificationMailNewSuggestSubmit.senderName";
    private static final String PROPERTY_NOTIFICATION_MAIL_NEW_SUGGEST_SUBMIT_DISABLE_SUBJECT = "suggest.notificationMailNewSuggestSubmitDisable.subject";
    private static final String PROPERTY_NOTIFICATION_MAIL_NEW_SUGGEST_SUBMIT_DISABLE_SENDER_NAME = "suggest.notificationMailNewSuggestSubmitDisable.senderName";
    private static final String PROPERTY_NOTIFICATION_MAIL_NEW_COMMENT_SUBMIT_SUBJECT = "suggest.notificationMailNewCommentSubmit.subject";
    private static final String PROPERTY_NOTIFICATION_MAIL_NEW_COMMENT_SUBMIT_SENDER_NAME = "suggest.notificationMailNewCommentSubmit.senderName";
    private static final String PROPERTY_NOTIFICATION_MAIL_NEW_REPORTED_MESSAGE_SUBJECT = "suggest.notificationMailNewReportedMessage.subject";
    private static final String PROPERTY_NOTIFICATION_MAIL_NEW_REPORTED_MESSAGE_SENDER_NAME = "suggest.notificationMailNewReportedMessage.senderName";
    private static final String PROPERTY_SORTER_LIST_ITEM_DATE_RESPONSE_ASC = "suggest.sorterListItemDateResponseAsc";
    private static final String PROPERTY_SORTER_LIST_ITEM_DATE_RESPONSE_DESC = "suggest.sorterListItemDateResponseDesc";
    private static final String PROPERTY_COMMENT_STATE_ENABLE = "suggest.manageCommentSubmit.stateEnable";
    private static final String PROPERTY_COMMENT_STATE_DISABLE = "suggest.manageCommentSubmit.stateDisable";
    private static final String PROPERTY_SORTER_LIST_ITEM_SCORE_ASC = "suggest.sorterListItemScoreAsc";
    private static final String PROPERTY_SORTER_LIST_ITEM_SCORE_DESC = "suggest.sorterListItemScoreDesc";
    private static final String PROPERTY_SORTER_LIST_ITEM_VIEW_ASC = "suggest.sorterListItemViewAsc";
    private static final String PROPERTY_SORTER_LIST_ITEM_VIEW_DESC = "suggest.sorterListItemViewDesc";
    private static final String PROPERTY_SORTER_LIST_ITEM_AMOUNT_COMMENT_ASC = "suggest.sorterListItemCommentAsc";
    private static final String PROPERTY_SORTER_LIST_ITEM_AMOUNT_COMMENT_DESC = "suggest.sorterListItemCommentDesc";
    private static final String PROPERTY_SORTER_LIST_ITEM_MANUAL = "suggest.sorterListItemManualDesc";
    private static final String REGEX_ID = "^[\\d]+$";
    private static final String PROPERTY_CHOOSE_CATEGORY = "suggest.suggestsubmit.choose.category";
    private static final String PROPERTY_CHOOSE_TYPE = "suggest.suggestsubmit.choose.type";
    private static final String PROPERTY_PROD_URL = "lutece.prod.url";

    /**
     * FormUtils
     *
     */
    private SuggestUtils( )
    {
    }

    /**
     * sendMail of notification for new suggest submit
     * 
     * @param suggest
     *            the suggest
     * @param suggestSubmit
     *            the new suggestSubmit
     * @param locale
     *            the locale
     * @param request
     *            the request
     */
    public static void sendNotificationNewSuggestSubmit( Suggest suggest, SuggestSubmit suggestSubmit, Locale locale, HttpServletRequest request )
    {
        try
        {
            String strSubject = I18nService.getLocalizedString( PROPERTY_NOTIFICATION_MAIL_NEW_SUGGEST_SUBMIT_SUBJECT, locale );
            String strSenderName = I18nService.getLocalizedString( PROPERTY_NOTIFICATION_MAIL_NEW_SUGGEST_SUBMIT_SENDER_NAME, locale );
            String strSenderEmail = MailService.getNoReplyEmail( );

            // we have to replace the src='image? string by a string containing the server url
            if ( suggestSubmit.getSuggestSubmitValue( ).toString( ).contains( "src='image?" ) )
            {
                suggestSubmit.setSuggestSubmitValue( suggestSubmit.getSuggestSubmitValue( ).toString( )
                        .replace( "src='image?", "src='" + AppPropertiesService.getProperty( PROPERTY_PROD_URL ) + "/image?" ) );
            }

            Collection<Recipient> listRecipients = AdminMailingListService.getRecipients( suggest.getIdMailingListSuggestSubmit( ) );
            Map<String, Object> model = new HashMap<>( );
            model.put( MARK_SUGGEST, suggest );
            model.put( MARK_SUGGEST_SUBMIT, suggestSubmit );
            model.put( MARK_BASE_URL, AppPathService.getBaseUrl( request ) );

            HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_NOTIFICATION_MAIL_NEW_SUGGEST_SUBMIT, locale, model );

            // Send Mail
            for ( Recipient recipient : listRecipients )
            {
                // Build the mail message
                MailService.sendMailHtml( recipient.getEmail( ), strSenderName, strSenderEmail, strSubject, t.getHtml( ) );
            }
        }
        catch( Exception e )
        {
            AppLogService.error( "Error during Notify new suggest submit  : " + e.getMessage( ) );
        }
    }

    /**
     * sendMail of notification for new suggest submit disable
     * 
     * @param suggest
     *            the suggest
     * @param suggestSubmit
     *            the suggest submit disable
     * @param locale
     *            the locale
     */
    public static void sendNotificationNewSuggestSubmitDisable( Suggest suggest, SuggestSubmit suggestSubmit, Locale locale )
    {
        try
        {
            String strSubject = I18nService.getLocalizedString( PROPERTY_NOTIFICATION_MAIL_NEW_SUGGEST_SUBMIT_DISABLE_SUBJECT, locale );
            String strSenderName = I18nService.getLocalizedString( PROPERTY_NOTIFICATION_MAIL_NEW_SUGGEST_SUBMIT_DISABLE_SENDER_NAME, locale );
            String strSenderEmail = MailService.getNoReplyEmail( );

            // we have to replace the src='image? string by a string containing the server url
            if ( suggestSubmit.getSuggestSubmitValue( ).toString( ).contains( "src='image?" ) )
            {
                suggestSubmit.setSuggestSubmitValue( suggestSubmit.getSuggestSubmitValue( ).toString( )
                        .replace( "src='image?", "src='" + AppPropertiesService.getProperty( PROPERTY_PROD_URL ) + "/image?" ) );
            }

            Collection<Recipient> listRecipients = AdminMailingListService.getRecipients( suggest.getIdMailingListSuggestSubmit( ) );
            Map<String, Object> model = new HashMap<>( );
            model.put( MARK_SUGGEST, suggest );
            model.put( MARK_SUGGEST_SUBMIT, suggestSubmit );

            HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_NOTIFICATION_MAIL_NEW_SUGGEST_SUBMIT_DISABLE, locale, model );

            // Send Mail
            for ( Recipient recipient : listRecipients )
            {
                // Build the mail message
                MailService.sendMailHtml( recipient.getEmail( ), strSenderName, strSenderEmail, strSubject, t.getHtml( ) );
            }
        }
        catch( Exception e )
        {
            AppLogService.error( "Error during Notify new suggest submit disable  : " + e.getMessage( ) );
        }
    }

    /**
     * sendMail of notification for new comment submit
     *
     * @param suggest
     *            the suggest
     * @param commentSubmit
     *            the new comment submit
     * @param locale
     *            the locale
     * @param request
     *            the request
     */
    public static void sendNotificationNewCommentSubmit( Suggest suggest, CommentSubmit commentSubmit, Locale locale, HttpServletRequest request )
    {
        try
        {
            String strSubject = I18nService.getLocalizedString( PROPERTY_NOTIFICATION_MAIL_NEW_COMMENT_SUBMIT_SUBJECT, locale );
            String strSenderName = I18nService.getLocalizedString( PROPERTY_NOTIFICATION_MAIL_NEW_COMMENT_SUBMIT_SENDER_NAME, locale );
            String strSenderEmail = MailService.getNoReplyEmail( );

            Collection<Recipient> listRecipients = AdminMailingListService.getRecipients( suggest.getIdMailingListSuggestSubmit( ) );
            Map<String, Object> model = new HashMap<>( );
            model.put( MARK_SUGGEST, suggest );
            model.put( MARK_COMMENT_SUBMIT, commentSubmit );
            model.put( MARK_BASE_URL, AppPathService.getBaseUrl( request ) );

            HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_NOTIFICATION_MAIL_NEW_COMMENT_SUBMIT, locale, model );

            // Send Mail
            for ( Recipient recipient : listRecipients )
            {
                // Build the mail message
                MailService.sendMailHtml( recipient.getEmail( ), strSenderName, strSenderEmail, strSubject, t.getHtml( ) );
            }
        }
        catch( Exception e )
        {
            AppLogService.error( "Error during Notify new comment : " + e.getMessage( ) );
        }
    }

    /**
     * sendMail of notification for new reported message
     * 
     * @param suggest
     *            the suggest
     * @param reportedMessage
     *            the reported Message
     * @param locale
     *            the locale
     * @param request
     *            the request
     */
    public static void sendNotificationNewReportedMessage( Suggest suggest, ReportedMessage reportedMessage, Locale locale, HttpServletRequest request )
    {
        try
        {
            String strSubject = I18nService.getLocalizedString( PROPERTY_NOTIFICATION_MAIL_NEW_REPORTED_MESSAGE_SUBJECT, locale );
            String strSenderName = I18nService.getLocalizedString( PROPERTY_NOTIFICATION_MAIL_NEW_REPORTED_MESSAGE_SENDER_NAME, locale );
            String strSenderEmail = MailService.getNoReplyEmail( );

            Collection<Recipient> listRecipients = AdminMailingListService.getRecipients( suggest.getIdMailingListSuggestSubmit( ) );
            Map<String, Object> model = new HashMap<>( );
            model.put( MARK_SUGGEST, suggest );
            model.put( MARK_REPORTED_MESSAGE, reportedMessage );
            model.put( MARK_BASE_URL, AppPathService.getBaseUrl( request ) );

            HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_NOTIFICATION_MAIL_NEW_REPORTED_MESSAGE, locale, model );

            // Send Mail
            for ( Recipient recipient : listRecipients )
            {
                // Build the mail message
                MailService.sendMailHtml( recipient.getEmail( ), strSenderName, strSenderEmail, strSubject, t.getHtml( ) );
            }
        }
        catch( Exception e )
        {
            AppLogService.error( "Error during Notify new repported message  : " + e.getMessage( ) );
        }
    }

    /**
     * return a timestamp Object which correspond with the string specified in parameter.
     * 
     * @param strDate
     *            the date who must convert
     * @param locale
     *            the locale
     * @return a timestamp Object which correspond with the string specified in parameter.
     */
    public static Timestamp getLastMinute( String strDate, Locale locale )
    {
        try
        {
            Date date;
            DateFormat dateFormat = DateFormat.getDateInstance( DateFormat.SHORT, locale );
            dateFormat.setLenient( false );
            date = dateFormat.parse( strDate.trim( ) );

            Calendar caldate = new GregorianCalendar( );
            caldate.setTime( date );
            caldate.set( Calendar.MILLISECOND, 0 );
            caldate.set( Calendar.SECOND, 0 );
            caldate.set( Calendar.HOUR_OF_DAY, caldate.getActualMaximum( Calendar.HOUR_OF_DAY ) );
            caldate.set( Calendar.MINUTE, caldate.getActualMaximum( Calendar.MINUTE ) );

            Timestamp timeStamp = new Timestamp( caldate.getTimeInMillis( ) );

            return timeStamp;
        }
        catch( ParseException e )
        {
            return null;
        }
    }

    /**
     * return a timestamp Object which correspond with the string specified in parameter.
     * 
     * @param strDate
     *            the date who must convert
     * @param locale
     *            the locale
     * @return a timestamp Object which correspond with the string specified in parameter.
     */
    public static Timestamp getFirstMinute( String strDate, Locale locale )
    {
        try
        {
            Date date;
            DateFormat dateFormat = DateFormat.getDateInstance( DateFormat.SHORT, locale );
            dateFormat.setLenient( false );
            date = dateFormat.parse( strDate.trim( ) );

            Calendar caldate = new GregorianCalendar( );
            caldate.setTime( date );
            caldate.set( Calendar.MILLISECOND, 0 );
            caldate.set( Calendar.SECOND, 0 );
            caldate.set( Calendar.HOUR_OF_DAY, caldate.getActualMinimum( Calendar.HOUR_OF_DAY ) );
            caldate.set( Calendar.MINUTE, caldate.getActualMinimum( Calendar.MINUTE ) );

            Timestamp timeStamp = new Timestamp( caldate.getTimeInMillis( ) );

            return timeStamp;
        }
        catch( ParseException e )
        {
            return null;
        }
    }

    /**
     * return the first day of week function of the date .
     * 
     * @param date
     *            the date
     * @return the first day of week function of the date.
     */
    public static Timestamp getFirstDayOfWeek( Timestamp date )
    {
        Calendar caldate = new GregorianCalendar( );
        caldate.setTime( date );
        caldate.set( Calendar.MILLISECOND, caldate.getActualMinimum( Calendar.MILLISECOND ) );
        caldate.set( Calendar.SECOND, caldate.getActualMinimum( Calendar.SECOND ) );
        caldate.set( Calendar.HOUR_OF_DAY, caldate.getActualMinimum( Calendar.HOUR_OF_DAY ) );
        caldate.set( Calendar.MINUTE, caldate.getActualMinimum( Calendar.MINUTE ) );
        caldate.set( Calendar.DAY_OF_WEEK, caldate.getFirstDayOfWeek( ) );

        Timestamp timeStamp = new Timestamp( caldate.getTimeInMillis( ) );

        return timeStamp;
    }

    /**
     * return the last day of week function of the date .
     * 
     * @param date
     *            the date
     * @return the last day of week function of the date.
     */
    public static Timestamp getLastDayOfWeek( Timestamp date )
    {
        Calendar caldate = new GregorianCalendar( );
        caldate.setTime( date );
        caldate.set( Calendar.MILLISECOND, caldate.getActualMaximum( Calendar.MILLISECOND ) );
        caldate.set( Calendar.SECOND, caldate.getActualMaximum( Calendar.SECOND ) );
        caldate.set( Calendar.HOUR_OF_DAY, caldate.getActualMaximum( Calendar.HOUR_OF_DAY ) );
        caldate.set( Calendar.MINUTE, caldate.getActualMaximum( Calendar.MINUTE ) );
        caldate.set( Calendar.DAY_OF_WEEK, caldate.getFirstDayOfWeek( ) + 6 );

        Timestamp timeStamp = new Timestamp( caldate.getTimeInMillis( ) );

        return timeStamp;
    }

    /**
     * return a timestamp Object which correspond at the fist minute of the date .
     * 
     * @param date
     *            the date
     * @return a timestamp Object which correspond at the fist minute of the date .
     */
    public static Timestamp getFirstMinute( Timestamp date )
    {
        Calendar caldate = new GregorianCalendar( );
        caldate.setTime( date );
        caldate.set( Calendar.MILLISECOND, caldate.getActualMinimum( Calendar.MILLISECOND ) );
        caldate.set( Calendar.SECOND, caldate.getActualMinimum( Calendar.SECOND ) );
        caldate.set( Calendar.HOUR_OF_DAY, caldate.getActualMinimum( Calendar.HOUR_OF_DAY ) );
        caldate.set( Calendar.MINUTE, caldate.getActualMinimum( Calendar.MINUTE ) );

        Timestamp timeStamp = new Timestamp( caldate.getTimeInMillis( ) );

        return timeStamp;
    }

    /**
     * return a timestamp Object which correspond at the last minute of the date .
     * 
     * @param date
     *            the date
     * @return a timestamp Object which correspond at the last minute of the date .
     */
    public static Timestamp getLastMinute( Timestamp date )
    {
        Calendar caldate = new GregorianCalendar( );
        caldate.setTime( date );
        caldate.set( Calendar.MILLISECOND, caldate.getActualMaximum( Calendar.MILLISECOND ) );
        caldate.set( Calendar.SECOND, caldate.getActualMaximum( Calendar.SECOND ) );
        caldate.set( Calendar.HOUR_OF_DAY, caldate.getActualMaximum( Calendar.HOUR_OF_DAY ) );
        caldate.set( Calendar.MINUTE, caldate.getActualMaximum( Calendar.MINUTE ) );

        Timestamp timeStamp = new Timestamp( caldate.getTimeInMillis( ) );

        return timeStamp;
    }

    /**
     * return the first day of month function of the date .
     * 
     * @param date
     *            the date
     * @return the first day of mont function of the date.
     */
    public static Timestamp getFirstDayOfMonth( Timestamp date )
    {
        Calendar caldate = new GregorianCalendar( );
        caldate.setTime( date );
        caldate.set( Calendar.MILLISECOND, caldate.getActualMinimum( Calendar.MILLISECOND ) );
        caldate.set( Calendar.SECOND, caldate.getActualMinimum( Calendar.SECOND ) );
        caldate.set( Calendar.HOUR_OF_DAY, caldate.getActualMinimum( Calendar.HOUR_OF_DAY ) );
        caldate.set( Calendar.MINUTE, caldate.getActualMinimum( Calendar.MINUTE ) );
        caldate.set( Calendar.DAY_OF_MONTH, caldate.getActualMinimum( Calendar.DAY_OF_MONTH ) );

        Timestamp timeStamp = new Timestamp( caldate.getTimeInMillis( ) );

        return timeStamp;
    }

    /**
     * return the last day of month function of the date .
     * 
     * @param date
     *            the date
     * @return the last day of mont function of the date.
     */
    public static Timestamp getLastDayOfMonth( Timestamp date )
    {
        Calendar caldate = new GregorianCalendar( );
        caldate.setTime( date );
        caldate.set( Calendar.MILLISECOND, caldate.getActualMaximum( Calendar.MILLISECOND ) );
        caldate.set( Calendar.SECOND, caldate.getActualMaximum( Calendar.SECOND ) );
        caldate.set( Calendar.HOUR_OF_DAY, caldate.getActualMaximum( Calendar.HOUR_OF_DAY ) );
        caldate.set( Calendar.MINUTE, caldate.getActualMaximum( Calendar.MINUTE ) );
        caldate.set( Calendar.DAY_OF_MONTH, caldate.getActualMaximum( Calendar.DAY_OF_MONTH ) );

        Timestamp timeStamp = new Timestamp( caldate.getTimeInMillis( ) );

        return timeStamp;
    }

    /**
     * Converts une java.sql.Timestamp date in a String date in a "jj/mm/aaaa" format
     *
     * @param date
     *            java.sql.Timestamp date to convert
     * @param locale
     *            the locale
     * @return strDate The String date in the short locale format or the emmpty String if the date is null
     */
    public static String getDateString( Timestamp date, Locale locale )
    {
        DateFormat dateFormat = DateFormat.getDateInstance( DateFormat.SHORT, locale );

        return dateFormat.format( date );
    }

    /**
     * return current date
     * 
     * @return return current date
     */
    public static Timestamp getCurrentDate( )
    {
        return new Timestamp( GregorianCalendar.getInstance( ).getTimeInMillis( ) );
    }

    /**
     * Return a date corresponding to the date provides in parameter add with a number of day
     * 
     * @param date
     *            the date
     * @param nDay
     *            the number of day to add
     * @return a timestamp Object which correspond at the date + nDay .
     */
    public static Timestamp getDateAfterNDay( Timestamp date, int nDay )
    {
        Calendar caldate = new GregorianCalendar( );
        caldate.setTime( date );
        caldate.add( Calendar.DATE, nDay );

        return new Timestamp( caldate.getTimeInMillis( ) );
    }

    /**
     * return an instance of IEntry function of type entry
     * 
     * @param request
     *            the request
     * @param plugin
     *            the plugin
     * @return an instance of IEntry function of type entry
     */
    public static IEntry createEntryByType( HttpServletRequest request, Plugin plugin )
    {
        String strIdType = request.getParameter( PARAMETER_ID_ENTRY_TYPE );
        int nIdType = -1;
        IEntry entry = null;
        EntryType entryType;

        if ( ( strIdType != null ) && !strIdType.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdType = Integer.parseInt( strIdType );
            }
            catch( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return null;
            }
        }

        if ( nIdType == -1 )
        {
            return null;
        }

        entryType = EntryTypeHome.findByPrimaryKey( nIdType, plugin );

        try
        {
            entry = (IEntry) Class.forName( entryType.getClassName( ) ).newInstance( );
            entry.setEntryType( entryType );
        }
        catch( ClassNotFoundException e )
        {
            // class doesn't exist
            AppLogService.error( e );
        }
        catch( InstantiationException e )
        {
            // Class is abstract or is an interface or haven't accessible builder
            AppLogService.error( e );
        }
        catch( IllegalAccessException e )
        {
            // can't access to rhe class
            AppLogService.error( e );
        }

        return entry;
    }

    /**
     * return the index in the list of the entry whose key is specified in parameter
     * 
     * @param nIdEntry
     *            the key of the entry
     * @param listEntry
     *            the list of the entry
     * @return the index in the list of the entry whose key is specified in parameter
     */
    public static int getIndexEntryInTheEntryList( int nIdEntry, List<IEntry> listEntry )
    {
        int nIndex = 0;

        for ( IEntry entry : listEntry )
        {
            if ( entry.getIdEntry( ) == nIdEntry )
            {
                return nIndex;
            }

            nIndex++;
        }

        return nIndex;
    }

    /**
     *
     * @param suggest
     * @param plugin
     * @param locale
     * @param nIdDefaultCategory
     * @param bBackOffice
     * @return
     */
    public static Map<String, Object> getModelHtmlForm( Suggest suggest, Plugin plugin, Locale locale, int nIdDefaultCategory, boolean bBackOffice )
    {
        List<IEntry> listEntryFirstLevel;
        Map<String, Object> model = new HashMap<>( );

        EntryFilter filter;
        StringBuffer strBuffer = new StringBuffer( );
        filter = new EntryFilter( );
        filter.setIdSuggest( suggest.getIdSuggest( ) );
        listEntryFirstLevel = EntryHome.getEntryList( filter, plugin );

        ArrayList<Category> listCats = new ArrayList<Category>( );
        Category category = new Category( );

        category.setIdCategory( -1 );
        category.setTitle( I18nService.getLocalizedString( PROPERTY_CHOOSE_CATEGORY, locale ) );

        if ( !suggest.getCategories( ).isEmpty( ) )
        {
            listCats.add( category );
        }

        listCats.addAll( suggest.getCategories( ) );

        SuggestSubmitType type = new SuggestSubmitType( );
        List<SuggestSubmitType> listTypes = suggest.getSuggestSubmitTypes( );
        List<SuggestSubmitType> listTypes2Show = new ArrayList<SuggestSubmitType>( );

        type.setIdType( -1 );
        type.setName( I18nService.getLocalizedString( PROPERTY_CHOOSE_TYPE, locale ) );

        for ( SuggestSubmitType t : listTypes )
        {
            if ( bBackOffice || t.getParameterizableInFO( ) )
            {
                listTypes2Show.add( t );
            }
        }

        if ( !listTypes2Show.isEmpty( ) )
        {
            listTypes2Show.add( 0, type );
        }

        ReferenceList refCategoryList = getRefListCategory( listCats );
        ReferenceList refTypeList = getRefListType( listTypes2Show );

        for ( IEntry entry : listEntryFirstLevel )
        {
            SuggestUtils.getHtmlFormEntry( entry.getIdEntry( ), plugin, strBuffer, locale );
        }

        CaptchaSecurityService captchaSecurityService = new CaptchaSecurityService( );

        if ( suggest.isActiveCaptcha( ) && PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) )
        {
            model.put( MARK_JCAPTCHA, captchaSecurityService.getHtmlCode( ) );
        }

        model.put( MARK_CATEGORY_LIST, refCategoryList );
        model.put( MARK_TYPE_LIST, refTypeList );
        model.put( MARK_ID_DEFAULT_CATEGORY, nIdDefaultCategory );
        model.put( MARK_SUGGEST, suggest );
        model.put( MARK_STR_ENTRY, strBuffer.toString( ) );
        model.put( MARK_LOCALE, locale );

        return model;
    }

    /**
     * insert in the string buffer the content of the html code of the entry
     * 
     * @param nIdEntry
     *            the key of the entry which html code must be insert in the stringBuffer
     * @param plugin
     *            the plugin
     * @param stringBuffer
     *            the buffer which contains the html code
     * @param locale
     *            the locale
     */
    public static void getHtmlFormEntry( int nIdEntry, Plugin plugin, StringBuffer stringBuffer, Locale locale )
    {
        Map<String, Object> model = new HashMap<>( );
        HtmlTemplate template;
        IEntry entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );
        model.put( MARK_ENTRY, entry );

        template = AppTemplateService.getTemplate( entry.getTemplateHtmlCodeForm( ), locale, model );
        stringBuffer.append( template.getHtml( ) );
    }

    /**
     * return the content of the html code response
     * 
     * @param response
     *            the response which html code must be generate
     * @param stringBuffer
     *            the stringBuffer
     * @param bTitle
     *            true if the response is a title
     * @param locale
     *            the locale
     */
    public static void getHtmlResponseEntry( Response response, StringBuffer stringBuffer, Locale locale, boolean bTitle )
    {
        if ( ( response != null ) && ( response.getEntry( ) != null ) )
        {
            Map<String, Object> model = new HashMap<>( );
            HtmlTemplate template;
            model.put( MARK_RESPONSE, response );
            model.put( MARK_IS_TITLE, bTitle );
            template = AppTemplateService.getTemplate( response.getEntry( ).getTemplateHtmlCodeResponse( ), locale, model );
            stringBuffer.append( template.getHtml( ) );
        }
    }

    /**
     * return the content of the html code of the suggest submit
     * 
     * @param suggestSubmit
     *            the suggestsubmit
     * @param locale
     *            the locale
     * @return the content of the html code of the suggest submit
     */
    public static String getHtmlSuggestSubmitValue( SuggestSubmit suggestSubmit, Locale locale )
    {
        StringBuffer strBuffer = new StringBuffer( );
        int ncptTitle = 1;

        for ( Response response : suggestSubmit.getResponses( ) )
        {
            if ( ncptTitle == 1 )
            {
                getHtmlResponseEntry( response, strBuffer, locale, true );
            }
            else
            {
                getHtmlResponseEntry( response, strBuffer, locale, false );
            }

            ncptTitle++;
        }

        return strBuffer.toString( );
    }

    /**
     * return the content of the html code of the suggest submit show in the list of suggest submit
     * 
     * @param suggestSubmit
     *            the suggestsubmit
     * @param locale
     *            the locale
     * @return the content of the html code of the suggest submit show in the list of suggest submit
     */
    public static String getHtmlSuggestSubmitValueShowInTheList( SuggestSubmit suggestSubmit, Locale locale )
    {
        StringBuffer strBuffer = new StringBuffer( );
        int nNumberCaractersShown = suggestSubmit.getSuggest( ).getNumberSuggestSubmitCaractersShown( );
        int nNumberCaractersInBuffer = 0;
        int ncptTitle = 1;

        for ( Response response : suggestSubmit.getResponses( ) )
        {
            if ( ( response.getValueResponse( ) != null ) && ( response.getEntry( ) != null ) && response.getEntry( ).isShowInSuggestSubmitList( ) )
            {
                if ( ( nNumberCaractersInBuffer + response.getValueResponse( ).length( ) ) <= nNumberCaractersShown )
                {
                    nNumberCaractersInBuffer += response.getValueResponse( ).length( );

                    if ( ncptTitle == 1 )
                    {
                        getHtmlResponseEntry( response, strBuffer, locale, true );
                    }
                    else
                    {
                        getHtmlResponseEntry( response, strBuffer, locale, false );
                    }
                }
                else
                {
                    Response lastResponse = new Response( );
                    lastResponse.setEntry( response.getEntry( ) );
                    lastResponse.setValueResponse( response.getValueResponse( ).substring( 0, nNumberCaractersShown - nNumberCaractersInBuffer ) + "..." );

                    if ( ncptTitle == 1 )
                    {
                        getHtmlResponseEntry( lastResponse, strBuffer, locale, true );
                    }
                    else
                    {
                        getHtmlResponseEntry( lastResponse, strBuffer, locale, false );
                    }

                    break;
                }
            }

            ncptTitle++;
        }

        return strBuffer.toString( );
    }

    /**
     * return the title of the suggest submit
     * 
     * @param suggestSubmit
     *            the suggestsubmit
     * @param locale
     *            the locale
     * @return the title of the suggest submit
     */
    public static String getSuggestSubmitTitle( SuggestSubmit suggestSubmit, Locale locale )
    {
        StringBuffer strBuffer = new StringBuffer( );

        if ( ( suggestSubmit.getResponses( ) != null ) && ( suggestSubmit.getResponses( ).size( ) != 0 ) && ( suggestSubmit.getResponses( ).get( 0 ) != null ) )
        {
            strBuffer.append( suggestSubmit.getResponses( ).get( 0 ).getValueResponse( ) );
        }

        return strBuffer.toString( );
    }

    /**
     * perform in the object suggestSubmit the responses associates to the suggestsubmit
     * 
     * @param request
     * @param suggestSubmit
     * @param plugin
     * @param locale
     * @return
     * @throws SiteMessageException
     */
    public static FormError getAllResponsesData( HttpServletRequest request, SuggestSubmit suggestSubmit, Plugin plugin, Locale locale )
    {
        List<IEntry> listEntry;
        EntryFilter filter;
        FormError formError = null;

        filter = new EntryFilter( );
        filter.setIdSuggest( suggestSubmit.getSuggest( ).getIdSuggest( ) );
        listEntry = EntryHome.getEntryList( filter, plugin );

        List<Response> listResponse = new ArrayList<Response>( );
        suggestSubmit.setResponses( listResponse );

        for ( IEntry entry : listEntry )
        {
            formError = getResponseEntry( request, entry.getIdEntry( ), plugin, suggestSubmit, false, locale );

            if ( formError != null )
            {
                return formError;
            }
        }

        return null;
    }

    /**
     * perform in the object suggestSubmit the responses associates with a entry specify in parameter. return null if there is no error in the response else
     * return a FormError Object
     * 
     * @param request
     *            the request
     * @param nIdEntry
     *            the key of the entry
     * @param plugin
     *            the plugin
     * @param suggestSubmit
     *            suggest Submit Object
     * @param bResponseNull
     *            true if the response create must be null
     * @param locale
     *            the locale
     * @return null if there is no error in the response else return a FormError Object
     */
    public static FormError getResponseEntry( HttpServletRequest request, int nIdEntry, Plugin plugin, SuggestSubmit suggestSubmit, boolean bResponseNull,
            Locale locale )
    {
        FormError formError = null;
        Response response = null;
        IEntry entry = null;
        List<Response> listResponse = new ArrayList<Response>( );
        entry = EntryHome.findByPrimaryKey( nIdEntry, plugin );

        if ( !bResponseNull )
        {
            formError = entry.getResponseData( suggestSubmit.getIdSuggestSubmit( ), request, listResponse, locale, plugin );
        }
        else
        {
            response = new Response( );
            response.setEntry( entry );
            listResponse.add( response );
        }

        if ( formError != null )
        {
            return formError;
        }

        suggestSubmit.getResponses( ).addAll( listResponse );

        return null;
    }

    /**
     * convert a string to int
     * 
     * @param strParameter
     *            the string parameter to convert
     * @return the conversion
     */
    public static int getIntegerParameter( String strParameter )
    {
        int nIdParameter = -1;

        try
        {
            if ( ( strParameter != null ) && strParameter.matches( REGEX_ID ) )
            {
                nIdParameter = Integer.parseInt( strParameter );
            }
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        return nIdParameter;
    }

    /**
     * Returns a copy of the string , with leading and trailing whitespace omitted.
     * 
     * @param strParameter
     *            the string parameter to convert
     * @return null if the strParameter is null other return with leading and trailing whitespace omitted.
     */
    public static String trim( String strParameter )
    {
        if ( strParameter != null )
        {
            return strParameter.trim( );
        }

        return strParameter;
    }

    /**
     * initialized the submit filter object with the period specified in parameter
     * 
     * @param submitFilter
     *            the filter to initialized
     * @param nIdPeriod
     *            the id of the period (DAY,WEEK,MONTH)
     */
    public static void initSubmitFilterByPeriod( SubmitFilter submitFilter, int nIdPeriod )
    {
        Timestamp date = getCurrentDate( );

        switch( nIdPeriod )
        {
            case CONSTANT_SUBMIT_FILTER_YESTERDAY:
                date = getDateAfterNDay( date, -1 );
                submitFilter.setDateFirst( getFirstMinute( date ) );
                submitFilter.setDateLast( getLastMinute( date ) );

                break;

            case CONSTANT_SUBMIT_FILTER_TO_DAY:
                submitFilter.setDateFirst( getFirstMinute( date ) );
                submitFilter.setDateLast( getLastMinute( date ) );

                break;

            case CONSTANT_SUBMIT_FILTER_WEEK:
                submitFilter.setDateFirst( getFirstDayOfWeek( date ) );
                submitFilter.setDateLast( getLastDayOfWeek( date ) );

                break;

            case CONSTANT_SUBMIT_FILTER_MONTH:
                submitFilter.setDateFirst( getFirstDayOfMonth( date ) );
                submitFilter.setDateLast( getLastDayOfMonth( date ) );

                break;

            default:
                break;
        }
    }

    /**
     * initialized the submit filter object with the sort specified in parameter
     * 
     * @param submitFilter
     *            the filter to initialized
     * @param nIdSort
     *            the id of the sort(date response, score)
     */
    public static void initSubmitFilterBySort( SubmitFilter submitFilter, int nIdSort )
    {
        switch( nIdSort )
        {
            case SubmitFilter.SORT_BY_DATE_RESPONSE_ASC:
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_DATE_RESPONSE_ASC );

                break;

            case SubmitFilter.SORT_BY_DATE_RESPONSE_DESC:
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_DATE_RESPONSE_DESC );

                break;

            case SubmitFilter.SORT_BY_SCORE_ASC:
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_SCORE_ASC );
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_DATE_RESPONSE_DESC );

                break;

            case SubmitFilter.SORT_BY_SCORE_DESC:
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_SCORE_DESC );
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_DATE_RESPONSE_DESC );

                break;

            case SubmitFilter.SORT_BY_NUMBER_COMMENT_ASC:
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_NUMBER_COMMENT_ASC );
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_DATE_RESPONSE_DESC );

                break;

            case SubmitFilter.SORT_BY_NUMBER_COMMENT_DESC:
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_NUMBER_COMMENT_DESC );
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_DATE_RESPONSE_DESC );

                break;

            case SubmitFilter.SORT_MANUALLY:
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_MANUALLY );

                break;

            case SubmitFilter.SORT_BY_NUMBER_VIEW_ASC:
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_NUMBER_VIEW_ASC );
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_DATE_RESPONSE_DESC );

                break;

            case SubmitFilter.SORT_BY_NUMBER_VIEW_DESC:
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_NUMBER_VIEW_DESC );
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_DATE_RESPONSE_DESC );

                break;

            case SubmitFilter.SORT_BY_PINNED_FIRST:
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_PINNED_FIRST );

                break;

            default:
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_SCORE_DESC );
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_DATE_RESPONSE_DESC );

                break;
        }
    }

    /**
     * initialized the submit filter object with the sort specified in parameter
     * 
     * @param submitFilter
     *            the filter to initialized
     * @param nIdSort
     *            the id of the sort(date response, score)
     */
    public static void initCommentFilterBySort( SubmitFilter submitFilter, int nIdSort )
    {
        switch( nIdSort )
        {
            case SubmitFilter.SORT_BY_DATE_RESPONSE_ASC:
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_DATE_RESPONSE_ASC );

                break;

            case SubmitFilter.SORT_BY_DATE_RESPONSE_DESC:
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_DATE_RESPONSE_DESC );

                break;

            case SubmitFilter.SORT_MANUALLY:
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_MANUALLY );

                break;

            case SubmitFilter.SORT_BY_DATE_MODIFY_ASC:
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_DATE_MODIFY_ASC );

                break;

            case SubmitFilter.SORT_BY_DATE_MODIFY_DESC:
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_DATE_MODIFY_DESC );

                break;

            default:
                submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_DATE_MODIFY_DESC );

                break;
        }
    }

    /**
     * Perform the vote on a suggest submit
     * 
     * @param nIdSuggestSubmit
     *            the id of the suggest submit
     * @param strLuteceUserKey
     *            the key of the lutece user who have vote
     * @param nScore
     *            the score of the vote
     * @param plugin
     *            the plugin
     */
    public static void doVoteSuggestSubmit( int nIdSuggestSubmit, int nScore, String strLuteceUserKey, Plugin plugin )
    {
        SuggestSubmit suggestSubmit = SuggestSubmitService.getService( ).findByPrimaryKey( nIdSuggestSubmit, false, plugin );

        if ( suggestSubmit != null )
        {
            suggestSubmit.setNumberVote( suggestSubmit.getNumberVote( ) + 1 );
            suggestSubmit.setNumberScore( suggestSubmit.getNumberScore( ) + nScore );
            SuggestSubmitService.getService( ).update( suggestSubmit, plugin );

            if ( strLuteceUserKey != null )
            {
                Vote vote = new Vote( );
                vote.setLuteceUserKey( strLuteceUserKey );
                vote.setIdSuggestSubmit( nIdSuggestSubmit );
                VoteHome.create( vote, plugin );
            }
        }
    }

    /**
     * Perform the report on a suggest submit
     * 
     * @param suggestSubmit
     *            the suggest submit
     * @param plugin
     *            the plugin
     */
    public static void doReportSuggestSubmit( SuggestSubmit suggestSubmit, Plugin plugin )
    {
        if ( suggestSubmit != null )
        {
            suggestSubmit.setReported( true );
            SuggestSubmitService.getService( ).update( suggestSubmit, plugin );
        }
    }

    /**
     * Init reference list with the different categories
     *
     *
     * @param listCategories
     *            the list of categories
     * @return reference list of category
     */
    public static ReferenceList getRefListCategory( List<Category> listCategories )
    {
        ReferenceList refListCategories = new ReferenceList( );

        for ( Category category : listCategories )
        {
            refListCategories.addItem( category.getIdCategory( ), category.getTitle( ) );
        }

        return refListCategories;
    }

    /**
     * Init reference list with the different types
     *
     *
     * @param listTypes
     *            the list of types
     * @return reference list of type
     */
    public static ReferenceList getRefListType( List<SuggestSubmitType> listTypes )
    {
        ReferenceList refListTypes = new ReferenceList( );

        for ( SuggestSubmitType type : listTypes )
        {
            refListTypes.addItem( type.getIdType( ), type.getName( ) );
        }

        return refListTypes;
    }

    /**
     * Init reference list with the different categories
     *
     *
     * @param listSuggests
     *            the list of categories
     * @param bIncludeDefault
     *            true if a default item must be insert
     *
     * @return reference list of category
     */
    public static ReferenceList getRefListSuggest( List<Suggest> listSuggests, boolean bIncludeDefault )
    {
        ReferenceList refListSuggests = new ReferenceList( );

        if ( bIncludeDefault )
        {
            refListSuggests.addItem( CONSTANT_ID_NULL, EMPTY_STRING );
        }

        for ( Suggest suggest : listSuggests )
        {
            refListSuggests.addItem( suggest.getIdSuggest( ), suggest.getTitle( ) );
        }

        return refListSuggests;
    }

    /**
     * Init reference list width the different sort
     *
     * @param locale
     *            the locale
     * @return reference list of sort
     */
    public static ReferenceList getRefListSuggestSort( Locale locale )
    {
        return getRefListSuggestSort( locale, false );
    }

    /**
     * Init reference list width the different sort
     *
     * @param locale
     *            the locale
     * @param bFront
     *            yes if the refList is display in front office
     * @return reference list of sort
     */
    public static ReferenceList getRefListSuggestSort( Locale locale, boolean bFront )
    {
        ReferenceList refListSorter = new ReferenceList( );

        refListSorter.addItem( CONSTANT_ID_NULL, EMPTY_STRING );
        addEmptyItem( refListSorter );
        refListSorter.addItem( SubmitFilter.SORT_BY_DATE_RESPONSE_ASC, I18nService.getLocalizedString( PROPERTY_SORTER_LIST_ITEM_DATE_RESPONSE_ASC, locale ) );
        refListSorter.addItem( SubmitFilter.SORT_BY_DATE_RESPONSE_DESC, I18nService.getLocalizedString( PROPERTY_SORTER_LIST_ITEM_DATE_RESPONSE_DESC, locale ) );
        refListSorter.addItem( SubmitFilter.SORT_BY_SCORE_ASC, I18nService.getLocalizedString( PROPERTY_SORTER_LIST_ITEM_SCORE_ASC, locale ) );
        refListSorter.addItem( SubmitFilter.SORT_BY_SCORE_DESC, I18nService.getLocalizedString( PROPERTY_SORTER_LIST_ITEM_SCORE_DESC, locale ) );
        refListSorter.addItem( SubmitFilter.SORT_BY_NUMBER_COMMENT_ASC, I18nService.getLocalizedString( PROPERTY_SORTER_LIST_ITEM_AMOUNT_COMMENT_ASC, locale ) );
        refListSorter
                .addItem( SubmitFilter.SORT_BY_NUMBER_COMMENT_DESC, I18nService.getLocalizedString( PROPERTY_SORTER_LIST_ITEM_AMOUNT_COMMENT_DESC, locale ) );

        if ( !bFront )
        {
            refListSorter.addItem( SubmitFilter.SORT_MANUALLY, I18nService.getLocalizedString( PROPERTY_SORTER_LIST_ITEM_MANUAL, locale ) );
        }

        refListSorter.addItem( SubmitFilter.SORT_BY_NUMBER_VIEW_ASC, I18nService.getLocalizedString( PROPERTY_SORTER_LIST_ITEM_VIEW_ASC, locale ) );
        refListSorter.addItem( SubmitFilter.SORT_BY_NUMBER_VIEW_DESC, I18nService.getLocalizedString( PROPERTY_SORTER_LIST_ITEM_VIEW_DESC, locale ) );

        return refListSorter;
    }

    /**
     * Init reference list width the different sort
     *
     * @param locale
     *            the locale
     * @return reference list of sort
     */
    public static ReferenceList getRefListFilterByPeriod( Locale locale )
    {
        ReferenceList refListFilterByPeriod = new ReferenceList( );

        refListFilterByPeriod.addItem( CONSTANT_ID_NULL, I18nService.getLocalizedString( PROPERTY_FILTER_ALL, locale ) );
        refListFilterByPeriod.addItem( CONSTANT_SUBMIT_FILTER_TO_DAY, I18nService.getLocalizedString( PROPERTY_FILTER_TO_DAY, locale ) );
        refListFilterByPeriod.addItem( CONSTANT_SUBMIT_FILTER_YESTERDAY, I18nService.getLocalizedString( PROPERTY_FILTER_YESTERDAY, locale ) );
        refListFilterByPeriod.addItem( CONSTANT_SUBMIT_FILTER_WEEK, I18nService.getLocalizedString( PROPERTY_FILTER_WEEK, locale ) );
        refListFilterByPeriod.addItem( CONSTANT_SUBMIT_FILTER_MONTH, I18nService.getLocalizedString( PROPERTY_FILTER_MONTH, locale ) );

        return refListFilterByPeriod;
    }

    /**
     * Init reference list width the different sort
     *
     * @param locale
     *            the locale
     * @return reference list of sort
     */
    public static ReferenceList getRefListCommentSort( Locale locale )
    {
        ReferenceList refListSorter = new ReferenceList( );

        refListSorter.addItem( CONSTANT_ID_NULL, EMPTY_STRING );
        refListSorter.addItem( SubmitFilter.SORT_BY_DATE_MODIFY_ASC, I18nService.getLocalizedString( PROPERTY_SORTER_LIST_ITEM_DATE_RESPONSE_ASC, locale ) );
        refListSorter.addItem( SubmitFilter.SORT_BY_DATE_MODIFY_DESC, I18nService.getLocalizedString( PROPERTY_SORTER_LIST_ITEM_DATE_RESPONSE_DESC, locale ) );

        return refListSorter;
    }

    /**
     * Init reference list width the different sort
     *
     * @param locale
     *            the locale
     * @return reference list of sort
     */
    public static ReferenceList getRefListCommentState( Locale locale )
    {
        ReferenceList refListSorter = new ReferenceList( );

        refListSorter.addItem( CONSTANT_ID_NULL, EMPTY_STRING );
        refListSorter.addItem( CommentSubmit.STATE_ENABLE, I18nService.getLocalizedString( PROPERTY_COMMENT_STATE_ENABLE, locale ) );
        refListSorter.addItem( CommentSubmit.STATE_DISABLE, I18nService.getLocalizedString( PROPERTY_COMMENT_STATE_DISABLE, locale ) );

        return refListSorter;
    }

    /**
     * write the http header in the response
     * 
     * @param request
     *            the httpServletRequest
     * @param response
     *            the http response
     * @param strFileName
     *            the name of the file who must insert in the response
     */
    public static void addHeaderResponse( HttpServletRequest request, HttpServletResponse response, String strFileName )
    {
        response.setHeader( "Content-Disposition", "attachment ;filename=\"" + strFileName + "\";" );
        response.setHeader( "Pragma", "public" );
        response.setHeader( "Expires", "0" );
        response.setHeader( "Cache-Control", "must-revalidate,post-check=0,pre-check=0" );
    }

    /**
     * Builds a query with filters placed in parameters
     * 
     * @param strSelect
     *            the select of the query
     * @param listStrFilter
     *            the list of filter to add in the query
     * @param strOrder
     *            the order by of the query
     * @return a query
     */
    public static String buildRequestWithFilter( String strSelect, List<String> listStrFilter, String strOrder )
    {
        StringBuffer strBuffer = new StringBuffer( );
        strBuffer.append( strSelect );

        int nCount = 0;

        for ( String strFilter : listStrFilter )
        {
            if ( ++nCount == 1 )
            {
                strBuffer.append( CONSTANT_WHERE );
            }

            strBuffer.append( strFilter );

            if ( nCount != listStrFilter.size( ) )
            {
                strBuffer.append( CONSTANT_AND );
            }
        }

        if ( strOrder != null )
        {
            strBuffer.append( strOrder );
        }

        return strBuffer.toString( );
    }

    /**
     * Like {@link List#retainAll(java.util.Collection)}, keeping first list order. This method is based on the fact that list1 and list2 have unique elements.
     *
     * @param list1
     *            the first list
     * @param list2
     *            the other list
     * @return first list
     */
    public static List<Integer> retainAllIdsKeepingFirstOrder( List<Integer> list1, List<Integer> list2 )
    {
        Iterator<Integer> it = list1.iterator( );

        // makes contains quicker
        TreeSet<Integer> ts = new TreeSet<Integer>( list2 );

        while ( it.hasNext( ) )
        {
            if ( !ts.contains( it.next( ) ) )
            {
                it.remove( );
            }
        }

        return list1;
    }

    /**
     * move a element in the list
     * 
     * @param nOldPosistion
     *            the old position
     * @param nNewPosition
     *            the new position
     * @param list
     *            The list
     */
    public static void moveElement( int nOldPosistion, int nNewPosition, ArrayList<Integer> list )
    {
        Integer element = list.get( nOldPosistion - 1 );
        list.remove( nOldPosistion - 1 );
        list.add( nNewPosition - 1, element );
    }

    /**
     * replace special characters in the string passed as a parameter
     *
     * @param strSource
     *            the string
     * @return substitute special in the string passed as a parameter
     */
    public static String substituteSpecialCaractersForExport( String strSource )
    {
        String strResult = EMPTY_STRING;

        if ( strSource != null )
        {
            strResult = strSource;
        }

        strResult = StringUtil.substitute( strResult, CONSTANT_CHARACTER_SIMPLE_QUOTE, CONSTANT_CHARACTER_DOUBLE_QUOTE );
        strResult = StringUtil.substitute( strResult, EMPTY_STRING, CONSTANTE_CHARACTER_RETURN );
        strResult = StringUtil.substitute( strResult, EMPTY_STRING, CONSTANTE_CHARACTERNEW_LINE );
        strResult = StringUtil.substitute( strResult, EMPTY_STRING, "<div[^>]+>" );
        strResult = StringUtil.substitute( strResult, EMPTY_STRING, "</div>" );

        return strResult;
    }

    /**
     * Add an empty element to a reference list
     * 
     * @param refList
     *            The reference list to add the empty element to
     */
    public static void addEmptyItem( ReferenceList refList )
    {
        ReferenceItem refEmpty = new ReferenceItem( );
        refEmpty.setCode( EMPTY_STRING + CONSTANT_ID_NULL );
        refEmpty.setName( EMPTY_STRING );
        refList.add( 0, refEmpty );
    }

    /**
     * Depopulate the suggest into a map of key - value
     * 
     * @param suggest
     *            the suggest
     * @return a map of key - value
     */
    public static Map<String, Object> depopulate( Suggest suggest )
    {
        Map<String, Object> mapAttributes = new HashMap<>( );

        for ( java.lang.reflect.Field field : Suggest.class.getDeclaredFields( ) )
        {
            SuggestAttribute attribute = field.getAnnotation( SuggestAttribute.class );

            if ( attribute != null )
            {
                String strAttributeKey = attribute.value( );

                try
                {
                    field.setAccessible( true );

                    Object attributeValue = ReflectionUtils.getField( field, suggest );
                    mapAttributes.put( strAttributeKey, attributeValue );
                }
                catch( SecurityException e )
                {
                    AppLogService.error( e );
                }
            }
        }

        return mapAttributes;
    }

    /**
     * create a filter for getting the list of pinned suggest submit
     * 
     * @param filter
     *            the init filter
     * @return a filter for getting f the list of pinned suggest submit
     */
    public static SubmitFilter createPinnedFilter( SubmitFilter filter )
    {
        SubmitFilter pinnedFilter = new SubmitFilter( );
        pinnedFilter.setIdSuggest( filter.getIdSuggest( ) );
        pinnedFilter.setDateFirst( filter.getDateFirst( ) );
        pinnedFilter.setDateLast( filter.getDateLast( ) );
        pinnedFilter.setIdCategory( filter.getIdCategory( ) );
        pinnedFilter.setIdType( filter.getIdType( ) );
        pinnedFilter.setIdReported( filter.getIdReported( ) );
        pinnedFilter.setIdSuggestSubmitState( filter.getIdSuggestSubmitState( ) );
        pinnedFilter.setIdPinned( SubmitFilter.ID_TRUE );
        pinnedFilter.setIdContainsCommentDisable( filter.getIdContainsCommentDisable( ) );
        initSubmitFilterBySort( pinnedFilter, SubmitFilter.SORT_MANUALLY );

        return pinnedFilter;
    }

    /**
     *
     * @param searchFields
     *            the search fields
     * @return a submit Filter
     */
    public static SubmitFilter getSuggestSubmitFilter( SuggestAdminSearchFields searchFields )
    {
        return getSuggestSubmitFilter( searchFields, null );
    }

    /**
     *
     * @param searchFields
     *            the search fields
     * @param nDefaultIdSort
     *            the defautlt sort
     * @return SubmitFilter
     */
    public static SubmitFilter getSuggestSubmitFilter( SuggestAdminSearchFields searchFields, Integer nDefaultIdSort )
    {
        SubmitFilter filter = new SubmitFilter( );
        filter.setIdSuggest( searchFields.getIdSuggest( ) );
        filter.setIdSuggestSubmitState( searchFields.getIdSuggestSumitState( ) );
        filter.setIdReported( searchFields.getIdSuggestSubmitReport( ) );
        filter.setIdCategory( searchFields.getIdCategory( ) );
        filter.setIdType( searchFields.getIdType( ) );
        filter.setIdContainsCommentDisable( searchFields.getIdSuggestSubmitContainsCommentDisable( ) );
        SuggestUtils.initSubmitFilterBySort(
                filter,
                ( ( searchFields.getIdSuggestSubmitSort( ) == SuggestUtils.CONSTANT_ID_NULL ) && ( nDefaultIdSort != null ) ) ? nDefaultIdSort : searchFields
                        .getIdSuggestSubmitSort( ) );
        // add sort by pinned first
        SuggestUtils.initSubmitFilterBySort( filter, SubmitFilter.SORT_BY_PINNED_FIRST );

        return filter;
    }

    /**
     * Add Avatar to model
     * 
     * @param model
     * @param luteceUserInfo
     */
    public static void addAvatarToModel( Map<String, Object> model, SuggestUserInfo luteceUserInfo )
    {

        if ( luteceUserInfo != null )
        {

            if ( !StringUtils.isEmpty( luteceUserInfo.getHomeMail( ) ) )
            {
                model.put( MARK_AVATAR, AvatarService.getAvatarUrl( luteceUserInfo.getHomeMail( ) ) );
            }
            else
                if ( !StringUtils.isEmpty( luteceUserInfo.getBusinessMail( ) ) )
                {
                    model.put( MARK_AVATAR, AvatarService.getAvatarUrl( luteceUserInfo.getBusinessMail( ) ) );
                }

        }

    }

}
