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
package fr.paris.lutece.plugins.digglike.utils;

import java.io.Serializable;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 * Copyright 2004 JavaFree.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * $Id: ProcessBBCode.java,v 1.18.2.2.4.4 2007/04/17 17:27:08 daltoncamargo Exp
 * $
 * @author Dalton Camargo - <a href="mailto:dalton@javabb.org">dalton@javabb.org
 *         </a> <br>
 * @author Ronald Tetsuo Miura
 */
public class ProcessBBCode implements Serializable
{
    private static final long serialVersionUID = 2923413457835274235L;
    private static final String CR_LF = "(?:\r\n|\r|\n)?";

    /** */
    private boolean acceptHTML = false;

    /** */
    private boolean acceptBBCode = true;

    /**
     * @return acceptBBCode
     */
    public boolean getAcceptBBCode(  )
    {
        return acceptBBCode;
    }

    /**
     * @param acceptBBCode the new acceptBBCode value
     */
    public void setAcceptBBCode( boolean acceptBBCode )
    {
        this.acceptBBCode = acceptBBCode;
    }

    /**
     * @return htmlAccepted
     */
    public boolean getAcceptHTML(  )
    {
        return acceptHTML;
    }

    /**
     * @param acceptHTML the new acceptHTML value
     */
    public void setAcceptHTML( boolean acceptHTML )
    {
        this.acceptHTML = acceptHTML;
    }

    /**
     * @param texto
     * @return TODO unuseful parameters.
     */
    public String preparePostText( String texto )
    {
        if ( !getAcceptHTML(  ) )
        {
            texto = escapeHtml( texto );
        }

        if ( getAcceptBBCode(  ) )
        {
            texto = process( texto );
        }

        return texto;
    }

    /**
     * @param string
     * @return HTML-formated message
     */
    private String process( String string )
    {
        string = string.replaceAll( "(\r\n|\n\r|\n|\r)", "<br>" );

        StringBuffer buffer = new StringBuffer( string );
        processCode( buffer );

        processNestedTags( buffer, "quote",
            "<table width='90%' cellspacing='1' cellpadding='1' border='0' " +
            "align='center'><tr><td><span class='genmed'><b>{BBCODE_PARAM}:</b></span></td></tr></table><table width='90%' cellspacing='1' cellpadding='1' border='0' align='center'><tr><td class='quote' bgcolor='#F3F5FF'>",
            " &nbsp;</td></tr></table>",
            "<table width='90%' cellspacing='1' cellpadding='3' border='0' " +
            "align='center'><tr><td><span class='genmed'><b>Quote:</b></span></td></tr><tr><td class='quote'>",
            " &nbsp;</td></tr></table>", "[*]", false, true, true );

        processNestedTags( buffer, "list", "<ol type=\"{BBCODE_PARAM}\">", "</ol>", "<ul>", "</ul>", "<li>", true,
            true, false );

        String str = buffer.toString(  );

        //str = str.replaceAll("(\r\n|\n\r|\n|\r)", "<br>");

        // [color]
        str = str.replaceAll( "\\[color=['\"]?(.*?[^'\"])['\"]?\\](.*?)\\[/color\\]", "<span style='color:$1'>$2</span>" );

        // [size]
        str = str.replaceAll( "\\[size=['\"]?([0-9]|[1-5][0-9])['\"]?\\](.*?)\\[/size\\]",
                "<span style='font-size:$1px'>$2</span>" );
        // [font]
        // [color]
        str = str.replaceAll( "\\[font=['\"]?(.*?[^'\"])['\"]?\\](.*?)\\[/font\\]", "<font face='$1'>$2</font>" );

        // [b][u][i]
        str = str.replaceAll( "\\[b\\](.*?)\\[/b\\]", "<b>$1</b>" );
        str = str.replaceAll( "\\[u\\](.*?)\\[/u\\]", "<u>$1</u>" );
        str = str.replaceAll( "\\[i\\](.*?)\\[/i\\]", "<i>$1</i>" );

        // [img]
        str = str.replaceAll( "\\[img\\](.*?)\\[/img\\]", "<img src='$1' border='0' alt='0'>" );

        // [url]
        str = str.replaceAll( "\\[url\\](.*?)\\[/url\\]", "<a href='$1' target='_blank'>$1</a>" );
        str = str.replaceAll( "\\[url=['\"]?(.*?[^'\"])['\"]?\\](.*?)\\[/url\\]",
                "<a href=\"$1\" target=\"_new\">$2</a>" );

        // [email]
        str = str.replaceAll( "\\[email\\](.*?)\\[/email\\]", "<a href='mailto:$1'>$1</a>" );

        return str;
    }

