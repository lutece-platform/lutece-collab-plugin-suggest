<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />
<jsp:useBean id="suggestSuggest" scope="session" class="fr.paris.lutece.plugins.suggest.web.SuggestJspBean" />
<%
	suggestSuggest.init( request,fr.paris.lutece.plugins.suggest.web.ManageSuggestJspBean.RIGHT_MANAGE_SUGGEST_LIKE);
%>
<%= suggestSuggest.getCreateEntry( request ) %>
<%@ include file="../../AdminFooter.jsp" %>