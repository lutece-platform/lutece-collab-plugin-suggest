/*
 * Copyright (c) 2002-2010, Mairie de Paris
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

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.xml.XmlUtil;


/**
 *
 * class comment
 *
 */
public class CommentSubmit
{
    private static final String TAG_DIGG_SUBMIT_COMMENT = "digg-submit-comment";
    private static final String TAG_DIGG_SUBMIT_COMMENT_DATE = "digg-submit-comment-date";
    private static final String TAG_DIGG_SUBMIT_COMMENT_VALUE = "digg-submit-comment-value";
    private int _nIdCommentSubmit;
    private DiggSubmit _diggSubmit;
    private Timestamp _tDateComment;
    private String _strValue;
    private boolean _nActive;
    private String _strLuteceUserKey;
    private boolean _bOfficialAnswer;
    private List<CommentSubmit> _listComments;
    private int _nIdParent;
    private Timestamp _tLastDateComment;

    /**
     * return the id of the comment submit
     * @return the id of the comment submit
     */
    public int getIdCommentSubmit(  )
    {
        return _nIdCommentSubmit;
    }

    /**
     * set the id of the comment submit
     * @param idCommentSubmit the id of the comment submit
     */
    public void setIdCommentSubmit( int idCommentSubmit )
    {
        _nIdCommentSubmit = idCommentSubmit;
    }

    /**
    *
    * @return the digg submit of the comment
    */
    public DiggSubmit getDiggSubmit(  )
    {
        return _diggSubmit;
    }

    /**
     * set the digg submit of the comment
     * @param diggSubmit the digg submit of the comment
     */
    public void setDiggSubmit( DiggSubmit diggSubmit )
    {
        _diggSubmit = diggSubmit;
    }

    /**
     *  return the comment date
     * @return the comment date
     */
    public Timestamp getDateComment(  )
    {
        return _tDateComment;
    }

    /**
     * set the comment date
     * @param commentDate the comment date
     */
    public void setDateComment( Timestamp commentDate )
    {
        _tDateComment = commentDate;
    }

    /**
    *
    * @return the value of the comment
    */
    public String getValue(  )
    {
        return _strValue;
    }

    /**
     * set the value of the comment
     * @param value the value of the comment
     */
    public void setValue( String value )
    {
        _strValue = value;
    }

    /**
    *
    * @return true if the comment is active
    */
    public boolean isActive(  )
    {
        return _nActive;
    }

    /**
     * set true if the comment is active
     * @param active true if the comment is active
     */
    public void setActive( boolean active )
    {
        _nActive = active;
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
     * @param luteceUserKey the lutece user key
     */
    public void setLuteceUserKey( String luteceUserKey )
    {
        _strLuteceUserKey = luteceUserKey;
    }

    /**
     * @param bOfficialAnswer the _bOfficialAnswer to set
     */
    public void setOfficialAnswer( Boolean bOfficialAnswer )
    {
        this._bOfficialAnswer = bOfficialAnswer;
    }

    /**
     * @return the _bOfficialAnswer
     */
    public Boolean isOfficialAnswer(  )
    {
        return _bOfficialAnswer;
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
     * @param nIdParent the _nIdParent to set
     */
    public void setIdParent( int nIdParent )
    {
        this._nIdParent = nIdParent;
    }

    /**
     * @return the _nIdParent
     */
    public int getIdParent(  )
    {
        return _nIdParent;
    }

    /**
     *  return the last comment date
     * @return the last comment date
     */
    public Timestamp getLastDateComment(  )
    {
        return _tLastDateComment;
    }

    /**
     * set the last comment date
     * @param lastCommentDate the last comment date
     */
    public void setLastDateComment( Timestamp lastCommentDate )
    {
        _tLastDateComment = lastCommentDate;
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
        XmlUtil.beginElement( strXml, TAG_DIGG_SUBMIT_COMMENT );
        XmlUtil.addElementHtml( strXml, TAG_DIGG_SUBMIT_COMMENT_DATE,
            DateUtil.getDateString( getDateComment(  ), locale ) );
        XmlUtil.addElementHtml( strXml, TAG_DIGG_SUBMIT_COMMENT_VALUE, getValue(  ) );

        XmlUtil.endElement( strXml, TAG_DIGG_SUBMIT_COMMENT );

        return strXml.toString(  );
    }
}
