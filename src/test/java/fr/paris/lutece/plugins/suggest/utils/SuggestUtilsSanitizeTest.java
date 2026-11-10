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
package fr.paris.lutece.plugins.suggest.utils;

import java.util.List;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.suggest.business.EntryTypeEditorBbcode;
import fr.paris.lutece.plugins.suggest.business.EntryTypeText;
import fr.paris.lutece.plugins.suggest.business.EntryTypeUrl;
import fr.paris.lutece.plugins.suggest.business.Response;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * Tests the cleaning of the values a user sends through the proposal form.
 */
public class SuggestUtilsSanitizeTest extends LuteceTestCase
{
    private static final String [ ] PAYLOADS = {
            "\"><img src=x onerror=\"alert(1)\">", "<script>alert(1)</script>", "<svg onload=alert(1)>", "<a href=\"javascript:alert(1)\">x</a>",
            "<p onmouseover=\"alert(1)\" style=\"position:fixed;inset:0\">x</p>", "<iframe srcdoc=\"<script>alert(1)</script>\"></iframe>",
            "' onmouseover='alert(1)"
    };
    private static final String TEXT = "L'idée n°1 : trier \"R&D\" < 50 mails & gagner > 2 h — « test » éàçœ 🙂 <b> a<b et c>d";

    /**
     * Turns the value of a multipart request into text no page can interpret: no element, no attribute delimiter, and the text shown is the text typed.
     */
    @Test
    public void testSanitizeResponsesEscapesMultipartValues( )
    {
        for ( String strPayload : PAYLOADS )
        {
            String strTyped = "Trier les mails " + strPayload;
            List<Response> listResponse = responses( strTyped );

            assertNull( SuggestUtils.sanitizeResponses( new EntryTypeText( ), listResponse, multipartRequest( ), Locale.FRENCH ) );

            String strValue = listResponse.get( 0 ).getValueResponse( );
            assertFalse( strValue.matches( "(?s).*[<>\"'].*" ), strValue );
            Element body = Jsoup.parseBodyFragment( strValue ).body( );
            assertEquals( 0, body.children( ).size( ) );
            assertEquals( strTyped, body.wholeText( ) );
        }
    }

    /**
     * Shows an ordinary text exactly as typed, and gives it back as plain text.
     */
    @Test
    public void testEscapeTextKeepsTheText( )
    {
        String strStored = SuggestUtils.escapeText( TEXT );

        assertEquals( TEXT, Jsoup.parseBodyFragment( strStored ).body( ).wholeText( ) );
        assertEquals( TEXT, SuggestUtils.toPlainText( strStored ) );
    }

    /**
     * Leaves the value of a request the core already sanitized untouched.
     */
    @Test
    public void testSanitizeResponsesLeavesSanitizedRequests( )
    {
        List<Response> listResponse = responses( "L&#39;idée" );

        assertNull( SuggestUtils.sanitizeResponses( new EntryTypeText( ), listResponse, new MockHttpServletRequest( ), Locale.FRENCH ) );
        assertEquals( "L&#39;idée", listResponse.get( 0 ).getValueResponse( ) );
    }

    /**
     * Accepts a web address as a link, refuses a script link, and leaves a BBCode value as typed.
     */
    @Test
    public void testSanitizeResponsesLinkAndBbcode( )
    {
        List<Response> listUrl = responses( "https://www.paris.fr/page?a=1&b=2" );
        assertNull( SuggestUtils.sanitizeResponses( new EntryTypeUrl( ), listUrl, multipartRequest( ), Locale.FRENCH ) );
        assertEquals( "https://www.paris.fr/page?a=1&amp;b=2", listUrl.get( 0 ).getValueResponse( ) );
        assertNotNull( SuggestUtils.sanitizeResponses( new EntryTypeUrl( ), responses( "javascript:alert(1)" ), multipartRequest( ), Locale.FRENCH ) );

        List<Response> listBbcode = responses( "[b]L'idée[/b]" );
        assertNull( SuggestUtils.sanitizeResponses( new EntryTypeEditorBbcode( ), listBbcode, multipartRequest( ), Locale.FRENCH ) );
        assertEquals( "[b]L'idée[/b]", listBbcode.get( 0 ).getValueResponse( ) );
    }

    /**
     * Builds a multipart request.
     *
     * @return the request
     */
    private static MockHttpServletRequest multipartRequest( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setContentType( "multipart/form-data; boundary=x" );

        return request;
    }

    /**
     * Builds the responses of an entry holding one value.
     *
     * @param strValue
     *            the value
     * @return the responses
     */
    private static List<Response> responses( String strValue )
    {
        Response response = new Response( );
        response.setValueResponse( strValue );

        return List.of( response );
    }
}
