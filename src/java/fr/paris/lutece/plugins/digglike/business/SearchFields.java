package fr.paris.lutece.plugins.digglike.business;

import java.io.Serializable;

public class SearchFields implements Serializable{
	
	
	private static final long serialVersionUID = 2512978426472997213L;
	private String _strQuery;
	private String _strPageIndex;
	private int _nIdFilterPeriod=SubmitFilter.ALL_INT;
	private int _nIdDiggSubmitSort=SubmitFilter.ALL_INT;
	private int _nIdFilterCategory=SubmitFilter.ALL_INT;
	
	
	public String getQuery() {
		return _strQuery;
	}
	public void setQuery(String strQuery) {
		this._strQuery = strQuery;
	}

	
	public int getIdFilterPeriod() {
		return _nIdFilterPeriod;
	}
	public void setIdFilterPeriod(int nIdFilterPeriod) {
		this._nIdFilterPeriod = nIdFilterPeriod;
	}
	
	public int getIdDiggSubmitSort() {
		return _nIdDiggSubmitSort;
	}
	public void setIdDiggSubmitSort(int nIdDiggSubmitSort) {
		this._nIdDiggSubmitSort = nIdDiggSubmitSort;
	}
	public int getIdFilterCategory() {
		return _nIdFilterCategory;
	}
	public void setIdFilterCategory(int nIdFilterCategory) {
		this._nIdFilterCategory = nIdFilterCategory;
	}
	public String getPageIndex() {
		return _strPageIndex;
	}
	public void setPageIndex(String _strPageIndex) {
		this._strPageIndex = _strPageIndex;
	}
}
