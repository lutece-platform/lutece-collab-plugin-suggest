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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * class SubmiFilter
 * 
 */
public class SubmitFilter
{
    public static final int SORT_BY_DATE_RESPONSE_ASC = 0;
    public static final int SORT_BY_DATE_RESPONSE_DESC = 1;
    public static final int SORT_BY_SCORE_ASC = 2;
    public static final int SORT_BY_SCORE_DESC = 3;
    public static final int SORT_BY_NUMBER_COMMENT_ASC = 4;
    public static final int SORT_BY_NUMBER_COMMENT_DESC = 5;
    public static final int SORT_BY_NUMBER_VOTE_ASC = 6;
    public static final int SORT_BY_NUMBER_VOTE_DESC = 7;
    public static final int SORT_MANUALLY = 8;
    public static final int SORT_BY_NUMBER_VIEW_ASC = 9;
    public static final int SORT_BY_NUMBER_VIEW_DESC = 10;
    public static final int SORT_BY_DATE_MODIFY_ASC = 11;
    public static final int SORT_BY_DATE_MODIFY_DESC = 12;
    public static final int SORT_BY_PINNED_FIRST = 13;
    public static final int ALL_INT = -1;
    public static final int ID_TRUE = 1;
    public static final int ID_FALSE = 0;
    public static final int ID_ASC = 1;
    public static final int ID_DESC = 0;
    public static final int ID_PARENT_NULL = 0;
    private int _nNumberVote = ALL_INT;
    private int _nIdSuggest = ALL_INT;
    private int _nIdSuggestSubmit = ALL_INT;
    private int _nIdEntry = ALL_INT;
    private int _nIdCategory = ALL_INT;
    private int _nIdType = ALL_INT;
    private int _nIdParent = ALL_INT;
    private int _nIdReported = ALL_INT;
    private Timestamp _tDateFirst;
    private Timestamp _tDateLast;
    private int _nIdSuggestSubmitState = ALL_INT;
    private int _nIdCommentSubmitState = ALL_INT;
    private int _nIdPinned = ALL_INT;
    private int _nIdContainsCommentDisable = ALL_INT;
    private String _strLuteceUserKey = null;
    private List<Integer> _listSortBy = new ArrayList<Integer>( );

    /**
     * 
     * @return the id of the suggest insert in the filter
     */
    public int getIdSuggest( )
    {
        return _nIdSuggest;
    }

    /**
     * set the id of the suggest in the filter
     * 
     * @param nIdSuggest
     *            the id of suggest to insert in the filter
     */
    public void setIdSuggest( int nIdSuggest )
    {
        _nIdSuggest = nIdSuggest;
    }

    /**
     * 
     * @return true if the filter contain an id of suggest
     */
    public boolean containsIdSuggest( )
    {
        return ( _nIdSuggest != ALL_INT );
    }

    /**
     * 
     * @return the suggest submit state
     * 
     * */
    public int getIdSuggestSubmitState( )
    {
        return _nIdSuggestSubmitState;
    }

    /**
     * Set the suggest submit state
     * 
     * @param idState
     *            the suggest submit state
     */
    public void setIdSuggestSubmitState( int idState )
    {
        _nIdSuggestSubmitState = idState;
    }

    /**
     * 
     * @return true if the filter contain the suggest submit state
     */
    public boolean containsIdSuggestSubmitState( )
    {
        return ( _nIdSuggestSubmitState != ALL_INT );
    }

    /**
     * 
     * @return the comment submit state
     * 
     * */
    public int getIdCommentSubmitState( )
    {
        return _nIdCommentSubmitState;
    }

    /**
     * Set the comment submit state
     * 
     * @param nIdState
     *            the comment submit state
     */
    public void setIdCommentSubmitState( int nIdState )
    {
        _nIdCommentSubmitState = nIdState;
    }

    /**
     * 
     * @return true if the filter contain the comment submit state
     */
    public boolean containsIdCommentSubmitState( )
    {
        return ( _nIdCommentSubmitState != ALL_INT );
    }

