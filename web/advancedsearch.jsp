<%@page contentType="text/html" errorPage="errorpage.jsp"%>
<%@taglib uri="SearchLib.tld" prefix="search" %>

<html>
<head>
  <title>regain - Erweiterte Suchen</title>
  <script src="regain.js" type="text/javascript"></script>
  <link href="regain.css" rel="stylesheet" type="text/css">
</head>

<body>
  <table class="top"><tr>
    <td><img src="img/logo_regain.gif" width="201" height="66"></td>
  </tr></table>

  <table class="content">
    <tr class="headline"><td>
      <b>Erweiterte Suche</b>
    </td></tr>
    <tr><td>

      <form name="search" action="search.jsp" method="get">
        <br/>
        <table>
          <tr>
            <td>Suchen nach:</td>
            <td><input name="query" size="40"/></td>
          </tr>
          <tr>
            <td>Dateiendung:</td>
            <td><search:input_fieldlist field="extension"/></td>
          </tr>
          <tr>
            <td></td><td><input type="submit" value="Suchen"/></td>
          </tr>
        </table>
      </form>

      <br>
    </td></tr>
  </table>

  <%@include file="footer.jsp" %>

</body>
</html>
