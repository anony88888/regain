<%@page contentType="text/html" errorPage="errorpage.jsp"%>

<html>
<head>
  <title>regain - Einstellungen</title>
  <script src="regain.js" type="text/javascript"></script>
  <link href="regain.css" rel="stylesheet" type="text/css">
</head>

<body>
  <table class="top"><tr>
    <td><img src="img/logo_regain.gif" width="201" height="66"></td>
  </tr></table>

  <table class="content">
    <tr class="headline"><td>
      <b>Einstellungen</b>
    </td></tr>
    <tr><td>
      <config:form action="config.jsp">
        <br/>
        <p>
          Indexierungsintervall: <config:interval/>
        </p>
        
        <p>
          <h4>Verzeichnisse</h4>
          <div class="hint">Geben Sie ein Verzeichnis an und dr&uuml;cken Sie auf 'Hinzuf&uuml;gen'.</div>
          <config:editlist name="dirlist" class="editlist"/>
        </p>
        
        <p>
          <h4>Ausgeschlossene Verzeichnisse</h4>
          <div class="hint">Geben Sie ein Verzeichnis an und dr&uuml;cken Sie auf 'Hinzuf&uuml;gen'.</div>
          <config:editlist name="dirblacklist" class="editlist"/>
        </p>
        
        <p>
          <h4>Webseiten</h4>
          <div class="hint">Geben Sie eine Webseite an und dr&uuml;cken Sie auf 'Hinzuf&uuml;gen'.</div>
          <config:editlist name="sitelist" class="editlist"/>
        </p>
        
        <p>
          <h4>Ausgeschlossene Webseiten-Unterverzeichnisse</h4>
          <div class="hint">Geben Sie eine Webseite an und dr&uuml;cken Sie auf 'Hinzuf&uuml;gen'.</div>
          <config:editlist name="siteblacklist" class="editlist"/>
        </p>
    
        <p>
          <br>
          <input type="submit" value="Einstellungen speichern">
        </p>
      </config:form>
    </td></tr>
  </table>

  <%@include file="footer.jsp" %>

</body>
</html>