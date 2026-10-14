/*
 * Copyright (c) 2002-2025, City of Paris
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

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import fr.paris.lutece.portal.web.util.Pager;
import fr.paris.lutece.portal.web.util.IPager;
import fr.paris.lutece.plugins.suggest.service.SuggestPlugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.plugins.suggest.business.ExportFormat;
import fr.paris.lutece.plugins.suggest.business.ExportFormatHome;
import fr.paris.lutece.plugins.suggest.service.ExportFormatResourceIdService;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.url.UrlItem;

import fr.paris.lutece.portal.service.upload.MultipartItem;

import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 *
 * class ExportFormatJspBean
 *
 */
@RequestScoped
@Named( "suggestExportFormatJspBean" )
@Controller( controllerJsp = "ManageExportFormat.jsp", controllerPath = "jsp/admin/plugins/suggest/", right = ManageSuggestJspBean.RIGHT_MANAGE_SUGGEST_LIKE, securityTokenEnabled = true )
public class ExportFormatJspBean extends MVCAdminJspBean
{
    private static final long serialVersionUID = 2892557628133467782L;

    // views and actions
    private static final String VIEW_MANAGE_EXPORT_FORMAT = "manageExportFormat";
    private static final String VIEW_CREATE_EXPORT_FORMAT = "createExportFormat";
    private static final String ACTION_CREATE_EXPORT_FORMAT = "createExportFormat";
    private static final String VIEW_MODIFY_EXPORT_FORMAT = "modifyExportFormat";
    private static final String ACTION_MODIFY_EXPORT_FORMAT = "modifyExportFormat";
    private static final String VIEW_CONFIRM_REMOVE_EXPORT_FORMAT = "confirmRemoveExportFormat";
    private static final String ACTION_REMOVE_EXPORT_FORMAT = "removeExportFormat";
    private static final String VIEW_DOWNLOAD_XSL_FILE = "downloadXslFile";

    // templates
    private static final String TEMPLATE_MANAGE_EXPORT = "admin/plugins/suggest/manage_export_format.html";
    private static final String TEMPLATE_CREATE_EXPORT = "admin/plugins/suggest/create_export_format.html";
    private static final String TEMPLATE_MODIFY_EXPORT = "admin/plugins/suggest/modify_export_format.html";

    // Markers
    private static final String MARK_EXPORT_LIST = "export_list";
    private static final String MARK_EXPORT = "export";

    // parameters form
    private static final String PARAMETER_ID_EXPORT = "id_export";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_DESCRIPTION = "description";
    private static final String PARAMETER_EXTENSION = "extension";

    // other constants
    private static final String EMPTY_STRING = "";
    private static final String CONSTANT_MIME_TYPE_OCTETSTREAM = "application/octet-stream";

    // message
    private static final String MESSAGE_CONFIRM_REMOVE_EXPORT = "suggest.message.confirmRemoveExportFormat";
    private static final String MESSAGE_MANDATORY_FIELD = "suggest.message.mandatory.field";
    private static final String MESSAGE_ERROR_DURING_DOWNLOAD_FILE = "suggest.message.errorDuringDownloadFile";
    private static final String MESSAGE_YOU_ARE_NOT_ALLOWED_TO_DOWLOAD_THIS_FILE = "suggest.message.youAreNotAllowedToDownloadFile";
    private static final String FIELD_TITLE = "suggest.createExportFormat.labelTitle";
    private static final String FIELD_DESCRIPTION = "suggest.createExportFormat.labelDescription";
    private static final String FIELD_EXTENSION = "suggest.createExportFormat.labelExtension";
    private static final String FIELD_XSL = "suggest.createExportFormat.labelXsl";
    private static final String MESSAGE_STYLESHEET_NOT_VALID = "suggest.message.stylesheetNotValid";
    private static final String FEATURE_EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    private static final String FEATURE_EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";

    // properties
    private static final String PROPERTY_ITEM_PER_PAGE = "suggest.itemsPerPage";
    private static final String PROPERTY_MANAGE_EXPORT_FORMAT_TITLE = "suggest.manageExportFormat.pageTitle";
    private static final String PROPERTY_MODIFY_EXPORT_FORMAT_TITLE = "suggest.modifyExportFormat.title";
    private static final String PROPERTY_CREATE_EXPORT_FORMAT_TITLE = "suggest.createExportFormat.title";

