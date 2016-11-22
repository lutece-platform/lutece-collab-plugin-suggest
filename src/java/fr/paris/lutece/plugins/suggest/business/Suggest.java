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

import fr.paris.lutece.plugins.suggest.business.attribute.SuggestAttribute;
import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.portal.service.editor.EditorBbcodeService;
import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.rbac.RBACResource;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionRemovalListenerService;
import fr.paris.lutece.portal.service.resource.IExtendableResource;
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
 * Class Suggest
 *
 */
public class Suggest implements IExtendableResource, AdminWorkgroupResource, RBACResource
{
    public static final String RESOURCE_TYPE = "SUGGEST_SUGGEST_TYPE";
    public static final String ROLE_NONE = "none";
    public static final String TAG_LIST_ENTRY = "suggest-entries";
    public static final int STATE_ENABLE = 1;
    public static final int STATE_DISABLE = 0;

    private static final String TAG_SUGGEST = "suggest";
    private static final String TAG_SUGGEST_TITLE = "suggest-title";
    private static final String TAG_SUGGEST_SUBMITS = "suggest-submits";

    private static SuggestWorkgroupRemovalListener _listenerWorkgroup;
    private static SuggestRegularExpressionRemovalListener _listenerRegularExpression;
    private static SuggestRoleRemovalListener _listenerRole;
    private int _nIdSuggest;
    private String _strTitle;
    private String _strUnavailabilityMessage;
    private String _strWorkgroup;
    private int _nIdMailingListSuggestSubmit;
    private int _nNumberVoteRequired = -1;
    private int _nNumberDayRequired = -1;
    private int _nNumberSuggestSubmitInTopScore = -1;
    private int _nNumberSuggestSubmitInTopComment = -1;
    private int _nNumberSuggestSubmitCaractersShown = -1;
    private int _nNumberSuggestSubmitPerPage = -1;
    private boolean _bActiveCaptcha;
    private boolean _bActiveSuggestSubmitAuthentification;
    private boolean _bActiveVoteAuthentification;
    private boolean _bActiveCommentAuthentification;
    private boolean _bActiveSuggestPropositionState;
    private boolean _bActiveSuggestSubmitPaginator;
    private boolean _bAuthorizedComment;
    private boolean _bDisableNewSuggestSubmit;
    private boolean _bEnableMailNewSuggestSubmit;
    private boolean _bDisableNewComment;
    private boolean _nLimitNumberVote;
    private boolean _bShowCategoryBlock;
    private boolean _bShowTopScoreBlock;
    private boolean _bShowTopCommentBlock;
    private String _strLibelleValidateButton;
    private String _strLibelleContribution;
    private Timestamp _tDateCreation;
    private boolean _bActive;
    private List<SuggestAction> _listActions;
    private VoteType _voteType;
    private List<IEntry> _listEntries;
    private List<Category> _listCategories;
    private List<SuggestSubmit> _listSuggestsSubmit;
    private List<SuggestSubmitType> _listSuggestSubmitTypes;
    private String _strRole;
    private String _strHeader;
    private int _nSortField;
    private String _strCodeTheme;
    private String _strConfirmationMessage;
    private boolean _bActiveEditorBbcode;
    private int _nIdDefaultSort = SuggestUtils.CONSTANT_ID_NULL;
    private boolean _bDefaultSuggest;
    @SuggestAttribute( "disableVote" )
    private boolean _bDisableVote;
    @SuggestAttribute( "displayCommentInSuggestSubmitList" )
    private boolean _bDisplayCommentInSuggestSubmitList;
    @SuggestAttribute( "numberCommentDisplayInSuggestSubmitList" )
    private Integer _nNumberCommentDisplayInSuggestSubmitList;
    @SuggestAttribute( "numberCharCommentDisplayInSuggestSubmitList" )
    private Integer _nNumberCharCommentDisplayInSuggestSubmitList;
    @SuggestAttribute( "enableMailNewCommentSubmit" )
    private boolean _bEnableMailNewCommentSubmit;
    @SuggestAttribute( "enableMailNewReportedSubmit" )
    private boolean _bEnableMailNewReportedSubmit;
    @SuggestAttribute( "enableTermsOfUse" )
    private boolean _bEnableTermsOfUse;
    @SuggestAttribute( "termsOfUse" )
    private String _strTermsOfUse;
    @SuggestAttribute( "enableReports" )
    private boolean _bEnableReports;
    @SuggestAttribute( "idImageResource" )
    private Integer _nIdImageResource = SuggestUtils.CONSTANT_ID_NULL;
    private ImageResource _image;
    @SuggestAttribute( "description" )
    private String _strDescription;
    @SuggestAttribute( "notificationNewCommentSenderName" )
    private String _strNotificationNewCommentSenderName;
    @SuggestAttribute( "notificationNewCommentTitle" )
    private String _strNotificationNewCommentTitle;
    @SuggestAttribute( "notificationNewCommentBody" )
    private String _strNotificationNewCommentBody;
    @SuggestAttribute( "notificationNewSuggestSubmitSenderName" )
    private String _strNotificationNewSuggestSubmitSenderName;
    @SuggestAttribute( "notificationNewSuggestSubmitTitle" )
    private String _strNotificationNewSuggestSubmitTitle;
    @SuggestAttribute( "notificationNewSuggestSubmitBody" )
    private String _strNotificationNewSuggestSubmitBody;
    

