<%@page contentType="text/html"%>

<html>
<head>
  <title>Suchanfrage</title>
</head>

<body>

  <form name="search" action="search.jsp" method="get">
    <p>
      <b>Suchen nach: </b>
      <input name="query" size="44"/>
    </p>
    <p>
      <input name="maxresults" size="4" value="10"/>&nbsp;Ergebnisse pro Seite&nbsp;
      <input type="submit" value="Search"/>
    </p>
  </form>

</body>
</html>
