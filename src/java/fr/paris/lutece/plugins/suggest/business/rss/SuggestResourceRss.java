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
package fr.paris.lutece.plugins.suggest.business.rss;

import fr.paris.lutece.plugins.suggest.business.Category;
import fr.paris.lutece.plugins.suggest.business.CommentSubmit;
import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestFilter;
import fr.paris.lutece.plugins.suggest.business.SuggestHome;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitState;
import fr.paris.lutece.plugins.suggest.business.SubmitFilter;
import fr.paris.lutece.plugins.suggest.service.CommentSubmitService;
import fr.paris.lutece.plugins.suggest.service.SuggestSubmitService;
import fr.paris.lutece.plugins.suggest.service.SuggestPlugin;
import fr.paris.lutece.portal.business.rss.FeedResource;
import fr.paris.lutece.portal.business.rss.FeedResourceImage;
import fr.paris.lutece.portal.business.rss.FeedResourceItem;
import fr.paris.lutece.portal.business.rss.IFeedResource;
import fr.paris.lutece.portal.business.rss.IFeedResourceImage;
import fr.paris.lutece.portal.business.rss.IFeedResourceItem;
import fr.paris.lutece.portal.business.rss.ResourceRss;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/**
 * DirectoryResourceRss.
 */
public class SuggestResourceRss extends ResourceRss
{
    // templates
    /** The Constant TEMPLATE_TASK_EVALUATION_CREATE_CONFIG. */
    private static final String TEMPLATE_TASK_EVALUATION_CREATE_CONFIG = "admin/plugins/suggest/rss/resource_create_config.html";

    /** The Constant TEMPLATE_TASK_EVALUATION_MODIFY_CONFIG. */
    private static final String TEMPLATE_TASK_EVALUATION_MODIFY_CONFIG = "admin/plugins/suggest/rss/resource_modify_config.html";

    /** The Constant TEMPLATE_PUSH_RSS_XML_SUGGEST. */
    private static final String TEMPLATE_PUSH_RSS_XML_SUGGEST = "admin/plugins/suggest/rss/rss_xml_suggest.html";

    /** The Constant TEMPLATE_PUSH_RSS_XML_SUBMIT. */
    private static final String TEMPLATE_PUSH_RSS_XML_SUBMIT = "admin/plugins/suggest/rss/rss_xml_submit.html";

    /** The Constant TEMPLATE_RSS_SUBMIT_DESCRIPTION. */
    private static final String TEMPLATE_RSS_SUBMIT_DESCRIPTION = "admin/plugins/suggest/rss/rss_submit_description.html";

    /** The Constant TEMPLATE_RSS_SUBMIT_ITEM_TITLE. */
    private static final String TEMPLATE_RSS_SUBMIT_ITEM_TITLE = "admin/plugins/suggest/rss/rss_submit_item_title.html";

    /** The Constant TEMPLATE_RSS_SUGGEST_ITEM_TITLE. */
    private static final String TEMPLATE_RSS_SUGGEST_ITEM_TITLE = "admin/plugins/suggest/rss/rss_suggest_item_title.html";

    // JSPs
    // private static final String JSP_MANAGE_COMMENT_SUBMIT = "/jsp/admin/plugins/suggest/ManageCommentSubmit.jsp?id_suggest_submit=";
    // private static final String JSP_MANAGE_SUGGEST_SUBMIT = "/jsp/admin/plugins/suggest/ManageSuggestSubmit.jsp?id_suggest=";

    // Markers
    /** The Constant MARK_SUGGEST_LIST. */
    private static final String MARK_SUGGEST_LIST = "suggest_list";

    /** The Constant MARK_SUGGEST_LIST_DEFAULT_ITEM. */
    private static final String MARK_SUGGEST_LIST_DEFAULT_ITEM = "suggest_list_default_item";

    /** The Constant MARK_RSS_ID_SUGGEST. */
    private static final String MARK_RSS_ID_SUGGEST = "id_suggest";

    /** The Constant MARK_RSS_ID_SUBMIT. */
    private static final String MARK_RSS_ID_SUBMIT = "id_submit";

    /** The Constant MARK_SUGGESTSUBMIT_LIST. */
    private static final String MARK_SUGGESTSUBMIT_LIST = "suggestsubmit_list";

    /** The Constant MARK_EMPTY_SUGGEST. */
    private static final String MARK_EMPTY_SUGGEST = "empty_suggest";

    /** The Constant MARK_RSS_ITEM_TITLE. */
    private static final String MARK_RSS_ITEM_TITLE = "item_title";

    /** The Constant MARK_RSS_ITEM_DESCRIPTION. */
    private static final String MARK_RSS_ITEM_DESCRIPTION = "item_description";

