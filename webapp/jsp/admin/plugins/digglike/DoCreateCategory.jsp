<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:useBean id="digglikeCategory" scope="session" class="fr.paris.lutece.plugins.digglike.web.CategoryJspBean" />

<% 
	digglikeCategory.init( request, fr.paris.lutece.plugins.digglike.web.ManageDigglikeJspBean.RIGHT_MANAGE_DIGG_LIKE);
    response.sendRedirect( digglikeCategory.doCreateCategory(request) );
%>
