package de.bund.bfr.crisis.client;

import java.awt.Font;
import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.Map;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.Pixel;
import org.gwtopenmaps.openlayers.client.Projection;
import org.gwtopenmaps.openlayers.client.Style;
import org.gwtopenmaps.openlayers.client.StyleMap;
import org.gwtopenmaps.openlayers.client.control.LayerSwitcher;
import org.gwtopenmaps.openlayers.client.control.OverviewMap;
import org.gwtopenmaps.openlayers.client.control.ScaleLine;
import org.gwtopenmaps.openlayers.client.control.SelectFeature;
import org.gwtopenmaps.openlayers.client.event.MapMoveEndListener;
import org.gwtopenmaps.openlayers.client.event.VectorFeatureSelectedListener;
import org.gwtopenmaps.openlayers.client.event.VectorFeatureUnselectedListener;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.filter.ComparisonFilter;
import org.gwtopenmaps.openlayers.client.filter.ComparisonFilter.Types;
import org.gwtopenmaps.openlayers.client.geometry.LineString;
import org.gwtopenmaps.openlayers.client.geometry.LinearRing;
import org.gwtopenmaps.openlayers.client.geometry.Point;
import org.gwtopenmaps.openlayers.client.geometry.Polygon;
import org.gwtopenmaps.openlayers.client.layer.OSM;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.layer.VectorOptions;
import org.gwtopenmaps.openlayers.client.popup.FramedCloud;
import org.gwtopenmaps.openlayers.client.popup.Popup;
import org.gwtopenmaps.openlayers.client.strategy.ClusterStrategy;
import org.gwtopenmaps.openlayers.client.strategy.Strategy;
import org.gwtopenmaps.openlayers.client.style.Rule;
import org.gwtopenmaps.openlayers.client.style.SymbolizerPoint;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle.MultiWordSuggestion;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.core.JsObject;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class TracingMap extends MapWidget {

	/**
	 * @author heisea
	 */
	public class RpcSuggestOracle extends SuggestOracle {
		@Override
		public void requestSuggestions(final Request request, final Callback callback) {
			mapService.searchSuggestions(request.getQuery(), new AsyncCallback<String>() {
				@Override
				public void onSuccess(String jsonResponse) {
					JsArrayString searchResults = JsonUtils.unsafeEval(jsonResponse);
					Response response = new Response();
					List<Suggestion> suggestions = new ArrayList<>();
					suggestions.add(new MultiWordSuggestion(request.getQuery(), request.getQuery()));
					for (String searchResult : JsoUtils.wrap(searchResults))
						suggestions.add(new MultiWordSuggestion(searchResult, highlightQuery(searchResult,
							request.getQuery())));

					response.setSuggestions(suggestions);
					callback.onSuggestionsReady(request, response);
				}

				private String highlightQuery(String searchResult, String query) {
					return replaceIgnoreCase(searchResult, "(" + query + ")", "<strong>$1</strong>");
				}

				private native String replaceIgnoreCase(String string, String searchString, String replacement) /*-{
																												return string.replace(new RegExp(searchString, "ig"), replacement);
																												}-*/;

				@Override
				public void onFailure(Throwable arg0) {
				}
			});
		}

		@Override
		public boolean isDisplayStringHTML() {
			return true;
		}
	}

	private static final Projection DEFAULT_PROJECTION = new Projection(
		"EPSG:4326"); // transform lonlat (provided in EPSG:4326) to OSM
						// coordinate system (the map projection)

	private Projection MAP_PROJ = null;

	static MapOptions defaultMapOptions = new MapOptions();

	private final MapServiceAsync mapService;

	ClusterStrategy clusterStrategy = null; // AnimatedClusterStrategy

	private Vector stationLayer = null, deliveryLayer = null,
			labelLayer = null;

	private java.util.Map<Integer, Station> stations = new HashMap<>();

	private java.util.Map<Integer, Delivery> deliveries = new HashMap<>();

	private java.util.Map<Integer, Set<VectorFeature>> stationDeliveryFeatures = new HashMap<>();

	private Logger logger;

	private boolean showArrows = true;

	private SuggestBox searchBox = new SuggestBox(new RpcSuggestOracle());

	public TracingMap() {
		this((MapServiceAsync) GWT.create(MapService.class));
	}

	public TracingMap(MapServiceAsync mapService) {
		super("100%", "100%", defaultMapOptions);
		this.mapService = mapService;
		logger = Logger.getLogger(this.getClass().getSimpleName());
		buildPanel();

		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				search("");
				centerTheMap(-1);
			}
		});
	}

	public void fillMap(List<Station> stations, List<Delivery> deliveries) {
		stationLayer.removeAllFeatures();

		this.stations.clear();
		List<VectorFeature> features = new ArrayList<>();
		for (Station station : stations) {
			this.stations.put(station.getId(), station);
			features.add(addStation2Feature(station));
		}
		this.stationLayer.addFeatures(features.toArray(new VectorFeature[0]));
		// clusterStrategy.setFeatures(stationLayer.getFeatures());

		this.deliveries.clear();
		this.stationDeliveryFeatures.clear();
		java.util.Set<String> existingRoutes = new HashSet<>();
		for (Delivery d : deliveries) {
			this.deliveries.put(d.getId(), d);

			boolean fromLarger = (d.getStationId() > d.getRecipientId());
			String routeId = fromLarger ? d.getRecipientId() + "_" + d.getStationId() :
				d.getStationId() + "_" + d.getRecipientId();
			if (existingRoutes.contains(routeId))
				continue;

			addDelivery2Feature(d.getId(), d.getStationId(), d.getRecipientId(), 30d);
		}

		centerTheMap(-1);
	}

	private void search(String searchString) {
		// MyCallbackGIS myCallback = new MyCallbackGIS(this);
		if (searchString == null || searchString.trim().isEmpty()) {
			mapService.search(searchString, new AsyncCallback<String>() {
				@Override
				public void onSuccess(String jsonResponse) {
					SearchResult searchResult = JsonUtils.unsafeEval(jsonResponse);
					fillMap(JsoUtils.wrap(searchResult.getStations()),
						JsoUtils.wrap(searchResult.getDeliveries()));
				}

				@Override
				public void onFailure(Throwable e) {
					com.google.gwt.user.client.Window.alert("Could not submit the search query to the server");
					logger.log(Level.SEVERE,
						"Could not submit the search query to the server", e);
				}
			});
		}
		else {
			mapService.getStationId(searchString, new AsyncCallback<String>() {
				@Override
				public void onSuccess(String jsonResponse) {
					if (!jsonResponse.equalsIgnoreCase("null")) {
						JsArrayString searchResults = JsonUtils.unsafeEval(jsonResponse);
						for (String searchResult : JsoUtils.wrap(searchResults)) {
							Integer sid = Integer.valueOf(searchResult);
							if (sid != null) {
								Station s = stations.get(sid);
								Map map = getMap();
								LonLat lonLat = new LonLat(s.getLongitude(), s.getLatitude());
								lonLat.transform(DEFAULT_PROJECTION.getProjectionCode(), map.getProjection());
								addDeliveries(sid);
								map.setCenter(lonLat, 9);
							}
						}
					}
				}

				@Override
				public void onFailure(Throwable e) {
					com.google.gwt.user.client.Window.alert("Could not submit the search query to the server");
					logger.log(Level.SEVERE,
						"Could not submit the search query to the server", e);
				}
			});
		}
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
		// clusterStrategy = new AnimatedClusterStrategy(new
		// AnimatedClusterStrategyOptions());
		clusterStrategy = new ClusterStrategy();
		clusterStrategy.setDistance(showArrows ? 60 : 0);
		clusterStrategy.setThreshold(2);

		VectorOptions vectorOptions = new VectorOptions();
		vectorOptions.setStrategies(new Strategy[] { clusterStrategy });
		vectorOptions.setRenderers(new String[] { "SVG" }); // "Canvas", bug,
															// see:
															// https://github.com/Leaflet/Leaflet/pull/2486
		stationLayer = new Vector("stations", vectorOptions);
		clusterStrategy.activate();
		stationLayer.setStyleMap(styleMap);
	}

	private Station getStation(String id) {
		Station result = null;
		int stationId = -1;
		try {
			stationId = Integer.parseInt(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (stationId >= 0)
			result = stations.get(stationId);
		return result;
	}

	private Delivery getDelivery(String id) {
		Delivery result = null;
		int deliveryId = -1;
		try {
			deliveryId = Integer.parseInt(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (deliveryId >= 0)
			result = deliveries.get(deliveryId);
		return result;
	}

	private void addDeliveries(Integer stationId) {
		if (!showArrows)
			return;

		// logger.log(Level.SEVERE, "addDeliveries - Start");
		try {
			deliveryLayer.removeAllFeatures();
			Set<VectorFeature> features = this.stationDeliveryFeatures.get(stationId);
			if (features != null)
				for (VectorFeature f : features)
					deliveryLayer.addFeature(f);
			/*
			 * for (VectorFeature vf : stationLayer.getFeatures()) {
			 * if (vf.getCluster() != null)
			 * continue;
			 * if (vf.getCenterLonLat() == null)
			 * continue;
			 * if (!bounds.containsLonLat(vf.getCenterLonLat(), true))
			 * continue;
			 * int stationId = Integer.parseInt(vf.getFeatureId());
			 * Set<VectorFeature> features = this.stationDeliveryFeatures.get(stationId);
			 * if (features != null)
			 * for (VectorFeature f : features)
			 * deliveryLayer.addFeature(f);
			 * addLabel(stationId);
			 * }
			 */
			// theMap.setLayerZIndex(labelLayer, 500);
		} catch (Exception e) {
			logger.log(Level.SEVERE,
				"addDeliveries - exception: " + e.getMessage());
		}
	}

	private void addLabels() {
		int minLables2Show = 10;
		labelLayer.removeAllFeatures();
		Bounds bounds = getMap().getExtent();
		if (bounds.getJSObject() != null) {
			HashMap<Integer, List<Integer>> hm = new HashMap<Integer, List<Integer>>();
			int maxSize = 0;
			for (VectorFeature vf : stationLayer.getFeatures()) {
				if (vf.getCenterLonLat() == null)
					continue;
				if (!bounds.containsLonLat(vf.getCenterLonLat(), true))
					continue;
				int stationId = Integer.parseInt(vf.getFeatureId());
				int s = this.stationDeliveryFeatures.get(stationId).size();
				if (!hm.containsKey(s)) hm.put(s, new ArrayList<Integer>());
				hm.get(s).add(stationId);
				if (s > maxSize) maxSize = s;
			}
			int lfd=0;
			for (int i=maxSize;i>0;i--) {
				if (hm.containsKey(i)) {
					List<Integer> l = hm.get(i);
					for (int stationId : l) {
						addLabel(stationId);
						lfd++;
					}
					if (lfd > minLables2Show) break;
				}
			}
		}
	}

	private void addLabel(int stationId) {
		Station station = this.stations.get(stationId);
		String sn = station.getName(); // station.getId() + ""
		Point point = station.getPoint();
		point.transform(DEFAULT_PROJECTION, MAP_PROJ);
		Bounds bounds = getMap().getExtent();
		Polygon rect = getRectangle(point.getX(), point.getY(), sn.trim().length() * bounds.getWidth() / 250,
				bounds.getHeight() / 60); // sn.trim().length() * 0.7 *
		VectorFeature vf = new VectorFeature(rect, createLabelStyle(sn));
		vf.setFeatureId("l" + String.valueOf(stationId));
		labelLayer.addFeature(vf);
	}
	
	public final Polygon getRectangle(double lon, double lat, double w, double h) {
		List<LinearRing> linearRingList = new ArrayList<LinearRing>();
		List<Point> points1 = new ArrayList<Point>();
		points1.add(new Point(lon - w/2, lat - h/2));
		points1.add(new Point(lon + w/2, lat - h/2));
		points1.add(new Point(lon + w/2, lat + h/2));
		points1.add(new Point(lon - w/2, lat + h/2));
		linearRingList.add(new LinearRing(points1.toArray(new Point[points1.size()])));

		Polygon p = new Polygon(linearRingList.toArray(new LinearRing[linearRingList.size()]));
		return p;
	}

	private void buildPanel() {
		OSM osmMapnik = OSM.Mapnik("Mapnik");
		// OSM osmCycle = OSM.CycleMap("CycleMap");

		osmMapnik.setIsBaseLayer(true);
		// osmCycle.setIsBaseLayer(true);

		final Map map = this.getMap();
		map.addLayer(osmMapnik);
		// theMap.addLayer(osmCycle);
		MAP_PROJ = new Projection(map.getProjection());

		// Lets add some default controls to the map
		// + sign in the upperright corner to display the layer switcher
		map.addControl(new LayerSwitcher());
		// + sign in the lowerright to display the overviewmap
		map.addControl(new OverviewMap());
		// Display the scaleline
		map.addControl(new ScaleLine());

		deliveryLayer = new Vector("visibleDeliveries");
		Style dss = createDeliverySelectedStyle();
		deliveryLayer.setStyleMap(new StyleMap(createDeliveryStyle(), dss, dss));
		stationLayer = new Vector("stations");
		labelLayer = new Vector("labels");
		addClusterStrategy();
		map.addLayer(deliveryLayer);
		map.addLayer(stationLayer);
		map.addLayer(labelLayer);

		map.addMapMoveEndListener(new MapMoveEndListener() {
			@Override
			public void onMapMoveEnd(MapMoveEndEvent eventObject) {
				addLabels();
			}
		});

		final SelectFeature selectFeature = new SelectFeature(new Vector[] {
			stationLayer, deliveryLayer, labelLayer });
		selectFeature.setAutoActivate(true);
		map.addControl(selectFeature);

		stationLayer.addVectorFeatureSelectedListener(new VectorFeatureSelectedListener() {
			public void onFeatureSelected(FeatureSelectedEvent eventObject) {
				VectorFeature vf = eventObject.getVectorFeature();
				if (vf.getCluster() != null) {
					Bounds clusterBounds = new Bounds();
					for (VectorFeature child : vf.getCluster())
						clusterBounds.extend(child.getGeometry().getBounds());
					map.zoomToExtent(clusterBounds);
				} else {
					StationPopup stationPopup = new StationPopup();
					stationPopup.updateStation(vf.getFeatureId());
					stationPopup.show();
				}
			}
		});

		// Add select feature for visibleDeliveries
		deliveryLayer.addVectorFeatureSelectedListener(new VectorFeatureSelectedListener() {
			public void onFeatureSelected(FeatureSelectedEvent eventObject) {
				Integer deliveryId = Integer.valueOf(eventObject.getVectorFeature().getFeatureId().substring(1));
				Delivery delivery = deliveries.get(deliveryId);
				DeliveryPopup deliveryPopup = new DeliveryPopup();
				deliveryPopup.updateStations(delivery.getStationId(), delivery.getRecipientId());
				deliveryPopup.show();
			}
		});

		createSearchBox();
	}

	private void centerTheMap(int zoomLevel) {
		Map map = getMap();
		// Center the Map
		if (stationLayer.getFeatures().length > 0) {
			if (zoomLevel < 0) {
				map.zoomToExtent(stationLayer.getDataExtent());
			} else {
				map.setCenter(stationLayer.getDataExtent().getCenterLonLat(), zoomLevel);
			}
		} else {
			LonLat lonLat = new LonLat(13.36438, 52.40967); // BfR
			// transform lonlat to OSM coordinate system
			lonLat.transform(DEFAULT_PROJECTION.getProjectionCode(), map.getProjection());
			map.setCenter(lonLat, zoomLevel);
		}
	}

	private void createSearchBox() {
		searchBox.setSize("250px", "40px");
		searchBox.setStyleName("gwt-SuggestBox");
		searchBox.addSelectionHandler(new SelectionHandler<SuggestOracle.Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> selectionEvent) {
				search(selectionEvent.getSelectedItem().getReplacementString());
			}
		});
		searchBox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				int key = event.getNativeEvent().getKeyCode();
				if (key == KeyCodes.KEY_ENTER)
					search(searchBox.getText());
			}
		});
	}

	private VectorFeature addDelivery2Feature(int id, int fromId, int toId, double angleInDegrees) {
		List<Point> pointList =
			getArcPoints(stations.get(fromId).getPoint(), stations.get(toId).getPoint(), angleInDegrees);
		if (pointList == null)
			return null;

		LineString arrow = new LineString(pointList.toArray(new Point[pointList.size()]));
		VectorFeature vf = new VectorFeature(arrow);
		vf.setFeatureId("d" + fromId);

		Set<VectorFeature> fromFeatures = stationDeliveryFeatures.get(fromId);
		if (fromFeatures == null)
			stationDeliveryFeatures.put(fromId, fromFeatures = new HashSet<VectorFeature>());
		fromFeatures.add(vf);

		Set<VectorFeature> toFeatures = stationDeliveryFeatures.get(toId);
		if (toFeatures == null)
			stationDeliveryFeatures.put(toId, toFeatures = new HashSet<VectorFeature>());
		toFeatures.add(vf);

		return vf;
	}

	private VectorFeature addStation2Feature(Station s) {
		Point point = s.getPoint();
		point.transform(DEFAULT_PROJECTION, MAP_PROJ);
		VectorFeature vf = new VectorFeature(point, createStationStyle());
		vf.setFeatureId(String.valueOf(s.getId()));
		return vf;
	}

	private List<Point> getArcPoints(Point pointA, Point pointB, double angleInDegrees) {
		if (pointA == null || pointB == null)
			return null;
		double angle = Math.PI / 180 * angleInDegrees; // Bogenwinkel
		double distAB = Math.sqrt((pointB.getX() - pointA.getX())
			* (pointB.getX() - pointA.getX())
			+ (pointB.getY() - pointA.getY())
			* (pointB.getY() - pointA.getY()));
		double r = distAB / 2 / Math.sin(angle);
		Point[] pointMs = getCircleCentres(pointA, pointB, r);
		Point pointM = null;
		pointM = pointMs[0];
		double angleA = Math.atan2(pointA.getY() - pointM.getY(), pointA.getX()
			- pointM.getX());
		double angleB = Math.atan2(pointB.getY() - pointM.getY(), pointB.getX()
			- pointM.getX());
		// Window.alert(pointA + " / " + pointB + " / " + pointM + " / " +
		// (angleA/Math.PI*180) + " / " + (angleB/Math.PI*180) + " / " +
		// ((angleB+2*Math.PI)/Math.PI*180));
		if (Math.abs(angleB - angleA) < Math.PI)
			return getArc(pointM, r, angleA, angleB, 20, true);
		else if (Math.abs(angleB + 2 * Math.PI - angleA) < Math.PI)
			return getArc(pointM, r, angleA, angleB + 2 * Math.PI, 20, true);
		else if (Math.abs(angleB - angleA - 2 * Math.PI) < Math.PI)
			return getArc(pointM, r, angleA + 2 * Math.PI, angleB, 20, true);
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

		// variables
		double resultX1 = 0, resultX2 = 0, resultY1 = 0, resultY2 = 0; // results
		double p1, q1, c1, c2, k1, k2, k3; // temps

		// check for special cases:
		if ((y1 == y2) && (x2 != x1)) { // y values identical
			resultX1 = x1 + (x2 * x2 + x1 * x1 - 2 * x1 * x2)
				/ (2 * x2 - 2 * x1);
			resultX2 = resultX1;
			p1 = y1 * y1 - r * r + resultX1 * resultX1 - 2 * x1 * resultX1 + x1
				* x1;
			resultY1 = y1 + Math.sqrt(y1 * y1 - p1);
			resultY2 = y1 - Math.sqrt(y1 * y1 - p1);
		} else if ((x2 == x1) && (y2 != y1)) {// x values identical
			resultY1 = y1 + (y2 * y2 + y1 * y1 - 2 * y1 * y2)
				/ (2 * y2 - 2 * y1);
			resultY2 = resultY1;
			q1 = x1 * x1 - r * r + resultY1 * resultY1 - 2 * y1 * resultY1 + y1
				* y1;
			resultX1 = x1 + Math.sqrt(x1 * x1 - q1);
			resultX2 = x1 - Math.sqrt(x1 * x1 - q1);
		} else if ((x2 == x1) && (y2 == y1)) {// centers identical
			// Window.alert("Centers identical... ");
		} else { // default case
			// ok let's calculate the constants
			c1 = (Math.pow(x2, 2.0) - Math.pow(x1, 2.0) - Math.pow(y1, 2.0) + Math
				.pow(y2, 2.0)) / (2.0 * x2 - 2.0 * x1);
			c2 = (y1 - y2) / (x2 - x1);
			k1 = 1.0 + (1.0 / Math.pow(c2, 2.0));
			k2 = 2.0 * x1 + (2.0 * y1) / (c2) + (2.0 * c1) / Math.pow(c2, 2.0);
			k3 = Math.pow(x1, 2.0) + Math.pow(c1, 2.0) / Math.pow(c2, 2.0)
				+ (2.0 * y1 * c1) / (c2) + Math.pow(y1, 2.0)
				- Math.pow(r, 2.0);
			// looks weired? Oh lord have mercy on me! it's just the beginning!
			// here the finish by using the pq formula:
			resultX1 = ((k2 / k1) / 2.0)
				+ Math.sqrt((Math.pow((k2 / k1), 2.0) / 4.0) - (k3 / k1));
			resultX2 = (k2 / k1) / 2.0
				- Math.sqrt((Math.pow((k2 / k1), 2.0) / 4.0) - (k3) / (k1));
			resultY1 = 1.0 / (c2) * resultX1 - (c1 / c2);
			resultY2 = 1.0 / (c2) * resultX2 - (c1 / c2);
		}
		/*
		 * // Output: Window.alert("ax: " + x1 + ", ay: " + y1 + "\nbx: " + x2 +
		 * ", by: " + y2 + "\nresultX1: " + resultX1 + ", resultY1: " + resultY1
		 * + "\nresultX2: " + resultX2 + ", resultY2: " + resultY2 + "\n r: " +
		 * r + ", distAB: " + distAB);
		 */
		return new Point[] { new Point(resultX1, resultY1),
			new Point(resultX2, resultY2) };
	}

	private Style createStationStyle() {
		Style stationStyle = new Style();
		stationStyle.setFillColor("yellow");
		stationStyle.setPointRadius(10);
		stationStyle.setFillOpacity(1.0);
		return stationStyle;
	}

	private Style createLabelStyle(String text) {
		Style labelStyle = new Style();
		labelStyle.setPointRadius(16);
		labelStyle.setFillColor("yellow");
		labelStyle.setLabel(text);
		labelStyle.setFontColor("#000000");
		labelStyle.setFontSize("15");
		labelStyle.setFillOpacity(0.1);
		labelStyle.setFontWeight("bold");
		return labelStyle;
	}

	private Style createDeliveryStyle() {
		Style deliveryStyle = new Style();
		deliveryStyle.setStrokeColor("#666666");
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
	 * Calculation of points on a circle (by centerpoint, radius and angle)
	 * var x = center.x + radius * Math.cos(angle * Math.PI/180); var y =
	 * center.y + radius * Math.sin(angle * Math.PI/180); Function to create an
	 * arc feature (by centerpoint, radius and angle)
	 * Function: objArc creates an arc (a linestring with n segments)
	 * Parameters: center - center point radius - radius of the arc alpha -
	 * starting angle (in Grad) omega - ending angle (in Grad) segments - number
	 * of segments for drawing the arc
	 * Returns: an array with four features, if flag=true arc feature (from
	 * Linestring) the startpoint (from Point) the endpoint (from Point) the
	 * chord (from LineString)
	 */
	private List<Point> getArc(Point center, double radius, double alpha,
			double omega, int segments, boolean clockwise) {
		List<Point> pointList = new ArrayList<Point>();
		Point lastPoint = null;
		for (int i = 0; i <= segments; i++) {
			double angle = alpha + (clockwise ? (omega - alpha) * i / segments : (alpha - omega) * i / segments);
			double x = center.getX() + radius * Math.cos(angle);
			double y = center.getY() + radius * Math.sin(angle);

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

	/**
	 * @param panel
	 */
	public void addChildrenToParent(AbsolutePanel panel) {
		panel.add(searchBox, 50, 10);
	}
}