    /** The Constant MARK_RSS_ITEM_DATE. */
    private static final String MARK_RSS_ITEM_DATE = "item_date";

    /** The Constant MARK_SUGGEST_SUBMIT_LIST_DEFAULT_ITEM. */
    private static final String MARK_SUGGEST_SUBMIT_LIST_DEFAULT_ITEM = "suggest_list_submit_default_item";

    /** The Constant MARK_IS_SUGGEST_SUBMIT_RSS. */
    private static final String MARK_IS_SUGGEST_SUBMIT_RSS = "suggest_is_suggest_submit_rss";

    /** The Constant MARK_RSS_SITE_DATE. */
    private static final String MARK_RSS_SITE_DATE = "site_date";

    /** The Constant MARK_RSS_NUMBER_VOTE. */
    private static final String MARK_RSS_NUMBER_VOTE = "number_vote";

    /** The Constant MARK_RSS_SCORE. */
    private static final String MARK_RSS_SCORE = "score";

    /** The Constant MARK_RSS_CATEGORY. */
    private static final String MARK_RSS_CATEGORY = "category";

    /** The Constant MARK_RSS_ITEM_STATE. */
    private static final String MARK_RSS_ITEM_STATE = "state";

    /** The Constant MARK_RSS_ITEM_AUTHOR. */
    private static final String MARK_RSS_ITEM_AUTHOR = "author";

    /** The Constant MARK_RSS_SITE_NAME. */
    private static final String MARK_RSS_SITE_NAME = "site_name";

    /** The Constant MARK_RSS_SITE_ID. */
    private static final String MARK_RSS_SITE_ID = "suggest_id";

    /** The Constant MARK_RSS_FILE_LANGUAGE. */
    private static final String MARK_RSS_FILE_LANGUAGE = "file_language";

    /** The Constant MARK_RSS_SITE_URL. */
    private static final String MARK_RSS_SITE_URL = "site_url";

    /** The Constant MARK_ITEM_LIST. */
    private static final String MARK_ITEM_LIST = "itemList";

    /** The Constant MARK_RSS_SITE_DESCRIPTION. */
    private static final String MARK_RSS_SITE_DESCRIPTION = "site_description";

    /** The Constant MARK_RSS_SITE_STATE. */
    private static final String MARK_RSS_SITE_STATE = "state";

    // Parameters
    /** The Constant PARAMETER_ID_SUGGEST. */
    private static final String PARAMETER_ID_SUGGEST = "id_suggest";

    /** The Constant PARAMETER_RSS_CHOICE. */
    private static final String PARAMETER_RSS_CHOICE = "rss_choice";

    /** The Constant PARAMETER_RSS_CHOICE_SUBMIT. */
    private static final String PARAMETER_RSS_CHOICE_SUBMIT = "suggest_submit";

    /** The Constant PARAMETER_ID_SUGGEST_SUBMIT. */
    private static final String PARAMETER_ID_SUGGEST_SUBMIT = "id_suggest_submit";

    /** The Constant PARAMETER_URL_FEED_IMAGE. */
    private static final String PARAMETER_URL_FEED_IMAGE = "/images/local/skin/valid-rss.png";

    /** The Constant PARAMETER_SUGGEST_COMMENT. */
    private static final String PARAMETER_SUGGEST_COMMENT = "suggest_comment";

    /** The Constant PARAMETER_SUGGEST_DETAIL. */
    private static final String PARAMETER_SUGGEST_DETAIL = "suggest_detail";

    // Properties
    /** The Constant FIELD_ID_SUGGEST. */
    private static final String FIELD_ID_SUGGEST = "suggest.resource_rss.label_suggest";

    /** The Constant FIELD_STATE. */
    private static final String FIELD_STATE = "suggest.resource_rss.state";

    /** The Constant FIELD_STATE_ENABLE. */
    private static final String FIELD_STATE_ENABLE = "suggest.resource_rss.enable";

    /** The Constant FIELD_STATE_DISABLE. */
    private static final String FIELD_STATE_DISABLE = "suggest.resource_rss.disable";

    /** The Constant FORMAT_RSS_DATE. */
    private static final String FORMAT_RSS_DATE = "EEE, d MMM yyyy HH:mm:ss Z";

    // Messages
    /** The Constant MESSAGE_MANDATORY_FIELD. */
    private static final String MESSAGE_MANDATORY_FIELD = "suggest.message.mandatory.field";

    /** The Constant MESSAGE_EMPTY_SUGGEST. */
    private static final String MESSAGE_EMPTY_SUGGEST = "suggest.message.empty_suggest";

    /** The Constant PROPERTY_SITE_LANGUAGE. */
    private static final String PROPERTY_SITE_LANGUAGE = "rss.language";

