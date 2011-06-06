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

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * class SubmiFilter
 *
 */
public class SubmitFilter
{
    public final static int SORT_BY_DATE_RESPONSE_ASC = 0;
    public final static int SORT_BY_DATE_RESPONSE_DESC = 1;
    public final static int SORT_BY_SCORE_ASC = 2;
    public final static int SORT_BY_SCORE_DESC = 3;
    public final static int SORT_BY_NUMBER_COMMENT_ASC = 4;
    public final static int SORT_BY_NUMBER_COMMENT_DESC = 5;
    public final static int SORT_BY_NUMBER_VOTE_ASC = 6;
    public final static int SORT_BY_NUMBER_VOTE_DESC = 7;
    public final static int SORT_MANUALLY = 8;
    public static final int ALL_INT = -1;
    public static final int ID_TRUE = 1;
    public static final int ID_FALSE = 0;
    public static final int ID_ASC = 1;
    public static final int ID_DESC = 0;
    private int _nNumberVote = ALL_INT;
    private int _nIdDigg = ALL_INT;
    private int _nIdDiggSubmit = ALL_INT;
    private int _nIdEntry = ALL_INT;
    private int _nIdCategory = ALL_INT;
    private int _nIdReported = ALL_INT;
    private Timestamp _tDateFirst;
    private Timestamp _tDateLast;
    private int _nIdDiggSubmitState = ALL_INT;
    private int _nIdCommentSubmitState = ALL_INT;
    private String _strLuteceUserKey = null;
    private List<Integer> _listSortBy = new ArrayList<Integer>(  );

    /**
     *
     * @return  the id of the digg insert in the filter
     */
    public int getIdDigg(  )
    {
        return _nIdDigg;
    }

    /**
     * set  the id of the digg in the filter
     * @param idDigg the id of digg to insert in the filter
     */
    public void setIdDigg( int idDigg )
    {
        _nIdDigg = idDigg;
    }

    /**
     *
     * @return true if the filter contain an id of digg
     */
    public boolean containsIdDigg(  )
    {
        return ( _nIdDigg != ALL_INT );
    }

    /**
    *
    * @return the digg submit state
    *
    *  */
    public int getIdDiggSubmitState(  )
    {
        return _nIdDiggSubmitState;
    }

    /**
     * Set the digg submit state
     * @param idState  the digg submit state
     */
    public void setIdDiggSubmitState( int idState )
    {
        _nIdDiggSubmitState = idState;
    }

    /**
     *
     * @return true if the filter contain the digg submit state
     */
    public boolean containsIdDiggSubmitState(  )
    {
        return ( _nIdDiggSubmitState != ALL_INT );
    }

    /**
    *
    * @return the comment submit state
    *
    *  */
    public int getIdCommentSubmitState(  )
    {
        return _nIdCommentSubmitState;
    }

    /**
     * Set the comment submit state
     * @param idState  the comment submit state
     */
    public void setIdCommentSubmitState( int idState )
    {
        _nIdCommentSubmitState = idState;
    }

    /**
     *
     * @return true if the filter contain the comment submit state
     */
    public boolean containsIdCommentSubmitState(  )
    {
        return ( _nIdCommentSubmitState != ALL_INT );
    }

    /**
    *
    * @return  the id of the digg submit insert in the filter
    */
    public int getIdDiggSubmit(  )
    {
        return _nIdDiggSubmit;
    }

    /**
     * set  the id of the digg submit in the filter
     * @param idDiggSubmit the id of digg submit to insert in the filter
     */
    public void setIdDiggSubmit( int idDiggSubmit )
    {
        _nIdDiggSubmit = idDiggSubmit;
    }

    /**
     *
     * @return true if the filter contain an id of digg
     */
    public boolean containsIdDiggSubmit(  )
    {
        return ( _nIdDiggSubmit != ALL_INT );
    }

    /**
    *
    * @return  the id of entry insert in the filter
    */
    public int getIdEntry(  )
    {
        return _nIdEntry;
    }

