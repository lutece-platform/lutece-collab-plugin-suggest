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
package fr.paris.lutece.plugins.suggest.business;

import fr.paris.lutece.plugins.suggest.service.upload.SuggestAsynchronousUploadHandler;
import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.upload.MultipartItem;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * A field where the author attaches one or more files, uploaded one by one before the form is sent. Its options are the accepted extensions, the maximum
 * size of a file and the maximum number of files.
 */
public class EntryTypeFile extends Entry
{
    public static final String ATTRIBUTE_MAX_FILES = "maxFiles";
    public static final String ATTRIBUTE_MAX_FILE_SIZE = "maxFileSize";
    public static final String ATTRIBUTE_EXTENSIONS = "extensions";
    private static final int DEFAULT_MAX_FILES = 5;
    private static final int DEFAULT_MAX_FILE_SIZE = 10;
    private static final String DEFAULT_EXTENSIONS = "jpg,jpeg,png,pdf,doc,docx,ppt,pptx";
    private static final long MEGA_BYTE = 1024L * 1024L;
    private static final String PARAMETER_MAX_FILES = "max_files";
    private static final String PARAMETER_MAX_FILE_SIZE = "max_file_size";
    private static final String PARAMETER_EXTENSIONS = "extensions";
    private static final String FIELD_MAX_FILES = "suggest.createEntryFile.labelMaxFiles";
    private static final String FIELD_MAX_FILE_SIZE = "suggest.createEntryFile.labelMaxFileSize";
    private static final String MARK_UPLOAD_HANDLER = "upload_handler";
    private static final String MARK_UPLOADED_FILES = "uploaded_files";
    private static final String MARK_FIELD_NAME = "field_name";
    private static final String TEMPLATE_CREATE = "admin/plugins/suggest/create_entry_type_file.html";
    private static final String TEMPLATE_MODIFY = "admin/plugins/suggest/modify_entry_type_file.html";
    private static final String TEMPLATE_HTML_CODE_FORM = "admin/plugins/suggest/html_code_form_entry_type_file.html";
    private static final String TEMPLATE_HTML_CODE_RESPONSE = "admin/plugins/suggest/html_code_response_entry_type_file.html";
    private static final String SEPARATOR = ",";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateHtmlCodeForm( )
    {
        return TEMPLATE_HTML_CODE_FORM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addHtmlFormModel( Map<String, Object> model, HttpServletRequest request )
    {
        SuggestAsynchronousUploadHandler handler = getUploadHandler( );
        String strFieldName = SuggestAsynchronousUploadHandler.getFieldName( getIdEntry( ) );
        model.put( MARK_UPLOAD_HANDLER, handler );
        model.put( MARK_FIELD_NAME, strFieldName );
        model.put( MARK_UPLOADED_FILES, ( request != null ) ? handler.getListUploadedFiles( strFieldName, request.getSession( ) ) : List.of( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFrontOfficeOnly( )
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRequestData( HttpServletRequest request, Locale locale )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );

        if ( StringUtils.isBlank( strTitle ) )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, new Object [ ] {
                    I18nService.getLocalizedString( FIELD_TITLE, locale )
            }, AdminMessage.TYPE_STOP );
        }

        int nMaxFiles = SuggestUtils.getIntegerParameter( request.getParameter( PARAMETER_MAX_FILES ) );
        int nMaxFileSize = SuggestUtils.getIntegerParameter( request.getParameter( PARAMETER_MAX_FILE_SIZE ) );

        if ( nMaxFiles <= 0 || nMaxFileSize <= 0 )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_NUMERIC_FIELD, new Object [ ] {
                    I18nService.getLocalizedString( ( nMaxFiles <= 0 ) ? FIELD_MAX_FILES : FIELD_MAX_FILE_SIZE, locale )
            }, AdminMessage.TYPE_STOP );
        }

        setTitle( strTitle );
        setHelpMessage( SuggestUtils.trim( request.getParameter( PARAMETER_HELP_MESSAGE ) ) );
        setComment( request.getParameter( PARAMETER_COMMENT ) );
        setMandatory( request.getParameter( PARAMETER_MANDATORY ) != null );
        setShowInSuggestSubmitList( request.getParameter( PARAMETER_SHOW_IN_SUGGEST_SUBMIT_LIST ) != null );

        setEntryAdditionalAttributeList( List.of( newAdditionalAttribute( ATTRIBUTE_MAX_FILES, Integer.toString( nMaxFiles ) ),
                newAdditionalAttribute( ATTRIBUTE_MAX_FILE_SIZE, Integer.toString( nMaxFileSize ) ),
                newAdditionalAttribute( ATTRIBUTE_EXTENSIONS, String.join( SEPARATOR, parseExtensions( request.getParameter( PARAMETER_EXTENSIONS ) ) ) ) ) );

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FormError getResponseData( int nIdSuggestSubmit, HttpServletRequest request, List<Response> listResponse, Locale locale, Plugin plugin )
    {
        List<MultipartItem> listFiles = getUploadHandler( ).getListUploadedFiles( SuggestAsynchronousUploadHandler.getFieldName( getIdEntry( ) ),
                request.getSession( ) );

        if ( listFiles.isEmpty( ) )
        {
            if ( !isMandatory( ) )
            {
                return null;
            }

            FormError formError = new FormError( );
            formError.setMandatoryError( true );
            formError.setTitleQuestion( getTitle( ) );

            return formError;
        }

        Response response = new Response( );
        response.setEntry( this );
        response.setValueResponse( listFiles.stream( ).map( MultipartItem::getName ).collect( Collectors.joining( ", " ) ) );
        listResponse.add( response );

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateHtmlCodeResponse( )
    {
        return TEMPLATE_HTML_CODE_RESPONSE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateCreate( )
    {
        return TEMPLATE_CREATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTemplateModify( )
    {
        return TEMPLATE_MODIFY;
    }

    /**
     * Returns the maximum number of files.
     *
     * @return the maximum number of files
     */
    public int getMaxFiles( )
    {
        return getIntAttribute( ATTRIBUTE_MAX_FILES, DEFAULT_MAX_FILES );
    }

    /**
     * Returns the maximum size of a file.
     *
     * @return the size in MB
     */
    public int getMaxFileSize( )
    {
        return getIntAttribute( ATTRIBUTE_MAX_FILE_SIZE, DEFAULT_MAX_FILE_SIZE );
    }

    /**
     * Returns the maximum size of a file.
     *
     * @return the size in bytes
     */
    public long getMaxFileSizeBytes( )
    {
        return getMaxFileSize( ) * MEGA_BYTE;
    }

    /**
     * Returns the accepted extensions.
     *
     * @return the extensions, comma separated
     */
    public String getExtensions( )
    {
        return String.join( SEPARATOR, getExtensionList( ) );
    }

    /**
     * Returns the accepted extensions as the accept attribute of a file input.
     *
     * @return the extensions, each prefixed with a dot
     */
    public String getAccept( )
    {
        return getExtensionList( ).stream( ).map( ext -> "." + ext ).collect( Collectors.joining( SEPARATOR ) );
    }

    /**
     * Tells whether a file name has an accepted extension.
     *
     * @param strFileName
     *            the file name
     * @return true if the extension is accepted
     */
    public boolean isAcceptedFileName( String strFileName )
    {
        return getExtensionList( ).contains( StringUtils.lowerCase( FilenameUtils.getExtension( strFileName ), Locale.ROOT ) );
    }

    /**
     * Returns the accepted extensions, lower case.
     *
     * @return the extensions
     */
    private List<String> getExtensionList( )
    {
        List<String> listExtensions = parseExtensions( getAdditionalAttribute( ATTRIBUTE_EXTENSIONS ) );

        return listExtensions.isEmpty( ) ? parseExtensions( DEFAULT_EXTENSIONS ) : listExtensions;
    }

    /**
     * Parses a list of extensions written with commas or spaces, with or without dots.
     *
     * @param strExtensions
     *            the extensions
     * @return the distinct extensions, lower case
     */
    private static List<String> parseExtensions( String strExtensions )
    {
        return Arrays.stream( StringUtils.defaultString( strExtensions ).split( "[,;\\s]+" ) ).map( s -> StringUtils.removeStart( s.trim( ), "." ) )
                .map( s -> s.toLowerCase( Locale.ROOT ) ).filter( StringUtils::isNotEmpty ).distinct( ).toList( );
    }

    /**
     * Returns the value of a numeric option.
     *
     * @param strName
     *            the option name
     * @param nDefault
     *            the value when the option is missing or not a positive number
     * @return the value
     */
    private int getIntAttribute( String strName, int nDefault )
    {
        int nValue = SuggestUtils.getIntegerParameter( getAdditionalAttribute( strName ) );

        return ( nValue > 0 ) ? nValue : nDefault;
    }

    /**
     * Returns the upload handler of the file fields.
     *
     * @return the upload handler
     */
    private static SuggestAsynchronousUploadHandler getUploadHandler( )
    {
        return CDI.current( ).select( SuggestAsynchronousUploadHandler.class ).get( );
    }
}
