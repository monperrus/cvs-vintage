<%@ taglib uri="WEB-INF/struts-logic.tld" prefix="logic" %>
<logic:redirect forward="welcome"/>

<%--

Redirect default requests to Welcome global ActionForward.
By using a redirect, the user-agent will change address to match the path of our Welcome ActionForward. 

--%>
