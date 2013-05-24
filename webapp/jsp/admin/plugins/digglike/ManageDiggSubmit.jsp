<%@page import="fr.paris.lutece.portal.web.pluginaction.IPluginActionResult"%>
<jsp:useBean id="digglikeDigg" scope="session" class="fr.paris.lutece.plugins.digglike.web.DiggJspBean" /><%
digglikeDigg.init( request,fr.paris.lutece.plugins.digglike.web.ManageDigglikeJspBean.RIGHT_MANAGE_DIGG_LIKE);
IPluginActionResult result = digglikeDigg.getManageDiggSubmit( request, response );

if ( result.getRedirect() != null ) {
	response.sendRedirect(result.getRedirect());
} else if ( result.getHtmlContent() != null ) {
	
%>
<jsp:include page="../../AdminHeader.jsp" />
<%= result.getHtmlContent(  ) %>
<%@ include file="../../AdminFooter.jsp" %>
<% } %><%@ page errorPage="../../ErrorPage.jsp" %>