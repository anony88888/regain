<%@page contentType="text/html" errorPage="errorpage.jsp"%>
<%@taglib uri="SearchLib.tld" prefix="search" %>

<html>
<head>
  <title>regain - Suche nach <search:param name="query"/></title>
</head>

<body>

  <form name="search" action="search.jsp" method="get">
    <search:input_hiddenparam name="index"/>
    <p>
      <b>Suchen nach: </b>
      <search:input_query/>
      <search:input_maxresults/>
      <input type="submit" value="Search"/>
    </p>
  </form>

  <br/>

  <table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr bgcolor="#0000EE">
      <td><font color="#FFFFFF">
        &nbsp;
        Ergebnisse für <b><search:param name="query"/></b>
      </font></td>
      <td align="right"><font color="#FFFFFF">
        Ergebnisse <b><search:stats_from/></b>-<b><search:stats_to/></b>
        von insgesamt <b><search:stats_total/></b>.
        Suchdauer: <b><search:stats_searchtime/></b> Sekunden.
        &nbsp;
      </font></td>
    </tr>

    <tr><td colspan="2"> <br/> </td></tr>

    <search:list msgNoResults="<tr><td colspan='2'>Es wurden leider keine Treffer gefunden!</td></tr>">
      <tr><td colspan="2">
        <search:hit_link/> <small>(Relevanz: <search:hit_score/>)</small><br/>
        <small>
          <search:hit_summary/><br/>
          <search:hit_path after="<br/>" createLinks="false"/>
          <font color="#008000"><search:hit_url/> - <search:hit_size/></font><br/>
          <br/>
        </small>
      </td></tr>
    </search:list>
  </table>

  <br/>

  Ergebnisseite:
  <search:navigation
  	targetPage="search.jsp"
  	msgBack="&lt;&lt; Zur&uuml;ck"
  	msgForward="Weiter &gt;&gt;"/>

  <br/>

  <form name="search" action="search.jsp" method="get">
    <search:input_hiddenparam name="index"/>
    <p>
      <b>Suchen nach: </b>
      <search:input_query/>
      <search:input_maxresults/>
      <input type="submit" value="Search"/>
    </p>
  </form>

</body>
</html>
