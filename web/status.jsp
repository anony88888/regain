<%@page contentType="text/html" errorPage="errorpage.jsp"%>

<html>
<head>
  <title>regain - Status</title>
  <script src="regain.js" type="text/javascript"></script>
  <link href="regain.css" rel="stylesheet" type="text/css">
  <status:autoupdate_meta/>
</head>

<body>
  <table class="top"><tr>
    <td><img src="img/logo_regain.gif" width="201" height="66"></td>
  </tr></table>

  <table class="content">
    <tr class="headline"><td>
      <b>Status</b>
    </td></tr>
    <tr><td>
      <h4>Automatisch aktualisieren</h4>
      <p>
        <status:autoupdate_form url="status.jsp"/>
      </p>

      <h4>Aktueller Suchindex</h4>
      <p>
        <status:currentindex/>
      </p>

      <h4>Laufende Indexierung</h4>
      <p>
        <status:indexupdate/>
      </p>
      <br>
    </td></tr>
  </table>

  <%@include file="footer.jsp" %>

</body>
</html>