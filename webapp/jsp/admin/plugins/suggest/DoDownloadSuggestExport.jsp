<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="suggestSuggest" scope="session" class="fr.paris.lutece.plugins.suggest.web.SuggestJspBean" />
<%
	suggestSuggest.init( request, fr.paris.lutece.plugins.suggest.web.ManageSuggestJspBean.RIGHT_MANAGE_SUGGEST_LIKE );
   String strResult =  suggestSuggest.doDownloadSuggestExport(request,response);
  if (!response.isCommitted())
  {
 	 response.sendRedirect(strResult);
   }
%>
