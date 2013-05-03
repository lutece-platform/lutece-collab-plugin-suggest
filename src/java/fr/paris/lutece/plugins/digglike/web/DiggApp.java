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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.digglike.business.Category;
import fr.paris.lutece.plugins.digglike.business.CategoryHome;
import fr.paris.lutece.plugins.digglike.business.CommentSubmit;
import fr.paris.lutece.plugins.digglike.business.Digg;
import fr.paris.lutece.plugins.digglike.business.DiggFilter;
import fr.paris.lutece.plugins.digglike.business.DiggHome;
import fr.paris.lutece.plugins.digglike.business.DiggSubmit;
import fr.paris.lutece.plugins.digglike.business.DiggSubmitState;
import fr.paris.lutece.plugins.digglike.business.DiggSubmitStateHome;
import fr.paris.lutece.plugins.digglike.business.DiggSubmitType;
import fr.paris.lutece.plugins.digglike.business.DiggSubmitTypeHome;
import fr.paris.lutece.plugins.digglike.business.DiggUserInfo;
import fr.paris.lutece.plugins.digglike.business.EntryFilter;
import fr.paris.lutece.plugins.digglike.business.EntryHome;
import fr.paris.lutece.plugins.digglike.business.FormError;
import fr.paris.lutece.plugins.digglike.business.IEntry;
import fr.paris.lutece.plugins.digglike.business.ReportedMessage;
import fr.paris.lutece.plugins.digglike.business.Response;
import fr.paris.lutece.plugins.digglike.business.SearchFields;
import fr.paris.lutece.plugins.digglike.business.SubmitFilter;
import fr.paris.lutece.plugins.digglike.business.VoteHome;
import fr.paris.lutece.plugins.digglike.business.VoteType;
import fr.paris.lutece.plugins.digglike.business.VoteTypeHome;
import fr.paris.lutece.plugins.digglike.service.CommentSubmitService;
import fr.paris.lutece.plugins.digglike.service.DiggSubmitService;
import fr.paris.lutece.plugins.digglike.service.DiggUserInfoService;
import fr.paris.lutece.plugins.digglike.service.ICommentSubmitService;
import fr.paris.lutece.plugins.digglike.service.IDiggSubmitService;
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
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;


/**
 * This class manages Form page.
 */
public class DiggApp implements XPageApplication
{
    public static final String ANCHOR_DIGG_SUBMIT = "digg";
    public static final String PARAMETER_CLEAR_FILTER = "clear_filter";
    public static final String PARAMETER_DIGG_DETAIL = "digg_detail";

    // markers
    private static final String MARK_DIGG = "digg";
    private static final String MARK_CONTENT_DIGG = "content_digg";
    private static final String MARK_LIST_DIGG_SUBMIT = "list_digg_submit";
    private static final String MARK_LABEL_DIGG = "label_digg";
    private static final String MARK_HEADER_DIGG = "header_digg";
    private static final String MARK_LIST_CATEGORIES_DIGG = "list_categories_digg";
    private static final String MARK_LIST_TYPES = "list_types";
    private static final String MARK_ID_DIGG = "id_digg";
    private static final Object MARK_DIGG_LIST = "list_digg";
    private static final String MARK_LIST_SUBMIT_TOP_COMMENT = "list_top_comment_digg";
    private static final String MARK_LIST_SUBMIT_TOP_POPULARITY_DIGG = "list_top_popularity_digg";
    private static final String MARK_ID_DIGG_SUBMIT = "id_digg_submit";
    private static final String MARK_LIST_COMMENT_SUBMIT_DIGG = "list_comment";
    private static final String MARK_LIST_SUB_COMMENT_SUBMIT_DIGG = "list_sub_comment";
    private static final String MARK_DIGG_COMMENT = "digg_comment";
    private static final String MARK_AUTHORIZED_COMMENT = "authorized_comment";
    private static final String MARK_DIGG_SUBMIT_MODERATE = "digg_submit_moderate";
    private static final String MARK_DIGG_SUBMIT = "digg_submit";
    private static final String MARK_COMMENT_SUBMIT = "comment_submit";
    private static final String MARK_LUTECE_USER = "lutece_user";
    
    private static final String MARK_LUTECE_USER_CONNECTED = "lutece_user_connected";
    private static final String MARK_UNAVAILABILITY_MESSAGE = "unavailability_message";
    private static final String MARK_NUMBER_SHOWN_CHARACTERS = "number_shown_characters";
    private static final String MARK_DISABLE_NEW_DIGG_SUBMIT = "disable_new_digg_submit";
    private static final String MARK_DISABLE_NEW_COMMENT_SUBMIT = "disable_new_comment_submit";
    private static final String MARK_QUERY = "query";
    private static final String MARK_ID_FILTER_CATEGORY_DIGG = "id_filter_category";
    private static final String MARK_ID_FILTER_PERIOD = "id_filter_period";
    private static final String MARK_LIST_DIGG_SUBMIT_SORT = "list_digg_submit_sort";
    private static final String MARK_LIST_FILTER_BY_PERIOD = "list_filter_by_period";
    private static final String MARK_ID_DIGG_SUBMIT_SORT = "id_digg_submit_sort";
    private static final String MARK_SHOW_CATEGORY_BLOCK = "show_category_block";
    private static final String MARK_SHOW_TOP_SCORE_BLOCK = "show_top_score_block";
    private static final String MARK_SHOW_TOP_COMMENT_BLOCK = "show_top_comment_block";
    private static final String MARK_DIGG_SUBMIT_VOTE_TYPE = "digg_submit_vote_type";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_JCAPTCHA = "jcaptcha";
    private static final String MARK_MAX_AMOUNT_COMMENTS = "number_comments";
    private static final String MARK_MAX_AMOUNT_COMMENTS_CHAR = "cumber_char_comments";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private static final String MARK_ACTIVE_EDITOR_BBCODE = "active_editor_bbcode";
    private static final String MARK_VIEW = "view";

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
    private static final String PARAMETER_ID_SUBMIT_DIGG = "id_digg_submit";
    private static final String PARAMETER_COMMENT_DIGG = "digg_comment";
    private static final String PARAMETER_COMMENT_VALUE_DIGG = "comment_value";
    private static final String PARAMETER_REPORTED_VALUE = "reported_value";
    private static final String PARAMETER_ID_FILTER_PERIOD = "id_filter_period";
    private static final String PARAMETER_ID_DIGG_SUBMIT_SORT = "id_digg_submit_sort";
    private static final String PARAMETER_VOTE_DIGG = "vote";
    private static final String PARAMETER_ID_FILTER_CATEGORY_DIGG = "id_filter_category";
    private static final String PARAMETER_ID_CATEGORY_DIGG = "id_category";
    private static final String PARAMETER_ID_TYPE_DIGG = "id_type";
    private static final String PARAMETER_VOTED = "voted";
    private static final String PARAMETER_QUERY = "query";
    private static final String PARAMETER_FILTER_PAGE_INDEX = "filter_page_index";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_COMMENT_ID_PARENT = "id_digg_comment";
    private static final String PARAMETER_ACTION = "action";
    private static final String PARAMETER_VIEW = "view";