    /** The Constant PROPERTY_WEBAPP_PROD_URL. */
    private static final String PROPERTY_WEBAPP_PROD_URL = "lutece.prod.url";

    /** The Constant PROPERTY_XPAGE_APPLICATION_ID. */
    private static final String PROPERTY_XPAGE_APPLICATION_ID = "suggest.xpage.applicationId";

    // Constants
    /** The Constant DEFAULT_XPAGE_ID. */
    private static final String DEFAULT_XPAGE_ID = "suggest";

    /** The Constant SLASH. */
    private static final String SLASH = "/";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contentResourceRss( )
    {
        Plugin pluginSuggest = PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME );

        SuggestFilter filter = new SuggestFilter( );
        if ( SuggestHome.getSuggestList( filter, pluginSuggest ).size( ) > 0 )
        {
            return true;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSaveConfig( HttpServletRequest request, Locale locale )
    {
        Plugin pluginSuggest = PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME );

        String stridSuggest = request.getParameter( PARAMETER_ID_SUGGEST );
        String strRssChoice = request.getParameter( PARAMETER_RSS_CHOICE );
        String stridSuggestSubmit = request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT + stridSuggest );

        boolean bisSubmitRss = strRssChoice.equals( PARAMETER_RSS_CHOICE_SUBMIT ) ? true : false;

        if ( stridSuggestSubmit.equals( MARK_EMPTY_SUGGEST ) )
        {
            stridSuggestSubmit = "0";
        }

        SuggestResourceRssConfig config = new SuggestResourceRssConfig( );
        config.setIdRss( this.getId( ) );
        config.setIdSuggest( Integer.parseInt( stridSuggest ) );
        config.setIdSuggestSubmit( Integer.parseInt( stridSuggestSubmit ) );
        config.setSubmitRss( bisSubmitRss );

