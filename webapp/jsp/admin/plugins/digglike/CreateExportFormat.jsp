<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />
<jsp:useBean id="digglikeExport" scope="session" class="fr.paris.lutece.plugins.digglike.web.ExportFormatJspBean" />
<% digglikeExport.init( request, fr.paris.lutece.plugins.digglike.web.ManageDigglikeJspBean.RIGHT_MANAGE_DIGG_LIKE ); %>
<%= digglikeExport.getCreateExportFormat( request ) %>
<%@ include file="../../AdminFooter.jsp" %>