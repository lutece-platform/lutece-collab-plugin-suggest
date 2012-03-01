/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.digglike.web;

import fr.paris.lutece.plugins.digglike.business.Category;
import fr.paris.lutece.plugins.digglike.business.CategoryHome;
import fr.paris.lutece.plugins.digglike.business.CommentSubmit;
import fr.paris.lutece.plugins.digglike.business.CommentSubmitHome;
import fr.paris.lutece.plugins.digglike.business.Digg;
import fr.paris.lutece.plugins.digglike.business.DiggFilter;
import fr.paris.lutece.plugins.digglike.business.DiggHome;
import fr.paris.lutece.plugins.digglike.business.DiggSubmit;
import fr.paris.lutece.plugins.digglike.business.DiggSubmitHome;
import fr.paris.lutece.plugins.digglike.business.DiggSubmitState;
import fr.paris.lutece.plugins.digglike.business.DiggSubmitStateHome;
import fr.paris.lutece.plugins.digglike.business.DiggSubmitType;
import fr.paris.lutece.plugins.digglike.business.DiggSubmitTypeHome;
import fr.paris.lutece.plugins.digglike.business.EntryFilter;
import fr.paris.lutece.plugins.digglike.business.EntryHome;
import fr.paris.lutece.plugins.digglike.business.FormError;
import fr.paris.lutece.plugins.digglike.business.IEntry;
import fr.paris.lutece.plugins.digglike.business.ReportedMessage;
import fr.paris.lutece.plugins.digglike.business.Response;
import fr.paris.lutece.plugins.digglike.business.ResponseHome;
import fr.paris.lutece.plugins.digglike.business.SubmitFilter;
import fr.paris.lutece.plugins.digglike.business.VoteHome;
import fr.paris.lutece.plugins.digglike.business.VoteType;
import fr.paris.lutece.plugins.digglike.business.VoteTypeHome;
import fr.paris.lutece.plugins.digglike.service.digglikesearch.DigglikeSearchService;
import fr.paris.lutece.plugins.digglike.utils.DiggUtils;
import fr.paris.lutece.portal.service.captcha.CaptchaSecurityService;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;


/**
 * This class manages Form page.
 */
public class DiggApp implements XPageApplication
{
    public static final String FULL_URL = "fullUrl";
    public static final String ANCHOR_DIGG_SUBMIT = "digg";
    public static final String PARAMETER_CLEAR_FILTER = "clear_filter";
    public static final String PARAMETER_DIGG_DETAIL = "digg_detail";

    // markers
    private static final String MARK_FORM_DIGG = "form_digg";
    private static final String MARK_CONTENT_DIGG = "content_digg";
    private static final String MARK_LIST_DIGG_SUBMIT = "list_digg_submit";
    private static final String MARK_LABEL_DIGG = "label_digg";
    private static final String MARK_HEADER_DIGG = "header_digg";
    private static final String MARK_LIST_CATEGORIES_DIGG = "list_categories_digg";
    private static final String MARK_LIST_TYPES = "list_types";
    private static final String MARK_ID_DIGG = "id_digg";
    private static final String MARK_MODE_DIGG = "mode_digg";
    private static final Object MARK_DIGG_LIST = "list_digg";
    private static final String MARK_LIST_SUBMIT_TOP_COMMENT = "list_top_comment_digg";
    private static final String MARK_LIST_SUBMIT_TOP_POPULARITY_DIGG = "list_top_popularity_digg";
    private static final String MARK_DIGG_DETAIL = "digg_detail";
    private static final String MARK_ID_DIGG_SUBMIT = "id_digg_submit";
    private static final String MARK_LIST_COMMENT_SUBMIT_DIGG = "list_comment";
    private static final String MARK_DIGG_COMMENT = "digg_comment";
    private static final String MARK_AUTHORIZED_COMMENT = "authorized_comment";
    private static final String MARK_DIGG_SUBMIT_MODERATE = "digg_submit_moderate";
    private static final String MARK_DIGG_SUBMIT = "digg_submit";
    private static final String MARK_COMMENT_SUBMIT = "comment_submit";
    private static final String MARK_LUTECE_USER = "lutece_user";
    private static final String MARK_UNAVAILABILITY_MESSAGE = "unavailability_message";
    private static final String MARK_NUMBER_SHOWN_CHARACTERS = "number_shown_characters";
    private static final String MARK_DISABLE_NEW_DIGG_SUBMIT = "disable_new_digg_submit";
    private static final String MARK_DISABLE_NEW_COMMENT_SUBMIT = "disable_new_comment_submit";
    private static final String MARK_QUERY = "query";
    private static final String MARK_ID_FILTER_CATEGORY_DIGG = "id_filter_category";
    private static final String MARK_ID_FILTER_PERIOD = "id_filter_period";
    private static final String MARK_LIST_DIGG_SUBMIT_SORT = "list_digg_submit_sort";
    private static final String MARK_ID_DIGG_SUBMIT_SORT = "id_digg_submit_sort";
    private static final String MARK_FILTER_DAY_VALUE = "filter_day_value";
    private static final String MARK_FILTER_YESTERDDAY_VALUE = "filter_yesterday_value";
    private static final String MARK_FILTER_WEEK_VALUE = "filter_week_value";
    private static final String MARK_FILTER_MONTH_VALUE = "filter_month_value";
    private static final String MARK_SHOW_CATEGORY_BLOCK = "show_category_block";
    private static final String MARK_SHOW_TOP_SCORE_BLOCK = "show_top_score_block";
    private static final String MARK_SHOW_TOP_COMMENT_BLOCK = "show_top_comment_block";
    private static final String MARK_DIGG_SUBMIT_VOTE_TYPE = "digg_submit_vote_type";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_JCAPTCHA = "jcaptcha";
    private static final String MARK_MAX_AMOUNT_COMMENTS = "number_comments";
    private static final String MARK_MAX_AMOUNT_COMMENTS_CHAR = "cumber_char_comments";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";

    // templates
    private static final String TEMPLATE_XPAGE_FRAME_DIGG = "skin/plugins/digglike/digg_frame.html";
    private static final String TEMPLATE_XPAGE_LIST_SUBMIT_DIGG = "skin/plugins/digglike/digg_list_submit.html";
    private static final String TEMPLATE_XPAGE_FORM_DIGG = "skin/plugins/digglike/digg_form.html";
    private static final String TEMPLATE_XPAGE_LIST_DIGG = "skin/plugins/digglike/digg_list.html";
    private static final String TEMPLATE_XPAGE_DETAIL_SUBMIT_DIGG = "skin/plugins/digglike/digg_detail.html";
    private static final String TEMPLATE_XPAGE_DIGG_REPORTED = "skin/plugins/digglike/digg_reported.html";
    private static final String TEMPLATE_XPAGE_COMMENT_SUBMIT_DIGG = "skin/plugins/digglike/digg_comment.html";
    private static final String JCAPTCHA_PLUGIN = "jcaptcha";

    // properties for page titles and path label
    private static final String PROPERTY_XPAGE_PAGETITLE = "digglike.xpage.pagetitle";
    private static final String PROPERTY_XPAGE_PATHLABEL = "digglike.xpage.pathlabel";
    private static final String PROPERTY_NUMBER_DIGG_SUBMIT_VALUE_SHOWN_CHARACTERS = "digglike.diggSubmitValue.NumberShownCharacters";
    private static final String PROPERTY_PAGE_APPLICATION_ID = "digglike.xpage.applicationId";
    private static final String PROPERTY_ITEM_PER_PAGE = "digglike.itemsPerPage.front";

