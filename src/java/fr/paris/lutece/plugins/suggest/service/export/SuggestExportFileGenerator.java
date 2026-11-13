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
package fr.paris.lutece.plugins.suggest.service.export;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.paris.lutece.plugins.filegenerator.service.IFileGenerator;
import fr.paris.lutece.plugins.suggest.business.EntryFilter;
import fr.paris.lutece.plugins.suggest.business.EntryHome;
import fr.paris.lutece.plugins.suggest.business.SubmitFilter;
import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.suggest.service.ICommentSubmitService;
import fr.paris.lutece.plugins.suggest.service.ISuggestSubmitService;
import fr.paris.lutece.plugins.suggest.service.SuggestJsonExportService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.file.FileUtil;

/**
 * Generates the JSON export of suggest submits of a suggest, as a file written on the server.
 */
public class SuggestExportFileGenerator implements IFileGenerator
{
    public static final String MIME_TYPE_JSON = "application/json";
    private static final String EXTENSION_JSON = ".json";
    private static final String EXTENSION_ZIP = ".zip";
    private static final String PATTERN_TIMESTAMP = "_yyyy-MM-dd-HH-mm-ss";
    private static final int MAX_NAME_LENGTH = 200;

    private final Suggest _suggest;
    private final List<Integer> _listIdSuggestSubmit;
    private final String _strDescription;
    private final String _strName;
    private final boolean _bZip;
    private final Plugin _plugin;
    private final ISuggestSubmitService _suggestSubmitService;
    private final ICommentSubmitService _commentSubmitService;
    private final SuggestJsonExportService _jsonExportService;

    /**
     * Creates the generator of an export
     *
     * @param suggest
     *            the suggest
     * @param listIdSuggestSubmit
     *            the ids of the suggest submits to export, in the order of the export
     * @param strDescription
     *            the description of the file
     * @param bZip
     *            true to compress the file once generated
     * @param plugin
     *            the plugin
     * @param suggestSubmitService
     *            the suggest submit service
     * @param commentSubmitService
     *            the comment service
     * @param jsonExportService
     *            the JSON export service
     */
    public SuggestExportFileGenerator( Suggest suggest, List<Integer> listIdSuggestSubmit, String strDescription, boolean bZip, Plugin plugin,
            ISuggestSubmitService suggestSubmitService, ICommentSubmitService commentSubmitService, SuggestJsonExportService jsonExportService )
    {
        _suggest = suggest;
        _listIdSuggestSubmit = new ArrayList<>( listIdSuggestSubmit );
        _strDescription = strDescription;
        _strName = StringUtils.substring( FileUtil.normalizeFileName( suggest.getTitle( ) ), 0, MAX_NAME_LENGTH )
                + LocalDateTime.now( ).format( DateTimeFormatter.ofPattern( PATTERN_TIMESTAMP ) );
        _bZip = bZip;
        _plugin = plugin;
        _suggestSubmitService = suggestSubmitService;
        _commentSubmitService = commentSubmitService;
        _jsonExportService = jsonExportService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Path generateFile( ) throws IOException
    {
        Path file = Paths.get( System.getProperty( "java.io.tmpdir" ), _strName + EXTENSION_JSON );
        Files.writeString( file, generateJson( ), StandardCharsets.UTF_8 );

        return file;
    }

    /**
     * Builds the JSON export
     *
     * @return the JSON export
     * @throws JsonProcessingException
     *             if the export cannot be serialized
     */
    public String generateJson( ) throws JsonProcessingException
    {
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdSuggest( _suggest.getIdSuggest( ) );
        _suggest.setEntries( EntryHome.getEntryList( entryFilter, _plugin ) );

        List<SuggestSubmit> listSuggestSubmit = new ArrayList<>( _listIdSuggestSubmit.size( ) );
        SubmitFilter commentFilter = new SubmitFilter( );

        for ( Integer nIdSuggestSubmit : _listIdSuggestSubmit )
        {
            SuggestSubmit suggestSubmit = _suggestSubmitService.findByPrimaryKey( nIdSuggestSubmit, false, true, _plugin );

            if ( suggestSubmit != null )
            {
                commentFilter.setIdSuggestSubmit( nIdSuggestSubmit );
                suggestSubmit.setComments( _commentSubmitService.getCommentSubmitList( commentFilter, _plugin ) );
                suggestSubmit.setSuggest( _suggest );
                listSuggestSubmit.add( suggestSubmit );
            }
        }

        return _jsonExportService.export( _suggest, listSuggestSubmit );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileName( )
    {
        return _strName + ( _bZip ? EXTENSION_ZIP : EXTENSION_JSON );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMimeType( )
    {
        return _bZip ? FileUtil.CONSTANT_MIME_TYPE_ZIP : MIME_TYPE_JSON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription( )
    {
        return _strDescription;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isZippable( )
    {
        return _bZip;
    }
}
