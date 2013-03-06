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
package fr.paris.lutece.plugins.digglike.business.rss;

import fr.paris.lutece.plugins.digglike.business.Category;
import fr.paris.lutece.plugins.digglike.business.CommentSubmit;
import fr.paris.lutece.plugins.digglike.business.CommentSubmitHome;
import fr.paris.lutece.plugins.digglike.business.Digg;
import fr.paris.lutece.plugins.digglike.business.DiggFilter;
import fr.paris.lutece.plugins.digglike.business.DiggHome;
import fr.paris.lutece.plugins.digglike.business.DiggSubmit;
import fr.paris.lutece.plugins.digglike.business.DiggSubmitHome;
import fr.paris.lutece.plugins.digglike.business.DiggSubmitState;
import fr.paris.lutece.plugins.digglike.business.SubmitFilter;
import fr.paris.lutece.plugins.digglike.service.DigglikePlugin;
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

import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * DirectoryResourceRss.
 */
public class DiggResourceRss extends ResourceRss
{
    // templates
    /** The Constant TEMPLATE_TASK_EVALUATION_CREATE_CONFIG. */
    private static final String TEMPLATE_TASK_EVALUATION_CREATE_CONFIG = "admin/plugins/digglike/rss/resource_create_config.html";

    /** The Constant TEMPLATE_TASK_EVALUATION_MODIFY_CONFIG. */
    private static final String TEMPLATE_TASK_EVALUATION_MODIFY_CONFIG = "admin/plugins/digglike/rss/resource_modify_config.html";

    /** The Constant TEMPLATE_PUSH_RSS_XML_DIGG. */
    private static final String TEMPLATE_PUSH_RSS_XML_DIGG = "admin/plugins/digglike/rss/rss_xml_digg.html";

    /** The Constant TEMPLATE_PUSH_RSS_XML_SUBMIT. */
    private static final String TEMPLATE_PUSH_RSS_XML_SUBMIT = "admin/plugins/digglike/rss/rss_xml_submit.html";

    /** The Constant TEMPLATE_RSS_SUBMIT_DESCRIPTION. */
    private static final String TEMPLATE_RSS_SUBMIT_DESCRIPTION = "admin/plugins/digglike/rss/rss_submit_description.html";

    /** The Constant TEMPLATE_RSS_SUBMIT_ITEM_TITLE. */
    private static final String TEMPLATE_RSS_SUBMIT_ITEM_TITLE = "admin/plugins/digglike/rss/rss_submit_item_title.html";

    /** The Constant TEMPLATE_RSS_DIGG_ITEM_TITLE. */
    private static final String TEMPLATE_RSS_DIGG_ITEM_TITLE = "admin/plugins/digglike/rss/rss_digg_item_title.html";

    // JSPs
    // private static final String JSP_MANAGE_COMMENT_SUBMIT = "/jsp/admin/plugins/digglike/ManageCommentSubmit.jsp?id_digg_submit=";
    // private static final String JSP_MANAGE_DIGG_SUBMIT = "/jsp/admin/plugins/digglike/ManageDiggSubmit.jsp?id_digg=";

    // Markers
    /** The Constant MARK_DIGG_LIST. */
    private static final String MARK_DIGG_LIST = "digglike_list";

    /** The Constant MARK_DIGG_LIST_DEFAULT_ITEM. */
    private static final String MARK_DIGG_LIST_DEFAULT_ITEM = "digglike_list_default_item";

    /** The Constant MARK_RSS_ID_DIGG. */
    private static final String MARK_RSS_ID_DIGG = "id_digg";

    /** The Constant MARK_RSS_ID_SUBMIT. */
    private static final String MARK_RSS_ID_SUBMIT = "id_submit";

    /** The Constant MARK_DIGGSUBMIT_LIST. */
    private static final String MARK_DIGGSUBMIT_LIST = "diggsubmit_list";

    /** The Constant MARK_EMPTY_DIGG. */
    private static final String MARK_EMPTY_DIGG = "empty_digg";

    /** The Constant MARK_RSS_ITEM_TITLE. */
    private static final String MARK_RSS_ITEM_TITLE = "item_title";

    /** The Constant MARK_RSS_ITEM_DESCRIPTION. */
    private static final String MARK_RSS_ITEM_DESCRIPTION = "item_description";

    /** The Constant MARK_RSS_ITEM_DATE. */
    private static final String MARK_RSS_ITEM_DATE = "item_date";