    /**
    * Initialize the Suggest
    */
    public static void init(  )
    {
        // Create removal listeners and register them
        if ( _listenerWorkgroup == null )
        {
            _listenerWorkgroup = new SuggestWorkgroupRemovalListener(  );
            WorkgroupRemovalListenerService.getService(  ).registerListener( _listenerWorkgroup );
        }

        if ( _listenerRegularExpression == null )
        {
            _listenerRegularExpression = new SuggestRegularExpressionRemovalListener(  );
            RegularExpressionRemovalListenerService.getService(  ).registerListener( _listenerRegularExpression );
        }

        if ( _listenerRole == null )
        {
            _listenerRole = new SuggestRoleRemovalListener(  );
            RoleRemovalListenerService.getService(  ).registerListener( _listenerRole );
        }
    }

    /**
     *
     * @return the id of the mailing list associate to the suggest submit
     */
    public int getIdMailingListSuggestSubmit(  )
    {
        return _nIdMailingListSuggestSubmit;
    }

    /**
     * set the id of the mailing list associate to the suggest submit
     * @param mailingListId the id of the mailing list associate to the suggest submit
     */
    public void setIdMailingListSuggestSubmit( int mailingListId )
    {
        _nIdMailingListSuggestSubmit = mailingListId;
    }

    /**
     *
     * @return true if the suggest for creating suggest contain a captcha
     */
    public boolean isActiveCaptcha(  )
    {
        return _bActiveCaptcha;
    }

    /**
     * set true if the suggest for creating suggest contain a captcha
     * @param activeCaptcha true if the suggest for creating suggest  contain a captcha
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
    * @return the libelle of the suggest contribution
    */
    public String getLibelleContribution(  )
    {
        return _strLibelleContribution;
    }

    /**
     * set the libelle of the suggest name
     * @param libelleSuggestName the libelle of the suggest contribution
     */
    public void setLibelleContribution( String libelleSuggestName )
    {
        _strLibelleContribution = libelleSuggestName;
    }

    /**
     *
     * @return the title of the suggest
     */
    public String getTitle(  )
    {
        return _strTitle;
    }

    /**
     * set the title of the suggest
     * @param strTitle the title of the suggest
     */
    public void setTitle( String strTitle )
    {
        this._strTitle = strTitle;
    }

    /**
     *
     * @return the unavailability message of the suggest
     */
    public String getUnavailabilityMessage(  )
    {
        return _strUnavailabilityMessage;
    }

    /**
     * set the unavailability message of the suggest
     * @param unavailabilityMessage the unavailability message of the suggest
     */
    public void setUnavailabilityMessage( String unavailabilityMessage )
    {
        _strUnavailabilityMessage = unavailabilityMessage;
    }

    /**
     *
     * @return the work group associate to the suggest
     */
    public String getWorkgroup(  )
    {
        return _strWorkgroup;
    }

    /**
     * set  the work group associate to the suggest
     * @param workGroup  the work group associate to the suggest
     */
    public void setWorkgroup( String workGroup )
    {
        _strWorkgroup = workGroup;
    }

    /**
     *
     * @return the id of the suggest
     */
    public int getIdSuggest(  )
    {
        return _nIdSuggest;
    }