    // message
    private static final String MESSAGE_FORM_ERROR = "digglike.message.formError";
    private static final String MESSAGE_MANDATORY_QUESTION = "digglike.message.mandatory.question";
    private static final String MESSAGE_ERROR = "digglike.message.Error";
    private static final String MESSAGE_MANDATORY_COMMENT = "digglike.message.mandatoryComment";
    private static final String MESSAGE_MANDATORY_REPORTED = "digglike.message.mandatoryReported";
    private static final String MESSAGE_CAPTCHA_ERROR = "digglike.message.captchaError";
    private static final String MESSAGE_NEW_DIGG_SUBMIT = "digglike.message.newDiggSubmit";
    private static final String MESSAGE_NEW_DIGG_SUBMIT_DISABLE = "digglike.message.newDiggSubmitDisable";
    private static final String MESSAGE_NEW_DIGG_SUBMIT_INVALID = "digglike.message.newDiggSubmitInvalid";
    private static final String MESSAGE_MESSAGE_SUBMIT_SAVE_ERROR = "digglike.message.submitSaveError";
    private static final String MESSAGE_NEW_COMMENT_SUBMIT = "digglike.message.newCommentSubmit";
    private static final String MESSAGE_NEW_COMMENT_SUBMIT_DISABLE = "digglike.message.newCommentSubmitDisable";
    private static final String MESSAGE_NEW_REPORTED_SUBMIT = "digglike.message.newReportedSubmit";
    private static final String MESSAGE_NEW_COMMENT_SUBMIT_INVALID = "digglike.message.newCommentSubmitInvalid";
    private static final String MESSAGE_ERROR_NO_CATEGORY = "digglike.message.errorNoCategorySelected";
    private static final String MESSAGE_ACCESS_DENIED = "digglike.message.accessDenied";
    

    // XPAGE URL

    // constant
    private static final String EMPTY_STRING = "";
    private static final String CONSTANTE_PARAMETER_TRUE_VALUE = "1";
    private static final String CONSTANTE_PARAMETER_FALSE_VALUE = "0";
    private static final String PATH_TYPE_VOTE_FOLDER = "skin/plugins/digglike/";
    private static final String ACTION_VIEW_DIGG_LIST = "view_digg_list";
    private static final String ACTION_VIEW_DIGG_SUBMIT_LIST = "view_digg_submit_list";
    private static final String ACTION_CREATE_DIGG_SUBMIT = "create_digg_submit";
    private static final String ACTION_DO_CREATE_DIGG_SUBMIT = "do_create_digg_submit";
    private static final String ACTION_DO_CREATE_COMMENT = "do_create_comment";
    private static final String ACTION_DO_CREATE_REPORT = "do_create_report";
    private static final String ACTION_DO_VOTE = "do_vote";
    private static final String ACTION_VIEW_DIGG_SUBMIT = "view_digg_submit";
    private static final String ACTION_CREATE_REPORT = "create_report";
    private static final String CONSTANT_VIEW_LIST_DIGG_SUBMIT = "view_digg_submit_list";
    private static final String CONSTANT_VIEW_DIGG_SUBMIT = "view_digg_submit";
    private static final String CONSTANT_VIEW_REPORT = "view_report";
    private static final String CONSTANT_VIEW_CREATE_DIGG_SUBMIT = "view_create_digg_submit";

    // session filter
    private static final String SESSION_SEARCH_FIELDS = "search_fields";

