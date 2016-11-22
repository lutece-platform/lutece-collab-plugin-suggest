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


/**
 *
 * class voteButton
 *
 */
public class VoteButton
{
    private int _nIdVoteButton;
    private String _strTitle;
    private String _strValue;
    private String _strIconMimeType;
    private byte[] _byIconContent;

    /**
       *
       * @return the id of the vote button
       */
    public int getIdIconButton(  )
    {
        return _nIdVoteButton;
    }

    /**
     * set the id of the vote button
     * @param idVoteButton the id of the vote button
     */
    public void setIdVoteButton( int idVoteButton )
    {
        _nIdVoteButton = idVoteButton;
    }

    /**
     *
     * @return the title of the vote button
     */
    public String getTitle(  )
    {
        return _strTitle;
    }

    /**
     * set the title of the vote button
     * @param title the title of the vote button
     */
    public void setTitle( String title )
    {
        _strTitle = title;
    }

    /**
     *
     * @return the value of the vote button
     */
    public String getValue(  )
    {
        return _strValue;
    }

    /**
     * set the value of the vote button
     * @param value the value of the vote value
     */
    public void setValue( String value )
    {
        _strValue = value;
    }

    /**
     *
     * @return the icon MimeType
     */
    public String getIconMimeType(  )
    {
        return _strIconMimeType;
    }

    /**
     * set the icon MimeType
     * @param iconMimeType the icon MimeType
     */
    public void setIconMimeType( String iconMimeType )
    {
        _strIconMimeType = iconMimeType;
    }

    /**
     *
     * @return the icon content
     */
    public byte[] getIconContent(  )
    {
        return _byIconContent;
    }

    /**
     * set the icon content
     * @param iconContent the icon content
     */
    public void setIconContent( byte[] iconContent )
    {
        _byIconContent = iconContent;
    }
}
