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
import fr.paris.lutece.plugins.suggest.business.Category;
import fr.paris.lutece.plugins.suggest.business.CategoryHome;
import fr.paris.lutece.plugins.suggest.service.CategoryResourceIdService;
import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.url.UrlItem;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * class CategoryJspBean
 *
 */
@RequestScoped
@Named( "suggestCategoryJspBean" )
@Controller( controllerJsp = "ManageCategory.jsp", controllerPath = "jsp/admin/plugins/suggest/", right = ManageSuggestJspBean.RIGHT_MANAGE_SUGGEST_LIKE, securityTokenEnabled = true )
public class CategoryJspBean extends MVCAdminJspBean
{
    private static final long serialVersionUID = -4972338182883838725L;

    // views and actions
    private static final String VIEW_MANAGE_CATEGORY = "manageCategory";
    private static final String VIEW_CREATE_CATEGORY = "createCategory";
    private static final String ACTION_CREATE_CATEGORY = "createCategory";
    private static final String VIEW_MODIFY_CATEGORY = "modifyCategory";
    private static final String ACTION_MODIFY_CATEGORY = "modifyCategory";
    private static final String VIEW_CONFIRM_REMOVE_CATEGORY = "confirmRemoveCategory";
    private static final String ACTION_REMOVE_CATEGORY = "removeCategory";

    // templates
    private static final String TEMPLATE_MANAGE_CATEGORY = "admin/plugins/suggest/manage_category.html";
    private static final String TEMPLATE_CREATE_CATEGORY = "admin/plugins/suggest/create_category.html";
    private static final String TEMPLATE_MODIFY_CATEGORY = "admin/plugins/suggest/modify_category.html";

    // Markers
    private static final String MARK_CATEGORY_LIST = "category_list";
    private static final String MARK_CATEGORY = "category";

    // parameters form
    private static final String PARAMETER_ID_CATEGORY = "id_category";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_COLOR = "color";

    // message
    private static final String MESSAGE_CONFIRM_REMOVE_CATEGORY = "suggest.message.confirmRemoveCategory";
    private static final String MESSAGE_MANDATORY_FIELD = "suggest.message.mandatory.field";
    private static final String MESSAGE_CATEGORY_ASSOCIATE_TO_SUGGEST = "suggest.message.categoryAssociateToSuggest";
    private static final String FIELD_TITLE = "suggest.createCategory.labelTitle";
    private static final String FIELD_COLOR = "suggest.createCategory.labelColor";

    // properties
    private static final String PROPERTY_ITEM_PER_PAGE = "suggest.itemsPerPage";
    private static final String PROPERTY_MANAGE_CATEGORY_TITLE = "suggest.manageCategory.pageTitle";
    private static final String PROPERTY_MODIFY_CATEGORY_TITLE = "suggest.modifyCategory.title";
    private static final String PROPERTY_CREATE_CATEGORY_TITLE = "suggest.createCategory.title";

    // Jsp Definition
    private static final String JSP_MANAGE_CATEGORY = "jsp/admin/plugins/suggest/ManageCategory.jsp";
    private static final String JSP_DO_REMOVE_CATEGORY = "jsp/admin/plugins/suggest/ManageCategory.jsp?action=" + ACTION_REMOVE_CATEGORY;

    @Inject
    @Pager( name = "suggest.categories", listBookmark = MARK_CATEGORY_LIST, defaultItemsPerPage = PROPERTY_ITEM_PER_PAGE, baseUrl = "jsp/admin/plugins/suggest/ManageCategory.jsp" )
    private IPager<Category, Category> _pager;


