/*
 * Copyright (c) 2002-2026, City of Paris
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

import java.util.List;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.suggest.service.ISuggestSubmitService;
import jakarta.inject.Inject;

import fr.paris.lutece.plugins.suggest.utils.SuggestUtils;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * Tests the filter of the suggest submits by the department of their authors.
 */
public class SuggestSubmitDepartmentFilterTest extends LuteceTestCase
{
    @Inject
    private ISuggestSubmitService _suggestSubmitService;

    /**
     * Keeps the suggest submits of the authors of one department, counts them, lists the departments of the authors, keeps the pinned suggest submits within
     * the department and author filters and lists the pinned suggest submits first, then the others, in a limited list.
     */
    @Test
    public void testFilterByDepartment( )
    {
        Suggest suggest = new Suggest( );
        suggest.setTitle( "Suggest" );
        suggest.setLibelleContribution( "Idea" );
        suggest.setVoteType( VoteTypeHome.findByPrimaryKey( 1, null ) );
        SuggestHome.create( suggest, null );
        long lStamp = System.nanoTime( );
        String strDsin = "DSIN " + lStamp;
        String strDfpe = "DFPE " + lStamp;

        int nIdFirst = createSuggestSubmit( suggest, "first" + lStamp, strDsin, false );
        int nIdSecond = createSuggestSubmit( suggest, "second" + lStamp, strDfpe, true );
        createSuggestSubmit( suggest, "third" + lStamp, "", false );

        SubmitFilter filter = new SubmitFilter( );
        filter.setIdSuggest( suggest.getIdSuggest( ) );
        filter.setDepartment( strDsin );

        assertEquals( List.of( nIdFirst ), SuggestSubmitHome.getSuggestSubmitListId( filter, null ) );
        assertEquals( 1, SuggestSubmitHome.getCountSuggestSubmit( filter, null ) );
        assertEquals( 1, SuggestSubmitHome.getSuggestSubmitList( filter, null ).size( ) );
        assertTrue( SuggestSubmitHome.getSuggestSubmitListId( SuggestUtils.createPinnedFilter( filter ), null ).isEmpty( ) );

        filter.setDepartment( strDfpe );
        assertEquals( List.of( nIdSecond ), SuggestSubmitHome.getSuggestSubmitListId( filter, null ) );

        filter.setDepartment( null );
        assertEquals( 3, SuggestSubmitHome.getCountSuggestSubmit( filter, null ) );

        SuggestUtils.initSubmitFilterBySort( filter, SubmitFilter.SORT_BY_DATE_RESPONSE_DESC );
        SuggestUtils.initSubmitFilterBySort( filter, SubmitFilter.SORT_BY_PINNED_FIRST );
        List<SuggestSubmit> listLimited = _suggestSubmitService.getSuggestSubmitList( filter, null, 10 );
        assertEquals( 3, listLimited.size( ) );
        assertEquals( nIdSecond, listLimited.get( 0 ).getIdSuggestSubmit( ) );

        filter.setLuteceUserKey( "first" + lStamp );
        assertTrue( SuggestSubmitHome.getSuggestSubmitListId( SuggestUtils.createPinnedFilter( filter ), null ).isEmpty( ) );

        int nIdStatePublish = SuggestSubmitStateHome.findByNumero( SuggestSubmit.STATE_PUBLISH, null ).getIdSuggestSubmitState( );
        assertEquals( List.of( strDfpe, strDsin ), SuggestUserInfoHome.findDepartments( suggest.getIdSuggest( ), nIdStatePublish, null ) );
    }

    /**
     * Creates a published suggest submit of an author of a department.
     *
     * @param suggest
     *            the suggest
     * @param strUserKey
     *            the key of the author
     * @param strDepartment
     *            the department of the author
     * @param bPinned
     *            true to pin the suggest submit
     * @return the id of the suggest submit
     */
    private static int createSuggestSubmit( Suggest suggest, String strUserKey, String strDepartment, boolean bPinned )
    {
        SuggestUserInfo userInfo = new SuggestUserInfo( );
        userInfo.setLuteceUserKey( strUserKey );
        userInfo.setLastName( strUserKey );
        userInfo.setDepartment( strDepartment );
        SuggestUserInfoHome.create( userInfo, null );

        SuggestSubmit suggestSubmit = new SuggestSubmit( );
        suggestSubmit.setSuggest( suggest );
        suggestSubmit.setSuggestSubmitState( SuggestSubmitStateHome.findByNumero( SuggestSubmit.STATE_PUBLISH, null ) );
        suggestSubmit.setSuggestSubmitTitle( "Idea of " + strUserKey );
        suggestSubmit.setLuteceUserKey( strUserKey );
        suggestSubmit.setPinned( bPinned );

        return SuggestSubmitHome.create( suggestSubmit, null );
    }
}
