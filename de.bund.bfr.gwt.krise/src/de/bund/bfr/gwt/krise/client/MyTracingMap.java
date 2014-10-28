package de.bund.bfr.gwt.krise.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.Projection;
import org.gwtopenmaps.openlayers.client.Style;
import org.gwtopenmaps.openlayers.client.StyleMap;
import org.gwtopenmaps.openlayers.client.control.LayerSwitcher;
import org.gwtopenmaps.openlayers.client.control.OverviewMap;
import org.gwtopenmaps.openlayers.client.control.ScaleLine;
import org.gwtopenmaps.openlayers.client.control.SelectFeature;
import org.gwtopenmaps.openlayers.client.event.MapMoveEndListener;
import org.gwtopenmaps.openlayers.client.event.VectorFeatureSelectedListener;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.filter.ComparisonFilter;
import org.gwtopenmaps.openlayers.client.filter.ComparisonFilter.Types;
import org.gwtopenmaps.openlayers.client.geometry.LineString;
import org.gwtopenmaps.openlayers.client.geometry.Point;
import org.gwtopenmaps.openlayers.client.layer.OSM;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.layer.VectorOptions;
import org.gwtopenmaps.openlayers.client.popup.FramedCloud;
import org.gwtopenmaps.openlayers.client.popup.Popup;
import org.gwtopenmaps.openlayers.client.strategy.AnimatedClusterStrategy;
import org.gwtopenmaps.openlayers.client.strategy.AnimatedClusterStrategyOptions;
import org.gwtopenmaps.openlayers.client.strategy.ClusterStrategy;
import org.gwtopenmaps.openlayers.client.strategy.Strategy;
import org.gwtopenmaps.openlayers.client.style.Rule;
import org.gwtopenmaps.openlayers.client.style.SymbolizerPoint;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;

import de.bund.bfr.gwt.krise.shared.Delivery;
import de.bund.bfr.gwt.krise.shared.MyTracingGISData;
import de.bund.bfr.gwt.krise.shared.Station;

public class MyTracingMap extends MapWidget {

	private static final Projection DEFAULT_PROJECTION = new Projection("EPSG:4326"); //transform lonlat (provided in EPSG:4326) to OSM coordinate system (the map projection)
	private static Projection MAP_PROJ = null;
	static MapOptions defaultMapOptions = new MapOptions();

	private final HsqldbServiceAsync hsqldbService;

	ClusterStrategy clusterStrategy = null; // AnimatedClusterStrategy
	private Vector stationLayer = null, deliveryLayer = null, labelLayer = null;

	private LinkedHashMap<Integer, Station> stations = null;
	private LinkedHashMap<Integer, HashSet<VectorFeature>> deliveries = null;

	private Map theMap = null;
	
	private long lastDeliveryRefresh = 0;

	public MyTracingMap() {
		this((HsqldbServiceAsync) GWT.create(HsqldbService.class));
	}

	public MyTracingMap(HsqldbServiceAsync hsqldbService) {
		super("100%", "100%", defaultMapOptions);
		this.hsqldbService = hsqldbService;
		buildPanel();
		fetchMyData("");
	}

	public void fillMap(MyTracingGISData result) {
		if (result != null) {
			stationLayer.removeAllFeatures();
			stations = result.getStations();
			VectorFeature[] features = new VectorFeature[stations.size()];
			int i = 0;
			for (Station station : stations.values()) {
				features[i] = addStation2Feature(station);
				i++;
			}

			stationLayer.addFeatures(features);
			clusterStrategy.setFeatures(features);

			deliveries = new LinkedHashMap<Integer, HashSet<VectorFeature>>();
			//Style ds = createDeliveryStyle();
			HashSet<Delivery> hs = result.getDeliveries();
			for (Delivery d : hs) {
				VectorFeature vf = addDelivery2Feature(d.getId(), d.getFrom(), d.getTo()); // , ds
				if (!deliveries.containsKey(d.getFrom())) deliveries.put(d.getFrom(), new HashSet<VectorFeature>());
				HashSet<VectorFeature> hd = deliveries.get(d.getFrom());
				hd.add(vf);
				if (!deliveries.containsKey(d.getTo())) deliveries.put(d.getTo(), new HashSet<VectorFeature>());
				hd = deliveries.get(d.getTo());
				hd.add(vf);
			}
			
			addDeliveries();
			
			centerTheMap(6);
		}
	}

