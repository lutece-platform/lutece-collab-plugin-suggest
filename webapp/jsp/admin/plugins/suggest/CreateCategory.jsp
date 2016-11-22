<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />
<jsp:useBean id="suggestCategory" scope="session" class="fr.paris.lutece.plugins.suggest.web.CategoryJspBean" />
<% suggestCategory.init( request, fr.paris.lutece.plugins.suggest.web.ManageSuggestJspBean.RIGHT_MANAGE_SUGGEST_LIKE);%>
<%= suggestCategory.getCreateCategory( request ) %>
<%@ include file="../../AdminFooter.jsp" %>