<%@page import="fr.paris.lutece.portal.web.pluginaction.IPluginActionResult"%>
<jsp:useBean id="suggestSuggest" scope="session" class="fr.paris.lutece.plugins.suggest.web.SuggestJspBean" /><%
suggestSuggest.init( request,fr.paris.lutece.plugins.suggest.web.ManageSuggestJspBean.RIGHT_MANAGE_SUGGEST_LIKE);
IPluginActionResult result = suggestSuggest.getManageSuggestSubmit( request, response );

if ( result.getRedirect() != null ) {
	response.sendRedirect(result.getRedirect());
} else if ( result.getHtmlContent() != null ) {
	
%>
<jsp:include page="../../AdminHeader.jsp" />
<%= result.getHtmlContent(  ) %>
<%@ include file="../../AdminFooter.jsp" %>
<% } %><%@ page errorPage="../../ErrorPage.jsp" %>