    // request parameters
    private static final String PARAMETER_ID_DIGG = "id_digg";
    private static final String PARAMETER_MODE_DIGG = "mode_digg";
    private static final String PARAMETER_SUBMIT_DIGG = "submit";
    private static final String PARAMETER_ID_SUBMIT_DIGG = "id_digg_submit";
    private static final String PARAMETER_COMMENT_DIGG = "digg_comment";
    private static final String PARAMETER_SUBMIT_COMMENT_DIGG = "submit_comment";
    private static final String PARAMETER_SUBMIT_REPORTED_DIGG = "submit_reported";
    private static final String PARAMETER_COMMENT_VALUE_DIGG = "comment_value";
    private static final String PARAMETER_REPORTED_VALUE = "reported_value";
    private static final String PARAMETER_ID_FILTER_PERIOD = "id_filter_period";
    private static final String PARAMETER_ID_DIGG_SUBMIT_SORT = "id_digg_submit_sort";
    private static final String PARAMETER_VOTE_DIGG = "vote";
    private static final String PARAMETER_ID_FILTER_CATEGORY_DIGG = "id_filter_category";
    private static final String PARAMETER_ID_CATEGORY_DIGG = "id_category";
    private static final String PARAMETER_ID_TYPE_DIGG = "id_type";
    private static final String PARAMETER_VOTED = "voted";
    private static final String PARAMETER_REPORT = "report";
    private static final String PARAMETER_QUERY = "query";
    private static final String PARAMETER_FILTER_PAGE_INDEX = "filter_page_index";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_COMMENT_ID_PARENT = "id_digg_comment";

    //message
    private static final String MESSAGE_FORM_ERROR = "digglike.message.formError";
    private static final String MESSAGE_MANDATORY_QUESTION = "digglike.message.mandatory.question";
    private static final String MESSAGE_ERROR = "digglike.message.Error";
    private static final String MESSAGE_ACCESS_DENIED = "digglike.message.accessDenied";
    private static final String MESSAGE_MANDATORY_COMMENT = "digglike.message.mandatoryComment";
    private static final String MESSAGE_MANDATORY_REPORTED = "digglike.message.mandatoryReported";
    private static final String MESSAGE_CAPTCHA_ERROR = "digglike.message.captchaError";
    private static final String MESSAGE_NEW_DIGG_SUBMIT = "digglike.message.newDiggSubmit";
    private static final String MESSAGE_NEW_DIGG_SUBMIT_DISABLE = "digglike.message.newDiggSubmitDisable";
    private static final String MESSAGE_NEW_DIGG_SUBMIT_INVALID = "digglike.message.newDiggSubmitInvalid";
    private static final String MESSAGE_NEW_COMMENT_SUBMIT = "digglike.message.newCommentSubmit";
    private static final String MESSAGE_NEW_COMMENT_SUBMIT_DISABLE = "digglike.message.newCommentSubmitDisable";
    private static final String MESSAGE_NEW_REPORTED_SUBMIT = "digglike.message.newReportedSubmit";
    private static final String MESSAGE_NEW_COMMENT_SUBMIT_INVALID = "digglike.message.newCommentSubmitInvalid";
    private static final String MESSAGE_ERROR_NO_CATEGORY = "digglike.message.errorNoCategorySelected";

    //XPAGE URL

    //constant
    private static final String EMPTY_STRING = "";
    private static final String CONSTANTE_PARAMETER_TRUE_VALUE = "1";
    private static final String CONSTANTE_PARAMETER_FALSE_VALUE = "0";
    private static final String PATH_TYPE_VOTE_FOLDER = "skin/plugins/digglike/";
    private static final String DEFAULT_PAGE_INDEX = "1";

    //session filter
    private static final String SESSION_FILTER_ID_PERIOD = "digglike_filter_id__period";
    private static final String SESSION_FILTER_ID_SORT = "digglike_filter_id__sort";
    private static final String SESSION_FILTER_ID_CATEGORY = "digglike_filter_id_category";
    private static final String SESSION_FILTER_QUERY = "digglike_filter_query";
    private static final String SESSION_FILTER_PAGE_INDEX = "digglike_filter_page_index";

