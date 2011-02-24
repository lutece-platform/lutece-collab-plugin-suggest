/*
 * Copyright (c) 2002-2010, Mairie de Paris
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

import fr.paris.lutece.portal.service.rbac.RBACResource;


/**
 *
 *class DefaultMessage
 *
 */
public class DefaultMessage implements RBACResource
{
    public static final String RESOURCE_TYPE = "DIGGLIKE_DEFAULT_MESSAGE_TYPE";
    private String _strUnavailabilityMessage;
    private String _strLibelleValidateButton;
    private String _strLibelleContribution;
    private int _nNumberDiggSubmitInTopScore = -1;
    private int _nNumberDiggSubmitInTopComment = -1;
    private int _nNumberDiggSubmitCaractersShown = -1;

    /**
     *
     * @return the default value of validate button
     */
    public String getLibelleValidateButton(  )
    {
        return _strLibelleValidateButton;
    }

    /**
     * set the default value of validate button
     * @param libelleValidateButton value of validate button
     */
    public void setLibelleValidateButton( String libelleValidateButton )
    {
        _strLibelleValidateButton = libelleValidateButton;
    }

    /**
     *
     * @return the default Unavailability Message who see by the user when the form will be enable
     */
    public String getUnavailabilityMessage(  )
    {
        return _strUnavailabilityMessage;
    }

    /**
     * set the Unavailability Message who see by the user when the form will be enable
     * @param unavailabilityMessage the default Unavailability Message
     */
    public void setUnavailabilityMessage( String unavailabilityMessage )
    {
        _strUnavailabilityMessage = unavailabilityMessage;
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
        return "";
    }

    /**
    *
    * @return the default libelle of  digg contribution
    */
    public String getLibelleContribution(  )
    {
        return _strLibelleContribution;
    }

    /**
     * set the default libelle of  digg contribution
     * @param libelleDiggName  the default libelle of  digg contribution
     */
    public void setLibelleContribution( String libelleDiggName )
    {
        _strLibelleContribution = libelleDiggName;
    }

    /**
     * return the number of digg submit display in the list of top comment
     * @return the number of digg submit display in the list of top comment
     */
    public int getNumberDiggSubmitInTopComment(  )
    {
        return _nNumberDiggSubmitInTopComment;
    }

    /**
     * Set the number of digg submit display in the list of top comment
     * @param numberTopComment the number of  digg submit in the list of top comment
     */
    public void setNumberDiggSubmitInTopComment( int numberTopComment )
    {
        _nNumberDiggSubmitInTopComment = numberTopComment;
    }

    /**
    * return the number of  digg submit display in the list of top score
    * @return the number of digg submit  display in the list of top score
    */
    public int getNumberDiggSubmitInTopScore(  )
    {
        return _nNumberDiggSubmitInTopScore;
    }

    /**
     * Set the number of comment display in the list of top score
     * @param numberTopScore the number of comment display in the list of top score
     */
    public void setNumberDiggSubmitInTopScore( int numberTopScore )
    {
        _nNumberDiggSubmitInTopScore = numberTopScore;
    }

    /**
        * return the nunber of caracters shown in the list of digg submit
        * @return the nunber of caracters shown in the list of digg submit
        */
    public int getNumberDiggSubmitCaractersShown(  )
    {
        return _nNumberDiggSubmitCaractersShown;
    }

    /**
     * set the nunber of caracters shown in the list of digg submit
     * @param numberCaractersShown the nunber of caracters shown in the list of digg submit
     */
    public void setNumberDiggSubmitCaractersShown( int numberCaractersShown )
    {
        _nNumberDiggSubmitCaractersShown = numberCaractersShown;
    }
}