    /** The Constant MARK_DIGG_SUBMIT_LIST_DEFAULT_ITEM. */
    private static final String MARK_DIGG_SUBMIT_LIST_DEFAULT_ITEM = "digglike_list_submit_default_item";

    /** The Constant MARK_IS_DIGG_SUBMIT_RSS. */
    private static final String MARK_IS_DIGG_SUBMIT_RSS = "digglike_is_digg_submit_rss";

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
    private static final String MARK_RSS_SITE_ID = "digg_id";

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
    /** The Constant PARAMETER_ID_DIGG. */
    private static final String PARAMETER_ID_DIGG = "id_digg";

    /** The Constant PARAMETER_RSS_CHOICE. */
    private static final String PARAMETER_RSS_CHOICE = "rss_choice";

    /** The Constant PARAMETER_RSS_CHOICE_SUBMIT. */
    private static final String PARAMETER_RSS_CHOICE_SUBMIT = "digg_submit";

    /** The Constant PARAMETER_ID_DIGG_SUBMIT. */
    private static final String PARAMETER_ID_DIGG_SUBMIT = "id_digg_submit";

    /** The Constant PARAMETER_URL_FEED_IMAGE. */
    private static final String PARAMETER_URL_FEED_IMAGE = "/images/local/skin/valid-rss.png";

    /** The Constant PARAMETER_DIGG_COMMENT. */
    private static final String PARAMETER_DIGG_COMMENT = "digg_comment";

    /** The Constant PARAMETER_DIGG_DETAIL. */
    private static final String PARAMETER_DIGG_DETAIL = "digg_detail";

    // Properties
    /** The Constant FIELD_ID_DIGG. */
    private static final String FIELD_ID_DIGG = "digglike.resource_rss.label_digg";

    /** The Constant FIELD_STATE. */
    private static final String FIELD_STATE = "digglike.resource_rss.state";

    /** The Constant FIELD_STATE_ENABLE. */
    private static final String FIELD_STATE_ENABLE = "digglike.resource_rss.enable";

    /** The Constant FIELD_STATE_DISABLE. */
    private static final String FIELD_STATE_DISABLE = "digglike.resource_rss.disable";

    /** The Constant FORMAT_RSS_DATE. */
    private static final String FORMAT_RSS_DATE = "EEE, d MMM yyyy HH:mm:ss Z";

    // Messages
    /** The Constant MESSAGE_MANDATORY_FIELD. */
    private static final String MESSAGE_MANDATORY_FIELD = "digglike.message.mandatory.field";

    /** The Constant MESSAGE_EMPTY_DIGG. */
    private static final String MESSAGE_EMPTY_DIGG = "digglike.message.empty_digg";

    /** The Constant PROPERTY_SITE_LANGUAGE. */
    private static final String PROPERTY_SITE_LANGUAGE = "rss.language";

    /** The Constant PROPERTY_WEBAPP_PROD_URL. */
    private static final String PROPERTY_WEBAPP_PROD_URL = "lutece.prod.url";

    /** The Constant PROPERTY_XPAGE_APPLICATION_ID. */
    private static final String PROPERTY_XPAGE_APPLICATION_ID = "digglike.xpage.applicationId";

    // Constants
    /** The Constant DEFAULT_XPAGE_ID. */
    private static final String DEFAULT_XPAGE_ID = "digg";

    /** The Constant SLASH. */
    private static final String SLASH = "/";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contentResourceRss(  )
    {
        Plugin pluginDigglike = PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME );

        DiggFilter filter = new DiggFilter(  );

