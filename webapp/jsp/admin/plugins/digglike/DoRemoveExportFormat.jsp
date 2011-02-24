<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="digglikeExport" scope="session" class="fr.paris.lutece.plugins.digglike.web.ExportFormatJspBean" />


<% 
	digglikeExport.init( request, fr.paris.lutece.plugins.digglike.web.ManageDigglikeJspBean.RIGHT_MANAGE_DIGG_LIKE );
    response.sendRedirect( digglikeExport.doRemoveExportFormat( request ) );
%>