/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import java.io.Serializable;


public class SearchFields implements Serializable
{
    private static final long serialVersionUID = 2512978426472997213L;
    private String _strQuery;
    private String _strLuteceUserName;
    private String _strPageIndex;
    private int _nIdFilterPeriod = SubmitFilter.ALL_INT;
    private int _nIdSuggestSubmitSort = SubmitFilter.ALL_INT;
    private int _nIdFilterCategory = SubmitFilter.ALL_INT;
    private int _nIdFilterSuggestSubmitType = SubmitFilter.ALL_INT;

    public String getQuery(  )
    {
        return _strQuery;
    }

    public void setQuery( String strQuery )
    {
        this._strQuery = strQuery;
    }

    public int getIdFilterPeriod(  )
    {
        return _nIdFilterPeriod;
    }

    public void setIdFilterPeriod( int nIdFilterPeriod )
    {
        this._nIdFilterPeriod = nIdFilterPeriod;
    }

    public int getIdSuggestSubmitSort(  )
    {
        return _nIdSuggestSubmitSort;
    }

    public void setIdSuggestSubmitSort( int nIdSuggestSubmitSort )
    {
        this._nIdSuggestSubmitSort = nIdSuggestSubmitSort;
    }

    public int getIdFilterCategory(  )
    {
        return _nIdFilterCategory;
    }

    public void setIdFilterCategory( int nIdFilterCategory )
    {
        this._nIdFilterCategory = nIdFilterCategory;
    }

    public String getPageIndex(  )
    {
        return _strPageIndex;
    }

    public void setPageIndex( String strPageIndex )
    {
        this._strPageIndex = strPageIndex;
    }

    public int getIdFilterSuggestSubmitType(  )
    {
        return _nIdFilterSuggestSubmitType;
    }

    public void setIdFilterSuggestSubmitType( int nIdFilterSuggestSubmitType )
    {
        this._nIdFilterSuggestSubmitType = nIdFilterSuggestSubmitType;
    }

	public String getLuteceUserName() {
		return _strLuteceUserName;
	}

	public void setLuteceUserName(String _strLuteceUserName) {
		this._strLuteceUserName = _strLuteceUserName;
	}
}