    //properties
    private static final String PROPERTY_MAX_AMOUNT_COMMENTS = "digglike.comments.max.qty";
    private static final String PROPERTY_MAX_AMOUNT_COMMENTS_CHAR = "digglike.comments.max.char.qty";
    private String _strFullUrl; //ex : http://toto:90/digg/jsp/site/Portal.jsp
    private int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 50 );

    /**
     * Returns the Form XPage result content depending on the request parameters and the current mode.
     *
     * @param request The HTTP request.
     * @param nMode The current mode.
     * @param plugin The Plugin.
     * @return The page content.
     * @throws SiteMessageException the SiteMessageException
     * @throws UserNotSignedException the UserNotSignedException
     */
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin )
        throws SiteMessageException, UserNotSignedException
    {
        _strFullUrl = request.getRequestURL(  ).toString(  );

        Digg digg;
        Locale locale = request.getLocale(  );
        LuteceUser luteceUser = null;
        XPage page = new XPage(  );

        page.setTitle( I18nService.getLocalizedString( PROPERTY_XPAGE_PAGETITLE, request.getLocale(  ) ) );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_XPAGE_PATHLABEL, request.getLocale(  ) ) );

        String strModeDigg = request.getParameter( PARAMETER_MODE_DIGG );
        String strSubmitDigg = request.getParameter( PARAMETER_SUBMIT_DIGG );

        String strContentDigg = EMPTY_STRING;
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( FULL_URL, _strFullUrl );

        int nNumberShownCharacters = AppPropertiesService.getPropertyInt( PROPERTY_NUMBER_DIGG_SUBMIT_VALUE_SHOWN_CHARACTERS,
                100 );
        String strIdDigg = request.getParameter( PARAMETER_ID_DIGG );
        int nIdDigg = DiggUtils.getIntegerParameter( strIdDigg );
        digg = DiggHome.findByPrimaryKey( nIdDigg, plugin );

        DiggSubmitState diggSubmitStatePublish = DiggSubmitStateHome.findByNumero( DiggSubmit.STATE_PUBLISH, plugin );

        String strPortalUrl = AppPathService.getPortalUrl(  );
        UrlItem urlDiggXpage = new UrlItem( strPortalUrl );
        urlDiggXpage.addParameter( XPageAppService.PARAM_XPAGE_APP,
            AppPropertiesService.getProperty( PROPERTY_PAGE_APPLICATION_ID ) );
        urlDiggXpage.addParameter( PARAMETER_ID_DIGG, strIdDigg );

        if ( ( digg == null ) || ( diggSubmitStatePublish == null ) )
        {
            //show the diggs list
            UrlItem urlDiggXpageHome = new UrlItem( strPortalUrl );
            urlDiggXpageHome.addParameter( XPageAppService.PARAM_XPAGE_APP,
                AppPropertiesService.getProperty( PROPERTY_PAGE_APPLICATION_ID ) );

            String strCurrentPageIndexDigg = "";
            strCurrentPageIndexDigg = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                    strCurrentPageIndexDigg );

            int nItemsPerPageDigg = _nDefaultItemsPerPage;
            nItemsPerPageDigg = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE,
                    nItemsPerPageDigg, _nDefaultItemsPerPage );
            
            String strQuery = request.getParameter( PARAMETER_QUERY );
            
            if( StringUtils.isNotBlank( strQuery ) )
            {
            	strContentDigg = getSearch( strQuery, locale, plugin, strCurrentPageIndexDigg, nItemsPerPageDigg,
	                    urlDiggXpageHome );
            }
            else
            {
	            strContentDigg = getHtmlListDigg( locale, plugin, strCurrentPageIndexDigg, nItemsPerPageDigg,
	                    urlDiggXpageHome );
            }
            
            model.put( MARK_CONTENT_DIGG, strContentDigg );

            page.setContent( strContentDigg );
        }
        else
        {
            if ( ( digg.getRole(  ) != null ) && !digg.getRole(  ).equals( Digg.ROLE_NONE ) &&
                    SecurityService.isAuthenticationEnable(  ) )
            {
                luteceUser = SecurityService.getInstance(  ).getRemoteUser( request );

                if ( !SecurityService.getInstance(  ).isUserInRole( request, digg.getRole(  ) ) )
                {
                    SiteMessageService.setMessage( request, MESSAGE_ACCESS_DENIED, SiteMessage.TYPE_STOP );
                }
            }

            if ( digg.isActive(  ) )
            {
                HttpSession session = request.getSession(  );
                String strIdSubmitDigg = request.getParameter( PARAMETER_ID_SUBMIT_DIGG );
                int nIdSubmitDigg = DiggUtils.getIntegerParameter( strIdSubmitDigg );
                String strSubmitCommentDigg = request.getParameter( PARAMETER_SUBMIT_COMMENT_DIGG );
                String strSubmitReportedDigg = request.getParameter( PARAMETER_SUBMIT_REPORTED_DIGG );

                String strVote = request.getParameter( PARAMETER_VOTE_DIGG );
                String strReport = request.getParameter( PARAMETER_REPORT );
                String strDetail = request.getParameter( PARAMETER_DIGG_DETAIL );

                String strQuery = request.getParameter( PARAMETER_QUERY );
                String strIdFilterPeriod = request.getParameter( PARAMETER_ID_FILTER_PERIOD );
                String strIdDiggSubmitSort = request.getParameter( PARAMETER_ID_DIGG_SUBMIT_SORT );
                String strIdFilterCategory = request.getParameter( PARAMETER_ID_FILTER_CATEGORY_DIGG );
                String strFilterPageIndex = request.getParameter( PARAMETER_FILTER_PAGE_INDEX );

                int nIdFilterPeriod = DiggUtils.getIntegerParameter( strIdFilterPeriod );
                int nIdDiggSubmitSort = DiggUtils.getIntegerParameter( strIdDiggSubmitSort );
                int nIdFilterCategory = DiggUtils.getIntegerParameter( strIdFilterCategory );

                VoteType voteType = VoteTypeHome.findByPrimaryKey( digg.getVoteType(  ).getIdVoteType(  ), plugin );
                digg.setVoteType( voteType );

                if ( request.getParameter( PARAMETER_CLEAR_FILTER ) != null )
                {
                    //clear all filter in session
                    clearSessionFilter( session );
                }
                else
                {
                    if ( strQuery == null )
                    {
                        if ( session.getAttribute( SESSION_FILTER_QUERY ) != null )
                        {
                            strQuery = (String) session.getAttribute( SESSION_FILTER_QUERY );
                        }
                        else
                        {
                            strQuery = EMPTY_STRING;
                        }
                    }
                    else
                    {
                        session.setAttribute( SESSION_FILTER_QUERY, strQuery );
                    }

                    if ( ( strIdFilterPeriod == null ) && ( session.getAttribute( SESSION_FILTER_ID_PERIOD ) != null ) )
                    {
                        nIdFilterPeriod = (Integer) session.getAttribute( SESSION_FILTER_ID_PERIOD );
                    }
                    else
                    {
                        session.setAttribute( SESSION_FILTER_ID_PERIOD, nIdFilterPeriod );
                    }

                    if ( ( strIdDiggSubmitSort == null ) && ( session.getAttribute( SESSION_FILTER_ID_SORT ) != null ) )
                    {
                        nIdDiggSubmitSort = (Integer) session.getAttribute( SESSION_FILTER_ID_SORT );
                    }
                    else
                    {
                        session.setAttribute( SESSION_FILTER_ID_SORT, nIdDiggSubmitSort );
                    }

                    if ( ( strIdFilterCategory == null ) &&
                            ( session.getAttribute( SESSION_FILTER_ID_CATEGORY ) != null ) )
                    {
                        nIdFilterCategory = (Integer) session.getAttribute( SESSION_FILTER_ID_CATEGORY );
                    }
                    else
                    {
                        session.setAttribute( SESSION_FILTER_ID_CATEGORY, nIdFilterCategory );
                    }

                    if ( ( strFilterPageIndex == null ) && ( session.getAttribute( SESSION_FILTER_PAGE_INDEX ) != null ) )
                    {
                        strFilterPageIndex = (String) session.getAttribute( SESSION_FILTER_PAGE_INDEX );
                    }
                    else
                    {
                        session.setAttribute( SESSION_FILTER_PAGE_INDEX, strFilterPageIndex );
                    }
                }

                if ( nIdSubmitDigg != -1 )
                {
                    //vote
                    if ( strVote != null )
                    {
                        if ( digg.isLimitNumberVote(  ) )
                        {
                            if ( digg.isActiveVoteAuthentification(  ) && SecurityService.isAuthenticationEnable(  ) )
                            {
                                luteceUser = ( luteceUser != null ) ? luteceUser
                                                                    : SecurityService.getInstance(  )
                                                                                     .getRemoteUser( request );

                                if ( VoteHome.getUserNumberVoteOnDiggSubmit( nIdSubmitDigg, luteceUser.getName(  ),
                                            plugin ) == 0 )
                                {
                                    doVote( strVote, nIdSubmitDigg, plugin, luteceUser.getName(  ) );
                                }
                            }
                            else if ( session.getAttribute( EMPTY_STRING + nIdSubmitDigg ) == null )
                            {
                                doVote( strVote, nIdSubmitDigg, plugin, null );
                                session.setAttribute( EMPTY_STRING + nIdSubmitDigg, PARAMETER_VOTED );
                            }
                        }
                        else
                        {
                            doVote( strVote, nIdSubmitDigg, plugin, null );
                        }

                        // Votes in list
                        if ( ( strDetail == null ) || strDetail.equals( CONSTANTE_PARAMETER_FALSE_VALUE ) )
                        {
                            strContentDigg = getHtmlListDiggSubmit( locale, plugin, digg, nIdFilterPeriod,
                                    nIdDiggSubmitSort, nIdFilterCategory,
                                    diggSubmitStatePublish.getIdDiggSubmitState(  ), strQuery, strFilterPageIndex,
                                    urlDiggXpage );
                        }
                        else
                        {
                            String strCommentDigg = request.getParameter( PARAMETER_COMMENT_DIGG );
                            strContentDigg = getHtmlDiggSubmitDetail( request, nMode, plugin, digg, nIdSubmitDigg,
                                    strCommentDigg );
                        }

                        model.put( MARK_MODE_DIGG, 0 );
                    }

                    //Report
                    else if ( ( strReport != null ) && digg.isDisableNewDiggSubmit(  ) )
                    {
                        strContentDigg = getHtmlReported( request, nMode, plugin, digg, nIdSubmitDigg, strDetail );
                        model.put( MARK_MODE_DIGG, 0 );
                    }

                    // new reported 
                    else if ( ( strSubmitReportedDigg != null ) && digg.isDisableNewDiggSubmit(  ) )
                    {
                        String strReportedValue = request.getParameter( PARAMETER_REPORTED_VALUE );

                        if ( ( strReportedValue == null ) || strReportedValue.trim(  ).equals( EMPTY_STRING ) )
                        {
                            SiteMessageService.setMessage( request, MESSAGE_MANDATORY_REPORTED, SiteMessage.TYPE_STOP );
                        }

                        DiggSubmit diggSubmit = DiggSubmitHome.findByPrimaryKey( nIdSubmitDigg, plugin );

                        if ( diggSubmit == null )
                        {
                            SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
                        }

                        DiggUtils.doReportDiggSubmit( nIdSubmitDigg, plugin );

                        ReportedMessage reportedMessage = new ReportedMessage(  );
                        reportedMessage.setDiggSubmit( diggSubmit );
                        reportedMessage.setValue( strReportedValue );
                        DiggUtils.sendNotificationNewReportedMessage( digg, reportedMessage, locale, request );

                        Map<String, Object> parameters = new HashMap<String, Object>(  );
                        parameters.put( PARAMETER_ID_SUBMIT_DIGG, nIdSubmitDigg );
                        parameters.put( PARAMETER_ID_DIGG, nIdDigg );
                        parameters.put( PARAMETER_MODE_DIGG, 0 );
                        urlDiggXpage.setAnchor( ANCHOR_DIGG_SUBMIT + nIdSubmitDigg );
                        parameters.put( PARAMETER_DIGG_DETAIL, strDetail );

                        SiteMessageService.setMessage( request, MESSAGE_NEW_REPORTED_SUBMIT, SiteMessage.TYPE_INFO,
                            urlDiggXpage.getUrl(  ), parameters );
                    }

                    //Display detail diggSubmit
                    else if ( ( strDetail != null ) && ( strSubmitCommentDigg == null ) )
                    {
                        String strCommentDigg = request.getParameter( PARAMETER_COMMENT_DIGG );

                        strContentDigg = getHtmlDiggSubmitDetail( request, nMode, plugin, digg, nIdSubmitDigg,
                                strCommentDigg );

                        //model.put( MARK_MODE_DIGG, 0 );
                    }

                    // Insert comment and display detail diggSubmit
                    else if ( ( strSubmitCommentDigg != null ) && digg.isAuthorizedComment(  ) )
                    {
                        if ( digg.isActiveCommentAuthentification(  ) && SecurityService.isAuthenticationEnable(  ) )
                        {
                            luteceUser = ( luteceUser != null ) ? luteceUser
                                                                : SecurityService.getInstance(  ).getRemoteUser( request );
                        }

                        String strCommentValueDigg = request.getParameter( PARAMETER_COMMENT_VALUE_DIGG );
                        String strMessage = MESSAGE_NEW_COMMENT_SUBMIT;
                        String strIdParentComment = request.getParameter( PARAMETER_COMMENT_ID_PARENT );
                        int nIdParentComment = 0;

                        if ( ( strIdParentComment != null ) && ( !strIdParentComment.trim(  ).equals( EMPTY_STRING ) ) )
                        {
                            nIdParentComment = Integer.valueOf( strIdParentComment );
                        }

                        if ( ( strCommentValueDigg == null ) || strCommentValueDigg.trim(  ).equals( EMPTY_STRING ) )
                        {
                            SiteMessageService.setMessage( request, MESSAGE_MANDATORY_COMMENT, SiteMessage.TYPE_STOP );
                        }

                        if ( digg.isActiveCaptcha(  ) && PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) )
                        {
                            CaptchaSecurityService captchaSecurityService = new CaptchaSecurityService(  );

                            if ( !captchaSecurityService.validate( request ) )
                            {
                                SiteMessageService.setMessage( request, MESSAGE_CAPTCHA_ERROR, SiteMessage.TYPE_STOP );
                            }
                        }

                        CommentSubmit commentSubmit = doInsertComment( request, digg, nIdSubmitDigg,
                                strCommentValueDigg, plugin, luteceUser, nIdParentComment );

                        if ( digg.isDisableNewComment(  ) )
                        {
                            DiggUtils.sendNotificationNewCommentSubmit( digg, commentSubmit, locale, request );
                            strMessage = MESSAGE_NEW_COMMENT_SUBMIT_DISABLE;
                        }

                        Map<String, Object> parameters = new HashMap<String, Object>(  );

                        parameters.put( PARAMETER_ID_SUBMIT_DIGG, nIdSubmitDigg );
                        parameters.put( PARAMETER_ID_DIGG, nIdDigg );
                        parameters.put( PARAMETER_MODE_DIGG, 0 );
                        parameters.put( PARAMETER_COMMENT_DIGG, CONSTANTE_PARAMETER_TRUE_VALUE );
                        parameters.put( PARAMETER_DIGG_DETAIL, CONSTANTE_PARAMETER_TRUE_VALUE );

                        Object[] args = { ( digg.getConfirmationMessage(  ) == null ) ? ""
                                                                                      : digg.getConfirmationMessage(  ) };
                        SiteMessageService.setMessage( request, strMessage, args, null, urlDiggXpage.getUrl(  ), null,
                            SiteMessage.TYPE_INFO, parameters );
                    }
                    else
                    {
                        strContentDigg = getHtmlListDiggSubmit( locale, plugin, digg, nIdFilterPeriod,
                                nIdDiggSubmitSort, nIdFilterCategory, diggSubmitStatePublish.getIdDiggSubmitState(  ),
                                strQuery, strFilterPageIndex, urlDiggXpage );
                        model.put( MARK_MODE_DIGG, 0 );
                    }
                }

                // insert new digg submit and Display list diggSubmit
                else if ( ( strModeDigg == null ) && ( strSubmitDigg != null ) )
                {
                    if ( digg.isActiveDiggSubmitAuthentification(  ) && SecurityService.isAuthenticationEnable(  ) )
                    {
                        luteceUser = ( luteceUser != null ) ? luteceUser
                                                            : SecurityService.getInstance(  ).getRemoteUser( request );
                    }

                    String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY_DIGG );
                    String strIdType = request.getParameter( PARAMETER_ID_TYPE_DIGG );
                    String strMessage = MESSAGE_NEW_DIGG_SUBMIT;
                    int nIdCategory = DiggUtils.getIntegerParameter( strIdCategory );
                    int nIdType = DiggUtils.getIntegerParameter( strIdType );

                    //Check if a category is selected (in the case or the digg has some categories)
                    if ( strIdCategory != null )
                    {
                        if ( strIdCategory.equals( "-2" ) )
                        {
                            SiteMessageService.setMessage( request, MESSAGE_ERROR_NO_CATEGORY, SiteMessage.TYPE_STOP );
                        }
                    }

                    DiggSubmit diggSubmit = doInsertResponse( request, nMode, plugin, digg, nIdCategory, nIdType,
                            luteceUser );

                    if ( digg.isDisableNewDiggSubmit(  ) )
                    {
                        DiggUtils.sendNotificationNewDiggSubmit( digg, diggSubmit, locale, request );
                        strMessage = MESSAGE_NEW_DIGG_SUBMIT_DISABLE;
                    }

                    //send notification to the admin
                    if ( digg.isEnableMailNewDiggSubmit(  ) )
                    {
                        DiggUtils.sendMailNewDiggSubmit( digg, diggSubmit, locale, request );
                    }

                    Map<String, Object> parameters = new HashMap<String, Object>(  );
                    parameters.put( PARAMETER_ID_DIGG, nIdDigg );
                    parameters.put( PARAMETER_MODE_DIGG, 0 );

                    Object[] args = { ( digg.getConfirmationMessage(  ) == null ) ? "" : digg.getConfirmationMessage(  ) };
                    SiteMessageService.setMessage( request, strMessage, args, null, urlDiggXpage.getUrl(  ), null,
                        SiteMessage.TYPE_INFO, parameters );
                }

                // Display form for post diggSubmit
                else if ( ( strModeDigg != null ) && strModeDigg.equals( "1" ) )
                {
                    strContentDigg = getHtmlForm( request, nMode, plugin, digg );
                    model.put( MARK_MODE_DIGG, 1 );
                }

                // Display list diggSubmit
                else
                {
                    strContentDigg = getHtmlListDiggSubmit( locale, plugin, digg, nIdFilterPeriod, nIdDiggSubmitSort,
                            nIdFilterCategory, diggSubmitStatePublish.getIdDiggSubmitState(  ), strQuery,
                            strFilterPageIndex, urlDiggXpage );
                    model.put( MARK_MODE_DIGG, 0 );
                }

                //Filter by comment
                if ( digg.isAuthorizedComment(  ) )
                {
                    SubmitFilter submmitFilterTopComment = new SubmitFilter(  );
                    submmitFilterTopComment.setIdDigg( digg.getIdDigg(  ) );
                    submmitFilterTopComment.setIdDiggSubmitState( diggSubmitStatePublish.getIdDiggSubmitState(  ) );

                    DiggUtils.initSubmitFilterBySort( submmitFilterTopComment,
                        DiggUtils.CONSTANT_SORT_BY_NUMBER_COMMENT_DESC );

                    List<DiggSubmit> listDiggSubmitTopDay = DiggSubmitHome.getDiggSubmitList( submmitFilterTopComment,
                            plugin, digg.getNumberDiggSubmitInTopComment(  ) );
                    model.put( MARK_LIST_SUBMIT_TOP_COMMENT, listDiggSubmitTopDay );
                }

                //Filter by popularity
                SubmitFilter submmitFilterTopPopularity = new SubmitFilter(  );
                submmitFilterTopPopularity.setIdDigg( digg.getIdDigg(  ) );

                DiggUtils.initSubmitFilterBySort( submmitFilterTopPopularity, DiggUtils.CONSTANT_SORT_BY_SCORE_DESC );

                submmitFilterTopPopularity.setIdDiggSubmitState( diggSubmitStatePublish.getIdDiggSubmitState(  ) );

                List<DiggSubmit> listDiggSubmitTopPopularity = DiggSubmitHome.getDiggSubmitList( submmitFilterTopPopularity,
                        plugin, digg.getNumberDiggSubmitInTopScore(  ) );
                ReferenceList refListDiggSort = DiggUtils.getRefListDiggSort( locale );

                //model
                model.put( MARK_ID_DIGG, digg.getIdDigg(  ) );
                model.put( MARK_QUERY, strQuery );
                model.put( MARK_ID_DIGG_SUBMIT_SORT, nIdDiggSubmitSort );
                model.put( MARK_ID_FILTER_PERIOD, nIdFilterPeriod );
                model.put( MARK_ID_FILTER_CATEGORY_DIGG, nIdFilterCategory );
                model.put( MARK_CONTENT_DIGG, strContentDigg );
                model.put( MARK_LABEL_DIGG, digg.getLibelleContribution(  ) );
                model.put( MARK_HEADER_DIGG, digg.getHeader(  ) );
                model.put( MARK_LIST_CATEGORIES_DIGG, digg.getCategories(  ) );
                model.put( MARK_LIST_TYPES, DiggSubmitTypeHome.getList( plugin ) );
                model.put( MARK_LIST_SUBMIT_TOP_POPULARITY_DIGG, listDiggSubmitTopPopularity );
                model.put( MARK_AUTHORIZED_COMMENT, digg.isAuthorizedComment(  ) );
                model.put( MARK_NUMBER_SHOWN_CHARACTERS, nNumberShownCharacters );
                model.put( MARK_FILTER_DAY_VALUE, DiggUtils.CONSTANT_SUBMIT_FILTER_TO_DAY );
                model.put( MARK_FILTER_YESTERDDAY_VALUE, DiggUtils.CONSTANT_SUBMIT_FILTER_YESTERDAY );
                model.put( MARK_FILTER_WEEK_VALUE, DiggUtils.CONSTANT_SUBMIT_FILTER_WEEK );
                model.put( MARK_FILTER_MONTH_VALUE, DiggUtils.CONSTANT_SUBMIT_FILTER_MONTH );
                model.put( MARK_LIST_DIGG_SUBMIT_SORT, refListDiggSort );
                model.put( MARK_SHOW_CATEGORY_BLOCK, digg.isShowCategoryBlock(  ) );
                model.put( MARK_SHOW_TOP_SCORE_BLOCK, digg.isShowTopScoreBlock(  ) );
                model.put( MARK_SHOW_TOP_COMMENT_BLOCK, digg.isShowTopCommentBlock(  ) );
            }
            else
            {
                model.put( MARK_UNAVAILABILITY_MESSAGE, digg.getUnavailabilityMessage(  ) );
            }

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_FRAME_DIGG, request.getLocale(  ),
                    model );
            page.setContent( template.getHtml(  ) );
        }

        return page;
    }
    
    /**
     * return the html list of digg submit
     * @param strQuery the strQuery
     * @param locale the locale
     * @param plugin the plugin
     * @param strCurrentPageIndexDigg the current page index
     * @param nItemsPerPageDigg the number of items per page
     * @param urlDiggXPage the url of the digg xpage
     * @throws SiteMessageException  SiteMessageException
     * @return the html template
     */

    private String getSearch( String strQuery, Locale locale, Plugin plugin, String strCurrentPageIndexDigg,
            int nItemsPerPageDigg, UrlItem urlDiggXPage ) throws SiteMessageException
    {
    	SubmitFilter filter = new SubmitFilter(  );
    	
    	List listResultSearch = DigglikeSearchService.getInstance(  ).getSearchResults( strQuery, filter, plugin );
    	
    	HashMap model = new HashMap(  );
    	Paginator paginator = new Paginator( listResultSearch, nItemsPerPageDigg, urlDiggXPage.getUrl(  ),
                PARAMETER_PAGE_INDEX, strCurrentPageIndexDigg );

        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + nItemsPerPageDigg );
        model.put( FULL_URL, _strFullUrl );
        model.put( MARK_CONTENT_DIGG, paginator.getPageItems(  ) );
    	
    	HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_XPAGE_LIST_DIGG, locale, model );

        return templateList.getHtml(  );
	}

	/**
     * Increment score
     *
     * @param strVote the value to add at score
     * @param nIdSubmitDigg the id of the digg submit
     * @param plugin the plugin
     * @param strUserKey the user key
     */
    private void doVote( String strVote, int nIdSubmitDigg, Plugin plugin, String strUserKey )
    {
        //Increment vote
        int nScore;

        if ( ( strVote != null ) && strVote.equals( "-2" ) )
        {
            nScore = Integer.parseInt( strVote );
            DiggUtils.doVoteDiggSubmit( nIdSubmitDigg, nScore, strUserKey, plugin );
        }
        else if ( ( strVote != null ) && strVote.equals( "-1" ) )
        {
            nScore = Integer.parseInt( strVote );
            DiggUtils.doVoteDiggSubmit( nIdSubmitDigg, nScore, strUserKey, plugin );
        }
        else if ( ( strVote != null ) && strVote.equals( "1" ) )
        {
            nScore = Integer.parseInt( strVote );
            DiggUtils.doVoteDiggSubmit( nIdSubmitDigg, nScore, strUserKey, plugin );
        }
        else if ( ( strVote != null ) && strVote.equals( "2" ) )
        {
            nScore = Integer.parseInt( strVote );
            DiggUtils.doVoteDiggSubmit( nIdSubmitDigg, nScore, strUserKey, plugin );
        }
    }

    /**
     * return the html list of digg submit
     * @param locale the locale
     * @param plugin the plugin
     * @param digg the digg
     * @param nFilterPeriod the filter Period
     * @param nSortDigg the filter sort
     * @param nIdFilterCategory the filter category
     * @param nIdDigSubmitState the digg submit state
     * @param strQuery the strQuery
     * @param strPageIndex the strPageIndex
     * @param urlDiggXPage the url of the Xpage
     * @return the html list of digg submit
     * @throws SiteMessageException  SiteMessageException
     */
    private String getHtmlListDiggSubmit( Locale locale, Plugin plugin, Digg digg, int nFilterPeriod, int nSortDigg,
        int nIdFilterCategory, int nIdDigSubmitState, String strQuery, String strPageIndex, UrlItem urlDiggXPage )
        throws SiteMessageException
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( FULL_URL, _strFullUrl );

        List<DiggSubmit> listDiggSubmit;
        List<DiggSubmit> listDiggSubmitTemp;
        List<DiggSubmit> listDiggSubmitT;
        SubmitFilter submitFilter = new SubmitFilter(  );

        //Filter the list
        DiggUtils.initSubmitFilterByPeriod( submitFilter, nFilterPeriod );
        DiggUtils.initSubmitFilterBySort( submitFilter, nSortDigg );

        submitFilter.setIdDigg( digg.getIdDigg(  ) );
        submitFilter.setIdDiggSubmitState( nIdDigSubmitState );
        submitFilter.setIdCategory( nIdFilterCategory );

        if ( ( strQuery != null ) && ( strQuery.trim(  ) != DiggUtils.EMPTY_STRING ) )
        {
            listDiggSubmitTemp = DigglikeSearchService.getInstance(  ).getSearchResults( strQuery, submitFilter, plugin );
            listDiggSubmit = new ArrayList<DiggSubmit>(  );

            for ( DiggSubmit d : listDiggSubmitTemp )
            {
                if ( d.getDiggSubmitState(  ).getIdDiggSubmitState(  ) == DiggSubmit.STATE_PUBLISH )
                {
                    submitFilter.setIdDiggSubmit( d.getIdDiggSubmit(  ) );
                    listDiggSubmitT = DiggSubmitHome.getDiggSubmitList( submitFilter, plugin );
                    listDiggSubmit.add( listDiggSubmitT.get( 0 ) );
                }
            }
        }
        else
        {
            listDiggSubmit = DiggSubmitHome.getDiggSubmitList( submitFilter, plugin );
        }

        if ( digg.isActiveDiggSubmitPaginator(  ) && ( digg.getNumberDiggSubmitPerPage(  ) > 0 ) )
        {
            Paginator paginator = new Paginator( listDiggSubmit, digg.getNumberDiggSubmitPerPage(  ),
                    urlDiggXPage.getUrl(  ), PARAMETER_FILTER_PAGE_INDEX, strPageIndex );
            listDiggSubmit = paginator.getPageItems(  );
            model.put( MARK_PAGINATOR, paginator );
        }

        model.put( MARK_LIST_DIGG_SUBMIT,
            getDiggSubmitDisplayList( listDiggSubmit, digg, CONSTANTE_PARAMETER_FALSE_VALUE, locale ) );
        model.put( MARK_AUTHORIZED_COMMENT, digg.isAuthorizedComment(  ) );
        model.put( MARK_DIGG_SUBMIT_MODERATE, digg.isDisableNewDiggSubmit(  ) );
        model.put( MARK_ID_DIGG, digg.getIdDigg(  ) );
        model.put( MARK_MAX_AMOUNT_COMMENTS, AppPropertiesService.getPropertyInt( PROPERTY_MAX_AMOUNT_COMMENTS, 20 ) );
        model.put( MARK_MAX_AMOUNT_COMMENTS_CHAR,
            AppPropertiesService.getPropertyInt( PROPERTY_MAX_AMOUNT_COMMENTS_CHAR, 20 ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_LIST_SUBMIT_DIGG, locale, model );

        return template.getHtml(  );
    }

    /**
     * return the html list of digg submit
     * @param locale the locale
     * @param plugin the plugin
     * @param strCurrentPageIndexDigg the current page index
     * @param nItemsPerPageDigg the number of items per page
     * @param urlDiggXPage the url of the digg xpage
     * @throws SiteMessageException  SiteMessageException
     * @return the html template
     */
    private String getHtmlListDigg( Locale locale, Plugin plugin, String strCurrentPageIndexDigg,
        int nItemsPerPageDigg, UrlItem urlDiggXPage ) throws SiteMessageException
    {
        DiggFilter filter = new DiggFilter(  );

        List listDigg = DiggHome.getDiggList( filter, plugin );

        HashMap model = new HashMap(  );
        Paginator paginator = new Paginator( listDigg, nItemsPerPageDigg, urlDiggXPage.getUrl(  ),
                PARAMETER_PAGE_INDEX, strCurrentPageIndexDigg );

        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + nItemsPerPageDigg );
        model.put( FULL_URL, _strFullUrl );
        model.put( MARK_DIGG_LIST, paginator.getPageItems(  ) );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_XPAGE_LIST_DIGG, locale, model );

        return templateList.getHtml(  );
    }

    /**
     * return a collection which contains digg submit and lutece user associate
     * @param listDiggSubmit the list of digg submit
     * @param digg the digg possessing submits
     * @param strDiggDetail the digg detail
     * @param locale the locale
     * @throws SiteMessageException SiteMessageException
     * @return a collection which contains digg submit and lutece user associate
     */
    private Collection<HashMap> getDiggSubmitDisplayList( Collection<DiggSubmit> listDiggSubmit, Digg digg,
        String strDiggDetail, Locale locale ) throws SiteMessageException
    {
        Collection<HashMap> listHashDigg = new ArrayList<HashMap>(  );
        LuteceUser luteceUser;

        for ( DiggSubmit diggSubmit : listDiggSubmit )
        {
            HashMap<String, Object> modelDigg = new HashMap<String, Object>(  );
            modelDigg.put( FULL_URL, _strFullUrl );
            luteceUser = null;

            modelDigg.put( MARK_DIGG_SUBMIT, diggSubmit );

            if ( SecurityService.isAuthenticationEnable(  ) && ( diggSubmit.getLuteceUserKey(  ) != null ) )
            {
                luteceUser = SecurityService.getInstance(  ).getUser( diggSubmit.getLuteceUserKey(  ) );
            }

            modelDigg.put( MARK_LUTECE_USER, luteceUser );
            modelDigg.put( MARK_DIGG_SUBMIT_VOTE_TYPE,
                getHtmlDiggSubmitVoteType( digg.getVoteType(  ).getTemplateFileName(  ), digg.getIdDigg(  ),
                    diggSubmit.getIdDiggSubmit(  ), CONSTANTE_PARAMETER_FALSE_VALUE, locale ) );

            listHashDigg.add( modelDigg );
        }

        return listHashDigg;
    }

    /**
     * return  a collection which contains comment and lutece user associate
     * @param listCommentSubmit the list of comment submit
     * @return a collection which contains comment and lutece user associate
     */
    private Collection<HashMap> getCommentSubmitDisplayList( Collection<CommentSubmit> listCommentSubmit )
    {
        Collection<HashMap> listHashComment = new ArrayList<HashMap>(  );
        LuteceUser luteceUser;

        for ( CommentSubmit commentSubmit : listCommentSubmit )
        {
            HashMap<String, Object> modelComment = new HashMap<String, Object>(  );
            modelComment.put( FULL_URL, _strFullUrl );
            luteceUser = null;

            modelComment.put( MARK_COMMENT_SUBMIT, commentSubmit );

            if ( SecurityService.isAuthenticationEnable(  ) && ( commentSubmit.getLuteceUserKey(  ) != null ) )
            {
                luteceUser = SecurityService.getInstance(  ).getUser( commentSubmit.getLuteceUserKey(  ) );
            }

            modelComment.put( MARK_LUTECE_USER, luteceUser );
            listHashComment.add( modelComment );
        }

        return listHashComment;
    }

    /**
     * the html digg submit detail
     * @param request the request
     * @param nMode The current mode.
     * @param plugin the plugin
     * @param digg the digg
     * @param nIdSubmitDigg the id of the digg submit
     * @param strComment the strComment
     * @return the html digg submit detail
     * @throws SiteMessageException SiteMessageException
     */
    private String getHtmlDiggSubmitDetail( HttpServletRequest request, int nMode, Plugin plugin, Digg digg,
        int nIdSubmitDigg, String strComment ) throws SiteMessageException
    {
        LuteceUser luteceUser = null;
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( FULL_URL, _strFullUrl );

        DiggSubmit diggSubmit = DiggSubmitHome.findByPrimaryKey( nIdSubmitDigg, plugin );

        if ( ( diggSubmit == null ) || ( diggSubmit.getDiggSubmitState(  ).getNumber(  ) == DiggSubmit.STATE_DISABLE ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
        }

        SubmitFilter submmitFilterComment = new SubmitFilter(  );
        submmitFilterComment.setIdDiggSubmit( diggSubmit.getIdDiggSubmit(  ) );
        submmitFilterComment.setIdCommentSubmitState( 1 );

        //diggSubmit.setNumberCommentEnable( CommentSubmitHome.getCountTagSubmit(submmitFilterComment, plugin) );
        List<CommentSubmit> listCommentSubmit = CommentSubmitHome.getCommentSubmitList( submmitFilterComment, plugin );

        if ( SecurityService.isAuthenticationEnable(  ) && ( diggSubmit.getLuteceUserKey(  ) != null ) )
        {
            luteceUser = SecurityService.getInstance(  ).getUser( diggSubmit.getLuteceUserKey(  ) );
        }

        model.put( MARK_ID_DIGG, digg.getIdDigg(  ) );
        model.put( MARK_DIGG_SUBMIT, diggSubmit );
        model.put( MARK_LUTECE_USER, luteceUser );
        model.put( MARK_DIGG_SUBMIT_VOTE_TYPE,
            getHtmlDiggSubmitVoteType( digg.getVoteType(  ).getTemplateFileName(  ), digg.getIdDigg(  ),
                diggSubmit.getIdDiggSubmit(  ), CONSTANTE_PARAMETER_TRUE_VALUE, request.getLocale(  ) ) );
        model.put( MARK_AUTHORIZED_COMMENT, digg.isAuthorizedComment(  ) );
        model.put( MARK_DIGG_SUBMIT_MODERATE, digg.isDisableNewDiggSubmit(  ) );

        if ( ( strComment != null ) && strComment.equals( CONSTANTE_PARAMETER_TRUE_VALUE ) &&
                digg.isAuthorizedComment(  ) )
        {
            model.put( MARK_LIST_COMMENT_SUBMIT_DIGG,
                getHtmlCommentSubmitList( request, listCommentSubmit, digg, nIdSubmitDigg ) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_DETAIL_SUBMIT_DIGG,
                request.getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * return the Html type of vote
     * @param strVoteTypeTemplateName the template use
     * @param nIdDigg the id of the digg
     * @param nIdDiggSubmit the id of the diggSubmit
     * @param strDiggDetail 1(vote in digg submit detail) or 0
     * @param locale the locale
     * @return the Html type of vote
     * @throws SiteMessageException SiteMessageException
     */
    private String getHtmlDiggSubmitVoteType( String strVoteTypeTemplateName, int nIdDigg, int nIdDiggSubmit,
        String strDiggDetail, Locale locale ) throws SiteMessageException
    {
        String strFilePath = PATH_TYPE_VOTE_FOLDER + strVoteTypeTemplateName;
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( FULL_URL, _strFullUrl );
        model.put( MARK_ID_DIGG, nIdDigg );
        model.put( MARK_ID_DIGG_SUBMIT, nIdDiggSubmit );
        model.put( MARK_DIGG_DETAIL, strDiggDetail );

        HtmlTemplate template = AppTemplateService.getTemplate( strFilePath, locale, model );

        return template.getHtml(  );
    }

    /**
     * return the html form reported
     * @param request the request
     * @param nMode The current mode.
     * @param plugin plugin
     * @param digg the digg
     * @param nIdSubmitDigg the id of the digg submit
     * @param strDetail 1(reported in digg submit detail) or 0
     * @return the html form reported
     * @throws SiteMessageException SiteMessageException
     */
    private String getHtmlReported( HttpServletRequest request, int nMode, Plugin plugin, Digg digg, int nIdSubmitDigg,
        String strDetail ) throws SiteMessageException
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( FULL_URL, _strFullUrl );

        DiggSubmit diggSubmit = DiggSubmitHome.findByPrimaryKey( nIdSubmitDigg, plugin );

        if ( ( diggSubmit == null ) || ( diggSubmit.getDiggSubmitState(  ).getNumber(  ) == DiggSubmit.STATE_DISABLE ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
        }

        model.put( MARK_ID_DIGG, digg.getIdDigg(  ) );
        model.put( MARK_DIGG_SUBMIT, diggSubmit );
        model.put( MARK_DIGG_DETAIL, strDetail );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_DIGG_REPORTED, request.getLocale(  ),
                model );

        return template.getHtml(  );
    }

    /**
     * return the html list of comment submit for a digg submit
     * @param request the request
     * @param listCommentSubmit the list of comment submit
     * @param digg the digg
     * @param nIdSubmitDigg the id of the digg submit
     * @return the html list of comment submit for a digg submit
     */
    private String getHtmlCommentSubmitList( HttpServletRequest request, List<CommentSubmit> listCommentSubmit,
        Digg digg, int nIdSubmitDigg )
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( FULL_URL, _strFullUrl );
        model.put( MARK_ID_DIGG, digg.getIdDigg(  ) );
        model.put( MARK_ID_DIGG_SUBMIT, nIdSubmitDigg );
        model.put( MARK_DIGG_COMMENT, CONSTANTE_PARAMETER_TRUE_VALUE );
        model.put( MARK_LIST_COMMENT_SUBMIT_DIGG, getCommentSubmitDisplayList( listCommentSubmit ) );
        model.put( MARK_DISABLE_NEW_COMMENT_SUBMIT, digg.isDisableNewComment(  ) );

        CaptchaSecurityService captchaSecurityService = new CaptchaSecurityService(  );

        if ( digg.isActiveCaptcha(  ) && PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) )
        {
            model.put( MARK_JCAPTCHA, captchaSecurityService.getHtmlCode(  ) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_COMMENT_SUBMIT_DIGG,
                request.getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * Return the html form
     * @param request The HTTP request
     * @param nMode The current mode.
     * @param plugin The Plugin
     * @param digg the digg
     * @return the html form
     * @throws SiteMessageException SiteMessageException
     */
    private String getHtmlForm( HttpServletRequest request, int nMode, Plugin plugin, Digg digg )
        throws SiteMessageException
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( FULL_URL, _strFullUrl );

        Locale locale = request.getLocale(  );

        //get form Recap
        model.put( MARK_FORM_DIGG, DiggUtils.getHtmlForm( digg, plugin, locale, _strFullUrl ) );
        model.put( MARK_DISABLE_NEW_DIGG_SUBMIT, digg.isDisableNewDiggSubmit(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_FORM_DIGG, locale, model );

        return template.getHtml(  );
    }

    /**
     * Return the new digg submit object
     * @param request The HTTP request
     * @param nMode The current mode.
     * @param plugin The Plugin
     * @param digg the digg
     * @param nIdCategory the id of the category
     * @param nIdType the id of the type
     * @param user the lutece user
     * @return the new digg submit object
     * @throws SiteMessageException SiteMessageException
     */
    private DiggSubmit doInsertResponse( HttpServletRequest request, int nMode, Plugin plugin, Digg digg,
        int nIdCategory, int nIdType, LuteceUser user )
        throws SiteMessageException
    {
        Locale locale = request.getLocale(  );

        if ( digg.isActiveCaptcha(  ) && PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) )
        {
            CaptchaSecurityService captchaSecurityService = new CaptchaSecurityService(  );

            if ( !captchaSecurityService.validate( request ) )
            {
                SiteMessageService.setMessage( request, MESSAGE_CAPTCHA_ERROR, SiteMessage.TYPE_STOP );
            }
        }

        //create form response
        DiggSubmit diggSubmit = new DiggSubmit(  );
        diggSubmit.setDigg( digg );
        diggSubmit.setDateResponse( DiggUtils.getCurrentDate(  ) );

        if ( digg.isDisableNewDiggSubmit(  ) )
        {
            diggSubmit.setDiggSubmitState( DiggSubmitStateHome.findByNumero( DiggSubmit.STATE_WAITING_FOR_PUBLISH,
                    plugin ) );
        }
        else
        {
            diggSubmit.setDiggSubmitState( DiggSubmitStateHome.findByNumero( DiggSubmit.STATE_PUBLISH, plugin ) );
        }

        diggSubmit.setDigg( digg );
        diggSubmit.setIdDiggSubmit( DiggSubmitHome.create( diggSubmit, plugin ) );
        doInsertResponseInDiggSubmit( request, diggSubmit, nIdCategory, nIdType, plugin );
        diggSubmit.setDiggSubmitValue( DiggUtils.getHtmlDiggSubmitValue( diggSubmit, locale ) );
        diggSubmit.setDiggSubmitValueShowInTheList( DiggUtils.getHtmlDiggSubmitValueShowInTheList( diggSubmit, locale ) );
        diggSubmit.setDiggSubmitTitle( DiggUtils.getDiggSubmitTitle( diggSubmit, locale ) );

        // Check XSS characters
         for ( Response response : diggSubmit.getResponses(  ) )
         {
             if ( StringUtil.containsXssCharacters( response.getValueResponse(  ) ) &&
                     !response.getValueResponse(  ).contains( "<img " ) && !response.getValueResponse(  ).contains( "<div id='mediaspace" ) )
             {
                     DiggSubmitHome.remove( diggSubmit.getIdDiggSubmit(  ), plugin);
                 SiteMessageService.setMessage( request, MESSAGE_NEW_DIGG_SUBMIT_INVALID, SiteMessage.TYPE_STOP );
             }
         }
        if ( user != null )
        {
            diggSubmit.setLuteceUserKey( user.getName(  ) );
        }

        //        diggSubmit.setIdDiggSubmit( DiggSubmitHome.create( diggSubmit, plugin ) );
        DiggSubmitHome.update( diggSubmit, plugin );

        //store response
        for ( Response response : diggSubmit.getResponses(  ) )
        {
            response.setDiggSubmit( diggSubmit );
            ResponseHome.create( response, plugin );
        }

        return diggSubmit;
    }

    /**
     * return the new comment submit create
     * @param request the http request
     * @param digg  the digg
     * @param nIdSubmitDigg the id of the digg submit associate to the comment
     * @param strCommentValueDigg the comment value
     * @param plugin the plugin
     * @param nIdParentComment the id of the parent of this comment
     * @param user the Lutece user associate to the comment
     * @return the new comment submit create
     * @throws SiteMessageException SiteMessageException
     */
    public CommentSubmit doInsertComment( HttpServletRequest request, Digg digg, int nIdSubmitDigg,
        String strCommentValueDigg, Plugin plugin, LuteceUser user, int nIdParentComment )
        throws SiteMessageException
    {
        _strFullUrl = request.getRequestURL(  ).toString(  );

        CommentSubmit commentSubmit = new CommentSubmit(  );

        DiggSubmit diggSubmit = DiggSubmitHome.findByPrimaryKey( nIdSubmitDigg, plugin );

        if ( ( diggSubmit == null ) || ( diggSubmit.getDiggSubmitState(  ).getNumber(  ) == DiggSubmit.STATE_DISABLE ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
        }

        // Check XSS characters
        if ( StringUtil.containsXssCharacters( strCommentValueDigg ) )
        {
            strCommentValueDigg = "";
            SiteMessageService.setMessage( request, MESSAGE_NEW_COMMENT_SUBMIT_INVALID, SiteMessage.TYPE_STOP );
        }

        if ( !digg.isDisableNewComment(  ) )
        {
            commentSubmit.setActive( true );
            diggSubmit.setNumberCommentEnable( diggSubmit.getNumberCommentEnable(  ) + 1 );
            DiggSubmitHome.update( diggSubmit, plugin );
        }

        commentSubmit.setDateComment( DiggUtils.getCurrentDate(  ) );
        commentSubmit.setDiggSubmit( diggSubmit );
        commentSubmit.setValue( strCommentValueDigg );
        commentSubmit.setOfficialAnswer( false );
        commentSubmit.setIdParent( nIdParentComment );

        if ( user != null )
        {
            commentSubmit.setLuteceUserKey( user.getName(  ) );
        }

        CommentSubmitHome.create( commentSubmit, plugin );

        return commentSubmit;
    }

    /**
     * Perform the digg submit
     * @param request request The HTTP request
     * @param diggSubmit diggSubmit
     * @param nIdCategory the category id of the digg submit
     * @param nIdType the type id
     * @param plugin the Plugin
     * @throws SiteMessageException the site Message exception
     */
    public void doInsertResponseInDiggSubmit( HttpServletRequest request, DiggSubmit diggSubmit, int nIdCategory,
        int nIdType, Plugin plugin ) throws SiteMessageException
    {
        _strFullUrl = request.getRequestURL(  ).toString(  );

        List<IEntry> listEntryFirstLevel;
        EntryFilter filter;
        Locale locale = request.getLocale(  );

        FormError formError = null;

        filter = new EntryFilter(  );
        filter.setIdDigg( diggSubmit.getDigg(  ).getIdDigg(  ) );
        listEntryFirstLevel = EntryHome.getEntryList( filter, plugin );

        List<Response> listResponse = new ArrayList<Response>(  );
        diggSubmit.setResponses( listResponse );

        for ( IEntry entry : listEntryFirstLevel )
        {
            formError = DiggUtils.getResponseEntry( request, entry.getIdEntry(  ), plugin, diggSubmit, false, locale );

            if ( formError != null )
            {
                if ( formError.isMandatoryError(  ) )
                {
                    Object[] tabRequiredFields = { formError.getTitleQuestion(  ) };
                    SiteMessageService.setMessage( request, MESSAGE_MANDATORY_QUESTION, tabRequiredFields,
                        SiteMessage.TYPE_STOP );
                }
                else
                {
                    Object[] tabFormError = { formError.getTitleQuestion(  ), formError.getErrorMessage(  ) };
                    SiteMessageService.setMessage( request, MESSAGE_FORM_ERROR, tabFormError, SiteMessage.TYPE_STOP );
                }
            }
        }

        if ( nIdCategory != -1 )
        {
            Category category = CategoryHome.findByPrimaryKey( nIdCategory, plugin );
            diggSubmit.setCategory( category );
        }

        if ( nIdType != -1 )
        {
            DiggSubmitType type = DiggSubmitTypeHome.findByPrimaryKey( nIdType, plugin );
            diggSubmit.setDiggSubmitType( type );
        }
    }

    /**
     * Clear params stores in  session
     * @param session the Http session
     */
    private void clearSessionFilter( HttpSession session )
    {
        session.setAttribute( SESSION_FILTER_QUERY, EMPTY_STRING );
        session.setAttribute( SESSION_FILTER_ID_PERIOD, -1 );
        session.setAttribute( SESSION_FILTER_ID_SORT, -1 );
        session.setAttribute( SESSION_FILTER_ID_CATEGORY, -1 );
        session.setAttribute( SESSION_FILTER_PAGE_INDEX, DEFAULT_PAGE_INDEX );
    }
}
