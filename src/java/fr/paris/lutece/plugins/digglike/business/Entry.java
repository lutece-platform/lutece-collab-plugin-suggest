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
 * class Entry
 *
 */
public class Entry implements IEntry
{
    //	parameters Entry 
    protected static final String PARAMETER_TITLE = "title";
    protected static final String PARAMETER_HELP_MESSAGE = "help_message";
    protected static final String PARAMETER_COMMENT = "comment";
    protected static final String PARAMETER_MANDATORY = "mandatory";
    protected static final String PARAMETER_HEIGHT = "height";
    protected static final String PARAMETER_WIDTH = "width";
    protected static final String PARAMETER_VALUE = "value";
    protected static final String PARAMETER_MAX_SIZE_ENTER = "max_size_enter";
    protected static final String PARAMETER_SHOW_IN_DIGG_SUBMIT_LIST = "show_in_digg_submit_list";

    //	message
    protected static final String MESSAGE_MANDATORY_FIELD = "digglike.message.mandatory.field";
    protected static final String MESSAGE_NUMERIC_FIELD = "digglike.message.numeric.field";
    protected static final String FIELD_TITLE = "digglike.createEntry.labelTitle";
    protected static final String FIELD_INSERT_GROUP = "digglike.modifydigglike.manageEnter.labelInsertGroup";
    protected static final String FIELD_HELP_MESSAGE = "digglike.createEntry.labelHelpMessage";
    protected static final String FIELD_COMMENT = "digglike.createEntry.labelComment";
    protected static final String FIELD_VALUE = "digglike.createEntry.labelValue";
    protected static final String FIELD_PRESENTATION = "digglike.createEntry.labelPresentation";
    protected static final String FIELD_MANDATORY = "digglike.createEntry.labelMandatory";
    protected static final String FIELD_WIDTH = "digglike.createEntry.labelWidth";
    protected static final String FIELD_HEIGHT = "digglike.createEntry.labelHeight";
    protected static final String FIELD_MAX_SIZE_ENTER = "digglike.createEntry.labelMaxSizeEnter";

    //  Jsp Definition
    protected static final String JSP_DOWNLOAD_FILE = "jsp/admin/plugins/digglike/DoDownloadFile.jsp";

    //MARK
    protected static final String MARK_ENTRY = "entry";

    //Other constants
    protected static final String EMPTY_STRING = "";
    private int _nIdEntry;
    private Digg _digg;
    private EntryType _entryType;
    private String _strTitle;
    private String _strHelpMessage;
    private String _strComment;
    private boolean _bMandatory;
    private int _nPosition;
    private String _strDefaultValue;
    private int _nHeight = -1;
    private int _nWidth = -1;
    private int _nMaxSizeEnter = -1;
    private List<RegularExpression> _listRegularExpressionList;
    private boolean _bShowInDiggSubmitList;
    private List<EntryAdditionalAttribute> _listEntryAdditionalAttribute;

    /**
     *  @return the entry comment
     */
    public String getComment(  )
    {
        return _strComment;
    }

    /**
     *  @return the type of the entry
     */
    public EntryType getEntryType(  )
    {
        return _entryType;
    }

    /**
     *  @return the entry  help message
     */
    public String getHelpMessage(  )
    {
        return _strHelpMessage;
    }

    /**
     * @return the  id of entry
     */
    public int getIdEntry(  )
    {
        return _nIdEntry;
    }

    /**
     * @return position entry
     */
    public int getPosition(  )
    {
        return _nPosition;
    }

    /**
     * @return title entry
     */
    public String getTitle(  )
    {
        return _strTitle;
    }

    /**
     * @return true if the question is mandatory
     */
    public boolean isMandatory(  )
    {
        return _bMandatory;
    }

    /**
     * @return true if the entry must be shown in digg submit list
     */
    public boolean isShowInDiggSubmitList(  )
    {
        return _bShowInDiggSubmitList;
    }

    /**
     * set entry comment
     * @param comment entry comment
     */
    public void setComment( String comment )
    {
        _strComment = comment;
    }

    /**
     * set the type of the entry
     * @param entryType the type of the entry
     */
    public void setEntryType( EntryType entryType )
    {
        _entryType = entryType;
    }

    /**
     * set  the entry  help message
     * @param  helpMessage the entry  help message
     */
    public void setHelpMessage( String helpMessage )
    {
        _strHelpMessage = helpMessage;
    }

    /**
     * set the id of the entry
     * @param idEntry  the id of the entry
     */
    public void setIdEntry( int idEntry )
    {
        _nIdEntry = idEntry;
    }

    /**
     * set true if the entry must be shown in digg submit list
     * @param  show true if the entry must be shown in digg submit list
     */
    public void setShowInDiggSubmitList( boolean show )
    {
        _bShowInDiggSubmitList = show;
    }

    /**
     * set true if the question is mandatory
     * @param  mandatory true if the question is mandatory
     */
    public void setMandatory( boolean mandatory )
    {
        _bMandatory = mandatory;
    }

    /**
     * set position entry
     * @param  position  position entry
     */
    public void setPosition( int position )
    {
        _nPosition = position;
    }

