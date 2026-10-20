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
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitType;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitTypeHome;
import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.url.UrlItem;

import fr.paris.lutece.portal.service.upload.MultipartItem;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * class SuggestSubmitTypeJspBean
 *
 */
@RequestScoped
@Named( "suggestSubmitTypeJspBean" )
@Controller( controllerJsp = "ManageSuggestSubmitType.jsp", controllerPath = "jsp/admin/plugins/suggest/", right = ManageSuggestJspBean.RIGHT_MANAGE_SUGGEST_LIKE, securityTokenEnabled = true )
public class SuggestSubmitTypeJspBean extends MVCAdminJspBean
{
    private static final long serialVersionUID = 2466547668533718657L;

    // views and actions
    private static final String VIEW_MANAGE_SUGGEST_SUBMIT_TYPE = "manageSuggestSubmitType";
    private static final String VIEW_CREATE_SUGGEST_SUBMIT_TYPE = "createSuggestSubmitType";
    private static final String ACTION_CREATE_SUGGEST_SUBMIT_TYPE = "createSuggestSubmitType";
    private static final String VIEW_MODIFY_SUGGEST_SUBMIT_TYPE = "modifySuggestSubmitType";
    private static final String ACTION_MODIFY_SUGGEST_SUBMIT_TYPE = "modifySuggestSubmitType";
    private static final String VIEW_CONFIRM_REMOVE_SUGGEST_SUBMIT_TYPE = "confirmRemoveSuggestSubmitType";
    private static final String ACTION_REMOVE_SUGGEST_SUBMIT_TYPE = "removeSuggestSubmitType";

    // templates
    private static final String TEMPLATE_MANAGE_SUGGEST_SUBMIT_TYPE = "admin/plugins/suggest/manage_suggest_submit_type.html";
    private static final String TEMPLATE_CREATE_SUGGEST_SUBMIT_TYPE = "admin/plugins/suggest/create_suggest_submit_type.html";
    private static final String TEMPLATE_MODIFY_SUGGEST_SUBMIT_TYPE = "admin/plugins/suggest/modify_suggest_submit_type.html";

    // Markers
    private static final String MARK_SUGGEST_SUBMIT_TYPE_LIST = "type_list";
    private static final String MARK_SUGGEST_SUBMIT_TYPE = "type";

    // parameters form
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
    private static final String JSP_DO_REMOVE_SUGGEST_SUBMIT_TYPE = "jsp/admin/plugins/suggest/ManageSuggestSubmitType.jsp?action=" + ACTION_REMOVE_SUGGEST_SUBMIT_TYPE;

    @Inject
    @Pager( name = "suggest.suggestSubmitTypes", listBookmark = MARK_SUGGEST_SUBMIT_TYPE_LIST, defaultItemsPerPage = PROPERTY_ITEM_PER_PAGE, baseUrl = "jsp/admin/plugins/suggest/ManageSuggestSubmitType.jsp" )
    private IPager<SuggestSubmitType, SuggestSubmitType> _pager;


    /**
     * Return management suggest submit type
     * 
     * @param request
     *            The Http request
     * @param model
     *            the model
     * @return the html of the suggest submit types management page
     */
    @View( value = VIEW_MANAGE_SUGGEST_SUBMIT_TYPE, defaultView = true )
    public String getManageSuggestSubmitType( HttpServletRequest request, Models model )
    {
        Plugin plugin = getPlugin( );
        List<SuggestSubmitType> listSuggestSubmitType = SuggestSubmitTypeHome.getList( plugin );
        _pager.withListItem( listSuggestSubmitType ).populateModels( request, model, getLocale( ) );

        return getPage( PROPERTY_MANAGE_SUGGEST_SUBMIT_TYPE_TITLE, TEMPLATE_MANAGE_SUGGEST_SUBMIT_TYPE );
    }

    /**
     * Gets the suggestsubmit type creation page
     * 
     * @param request
     *            The HTTP request
     * @param model
     *            the model
     * @return The export format creation page
     */
    @View( VIEW_CREATE_SUGGEST_SUBMIT_TYPE )
    public String getCreateSuggestSubmitType( HttpServletRequest request, Models model )
    {
        return getPage( PROPERTY_CREATE_SUGGEST_SUBMIT_TYPE_TITLE, TEMPLATE_CREATE_SUGGEST_SUBMIT_TYPE );
    }

    /**
     * Perform the suggestsubmit type creation
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_CREATE_SUGGEST_SUBMIT_TYPE )
    public String doCreateSuggestSubmitType( HttpServletRequest request )
    {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        SuggestSubmitType suggestSubmitType = new SuggestSubmitType( );
        String strError = getSuggestSubmitTypeData( multipartRequest, suggestSubmitType );

        if ( strError != null )
        {
            addError( strError );
            return redirectView( request, VIEW_CREATE_SUGGEST_SUBMIT_TYPE );
        }

        SuggestSubmitTypeHome.create( suggestSubmitType, getPlugin( ) );

        return redirect( request, getJspManageSuggestSubmitType( request ) );
    }

    /**
     * Gets the suggestSubmitType modification page
     * 
     * @param request
     *            The HTTP request
     * @param model
     *            the model
     * @return The export format creation page
     */
    @View( VIEW_MODIFY_SUGGEST_SUBMIT_TYPE )
    public String getModifySuggestSubmitType( HttpServletRequest request, Models model )
    {
        Plugin plugin = getPlugin( );
        SuggestSubmitType suggestSubmitType;
        String strIdSuggestSubmitType = request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT_TYPE );
        int nIdSuggestSubmitType = -1;

