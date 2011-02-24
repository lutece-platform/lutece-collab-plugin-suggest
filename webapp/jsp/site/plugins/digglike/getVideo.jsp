<%@ page import="fr.paris.lutece.portal.service.util.AppLogService"%>
<jsp:useBean id="digglikeDiggGetVideo" scope="session" class="fr.paris.lutece.plugins.digglike.web.DigglikeVideoResourceServlet" />
<%
 try
 {
  byte[] fileContent = digglikeDiggGetVideo.processRequest(request, response);
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
