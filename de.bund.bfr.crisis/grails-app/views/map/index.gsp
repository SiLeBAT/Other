<html>
<head>
  <!-- Integrate with Sitemesh layouts           -->
  <meta name="layout" content="bare" />

  <!--                                           -->
  <!-- Any title is fine                         -->
  <!--                                           -->
  <title>FoodChainLab Map</title>

  <!--                                           -->
  <!-- This script loads your compiled module.   -->
  <!-- If you add any GWT meta tags, they must   -->
  <!-- be added before this line.                -->
  <!--                                           -->
  <script type="text/javascript" src="${resource(dir: 'gwt/de.bund.bfr.crisis.TracingApp', file: 'de.bund.bfr.crisis.TracingApp.nocache.js')}"></script>
  
  <script src="http://www.openlayers.org/api/OpenLayers.js"></script>
  <script src="http://www.openstreetmap.org/openlayers/OpenStreetMap.js"></script>
  <asset:stylesheet src="map.css"/>
</head>

<!--                                           -->
<!-- The body can have arbitrary html, or      -->
<!-- you can leave the body empty if you want  -->
<!-- to create a completely dynamic ui         -->
<!--                                           -->
<body>
  <!-- OPTIONAL: include this if you want history support -->
  <iframe id="__gwt_historyFrame" style="width:0;height:0;border:0"></iframe>

  <!-- Add the rest of the page here, or leave it -->
  <!-- blank for a completely dynamic interface.  -->
  <div id="mapContainer"></div>
  
</body>
</html>
