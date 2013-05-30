/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.digglike.business;

import fr.paris.lutece.portal.service.image.ImageResource;
import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.List;


/**
 *
 *  Interface IDiggSubmitTypeDAO
 *
 */
public interface IDiggSubmitTypeDAO
{
    /**
    * Insert a new record in the table.
    *
    * @param diggSubmitType instance of the Digg Submit Type object to insert
    * @param plugin the plugin
    * @return the id of the new Digg
    */
    int insert( DiggSubmitType diggSubmitType, Plugin plugin );

    /**
     * Load the data of the diggSubmitType from the table
     *
     * @param nIdDiggSubmitType The identifier of the formResponse
     * @param plugin the plugin
     * @return the instance of the diggSubmitType
     */
    DiggSubmitType load( int nIdDiggSubmitType, Plugin plugin );

    /**
     * Load the image of the diggSubmitType from the table
     *
     * @param nIdDiggSubmitType The identifier of the formResponse
     * @param plugin the plugin
     * @return the instance of the ImageResource
     */
    ImageResource loadImage( int nIdDiggSubmitType, Plugin plugin );

    /**
     * Delete   the digg submit type whose identifier is specified in parameter
     *
     * @param nIdDiggSubmitType The identifier of the digg submit
     * @param plugin the plugin
     */
    void delete( int nIdDiggSubmitType, Plugin plugin );

    /**
     * Update the the diggSubmitType in the table
     *
     * @param diggSubmitType instance of the diggSubmit object to update
     * @param plugin the plugin
     */
    void store( DiggSubmitType diggSubmitType, Plugin plugin );

    /**
     * Load the list of the diggSubmitType from the table
     *
     * @param plugin the plugin
     * @return the instance of the diggSubmitType
     */
    List<DiggSubmitType> selectList( Plugin plugin );
    
    /**
     * Load the list of the diggSubmitType from the table
     * @param nIdDigg the id Digg
     * @param plugin the plugin
     * @return the instance of the diggSubmitType
     */
    List<DiggSubmitType> selectListByIdDigg(int nIdDigg, Plugin plugin );
    
    /**
     * true if there is a  digg associate to the digg submit type
     * @param nIdType the key of the type
     * @param plugin the plugin
     * @return true if there is a digg associate to the type
     */
    boolean isAssociateToDigg( int nIdType, Plugin plugin );
    
       /**
     * Delete an association between digg and a digg submit type
     *
     * @param nIdDigg The identifier of the digg
     * @param nIdDiggSubmitType nIdDiggSubmitType
     * @param plugin the plugin
     */
    void deleteDiggAssociation( int nIdDigg, int nIdDiggSubmitType, Plugin plugin );
    /**
     * insert an association between digg and a digg submit type
     *
     * @param nIdDigg The identifier of the digg
     * @param nIdDiggSubmitType nIdDiggSubmitType
     * @param plugin the plugin
     */
    void insertDiggAssociation( int nIdDigg, int nIdDiggSubmitType, Plugin plugin );
    
}
