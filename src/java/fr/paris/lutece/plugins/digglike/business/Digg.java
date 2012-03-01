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

import fr.paris.lutece.portal.service.rbac.RBACResource;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionRemovalListenerService;
import fr.paris.lutece.portal.service.role.RoleRemovalListenerService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupResource;
import fr.paris.lutece.portal.service.workgroup.WorkgroupRemovalListenerService;
import fr.paris.lutece.util.xml.XmlUtil;

import java.sql.Timestamp;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * Class Digg
 *
 */
public class Digg implements AdminWorkgroupResource, RBACResource
{
    public static final String RESOURCE_TYPE = "DIGGLIKE_DIGG_TYPE";
    public static final int STATE_ENABLE = 1;
    public static final int STATE_DISABLE = 0;
    public static final String ROLE_NONE = "none";
    private static final String TAG_DIGG = "digg";
    private static final String TAG_DIGG_TITLE = "digg-title";
    private static final String TAG_DIGGS_SUBMIT = "diggs-submit";
    private static final int SORT_BY_DATE = 0;
    private static final int SORT_BY_SCORE = 1;
    private static final int SORT_BY_VOTE = 2;
    private static final int SORT_MANUALLY = 3;
    private static DiggWorkgroupRemovalListener _listenerWorkgroup;
    private static DiggRegularExpressionRemovalListener _listenerRegularExpression;
    private static DiggRoleRemovalListener _listenerRole;
    private int _nIdDigg;
    private String _strTitle;
    private String _strUnavailabilityMessage;
    private String _strWorkgroup;
    private int _nIdMailingListDiggSubmit;
    private int _nIdMailingListNewDiggSubmit;
    private int _nIdMailingListComment;
    private int _nNumberVoteRequired = -1;
    private int _nNumberDayRequired = -1;
    private int _nNumberDiggSubmitInTopScore = -1;
    private int _nNumberDiggSubmitInTopComment = -1;
    private int _nNumberDiggSubmitCaractersShown = -1;
    private int _nNumberDiggSubmitPerPage = -1;
    private boolean _bActiveCaptcha;
    private boolean _bActiveDiggSubmitAuthentification;
    private boolean _bActiveVoteAuthentification;
    private boolean _bActiveCommentAuthentification;
    private boolean _bActiveDiggPropositionState;
    private boolean _bActiveDiggSubmitPaginator;
    private boolean _bAuthorizedComment;
    private boolean _bDisableNewDiggSubmit;
    private boolean _bEnableMailNewDiggSubmit;
    private boolean _bDisableNewComment;
    private boolean _nLimitNumberVote;
    private boolean _bShowCategoryBlock;
    private boolean _bShowTopScoreBlock;
    private boolean _bShowTopCommentBlock;
    private String _strLibelleValidateButton;
    private String _strLibelleContribution;
    private Timestamp _tDateCreation;
    private boolean _bActive;
    private List<DiggAction> _listActions;
    private VoteType _voteType;
    private List<IEntry> _listEntries;
    private List<Category> _listCategories;
    private List<DiggSubmit> _listDiggsSubmit;
    private String _strRole;
    private String _strHeader;
    private int _nSortField;
    private String _strCodeTheme;
    private String _strConfirmationMessage;

    /**
     * Initialize the Digg
     */
    public static void init(  )
    {
        // Create removal listeners and register them
        if ( _listenerWorkgroup == null )
        {
            _listenerWorkgroup = new DiggWorkgroupRemovalListener(  );
            WorkgroupRemovalListenerService.getService(  ).registerListener( _listenerWorkgroup );
        }

        if ( _listenerRegularExpression == null )
        {
            _listenerRegularExpression = new DiggRegularExpressionRemovalListener(  );
            RegularExpressionRemovalListenerService.getService(  ).registerListener( _listenerRegularExpression );
        }

        if ( _listenerRole == null )
        {
            _listenerRole = new DiggRoleRemovalListener(  );
            RoleRemovalListenerService.getService(  ).registerListener( _listenerRole );
        }
    }

    /**
     *
     * @return the id of the mailing list associate to the digg submit
     */
    public int getIdMailingListDiggSubmit(  )
    {
        return _nIdMailingListDiggSubmit;
    }

