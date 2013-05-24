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
package fr.paris.lutece.plugins.digglike.web.action;

import java.io.Serializable;
import java.util.List;

/**
 * Visualization of all needed session values. Many features depends on search
 * result or paginator. Those fields may be required for actions.
 * 
 */
public final class DigglikeAdminSearchFields implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int _nIdDigg;
	private int _nIdDiggSumitState=-1;
	private int _nIdDiggSubmitSort=-1;
	private int _nIdDiggSubmitReport=-1;
	private String _strQuery;
	private  List<String> _selectedDiggSubmit;

	public int getIdDiggSubmitSort() {
		return _nIdDiggSubmitSort;
	}

	public void setIdDiggSubmitSort(int nIdDiggSubmitSort) {
		this._nIdDiggSubmitSort = nIdDiggSubmitSort;
	}

	public int getIdDiggSubmitReport() {
		return _nIdDiggSubmitReport;
	}

	public void setIdDiggSubmitReport(int nIdDiggSubmitReport) {
		this._nIdDiggSubmitReport = nIdDiggSubmitReport;
	}

	public int getIdDiggSumitState() {
		return _nIdDiggSumitState;
	}

	public void setIdDiggSumitState(int nIdDiggSumitState) {
		this._nIdDiggSumitState = nIdDiggSumitState;
	}

	public String getQuery() {
		return _strQuery;
	}

	public void setQuery(String strQuery) {
		this._strQuery = strQuery;
	}

	public List<String>getSelectedDiggSubmit() {
		return _selectedDiggSubmit;
	}

	public void setSelectedDiggSubmit(List<String> selectedDiggSubmit) {
		this._selectedDiggSubmit = selectedDiggSubmit;
	}

	public int getIdDigg() {
		return _nIdDigg;
	}

	public void setIdDigg(int _nIdDigg) {
		this._nIdDigg = _nIdDigg;
	}

}
