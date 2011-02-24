/*
 * Copyright (c) 2002-2010, Mairie de Paris
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
package fr.paris.lutece.plugins.digglike.business;

import fr.paris.lutece.plugins.digglike.utils.DiggUtils;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.Paginator;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * class EntryTypeText
 *
 */
public class EntryTypeText extends Entry
{
    private final String _template_create = "admin/plugins/digglike/create_entry_type_text.html";
    private final String _template_modify = "admin/plugins/digglike/modify_entry_type_text.html";
    private final String _template_html_code_form = "admin/plugins/digglike/html_code_form_entry_type_text.html";
    private final String _template_html_code_response = "admin/plugins/digglike/html_code_response_entry_type_text.html";

    /**
     * Get the the template of  HtmlCode  of   the entry
     * @return the template HtmlCode  of   the entry
     *
     * */
    public String getTemplateHtmlCodeForm(  )
    {
        return _template_html_code_form;
    }

    /**
     * Get the request data
     * @param request HttpRequest
     * @param locale the locale
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
        String strMaxSizeEnter = request.getParameter( PARAMETER_MAX_SIZE_ENTER );
        String strShowInDiggSubmitList = request.getParameter( PARAMETER_SHOW_IN_DIGG_SUBMIT_LIST );

        int nWidth = -1;
        int nMaxSizeEnter = -1;

        String strFieldError = EMPTY_STRING;

        if ( ( strTitle == null ) || strTitle.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_TITLE;
        }

        else if ( ( strWidth == null ) || strWidth.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_WIDTH;
        }

        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        try
        {
            nWidth = Integer.parseInt( strWidth );
        }
        catch ( NumberFormatException ne )
        {
            strFieldError = FIELD_WIDTH;
        }

        try
        {
            if ( ( strMaxSizeEnter != null ) && !strMaxSizeEnter.trim(  ).equals( EMPTY_STRING ) )
            {
                nMaxSizeEnter = Integer.parseInt( strMaxSizeEnter );
            }
        }
        catch ( NumberFormatException ne )
        {
            strFieldError = FIELD_MAX_SIZE_ENTER;
        }

        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_NUMERIC_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        this.setTitle( strTitle );
        this.setHelpMessage( DiggUtils.trim( strHelpMessage ) );
        this.setComment( strComment );

        this.setDefaultValue( strValue );
        this.setWidth( nWidth );
        this.setMaxSizeEnter( nMaxSizeEnter );

        if ( strMandatory != null )
        {
            this.setMandatory( true );
        }
        else
        {
            this.setMandatory( false );
        }

        if ( strShowInDiggSubmitList != null )
        {
            this.setShowInDiggSubmitList( true );
        }
        else
        {
            this.setShowInDiggSubmitList( false );
        }

        return null;
    }

    /**
     * Get the request data
     * @param request HttpRequest
     * @param locale the locale
     * @param plugin the plugin
     * @return null if all data requiered are in the request else the url of jsp error
     */
    public String getRequestData( HttpServletRequest request, Locale locale, Plugin plugin )
    {
        return getRequestData( request, locale );
    }

    /**
     * Get template create url of the entry
     * @return template create url of the entry
     */
    public String getTemplateCreate(  )
    {
        return _template_create;
    }

    /**
     * Get the template modify url  of the entry
     * @return template modify url  of the entry
     */
    public String getTemplateModify(  )
    {
        return _template_modify;
    }

    /**
     * The paginator who is use in the template modify of the entry
     * @param nItemPerPage Number of items to display per page
     * @param strBaseUrl The base Url for build links on each page link
     * @param strPageIndexParameterName The parameter name for the page index
     * @param strPageIndex The current page index
     * @return the paginator who is use in the template modify of the entry
     */
    public Paginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
        String strPageIndex )
    {
        return new Paginator( this.getRegularExpressionList(  ), nItemPerPage, strBaseUrl, strPageIndexParameterName,
            strPageIndex );
    }

    /**
     * return the list of regular expression whose not associate to the entry
     * @param entry the entry
     * @param plugin the plugin
     * @return the list of regular expression whose not associate to the entry
     */
    public ReferenceList getReferenceListRegularExpression( IEntry entry, Plugin plugin )
    {
        ReferenceList refListRegularExpression = new ReferenceList(  );

        if ( RegularExpressionService.getInstance(  ).isAvailable(  ) )
        {
            List<RegularExpression> listRegularExpression = RegularExpressionService.getInstance(  )
                                                                                    .getAllRegularExpression(  );

            for ( RegularExpression regularExpression : listRegularExpression )
            {
                if ( !entry.getRegularExpressionList(  ).contains( regularExpression ) )
                {
                    refListRegularExpression.addItem( regularExpression.getIdExpression(  ),
                        regularExpression.getTitle(  ) );
                }
            }
        }

        return refListRegularExpression;
    }

    /**
     * save in the list of response the response associate to the entry in the form submit
     * @param request HttpRequest
     * @param listResponse the list of response associate to the entry in the form submit
     * @param locale the locale
     * @return a Form error object if there is an error in the response
     */
    public FormError getResponseData( HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        String strValueEntry = request.getParameter( DiggUtils.EMPTY_STRING + this.getIdEntry(  ) ).trim(  );
        List<RegularExpression> listRegularExpression = this.getRegularExpressionList(  );
        Response response = new Response(  );
        response.setEntry( this );

        if ( strValueEntry != null )
        {
            if ( this.isMandatory(  ) )
            {
                if ( strValueEntry.equals( DiggUtils.EMPTY_STRING ) )
                {
                    FormError formError = new FormError(  );
                    formError.setMandatoryError( true );
                    formError.setTitleQuestion( this.getTitle(  ) );

                    return formError;
                }
            }

            if ( ( listRegularExpression != null ) && ( listRegularExpression.size(  ) != 0 ) &&
                    RegularExpressionService.getInstance(  ).isAvailable(  ) )
            {
                for ( RegularExpression regularExpression : listRegularExpression )
                {
                    if ( !RegularExpressionService.getInstance(  ).isMatches( strValueEntry, regularExpression ) )
                    {
                        FormError formError = new FormError(  );
                        formError.setMandatoryError( false );
                        formError.setTitleQuestion( this.getTitle(  ) );
                        formError.setErrorMessage( regularExpression.getErrorMessage(  ) );

                        return formError;
                    }
                }
            }

            response.setValueResponse( strValueEntry );
        }

        listResponse.add( response );

        return null;
    }

    /**
     * save in the list of response the response associate to the entry in the form submit
     * @param nIdDiggSubmit The if of the DiggSubmit
     * @param request HttpRequest
     * @param listResponse the list of response associate to the entry in the form submit
     * @param locale the locale
     * @param plugin the plugin
     * @return a Form error object if there is an error in the response
     */
    public FormError getResponseData( int nIdDiggSubmit, HttpServletRequest request, List<Response> listResponse,
        Locale locale, Plugin plugin )
    {
        return getResponseData( request, listResponse, locale );
    }

    /**
     * Get the template of the html code of the response value  associate to the entry
     * @param response the response associate to the entry
     * @return the template of the html code of the response value  associate to the entry
     */
    public String getTemplateHtmlCodeResponse(  )
    {
        return _template_html_code_response;
    }
}