    /**
     * set the id of the mailing list associate to the digg submit
     * @param mailingListId the id of the mailing list associate to the digg submit
     */
    public void setIdMailingListDiggSubmit( int mailingListId )
    {
        _nIdMailingListDiggSubmit = mailingListId;
    }

    /**
    *
    * @return the id of the mailing list associate to the comment
    */
    public int getIdMailingListComment(  )
    {
        return _nIdMailingListComment;
    }

    /**
     * set the id of the mailing list associate to the comment
     * @param mailingListId the id of the mailing list associate to the comment submit
     */
    public void setIdMailingListComment( int mailingListId )
    {
        _nIdMailingListComment = mailingListId;
    }

    /**
     *
     * @return true if the digg for creating digg contain a captcha
     */
    public boolean isActiveCaptcha(  )
    {
        return _bActiveCaptcha;
    }

    /**
     * set true if the digg for creating digg contain a captcha
     * @param activeCaptcha true if the digg for creating digg  contain a captcha
     */
    public void setActiveCaptcha( boolean activeCaptcha )
    {
        _bActiveCaptcha = activeCaptcha;
    }

    /**
        *
        * @return the libelle of the validate button
        */
    public String getLibelleValidateButton(  )
    {
        return _strLibelleValidateButton;
    }

    /**
     * set the libelle of the validate button
     * @param libelleValidateButton the libelle of the validate button
     */
    public void setLibelleValidateButton( String libelleValidateButton )
    {
        _strLibelleValidateButton = libelleValidateButton;
    }

    /**
    *
    * @return the libelle of the digg contribution
    */
    public String getLibelleContribution(  )
    {
        return _strLibelleContribution;
    }

    /**
     * set the libelle of the digg name
     * @param libelleDiggName the libelle of the digg contribution
     */
    public void setLibelleContribution( String libelleDiggName )
    {
        _strLibelleContribution = libelleDiggName;
    }

    /**
     *
     * @return the title of the digg
     */
    public String getTitle(  )
    {
        return _strTitle;
    }

    /**
     * set the title of the digg
     * @param strTitle the title of the digg
     */
    public void setTitle( String strTitle )
    {
        this._strTitle = strTitle;
    }

    /**
     *
     * @return the unavailability message of the digg
     */
    public String getUnavailabilityMessage(  )
    {
        return _strUnavailabilityMessage;
    }

    /**
     * set the unavailability message of the digg
     * @param unavailabilityMessage the unavailability message of the digg
     */
    public void setUnavailabilityMessage( String unavailabilityMessage )
    {
        _strUnavailabilityMessage = unavailabilityMessage;
    }

    /**
     *
     * @return the work group associate to the digg
     */
    public String getWorkgroup(  )
    {
        return _strWorkgroup;
    }

    /**
     * set  the work group associate to the digg
     * @param workGroup  the work group associate to the digg
     */
    public void setWorkgroup( String workGroup )
    {
        _strWorkgroup = workGroup;
    }

    /**
     *
     * @return the id of the digg
     */
    public int getIdDigg(  )
    {
        return _nIdDigg;
    }

    /**
     * set the id of the digg
     * @param idDigg the id of the digg
     */
    public void setIdDigg( int idDigg )
    {
        _nIdDigg = idDigg;
    }

    /**
     *
     * @return true if the digg is active
     */
    public boolean isActive(  )
    {
        return _bActive;
    }

    /**
     * set true if the digg is active
     * @param active true if the digg is active
     */
    public void setActive( boolean active )
    {
        _bActive = active;
    }

    /**
     *
     * @return the creation date
     */
    public Timestamp getDateCreation(  )
    {
        return _tDateCreation;
    }

    /**
     * set the creation date
     * @param dateCreation the creation date
     */
    public void setDateCreation( Timestamp dateCreation )
    {
        _tDateCreation = dateCreation;
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
        return "" + _nIdDigg;
    }

    /**
     *
     * @return a list of action can be use for the digg
     */
    public List<DiggAction> getActions(  )
    {
        return _listActions;
    }

