/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.suggest.business.Category;
import fr.paris.lutece.plugins.suggest.business.CategoryHome;
import fr.paris.lutece.plugins.suggest.business.CommentSubmit;
import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestFilter;
import fr.paris.lutece.plugins.suggest.business.SuggestHome;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitState;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitStateHome;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitType;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitTypeHome;
import fr.paris.lutece.plugins.suggest.business.SuggestUserInfo;
import fr.paris.lutece.plugins.suggest.business.FormError;
import fr.paris.lutece.plugins.suggest.business.ReportedMessage;
import fr.paris.lutece.plugins.suggest.business.ReportedMessageHome;
import fr.paris.lutece.plugins.suggest.business.Response;
import fr.paris.lutece.plugins.suggest.business.SearchFields;
import fr.paris.lutece.plugins.suggest.business.SubmitFilter;
import fr.paris.lutece.plugins.suggest.business.VoteHome;
import fr.paris.lutece.plugins.suggest.business.VoteType;
import fr.paris.lutece.plugins.suggest.business.VoteTypeHome;
import fr.paris.lutece.plugins.suggest.service.CommentSubmitService;
import fr.paris.lutece.plugins.suggest.service.SuggestSubmitService;
import fr.paris.lutece.plugins.suggest.service.SuggestUserInfoService;
import fr.paris.lutece.plugins.suggest.service.SuggestService;
import fr.paris.lutece.plugins.suggest.service.ICommentSubmitService;
import fr.paris.lutece.plugins.suggest.service.ISuggestSubmitService;
import fr.paris.lutece.plugins.suggest.service.suggestsearch.SuggestSearchService;
import fr.paris.lutece.plugins.suggest.service.subscription.SuggestSubscriptionProviderService;
import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.portal.service.captcha.CaptchaSecurityService;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;


/**
 * This class manages Form page.
 */
public class SuggestApp implements XPageApplication
{
    public static final String ANCHOR_SUGGEST_SUBMIT = "suggest";
    public static final String ANCHOR_FRAMESET_CONTENT_SUGGEST = "frameset_content_suggest";
    public static final String PARAMETER_CLEAR_FILTER = "clear_filter";
    public static final String PARAMETER_SUGGEST_DETAIL = "suggest_detail";
    public static final String ACTION_VIEW_SUGGEST_SUBMIT = "view_suggest_submit";

    // markers
    private static final String MARK_SUGGEST = "suggest";
    private static final String MARK_CONTENT_SUGGEST = "content_suggest";
    private static final String MARK_LIST_SUGGEST_SUBMIT = "list_suggest_submit";
    private static final String MARK_LABEL_SUGGEST = "label_suggest";
    private static final String MARK_HEADER_SUGGEST = "header_suggest";
    private static final String MARK_LIST_CATEGORIES_SUGGEST = "list_categories_suggest";
    private static final String MARK_ID_SUGGEST = "id_suggest";
    private static final String MARK_SUGGEST_LIST = "list_suggest";
    private static final String MARK_LIST_SUBMIT_TOP_COMMENT = "list_top_comment_suggest";
    private static final String MARK_LIST_SUBMIT_TOP_POPULARITY_SUGGEST = "list_top_popularity_suggest";
    private static final String MARK_ID_SUGGEST_SUBMIT = "id_suggest_submit";
    private static final String MARK_LIST_COMMENT_SUBMIT_SUGGEST = "list_comment";
    private static final String MARK_LIST_SUB_COMMENT_SUBMIT_SUGGEST = "list_sub_comment";
    private static final String MARK_SUGGEST_COMMENT = "suggest_comment";
    private static final String MARK_AUTHORIZED_COMMENT = "authorized_comment";
    private static final String MARK_AUTHORIZED_VOTE = "authorized_vote";
    private static final String MARK_DISPLAY_COMMENT_IN_LIST = "display_comment_in_list";
    private static final String MARK_ENABLE_SUGGEST_REPORTS = "enable_suggest_reports";
    private static final String MARK_SUGGEST_SUBMIT = "suggest_submit";
    private static final String MARK_COMMENT_SUBMIT = "comment_submit";
    private static final String MARK_LUTECE_USER = "lutece_user";
    private static final String MARK_LUTECE_USER_CONNECTED = "lutece_user_connected";
    private static final String MARK_UNAVAILABILITY_MESSAGE = "unavailability_message";
    private static final String MARK_NUMBER_SHOWN_CHARACTERS = "number_shown_characters";
    private static final String MARK_DISABLE_NEW_SUGGEST_SUBMIT = "disable_new_suggest_submit";
    private static final String MARK_DISABLE_NEW_COMMENT_SUBMIT = "disable_new_comment_submit";
    private static final String MARK_QUERY = "query";
    private static final String MARK_ID_FILTER_CATEGORY_SUGGEST = "id_filter_category";
    private static final String MARK_ID_FILTER_TYPE = "id_filter_type";
    private static final String MARK_TYPE_SELECTED = "type_selected";
    private static final String MARK_ID_FILTER_PERIOD = "id_filter_period";
    private static final String MARK_LIST_SUGGEST_SUBMIT_SORT = "list_suggest_submit_sort";
    private static final String MARK_LIST_FILTER_BY_PERIOD = "list_filter_by_period";
    private static final String MARK_ID_SUGGEST_SUBMIT_SORT = "id_suggest_submit_sort";
    private static final String MARK_SHOW_CATEGORY_BLOCK = "show_category_block";
    private static final String MARK_SHOW_TOP_SCORE_BLOCK = "show_top_score_block";
    private static final String MARK_SHOW_TOP_COMMENT_BLOCK = "show_top_comment_block";
    private static final String MARK_SUGGEST_SUBMIT_VOTE_TYPE = "suggest_submit_vote_type";
  
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_JCAPTCHA = "jcaptcha";
    private static final String MARK_MAX_AMOUNT_COMMENTS = "number_comments";
    private static final String MARK_MAX_AMOUNT_COMMENTS_CHAR = "cumber_char_comments";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_ACTIVE_EDITOR_BBCODE = "active_editor_bbcode";
    private static final String MARK_VIEW = "view";
    private static final String MARK_USER_SUBSCRIBED = "user_subscribed";
    private static final String MARK_IS_EXTEND_INSTALLED = "isExtendInstalled";

    // templates
    private static final String TEMPLATE_XPAGE_FRAME_SUGGEST = "skin/plugins/suggest/suggest_frame.html";
    private static final String TEMPLATE_XPAGE_LIST_SUBMIT_SUGGEST = "skin/plugins/suggest/suggest_list_submit.html";
    private static final String TEMPLATE_XPAGE_FORM_SUGGEST = "skin/plugins/suggest/suggest_form.html";
    private static final String TEMPLATE_XPAGE_LIST_SUGGEST = "skin/plugins/suggest/suggest_list.html";
    private static final String TEMPLATE_XPAGE_DETAIL_SUBMIT_SUGGEST = "skin/plugins/suggest/suggest_detail.html";
    private static final String TEMPLATE_XPAGE_SUGGEST_REPORTED = "skin/plugins/suggest/suggest_reported.html";
    private static final String TEMPLATE_XPAGE_SUGGEST_SUB_COMMENT = "skin/plugins/suggest/suggest_sub_comment.html";
    private static final String TEMPLATE_XPAGE_COMMENT_SUBMIT_SUGGEST = "skin/plugins/suggest/suggest_comment.html";
    private static final String JCAPTCHA_PLUGIN = "jcaptcha";

    // properties for page titles and path label
    private static final String PROPERTY_XPAGE_PAGETITLE = "suggest.xpage.pagetitle";
    private static final String PROPERTY_XPAGE_PATHLABEL = "suggest.xpage.pathlabel";
    private static final String PROPERTY_NUMBER_SUGGEST_SUBMIT_VALUE_SHOWN_CHARACTERS = "suggest.suggestSubmitValue.NumberShownCharacters";
    private static final String PROPERTY_PAGE_APPLICATION_ID = "suggest.xpage.applicationId";
    private static final String PROPERTY_ITEM_PER_PAGE = "suggest.itemsPerPage.front";

    // request parameters
    private static final String PARAMETER_ID_SUGGEST = "id_suggest";
    private static final String PARAMETER_ID_SUBMIT_SUGGEST = "id_suggest_submit";
    private static final String PARAMETER_COMMENT_SUGGEST = "suggest_comment";
    private static final String PARAMETER_COMMENT_VALUE_SUGGEST = "comment_value";
    private static final String PARAMETER_REPORTED_VALUE = "reported_value";
    private static final String PARAMETER_ID_FILTER_PERIOD = "id_filter_period";
    private static final String PARAMETER_ID_SUGGEST_SUBMIT_SORT = "id_suggest_submit_sort";
    private static final String PARAMETER_VOTE_SUGGEST = "vote";
    private static final String PARAMETER_ID_FILTER_CATEGORY_SUGGEST = "id_filter_category";
    private static final String PARAMETER_ID_FILTER_SUGGEST_SUBMIT_TYPE = "id_filter_type";
    private static final String PARAMETER_LUTECE_USER_NAME_FILTER = "lutece_user_name_filter";
    private static final String PARAMETER_ID_CATEGORY_SUGGEST = "id_category";
    private static final String PARAMETER_ID_TYPE_SUGGEST = "id_type";
    private static final String PARAMETER_VOTED = "voted";
    private static final String PARAMETER_QUERY = "query";
    private static final String PARAMETER_FILTER_PAGE_INDEX = "filter_page_index";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_COMMENT_ID_PARENT = "id_suggest_comment";
    private static final String PARAMETER_ACTION = "action";
    private static final String PARAMETER_VIEW = "view";
    private static final String PARAMETER_TERMS_OF_USE = "terms_of_use";
    private static final String PARAMETER_PAGE = "page";

    // message
    private static final String MESSAGE_FORM_ERROR = "suggest.message.formError";
    private static final String MESSAGE_MANDATORY_QUESTION = "suggest.message.mandatory.question";
    private static final String MESSAGE_ERROR = "suggest.message.Error";
    private static final String MESSAGE_MANDATORY_COMMENT = "suggest.message.mandatoryComment";
    private static final String MESSAGE_MANDATORY_REPORTED = "suggest.message.mandatoryReported";
    private static final String MESSAGE_CAPTCHA_ERROR = "suggest.message.captchaError";
    private static final String MESSAGE_NEW_SUGGEST_SUBMIT = "suggest.message.newSuggestSubmit";
    private static final String MESSAGE_NEW_SUGGEST_SUBMIT_DISABLE = "suggest.message.newSuggestSubmitDisable";
    private static final String MESSAGE_MESSAGE_SUBMIT_SAVE_ERROR = "suggest.message.submitSaveError";
    private static final String MESSAGE_NEW_COMMENT_SUBMIT = "suggest.message.newCommentSubmit";
    private static final String MESSAGE_NEW_COMMENT_SUBMIT_DISABLE = "suggest.message.newCommentSubmitDisable";
    private static final String MESSAGE_NEW_REPORTED_SUBMIT = "suggest.message.newReportedSubmit";
    private static final String MESSAGE_ERROR_NO_CATEGORY = "suggest.message.errorNoCategorySelected";
    private static final String MESSAGE_ERROR_NO_SUGGEST_SUBMIT_TYPE_SELECTED = "suggest.message.errorNoSuggestSubmitTypeSelected";
    private static final String MESSAGE_ERROR_MUST_SELECTED_TERMS_OF_USE = "suggest.message.youMustSelectTermsOfUse";
    private static final String MESSAGE_ACCESS_DENIED = "suggest.message.accessDenied";

