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
package fr.paris.lutece.plugins.digglike.business;

import fr.paris.lutece.plugins.digglike.utils.DiggUtils;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.xml.XmlUtil;

import java.sql.Timestamp;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * class DiggSubmit
 *
 */
public class DiggSubmit
{
    public static final int STATE_DISABLE = 1;
    public static final int STATE_WAITING_FOR_PUBLISH = 2;
    public static final int STATE_PUBLISH = 3;
    private static final String TAG_DIGG_SUBMIT = "digg-submit";
    private static final String TAG_DIGG_SUBMIT_CATEGORY = "digg-submit-category";
    private static final String TAG_DIGG_SUBMIT_TYPE = "digg-submit-type";
    private static final String TAG_DIGG_SUBMIT_DATE_RESPONSE = "digg-submit-date-response";
    private static final String TAG_DIGG_SUBMIT_NUMBER_VOTE = "digg-submit-number-vote";
    private static final String TAG_DIGG_SUBMIT_NUMBER_COMMENT = "digg-submit-number-comment";
    private static final String TAG_DIGG_SUBMIT_SCORE = "digg-submit-score";
    private static final String TAG_DIGG_SUBMIT_VALUE = "digg-submit-value";
    private static final String TAG_DIGGS_SUBMIT_COMMENTS = "diggs-submit-comments";
    private int _nIdDiggSubmit;
    private Timestamp _tDateResponse;
    private Digg _digg;
    private DiggSubmitState _diggSubmitState;
    private List<Response> _listResponse;
    private Category _category;
    private int _nNumberVote;
    private int _nNumberScore;
    private int _nNumberEnableComment;
    private int _nNumberComment;
    private int _nNumberView;
    private String _strDiggSubmitValue;
    private String _strDiggSubmitTitle;
    private String _strDiggSubmitValueShowInTheList;
    private boolean _bReported;
    private String _strLuteceUserKey;
    private List<CommentSubmit> _listComments;
    private int _nDiggSubmitOrder;
    private DiggSubmitType _diggSubmitType;
    private boolean _bDisableVote;
    private boolean _bDisableComment;
    private boolean _bPinned;
    private List<ReportedMessage> _listReportedMessages;

    /**
     * true if the digg submit have been reported by people
     * @return true if the digg submit have been reported by people
     */
    public boolean isReported(  )
    {
        return _bReported;
    }

    /**
     * set true if the digg submit have been reported by people
     * @param reported true if the digg submit have been reported by people
     */
    public void setReported( boolean reported )
    {
        _bReported = reported;
    }

    /**
    *
    * @return the title of the digg submit
    */
    public String getDiggSubmitTitle(  )
    {
        return _strDiggSubmitTitle;
    }

    /**
     * set the title of the digg submit
     * @param diggSubmitTitle the title of the digg submit
     */
    public void setDiggSubmitTitle( String diggSubmitTitle )
    {
        _strDiggSubmitTitle = diggSubmitTitle;
    }

    /**
    *
    * @return the digg associate to the submit
    */
    public Digg getDigg(  )
    {
        return _digg;
    }

    /**
     *
     * set  the digg associate to the submit
     * @param digg the digg associate to the submit
     */
    public void setDigg( Digg digg )
    {
        this._digg = digg;
    }

    /**
     * return the id of the digg submit
     * @return the id of the digg submit
     */
    public int getIdDiggSubmit(  )
    {
        return _nIdDiggSubmit;
    }

    /**
     * set the id of the digg submit
     * @param idDiggSubmit the id of the digg submit
     */
    public void setIdDiggSubmit( int idDiggSubmit )
    {
        _nIdDiggSubmit = idDiggSubmit;
    }

    /**
     *  return the digg submit date
     * @return the digg submit date
     */
    public Timestamp getDateResponse(  )
    {
        return _tDateResponse;
    }

    /**
     * set the digg submit date
     * @param dateResponse digg submit date
     */
    public void setDateResponse( Timestamp dateResponse )
    {
        _tDateResponse = dateResponse;
    }

    /**
     *
     * @return the list of response associate to the digg submit
     */
    public List<Response> getResponses(  )
    {
        return _listResponse;
    }

