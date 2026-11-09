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
package fr.paris.lutece.plugins.suggest.service.upload;

import fr.paris.lutece.plugins.asynchronousupload.service.AbstractAsynchronousUploadHandler;
import fr.paris.lutece.plugins.asynchronousupload.service.SessionMultipartItemList;
import fr.paris.lutece.plugins.suggest.business.EntryHome;
import fr.paris.lutece.plugins.suggest.business.EntryTypeFile;
import fr.paris.lutece.plugins.suggest.business.IEntry;
import fr.paris.lutece.plugins.suggest.service.SuggestPlugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.upload.MultipartItem;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.filesystem.UploadUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

/**
 * Keeps in session the files an author attaches to the file fields of a proposal before submitting it, and refuses the files the options of the field
 * forbid: a type not accepted, a file too big, too many files.
 */
@ApplicationScoped
@Named( SuggestAsynchronousUploadHandler.BEAN_NAME )
public class SuggestAsynchronousUploadHandler extends AbstractAsynchronousUploadHandler
{
    public static final String BEAN_NAME = "suggest.suggestAsynchronousUploadHandler";
    private static final String PREFIX_FIELD = "attachments_";
    private static final String HANDLER_NAME = "suggestAsynchronousUploadHandler";
    private static final String SESSION_ATTRIBUTE_UPLOADED_FILES = "suggest.uploadedFiles";
    private static final String MESSAGE_UNKNOWN_FIELD = "suggest.message.attachments.unknownField";
    private static final String MESSAGE_EXTENSION = "suggest.message.attachments.extension";
    private static final String MESSAGE_MAX_SIZE = "suggest.message.attachments.maxSize";
    private static final String MESSAGE_MAX_FILES = "suggest.message.attachments.maxFiles";

    /**
     * Returns the upload field name of a file field.
     *
     * @param nIdEntry
     *            the id of the file field
     * @return the upload field name
     */
    public static String getFieldName( int nIdEntry )
    {
        return PREFIX_FIELD + nIdEntry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String canUploadFiles( HttpServletRequest request, String strFieldName, List<MultipartItem> listFileItemsToUpload, Locale locale )
    {
        EntryTypeFile entry = getEntry( strFieldName );

        if ( entry == null )
        {
            return I18nService.getLocalizedString( MESSAGE_UNKNOWN_FIELD, locale );
        }

        if ( getListUploadedFiles( strFieldName, request.getSession( ) ).size( ) + listFileItemsToUpload.size( ) > entry.getMaxFiles( ) )
        {
            return I18nService.getLocalizedString( MESSAGE_MAX_FILES, new Object [ ] {
                    entry.getMaxFiles( )
            }, locale );
        }

        for ( MultipartItem fileItem : listFileItemsToUpload )
        {
            if ( !entry.isAcceptedFileName( fileItem.getName( ) ) )
            {
                return I18nService.getLocalizedString( MESSAGE_EXTENSION, new Object [ ] {
                        entry.getExtensions( )
                }, locale );
            }

            if ( fileItem.getSize( ) > entry.getMaxFileSizeBytes( ) )
            {
                return I18nService.getLocalizedString( MESSAGE_MAX_SIZE, new Object [ ] {
                        entry.getMaxFileSize( )
                }, locale );
            }
        }

        return null;
    }

    /**
     * Returns the file field an upload field name designates.
     *
     * @param strFieldName
     *            the upload field name
     * @return the file field, or null when the name designates no file field
     */
    private static EntryTypeFile getEntry( String strFieldName )
    {
        if ( !StringUtils.startsWith( strFieldName, PREFIX_FIELD ) || !StringUtils.isNumeric( strFieldName.substring( PREFIX_FIELD.length( ) ) ) )
        {
            return null;
        }

        IEntry entry = EntryHome.findByPrimaryKey( Integer.parseInt( strFieldName.substring( PREFIX_FIELD.length( ) ) ),
                PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME ) );

        return ( entry instanceof EntryTypeFile entryTypeFile ) ? entryTypeFile : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MultipartItem> getListUploadedFiles( String strFieldName, HttpSession session )
    {
        return getSessionFiles( session ).computeIfAbsent( strFieldName, s -> new SessionMultipartItemList( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFileItemToUploadedFilesList( MultipartItem fileItem, String strFieldName, HttpServletRequest request )
    {
        String strFileName = UploadUtil.cleanFileName( fileItem.getName( ).trim( ) );
        List<MultipartItem> listUploadedFiles = getListUploadedFiles( strFieldName, request.getSession( ) );

        synchronized( listUploadedFiles )
        {
            boolean bAlreadyUploaded = listUploadedFiles.stream( )
                    .anyMatch( f -> UploadUtil.cleanFileName( f.getName( ).trim( ) ).equals( strFileName ) && f.getSize( ) == fileItem.getSize( ) );

            if ( !bAlreadyUploaded )
            {
                listUploadedFiles.add( fileItem );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeFileItem( String strFieldName, HttpSession session, int nIndex )
    {
        List<MultipartItem> listUploadedFiles = getListUploadedFiles( strFieldName, session );

        if ( nIndex >= 0 && nIndex < listUploadedFiles.size( ) )
        {
            delete( listUploadedFiles.remove( nIndex ) );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHandlerName( )
    {
        return HANDLER_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSessionFiles( HttpSession session )
    {
        Map<String, List<MultipartItem>> mapFiles = findSessionFiles( session );

        if ( mapFiles != null )
        {
            mapFiles.values( ).forEach( list -> list.forEach( SuggestAsynchronousUploadHandler::delete ) );
            session.removeAttribute( SESSION_ATTRIBUTE_UPLOADED_FILES );
        }
    }

    /**
     * Returns the files of the session, by field name, if any file was uploaded.
     *
     * @param session
     *            the session
     * @return the files by field name, or null
     */
    @SuppressWarnings( "unchecked" )
    private static Map<String, List<MultipartItem>> findSessionFiles( HttpSession session )
    {
        return (Map<String, List<MultipartItem>>) session.getAttribute( SESSION_ATTRIBUTE_UPLOADED_FILES );
    }

    /**
     * Returns the files of the session, by field name, created at the first upload.
     *
     * @param session
     *            the session
     * @return the files by field name
     */
    private static synchronized Map<String, List<MultipartItem>> getSessionFiles( HttpSession session )
    {
        Map<String, List<MultipartItem>> mapFiles = findSessionFiles( session );

        if ( mapFiles == null )
        {
            mapFiles = new ConcurrentHashMap<>( );
            session.setAttribute( SESSION_ATTRIBUTE_UPLOADED_FILES, mapFiles );
        }

        return mapFiles;
    }

    /**
     * Deletes the temporary content of an uploaded file.
     *
     * @param fileItem
     *            the file
     */
    private static void delete( MultipartItem fileItem )
    {
        try
        {
            fileItem.delete( );
        }
        catch( IOException e )
        {
            AppLogService.error( "Unable to delete the uploaded file {}", fileItem.getName( ), e );
        }
    }
}