    // Jsp Definition
    private static final String JSP_MANAGE_EXPORT_FORMAT = "jsp/admin/plugins/suggest/ManageExportFormat.jsp";
    private static final String JSP_DO_REMOVE_EXPORT_FORMAT = "jsp/admin/plugins/suggest/ManageExportFormat.jsp?action=" + ACTION_REMOVE_EXPORT_FORMAT;

    @Inject
    @Pager( name = "suggest.exportFormats", listBookmark = MARK_EXPORT_LIST, defaultItemsPerPage = PROPERTY_ITEM_PER_PAGE, baseUrl = "jsp/admin/plugins/suggest/ManageExportFormat.jsp" )
    private IPager<ExportFormat, ExportFormat> _pager;


    /**
     * Return management export format ( list of export format )
     * 
     * @param request
     *            The Http request
     * @param model
     *            the model
     * @return Html ExportFormat
     */
    @View( value = VIEW_MANAGE_EXPORT_FORMAT, defaultView = true )
    public String getManageExportFormat( HttpServletRequest request, Models model )
    {
        Plugin plugin = getPlugin( );
        List<ExportFormat> listExportFormat = ExportFormatHome.getList( plugin );
        listExportFormat = (List<ExportFormat>) RBACService.getAuthorizedCollection( listExportFormat, ExportFormatResourceIdService.PERMISSION_MANAGE,
                (User) getUser( ) );
        _pager.withListItem( listExportFormat ).populateModels( request, model, getLocale( ) );

        return getPage( PROPERTY_MANAGE_EXPORT_FORMAT_TITLE, TEMPLATE_MANAGE_EXPORT );
    }

    /**
     * Gets the export format creation page
     * 
     * @param request
     *            The HTTP request
     * @param model
     *            the model
     * @return The export format creation page
     */
    @View( VIEW_CREATE_EXPORT_FORMAT )
    public String getCreateExportFormat( HttpServletRequest request, Models model )
    {
        if ( !RBACService.isAuthorized( ExportFormat.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, ExportFormatResourceIdService.PERMISSION_MANAGE, (User) getUser( ) ) )
        {
            return getManageExportFormat( request, model );
        }

        return getPage( PROPERTY_CREATE_EXPORT_FORMAT_TITLE, TEMPLATE_CREATE_EXPORT );
    }

    /**
     * Perform the export format creation
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_CREATE_EXPORT_FORMAT )
    public String doCreateExportFormat( HttpServletRequest request )
    {
        if ( !RBACService.isAuthorized( ExportFormat.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, ExportFormatResourceIdService.PERMISSION_MANAGE, (User) getUser( ) ) )
        {
            return redirect( request, getJspManageExportFormat( request ) );
        }

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        ExportFormat exportFormat = new ExportFormat( );
        String strError = getExportFormatData( multipartRequest, exportFormat );

        if ( strError != null )
        {
            return redirect( request, strError );
        }

        ExportFormatHome.create( exportFormat, getPlugin( ) );

        return redirect( request, getJspManageExportFormat( request ) );
    }

    /**
     * Gets the export format modification page
     * 
     * @param request
     *            The HTTP request
     * @param model
     *            the model
     * @return The export format creation page
     */
    @View( VIEW_MODIFY_EXPORT_FORMAT )
    public String getModifyExportFormat( HttpServletRequest request, Models model )
    {
        Plugin plugin = getPlugin( );
        ExportFormat exportFormat;
        String strIdExport = request.getParameter( PARAMETER_ID_EXPORT );
        int nIdExport = -1;

        if ( ( strIdExport != null ) && !strIdExport.equals( EMPTY_STRING )
                && RBACService.isAuthorized( ExportFormat.RESOURCE_TYPE, strIdExport, ExportFormatResourceIdService.PERMISSION_MANAGE, (User) getUser( ) ) )
        {
            try
            {
                nIdExport = Integer.parseInt( strIdExport );
            }
            catch( NumberFormatException ne )
            {
                AppLogService.error( ne.getMessage( ), ne );

                return getManageExportFormat( request, model );
            }
        }
        else
        {
            return getManageExportFormat( request, model );
        }

        exportFormat = ExportFormatHome.findByPrimaryKey( nIdExport, plugin );

        if ( exportFormat == null )
        {
            return getManageExportFormat( request, model );
        }

        model.put( MARK_EXPORT, exportFormat );

        return getPage( PROPERTY_MODIFY_EXPORT_FORMAT_TITLE, TEMPLATE_MODIFY_EXPORT );
    }

