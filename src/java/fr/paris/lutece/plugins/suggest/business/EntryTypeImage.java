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
package fr.paris.lutece.plugins.suggest.business;

import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;

import org.apache.commons.fileupload.FileItem;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * class EntryTypeImage
 *
 */
public class EntryTypeImage extends Entry
{
    private static final String PARAMETER_WIDTH = "width";
    private static final String PARAMETER_HEIGHT = "height";
    private static final String MESSAGE_IMAGE_TOO_HEAVY = "suggest.message.imageTooHeavy";
    private final String _template_create = "admin/plugins/suggest/create_entry_type_image.html";
    private final String _template_modify = "admin/plugins/suggest/modify_entry_type_image.html";
    private final String _template_html_code_form = "admin/plugins/suggest/html_code_form_entry_type_image.html";
    private final String _template_html_code_response = "admin/plugins/suggest/html_code_response_entry_type_image.html";

    /**
     * Get the HtmlCode of the entry
     * 
     * @return the HtmlCode of the entry
     *
     * */
    public String getTemplateHtmlCodeForm( )
    {
        return _template_html_code_form;
    }

    /**
     * Get the request data
     * 
     * @param request
     *            HttpRequest
     * @param locale
     *            the locale
     * @return null if all data requiered are in the request else the url of jsp error
     */
    public String getRequestData( HttpServletRequest request, Locale locale )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strHelpMessage = request.getParameter( PARAMETER_HELP_MESSAGE );
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strValue = request.getParameter( PARAMETER_VALUE );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strWidth = request.getParameter( PARAMETER_WIDTH );
        String strHeight = request.getParameter( PARAMETER_HEIGHT );
        String strShowInSuggestSubmitList = request.getParameter( PARAMETER_SHOW_IN_SUGGEST_SUBMIT_LIST );

        int nWidth = -1;
        int nHeight = -1;

        String strFieldError = EMPTY_STRING;

        if ( ( strTitle == null ) || strTitle.trim( ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_TITLE;
        }

        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object [ ] tabRequiredFields = {
                I18nService.getLocalizedString( strFieldError, locale )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        try
        {
            if ( !( ( strHeight == null ) || strHeight.trim( ).equals( EMPTY_STRING ) ) )
            {
                nHeight = Integer.parseInt( strHeight );
            }
        }
        catch( NumberFormatException ne )
        {
            strFieldError = FIELD_HEIGHT;
        }

        try
        {
            if ( !( ( strWidth == null ) || strWidth.trim( ).equals( EMPTY_STRING ) ) )
            {
                nWidth = Integer.parseInt( strWidth );
            }
        }
        catch( NumberFormatException ne )
        {
            strFieldError = FIELD_WIDTH;
        }

        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object [ ] tabRequiredFields = {
                I18nService.getLocalizedString( strFieldError, locale )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_NUMERIC_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        this.setTitle( strTitle );
        this.setHelpMessage( SuggestUtils.trim( strHelpMessage ) );
        this.setComment( strComment );

        this.setDefaultValue( strValue );
        this.setWidth( nWidth );
        this.setHeight( nHeight );

        if ( strMandatory != null )
        {
            this.setMandatory( true );
        }
        else
        {
            this.setMandatory( false );
        }

        if ( strShowInSuggestSubmitList != null )
        {
            this.setShowInSuggestSubmitList( true );
        }
        else
        {
            this.setShowInSuggestSubmitList( false );
        }

        return null;
    }

    /**
     * Get template create url of the entry
     * 
     * @return template create url of the entry
     */
    public String getTemplateCreate( )
    {
        return _template_create;
    }

    /**
     * Get the template modify url of the entry
     * 
     * @return template modify url of the entry
     */
    public String getTemplateModify( )
    {
        return _template_modify;
    }

    /**
     * save in the list of response the response associate to the entry in the form submit
     * 
     * @param nIdSuggestSubmit
     *            the id of the SuggestSubmit
     * @param request
     *            HttpRequest
     * @param listResponse
     *            the list of response associate to the entry in the form submit
     * @param locale
     *            the locale
     * @param plugin
     *            the plugin
     * @return a Form error object if there is an error in the response
     */
    public FormError getResponseData( int nIdSuggestSubmit, HttpServletRequest request, List<Response> listResponse, Locale locale, Plugin plugin )
    {
        MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
        FileItem item = mRequest.getFile( SuggestUtils.EMPTY_STRING + this.getIdEntry( ) );

        byte [ ] bytes = item.get( );

        Response response = new Response( );
        response.setEntry( this );

        if ( bytes != null )
        {
            ImageResource image = new ImageResource( );
            image.setImage( bytes );
            image.setMimeType( item.getContentType( ) );
            response.setImage( image );
            response.setValueResponse( FileUploadService.getFileNameOnly( item ) );
        }
        else
        {
            if ( this.isMandatory( ) )
            {
                FormError formError = new FormError( );
                formError.setMandatoryError( true );
                formError.setTitleQuestion( this.getTitle( ) );

                return formError;
            }
        }

        listResponse.add( response );

        return null;
    }

    /**
     * Get the template of the html code of the response value associate to the entry
     * 
     * @return the template of the html code of the response value associate to the entry
     */
    public String getTemplateHtmlCodeResponse( )
    {
        return _template_html_code_response;
    }
}
