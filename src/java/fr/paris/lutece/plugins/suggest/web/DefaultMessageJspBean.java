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
package fr.paris.lutece.plugins.suggest.web;

import java.util.HashMap;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.suggest.business.DefaultMessage;
import fr.paris.lutece.plugins.suggest.business.DefaultMessageHome;
import fr.paris.lutece.plugins.suggest.service.DefaultMessageResourceIdService;
import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 *
 * class DefaultMessageJspBean
 *
 */
public class DefaultMessageJspBean extends PluginAdminPageJspBean
{

    /**
	 * 
	 */
    private static final long serialVersionUID = -8248033294625285115L;

    // templates
    private static final String TEMPLATE_MANAGE_DEFAULT_MESSAGE = "admin/plugins/suggest/manage_default_message.html";

    // Markers
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_DEFAULT_MESSAGE = "default_message";

    // parameters form
    private static final String PARAMETER_UNAVAILABILITY_MESSAGE = "unavailability_message";
    private static final String PARAMETER_LIBELLE_VALIDATE_BUTTON = "libelle_validate_button";
    private static final String PARAMETER_LIBELLE_CONTRIBUTION = "libelle_contribution";
    private static final String PARAMETER_NUMBER_SUGGEST_SUBMIT_IN_TOP_SCORE = "number_suggest_submit_in_top_score";
    private static final String PARAMETER_NUMBER_SUGGEST_SUBMIT_IN_TOP_COMMENT = "number_suggest_submit_in_top_comment";
    private static final String PARAMETER_NUMBER_SUGGEST_SUBMIT_CARACTERS_SHOWN = "number_suggest_submit_caracters_shown";
    private static final String PARAMETER_NOTIFICATION_NEW_COMMENT_TITLE = "notification_new_comment_title";
    private static final String PARAMETER_NOTIFICATION_NEW_COMMENT_BODY = "notification_new_comment_body";
    private static final String PARAMETER_NOTIFICATION_NEW_SUGGEST_SUBMIT_TITLE = "notification_new_suggest_submit_title";
    private static final String PARAMETER_NOTIFICATION_NEW_SUGGEST_SUBMIT_BODY = "notification_new_suggest_submit_body";

    // other constants
    private static final String EMPTY_STRING = "";

    // message
    private static final String MESSAGE_MANDATORY_FIELD = "suggest.message.mandatory.field";
    private static final String FIELD_UNAVAILABILITY_MESSAGE = "suggest.manageDefaultMessage.labelUnavailabilityMessage";
    private static final String FIELD_LIBELLE_VALIDATE_BUTTON = "suggest.manageDefaultMessage.labelLibelleValidateButton";
    private static final String FIELD_LIBELLE_CONTRIBUTION = "suggest.manageDefaultMessage.labelLibelleContribution";
    private static final String FIELD_NUMBER_SUGGEST_SUBMIT_IN_TOP_SCORE = "suggest.createSuggest.labelNumberSuggestSumitInTopScore";
    private static final String FIELD_NUMBER_SUGGEST_SUBMIT_IN_TOP_COMMENT = "suggest.createSuggest.labelNumberSuggestSumitInTopComment";
    private static final String FIELD_NUMBER_SUGGEST_SUBMIT_CARACTERS_SHOWN = "suggest.createSuggest.labelNumberSuggestSubmitCaractersShown";

    private static final String FIELD_NOTIFICATION_NEW_COMMENT_TITLE = "suggest.manageDefaultMessage.labelNotificationNewCommentTitle";
    private static final String FIELD_NOTIFICATION_NEW_COMMENT_BODY = "suggest.manageDefaultMessage.labelNotificationNewCommentBody";
    private static final String FIELD_NOTIFICATION_NEW_SUGGEST_DUBMIT_TITLE = "suggest.manageDefaultMessage.labelNotificationNewSuggestSubmitTitle";
    private static final String FIELD_NOTIFICATION_NEW_SUGGEST_DUBMIT_BODY = "suggest.manageDefaultMessage.labelNotificationNewSuggestSubmitBody";

