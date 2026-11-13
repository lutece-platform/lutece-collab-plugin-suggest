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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests the range shifts that renumber a list of suggest submits.
 */
public class SuggestUtilsOrderTest
{
    /**
     * Applies shifts to list orders the way the database does: each range is shifted once, from the orders it had before any shift.
     *
     * @param listOrders
     *            the list orders
     * @param listShifts
     *            the shifts
     * @return the shifted list orders
     */
    private static List<Integer> apply( List<Integer> listOrders, List<int [ ]> listShifts )
    {
        List<Integer> listResult = new ArrayList<>( listOrders );

        for ( int [ ] shift : listShifts )
        {
            for ( int i = 0; i < listOrders.size( ); i++ )
            {
                if ( ( listOrders.get( i ) >= shift [0] ) && ( listOrders.get( i ) <= shift [1] ) )
                {
                    listResult.set( i, listOrders.get( i ) + shift [2] );
                }
            }
        }

        return listResult;
    }

    /**
     * A clean list needs no shift.
     */
    @Test
    public void testNothingToDo( )
    {
        assertTrue( SuggestUtils.getOrderShifts( List.of( 1, 2, 3 ) ).isEmpty( ) );
        assertTrue( SuggestUtils.getOrderShifts( List.of( ) ).isEmpty( ) );
    }

    /**
     * Moving the last element first takes two shifts, whatever the size of the list.
     */
    @Test
    public void testMove( )
    {
        List<Integer> listOrders = List.of( 5, 1, 2, 3, 4 );
        List<int [ ]> listShifts = SuggestUtils.getOrderShifts( listOrders );

        assertEquals( 2, listShifts.size( ) );
        assertArrayEquals( new int [ ] {
                5, 5, -4
        }, listShifts.get( 0 ) );
        assertArrayEquals( new int [ ] {
                1, 4, 1
        }, listShifts.get( 1 ) );
        assertEquals( List.of( 1, 2, 3, 4, 5 ), apply( listOrders, listShifts ) );
    }

    /**
     * A gap left by a removed element is closed by one shift.
     */
    @Test
    public void testGap( )
    {
        List<Integer> listOrders = List.of( 1, 2, 4, 5, 6 );
        List<int [ ]> listShifts = SuggestUtils.getOrderShifts( listOrders );

        assertEquals( 1, listShifts.size( ) );
        assertEquals( List.of( 1, 2, 3, 4, 5 ), apply( listOrders, listShifts ) );
    }

    /**
     * Orders that are duplicated or not positive cannot be shifted by ranges.
     */
    @Test
    public void testUnsafeOrders( )
    {
        assertNull( SuggestUtils.getOrderShifts( List.of( 1, 1, 2 ) ) );
        assertNull( SuggestUtils.getOrderShifts( List.of( 0, 1, 2 ) ) );
    }
}
