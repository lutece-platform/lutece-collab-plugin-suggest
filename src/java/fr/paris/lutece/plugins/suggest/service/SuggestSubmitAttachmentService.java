/*
 * Copyright (c) 2002-2026, City of Paris
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
package fr.paris.lutece.plugins.suggest.service;

import fr.paris.lutece.plugins.suggest.business.SuggestSubmitAttachment;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitAttachmentHome;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.FileServiceException;
import fr.paris.lutece.portal.service.file.IFileStoreServiceProvider;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.upload.MultipartItem;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.filesystem.UploadUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores, lists and removes the files attached to the suggest submits, kept by the core file service.
 */
@ApplicationScoped
public class SuggestSubmitAttachmentService
{
    @Inject
    private FileService _fileService;

    /**
     * Stores files and attaches them to a suggest submit.
     *
     * @param nIdSuggestSubmit
     *            the suggest submit id
     * @param nIdEntry
     *            the id of the file field
     * @param listFiles
     *            the uploaded files
     * @throws FileServiceException
     *             if a file cannot be stored
     */
    public void attach( int nIdSuggestSubmit, int nIdEntry, List<MultipartItem> listFiles ) throws FileServiceException
    {
        IFileStoreServiceProvider provider = getProvider( );

        for ( MultipartItem fileItem : listFiles )
        {
            SuggestSubmitAttachment attachment = new SuggestSubmitAttachment( );
            attachment.setIdSuggestSubmit( nIdSuggestSubmit );
            attachment.setIdEntry( nIdEntry );
            attachment.setFileKey( provider.storeFileItem( fileItem ) );
            attachment.setFileName( UploadUtil.cleanFileName( fileItem.getName( ).trim( ) ) );
            attachment.setMimeType( fileItem.getContentType( ) );
            attachment.setSize( fileItem.getSize( ) );
            SuggestSubmitAttachmentHome.create( attachment, getPlugin( ) );
        }
    }

    /**
     * Lists the attachments of a suggest submit, in upload order.
     *
     * @param nIdSuggestSubmit
     *            the suggest submit id
     * @return the attachments
     */
    public List<SuggestSubmitAttachment> getAttachments( int nIdSuggestSubmit )
    {
        return SuggestSubmitAttachmentHome.findBySuggestSubmit( nIdSuggestSubmit, getPlugin( ) );
    }

    /**
     * Returns the download link of each attachment of a suggest submit.
     *
     * @param listAttachments
     *            the attachments
     * @param bBackOffice
     *            true for back office links
     * @return the links by attachment id
     */
    public Map<String, String> getDownloadUrls( List<SuggestSubmitAttachment> listAttachments, boolean bBackOffice )
    {
        Map<String, String> mapUrls = new LinkedHashMap<>( );

        for ( SuggestSubmitAttachment attachment : listAttachments )
        {
            String strUrl = bBackOffice ? getProvider( ).getFileDownloadUrlBO( attachment.getFileKey( ) )
                    : getProvider( ).getFileDownloadUrlFO( attachment.getFileKey( ) );
            mapUrls.put( Integer.toString( attachment.getIdAttachment( ) ), strUrl );
        }

        return mapUrls;
    }

    /**
     * Returns an attachment.
     *
     * @param nIdAttachment
     *            the attachment id
     * @return the attachment, or null
     */
    public SuggestSubmitAttachment findByPrimaryKey( int nIdAttachment )
    {
        return SuggestSubmitAttachmentHome.findByPrimaryKey( nIdAttachment, getPlugin( ) );
    }

    /**
     * Removes an attachment and its file.
     *
     * @param attachment
     *            the attachment
     */
    public void remove( SuggestSubmitAttachment attachment )
    {
        try
        {
            getProvider( ).delete( attachment.getFileKey( ) );
        }
        catch( FileServiceException e )
        {
            AppLogService.error( "Unable to delete the file {} of the attachment {}", attachment.getFileKey( ), attachment.getIdAttachment( ), e );
        }

        SuggestSubmitAttachmentHome.remove( attachment.getIdAttachment( ), getPlugin( ) );
    }

    /**
     * Removes the attachments of a suggest submit and their files.
     *
     * @param nIdSuggestSubmit
     *            the suggest submit id
     */
    public void removeBySuggestSubmit( int nIdSuggestSubmit )
    {
        getAttachments( nIdSuggestSubmit ).forEach( this::remove );
    }

    /**
     * Returns the default file store of the core.
     *
     * @return the file store provider
     */
    private IFileStoreServiceProvider getProvider( )
    {
        return _fileService.getFileStoreServiceProvider( );
    }

    /**
     * Returns the suggest plugin.
     *
     * @return the plugin
     */
    private static Plugin getPlugin( )
    {
        return PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME );
    }
}
