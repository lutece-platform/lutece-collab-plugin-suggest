/*
 * Copyright (c) 2002-2011, Mairie de Paris
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

import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.Paginator;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 *
 *IEntry Class
 */
public interface IEntry
{
    /**
     * @return the  id of entry
     */
    int getIdEntry(  );

    /**
     * set the id of the entry
     * @param idEntry  the id of the entry
     */
    void setIdEntry( int idEntry );

    /**
     * @return title entry
     */
    String getTitle(  );

    /**
     * set title entry
     * @param  title title
     */
    void setTitle( String title );

    /**
     *  @return the entry  help message
     */
    String getHelpMessage(  );

    /**
     * set  the entry  help message
     * @param  helpMessage the entry  help message
     */
    void setHelpMessage( String helpMessage );

    /**
     *  @return the entry comment
     */
    String getComment(  );

    /**
     * set entry comment
     * @param comment entry comment
     */
    void setComment( String comment );

    /**
     * @return true if the question is mandatory
     */
    boolean isMandatory(  );

    /**
     * @return true if the entry must be shown in digg submit list
     */
    boolean isShowInDiggSubmitList(  );

    /**
     * set true if the question is mandatory
     * @param  mandatory true if the question is mandatory
     */
    void setMandatory( boolean mandatory );

    /**
     * set true if the entry must be shown in digg submit list
     * @param  show true if the entry must be shown in digg submit list
     */
    void setShowInDiggSubmitList( boolean show );

    /**
     * @return position entry
     */
    int getPosition(  );

    /**
     * set position entry
     * @param  position  position entry
     */
    void setPosition( int position );

    /**
     *
     * @return the form of the entry
     */
    Digg getDigg(  );

    /**
     * set the digg of the entry
     * @param digg the digg of the entry
     */
    void setDigg( Digg digg );

    /**
     *  @return the type of the entry
     */
    EntryType getEntryType(  );

    /**
     * set the type of the entry
     * @param entryType the type of the entry
     */
    void setEntryType( EntryType entryType );

    /**
    *
    * @return a list of regular expression which is associate to the entry
    */
    List<RegularExpression> getRegularExpressionList(  );

    /**
     * set a list of regular expression which is associate to the entry
     * @param regularExpressionList a list of regular expression which is associate to the entry
     */
    void setRegularExpressionList( List<RegularExpression> regularExpressionList );

    /**
    *
    * @return the value of the entry
    */
    String getDefaultValue(  );

    /**
     * set the default value of the entry
     * @param value the default value of the entry
     */
    void setDefaultValue( String value );

    /**
     *
     * @return the width of the entry
     */
    int getWidth(  );

    /**
     * set the width of the entry
     * @param width the width of the entry
     */
    void setWidth( int width );

    /**
     *
     * @return  the height of the entry
     */
    int getHeight(  );

    /**
     * set the height of the entry
     * @param height  the height of the entry
     */
    void setHeight( int height );

    /**
    *
    * @return the max size of enter user
    */
    int getMaxSizeEnter(  );

    /**
     * set the max size of enter user
     * @param maxSizeEnter the max size of enter user
     */
    void setMaxSizeEnter( int maxSizeEnter );

    /**
     * Get the request data
     * @param request HttpRequest
     * @param locale the locale
     * @return null if all data requiered are in the request else the url of jsp error
     */
    String getRequestData( HttpServletRequest request, Locale locale );

    /**
     * save in the list of response the response associate to the entry in the form submit
     * @param request HttpRequest
     * @param listResponse the list of response associate to the entry in the form submit
     * @param locale the locale
     * @return a Form error object if there is an error in the response
     */
    FormError getResponseData( HttpServletRequest request, List<Response> listResponse, Locale locale );

    /**
    * save in the list of response the response associate to the entry in the form submit
    * @param nIdDiggSubmit the id of the diggSubmit
    * @param request HttpRequest
    * @param listResponse the list of response associate to the entry in the form submit
    * @param locale the locale
    * @param plugin the current plugin
    * @return a Form error object if there is an error in the response
    */
    FormError getResponseData( int nIdDiggSubmit, HttpServletRequest request, List<Response> listResponse,
        Locale locale, Plugin plugin );

    /**
     * Get the HtmlCode  of   the entry
     * @return the HtmlCode  of   the entry
     *
     * */
    String getTemplateHtmlCodeForm(  );

    /**
     * Get the template of the html code of the response value  associate to the entry
     * @return the template of the html code of the response value  associate to the entry
     */
    String getTemplateHtmlCodeResponse(  );

    /**
     * Get template create url of the entry
     * @return template create url of the entry
     */
    String getTemplateCreate(  );

    /**
     * Get the template modify url  of the entry
     * @return template modify url  of the entry
     */
    String getTemplateModify(  );

    /**
     * The paginator who is use in the template modify of the entry
     * @param nItemPerPage Number of items to display per page
    * @param strBaseUrl The base Url for build links on each page link
    * @param strPageIndexParameterName The parameter name for the page index
    * @param strPageIndex The current page index
     * @return the paginator who is use in the template modify of the entry
     */
    Paginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName, String strPageIndex );

    /**
     * Get the list of regular expression  who is use in the template modify of the entry
     * @param plugin the plugin
     * @param entry the entry
     * @return the regular expression list who is use in the template modify of the entry
     */
    ReferenceList getReferenceListRegularExpression( IEntry entry, Plugin plugin );

    /**
    *
    * @return a list of additional attribute which is associate to the entry
    */
    List<EntryAdditionalAttribute> getEntryAdditionalAttributeList(  );

    /**
     * set a list of additional attribute which is associate to the entry
     * @param entryAdditionalAttributeList a list of additional attribute which is associate to the entry
     */
    void setEntryAdditionalAttributeList( List<EntryAdditionalAttribute> entryAdditionalAttributeList );
}
