<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="suggestExport" scope="session" class="fr.paris.lutece.plugins.suggest.web.ExportFormatJspBean" />
<% 
	suggestExport.init( request, fr.paris.lutece.plugins.suggest.web.ManageSuggestJspBean.RIGHT_MANAGE_SUGGEST_LIKE );
   String strResult =  suggestExport.doDownloadXslFile(request,response);
  if (!response.isCommitted())
  {
 	 response.sendRedirect(strResult);
   }
%>
