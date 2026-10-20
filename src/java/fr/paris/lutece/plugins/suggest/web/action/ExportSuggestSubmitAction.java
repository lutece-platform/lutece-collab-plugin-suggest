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
package fr.paris.lutece.plugins.suggest.web.action;

import fr.paris.lutece.api.user.User;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.enterprise.context.ApplicationScoped;
import fr.paris.lutece.plugins.suggest.business.CommentSubmit;
import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestHome;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.suggest.business.EntryFilter;
import fr.paris.lutece.plugins.suggest.business.EntryHome;
import fr.paris.lutece.plugins.suggest.business.SubmitFilter;
import fr.paris.lutece.plugins.suggest.service.ICommentSubmitService;
import fr.paris.lutece.plugins.suggest.service.ISuggestSubmitService;
import fr.paris.lutece.plugins.suggest.service.SuggestJsonExportService;
import fr.paris.lutece.plugins.suggest.service.SuggestPlugin;
import fr.paris.lutece.plugins.suggest.service.SuggestResourceIdService;
import fr.paris.lutece.plugins.suggest.service.SuggestService;
import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.pluginaction.AbstractPluginAction;
import fr.paris.lutece.portal.web.pluginaction.DefaultPluginActionResult;
import fr.paris.lutece.portal.web.pluginaction.IPluginActionResult;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * Exports the suggest submits of a suggest (search result or all) as JSON
 *
 */
@ApplicationScoped
@Named( "suggest.exportSuggestSubmitAction" )
public class ExportSuggestSubmitAction extends AbstractPluginAction<SuggestAdminSearchFields> implements ISuggestAction
{
    @Inject
    private ICommentSubmitService _commentSubmitService;

    @Inject
    private ISuggestSubmitService _suggestSubmitService;

    @Inject
    private SuggestJsonExportService _suggestJsonExportService;

    private static final String ACTION_NAME = "Export Suggest JSON";
    private static final String MESSAGE_YOU_ARE_NOT_ALLOWED_TO_DOWLOAD_THIS_FILE = "suggest.message.youAreNotAllowedToDownloadFile";
    private static final String MESSAGE_ERROR_DURING_DOWNLOAD_FILE = "suggest.message.errorDuringDownloadFile";
    private static final String PARAMETER_BUTTON_EXPORT_ALL = "export_search_all";
    private static final String PARAMETER_BUTTON_EXPORT_SEARCH = "export_search_result";
    private static final String PARAMETER_ID_SUGGEST = "id_suggest";
    private static final String EXPORT_EXTENSION = ".json";
    private static final String CONSTANT_MIME_TYPE_JSON = "application/json";

    /**
     * {@inheritDoc}
     */
    @Override
    public void fillModel( HttpServletRequest request, AdminUser adminUser, Map<String, Object> model )
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName( )
    {
        return ACTION_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getButtonTemplate( )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInvoked( HttpServletRequest request )
    {
        return ( request.getParameter( PARAMETER_BUTTON_EXPORT_SEARCH ) != null ) || ( request.getParameter( PARAMETER_BUTTON_EXPORT_ALL ) != null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPluginActionResult process( HttpServletRequest request, HttpServletResponse response, AdminUser adminUser, SuggestAdminSearchFields searchFields )
            throws AccessDeniedException
    {
        Plugin plugin = getPlugin( );
        IPluginActionResult result = new DefaultPluginActionResult( );

        int nIdSuggest = SuggestUtils.getIntegerParameter( request.getParameter( PARAMETER_ID_SUGGEST ) );
        Suggest suggest = SuggestHome.findByPrimaryKey( nIdSuggest, plugin );

        if ( ( suggest == null )
                || !SuggestService.isAuthorized( suggest.getIdSuggest( ), SuggestResourceIdService.PERMISSION_MANAGE_SUGGEST_SUBMIT, (User) adminUser ) )
        {
            throw new AccessDeniedException( I18nService.getLocalizedString( MESSAGE_YOU_ARE_NOT_ALLOWED_TO_DOWLOAD_THIS_FILE, request.getLocale( ) ) );
        }

        EntryFilter entryfilter = new EntryFilter( );
        entryfilter.setIdSuggest( suggest.getIdSuggest( ) );
        suggest.setEntries( EntryHome.getEntryList( entryfilter, plugin ) );

        List<Integer> listIdSuggestSubmit = _suggestSubmitService.getSuggestSubmitListId( SuggestUtils.getSuggestSubmitFilter( searchFields ), plugin );
        List<SuggestSubmit> listSuggestSubmit = new ArrayList<>( listIdSuggestSubmit.size( ) );
        SubmitFilter commentFilter = new SubmitFilter( );

        for ( Integer nIdSuggestSubmit : listIdSuggestSubmit )
        {
            SuggestSubmit suggestSubmit = _suggestSubmitService.findByPrimaryKey( nIdSuggestSubmit, false, true, plugin );
            commentFilter.setIdSuggestSubmit( nIdSuggestSubmit );
            List<CommentSubmit> listCommentSubmit = _commentSubmitService.getCommentSubmitList( commentFilter, plugin );
            suggestSubmit.setComments( listCommentSubmit );
            suggestSubmit.setSuggest( suggest );
            listSuggestSubmit.add( suggestSubmit );
        }

        try
        {
            byte [ ] byteFileOutPut = _suggestJsonExportService.export( suggest, listSuggestSubmit ).getBytes( StandardCharsets.UTF_8 );

            SuggestUtils.addHeaderResponse( request, response, suggest.getTitle( ) + EXPORT_EXTENSION );
            response.setContentType( CONSTANT_MIME_TYPE_JSON );
            response.setCharacterEncoding( StandardCharsets.UTF_8.name( ) );
            response.setContentLength( byteFileOutPut.length );

            try ( OutputStream os = response.getOutputStream( ) )
            {
                os.write( byteFileOutPut );
            }
        }
        catch( IOException e )
        {
            AppLogService.error( e.getMessage( ), e );
            result.setRedirect( AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_DURING_DOWNLOAD_FILE, AdminMessage.TYPE_STOP ) );
        }

        result.setNoop( true );

        return result;
    }

    /**
     * Gets the plugin
     * 
     * @return the plugin
     */
    private Plugin getPlugin( )
    {
        return PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME );
    }
}
