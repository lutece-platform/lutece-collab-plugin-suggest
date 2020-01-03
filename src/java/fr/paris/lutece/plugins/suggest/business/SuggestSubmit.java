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
package fr.paris.lutece.plugins.suggest.business;

import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.portal.service.resource.IExtendableResource;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.xml.XmlUtil;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * class SuggestSubmit
 * 
 */
public class SuggestSubmit implements IExtendableResource
{
    public static final String RESOURCE_TYPE = "SUGGEST_SUGGEST_SUBMIT_TYPE";
    public static final int STATE_DISABLE = 1;
    public static final int STATE_WAITING_FOR_PUBLISH = 2;
    public static final int STATE_PUBLISH = 3;
    private static final String TAG_SUGGEST_SUBMIT = "suggest-submit";
    private static final String TAG_SUGGEST_SUBMIT_TITLE = "suggest-submit-title";
    private static final String TAG_SUGGEST_SUBMIT_CATEGORY = "suggest-submit-category";
    private static final String TAG_SUGGEST_SUBMIT_TYPE = "suggest-submit-type";
    private static final String TAG_SUGGEST_SUBMIT_DATE_RESPONSE = "suggest-submit-date-response";
    private static final String TAG_SUGGEST_SUBMIT_NUMBER_VOTE = "suggest-submit-number-vote";
    private static final String TAG_SUGGEST_SUBMIT_NUMBER_COMMENT = "suggest-submit-number-comment";
    private static final String TAG_SUGGEST_SUBMIT_SCORE = "suggest-submit-score";
    private static final String TAG_SUGGESTS_SUBMIT_COMMENTS = "suggest-submit-comments";
    private static final String TAG_SUGGESTS_SUBMIT_RESPONSES = "suggest-submit-responses";
    private int _nIdSuggestSubmit;
    private Timestamp _tDateResponse;
    private Suggest _suggest;
    private SuggestSubmitState _suggestSubmitState;
    private List<Response> _listResponse;
    private Category _category;
    private int _nNumberVote;
    private int _nNumberScore;
    private int _nNumberEnableComment;
    private int _nNumberComment;
    private int _nNumberView;
    private String _strSuggestSubmitValue;
    private String _strSuggestSubmitTitle;
    private String _strSuggestSubmitValueShowInTheList;
    private boolean _bReported;
    private String _strLuteceUserKey;
    private List<CommentSubmit> _listComments;
    private int _nSuggestSubmitOrder;
    private SuggestSubmitType _suggestSubmitType;
    private boolean _bDisableVote;
    private boolean _bDisableComment;
    private boolean _bPinned;
    private List<ReportedMessage> _listReportedMessages;
    private Integer _nIdImageResource;

    /**
     * true if the suggest submit have been reported by people
     * 
     * @return true if the suggest submit have been reported by people
     */
    public boolean isReported( )
    {
        return _bReported;
    }

    /**
     * set true if the suggest submit have been reported by people
     * 
     * @param reported
     *            true if the suggest submit have been reported by people
     */
    public void setReported( boolean reported )
    {
        _bReported = reported;
    }

    /**
     * 
     * @return the title of the suggest submit
     */
    public String getSuggestSubmitTitle( )
    {
        return _strSuggestSubmitTitle;
    }

    /**
     * set the title of the suggest submit
     * 
     * @param suggestSubmitTitle
     *            the title of the suggest submit
     */
    public void setSuggestSubmitTitle( String suggestSubmitTitle )
    {
        _strSuggestSubmitTitle = suggestSubmitTitle;
    }

    /**
     * 
     * @return the suggest associate to the submit
     */
    public Suggest getSuggest( )
    {
        return _suggest;
    }

    /**
     * 
     * set the suggest associate to the submit
     * 
     * @param suggest
     *            the suggest associate to the submit
     */
    public void setSuggest( Suggest suggest )
    {
        this._suggest = suggest;
    }

    /**
     * return the id of the suggest submit
     * 
     * @return the id of the suggest submit
     */
    public int getIdSuggestSubmit( )
    {
        return _nIdSuggestSubmit;
    }

