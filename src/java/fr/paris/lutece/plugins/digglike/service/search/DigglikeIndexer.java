/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.digglike.service.search;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.demo.html.HTMLParser;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import fr.paris.lutece.plugins.digglike.business.Digg;
import fr.paris.lutece.plugins.digglike.business.DiggFilter;
import fr.paris.lutece.plugins.digglike.business.DiggHome;
import fr.paris.lutece.plugins.digglike.business.DiggSubmit;
import fr.paris.lutece.plugins.digglike.business.DiggSubmitHome;
import fr.paris.lutece.plugins.digglike.business.SubmitFilter;
import fr.paris.lutece.plugins.digglike.service.DigglikePlugin;
import fr.paris.lutece.plugins.digglike.service.digglikesearch.DigglikeSearchItem;
import fr.paris.lutece.plugins.digglike.web.DiggApp;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.search.IndexationService;
import fr.paris.lutece.portal.service.search.SearchIndexer;
import fr.paris.lutece.portal.service.search.SearchItem;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.url.UrlItem;


/**
 * DiggLikeIndexer
 *
 */
public class DigglikeIndexer implements SearchIndexer
{
    public static final String INDEX_TYPE_DIGG = "digg";
    public static final String PROPERTY_INDEXER_NAME = "digglike.indexer.name";
    public static final String SHORT_NAME = "dgl";
    private static final String ENABLE_VALUE_TRUE = "1";
    private static final String PROPERTY_INDEXER_DESCRIPTION = "digglike.indexer.description";
    private static final String PROPERTY_INDEXER_VERSION = "digglike.indexer.version";
    private static final String PROPERTY_INDEXER_ENABLE = "digglike.indexer.enable";
    private static final String PROPERTY_XPAGE_APPLICATION_ID = "digglike.xpage.applicationId";
    private static final String JSP_SEARCH_DIGG = "jsp/site/Portal.jsp?page=digg";

    // request parameters
    private static final String PARAMETER_ID_DIGG = "id_digg";
    private static final String PARAMETER_ID_DIGG_SUBMIT = "id_digg_submit";

