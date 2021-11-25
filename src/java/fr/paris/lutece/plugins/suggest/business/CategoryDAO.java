/*
 * Copyright (c) 2002-2020, Mairie de Paris
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
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * class CategoryDAO
 *
 */
public class CategoryDAO implements ICategoryDAO
{
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_category) FROM suggest_category";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_category,title,color FROM suggest_category WHERE id_category=?";
    private static final String SQL_QUERY_SELECT = "SELECT id_category,title,color FROM suggest_category ORDER BY title";
    private static final String SQL_QUERY_INSERT = "INSERT INTO suggest_category (id_category,title,color )VALUES(?,?,?)";
    private static final String SQL_QUERY_UPDATE = "UPDATE suggest_category SET id_category=?,title=?,color=? WHERE id_category=?";
    private static final String SQL_QUERY_DELETE = "DELETE FROM suggest_category WHERE id_category = ? ";
    private static final String SQL_QUERY_COUNT_NUMBER_OF_SUGGEST_ASSOCIATE_TO_THE_CATEGORY = "select COUNT(id_category) "
            + " FROM suggest_suggest_category WHERE id_category=? ";
    private static final String SQL_QUERY_SELECT_BY_ID_SUGGEST = "SELECT c.id_category,c.title,c.color FROM suggest_category c,suggest_suggest_category dc "
            + "WHERE c.id_category=dc.id_category AND dc.id_suggest=? ORDER BY title";
    private static final String SQL_QUERY_DELETE_ASSOCIATED_CATEGORIE = "DELETE FROM suggest_suggest_category WHERE id_suggest = ? and id_category= ? ";
    private static final String SQL_QUERY_INSERT_ASSOCIATED_CATEGORY = "INSERT INTO suggest_suggest_category(id_suggest,id_category) VALUES(?,?) ";

    /**
     * Generates a new primary key
     *
     * @param plugin
     *            the plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin ) )
        {
            daoUtil.executeQuery( );
            daoUtil.next( );
            return daoUtil.getInt( 1 ) + 1;
        }
    }

    /**
     * Insert a new record in the table.
     *
     * @param category
     *            instance of the Category object to insert
     * @param plugin
     *            the plugin
     */
    @Override
    public void insert( Category category, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            category.setIdCategory( newPrimaryKey( plugin ) );
            daoUtil.setInt( 1, category.getIdCategory( ) );
            daoUtil.setString( 2, category.getTitle( ) );
            daoUtil.setString( 3, category.getColor( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * update record in the table.
     *
     * @param category
     *            instance of the Category object to update
     * @param plugin
     *            the plugin
     */
    @Override
    public void store( Category category, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            daoUtil.setInt( 1, category.getIdCategory( ) );
            daoUtil.setString( 2, category.getTitle( ) );
            daoUtil.setString( 3, category.getColor( ) );
            daoUtil.setInt( 4, category.getIdCategory( ) );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * Load the data of the category from the table
     *
     * @param idKey
     *            The identifier of the category
     * @param plugin
     *            the plugin
     * @return the instance of the Category
     */
    @Override
    public Category load( int idKey, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin ) )
        {
            daoUtil.setInt( 1, idKey );
            daoUtil.executeQuery( );

            Category category = null;

            if ( daoUtil.next( ) )
            {
                category = new Category( );
                category.setIdCategory( daoUtil.getInt( 1 ) );
                category.setTitle( daoUtil.getString( 2 ) );
                category.setColor( daoUtil.getString( 3 ) );
            }

            return category;
        }
    }

    /**
     * Load the data of all category returns them in a list
     * 
     * @param plugin
     *            the plugin
     * @return the list of category
     */
    @Override
    public List<Category> select( Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.executeQuery( );

            Category category;
            List<Category> listCategory = new ArrayList<>( );

            while ( daoUtil.next( ) )
            {
                category = new Category( );
                category.setIdCategory( daoUtil.getInt( 1 ) );
                category.setTitle( daoUtil.getString( 2 ) );
                category.setColor( daoUtil.getString( 3 ) );
                listCategory.add( category );
            }

            return listCategory;
        }
    }

    /**
     * Delete a record from the table
     *
     * @param nIdCategory
     *            The identifier of the category
     * @param plugin
     *            the plugin
     */
    @Override
    public void delete( int nIdCategory, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nIdCategory );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * true if there is a suggest associate to the category
     * 
     * @param nIdCategory
     *            the key of the category
     * @param plugin
     *            the plugin
     * @return true if there is a suggest associate to the category
     */
    @Override
    public boolean isAssociateToSuggest( int nIdCategory, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_NUMBER_OF_SUGGEST_ASSOCIATE_TO_THE_CATEGORY, plugin ) )
        {
            daoUtil.setInt( 1, nIdCategory );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                if ( daoUtil.getInt( 1 ) != 0 )
                {
                    daoUtil.free( );

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public List<Category> select( int nIdSuggest, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_SUGGEST, plugin ) )
        {
            daoUtil.setInt( 1, nIdSuggest );
            daoUtil.executeQuery( );

            Category category;
            List<Category> listCategory = new ArrayList<>( );

            while ( daoUtil.next( ) )
            {
                category = new Category( );
                category.setIdCategory( daoUtil.getInt( 1 ) );
                category.setTitle( daoUtil.getString( 2 ) );
                category.setColor( daoUtil.getString( 3 ) );
                listCategory.add( category );
            }

            return listCategory;
        }
    }

    /**
     * Delete an association between suggest and categories
     *
     * @param nIdSuggest
     *            The identifier of the suggest
     * @param nIdCategory
     *            The identifier of the category
     * @param plugin
     *            the plugin
     */
    @Override
    public void deleteSuggestAssociation( int nIdSuggest, int nIdCategory, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ASSOCIATED_CATEGORIE, plugin ) )
        {
            daoUtil.setInt( 1, nIdSuggest );
            daoUtil.setInt( 2, nIdCategory );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * insert an association between suggest and a category
     *
     * @param nIdSuggest
     *            The identifier of the category
     * @param nIdCategory
     *            The identifier of the category
     * @param plugin
     *            the plugin
     */
    @Override
    public void insertSuggestAssociation( int nIdSuggest, int nIdCategory, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_ASSOCIATED_CATEGORY, plugin ) )
        {
            daoUtil.setInt( 1, nIdSuggest );
            daoUtil.setInt( 2, nIdCategory );
            daoUtil.executeUpdate( );
        }
    }
}