    // properties
    private static final String PROPERTY_MAX_AMOUNT_COMMENTS = "digglike.comments.max.qty";
    private static final String PROPERTY_MAX_AMOUNT_COMMENTS_CHAR = "digglike.comments.max.char.qty";
    private int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 50 );
    private Plugin _plugin;
    private int _nIdDiggSubmitStatePublish = DiggUtils.CONSTANT_ID_NULL;
    private UrlItem _urlDiggXpageHome;
    private int _nNumberShownCharacters = DiggUtils.CONSTANT_ID_NULL;
    private IDiggSubmitService _diggSubmitService = DiggSubmitService.getService(  );
    private ICommentSubmitService _commentSubmitService = CommentSubmitService.getService(  );

    /**
     * Returns the DiggLike XPage result content depending on the request
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

        if ( ACTION_VIEW_DIGG_LIST.equals( strAction ) )
        {
            page = getViewDiggList( page, nMode, request );
        }
        else if ( ACTION_VIEW_DIGG_SUBMIT_LIST.equals( strAction ) )
        {
            page = getViewDiggSubmitList( page, nMode, request );
        }

        else if ( ACTION_VIEW_DIGG_SUBMIT.equals( strAction ) )
        {
            page = getViewDiggSubmit( page, nMode, request );
        }
        else if ( ACTION_VIEW_DIGG_SUBMIT.equals( strAction ) )
        {
            page = getViewDiggSubmit( page, nMode, request );
        }

        else if ( ACTION_CREATE_DIGG_SUBMIT.equals( strAction ) )
        {
            page = getViewCreateDiggSubmit( page, nMode, request );
        }
        else if ( ACTION_CREATE_REPORT.equals( strAction ) )
        {
            page = getViewCreateReport( page, nMode, request );
        }

        else if ( ACTION_DO_CREATE_DIGG_SUBMIT.equals( strAction ) )
        {
            doCreateDiggSubmit( page, nMode, request );
            page = getViewDiggSubmitList( page, nMode, request );
        }
        else if ( ACTION_DO_CREATE_COMMENT.equals( strAction ) )
        {
            doCreateComment( page, nMode, request );
            page = getViewDiggSubmit( page, nMode, request );
        }
        else if ( ACTION_DO_CREATE_REPORT.equals( strAction ) )
        {
            doReport( page, nMode, request );
            page = getViewDiggSubmit( page, nMode, request );
        }

        else if ( ACTION_DO_VOTE.equals( strAction ) )
        {
            doVote( page, nMode, request );

            String strView = request.getParameter( PARAMETER_VIEW );

            if ( ( strView != null ) && strView.equals( CONSTANT_VIEW_DIGG_SUBMIT ) )
            {
                page = getViewDiggSubmit( page, nMode, request );
            }
            else
            {
                page = getViewDiggSubmitList( page, nMode, request );
            }
        }
        else
        {
            if ( ( request.getParameter( PARAMETER_ID_DIGG ) != null ) ||
                    ( getIdDefaultDigg(  ) != DiggUtils.CONSTANT_ID_NULL ) )
            {
                page = getViewDiggSubmitList( page, nMode, request );
            }
            else
            {
                page = getViewDiggList( page, nMode, request );
            }
        }

        return page;
    }

    /**
     * Display Digg List
     * @param request the {@link HttpServletRequest}
     * @param nMode the mode
     * @param page {@link XPage}
     * @return {@link XPage}
     * @throws UserNotSignedException {@link UserNotSignedException}
     * @throws SiteMessageException {@link SiteMessageException}
     */
    public XPage getViewDiggList( XPage page, int nMode, HttpServletRequest request )
        throws UserNotSignedException, SiteMessageException
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        LuteceUser luteceUserConnected = SecurityService.getInstance(  ).getRegisteredUser( request );

        String strContentDigg = EMPTY_STRING;
        page.setTitle( I18nService.getLocalizedString( PROPERTY_XPAGE_PAGETITLE, request.getLocale(  ) ) );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_XPAGE_PATHLABEL, request.getLocale(  ) ) );

        // show the diggs list
        String strCurrentPageIndexDigg = "";
        strCurrentPageIndexDigg = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                strCurrentPageIndexDigg );

        int nItemsPerPageDigg = _nDefaultItemsPerPage;
        nItemsPerPageDigg = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, nItemsPerPageDigg,
                _nDefaultItemsPerPage );

        strContentDigg = getHtmlListDigg( request.getLocale(  ), _plugin, strCurrentPageIndexDigg, nItemsPerPageDigg,
                getNewUrlItemPage(  ), luteceUserConnected );

        model.put( MARK_CONTENT_DIGG, strContentDigg );

        page.setContent( strContentDigg );

        return page;
    }

    /**
     * Display DiggSubmit List
     * @param request the {@link HttpServletRequest}
     * @param nMode the mode
     * @param page {@link XPage}
     * @return {@link XPage}
     * @throws UserNotSignedException {@link UserNotSignedException}
     * @throws SiteMessageException {@link SiteMessageException}
     */
    public XPage getViewDiggSubmitList( XPage page, int nMode, HttpServletRequest request )
        throws UserNotSignedException, SiteMessageException
    {
        String strIdDigg = request.getParameter( PARAMETER_ID_DIGG );

        int nIdDigg = DiggUtils.getIntegerParameter( strIdDigg );
        Digg digg = DiggHome.findByPrimaryKey( nIdDigg, _plugin );
        LuteceUser luteceUserConnected = SecurityService.getInstance(  ).getRegisteredUser( request );
        
        
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_VIEW, CONSTANT_VIEW_LIST_DIGG_SUBMIT );

      

        if ( digg == null )
        {
            digg = DiggHome.findByPrimaryKey( getIdDefaultDigg(  ), _plugin );
        }
        
        
        
        //testAuthorizationAccess
        testUserAuthorizationAccess(digg, request, luteceUserConnected);
        
        UrlItem urlDiggXpage = getNewUrlItemPage(  );
        urlDiggXpage.addParameter( PARAMETER_ACTION, CONSTANT_VIEW_LIST_DIGG_SUBMIT );
        urlDiggXpage.addParameter( PARAMETER_ID_DIGG, nIdDigg );

        SearchFields searchFields = getSearchFields( request );
        addDiggPageFrameset( getHtmlListDiggSubmit( request.getLocale(  ), _plugin, digg, searchFields, urlDiggXpage,
                luteceUserConnected ), request, page, digg, model, searchFields, luteceUserConnected );

        return page;
    }

    /**
     * Display view  DiggSubmit
     * @param request the {@link HttpServletRequest}
     * @param nMode the mode
     * @param page {@link XPage}
     * @return {@link XPage}
     * @throws UserNotSignedException {@link UserNotSignedException}
     * @throws SiteMessageException {@link SiteMessageException}
     */
    public XPage getViewDiggSubmit( XPage page, int nMode, HttpServletRequest request )
        throws UserNotSignedException, SiteMessageException
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        LuteceUser luteceUserConnected = SecurityService.getInstance(  ).getRegisteredUser( request );
        String strIdSubmitDigg = request.getParameter( PARAMETER_ID_SUBMIT_DIGG );
        int nIdSubmitDigg = DiggUtils.getIntegerParameter( strIdSubmitDigg );
        DiggSubmit diggSubmit = _diggSubmitService.findByPrimaryKey( nIdSubmitDigg, true, _plugin );
        diggSubmit.setDigg( DiggHome.findByPrimaryKey( diggSubmit.getDigg(  ).getIdDigg(  ), _plugin ) );
        
        //testAuthorizationAccess
        testUserAuthorizationAccess(diggSubmit.getDigg(), request, luteceUserConnected);
        
        model.put( MARK_VIEW, CONSTANT_VIEW_DIGG_SUBMIT );

        SearchFields searchFields = getSearchFields( request );
        addDiggPageFrameset( getHtmlDiggSubmitDetail( request, nMode, _plugin, diggSubmit, luteceUserConnected ),
            request, page, diggSubmit.getDigg(  ), model, searchFields, luteceUserConnected );

        return page;
    }

    /**
     * Display create DiggSubmit Form
     * @param request the {@link HttpServletRequest}
     * @param nMode the mode
     * @param page {@link XPage}
     * @return {@link XPage}
     * @throws UserNotSignedException {@link UserNotSignedException}
     * @throws SiteMessageException {@link SiteMessageException}
     */
    public XPage getViewCreateDiggSubmit( XPage page, int nMode, HttpServletRequest request )
        throws UserNotSignedException, SiteMessageException
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_VIEW, CONSTANT_VIEW_CREATE_DIGG_SUBMIT );

        String strIdDigg = request.getParameter( PARAMETER_ID_DIGG );
        int nIdDigg = DiggUtils.getIntegerParameter( strIdDigg );
        LuteceUser luteceUserConnected = SecurityService.getInstance(  ).getRegisteredUser( request );
        Digg digg = DiggHome.findByPrimaryKey( nIdDigg, _plugin );

        SearchFields searchFields = getSearchFields( request );

        addDiggPageFrameset( getHtmlForm( request, nMode, _plugin, digg, searchFields.getIdFilterCategory(  ) ),
            request, page, digg, model, searchFields, luteceUserConnected );

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
        String strIdSubmitDigg = request.getParameter( PARAMETER_ID_SUBMIT_DIGG );
        int nIdSubmitDigg = DiggUtils.getIntegerParameter( strIdSubmitDigg );
        DiggSubmit diggSubmit = _diggSubmitService.findByPrimaryKey( nIdSubmitDigg, true, _plugin );
        diggSubmit.setDigg( DiggHome.findByPrimaryKey( diggSubmit.getDigg(  ).getIdDigg(  ), _plugin ) );
        model.put( MARK_VIEW, CONSTANT_VIEW_REPORT );

        SearchFields searchFields = getSearchFields( request );
        addDiggPageFrameset( getHtmlReported( request, nMode, _plugin, diggSubmit ), request, page,
            diggSubmit.getDigg(  ), model, searchFields, luteceUserConnected );

        return page;
    }

    /**
     *  Perform Action create Digg Submit
     * @param request the {@link HttpServletRequest}
     * @param nMode the mode
     * @param page {@link XPage}
     * @return {@link XPage}
     * @throws UserNotSignedException {@link UserNotSignedException}
     * @throws SiteMessageException {@link SiteMessageException}
     */
    public void doCreateDiggSubmit( XPage page, int nMode, HttpServletRequest request )
        throws UserNotSignedException, SiteMessageException
    {
        String strIdDigg = request.getParameter( PARAMETER_ID_DIGG );

        int nIdDigg = DiggUtils.getIntegerParameter( strIdDigg );
        LuteceUser luteceUserConnected = null;

        Digg digg = DiggHome.findByPrimaryKey( nIdDigg, _plugin );

        if ( digg.isActiveDiggSubmitAuthentification(  ) && SecurityService.isAuthenticationEnable(  ) )
        {
            luteceUserConnected = ( luteceUserConnected != null ) ? luteceUserConnected
                                                                  : SecurityService.getInstance(  )
                                                                                   .getRemoteUser( request );
            if ( luteceUserConnected == null )
            {
                throw new UserNotSignedException( );
            }
            
           //testAuthorizationAccess
            testUserAuthorizationAccess(digg, request, luteceUserConnected);
        }

        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY_DIGG );
        String strIdType = request.getParameter( PARAMETER_ID_TYPE_DIGG );
        String strMessage = MESSAGE_NEW_DIGG_SUBMIT;
        int nIdCategory = DiggUtils.getIntegerParameter( strIdCategory );
        int nIdType = DiggUtils.getIntegerParameter( strIdType );

        //Check if a category is selected (in the case or the digg has some categories)
        if ( strIdCategory != null )
        {
            if ( strIdCategory.equals( Integer.toString( Category.DEFAULT_ID_CATEGORY ) ) )
            {
                SiteMessageService.setMessage( request, MESSAGE_ERROR_NO_CATEGORY, SiteMessage.TYPE_STOP );
            }
        }

        DiggSubmit diggSubmit = doInsertDiggSubmit( request, nMode, _plugin, digg, nIdCategory, nIdType,
                luteceUserConnected );

        if ( digg.isDisableNewDiggSubmit(  ) )
        {
            DiggUtils.sendNotificationNewDiggSubmit( digg, diggSubmit, request.getLocale(  ), request );
            strMessage = MESSAGE_NEW_DIGG_SUBMIT_DISABLE;
        }

        //send notification to the admin
        if ( digg.isEnableMailNewDiggSubmit(  ) )
        {
            DiggUtils.sendMailNewDiggSubmit( digg, diggSubmit, request.getLocale(  ), request );
        }

        Map<String, Object> parameters = new HashMap<String, Object>(  );
        parameters.put( PARAMETER_ID_DIGG, nIdDigg );
        parameters.put( PARAMETER_ACTION, CONSTANT_VIEW_LIST_DIGG_SUBMIT );

        if ( !StringUtils.isEmpty( digg.getConfirmationMessage(  ) ) )
        {
            Object[] args = { ( digg.getConfirmationMessage(  ) == null ) ? "" : digg.getConfirmationMessage(  ) };
            SiteMessageService.setMessage( request, strMessage, args, null, getNewUrlItemPage(  ).getUrl(  ), null,
                SiteMessage.TYPE_INFO, parameters );
        }
    }

    /**
     *  Perform Action create Comment
     * @param request the {@link HttpServletRequest}
     * @param nMode the mode
     * @param page {@link XPage}
     * @return {@link XPage}
     * @throws UserNotSignedException {@link UserNotSignedException}
     * @throws SiteMessageException {@link SiteMessageException}
     */
    public void doCreateComment( XPage page, int nMode, HttpServletRequest request )
        throws UserNotSignedException, SiteMessageException
    {
        String strIdSubmitDigg = request.getParameter( PARAMETER_ID_SUBMIT_DIGG );
        int nIdSubmitDigg = DiggUtils.getIntegerParameter( strIdSubmitDigg );
        DiggSubmit diggSubmit = _diggSubmitService.findByPrimaryKey( nIdSubmitDigg, true, _plugin );
        Digg digg = DiggHome.findByPrimaryKey( diggSubmit.getDigg(  ).getIdDigg(  ), _plugin );
        diggSubmit.setDigg( digg );

        LuteceUser luteceUserConnected = null;

        if ( digg.isActiveCommentAuthentification(  ) && SecurityService.isAuthenticationEnable(  ) )
        {
            luteceUserConnected = ( luteceUserConnected != null ) ? luteceUserConnected
                                                                  : SecurityService.getInstance(  )
                                                                                   .getRemoteUser( request );
            if ( luteceUserConnected == null )
            {
                throw new UserNotSignedException( );
            }
          //testAuthorizationAccess
            testUserAuthorizationAccess(digg, request, luteceUserConnected);
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

        CommentSubmit commentSubmit = doInsertComment( request, diggSubmit, strCommentValueDigg, _plugin,
                luteceUserConnected, nIdParentComment );

        if ( digg.isDisableNewComment(  ) )
        {
            DiggUtils.sendNotificationNewCommentSubmit( digg, commentSubmit, request.getLocale(  ), request );
            strMessage = MESSAGE_NEW_COMMENT_SUBMIT_DISABLE;
        }

        if ( !StringUtils.isEmpty( digg.getConfirmationMessage(  ) ) )
        {
            Map<String, Object> parameters = new HashMap<String, Object>(  );

            parameters.put( PARAMETER_ID_SUBMIT_DIGG, nIdSubmitDigg );
            parameters.put( PARAMETER_ID_DIGG, digg.getIdDigg(  ) );
            parameters.put( PARAMETER_COMMENT_DIGG, CONSTANTE_PARAMETER_TRUE_VALUE );
            parameters.put( PARAMETER_ACTION, CONSTANT_VIEW_DIGG_SUBMIT );

            Object[] args = { ( digg.getConfirmationMessage(  ) == null ) ? "" : digg.getConfirmationMessage(  ) };
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
     * @return {@link XPage}
     * @throws UserNotSignedException {@link UserNotSignedException}
     * @throws SiteMessageException {@link SiteMessageException}
     */
    public void doReport( XPage page, int nMode, HttpServletRequest request )
        throws UserNotSignedException, SiteMessageException
    {
        String strIdSubmitDigg = request.getParameter( PARAMETER_ID_SUBMIT_DIGG );
        int nIdSubmitDigg = DiggUtils.getIntegerParameter( strIdSubmitDigg );

        DiggSubmit diggSubmit = _diggSubmitService.findByPrimaryKey( nIdSubmitDigg, true, _plugin );
        Digg digg = DiggHome.findByPrimaryKey( diggSubmit.getDigg(  ).getIdDigg(  ), _plugin );
        diggSubmit.setDigg( digg );

        String strReportedValue = request.getParameter( PARAMETER_REPORTED_VALUE );

        if ( ( strReportedValue == null ) || strReportedValue.trim(  ).equals( EMPTY_STRING ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_MANDATORY_REPORTED, SiteMessage.TYPE_STOP );
        }

        if ( diggSubmit == null )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
        }
        
        LuteceUser luteceUserConnected = null;

        if ( digg.isActiveCommentAuthentification(  ) && SecurityService.isAuthenticationEnable(  ) )
        {
            luteceUserConnected = ( luteceUserConnected != null ) ? luteceUserConnected
                                                                  : SecurityService.getInstance(  )
                                                                                   .getRemoteUser( request );
            if ( luteceUserConnected == null )
            {
                throw new UserNotSignedException( );
            }
          //testAuthorizationAccess
            testUserAuthorizationAccess(digg, request, luteceUserConnected);
        }

        DiggUtils.doReportDiggSubmit( diggSubmit, _plugin );

        ReportedMessage reportedMessage = new ReportedMessage(  );
        reportedMessage.setDiggSubmit( diggSubmit );
        reportedMessage.setValue( strReportedValue );
        DiggUtils.sendNotificationNewReportedMessage( digg, reportedMessage, request.getLocale(  ), request );

        Map<String, Object> parameters = new HashMap<String, Object>(  );
        parameters.put( PARAMETER_ID_SUBMIT_DIGG, nIdSubmitDigg );
        parameters.put( PARAMETER_ID_DIGG, digg.getIdDigg(  ) );
        parameters.put( PARAMETER_ACTION, CONSTANT_VIEW_DIGG_SUBMIT );

        UrlItem urlItemPage = getNewUrlItemPage(  );
        urlItemPage.setAnchor( ANCHOR_DIGG_SUBMIT + nIdSubmitDigg );

        SiteMessageService.setMessage( request, MESSAGE_NEW_REPORTED_SUBMIT, null, null, urlItemPage.getUrl(  ), null,
            SiteMessage.TYPE_INFO, parameters );
    }

    /**
     * Perform Action Vote
     * @param request the {@link HttpServletRequest}
     * @param nMode the mode
     * @param page {@link XPage}
     * @return {@link XPage}
     * @throws UserNotSignedException {@link UserNotSignedException}
     * @throws SiteMessageException {@link SiteMessageException}
     */
    public void doVote( XPage page, int nMode, HttpServletRequest request )
        throws UserNotSignedException, SiteMessageException
    {
        String strVote = request.getParameter( PARAMETER_VOTE_DIGG );
        String strIdSubmitDigg = request.getParameter( PARAMETER_ID_SUBMIT_DIGG );
        int nIdSubmitDigg = DiggUtils.getIntegerParameter( strIdSubmitDigg );
        DiggSubmit diggSubmit = _diggSubmitService.findByPrimaryKey( nIdSubmitDigg, true, _plugin );
        Digg digg = DiggHome.findByPrimaryKey( diggSubmit.getDigg(  ).getIdDigg(  ), _plugin );
        diggSubmit.setDigg( digg );

        LuteceUser luteceUserConnected = null;

        if ( digg.isLimitNumberVote(  ) )
        {
            if ( digg.isActiveVoteAuthentification(  ) && SecurityService.isAuthenticationEnable(  ) )
            {
                luteceUserConnected = ( luteceUserConnected != null ) ? luteceUserConnected
                                                                      : SecurityService.getInstance(  )
                                                                                       .getRemoteUser( request );
                if ( luteceUserConnected == null )
                {
                    throw new UserNotSignedException( );
                }
                else if(digg.getRole() != null && !SecurityService.getInstance().isUserInRole(request, digg.getRole()))
                {
                	SiteMessageService.setMessage( request, MESSAGE_ACCESS_DENIED, SiteMessage.TYPE_STOP );
                	
                }

                if ( VoteHome.getUserNumberVoteOnDiggSubmit( nIdSubmitDigg, luteceUserConnected.getName(  ), _plugin ) == 0 )
                {
                    doVote( strVote, nIdSubmitDigg, _plugin, luteceUserConnected.getName(  ) );
                }
            }
            else if ( request.getSession(  ).getAttribute( EMPTY_STRING + nIdSubmitDigg ) == null )
            {
                doVote( strVote, nIdSubmitDigg, _plugin, null );
                request.getSession(  ).setAttribute( EMPTY_STRING + nIdSubmitDigg, PARAMETER_VOTED );
            }
        }
        else
        {
            doVote( strVote, nIdSubmitDigg, _plugin, null );
        }
    }

    /**
     * Increment score
     *
     * @param strVote
     *            the value to add at score
     * @param nIdSubmitDigg
     *            the id of the digg submit
     * @param plugin
     *            the plugin
     * @param strUserKey
     *            the user key
     */
    private void doVote( String strVote, int nIdSubmitDigg, Plugin plugin, String strUserKey )
    {
        // Increment vote
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
     * @param digg  the digg
     * @param searchFields the searchFields
     * @param urlDiggXPage   the url of the Xpage
     * @param luteceUserConnected the lutece UserConnected
     * @return the html list of digg submit
     * @throws SiteMessageException SiteMessageException {@link SiteMessageException}
     */
    private String getHtmlListDiggSubmit( Locale locale, Plugin plugin, Digg digg, SearchFields searchFields,
        UrlItem urlDiggXPage, LuteceUser luteceUserConnected )
        throws SiteMessageException
    {
        Map<String, Object> model = new HashMap<String, Object>(  );

        List<Integer> listIdDiggSubmit;

        SubmitFilter submitFilter = new SubmitFilter(  );

        // Filter the list
        DiggUtils.initSubmitFilterByPeriod( submitFilter, searchFields.getIdFilterPeriod(  ) );
        DiggUtils.initSubmitFilterBySort( submitFilter,
            ( searchFields.getIdDiggSubmitSort(  ) != DiggUtils.CONSTANT_ID_NULL )
            ? searchFields.getIdDiggSubmitSort(  ) : digg.getIdDefaultSort(  ) );
        submitFilter.setIdDigg( digg.getIdDigg(  ) );
        submitFilter.setIdDiggSubmitState( _nIdDiggSubmitStatePublish );
        submitFilter.setIdCategory( searchFields.getIdFilterCategory(  ) );

        listIdDiggSubmit = DigglikeSearchService.getInstance(  )
                                                .getSearchResults( searchFields.getQuery(  ), submitFilter, plugin );

        if ( digg.isActiveDiggSubmitPaginator(  ) && ( digg.getNumberDiggSubmitPerPage(  ) > 0 ) )
        {
            Paginator paginator = new Paginator( listIdDiggSubmit, digg.getNumberDiggSubmitPerPage(  ),
                    urlDiggXPage.getUrl(  ), PARAMETER_FILTER_PAGE_INDEX, searchFields.getPageIndex(  ) );
            listIdDiggSubmit = paginator.getPageItems(  );
            model.put( MARK_PAGINATOR, paginator );
        }

        model.put( MARK_DIGG, digg );
        model.put( MARK_LIST_DIGG_SUBMIT, getDiggSubmitDisplayList( listIdDiggSubmit, digg, locale, plugin ) );

        model.put( MARK_AUTHORIZED_COMMENT, digg.isAuthorizedComment(  ) );
        model.put( MARK_DIGG_SUBMIT_MODERATE, digg.isDisableNewDiggSubmit(  ) );
        model.put( MARK_ID_DIGG, digg.getIdDigg(  ) );

        String strMaxAmountComments = AppPropertiesService.getProperty( PROPERTY_MAX_AMOUNT_COMMENTS );

        if ( !StringUtils.isEmpty( strMaxAmountComments ) )
        {
            model.put( MARK_MAX_AMOUNT_COMMENTS, DiggUtils.getIntegerParameter( strMaxAmountComments ) );
            model.put( MARK_MAX_AMOUNT_COMMENTS_CHAR,
                AppPropertiesService.getPropertyInt( PROPERTY_MAX_AMOUNT_COMMENTS_CHAR, 20 ) );
        }

        model.put( MARK_LUTECE_USER_CONNECTED, luteceUserConnected );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_LIST_SUBMIT_DIGG, locale, model );

        return template.getHtml(  );
    }

    /**
     * return the html list of digg
     *
     * @param locale
     *            the locale
     * @param plugin
     *            the plugin
     * @param strCurrentPageIndexDigg
     *            the current page index
     * @param nItemsPerPageDigg
     *            the number of items per page
     * @param urlDiggXPage
     *            the url of the digg xpage
     * @param luteceUserConnected
     *            luteceUser
     * @throws SiteMessageException
     *             SiteMessageException
     * @return the html list of digg
     */
    private String getHtmlListDigg( Locale locale, Plugin plugin, String strCurrentPageIndexDigg,
        int nItemsPerPageDigg, UrlItem urlDiggXPage, LuteceUser luteceUserConnected )
        throws SiteMessageException
    {
        DiggFilter filter = new DiggFilter(  );

        List listDigg = DiggHome.getDiggList( filter, plugin );

        HashMap model = new HashMap(  );
        Paginator paginator = new Paginator( listDigg, nItemsPerPageDigg, urlDiggXPage.getUrl(  ),
                PARAMETER_PAGE_INDEX, strCurrentPageIndexDigg );

        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + nItemsPerPageDigg );

        model.put( MARK_DIGG_LIST, paginator.getPageItems(  ) );
        model.put( MARK_LUTECE_USER_CONNECTED, luteceUserConnected );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_XPAGE_LIST_DIGG, locale, model );

        return templateList.getHtml(  );
    }

    /**
     * return a collection which contains digg submit and lutece user associate
     *
     * @param listDiggSubmit
     *            the list of digg submit
     * @param digg
     *            the digg possessing submits
     * @param strDiggDetail
     *            the digg detail
     * @param locale
     *            the locale
     * @throws SiteMessageException
     *             SiteMessageException
     * @return a collection which contains digg submit and lutece user associate
     */
    private Collection<HashMap> getDiggSubmitDisplayList( Collection<Integer> listDiggSubmit, Digg digg, Locale locale,
        Plugin plugin ) throws SiteMessageException
    {
        DiggUserInfo luteceUserInfo;
        DiggSubmit diggSubmit;
        Collection<HashMap> listHashDigg = new ArrayList<HashMap>(  );
        String strPropertyMaxAmountComments = AppPropertiesService.getProperty( PROPERTY_MAX_AMOUNT_COMMENTS );

        for ( Integer idDiggSubmit : listDiggSubmit )
        {
            HashMap<String, Object> modelDigg = new HashMap<String, Object>(  );

            luteceUserInfo = null;
            diggSubmit = _diggSubmitService.findByPrimaryKey( idDiggSubmit,
                    ( digg.isAuthorizedComment(  ) && !StringUtils.isEmpty( strPropertyMaxAmountComments ) ), plugin );
            modelDigg.put( MARK_DIGG_SUBMIT, diggSubmit );

            if ( SecurityService.isAuthenticationEnable(  ) && ( diggSubmit.getLuteceUserKey(  ) != null ) )
            {
            	luteceUserInfo = DiggUserInfoService.getService().findDiggUserInfoByKey(diggSubmit.getLuteceUserKey(  ) , plugin);
            }

            modelDigg.put( MARK_LUTECE_USER, luteceUserInfo );
            modelDigg.put( MARK_DIGG_SUBMIT_VOTE_TYPE,
                getHtmlDiggSubmitVoteType( digg, diggSubmit.getIdDiggSubmit(  ), CONSTANT_VIEW_LIST_DIGG_SUBMIT, locale ) );

            listHashDigg.add( modelDigg );
        }

        return listHashDigg;
    }

    /**
     * return a collection which contains comment and lutece user associate
     *
     * @param listCommentSubmit
     *            the list of comment submit
     * @return a collection which contains comment and lutece user associate
     */
    private Collection<HashMap> getCommentSubmitDisplayList( Collection<CommentSubmit> listCommentSubmit,Plugin plugin )
    {
        Collection<HashMap> listHashComment = new ArrayList<HashMap>(  );
        DiggUserInfo luteceUserInfo;

        for ( CommentSubmit commentSubmit : listCommentSubmit )
        {
            HashMap<String, Object> modelComment = new HashMap<String, Object>(  );

            luteceUserInfo = null;

            modelComment.put( MARK_COMMENT_SUBMIT, commentSubmit );

            if ( SecurityService.isAuthenticationEnable(  ) && ( commentSubmit.getLuteceUserKey(  ) != null ) )
            {
            	luteceUserInfo = DiggUserInfoService.getService().findDiggUserInfoByKey(commentSubmit.getLuteceUserKey(  ) , plugin);
            }

            modelComment.put( MARK_LUTECE_USER, luteceUserInfo );
            if( commentSubmit.getComments() != null && commentSubmit.getComments().size() > 0)
            {
            	modelComment.put( MARK_LIST_SUB_COMMENT_SUBMIT_DIGG, getCommentSubmitDisplayList( commentSubmit.getComments(),plugin ) );
            	
            }
            
            
            listHashComment.add( modelComment );
        }

        return listHashComment;
    }

    /**
     * the html digg submit detail
     *
     * @param request
     *            the request
     * @param nMode
     *            The current mode.
     * @param plugin
     *            the plugin
     * @param diggSubmit
     *            the {@link DiggSubmit}
     *
    
     * @param luteceUserConnected
     *            the lutece user
     * @return the html digg submit detail
     * @throws SiteMessageException
     *             SiteMessageException
     */
    private String getHtmlDiggSubmitDetail( HttpServletRequest request, int nMode, Plugin plugin,
        DiggSubmit diggSubmit, LuteceUser luteceUserConnected )
        throws SiteMessageException
    {
    	DiggUserInfo luteceUserInfo=null;
        HashMap<String, Object> model = new HashMap<String, Object>(  );

        if ( ( diggSubmit == null ) || ( diggSubmit.getDiggSubmitState(  ).getNumber(  ) == DiggSubmit.STATE_DISABLE ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
        }

        // update number view
        diggSubmit.setNumberView( diggSubmit.getNumberView(  ) + 1 );
        _diggSubmitService.update( diggSubmit, false, plugin );

        if ( SecurityService.isAuthenticationEnable(  ) && ( diggSubmit.getLuteceUserKey(  ) != null ) )
        {
        	luteceUserInfo = DiggUserInfoService.getService().findDiggUserInfoByKey(diggSubmit.getLuteceUserKey(  ) , plugin);
        }

        model.put( MARK_ID_DIGG, diggSubmit.getDigg(  ).getIdDigg(  ) );
        model.put( MARK_DIGG_SUBMIT, diggSubmit );
        model.put( MARK_LUTECE_USER, luteceUserInfo );
        model.put( MARK_LUTECE_USER_CONNECTED, luteceUserConnected );
        model.put( MARK_DIGG_SUBMIT_VOTE_TYPE,
            getHtmlDiggSubmitVoteType( diggSubmit.getDigg(  ), diggSubmit.getIdDiggSubmit(  ),
                CONSTANT_VIEW_DIGG_SUBMIT, request.getLocale(  ) ) );
        model.put( MARK_AUTHORIZED_COMMENT, diggSubmit.getDigg(  ).isAuthorizedComment(  ) );
        model.put( MARK_DIGG_SUBMIT_MODERATE, diggSubmit.getDigg(  ).isDisableNewDiggSubmit(  ) );

        if ( diggSubmit.getDigg(  ).isAuthorizedComment(  ) )
        {
            model.put( MARK_LIST_COMMENT_SUBMIT_DIGG,
                getHtmlCommentSubmitList( request, diggSubmit.getComments(  ), diggSubmit.getDigg(  ),
                    diggSubmit.getIdDiggSubmit(  ), luteceUserConnected ,plugin) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_DETAIL_SUBMIT_DIGG,
                request.getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * return the Html type of vote
     *
     * @param strVoteTypeTemplateName
     *            the template use
     * @param digg
     *            the digg
     * @param nIdDiggSubmit
     *            the id of the diggSubmit
     * @param strView the view which is display the vote type
     * @param locale
     *            the locale
     * @return the Html type of vote
     * @throws SiteMessageException
     *             SiteMessageException
     */
    private String getHtmlDiggSubmitVoteType( Digg digg, int nIdDiggSubmit, String strView, Locale locale )
        throws SiteMessageException
    {
        VoteType voteType = VoteTypeHome.findByPrimaryKey( digg.getVoteType(  ).getIdVoteType(  ), _plugin );

        String strFilePath = PATH_TYPE_VOTE_FOLDER + voteType.getTemplateFileName(  );
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_ID_DIGG, digg.getIdDigg(  ) );
        model.put( MARK_ID_DIGG_SUBMIT, nIdDiggSubmit );
        model.put( MARK_VIEW, strView );

        HtmlTemplate template = AppTemplateService.getTemplate( strFilePath, locale, model );

        return template.getHtml(  );
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
     * @param diggSubmit
     *            the diggSubmit
    
     * @return the html form reported
     * @throws SiteMessageException
     *             SiteMessageException
     */
    private String getHtmlReported( HttpServletRequest request, int nMode, Plugin plugin, DiggSubmit diggSubmit )
        throws SiteMessageException
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );

        if ( ( diggSubmit == null ) || ( diggSubmit.getDiggSubmitState(  ).getNumber(  ) == DiggSubmit.STATE_DISABLE ) )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR, SiteMessage.TYPE_STOP );
        }

        model.put( MARK_ID_DIGG, diggSubmit.getDigg(  ).getIdDigg(  ) );
        model.put( MARK_DIGG_SUBMIT, diggSubmit );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_DIGG_REPORTED, request.getLocale(  ),
                model );

        return template.getHtml(  );
    }

    /**
     * return the html list of comment submit for a digg submit
     *
     * @param request
     *            the request
     * @param listCommentSubmit
     *            the list of comment submit
     * @param digg
     *            the digg
     * @param plugin
     *            plugin
     * @param nIdSubmitDigg
     *            the id of the digg submit
     * @param luteceUserConnected
     *            lutece user connected
     * @return the html list of comment submit for a digg submit
     */
    private String getHtmlCommentSubmitList( HttpServletRequest request, List<CommentSubmit> listCommentSubmit,
        Digg digg, int nIdSubmitDigg, LuteceUser luteceUserConnected ,Plugin plugin)
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );

        model.put( MARK_DIGG, digg );
        model.put( MARK_ID_DIGG, digg.getIdDigg(  ) );
        model.put( MARK_ID_DIGG_SUBMIT, nIdSubmitDigg );
        model.put( MARK_DIGG_COMMENT, CONSTANTE_PARAMETER_TRUE_VALUE );
        model.put( MARK_LIST_COMMENT_SUBMIT_DIGG, getCommentSubmitDisplayList( listCommentSubmit,plugin ) );
        model.put( MARK_DISABLE_NEW_COMMENT_SUBMIT, digg.isDisableNewComment(  ) );
        model.put( MARK_ACTIVE_EDITOR_BBCODE, digg.isActiveEditorBbcode(  ) );
        model.put( MARK_LUTECE_USER_CONNECTED, luteceUserConnected );

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
     * Return the html form for creating digg submit
     *
     * @param request
     *            The HTTP request
     * @param nMode
     *            The current mode.
     * @param plugin
     *            The Plugin
     * @param digg
     *            the digg
     * @param nIdDefaultCategory the id of the default category
     * @return the html form
     * @throws SiteMessageException
     *             SiteMessageException
     */
    private String getHtmlForm( HttpServletRequest request, int nMode, Plugin plugin, Digg digg, int nIdDefaultCategory )
        throws SiteMessageException
    {
        Map<String, Object> model = DiggUtils.getModelHtmlForm( digg, plugin, request.getLocale(  ), nIdDefaultCategory );

        // get form Recap
        model.put( MARK_DISABLE_NEW_DIGG_SUBMIT, digg.isDisableNewDiggSubmit(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_FORM_DIGG, request.getLocale(  ), model );

        return template.getHtml(  );
    }

    /**
     * Return the new digg submit object
     *
     * @param request
     *            The HTTP request
     * @param nMode
     *            The current mode.
     * @param plugin
     *            The Plugin
     * @param digg
     *            the digg
     * @param nIdCategory
     *            the id of the category
     * @param nIdType
     *            the id of the type
     * @param user
     *            the lutece user
     * @return the new digg submit object
     * @throws SiteMessageException
     *             SiteMessageException
     */
    private DiggSubmit doInsertDiggSubmit( HttpServletRequest request, int nMode, Plugin plugin, Digg digg,
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

        // create form response
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
        doInsertResponseInDiggSubmit( request, diggSubmit, nIdCategory, nIdType, plugin );
       
        if ( user != null )
        {
            diggSubmit.setLuteceUserKey( user.getName(  ) );
            //insert DiggSubmitInfi=ormation if not exists
            DiggUserInfoService.getService().updateDiggUserInfoByLuteceUser(user, plugin);
            
        }

        try
        {
            _diggSubmitService.create( diggSubmit, plugin,locale );
        }
        catch ( Exception ex )
        {
            // something very wrong happened... a database check might be needed
            AppLogService.error( ex.getMessage(  ) + " for DiggSubmit " + diggSubmit.getIdDiggSubmit(  ), ex );
            // revert
            // we clear the DB form the given formsubmit (FormSubmitHome also
            // removes the reponses)
            _diggSubmitService.remove( diggSubmit.getIdDiggSubmit(  ), plugin );
            // throw a message to the user
            SiteMessageService.setMessage( request, MESSAGE_MESSAGE_SUBMIT_SAVE_ERROR, SiteMessage.TYPE_ERROR );
        }

        return diggSubmit;
    }

    /**
     * return the new comment submit create
     *
     * @param request
     *            the http request
     * @param diggSubmit
     *            the DiggSubmit
    
     * @param strCommentValueDigg
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
    public CommentSubmit doInsertComment( HttpServletRequest request, DiggSubmit diggSubmit,
        String strCommentValueDigg, Plugin plugin, LuteceUser user, int nIdParentComment )
        throws SiteMessageException
    {
        CommentSubmit commentSubmit = new CommentSubmit(  );

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

        if ( !diggSubmit.getDigg(  ).isDisableNewComment(  ) )
        {
            commentSubmit.setActive( true );
            diggSubmit.setNumberCommentEnable( diggSubmit.getNumberCommentEnable(  ) + 1 );
            _diggSubmitService.update( diggSubmit, plugin );
        }

        commentSubmit.setDateComment( DiggUtils.getCurrentDate(  ) );
        commentSubmit.setDiggSubmit( diggSubmit );
        commentSubmit.setValue( strCommentValueDigg );
        commentSubmit.setOfficialAnswer( false );
        commentSubmit.setIdParent( nIdParentComment );

        if ( user != null )
        {
            commentSubmit.setLuteceUserKey( user.getName(  ) );
          //insert DiggSubmitInfiormation if not exists
            DiggUserInfoService.getService().updateDiggUserInfoByLuteceUser(user, plugin);
        }

        _commentSubmitService.create( commentSubmit, plugin );

        return commentSubmit;
    }

    /**
     * Perform the digg submit
     *
     * @param request
     *            request The HTTP request
     * @param diggSubmit
     *            diggSubmit
     * @param nIdCategory
     *            the category id of the digg submit
     * @param nIdType
     *            the type id
     * @param plugin
     *            the Plugin
     * @throws SiteMessageException
     *             the site Message exception
     */
    public void doInsertResponseInDiggSubmit( HttpServletRequest request, DiggSubmit diggSubmit, int nIdCategory,
        int nIdType, Plugin plugin ) throws SiteMessageException
    {
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

        if ( nIdCategory != DiggUtils.CONSTANT_ID_NULL )
        {
            Category category = CategoryHome.findByPrimaryKey( nIdCategory, plugin );
            diggSubmit.setCategory( category );
        }

        if ( nIdType != DiggUtils.CONSTANT_ID_NULL )
        {
            DiggSubmitType type = DiggSubmitTypeHome.findByPrimaryKey( nIdType, plugin );
            diggSubmit.setDiggSubmitType( type );
        }
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

        if ( _nIdDiggSubmitStatePublish == DiggUtils.CONSTANT_ID_NULL )
        {
            DiggSubmitState diggSubmitStatePublish = DiggSubmitStateHome.findByNumero( DiggSubmit.STATE_PUBLISH, plugin );

            if ( diggSubmitStatePublish != null )
            {
                _nIdDiggSubmitStatePublish = diggSubmitStatePublish.getIdDiggSubmitState(  );
            }
        }

        String strPortalUrl = AppPathService.getPortalUrl(  );

        if ( _urlDiggXpageHome == null )
        {
            _urlDiggXpageHome = new UrlItem( strPortalUrl );
            _urlDiggXpageHome.addParameter( XPageAppService.PARAM_XPAGE_APP,
                AppPropertiesService.getProperty( PROPERTY_PAGE_APPLICATION_ID ) );
        }

        if ( _nNumberShownCharacters == DiggUtils.CONSTANT_ID_NULL )
        {
            _nNumberShownCharacters = AppPropertiesService.getPropertyInt( PROPERTY_NUMBER_DIGG_SUBMIT_VALUE_SHOWN_CHARACTERS,
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
        String strIdDiggSubmitSort = request.getParameter( PARAMETER_ID_DIGG_SUBMIT_SORT );
        String strIdFilterCategory = request.getParameter( PARAMETER_ID_FILTER_CATEGORY_DIGG );
        String strFilterPageIndex = request.getParameter( PARAMETER_FILTER_PAGE_INDEX );

        int nIdFilterPeriod = DiggUtils.getIntegerParameter( strIdFilterPeriod );
        int nIdDiggSubmitSort = DiggUtils.getIntegerParameter( strIdDiggSubmitSort );
        int nIdFilterCategory = DiggUtils.getIntegerParameter( strIdFilterCategory );

        SearchFields searchFields = ( session.getAttribute( SESSION_SEARCH_FIELDS ) != null )
            ? (SearchFields) session.getAttribute( SESSION_SEARCH_FIELDS ) : new SearchFields(  );
        searchFields.setQuery( ( strQuery != null ) ? strQuery : searchFields.getQuery(  ) );
        searchFields.setIdFilterPeriod( ( strIdFilterPeriod != null ) ? nIdFilterPeriod
                                                                      : searchFields.getIdFilterPeriod(  ) );
        searchFields.setIdDiggSubmitSort( ( strIdDiggSubmitSort != null ) ? nIdDiggSubmitSort
                                                                          : searchFields.getIdDiggSubmitSort(  ) );
        searchFields.setIdFilterCategory( ( strIdFilterCategory != null ) ? nIdFilterCategory
                                                                          : searchFields.getIdFilterCategory(  ) );
        searchFields.setPageIndex( ( strFilterPageIndex != null ) ? strFilterPageIndex : searchFields.getPageIndex(  ) );

        // update search Fields in session
        session.setAttribute( SESSION_SEARCH_FIELDS, searchFields );

        return searchFields;
    }

    /**
     *
     * @return the Id of the default Digg
     */
    private int getIdDefaultDigg(  )
    {
        int nIdDefaultDigg;
        DiggFilter filterDefaultDigg = new DiggFilter(  );
        filterDefaultDigg.setIdDefaultDigg( DiggFilter.ID_TRUE );

        List listDefaultDigg = DiggHome.getDiggList( filterDefaultDigg, _plugin );
        nIdDefaultDigg = ( ( listDefaultDigg != null ) && ( listDefaultDigg.size(  ) > 0 ) )
            ? ( (Digg) listDefaultDigg.get( 0 ) ).getIdDigg(  ) : DiggUtils.CONSTANT_ID_NULL;

        return nIdDefaultDigg;
    }

    /**
     * Add the digg  page frameset
     * @param strContentDigg
     * @param request the {@link HttpServletRequest}
     * @param page {@link XPage}
     * @param digg {@link Digg}
     * @param model the model
     * @param searchFields the searchField
     * @param luteceUserConnected the luteceUserConnected
     */
    private void addDiggPageFrameset( String strContentDigg, HttpServletRequest request, XPage page, Digg digg,
        Map<String, Object> model, SearchFields searchFields, LuteceUser luteceUserConnected )
    {
        page.setTitle( I18nService.getLocalizedString( PROPERTY_XPAGE_PAGETITLE, request.getLocale(  ) ) );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_XPAGE_PATHLABEL, request.getLocale(  ) ) );

        if ( digg.isActive(  ) )
        {
            //Filter by comment
            if ( digg.isAuthorizedComment(  ) )
            {
                SubmitFilter submmitFilterTopComment = new SubmitFilter(  );
                submmitFilterTopComment.setIdDigg( digg.getIdDigg(  ) );
                submmitFilterTopComment.setIdDiggSubmitState( _nIdDiggSubmitStatePublish );
                submmitFilterTopComment.setIdCategory(searchFields.getIdFilterCategory());

                DiggUtils.initSubmitFilterBySort( submmitFilterTopComment, SubmitFilter.SORT_BY_NUMBER_COMMENT_DESC );

                List<DiggSubmit> listDiggSubmitTopDay = _diggSubmitService.getDiggSubmitList( submmitFilterTopComment,
                        _plugin, digg.getNumberDiggSubmitInTopComment(  ) );
                model.put( MARK_LIST_SUBMIT_TOP_COMMENT, listDiggSubmitTopDay );
            }

            //Filter by popularity
            SubmitFilter submmitFilterTopPopularity = new SubmitFilter(  );
            submmitFilterTopPopularity.setIdDigg( digg.getIdDigg(  ) );

            DiggUtils.initSubmitFilterBySort( submmitFilterTopPopularity, SubmitFilter.SORT_BY_SCORE_DESC );

            submmitFilterTopPopularity.setIdDiggSubmitState( _nIdDiggSubmitStatePublish );
            submmitFilterTopPopularity.setIdCategory(searchFields.getIdFilterCategory());
            List<DiggSubmit> listDiggSubmitTopPopularity = _diggSubmitService.getDiggSubmitList( submmitFilterTopPopularity,
                    _plugin, digg.getNumberDiggSubmitInTopScore(  ) );
            ReferenceList refListDiggSort = DiggUtils.getRefListDiggSort( request.getLocale(  ) );
            ReferenceList refListFilterByPeriod = DiggUtils.getRefListFilterByPeriod( request.getLocale(  ) );

            //model
            model.put( MARK_ID_DIGG, digg.getIdDigg(  ) );
            model.put( MARK_QUERY, searchFields.getQuery(  ) );
            model.put( MARK_ID_DIGG_SUBMIT_SORT, searchFields.getIdDiggSubmitSort(  ) );
            model.put( MARK_ID_FILTER_PERIOD, searchFields.getIdFilterPeriod(  ) );
            model.put( MARK_ID_FILTER_CATEGORY_DIGG, searchFields.getIdFilterCategory(  ) );
            model.put( MARK_CONTENT_DIGG, strContentDigg );
            model.put( MARK_LABEL_DIGG, digg.getLibelleContribution(  ) );
            model.put( MARK_HEADER_DIGG, digg.getHeader(  ) );
            model.put( MARK_LIST_CATEGORIES_DIGG, digg.getCategories(  ) );
            model.put( MARK_LIST_TYPES, DiggSubmitTypeHome.getList( _plugin ) );
            model.put( MARK_LIST_SUBMIT_TOP_POPULARITY_DIGG, listDiggSubmitTopPopularity );
            model.put( MARK_AUTHORIZED_COMMENT, digg.isAuthorizedComment(  ) );
            model.put( MARK_NUMBER_SHOWN_CHARACTERS, _nNumberShownCharacters );

            model.put( MARK_LIST_DIGG_SUBMIT_SORT, refListDiggSort );
            model.put( MARK_LIST_FILTER_BY_PERIOD, refListFilterByPeriod );

            model.put( MARK_SHOW_CATEGORY_BLOCK, digg.isShowCategoryBlock(  ) );
            model.put( MARK_SHOW_TOP_SCORE_BLOCK, digg.isShowTopScoreBlock(  ) );
            model.put( MARK_SHOW_TOP_COMMENT_BLOCK, digg.isShowTopCommentBlock(  ) );
        }
        else
        {
            model.put( MARK_UNAVAILABILITY_MESSAGE, digg.getUnavailabilityMessage(  ) );
        }

        model.put( MARK_LUTECE_USER_CONNECTED, luteceUserConnected );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_XPAGE_FRAME_DIGG, request.getLocale(  ), model );
        page.setContent( template.getHtml(  ) );
    }

    /**
     * return a new UrlItem Xpage
     * @return a new UrlItem Xpage
     */
    private UrlItem getNewUrlItemPage(  )
    {
        return new UrlItem( _urlDiggXpageHome.getUrl(  ) );
    }
    
    
    /**
     * Test if a user can process action to a digg
     * @param digg the digg
     * @param request the {@link HttpServletRequest}
     * @param user The LuteceUser
     * @throws UserNotSignedException {@link UserNotSignedException}
     * @throws SiteMessageException {@link SiteMessageException}
     */
    void testUserAuthorizationAccess(Digg digg,HttpServletRequest request,LuteceUser user)  throws UserNotSignedException, SiteMessageException
    {
    	
    	if(digg.getRole() != null && ! Digg.ROLE_NONE.equals(digg.getRole()) )
        {
	        if ( user == null )
	        {
	            throw new UserNotSignedException( );
	        }
	        
	        else if(!SecurityService.getInstance().isUserInRole(request, digg.getRole()))
	        {
	        	SiteMessageService.setMessage( request, MESSAGE_ACCESS_DENIED, SiteMessage.TYPE_STOP );
	        	
	        }
        }
    	
    }
    
    

    
    
    
    
    
}
