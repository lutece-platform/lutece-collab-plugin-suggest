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

import fr.paris.lutece.plugins.suggest.business.SuggestSubmitType;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitTypeHome;
import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.fileupload.FileItem;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * class SuggestSubmitTypeJspBean
 *
 */
public class SuggestSubmitTypeJspBean extends PluginAdminPageJspBean
{
    private static final long serialVersionUID = 2466547668533718657L;

    // templates
    private static final String TEMPLATE_MANAGE_SUGGEST_SUBMIT_TYPE = "admin/plugins/suggest/manage_suggest_submit_type.html";
    private static final String TEMPLATE_CREATE_SUGGEST_SUBMIT_TYPE = "admin/plugins/suggest/create_suggest_submit_type.html";
    private static final String TEMPLATE_MODIFY_SUGGEST_SUBMIT_TYPE = "admin/plugins/suggest/modify_suggest_submit_type.html";

    // Markers
    private static final String MARK_SUGGEST_SUBMIT_TYPE_LIST = "type_list";
    private static final String MARK_SUGGEST_SUBMIT_TYPE = "type";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";

    // parameters form
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_ID_SUGGEST_SUBMIT_TYPE = "id_type";
    private static final String PARAMETER_NAME = "name";
    private static final String PARAMETER_COLOR = "color";
    private static final String PARAMETER_PARAMETERIZABLE = "parameterizable";
    private static final String PARAMETER_IMAGE_SOURCE = "image_source";
    private static final String PARAMETER_UPDATE_FILE = "update_file";

    // other constants
    private static final String EMPTY_STRING = "";

    // message
    private static final String MESSAGE_MANDATORY_FIELD = "suggest.message.mandatory.field";
    private static final String FIELD_NAME = "suggest.createSuggestSubmitType.labelName";
    private static final String MESSAGE_CONFIRM_REMOVE_SUGGEST_SUBMIT_TYPE = "suggest.message.confirmRemoveSuggestSubmitType";
    private static final String MESSAGE_SUGGEST_SUBMIT_TYPE_ASSOCIATE_TO_SUGGEST = "suggest.message.suggestSubmitTypeAssociateToSuggest";

    // properties
    private static final String PROPERTY_ITEM_PER_PAGE = "suggest.itemsPerPage";
    private static final String PROPERTY_MANAGE_SUGGEST_SUBMIT_TYPE_TITLE = "suggest.manageSuggestSubmitType.pageTitle";
    private static final String PROPERTY_MODIFY_SUGGEST_SUBMIT_TYPE_TITLE = "suggest.modifySuggestSubmitType.title";
    private static final String PROPERTY_CREATE_SUGGEST_SUBMIT_TYPE_TITLE = "suggest.createSuggestSubmitType.title";

    // Jsp Definition
    private static final String JSP_MANAGE_SUGGEST_SUBMIT_TYPE = "jsp/admin/plugins/suggest/ManageSuggestSubmitType.jsp";
    private static final String JSP_DO_REMOVE_SUGGEST_SUBMIT_TYPE = "jsp/admin/plugins/suggest/DoRemoveSuggestSubmitType.jsp";

