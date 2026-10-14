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
package fr.paris.lutece.plugins.suggest.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.test.LuteceTestCase;

import org.junit.jupiter.api.Test;

/**
 * Tests the category business layer.
 */
public class CategoryHomeTest extends LuteceTestCase
{
    private static final String TITLE_1 = "Category title 1";
    private static final String TITLE_2 = "Category title 2";
    private static final String COLOR_1 = "#112233";
    private static final String COLOR_2 = "#445566";

    /**
     * Creates, reads, updates and removes a category.
     */
    @Test
    public void testCrud( )
    {
        Plugin plugin = null;
        Category category = new Category( );
        category.setTitle( TITLE_1 );
        category.setColor( COLOR_1 );

        CategoryHome.create( category, plugin );

        Category stored = CategoryHome.findByPrimaryKey( category.getIdCategory( ), plugin );
        assertNotNull( stored );
        assertEquals( TITLE_1, stored.getTitle( ) );
        assertEquals( COLOR_1, stored.getColor( ) );

        category.setTitle( TITLE_2 );
        category.setColor( COLOR_2 );
        CategoryHome.update( category, plugin );

        stored = CategoryHome.findByPrimaryKey( category.getIdCategory( ), plugin );
        assertEquals( TITLE_2, stored.getTitle( ) );
        assertEquals( COLOR_2, stored.getColor( ) );
        assertTrue( CategoryHome.getList( plugin ).stream( ).anyMatch( c -> c.getIdCategory( ) == category.getIdCategory( ) ) );

        CategoryHome.remove( category.getIdCategory( ), plugin );
        assertNull( CategoryHome.findByPrimaryKey( category.getIdCategory( ), plugin ) );
    }
}