    /**
     * 
     * @return the id of the suggest submit insert in the filter
     */
    public int getIdSuggestSubmit( )
    {
        return _nIdSuggestSubmit;
    }

    /**
     * set the id of the suggest submit in the filter
     * 
     * @param nIdSuggestSubmit
     *            the id of suggest submit to insert in the filter
     */
    public void setIdSuggestSubmit( int nIdSuggestSubmit )
    {
        _nIdSuggestSubmit = nIdSuggestSubmit;
    }

    /**
     * 
     * @return true if the filter contain an id of suggest
     */
    public boolean containsIdSuggestSubmit( )
    {
        return ( _nIdSuggestSubmit != ALL_INT );
    }

    /**
     * 
     * @return the id of entry insert in the filter
     */
    public int getIdEntry( )
    {
        return _nIdEntry;
    }

    /**
     * set the id of entry depend in the filter
     * 
     * @param nIdEntry
     *            the id of entry depend to insert in the filter
     */
    public void setIdEntry( int nIdEntry )
    {
        _nIdEntry = nIdEntry;
    }

    /**
     * 
     * @return true if the filter contain an id of entry depend
     */
    public boolean containsIdEntry( )
    {
        return ( _nIdEntry != ALL_INT );
    }

    /**
     * 
     * @return date of the first submit
     */
    public Timestamp getDateFirst( )
    {
        return _tDateFirst;
    }

    /**
     * set the date of the first submit
     * 
     * @param begin
     *            date of the first submit
     */
    public void setDateFirst( Timestamp begin )
    {
        _tDateFirst = begin;
    }

    /**
     * 
     * @return true if the filter contain the date of the first submit
     */
    public boolean containsDateFirst( )
    {
        return ( _tDateFirst != null );
    }

    /**
     * 
     * @return date of the last submit
     */
    public Timestamp getDateLast( )
    {
        return _tDateLast;
    }

    /**
     * set the date of the last submit
     * 
     * @param end
     *            the date of the last submit
     */
    public void setDateLast( Timestamp end )
    {
        _tDateLast = end;
    }

    /**
     * 
     * @return true if the filter contain the date of the last submit
     */
    public boolean containsDateLast( )
    {
        return ( _tDateLast != null );
    }

    /**
     * return the list of sort
     * 
     * @return the list of sort
     */
    public List<Integer> getSortBy( )
    {
        return _listSortBy;
    }

    /**
     * set the list of sort
     * 
     * @param listSortBy
     *            the list of sort
     */
    public void setSortBy( List<Integer> listSortBy )
    {
        _listSortBy = listSortBy;
    }

    /**
     * 
     * @return true if the filter contain the list of sort
     */
    public boolean containsSortBy( )
    {
        return ( ( _listSortBy != null ) && ( _listSortBy.size( ) != 0 ) );
    }

