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
package fr.paris.lutece.plugins.suggest.service.search;

import fr.paris.lutece.plugins.suggest.business.CommentSubmit;
import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestFilter;
import fr.paris.lutece.plugins.suggest.business.SuggestHome;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.suggest.business.SubmitFilter;
import fr.paris.lutece.plugins.suggest.service.SuggestSubmitService;
import fr.paris.lutece.plugins.suggest.service.SuggestPlugin;
import fr.paris.lutece.plugins.suggest.service.suggestsearch.SuggestSearchItem;
import fr.paris.lutece.plugins.suggest.web.SuggestApp;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.SearchIndexer;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.url.UrlItem;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * SuggestIndexer
 * 
 */
public class SuggestIndexer implements SearchIndexer
{
    public static final String INDEX_TYPE_SUGGEST = "suggest";
    public static final String PROPERTY_INDEXER_NAME = "suggest.indexer.name";
    public static final String SHORT_NAME = "dgl";
    private static final String ENABLE_VALUE_TRUE = "1";
    private static final String PROPERTY_INDEXER_DESCRIPTION = "suggest.indexer.description";
    private static final String PROPERTY_INDEXER_VERSION = "suggest.indexer.version";
    private static final String PROPERTY_INDEXER_ENABLE = "suggest.indexer.enable";
    private static final String PROPERTY_XPAGE_APPLICATION_ID = "suggest.xpage.applicationId";
    private static final String JSP_SEARCH_SUGGEST = "jsp/site/Portal.jsp?page=suggest";

    // request parameters
    private static final String PARAMETER_ID_SUGGEST = "id_suggest";
    private static final String PARAMETER_ID_SUGGEST_SUBMIT = "id_suggest_submit";
    private static final String PARAMETER_ACTION = "action";
    private static final String PARAMETER_SUGGEST_VIEW_DETAIL = "view_suggest_submit";

