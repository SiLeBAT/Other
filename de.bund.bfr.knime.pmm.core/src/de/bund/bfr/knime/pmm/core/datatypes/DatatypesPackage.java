/**
 */
package de.bund.bfr.knime.pmm.core.datatypes;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains
 * accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * 
 * @see de.bund.bfr.knime.pmm.core.datatypes.DatatypesFactory
 * @model kind="package"
 * @generated
 */
public interface DatatypesPackage extends EPackage {
	/**
	 * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	String eNAME = "datatypes";

	/**
	 * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	String eNS_URI = "http:///de/bund/bfr/knime/pmm/core/datatypes.ecore";

	/**
	 * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	String eNS_PREFIX = "de.bund.bfr.knime.pmm.core.datatypes";

	/**
	 * The singleton instance of the package. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	DatatypesPackage eINSTANCE = de.bund.bfr.knime.pmm.core.datatypes.impl.DatatypesPackageImpl
			.init();

	/**
	 * The meta object id for the '
	 * {@link de.bund.bfr.knime.pmm.core.datatypes.impl.EPairImpl
	 * <em>EPair</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see de.bund.bfr.knime.pmm.core.datatypes.impl.EPairImpl
	 * @see de.bund.bfr.knime.pmm.core.datatypes.impl.DatatypesPackageImpl#getEPair()
	 * @generated
	 */
	int EPAIR = 0;

	/**
	 * The feature id for the '<em><b>Value1</b></em>' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int EPAIR__VALUE1 = 0;

	/**
	 * The feature id for the '<em><b>Value2</b></em>' containment reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int EPAIR__VALUE2 = 1;

	/**
	 * The number of structural features of the '<em>EPair</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int EPAIR_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '
	 * {@link de.bund.bfr.knime.pmm.core.datatypes.impl.EStringImpl
	 * <em>EString</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see de.bund.bfr.knime.pmm.core.datatypes.impl.EStringImpl
	 * @see de.bund.bfr.knime.pmm.core.datatypes.impl.DatatypesPackageImpl#getEString()
	 * @generated
	 */
	int ESTRING = 1;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ESTRING__VALUE = 0;

	/**
	 * The number of structural features of the '<em>EString</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ESTRING_FEATURE_COUNT = 1;

	/**
	 * Returns the meta object for class '
	 * {@link de.bund.bfr.knime.pmm.core.datatypes.EPair <em>EPair</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>EPair</em>'.
	 * @see de.bund.bfr.knime.pmm.core.datatypes.EPair
	 * @generated
	 */
	EClass getEPair();

	/**
	 * Returns the meta object for the containment reference '
	 * {@link de.bund.bfr.knime.pmm.core.datatypes.EPair#getValue1
	 * <em>Value1</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference '<em>Value1</em>'.
	 * @see de.bund.bfr.knime.pmm.core.datatypes.EPair#getValue1()
	 * @see #getEPair()
	 * @generated
	 */
	EReference getEPair_Value1();

	/**
	 * Returns the meta object for the containment reference '
	 * {@link de.bund.bfr.knime.pmm.core.datatypes.EPair#getValue2
	 * <em>Value2</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference '<em>Value2</em>'.
	 * @see de.bund.bfr.knime.pmm.core.datatypes.EPair#getValue2()
	 * @see #getEPair()
	 * @generated
	 */
	EReference getEPair_Value2();

	/**
	 * Returns the meta object for class '
	 * {@link de.bund.bfr.knime.pmm.core.datatypes.EString <em>EString</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>EString</em>'.
	 * @see de.bund.bfr.knime.pmm.core.datatypes.EString
	 * @generated
	 */
	EClass getEString();

	/**
	 * Returns the meta object for the attribute '
	 * {@link de.bund.bfr.knime.pmm.core.datatypes.EString#getValue
	 * <em>Value</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see de.bund.bfr.knime.pmm.core.datatypes.EString#getValue()
	 * @see #getEString()
	 * @generated
	 */
	EAttribute getEString_Value();

	/**
	 * Returns the factory that creates the instances of the model. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	DatatypesFactory getDatatypesFactory();

	/**
	 * <!-- begin-user-doc --> Defines literals for the meta objects that
	 * represent
	 * <ul>
	 * <li>each class,</li>
	 * <li>each feature of each class,</li>
	 * <li>each enum,</li>
	 * <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '
		 * {@link de.bund.bfr.knime.pmm.core.datatypes.impl.EPairImpl
		 * <em>EPair</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see de.bund.bfr.knime.pmm.core.datatypes.impl.EPairImpl
		 * @see de.bund.bfr.knime.pmm.core.datatypes.impl.DatatypesPackageImpl#getEPair()
		 * @generated
		 */
		EClass EPAIR = eINSTANCE.getEPair();

		/**
		 * The meta object literal for the '<em><b>Value1</b></em>' containment
		 * reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference EPAIR__VALUE1 = eINSTANCE.getEPair_Value1();

		/**
		 * The meta object literal for the '<em><b>Value2</b></em>' containment
		 * reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference EPAIR__VALUE2 = eINSTANCE.getEPair_Value2();

		/**
		 * The meta object literal for the '
		 * {@link de.bund.bfr.knime.pmm.core.datatypes.impl.EStringImpl
		 * <em>EString</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 * 
		 * @see de.bund.bfr.knime.pmm.core.datatypes.impl.EStringImpl
		 * @see de.bund.bfr.knime.pmm.core.datatypes.impl.DatatypesPackageImpl#getEString()
		 * @generated
		 */
		EClass ESTRING = eINSTANCE.getEString();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute ESTRING__VALUE = eINSTANCE.getEString_Value();

	}

} // DatatypesPackage
