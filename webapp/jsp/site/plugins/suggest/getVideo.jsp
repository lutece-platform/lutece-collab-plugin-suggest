<%@ page import="fr.paris.lutece.portal.service.util.AppLogService"%>
<jsp:useBean id="suggestSuggestGetVideo" scope="session" class="fr.paris.lutece.plugins.suggest.web.SuggestVideoResourceServlet" />
<%
 try
 {
  byte[] fileContent = suggestSuggestGetVideo.processRequest(request, response);
  if (fileContent != null)
  {
   response.getOutputStream().write(fileContent);
  }
 } 
 catch (Exception e)
 {
	 AppLogService.error(e.getMessage(), e);
 }
 finally{
  response.getOutputStream().flush();
 }
%>
