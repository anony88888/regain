<%@page contentType="text/html" errorPage="errorpage.jsp"%>

<html>
<head>
  <title>regain - Kein Index vorhanden</title>
  <script src="regain.js" type="text/javascript"></script>
  <link href="regain.css" rel="stylesheet" type="text/css">
</head>

<body>
  <table class="top"><tr>
    <td><img src="img/logo_regain.gif" width="201" height="66"></td>
  </tr></table>

  <table class="content">
    <tr class="headline"><td>
      <b>Kein Index vorhanden</b>
    </td></tr>
    <tr><td>
      <h4>Kein Index vorhanden</h4>
      
      <p>
        Bevor mit regain gesucht werden kann, muss zuerst ein Suchindex erstellt
        werden. Bitte geben Sie dazu in der
        <a href="config.jsp">Einstellungen-Seite</a> an, welche Verzeichnisse
        und Webseiten Sie in den Suchindex aufnehmen wollen.
      </p>
      <p>
        Nachdem Sie diese Angaben gemacht haben, wird ein neuer Suchindex
        erstellt. Sie können den Fortschritt der Indexierung auf der
        <a href="status.jsp">Status-Seite</a> mitverfolgen.
      </p>
      <br>
    </td></tr>
  </table>

  <%@include file="footer.jsp" %>

</body>
</html>