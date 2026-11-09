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
package fr.paris.lutece.plugins.suggest.service;

import java.util.List;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestHome;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitComplement;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitComplementHome;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitHome;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmitStateHome;
import fr.paris.lutece.plugins.suggest.business.VoteTypeHome;
import fr.paris.lutece.test.LuteceTestCase;
import jakarta.inject.Inject;

/**
 * Tests the complements of the suggest submits.
 */
public class SuggestSubmitComplementServiceTest extends LuteceTestCase
{
    @Inject
    private SuggestSubmitComplementService _complementService;

    /**
     * Adds two complements, lists them oldest first, removes one.
     */
    @Test
    public void testAddListRemove( )
    {
        SuggestSubmit suggestSubmit = createSuggestSubmit( );

        SuggestSubmitComplement first = _complementService.addComplement( suggestSubmit.getIdSuggestSubmit( ), "  First  " );
        SuggestSubmitComplement second = _complementService.addComplement( suggestSubmit.getIdSuggestSubmit( ), "Second" );

        List<SuggestSubmitComplement> listComplements = _complementService.getComplements( suggestSubmit.getIdSuggestSubmit( ) );
        assertEquals( 2, listComplements.size( ) );
        assertEquals( "First", listComplements.get( 0 ).getValue( ) );
        assertEquals( second.getIdComplement( ), listComplements.get( 1 ).getIdComplement( ) );
        assertNotNull( listComplements.get( 0 ).getDateComplement( ) );

        _complementService.removeComplement( first );

        assertNull( _complementService.findByPrimaryKey( first.getIdComplement( ) ) );
        assertEquals( 1, _complementService.getComplements( suggestSubmit.getIdSuggestSubmit( ) ).size( ) );
    }

    /**
     * Removing a suggest submit removes its complements.
     */
    @Test
    public void testRemoveSuggestSubmitRemovesComplements( )
    {
        SuggestSubmit suggestSubmit = createSuggestSubmit( );
        SuggestSubmitComplement complement = _complementService.addComplement( suggestSubmit.getIdSuggestSubmit( ), "Complement" );

        SuggestSubmitHome.remove( suggestSubmit.getIdSuggestSubmit( ), null );

        assertNull( SuggestSubmitComplementHome.findByPrimaryKey( complement.getIdComplement( ), null ) );
    }

    /**
     * Creates a published suggest submit in a new suggest.
     *
     * @return the suggest submit
     */
    private static SuggestSubmit createSuggestSubmit( )
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
        suggestSubmit.setIdSuggestSubmit( SuggestSubmitHome.create( suggestSubmit, null ) );

        return suggestSubmit;
    }
}
