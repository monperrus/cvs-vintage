<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/app.tld"    prefix="app" %>
<%@ taglib uri="/WEB-INF/struts.tld" prefix="struts" %>

<html>
<head>
<struts:ifParameterEquals name="action" value="Create">
  <title><struts:message key="subscription.title.create"/></title>
</struts:ifParameterEquals>
<struts:ifParameterEquals name="action" value="Delete">
  <title><struts:message key="subscription.title.delete"/></title>
</struts:ifParameterEquals>
<struts:ifParameterEquals name="action" value="Edit">
  <title><struts:message key="subscription.title.edit"/></title>
</struts:ifParameterEquals>
</head>
<body bgcolor="white">

<struts:errors/>

<struts:form action="saveSubscription.do" name="subscriptionForm"
               type="org.apache.struts.example.SubscriptionForm">
<table border="0" width="100%">

  <tr>
    <th align="right">
      <struts:message key="prompt.username"/>
    </th>
    <td align="left">
        <jsp:getProperty name="user" property="username"/>
    </td>
  </tr>

  <tr>
    <th align="right">
      <struts:message key="prompt.mailHostname"/>
    </th>
    <td align="left">
      <struts:text name="host" size="50"/>
    </td>
  </tr>

  <tr>
    <th align="right">
      <struts:message key="prompt.mailUsername"/>
    </th>
    <td align="left">
      <struts:text name="username" size="50"/>
    </td>
  </tr>

  <tr>
    <th align="right">
      <struts:message key="prompt.mailPassword"/>
    </th>
    <td align="left">
      <struts:password name="password" size="50"/>
    </td>
  </tr>

  <tr>
    <th align="right">
      <struts:message key="prompt.mailServerType"/>
    </th>
    <td align="left">
<%--
      <struts:select name="type">
        <struts:option value="pop3">
          <struts:message key="option.pop3"/>
        </struts:option>
        <struts:option value="imap">
          <struts:message key="option.imap"/>
        </struts:option>
      </struts:select>
--%>
      <struts:radio name="type" value="imap">
        <struts:message key="option.imap"/>
      </struts:radio>
      <struts:radio name="type" value="pop3">
        <struts:message key="option.pop3"/>
      </struts:radio>
    </td>
  </tr>

  <tr>
    <td align="right">
      <struts:ifParameterNotEquals name="action" value="Delete">
        <struts:submit>
          <struts:message key="button.save"/>
        </struts:submit>
      </struts:ifParameterNotEquals>
      <struts:ifParameterEquals name="action" value="Delete">
        <struts:submit>
          <struts:message key="button.confirm"/>
        </struts:submit>
      </struts:ifParameterEquals>
    </td>
    <td align="left">
      <struts:ifParameterNotEquals name="action" value="Delete">
        <struts:reset>
          <struts:message key="button.reset"/>
        </struts:reset>
      </struts:ifParameterNotEquals>
      &nbsp;
      <struts:submit>
        <struts:message key="button.cancel"/>
      </struts:submit>
    </td>
  </tr>

</table>

<input type="hidden" name="action"
 value="<struts:parameter name="action"/>">

</struts:form>
