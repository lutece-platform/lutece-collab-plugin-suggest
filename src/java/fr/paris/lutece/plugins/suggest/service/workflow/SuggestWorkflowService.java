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
package fr.paris.lutece.plugins.suggest.service.workflow;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.suggest.business.Suggest;
import fr.paris.lutece.plugins.suggest.business.SuggestSubmit;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;

/**
 * Workflow service methods for plugin-suggest
 */
public class SuggestWorkflowService
{
    public static final String BEAN_NAME = "suggest.suggestWorkflowService";

    /**
     * Private constructor
     */
    private SuggestWorkflowService( )
    {
    }

    /**
     * Process the automatic workflow actions when a SuggestSubmit is created
     * 
     * @param suggest
     *            The Suggest associated with the workflow
     * @param suggestSubmit
     *            The SuggestSubmit processing the workflow action
     * @param user
     *            The user
     */
    public static void processActionOnSuggestSubmitCreation( Suggest suggest, SuggestSubmit suggestSubmit, User user )
    {
        CDI.current( ).select( WorkflowService.class ).get( ).getState( suggestSubmit.getIdSuggestSubmit( ), SuggestSubmit.RESOURCE_TYPE, suggest.getIdWorkflow( ),
                suggest.getIdSuggest( ) );
        CDI.current( ).select( WorkflowService.class ).get( ).executeActionAutomatic( suggestSubmit.getIdSuggestSubmit( ), SuggestSubmit.RESOURCE_TYPE, suggest.getIdWorkflow( ),
                suggest.getIdSuggest( ), user );
    }

    /**
     * Remove the resources linked to the specified workflow and consultation
     * 
     * @param nIdWorkflow
     *            The workflow's ID
     * @param nIdSuggest
     *            The consultation's ID
     * @param user
     *            The user performing the remove action
     */
    public static void removeResources( int nIdWorkflow, int nIdSuggest, User user )
    {
        WorkflowService workflowService = CDI.current( ).select( WorkflowService.class ).get( );
        if ( workflowService.isAvailable( ) )
        {
            List<Integer> listIdWorkflowState = getListIdWorkflowState( nIdWorkflow, user );
            List<Integer> listIdResources = workflowService.getAuthorizedResourceList( SuggestSubmit.RESOURCE_TYPE, nIdWorkflow, listIdWorkflowState,
                    nIdSuggest, user );

            workflowService.doRemoveWorkFlowResourceByListId( listIdResources, SuggestSubmit.RESOURCE_TYPE, nIdWorkflow );
        }
    }

    /**
     * Retrieve the list of state identifiers for the specified workflow. The list is filtered depending on the permissions of the specified user.
     * 
     * @param nIdWorkflow
     *            the workflow id
     * 
     * @param adminUser
     *            the user
     * 
     * @return the list of workflow state identifiers
     */
    public static List<Integer> getListIdWorkflowState( int nIdWorkflow, User user )
    {
        List<Integer> listIdState = new ArrayList<>( );
        Collection<State> collectionStates = CDI.current( ).select( WorkflowService.class ).get( ).getAllStateByWorkflow( nIdWorkflow, user );

        for ( State state : collectionStates )
        {
            listIdState.add( state.getId( ) );
        }
        return listIdState;
    }

    /**
     * Returns the current workflow state of a suggest submit, read without creating its workflow resource.
     *
     * @param suggest
     *            the suggest owning the submit
     * @param suggestSubmit
     *            the suggest submit
     * @return the state, or null when the suggest has no workflow or the workflow is not installed
     */
    public static State getState( Suggest suggest, SuggestSubmit suggestSubmit )
    {
        Instance<IStateService> stateService = getStateService( );

        if ( ( suggest.getIdWorkflow( ) <= 0 ) || stateService.isUnsatisfied( ) )
        {
            return null;
        }

        return stateService.get( ).findByResource( suggestSubmit.getIdSuggestSubmit( ), SuggestSubmit.RESOURCE_TYPE, suggest.getIdWorkflow( ) );
    }

    /**
     * Returns every state of the workflow of a suggest, in workflow order.
     *
     * @param suggest
     *            the suggest
     * @return the states, empty when the suggest has no workflow or the workflow is not installed
     */
    public static List<State> getStates( Suggest suggest )
    {
        Instance<IStateService> stateService = getStateService( );

        if ( ( suggest.getIdWorkflow( ) <= 0 ) || stateService.isUnsatisfied( ) )
        {
            return Collections.emptyList( );
        }

        StateFilter filter = new StateFilter( );
        filter.setIdWorkflow( suggest.getIdWorkflow( ) );

        List<State> listStates = new ArrayList<>( stateService.get( ).getListStateByFilter( filter ) );
        listStates.sort( Comparator.comparingInt( State::getOrder ) );

        return listStates;
    }

    /**
     * Returns the workflow state service, unsatisfied when plugin-workflow is not deployed.
     *
     * @return the state service instance
     */
    private static Instance<IStateService> getStateService( )
    {
        return CDI.current( ).select( IStateService.class, NamedLiteral.of( StateService.BEAN_SERVICE ) );
    }
}