    /**
     * set a list of action can be use for the digg
     * @param diggActions a list of action must be use for the digg
     */
    public void setActions( List<DiggAction> diggActions )
    {
        _listActions = diggActions;
    }

    /**
     * return the number of vote required for publishing a digg submit
     * @return the number of vote required for publishing a digg submit
     */
    public int getNumberVoteRequired(  )
    {
        return _nNumberVoteRequired;
    }

    /**
     * Set the number of vote required for publishing a digg submit
     * @param numberVoteRequired the number of vote required for publishing a digg submit
      */
    public void setNumberVoteRequired( int numberVoteRequired )
    {
        _nNumberVoteRequired = numberVoteRequired;
    }

    /**
     * return the number of day required for disable a digg submit
     * @return  the number of day required for disable a digg submit
     */
    public int getNumberDayRequired(  )
    {
        return _nNumberDayRequired;
    }

    /**
     * set the number of day required for disable a digg submit
     * @param numberDayRequired the number of day required for disable a digg submit
     */
    public void setNumberDayRequired( int numberDayRequired )
    {
        _nNumberDayRequired = numberDayRequired;
    }

    /**
     * return true if the form of digg submit creation required a captcha
     * @return true if the form of digg submit creation required a captcha
     */
    public boolean isActiveDiggSubmitAuthentification(  )
    {
        return _bActiveDiggSubmitAuthentification;
    }

    /**
    *  set true if the form of digg submit creation required a captcha
    * @param activeDiggSubmitAuthentification true if the form of digg submit creation required a captcha
    */
    public void setActiveDiggSubmitAuthentification( boolean activeDiggSubmitAuthentification )
    {
        _bActiveDiggSubmitAuthentification = activeDiggSubmitAuthentification;
    }

    /**
     *
     * @return true if the vote on  digg submit required an authentification
     */
    public boolean isActiveVoteAuthentification(  )
    {
        return _bActiveVoteAuthentification;
    }

    /**
     * set true if the vote on  digg submit required an authentification
     * @param activeVoteAuthentification true if the vote on  digg submit required an authentification
     */
    public void setActiveVoteAuthentification( boolean activeVoteAuthentification )
    {
        _bActiveVoteAuthentification = activeVoteAuthentification;
    }

    /**
     *
     * @return true if the comment on  digg submit required an authentification
     */
    public boolean isActiveCommentAuthentification(  )
    {
        return _bActiveCommentAuthentification;
    }

    /**
     * set true if the comment on  digg submit required an authentification
     * @param activeCommentAuthentification true if the comment on  digg submit required an authentification
     */
    public void setActiveCommentAuthentification( boolean activeCommentAuthentification )
    {
        _bActiveCommentAuthentification = activeCommentAuthentification;
    }

    /**
     *
     * @return true if the new digg submit must be disable
     */
    public boolean isDisableNewDiggSubmit(  )
    {
        return _bDisableNewDiggSubmit;
    }

    /**
     * set true if the new digg submit must be disable
     * @param disableNewDiggSubmit true if the new digg submit must be disable
     */
    public void setDisableNewDiggSubmit( boolean disableNewDiggSubmit )
    {
        _bDisableNewDiggSubmit = disableNewDiggSubmit;
    }

    /**
         *
         * @return true if a mail must be sent when the new digg is submitted
         */
    public boolean isEnableMailNewDiggSubmit(  )
    {
        return _bEnableMailNewDiggSubmit;
    }

    /**
     * set true if a mail must be sent when the new digg is submitted
     * @param enableMailNewDiggSubmit true if a mail must be sent when the new digg is submitted
     */
    public void setEnableMailNewDiggSubmit( boolean enableMailNewDiggSubmit )
    {
        _bEnableMailNewDiggSubmit = enableMailNewDiggSubmit;
    }

    /**
     *
     * @return true if the new comment must be disable
     */
    public boolean isDisableNewComment(  )
    {
        return _bDisableNewComment;
    }

    /**
     * true if the new comment  must be disable
     * @param disableNewComment true if the new comment must be disable
     */
    public void setDisableNewComment( boolean disableNewComment )
    {
        _bDisableNewComment = disableNewComment;
    }