    /**
     * set the id of the suggest
     * @param idSuggest the id of the suggest
     */
    public void setIdSuggest( int idSuggest )
    {
        _nIdSuggest = idSuggest;
    }

    /**
     *
     * @return true if the suggest is active
     */
    public boolean isActive(  )
    {
        return _bActive;
    }

    /**
     * set true if the suggest is active
     * @param active true if the suggest is active
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
        return "" + _nIdSuggest;
    }

    /**
     *
     * @return a list of action can be use for the suggest
     */
    public List<SuggestAction> getActions(  )
    {
        return _listActions;
    }

    /**
     * set a list of action can be use for the suggest
     * @param suggestActions a list of action must be use for the suggest
     */
    public void setActions( List<SuggestAction> suggestActions )
    {
        _listActions = suggestActions;
    }

    /**
     * return the number of vote required for publishing a suggest submit
     * @return the number of vote required for publishing a suggest submit
     */
    public int getNumberVoteRequired(  )
    {
        return _nNumberVoteRequired;
    }

    /**
     * Set the number of vote required for publishing a suggest submit
     * @param numberVoteRequired the number of vote required for publishing a suggest submit
      */
    public void setNumberVoteRequired( int numberVoteRequired )
    {
        _nNumberVoteRequired = numberVoteRequired;
    }

    /**
     * return the number of day required for disable a suggest submit
     * @return  the number of day required for disable a suggest submit
     */
    public int getNumberDayRequired(  )
    {
        return _nNumberDayRequired;
    }

    /**
     * set the number of day required for disable a suggest submit
     * @param numberDayRequired the number of day required for disable a suggest submit
     */
    public void setNumberDayRequired( int numberDayRequired )
    {
        _nNumberDayRequired = numberDayRequired;
    }

    /**
     * return true if the form of suggest submit creation required a captcha
     * @return true if the form of suggest submit creation required a captcha
     */
    public boolean isActiveSuggestSubmitAuthentification(  )
    {
        return _bActiveSuggestSubmitAuthentification;
    }

    /**
    *  set true if the form of suggest submit creation required a captcha
    * @param activeSuggestSubmitAuthentification true if the form of suggest submit creation required a captcha
    */
    public void setActiveSuggestSubmitAuthentification( boolean activeSuggestSubmitAuthentification )
    {
        _bActiveSuggestSubmitAuthentification = activeSuggestSubmitAuthentification;
    }

    /**
     *
     * @return true if the vote on  suggest submit required an authentification
     */
    public boolean isActiveVoteAuthentification(  )
    {
        return _bActiveVoteAuthentification;
    }

    /**
     * set true if the vote on  suggest submit required an authentification
     * @param activeVoteAuthentification true if the vote on  suggest submit required an authentification
     */
    public void setActiveVoteAuthentification( boolean activeVoteAuthentification )
    {
        _bActiveVoteAuthentification = activeVoteAuthentification;
    }

    /**
     *
     * @return true if the comment on  suggest submit required an authentification
     */
    public boolean isActiveCommentAuthentification(  )
    {
        return _bActiveCommentAuthentification;
    }

    /**
     * set true if the comment on  suggest submit required an authentification
     * @param activeCommentAuthentification true if the comment on  suggest submit required an authentification
     */
    public void setActiveCommentAuthentification( boolean activeCommentAuthentification )
    {
        _bActiveCommentAuthentification = activeCommentAuthentification;
    }

    /**
     *
     * @return true if the new suggest submit must be disable
     */
    public boolean isDisableNewSuggestSubmit(  )
    {
        return _bDisableNewSuggestSubmit;
    }

    /**
     * set true if the new suggest submit must be disable
     * @param disableNewSuggestSubmit true if the new suggest submit must be disable
     */
    public void setDisableNewSuggestSubmit( boolean disableNewSuggestSubmit )
    {
        _bDisableNewSuggestSubmit = disableNewSuggestSubmit;
    }

    /**
         *
         * @return true if a mail must be sent when the new suggest is submitted
         */
    public boolean isEnableMailNewSuggestSubmit(  )
    {
        return _bEnableMailNewSuggestSubmit;
    }

