/*
 * Copyright (c) 2002-2025, City of Paris
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
import java.util.Locale;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * A file the author attached to a suggest submit, stored by the core file service.
 */
public class SuggestSubmitAttachment implements Serializable
{
    private static final long serialVersionUID = 1L;

    private int _nIdAttachment;
    private int _nIdSuggestSubmit;
    private int _nIdEntry;
    private String _strFileKey;
    private String _strFileName;
    private String _strMimeType;
    private long _lSize;

    /**
     * Returns the attachment id.
     *
     * @return the id
     */
    public int getIdAttachment( )
    {
        return _nIdAttachment;
    }

    /**
     * Sets the attachment id.
     *
     * @param nIdAttachment
     *            the id
     */
    public void setIdAttachment( int nIdAttachment )
    {
        _nIdAttachment = nIdAttachment;
    }

    /**
     * Returns the id of the suggest submit.
     *
     * @return the suggest submit id
     */
    public int getIdSuggestSubmit( )
    {
        return _nIdSuggestSubmit;
    }

    /**
     * Sets the id of the suggest submit.
     *
     * @param nIdSuggestSubmit
     *            the suggest submit id
     */
    public void setIdSuggestSubmit( int nIdSuggestSubmit )
    {
        _nIdSuggestSubmit = nIdSuggestSubmit;
    }

    /**
     * Returns the id of the file field the file was attached with.
     *
     * @return the entry id
     */
    public int getIdEntry( )
    {
        return _nIdEntry;
    }

    /**
     * Sets the id of the file field the file was attached with.
     *
     * @param nIdEntry
     *            the entry id
     */
    public void setIdEntry( int nIdEntry )
    {
        _nIdEntry = nIdEntry;
    }

    /**
     * Returns the key of the file in the core file service.
     *
     * @return the file key
     */
    public String getFileKey( )
    {
        return _strFileKey;
    }

    /**
     * Sets the key of the file in the core file service.
     *
     * @param strFileKey
     *            the file key
     */
    public void setFileKey( String strFileKey )
    {
        _strFileKey = strFileKey;
    }

    /**
     * Returns the file name.
     *
     * @return the file name
     */
    public String getFileName( )
    {
        return _strFileName;
    }

    /**
     * Sets the file name.
     *
     * @param strFileName
     *            the file name
     */
    public void setFileName( String strFileName )
    {
        _strFileName = strFileName;
    }

    /**
     * Returns the MIME type.
     *
     * @return the MIME type
     */
    public String getMimeType( )
    {
        return _strMimeType;
    }

    /**
     * Sets the MIME type.
     *
     * @param strMimeType
     *            the MIME type
     */
    public void setMimeType( String strMimeType )
    {
        _strMimeType = strMimeType;
    }

    /**
     * Returns the size in bytes.
     *
     * @return the size
     */
    public long getSize( )
    {
        return _lSize;
    }

    /**
     * Sets the size in bytes.
     *
     * @param lSize
     *            the size
     */
    public void setSize( long lSize )
    {
        _lSize = lSize;
    }

    /**
     * Returns the extension of the file name, upper case, for display.
     *
     * @return the extension, or an empty string
     */
    public String getExtension( )
    {
        return StringUtils.upperCase( StringUtils.defaultString( FilenameUtils.getExtension( _strFileName ) ), Locale.ROOT );
    }
}
