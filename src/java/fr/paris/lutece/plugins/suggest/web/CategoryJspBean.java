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
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * class CategoryJspBean
 *
 */
public class CategoryJspBean extends PluginAdminPageJspBean
{
    private static final long serialVersionUID = -4972338182883838725L;

    // templates
    private static final String TEMPLATE_MANAGE_CATEGORY = "admin/plugins/suggest/manage_category.html";
    private static final String TEMPLATE_CREATE_CATEGORY = "admin/plugins/suggest/create_category.html";
    private static final String TEMPLATE_MODIFY_CATEGORY = "admin/plugins/suggest/modify_category.html";

    // Markers
    private static final String MARK_CATEGORY_LIST = "category_list";
    private static final String MARK_CATEGORY = "category";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";

    // parameters form
    private static final String PARAMETER_ID_CATEGORY = "id_category";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
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
    private static final String JSP_DO_REMOVE_CATEGORY = "jsp/admin/plugins/suggest/DoRemoveCategory.jsp";

    // session fields
    private int _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( PROPERTY_ITEM_PER_PAGE, 15 );
    private String _strCurrentPageIndexExport;
    private int _nItemsPerPageCategory;

    /**
     * Return management category( list of category)
     * 
     * @param request
     *            The Http request
     * @return Html form
     */
    public String getManageCategory( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        Locale locale = getLocale( );
        Map<String, Object> model = new HashMap<>( );
        List<Category> listCategory = CategoryHome.getList( plugin );
        listCategory = (List<Category>) RBACService.getAuthorizedCollection( listCategory, CategoryResourceIdService.PERMISSION_MANAGE, getUser( ) );

        _strCurrentPageIndexExport = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndexExport );
        _nItemsPerPageCategory = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPageCategory, _nDefaultItemsPerPage );

        Paginator<Category> paginator = new Paginator<Category>( listCategory, _nItemsPerPageCategory, getJspManageCategory( request ), PARAMETER_PAGE_INDEX,
                _strCurrentPageIndexExport );
        model.put( MARK_PAGINATOR, paginator );
        model.put( MARK_NB_ITEMS_PER_PAGE, SuggestUtils.EMPTY_STRING + _nItemsPerPageCategory );
        model.put( MARK_CATEGORY_LIST, paginator.getPageItems( ) );
        setPageTitleProperty( PROPERTY_MANAGE_CATEGORY_TITLE );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_MANAGE_CATEGORY, locale, model );

        return getAdminPage( templateList.getHtml( ) );
    }

    /**
     * Gets the category creation page
     * 
     * @param request
     *            The HTTP request
     * @return The category creation page
     */
    public String getCreateCategory( HttpServletRequest request )
    {
        if ( !RBACService.isAuthorized( Category.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, CategoryResourceIdService.PERMISSION_MANAGE, getUser( ) ) )
        {
            return getManageCategory( request );
        }

        Locale locale = getLocale( );
        Map<String, Object> model = new HashMap<>( );
        setPageTitleProperty( PROPERTY_CREATE_CATEGORY_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_CATEGORY, locale, model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the category creation
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doCreateCategory( HttpServletRequest request )
    {
        if ( !RBACService.isAuthorized( Category.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID, CategoryResourceIdService.PERMISSION_MANAGE, getUser( ) ) )
        {
            return getJspManageCategory( request );
        }

        Category category = new Category( );
        String strError = getCategoryData( request, category );

        if ( strError != null )
        {
            return strError;
        }

        CategoryHome.create( category, getPlugin( ) );

        return getJspManageCategory( request );
    }

    /**
     * Gets the category modification page
     * 
     * @param request
     *            The HTTP request
     * @return the category modification page
     */
    public String getModifyCategory( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        Locale locale = getLocale( );
        Category category;
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        Map<String, Object> model = new HashMap<>( );

        int nIdCategory = -1;

        if ( ( strIdCategory != null ) && !strIdCategory.equals( SuggestUtils.EMPTY_STRING )
                && RBACService.isAuthorized( Category.RESOURCE_TYPE, strIdCategory, CategoryResourceIdService.PERMISSION_MANAGE, getUser( ) ) )
        {
            try
            {
                nIdCategory = Integer.parseInt( strIdCategory );
            }
            catch( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getManageCategory( request );
            }
        }
        else
        {
            return getManageCategory( request );
        }

        category = CategoryHome.findByPrimaryKey( nIdCategory, plugin );
        model.put( MARK_CATEGORY, category );
        setPageTitleProperty( PROPERTY_MODIFY_CATEGORY_TITLE );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_CATEGORY, locale, model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Perform the category modification
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doModifyCategory( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        Category category;
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        int nIdCategory = -1;

        if ( ( strIdCategory != null ) && !strIdCategory.equals( SuggestUtils.EMPTY_STRING )
                && RBACService.isAuthorized( Category.RESOURCE_TYPE, strIdCategory, CategoryResourceIdService.PERMISSION_MANAGE, getUser( ) ) )
        {
            try
            {
                nIdCategory = Integer.parseInt( strIdCategory );
            }
            catch( NumberFormatException ne )
            {
                AppLogService.error( ne );

                return getJspManageCategory( request );
            }
        }
        else
        {
            return getJspManageCategory( request );
        }

        category = new Category( );
        category.setIdCategory( nIdCategory );

        String strError = getCategoryData( request, category );

        if ( strError != null )
        {
            return strError;
        }

        CategoryHome.update( category, plugin );

        return getJspManageCategory( request );
    }

    /**
     * Gets the confirmation page of delete category
     * 
     * @param request
     *            The HTTP request
     * @return the confirmation page of delete category
     */
    public String getConfirmRemoveCategory( HttpServletRequest request )
    {
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );

        if ( strIdCategory == null )
        {
            return getHomeUrl( request );
        }

        UrlItem url = new UrlItem( JSP_DO_REMOVE_CATEGORY );
        url.addParameter( PARAMETER_ID_CATEGORY, strIdCategory );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_CATEGORY, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Perform the category supression
     * 
     * @param request
     *            The HTTP request
     * @return The URL to go after performing the action
     */
    public String doRemoveCategory( HttpServletRequest request )
    {
        Plugin plugin = getPlugin( );
        String strIdCategory = request.getParameter( PARAMETER_ID_CATEGORY );
        int nIdCategory = -1;

        if ( ( strIdCategory != null ) && !strIdCategory.equals( SuggestUtils.EMPTY_STRING )
                && RBACService.isAuthorized( Category.RESOURCE_TYPE, strIdCategory, CategoryResourceIdService.PERMISSION_MANAGE, getUser( ) ) )
        {
            try
            {
                nIdCategory = Integer.parseInt( strIdCategory );
            }
            catch( NumberFormatException ne )
            {
                AppLogService.error( ne );
            }
        }

        if ( nIdCategory != -1 )
        {
            if ( CategoryHome.isAssociateToSuggest( nIdCategory, plugin ) )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_CATEGORY_ASSOCIATE_TO_SUGGEST, AdminMessage.TYPE_STOP );
            }

            CategoryHome.remove( nIdCategory, plugin );
        }

        return getJspManageCategory( request );
    }

    /**
     * Get the request data and if there is no error insert the data in the regularExpression object specified in parameter. return null if there is no error or
     * else return the error page url
     * 
     * @param request
     *            the request
     * @param category
     *            the category Object
     * @return null if there is no error or else return the error page url
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

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
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
}