	private void fetchMyData(String station) {
		MyCallbackGIS myCallback = new MyCallbackGIS(this);
		hsqldbService.getGISData(station, myCallback);
	}

	private void addClusterStrategy() {
		Rule[] rules = new Rule[3];

		ComparisonFilter filter0 = new ComparisonFilter();
		filter0.setType(Types.BETWEEN);
		filter0.setProperty("count");
		filter0.setNumberLowerBoundary(2);
		filter0.setNumberUpperBoundary(4);
		ComparisonFilter filter1 = new ComparisonFilter();
		filter1.setType(Types.BETWEEN);
		filter1.setProperty("count");
		filter1.setNumberLowerBoundary(5);
		filter1.setNumberUpperBoundary(20);
		ComparisonFilter filter2 = new ComparisonFilter();
		filter2.setType(Types.GREATER_THAN);
		filter2.setProperty("count");
		filter2.setNumberValue(20);

		rules[0] = new Rule();
		SymbolizerPoint symbolizer0 = new SymbolizerPoint();
		symbolizer0.setFillColor("green");
		symbolizer0.setFillOpacity(0.9);
		symbolizer0.setStrokeColor("green");
		symbolizer0.setStrokeOpacity(0.5);
		symbolizer0.setStrokeWidth(12);
		symbolizer0.setPointRadius(10);
		rules[0].setFilter(filter0);
		rules[0].setSymbolizer(symbolizer0);

		rules[1] = new Rule();
		SymbolizerPoint symbolizer1 = new SymbolizerPoint();
		symbolizer1.setFillColor("orange");
		symbolizer1.setFillOpacity(0.9);
		symbolizer1.setStrokeColor("orange");
		symbolizer1.setStrokeOpacity(0.5);
		symbolizer1.setStrokeWidth(12);
		symbolizer1.setPointRadius(10);
		rules[1].setFilter(filter1);
		rules[1].setSymbolizer(symbolizer1);

		rules[2] = new Rule();
		SymbolizerPoint symbolizer2 = new SymbolizerPoint();
		symbolizer2.setFillColor("red");
		symbolizer2.setFillOpacity(0.9);
		symbolizer2.setStrokeColor("red");
		symbolizer2.setStrokeOpacity(0.5);
		symbolizer2.setStrokeWidth(12);
		symbolizer2.setPointRadius(10);
		rules[2].setFilter(filter2);
		rules[2].setSymbolizer(symbolizer2);

		Style defaultStyle = new Style();
		defaultStyle.setLabel("${count}");
		defaultStyle.setFontColor("#FFFFFF");
		defaultStyle.setFontSize("20px");

		final StyleMap styleMap = new StyleMap(defaultStyle);
		styleMap.addRules(rules, "default");

		// Add Layers
		//clusterStrategy = new AnimatedClusterStrategy(new AnimatedClusterStrategyOptions());
		clusterStrategy = new ClusterStrategy();
		clusterStrategy.setDistance(20);
		clusterStrategy.setThreshold(2);

		VectorOptions vectorOptions = new VectorOptions();
		vectorOptions.setStrategies(new Strategy[] {clusterStrategy});
		vectorOptions.setRenderers(new String[] {"SVG"}); // "Canvas", bug, see: https://github.com/Leaflet/Leaflet/pull/2486
		stationLayer = new Vector("stations", vectorOptions);
		clusterStrategy.activate();
		stationLayer.setStyleMap(styleMap);
	}

