<%@page contentType="text/html"%>
<%@taglib uri="SearchLib.tld" prefix="search" %>

<html>
<head>
  <title><search:msg key="errorSearchingFor"/> <search:input_query/></title>
</head>

<body>

  <%
  Throwable exc = (Throwable) request.getAttribute("javax.servlet.jsp.jspException");
  %>

  <p>
  <search:msg key="error.checkInput"/><br>
  <br>
  <search:msg key="errorMessage"/>:
  <%
  out.print(exc.getMessage());
  %>
  <br>
  <search:msg key="error.moreInfo"/><br>
  </p>

  <form name="search" action="search.jsp" method="get">
    <search:msg key="error.searchFor"/>:
    <search:input_hiddenparam name="index"/>
    <search:input_query/>
    <search:input_maxresults/>
    <search:input_submit text="{msg:search}"/>
  </form>
  
  <%-- Add the stack trace as hidden text --%>

  <pre style="color:FFFFFF; font-size:small;">  
  Stacktrace:
  <%
  exc.printStackTrace(new java.io.PrintWriter(out));
  %>
  </pre>

</body>
</html>
