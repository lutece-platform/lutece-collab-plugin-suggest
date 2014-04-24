/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import fr.paris.lutece.plugins.digglike.service.DiggSubmitService;
import fr.paris.lutece.plugins.digglike.utils.DiggUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;


/**
*
* class EntryTypeVideo
*
*/
public class EntryTypeVideo extends Entry
{
    private static final String PARAMETER_CREDITS = "credits";
    private static final String PARAMETER_AUTOSTART = "autostart";
    private static final String PARAMETER_LOOP = "loop";
    private static final String PARAMETER_QUALITY = "quality";
    private static final String PARAMETER_ALIGNMENT = "alignment";
    private static final String PARAMETER_MENU = "menu";
    private static final String MESSAGE_FILE_TOO_HEAVY = "digglike.message.fileTooHeavy";
    private static final String PROPERTY_CREDITS = "digglike.createEntry.labelCredits";
    private final String _template_create = "admin/plugins/digglike/create_entry_type_video.html";
    private final String _template_modify = "admin/plugins/digglike/modify_entry_type_video.html";
    private final String _template_html_code_form = "admin/plugins/digglike/html_code_form_entry_type_video.html";
    private final String _template_html_code_response = "admin/plugins/digglike/html_code_response_entry_type_video.html";

    /**
     * Get the HtmlCode  of   the entry
     * @return the HtmlCode  of   the entry
     *
     * */
    public String getTemplateHtmlCodeForm(  )
    {
        return _template_html_code_form;
    }

