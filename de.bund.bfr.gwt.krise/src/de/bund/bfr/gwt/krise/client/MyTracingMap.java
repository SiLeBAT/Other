package de.bund.bfr.gwt.krise.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.Projection;
import org.gwtopenmaps.openlayers.client.Style;
import org.gwtopenmaps.openlayers.client.control.SelectFeature;
import org.gwtopenmaps.openlayers.client.event.VectorFeatureSelectedListener;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.geometry.LineString;
import org.gwtopenmaps.openlayers.client.geometry.Point;
import org.gwtopenmaps.openlayers.client.layer.OSM;
import org.gwtopenmaps.openlayers.client.layer.Vector;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
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

	private Vector stationLayer = null, deliveryLayer = null;
	private SelectFeature selectFeature = null;
	
	private LinkedHashMap<Integer, Station> stations = null;

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
			Window.alert(stations.size()+"");
			for (Station station : stations.values()) {
				addStation(station);
			}
			deliveryLayer.removeAllFeatures();
			HashSet<Delivery> deliveries = result.getDeliveries();
			for (Delivery delivery : deliveries) {
				//addDelivery(delivery.getId(), delivery.getFrom(), delivery.getTo());
			}
		}
	}

	private void fetchMyData(String station) {
		MyCallbackGIS myCallback = new MyCallbackGIS(this);
		hsqldbService.getGISData(station, myCallback);
	}

	private void buildPanel() {
		OSM osmMapnik = OSM.Mapnik("Mapnik");
		//OSM osmCycle = OSM.CycleMap("CycleMap");

		osmMapnik.setIsBaseLayer(true);
		//osmCycle.setIsBaseLayer(true);

		Map theMap = this.getMap();
		theMap.addLayer(osmMapnik);
		//theMap.addLayer(osmCycle);
		MAP_PROJ = new Projection(theMap.getProjection());

		// Add Layers
		stationLayer = new Vector("stations");
		deliveryLayer = new Vector("deliveries");
		theMap.addLayer(stationLayer);
		theMap.addLayer(deliveryLayer);

		// Add select feature for the point
		selectFeature = new SelectFeature(stationLayer);
		selectFeature.setAutoActivate(true);
		//selectFeature.setMultiple(true);
		theMap.addControl(selectFeature);
		stationLayer.addVectorFeatureSelectedListener(new VectorFeatureSelectedListener() {
			public void onFeatureSelected(FeatureSelectedEvent eventObject) {
				VectorFeature[] svf = stationLayer.getSelectedFeatures();
				if (svf != null) {
					for (int i = 0; i < svf.length; i++) {
						Window.alert("The vector is now selected.\nIt will get de-selected when closing this popup.\n" + svf[i].getFeatureId());
						selectFeature.unSelect(svf[i]);
					}
				}
			}
		});

		stations = new LinkedHashMap<Integer, Station>();
		// Add Stations
		for (int i = 0; i < 10; i++) {
			Station s = new Station(i, "BfR" + i, 13.36438 + i, 52.40967);
			stations.put(-i, s);
			addStation(s);
		}

		// Add Deliveries
		addDelivery(1, -2, -5);

		// Center the Map
		LonLat lonLat = new LonLat(13.36438, 52.40967);
		lonLat.transform(DEFAULT_PROJECTION.getProjectionCode(), theMap.getProjection()); //transform lonlat to OSM coordinate system
		theMap.setCenter(lonLat, 7);

		addSearchBox();
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
		textItem.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharacterValue() == 13) { // event.getKeyName().equals("Enter")
					//Window.alert(event.getItem().getValue() + " ->" + event.getCharacterValue());
					fetchMyData(event.getItem().getValue() + "");
				}
			}
		});
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

	private void addDelivery(int id, int from, int to) {
		if (stations != null) {
			//List<Point> pointList = getLink(new Point(17.36438, 52.40967), new Point(13.36438, 52.40967));
			List<Point> pointList = getLink(stations.get(from).getPoint(), stations.get(to).getPoint());
			LineString arrow = new LineString(pointList.toArray(new Point[pointList.size()]));
			deliveryLayer.addFeature(new VectorFeature(arrow, createDeliveryStyle()));
		}
	}

	private void addStation(Station s) {
		Point point = s.getPoint();
		point.transform(DEFAULT_PROJECTION, MAP_PROJ);
		final VectorFeature vf = new VectorFeature(point, createStationStyle(s.getName()));
		vf.setFeatureId("" + s.getId());
		stationLayer.addFeature(vf);
		/*
		 * theMap.addMapZoomListener(new MapZoomListener() { public void
		 * onMapZoom(MapZoomEvent eventObject) {
		 * //vf.getGeometry().transform(DEFAULT_PROJECTION, MAP_PROJ);
		 * vf.redrawParent(); } });
		 */
	}

	private List<Point> getLink(Point pointA, Point pointB) {
		double angle = Math.PI / 180 * 30; // Bogenwinkel
		double r = Math.sqrt((pointB.getX() - pointA.getX()) * (pointB.getX() - pointA.getX()) + (pointB.getY() - pointA.getY()) * (pointB.getY() - pointA.getY())) / 2
				/ Math.sin(angle);
		Point pointM = getCircleCentre(pointA, pointB, r);
		double angleA, angleB;
		if (pointA.getX() == pointB.getX()) {
			angleA = Math.acos((pointA.getY() - pointM.getY()) / r);
			angleB = Math.acos((pointB.getY() - pointM.getY()) / r);
		} else {
			angleA = Math.asin((pointA.getX() - pointM.getX()) / r);
			angleB = Math.asin((pointB.getX() - pointM.getX()) / r);
		}

		Point lastPoint = null;
		List<Point> pointList = new ArrayList<Point>();
		double numSteps = 20;
		for (int i = 0; i <= numSteps; i++) {
			double t = angleA + (angleB - angleA) * i / numSteps;
			double x = r * Math.sin(t) + pointM.getX();
			double y = r * Math.cos(t) + pointM.getY();

			Point newPoint = new Point(x, y);
			pointList.add(newPoint);

			if (lastPoint != null && i == numSteps / 2) {
				pointList.addAll(getArrowPoints(lastPoint, newPoint));
			}
			lastPoint = newPoint;
		}
		for (Point p : pointList) {
			p.transform(DEFAULT_PROJECTION, MAP_PROJ);
		}
		return pointList;
	}

	private List<Point> getArrowPoints(Point pointA, Point pointB) {
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

	private Point getCircleCentre(Point pointA, Point pointB, double r) {
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
		return new Point(resultX2, resultY2);
	}

	private Style createStationStyle(String text) {
		Style stationStyle = new Style();
		stationStyle.setFillColor("red");
		stationStyle.setPointRadius(15);
		stationStyle.setLabel(text);
		stationStyle.setFillOpacity(1.0);
		return stationStyle;
	}

	private Style createDeliveryStyle() {
		Style deliveryStyle = new Style();
		deliveryStyle.setStrokeColor("#0033ff");
		deliveryStyle.setStrokeWidth(5);
		return deliveryStyle;
	}
}