    /**
     * Returns the indexer service description
     * 
     * @return The indexer service description
     */
    public String getDescription( )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_DESCRIPTION );
    }

    /**
     * Index suggest documents
     * 
     * @throws IOException
     *             Exception
     * @throws InterruptedException
     *             Exception
     * @throws SiteMessageException
     *             Exception
     */
    public void indexDocuments( ) throws IOException, InterruptedException, SiteMessageException
    {
        Plugin plugin = PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME );

        // filter on suggest state(the suggest submit are add if the suggest is activated)
        SuggestFilter suggestFilter = new SuggestFilter( );
        suggestFilter.setIdState( Suggest.STATE_ENABLE );

        List<Suggest> suggestActivatedList = SuggestHome.getSuggestList( suggestFilter, plugin );
        List<Integer> suggestSubmitActivatedList;

        SubmitFilter submitFilter = new SubmitFilter( );
        submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_SCORE_DESC );
        submitFilter.getSortBy( ).add( SubmitFilter.SORT_BY_DATE_RESPONSE_DESC );

        for ( Suggest suggest : suggestActivatedList )
        {
            submitFilter.setIdSuggest( suggest.getIdSuggest( ) );
            suggestSubmitActivatedList = SuggestSubmitService.getService( ).getSuggestSubmitListId( submitFilter, plugin );

            for ( Integer idSuggestSubmit : suggestSubmitActivatedList )
            {
                List<Document> listDocSuggestSubmit = null;

                try
                {
                    listDocSuggestSubmit = getDocuments( idSuggestSubmit.toString( ) );
                }
                catch( Exception e )
                {
                    String strMessage = "Suggest ID : " + suggest.getIdSuggest( );
                    IndexationService.error( this, e, strMessage );
                }

                if ( ( listDocSuggestSubmit != null ) && ( !listDocSuggestSubmit.isEmpty( ) ) )
                {
                    for ( Document docSuggestSubmit : listDocSuggestSubmit )
                    {
                        IndexationService.write( docSuggestSubmit );
                    }
                }
            }
        }
    }

    /**
     * Return Lucene documents
     * 
     * @param strIdSuggest
     *            the if of the suggest
     * @return a list of Documents
     * @throws IOException
     *             Exception
     * @throws InterruptedException
     *             Exception
     * @throws SiteMessageException
     *             Exception
     */
    public List<Document> getDocuments( String strIdSuggestSubmit ) throws IOException, InterruptedException, SiteMessageException
    {
        List<org.apache.lucene.document.Document> listDocs = new ArrayList<>( );
        String strPortalUrl = AppPathService.getPortalUrl( );
        Integer nIdSuggestSubmit = Integer.parseInt( strIdSuggestSubmit );
        Plugin plugin = PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME );
        SuggestSubmit suggestSubmit = SuggestSubmitService.getService( ).findByPrimaryKey( nIdSuggestSubmit, true, plugin );

        if ( suggestSubmit != null )
        {
            // Add comment
            UrlItem url = new UrlItem( strPortalUrl );
            url.addParameter( XPageAppService.PARAM_XPAGE_APP, AppPropertiesService.getProperty( PROPERTY_XPAGE_APPLICATION_ID, INDEX_TYPE_SUGGEST ) );
            url.addParameter( PARAMETER_ID_SUGGEST, suggestSubmit.getSuggest( ).getIdSuggest( ) );
            url.addParameter( PARAMETER_ID_SUGGEST_SUBMIT, suggestSubmit.getIdSuggestSubmit( ) );
            url.addParameter( PARAMETER_ACTION, PARAMETER_SUGGEST_VIEW_DETAIL );
            url.addParameter( SuggestApp.PARAMETER_SUGGEST_DETAIL, 1 );

            org.apache.lucene.document.Document docSuggestSubmit = getDocument( suggestSubmit, url.getUrl( ) );
            listDocs.add( docSuggestSubmit );
        }

        return listDocs;
    }

    /**
     * Builds a document which will be used by Lucene during the indexing of the suggest submit list
     * 
     * @param suggestSubmit
     *            the suggest submit to index
     * @param strUrl
     *            the url of suggest submit
     * @return a lucene document
     * 
     * @throws IOException
     *             The IO Exception
     * @throws InterruptedException
     *             The InterruptedException
     */
    public static org.apache.lucene.document.Document getDocument( SuggestSubmit suggestSubmit, String strUrl ) throws IOException
    {
        // make a new, empty document
        org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document( );

        FieldType ft = new FieldType( StringField.TYPE_STORED );
        ft.setOmitNorms( false );

        FieldType ftNo = new FieldType( StringField.TYPE_STORED );
        ftNo.setIndexOptions( IndexOptions.NONE );
        ftNo.setTokenized( false );
        ftNo.setOmitNorms( false );

        // Add the url as a field named "url". Use an UnIndexed field, so
        // that the url is just stored with the question/answer, but is not searchable.
        doc.add( new Field( SearchItem.FIELD_URL, strUrl, ft ) );

        doc.add( new Field( SuggestSearchItem.FIELD_ID_SUGGEST, String.valueOf( suggestSubmit.getSuggest( ).getIdSuggest( ) ), ft ) );
        doc.add( new Field( SuggestSearchItem.FIELD_ID_SUGGEST_SUBMIT, Integer.toString( suggestSubmit.getIdSuggestSubmit( ) ), ft ) );

        // Add the uid as a field, so that index can be incrementally maintained.
        // This field is not stored with question/answer, it is indexed, but it is not
        // tokenized prior to indexing.
        doc.add( new Field( SearchItem.FIELD_UID, String.valueOf( suggestSubmit.getIdSuggestSubmit( ) ) + "_" + SHORT_NAME, ft ) );
        // Add state
        doc.add( new Field( SearchItem.FIELD_STATE, Integer.toString( suggestSubmit.getSuggestSubmitState( ).getIdSuggestSubmitState( ) ), ft ) );

        StringWriter writerFieldContent = new StringWriter( );
        writerFieldContent.write( suggestSubmit.getSuggestSubmitValue( ) );

        // Add the list of comments
        if ( suggestSubmit.getComments( ) != null )
        {
            for ( CommentSubmit comment : suggestSubmit.getComments( ) )
            {
                writerFieldContent.write( comment.getValue( ) );
            }
        }

        ContentHandler handler = new BodyContentHandler( );
        Metadata metadata = new Metadata( );
        try
        {
            new HtmlParser( ).parse( new ByteArrayInputStream( writerFieldContent.toString( ).getBytes( ) ), handler, metadata, new ParseContext( ) );
        }
        catch( SAXException | TikaException e )
        {
            throw new AppException( "Error during page parsing : " + e.getMessage( ), e );
        }

        // the content of the article is recovered in the parser because this one
        // had replaced the encoded caracters (as &eacute;) by the corresponding special caracter (as ?)
        StringBuilder sb = new StringBuilder( handler.toString( ) );
        // Add the tag-stripped contents as a Reader-valued Text field so it will
        // get tokenized and indexed.
        doc.add( new Field( SearchItem.FIELD_CONTENTS, sb.toString( ), TextField.TYPE_NOT_STORED ) );

        // Add the title as a separate Text field, so that it can be searched
        // separately.
        doc.add( new Field( SearchItem.FIELD_TITLE, suggestSubmit.getSuggestSubmitTitle( ), ftNo ) );

        // Add the summary as an UnIndexed field, so that it is stored and returned
        // with hit documents for display.
        doc.add( new Field( SearchItem.FIELD_TYPE, INDEX_TYPE_SUGGEST, ft ) );

        Plugin plugin = PluginService.getPlugin( SuggestPlugin.PLUGIN_NAME );
        Suggest suggest = SuggestHome.findByPrimaryKey( suggestSubmit.getSuggest( ).getIdSuggest( ), plugin );

        if ( suggest.getRole( ) != null )
        {
            doc.add( new Field( SearchItem.FIELD_ROLE, suggest.getRole( ), ft ) );
        }

        // return the document
        return doc;
    }

    /**
     * Get the name of the indexer.
     * 
     * @return The name
     */
    public String getName( )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_NAME );
    }

    /**
     * Get the version of the indexer
     * 
     * @return The version number
     */
    public String getVersion( )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_VERSION );
    }

    /**
     * Get the state of indexer
     * 
     * @return Return true if the indexer is enabled, false else.
     */
    public boolean isEnable( )
    {
        boolean bReturn = false;
        String strEnable = AppPropertiesService.getProperty( PROPERTY_INDEXER_ENABLE );

        if ( ( strEnable != null ) && ( strEnable.equalsIgnoreCase( Boolean.TRUE.toString( ) ) || strEnable.equals( ENABLE_VALUE_TRUE ) )
                && PluginService.isPluginEnable( SuggestPlugin.PLUGIN_NAME ) )
        {
            bReturn = true;
        }

        return bReturn;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getListType( )
    {
        List<String> listType = new ArrayList<>( );
        listType.add( INDEX_TYPE_SUGGEST );

        return listType;
    }

    /**
     * {@inheritDoc}
     */
    public String getSpecificSearchAppUrl( )
    {
        return JSP_SEARCH_SUGGEST;
    }
}
