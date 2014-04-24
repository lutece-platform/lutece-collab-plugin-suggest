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
package fr.paris.lutece.plugins.digglike.business;


/**
 *
 * class vote type
 *
 */
public class VideoType
{
    private int _nIdDiggSubmit;
    private String _strCredits;
    private String _strMimeType;
    private byte[] _video;

    /**
         *
         * @return the id of the video (or diggsubmit)
         */
    public int getIdDiggSubmit(  )
    {
        return _nIdDiggSubmit;
    }

    /**
     * set the id of the DiggSubmit
     * @param nidDiggSubmit the id of the vote type
     */
    public void setIdDiggSubmit( int nidDiggSubmit )
    {
        _nIdDiggSubmit = nidDiggSubmit;
    }

    /**
     *
     * @return the credits of the video
     */
    public String getCredits(  )
    {
        return _strCredits;
    }

    /**
     * set the credits of the video
     * @param strCredits the credits of the video
     */
    public void setCredits( String strCredits )
    {
        _strCredits = strCredits;
    }

    /**
     * @param strMimeType the _strMimeType to set
     */
    public void setMimeType( String strMimeType )
    {
        this._strMimeType = strMimeType;
    }

    /**
     * @return the _strMimeType
     */
    public String getMimeType(  )
    {
        return _strMimeType;
    }

    /**
     * @param video the _video to set
     */
    public void setVideo( byte[] video )
    {
        this._video = video;
    }

    /**
     * @return the _video
     */
    public byte[] getVideo(  )
    {
        return _video;
    }
}