    private static final String MESSAGE_ILLOGICAL_NUMBER_SUGGEST_SUBMIT_IN_TOP_SCORE = "suggest.message.illogicalNumberSuggestSumitInTopScore";
    private static final String MESSAGE_ILLOGICAL_NUMBER_SUGGEST_SUBMIT_IN_TOP_COMMENT = "suggest.message.illogicalNumberSuggestSumitInTopComment";
    private static final String MESSAGE_ILLOGICAL_NUMBER_SUGGEST_SUBMIT_CARACTERS_SHOWN = "suggest.message.illogicalNumberSuggestSubmitCaractersShown";

    // properties
    private static final String PROPERTY_MANAGE_DEFAULT_MESSAGE_TITLE = "form.manageDefaultMessage.title";

    /**
     * gets the manage default message page
     * 
     * @param request
     *            the Http request
     * @return the manage default message page
     */
    public String getManageDefaultMessage( HttpServletRequest request )
    {
        Locale locale = getLocale( );
        HashMap<String, Object> model = new HashMap<>( );
        DefaultMessage defaultMessage = DefaultMessageHome.find( getPlugin( ) );
        model.put( MARK_DEFAULT_MESSAGE, defaultMessage );
        model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
        model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage( ) );
        setPageTitleProperty( PROPERTY_MANAGE_DEFAULT_MESSAGE_TITLE );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_DEFAULT_MESSAGE, locale, model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * perform the default message modification
     * 
     * @param request
     *            the Http request
     * @return The URL to go after performing the action
     */
    public String doModifyDefaultMessage( HttpServletRequest request )
    {
        if ( RBACService.isAuthorized( DefaultMessage.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, DefaultMessageResourceIdService.PERMISSION_MANAGE, getUser( ) ) )
        {
            DefaultMessage defaultMessage = new DefaultMessage( );
            String strError = getDefaultMessageData( request, defaultMessage );

            if ( strError != null )
            {
                return strError;
            }

            DefaultMessageHome.update( defaultMessage, getPlugin( ) );
        }

        return getHomeUrl( request );
    }

