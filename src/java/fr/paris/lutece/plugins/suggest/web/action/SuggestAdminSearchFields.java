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
package fr.paris.lutece.plugins.suggest.web.action;

import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Visualization of all needed session values. Many features depends on search result or paginator. Those fields may be required for actions.
 *
 */
public final class SuggestAdminSearchFields implements Serializable
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int _nIdSuggest;
    private int _nIdSuggestSumitState = SuggestUtils.CONSTANT_ID_NULL;
    private int _nIdSuggestSubmitSort = SuggestUtils.CONSTANT_ID_NULL;
    private int _nIdSuggestSubmitReport = SuggestUtils.CONSTANT_ID_NULL;
    private int _nIdCategory = SuggestUtils.CONSTANT_ID_NULL;
    private int _nIdType = SuggestUtils.CONSTANT_ID_NULL;
    private int _nIdSuggestSubmitContainsCommentDisable = SuggestUtils.CONSTANT_ID_NULL;
    private String _strQuery;
    private List<String> _selectedSuggestSubmit;

    public int getIdSuggestSubmitSort( )
    {
        return _nIdSuggestSubmitSort;
    }

    public void setIdSuggestSubmitSort( int nIdSuggestSubmitSort )
    {
        this._nIdSuggestSubmitSort = nIdSuggestSubmitSort;
    }

    public int getIdSuggestSubmitReport( )
    {
        return _nIdSuggestSubmitReport;
    }

    public void setIdSuggestSubmitReport( int nIdSuggestSubmitReport )
    {
        this._nIdSuggestSubmitReport = nIdSuggestSubmitReport;
    }

    public int getIdSuggestSumitState( )
    {
        return _nIdSuggestSumitState;
    }

    public void setIdSuggestSumitState( int nIdSuggestSumitState )
    {
        this._nIdSuggestSumitState = nIdSuggestSumitState;
    }

    public String getQuery( )
    {
        return _strQuery;
    }

    public void setQuery( String strQuery )
    {
        this._strQuery = strQuery;
    }

    public List<String> getSelectedSuggestSubmit( )
    {
        return _selectedSuggestSubmit;
    }

    public void setSelectedSuggestSubmit( List<String> selectedSuggestSubmit )
    {
        this._selectedSuggestSubmit = selectedSuggestSubmit;
    }

    public int getIdSuggest( )
    {
        return _nIdSuggest;
    }

    public void setIdSuggest( int nIdSuggest )
    {
        this._nIdSuggest = nIdSuggest;
    }

    public int getIdCategory( )
    {
        return _nIdCategory;
    }

    public void setIdCategory( int nIdCategory )
    {
        this._nIdCategory = nIdCategory;
    }

    public int getIdType( )
    {
        return _nIdType;
    }

    public void setIdType( int nIdType )
    {
        this._nIdType = nIdType;
    }

    public int getIdSuggestSubmitContainsCommentDisable( )
    {
        return _nIdSuggestSubmitContainsCommentDisable;
    }

    public void setIdSuggestSubmitContainsCommentDisable( int nIdSuggestSubmitContainsCommentDisable )
    {
        this._nIdSuggestSubmitContainsCommentDisable = nIdSuggestSubmitContainsCommentDisable;
    }
}