    /**
    *
    * @return the list of entries associate to the digg
    */
    public List<IEntry> getEntries(  )
    {
        return _listEntries;
    }

    /**
     * set the list of entries associate to the digg
     * @param listEntries the list of entries associate to the digg
     */
    public void setEntries( List<IEntry> listEntries )
    {
        _listEntries = listEntries;
    }

    /**
    *
    * @return the list of categories associate to the digg
    */
    public List<Category> getCategories(  )
    {
        return _listCategories;
    }

    /**
     * set the list of categories associate to the digg
     * @param listCategories the list of categories associate to the digg
     */
    public void setCategories( List<Category> listCategories )
    {
        _listCategories = listCategories;
    }

    /**
     *
     * @return the vote type associate to the digg
     */
    public VoteType getVoteType(  )
    {
        return _voteType;
    }

    /**
     * set the vote type associate to the digg
     * @param type  the vote type associate to the digg
     */
    public void setVoteType( VoteType type )
    {
        _voteType = type;
    }

    /**
     * return true if the comment are authorized
     * @return true if the comment are authorized
      */
    public boolean isAuthorizedComment(  )
    {
        return _bAuthorizedComment;
    }

    /**
     * set true if the comment are authorized
     * @param authorizedComment true if the comment are authorized
     */
    public void setAuthorizedComment( boolean authorizedComment )
    {
        _bAuthorizedComment = authorizedComment;
    }

    /**
     * return true if the digg proposition state exist
     * @return true if the digg proposition state exist
     */
    public boolean isActiveDiggPropositionState(  )
    {
        return _bActiveDiggPropositionState;
    }

