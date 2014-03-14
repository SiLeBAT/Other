package de.bund.bfr.knime.pmm.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import de.bund.bfr.knime.pmm.core.common.CommonPackage;
import de.bund.bfr.knime.pmm.core.data.DataPackage;
import de.bund.bfr.knime.pmm.core.datatypes.DatatypesFactory;
import de.bund.bfr.knime.pmm.core.datatypes.DatatypesPackage;
import de.bund.bfr.knime.pmm.core.datatypes.EPair;
import de.bund.bfr.knime.pmm.core.datatypes.EString;
import de.bund.bfr.knime.pmm.core.models.ModelsPackage;

public class EmfUtilities {

	private EmfUtilities() {
	}

	public static <T extends EObject> List<T> copy(List<T> list) {
		List<T> copy = new ArrayList<T>();

		for (T obj : list) {
			copy.add(EcoreUtil.copy(obj));
		}

		return copy;
	}

	public static <K, V extends EObject> Map<K, V> copy(Map<K, V> map) {
		Map<K, V> copy = new LinkedHashMap<K, V>();

		for (Map.Entry<K, V> entry : map.entrySet()) {
			copy.put(entry.getKey(), EcoreUtil.copy(entry.getValue()));
		}

		return copy;
	}

	public static String toXml(Object obj) {
		ResourceSet resourceSet = new ResourceSetImpl();

		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("*", new XMIResourceFactoryImpl());

		Resource resource = resourceSet.createResource(URI
				.createURI("file:///null"));
		EObject eObj = toEObject(obj);

		if (eObj != null) {
			resource.getContents().add(eObj);
			resource.getContents().addAll(getNonContainments(eObj));
			removeDuplicates(resource.getContents());
		}

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			resource.save(outputStream, null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			return outputStream.toString("UTF-8");
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

		ResourceSet resourceSet = new ResourceSetImpl();

		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("*", new XMIResourceFactoryImpl());
		resourceSet.getPackageRegistry().put(CommonPackage.eNS_URI,
				CommonPackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(DataPackage.eNS_URI,
				DataPackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(ModelsPackage.eNS_URI,
				ModelsPackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(DatatypesPackage.eNS_URI,
				DatatypesPackage.eINSTANCE);

		Resource resource = resourceSet.createResource(URI
				.createURI("file:///nulls"));

		try {
			resource.load(new ByteArrayInputStream(xml.getBytes("UTF-8")), null);

			if (!resource.getContents().isEmpty()) {
				return (T) toObject(resource.getContents().get(0));
			} else {
				return defaultObj;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return defaultObj;
		}
	}

	public static String listToXml(List<?> list) {
		ResourceSet resourceSet = new ResourceSetImpl();

		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("*", new XMIResourceFactoryImpl());

		Resource resource = resourceSet.createResource(URI
				.createURI("file:///null"));

		for (Object obj : list) {
			EObject eObj = toEObject(obj);

			resource.getContents().add(eObj);
			resource.getContents().addAll(getNonContainments(eObj));
		}

		removeDuplicates(resource.getContents());

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			resource.save(outputStream, null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			return outputStream.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> listFromXml(String xml, List<T> defaultList) {
		ResourceSet resourceSet = new ResourceSetImpl();

		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("*", new XMIResourceFactoryImpl());
		resourceSet.getPackageRegistry().put(CommonPackage.eNS_URI,
				CommonPackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(DataPackage.eNS_URI,
				DataPackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(ModelsPackage.eNS_URI,
				ModelsPackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(DatatypesPackage.eNS_URI,
				DatatypesPackage.eINSTANCE);

		Resource resource = resourceSet.createResource(URI
				.createURI("file:///null"));

		try {
			List<T> list = new ArrayList<T>();

			resource.load(new ByteArrayInputStream(xml.getBytes("UTF-8")), null);

			EObject firstObj = null;

			if (!resource.getContents().isEmpty()) {
				firstObj = resource.getContents().get(0);
			}

			for (EObject eObj : resource.getContents()) {
				if (eObj.eClass() == firstObj.eClass()) {
					list.add((T) toObject(eObj));
				}
			}

			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return defaultList;
		}
	}

	public static String mapToXml(Map<?, ?> map) {
		ResourceSet resourceSet = new ResourceSetImpl();

		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("*", new XMIResourceFactoryImpl());

		Resource resource = resourceSet.createResource(URI
				.createURI("file:///null"));

		for (Map.Entry<?, ?> entry : map.entrySet()) {
			EPair pair = DatatypesFactory.eINSTANCE.createEPair();
			EObject eKey = EcoreUtil.copy(toEObject(entry.getKey()));
			EObject eValue = EcoreUtil.copy(toEObject(entry.getValue()));

			pair.setValue1(eKey);
			pair.setValue2(eValue);
			resource.getContents().add(pair);
			resource.getContents().addAll(getNonContainments(eKey));
			resource.getContents().addAll(getNonContainments(eValue));
		}

		removeDuplicates(resource.getContents());

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		try {
			resource.save(outputStream, null);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			return outputStream.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> mapFromXml(String xml, Map<K, V> defaultMap) {
		ResourceSet resourceSet = new ResourceSetImpl();

		resourceSet.getResourceFactoryRegistry().getProtocolToFactoryMap()
				.put("*", new XMIResourceFactoryImpl());
		resourceSet.getPackageRegistry().put(CommonPackage.eNS_URI,
				CommonPackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(DataPackage.eNS_URI,
				DataPackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(ModelsPackage.eNS_URI,
				ModelsPackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(DatatypesPackage.eNS_URI,
				DatatypesPackage.eINSTANCE);

		Resource resource = resourceSet.createResource(URI
				.createURI("file:///null"));

		try {
			Map<K, V> map = new LinkedHashMap<K, V>();

			resource.load(new ByteArrayInputStream(xml.getBytes("UTF-8")), null);

			for (EObject obj : resource.getContents()) {
				if (obj instanceof EPair) {
					EPair entry = (EPair) obj;
					Object key = toObject(entry.getValue1());
					Object value = toObject(entry.getValue2());

					map.put((K) key, (V) value);
				}
			}

			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return defaultMap;
		}
	}

	private static EObject toEObject(Object obj) {
		EObject eObj = null;

		if (obj instanceof EObject) {
			eObj = (EObject) obj;
		} else if (obj instanceof String) {
			eObj = DatatypesFactory.eINSTANCE.createEString();
			((EString) eObj).setValue((String) obj);
		}

		return eObj;
	}

	private static Object toObject(EObject eObj) {
		if (eObj instanceof EString) {
			return ((EString) eObj).getValue();
		} else {
			return eObj;
		}
	}

	private static Set<EObject> getNonContainments(EObject obj) {
		Set<EObject> references = new LinkedHashSet<EObject>();
		Set<EObject> nonContainments = new LinkedHashSet<EObject>();

		findReferences(obj, references, nonContainments);

		return nonContainments;
	}

	private static void findReferences(EObject obj, Set<EObject> references,
			Set<EObject> nonContainments) {
		for (EReference ref : obj.eClass().getEAllReferences()) {
			if (ref.isMany()) {
				for (Object listObj : (List<?>) obj.eGet(ref)) {
					if (!(listObj instanceof Map.Entry)) {
						EObject refObj = toEObject(listObj);

						if (refObj != null && references.add(refObj)) {
							if (!ref.isContainment()) {
								nonContainments.add(refObj);
							}

							findReferences(refObj, references, nonContainments);
						}
					}
				}
			} else {
				EObject refObj = toEObject(obj.eGet(ref));

				if (refObj != null && references.add(refObj)) {
					if (!ref.isContainment()) {
						nonContainments.add(refObj);
					}

					findReferences(refObj, references, nonContainments);
				}
			}
		}
	}

	private static void removeDuplicates(List<EObject> list) {
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = i + 1; j < list.size(); j++) {
				if (EcoreUtil.equals(list.get(i), list.get(j))) {
					EObject newObj = list.get(i);
					EObject oldObj = list.remove(j);

					for (int k = 0; k < list.size(); k++) {
						replace(list.get(k), oldObj, newObj,
								new LinkedHashSet<EObject>());
					}

					j--;
				}
			}
		}
	}

	private static void replace(EObject obj, EObject oldObj, EObject newObj,
			Set<EObject> references) {
		for (EReference ref : obj.eClass().getEAllReferences()) {
			if (ref.isMany()) {
				List<?> list = (List<?>) obj.eGet(ref);

				if (!ref.isContainment()) {
					replaceInList(list, oldObj, newObj);
				} else {
					for (int i = 0; i < list.size(); i++) {
						Object listObj = list.get(i);

						if (!(listObj instanceof Map.Entry)) {
							EObject refObj = toEObject(listObj);

							if (refObj != null && refObj != oldObj
									&& references.add(refObj)) {
								replace(refObj, oldObj, newObj, references);
							}
						}
					}
				}
			} else {
				EObject refObj = toEObject(obj.eGet(ref));

				if (!ref.isContainment()) {
					if (refObj == oldObj) {
						obj.eSet(ref, newObj);
					}
				} else {
					if (refObj != null && references.add(refObj)) {
						replace(refObj, oldObj, newObj, references);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> void replaceInList(List<T> list, EObject oldObj,
			EObject newObj) {
		for (int i = 0; i < list.size(); i++) {
			T refObj = list.get(i);

			if (refObj == oldObj) {
				list.set(i, (T) newObj);
			}
		}
	}
}
