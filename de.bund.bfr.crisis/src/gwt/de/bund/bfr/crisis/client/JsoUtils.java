package de.bund.bfr.crisis.client;

import java.util.AbstractList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;

public class JsoUtils {
	private JsoUtils() {
	};

	static <T extends JavaScriptObject> List<T> wrap(final JsArray<T> array) {
		return new AbstractList<T>() {
			@Override
			public T get(int index) {
				return array.get(index);
			}

			@Override
			public int size() {
				return array.length();
			}
			
			@Override
			public boolean add(T e) {
				array.push(e);
				return true;
			}
			
			@Override
			public T set(int index, T element) {
				T old = array.get(index);
				array.set(index, element);
				return old;
			}
		};
	}
	
	static List<String> wrap(final JsArrayString array) {
		return new AbstractList<String>() {
			@Override
			public String get(int index) {
				return array.get(index);
			}

			@Override
			public int size() {
				return array.length();
			}
			
			@Override
			public boolean add(String e) {
				array.push(e);
				return true;
			}
			
			@Override
			public String set(int index, String element) {
				String old = array.get(index);
				array.set(index, element);
				return old;
			}
		};
	}
}