    /**
     * Perform the export format modification
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_MODIFY_EXPORT_FORMAT )
    public String doModifyExportFormat( HttpServletRequest request )
    {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Plugin plugin = getPlugin( );
        ExportFormat exportFormat;
        String strIdExport = multipartRequest.getParameter( PARAMETER_ID_EXPORT );
        int nIdExport = -1;

        if ( ( strIdExport != null ) && !strIdExport.equals( EMPTY_STRING )
                && RBACService.isAuthorized( ExportFormat.RESOURCE_TYPE, strIdExport, ExportFormatResourceIdService.PERMISSION_MANAGE, (User) getUser( ) ) )
        {
            try
            {
                nIdExport = Integer.parseInt( strIdExport );
            }
            catch( NumberFormatException ne )
            {
                AppLogService.error( ne.getMessage( ), ne );

                return redirect( request, getHomeUrl( request ) );
            }
        }
        else
        {
            return redirect( request, getHomeUrl( request ) );
        }

        exportFormat = ExportFormatHome.findByPrimaryKey( nIdExport, plugin );

        String strError = getExportFormatData( multipartRequest, exportFormat );

        if ( strError != null )
        {
            return redirect( request, strError );
        }

        ExportFormatHome.update( exportFormat, getPlugin( ) );

        return redirect( request, getJspManageExportFormat( request ) );
    }

    /**
     * Gets the confirmation page of delete export format
     * 
     * @param request
     *            The HTTP request
     * @return the confirmation page of delete export format
     */
    @View( value = VIEW_CONFIRM_REMOVE_EXPORT_FORMAT, securityTokenAction = ACTION_REMOVE_EXPORT_FORMAT )
    public String getConfirmRemoveExportFormat( HttpServletRequest request )
    {
        if ( request.getParameter( PARAMETER_ID_EXPORT ) == null )
        {
            return redirect( request, getHomeUrl( request ) );
        }

        String strIdExport = request.getParameter( PARAMETER_ID_EXPORT );
        UrlItem url = new UrlItem( JSP_DO_REMOVE_EXPORT_FORMAT );
        url.addParameter( PARAMETER_ID_EXPORT, strIdExport );

        return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_EXPORT, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Perform the export format supression
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_REMOVE_EXPORT_FORMAT )
    public String doRemoveExportFormat( HttpServletRequest request )
    {
        String strIdExport = request.getParameter( PARAMETER_ID_EXPORT );
        int nIdExport = -1;

        if ( ( request.getParameter( PARAMETER_ID_EXPORT ) == null )
                || !RBACService.isAuthorized( ExportFormat.RESOURCE_TYPE, strIdExport, ExportFormatResourceIdService.PERMISSION_MANAGE, (User) getUser( ) ) )
        {
            return redirect( request, getJspManageExportFormat( request ) );
        }

        try
        {
            nIdExport = Integer.parseInt( strIdExport );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne.getMessage( ), ne );

            return redirect( request, getHomeUrl( request ) );
        }

        if ( nIdExport != -1 )
        {
            ExportFormatHome.remove( nIdExport, getPlugin( ) );
        }