    /**
     * set true if a mail must be sent when the new suggest is submitted
     * @param enableMailNewSuggestSubmit true if a mail must be sent when the new suggest is submitted
     */
    public void setEnableMailNewSuggestSubmit( boolean enableMailNewSuggestSubmit )
    {
        _bEnableMailNewSuggestSubmit = enableMailNewSuggestSubmit;
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
    * @return the list of entries associate to the suggest
    */
    public List<IEntry> getEntries(  )
    {
        return _listEntries;
    }

    /**
     * set the list of entries associate to the suggest
     * @param listEntries the list of entries associate to the suggest
     */
    public void setEntries( List<IEntry> listEntries )
    {
        _listEntries = listEntries;
    }

    /**
    *
    * @return the list of categories associate to the suggest
    */
    public List<Category> getCategories(  )
    {
        return _listCategories;
    }

    /**
     * set the list of categories associate to the suggest
     * @param listCategories the list of categories associate to the suggest
     */
    public void setCategories( List<Category> listCategories )
    {
        _listCategories = listCategories;
    }

    /**
     *
     * @return the vote type associate to the suggest
     */
    public VoteType getVoteType(  )
    {
        return _voteType;
    }

    /**
     * set the vote type associate to the suggest
     * @param type  the vote type associate to the suggest
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
     * return true if the suggest proposition state exist
     * @return true if the suggest proposition state exist
     */
    public boolean isActiveSuggestPropositionState(  )
    {
        return _bActiveSuggestPropositionState;
    }

    /**
     * set true if the suggest proposition state exist
     * @param activeSuggestPropositionState true if the suggest proposition state exist
     */
    public void setActiveSuggestPropositionState( boolean activeSuggestPropositionState )
    {
        _bActiveSuggestPropositionState = activeSuggestPropositionState;
    }

    /**
    * return the number of suggest submit display in the list of top comment
    * @return the number of suggest submit display in the list of top comment
    */
    public int getNumberSuggestSubmitInTopComment(  )
    {
        return _nNumberSuggestSubmitInTopComment;
    }

    /**
     * Set the number of suggest submit display in the list of top comment
     * @param numberTopComment the number of  suggest submit in the list of top comment
      */
    public void setNumberSuggestSubmitInTopComment( int numberTopComment )
    {
        _nNumberSuggestSubmitInTopComment = numberTopComment;
    }

    /**
    * return the number of  suggest submit display in the list of top score
    * @return the number of suggest submit  display in the list of top score
    */
    public int getNumberSuggestSubmitInTopScore(  )
    {
        return _nNumberSuggestSubmitInTopScore;
    }

    /**
     * Set the number of comment display in the list of top score
     * @param numberTopScore the top score
      */
    public void setNumberSuggestSubmitInTopScore( int numberTopScore )
    {
        _nNumberSuggestSubmitInTopScore = numberTopScore;
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
     * return the nunber of caracters shown in the list of suggest submit
     * @return the nunber of caracters shown in the list of suggest submit
     */
    public int getNumberSuggestSubmitCaractersShown(  )
    {
        return _nNumberSuggestSubmitCaractersShown;
    }

    /**
     * set the nunber of caracters shown in the list of suggest submit
     * @param numberCaractersShown the nunber of caracters shown in the list of suggest submit
     */
    public void setNumberSuggestSubmitCaractersShown( int numberCaractersShown )
    {
        _nNumberSuggestSubmitCaractersShown = numberCaractersShown;
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
     * @return the suggest submit List associate to the suggest
     */
    public List<SuggestSubmit> getSuggestsSubmit(  )
    {
        return _listSuggestsSubmit;
    }

    /**
     * set the suggest submit List associate to the suggest
     * @param suggestsSubmit the suggest submit List associate to the suggest
     */
    public void setSuggestsSubmit( List<SuggestSubmit> suggestsSubmit )
    {
        _listSuggestsSubmit = suggestsSubmit;
    }

    /**
     * Returns the xml of this suggest
     * 
     * @param request The HTTP Servlet request
     * @param sbListSuggestSubmit The string buffer
     * @param locale la locale
     * @return the xml of this suggest
     */
    public String getXml( HttpServletRequest request, StringBuffer sbListSuggestSubmit, Locale locale )
    {
        StringBuffer strXml = new StringBuffer(  );
        XmlUtil.beginElement( strXml, TAG_SUGGEST );
        XmlUtil.addElementHtml( strXml, TAG_SUGGEST_TITLE, getTitle(  ) );
        XmlUtil.beginElement( strXml, TAG_LIST_ENTRY );

        if ( ( this.getEntries(  ) != null ) && !this.getEntries(  ).isEmpty(  ) )
        {
            for ( IEntry entry : this.getEntries(  ) )
            {
                entry.getXml( locale, strXml );
            }
        }

        XmlUtil.endElement( strXml, TAG_LIST_ENTRY );
        XmlUtil.beginElement( strXml, TAG_SUGGEST_SUBMITS );
        strXml.append( sbListSuggestSubmit );
        XmlUtil.endElement( strXml, TAG_SUGGEST_SUBMITS );
        XmlUtil.endElement( strXml, TAG_SUGGEST );

        return strXml.toString(  );
    }

    /**
     *
     * @return the number of suggest submit per page
     */
    public int getNumberSuggestSubmitPerPage(  )
    {
        return _nNumberSuggestSubmitPerPage;
    }

    /**
     * set the number of suggest submit per page
     * @param numberSuggestSubmitPerPage the number of suggest submit per page
     */
    public void setNumberSuggestSubmitPerPage( int numberSuggestSubmitPerPage )
    {
        _nNumberSuggestSubmitPerPage = numberSuggestSubmitPerPage;
    }

    /**
     *
     * @return true if the paginator is enable
     */
    public boolean isActiveSuggestSubmitPaginator(  )
    {
        return _bActiveSuggestSubmitPaginator;
    }

    /**
     * set true if the paginator is enable
     * @param activeSuggestSubmitPaginator true if the paginator is enable
     */
    public void setActiveSuggestSubmitPaginator( boolean activeSuggestSubmitPaginator )
    {
        _bActiveSuggestSubmitPaginator = activeSuggestSubmitPaginator;
    }

    /**
     * Gets the suggest role
     * @return suggest's role as a String
     *
     */
    public String getRole(  )
    {
        return _strRole;
    }

    /**
     * Sets the suggest's role
     * @param strRole The role
     *
     */
    public void setRole( String strRole )
    {
        _strRole = strRole;
    }

    /**Sets the suggest's header
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

    /**
     * set true if the editor BBcode must be display
     * @param nActiveEditorBbCode true if the editor BBcode must be display
     */
    public void setActiveEditorBbcode( boolean nActiveEditorBbCode )
    {
        this._bActiveEditorBbcode = nActiveEditorBbCode;
    }

    /**
     *
     * @return true if the editor BBcode must be display
     */
    public boolean isActiveEditorBbcode(  )
    {
        return _bActiveEditorBbcode;
    }

    /**
     * parseBbcodeValue
     * @param strValue the value to parse
     * @return the result of the parsing
     */
    public String parseBbcodeValue( String strValue )
    {
        if ( isActiveEditorBbcode(  ) )
        {
            return EditorBbcodeService.getInstance(  ).parse( strValue );
        }

        return strValue;
    }

    /**
     *
     * @return the default sort id
     */
    public int getIdDefaultSort(  )
    {
        return _nIdDefaultSort;
    }

    /**
     * set id Default Sort
     * @param nIdDefaultSort  the default sort id
     */
    public void setIdDefaultSort( int nIdDefaultSort )
    {
        _nIdDefaultSort = nIdDefaultSort;
    }

    /**
     *
     * @return true if the suggest is a Default suggest
     */
    public boolean isDefaultSuggest(  )
    {
        return _bDefaultSuggest;
    }

    /**
     * set true if the suggest is a Default suggest
     * @param bDefaultSuggest true if the suggest is a Default suggest
     */
    public void setDefaultSuggest( boolean bDefaultSuggest )
    {
        _bDefaultSuggest = bDefaultSuggest;
    }

    /**
     *
     * @return true if the vote is disable
     */
    public boolean isDisableVote(  )
    {
        return _bDisableVote;
    }

    /**
     * set true if the vote is disable
     * @param bDisable true if the vote is disable
     */
    public void setDisableVote( boolean bDisable )
    {
        _bDisableVote = bDisable;
    }

    /**
     *
     * @return true if the list of comment must be display in suggest submit list
     */
    public boolean isDisplayCommentInSuggestSubmitList(  )
    {
        return _bDisplayCommentInSuggestSubmitList;
    }

    /**
     *
     * @param bDisplayCommentInSuggestSubmitList true if the list of comment must be display in suggest submit list
     */
    public void setDisplayCommentInSuggestSubmitList( boolean bDisplayCommentInSuggestSubmitList )
    {
        this._bDisplayCommentInSuggestSubmitList = bDisplayCommentInSuggestSubmitList;
    }

    /**
     *
     * @return the number of comment submit display by suggest submit in the list of suggest submit
     */
    public Integer getNumberCommentDisplayInSuggestSubmitList(  )
    {
        return _nNumberCommentDisplayInSuggestSubmitList;
    }

    /**
     *
     * @param nNumberCommentDisplayInSuggestSubmitList the number of comment submit display by suggest submit in the list of suggest submit
     */
    public void setNumberCommentDisplayInSuggestSubmitList( Integer nNumberCommentDisplayInSuggestSubmitList )
    {
        this._nNumberCommentDisplayInSuggestSubmitList = nNumberCommentDisplayInSuggestSubmitList;
    }

    /**
     *
     * @return the number of  char comment  display by suggest submit in the list of suggest submit
     */
    public Integer getNumberCharCommentDisplayInSuggestSubmitList(  )
    {
        return _nNumberCharCommentDisplayInSuggestSubmitList;
    }

    /**
     *
     * @param nNumberCommentDisplayInSuggestSubmitList the number of  char comment  display by suggest submit in the list of suggest submit
     */
    public void setNumberCharCommentDisplayInSuggestSubmitList( Integer nNumberCommentDisplayInSuggestSubmitList )
    {
        this._nNumberCharCommentDisplayInSuggestSubmitList = nNumberCommentDisplayInSuggestSubmitList;
    }

    /**
    *
    * @return true if a mail must be sent when the new comment is submitted
    */
    public boolean isEnableMailNewCommentSubmit(  )
    {
        return _bEnableMailNewCommentSubmit;
    }

    /**
    * set true if a mail must be sent when the new comment is submitted
    * @param bEnableMailNewCommentSubmit true if a mail must be sent when the new comment is submitted
    */
    public void setEnableMailNewCommentSubmit( boolean bEnableMailNewCommentSubmit )
    {
        _bEnableMailNewCommentSubmit = bEnableMailNewCommentSubmit;
    }

    /**
    *
    * @return true if a mail must be sent when the new comment is submitted
    */
    public boolean isEnableMailNewReportedSubmit(  )
    {
        return _bEnableMailNewReportedSubmit;
    }

    /**
    * set true if a mail must be sent when the new reported is submitted
    * @param bEnableMailNewReportedSubmit true if a mail must be sent when the new comment is submitted
    */
    public void setEnableMailNewReportedSubmit( boolean bEnableMailNewReportedSubmit )
    {
        _bEnableMailNewReportedSubmit = bEnableMailNewReportedSubmit;
    }

    /**
    *
    * @return the suggest submit type List associate to the suggest
    */
    public List<SuggestSubmitType> getSuggestSubmitTypes(  )
    {
        return _listSuggestSubmitTypes;
    }

    /**
     * set the suggest submit type List associate to the suggest
     * @param suggestSubmitTypes the suggest submit type List associate to the suggest
     */
    public void setSuggestSubmiTypes( List<SuggestSubmitType> suggestSubmitTypes )
    {
        _listSuggestSubmitTypes = suggestSubmitTypes;
    }

    /**
     *
     * @return true if Terms of use must be enable
     */
    public boolean isEnableTermsOfUse(  )
    {
        return _bEnableTermsOfUse;
    }

    /**
     *
     * @param bEnableTermsOfUse  true if Terms of use must be enable
     */
    public void setEnableTermsOfUse( boolean bEnableTermsOfUse )
    {
        _bEnableTermsOfUse = bEnableTermsOfUse;
    }

    /**
     * the terms of use
     * @return the terms of use
     */
    public String getTermsOfUse(  )
    {
        return _strTermsOfUse;
    }

    /**
     * set Terms of use
     * @param strTermsOfUse the terms of use
     */
    public void setTermsOfUse( String strTermsOfUse )
    {
        this._strTermsOfUse = strTermsOfUse;
    }

    /**
     *
     * @return the suggest description
     */
    public String getDescription(  )
    {
        return _strDescription;
    }

    /**
     * 
     * @param strDescription set the suggest description
     */
    public void setDescription( String strDescription )
    {
        this._strDescription = strDescription;
    }

    /**
     *
     * @return true if reports are enable
     */
    public boolean isEnableReports(  )
    {
        return _bEnableReports;
    }

    /**
     * 
     * @param bEnableReports true if reports are enable
     */
    public void setEnableReports( boolean bEnableReports )
    {
        this._bEnableReports = bEnableReports;
    }

    /**
     * the image resource id associate to the suggest
     * @return Resource Image
     */
    public Integer getIdImageResource(  )
    {
        return _nIdImageResource;
    }

    /**
     * image resource id associate to the suggest
     * @param nIdImageResource image resource id associate to the suggest
     */
    public void setIdImageResource( Integer nIdImageResource )
    {
        _nIdImageResource = nIdImageResource;
    }

    /**
     * get Image
     * @return Image Resource
     */
    public ImageResource getImage(  )
    {
        return _image;
    }

    /**
     * set Image Resource
     * @param image  Image Resource
     */
    public void setImage( ImageResource image )
    {
        this._image = image;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIdExtendableResource(  )
    {
        return Integer.toString( _nIdSuggest );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtendableResourceType(  )
    {
        return RESOURCE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtendableResourceName(  )
    {
        return _strTitle;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtendableResourceDescription(  )
    {
        return _strDescription;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExtendableResourceImageUrl(  )
    {
        if ( _nIdImageResource != null )
        {
            StringBuilder sbUrl = new StringBuilder( SuggestUtils.SERVLET_IMAGE_PATH );
            sbUrl.append( _nIdImageResource );

            return sbUrl.toString(  );
        }

        return null;
    }
    
    /**
     * 
     * @return getNotificationNewCommentSenderName
     */
    public String getNotificationNewCommentSenderName() {
		return _strNotificationNewCommentSenderName;
	}

    /**
     * 
     * @param _strNotificationNewCommentSenderName
     */
	public void setNotificationNewCommentSenderName(
			String _strNotificationNewCommentSenderName) {
		this._strNotificationNewCommentSenderName = _strNotificationNewCommentSenderName;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getNotificationNewCommentTitle() {
		return _strNotificationNewCommentTitle;
	}
	/**
	 * 
	 * @param _strNotificationNewCommentTitle
	 */
	public void setNotificationNewCommentTitle(
			String _strNotificationNewCommentTitle) {
		this._strNotificationNewCommentTitle = _strNotificationNewCommentTitle;
	}
	/**
	 * 
	 * @return
	 */
	public String getNotificationNewCommentBody() {
		return _strNotificationNewCommentBody;
	}
	/**
	 * 
	 * @param _strNotificationNewCommentBody
	 */
	public void setNotificationNewCommentBody(
			String _strNotificationNewCommentBody) {
		this._strNotificationNewCommentBody = _strNotificationNewCommentBody;
	}
	/**
	 * 
	 * @return
	 */
	public String getNotificationNewSuggestSubmitSenderName() {
		return _strNotificationNewSuggestSubmitSenderName;
	}
	/**
	 * 
	 * @param _strNotificationNewSuggestSubmitSenderName
	 */
	public void setNotificationNewSuggestSubmitSenderName(
			String _strNotificationNewSuggestSubmitSenderName) {
		this._strNotificationNewSuggestSubmitSenderName = _strNotificationNewSuggestSubmitSenderName;
	}
	/**
	 * 
	 * @return
	 */
	public String getNotificationNewSuggestSubmitTitle() {
		return _strNotificationNewSuggestSubmitTitle;
	}
	/**
	 * 
	 * @param _strNotificationNewSuggestSubmitTitle
	 */
	public void setNotificationNewSuggestSubmitTitle(
			String _strNotificationNewSuggestSubmitTitle) {
		this._strNotificationNewSuggestSubmitTitle = _strNotificationNewSuggestSubmitTitle;
	}
	/**
	 * 
	 * @return
	 */
	public String getNotificationNewSuggestSubmitBody() {
		return _strNotificationNewSuggestSubmitBody;
	}
	/**
	 * 
	 * @param _strNotificationSuggestSubmitBody
	 */
	public void setNotificationNewSuggestSubmitBody(
			String _strNotificationSuggestSubmitBody) {
		this._strNotificationNewSuggestSubmitBody = _strNotificationSuggestSubmitBody;
	}
}