	private void setPopup(VectorFeature vf) {
		Popup popup;
		Bounds b = theMap.getExtent();
		if (vf.getCluster() == null) {
			popup = new FramedCloud("id1", vf.getCenterLonLat(), null, vf.getFeatureId() + " -- " + b.containsLonLat(vf.getCenterLonLat(), true), null, true);
		} else {
			int count = vf.getAttributes().getAttributeAsInt("count");
			popup = new FramedCloud("id1", vf.getCenterLonLat(), null, "<h1>Hello</H1>Here are " + count + " features." + b.containsLonLat(vf.getCenterLonLat(), true), null, true);
		}
		popup.setPanMapIfOutOfView(true); // this set the popup in a strategic way, and pans the map if needed.
		popup.setAutoSize(true);
		vf.setPopup(popup);
	}
	private void addDeliveries() {
		if (System.currentTimeMillis() - lastDeliveryRefresh > 2000) {
			lastDeliveryRefresh = System.currentTimeMillis();
			//theMap.removeLayer(deliveryLayer);
			deliveryLayer.removeAllFeatures();
			if (stationLayer != null && stationLayer.getFeatures() != null) {
				VectorFeature[] vfs = stationLayer.getFeatures();
				for (VectorFeature vf : vfs) {
					if (vf != null && vf.getFeatureId() != null) {
						Bounds b = theMap.getExtent();
						if (vf.getCluster() == null && b.containsLonLat(vf.getCenterLonLat(), true)) {
							int stationId = -1;
							try{stationId = Integer.parseInt(vf.getFeatureId());}
							catch (Exception e) {}
							if (stationId >= 0) {
								if (deliveries != null && deliveries.containsKey(stationId)) {
									HashSet<VectorFeature> hs = deliveries.get(stationId);
									for (VectorFeature vff : hs) {
										deliveryLayer.addFeature(vff);									
									}
								}
							}
						}
					}
				}
			}	
			//theMap.addLayer(deliveryLayer);
		}
	}

	private void buildPanel() {
		OSM osmMapnik = OSM.Mapnik("Mapnik");
		//OSM osmCycle = OSM.CycleMap("CycleMap");

		osmMapnik.setIsBaseLayer(true);
		//osmCycle.setIsBaseLayer(true);

		theMap = this.getMap();
		theMap.addLayer(osmMapnik);
		//theMap.addLayer(osmCycle);
		MAP_PROJ = new Projection(theMap.getProjection());

		//Lets add some default controls to the map
		theMap.addControl(new LayerSwitcher()); //+ sign in the upperright corner to display the layer switcher
		theMap.addControl(new OverviewMap()); //+ sign in the lowerright to display the overviewmap
		theMap.addControl(new ScaleLine()); //Display the scaleline

		deliveryLayer = new Vector("deliveries");
		Style dss = createDeliverySelectedStyle();
		deliveryLayer.setStyleMap(new StyleMap(createDeliveryStyle(), dss, dss));
		stationLayer = new Vector("stations");
		labelLayer = new Vector("labels");
		addClusterStrategy();
		theMap.addLayer(stationLayer);
		theMap.addLayer(deliveryLayer);
		theMap.addLayer(labelLayer);

		theMap.addMapMoveEndListener(new MapMoveEndListener() {
			@Override
			public void onMapMoveEnd(MapMoveEndEvent eventObject) {
				addDeliveries();
			}
		});

		final SelectFeature selectFeature = new SelectFeature(new Vector[] {stationLayer, deliveryLayer});
		selectFeature.setAutoActivate(true);
		theMap.addControl(selectFeature);

		stationLayer.addVectorFeatureSelectedListener(new VectorFeatureSelectedListener() {
			public void onFeatureSelected(FeatureSelectedEvent eventObject) {
				VectorFeature vf = eventObject.getVectorFeature();
				setPopup(vf);
				theMap.addPopup(vf.getPopup());
			}
		});
		// Add select feature for deliveries 
		deliveryLayer.addVectorFeatureSelectedListener(new VectorFeatureSelectedListener() {
			public void onFeatureSelected(FeatureSelectedEvent eventObject) {
				VectorFeature[] svf = deliveryLayer.getSelectedFeatures();
				if (svf != null) {
					for (int i = 0; i < svf.length; i++) {
						//Window.alert("The vector is now selected.\n" + svf[i].getFeatureId());
						//selectFeature.unSelect(svf[i]);
					}
				}
			}
		});

		addSearchBox();
	}
	private void centerTheMap(int zoomLevel) {
		// Center the Map		
		if (stationLayer != null && stationLayer.getFeatures() != null && stationLayer.getFeatures().length > 0) {
			if (zoomLevel < 0) {
				theMap.zoomToExtent(stationLayer.getDataExtent());
			}
			else {
				theMap.setCenter(stationLayer.getDataExtent().getCenterLonLat(), zoomLevel);							
			}
		}
		else {
			LonLat lonLat = new LonLat(13.36438, 52.40967); // BfR
			lonLat.transform(DEFAULT_PROJECTION.getProjectionCode(), theMap.getProjection()); //transform lonlat to OSM coordinate system
			theMap.setCenter(lonLat, zoomLevel);			
		}
	}