    // XPAGE URL

    // constant
    private static final String EMPTY_STRING = "";
    private static final String CONSTANTE_PARAMETER_TRUE_VALUE = "1";
    private static final String PATH_TYPE_VOTE_FOLDER = "skin/plugins/suggest/";
    private static final String ACTION_VIEW_SUGGEST_LIST = "view_suggest_list";
    private static final String ACTION_VIEW_SUGGEST_SUBMIT_LIST = "view_suggest_submit_list";
    private static final String ACTION_CREATE_SUGGEST_SUBMIT = "create_suggest_submit";
    private static final String ACTION_DO_CREATE_SUGGEST_SUBMIT = "do_create_suggest_submit";
    private static final String ACTION_DO_CREATE_COMMENT = "do_create_comment";
    private static final String ACTION_DO_CREATE_REPORT = "do_create_report";
    private static final String ACTION_DO_VOTE = "do_vote";
    private static final String ACTION_CREATE_REPORT = "create_report";
    private static final String ACTION_CREATE_SUB_COMMENT = "create_sub_comment";
    private static final String ACTION_SUBSCRIBE_SUGGEST = "subscribe_suggest";
    private static final String ACTION_UNSUBSCRIBE_SUGGEST = "unsubscribe_suggest";
    private static final String CONSTANT_VIEW_LIST_SUGGEST_SUBMIT = "view_suggest_submit_list";
    private static final String CONSTANT_VIEW_SUGGEST_SUBMIT = "view_suggest_submit";
    private static final String CONSTANT_VIEW_REPORT = "view_report";
    private static final String CONSTANT_VIEW_CREATE_SUGGEST_SUBMIT = "view_create_suggest_submit";
    private static final String CONSTANT_SUGGEST = "suggest";

    // session filter
    private static final String SESSION_SEARCH_FIELDS = "search_fields";