    private static void processCode( StringBuffer buffer )
    {
        int start = buffer.indexOf( "[code]" );
        int end;

        for ( ; ( start >= 0 ) && ( start < buffer.length(  ) ); start = buffer.indexOf( "[code]", end ) )
        {
            end = buffer.indexOf( "[/code]", start );

            if ( end < 0 )
            {
                break;
            }

            end += "[/code]".length(  );

            String content = buffer.substring( start + "[code]".length(  ), end - "[/code]".length(  ) );
            content = escapeBBcode( content );

            /*
             * String replacement = "<!-- [ -code- ] --></span>"
             * +
             * "<table width='90%' cellspacing='1' cellpadding='3' border='0' align='center'>"
             * + "<tr><td><span class='genmed'><b>Code:</b></span></td></tr>"
             * + "<tr><td class='code'>"
             * + content
             * +
             * "</td></tr></table><span class='postbody'><!-- [/ -code- ] -->";
             */
            content = content.replaceAll( "<br>", "\n" );

            String replacement = "<!-- [ -code- ] --></span>" //
                 +"<textarea name=\"code\" id=\"code\" class=\"java\" rows=\"15\" cols=\"100\">" + content +
                "</textarea><span class='postbody'><!-- [/ -code- ] -->";

            buffer.replace( start, end, replacement );

            end = start + replacement.length(  );
        }
    }

    /**
     * @param content
     * @return -
     */
    public static String escapeBBcode( String content )
    {
        // escaping single characters
        content = replaceAll( content, "[]\t".toCharArray(  ), new String[] { "&#91;", "&#93;", "&nbsp; &nbsp;" } );

        // taking off start and end line breaks
        content = content.replaceAll( "\\A\r\n|\\A\r|\\A\n|\r\n\\z|\r\\z|\n\\z", "" );

        // replacing spaces for &nbsp; to keep indentation
        content = content.replaceAll( "  ", "&nbsp; " );
        content = content.replaceAll( "  ", " &nbsp;" );

        return content;
    }

    /**
     * @param content
     * @return -
     */
    private static String escapeHtml( String content )
    {
        // escaping single characters
        content = replaceAll( content, "&<>".toCharArray(  ), new String[] { "&amp;", "&lt;", "&gt;" } );

        // replacing line breaks for <br>
        //content = content.replaceAll("\r\n", "<br>");
        //content = replaceAll(content, "\n\r".toCharArray(), new String[] { "<br>", "<br>" });
        return content;
    }

    private static String replaceAll( String str, char[] chars, String[] replacement )
    {
        StringBuffer buffer = new StringBuffer(  );

        for ( int i = 0; i < str.length(  ); i++ )
        {
            char c = str.charAt( i );
            boolean matched = false;

            for ( int j = 0; j < chars.length; j++ )
            {
                if ( c == chars[j] )
                {
                    buffer.append( replacement[j] );
                    matched = true;
                }
            }

            if ( !matched )
            {
                buffer.append( c );
            }
        }

        return buffer.toString(  );
    }

