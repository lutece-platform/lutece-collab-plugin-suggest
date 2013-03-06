/*
 * Copyright (c) 2002-2012, Mairie de Paris
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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;


/**
 *
 *class category Home
 *
 */
public final class CategoryHome
{
    // Static variable pointed at the DAO instance
    private static ICategoryDAO _dao = (ICategoryDAO) SpringContextService.getPluginBean( "digglike",
            "digglike.categoryDAO" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private CategoryHome(  )
    {
    }

    /**
     * Creation of an instance of  category
     *
     * @param category The instance of the Category which contains the informations to store
     * @param plugin the Plugin
      */
    public static void create( Category category, Plugin plugin )
    {
        _dao.insert( category, plugin );
    }

    /**
     * Update of the category which is specified in parameter
     *
     * @param category The instance of the Category which contains the informations to update
     * @param plugin the Plugin
     *
     */
    public static void update( Category category, Plugin plugin )
    {
        _dao.store( category, plugin );
    }

    /**
     * Remove the category whose identifier is specified in parameter
     *
     * @param nIdCategory The category Id
     * @param plugin the Plugin
     */
    public static void remove( int nIdCategory, Plugin plugin )
    {
        _dao.delete( nIdCategory, plugin );
    }

    /**
     * Returns an instance of a category whose identifier is specified in parameter
     *
     * @param idKey The category primary key
     * @param plugin the Plugin
     * @return an instance of category
     */
    public static Category findByPrimaryKey( int idKey, Plugin plugin )
    {
        return _dao.load( idKey, plugin );
    }

    /**
        * Returns a list of all category
        *
        * @param plugin the plugin
        * @return  the list of category
        */
    public static List<Category> getList( Plugin plugin )
    {
        return _dao.select( plugin );
    }

    /**
     * true if there is a  digg associate to the category
     * @param nIdCategory the key of the category
     * @param plugin the plugin
     * @return true if there is a digg associate to the category
     */
    public static boolean isAssociateToDigg( int nIdCategory, Plugin plugin )
    {
        return _dao.isAssociateToDigg( nIdCategory, plugin );
    }
    /**
     * Returns a list of all category associate to the digg
     * @param nIdDigg the id digg
     * @param plugin the plugin
     * @return  the list of category
     */
 public static List<Category> getListByIdDigg(int nIdDigg,Plugin plugin )
 {
     return _dao.select(nIdDigg, plugin );
 } 
}