    /**
     * set the id of the suggest submit
     * 
     * @param idSuggestSubmit
     *            the id of the suggest submit
     */
    public void setIdSuggestSubmit( int idSuggestSubmit )
    {
        _nIdSuggestSubmit = idSuggestSubmit;
    }

    /**
     * return the suggest submit date
     * 
     * @return the suggest submit date
     */
    public Timestamp getDateResponse( )
    {
        return _tDateResponse;
    }

    /**
     * set the suggest submit date
     * 
     * @param dateResponse
     *            suggest submit date
     */
    public void setDateResponse( Timestamp dateResponse )
    {
        _tDateResponse = dateResponse;
    }

    /**
     * 
     * @return the list of response associate to the suggest submit
     */
    public List<Response> getResponses( )
    {
        return _listResponse;
    }

    /**
     * set the list of response associate to the suggest submit
     * 
     * @param listResponse
     *            the list of response associate to the suggest submit
     */
    public void setResponses( List<Response> listResponse )
    {
        _listResponse = listResponse;
    }

    /**
     * 
     * @return the suggest submit state (published,enabled,disabled)
     */
    public SuggestSubmitState getSuggestSubmitState( )
    {
        return _suggestSubmitState;
    }

    /**
     * set the suggest submit state (published,enabled,disabled)
     * 
     * @param submitState
     *            the suggest submit state (published,enabled,disabled)
     */
    public void setSuggestSubmitState( SuggestSubmitState submitState )
    {
        _suggestSubmitState = submitState;
    }

    /**
     * 
     * @return the category associated to the suggest submit
     */
    public Category getCategory( )
    {
        return _category;
    }

    /**
     * set the category associated to the suggest submit
     * 
     * @param category
     *            the category associated to the suggest submit
     */
    public void setCategory( Category category )
    {
        _category = category;
    }

    /**
     * 
     * @return the number of vote
     */
    public int getNumberVote( )
    {
        return _nNumberVote;
    }

    /**
     * set the number of vote
     * 
     * @param numberVote
     *            the number of vote
     */
    public void setNumberVote( int numberVote )
    {
        _nNumberVote = numberVote;
    }

    /**
     * return the score of the suggest submit
     * 
     * @return the score of the suggest submit
     */
    public int getNumberScore( )
    {
        return _nNumberScore;
    }

    /**
     * set the score of the suggest submit
     * 
     * @param numberScore
     *            the score of the suggest submit
     */
    public void setNumberScore( int numberScore )
    {
        _nNumberScore = numberScore;
    }

    /**
     * 
     * @return the suggest submit value
     */
    public String getSuggestSubmitValue( )
    {
        return _strSuggestSubmitValue;
    }

    /**
     * set the suggest submit value
     * 
     * @param suggestSubmitValue
     *            the suggest submit value
     */
    public void setSuggestSubmitValue( String suggestSubmitValue )
    {
        _strSuggestSubmitValue = suggestSubmitValue;
    }

    /**
     * 
     * @return the suggest submit value show in the list of suggest submit
     */
    public String getSuggestSubmitValueShowInTheList( )
    {
        return _strSuggestSubmitValueShowInTheList;
    }

    /**
     * set the suggest submit value show in the list of suggest submit
     * 
     * @param suggestSubmitValue
     *            the suggest submit value show in the list of suggest submit
     */
    public void setSuggestSubmitValueShowInTheList( String suggestSubmitValue )
    {
        _strSuggestSubmitValueShowInTheList = suggestSubmitValue;
    }

    /**
     * 
     * @return the number of enable comment associated to the suggest submit
     */
    public int getNumberCommentEnable( )
    {
        return _nNumberEnableComment;
    }

    /**
     * set the number of enable comment associated to the suggest submit
     * 
     * @param numberComment
     *            the number of enable comment associated to the suggest submit
     */
    public void setNumberCommentEnable( int numberComment )
    {
        _nNumberEnableComment = numberComment;
    }

    /**
     * 
     * @return the number of comment associated to the suggest submit
     */
    public int getNumberComment( )
    {
        return _nNumberComment;
    }

