package de.bund.bfr.knime.pmm.core;

import java.awt.Color;
import java.awt.Shape;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.input.ReaderInputStream;

public class XmlUtilities {

	private XmlUtilities() {
	}

	public static String toXml(Object obj) {
		Thread currentThread = Thread.currentThread();
		ClassLoader currentLoader = Thread.currentThread()
				.getContextClassLoader();
		ClassLoader newLoader = Activator.class.getClassLoader();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XMLEncoder encoder = new XMLEncoder(out);

		currentThread.setContextClassLoader(newLoader);
		encoder.writeObject(obj);
		encoder.close();
		currentThread.setContextClassLoader(currentLoader);

		try {
			return out.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T fromXml(String xml, T defaultObj) {
		if (xml == null) {
			return defaultObj;
		}

		Thread currentThread = Thread.currentThread();
		ClassLoader currentLoader = Thread.currentThread()
				.getContextClassLoader();
		ClassLoader newLoader = Activator.class.getClassLoader();
		StringReader in = new StringReader(xml);
		XMLDecoder decoder = new XMLDecoder(new ReaderInputStream(in));
		Object readObj;

		currentThread.setContextClassLoader(newLoader);
		readObj = decoder.readObject();
		decoder.close();
		currentThread.setContextClassLoader(currentLoader);

		try {
			return (T) readObj;
		} catch (Exception e) {
			return defaultObj;
		}
	}

	public static String colorMapToXml(Map<String, Color> map) {
		Map<String, String> newMap = new LinkedHashMap<String, String>();

		for (Map.Entry<String, Color> entry : map.entrySet()) {
			String colorString = "#"
					+ Integer.toHexString(entry.getValue().getRGB()).substring(
							2);

			newMap.put(entry.getKey(), colorString);
		}

		return toXml(newMap);
	}

	public static Map<String, Color> colorMapFromXml(String xml) {
		Map<String, Color> newMap = new LinkedHashMap<String, Color>();

		for (Map.Entry<String, String> entry : fromXml(xml,
				new LinkedHashMap<String, String>()).entrySet()) {
			newMap.put(entry.getKey(), Color.decode(entry.getValue()));
		}

		return newMap;
	}

	public static String shapeMapToXml(Map<String, Shape> map) {
		Map<String, String> newMap = new LinkedHashMap<String, String>();
		Map<Shape, String> shapeMap = (new ColorAndShapeCreator(0))
				.getNameByShapeMap();

		for (Map.Entry<String, Shape> entry : map.entrySet()) {
			newMap.put(entry.getKey(), shapeMap.get(entry.getValue()));
		}

		return toXml(newMap);
	}

	public static Map<String, Shape> shapeMapFromXml(String xml) {
		Map<String, Shape> newMap = new LinkedHashMap<String, Shape>();
		Map<String, Shape> shapeMap = (new ColorAndShapeCreator(0))
				.getShapeByNameMap();

		for (Map.Entry<String, String> entry : fromXml(xml,
				new LinkedHashMap<String, String>()).entrySet()) {
			newMap.put(entry.getKey(), shapeMap.get(entry.getValue()));
		}

		return newMap;
	}

	public static String colorListMapToXml(Map<String, List<Color>> map) {
		Map<String, List<String>> newMap = new LinkedHashMap<String, List<String>>();

		for (Map.Entry<String, List<Color>> entry : map.entrySet()) {
			List<String> list = new ArrayList<String>();

			for (Color color : entry.getValue()) {
				list.add("#" + Integer.toHexString(color.getRGB()).substring(2));
			}

			newMap.put(entry.getKey(), list);
		}

		return toXml(newMap);
	}

	public static Map<String, List<Color>> colorListMapFromXml(String xml) {
		Map<String, List<Color>> newMap = new LinkedHashMap<String, List<Color>>();

		for (Map.Entry<String, List<String>> entry : fromXml(xml,
				new LinkedHashMap<String, List<String>>()).entrySet()) {
			List<Color> list = new ArrayList<Color>();

			for (String s : entry.getValue()) {
				list.add(Color.decode(s));
			}

			newMap.put(entry.getKey(), list);
		}

		return newMap;
	}

	public static String shapeListMapToXml(Map<String, List<Shape>> map) {
		Map<String, List<String>> newMap = new LinkedHashMap<String, List<String>>();
		Map<Shape, String> shapeMap = (new ColorAndShapeCreator(0))
				.getNameByShapeMap();

		for (Map.Entry<String, List<Shape>> entry : map.entrySet()) {
			List<String> list = new ArrayList<String>();

			for (Shape shape : entry.getValue()) {
				list.add(shapeMap.get(shape));
			}

			newMap.put(entry.getKey(), list);
		}

		return toXml(newMap);
	}

	public static Map<String, List<Shape>> shapeListMapFromXml(String xml) {
		Map<String, List<Shape>> newMap = new LinkedHashMap<String, List<Shape>>();
		Map<String, Shape> shapeMap = (new ColorAndShapeCreator(0))
				.getShapeByNameMap();

		for (Map.Entry<String, List<String>> entry : fromXml(xml,
				new LinkedHashMap<String, List<String>>()).entrySet()) {
			List<Shape> list = new ArrayList<Shape>();

			for (String s : entry.getValue()) {
				list.add(shapeMap.get(s));
			}

			newMap.put(entry.getKey(), list);
		}

		return newMap;
	}

}
