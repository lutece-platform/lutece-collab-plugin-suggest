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

/**
 *
 * class FormError
 *
 */
public class FormError
{
    private String _strTitleQuestion;
    private String _strErrorMessage;
    private boolean _bMandatoryError;

    /**
     * return true if the error is a mandatory error
     * 
     * @return true if the error is a mandatory error
     */
    public boolean isMandatoryError( )
    {
        return _bMandatoryError;
    }

    /**
     * set true if the error is a mandatory error
     * 
     * @param mandatoryError
     *            true if the error is a mandatory error
     */
    public void setMandatoryError( boolean mandatoryError )
    {
        _bMandatoryError = mandatoryError;
    }

    /**
     * Gets the error Message
     * 
     * @return the error Message
     */
    public String getErrorMessage( )
    {
        return _strErrorMessage;
    }

    /**
     * set the error message
     * 
     * @param errorMessage
     *            the erroer message
     */
    public void setErrorMessage( String errorMessage )
    {
        _strErrorMessage = errorMessage;
    }

    /**
     *
     * @return the title of the mandatory question
     */
    public String getTitleQuestion( )
    {
        return _strTitleQuestion;
    }

    /**
     * set the title of the mandatory question
     * 
     * @param titleMandatoryQuestion
     *            the title of the mandatory question
     */
    public void setTitleQuestion( String titleMandatoryQuestion )
    {
        _strTitleQuestion = titleMandatoryQuestion;
    }
}
