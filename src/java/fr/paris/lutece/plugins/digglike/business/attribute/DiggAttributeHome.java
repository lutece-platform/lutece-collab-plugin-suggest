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
package fr.paris.lutece.plugins.digglike.business.attribute;

import java.util.Map;
import java.util.Map.Entry;

import fr.paris.lutece.plugins.digglike.service.DigglikePlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;


/**
 *
 * DiggAttributeHome
 *
 */
public final class DiggAttributeHome
{
    private static final String BEAN_DIGG_ATTRIBUTE_DAO = "digglike.diggAttributeDAO";
    private static Plugin _plugin = PluginService.getPlugin( DigglikePlugin.PLUGIN_NAME );
    private static IDiggAttributeDAO _dao = SpringContextService.getBean( BEAN_DIGG_ATTRIBUTE_DAO );

    /**
     * Load the attributes of the digg
     * @param nIdDigg the id digg
     * @return a map of key - value
     */
    public static Map<String, Object> findByPrimaryKey( int nIdDigg )
    {
        return _dao.load( nIdDigg, _plugin );
    }

    /**
     * create a digg Attribute
     * @param nIdDigg the id of the digg
     * @param strAttributeKey the attribute key	
     * @param attributeValue the attribute value
     */
    public static void create( int nIdDigg, String strAttributeKey, Object attributeValue )
    {
        _dao.insert( nIdDigg, strAttributeKey, attributeValue, _plugin );
    }

    /**
     * Create the attribute of the digg
     * @param nIdDigg the if of the digg
     * @param mapAttributes the map of attributes
     */
    public static void create( int nIdDigg, Map<String, Object> mapAttributes )
    {
        for ( Entry<String, Object> attribute : mapAttributes.entrySet(  ) )
        {
            create( nIdDigg, attribute.getKey(  ), attribute.getValue(  ) );
        }
    }

    /**
     * Remove the attributes of the digg
     * @param nIdDigg the id digg
     */
    public static void remove( int nIdDigg )
    {
        _dao.remove( nIdDigg, _plugin );
    }
}
