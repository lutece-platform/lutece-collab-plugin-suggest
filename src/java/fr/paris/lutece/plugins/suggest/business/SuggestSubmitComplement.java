/*
 * Copyright (c) 2002-2026, City of Paris
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
import java.sql.Timestamp;

/**
 * A text the author adds below a published suggest submit.
 */
public class SuggestSubmitComplement implements Serializable
{
    private static final long serialVersionUID = 1L;

    private int _nIdComplement;
    private int _nIdSuggestSubmit;
    private Timestamp _dateComplement;
    private String _strValue;

    /**
     * Returns the complement id.
     *
     * @return the id
     */
    public int getIdComplement( )
    {
        return _nIdComplement;
    }

    /**
     * Sets the complement id.
     *
     * @param nIdComplement
     *            the id
     */
    public void setIdComplement( int nIdComplement )
    {
        _nIdComplement = nIdComplement;
    }

    /**
     * Returns the id of the completed suggest submit.
     *
     * @return the suggest submit id
     */
    public int getIdSuggestSubmit( )
    {
        return _nIdSuggestSubmit;
    }

    /**
     * Sets the id of the completed suggest submit.
     *
     * @param nIdSuggestSubmit
     *            the suggest submit id
     */
    public void setIdSuggestSubmit( int nIdSuggestSubmit )
    {
        _nIdSuggestSubmit = nIdSuggestSubmit;
    }

    /**
     * Returns the publication date.
     *
     * @return the date
     */
    public Timestamp getDateComplement( )
    {
        return _dateComplement;
    }

    /**
     * Sets the publication date.
     *
     * @param dateComplement
     *            the date
     */
    public void setDateComplement( Timestamp dateComplement )
    {
        _dateComplement = dateComplement;
    }

    /**
     * Returns the text.
     *
     * @return the text
     */
    public String getValue( )
    {
        return _strValue;
    }

    /**
     * Sets the text.
     *
     * @param strValue
     *            the text
     */
    public void setValue( String strValue )
    {
        _strValue = strValue;
    }
}
