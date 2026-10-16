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
package fr.paris.lutece.plugins.suggest.web;

import java.util.List;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.suggest.business.CommentSubmit;
import fr.paris.lutece.plugins.suggest.business.CommentSubmitHome;
import fr.paris.lutece.plugins.suggest.business.SubmitFilter;
import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestHome;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitHome;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitStateHome;
import fr.paris.lutece.plugins.suggest.business.VoteTypeHome;
import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.security.ISecurityTokenService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.test.AdminUserUtils;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;
import jakarta.inject.Inject;

/**
 * Tests the suggest admin bean.
 */
public class SuggestJspBeanTest extends LuteceTestCase
{
    private static final String ANSWER = "L&#39;équipe IA a étudié votre idée, point d&#39;étape à J+15.";

    @Inject
    private SuggestJspBean _bean;
    @Inject
    private ISecurityTokenService _securityTokenService;

    /**
     * Checks that an official answer holding the apostrophes encoded by the request filter is published as sent.
     *
     * @throws AccessDeniedException
     *             if the controller refuses the request
     */
    @Test
    public void testDoSubmitOfficialAnswerWithApostrophes( ) throws AccessDeniedException
    {
        int nIdSuggestSubmit = createSuggestSubmit( );

        submitOfficialAnswer( nIdSuggestSubmit, null );

        List<CommentSubmit> listComments = getComments( nIdSuggestSubmit );
        assertEquals( 1, listComments.size( ) );
        assertTrue( listComments.get( 0 ).isOfficialAnswer( ) );
        assertEquals( ANSWER, listComments.get( 0 ).getValue( ) );
    }

    /**
     * Checks that an official answer naming a comment of the suggest submit is published under that comment.
     *
     * @throws AccessDeniedException
     *             if the controller refuses the request
     */
    @Test
    public void testDoSubmitOfficialAnswerToComment( ) throws AccessDeniedException
    {
        int nIdSuggestSubmit = createSuggestSubmit( );
        int nIdComment = createComment( nIdSuggestSubmit, 0 );

        submitOfficialAnswer( nIdSuggestSubmit, nIdComment );

        SubmitFilter filter = new SubmitFilter( );
        filter.setIdParent( nIdComment );
        List<CommentSubmit> listAnswers = CommentSubmitHome.getCommentSubmitList( filter, null, null );
        assertEquals( 1, listAnswers.size( ) );
        assertTrue( listAnswers.get( 0 ).isOfficialAnswer( ) );
        assertEquals( ANSWER, listAnswers.get( 0 ).getValue( ) );
        assertEquals( nIdSuggestSubmit, listAnswers.get( 0 ).getSuggestSubmit( ).getIdSuggestSubmit( ) );
    }

    /**
     * Checks that an official answer naming a comment of another suggest submit is not published.
     *
     * @throws AccessDeniedException
     *             if the controller refuses the request
     */
    @Test
    public void testDoSubmitOfficialAnswerToCommentOfAnotherSuggestSubmit( ) throws AccessDeniedException
    {
        int nIdSuggestSubmit = createSuggestSubmit( );
        int nIdOtherComment = createComment( createSuggestSubmit( ), 0 );

        submitOfficialAnswer( nIdSuggestSubmit, nIdOtherComment );

        assertTrue( getComments( nIdSuggestSubmit ).isEmpty( ) );
    }

    /**
     * Checks that an official answer to an answer at the deepest level of the discussions is published beside it, under the comment it answers.
     *
     * @throws AccessDeniedException
     *             if the controller refuses the request
     */
    @Test
    public void testDoSubmitOfficialAnswerToAnswerAtTheDeepestLevel( ) throws AccessDeniedException
    {
        int nIdSuggestSubmit = createSuggestSubmit( );
        int nIdComment = createComment( nIdSuggestSubmit, 0 );
        int nIdAnswer = createComment( nIdSuggestSubmit, nIdComment );

        submitOfficialAnswer( nIdSuggestSubmit, nIdAnswer );

        SubmitFilter filter = new SubmitFilter( );
        filter.setIdParent( nIdComment );
        List<CommentSubmit> listAnswers = CommentSubmitHome.getCommentSubmitList( filter, null, null );
        assertEquals( 2, listAnswers.size( ) );
        assertTrue( listAnswers.stream( ).anyMatch( CommentSubmit::isOfficialAnswer ) );
    }

    /**
     * Posts an official answer through the controller, as the back office form does.
     *
     * @param nIdSuggestSubmit
     *            the suggest submit
     * @param nIdParent
     *            the answered comment, or null for an answer to the suggest submit
     * @throws AccessDeniedException
     *             if the controller refuses the request
     */
    private void submitOfficialAnswer( int nIdSuggestSubmit, Integer nIdParent ) throws AccessDeniedException
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( "action", "submitOfficialAnswer" );
        request.addParameter( "comment_value", ANSWER );
        request.addParameter( "id_suggest_submit", Integer.toString( nIdSuggestSubmit ) );

        if ( nIdParent != null )
        {
            request.addParameter( "id_parent", Integer.toString( nIdParent ) );
        }

        request.addParameter( SecurityTokenService.PARAMETER_TOKEN, _securityTokenService.getToken( request, "suggest" ) );
        AdminUserUtils.registerAdminUserWithRight( request, new AdminUser( ), ManageSuggestJspBean.RIGHT_MANAGE_SUGGEST_LIKE );
        _bean.processController( request, new MockHttpServletResponse( ) );
    }

    /**
     * Creates a published suggest submit in a new suggest.
     *
     * @return the id of the suggest submit
     */
    private static int createSuggestSubmit( )
    {
        Suggest suggest = new Suggest( );
        suggest.setTitle( "Suggest" );
        suggest.setLibelleContribution( "Idea" );
        suggest.setVoteType( VoteTypeHome.findByPrimaryKey( 1, null ) );
        SuggestHome.create( suggest, null );

        SuggestSubmit suggestSubmit = new SuggestSubmit( );
        suggestSubmit.setSuggest( suggest );
        suggestSubmit.setSuggestSubmitState( SuggestSubmitStateHome.findByNumero( SuggestSubmit.STATE_PUBLISH, null ) );
        suggestSubmit.setSuggestSubmitTitle( "Idea" );

        return SuggestSubmitHome.create( suggestSubmit, null );
    }

    /**
     * Creates a published comment of a user on a suggest submit.
     *
     * @param nIdSuggestSubmit
     *            the suggest submit
     * @param nIdParent
     *            the answered comment, 0 for a comment
     * @return the id of the comment
     */
    private static int createComment( int nIdSuggestSubmit, int nIdParent )
    {
        CommentSubmit comment = new CommentSubmit( );
        comment.setSuggestSubmit( SuggestSubmitHome.findByPrimaryKey( nIdSuggestSubmit, null ) );
        comment.setDateComment( SuggestUtils.getCurrentDate( ) );
        comment.setDateModify( comment.getDateComment( ) );
        comment.setValue( "Comment" );
        comment.setActive( true );
        comment.setLuteceUserKey( "user" );
        comment.setIdParent( nIdParent );
        CommentSubmitHome.create( comment, null );

        return comment.getIdCommentSubmit( );
    }

    /**
     * Returns the comments of a suggest submit.
     *
     * @param nIdSuggestSubmit
     *            the suggest submit
     * @return the comments
     */
    private static List<CommentSubmit> getComments( int nIdSuggestSubmit )
    {
        SubmitFilter filter = new SubmitFilter( );
        filter.setIdSuggestSubmit( nIdSuggestSubmit );

        return CommentSubmitHome.getCommentSubmitList( filter, null, null );
    }
}
