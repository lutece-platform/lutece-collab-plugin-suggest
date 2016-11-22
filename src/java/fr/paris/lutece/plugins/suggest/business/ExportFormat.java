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

import fr.paris.lutece.portal.service.rbac.RBACResource;


/**
 *
 *  class ExportFormat
 *
 */
public class ExportFormat implements RBACResource
{
    public static final String RESOURCE_TYPE = "SUGGEST_EXPORT_FORMAT_TYPE";
    private int _nIdExport;
    private String _strTitle;
    private String _strDescription;
    private String _strExtension;
    private byte[] _xsl;

    /**
     *
     * @return the id of the export format
     */
    public int getIdExport(  )
    {
        return _nIdExport;
    }

    /**
     * set  the id of the export format
     * @param idExport  the id of the export format
     */
    public void setIdExport( int idExport )
    {
        _nIdExport = idExport;
    }

    /**
     *
     * @return the description of the export format
     */
    public String getDescription(  )
    {
        return _strDescription;
    }

    /**
     * set the description of the export format
     * @param description the description of the export format
     */
    public void setDescription( String description )
    {
        _strDescription = description;
    }

    /**
     *
     * @return the title of the export format
     */
    public String getTitle(  )
    {
        return _strTitle;
    }

    /**
     * set the title of the export format
     * @param title the title of the export format
     */
    public void setTitle( String title )
    {
        _strTitle = title;
    }

    /**
     *
     * @return the xsl file of the export format
     */
    public byte[] getXsl(  )
    {
        return _xsl;
    }

    /**
     * set the xsl file of the export format
     * @param donnees the xsl file of the export format
     */
    public void setXsl( byte[] donnees )
    {
        _xsl = donnees;
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
        return "" + _nIdExport;
    }

    /**
     * get the extension of the output file generate by the xsl
     * @return the extension of the out put file
     */
    public String getExtension(  )
    {
        return _strExtension;
    }

    /**
     * set the extension of the output file generate by the xsl
     * @param extension the extension of the out put file
     */
    public void setExtension( String extension )
    {
        _strExtension = extension;
    }
}