	private void addSearchBox() {
		final com.smartgwt.client.widgets.Window searchBox = new com.smartgwt.client.widgets.Window();
		searchBox.setWidth(250);
		searchBox.setHeight(40);
		searchBox.setShowMinimizeButton(false);
		searchBox.setShowCloseButton(false);
		searchBox.setIsModal(false);
		searchBox.setTop(10);
		searchBox.setLeft(45);
		searchBox.setOpacity(80);
		searchBox.setShowHeader(false);
		searchBox.setShowStatusBar(false);

		TextItem textItem = new TextItem();
		textItem.setHeight("100%");
		textItem.setWidth("100%");
		textItem.setShowTitle(false);
		textItem.setMask(null);

		textItem.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getKeyName().equals("Enter")) {
					//Window.alert(event.getItem().getValue()+"");
					fetchMyData(event.getItem().getValue() == null ? "" : event.getItem().getValue() + "");
				}
			}
		});
		/*
		 * textItem.addKeyPressHandler(new KeyPressHandler() {
		 * 
		 * @Override public void onKeyPress(KeyPressEvent event) { if
		 * (event.getCharacterValue() == 13) { //
		 * event.getKeyName().equals("Enter")
		 * //Window.alert(event.getItem().getValue() + " ->" +
		 * event.getCharacterValue()); fetchMyData(event.getItem().getValue() +
		 * ""); } } });
		 */

		DynamicForm form = new DynamicForm();
		form.setWidth100();
		form.setHeight100();
		form.setNumCols(1);
		form.setItems(textItem);
		form.setOverflow(Overflow.HIDDEN);

		searchBox.addItem(form);
		searchBox.show();
	}

	private VectorFeature addDelivery2Feature(int id, int from, int to) {
		VectorFeature vf = null;
		if (stations != null && stations.get(from) != null && stations.get(to) != null) {
			List<Point> pointList = getLink(stations.get(from).getPoint(), stations.get(to).getPoint(), 30);
			if (pointList != null) {
				LineString arrow = new LineString(pointList.toArray(new Point[pointList.size()]));
				vf = new VectorFeature(arrow);
				vf.setFeatureId("" + id);
				//deliveryLayer.addFeature(vf);
			}
		}
		return vf;
	}

	private VectorFeature addStation2Feature(Station s) {
		Point point = s.getPoint();
		point.transform(DEFAULT_PROJECTION, MAP_PROJ);
		VectorFeature vf = new VectorFeature(point, createStationStyle(s.getName()));//, createStationStyle(s.getName())); // s.getName()
		vf.setFeatureId("" + s.getId());
		return vf;
		/*
		 * theMap.addMapZoomListener(new MapZoomListener() { public void
		 * onMapZoom(MapZoomEvent eventObject) {
		 * //vf.getGeometry().transform(DEFAULT_PROJECTION, MAP_PROJ);
		 * vf.redrawParent(); } });
		 */
	}

	private List<Point> getLink(Point pointA, Point pointB, double bogenwinkel) {
		if (pointA == null || pointB == null) return null;
		double angle = Math.PI / 180 * bogenwinkel; // Bogenwinkel
		double distAB = Math.sqrt((pointB.getX() - pointA.getX()) * (pointB.getX() - pointA.getX()) + (pointB.getY() - pointA.getY()) * (pointB.getY() - pointA.getY()));
		double r = distAB / 2 / Math.sin(angle);
		Point[] pointMs = getCircleCentres(pointA, pointB, r);
		Point pointM = null;
		pointM = pointMs[0];
		double angleA = Math.atan2(pointA.getY() - pointM.getY(), pointA.getX() - pointM.getX());
		double angleB = Math.atan2(pointB.getY() - pointM.getY(), pointB.getX() - pointM.getX());
		//Window.alert(pointA + " / " + pointB + " / " + pointM + " / " + (angleA/Math.PI*180) + " / " + (angleB/Math.PI*180) + " / " + ((angleB+2*Math.PI)/Math.PI*180));
		if (Math.abs(angleB - angleA) < Math.PI) return getArc(pointM, r, angleA, angleB, 20, true);
		else if (Math.abs(angleB + 2 * Math.PI - angleA) < Math.PI) return getArc(pointM, r, angleA, angleB + 2 * Math.PI, 20, true);
		else if (Math.abs(angleB - angleA - 2 * Math.PI) < Math.PI) return getArc(pointM, r, angleA + 2 * Math.PI, angleB, 20, true);
		return getArc(pointM, r, angleA, angleB, 4, true);
	}

	private List<Point> getArrow(Point pointA, Point pointB) {
		List<Point> pointList = new ArrayList<Point>();
		double angle = Math.PI / 180 * 20;
		double x = pointA.getX() - pointB.getX();
		double y = pointA.getY() - pointB.getY();
		double newX = pointB.getX() + Math.cos(angle) * x - Math.sin(angle) * y;
		double newY = pointB.getY() + Math.sin(angle) * x + Math.cos(angle) * y;
		pointList.add(new Point(newX, newY));
		pointList.add(new Point(pointB.getX(), pointB.getY()));
		newX = pointB.getX() + Math.cos(-angle) * x - Math.sin(-angle) * y;
		newY = pointB.getY() + Math.sin(-angle) * x + Math.cos(-angle) * y;
		pointList.add(new Point(newX, newY));
		pointList.add(new Point(pointB.getX(), pointB.getY()));
		return pointList;
	}

	private Point[] getCircleCentres(Point pointA, Point pointB, double r) {
		double x1 = pointA.getX();
		double y1 = pointA.getY();

		double x2 = pointB.getX();
		double y2 = pointB.getY();

		//variables
		double resultX1 = 0, resultX2 = 0, resultY1 = 0, resultY2 = 0; //results
		double p1, q1, c1, c2, k1, k2, k3; //temps

		//check for special cases:
		if ((y1 == y2) && (x2 != x1)) { //y values identical 
			resultX1 = x1 + (x2 * x2 + x1 * x1 - 2 * x1 * x2) / (2 * x2 - 2 * x1);
			resultX2 = resultX1;
			p1 = y1 * y1 - r * r + resultX1 * resultX1 - 2 * x1 * resultX1 + x1 * x1;
			resultY1 = y1 + Math.sqrt(y1 * y1 - p1);
			resultY2 = y1 - Math.sqrt(y1 * y1 - p1);
		} else if ((x2 == x1) && (y2 != y1)) {// x values identical
			resultY1 = y1 + (y2 * y2 + y1 * y1 - 2 * y1 * y2) / (2 * y2 - 2 * y1);
			resultY2 = resultY1;
			q1 = x1 * x1 - r * r + resultY1 * resultY1 - 2 * y1 * resultY1 + y1 * y1;
			resultX1 = x1 + Math.sqrt(x1 * x1 - q1);
			resultX2 = x1 - Math.sqrt(x1 * x1 - q1);
		} else if ((x2 == x1) && (y2 == y1)) {//centers identical
			//Window.alert("Centers identical... ");
		} else { //default case
			// ok let's calculate the constants
			c1 = (Math.pow(x2, 2.0) - Math.pow(x1, 2.0) - Math.pow(y1, 2.0) + Math.pow(y2, 2.0)) / (2.0 * x2 - 2.0 * x1);
			c2 = (y1 - y2) / (x2 - x1);
			k1 = 1.0 + (1.0 / Math.pow(c2, 2.0));
			k2 = 2.0 * x1 + (2.0 * y1) / (c2) + (2.0 * c1) / Math.pow(c2, 2.0);
			k3 = Math.pow(x1, 2.0) + Math.pow(c1, 2.0) / Math.pow(c2, 2.0) + (2.0 * y1 * c1) / (c2) + Math.pow(y1, 2.0) - Math.pow(r, 2.0);
			//looks weired? Oh lord have mercy on me! it's just the beginning!
			//here the finish by using the pq formula:
			resultX1 = ((k2 / k1) / 2.0) + Math.sqrt((Math.pow((k2 / k1), 2.0) / 4.0) - (k3 / k1));
			resultX2 = (k2 / k1) / 2.0 - Math.sqrt((Math.pow((k2 / k1), 2.0) / 4.0) - (k3) / (k1));
			resultY1 = 1.0 / (c2) * resultX1 - (c1 / c2);
			resultY2 = 1.0 / (c2) * resultX2 - (c1 / c2);
		}
		/*
		 * // Output: Window.alert("ax: " + x1 + ", ay: " + y1 + "\nbx: " + x2 +
		 * ", by: " + y2 + "\nresultX1: " + resultX1 + ", resultY1: " + resultY1
		 * + "\nresultX2: " + resultX2 + ", resultY2: " + resultY2 + "\n r: " +
		 * r + ", distAB: " + distAB);
		 */
		return new Point[] { new Point(resultX1, resultY1), new Point(resultX2, resultY2) };
	}

	private Style createStationStyle(String text) { // String text
		Style stationStyle = new Style();
		stationStyle.setFillColor("blue");
		stationStyle.setPointRadius(12);
		stationStyle.setLabel(text);
		stationStyle.setFillOpacity(1.0);
		return stationStyle;
	}

	private Style createDeliveryStyle() {
		Style deliveryStyle = new Style();
		deliveryStyle.setStrokeColor("#888888");
		deliveryStyle.setStrokeWidth(3);
		return deliveryStyle;
	}
	private Style createDeliverySelectedStyle() {
		Style deliveryStyle = new Style();
		deliveryStyle.setStrokeColor("#0000ff");
		deliveryStyle.setStrokeWidth(3);
		return deliveryStyle;
	}

	/**
	 * Both cases are simply the calculation of points on a circle. The only
	 * difference is that for the animation the points are not used to draw the
	 * arc.
	 * 
	 * Calculation of points on a circle (by centerpoint, radius and angle)
	 * 
	 * var x = center.x + radius * Math.cos(angle * Math.PI/180); var y =
	 * center.y + radius * Math.sin(angle * Math.PI/180); Function to create an
	 * arc feature (by centerpoint, radius and angle)
	 * 
	 * Function: objArc creates an arc (a linestring with n segments)
	 * 
	 * Parameters: center - center point radius - radius of the arc alpha -
	 * starting angle (in Grad) omega - ending angle (in Grad) segments - number
	 * of segments for drawing the arc
	 * 
	 * Returns: an array with four features, if flag=true arc feature (from
	 * Linestring) the startpoint (from Point) the endpoint (from Point) the
	 * chord (from LineString)
	 */
	private List<Point> getArc(Point center, double radius, double alpha, double omega, int segments, boolean clockwise) {
		List<Point> pointList = new ArrayList<Point>();
		Point lastPoint = null;
		for (int i = 0; i <= segments; i++) {
			double Angle = alpha + (clockwise ? (omega - alpha) * i / segments : (alpha - omega) * i / segments);
			double x = center.getX() + radius * Math.cos(Angle);
			double y = center.getY() + radius * Math.sin(Angle);

			Point newPoint = new Point(x, y);
			pointList.add(newPoint);
			if (lastPoint != null && i == Math.floor(3 * segments / 4)) {
				pointList.addAll(getArrow(lastPoint, newPoint));
			}
			lastPoint = newPoint;
		}
		for (Point p : pointList) {
			p.transform(DEFAULT_PROJECTION, MAP_PROJ);
		}
		return pointList;
	}

}