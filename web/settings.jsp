<%@page contentType="text/html" errorPage="errorpage.jsp"%>
<%@taglib uri="SearchLib.tld" prefix="search" %>

<html>
<head>
  <title>regain - Einstellungen</title>
  <script src="regain.js" type="text/javascript"></script>
</head>

<body>

  <settings:form action="settings.jsp">
    Indexierungsintervall: <settings:interval/>
    
    <p>
      Verzeichnisse:<br>
      <settings:editlist name="dirlist" class="blubb"/>
    </p>
    
    <p>
      Ausgeschlossene Verzeichnisse:<br>
      <settings:editlist name="dirblacklist" class="blubb"/>
    </p>
    
    <p>
      Webseiten:<br>
      <settings:editlist name="sitelist" class="blubb"/>
    </p>
    
    <p>
      Ausgeschlossene Webseiten-Unterverzeichnisse:<br>
      <settings:editlist name="siteblacklist" class="blubb"/>
    </p>

    <input type="submit" value="Einstellungen speichern">
  </settings:form>

  <!--
  <form name="settings" action="Settings.jsp" method="post">
    Indexierungsintervall:
	  <select name="interval">
			<option value="60">Eine Stunde</option>
			<option value="1440">Ein Tag</option>
			<option value="10080">Eine Woche</option>
		</select>
		
		<p>
		Verzeichnisse:<br>
		<select id="dirlist" name="dirlist" size="5">
			<option>e:\Eigene Dateien</option>
			<option>c:\Temp</option>
		</select>
		<br>
		<input type="text" size="20" id="dirlist-entry">
		<button type="button" onClick="addToList('dirlist')">Hinzufügen</button>
		<button type="button" onClick="removeFromList('dirlist')">Entfernen</button>
		</p>
		
		<p>
		Ausgeschlossene Verzeichnisse:<br>
		<select id="blacklist" name="blacklist" size="5">
			<option>e:\Eigene Dateien\Studium</option>
		</select>
		<br>
		<input type="text" size="20" id="blacklist-entry">
		<button type="button" onClick="addToList('blacklist')">Hinzufügen</button>
		<button type="button" onClick="removeFromList('blacklist')">Entfernen</button>
		</p>
		
		<p>
		Webseiten:<br>
		<select id="sitelist" name="sitelist" size="5">
			<option>www.murfman.de</option>
		</select>
		<br>
		<input type="text" size="20" id="sitelist-entry">
		<button type="button" onClick="addToList('sitelist')">Hinzufügen</button>
		<button type="button" onClick="removeFromList('sitelist')">Entfernen</button>
		</p>
    
    <input type="submit" value="Einstellungen speichern">
    
  </form>
  -->

</body>
</html>