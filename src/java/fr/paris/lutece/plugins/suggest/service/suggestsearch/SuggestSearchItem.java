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
package fr.paris.lutece.plugins.suggest.service.suggestsearch;

import fr.paris.lutece.portal.service.search.SearchItem;

import org.apache.lucene.document.Document;

/**
 * SuggestSearchItem
 */
public class SuggestSearchItem extends SearchItem
{
    public static final String FIELD_ID_SUGGEST = "id_suggest";
    public static final String FIELD_ID_SUGGEST_SUBMIT = "id_suggest_submit";

    // Variables declarations
    private String _strIdSuggest;
    private String _strIdSuggestSubmit;

    /**
     * @param document
     *            a document
     */
    public SuggestSearchItem( Document document )
    {
        super( document );
        setIdSuggest( document.get( FIELD_ID_SUGGEST ) );
        setIdSuggestSubmit( document.get( FIELD_ID_SUGGEST_SUBMIT ) );
    }

    /**
     * @return the id of the suggest
     */
    public String getIdSuggest( )
    {
        return _strIdSuggest;
    }

    /**
     * @param idSuggest
     *            the id of the suggest
     */
    public void setIdSuggest( String idSuggest )
    {
        _strIdSuggest = idSuggest;
    }

    /**
     * Id suggest submit
     * 
     * @return the suggest submit id
     */
    public String getIdSuggestSubmit( )
    {
        return _strIdSuggestSubmit;
    }

    /**
     * The the suggest submit id
     * 
     * @param strIdSuggestSubmit
     *            the new suggest submit id
     */
    public void setIdSuggestSubmit( String strIdSuggestSubmit )
    {
        _strIdSuggestSubmit = strIdSuggestSubmit;
    }
}