        SuggestResourceRssConfigHome.create( config, pluginSuggest );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doUpdateConfig( HttpServletRequest request, Locale locale )
    {
        Plugin pluginSuggest = PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME );
        String strIdSuggest = request.getParameter( PARAMETER_ID_SUGGEST );
        String strRssChoice = request.getParameter( PARAMETER_RSS_CHOICE );
        String stridSuggestSubmit = request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT + strIdSuggest );
        boolean bisSubmitRss = strRssChoice.equals( PARAMETER_RSS_CHOICE_SUBMIT ) ? true : false;

        if ( stridSuggestSubmit.equals( MARK_EMPTY_SUGGEST ) )
        {
            stridSuggestSubmit = "0";
        }

        SuggestResourceRssConfig config = new SuggestResourceRssConfig( );
        config.setIdRss( this.getId( ) );
        config.setIdSuggest( Integer.parseInt( strIdSuggest ) );
        config.setIdSuggestSubmit( Integer.parseInt( stridSuggestSubmit ) );
        config.setSubmitRss( bisSubmitRss );

        SuggestResourceRssConfigHome.update( config, pluginSuggest );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doValidateConfigForm( HttpServletRequest request, Locale locale )
    {
        Plugin pluginSuggest = PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME );
        String strError = "";
        String stridSuggest = request.getParameter( PARAMETER_ID_SUGGEST );
        String stridSuggestSubmit = request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT + stridSuggest );
        String strRssChoice = request.getParameter( PARAMETER_RSS_CHOICE );
        boolean bisSubmitRss = strRssChoice.equals( PARAMETER_RSS_CHOICE_SUBMIT ) ? true : false;

        if ( stridSuggest == null )
        {
            strError = FIELD_ID_SUGGEST;
        }

        if ( bisSubmitRss && stridSuggestSubmit.equals( MARK_EMPTY_SUGGEST ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_EMPTY_SUGGEST, AdminMessage.TYPE_STOP );
        }

        if ( !strError.equals( "" ) )
        {
            Object [ ] tabRequiredFields = {
                I18nService.getLocalizedString( strError, locale )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        if ( bisSubmitRss )
        {
            SuggestSubmit suggestSubmit = SuggestSubmitService.getService( ).findByPrimaryKey( Integer.parseInt( stridSuggestSubmit ), false, pluginSuggest );
            this.setName( suggestSubmit.getSuggestSubmitTitle( ) );
            this.setDescription( suggestSubmit.getSuggestSubmitValue( ).replaceAll( "<div[^>]+>", "" ).replaceAll( "</div>", "" ) );
        }
        else
        {
            Suggest suggest = SuggestHome.findByPrimaryKey( Integer.parseInt( stridSuggest ), pluginSuggest );
            this.setName( suggest.getTitle( ) );
            this.setDescription( suggest.getLibelleContribution( ) );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayCreateConfigForm( HttpServletRequest request, Locale locale )
    {
        Plugin pluginSuggest = PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME );

        SuggestFilter filter = new SuggestFilter( );

        List<Suggest> suggestList = SuggestHome.getSuggestList( filter, pluginSuggest );
        ReferenceList referenceSuggest = new ReferenceList( );

        HashMap<String, Object> model = new HashMap<>( );

        Map<String, ReferenceList> suggestMap = new HashMap<>( );

        for ( Suggest suggest : suggestList )
        {
            SubmitFilter submitFilter = new SubmitFilter( );
            submitFilter.setIdSuggest( suggest.getIdSuggest( ) );

            List<SuggestSubmit> suggestSubmitList = SuggestSubmitService.getService( ).getSuggestSubmitList( submitFilter, pluginSuggest );
            ReferenceList referenceSuggestSubmit = new ReferenceList( );

            for ( SuggestSubmit suggestSubmit : suggestSubmitList )
            {
                referenceSuggestSubmit.addItem( suggestSubmit.getIdSuggestSubmit( ), suggestSubmit.getSuggestSubmitValue( ).replaceAll( "<div[^>]+>", "" )
                        .replaceAll( "</div>", "" ) );
            }

            suggestMap.put( String.valueOf( suggest.getIdSuggest( ) ), referenceSuggestSubmit );
            referenceSuggest.addItem( suggest.getIdSuggest( ), suggest.getTitle( ) );
        }

        model.put( MARK_SUGGESTSUBMIT_LIST, suggestMap );
        model.put( MARK_SUGGEST_SUBMIT_LIST_DEFAULT_ITEM, -1 );
        model.put( MARK_SUGGEST_LIST_DEFAULT_ITEM, -1 );
        model.put( MARK_SUGGEST_LIST, referenceSuggest );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_EVALUATION_CREATE_CONFIG, locale, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayModifyConfigForm( HttpServletRequest request, Locale locale )
    {
        Plugin pluginSuggest = PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME );
        SuggestFilter filter = new SuggestFilter( );
        List<Suggest> suggestList = SuggestHome.getSuggestList( filter, pluginSuggest );
        ReferenceList referenceSuggest = new ReferenceList( );

        SuggestResourceRssConfig suggestResourceRssConfig = SuggestResourceRssConfigHome.findByPrimaryKey( this.getId( ), pluginSuggest );
        HashMap<String, Object> model = new HashMap<>( );

        if ( request.getParameter( PARAMETER_ID_SUGGEST ) != null )
        {
            int idSuggest = Integer.parseInt( request.getParameter( PARAMETER_ID_SUGGEST ) );

            model.put( MARK_SUGGEST_LIST_DEFAULT_ITEM, idSuggest );
        }
        else
        {
            model.put( MARK_SUGGEST_LIST_DEFAULT_ITEM, suggestResourceRssConfig.getIdSuggest( ) );
        }

        Map<String, ReferenceList> suggestMap = new HashMap<>( );

        for ( Suggest suggest : suggestList )
        {
            SubmitFilter submitFilter = new SubmitFilter( );
            submitFilter.setIdSuggest( suggest.getIdSuggest( ) );

            List<SuggestSubmit> suggestSubmitList = SuggestSubmitService.getService( ).getSuggestSubmitList( submitFilter, pluginSuggest );
            ReferenceList referenceSuggestSubmit = new ReferenceList( );

            for ( SuggestSubmit suggestSubmit : suggestSubmitList )
            {
                referenceSuggestSubmit.addItem( suggestSubmit.getIdSuggestSubmit( ), suggestSubmit.getSuggestSubmitValue( ).replaceAll( "<div[^>]+>", "" )
                        .replaceAll( "</div>", "" ) );
            }

            suggestMap.put( String.valueOf( suggest.getIdSuggest( ) ), referenceSuggestSubmit );
            referenceSuggest.addItem( suggest.getIdSuggest( ), suggest.getTitle( ) );
        }

        if ( request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT ) != null )
        {
            int idSuggestSubmit = Integer.parseInt( request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT ) );

            model.put( MARK_SUGGEST_SUBMIT_LIST_DEFAULT_ITEM, idSuggestSubmit );
        }
        else
        {
            model.put( MARK_SUGGEST_SUBMIT_LIST_DEFAULT_ITEM, suggestResourceRssConfig.getIdSuggestSubmit( ) );
        }

        model.put( MARK_IS_SUGGEST_SUBMIT_RSS, suggestResourceRssConfig.isSubmitRss( ) );
        model.put( MARK_SUGGESTSUBMIT_LIST, suggestMap );
        model.put( MARK_SUGGEST_LIST, referenceSuggest );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_EVALUATION_MODIFY_CONFIG, locale, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    public String createHtmlRss( )
    {
        HashMap<String, Object> model = new HashMap<>( );
        Plugin pluginSuggestglike = PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME );
        SuggestResourceRssConfig config = SuggestResourceRssConfigHome.findByPrimaryKey( this.getId( ), pluginSuggestglike );

        String strRssFileLanguage = AppPropertiesService.getProperty( PROPERTY_SITE_LANGUAGE );
        Locale locale = new Locale( strRssFileLanguage );

        String strWebAppUrl = AppPropertiesService.getProperty( PROPERTY_WEBAPP_PROD_URL );
        String strSiteUrl = strWebAppUrl;

        if ( config.isSubmitRss( ) )
        {
            // Submit Rss
            SuggestSubmit suggestSubmit = SuggestSubmitService.getService( ).findByPrimaryKey( config.getIdSuggestSubmit( ), false, pluginSuggestglike );
            SuggestSubmitState suggestsubmitStage = suggestSubmit.getSuggestSubmitState( );
            Category category = suggestSubmit.getCategory( );
            model.put( MARK_RSS_SITE_NAME, suggestSubmit.getSuggestSubmitTitle( ) );
            model.put( MARK_RSS_SITE_ID, suggestSubmit.getIdSuggestSubmit( ) );
            model.put( MARK_RSS_FILE_LANGUAGE, strRssFileLanguage );
            model.put( MARK_RSS_SITE_URL, strSiteUrl );

            model.put( MARK_RSS_NUMBER_VOTE, suggestSubmit.getNumberVote( ) );
            model.put( MARK_RSS_SCORE, suggestSubmit.getNumberScore( ) );

            model.put( MARK_RSS_SITE_STATE, suggestsubmitStage.getTitle( ) );
            model.put( MARK_RSS_SITE_DESCRIPTION, suggestSubmit.getSuggestSubmitValue( ).replaceAll( "<div[^>]+>", "" ).replaceAll( "</div>", "" ) );

            if ( category != null )
            {
                model.put( MARK_RSS_CATEGORY, category.getTitle( ) );
            }

            model.put( MARK_RSS_SITE_DATE, new SimpleDateFormat( FORMAT_RSS_DATE, Locale.ENGLISH ).format( suggestSubmit.getDateResponse( ) ) );

            SubmitFilter submitFilter = new SubmitFilter( );
            submitFilter.setIdSuggestSubmit( config.getIdSuggestSubmit( ) );

            List<CommentSubmit> listResultCommentSubmit = CommentSubmitService.getService( ).getCommentSubmitList( submitFilter, pluginSuggestglike );
            List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>( );

            // Descriptino of the comments
            for ( CommentSubmit commentSubmit : listResultCommentSubmit )
            {
                HashMap<String, Object> item = new HashMap<>( );

                if ( commentSubmit.getLuteceUserKey( ) != null )
                {
                    item.put( MARK_RSS_ITEM_AUTHOR, commentSubmit.getLuteceUserKey( ) );
                }
                else
                {
                    item.put( MARK_RSS_ITEM_AUTHOR, null );
                }

                item.put(
                        MARK_RSS_ITEM_STATE,
                        commentSubmit.isActive( ) ? I18nService.getLocalizedString( FIELD_STATE_ENABLE, locale ) : I18nService.getLocalizedString(
                                FIELD_STATE_DISABLE, locale ) );
                item.put( MARK_RSS_ITEM_DESCRIPTION, commentSubmit.getValue( ) );
                item.put( MARK_RSS_ID_SUBMIT, suggestSubmit.getIdSuggestSubmit( ) );
                item.put( MARK_RSS_ITEM_DATE, new SimpleDateFormat( FORMAT_RSS_DATE, Locale.ENGLISH ).format( commentSubmit.getDateComment( ) ) );

                listItem.add( item );
            }

            model.put( MARK_ITEM_LIST, listItem );

            model.put( MARK_RSS_ID_SUGGEST, config.getIdSuggest( ) );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PUSH_RSS_XML_SUBMIT, locale, model );

            return template.getHtml( );
        }
        else
        {
            // Suggest Rss
            Suggest suggest = SuggestHome.findByPrimaryKey( config.getIdSuggest( ), pluginSuggestglike );

            model.put( MARK_RSS_SITE_NAME, suggest.getTitle( ) );
            model.put( MARK_RSS_SITE_ID, suggest.getIdSuggest( ) );
            model.put( MARK_RSS_FILE_LANGUAGE, strRssFileLanguage );
            model.put( MARK_RSS_SITE_URL, strSiteUrl );

            if ( suggest.isActive( ) )
            {
                model.put( MARK_RSS_SITE_DESCRIPTION, I18nService.getLocalizedString( FIELD_STATE_ENABLE, locale ) );
            }
            else
            {
                model.put( MARK_RSS_SITE_DESCRIPTION, I18nService.getLocalizedString( FIELD_STATE_DISABLE, locale ) );
            }

            model.put( MARK_RSS_SITE_DATE, new SimpleDateFormat( FORMAT_RSS_DATE, Locale.ENGLISH ).format( suggest.getDateCreation( ) ) );

            SubmitFilter submitFilter = new SubmitFilter( );
            submitFilter.setIdSuggest( config.getIdSuggest( ) );

            List<SuggestSubmit> listResultSuggestSubmit = SuggestSubmitService.getService( ).getSuggestSubmitList( submitFilter, pluginSuggestglike );
            List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>( );

            // Description of the submits
            for ( SuggestSubmit suggestSubmit : listResultSuggestSubmit )
            {
                HashMap<String, Object> item = new HashMap<>( );
                SuggestSubmitState suggestsubmitState = suggestSubmit.getSuggestSubmitState( );
                Category category = suggestSubmit.getCategory( );

                item.put( MARK_RSS_ITEM_TITLE, suggestSubmit.getSuggestSubmitTitle( ) );
                item.put( MARK_RSS_NUMBER_VOTE, suggestSubmit.getNumberVote( ) );
                item.put( MARK_RSS_SCORE, suggestSubmit.getNumberScore( ) );
                item.put( MARK_RSS_ITEM_STATE, suggestsubmitState.getTitle( ) );

                if ( category != null )
                {
                    item.put( MARK_RSS_CATEGORY, suggestSubmit.getSuggestSubmitTitle( ) );
                }

                item.put( MARK_RSS_ITEM_DESCRIPTION, suggestSubmit.getSuggestSubmitValue( ).replaceAll( "<div[^>]+>", "" ).replaceAll( "</div>", "" ) );
                item.put( MARK_RSS_ID_SUBMIT, suggestSubmit.getIdSuggestSubmit( ) );
                item.put( MARK_RSS_ITEM_DATE, new SimpleDateFormat( FORMAT_RSS_DATE, Locale.ENGLISH ).format( suggestSubmit.getDateResponse( ) ) );
                listItem.add( item );
            }

            model.put( MARK_ITEM_LIST, listItem );

            model.put( MARK_RSS_ID_SUGGEST, config.getIdSuggest( ) );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PUSH_RSS_XML_SUGGEST, locale, model );

            return template.getHtml( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IFeedResource getFeed( )
    {
        String strRssFileLanguage = AppPropertiesService.getProperty( PROPERTY_SITE_LANGUAGE );
        Locale locale = new Locale( strRssFileLanguage );

        String strWebAppUrl = AppPropertiesService.getProperty( PROPERTY_WEBAPP_PROD_URL );
        String strSiteUrl = strWebAppUrl;

        Plugin pluginSuggestglike = PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME );
        SuggestResourceRssConfig config = SuggestResourceRssConfigHome.findByPrimaryKey( this.getId( ), pluginSuggestglike );

        if ( config.isSubmitRss( ) )
        {
            // Submit Rss
            SuggestSubmit suggestSubmit = SuggestSubmitService.getService( ).findByPrimaryKey( config.getIdSuggestSubmit( ), false, pluginSuggestglike );
            SuggestSubmitState suggestsubmitStage = suggestSubmit.getSuggestSubmitState( );
            Category category = suggestSubmit.getCategory( );

            IFeedResource resource = new FeedResource( );
            resource.setTitle( suggestSubmit.getSuggestSubmitTitle( ) );
            /**
             * The link is changed to access to the suggest in FO
             * 
             * @since v2.2.5
             */

            // resource.setLink( strSiteUrl + JSP_MANAGE_SUGGEST_SUBMIT + suggestSubmit.getIdSuggestSubmit( ) );
            resource.setLink( buildUrlSuggest( strSiteUrl, suggestSubmit.getSuggest( ) ) );
            resource.setLanguage( strRssFileLanguage );

            Date date = new Date( suggestSubmit.getDateResponse( ).getTime( ) );
            resource.setDate( date );

            IFeedResourceImage image = new FeedResourceImage( );
            image.setUrl( strSiteUrl + PARAMETER_URL_FEED_IMAGE );
            image.setTitle( suggestSubmit.getSuggestSubmitTitle( ) );
            image.setLink( strSiteUrl );

            resource.setImage( image );

            String strDescription;
            Map<String, Object> model = new HashMap<>( );

            model.put( MARK_RSS_SITE_DESCRIPTION, suggestSubmit.getSuggestSubmitValue( ).replaceAll( "<div[^>]+>", "" ).replaceAll( "</div>", "" ) );
            model.put( MARK_RSS_SITE_STATE, suggestsubmitStage.getTitle( ) );

            if ( category != null )
            {
                model.put( MARK_RSS_CATEGORY, category.getTitle( ) );
            }

            model.put( MARK_RSS_NUMBER_VOTE, suggestSubmit.getNumberVote( ) );
            model.put( MARK_RSS_SCORE, suggestSubmit.getNumberScore( ) );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_RSS_SUBMIT_DESCRIPTION, locale, model );
            strDescription = template.getHtml( );

            resource.setDescription( strDescription );

            SubmitFilter submitFilter = new SubmitFilter( );
            submitFilter.setIdSuggestSubmit( config.getIdSuggestSubmit( ) );

            List<CommentSubmit> listResultCommentSubmit = CommentSubmitService.getService( ).getCommentSubmitList( submitFilter, pluginSuggestglike );
            List<IFeedResourceItem> listItems = new ArrayList<IFeedResourceItem>( );

            // Description of the comments
            for ( CommentSubmit commentSubmit : listResultCommentSubmit )
            {
                IFeedResourceItem item = new FeedResourceItem( );

                String strTitleItem;
                Map<String, Object> model2 = new HashMap<>( );

                if ( commentSubmit.getLuteceUserKey( ) != null )
                {
                    model2.put( MARK_RSS_ITEM_AUTHOR, commentSubmit.getLuteceUserKey( ) );
                }
                else
                {
                    model2.put( MARK_RSS_ITEM_AUTHOR, null );
                }

                model2.put(
                        MARK_RSS_ITEM_STATE,
                        commentSubmit.isActive( ) ? I18nService.getLocalizedString( FIELD_STATE_ENABLE, locale ) : I18nService.getLocalizedString(
                                FIELD_STATE_DISABLE, locale ) );

                HtmlTemplate template2 = AppTemplateService.getTemplate( TEMPLATE_RSS_SUBMIT_ITEM_TITLE, locale, model2 );
                strTitleItem = template2.getHtml( );

                item.setTitle( strTitleItem );
                /**
                 * The link is changed to access to the suggest submit in FO
                 * 
                 * @since v2.2.5
                 */

                // item.setLink( strSiteUrl + JSP_MANAGE_COMMENT_SUBMIT + suggestSubmit.getIdSuggestSubmit( ) );
                item.setLink( buildUrlSuggestSubmit( strSiteUrl, suggestSubmit ) );
                item.setDescription( commentSubmit.getValue( ) );
                item.setDate( commentSubmit.getDateComment( ) );

                listItems.add( item );
            }

            resource.setItems( listItems );

            return resource;
        }
        else
        {
            // Suggest Rss
            Suggest suggest = SuggestHome.findByPrimaryKey( config.getIdSuggest( ), pluginSuggestglike );

            IFeedResource resource = new FeedResource( );
            resource.setTitle( suggest.getTitle( ) );
            /**
             * The link is changed to access to the suggest in FO
             * 
             * @since v2.2.5
             */

            // resource.setLink( strSiteUrl + JSP_MANAGE_SUGGEST_SUBMIT + suggest.getIdSuggest( ) );
            resource.setLink( buildUrlSuggest( strSiteUrl, suggest ) );
            resource.setLanguage( strRssFileLanguage );

            Date date = new Date( suggest.getDateCreation( ).getTime( ) );
            resource.setDate( date );

            IFeedResourceImage image = new FeedResourceImage( );
            image.setUrl( strSiteUrl + PARAMETER_URL_FEED_IMAGE );
            image.setTitle( suggest.getTitle( ) );
            image.setLink( strSiteUrl );

            resource.setImage( image );

            if ( suggest.isActive( ) )
            {
                resource.setDescription( I18nService.getLocalizedString( FIELD_STATE, locale ) + " : "
                        + I18nService.getLocalizedString( FIELD_STATE_ENABLE, locale ) );
            }
            else
            {
                resource.setDescription( I18nService.getLocalizedString( FIELD_STATE, locale ) + " : "
                        + I18nService.getLocalizedString( FIELD_STATE_DISABLE, locale ) );
            }

            SubmitFilter submitFilter = new SubmitFilter( );
            submitFilter.setIdSuggest( config.getIdSuggest( ) );

            List<SuggestSubmit> listResultSuggestSubmit = SuggestSubmitService.getService( ).getSuggestSubmitList( submitFilter, pluginSuggestglike );
            List<IFeedResourceItem> listItems = new ArrayList<IFeedResourceItem>( );

            // Description of the submits
            for ( SuggestSubmit suggestSubmit : listResultSuggestSubmit )
            {
                IFeedResourceItem item = new FeedResourceItem( );

                String strTitle;
                Map<String, Object> model = new HashMap<>( );
                SuggestSubmitState suggestsubmitState = suggestSubmit.getSuggestSubmitState( );
                Category category = suggestSubmit.getCategory( );

                model.put( MARK_RSS_ITEM_TITLE, suggestSubmit.getSuggestSubmitTitle( ) );
                model.put( MARK_RSS_NUMBER_VOTE, suggestSubmit.getNumberVote( ) );
                model.put( MARK_RSS_SCORE, suggestSubmit.getNumberScore( ) );
                model.put( MARK_RSS_ITEM_STATE, suggestsubmitState.getTitle( ) );

                if ( category != null )
                {
                    model.put( MARK_RSS_CATEGORY, suggestSubmit.getSuggestSubmitTitle( ) );
                }

                HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_RSS_SUGGEST_ITEM_TITLE, locale, model );
                strTitle = template.getHtml( );

                item.setTitle( strTitle );
                /**
                 * The link is changed to access to the suggest submit in FO
                 * 
                 * @since v2.2.5
                 */

                // item.setLink( strSiteUrl + JSP_MANAGE_COMMENT_SUBMIT + suggestSubmit.getIdSuggestSubmit( ) );
                item.setLink( buildUrlSuggestSubmit( strSiteUrl, suggestSubmit ) );
                item.setDescription( suggestSubmit.getSuggestSubmitValue( ).replaceAll( "<div[^>]+>", "" ).replaceAll( "</div>", "" ) );
                item.setDate( suggestSubmit.getDateResponse( ) );

                listItems.add( item );
            }

            resource.setItems( listItems );

            return resource;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteResourceRssConfig( int idResourceRss )
    {
        Plugin pluginDirectory = PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME );
        SuggestResourceRssConfigHome.remove( idResourceRss, pluginDirectory );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getParameterToApply( HttpServletRequest request )
    {
        Map<String, String> map = new HashMap<String, String>( );

        map.put( PARAMETER_ID_SUGGEST, request.getParameter( PARAMETER_ID_SUGGEST ) );

        return map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkResource( )
    {
        Plugin pluginDirectory = PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME );
        SuggestResourceRssConfig config = SuggestResourceRssConfigHome.findByPrimaryKey( this.getId( ), pluginDirectory );
        Suggest suggest = SuggestHome.findByPrimaryKey( config.getIdSuggest( ), pluginDirectory );

        return ( suggest != null );
    }

    /**
     * Builds the url suggest submit.
     *
     * @param strSiteUrl
     *            the str site url
     * @param suggestSubmit
     *            the suggest submit
     * @return the string
     */
    private static String buildUrlSuggestSubmit( String strSiteUrl, SuggestSubmit suggestSubmit )
    {
        if ( ( suggestSubmit != null ) && ( suggestSubmit.getSuggest( ) != null ) )
        {
            UrlItem url = new UrlItem( strSiteUrl + SLASH + AppPathService.getPortalUrl( ) );
            url.addParameter( XPageAppService.PARAM_XPAGE_APP, AppPropertiesService.getProperty( PROPERTY_XPAGE_APPLICATION_ID, DEFAULT_XPAGE_ID ) );
            url.addParameter( PARAMETER_ID_SUGGEST, suggestSubmit.getSuggest( ).getIdSuggest( ) );
            url.addParameter( PARAMETER_ID_SUGGEST_SUBMIT, suggestSubmit.getIdSuggestSubmit( ) );
            url.addParameter( PARAMETER_SUGGEST_COMMENT, "1" );
            url.addParameter( PARAMETER_SUGGEST_DETAIL, "1" );

            return url.getUrl( );
        }

        AppLogService.debug( "SuggestResourceRss - SuggestSubmit is null when trying to build the URL." );

        return StringUtils.EMPTY;
    }

    /**
     * Builds the url suggest submit.
     *
     * @param strSiteUrl
     *            the str site url
     * @param suggest
     *            the suggest
     * @return the string
     */
    private static String buildUrlSuggest( String strSiteUrl, Suggest suggest )
    {
        if ( suggest != null )
        {
            UrlItem url = new UrlItem( strSiteUrl + SLASH + AppPathService.getPortalUrl( ) );
            url.addParameter( XPageAppService.PARAM_XPAGE_APP, AppPropertiesService.getProperty( PROPERTY_XPAGE_APPLICATION_ID, DEFAULT_XPAGE_ID ) );
            url.addParameter( PARAMETER_ID_SUGGEST, suggest.getIdSuggest( ) );

            return url.getUrl( );
        }

        AppLogService.debug( "SuggestResourceRss - Suggest is null when trying to build the URL." );

        return StringUtils.EMPTY;
    }
}