        if ( ( strIdSuggestSubmitType != null ) && !strIdSuggestSubmitType.equals( EMPTY_STRING ) )
        {
            try
            {
                nIdSuggestSubmitType = Integer.parseInt( strIdSuggestSubmitType );
            }
            catch( NumberFormatException ne )
            {
                AppLogService.error( ne.getMessage( ), ne );

                return getManageSuggestSubmitType( request, model );
            }
        }
        else
        {
            return getManageSuggestSubmitType( request, model );
        }

        suggestSubmitType = SuggestSubmitTypeHome.findByPrimaryKey( nIdSuggestSubmitType, plugin );

        if ( suggestSubmitType == null )
        {
            return getManageSuggestSubmitType( request, model );
        }

        model.put( MARK_SUGGEST_SUBMIT_TYPE, suggestSubmitType );

        return getPage( PROPERTY_MODIFY_SUGGEST_SUBMIT_TYPE_TITLE, TEMPLATE_MODIFY_SUGGEST_SUBMIT_TYPE );
    }

    /**
     * Perform the suggestSubmitType modification
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_MODIFY_SUGGEST_SUBMIT_TYPE )
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
                AppLogService.error( ne.getMessage( ), ne );

                return redirect( request, getHomeUrl( request ) );
            }
        }
        else
        {
            return redirect( request, getHomeUrl( request ) );
        }

        suggestSubmitType = SuggestSubmitTypeHome.findByPrimaryKey( nIdSuggestSubmitType, plugin );

        String strError = getSuggestSubmitTypeData( multipartRequest, suggestSubmitType );

        if ( strError != null )
        {
            addError( strError );
            return redirect( request, VIEW_MODIFY_SUGGEST_SUBMIT_TYPE, PARAMETER_ID_SUGGEST_SUBMIT_TYPE, nIdSuggestSubmitType );
        }

        SuggestSubmitTypeHome.update( suggestSubmitType, getPlugin( ) );

        return redirect( request, getJspManageSuggestSubmitType( request ) );
    }

    /**
     * Confirm the suggestsubmittype removal
     * 
     * @param request
     *            The HTTP request
     * @return The forward URL
     */
    @View( value = VIEW_CONFIRM_REMOVE_SUGGEST_SUBMIT_TYPE, securityTokenAction = ACTION_REMOVE_SUGGEST_SUBMIT_TYPE )
    public String removeSuggestSubmitType( HttpServletRequest request )
    {
        if ( request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT_TYPE ) == null )
        {
            return redirect( request, getHomeUrl( request ) );
        }

        String strIdSuggestSubmitType = request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT_TYPE );

        UrlItem url = new UrlItem( JSP_DO_REMOVE_SUGGEST_SUBMIT_TYPE );
        url.addParameter( PARAMETER_ID_SUGGEST_SUBMIT_TYPE, strIdSuggestSubmitType );

        return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_SUGGEST_SUBMIT_TYPE, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Perform the suggestSubmitType suppression
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_REMOVE_SUGGEST_SUBMIT_TYPE )
    public String doRemoveSuggestSubmitType( HttpServletRequest request )
    {
        String strIdSuggestSubmitType = request.getParameter( PARAMETER_ID_SUGGEST_SUBMIT_TYPE );
        Plugin plugin = getPlugin( );
        int nIdSuggestSubmitType = SuggestUtils.getIntegerParameter( strIdSuggestSubmitType );

        if ( SuggestSubmitTypeHome.isAssociateToSuggest( nIdSuggestSubmitType, plugin ) )
        {
            addError( MESSAGE_SUGGEST_SUBMIT_TYPE_ASSOCIATE_TO_SUGGEST, getLocale( ) );
            return redirectView( request, VIEW_MANAGE_SUGGEST_SUBMIT_TYPE );
        }

        if ( nIdSuggestSubmitType != -1 )
        {
            SuggestSubmitTypeHome.remove( nIdSuggestSubmitType, plugin );
        }

        return redirect( request, getJspManageSuggestSubmitType( request ) );
    }

    /**
     * Get the request data and if there is no error insert the data in the suggestSubmitType object specified in parameter. return null if there is no error or
     * else return the localized error message
     * 
     * @param multipartRequest
     *            the request
     * @param suggestSubmitType
     *            the suggestSubmitType Object
     * @return null if there is no error or else return the localized error message
     */
    private String getSuggestSubmitTypeData( MultipartHttpServletRequest multipartRequest, SuggestSubmitType suggestSubmitType )
    {
        String strName = multipartRequest.getParameter( PARAMETER_NAME );
        String strColor = multipartRequest.getParameter( PARAMETER_COLOR );
        String strParameterizable = multipartRequest.getParameter( PARAMETER_PARAMETERIZABLE );
        Boolean bParameterizable = true;
        String strFieldError = EMPTY_STRING;
        MultipartItem imageSource = multipartRequest.getFile( PARAMETER_IMAGE_SOURCE );
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

            return I18nService.getLocalizedString( MESSAGE_MANDATORY_FIELD, tabRequiredFields, getLocale( ) );
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
     * return the url of manage suggest submit type
     * 
     * @param request
     *            the request
     * @return the url of manage suggest submit type
     */
    private String getJspManageSuggestSubmitType( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_SUGGEST_SUBMIT_TYPE;
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