    /**
     * set title entry
     * @param  title title
     */
    public void setTitle( String title )
    {
        _strTitle = title;
    }

    /**
     *
     * @return the digg of the entry
     */
    public Digg getDigg(  )
    {
        return _digg;
    }

    /**
     * set the digg of the entry
     * @param digg the digg of the entry
     */
    public void setDigg( Digg digg )
    {
        this._digg = digg;
    }

    /**
    *
    * @return a list of regular expression which is associate to the entry
    */
    public List<RegularExpression> getRegularExpressionList(  )
    {
        return _listRegularExpressionList;
    }

    /**
     * set a list of regular expression which is associate to the entry
     * @param regularExpressionList a list of regular expression which is associate to the entry
     */
    public void setRegularExpressionList( List<RegularExpression> regularExpressionList )
    {
        _listRegularExpressionList = regularExpressionList;
    }

    /**
    *
    * @return the value of the entry
    */
    public String getDefaultValue(  )
    {
        return _strDefaultValue;
    }

    /**
     * set the default value of the entry
     * @param value the default value of the entry
     */
    public void setDefaultValue( String value )
    {
        _strDefaultValue = value;
    }

    /**
     *
     * @return the width of the entry
     */
    public int getWidth(  )
    {
        return _nWidth;
    }

    /**
     * set the width of the entry
     * @param width the width of the entry
     */
    public void setWidth( int width )
    {
        this._nWidth = width;
    }

    /**
     *
     * @return  the height of the entry
     */
    public int getHeight(  )
    {
        return _nHeight;
    }

    /**
     * set the height of the entry
     * @param height  the height of the entry
     */
    public void setHeight( int height )
    {
        _nHeight = height;
    }

    /**
    *
    * @return the max size of enter user
    */
    public int getMaxSizeEnter(  )
    {
        return _nMaxSizeEnter;
    }

    /**
     * set the max size of enter user
     * @param maxSizeEnter the max size of enter user
     */
    public void setMaxSizeEnter( int maxSizeEnter )
    {
        _nMaxSizeEnter = maxSizeEnter;
    }

    /**
     * Get the HtmlCode  of   the entry
     * @return the HtmlCode  of   the entry
     *
     * */
    public String getTemplateHtmlCodeForm(  )
    {
        return null;
    }

    /**
     * Get the template of the html code of the response value  associate to the entry
     * @return the template of the html code of the response value  associate to the entry
     */
    public String getTemplateHtmlCodeResponse(  )
    {
        return EMPTY_STRING;
    }

    /**
     * Get the request data
     * @param request HttpRequest
     * @param locale the locale
     * @return null if all data requiered are in the request else the url of jsp error
     */
    public String getRequestData( HttpServletRequest request, Locale locale )
    {
        return null;
    }

    /**
     * save in the list of response the response associate to the entry in the form submit
     * @param request HttpRequest
     * @param listResponse the list of response associate to the entry in the form submit
     * @param locale the locale
     * @return a Form error object if there is an error in the response
     */
    public FormError getResponseData( HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        return null;
    }

    /**
     * save in the list of response the response associate to the entry in the form submit
     * @param nIdDiggSubmit the id of the DiggSubmit
     * @param request HttpRequest
     * @param listResponse the list of response associate to the entry in the form submit
     * @param locale the locale
     * @param plugin the plugin
     * @return a Form error object if there is an error in the response
     */
    public FormError getResponseData( int nIdDiggSubmit, HttpServletRequest request, List<Response> listResponse,
        Locale locale, Plugin plugin )
    {
        return null;
    }

    /**
     * Get template create url of the entry
     * @return template create url of the entry
     */
    public String getTemplateCreate(  )
    {
        return null;
    }

    /**
     * Get the template modify url  of the entry
     * @return template modify url  of the entry
     */
    public String getTemplateModify(  )
    {
        return null;
    }

    /**
     * The paginator who is use in the template modify of the entry
     * @param nItemPerPage Number of items to display per page
     * @param strBaseUrl The base Url for build links on each page link
     * @param strPageIndexParameterName The parameter name for the page index
     * @param strPageIndex The current page index
     * @return the paginator who is use in the template modify of the entry
     */
    public Paginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
        String strPageIndex )
    {
        return null;
    }

    /**
     * Get the list of regular expression  who is use in the template modify of the entry
     * @param entry the entry
     * @param plugin the plugin
     * @return the regular expression list who is use in the template modify of the entry
     */
    public ReferenceList getReferenceListRegularExpression( IEntry entry, Plugin plugin )
    {
        return null;
    }

    /**
    *
    * @return a list of additional attribute which is associate to the entry
    */
    public List<EntryAdditionalAttribute> getEntryAdditionalAttributeList(  )
    {
        return _listEntryAdditionalAttribute;
    }

    /**
     * set a list of additional attribute which is associate to the entry
     * @param entryAdditionalAttributeList a list of additional attribute which is associate to the entry
     */
    public void setEntryAdditionalAttributeList( List<EntryAdditionalAttribute> entryAdditionalAttributeList )
    {
        _listEntryAdditionalAttribute = entryAdditionalAttributeList;
    }
}
