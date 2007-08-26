<?xml version="1.0"?>

<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  >

<xsl:template match="/">
<html>

<head>
<title>MEGUNI</title>
<link type="text/css" href="css/global.css" rel="stylesheet"/>
</head>

<body>
<center>
  <div id="logo"><a href="index.html">MEGUNI</a></div><br/>
</center>

<xsl:copy-of select="/xhtml:html/xhtml:body" xmlns:xhtml="http://www.w3.org/1999/xhtml"/>

<center>
<table>
<tr><td align="middle">
<a href="acknowledgements.html">Acknowledgements</a>&#160;&#160;<a href="login.html">Login</a>&#160;&#160;<a href="community.html">Community</a>&#160;&#160;<a href="about.html">About</a>
</td></tr>

<tr><td align="middle">
Copyright &#169; 2005 MEGUNI
</td></tr>
</table>
</center>
</body>
</html>
</xsl:template>

</xsl:stylesheet>
