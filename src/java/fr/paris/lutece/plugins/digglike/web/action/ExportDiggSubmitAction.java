/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.digglike.web.action;

import fr.paris.lutece.plugins.digglike.business.CommentSubmit;
import fr.paris.lutece.plugins.digglike.business.Digg;
import fr.paris.lutece.plugins.digglike.business.DiggHome;
import fr.paris.lutece.plugins.digglike.business.DiggSubmit;
import fr.paris.lutece.plugins.digglike.business.EntryFilter;
import fr.paris.lutece.plugins.digglike.business.EntryHome;
import fr.paris.lutece.plugins.digglike.business.ExportFormat;
import fr.paris.lutece.plugins.digglike.business.ExportFormatHome;
import fr.paris.lutece.plugins.digglike.business.SubmitFilter;
import fr.paris.lutece.plugins.digglike.service.CommentSubmitService;
import fr.paris.lutece.plugins.digglike.service.DiggSubmitService;
import fr.paris.lutece.plugins.digglike.service.DigglikePlugin;
import fr.paris.lutece.plugins.digglike.service.DigglikeResourceIdService;
import fr.paris.lutece.plugins.digglike.utils.DiggUtils;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.html.XmlTransformerService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.pluginaction.AbstractPluginAction;
import fr.paris.lutece.portal.web.pluginaction.DefaultPluginActionResult;
import fr.paris.lutece.portal.web.pluginaction.IPluginActionResult;
import fr.paris.lutece.util.UniqueIDGenerator;
import fr.paris.lutece.util.filesystem.FileSystemUtil;
import fr.paris.lutece.util.xml.XmlUtil;

import java.io.IOException;
import java.io.OutputStream;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * Exports records (search records or all records)
 *
 */
public class ExportDiggSubmitAction extends AbstractPluginAction<DigglikeAdminSearchFields> implements IDigglikeAction
{
    private static final String ACTION_NAME = "Export Digglike XSL";
    private static final String MESSAGE_YOU_ARE_NOT_ALLOWED_TO_DOWLOAD_THIS_FILE = "digglike.message.youAreNotAllowedToDownloadFile";
    private static final String MESSAGE_YOU_MUST_SELECT_EXPORT_FORMAT = "digglike.message.youMustSelectExportFormat";
    private static final String MESSAGE_ERROR_DURING_DOWNLOAD_FILE = "digglike.message.errorDuringDownloadFile";
    private static final String MARK_EXPORT_FORMAT_REF_LIST = "export_format_list";
    private static final String PARAMETER_BUTTON_EXPORT_ALL = "export_search_all";
    private static final String PARAMETER_BUTTON_EXPORT_SEARCH = "export_search_result";
    private static final String PARAMETER_ID_DIGG = "id_digg";
    private static final String XSL_UNIQUE_PREFIX_ID = UniqueIDGenerator.getNewId(  ) + "digg-";
    private static final String PARAMETER_ID_EXPORT_FORMAT = "id_export_format";
    private static final String EXPORT_CSV_EXT = "csv";
    private static final String DEAFULT_ENCODING = "UTF-8";
    private static final String CONSTANT_MIME_TYPE_CSV = "application/csv";
    private static final String CONSTANT_MIME_TYPE_OCTETSTREAM = "application/octet-stream";

    //PROPERTY
    private static final String PROPERTY_EXPORT_ENCODING_CSV = "digglike.exportFileEncoding.csv";
    private static final String PROPERTY_EXPORT_ENCODING_XML = "digglike.exportFileEncoding.xml";

