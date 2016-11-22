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
package fr.paris.lutece.plugins.suggest.business;

import java.util.List;


/**
 *
 * class vote type
 *
 */
public class VoteType
{
    private int _nIdVoteType;
    private String _strTitle;
    private List<VoteButton> _listVoteButtons;
    private String _strTemplateFileName;

    /**
         *
         * @return the id of the vote type
         */
    public int getIdVoteType(  )
    {
        return _nIdVoteType;
    }

    /**
     * set the id of the vote type
     * @param idVoteType the id of the vote type
     */
    public void setIdVoteType( int idVoteType )
    {
        _nIdVoteType = idVoteType;
    }

    /**
     *
     * @return the title of the vote type
     */
    public String getTitle(  )
    {
        return _strTitle;
    }

    /**
     * set the title of the vote type
     * @param title the title of the vote type
     */
    public void setTitle( String title )
    {
        _strTitle = title;
    }

    /**
     *
     * @return the list of vote buttons associated to the vote type
     */
    public List<VoteButton> getVoteButtons(  )
    {
        return _listVoteButtons;
    }

    /**
     * set the list of vote buttons associated to the vote type
     * @param voteButtons the list of vote buttons associated to the vote type
     */
    public void setVoteButtons( List<VoteButton> voteButtons )
    {
        _listVoteButtons = voteButtons;
    }

    /**
     *
     * @return the template file name
     */
    public String getTemplateFileName(  )
    {
        return _strTemplateFileName;
    }

    /**
     * set the template file name
     * @param templateFileName the template file name
     */
    public void setTemplateFileName( String templateFileName )
    {
        _strTemplateFileName = templateFileName;
    }
}