    /**
     * @param buffer
     * @param tagName
     * @param openSubstWithParam
     * @param closeSubstWithParam
     * @param openSubstWithoutParam
     * @param closeSubstWithoutParam
     * @param internalSubst
     * @param processInternalTags
     * @param acceptParam
     * @param requiresQuotedParam
     */
    private static void processNestedTags( StringBuffer buffer, String tagName, String openSubstWithParam,
        String closeSubstWithParam, String openSubstWithoutParam, String closeSubstWithoutParam, String internalSubst,
        boolean processInternalTags, boolean acceptParam, boolean requiresQuotedParam )
    {
        String str = buffer.toString(  );

        Stack<MutableCharSequence> openStack = new Stack<MutableCharSequence>(  );
        Set<MutableCharSequence> subsOpen = new HashSet<MutableCharSequence>(  );
        Set<MutableCharSequence> subsClose = new HashSet<MutableCharSequence>(  );
        Set<MutableCharSequence> subsInternal = new HashSet<MutableCharSequence>(  );

        String openTag = CR_LF + "\\[" + tagName +
            ( acceptParam ? ( requiresQuotedParam ? "(?:=\"(.*?)\")?" : "(?:=\"?(.*?)\"?)?" ) : "" ) + "\\]" + CR_LF;
        String closeTag = CR_LF + "\\[/" + tagName + "\\]" + CR_LF;
        String internTag = CR_LF + "\\[\\*\\]" + CR_LF;

        String patternString = "(" + openTag + ")|(" + closeTag + ")";

        if ( processInternalTags )
        {
            patternString += ( "|(" + internTag + ")" );
        }

        Pattern tagsPattern = Pattern.compile( patternString );
        Matcher matcher = tagsPattern.matcher( str );

        int openTagGroup;
        int paramGroup;
        int closeTagGroup;
        int internalTagGroup;

        if ( acceptParam )
        {
            openTagGroup = 1;
            paramGroup = 2;
            closeTagGroup = 3;
            internalTagGroup = 4;
        }
        else
        {
            openTagGroup = 1;
            paramGroup = -1; // INFO
            closeTagGroup = 2;
            internalTagGroup = 3;
        }

        while ( matcher.find(  ) )
        {
            int length = matcher.end(  ) - matcher.start(  );
            MutableCharSequence matchedSeq = new MutableCharSequence( str, matcher.start(  ), length );

            // test opening tags
            if ( matcher.group( openTagGroup ) != null )
            {
                if ( acceptParam && ( matcher.group( paramGroup ) != null ) )
                {
                    matchedSeq.param = matcher.group( paramGroup );
                }

                openStack.push( matchedSeq );

                // test closing tags
            }
            else if ( ( matcher.group( closeTagGroup ) != null ) && !openStack.isEmpty(  ) )
            {
                MutableCharSequence openSeq = openStack.pop(  );

                if ( acceptParam )
                {
                    matchedSeq.param = openSeq.param;
                }

                subsOpen.add( openSeq );
                subsClose.add( matchedSeq );

                // test internal tags
            }
            else if ( processInternalTags && ( matcher.group( internalTagGroup ) != null ) && ( !openStack.isEmpty(  ) ) )
            {
                subsInternal.add( matchedSeq );
            }
            else
            {
                // assert (false);
            }
        }

        LinkedList<MutableCharSequence> subst = new LinkedList<MutableCharSequence>(  );
        subst.addAll( subsOpen );
        subst.addAll( subsClose );
        subst.addAll( subsInternal );

        Collections.sort( subst,
            new Comparator<MutableCharSequence>(  )
            {
                public int compare( MutableCharSequence o1, MutableCharSequence o2 )
                {
                    return -( o1.start - o2.start );
                }
            } );

        buffer.delete( 0, buffer.length(  ) );

        int start = 0;

        while ( !subst.isEmpty(  ) )
        {
            MutableCharSequence seq = subst.removeLast(  );
            buffer.append( str.substring( start, seq.start ) );

            if ( subsClose.contains( seq ) )
            {
                if ( seq.param != null )
                {
                    buffer.append( closeSubstWithParam );
                }
                else
                {
                    buffer.append( closeSubstWithoutParam );
                }
            }
            else if ( subsInternal.contains( seq ) )
            {
                buffer.append( internalSubst );
            }
            else if ( subsOpen.contains( seq ) )
            {
                Matcher m = Pattern.compile( openTag ).matcher( str.substring( seq.start, seq.start + seq.length ) );

                if ( m.matches(  ) )
                {
                    if ( acceptParam && ( seq.param != null ) )
                    {
                        buffer.append(  //
                            openSubstWithParam.replaceAll( "\\{BBCODE_PARAM\\}", seq.param ) );
                    }
                    else
                    {
                        buffer.append( openSubstWithoutParam );
                    }
                }
            }

            start = seq.start + seq.length;
        }

        buffer.append( str.substring( start ) );
    }

    static class MutableCharSequence implements CharSequence
    {
        /** */
        public CharSequence base;

        /** */
        public int start;

        /** */
        public int length;

        /** */
        public String param = null;

        /**
         */
        public MutableCharSequence(  )
        {
            //
        }

        /**
         * @param base
         * @param start
         * @param length
         */
        public MutableCharSequence( CharSequence base, int start, int length )
        {
            reset( base, start, length );
        }

        /**
         * @see java.lang.CharSequence#length()
         */
        public int length(  )
        {
            return this.length;
        }

        /**
         * @see java.lang.CharSequence#charAt(int)
         */
        public char charAt( int index )
        {
            return this.base.charAt( this.start + index );
        }

        /**
         * @see java.lang.CharSequence#subSequence(int, int)
         */
        public CharSequence subSequence( int pStart, int end )
        {
            return new MutableCharSequence( this.base, this.start + pStart, this.start + ( end - pStart ) );
        }

        /**
         * @param pBase
         * @param pStart
         * @param pLength
         * @return -
         */
        public CharSequence reset( CharSequence pBase, int pStart, int pLength )
        {
            this.base = pBase;
            this.start = pStart;
            this.length = pLength;

            return this;
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString(  )
        {
            StringBuffer sb = new StringBuffer(  );

            for ( int i = this.start; i < ( this.start + this.length ); i++ )
            {
                sb.append( this.base.charAt( i ) );
            }

            return sb.toString(  );
        }
    }
}
