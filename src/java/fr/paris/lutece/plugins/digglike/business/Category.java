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
package fr.paris.lutece.plugins.digglike.business;

import fr.paris.lutece.plugins.digglike.utils.DiggUtils;
import fr.paris.lutece.portal.service.rbac.RBACResource;


/**
 *
 * class category
 *
 */
public class Category implements RBACResource
{
    public static final String RESOURCE_TYPE = "DIGGLIKE_CATEGORY_TYPE";
    public static final int DEFAULT_ID_CATEGORY = -1;
    private int _nIdCategory;
    private String _strTitle;
    private String _strColor;

    /**
       *
       * @return the id of the category
       */
    public int getIdCategory(  )
    {
        return _nIdCategory;
    }

    /**
     * set the id of the category
     * @param idCategory the id of the category
     */
    public void setIdCategory( int idCategory )
    {
        _nIdCategory = idCategory;
    }

    /**
     *
     * @return the title of the category
     */
    public String getTitle(  )
    {
        return _strTitle;
    }

    /**
     * set the title of the category
     * @param title the title of the category
     */
    public void setTitle( String title )
    {
        _strTitle = title;
    }

    /**
     * @param obj the category to compare
     * @return true if category in parameter is the same category
     *
     */
    public boolean equals( Object obj )
    {
        if ( obj instanceof Category && ( (Category) obj ).getIdCategory( ) == _nIdCategory )
        {
            return true;
        }

        return false;
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
        return DiggUtils.EMPTY_STRING + _nIdCategory;
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
}