    /**
     * set the list of response associate to the digg submit
     * @param listResponse the list of response associate to the digg submit
     */
    public void setResponses( List<Response> listResponse )
    {
        _listResponse = listResponse;
    }

    /**
     *
     * @return the digg submit state (published,enabled,disabled)
     */
    public DiggSubmitState getDiggSubmitState(  )
    {
        return _diggSubmitState;
    }

    /**
     * set  the digg submit state (published,enabled,disabled)
     * @param submitState the digg submit state (published,enabled,disabled)
     */
    public void setDiggSubmitState( DiggSubmitState submitState )
    {
        _diggSubmitState = submitState;
    }

    /**
     *
     * @return the category associated to the digg submit
     */
    public Category getCategory(  )
    {
        return _category;
    }

    /**
     * set the category associated to the digg submit
     * @param category the category associated to the digg submit
     */
    public void setCategory( Category category )
    {
        _category = category;
    }

    /**
     *
     * @return the number of vote
     */
    public int getNumberVote(  )
    {
        return _nNumberVote;
    }

    /**
     * set the number of vote
     * @param numberVote the number of vote
     */
    public void setNumberVote( int numberVote )
    {
        _nNumberVote = numberVote;
    }

    /**
     * return the score of the digg submit
     * @return the score of the digg submit
     */
    public int getNumberScore(  )
    {
        return _nNumberScore;
    }

    /**
     * set the score of the digg submit
     * @param numberScore  the score of the digg submit
     */
    public void setNumberScore( int numberScore )
    {
        _nNumberScore = numberScore;
    }

    /**
     *
     * @return the digg submit value
     */
    public String getDiggSubmitValue(  )
    {
        return _strDiggSubmitValue;
    }

    /**
     * set the digg submit value
     * @param diggSubmitValue the digg submit value
     */
    public void setDiggSubmitValue( String diggSubmitValue )
    {
        _strDiggSubmitValue = diggSubmitValue;
    }

    /**
     *
     * @return the digg submit value show in the list of digg submit
     */
    public String getDiggSubmitValueShowInTheList(  )
    {
        return _strDiggSubmitValueShowInTheList;
    }

    /**
     * set the digg submit value show in the list of digg submit
     * @param diggSubmitValue the digg submit value show in the list of digg submit
     */
    public void setDiggSubmitValueShowInTheList( String diggSubmitValue )
    {
        _strDiggSubmitValueShowInTheList = diggSubmitValue;
    }

    /**
     *
     * @return the number of  enable comment associated to the digg submit
     */
    public int getNumberCommentEnable(  )
    {
        return _nNumberEnableComment;
    }

    /**
     * set the number of enable comment  associated to the digg submit
     * @param numberComment the number of  enable comment  associated to the digg submit
     */
    public void setNumberCommentEnable( int numberComment )
    {
        _nNumberEnableComment = numberComment;
    }

    /**
     *
     * @return the number of comment  associated to the digg submit
     */
    public int getNumberComment(  )
    {
        return _nNumberComment;
    }

    /**
     * set the number of  comment  associated to the digg submit
     * @param numberComment the number of   comment  associated to the digg submit
     */
    public void setNumberComment( int numberComment )
    {
        _nNumberComment = numberComment;
    }

    /**
    *
    * @return the number of view  associated to the digg submit
    */
    public int getNumberView(  )
    {
        return _nNumberView;
    }

    /**
     * set the number of  view  associated to the digg submit
     * @param numberComment the number of   view  associated to the digg submit
     */
    public void setNumberView( int numberView )
    {
        _nNumberView = numberView;
    }

    /**
    *
    * @return the lutece user key associate to the digg submit
    */
    public String getLuteceUserKey(  )
    {
        return _strLuteceUserKey;
    }

    /**
     *
     * set  the lutece user key associate to the digg submit
     * @param luteceUserKey the lutece user key associate to the digg submit
     */
    public void setLuteceUserKey( String luteceUserKey )
    {
        _strLuteceUserKey = luteceUserKey;
    }

