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

/**
 *
 * class reportedMessage
 *
 */
public class ReportedMessage
{
    private int _nIdReported;
    private SuggestSubmit _suggestSubmit;
    private Timestamp _tDateReported;
    private String _strValue;

    /**
     *
     * @return the suggest submit of the reported
     */
    public SuggestSubmit getSuggestSubmit( )
    {
        return _suggestSubmit;
    }

    /**
     * set the suggest submit of the reported
     * 
     * @param suggestSubmit
     *            the suggest submit of the reported
     */
    public void setSuggestSubmit( SuggestSubmit suggestSubmit )
    {
        _suggestSubmit = suggestSubmit;
    }

    /**
     * return the reported date
     * 
     * @return the reported date
     */
    public Timestamp getDateReported( )
    {
        return _tDateReported;
    }

    /**
     * set the reported date
     * 
     * @param reportedDate
     *            the reported date
     */
    public void setDateReported( Timestamp reportedDate )
    {
        _tDateReported = reportedDate;
    }

    /**
     *
     * @return the value of the reported
     */
    public String getValue( )
    {
        return _strValue;
    }

    /**
     * set the value of the reported
     * 
     * @param strValue
     *            the value of the reported
     */
    public void setValue( String strValue )
    {
        _strValue = strValue;
    }

    /**
     *
     * @return the reported id
     */
    public int getIdReported( )
    {
        return _nIdReported;
    }

    /**
     * 
     * @param nIdReported
     *            the reported id
     */
    public void setIdReported( int nIdReported )
    {
        this._nIdReported = nIdReported;
    }
}