    /**
     * set the number of comment associated to the suggest submit
     * 
     * @param numberComment
     *            the number of comment associated to the suggest submit
     */
    public void setNumberComment( int numberComment )
    {
        _nNumberComment = numberComment;
    }

    /**
     * 
     * @return the number of view associated to the suggest submit
     */
    public int getNumberView( )
    {
        return _nNumberView;
    }

    /**
     * set the number of view associated to the suggest submit
     * 
     * @param nNumberView
     *            the number of view associated to the suggest submit
     */
    public void setNumberView( int nNumberView )
    {
        _nNumberView = nNumberView;
    }

    /**
     * 
     * @return the lutece user key associate to the suggest submit
     */
    public String getLuteceUserKey( )
    {
        return _strLuteceUserKey;
    }

    /**
     * 
     * set the lutece user key associate to the suggest submit
     * 
     * @param strLuteceUserKey
     *            the lutece user key associate to the suggest submit
     */
    public void setLuteceUserKey( String strLuteceUserKey )
    {
        _strLuteceUserKey = strLuteceUserKey;
    }

    /**
     * Returns the xml of this suggest submit
     * 
     * @param request
     *            The HTTP Servlet request
     * @param locale
     *            the Locale
     * @return the xml of this suggest submit
     */
    public String getXml( HttpServletRequest request, Locale locale )
    {
        StringBuffer strXml = new StringBuffer( );
        XmlUtil.beginElement( strXml, TAG_SUGGEST_SUBMIT );
        XmlUtil.addElementHtml( strXml, TAG_SUGGEST_SUBMIT_DATE_RESPONSE, DateUtil.getDateString( this.getDateResponse( ), locale ) );
        XmlUtil.addElementHtml( strXml, TAG_SUGGEST_SUBMIT_SCORE, Integer.toString( this.getNumberScore( ) ) );
        XmlUtil.addElementHtml( strXml, TAG_SUGGEST_SUBMIT_NUMBER_VOTE, Integer.toString( this.getNumberVote( ) ) );
        XmlUtil.addElementHtml( strXml, TAG_SUGGEST_SUBMIT_NUMBER_COMMENT, Integer.toString( this.getNumberComment( ) ) );
        XmlUtil.addElementHtml( strXml, TAG_SUGGEST_SUBMIT_CATEGORY, ( this.getCategory( ) != null ) ? this.getCategory( ).getTitle( )
                : SuggestUtils.EMPTY_STRING );
        XmlUtil.addElementHtml( strXml, TAG_SUGGEST_SUBMIT_TYPE, ( this.getSuggestSubmitType( ) != null ) ? this.getSuggestSubmitType( ).getName( )
                : SuggestUtils.EMPTY_STRING );
        XmlUtil.addElementHtml( strXml, TAG_SUGGEST_SUBMIT_TITLE, this.getSuggestSubmitTitle( ) );

        XmlUtil.beginElement( strXml, TAG_SUGGESTS_SUBMIT_RESPONSES );

        if ( ( getResponses( ) != null ) && ( getResponses( ).size( ) != 0 ) )
        {
            HashMap<Integer, Response> hashResponsesEntry = new HashMap<Integer, Response>( );

            for ( Response response : getResponses( ) )
            {
                hashResponsesEntry.put( response.getEntry( ).getIdEntry( ), response );
            }

            for ( IEntry entry : this.getSuggest( ).getEntries( ) )
            {
                if ( hashResponsesEntry.containsKey( entry.getIdEntry( ) ) )
                {
                    strXml.append( hashResponsesEntry.get( entry.getIdEntry( ) ).getXml( request, locale ) );
                }
                else
                {
                    // add xml empty response for this suggest submit
                    Response responseEmpty = new Response( );
                    responseEmpty.setEntry( entry );
                    responseEmpty.setValueResponse( SuggestUtils.EMPTY_STRING );
                    strXml.append( responseEmpty.getXml( request, locale ) );
                }
            }
        }

        XmlUtil.endElement( strXml, TAG_SUGGESTS_SUBMIT_RESPONSES );

        XmlUtil.beginElement( strXml, TAG_SUGGESTS_SUBMIT_COMMENTS );

        if ( ( getComments( ) != null ) && ( getComments( ).size( ) != 0 ) )
        {
            for ( CommentSubmit commentSubmit : getComments( ) )
            {
                strXml.append( commentSubmit.getXml( request, locale ) );
            }
        }

        XmlUtil.endElement( strXml, TAG_SUGGESTS_SUBMIT_COMMENTS );

        XmlUtil.endElement( strXml, TAG_SUGGEST_SUBMIT );

        return strXml.toString( );
    }