        if ( DiggHome.getDiggList( filter, pluginDigglike ) != null )
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
        Plugin pluginDigglike = PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME );

        String stridDigg = request.getParameter( PARAMETER_ID_DIGG );
        String strRssChoice = request.getParameter( PARAMETER_RSS_CHOICE );
        String stridDiggSubmit = request.getParameter( PARAMETER_ID_DIGG_SUBMIT + stridDigg );

        boolean bisSubmitRss = strRssChoice.equals( PARAMETER_RSS_CHOICE_SUBMIT ) ? true : false;

        if ( stridDiggSubmit.equals( MARK_EMPTY_DIGG ) )
        {
            stridDiggSubmit = "0";
        }

        DiggResourceRssConfig config = new DiggResourceRssConfig(  );
        config.setIdRss( this.getId(  ) );
        config.setIdDigg( Integer.parseInt( stridDigg ) );
        config.setIdDiggSubmit( Integer.parseInt( stridDiggSubmit ) );
        config.setSubmitRss( bisSubmitRss );

        DiggResourceRssConfigHome.create( config, pluginDigglike );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doUpdateConfig( HttpServletRequest request, Locale locale )
    {
        Plugin pluginDigglike = PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME );
        String strIdDigg = request.getParameter( PARAMETER_ID_DIGG );
        String strRssChoice = request.getParameter( PARAMETER_RSS_CHOICE );
        String stridDiggSubmit = request.getParameter( PARAMETER_ID_DIGG_SUBMIT + strIdDigg );
        boolean bisSubmitRss = strRssChoice.equals( PARAMETER_RSS_CHOICE_SUBMIT ) ? true : false;

        if ( stridDiggSubmit.equals( MARK_EMPTY_DIGG ) )
        {
            stridDiggSubmit = "0";
        }

        DiggResourceRssConfig config = new DiggResourceRssConfig(  );
        config.setIdRss( this.getId(  ) );
        config.setIdDigg( Integer.parseInt( strIdDigg ) );
        config.setIdDiggSubmit( Integer.parseInt( stridDiggSubmit ) );
        config.setSubmitRss( bisSubmitRss );

        DiggResourceRssConfigHome.update( config, pluginDigglike );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doValidateConfigForm( HttpServletRequest request, Locale locale )
    {
        Plugin pluginDigglike = PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME );
        String strError = "";
        String stridDigg = request.getParameter( PARAMETER_ID_DIGG );
        String stridDiggSubmit = request.getParameter( PARAMETER_ID_DIGG_SUBMIT + stridDigg );
        String strRssChoice = request.getParameter( PARAMETER_RSS_CHOICE );
        boolean bisSubmitRss = strRssChoice.equals( PARAMETER_RSS_CHOICE_SUBMIT ) ? true : false;

        if ( stridDigg == null )
        {
            strError = FIELD_ID_DIGG;
        }

        if ( bisSubmitRss && stridDiggSubmit.equals( MARK_EMPTY_DIGG ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_EMPTY_DIGG, AdminMessage.TYPE_STOP );
        }

        if ( !strError.equals( "" ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        if ( bisSubmitRss )
        {
            DiggSubmit diggSubmit = DiggSubmitHome.findByPrimaryKey( Integer.parseInt( stridDiggSubmit ), pluginDigglike );
            this.setName( diggSubmit.getDiggSubmitTitle(  ) );
            this.setDescription( diggSubmit.getDiggSubmitValue(  ).replaceAll( "<div[^>]+>", "" )
                                           .replaceAll( "</div>", "" ) );
        }
        else
        {
            Digg digg = DiggHome.findByPrimaryKey( Integer.parseInt( stridDigg ), pluginDigglike );
            this.setName( digg.getTitle(  ) );
            this.setDescription( digg.getLibelleContribution(  ) );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayCreateConfigForm( HttpServletRequest request, Locale locale )
    {
        Plugin pluginDigglike = PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME );

        DiggFilter filter = new DiggFilter(  );

        List<Digg> diggList = DiggHome.getDiggList( filter, pluginDigglike );
        ReferenceList referenceDigg = new ReferenceList(  );

        HashMap<String, Object> model = new HashMap<String, Object>(  );

        Map<String, ReferenceList> diggMap = new HashMap<String, ReferenceList>(  );

        for ( Digg digg : diggList )
        {
            SubmitFilter submitFilter = new SubmitFilter(  );
            submitFilter.setIdDigg( digg.getIdDigg(  ) );

            List<DiggSubmit> diggSubmitList = DiggSubmitHome.getDiggSubmitList( submitFilter, pluginDigglike );
            ReferenceList referenceDiggSubmit = new ReferenceList(  );

            for ( DiggSubmit diggSubmit : diggSubmitList )
            {
                referenceDiggSubmit.addItem( diggSubmit.getIdDiggSubmit(  ),
                    diggSubmit.getDiggSubmitValue(  ).replaceAll( "<div[^>]+>", "" ).replaceAll( "</div>", "" ) );
            }

            diggMap.put( String.valueOf( digg.getIdDigg(  ) ), referenceDiggSubmit );
            referenceDigg.addItem( digg.getIdDigg(  ), digg.getTitle(  ) );
        }

        model.put( MARK_DIGGSUBMIT_LIST, diggMap );
        model.put( MARK_DIGG_SUBMIT_LIST_DEFAULT_ITEM, -1 );
        model.put( MARK_DIGG_LIST_DEFAULT_ITEM, -1 );
        model.put( MARK_DIGG_LIST, referenceDigg );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_EVALUATION_CREATE_CONFIG, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayModifyConfigForm( HttpServletRequest request, Locale locale )
    {
        Plugin pluginDigglike = PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME );
        DiggFilter filter = new DiggFilter(  );
        List<Digg> diggList = DiggHome.getDiggList( filter, pluginDigglike );
        ReferenceList referenceDigg = new ReferenceList(  );

        DiggResourceRssConfig diggResourceRssConfig = DiggResourceRssConfigHome.findByPrimaryKey( this.getId(  ),
                pluginDigglike );
        HashMap<String, Object> model = new HashMap<String, Object>(  );

        if ( request.getParameter( PARAMETER_ID_DIGG ) != null )
        {
            int idDigg = Integer.parseInt( request.getParameter( PARAMETER_ID_DIGG ) );

            model.put( MARK_DIGG_LIST_DEFAULT_ITEM, idDigg );
        }
        else
        {
            model.put( MARK_DIGG_LIST_DEFAULT_ITEM, diggResourceRssConfig.getIdDigg(  ) );
        }

        Map<String, ReferenceList> diggMap = new HashMap<String, ReferenceList>(  );

        for ( Digg digg : diggList )
        {
            SubmitFilter submitFilter = new SubmitFilter(  );
            submitFilter.setIdDigg( digg.getIdDigg(  ) );

            List<DiggSubmit> diggSubmitList = DiggSubmitHome.getDiggSubmitList( submitFilter, pluginDigglike );
            ReferenceList referenceDiggSubmit = new ReferenceList(  );

            for ( DiggSubmit diggSubmit : diggSubmitList )
            {
                referenceDiggSubmit.addItem( diggSubmit.getIdDiggSubmit(  ),
                    diggSubmit.getDiggSubmitValue(  ).replaceAll( "<div[^>]+>", "" ).replaceAll( "</div>", "" ) );
            }

            diggMap.put( String.valueOf( digg.getIdDigg(  ) ), referenceDiggSubmit );
            referenceDigg.addItem( digg.getIdDigg(  ), digg.getTitle(  ) );
        }

        if ( request.getParameter( PARAMETER_ID_DIGG_SUBMIT ) != null )
        {
            int idDiggSubmit = Integer.parseInt( request.getParameter( PARAMETER_ID_DIGG_SUBMIT ) );

            model.put( MARK_DIGG_SUBMIT_LIST_DEFAULT_ITEM, idDiggSubmit );
        }
        else
        {
            model.put( MARK_DIGG_SUBMIT_LIST_DEFAULT_ITEM, diggResourceRssConfig.getIdDiggSubmit(  ) );
        }

        model.put( MARK_IS_DIGG_SUBMIT_RSS, diggResourceRssConfig.isSubmitRss(  ) );
        model.put( MARK_DIGGSUBMIT_LIST, diggMap );
        model.put( MARK_DIGG_LIST, referenceDigg );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_EVALUATION_MODIFY_CONFIG, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String createHtmlRss(  )
    {
        HashMap<String, Object> model = new HashMap<String, Object>(  );
        Plugin pluginDiggglike = PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME );
        DiggResourceRssConfig config = DiggResourceRssConfigHome.findByPrimaryKey( this.getId(  ), pluginDiggglike );

        String strRssFileLanguage = AppPropertiesService.getProperty( PROPERTY_SITE_LANGUAGE );
        Locale locale = new Locale( strRssFileLanguage );

        String strWebAppUrl = AppPropertiesService.getProperty( PROPERTY_WEBAPP_PROD_URL );
        String strSiteUrl = strWebAppUrl;

        if ( config.isSubmitRss(  ) )
        {
            //Submit Rss
            DiggSubmit diggSubmit = DiggSubmitHome.findByPrimaryKey( config.getIdDiggSubmit(  ), pluginDiggglike );
            DiggSubmitState diggsubmitStage = diggSubmit.getDiggSubmitState(  );
            Category category = diggSubmit.getCategory(  );
            model.put( MARK_RSS_SITE_NAME, diggSubmit.getDiggSubmitTitle(  ) );
            model.put( MARK_RSS_SITE_ID, diggSubmit.getIdDiggSubmit(  ) );
            model.put( MARK_RSS_FILE_LANGUAGE, strRssFileLanguage );
            model.put( MARK_RSS_SITE_URL, strSiteUrl );

            model.put( MARK_RSS_NUMBER_VOTE, diggSubmit.getNumberVote(  ) );
            model.put( MARK_RSS_SCORE, diggSubmit.getNumberScore(  ) );

            model.put( MARK_RSS_SITE_STATE, diggsubmitStage.getTitle(  ) );
            model.put( MARK_RSS_SITE_DESCRIPTION,
                diggSubmit.getDiggSubmitValue(  ).replaceAll( "<div[^>]+>", "" ).replaceAll( "</div>", "" ) );

            if ( category != null )
            {
                model.put( MARK_RSS_CATEGORY, category.getTitle(  ) );
            }

            model.put( MARK_RSS_SITE_DATE,
                new SimpleDateFormat( FORMAT_RSS_DATE, Locale.ENGLISH ).format( diggSubmit.getDateResponse(  ) ) );

            SubmitFilter submitFilter = new SubmitFilter(  );
            submitFilter.setIdDiggSubmit( config.getIdDiggSubmit(  ) );

            List<CommentSubmit> listResultCommentSubmit = CommentSubmitHome.getCommentSubmitList( submitFilter,
                    pluginDiggglike );
            List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>(  );

            //Descriptino of the comments
            for ( CommentSubmit commentSubmit : listResultCommentSubmit )
            {
                HashMap<String, Object> item = new HashMap<String, Object>(  );

                if ( commentSubmit.getLuteceUserKey(  ) != null )
                {
                    item.put( MARK_RSS_ITEM_AUTHOR, commentSubmit.getLuteceUserKey(  ) );
                }
                else
                {
                    item.put( MARK_RSS_ITEM_AUTHOR, null );
                }

                item.put( MARK_RSS_ITEM_STATE,
                    commentSubmit.isActive(  ) ? I18nService.getLocalizedString( FIELD_STATE_ENABLE, locale )
                                               : I18nService.getLocalizedString( FIELD_STATE_DISABLE, locale ) );
                item.put( MARK_RSS_ITEM_DESCRIPTION, commentSubmit.getValue(  ) );
                item.put( MARK_RSS_ID_SUBMIT, diggSubmit.getIdDiggSubmit(  ) );
                item.put( MARK_RSS_ITEM_DATE,
                    new SimpleDateFormat( FORMAT_RSS_DATE, Locale.ENGLISH ).format( commentSubmit.getDateComment(  ) ) );

                listItem.add( item );
            }

            model.put( MARK_ITEM_LIST, listItem );

            model.put( MARK_RSS_ID_DIGG, config.getIdDigg(  ) );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PUSH_RSS_XML_SUBMIT, locale, model );

            return template.getHtml(  );
        }
        else
        {
            //Digg Rss
            Digg digg = DiggHome.findByPrimaryKey( config.getIdDigg(  ), pluginDiggglike );

            model.put( MARK_RSS_SITE_NAME, digg.getTitle(  ) );
            model.put( MARK_RSS_SITE_ID, digg.getIdDigg(  ) );
            model.put( MARK_RSS_FILE_LANGUAGE, strRssFileLanguage );
            model.put( MARK_RSS_SITE_URL, strSiteUrl );

            if ( digg.isActive(  ) )
            {
                model.put( MARK_RSS_SITE_DESCRIPTION, I18nService.getLocalizedString( FIELD_STATE_ENABLE, locale ) );
            }
            else
            {
                model.put( MARK_RSS_SITE_DESCRIPTION, I18nService.getLocalizedString( FIELD_STATE_DISABLE, locale ) );
            }

            model.put( MARK_RSS_SITE_DATE,
                new SimpleDateFormat( FORMAT_RSS_DATE, Locale.ENGLISH ).format( digg.getDateCreation(  ) ) );

            SubmitFilter submitFilter = new SubmitFilter(  );
            submitFilter.setIdDigg( config.getIdDigg(  ) );

            List<DiggSubmit> listResultDiggSubmit = DiggSubmitHome.getDiggSubmitList( submitFilter, pluginDiggglike );
            List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>(  );

            //Description of  the submits
            for ( DiggSubmit diggSubmit : listResultDiggSubmit )
            {
                HashMap<String, Object> item = new HashMap<String, Object>(  );
                DiggSubmitState diggsubmitState = diggSubmit.getDiggSubmitState(  );
                Category category = diggSubmit.getCategory(  );

                item.put( MARK_RSS_ITEM_TITLE, diggSubmit.getDiggSubmitTitle(  ) );
                item.put( MARK_RSS_NUMBER_VOTE, diggSubmit.getNumberVote(  ) );
                item.put( MARK_RSS_SCORE, diggSubmit.getNumberScore(  ) );
                item.put( MARK_RSS_ITEM_STATE, diggsubmitState.getTitle(  ) );

                if ( category != null )
                {
                    item.put( MARK_RSS_CATEGORY, diggSubmit.getDiggSubmitTitle(  ) );
                }

                item.put( MARK_RSS_ITEM_DESCRIPTION,
                    diggSubmit.getDiggSubmitValue(  ).replaceAll( "<div[^>]+>", "" ).replaceAll( "</div>", "" ) );
                item.put( MARK_RSS_ID_SUBMIT, diggSubmit.getIdDiggSubmit(  ) );
                item.put( MARK_RSS_ITEM_DATE,
                    new SimpleDateFormat( FORMAT_RSS_DATE, Locale.ENGLISH ).format( diggSubmit.getDateResponse(  ) ) );
                listItem.add( item );
            }

            model.put( MARK_ITEM_LIST, listItem );

            model.put( MARK_RSS_ID_DIGG, config.getIdDigg(  ) );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PUSH_RSS_XML_DIGG, locale, model );

            return template.getHtml(  );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IFeedResource getFeed(  )
    {
        String strRssFileLanguage = AppPropertiesService.getProperty( PROPERTY_SITE_LANGUAGE );
        Locale locale = new Locale( strRssFileLanguage );

        String strWebAppUrl = AppPropertiesService.getProperty( PROPERTY_WEBAPP_PROD_URL );
        String strSiteUrl = strWebAppUrl;

        Plugin pluginDiggglike = PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME );
        DiggResourceRssConfig config = DiggResourceRssConfigHome.findByPrimaryKey( this.getId(  ), pluginDiggglike );

        if ( config.isSubmitRss(  ) )
        {
            //Submit Rss
            DiggSubmit diggSubmit = DiggSubmitHome.findByPrimaryKey( config.getIdDiggSubmit(  ), pluginDiggglike );
            DiggSubmitState diggsubmitStage = diggSubmit.getDiggSubmitState(  );
            Category category = diggSubmit.getCategory(  );

            IFeedResource resource = new FeedResource(  );
            resource.setTitle( diggSubmit.getDiggSubmitTitle(  ) );
            /**
                     * The link is changed to access to the digg in FO
                     * @since v2.2.5
                     */

            // resource.setLink( strSiteUrl + JSP_MANAGE_DIGG_SUBMIT + diggSubmit.getIdDiggSubmit( ) );
            resource.setLink( buildUrlDigg( strSiteUrl, diggSubmit.getDigg(  ) ) );
            resource.setLanguage( strRssFileLanguage );

            Date date = new Date( diggSubmit.getDateResponse(  ).getTime(  ) );
            resource.setDate( date );

            IFeedResourceImage image = new FeedResourceImage(  );
            image.setUrl( strSiteUrl + PARAMETER_URL_FEED_IMAGE );
            image.setTitle( diggSubmit.getDiggSubmitTitle(  ) );
            image.setLink( strSiteUrl );

            resource.setImage( image );

            String strDescription;
            Map<String, Object> model = new HashMap<String, Object>(  );

            model.put( MARK_RSS_SITE_DESCRIPTION,
                diggSubmit.getDiggSubmitValue(  ).replaceAll( "<div[^>]+>", "" ).replaceAll( "</div>", "" ) );
            model.put( MARK_RSS_SITE_STATE, diggsubmitStage.getTitle(  ) );

            if ( category != null )
            {
                model.put( MARK_RSS_CATEGORY, category.getTitle(  ) );
            }

            model.put( MARK_RSS_NUMBER_VOTE, diggSubmit.getNumberVote(  ) );
            model.put( MARK_RSS_SCORE, diggSubmit.getNumberScore(  ) );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_RSS_SUBMIT_DESCRIPTION, locale, model );
            strDescription = template.getHtml(  );

            resource.setDescription( strDescription );

            SubmitFilter submitFilter = new SubmitFilter(  );
            submitFilter.setIdDiggSubmit( config.getIdDiggSubmit(  ) );

            List<CommentSubmit> listResultCommentSubmit = CommentSubmitHome.getCommentSubmitList( submitFilter,
                    pluginDiggglike );
            List<IFeedResourceItem> listItems = new ArrayList<IFeedResourceItem>(  );

            //Description of the comments	
            for ( CommentSubmit commentSubmit : listResultCommentSubmit )
            {
                IFeedResourceItem item = new FeedResourceItem(  );

                String strTitleItem;
                Map<String, Object> model2 = new HashMap<String, Object>(  );

                if ( commentSubmit.getLuteceUserKey(  ) != null )
                {
                    model2.put( MARK_RSS_ITEM_AUTHOR, commentSubmit.getLuteceUserKey(  ) );
                }
                else
                {
                    model2.put( MARK_RSS_ITEM_AUTHOR, null );
                }

                model2.put( MARK_RSS_ITEM_STATE,
                    commentSubmit.isActive(  ) ? I18nService.getLocalizedString( FIELD_STATE_ENABLE, locale )
                                               : I18nService.getLocalizedString( FIELD_STATE_DISABLE, locale ) );

                HtmlTemplate template2 = AppTemplateService.getTemplate( TEMPLATE_RSS_SUBMIT_ITEM_TITLE, locale, model2 );
                strTitleItem = template2.getHtml(  );

                item.setTitle( strTitleItem );
                /**
                         * The link is changed to access to the digg submit in FO
                         * @since v2.2.5
                         */

                // item.setLink( strSiteUrl + JSP_MANAGE_COMMENT_SUBMIT + diggSubmit.getIdDiggSubmit( ) );
                item.setLink( buildUrlDiggSubmit( strSiteUrl, diggSubmit ) );
                item.setDescription( commentSubmit.getValue(  ) );
                item.setDate( commentSubmit.getDateComment(  ) );

                listItems.add( item );
            }

            resource.setItems( listItems );

            return resource;
        }
        else
        {
            //Digg Rss
            Digg digg = DiggHome.findByPrimaryKey( config.getIdDigg(  ), pluginDiggglike );

            IFeedResource resource = new FeedResource(  );
            resource.setTitle( digg.getTitle(  ) );
            /**
                     * The link is changed to access to the digg in FO
                     * @since v2.2.5
                     */

            // resource.setLink( strSiteUrl + JSP_MANAGE_DIGG_SUBMIT + digg.getIdDigg(  ) );
            resource.setLink( buildUrlDigg( strSiteUrl, digg ) );
            resource.setLanguage( strRssFileLanguage );

            Date date = new Date( digg.getDateCreation(  ).getTime(  ) );
            resource.setDate( date );

            IFeedResourceImage image = new FeedResourceImage(  );
            image.setUrl( strSiteUrl + PARAMETER_URL_FEED_IMAGE );
            image.setTitle( digg.getTitle(  ) );
            image.setLink( strSiteUrl );

            resource.setImage( image );

            if ( digg.isActive(  ) )
            {
                resource.setDescription( I18nService.getLocalizedString( FIELD_STATE, locale ) + " : " +
                    I18nService.getLocalizedString( FIELD_STATE_ENABLE, locale ) );
            }
            else
            {
                resource.setDescription( I18nService.getLocalizedString( FIELD_STATE, locale ) + " : " +
                    I18nService.getLocalizedString( FIELD_STATE_DISABLE, locale ) );
            }

            SubmitFilter submitFilter = new SubmitFilter(  );
            submitFilter.setIdDigg( config.getIdDigg(  ) );

            List<DiggSubmit> listResultDiggSubmit = DiggSubmitHome.getDiggSubmitList( submitFilter, pluginDiggglike );
            List<IFeedResourceItem> listItems = new ArrayList<IFeedResourceItem>(  );

            //Description of  the submits
            for ( DiggSubmit diggSubmit : listResultDiggSubmit )
            {
                IFeedResourceItem item = new FeedResourceItem(  );

                String strTitle;
                Map<String, Object> model = new HashMap<String, Object>(  );
                DiggSubmitState diggsubmitState = diggSubmit.getDiggSubmitState(  );
                Category category = diggSubmit.getCategory(  );

                model.put( MARK_RSS_ITEM_TITLE, diggSubmit.getDiggSubmitTitle(  ) );
                model.put( MARK_RSS_NUMBER_VOTE, diggSubmit.getNumberVote(  ) );
                model.put( MARK_RSS_SCORE, diggSubmit.getNumberScore(  ) );
                model.put( MARK_RSS_ITEM_STATE, diggsubmitState.getTitle(  ) );

                if ( category != null )
                {
                    model.put( MARK_RSS_CATEGORY, diggSubmit.getDiggSubmitTitle(  ) );
                }

                HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_RSS_DIGG_ITEM_TITLE, locale, model );
                strTitle = template.getHtml(  );

                item.setTitle( strTitle );
                /**
                         * The link is changed to access to the digg submit in FO
                         * @since v2.2.5
                         */

                // item.setLink( strSiteUrl + JSP_MANAGE_COMMENT_SUBMIT + diggSubmit.getIdDiggSubmit( ) );
                item.setLink( buildUrlDiggSubmit( strSiteUrl, diggSubmit ) );
                item.setDescription( diggSubmit.getDiggSubmitValue(  ).replaceAll( "<div[^>]+>", "" )
                                               .replaceAll( "</div>", "" ) );
                item.setDate( diggSubmit.getDateResponse(  ) );

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
        Plugin pluginDirectory = PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME );
        DiggResourceRssConfigHome.remove( idResourceRss, pluginDirectory );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getParameterToApply( HttpServletRequest request )
    {
        Map<String, String> map = new HashMap<String, String>(  );

        map.put( PARAMETER_ID_DIGG, request.getParameter( PARAMETER_ID_DIGG ) );

        return map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkResource(  )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME );
        DiggResourceRssConfig config = DiggResourceRssConfigHome.findByPrimaryKey( this.getId(  ), pluginDirectory );
        Digg digg = DiggHome.findByPrimaryKey( config.getIdDigg(  ), pluginDirectory );

        return ( digg != null );
    }

    /**
         * Builds the url digg submit.
         *
         * @param strSiteUrl the str site url
         * @param diggSubmit the digg submit
         * @return the string
         */
    private static String buildUrlDiggSubmit( String strSiteUrl, DiggSubmit diggSubmit )
    {
        if ( ( diggSubmit != null ) && ( diggSubmit.getDigg(  ) != null ) )
        {
            UrlItem url = new UrlItem( strSiteUrl + SLASH + AppPathService.getPortalUrl(  ) );
            url.addParameter( XPageAppService.PARAM_XPAGE_APP,
                AppPropertiesService.getProperty( PROPERTY_XPAGE_APPLICATION_ID, DEFAULT_XPAGE_ID ) );
            url.addParameter( PARAMETER_ID_DIGG, diggSubmit.getDigg(  ).getIdDigg(  ) );
            url.addParameter( PARAMETER_ID_DIGG_SUBMIT, diggSubmit.getIdDiggSubmit(  ) );
            url.addParameter( PARAMETER_DIGG_COMMENT, "1" );
            url.addParameter( PARAMETER_DIGG_DETAIL, "1" );

            return url.getUrl(  );
        }

        AppLogService.debug( "DiggResourceRss - DiggSubmit is null when trying to build the URL." );

        return StringUtils.EMPTY;
    }

    /**
     * Builds the url digg submit.
     *
     * @param strSiteUrl the str site url
     * @param digg the digg
     * @return the string
     */
    private static String buildUrlDigg( String strSiteUrl, Digg digg )
    {
        if ( digg != null )
        {
            UrlItem url = new UrlItem( strSiteUrl + SLASH + AppPathService.getPortalUrl(  ) );
            url.addParameter( XPageAppService.PARAM_XPAGE_APP,
                AppPropertiesService.getProperty( PROPERTY_XPAGE_APPLICATION_ID, DEFAULT_XPAGE_ID ) );
            url.addParameter( PARAMETER_ID_DIGG, digg.getIdDigg(  ) );

            return url.getUrl(  );
        }

        AppLogService.debug( "DiggResourceRss - Digg is null when trying to build the URL." );

        return StringUtils.EMPTY;
    }
}
