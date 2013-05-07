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
package fr.paris.lutece.plugins.digglike.service.digglikesearch;

import fr.paris.lutece.portal.service.search.SearchItem;

import org.apache.lucene.document.Document;


/**
 * DigglikeSearchItem
 */
public class DigglikeSearchItem extends SearchItem
{
    public static final String FIELD_ID_DIGG = "id_digg";
    public static final String FIELD_ID_DIGG_SUBMIT = "id_digg_submit";

    // Variables declarations
    private String _strIdDigg;
    private String _strIdDiggSubmit;

    /**
     * @param document a document
     */
    public DigglikeSearchItem( Document document )
    {
        super( document );
        setIdDigg( document.get( FIELD_ID_DIGG ) );
        setIdDiggSubmit( document.get( FIELD_ID_DIGG_SUBMIT ) );
    }

    /**
     * @return the id of the digg
     */
    public String getIdDigg(  )
    {
        return _strIdDigg;
    }

    /**
     * @param idDigg the id of the digg
     */
    public void setIdDigg( String idDigg )
    {
        _strIdDigg = idDigg;
    }

    /**
     * Id digg submit
     * @return the digg submit id
     */
    public String getIdDiggSubmit(  )
    {
        return _strIdDiggSubmit;
    }

    /**
     * The the digg submit id
     * @param strIdDiggSubmit the new digg submit id
     */
    public void setIdDiggSubmit( String strIdDiggSubmit )
    {
        _strIdDiggSubmit = strIdDiggSubmit;
    }
}
