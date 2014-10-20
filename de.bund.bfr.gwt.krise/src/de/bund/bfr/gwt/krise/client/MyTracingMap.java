package de.bund.bfr.gwt.krise.client;

import java.util.ArrayList;
import java.util.List;

import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.Projection;
import org.gwtopenmaps.openlayers.client.Style;
import org.gwtopenmaps.openlayers.client.control.SelectFeature;
import org.gwtopenmaps.openlayers.client.event.MapZoomListener;
import org.gwtopenmaps.openlayers.client.event.VectorFeatureSelectedListener;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.geometry.LineString;
import org.gwtopenmaps.openlayers.client.geometry.Point;
import org.gwtopenmaps.openlayers.client.layer.OSM;
import org.gwtopenmaps.openlayers.client.layer.Vector;

import com.google.gwt.user.client.Window;

public class MyTracingMap extends MapWidget {

	private static final Projection DEFAULT_PROJECTION = new Projection("EPSG:4326"); //transform lonlat (provided in EPSG:4326) to OSM coordinate system (the map projection)
	static MapOptions defaultMapOptions = new MapOptions();

	public MyTracingMap() {
		super("100%", "100%", defaultMapOptions);
		buildPanel();
	}

	private void buildPanel() {
		OSM osmMapnik = OSM.Mapnik("Mapnik");
		//OSM osmCycle = OSM.CycleMap("CycleMap");

		osmMapnik.setIsBaseLayer(true);
		//osmCycle.setIsBaseLayer(true);

		final Map theMap = this.getMap();
		theMap.addLayer(osmMapnik);
		//theMap.addLayer(osmCycle);
		LonLat lonLat = new LonLat(13.36438, 52.40967);
		lonLat.transform(DEFAULT_PROJECTION.getProjectionCode(), theMap.getProjection()); //transform lonlat to OSM coordinate system
		theMap.setCenter(lonLat, 7);
		final Projection mapProj = new Projection(theMap.getProjection());

		// Add Stations
		int numStations = 10;
		final Vector stationLayer = new Vector("stations");
		// Add select feature for the point
		final SelectFeature selectFeature = new SelectFeature(stationLayer);
		selectFeature.setAutoActivate(true);
		//selectFeature.setMultiple(true);
		theMap.addControl(selectFeature);

		for (int i = 0; i < numStations; i++) {
			Point point = new Point(13.36438 + i, 52.40967);
			point.transform(DEFAULT_PROJECTION, mapProj);
			final VectorFeature vf = new VectorFeature(point, createStationStyle("BfR"+i));
			vf.setFeatureId("" + i);
			stationLayer.addFeature(vf);
			/*
			theMap.addMapZoomListener(new MapZoomListener() {
				public void onMapZoom(MapZoomEvent eventObject) {
					//vf.getGeometry().transform(DEFAULT_PROJECTION, mapProj);
					vf.redrawParent();
				}
			});
			*/
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
		}
		theMap.addLayer(stationLayer);

		// Add Deliveries
		Vector deliveryLayer = new Vector("deliveries");
		List<Point> pointList = getLink(new Point(17.36438, 52.40967), new Point(13.36438, 52.40967), mapProj);
		LineString geometry = new LineString(pointList.toArray(new Point[pointList.size()]));
		Style style = new Style();
		style.setStrokeColor("#0033ff");
		style.setStrokeWidth(5);
		deliveryLayer.addFeature(new VectorFeature(geometry, style));
		theMap.addLayer(deliveryLayer);
	}

	private List<Point> getLink(Point pointA, Point pointB, Projection mapProj) {
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
			p.transform(DEFAULT_PROJECTION, mapProj);
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
			Window.alert("Centers identical... ");
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
}