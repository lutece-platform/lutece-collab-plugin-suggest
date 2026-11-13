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
package fr.paris.lutece.plugins.suggest.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.suggest.business.Category;
import fr.paris.lutece.plugins.suggest.business.Suggest;

/**
 * Tests the category a proposal may be filed under.
 */
public class SuggestUtilsCategoryTest
{
    /**
     * Any choice is allowed when the suggest has no category.
     */
    @Test
    public void testNoCategory( )
    {
        Suggest suggest = new Suggest( );
        suggest.setCategories( List.of( ) );

        assertTrue( SuggestUtils.isCategoryAllowed( suggest, SuggestUtils.CONSTANT_ID_NULL ) );
        assertTrue( SuggestUtils.isCategoryAllowed( suggest, 7 ) );
    }

    /**
     * Only a category of the suggest is allowed when it has some: no choice and a category of another suggest are refused.
     */
    @Test
    public void testCategoriesOfTheSuggest( )
    {
        Category category = new Category( );
        category.setIdCategory( 3 );
        Suggest suggest = new Suggest( );
        suggest.setCategories( List.of( category ) );

        assertTrue( SuggestUtils.isCategoryAllowed( suggest, 3 ) );
        assertFalse( SuggestUtils.isCategoryAllowed( suggest, SuggestUtils.CONSTANT_ID_NULL ) );
        assertFalse( SuggestUtils.isCategoryAllowed( suggest, 4 ) );
    }
}
