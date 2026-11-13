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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests the options of the file field.
 */
public class EntryTypeFileTest
{
    /**
     * Builds a file field with the given options.
     *
     * @param strMaxFiles
     *            the maximum number of files
     * @param strMaxFileSize
     *            the maximum size in MB
     * @param strExtensions
     *            the extensions
     * @return the file field
     */
    private static EntryTypeFile entry( String strMaxFiles, String strMaxFileSize, String strExtensions )
    {
        EntryTypeFile entry = new EntryTypeFile( );
        entry.setEntryAdditionalAttributeList( List.of( attribute( EntryTypeFile.ATTRIBUTE_MAX_FILES, strMaxFiles ),
                attribute( EntryTypeFile.ATTRIBUTE_MAX_FILE_SIZE, strMaxFileSize ), attribute( EntryTypeFile.ATTRIBUTE_EXTENSIONS, strExtensions ) ) );

        return entry;
    }

    /**
     * Builds an option.
     *
     * @param strName
     *            the name
     * @param strValue
     *            the value
     * @return the option
     */
    private static EntryAdditionalAttribute attribute( String strName, String strValue )
    {
        EntryAdditionalAttribute attribute = new EntryAdditionalAttribute( );
        attribute.setName( strName );
        attribute.setValue( strValue );

        return attribute;
    }

    /**
     * The options of the field are read, and the accepted extensions match whatever the case.
     */
    @Test
    public void testOptions( )
    {
        EntryTypeFile entry = entry( "3", "2", " .PDF, docx ;png" );

        assertEquals( 3, entry.getMaxFiles( ) );
        assertEquals( 2L * 1024 * 1024, entry.getMaxFileSizeBytes( ) );
        assertEquals( "pdf,docx,png", entry.getExtensions( ) );
        assertEquals( ".pdf,.docx,.png", entry.getAccept( ) );
        assertTrue( entry.isAcceptedFileName( "Rapport.PDF" ) );
        assertFalse( entry.isAcceptedFileName( "outil.exe" ) );
        assertFalse( entry.isAcceptedFileName( "sans-extension" ) );
    }

    /**
     * Missing or invalid options fall back to the defaults.
     */
    @Test
    public void testDefaults( )
    {
        EntryTypeFile entry = entry( "0", "abc", "" );

        assertEquals( 5, entry.getMaxFiles( ) );
        assertEquals( 10, entry.getMaxFileSize( ) );
        assertTrue( entry.isAcceptedFileName( "schema.pptx" ) );
        assertEquals( 5, new EntryTypeFile( ).getMaxFiles( ) );
    }
}
