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
package fr.paris.lutece.plugins.suggest.business.rss;


/**
 *
 * SuggestResourceRssConfig
 *
 */
public class SuggestResourceRssConfig
{
    private int _nIdRss;
    private int _nIdSuggest;
    private boolean _bisSubmitRss;
    private int _nIdSuggestSubmit;

    /**
    *
    * @return id Rss
    */
    public int getIdRss(  )
    {
        return _nIdRss;
    }

    /**
     * set id Rss
     * @param idRss id Rss
     */
    public void setIdRss( int idRss )
    {
        _nIdRss = idRss;
    }

    /**
    *
    * @return id suggest
    */
    public int getIdSuggest(  )
    {
        return _nIdSuggest;
    }

    /**
     * set id suggest
     * @param idSuggest id suggest
     */
    public void setIdSuggest( int idSuggest )
    {
        _nIdSuggest = idSuggest;
    }

    /**
    *
    * @return true if it is a submit RSS
    */
    public boolean isSubmitRss(  )
    {
        return _bisSubmitRss;
    }

    /**
     * set true if it is a submit RSS
     * @param isSubmitRss true if it is a submit RSS
     */
    public void setSubmitRss( boolean isSubmitRss )
    {
        _bisSubmitRss = isSubmitRss;
    }

    /**
    *
    * @return id suggestSubmit
    */
    public int getIdSuggestSubmit(  )
    {
        return _nIdSuggestSubmit;
    }

    /**
     * set id suggestSubmit
     * @param idSuggestSubmit id suggestSubmit
     */
    public void setIdSuggestSubmit( int idSuggestSubmit )
    {
        _nIdSuggestSubmit = idSuggestSubmit;
    }
}