    // session fields
    private int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 15 );
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;

    /**
     * Return management suggest submit type
     * 
     * @param request
     *            The Http request
     * @return Html ExportFormat
     */
    public String getManageSuggestSubmitType( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        Locale locale = getLocale( );
        Map<String, Object> model = new HashMap<>( );
        List<SuggestSubmitType> listSuggestSubmitType = SuggestSubmitTypeHome.getList( plugin );
        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage, _nDefaultItemsPerPage );

        Paginator<SuggestSubmitType> paginator = new Paginator<SuggestSubmitType>( listSuggestSubmitType, _nItemsPerPage,
                getJspManageSuggestSubmitType( request ), PARAMETER_PAGE_INDEX, _strCurrentPageIndex );

        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, EMPTY_STRING + _nItemsPerPage );
        model.put( MARK_SUGGEST_SUBMIT_TYPE_LIST, paginator.getPageItems( ) );
        setPageTitleProperty( PROPERTY_MANAGE_SUGGEST_SUBMIT_TYPE_TITLE );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_SUGGEST_SUBMIT_TYPE, locale, model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Gets the suggestsubmit type creation page
     * 
     * @param request
     *            The HTTP request
     * @return The export format creation page
     */
    public String getCreateSuggestSubmitType( HttpServletRequest request )
    {
        Locale locale = getLocale( );
        Map<String, Object> model = new HashMap<>( );
        setPageTitleProperty( PROPERTY_CREATE_SUGGEST_SUBMIT_TYPE_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_SUGGEST_SUBMIT_TYPE, locale, model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the suggestsubmit type creation
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doCreateSuggestSubmitType( HttpServletRequest request )
    {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        SuggestSubmitType suggestSubmitType = new SuggestSubmitType( );
        String strError = getSuggestSubmitTypeData( multipartRequest, suggestSubmitType );

        if ( strError != null )
        {
            return strError;
        }

        SuggestSubmitTypeHome.create( suggestSubmitType, getPlugin( ) );

        return getJspManageSuggestSubmitType( request );
    }

    /**
     * Gets the suggestSubmitType modification page
     * 
     * @param request
     *            The HTTP request
     * @return The export format creation page
     */
    public String getModifySuggestSubmitType( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        Locale locale = getLocale( );
        SuggestSubmitType suggestSubmitType;
        String strIdSuggestSubmitType = request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT_TYPE );
        Map<String, Object> model = new HashMap<>( );
        int nIdSuggestSubmitType = -1;

        if ( ( strIdSuggestSubmitType != null ) && !strIdSuggestSubmitType.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdSuggestSubmitType = Integer.parseInt( strIdSuggestSubmitType );
            }
            catch( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getManageSuggestSubmitType( request );
            }
        }
        else
        {
            return getManageSuggestSubmitType( request );
        }

        suggestSubmitType = SuggestSubmitTypeHome.findByPrimaryKey( nIdSuggestSubmitType, plugin );
        model.put( MARK_SUGGEST_SUBMIT_TYPE, suggestSubmitType );
        setPageTitleProperty( PROPERTY_MODIFY_SUGGEST_SUBMIT_TYPE_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_SUGGEST_SUBMIT_TYPE, locale, model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the suggestSubmitType modification
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doModifySuggestSubmitType( HttpServletRequest request )
    {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Plugin plugin = getPlugin( );
        SuggestSubmitType suggestSubmitType;
        String strIdSuggestSubmitType = multipartRequest.getParameter( PARAMETER_ID_SUGGEST_SUBMIT_TYPE );
        int nIdSuggestSubmitType = -1;

        if ( ( strIdSuggestSubmitType != null ) && !strIdSuggestSubmitType.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdSuggestSubmitType = Integer.parseInt( strIdSuggestSubmitType );
            }
            catch( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getHomeUrl( request );
            }
        }
        else
        {
            return getHomeUrl( request );
        }

        suggestSubmitType = SuggestSubmitTypeHome.findByPrimaryKey( nIdSuggestSubmitType, plugin );

        String strError = getSuggestSubmitTypeData( multipartRequest, suggestSubmitType );

        if ( strError != null )
        {
            return strError;
        }

        SuggestSubmitTypeHome.update( suggestSubmitType, getPlugin( ) );

        return getJspManageSuggestSubmitType( request );
    }

    /**
     * Confirm the suggestsubmittype removal
     * 
     * @param request
     *            The HTTP request
     * @return The forward URL
     */
    public String removeSuggestSubmitType( HttpServletRequest request )
    {
        if ( request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT_TYPE ) == null )
        {
            return getHomeUrl( request );
        }

        String strIdSuggestSubmitType = request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT_TYPE );

        UrlItem url = new UrlItem( JSP_DO_REMOVE_SUGGEST_SUBMIT_TYPE );
        url.addParameter( PARAMETER_ID_SUGGEST_SUBMIT_TYPE, strIdSuggestSubmitType );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_SUGGEST_SUBMIT_TYPE, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform the suggestSubmitType suppression
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doRemoveSuggestSubmitType( HttpServletRequest request )
    {
        String strIdSuggestSubmitType = request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT_TYPE );
        Plugin plugin = getPlugin( );
        int nIdSuggestSubmitType = SuggestUtils.getIntegerParameter( strIdSuggestSubmitType );

        if ( SuggestSubmitTypeHome.isAssociateToSuggest( nIdSuggestSubmitType, plugin ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_SUGGEST_SUBMIT_TYPE_ASSOCIATE_TO_SUGGEST, AdminMessage.TYPE_STOP );
        }

        if ( nIdSuggestSubmitType != -1 )
        {
            SuggestSubmitTypeHome.remove( nIdSuggestSubmitType, plugin );
        }

        return getJspManageSuggestSubmitType( request );
    }

    /**
     * Get the request data and if there is no error insert the data in the suggestSubmitType object specified in parameter. return null if there is no error or
     * else return the error page url
     * 
     * @param multipartRequest
     *            the request
     * @param suggestSubmitType
     *            the suggestSubmitType Object
     * @return null if there is no error or else return the error page url
     */
    private String getSuggestSubmitTypeData( MultipartHttpServletRequest multipartRequest, SuggestSubmitType suggestSubmitType )
    {
        String strName = multipartRequest.getParameter( PARAMETER_NAME );
        String strColor = multipartRequest.getParameter( PARAMETER_COLOR );
        String strParameterizable = multipartRequest.getParameter( PARAMETER_PARAMETERIZABLE );
        Boolean bParameterizable = true;
        String strFieldError = EMPTY_STRING;
        FileItem imageSource = multipartRequest.getFile( PARAMETER_IMAGE_SOURCE );
        String strImageName = FileUploadService.getFileNameOnly( imageSource );
        String strUpdateFile = multipartRequest.getParameter( PARAMETER_UPDATE_FILE );

        if ( ( strName == null ) || strName.trim( ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_NAME;
        }

        else
            if ( ( strParameterizable == null ) || strParameterizable.trim( ).equals( EMPTY_STRING ) )
            {
                bParameterizable = false;
            }

        // Mandatory fields
        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object [ ] tabRequiredFields = {
                I18nService.getLocalizedString( strFieldError, getLocale( ) )
            };

            return AdminMessageService.getMessageUrl( multipartRequest, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        if ( ( suggestSubmitType.getIdType( ) == SuggestUtils.CONSTANT_ID_NULL ) || ( strUpdateFile != null ) )
        {
            ImageResource image = new ImageResource( );
            byte [ ] baImageSource = imageSource.get( );

            if ( ( strImageName != null ) && !strImageName.equals( "" ) )
            {
                image.setImage( baImageSource );
                image.setMimeType( imageSource.getContentType( ) );
            }

            suggestSubmitType.setPictogram( image );
        }

        suggestSubmitType.setColor( strColor );
        suggestSubmitType.setName( strName );
        suggestSubmitType.setParameterizableInFO( bParameterizable );

        return null;
    }

    /**
     * return the url of manage export format
     * 
     * @param request
     *            the request
     * @return the url of manage export format
     */
    private String getJspManageSuggestSubmitType( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_SUGGEST_SUBMIT_TYPE;
    }
}