        return redirect( request, getJspManageExportFormat( request ) );
    }

    /**
     * Get the request data and if there is no error insert the data in the exportFormat object specified in parameter. return null if there is no error or else
     * return the error page url
     * 
     * @param multipartRequest
     *            the request
     * @param exportFormat
     *            the exportFormat Object
     * @return null if there is no error or else return the error page url
     */
    private String getExportFormatData( MultipartHttpServletRequest multipartRequest, ExportFormat exportFormat )
    {
        String strTitle = multipartRequest.getParameter( PARAMETER_TITLE );
        String strDescription = multipartRequest.getParameter( PARAMETER_DESCRIPTION );
        String strExtension = multipartRequest.getParameter( PARAMETER_EXTENSION );

        String strFieldError = EMPTY_STRING;
        MultipartItem fileSource = multipartRequest.getFile( Parameters.STYLESHEET_SOURCE );
        String strFilename = FileUploadService.getFileNameOnly( fileSource );

        if ( ( strTitle == null ) || strTitle.trim( ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_TITLE;
        }

        else
            if ( ( strDescription == null ) || strDescription.trim( ).equals( EMPTY_STRING ) )
            {
                strFieldError = FIELD_DESCRIPTION;
            }
            else
                if ( ( strExtension == null ) || strExtension.trim( ).equals( EMPTY_STRING ) )
                {
                    strFieldError = FIELD_EXTENSION;
                }

                else
                    if ( ( strFilename == null ) || ( strFilename.equals( "" ) && ( exportFormat.getXsl( ) == null ) ) )
                    {
                        strFieldError = FIELD_XSL;
                    }

        // Mandatory fields
        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object [ ] tabRequiredFields = {
                I18nService.getLocalizedString( strFieldError, getLocale( ) )
            };

            return AdminMessageService.getMessageUrl( multipartRequest, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        byte [ ] baXslSource = fileSource.get( );

        // Check the XML validity of the XSL stylesheet
        if ( ( strFilename != null ) && !strFilename.equals( "" ) && ( isValid( baXslSource ) != null ) )
        {
            Object [ ] args = {
                isValid( baXslSource )
            };

            return AdminMessageService.getMessageUrl( multipartRequest, MESSAGE_STYLESHEET_NOT_VALID, args, AdminMessage.TYPE_STOP );
        }

        exportFormat.setTitle( strTitle );
        exportFormat.setDescription( strDescription );
        exportFormat.setExtension( strExtension );

        if ( ( strFilename != null ) && !strFilename.equals( "" ) )
        {
            exportFormat.setXsl( baXslSource );
        }

        return null;
    }

    /**
     * write in the http response the value of the response whose identifier is specified in the request if there is no response return a error
     * 
     * @param request
     *            the http request
     * @return The URL to go after performing the action
     */
    @View( VIEW_DOWNLOAD_XSL_FILE )
    public String doDownloadXslFile( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        ExportFormat exportFormat;
        String strIdExport = request.getParameter( PARAMETER_ID_EXPORT );
        int nIdExport = -1;

        if ( ( strIdExport != null ) && !strIdExport.equals( EMPTY_STRING )
                && RBACService.isAuthorized( ExportFormat.RESOURCE_TYPE, strIdExport, ExportFormatResourceIdService.PERMISSION_MANAGE, (User) getUser( ) ) )
        {
            try
            {
                nIdExport = Integer.parseInt( strIdExport );
            }
            catch( NumberFormatException ne )
            {
                AppLogService.error( ne.getMessage( ), ne );

                return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_DURING_DOWNLOAD_FILE, AdminMessage.TYPE_STOP ) );
            }
        }
        else
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_YOU_ARE_NOT_ALLOWED_TO_DOWLOAD_THIS_FILE, AdminMessage.TYPE_STOP ) );
        }

        exportFormat = ExportFormatHome.findByPrimaryKey( nIdExport, plugin );

        if ( exportFormat != null )
        {
            download( exportFormat.getXsl( ), exportFormat.getTitle( ) + ".xsl", CONSTANT_MIME_TYPE_OCTETSTREAM );

            return null;
        }

        return redirect( request, getJspManageExportFormat( request ) );
    }

    /**
     * Use parsing for validate the modify xsl file
     * 
     * @param baXslSource
     *            the xsl source
     * @return the message exception when the validation is false
     */
    private String isValid( byte [ ] baXslSource )
    {
        String strError = null;

        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance( );
            factory.setFeature( FEATURE_EXTERNAL_GENERAL_ENTITIES, false );
            factory.setFeature( FEATURE_EXTERNAL_PARAMETER_ENTITIES, false );
            SAXParser analyzer = factory.newSAXParser( );
            InputSource is = new InputSource( new ByteArrayInputStream( baXslSource ) );
            analyzer.getXMLReader( ).parse( is );
        }
        catch( Exception e )
        {
            strError = e.getMessage( );
        }

        return strError;
    }

    /**
     * return the url of manage export format
     * 
     * @param request
     *            the request
     * @return the url of manage export format
     */
    private String getJspManageExportFormat( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_EXPORT_FORMAT;
    }

    /**
     * Returns the suggest plugin.
     *
     * @return the plugin
     */
    @Override
    public Plugin getPlugin( )
    {
        return PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME );
    }
}
