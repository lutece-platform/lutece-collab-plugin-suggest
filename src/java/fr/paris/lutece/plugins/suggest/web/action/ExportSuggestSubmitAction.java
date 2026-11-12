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
import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestHome;
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

import fr.paris.lutece.plugins.filegenerator.service.TemporaryFileGeneratorService;
import fr.paris.lutece.plugins.suggest.service.export.SuggestExportFileGenerator;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

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

    @Inject
    private TemporaryFileGeneratorService _temporaryFileGeneratorService;

    private static final String ACTION_NAME = "Export Suggest JSON";
    private static final String MESSAGE_YOU_ARE_NOT_ALLOWED_TO_DOWLOAD_THIS_FILE = "suggest.message.youAreNotAllowedToDownloadFile";
    private static final String MESSAGE_ERROR_DURING_DOWNLOAD_FILE = "suggest.message.errorDuringDownloadFile";
    private static final String PARAMETER_BUTTON_EXPORT_ALL = "export_search_all";
    private static final String PARAMETER_BUTTON_EXPORT_SEARCH = "export_search_result";
    private static final String PARAMETER_ID_SUGGEST = "id_suggest";
    private static final String MESSAGE_EXPORT_DESCRIPTION = "suggest.export.description";
    private static final String PROPERTY_ASYNCHRONOUS_THRESHOLD = "suggest.export.asynchronous.threshold";
    private static final int DEFAULT_ASYNCHRONOUS_THRESHOLD = 1000;
    private static final String JSP_MANAGE_SUGGEST_SUBMIT = "jsp/admin/plugins/suggest/ManageSuggest.jsp?view=manageSuggestSubmit&export=started&id_suggest=";
    private static final String EXPORT_EXTENSION = ".json";

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

        List<Integer> listIdSuggestSubmit = _suggestSubmitService.getSuggestSubmitListId( SuggestUtils.getSuggestSubmitFilter( searchFields ), plugin );
        boolean bAsynchronous = listIdSuggestSubmit.size( ) > AppPropertiesService.getPropertyInt( PROPERTY_ASYNCHRONOUS_THRESHOLD, DEFAULT_ASYNCHRONOUS_THRESHOLD );
        String strDescription = I18nService.getLocalizedString( MESSAGE_EXPORT_DESCRIPTION, new Object [ ] {
                suggest.getTitle( ), listIdSuggestSubmit.size( )
        }, adminUser.getLocale( ) );
        SuggestExportFileGenerator generator = new SuggestExportFileGenerator( suggest, listIdSuggestSubmit, strDescription, bAsynchronous, plugin,
                _suggestSubmitService, _commentSubmitService, _suggestJsonExportService );

        if ( bAsynchronous )
        {
            _temporaryFileGeneratorService.generateFile( generator, adminUser );
            result.setRedirect( AppPathService.getBaseUrl( request ) + JSP_MANAGE_SUGGEST_SUBMIT + suggest.getIdSuggest( ) );

            return result;
        }

        try
        {
            byte [ ] byteFileOutPut = generator.generateJson( ).getBytes( StandardCharsets.UTF_8 );

            SuggestUtils.addHeaderResponse( request, response, suggest.getTitle( ) + EXPORT_EXTENSION );
            response.setContentType( SuggestExportFileGenerator.MIME_TYPE_JSON );
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
