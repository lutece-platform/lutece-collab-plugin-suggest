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

import fr.paris.lutece.portal.service.rbac.RBACResource;

/**
 *
 * class DefaultMessage
 *
 */
public class DefaultMessage implements RBACResource
{
    public static final String RESOURCE_TYPE = "SUGGEST_DEFAULT_MESSAGE_TYPE";
    private String _strUnavailabilityMessage;
    private String _strLibelleValidateButton;
    private String _strLibelleContribution;
    private int _nNumberSuggestSubmitInTopScore = -1;
    private int _nNumberSuggestSubmitInTopComment = -1;
    private int _nNumberSuggestSubmitCaractersShown = -1;
    private String _strNotificationNewCommentTitle;
    private String _strNotificationNewCommentBody;
    private String _strNotificationNewSuggestSubmitTitle;
    private String _strNotificationNewSuggestSubmitBody;

    /**
     * 
     * @return getNotificationNewCommentTitle
     */
    public String getNotificationNewCommentTitle( )
    {
        return _strNotificationNewCommentTitle;
    }

    /**
     * 
     * @param strNotificationNewCommentTitle
     *            NotificationNewCommentTitle
     */
    public void setNotificationNewCommentTitle( String strNotificationNewCommentTitle )
    {
        this._strNotificationNewCommentTitle = strNotificationNewCommentTitle;
    }

    /**
     * 
     * @return NotificationNewCommentBody
     */
    public String getNotificationNewCommentBody( )
    {
        return _strNotificationNewCommentBody;
    }

    /**
     * 
     * @param strNotificationNewCommentBody
     *            NotificationNewCommentBody
     */
    public void setNotificationNewCommentBody( String strNotificationNewCommentBody )
    {
        this._strNotificationNewCommentBody = strNotificationNewCommentBody;
    }

    /**
     * 
     * @return NotificationNewSuggestSubmitTitle
     */
    public String getNotificationNewSuggestSubmitTitle( )
    {
        return _strNotificationNewSuggestSubmitTitle;
    }

    /**
     * 
     * @param strNotificationNewSuggestSubmitTitle
     *            NotificationNewSuggestSubmitTitle
     */
    public void setNotificationNewSuggestSubmitTitle( String strNotificationNewSuggestSubmitTitle )
    {
        this._strNotificationNewSuggestSubmitTitle = strNotificationNewSuggestSubmitTitle;
    }

    /**
     * 
     * @return NotificationSuggestSubmitBody
     */
    public String getNotificationNewSuggestSubmitBody( )
    {
        return _strNotificationNewSuggestSubmitBody;
    }

    /**
     * 
     * @param strNotificationSuggestSubmitBody
     *            NotificationSuggestSubmitBody
     */
    public void setNotificationNewSuggestSubmitBody( String strNotificationSuggestSubmitBody )
    {
        this._strNotificationNewSuggestSubmitBody = strNotificationSuggestSubmitBody;
    }

    /**
     *
     * @return the default value of validate button
     */
    public String getLibelleValidateButton( )
    {
        return _strLibelleValidateButton;
    }

    /**
     * set the default value of validate button
     * 
     * @param libelleValidateButton
     *            value of validate button
     */
    public void setLibelleValidateButton( String libelleValidateButton )
    {
        _strLibelleValidateButton = libelleValidateButton;
    }

    /**
     *
     * @return the default Unavailability Message who see by the user when the form will be enable
     */
    public String getUnavailabilityMessage( )
    {
        return _strUnavailabilityMessage;
    }

    /**
     * set the Unavailability Message who see by the user when the form will be enable
     * 
     * @param unavailabilityMessage
     *            the default Unavailability Message
     */
    public void setUnavailabilityMessage( String unavailabilityMessage )
    {
        _strUnavailabilityMessage = unavailabilityMessage;
    }

    /**
     * RBAC resource implmentation
     * 
     * @return The resource type code
     */
    public String getResourceTypeCode( )
    {
        return RESOURCE_TYPE;
    }

    /**
     * RBAC resource implmentation
     * 
     * @return The resourceId
     */
    public String getResourceId( )
    {
        return "";
    }

    /**
     *
     * @return the default libelle of suggest contribution
     */
    public String getLibelleContribution( )
    {
        return _strLibelleContribution;
    }

    /**
     * set the default libelle of suggest contribution
     * 
     * @param libelleSuggestName
     *            the default libelle of suggest contribution
     */
    public void setLibelleContribution( String libelleSuggestName )
    {
        _strLibelleContribution = libelleSuggestName;
    }

    /**
     * return the number of suggest submit display in the list of top comment
     * 
     * @return the number of suggest submit display in the list of top comment
     */
    public int getNumberSuggestSubmitInTopComment( )
    {
        return _nNumberSuggestSubmitInTopComment;
    }

    /**
     * Set the number of suggest submit display in the list of top comment
     * 
     * @param numberTopComment
     *            the number of suggest submit in the list of top comment
     */
    public void setNumberSuggestSubmitInTopComment( int numberTopComment )
    {
        _nNumberSuggestSubmitInTopComment = numberTopComment;
    }

    /**
     * return the number of suggest submit display in the list of top score
     * 
     * @return the number of suggest submit display in the list of top score
     */
    public int getNumberSuggestSubmitInTopScore( )
    {
        return _nNumberSuggestSubmitInTopScore;
    }

    /**
     * Set the number of comment display in the list of top score
     * 
     * @param numberTopScore
     *            the number of comment display in the list of top score
     */
    public void setNumberSuggestSubmitInTopScore( int numberTopScore )
    {
        _nNumberSuggestSubmitInTopScore = numberTopScore;
    }

    /**
     * return the nunber of caracters shown in the list of suggest submit
     * 
     * @return the nunber of caracters shown in the list of suggest submit
     */
    public int getNumberSuggestSubmitCaractersShown( )
    {
        return _nNumberSuggestSubmitCaractersShown;
    }

    /**
     * set the nunber of caracters shown in the list of suggest submit
     * 
     * @param numberCaractersShown
     *            the nunber of caracters shown in the list of suggest submit
     */
    public void setNumberSuggestSubmitCaractersShown( int numberCaractersShown )
    {
        _nNumberSuggestSubmitCaractersShown = numberCaractersShown;
    }
}