    /**
     * set the id of entry depend  in the filter
     * @param idEntry the id of entry depend to insert in the filter
     */
    public void setIdEntry( int idEntry )
    {
        _nIdEntry = idEntry;
    }

    /**
     *
     * @return true if the filter contain an id of entry depend
     */
    public boolean containsIdEntry(  )
    {
        return ( _nIdEntry != ALL_INT );
    }

    /**
         *
         * @return date of the first submit
         */
    public Timestamp getDateFirst(  )
    {
        return _tDateFirst;
    }

    /**
     * set the date of the first submit
     * @param begin date of the first submit
     */
    public void setDateFirst( Timestamp begin )
    {
        _tDateFirst = begin;
    }

    /**
    *
    * @return true if the filter contain the date of the first submit
    */
    public boolean containsDateFirst(  )
    {
        return ( _tDateFirst != null );
    }

    /**
     *
     * @return date of the last submit
     */
    public Timestamp getDateLast(  )
    {
        return _tDateLast;
    }

    /**
     * set the date of the last submit
     * @param end  the date of the last submit
     */
    public void setDateLast( Timestamp end )
    {
        _tDateLast = end;
    }

    /**
    *
    * @return true if the filter contain the date of the last submit
    */
    public boolean containsDateLast(  )
    {
        return ( _tDateLast != null );
    }

    /**
    * return the list of sort
    * @return the list of sort
    */
    public List<Integer> getSortBy(  )
    {
        return _listSortBy;
    }

    /**
     * set the list of sort
     * @param listSortBy the list of sort
     */
    public void setSortBy( List<Integer> listSortBy )
    {
        _listSortBy = listSortBy;
    }

    /**
    *
    * @return true if the filter contain the list of sort
    */
    public boolean containsSortBy(  )
    {
        return ( ( _listSortBy != null ) && ( _listSortBy.size(  ) != 0 ) );
    }

    /**
     * convert a int to a boolean
     * @param nIdBoolean the id to convert
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
     * @return  the id of the category insert in the filter
     */
    public int getIdCategory(  )
    {
        return _nIdCategory;
    }

    /**
     * set  the id of the category in the filter
     * @param idCategory the id of the category to insert in the filter
     */
    public void setIdCategory( int idCategory )
    {
        _nIdCategory = idCategory;
    }

    /**
     *
     * @return true if the filter contain an id of category
     */
    public boolean containsIdCategory(  )
    {
        return ( _nIdCategory != ALL_INT );
    }

    /**
    *
    * @return 1 if the diggs return must be reported
    *         0 if the diggss return must not  be reported
    */
    public int getIdReported(  )
    {
        return _nIdReported;
    }

    /**
     * set   1 if the diggs return must be reported
     *       0 if the diggss return must not  be reported
     * @param idReported idReported
     */
    public void setIdReported( int idReported )
    {
        _nIdReported = idReported;
    }

    /**
     *
     * @return true if the filter contain reported
     */
    public boolean containsIdReported(  )
    {
        return ( _nIdReported != ALL_INT );
    }

    /**
          *
          * @return the number of vote in the filter
          */
    public int getNumberVote(  )
    {
        return _nNumberVote;
    }

    /**
     * set the number of vote in the filter
     * @param numberVote the number of vote
     */
    public void setNumberVote( int numberVote )
    {
        _nNumberVote = numberVote;
    }

    /**
    *
    * @return true if the filter contain a number of vote
    */
    public boolean containsNumberVote(  )
    {
        return ( _nNumberVote != ALL_INT );
    }

    /**
     * @param strLuteceUserKey the _strLuteceUserKey to set
     */
    public void setLuteceUserKey( String strLuteceUserKey )
    {
        this._strLuteceUserKey = strLuteceUserKey;
    }

    /**
     * @return the _strLuteceUserKey
     */
    public String getLuteceUserKey(  )
    {
        return _strLuteceUserKey;
    }

    /**
    *
    * @return true if the filter contain a number of vote
    */
    public boolean containsLuteceUserKey(  )
    {
        return ( _strLuteceUserKey != null );
    }
}
