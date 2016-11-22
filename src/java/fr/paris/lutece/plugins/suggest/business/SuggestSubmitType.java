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

import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.rbac.RBACResource;

import java.io.Serializable;

/**
 *
 * class SuggestSubmit
 *
 */
public class SuggestSubmitType implements RBACResource, Serializable
{
    private static final long serialVersionUID = 6012149058400580840L;
    public static final String RESOURCE_TYPE = "SUGGEST_SUGGEST_SUBMIT_TYPE";
    private int _nIdType;
    private String _strName;
    private String _strColor;
    private ImageResource _imgPictogram;
    private Boolean _bParameterizableInFO;
    private int _nIdXSLStyleSheet;
    private Integer _nIdImageResource;

    /**
     * @param nIdType the _nIdType to set
     */
    public void setIdType( int nIdType )
    {
        this._nIdType = nIdType;
    }

    /**
     * @return the _nIdType
     */
    public int getIdType(  )
    {
        return _nIdType;
    }

    /**
     * @param strName the _strName to set
     */
    public void setName( String strName )
    {
        this._strName = strName;
    }

    /**
     * @return the _strName
     */
    public String getName(  )
    {
        return _strName;
    }

    /**
     * @param strColor the _strColor to set
     */
    public void setColor( String strColor )
    {
        this._strColor = strColor;
    }

    /**
     * @return the _strColor
     */
    public String getColor(  )
    {
        return _strColor;
    }

    /**
     * @param imgPictogram the _imgPictogram to set
     */
    public void setPictogram( ImageResource imgPictogram )
    {
        this._imgPictogram = imgPictogram;
    }

    /**
     * @return the _imgPictogram
     */
    public ImageResource getPictogram(  )
    {
        return _imgPictogram;
    }

    /**
     * @param bParameterizableInFO the _bParameterizableInFO to set
     */
    public void setParameterizableInFO( Boolean bParameterizableInFO )
    {
        this._bParameterizableInFO = bParameterizableInFO;
    }

    /**
     * @return the _bParameterizableInFO
     */
    public Boolean getParameterizableInFO(  )
    {
        return _bParameterizableInFO;
    }

    /**
    * RBAC resource implmentation
    * @return The resource type code
    */
    public String getResourceTypeCode(  )
    {
        return RESOURCE_TYPE;
    }

    /**
     * RBAC resource implmentation
     * @return The resourceId
     */
    public String getResourceId(  )
    {
        return "" + _nIdType;
    }

    /**
     * @param idXSLStyleSheet the idXSLStyleSheet to set
     */
    public void setIdXSLStyleSheet( int idXSLStyleSheet )
    {
        this._nIdXSLStyleSheet = idXSLStyleSheet;
    }

    /**
     * @return the idXSLStyleSheet
     */
    public int getIdXSLStyleSheet(  )
    {
        return _nIdXSLStyleSheet;
    }

    /**
     * @param obj the Suggest Submit Type  to compare
     * @return true if Suggest Submit Type  in parameter is the same object
     *
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof SuggestSubmitType && ( (SuggestSubmitType) obj ).getIdType( ) == _nIdType )
        {
            return true;
        }

        return false;
    }

    /**
     * the image resource id associate to the response
     * @return Resource Image
     */
    public Integer getIdImageResource(  )
    {
        return _nIdImageResource;
    }

    /**
     * image resource id associate to the response
     * @param idImageResource image resource id associate to the response
     */
    public void setIdImageResource( Integer idImageResource )
    {
        _nIdImageResource = idImageResource;
    }
}