    // properties
    private int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 50 );
    private Plugin _plugin;
    private int _nIdSuggestSubmitStatePublish = SuggestUtils.CONSTANT_ID_NULL;
    private UrlItem _urlSuggestXpageHome;
    private int _nNumberShownCharacters = SuggestUtils.CONSTANT_ID_NULL;
    private ISuggestSubmitService _suggestSubmitService = SuggestSubmitService.getService(  );
    private ICommentSubmitService _commentSubmitService = CommentSubmitService.getService(  );

    /**
     * Returns the Suggest XPage result content depending on the request
     * parameters and the current mode.
     *
     * @param request the {@link HttpServletRequest}
     * @param nMode the mode
     * @param plugin {@link Plugin}
     * @return {@link XPage}
     * @throws UserNotSignedException {@link UserNotSignedException}
     * @throws SiteMessageException {@link SiteMessageException}
     */
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin )
        throws UserNotSignedException, SiteMessageException
    {
        XPage page = new XPage(  );
        init( request, plugin );

        if ( request.getParameter( PARAMETER_CLEAR_FILTER ) != null )
        {
            //clear all filter in session
            clearSessionFilter( request.getSession(  ) );
        }

        String strAction = request.getParameter( PARAMETER_ACTION );

        if ( ACTION_VIEW_SUGGEST_LIST.equals( strAction ) )
        {
            page = getViewSuggestList( page, nMode, request );
        }
        else if ( ACTION_VIEW_SUGGEST_SUBMIT_LIST.equals( strAction ) )
        {
            page = getViewSuggestSubmitList( page, nMode, request );
        }

        else if ( ACTION_VIEW_SUGGEST_SUBMIT.equals( strAction ) )
        {
            page = getViewSuggestSubmit( page, nMode, request );
        }

        else if ( ACTION_CREATE_SUGGEST_SUBMIT.equals( strAction ) )
        {
            page = getViewCreateSuggestSubmit( page, nMode, request );
        }
        else if ( ACTION_CREATE_REPORT.equals( strAction ) )
        {
            page = getViewCreateReport( page, nMode, request );
        }
        else if ( ACTION_CREATE_SUB_COMMENT.equals( strAction ) )
        {
            page = getViewCreateSubComment( page, nMode, request );
        }

        else if ( ACTION_DO_CREATE_SUGGEST_SUBMIT.equals( strAction ) )
        {
            doCreateSuggestSubmit( page, nMode, request );
            page = getViewSuggestSubmitList( page, nMode, request );
        }
        else if ( ACTION_DO_CREATE_COMMENT.equals( strAction ) )
        {
            doCreateComment( page, nMode, request );
            page = getViewSuggestSubmit( page, nMode, request );
        }
        else if ( ACTION_DO_CREATE_REPORT.equals( strAction ) )
        {
            doReport( page, nMode, request );
            page = getViewSuggestSubmit( page, nMode, request );
        }

        else if ( ACTION_DO_VOTE.equals( strAction ) )
        {
            doVote( page, nMode, request );

            String strView = request.getParameter( PARAMETER_VIEW );

            if ( ( strView != null ) && strView.equals( CONSTANT_VIEW_SUGGEST_SUBMIT ) )
            {
                page = getViewSuggestSubmit( page, nMode, request );
            }
            else
            {
                page = getViewSuggestSubmitList( page, nMode, request );
            }
        }
        else if ( ACTION_SUBSCRIBE_SUGGEST.equals( strAction ) )
        {
            doSubscribeSuggest( request );
        }
        else if ( ACTION_UNSUBSCRIBE_SUGGEST.equals( strAction ) )
        {
            doUnsubscribeSuggest( request );
        }
        else
        {
            if ( ( request.getParameter( PARAMETER_ID_SUGGEST ) != null ) ||
                    ( SuggestService.getInstance().getIdDefaultSuggest(  ) != SuggestUtils.CONSTANT_ID_NULL ) )
            {
                page = getViewSuggestSubmitList( page, nMode, request );
            }
            else
            {
                page = getViewSuggestList( page, nMode, request );
            }
        }

        return page;
    }

    /**
     * Display Suggest List
     * @param request the {@link HttpServletRequest}
     * @param nMode the mode
     * @param page {@link XPage}
     * @return {@link XPage}
     * @throws UserNotSignedException {@link UserNotSignedException}
     * @throws SiteMessageException {@link SiteMessageException}
     */
    public XPage getViewSuggestList( XPage page, int nMode, HttpServletRequest request )
        throws UserNotSignedException, SiteMessageException
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        LuteceUser luteceUserConnected = SecurityService.getInstance(  ).getRegisteredUser( request );

        String strContentSuggest = EMPTY_STRING;
        page.setTitle( I18nService.getLocalizedString( PROPERTY_XPAGE_PAGETITLE, request.getLocale(  ) ) );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_XPAGE_PATHLABEL, request.getLocale(  ) ) );

        // show the suggests list
        String strCurrentPageIndexSuggest = "";
        strCurrentPageIndexSuggest = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                strCurrentPageIndexSuggest );

        int nItemsPerPageSuggest = _nDefaultItemsPerPage;
        nItemsPerPageSuggest = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, nItemsPerPageSuggest,
                _nDefaultItemsPerPage );

        strContentSuggest = getHtmlListSuggest( request.getLocale(  ), _plugin, strCurrentPageIndexSuggest, nItemsPerPageSuggest,
                getNewUrlItemPage(  ), luteceUserConnected );

        model.put( MARK_CONTENT_SUGGEST, strContentSuggest );

        page.setContent( strContentSuggest );

        return page;
    }

    /**
     * Display SuggestSubmit List
     * @param request the {@link HttpServletRequest}
     * @param nMode the mode
     * @param page {@link XPage}
     * @return {@link XPage}
     * @throws UserNotSignedException {@link UserNotSignedException}
     * @throws SiteMessageException {@link SiteMessageException}
     */
    public XPage getViewSuggestSubmitList( XPage page, int nMode, HttpServletRequest request )
        throws UserNotSignedException, SiteMessageException
    {
        String strIdSuggest = request.getParameter( PARAMETER_ID_SUGGEST );

        int nIdSuggest = SuggestUtils.getIntegerParameter( strIdSuggest );
        Suggest suggest = SuggestHome.findByPrimaryKey( nIdSuggest, _plugin );
        LuteceUser luteceUserConnected = SecurityService.getInstance(  ).getRegisteredUser( request );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_VIEW, CONSTANT_VIEW_LIST_SUGGEST_SUBMIT );

        if ( suggest == null )
        {
            suggest = SuggestHome.findByPrimaryKey(SuggestService.getInstance(). getIdDefaultSuggest(  ), _plugin );
        }

        //testAuthorizationAccess
        testUserAuthorizationAccess( suggest, request, luteceUserConnected );

        if ( luteceUserConnected != null )
        {
            model.put( MARK_LUTECE_USER_CONNECTED, luteceUserConnected );

            String strIdFilterCategory = request.getParameter( PARAMETER_ID_FILTER_CATEGORY_SUGGEST );
            int nIdCategory = -1;

            if ( StringUtils.isNotEmpty( strIdFilterCategory ) && StringUtils.isNumeric( strIdFilterCategory ) )
            {
                nIdCategory = Integer.parseInt( strIdFilterCategory );
            }

            if ( nIdCategory > 0 )
            {
                model.put( MARK_USER_SUBSCRIBED,
                    SuggestSubscriptionProviderService.getService(  )
                                                       .hasUserSubscribedToSuggestCategory( luteceUserConnected,
                        nIdCategory ) );
            }
            else
            {
                model.put( MARK_USER_SUBSCRIBED,
                    SuggestSubscriptionProviderService.getService(  )
                                                       .hasUserSubscribedToSuggest( luteceUserConnected, nIdSuggest ) );
            }
        }

        UrlItem urlSuggestXpage = getNewUrlItemPage(  );
        urlSuggestXpage.addParameter( PARAMETER_ACTION, CONSTANT_VIEW_LIST_SUGGEST_SUBMIT );
        urlSuggestXpage.addParameter( PARAMETER_ID_SUGGEST, nIdSuggest );

        SearchFields searchFields = getSearchFields( request );
        addSuggestPageFrameset( getHtmlListSuggestSubmit( request.getLocale(  ), _plugin, suggest, searchFields, urlSuggestXpage,
                luteceUserConnected ), request, page, suggest, model, searchFields, luteceUserConnected );

        return page;
    }

    /**
     * Display view SuggestSubmit
     * @param request the {@link HttpServletRequest}
     * @param nMode the mode
     * @param page {@link XPage}
     * @return {@link XPage}
     * @throws UserNotSignedException {@link UserNotSignedException}
     * @throws SiteMessageException {@link SiteMessageException}
     */
    public XPage getViewSuggestSubmit( XPage page, int nMode, HttpServletRequest request )
        throws UserNotSignedException, SiteMessageException
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        LuteceUser luteceUserConnected = SecurityService.getInstance(  ).getRegisteredUser( request );
        String strIdSubmitSuggest = request.getParameter( PARAMETER_ID_SUBMIT_SUGGEST );
        int nIdSubmitSuggest = SuggestUtils.getIntegerParameter( strIdSubmitSuggest );
        SuggestSubmit suggestSubmit = _suggestSubmitService.findByPrimaryKey( nIdSubmitSuggest, true, _plugin );
        suggestSubmit.setSuggest( SuggestHome.findByPrimaryKey( suggestSubmit.getSuggest(  ).getIdSuggest(  ), _plugin ) );

        //testAuthorizationAccess
        testUserAuthorizationAccess( suggestSubmit.getSuggest(  ), request, luteceUserConnected );

        model.put( MARK_VIEW, CONSTANT_VIEW_SUGGEST_SUBMIT );

        SearchFields searchFields = getSearchFields( request );
        addSuggestPageFrameset( getHtmlSuggestSubmitDetail( request, nMode, _plugin, suggestSubmit, luteceUserConnected ),
            request, page, suggestSubmit.getSuggest(  ), model, searchFields, luteceUserConnected );

        return page;
    }

    /**
     * Display create SuggestSubmit Form
     * @param request the {@link HttpServletRequest}
     * @param nMode the mode
     * @param page {@link XPage}
     * @return {@link XPage}
     * @throws UserNotSignedException {@link UserNotSignedException}
     * @throws SiteMessageException {@link SiteMessageException}
     */
    public XPage getViewCreateSuggestSubmit( XPage page, int nMode, HttpServletRequest request )
        throws UserNotSignedException, SiteMessageException
    {
       
    	
    	LuteceUser luteceUserConnected = SecurityService.getInstance(  ).getRegisteredUser( request );
    	String strIdSuggest = request.getParameter( PARAMETER_ID_SUGGEST );
        int nIdSuggest = SuggestUtils.getIntegerParameter( strIdSuggest );
        Suggest suggest = SuggestHome.findByPrimaryKey( nIdSuggest, _plugin );

        if ( suggest.isActiveSuggestSubmitAuthentification(  ) && SecurityService.isAuthenticationEnable(  ) )
        {
            luteceUserConnected = SecurityService.getInstance(  ).getRemoteUser( request );

            if ( luteceUserConnected == null )
            {
                throw new UserNotSignedException(  );
            }

            //testAuthorizationAccess
            testUserAuthorizationAccess( suggest, request, luteceUserConnected );
        }

        
        
        
        
    	Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_VIEW, CONSTANT_VIEW_CREATE_SUGGEST_SUBMIT );
        
        SearchFields searchFields = getSearchFields( request );
        addSuggestPageFrameset( getHtmlForm( request, nMode, _plugin, suggest, searchFields.getIdFilterCategory(  ) ),
            request, page, suggest, model, searchFields, luteceUserConnected );

        return page;
    }
    
    /**
     * Display create sub comment
     * @param request the {@link HttpServletRequest}
     * @param nMode the mode
     * @param page {@link XPage}
     * @return {@link XPage}
     * @throws UserNotSignedException {@link UserNotSignedException}
     * @throws SiteMessageException {@link SiteMessageException}
     */
    public XPage getViewCreateSubComment( XPage page, int nMode, HttpServletRequest request )
        throws UserNotSignedException, SiteMessageException
    {
    	
    	
    	String strIdParentComment = request.getParameter( PARAMETER_COMMENT_ID_PARENT );
        int nIdParentComment = SuggestUtils.getIntegerParameter( strIdParentComment );
        LuteceUser luteceUserConnected = SecurityService.getInstance(  ).getRegisteredUser( request );
        
        String strIdSuggest = request.getParameter( PARAMETER_ID_SUGGEST );
        int nIdSuggest = SuggestUtils.getIntegerParameter( strIdSuggest );
        Suggest suggest = SuggestHome.findByPrimaryKey( nIdSuggest, _plugin );
        
        if ( suggest.isActiveCommentAuthentification(  ) && SecurityService.isAuthenticationEnable(  ) )
        {
            luteceUserConnected = SecurityService.getInstance(  ).getRemoteUser( request );

            if ( luteceUserConnected == null )
            {
                throw new UserNotSignedException(  );
            }

            //testAuthorizationAccess
            testUserAuthorizationAccess( suggest, request, luteceUserConnected );
        }
        
        Map<String, Object> model = new HashMap<String, Object>(  );
        
        CaptchaSecurityService captchaSecurityService = new CaptchaSecurityService(  );

        
        if ( suggest!=null  && suggest.isActiveCaptcha(  ) && PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) )
        {
            model.put( MARK_JCAPTCHA, captchaSecurityService.getHtmlCode(  ) );
        }
        
        model.put( MARK_LUTECE_USER_CONNECTED, luteceUserConnected );
        model.put(MARK_ID_SUGGEST, strIdSuggest);
        CommentSubmit commentSubmit = _commentSubmitService.findByPrimaryKey(nIdParentComment, _plugin);
        model.put(MARK_COMMENT_SUBMIT, commentSubmit);
        model.put( MARK_DISABLE_NEW_COMMENT_SUBMIT, suggest.isDisableNewComment(  ) );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_SUGGEST_SUB_COMMENT, request.getLocale(  ), model );
        page.setTitle( I18nService.getLocalizedString( PROPERTY_XPAGE_PAGETITLE, request.getLocale(  ) ) );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_XPAGE_PATHLABEL, request.getLocale(  ) ) );
        page.setContent( template.getHtml(  ) );
        
        return page;
    }

    /**
     * Display create Report form
     * @param request the {@link HttpServletRequest}
     * @param nMode the mode
     * @param page {@link XPage}
     * @return {@link XPage}
     * @throws UserNotSignedException {@link UserNotSignedException}
     * @throws SiteMessageException {@link SiteMessageException}
     */
    public XPage getViewCreateReport( XPage page, int nMode, HttpServletRequest request )
        throws UserNotSignedException, SiteMessageException
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        LuteceUser luteceUserConnected = SecurityService.getInstance(  ).getRegisteredUser( request );
        String strIdSubmitSuggest = request.getParameter( PARAMETER_ID_SUBMIT_SUGGEST );
        int nIdSubmitSuggest = SuggestUtils.getIntegerParameter( strIdSubmitSuggest );
        SuggestSubmit suggestSubmit = _suggestSubmitService.findByPrimaryKey( nIdSubmitSuggest, true, _plugin );
        suggestSubmit.setSuggest( SuggestHome.findByPrimaryKey( suggestSubmit.getSuggest(  ).getIdSuggest(  ), _plugin ) );
        model.put( MARK_VIEW, CONSTANT_VIEW_REPORT );

        SearchFields searchFields = getSearchFields( request );
        addSuggestPageFrameset( getHtmlReported( request, nMode, _plugin, suggestSubmit ), request, page,
            suggestSubmit.getSuggest(  ), model, searchFields, luteceUserConnected );

        return page;
    }

    /**
     * Perform Action create Suggest Submit
     * @param request the {@link HttpServletRequest}
     * @param nMode the mode
     * @param page {@link XPage}
     * @throws UserNotSignedException {@link UserNotSignedException}
     * @throws SiteMessageException {@link SiteMessageException}
     */
    public void doCreateSuggestSubmit( XPage page, int nMode, HttpServletRequest request )
        throws UserNotSignedException, SiteMessageException
    {
        String strIdSuggest = request.getParameter( PARAMETER_ID_SUGGEST );

        int nIdSuggest = SuggestUtils.getIntegerParameter( strIdSuggest );
        LuteceUser luteceUserConnected = null;

        Suggest suggest = SuggestHome.findByPrimaryKey( nIdSuggest, _plugin );

        if ( suggest.isActiveSuggestSubmitAuthentification(  ) && SecurityService.isAuthenticationEnable(  ) )
        {
            luteceUserConnected = SecurityService.getInstance(  ).getRemoteUser( request );

            if ( luteceUserConnected == null )
            {
                throw new UserNotSignedException(  );
            }

            //testAuthorizationAccess
            testUserAuthorizationAccess( suggest, request, luteceUserConnected );
        }

        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY_SUGGEST );
        String strIdType = request.getParameter( PARAMETER_ID_TYPE_SUGGEST );
        String strMessage = MESSAGE_NEW_SUGGEST_SUBMIT;
        int nIdCategory = SuggestUtils.getIntegerParameter( strIdCategory );
        int nIdType = SuggestUtils.getIntegerParameter( strIdType );
        String strTermsOfUse = request.getParameter( PARAMETER_TERMS_OF_USE );

        //Check if  terms of used is selected 
        if ( suggest.isEnableTermsOfUse(  ) && ( strTermsOfUse == null ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR_MUST_SELECTED_TERMS_OF_USE, SiteMessage.TYPE_STOP );
        }

        //Check if a category is selected (in the case or the suggest has some categories)
        if ( !suggest.getCategories(  ).isEmpty(  ) )
        {
            if ( ( strIdCategory == null ) || strIdCategory.equals( Integer.toString( SuggestUtils.CONSTANT_ID_NULL ) ) )
            {
                SiteMessageService.setMessage( request, MESSAGE_ERROR_NO_CATEGORY, SiteMessage.TYPE_STOP );
            }
        }

        //Check if a type is selected (in the case or the suggest has some type)
        if ( !suggest.getSuggestSubmitTypes(  ).isEmpty(  ) )
        {
            if ( ( strIdType == null ) || strIdType.equals( Integer.toString( SuggestUtils.CONSTANT_ID_NULL ) ) )
            {
                SiteMessageService.setMessage( request, MESSAGE_ERROR_NO_SUGGEST_SUBMIT_TYPE_SELECTED,
                    SiteMessage.TYPE_STOP );
            }
        }

        SuggestSubmit suggestSubmit = doInsertSuggestSubmit( request, nMode, _plugin, suggest, nIdCategory, nIdType,
                luteceUserConnected );

        if ( suggest.isDisableNewSuggestSubmit(  ) )
        {
            strMessage = MESSAGE_NEW_SUGGEST_SUBMIT_DISABLE;
        }

        if ( suggest.isEnableMailNewSuggestSubmit(  ) && ( suggest.getIdMailingListSuggestSubmit(  ) != SuggestUtils.CONSTANT_ID_NULL ) )
        {
            SuggestUtils.sendNotificationNewSuggestSubmit( suggest, suggestSubmit, request.getLocale(  ), request );
        }

        Map<String, Object> parameters = new HashMap<String, Object>(  );
        parameters.put( PARAMETER_ID_SUGGEST, nIdSuggest );
        parameters.put( PARAMETER_ACTION, CONSTANT_VIEW_LIST_SUGGEST_SUBMIT );

        if ( !StringUtils.isEmpty( suggest.getConfirmationMessage(  ) ) )
        {
            Object[] args = { ( suggest.getConfirmationMessage(  ) == null ) ? "" : suggest.getConfirmationMessage(  ) };
            SiteMessageService.setMessage( request, strMessage, args, null, getNewUrlItemPage(  ).getUrl(  ), null,
                SiteMessage.TYPE_INFO, parameters );
        }
    }

    /**
     * Do subscribe to a suggest or a suggest submit. If the parameter
     * {@link #PARAMETER_ID_SUBMIT_SUGGEST} has a value, then subscribe to the suggest
     * submit, otherwise subscribe to the suggest
     * @param request The request
     * @throws SiteMessageException If the suggest or the suggest submit does not
     *             exist
     */
    public void doSubscribeSuggest( HttpServletRequest request )
        throws SiteMessageException
    {
        LuteceUser luteceUserConnected = SecurityService.getInstance(  ).getRegisteredUser( request );
        String strIdSubmitSuggest = request.getParameter( PARAMETER_ID_SUBMIT_SUGGEST );

        UrlItem urlItem = new UrlItem( request.getRequestURL(  ).toString(  ) );
        urlItem.addParameter( PARAMETER_PAGE, CONSTANT_SUGGEST );
        urlItem.addParameter( PARAMETER_ID_SUGGEST, request.getParameter( PARAMETER_ID_SUGGEST ) );

        if ( StringUtils.isNotEmpty( strIdSubmitSuggest ) )
        {
            int nIdSubmitSuggest = SuggestUtils.getIntegerParameter( strIdSubmitSuggest );
            SuggestSubmit suggestSubmit = _suggestSubmitService.findByPrimaryKey( nIdSubmitSuggest, true, _plugin );

            if ( suggestSubmit == null )
            {
                SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );

                return;
            }

            SuggestSubscriptionProviderService.getService(  )
                                               .createSuggestSubmitSubscription( luteceUserConnected, nIdSubmitSuggest );

            urlItem.addParameter( PARAMETER_ID_SUBMIT_SUGGEST, strIdSubmitSuggest );
            urlItem.addParameter( PARAMETER_ACTION, ACTION_VIEW_SUGGEST_SUBMIT );
        }
        else
        {
            String strIdFilterCategory = request.getParameter( PARAMETER_ID_FILTER_CATEGORY_SUGGEST );
            int nIdCategory = -1;

            if ( StringUtils.isNotEmpty( strIdFilterCategory ) && StringUtils.isNumeric( strIdFilterCategory ) )
            {
                nIdCategory = Integer.parseInt( strIdFilterCategory );
            }

            if ( nIdCategory > 0 )
            {
                if ( CategoryHome.findByPrimaryKey( nIdCategory, _plugin ) == null )
                {
                    SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );

                    return;
                }

                SuggestSubscriptionProviderService.getService(  )
                                                   .createSuggestCategorySubscription( luteceUserConnected, nIdCategory );
                urlItem.addParameter( PARAMETER_ID_FILTER_CATEGORY_SUGGEST, strIdFilterCategory );
            }
            else
            {
                String strIdSuggest = request.getParameter( PARAMETER_ID_SUGGEST );
                int nIdSuggest = SuggestUtils.getIntegerParameter( strIdSuggest );

                if ( SuggestHome.findByPrimaryKey( nIdSuggest, _plugin ) == null )
                {
                    SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );

                    return;
                }

                SuggestSubscriptionProviderService.getService(  ).createSuggestSubscription( luteceUserConnected, nIdSuggest );
            }

            urlItem.addParameter( PARAMETER_ACTION, ACTION_VIEW_SUGGEST_SUBMIT_LIST );
        }

        try
        {
            LocalVariables.getResponse(  ).sendRedirect( urlItem.getUrl(  ) );
        }
        catch ( IOException e )
        {
            AppLogService.error( e.getMessage(  ), e );
            SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
        }
    }

    /**
     * Do unsubscribe to a suggest or a suggest submit. If the parameter
     * {@link #PARAMETER_ID_SUBMIT_SUGGEST} has a value, then unsubscribe to the
     * suggest submit, otherwise unsubscribe to the suggest
     * @param request The request
     * @throws SiteMessageException If the suggest or the suggest submit does not
     *             exist
     */
    public void doUnsubscribeSuggest( HttpServletRequest request )
        throws SiteMessageException
    {
        LuteceUser luteceUserConnected = SecurityService.getInstance(  ).getRegisteredUser( request );
        String strIdSubmitSuggest = request.getParameter( PARAMETER_ID_SUBMIT_SUGGEST );

        UrlItem urlItem = new UrlItem( request.getRequestURL(  ).toString(  ) );
        urlItem.addParameter( PARAMETER_PAGE, CONSTANT_SUGGEST );
        urlItem.addParameter( PARAMETER_ID_SUGGEST, request.getParameter( PARAMETER_ID_SUGGEST ) );

        if ( StringUtils.isNotEmpty( strIdSubmitSuggest ) )
        {
            int nIdSubmitSuggest = SuggestUtils.getIntegerParameter( strIdSubmitSuggest );
            SuggestSubmit suggestSubmit = _suggestSubmitService.findByPrimaryKey( nIdSubmitSuggest, true, _plugin );

            if ( suggestSubmit == null )
            {
                SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );

                return;
            }

            SuggestSubscriptionProviderService.getService(  )
                                               .removeSuggestSubmitSubscription( luteceUserConnected, nIdSubmitSuggest );

            urlItem.addParameter( PARAMETER_ID_SUBMIT_SUGGEST, strIdSubmitSuggest );
            urlItem.addParameter( PARAMETER_ACTION, ACTION_VIEW_SUGGEST_SUBMIT );
        }
        else
        {
            String strIdFilterCategory = request.getParameter( PARAMETER_ID_FILTER_CATEGORY_SUGGEST );
            int nIdCategory = -1;

            if ( StringUtils.isNotEmpty( strIdFilterCategory ) && StringUtils.isNumeric( strIdFilterCategory ) )
            {
                nIdCategory = Integer.parseInt( strIdFilterCategory );
            }

            if ( nIdCategory > 0 )
            {
                if ( CategoryHome.findByPrimaryKey( nIdCategory, _plugin ) == null )
                {
                    SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );

                    return;
                }

                SuggestSubscriptionProviderService.getService(  )
                                                   .createSuggestCategorySubscription( luteceUserConnected, nIdCategory );
                urlItem.addParameter( PARAMETER_ID_FILTER_CATEGORY_SUGGEST, strIdFilterCategory );
            }
            else
            {
                String strIdSuggest = request.getParameter( PARAMETER_ID_SUGGEST );
                int nIdSuggest = SuggestUtils.getIntegerParameter( strIdSuggest );
                Suggest suggest = SuggestHome.findByPrimaryKey( nIdSuggest, _plugin );

                if ( suggest == null )
                {
                    SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );

                    return;
                }

                SuggestSubscriptionProviderService.getService(  ).removeSuggestSubscription( luteceUserConnected, nIdSuggest );
            }

            urlItem.addParameter( PARAMETER_ACTION, ACTION_VIEW_SUGGEST_SUBMIT_LIST );
        }

        try
        {
            LocalVariables.getResponse(  ).sendRedirect( urlItem.getUrl(  ) );
        }
        catch ( IOException e )
        {
            AppLogService.error( e.getMessage(  ), e );
            SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
        }
    }

    /**
     * Perform Action create Comment
     * @param request the {@link HttpServletRequest}
     * @param nMode the mode
     * @param page {@link XPage}
     * @throws UserNotSignedException {@link UserNotSignedException}
     * @throws SiteMessageException {@link SiteMessageException}
     */
    public void doCreateComment( XPage page, int nMode, HttpServletRequest request )
        throws UserNotSignedException, SiteMessageException
    {
        String strIdSubmitSuggest = request.getParameter( PARAMETER_ID_SUBMIT_SUGGEST );
        int nIdSubmitSuggest = SuggestUtils.getIntegerParameter( strIdSubmitSuggest );
        SuggestSubmit suggestSubmit = _suggestSubmitService.findByPrimaryKey( nIdSubmitSuggest, true, _plugin );
        Suggest suggest = null;

        if ( suggestSubmit != null )
        {
            suggest = SuggestHome.findByPrimaryKey( suggestSubmit.getSuggest(  ).getIdSuggest(  ), _plugin );
            suggestSubmit.setSuggest( suggest );
        }

        LuteceUser luteceUserConnected = null;

        if ( ( suggest == null ) || ( suggestSubmit == null ) || !suggest.isAuthorizedComment(  ) ||
                suggestSubmit.isDisableComment(  ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );

            return;
        }

        if ( suggest.isActiveCommentAuthentification(  ) && SecurityService.isAuthenticationEnable(  ) )
        {
            luteceUserConnected = SecurityService.getInstance(  ).getRemoteUser( request );

            if ( luteceUserConnected == null )
            {
                throw new UserNotSignedException(  );
            }

            //testAuthorizationAccess
            testUserAuthorizationAccess( suggest, request, luteceUserConnected );
        }

        String strCommentValueSuggest = request.getParameter( PARAMETER_COMMENT_VALUE_SUGGEST );
        String strMessage = MESSAGE_NEW_COMMENT_SUBMIT;
        String strIdParentComment = request.getParameter( PARAMETER_COMMENT_ID_PARENT );
        int nIdParentComment = SubmitFilter.ID_PARENT_NULL;

        if ( ( strIdParentComment != null ) && ( !strIdParentComment.trim(  ).equals( EMPTY_STRING ) ) )
        {
            nIdParentComment = SuggestUtils.getIntegerParameter( strIdParentComment );
        }

        if ( ( strCommentValueSuggest == null ) || strCommentValueSuggest.trim(  ).equals( EMPTY_STRING ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_MANDATORY_COMMENT, SiteMessage.TYPE_STOP );
        }

        if ( suggest.isActiveCaptcha(  ) && PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) )
        {
            CaptchaSecurityService captchaSecurityService = new CaptchaSecurityService(  );

            if ( !captchaSecurityService.validate( request ) )
            {
                SiteMessageService.setMessage( request, MESSAGE_CAPTCHA_ERROR, SiteMessage.TYPE_STOP );
            }
        }

        CommentSubmit commentSubmit = doInsertComment( request, suggestSubmit, strCommentValueSuggest, _plugin,
                luteceUserConnected, nIdParentComment );

        if ( suggest.isEnableMailNewCommentSubmit(  ) &&
                ( suggest.getIdMailingListSuggestSubmit(  ) != SuggestUtils.CONSTANT_ID_NULL ) )
        {
            SuggestUtils.sendNotificationNewCommentSubmit( suggest, commentSubmit, request.getLocale(  ), request );
            strMessage = MESSAGE_NEW_COMMENT_SUBMIT_DISABLE;
        }

        if ( !StringUtils.isEmpty( suggest.getConfirmationMessage(  ) ) )
        {
            Map<String, Object> parameters = new HashMap<String, Object>(  );

            parameters.put( PARAMETER_ID_SUBMIT_SUGGEST, nIdSubmitSuggest );
            parameters.put( PARAMETER_ID_SUGGEST, suggest.getIdSuggest(  ) );
            parameters.put( PARAMETER_COMMENT_SUGGEST, CONSTANTE_PARAMETER_TRUE_VALUE );
            parameters.put( PARAMETER_ACTION, CONSTANT_VIEW_SUGGEST_SUBMIT );

            Object[] args = { ( suggest.getConfirmationMessage(  ) == null ) ? "" : suggest.getConfirmationMessage(  ) };
            SiteMessageService.setMessage( request, strMessage, args, null, getNewUrlItemPage(  ).getUrl(  ), null,
                SiteMessage.TYPE_INFO, parameters );
        }
    }

    /**
     *
     * Perform Action Report
     * @param request the {@link HttpServletRequest}
     * @param nMode the mode
     * @param page {@link XPage}
     * @throws UserNotSignedException {@link UserNotSignedException}
     * @throws SiteMessageException {@link SiteMessageException}
     */
    public void doReport( XPage page, int nMode, HttpServletRequest request )
        throws UserNotSignedException, SiteMessageException
    {
        String strIdSubmitSuggest = request.getParameter( PARAMETER_ID_SUBMIT_SUGGEST );
        int nIdSubmitSuggest = SuggestUtils.getIntegerParameter( strIdSubmitSuggest );

        SuggestSubmit suggestSubmit = _suggestSubmitService.findByPrimaryKey( nIdSubmitSuggest, true, _plugin );
        Suggest suggest = null;

        if ( suggestSubmit != null )
        {
            suggest = SuggestHome.findByPrimaryKey( suggestSubmit.getSuggest(  ).getIdSuggest(  ), _plugin );
            suggestSubmit.setSuggest( suggest );
        }

        String strReportedValue = request.getParameter( PARAMETER_REPORTED_VALUE );

        if ( ( strReportedValue == null ) || strReportedValue.trim(  ).equals( EMPTY_STRING ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_MANDATORY_REPORTED, SiteMessage.TYPE_STOP );
        }

        if ( ( suggestSubmit == null ) || ( suggest == null ) || !suggest.isEnableReports(  ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );

            return;
        }

        LuteceUser luteceUserConnected = null;

        if ( suggest.isActiveCommentAuthentification(  ) && SecurityService.isAuthenticationEnable(  ) )
        {
            luteceUserConnected = SecurityService.getInstance(  ).getRemoteUser( request );

            if ( luteceUserConnected == null )
            {
                throw new UserNotSignedException(  );
            }

            //testAuthorizationAccess
            testUserAuthorizationAccess( suggest, request, luteceUserConnected );
        }

        SuggestUtils.doReportSuggestSubmit( suggestSubmit, _plugin );

        ReportedMessage reportedMessage = new ReportedMessage(  );
        reportedMessage.setSuggestSubmit( suggestSubmit );
        reportedMessage.setValue( strReportedValue );

        ReportedMessageHome.create( reportedMessage, _plugin );

        if ( suggest.isEnableMailNewReportedSubmit(  ) &&
                ( suggest.getIdMailingListSuggestSubmit(  ) != SuggestUtils.CONSTANT_ID_NULL ) )
        {
            SuggestUtils.sendNotificationNewReportedMessage( suggest, reportedMessage, request.getLocale(  ), request );
        }

        Map<String, Object> parameters = new HashMap<String, Object>(  );
        parameters.put( PARAMETER_ID_SUBMIT_SUGGEST, nIdSubmitSuggest );
        parameters.put( PARAMETER_ID_SUGGEST, suggest.getIdSuggest(  ) );
        parameters.put( PARAMETER_ACTION, CONSTANT_VIEW_SUGGEST_SUBMIT );

        UrlItem urlItemPage = getNewUrlItemPage(  );
        urlItemPage.setAnchor( ANCHOR_SUGGEST_SUBMIT + nIdSubmitSuggest );

        SiteMessageService.setMessage( request, MESSAGE_NEW_REPORTED_SUBMIT, null, null, urlItemPage.getUrl(  ), null,
            SiteMessage.TYPE_INFO, parameters );
    }

    /**
     * Perform Action Vote
     * @param request the {@link HttpServletRequest}
     * @param nMode the mode
     * @param page {@link XPage}
     * @throws UserNotSignedException {@link UserNotSignedException}
     * @throws SiteMessageException {@link SiteMessageException}
     */
    public void doVote( XPage page, int nMode, HttpServletRequest request )
        throws UserNotSignedException, SiteMessageException
    {
        String strVote = request.getParameter( PARAMETER_VOTE_SUGGEST );
        String strIdSubmitSuggest = request.getParameter( PARAMETER_ID_SUBMIT_SUGGEST );
        int nIdSubmitSuggest = SuggestUtils.getIntegerParameter( strIdSubmitSuggest );
        SuggestSubmit suggestSubmit = _suggestSubmitService.findByPrimaryKey( nIdSubmitSuggest, true, _plugin );
        Suggest suggest = SuggestHome.findByPrimaryKey( suggestSubmit.getSuggest(  ).getIdSuggest(  ), _plugin );
        suggestSubmit.setSuggest( suggest );

        LuteceUser luteceUserConnected = null;

        if ( suggest.isLimitNumberVote(  ) )
        {
            if ( suggest.isActiveVoteAuthentification(  ) && SecurityService.isAuthenticationEnable(  ) )
            {
                luteceUserConnected = SecurityService.getInstance(  ).getRemoteUser( request );

                if ( luteceUserConnected == null )
                {
                    throw new UserNotSignedException(  );
                }
                else if ( ( suggest.getRole(  ) != null ) &&
                        !SecurityService.getInstance(  ).isUserInRole( request, suggest.getRole(  ) ) )
                {
                    SiteMessageService.setMessage( request, MESSAGE_ACCESS_DENIED, SiteMessage.TYPE_STOP );
                }

                if ( VoteHome.getUserNumberVoteOnSuggestSubmit( nIdSubmitSuggest, luteceUserConnected.getName(  ), _plugin ) == 0 )
                {
                    doVote( strVote, nIdSubmitSuggest, _plugin, luteceUserConnected.getName(  ) );
                }
            }
            else if ( request.getSession(  ).getAttribute( EMPTY_STRING + nIdSubmitSuggest ) == null )
            {
                doVote( strVote, nIdSubmitSuggest, _plugin, null );
                request.getSession(  ).setAttribute( EMPTY_STRING + nIdSubmitSuggest, PARAMETER_VOTED );
            }
        }
        else
        {
            doVote( strVote, nIdSubmitSuggest, _plugin, null );
        }
    }

    /**
     * Increment score
     *
     * @param strVote
     *            the value to add at score
     * @param nIdSubmitSuggest
     *            the id of the suggest submit
     * @param plugin
     *            the plugin
     * @param strUserKey
     *            the user key
     */
    private void doVote( String strVote, int nIdSubmitSuggest, Plugin plugin, String strUserKey )
    {
        // Increment vote
        int nScore;

        if ( ( strVote != null ) && strVote.equals( "-2" ) )
        {
            nScore = Integer.parseInt( strVote );
            SuggestUtils.doVoteSuggestSubmit( nIdSubmitSuggest, nScore, strUserKey, plugin );
        }
        else if ( ( strVote != null ) && strVote.equals( "-1" ) )
        {
            nScore = Integer.parseInt( strVote );
            SuggestUtils.doVoteSuggestSubmit( nIdSubmitSuggest, nScore, strUserKey, plugin );
        }
        else if ( ( strVote != null ) && strVote.equals( "1" ) )
        {
            nScore = Integer.parseInt( strVote );
            SuggestUtils.doVoteSuggestSubmit( nIdSubmitSuggest, nScore, strUserKey, plugin );
        }
        else if ( ( strVote != null ) && strVote.equals( "2" ) )
        {
            nScore = Integer.parseInt( strVote );
            SuggestUtils.doVoteSuggestSubmit( nIdSubmitSuggest, nScore, strUserKey, plugin );
        }
    }

    /**
     * return the html list of suggest submit
     * @param locale the locale
     * @param plugin the plugin
     * @param suggest the suggest
     * @param searchFields the searchFields
     * @param urlSuggestXPage the url of the Xpage
     * @param luteceUserConnected the lutece UserConnected
     * @return the html list of suggest submit
     * @throws SiteMessageException SiteMessageException
     *             {@link SiteMessageException}
     */
    private String getHtmlListSuggestSubmit( Locale locale, Plugin plugin, Suggest suggest, SearchFields searchFields,
        UrlItem urlSuggestXPage, LuteceUser luteceUserConnected )
        throws SiteMessageException
    {
        Map<String, Object> model = new HashMap<String, Object>(  );

        List<Integer> listIdSuggestSubmit;

        SubmitFilter submitFilter = new SubmitFilter(  );

        // Filter the list
        SuggestUtils.initSubmitFilterByPeriod( submitFilter, searchFields.getIdFilterPeriod(  ) );
        SuggestUtils.initSubmitFilterBySort( submitFilter,
            ( searchFields.getIdSuggestSubmitSort(  ) != SuggestUtils.CONSTANT_ID_NULL )
            ? searchFields.getIdSuggestSubmitSort(  ) : suggest.getIdDefaultSort(  ) );
        //add sort by pinned first
        SuggestUtils.initSubmitFilterBySort( submitFilter, SubmitFilter.SORT_BY_PINNED_FIRST );

        submitFilter.setIdSuggest( suggest.getIdSuggest(  ) );

        submitFilter.setIdSuggestSubmitState( _nIdSuggestSubmitStatePublish );
        submitFilter.setIdCategory( searchFields.getIdFilterCategory(  ) );
        submitFilter.setIdType( searchFields.getIdFilterSuggestSubmitType(  ) );
        submitFilter.setLuteceUserKey(searchFields.getLuteceUserName());
        
        listIdSuggestSubmit = SuggestSearchService.getInstance(  )
                                                .getSearchResults( searchFields.getQuery(  ), submitFilter, plugin );

        if ( suggest.isActiveSuggestSubmitPaginator(  ) && ( suggest.getNumberSuggestSubmitPerPage(  ) > 0 ) )
        {
            Paginator<Integer> paginator = new Paginator<Integer>( listIdSuggestSubmit,
                    suggest.getNumberSuggestSubmitPerPage(  ), urlSuggestXPage.getUrl(  ), PARAMETER_FILTER_PAGE_INDEX,
                    searchFields.getPageIndex(  ) );
            listIdSuggestSubmit = paginator.getPageItems(  );
            model.put( MARK_PAGINATOR, paginator );
        }

        model.put( MARK_SUGGEST, suggest );
        model.put( MARK_LIST_SUGGEST_SUBMIT, getSuggestSubmitDisplayList( listIdSuggestSubmit, suggest, locale, plugin ) );

        model.put( MARK_AUTHORIZED_COMMENT, suggest.isAuthorizedComment(  ) );
        model.put( MARK_AUTHORIZED_VOTE, !suggest.isDisableVote(  ) );
        model.put( MARK_DISPLAY_COMMENT_IN_LIST, suggest.isDisplayCommentInSuggestSubmitList(  ) );
        model.put( MARK_ENABLE_SUGGEST_REPORTS, suggest.isEnableReports(  ) );
        model.put( MARK_ID_SUGGEST, suggest.getIdSuggest(  ) );

        if ( suggest.isAuthorizedComment(  ) )
        {
            model.put( MARK_MAX_AMOUNT_COMMENTS, suggest.getNumberCommentDisplayInSuggestSubmitList(  ) );
            model.put( MARK_MAX_AMOUNT_COMMENTS_CHAR, suggest.getNumberCharCommentDisplayInSuggestSubmitList(  ) );
        }

        model.put( MARK_LUTECE_USER_CONNECTED, luteceUserConnected );
        model.put( MARK_IS_EXTEND_INSTALLED, PortalService.isExtendActivated(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_LIST_SUBMIT_SUGGEST, locale, model );

        return template.getHtml(  );
    }

    /**
     * return the html list of suggest
     *
     * @param locale
     *            the locale
     * @param plugin
     *            the plugin
     * @param strCurrentPageIndexSuggest
     *            the current page index
     * @param nItemsPerPageSuggest
     *            the number of items per page
     * @param urlSuggestXPage
     *            the url of the suggest xpage
     * @param luteceUserConnected
     *            luteceUser
     * @throws SiteMessageException
     *             SiteMessageException
     * @return the html list of suggest
     */
    private String getHtmlListSuggest( Locale locale, Plugin plugin, String strCurrentPageIndexSuggest,
        int nItemsPerPageSuggest, UrlItem urlSuggestXPage, LuteceUser luteceUserConnected )
        throws SiteMessageException
    {
        SuggestFilter filter = new SuggestFilter(  );
        filter.setIdState( Suggest.STATE_ENABLE );

        List<Suggest> listSuggest = SuggestHome.getSuggestList( filter, plugin );
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        Paginator<Suggest> paginator = new Paginator<Suggest>( listSuggest, nItemsPerPageSuggest, urlSuggestXPage.getUrl(  ),
                PARAMETER_PAGE_INDEX, strCurrentPageIndexSuggest );

        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + nItemsPerPageSuggest );

        model.put( MARK_SUGGEST_LIST, paginator.getPageItems(  ) );
        model.put( MARK_LUTECE_USER_CONNECTED, luteceUserConnected );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_XPAGE_LIST_SUGGEST, locale, model );

        return templateList.getHtml(  );
    }

    /**
     * return a collection which contains suggest submit and lutece user associate
     *
     * @param listSuggestSubmit
     *            the list of suggest submit
     * @param suggest
     *            the suggest possessing submits
     * @param strSuggestDetail
     *            the suggest detail
     * @param locale
     *            the locale
     * @throws SiteMessageException
     *             SiteMessageException
     * @return a collection which contains suggest submit and lutece user associate
     */
    private Collection<HashMap> getSuggestSubmitDisplayList( Collection<Integer> listSuggestSubmit, Suggest suggest, Locale locale,
        Plugin plugin ) throws SiteMessageException
    {
        SuggestUserInfo luteceUserInfo;
        SuggestSubmit suggestSubmit;
        Collection<HashMap> listHashSuggest = new ArrayList<HashMap>(  );

        for ( Integer idSuggestSubmit : listSuggestSubmit )
        {
            HashMap<String, Object> modelSuggest = new HashMap<String, Object>(  );

            luteceUserInfo = null;
            suggestSubmit = _suggestSubmitService.findByPrimaryKey( idSuggestSubmit,
                    ( suggest.isAuthorizedComment(  ) && suggest.isDisplayCommentInSuggestSubmitList(  ) ),
                    suggest.getNumberCommentDisplayInSuggestSubmitList(  ), plugin );
            modelSuggest.put( MARK_SUGGEST_SUBMIT, suggestSubmit );

            if ( SecurityService.isAuthenticationEnable(  ) && ( suggestSubmit.getLuteceUserKey(  ) != null ) )
            {
                luteceUserInfo = SuggestUserInfoService.getService(  )
                                                    .findSuggestUserInfoByKey( suggestSubmit.getLuteceUserKey(  ), plugin );
            }

            modelSuggest.put( MARK_LUTECE_USER, luteceUserInfo );
            SuggestUtils.addAvatarToModel(modelSuggest, luteceUserInfo);
            
            
            if ( !suggest.isDisableVote(  ) )
            {
                modelSuggest.put( MARK_SUGGEST_SUBMIT_VOTE_TYPE,
                    getHtmlSuggestSubmitVoteType( suggest, suggestSubmit, CONSTANT_VIEW_LIST_SUGGEST_SUBMIT, locale ) );
            }

            listHashSuggest.add( modelSuggest );
        }

        return listHashSuggest;
    }

    /**
     * return a collection which contains comment and lutece user associate
     *
     * @param listCommentSubmit
     *            the list of comment submit
     * @return a collection which contains comment and lutece user associate
     */
    private Collection<HashMap> getCommentSubmitDisplayList( Collection<CommentSubmit> listCommentSubmit, Plugin plugin )
    {
        Collection<HashMap> listHashComment = new ArrayList<HashMap>(  );
        SuggestUserInfo luteceUserInfo;

        for ( CommentSubmit commentSubmit : listCommentSubmit )
        {
            HashMap<String, Object> modelComment = new HashMap<String, Object>(  );

            luteceUserInfo = null;

            modelComment.put( MARK_COMMENT_SUBMIT, commentSubmit );

            if ( SecurityService.isAuthenticationEnable(  ) && ( commentSubmit.getLuteceUserKey(  ) != null ) )
            {
                luteceUserInfo = SuggestUserInfoService.getService(  )
                                                    .findSuggestUserInfoByKey( commentSubmit.getLuteceUserKey(  ), plugin );
            }

            modelComment.put( MARK_LUTECE_USER, luteceUserInfo );
            SuggestUtils.addAvatarToModel(modelComment, luteceUserInfo);
            
            
            modelComment.put( MARK_LIST_SUB_COMMENT_SUBMIT_SUGGEST,
                ( ( commentSubmit.getComments(  ) != null ) && !commentSubmit.getComments(  ).isEmpty(  ) )
                ? getCommentSubmitDisplayList( commentSubmit.getComments(  ), plugin ) : null );

            listHashComment.add( modelComment );
        }

        return listHashComment;
    }

    /**
     * the html suggest submit detail
     *
     * @param request
     *            the request
     * @param nMode
     *            The current mode.
     * @param plugin
     *            the plugin
     * @param suggestSubmit
     *            the {@link SuggestSubmit}
     *
     *
     * @param luteceUserConnected
     *            the lutece user
     * @return the html suggest submit detail
     * @throws SiteMessageException
     *             SiteMessageException
     */
    private String getHtmlSuggestSubmitDetail( HttpServletRequest request, int nMode, Plugin plugin,
        SuggestSubmit suggestSubmit, LuteceUser luteceUserConnected )
        throws SiteMessageException
    {
        SuggestUserInfo luteceUserInfo = null;
        HashMap<String, Object> model = new HashMap<String, Object>(  );

        if ( ( suggestSubmit == null ) || ( suggestSubmit.getSuggestSubmitState(  ).getNumber(  ) == SuggestSubmit.STATE_DISABLE ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );

            return null;
        }

        // update number view
        suggestSubmit.setNumberView( suggestSubmit.getNumberView(  ) + 1 );
        _suggestSubmitService.update( suggestSubmit, false, plugin );

        if ( SecurityService.isAuthenticationEnable(  ) && ( suggestSubmit.getLuteceUserKey(  ) != null ) )
        {
            luteceUserInfo = SuggestUserInfoService.getService(  )
                                                .findSuggestUserInfoByKey( suggestSubmit.getLuteceUserKey(  ), plugin );
        }

        if ( luteceUserConnected != null )
        {
            model.put( MARK_USER_SUBSCRIBED,
                SuggestSubscriptionProviderService.getService(  )
                                                   .hasUserSubscribedToSuggestSubmit( luteceUserConnected,
                    suggestSubmit.getIdSuggestSubmit(  ) ) );
        }
        

        model.put( MARK_ID_SUGGEST, suggestSubmit.getSuggest(  ).getIdSuggest(  ) );
        model.put( MARK_SUGGEST_SUBMIT, suggestSubmit );
        model.put( MARK_LUTECE_USER, luteceUserInfo );
        SuggestUtils.addAvatarToModel(model, luteceUserInfo);
        
        model.put( MARK_LUTECE_USER_CONNECTED, luteceUserConnected );
        model.put( MARK_SUGGEST_SUBMIT_VOTE_TYPE,
            getHtmlSuggestSubmitVoteType( suggestSubmit.getSuggest(  ), suggestSubmit, CONSTANT_VIEW_SUGGEST_SUBMIT,
                request.getLocale(  ) ) );
        
       
        
        
        model.put( MARK_AUTHORIZED_COMMENT, suggestSubmit.getSuggest(  ).isAuthorizedComment(  ) );
        model.put( MARK_AUTHORIZED_VOTE, !suggestSubmit.getSuggest(  ).isDisableVote(  ) );
        model.put( MARK_ENABLE_SUGGEST_REPORTS, suggestSubmit.getSuggest(  ).isEnableReports(  ) );
        model.put( MARK_IS_EXTEND_INSTALLED, PortalService.isExtendActivated(  ) );

        if ( suggestSubmit.getSuggest(  ).isAuthorizedComment(  ) && !suggestSubmit.isDisableComment(  ) )
        {
            model.put( MARK_LIST_COMMENT_SUBMIT_SUGGEST,
                getHtmlCommentSubmitList( request, suggestSubmit.getComments(  ), suggestSubmit.getSuggest(  ),
                    suggestSubmit.getIdSuggestSubmit(  ), luteceUserConnected, plugin ) );
        }
        CaptchaSecurityService captchaSecurityService = new CaptchaSecurityService(  );

        if ( suggestSubmit.getSuggest(  ).isActiveCaptcha(  ) && PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) )
        {
            model.put( MARK_JCAPTCHA, captchaSecurityService.getHtmlCode(  ) );
        }


        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_DETAIL_SUBMIT_SUGGEST,
                request.getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * return the Html type of vote
     *
     * @param strVoteTypeTemplateName
     *            the template use
     * @param suggest
     *            the suggest
     * @param nIdSuggestSubmit
     *            the id of the suggestSubmit
     * @param strView the view which is display the vote type
     * @param locale
     *            the locale
     * @return the Html type of vote
     * @throws SiteMessageException
     *             SiteMessageException
     */
    private String getHtmlSuggestSubmitVoteType( Suggest suggest, SuggestSubmit suggestSubmit, String strView, Locale locale )
        throws SiteMessageException
    {
        if ( !suggest.isDisableVote(  ) && !suggestSubmit.isDisableVote(  ) )
        {
            VoteType voteType = VoteTypeHome.findByPrimaryKey( suggest.getVoteType(  ).getIdVoteType(  ), _plugin );

            String strFilePath = PATH_TYPE_VOTE_FOLDER + voteType.getTemplateFileName(  );
            HashMap<String, Object> model = new HashMap<String, Object>(  );
            model.put( MARK_ID_SUGGEST, suggest.getIdSuggest(  ) );
            model.put( MARK_ID_SUGGEST_SUBMIT, suggestSubmit.getIdSuggestSubmit(  ) );
            model.put( MARK_VIEW, strView );

            HtmlTemplate template = AppTemplateService.getTemplate( strFilePath, locale, model );

            return template.getHtml(  );
        }

        return null;
    }

    /**
     * return the html form reported
     *
     * @param request
     *            the request
     * @param nMode
     *            The current mode.
     * @param plugin
     *            plugin
     * @param suggestSubmit
     *            the suggestSubmit
     *
     * @return the html form reported
     * @throws SiteMessageException
     *             SiteMessageException
     */
    private String getHtmlReported( HttpServletRequest request, int nMode, Plugin plugin, SuggestSubmit suggestSubmit )
        throws SiteMessageException
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );

        if ( ( suggestSubmit == null ) || ( suggestSubmit.getSuggestSubmitState(  ).getNumber(  ) == SuggestSubmit.STATE_DISABLE ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );

            return null;
        }

        model.put( MARK_ID_SUGGEST, suggestSubmit.getSuggest(  ).getIdSuggest(  ) );
        model.put( MARK_SUGGEST_SUBMIT, suggestSubmit );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_SUGGEST_REPORTED, request.getLocale(  ),
                model );

        return template.getHtml(  );
    }

    /**
     * return the html list of comment submit for a suggest submit
     *
     * @param request
     *            the request
     * @param listCommentSubmit
     *            the list of comment submit
     * @param suggest
     *            the suggest
     * @param plugin
     *            plugin
     * @param nIdSubmitSuggest
     *            the id of the suggest submit
     * @param luteceUserConnected
     *            lutece user connected
     * @return the html list of comment submit for a suggest submit
     */
    private String getHtmlCommentSubmitList( HttpServletRequest request, List<CommentSubmit> listCommentSubmit,
        Suggest suggest, int nIdSubmitSuggest, LuteceUser luteceUserConnected, Plugin plugin )
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );

        model.put( MARK_SUGGEST, suggest );
        model.put( MARK_ID_SUGGEST, suggest.getIdSuggest(  ) );
        model.put( MARK_ID_SUGGEST_SUBMIT, nIdSubmitSuggest );
        model.put( MARK_SUGGEST_COMMENT, CONSTANTE_PARAMETER_TRUE_VALUE );
        model.put( MARK_LIST_COMMENT_SUBMIT_SUGGEST, getCommentSubmitDisplayList( listCommentSubmit, plugin ) );
        model.put( MARK_DISABLE_NEW_COMMENT_SUBMIT, suggest.isDisableNewComment(  ) );
        model.put( MARK_ACTIVE_EDITOR_BBCODE, suggest.isActiveEditorBbcode(  ) );
        model.put( MARK_LUTECE_USER_CONNECTED, luteceUserConnected );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_COMMENT_SUBMIT_SUGGEST,
                request.getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * Return the html form for creating suggest submit
     *
     * @param request
     *            The HTTP request
     * @param nMode
     *            The current mode.
     * @param plugin
     *            The Plugin
     * @param suggest
     *            the suggest
     * @param nIdDefaultCategory the id of the default category
     * @return the html form
     * @throws SiteMessageException
     *             SiteMessageException
     */
    private String getHtmlForm( HttpServletRequest request, int nMode, Plugin plugin, Suggest suggest, int nIdDefaultCategory )
        throws SiteMessageException
    {
        Map<String, Object> model = SuggestUtils.getModelHtmlForm( suggest, plugin, request.getLocale(  ),
                nIdDefaultCategory, false );

        // get form Recap
        model.put( MARK_DISABLE_NEW_SUGGEST_SUBMIT, suggest.isDisableNewSuggestSubmit(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_FORM_SUGGEST, request.getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * Return the new suggest submit object
     *
     * @param request
     *            The HTTP request
     * @param nMode
     *            The current mode.
     * @param plugin
     *            The Plugin
     * @param suggest
     *            the suggest
     * @param nIdCategory
     *            the id of the category
     * @param nIdType
     *            the id of the type
     * @param user
     *            the lutece user
     * @return the new suggest submit object
     * @throws SiteMessageException
     *             SiteMessageException
     */
    private SuggestSubmit doInsertSuggestSubmit( HttpServletRequest request, int nMode, Plugin plugin, Suggest suggest,
        int nIdCategory, int nIdType, LuteceUser user )
        throws SiteMessageException
    {
        Locale locale = request.getLocale(  );
        List<Response> listResponse = new ArrayList<Response>(  );

        if ( suggest.isActiveCaptcha(  ) && PluginService.isPluginEnable( JCAPTCHA_PLUGIN ) )
        {
            CaptchaSecurityService captchaSecurityService = new CaptchaSecurityService(  );

            if ( !captchaSecurityService.validate( request ) )
            {
                SiteMessageService.setMessage( request, MESSAGE_CAPTCHA_ERROR, SiteMessage.TYPE_STOP );
            }
        }

        SuggestSubmit suggestSubmit = new SuggestSubmit(  );
        suggestSubmit.setSuggest( suggest );
        suggestSubmit.setResponses( listResponse );

        FormError formError = SuggestUtils.getAllResponsesData( request, suggestSubmit, plugin, locale );

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

        // perform suggest submit
        if ( nIdCategory != SuggestUtils.CONSTANT_ID_NULL )
        {
            Category category = CategoryHome.findByPrimaryKey( nIdCategory, plugin );
            suggestSubmit.setCategory( category );
        }

        if ( nIdType != SuggestUtils.CONSTANT_ID_NULL )
        {
            SuggestSubmitType type = SuggestSubmitTypeHome.findByPrimaryKey( nIdType, plugin );
            suggestSubmit.setSuggestSubmitType( type );
        }

        if ( user != null )
        {
            suggestSubmit.setLuteceUserKey( user.getName(  ) );
            //insert SuggestSubmitInfi=ormation if not exists
            SuggestUserInfoService.getService(  ).updateSuggestUserInfoByLuteceUser( user, plugin );
        }

        try
        {
            _suggestSubmitService.create( suggestSubmit, plugin, locale );
        }
        catch ( Exception ex )
        {
            // something very wrong happened... a database check might be needed
            AppLogService.error( ex.getMessage(  ) + " for SuggestSubmit " + suggestSubmit.getIdSuggestSubmit(  ), ex );
            // revert
            // we clear the DB form the given formsubmit (FormSubmitHome also
            // removes the reponses)
            _suggestSubmitService.remove( suggestSubmit.getIdSuggestSubmit(  ), plugin );
            // throw a message to the user
            SiteMessageService.setMessage( request, MESSAGE_MESSAGE_SUBMIT_SAVE_ERROR, SiteMessage.TYPE_ERROR );
        }

        return suggestSubmit;
    }

    /**
     * return the new comment submit create
     *
     * @param request
     *            the http request
     * @param suggestSubmit
     *            the SuggestSubmit
     *
     * @param strCommentValueSuggest
     *            the comment value
     * @param plugin
     *            the plugin
     * @param nIdParentComment
     *            the id of the parent of this comment
     * @param user
     *            the Lutece user associate to the comment
     * @return the new comment submit create
     * @throws SiteMessageException
     *             SiteMessageException
     */
    public CommentSubmit doInsertComment( HttpServletRequest request, SuggestSubmit suggestSubmit,
        String strCommentValueSuggest, Plugin plugin, LuteceUser user, int nIdParentComment )
        throws SiteMessageException
    {
        CommentSubmit commentSubmit = new CommentSubmit(  );

        if ( ( suggestSubmit == null ) || ( suggestSubmit.getSuggestSubmitState(  ).getNumber(  ) == SuggestSubmit.STATE_DISABLE ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );

            return null;
        }

        suggestSubmit.setNumberComment( suggestSubmit.getNumberComment(  ) + 1 );

        if ( !suggestSubmit.getSuggest(  ).isDisableNewComment(  ) )
        {
            commentSubmit.setActive( true );
            suggestSubmit.setNumberCommentEnable( suggestSubmit.getNumberCommentEnable(  ) + 1 );
        }

        _suggestSubmitService.update( suggestSubmit, plugin );

        commentSubmit.setDateComment( SuggestUtils.getCurrentDate(  ) );
        commentSubmit.setSuggestSubmit( suggestSubmit );
        commentSubmit.setValue( strCommentValueSuggest );
        commentSubmit.setOfficialAnswer( false );
        commentSubmit.setIdParent( nIdParentComment );

        if ( user != null )
        {
            commentSubmit.setLuteceUserKey( user.getName(  ) );
            //insert SuggestSubmitInfiormation if not exists
            SuggestUserInfoService.getService(  ).updateSuggestUserInfoByLuteceUser( user, plugin );
        }

        _commentSubmitService.create( commentSubmit, plugin );

        return commentSubmit;
    }

    /**
     * Clear params stores in session
     *
     * @param session
     *            the Http session
     */
    private void clearSessionFilter( HttpSession session )
    {
        session.setAttribute( SESSION_SEARCH_FIELDS, null );
    }

    /**
     *
     * method init
     * @param request
     *            The HTTP request
     * @param plugin
     *            The plugin
     */
    public void init( HttpServletRequest request, Plugin plugin )
    {
        _plugin = plugin;

        if ( _nIdSuggestSubmitStatePublish == SuggestUtils.CONSTANT_ID_NULL )
        {
            SuggestSubmitState suggestSubmitStatePublish = SuggestSubmitStateHome.findByNumero( SuggestSubmit.STATE_PUBLISH, plugin );

            if ( suggestSubmitStatePublish != null )
            {
                _nIdSuggestSubmitStatePublish = suggestSubmitStatePublish.getIdSuggestSubmitState(  );
            }
        }

        String strPortalUrl = AppPathService.getPortalUrl(  );

        if ( _urlSuggestXpageHome == null )
        {
            _urlSuggestXpageHome = new UrlItem( strPortalUrl );
            _urlSuggestXpageHome.addParameter( XPageAppService.PARAM_XPAGE_APP,
                AppPropertiesService.getProperty( PROPERTY_PAGE_APPLICATION_ID ) );
        }

        if ( _nNumberShownCharacters == SuggestUtils.CONSTANT_ID_NULL )
        {
            _nNumberShownCharacters = AppPropertiesService.getPropertyInt( PROPERTY_NUMBER_SUGGEST_SUBMIT_VALUE_SHOWN_CHARACTERS,
                    100 );
        }
    }

    /**
     *
     * Return the searcheField contains in the request or store in session
     * @param request {@link HttpServletRequest}
     * @return the SearchField
     */
    private SearchFields getSearchFields( HttpServletRequest request )
    {
        HttpSession session = request.getSession(  );
        String strQuery = request.getParameter( PARAMETER_QUERY );
        String strIdFilterPeriod = request.getParameter( PARAMETER_ID_FILTER_PERIOD );
        String strIdSuggestSubmitSort = request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT_SORT );
        String strIdFilterCategory = request.getParameter( PARAMETER_ID_FILTER_CATEGORY_SUGGEST );
        String strFilterPageIndex = request.getParameter( PARAMETER_FILTER_PAGE_INDEX );
        String strIdFilterSuggestSubmitType = request.getParameter( PARAMETER_ID_FILTER_SUGGEST_SUBMIT_TYPE );
        String strLuteceUserName = request.getParameter( PARAMETER_LUTECE_USER_NAME_FILTER );

        int nIdFilterPeriod = SuggestUtils.getIntegerParameter( strIdFilterPeriod );
        int nIdSuggestSubmitSort = SuggestUtils.getIntegerParameter( strIdSuggestSubmitSort );
        int nIdFilterCategory = SuggestUtils.getIntegerParameter( strIdFilterCategory );
        int nIdFilterSuggestSubmitType = SuggestUtils.getIntegerParameter( strIdFilterSuggestSubmitType );

        SearchFields searchFields = ( session.getAttribute( SESSION_SEARCH_FIELDS ) != null )
            ? (SearchFields) session.getAttribute( SESSION_SEARCH_FIELDS ) : new SearchFields(  );
        searchFields.setQuery( ( strQuery != null ) ? strQuery : searchFields.getQuery(  ) );
        
        
        searchFields.setIdFilterPeriod( ( strIdFilterPeriod != null ) ? nIdFilterPeriod
                                                                      : searchFields.getIdFilterPeriod(  ) );
        searchFields.setIdSuggestSubmitSort( ( strIdSuggestSubmitSort != null ) ? nIdSuggestSubmitSort
                                                                          : searchFields.getIdSuggestSubmitSort(  ) );
        searchFields.setIdFilterCategory( ( strIdFilterCategory != null ) ? nIdFilterCategory
                                                                          : searchFields.getIdFilterCategory(  ) );
        searchFields.setIdFilterSuggestSubmitType( ( strIdFilterSuggestSubmitType != null ) ? nIdFilterSuggestSubmitType
                                                                                      : searchFields.getIdFilterSuggestSubmitType(  ) );
        searchFields.setPageIndex( ( strFilterPageIndex != null ) ? strFilterPageIndex : searchFields.getPageIndex(  ) );

        searchFields.setLuteceUserName( ( strLuteceUserName != null ) ? strLuteceUserName : searchFields.getLuteceUserName(  ) );
        
        // update search Fields in session
        session.setAttribute( SESSION_SEARCH_FIELDS, searchFields );
        
        

        return searchFields;
    }



    /**
     * Add the suggest page frameset
     * @param strContentSuggest
     * @param request the {@link HttpServletRequest}
     * @param page {@link XPage}
     * @param suggest {@link Suggest}
     * @param model the model
     * @param searchFields the searchField
     * @param luteceUserConnected the luteceUserConnected
     */
    private void addSuggestPageFrameset( String strContentSuggest, HttpServletRequest request, XPage page, Suggest suggest,
        Map<String, Object> model, SearchFields searchFields, LuteceUser luteceUserConnected )
    {
        page.setTitle( I18nService.getLocalizedString( PROPERTY_XPAGE_PAGETITLE, request.getLocale(  ) ) );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_XPAGE_PATHLABEL, request.getLocale(  ) ) );

        if ( suggest.isActive(  ) )
        {
            //Filter by comment
            if ( suggest.isAuthorizedComment(  ) && suggest.isShowTopCommentBlock(  ) )
            {
                SubmitFilter submmitFilterTopComment = new SubmitFilter(  );
                submmitFilterTopComment.setIdSuggest( suggest.getIdSuggest(  ) );
                submmitFilterTopComment.setIdSuggestSubmitState( _nIdSuggestSubmitStatePublish );
                submmitFilterTopComment.setIdCategory( searchFields.getIdFilterCategory(  ) );

                SuggestUtils.initSubmitFilterBySort( submmitFilterTopComment, SubmitFilter.SORT_BY_NUMBER_COMMENT_DESC );

                List<SuggestSubmit> listSuggestSubmitTopComment = _suggestSubmitService.getSuggestSubmitList( submmitFilterTopComment,
                        _plugin, suggest.getNumberSuggestSubmitInTopComment(  ) );
                model.put( MARK_LIST_SUBMIT_TOP_COMMENT, listSuggestSubmitTopComment );
            }

            //Filter by popularity
            if ( suggest.isShowTopScoreBlock(  ) )
            {
                SubmitFilter submmitFilterTopPopularity = new SubmitFilter(  );
                submmitFilterTopPopularity.setIdSuggest( suggest.getIdSuggest(  ) );

                SuggestUtils.initSubmitFilterBySort( submmitFilterTopPopularity, SubmitFilter.SORT_BY_SCORE_DESC );

                submmitFilterTopPopularity.setIdSuggestSubmitState( _nIdSuggestSubmitStatePublish );
                submmitFilterTopPopularity.setIdCategory( searchFields.getIdFilterCategory(  ) );

                List<SuggestSubmit> listSuggestSubmitTopPopularity = _suggestSubmitService.getSuggestSubmitList( submmitFilterTopPopularity,
                        _plugin, suggest.getNumberSuggestSubmitInTopScore(  ) );
                model.put( MARK_LIST_SUBMIT_TOP_POPULARITY_SUGGEST, listSuggestSubmitTopPopularity );
            }

            //category Block
            if ( suggest.isShowCategoryBlock(  ) )
            {
                model.put( MARK_LIST_CATEGORIES_SUGGEST, suggest.getCategories(  ) );
            }

            ReferenceList refListSuggestSort = SuggestUtils.getRefListSuggestSort( request.getLocale(  ), true );
            ReferenceList refListFilterByPeriod = SuggestUtils.getRefListFilterByPeriod( request.getLocale(  ) );

            //model
            model.put( MARK_ID_SUGGEST, suggest.getIdSuggest(  ) );
            model.put( MARK_QUERY, searchFields.getQuery(  ) );
            model.put( MARK_ID_SUGGEST_SUBMIT_SORT, searchFields.getIdSuggestSubmitSort(  ) );
            model.put( MARK_ID_FILTER_PERIOD, searchFields.getIdFilterPeriod(  ) );
            model.put( MARK_ID_FILTER_CATEGORY_SUGGEST, searchFields.getIdFilterCategory(  ) );
            model.put( MARK_ID_FILTER_TYPE, searchFields.getIdFilterSuggestSubmitType(  ) );

            if ( searchFields.getIdFilterSuggestSubmitType(  ) != SuggestUtils.CONSTANT_ID_NULL )
            {
                model.put( MARK_TYPE_SELECTED,
                    SuggestSubmitTypeHome.findByPrimaryKey( searchFields.getIdFilterSuggestSubmitType(  ), _plugin ) );
            }

            model.put( MARK_CONTENT_SUGGEST, strContentSuggest );
            model.put( MARK_LABEL_SUGGEST, suggest.getLibelleContribution(  ) );
            model.put( MARK_HEADER_SUGGEST, suggest.getHeader(  ) );

            model.put( MARK_AUTHORIZED_COMMENT, suggest.isAuthorizedComment(  ) );
            model.put( MARK_AUTHORIZED_VOTE, !suggest.isDisableVote(  ) );
            model.put( MARK_NUMBER_SHOWN_CHARACTERS, _nNumberShownCharacters );

            model.put( MARK_LIST_SUGGEST_SUBMIT_SORT, refListSuggestSort );
            model.put( MARK_LIST_FILTER_BY_PERIOD, refListFilterByPeriod );

            model.put( MARK_SHOW_CATEGORY_BLOCK, suggest.isShowCategoryBlock(  ) );
            model.put( MARK_SHOW_TOP_SCORE_BLOCK, suggest.isShowTopScoreBlock(  ) );
            model.put( MARK_SHOW_TOP_COMMENT_BLOCK, suggest.isShowTopCommentBlock(  ) );
            model.put( MARK_IS_EXTEND_INSTALLED, PortalService.isExtendActivated(  ) );
        }
        else
        {
            model.put( MARK_UNAVAILABILITY_MESSAGE, suggest.getUnavailabilityMessage(  ) );
        }

        model.put( MARK_LUTECE_USER_CONNECTED, luteceUserConnected );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_FRAME_SUGGEST, request.getLocale(  ), model );
        page.setContent( template.getHtml(  ) );
    }

    /**
     * return a new UrlItem Xpage
     * @return a new UrlItem Xpage
     */
    private UrlItem getNewUrlItemPage(  )
    {
        return new UrlItem( _urlSuggestXpageHome.getUrl(  ) );
    }

    /**
     * Test if a user can process action to a suggest
     * @param suggest the suggest
     * @param request the {@link HttpServletRequest}
     * @param user The LuteceUser
     * @throws UserNotSignedException {@link UserNotSignedException}
     * @throws SiteMessageException {@link SiteMessageException}
     */
    private void testUserAuthorizationAccess( Suggest suggest, HttpServletRequest request, LuteceUser user )
        throws UserNotSignedException, SiteMessageException
    {
        if ( ( suggest.getRole(  ) != null ) && !Suggest.ROLE_NONE.equals( suggest.getRole(  ) ) )
        {
            if ( user == null )
            {
                throw new UserNotSignedException(  );
            }

            else if ( !SecurityService.getInstance(  ).isUserInRole( request, suggest.getRole(  ) ) )
            {
                SiteMessageService.setMessage( request, MESSAGE_ACCESS_DENIED, SiteMessage.TYPE_STOP );
            }
        }
    }
}