    /**
     * set true if the digg proposition state exist
     * @param activeDiggPropositionState true if the digg proposition state exist
     */
    public void setActiveDiggPropositionState( boolean activeDiggPropositionState )
    {
        _bActiveDiggPropositionState = activeDiggPropositionState;
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
     * @param numberTopScore the top score
      */
    public void setNumberDiggSubmitInTopScore( int numberTopScore )
    {
        _nNumberDiggSubmitInTopScore = numberTopScore;
    }

    /**
    *
    * @return true if the user can submit just one  vote
    */
    public boolean isLimitNumberVote(  )
    {
        return _nLimitNumberVote;
    }

    /**
     * set true if the user can submit just one  vote
     * @param numberVote true if the user can submit just one  vote
     */
    public void setLimitNumberVote( boolean numberVote )
    {
        _nLimitNumberVote = numberVote;
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

    /**
     *
     * @return true if the category bloc must be show
     */
    public boolean isShowCategoryBlock(  )
    {
        return _bShowCategoryBlock;
    }

    /**
     * set true if the category bloc must be show
     * @param activeCategoryBloc true if the category bloc must be show
     */
    public void setShowCategoryBlock( boolean activeCategoryBloc )
    {
        _bShowCategoryBlock = activeCategoryBloc;
    }

    /**
     *
     * @return true if the top score bloc must be show
     */
    public boolean isShowTopScoreBlock(  )
    {
        return _bShowTopScoreBlock;
    }

    /**
     * set true if the top score bloc must be show
     * @param activeTopScoreBloc true if the top score bloc must be show
     */
    public void setShowTopScoreBlock( boolean activeTopScoreBloc )
    {
        _bShowTopScoreBlock = activeTopScoreBloc;
    }

    /**
     *
     * @return true if the top comment bloc must be show
     */
    public boolean isShowTopCommentBlock(  )
    {
        return _bShowTopCommentBlock;
    }

    /**
     * set true if the top comment bloc must be show
     * @param activeTopCommentBloc true if the top comment bloc must be show
     */
    public void setShowTopCommentBlock( boolean activeTopCommentBloc )
    {
        _bShowTopCommentBlock = activeTopCommentBloc;
    }

    /**
     *
     * @return the digg submit List associate to the digg
     */
    public List<DiggSubmit> getDiggsSubmit(  )
    {
        return _listDiggsSubmit;
    }

    /**
     * set the digg submit List associate to the digg
     * @param diggsSubmit the digg submit List associate to the digg
     */
    public void setDiggsSubmit( List<DiggSubmit> diggsSubmit )
    {
        _listDiggsSubmit = diggsSubmit;
    }

    /**
    * Returns the xml of this digg
    *
    * @param request The HTTP Servlet request
    * @param locale la locale
    * @return the xml of this digg
    */
    public String getXml( HttpServletRequest request, Locale locale )
    {
        StringBuffer strXml = new StringBuffer(  );
        XmlUtil.beginElement( strXml, TAG_DIGG );
        XmlUtil.addElementHtml( strXml, TAG_DIGG_TITLE, getTitle(  ) );
        XmlUtil.beginElement( strXml, TAG_DIGGS_SUBMIT );

        if ( ( getDiggsSubmit(  ) != null ) && ( getDiggsSubmit(  ).size(  ) != 0 ) )
        {
            for ( DiggSubmit diggSubmit : getDiggsSubmit(  ) )
            {
                strXml.append( diggSubmit.getXml( request, locale ) );
            }
        }

        XmlUtil.endElement( strXml, TAG_DIGGS_SUBMIT );
        XmlUtil.endElement( strXml, TAG_DIGG );

        return strXml.toString(  );
    }

    /**
     *
     * @return the number of digg submit per page
     */
    public int getNumberDiggSubmitPerPage(  )
    {
        return _nNumberDiggSubmitPerPage;
    }

    /**
     * set the number of digg submit per page
     * @param numberDiggSubmitPerPage the number of digg submit per page
     */
    public void setNumberDiggSubmitPerPage( int numberDiggSubmitPerPage )
    {
        _nNumberDiggSubmitPerPage = numberDiggSubmitPerPage;
    }

    /**
     *
     * @return true if the paginator is enable
     */
    public boolean isActiveDiggSubmitPaginator(  )
    {
        return _bActiveDiggSubmitPaginator;
    }

    /**
     * set true if the paginator is enable
     * @param activeDiggSubmitPaginator true if the paginator is enable
     */
    public void setActiveDiggSubmitPaginator( boolean activeDiggSubmitPaginator )
    {
        _bActiveDiggSubmitPaginator = activeDiggSubmitPaginator;
    }

    /**
     * Gets the digg role
     * @return digg's role as a String
     *
     */
    public String getRole(  )
    {
        return _strRole;
    }

    /**
     * Sets the digg's role
     * @param strRole The role
     *
     */
    public void setRole( String strRole )
    {
        _strRole = strRole;
    }

    /**
     * @param _nIdMailingListNewDiggSubmit the _nIdMailingListNewDiggSubmit to set
     */
    public void setIdMailingListNewDiggSubmit( int _nIdMailingListNewDiggSubmit )
    {
        this._nIdMailingListNewDiggSubmit = _nIdMailingListNewDiggSubmit;
    }

    /**Gets the digg's header
     * @return the _nIdMailingListNewDiggSubmit
     */
    public int getIdMailingListNewDiggSubmit(  )
    {
        return _nIdMailingListNewDiggSubmit;
    }

    /**Sets the digg's header
     * @param strHeader the strHeader to set
     */
    public void setHeader( String strHeader )
    {
        this._strHeader = strHeader;
    }

    /**
     * @return the strHeader
     */
    public String getHeader(  )
    {
        return _strHeader;
    }

    /**
     * @param nSortField the _nSortField to set
     */
    public void setSortField( int nSortField )
    {
        this._nSortField = nSortField;
    }

    /**
     * @return the _nSortField
     */
    public int getSortField(  )
    {
        return _nSortField;
    }

    /**
    *
    * @return the theme code
    */
    public String getCodeTheme(  )
    {
        return _strCodeTheme;
    }

    /**
     * set the theme code
     * @param strCodeTheme the theme code
     */
    public void setCodeTheme( String strCodeTheme )
    {
        _strCodeTheme = strCodeTheme;
    }

    /**
     * @return the strMessage
     */
    public String getConfirmationMessage(  )
    {
        return _strConfirmationMessage;
    }

    /**
     * @param strConfirmationMessage the message of the validation to set
     */
    public void setConfirmationMessage( String strConfirmationMessage )
    {
        _strConfirmationMessage = strConfirmationMessage;
    }
}