    /**
     * Returns the xml of this digg submit
     *
     * @param request The HTTP Servlet request
     * @param locale the Locale
     * @return the xml of this digg submit
     */
    public String getXml( HttpServletRequest request, Locale locale )
    {
        StringBuffer strXml = new StringBuffer(  );
        XmlUtil.beginElement( strXml, TAG_DIGG_SUBMIT );
        XmlUtil.addElementHtml( strXml, TAG_DIGG_SUBMIT_DATE_RESPONSE,
            DateUtil.getDateString( getDateResponse(  ), locale ) );
        XmlUtil.addElementHtml( strXml, TAG_DIGG_SUBMIT_SCORE, Integer.toString( getNumberScore(  ) ) );
        XmlUtil.addElementHtml( strXml, TAG_DIGG_SUBMIT_NUMBER_VOTE, Integer.toString( getNumberVote(  ) ) );
        XmlUtil.addElementHtml( strXml, TAG_DIGG_SUBMIT_NUMBER_COMMENT, Integer.toString( getNumberComment(  ) ) );
        XmlUtil.addElementHtml( strXml, TAG_DIGG_SUBMIT_VALUE, getDiggSubmitValue(  ) );
        XmlUtil.addElementHtml( strXml, TAG_DIGG_SUBMIT_CATEGORY, getCategory(  ) != null?getCategory(  ).getTitle(  ): DiggUtils.EMPTY_STRING);
        XmlUtil.addElementHtml( strXml, TAG_DIGG_SUBMIT_TYPE, getDiggSubmitType(  ) != null?getDiggSubmitType(  ).getName(  ): DiggUtils.EMPTY_STRING );
        
        XmlUtil.beginElement( strXml, TAG_DIGGS_SUBMIT_COMMENTS );

        if ( ( getComments(  ) != null ) && ( getComments(  ).size(  ) != 0 ) )
        {
            for ( CommentSubmit commentSubmit : getComments(  ) )
            {
                strXml.append( commentSubmit.getXml( request, locale ) );
            }
        }

        XmlUtil.endElement( strXml, TAG_DIGGS_SUBMIT_COMMENTS );

        XmlUtil.endElement( strXml, TAG_DIGG_SUBMIT );

        return strXml.toString(  );
    }

    /**
     * @param listComments the _listComments to set
     */
    public void setComments( List<CommentSubmit> listComments )
    {
        this._listComments = listComments;
    }

    /**
     * @return the _listComments
     */
    public List<CommentSubmit> getComments(  )
    {
        return _listComments;
    }

    /**
     * @param nDiggSubmitOrder the _nDiggSubmitOrder to set
     */
    public void setDiggSubmitOrder( int nDiggSubmitOrder )
    {
        this._nDiggSubmitOrder = nDiggSubmitOrder;
    }

    /**
     * @return the _nDiggSubmitOrder
     */
    public int getDiggSubmitOrder(  )
    {
        return _nDiggSubmitOrder;
    }

    /**
     * @param diggSubmitType the diggSubmitType to set
     */
    public void setDiggSubmitType( DiggSubmitType diggSubmitType )
    {
        this._diggSubmitType = diggSubmitType;
    }

    /**
     * @return the diggSubmitType
     */
    public DiggSubmitType getDiggSubmitType(  )
    {
        return _diggSubmitType;
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
     * @param bDisable true if the vote is disable
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
	 * @param bDisable true if the comment is disable
	 */
	public void setDisableComment( boolean bDisable )
	{
	    _bDisableComment = bDisable;
	}

    /**
     * 
     * @return true if the diggsubmit is pinned
     */
	public boolean isPinned() {
		return _bPinned;
	}

	/**
	 * 
	 * @param _bPinned true if the diggsubmit is pinned
	 */
	public void setPinned(boolean _bPinned) {
		this._bPinned = _bPinned;
	}

	
	/**
	 * 
	 * @return the list of reported Messages
	 */
	public List<ReportedMessage> getReportedMessages() {
		return _listReportedMessages;
	}
	
	/**
	 * set the list of reported Messages
	 * @param _listReportedMessages the list of reported Messages
	 */
	public void setReportedMessages(List<ReportedMessage> _listReportedMessages) {
		this._listReportedMessages = _listReportedMessages;
	}
    
}
