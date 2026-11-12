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
package fr.paris.lutece.plugins.suggest.business.statistics;

import java.io.Serializable;
import java.util.Date;

/**
 * A count of suggest submits under a label: a department, or a period starting on a date.
 */
public class StatisticsCount implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final String _strLabel;
    private final Date _dateStart;
    private final int _nCount;

    /**
     * Creates a count under a label
     *
     * @param strLabel
     *            the label
     * @param dateStart
     *            the first day of the period counted, or null when the label is not a period
     * @param nCount
     *            the number of suggest submits
     */
    public StatisticsCount( String strLabel, Date dateStart, int nCount )
    {
        _strLabel = strLabel;
        _dateStart = dateStart;
        _nCount = nCount;
    }

    /**
     * Returns the label
     *
     * @return the label
     */
    public String getLabel( )
    {
        return _strLabel;
    }

    /**
     * Returns the first day of the period counted
     *
     * @return the first day, or null when the label is not a period
     */
    public Date getDateStart( )
    {
        return _dateStart;
    }

    /**
     * Returns the number of suggest submits
     *
     * @return the number of suggest submits
     */
    public int getCount( )
    {
        return _nCount;
    }
}