    /**
     * @param listComments
     *            the _listComments to set
     */
    public void setComments( List<CommentSubmit> listComments )
    {
        this._listComments = listComments;
    }

    /**
     * @return the _listComments
     */
    public List<CommentSubmit> getComments( )
    {
        return _listComments;
    }

    /**
     * @param nSuggestSubmitOrder
     *            the _nSuggestSubmitOrder to set
     */
    public void setSuggestSubmitOrder( int nSuggestSubmitOrder )
    {
        this._nSuggestSubmitOrder = nSuggestSubmitOrder;
    }

    /**
     * @return the _nSuggestSubmitOrder
     */
    public int getSuggestSubmitOrder( )
    {
        return _nSuggestSubmitOrder;
    }

    /**
     * @param suggestSubmitType
     *            the suggestSubmitType to set
     */
    public void setSuggestSubmitType( SuggestSubmitType suggestSubmitType )
    {
        this._suggestSubmitType = suggestSubmitType;
    }

    /**
     * @return the suggestSubmitType
     */
    public SuggestSubmitType getSuggestSubmitType( )
    {
        return _suggestSubmitType;
    }

    /**
     * 
     * @return true if the vote is disable
     */
    public boolean isDisableVote( )
    {
        return _bDisableVote;
    }

    /**
     * set true if the vote is disable
     * 
     * @param bDisable
     *            true if the vote is disable
     */
    public void setDisableVote( boolean bDisable )
    {
        _bDisableVote = bDisable;
    }

    /**
     * 
     * @return true if the vote is disable
     */
    public boolean isDisableComment( )
    {
        return _bDisableComment;
    }

    /**
     * set true if the vote is disable
     * 
     * @param bDisable
     *            true if the comment is disable
     */
    public void setDisableComment( boolean bDisable )
    {
        _bDisableComment = bDisable;
    }

    /**
     * 
     * @return true if the suggestsubmit is pinned
     */
    public boolean isPinned( )
    {
        return _bPinned;
    }

    /**
     * 
     * @param bPinned
     *            true if the suggestsubmit is pinned
     */
    public void setPinned( boolean bPinned )
    {
        this._bPinned = bPinned;
    }

    /**
     * 
     * @return the list of reported Messages
     */
    public List<ReportedMessage> getReportedMessages( )
    {
        return _listReportedMessages;
    }

    /**
     * set the list of reported Messages
     * 
     * @param listReportedMessages
     *            the list of reported Messages
     */
    public void setReportedMessages( List<ReportedMessage> listReportedMessages )
    {
        this._listReportedMessages = listReportedMessages;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdExtendableResource( )
    {
        return Integer.toString( _nIdSuggestSubmit );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtendableResourceType( )
    {
        return RESOURCE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtendableResourceName( )
    {
        return _strSuggestSubmitTitle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtendableResourceDescription( )
    {
        return _strSuggestSubmitTitle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtendableResourceImageUrl( )
    {
        if ( _nIdImageResource != null )
        {
            StringBuilder sbUrl = new StringBuilder( SuggestUtils.SERVLET_IMAGE_PATH );
            sbUrl.append( _nIdImageResource );

            return sbUrl.toString( );
        }

        return null;
    }

    /**
     * the image id resource associate to the suggest submit
     * 
     * @return the image id resource associate to the suggest submit
     */
    public Integer getIdImageResource( )
    {
        return _nIdImageResource;
    }

    /**
     * set the image id resource associate to the suggest submit
     * 
     * @param nIdImageRessource
     *            the image id resource associate to the suggest submit
     */
    public void setIdImageResource( Integer nIdImageRessource )
    {
        this._nIdImageResource = nIdImageRessource;
    }
}