    /**
     * Get the request data and if there is no error insert the data in the default Message object specified in parameter. return null if there is no error or
     * else return the error page url
     * 
     * @param request
     *            the request
     * @param defaultMessage
     *            the default message
     * @return null if there is no error or else return the error page url
     */
    private String getDefaultMessageData( HttpServletRequest request, DefaultMessage defaultMessage )
    {
        String strLibelleContribution = request.getParameter( PARAMETER_LIBELLE_CONTRIBUTION );
        String strUnavailabilityMessage = request.getParameter( PARAMETER_UNAVAILABILITY_MESSAGE );
        String strLibelleValidateButton = request.getParameter( PARAMETER_LIBELLE_VALIDATE_BUTTON );
        String strNumberSuggestSubmitInTopScore = request.getParameter( PARAMETER_NUMBER_SUGGEST_SUBMIT_IN_TOP_SCORE );
        String strNumberSuggestSubmitInTopComment = request.getParameter( PARAMETER_NUMBER_SUGGEST_SUBMIT_IN_TOP_COMMENT );
        String strNumberSuggestSubmitCaractersShown = request.getParameter( PARAMETER_NUMBER_SUGGEST_SUBMIT_CARACTERS_SHOWN );
        String strNotificationNewCommentTitle = request.getParameter( PARAMETER_NOTIFICATION_NEW_COMMENT_TITLE );
        String strNotificationNewCommentBody = request.getParameter( PARAMETER_NOTIFICATION_NEW_COMMENT_BODY );
        String strNotificationNewSuggestSubmitTitle = request.getParameter( PARAMETER_NOTIFICATION_NEW_SUGGEST_SUBMIT_TITLE );
        String strNotificationNewSuggestSubmitBody = request.getParameter( PARAMETER_NOTIFICATION_NEW_SUGGEST_SUBMIT_BODY );

        int nNumberSuggestSubmitInTopScore = SuggestUtils.getIntegerParameter( strNumberSuggestSubmitInTopScore );
        int nNumberSuggestSubmitInTopComment = SuggestUtils.getIntegerParameter( strNumberSuggestSubmitInTopComment );
        int nNumberSuggestSubmitCaractersShown = SuggestUtils.getIntegerParameter( strNumberSuggestSubmitCaractersShown );

        String strFieldError = EMPTY_STRING;

        if ( StringUtils.isEmpty( strLibelleContribution ) )
        {
            strFieldError = FIELD_LIBELLE_CONTRIBUTION;
        }
        else
            if ( StringUtils.isEmpty( strUnavailabilityMessage ) )
            {
                strFieldError = FIELD_UNAVAILABILITY_MESSAGE;
            }

            else
                if ( StringUtils.isEmpty( strLibelleValidateButton ) )
                {
                    strFieldError = FIELD_LIBELLE_VALIDATE_BUTTON;
                }
                else
                    if ( StringUtils.isEmpty( strNumberSuggestSubmitInTopScore ) )
                    {
                        strFieldError = FIELD_NUMBER_SUGGEST_SUBMIT_IN_TOP_SCORE;
                    }

                    else
                        if ( StringUtils.isEmpty( strNumberSuggestSubmitInTopComment ) )
                        {
                            strFieldError = FIELD_NUMBER_SUGGEST_SUBMIT_IN_TOP_COMMENT;
                        }
                        else
                            if ( StringUtils.isEmpty( strNumberSuggestSubmitCaractersShown ) )
                            {
                                strFieldError = FIELD_NUMBER_SUGGEST_SUBMIT_CARACTERS_SHOWN;
                            }

                            else
                                if ( StringUtils.isEmpty( strNotificationNewCommentTitle ) )
                                {
                                    strFieldError = FIELD_NOTIFICATION_NEW_COMMENT_TITLE;

                                }
                                else
                                    if ( StringUtils.isEmpty( strNotificationNewCommentBody ) )
                                    {
                                        strFieldError = FIELD_NOTIFICATION_NEW_COMMENT_BODY;

                                    }

                                    else
                                        if ( StringUtils.isEmpty( strNotificationNewSuggestSubmitTitle ) )
                                        {

                                            strFieldError = FIELD_NOTIFICATION_NEW_SUGGEST_DUBMIT_TITLE;
                                        }
                                        else
                                            if ( StringUtils.isEmpty( strNotificationNewSuggestSubmitBody ) )
                                            {

                                                strFieldError = FIELD_NOTIFICATION_NEW_SUGGEST_DUBMIT_BODY;
                                            }

        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object [ ] tabRequiredFields = {
                I18nService.getLocalizedString( strFieldError, getLocale( ) )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        if ( nNumberSuggestSubmitInTopScore < 0 )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_NUMBER_SUGGEST_SUBMIT_IN_TOP_SCORE, AdminMessage.TYPE_STOP );
        }

        if ( nNumberSuggestSubmitInTopComment < 0 )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_NUMBER_SUGGEST_SUBMIT_IN_TOP_COMMENT, AdminMessage.TYPE_STOP );
        }

        if ( nNumberSuggestSubmitCaractersShown < 0 )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ILLOGICAL_NUMBER_SUGGEST_SUBMIT_CARACTERS_SHOWN, AdminMessage.TYPE_STOP );
        }

        defaultMessage.setLibelleContribution( strLibelleContribution );
        defaultMessage.setUnavailabilityMessage( strUnavailabilityMessage );
        defaultMessage.setLibelleValidateButton( strLibelleValidateButton );
        defaultMessage.setNumberSuggestSubmitInTopScore( nNumberSuggestSubmitInTopScore );
        defaultMessage.setNumberSuggestSubmitInTopComment( nNumberSuggestSubmitInTopComment );
        defaultMessage.setNumberSuggestSubmitCaractersShown( nNumberSuggestSubmitCaractersShown );

        defaultMessage.setNotificationNewCommentTitle( strNotificationNewCommentTitle );
        defaultMessage.setNotificationNewCommentBody( strNotificationNewCommentBody );
        defaultMessage.setNotificationNewSuggestSubmitTitle( strNotificationNewSuggestSubmitTitle );
        defaultMessage.setNotificationNewSuggestSubmitBody( strNotificationNewSuggestSubmitBody );

        return null;
    }
}
