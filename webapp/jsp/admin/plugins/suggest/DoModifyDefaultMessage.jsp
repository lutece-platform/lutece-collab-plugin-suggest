<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="suggestDefaultMessage" scope="session" class="fr.paris.lutece.plugins.suggest.web.DefaultMessageJspBean" />


<% 
	suggestDefaultMessage.init( request, fr.paris.lutece.plugins.suggest.web.ManageSuggestJspBean.RIGHT_MANAGE_SUGGEST_LIKE);
    response.sendRedirect( suggestDefaultMessage.doModifyDefaultMessage( request ) );
%>