    /**
     * Return management category( list of category)
     * 
     * @param request
     *            The Http request
     * @param model
     *            the model
     * @return Html form
     */
    @View( value = VIEW_MANAGE_CATEGORY, defaultView = true )
    public String getManageCategory( HttpServletRequest request, Models model )
    {
        Plugin plugin = getPlugin( );
        List<Category> listCategory = CategoryHome.getList( plugin );
        listCategory = (List<Category>) RBACService.getAuthorizedCollection( listCategory, CategoryResourceIdService.PERMISSION_MANAGE, (User) getUser( ) );

        _pager.withListItem( listCategory ).populateModels( request, model, getLocale( ) );

        return getPage( PROPERTY_MANAGE_CATEGORY_TITLE, TEMPLATE_MANAGE_CATEGORY );
    }

    /**
     * Gets the category creation page
     * 
     * @param request
     *            The HTTP request
     * @param model
     *            the model
     * @return The category creation page
     */
    @View( VIEW_CREATE_CATEGORY )
    public String getCreateCategory( HttpServletRequest request, Models model )
    {
        if ( !RBACService.isAuthorized( Category.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, CategoryResourceIdService.PERMISSION_MANAGE, (User) getUser( ) ) )
        {
            return getManageCategory( request, model );
        }

        return getPage( PROPERTY_CREATE_CATEGORY_TITLE, TEMPLATE_CREATE_CATEGORY );
    }

    /**
     * Perform the category creation
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_CREATE_CATEGORY )
    public String doCreateCategory( HttpServletRequest request )
    {
        if ( !RBACService.isAuthorized( Category.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, CategoryResourceIdService.PERMISSION_MANAGE, (User) getUser( ) ) )
        {
            return redirect( request, getJspManageCategory( request ) );
        }

        Category category = new Category( );
        String strError = getCategoryData( request, category );

        if ( strError != null )
        {
            addError( strError );
            return redirectView( request, VIEW_CREATE_CATEGORY );
        }

        CategoryHome.create( category, getPlugin( ) );

        return redirect( request, getJspManageCategory( request ) );
    }

    /**
     * Gets the category modification page
     * 
     * @param request
     *            The HTTP request
     * @param model
     *            the model
     * @return the category modification page
     */
    @View( VIEW_MODIFY_CATEGORY )
    public String getModifyCategory( HttpServletRequest request, Models model )
    {
        Plugin plugin = getPlugin( );
        Category category;
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );

        int nIdCategory = -1;

        if ( ( strIdCategory != null ) && !strIdCategory.equals( SuggestUtils.EMPTY_STRING )
                && RBACService.isAuthorized( Category.RESOURCE_TYPE, strIdCategory, CategoryResourceIdService.PERMISSION_MANAGE, (User) getUser( ) ) )
        {
            try
            {
                nIdCategory = Integer.parseInt( strIdCategory );
            }
            catch( NumberFormatException ne )
            {
                AppLogService.error( ne.getMessage( ), ne );

                return getManageCategory( request, model );
            }
        }
        else
        {
            return getManageCategory( request, model );
        }

        category = CategoryHome.findByPrimaryKey( nIdCategory, plugin );

        if ( category == null )
        {
            return getManageCategory( request, model );
        }

        model.put( MARK_CATEGORY, category );