    /**
     * {@inheritDoc}
     */
    @Override
    public void fillModel( HttpServletRequest request, AdminUser adminUser, Map<String, Object> model )
    {
        model.put( MARK_EXPORT_FORMAT_REF_LIST, ExportFormatHome.getListExport( getPlugin(  ) ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName(  )
    {
        return ACTION_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getButtonTemplate(  )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInvoked( HttpServletRequest request )
    {
        return ( request.getParameter( PARAMETER_BUTTON_EXPORT_SEARCH ) != null ) ||
        ( request.getParameter( PARAMETER_BUTTON_EXPORT_ALL ) != null );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPluginActionResult process( HttpServletRequest request, HttpServletResponse response, AdminUser adminUser,
        DigglikeAdminSearchFields searchFields ) throws AccessDeniedException
    {
        Plugin plugin = getPlugin(  );
        IPluginActionResult result = new DefaultPluginActionResult(  );

        String strIdExportFormat = request.getParameter( PARAMETER_ID_EXPORT_FORMAT );
        String strIdDigg = request.getParameter( PARAMETER_ID_DIGG );
        int nIdExportFormat = DiggUtils.getIntegerParameter( strIdExportFormat );
        int nIdDigg = DiggUtils.getIntegerParameter( strIdDigg );

        Digg digg = DiggHome.findByPrimaryKey( nIdDigg, plugin );

        if ( ( digg == null ) ||
                !RBACService.isAuthorized( Digg.RESOURCE_TYPE, Integer.toString( digg.getIdDigg(  ) ),
                    DigglikeResourceIdService.PERMISSION_MANAGE_DIGG_SUBMIT, adminUser ) )
        {
            throw new AccessDeniedException( I18nService.getLocalizedString( 
                    MESSAGE_YOU_ARE_NOT_ALLOWED_TO_DOWLOAD_THIS_FILE, request.getLocale(  ) ) );
        }

        ExportFormat exportFormat;
        exportFormat = ExportFormatHome.findByPrimaryKey( nIdExportFormat, plugin );

        if ( exportFormat == null )
        {
            result.setRedirect( AdminMessageService.getMessageUrl( request, MESSAGE_YOU_MUST_SELECT_EXPORT_FORMAT,
                    AdminMessage.TYPE_STOP ) );
        }
        else
        {
            EntryFilter entryfilter = new EntryFilter(  );
            entryfilter.setIdDigg( digg.getIdDigg(  ) );
            //set digg entries
            digg.setEntries( EntryHome.getEntryList( entryfilter, plugin ) );

            SubmitFilter filter = DiggUtils.getDiggSubmitFilter( searchFields );
            List<Integer> listIdDiggSubmit = DiggSubmitService.getService(  ).getDiggSubmitListId( filter, plugin );

            StringBuffer strBufferListDiggSubmitXml = new StringBuffer(  );
            DiggSubmit diggSubmit = null;
            List<CommentSubmit> listCommentSubmit;
            //reinit filter for comment
            filter = new SubmitFilter(  );

            for ( Integer nIdDiggSubmit : listIdDiggSubmit )
            {
                diggSubmit = DiggSubmitService.getService(  ).findByPrimaryKey( nIdDiggSubmit, false, true, plugin );
                filter.setIdDiggSubmit( nIdDiggSubmit );
                listCommentSubmit = CommentSubmitService.getService(  ).getCommentSubmitList( filter, plugin );
                diggSubmit.setComments( listCommentSubmit );
                diggSubmit.setDigg( digg );
                strBufferListDiggSubmitXml.append( diggSubmit.getXml( request, adminUser.getLocale(  ) ) );
            }

            String strXmlSource = XmlUtil.getXmlHeader(  ) +
                digg.getXml( request, strBufferListDiggSubmitXml, adminUser.getLocale(  ) );
            XmlTransformerService xmlTransformerService = new XmlTransformerService(  );

            String strFileOutPut = xmlTransformerService.transformBySourceWithXslCache( strXmlSource,
                    exportFormat.getXsl(  ), XSL_UNIQUE_PREFIX_ID + nIdExportFormat, null, null );

            String strFormatExtension = exportFormat.getExtension(  ).trim(  );
            String strFileName = digg.getTitle(  ) + "." + strFormatExtension;
            boolean isExporTypeCSV = ( ( strFormatExtension != null ) && strFormatExtension.equals( EXPORT_CSV_EXT ) );

            String strEncoding = isExporTypeCSV
                ? AppPropertiesService.getProperty( PROPERTY_EXPORT_ENCODING_CSV, DEAFULT_ENCODING )
                : AppPropertiesService.getProperty( PROPERTY_EXPORT_ENCODING_XML, DEAFULT_ENCODING );
            String strResponseContentType = null;

            if ( isExporTypeCSV )
            {
                strResponseContentType = CONSTANT_MIME_TYPE_CSV;
            }
            else
            {
                String strMimeType = FileSystemUtil.getMIMEType( strFileName );
                strResponseContentType = ( strMimeType != null ) ? strMimeType : CONSTANT_MIME_TYPE_OCTETSTREAM;
            }

            DiggUtils.addHeaderResponse( request, response, strFileName );
            response.setContentType( strResponseContentType );
            response.setCharacterEncoding( strEncoding );

            try
            {
                byte[] byteFileOutPut = strFileOutPut.getBytes( strEncoding );
                response.setContentLength( (int) byteFileOutPut.length );

                OutputStream os = response.getOutputStream(  );
                os.write( byteFileOutPut );
                os.close(  );
            }
            catch ( IOException e )
            {
                AppLogService.error( e );

                result.setRedirect( AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_DURING_DOWNLOAD_FILE,
                        AdminMessage.TYPE_STOP ) );
            }
        }

        result.setNoop( true );

        return result;
    }

    /**
     * Gets the plugin
     * @return the plugin
     */
    private Plugin getPlugin(  )
    {
        return PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME );
    }
}
