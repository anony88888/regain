<%@page contentType="text/html" errorPage="errorpage.jsp"%>
<%@taglib uri="SearchLib.tld" prefix="search" %>

<html>
<head>
  <title>regain - Suche nach <search:param name="query"/></title>
  <link href="regain.css" rel="stylesheet" type="text/css">
</head>

<body>

  <form name="search" action="search.jsp" method="get">
    <p class="top">
      <img src="img/logo_regain.gif" width="201" height="66" class="logo">
      Suchen nach:
      <search:input_hiddenparam name="index"/>
      <search:input_query/>
      <search:input_maxresults/>
      <input type="submit" value="Search"/>
    </p>
  </form>

  <table class="content">
    <tr class="headline">
      <td>
        Ergebnisse für <b><search:param name="query"/></b>
      </td>
      <td class="headlineRight">
        Ergebnisse <b><search:stats_from/></b>-<b><search:stats_to/></b>
        von insgesamt <b><search:stats_total/></b>.
        Suchdauer: <b><search:stats_searchtime/></b> Sekunden.
        &nbsp;
      </td>
    </tr>

    <tr><td colspan="2"> <br/> </td></tr>

    <search:list msgNoResults="<tr><td colspan='2'>Es wurden leider keine Treffer gefunden!<br/><br/></td></tr>">
      <tr><td colspan="2">
        <search:hit_link/>
        <span class="hitDetails">
        (Relevanz: <search:hit_score/>)<br/>
        <search:hit_summary/><br/>
        <search:hit_path after="<br/>" createLinks="false"/>
        <span class="hitInfo"><search:hit_url/> - <search:hit_size/></span><br/>
        <br/></span>
      </td></tr>
    </search:list>
  </table>

  <p class="navigation">
    <img src="img/logo_regain_small.gif" width="121" height="40"><br />
    Ergebnisseite:
    <search:navigation
            targetPage="search.jsp"
            msgBack="<img src='img/back.gif' title='Zur&uuml;ck' border='0'/>"
            msgForward="<img src='img/forward.gif' title='Weiter' border='0'/>"/>
  </p>

  <br/>

  <form name="search" action="search.jsp" method="get">
    <search:input_hiddenparam name="index"/>
    <table class="searchBottom"><tr><td>
      <b>Suchen nach: </b>
      <search:input_query/>
      <search:input_maxresults/>
      <input type="submit" value="Search"/>
    </td></tr></table>
  </form>

</body>
</html>
