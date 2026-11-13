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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests which comments count among the comments of a suggest submit.
 */
public class CommentSubmitTest
{
    /**
     * Builds a comment.
     *
     * @param bOfficialAnswer
     *            true for an answer of the team
     * @param nIdParent
     *            the answered comment, or 0
     * @return the comment
     */
    private static CommentSubmit comment( boolean bOfficialAnswer, int nIdParent )
    {
        CommentSubmit comment = new CommentSubmit( );
        comment.setOfficialAnswer( bOfficialAnswer );
        comment.setIdParent( nIdParent );

        return comment;
    }

    /**
     * Comments and answers of the team to a comment belong to the discussion, an official answer to the suggest submit does not.
     */
    @Test
    public void testIsInDiscussion( )
    {
        assertTrue( comment( false, SubmitFilter.ID_PARENT_NULL ).isInDiscussion( ) );
        assertTrue( comment( false, 12 ).isInDiscussion( ) );
        assertTrue( comment( true, 12 ).isInDiscussion( ) );
        assertFalse( comment( true, SubmitFilter.ID_PARENT_NULL ).isInDiscussion( ) );
    }
}
