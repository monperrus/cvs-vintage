<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/app.tld"    prefix="app" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-form.tld" prefix="form" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<app:checkLogon/>

<%-- In real life, these would be loaded from a database --%>
<%
  java.util.ArrayList list = new java.util.ArrayList();
  list.add(new org.apache.struts.example.LabelValueBean("IMAP Protocol", "imap"));
  list.add(new org.apache.struts.example.LabelValueBean("POP3 Protocol", "pop3"));
  pageContext.setAttribute("serverTypes", list);
%>

<form:html>
<head>
<logic:equal name="subscriptionForm" property="action"
            scope="request" value="Create">
  <title><bean:message key="subscription.title.create"/></title>
</logic:equal>
<logic:equal name="subscriptionForm" property="action"
            scope="request" value="Delete">
  <title><bean:message key="subscription.title.delete"/></title>
</logic:equal>
<logic:equal name="subscriptionForm" property="action"
            scope="request" value="Edit">
  <title><bean:message key="subscription.title.edit"/></title>
</logic:equal>
<form:base/>
</head>
<body bgcolor="white">

<form:errors/>

<form:form action="saveSubscription.do" name="subscriptionForm"
            focus="host"
            scope="request"
             type="org.apache.struts.example.SubscriptionForm">
<form:hidden property="action"/>
<table border="0" width="100%">

  <tr>
    <th align="right">
      <bean:message key="prompt.username"/>
    </th>
    <td align="left">
        <bean:write name="user" property="username" filter="true"/>
    </td>
  </tr>

  <tr>
    <th align="right">
      <bean:message key="prompt.mailHostname"/>
    </th>
    <td align="left">
      <form:textarea property="host" cols="50" rows="1"/>
    </td>
  </tr>

  <tr>
    <th align="right">
      <bean:message key="prompt.mailUsername"/>
    </th>
    <td align="left">
      <form:text property="username" size="50"/>
    </td>
  </tr>

  <tr>
    <th align="right">
      <bean:message key="prompt.mailPassword"/>
    </th>
    <td align="left">
      <form:password property="password" size="50"/>
    </td>
  </tr>

  <tr>
    <th align="right">
      <bean:message key="prompt.mailServerType"/>
    </th>
    <td align="left">
      <form:select property="type">
        <form:options collection="serverTypes" property="value"
                   labelProperty="label"/>
      </form:select>
    </td>
  </tr>

  <tr>
    <th align="right">
      <bean:message key="prompt.autoConnect"/>
    </th>
    <td align="left">
      <form:checkbox property="autoConnect"/>
    </td>
  </tr>

  <tr>
    <td align="right">
      <logic:equal name="subscriptionForm" property="action"
                  scope="request" value="Create">
        <form:submit>
          <bean:message key="button.save"/>
        </form:submit>
      </logic:equal>
      <logic:equal name="subscriptionForm" property="action"
                  scope="request" value="Delete">
        <form:submit>
          <bean:message key="button.confirm"/>
        </form:submit>
      </logic:equal>
      <logic:equal name="subscriptionForm" property="action"
                  scope="request" value="Edit">
        <form:submit>
          <bean:message key="button.save"/>
        </form:submit>
      </logic:equal>
    </td>
    <td align="left">
      <logic:notEqual name="subscriptionForm" property="action"
                     scope="request" value="Delete">
        <form:reset>
          <bean:message key="button.reset"/>
        </form:reset>
      </logic:notEqual>
      &nbsp;
      <form:cancel>
        <bean:message key="button.cancel"/>
      </form:cancel>
    </td>
  </tr>

</table>

</form:form>

</body>
</form:html>