    /**
     * @param nSort
     *            The sort
     * @return true if the filter contain the sort
     */
    public boolean containsSortBy( Integer nSort )
    {
        if ( ( nSort != null ) && ( _listSortBy != null ) && ( _listSortBy.size( ) != 0 ) )
        {
            for ( Integer nSortBy : _listSortBy )
            {
                if ( nSortBy.equals( nSort ) )
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * convert a int to a boolean
     * 
     * @param nIdBoolean
     *            the id to convert
     * @return boolean
     */
    public boolean convertIdBoolean( int nIdBoolean )
    {
        if ( nIdBoolean == ID_TRUE )
        {
            return true;
        }

        return false;
    }

    /**
     * 
     * @return the id of the category insert in the filter
     */
    public int getIdCategory( )
    {
        return _nIdCategory;
    }

    /**
     * set the id of the category in the filter
     * 
     * @param nIdCategory
     *            the id of the category to insert in the filter
     */
    public void setIdCategory( int nIdCategory )
    {
        _nIdCategory = nIdCategory;
    }

    /**
     * 
     * @return true if the filter contain an id of category
     */
    public boolean containsIdCategory( )
    {
        return ( _nIdCategory != ALL_INT );
    }

    /**
     * 
     * @return 1 if the suggests return must be reported 0 if the suggests return must not be reported
     */
    public int getIdReported( )
    {
        return _nIdReported;
    }

    /**
     * set 1 if the suggests return must be reported 0 if the suggests return must not be reported
     * 
     * @param nIdReported
     *            idReported
     */
    public void setIdReported( int nIdReported )
    {
        _nIdReported = nIdReported;
    }

    /**
     * 
     * @return true if the filter contain reported
     */
    public boolean containsIdReported( )
    {
        return ( _nIdReported != ALL_INT );
    }

    /**
     * 
     * @return the number of vote in the filter
     */
    public int getNumberVote( )
    {
        return _nNumberVote;
    }

    /**
     * set the number of vote in the filter
     * 
     * @param nNumberVote
     *            the number of vote
     */
    public void setNumberVote( int nNumberVote )
    {
        _nNumberVote = nNumberVote;
    }

    /**
     * 
     * @return true if the filter contain a number of vote
     */
    public boolean containsNumberVote( )
    {
        return ( _nNumberVote != ALL_INT );
    }

    /**
     * @param strLuteceUserKey
     *            the _strLuteceUserKey to set
     */
    public void setLuteceUserKey( String strLuteceUserKey )
    {
        this._strLuteceUserKey = strLuteceUserKey;
    }

    /**
     * @return the _strLuteceUserKey
     */
    public String getLuteceUserKey( )
    {
        return _strLuteceUserKey;
    }

    /**
     * 
     * @return true if the filter contain a number of vote
     */
    public boolean containsLuteceUserKey( )
    {
        return ( !StringUtils.isEmpty( _strLuteceUserKey ) );
    }

    /**
     * 
     * @return the id of the suggest insert in the filter
     */
    public int getIdParent( )
    {
        return _nIdParent;
    }

    /**
     * set the id of the suggest in the filter
     * 
     * @param nIdParent
     *            the id of suggest to insert in the filter
     */
    public void setIdParent( int nIdParent )
    {
        _nIdParent = nIdParent;
    }

    /**
     * 
     * @return true if the filter contain an id of suggest
     */
    public boolean containsIdParent( )
    {
        return ( _nIdParent != ALL_INT );
    }

    /**
     * 
     * @return 1 if the suggests return must be reported 0 if the suggestss return must not be reported
     */
    public int getIdPinned( )
    {
        return _nIdPinned;
    }

    /**
     * set 1 if the suggestsubmit return must be pinned 0 if the suggestsubmit return must not be pinned
     * 
     * @param nIdPinned
     *            idPinned
     */
    public void setIdPinned( int nIdPinned )
    {
        _nIdPinned = nIdPinned;
    }

    /**
     * 
     * @return true if the filter contain reported
     */
    public boolean containsIdPinned( )
    {
        return ( _nIdPinned != ALL_INT );
    }

    /**
     * 
     * @return the id of the type insert in the filter
     */
    public int getIdType( )
    {
        return _nIdType;
    }

    /**
     * set the id of the type in the filter
     * 
     * @param nIdType
     *            the id of the type to insert in the filter
     */
    public void setIdType( int nIdType )
    {
        _nIdType = nIdType;
    }

    /**
     * 
     * @return true if the filter contain an id of type
     */
    public boolean containsIdType( )
    {
        return ( _nIdType != ALL_INT );
    }

    /**
     * 
     * @return 1 if the suggestsubmit return must contains comment disable 0 if the suggestsubmit return must not contains comment disable
     */
    public int getIdContainsCommentDisable( )
    {
        return _nIdContainsCommentDisable;
    }

    /**
     * 
     * @param nIdContainsCommentDisable
     *            1 if the suggestsubmit return must contains comment disable 0 if the suggestsubmit return must not contains comment disable
     */
    public void setIdContainsCommentDisable( int nIdContainsCommentDisable )
    {
        this._nIdContainsCommentDisable = nIdContainsCommentDisable;
    }

    /**
     * 
     * @return true if the filter contains Id comment Disable
     */
    public boolean containsIdContainsCommentDisable( )
    {
        return ( _nIdContainsCommentDisable != ALL_INT );
    }
}
