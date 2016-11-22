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
 * class SuggestFilter
 *
 */
public class SuggestFilter
{
    public static final String ALL_STRING = "all";
    public static final int ALL_INT = -1;
    public static final int ID_TRUE = 1;
    public static final int ID_FALSE = 0;
    private int _nIdState = ALL_INT;
    private int _nIdDefaultSuggest = ALL_INT;
    private String _strWorkgroup = ALL_STRING;
    private String _strRole = Suggest.ROLE_NONE;

    /**
     * @return the _strRole
     */
    public String getRole(  )
    {
        return _strRole;
    }

    /**
     * @param strRole the _strRole to set
     */
    public void setRole( String strRole )
    {
        _strRole = strRole;
    }

    /**
    *
    * @return 1 if the suggests return must be enabled
    *                    0 if the suggestss return must be disabled
    */
    public int getIdState(  )
    {
        return _nIdState;
    }

    /**
     * Set 1 if the  suggests return must be enabled
     *            0 if the  suggests return must be disabled
     * @param idState  1 if the  suggests return must be enabled
     *                                      0 if the  suggests return must be disabled
     */
    public void setIdState( int idState )
    {
        _nIdState = idState;
    }

    /**
     *
     * @return true if the filter contain suggest state
     */
    public boolean containsIdState(  )
    {
        return ( _nIdState != ALL_INT );
    }

    /**
         *
         * @return the workgroup of the search suggests
         */
    public String getWorkgroup(  )
    {
        return _strWorkgroup;
    }

    /**
     * set the workgroup of the search suggests
     * @param workgroup the workgroup of the search suggests
     */
    public void setWorkGroup( String workgroup )
    {
        _strWorkgroup = workgroup;
    }

    /**
     *
     * @return true if the filter contain workgroup criteria
     */
    public boolean containsWorkgroupCriteria(  )
    {
        return ( !_strWorkgroup.equals( ALL_STRING ) );
    }

    /**
    *
    * @return true if the filter contain role criteria
    */
    public boolean containsRoleCriteria(  )
    {
        return ( !_strRole.equals( Suggest.ROLE_NONE ) );
    }

    /**
    *
    * @return 1 if the suggests return must be a default suggest
    *         0 if the suggests return must no be a default suggest
    */
    public int getIdDefaultSuggest(  )
    {
        return _nIdDefaultSuggest;
    }

    /**
     * Set 1 if the suggests return must be a default suggest
    *         0 if the suggests return must no be a default suggest
     * @param idDefaultSuggest  1 if the  suggests return must be a default suggest
     *                                      0 if the  suggests return must not be a default suggest
     */
    public void setIdDefaultSuggest( int idDefaultSuggest )
    {
        _nIdDefaultSuggest = idDefaultSuggest;
    }

    /**
     *
     * @return true if the filter contain suggest state
     */
    public boolean containsIdDefaultSuggest(  )
    {
        return ( _nIdDefaultSuggest != ALL_INT );
    }
}