    /**
     * Get the request data
     * @param request HttpRequest
     * @param locale the local
     * @return null if all data required are in the request else the url of jsp error
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
        String strShowInDiggSubmitList = request.getParameter( PARAMETER_SHOW_IN_DIGG_SUBMIT_LIST );
        String strAutostart = request.getParameter( PARAMETER_AUTOSTART );
        String strLoop = request.getParameter( PARAMETER_LOOP );
        String strQuality = request.getParameter( PARAMETER_QUALITY );
        String strAlignment = request.getParameter( PARAMETER_ALIGNMENT );
        String strMenu = request.getParameter( PARAMETER_MENU );

        int nWidth = -1;
        int nHeight = -1;

        String strFieldError = EMPTY_STRING;

        if ( ( strTitle == null ) || strTitle.trim(  ).equals( EMPTY_STRING ) )
        {
            strFieldError = FIELD_TITLE;
        }

        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        try
        {
            if ( !( ( strHeight == null ) || strHeight.trim(  ).equals( EMPTY_STRING ) ) )
            {
                nHeight = Integer.parseInt( strHeight );
            }
        }
        catch ( NumberFormatException ne )
        {
            strFieldError = FIELD_HEIGHT;
        }

        try
        {
            if ( !( ( strWidth == null ) || strWidth.trim(  ).equals( EMPTY_STRING ) ) )
            {
                nWidth = Integer.parseInt( strWidth );
            }
        }
        catch ( NumberFormatException ne )
        {
            strFieldError = FIELD_WIDTH;
        }

        if ( !strFieldError.equals( EMPTY_STRING ) )
        {
            Object[] tabRequiredFields = { I18nService.getLocalizedString( strFieldError, locale ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_NUMERIC_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        List<EntryAdditionalAttribute> entryAdditionalAttributeList = new ArrayList<EntryAdditionalAttribute>(  );
        EntryAdditionalAttribute entryAdditionalAttributeAutostart = new EntryAdditionalAttribute(  );
        entryAdditionalAttributeAutostart.setName( PARAMETER_AUTOSTART );
        entryAdditionalAttributeAutostart.setValue( strAutostart );
        entryAdditionalAttributeList.add( entryAdditionalAttributeAutostart );

        EntryAdditionalAttribute entryAdditionalAttributeLoop = new EntryAdditionalAttribute(  );
        entryAdditionalAttributeLoop.setName( PARAMETER_LOOP );
        entryAdditionalAttributeLoop.setValue( strLoop );
        entryAdditionalAttributeList.add( entryAdditionalAttributeLoop );

        EntryAdditionalAttribute entryAdditionalAttributeAlignment = new EntryAdditionalAttribute(  );
        entryAdditionalAttributeAlignment.setName( PARAMETER_ALIGNMENT );
        entryAdditionalAttributeAlignment.setValue( strAlignment );
        entryAdditionalAttributeList.add( entryAdditionalAttributeAlignment );

        EntryAdditionalAttribute entryAdditionalAttributeQuality = new EntryAdditionalAttribute(  );
        entryAdditionalAttributeQuality.setName( PARAMETER_QUALITY );
        entryAdditionalAttributeQuality.setValue( strQuality );
        entryAdditionalAttributeList.add( entryAdditionalAttributeQuality );

        EntryAdditionalAttribute entryAdditionalAttributeMenu = new EntryAdditionalAttribute(  );
        entryAdditionalAttributeMenu.setName( PARAMETER_MENU );
        entryAdditionalAttributeMenu.setValue( strMenu );
        entryAdditionalAttributeList.add( entryAdditionalAttributeMenu );

        this.setTitle( strTitle );
        this.setHelpMessage( DiggUtils.trim( strHelpMessage ) );
        this.setComment( strComment );

        this.setDefaultValue( strValue );
        this.setWidth( nWidth );
        this.setHeight( nHeight );

        this.setEntryAdditionalAttributeList( entryAdditionalAttributeList );

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
     * save in the list of response the response associate to the entry in the form submit
     * @param nIdDiggSubmit the id of the DiggSubmit
     * @param request HttpRequest
     * @param listResponse the list of response associate to the entry in the form submit
     * @param locale the locale
     * @param plugin the plugin
     * @return a Form error object if there is an error in the response
     */
    public FormError getResponseData( int nIdDiggSubmit, HttpServletRequest request, List<Response> listResponse,
        Locale locale, Plugin plugin )
    {
        String strCredits = request.getParameter( PARAMETER_CREDITS + this.getIdEntry(  ) );

        MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
        FileItem item = mRequest.getFile( DiggUtils.EMPTY_STRING + this.getIdEntry(  ) );

        byte[] bytes = item.get(  );

        Response response = new Response(  );
        response.setEntry( this );

        if ( bytes != null )
        {
            VideoType video = new VideoType(  );
            video.setVideo( bytes );
            video.setMimeType( item.getContentType(  ) );
            video.setCredits( strCredits );
            video.setIdDiggSubmit( nIdDiggSubmit );

            try
            {
                VideoTypeHome.create( video, plugin );

                String strMenu = "false";
                String strLoop = "false";
                String strAutostart = "false";
                //                String strAlignment = "bottom";
                String strQuality = "low";

                for ( EntryAdditionalAttribute attr : this.getEntryAdditionalAttributeList(  ) )
                {
                    if ( attr.getName(  ).equals( PARAMETER_AUTOSTART ) )
                    {
                        strAutostart = attr.getValue(  );
                    }
                    //                    else if ( attr.getName(  ).equals( PARAMETER_ALIGNMENT ) )
                    //                    {
                    //                        strAlignment = attr.getValue(  );
                    //                    }
                    else if ( attr.getName(  ).equals( PARAMETER_LOOP ) )
                    {
                        strLoop = attr.getValue(  );
                    }
                    else if ( attr.getName(  ).equals( PARAMETER_QUALITY ) )
                    {
                        strQuality = attr.getValue(  );
                    }
                    else if ( attr.getName(  ).equals( PARAMETER_MENU ) )
                    {
                        strMenu = attr.getValue(  );
                    }
                }

                String strResponse = "<div id='mediaspace" + nIdDiggSubmit + "'></div>" +
                    "<script type='text/javascript' src='js/player/swfobject.js'></script>" +
                    "<script type='text/javascript'>" + " var so = new SWFObject('js/player/player.swf','mpl','" +
                    this.getWidth(  ) + "','" + this.getHeight(  ) + "','9');" +
                    "  so.addParam('allowfullscreen','true');" + "  so.addParam('allowscriptaccess','always');" +
                    "  so.addParam('wmode','opaque');" + "  so.addParam('quality','" + strQuality + "');" +
                    "  so.addParam('menu','" + strMenu + "');" +
                    "  so.addVariable('file','../../jsp/site/plugins/digglike/getVideo.jsp?video_id=" + nIdDiggSubmit +
                    "');" + "  so.addVariable('provider','video');" + "  so.addVariable('autostart','" + strAutostart +
                    "');" + "  so.addVariable('icons','false');" + "  so.addVariable('repeat','" + strLoop + "');" +
                    "  so.write('mediaspace" + nIdDiggSubmit + "');" + "</script>";

                strResponse += ( "<br /><b>" + I18nService.getLocalizedString( PROPERTY_CREDITS, locale ) +
                "&nbsp;:&nbsp;</b>" + strCredits );

                response.setValueResponse( strResponse );
            }
            catch ( com.mysql.jdbc.PacketTooBigException e )
            {
                //Remove the digg submit potentially created
                DiggSubmitService.getService(  ).remove( nIdDiggSubmit, plugin );
                //Remove the video potentially created
                VideoTypeHome.remove( nIdDiggSubmit, plugin );

                FormError formError = new FormError(  );
                formError.setTitleQuestion( this.getTitle(  ) );
                formError.setErrorMessage( I18nService.getLocalizedString( MESSAGE_FILE_TOO_HEAVY, locale ) );

                return formError;
            }
        }
        else
        {
            if ( this.isMandatory(  ) )
            {
                FormError formError = new FormError(  );
                formError.setMandatoryError( true );
                formError.setTitleQuestion( this.getTitle(  ) );

                return formError;
            }
        }

        listResponse.add( response );

        return null;
    }

    /**
     * Get the template of the html code of the response value  associate to the entry
    * @return the template of the html code of the response value  associate to the entry
     */
    public String getTemplateHtmlCodeResponse(  )
    {
        return _template_html_code_response;
    }
}
