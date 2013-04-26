/*
 * Copyright (c) 2002-2012, Mairie de Paris
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

import fr.paris.lutece.portal.service.image.ImageResource;


/**
 *
 * class Response
 *
 */
public class Response
{
    private int _nIdResponse;
    private String _strValueResponse;
    private IEntry _entry;
    private DiggSubmit _diggSubmit;
    private ImageResource _image;

    /**
     *
     * @return the digg submit of the response
     */
    public DiggSubmit getDiggSubmit(  )
    {
        return _diggSubmit;
    }

    /**
     * set the digg submit of the response
     * @param diggSubmit the digg submit of the response
     */
    public void setDiggSubmit( DiggSubmit diggSubmit )
    {
        _diggSubmit = diggSubmit;
    }

    /**
    *
    * @return the entry associate to the response
    */
    public IEntry getEntry(  )
    {
        return _entry;
    }

    /**
     * set the entry associate to the response
     * @param entry the entry associate to the response
     */
    public void setEntry( IEntry entry )
    {
        _entry = entry;
    }

    /**
     *
     * @return the id of the response
     */
    public int getIdResponse(  )
    {
        return _nIdResponse;
    }

    /**
     * set the id of the response
     * @param idResponse the id of the response
     */
    public void setIdResponse( int idResponse )
    {
        _nIdResponse = idResponse;
    }

    /**
     *
     * @return the value of the response
     */
    public String getValueResponse(  )
    {
        return _strValueResponse;
    }

    /**
     * set the value of the response
     * @param strValueResponse Value of the response
     */
    public void setValueResponse( String strValueResponse )
    {
        _strValueResponse = strValueResponse;
    }

    /**
     * get Image
     * @return Image Resource
     */
	public ImageResource getImage() {
		return _image;
	}
	/**
	 * set Image Resource
	 * @param image  Image Resource
	 */
	public void setImage(ImageResource image) {
		this._image = image;
	}
}
