<%@page contentType="text/html" errorPage="errorpage.jsp"%>

<html>
<head>
  <title>regain - Einstellungen</title>
  <script src="regain.js" type="text/javascript"></script>
</head>

<body>

  <config:form action="config.jsp">
    Indexierungsintervall: <config:interval/>
    
    <p>
      Verzeichnisse:<br>
      <config:editlist name="dirlist" class="blubb"/>
    </p>
    
    <p>
      Ausgeschlossene Verzeichnisse:<br>
      <config:editlist name="dirblacklist" class="blubb"/>
    </p>
    
    <p>
      Webseiten:<br>
      <config:editlist name="sitelist" class="blubb"/>
    </p>
    
    <p>
      Ausgeschlossene Webseiten-Unterverzeichnisse:<br>
      <config:editlist name="siteblacklist" class="blubb"/>
    </p>

    <input type="submit" value="Einstellungen speichern">
  </config:form>

</body>
</html>