    /**
     * Returns the indexer service description
     * @return The indexer service description
     */
    public String getDescription(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_DESCRIPTION );
    }

    /**
    * Index digglike documents
    * @throws IOException Exception
    * @throws InterruptedException Exception
    * @throws SiteMessageException Exception
    */
    public void indexDocuments(  ) throws IOException, InterruptedException, SiteMessageException
    {
        String strPortalUrl = AppPathService.getPortalUrl(  );

        Plugin plugin = PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME );

        //filter on digg state(the digg submit are add if the digg is activated)
        DiggFilter diggFilter = new DiggFilter(  );
        diggFilter.setIdState( Digg.STATE_ENABLE );

        List<Digg> diggActivatedList = DiggHome.getDiggList( diggFilter, plugin );
        List<DiggSubmit> diggSubmitActivatedList;

        SubmitFilter submitFilter = new SubmitFilter(  );
        //        submitFilter.setIdDiggSubmitState( DiggSubmit.STATE_PUBLISH );
        submitFilter.getSortBy(  ).add( SubmitFilter.SORT_BY_SCORE_DESC );
        submitFilter.getSortBy(  ).add( SubmitFilter.SORT_BY_DATE_RESPONSE_DESC );

        for ( Digg digg : diggActivatedList )
        {
            submitFilter.setIdDigg( digg.getIdDigg(  ) );
            diggSubmitActivatedList = DiggSubmitHome.getDiggSubmitList( submitFilter, plugin );

            for ( DiggSubmit diggSubmit : diggSubmitActivatedList )
            {
                diggSubmit.setDigg( digg );

                UrlItem url = new UrlItem( strPortalUrl );
                url.addParameter( XPageAppService.PARAM_XPAGE_APP,
                    AppPropertiesService.getProperty( PROPERTY_XPAGE_APPLICATION_ID, "digg" ) );
                url.addParameter( PARAMETER_ID_DIGG, digg.getIdDigg(  ) );
                url.addParameter( PARAMETER_ID_DIGG_SUBMIT, diggSubmit.getIdDiggSubmit(  ) );
                url.addParameter( DiggApp.PARAMETER_DIGG_DETAIL, 1 );

                //url.addParameter( DiggApp.PARAMETER_CLEAR_FILTER,DiggApp.PARAMETER_CLEAR_FILTER);
                //url.setAnchor(DiggApp.ANCHOR_DIGG_SUBMIT+diggSubmit.getIdDiggSubmit());
                org.apache.lucene.document.Document docDiggSubmit = null;
                try
                {
                	docDiggSubmit = getDocument( diggSubmit, url.getUrl(  ) );
                }
                catch ( Exception e )
                {
                	String strMessage = "Digg ID : " + digg.getIdDigg(  );
                	IndexationService.error( this, e, strMessage );
                }
                if ( docDiggSubmit != null )
                {
                	IndexationService.write( docDiggSubmit );
                }
            }
        }
    }

    /**
     * Return Lucene documents
     * @param strIdDigg the if of the digg
     * @return  a list of Documents
     * @throws IOException Exception
     * @throws InterruptedException Exception
     * @throws SiteMessageException Exception
     */
    public List<Document> getDocuments( String strIdDigg )
        throws IOException, InterruptedException, SiteMessageException
    {
        List<org.apache.lucene.document.Document> listDocs = new ArrayList<org.apache.lucene.document.Document>(  );
        String strPortalUrl = AppPathService.getPortalUrl(  );

        Plugin plugin = PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME );

        //filter on digg state(the digg submit are add if the digg is activated)
        /*DiggFilter diggFilter = new DiggFilter(  );
        diggFilter.setIdState( Digg.STATE_ENABLE );
        
        List<Digg> diggActivatedList = DiggHome.getDiggList( diggFilter, plugin );*/

        /*Digg digg = DiggHome.findByPrimaryKey(nIdDigg, plugin);
        List<DiggSubmit> diggSubmitActivatedList;
        
        SubmitFilter submitFilter = new SubmitFilter(  );
        submitFilter.setIdDiggSubmitState( DiggSubmit.STATE_PUBLISH );
        
        submitFilter.getSortBy(  ).add( SubmitFilter.SORT_BY_SCORE_DESC );
        submitFilter.getSortBy(  ).add( SubmitFilter.SORT_BY_DATE_RESPONSE_DESC );
        submitFilter.setIdDigg( digg.getIdDigg(  ) );
        diggSubmitActivatedList = DiggSubmitHome.getDiggSubmitList( submitFilter, plugin );
        
        for ( DiggSubmit diggSubmit : diggSubmitActivatedList )
        {
            diggSubmit.setDigg( digg );*/
        DiggSubmit diggSubmit = DiggSubmitHome.findByPrimaryKey( Integer.parseInt( strIdDigg ), plugin );

        if ( diggSubmit != null )
        {
            UrlItem url = new UrlItem( strPortalUrl );
            url.addParameter( XPageAppService.PARAM_XPAGE_APP,
                AppPropertiesService.getProperty( PROPERTY_XPAGE_APPLICATION_ID, "digg" ) );
            url.addParameter( PARAMETER_ID_DIGG, diggSubmit.getDigg(  ).getIdDigg(  ) );
            url.addParameter( PARAMETER_ID_DIGG_SUBMIT, diggSubmit.getIdDiggSubmit(  ) );
            url.addParameter( DiggApp.PARAMETER_DIGG_DETAIL, 1 );

            //url.addParameter( DiggApp.PARAMETER_CLEAR_FILTER,DiggApp.PARAMETER_CLEAR_FILTER);
            //url.setAnchor(DiggApp.ANCHOR_DIGG_SUBMIT+diggSubmit.getIdDiggSubmit());
            org.apache.lucene.document.Document docDiggSubmit = getDocument( diggSubmit, url.getUrl(  ) );
            listDocs.add( docDiggSubmit );
        }

        //}
        return listDocs;
    }

    /**
     * Builds a document which will be used by Lucene during the indexing of the digg submit list
     *
     * @param diggSubmit the digg submit to index
     * @param strUrl the url of digg submit
     * @return a lucene document
    
     * @throws IOException The IO Exception
     * @throws InterruptedException The InterruptedException
     */
    public static org.apache.lucene.document.Document getDocument( DiggSubmit diggSubmit, String strUrl )
        throws IOException, InterruptedException
    {
        // make a new, empty document
        org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document(  );

        // Add the url as a field named "url".  Use an UnIndexed field, so
        // that the url is just stored with the question/answer, but is not searchable.
        doc.add( new Field( SearchItem.FIELD_URL, strUrl, Field.Store.YES, Field.Index.NOT_ANALYZED ) );
        doc.add( new Field( DigglikeSearchItem.FIELD_ID_DIGG, String.valueOf( diggSubmit.getDigg(  ).getIdDigg(  ) ),
                Field.Store.YES, Field.Index.NOT_ANALYZED ) );
        doc.add( new Field( DigglikeSearchItem.FIELD_ID_DIGG_SUBMIT,
                Integer.toString( diggSubmit.getIdDiggSubmit(  ) ), Field.Store.YES, Field.Index.NOT_ANALYZED ) );

        // Add the uid as a field, so that index can be incrementally maintained.
        // This field is not stored with question/answer, it is indexed, but it is not
        // tokenized prior to indexing.
        doc.add( new Field( DigglikeSearchItem.FIELD_UID,
                String.valueOf( diggSubmit.getIdDiggSubmit(  ) ) + "_" + SHORT_NAME, Field.Store.YES,
                Field.Index.NOT_ANALYZED ) );

        StringReader readerDiggSubmit = new StringReader( diggSubmit.getDiggSubmitValue(  ) );
        HTMLParser parser = new HTMLParser( readerDiggSubmit );

        //the content of the question/answer is recovered in the parser because this one
        //had replaced the encoded caracters (as &eacute;) by the corresponding special caracter (as ?)
        Reader reader = parser.getReader(  );
        int c;
        StringBuffer sb = new StringBuffer(  );

        while ( ( c = reader.read(  ) ) != -1 )
        {
            sb.append( String.valueOf( (char) c ) );
        }

        reader.close(  );

        // Add the tag-stripped contents as a Reader-valued Text field so it will
        // get tokenized and indexed.
        doc.add( new Field( DigglikeSearchItem.FIELD_CONTENTS, sb.toString(  ), Field.Store.NO, Field.Index.ANALYZED ) );

        // Add the title as a separate Text field, so that it can be searched
        // separately.
        doc.add( new Field( DigglikeSearchItem.FIELD_TITLE, diggSubmit.getDiggSubmitTitle(  ), Field.Store.YES,
                Field.Index.NO ) );

        // Add the summary as an UnIndexed field, so that it is stored and returned
        // with hit documents for display.
        // doc.add( new Field( SearchItem.FIELD_SUMMARY, diggSubmit.getDiggSubmitValueShowInTheList(), Field.Store.YES, Field.Index.NO ) );
        doc.add( new Field( DigglikeSearchItem.FIELD_TYPE, INDEX_TYPE_DIGG, Field.Store.YES, Field.Index.NOT_ANALYZED ) );

        Plugin plugin = PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME );
        Digg digg = DiggHome.findByPrimaryKey( diggSubmit.getDigg(  ).getIdDigg(  ), plugin );
        
        doc.add( new Field( SearchItem.FIELD_ROLE, digg.getRole(  ), Field.Store.YES,
                Field.Index.NOT_ANALYZED ) );

        // return the document
        return doc;
    }

    /**
     * Get the name of the indexer.
     * @return The name
     */
    public String getName(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_NAME );
    }

    /**
     * Get the version of the indexer
     * @return The version number
     */
    public String getVersion(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_INDEXER_VERSION );
    }

    /**
     * Get the state of indexer
     * @return Return true if the indexer is enabled, false else.
     */
    public boolean isEnable(  )
    {
        boolean bReturn = false;
        String strEnable = AppPropertiesService.getProperty( PROPERTY_INDEXER_ENABLE );

        if ( ( strEnable != null ) &&
                ( strEnable.equalsIgnoreCase( Boolean.TRUE.toString(  ) ) || strEnable.equals( ENABLE_VALUE_TRUE ) ) &&
                PluginService.isPluginEnable( DigglikePlugin.PLUGIN_NAME ) )
        {
            bReturn = true;
        }

        return bReturn;
    }

    /**
     * {@inheritDoc}
     */
	public List<String> getListType(  )
	{
		List<String> listType = new ArrayList<String>(  );
		listType.add( INDEX_TYPE_DIGG );
		
		return listType;
	}

	/**
     * {@inheritDoc}
     */
	public String getSpecificSearchAppUrl(  )
	{
		return JSP_SEARCH_DIGG;
	}

}