        return getPage( PROPERTY_MODIFY_CATEGORY_TITLE, TEMPLATE_MODIFY_CATEGORY );
    }

    /**
     * Perform the category modification
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_MODIFY_CATEGORY )
    public String doModifyCategory( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        Category category;
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        int nIdCategory = -1;

        if ( ( strIdCategory != null ) && !strIdCategory.equals( SuggestUtils.EMPTY_STRING )
                && RBACService.isAuthorized( Category.RESOURCE_TYPE, strIdCategory, CategoryResourceIdService.PERMISSION_MANAGE, (User) getUser( ) ) )
        {
            try
            {
                nIdCategory = Integer.parseInt( strIdCategory );
            }
            catch( NumberFormatException ne )
            {
                AppLogService.error( ne.getMessage( ), ne );

                return redirect( request, getJspManageCategory( request ) );
            }
        }
        else
        {
            return redirect( request, getJspManageCategory( request ) );
        }

        category = new Category( );
        category.setIdCategory( nIdCategory );

        String strError = getCategoryData( request, category );

        if ( strError != null )
        {
            addError( strError );
            return redirect( request, VIEW_MODIFY_CATEGORY, PARAMETER_ID_CATEGORY, nIdCategory );
        }

        CategoryHome.update( category, plugin );

        return redirect( request, getJspManageCategory( request ) );
    }

    /**
     * Gets the confirmation page of delete category
     * 
     * @param request
     *            The HTTP request
     * @return the confirmation page of delete category
     */
    @View( value = VIEW_CONFIRM_REMOVE_CATEGORY, securityTokenAction = ACTION_REMOVE_CATEGORY )
    public String getConfirmRemoveCategory( HttpServletRequest request )
    {
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );

        if ( strIdCategory == null )
        {
            return redirect( request, getHomeUrl( request ) );
        }

        UrlItem url = new UrlItem( JSP_DO_REMOVE_CATEGORY );
        url.addParameter( PARAMETER_ID_CATEGORY, strIdCategory );

        return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_CATEGORY, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Perform the category supression
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    @Action( ACTION_REMOVE_CATEGORY )
    public String doRemoveCategory( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        int nIdCategory = -1;

        if ( ( strIdCategory != null ) && !strIdCategory.equals( SuggestUtils.EMPTY_STRING )
                && RBACService.isAuthorized( Category.RESOURCE_TYPE, strIdCategory, CategoryResourceIdService.PERMISSION_MANAGE, (User) getUser( ) ) )
        {
            try
            {
                nIdCategory = Integer.parseInt( strIdCategory );
            }
            catch( NumberFormatException ne )
            {
                AppLogService.error( ne.getMessage( ), ne );
            }
        }

        if ( nIdCategory != -1 )
        {
            if ( CategoryHome.isAssociateToSuggest( nIdCategory, plugin ) )
            {
                addError( MESSAGE_CATEGORY_ASSOCIATE_TO_SUGGEST, getLocale( ) );
                return redirectView( request, VIEW_MANAGE_CATEGORY );
            }

            CategoryHome.remove( nIdCategory, plugin );
        }

        return redirect( request, getJspManageCategory( request ) );
    }

    /**
     * Get the request data and if there is no error insert the data in the category object specified in parameter. return null if there is no error or else
     * return the localized error message
     * 
     * @param request
     *            the request
     * @param category
     *            the category Object
     * @return null if there is no error or else return the localized error message
     */
    private String getCategoryData( HttpServletRequest request, Category category )
    {
        String strTitle = ( request.getParameter( PARAMETER_TITLE ) == null ) ? null : request.getParameter( PARAMETER_TITLE ).trim( );
        String strColor = ( request.getParameter( PARAMETER_COLOR ) == null ) ? null : request.getParameter( PARAMETER_COLOR ).trim( );
        String strFieldError = SuggestUtils.EMPTY_STRING;

        if ( ( strTitle == null ) || strTitle.equals( SuggestUtils.EMPTY_STRING ) )
        {
            strFieldError = FIELD_TITLE;
        }

        if ( ( strColor == null ) || strColor.equals( SuggestUtils.EMPTY_STRING ) )
        {
            strFieldError = FIELD_COLOR;
        }

        // Mandatory fields
        if ( !strFieldError.equals( SuggestUtils.EMPTY_STRING ) )
        {
            Object [ ] tabRequiredFields = {
                I18nService.getLocalizedString( strFieldError, getLocale( ) )
            };

            return I18nService.getLocalizedString( MESSAGE_MANDATORY_FIELD, tabRequiredFields, getLocale( ) );
        }

        category.setTitle( strTitle );
        category.setColor( strColor );

        return null;
    }

    /**
     * return the url of manage category
     * 
     * @param request
     *            the request
     * @return the url of manage category
     */
    private String getJspManageCategory( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_CATEGORY;
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
