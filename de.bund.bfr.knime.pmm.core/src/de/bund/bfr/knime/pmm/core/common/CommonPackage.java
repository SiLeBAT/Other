/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.bund.bfr.knime.pmm.core.common;

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
 * @see de.bund.bfr.knime.pmm.core.common.CommonFactory
 * @model kind="package"
 * @generated
 */
public interface CommonPackage extends EPackage {
	/**
	 * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	String eNAME = "common";

	/**
	 * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	String eNS_URI = "http:///de/bund/bfr/knime/pmm/core/common.ecore";

	/**
	 * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	String eNS_PREFIX = "de.bund.bfr.knime.pmm.core.common";

	/**
	 * The singleton instance of the package. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	CommonPackage eINSTANCE = de.bund.bfr.knime.pmm.core.common.impl.CommonPackageImpl
			.init();

	/**
	 * The meta object id for the '
	 * {@link de.bund.bfr.knime.pmm.core.common.impl.QuantityTypeImpl
	 * <em>Quantity Type</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see de.bund.bfr.knime.pmm.core.common.impl.QuantityTypeImpl
	 * @see de.bund.bfr.knime.pmm.core.common.impl.CommonPackageImpl#getQuantityType()
	 * @generated
	 */
	int QUANTITY_TYPE = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int QUANTITY_TYPE__ID = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int QUANTITY_TYPE__NAME = 1;

	/**
	 * The feature id for the '<em><b>Units</b></em>' reference list. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int QUANTITY_TYPE__UNITS = 2;

	/**
	 * The feature id for the '<em><b>Default Unit</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int QUANTITY_TYPE__DEFAULT_UNIT = 3;

	/**
	 * The number of structural features of the '<em>Quantity Type</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int QUANTITY_TYPE_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '
	 * {@link de.bund.bfr.knime.pmm.core.common.impl.UnitImpl <em>Unit</em>}'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see de.bund.bfr.knime.pmm.core.common.impl.UnitImpl
	 * @see de.bund.bfr.knime.pmm.core.common.impl.CommonPackageImpl#getUnit()
	 * @generated
	 */
	int UNIT = 1;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int UNIT__ID = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int UNIT__NAME = 1;

	/**
	 * The feature id for the '<em><b>Convert From</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int UNIT__CONVERT_FROM = 2;

	/**
	 * The feature id for the '<em><b>Convert To</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int UNIT__CONVERT_TO = 3;

	/**
	 * The feature id for the '<em><b>Quantity Type</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int UNIT__QUANTITY_TYPE = 4;

	/**
	 * The number of structural features of the '<em>Unit</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int UNIT_FEATURE_COUNT = 5;

	/**
	 * Returns the meta object for class '
	 * {@link de.bund.bfr.knime.pmm.core.common.QuantityType
	 * <em>Quantity Type</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Quantity Type</em>'.
	 * @see de.bund.bfr.knime.pmm.core.common.QuantityType
	 * @generated
	 */
	EClass getQuantityType();

	/**
	 * Returns the meta object for the attribute '
	 * {@link de.bund.bfr.knime.pmm.core.common.QuantityType#getId <em>Id</em>}
	 * '. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see de.bund.bfr.knime.pmm.core.common.QuantityType#getId()
	 * @see #getQuantityType()
	 * @generated
	 */
	EAttribute getQuantityType_Id();

	/**
	 * Returns the meta object for the attribute '
	 * {@link de.bund.bfr.knime.pmm.core.common.QuantityType#getName
	 * <em>Name</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see de.bund.bfr.knime.pmm.core.common.QuantityType#getName()
	 * @see #getQuantityType()
	 * @generated
	 */
	EAttribute getQuantityType_Name();

	/**
	 * Returns the meta object for the reference list '
	 * {@link de.bund.bfr.knime.pmm.core.common.QuantityType#getUnits
	 * <em>Units</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference list '<em>Units</em>'.
	 * @see de.bund.bfr.knime.pmm.core.common.QuantityType#getUnits()
	 * @see #getQuantityType()
	 * @generated
	 */
	EReference getQuantityType_Units();

	/**
	 * Returns the meta object for the reference '
	 * {@link de.bund.bfr.knime.pmm.core.common.QuantityType#getDefaultUnit
	 * <em>Default Unit</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Default Unit</em>'.
	 * @see de.bund.bfr.knime.pmm.core.common.QuantityType#getDefaultUnit()
	 * @see #getQuantityType()
	 * @generated
	 */
	EReference getQuantityType_DefaultUnit();

	/**
	 * Returns the meta object for class '
	 * {@link de.bund.bfr.knime.pmm.core.common.Unit <em>Unit</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Unit</em>'.
	 * @see de.bund.bfr.knime.pmm.core.common.Unit
	 * @generated
	 */
	EClass getUnit();

	/**
	 * Returns the meta object for the attribute '
	 * {@link de.bund.bfr.knime.pmm.core.common.Unit#getId <em>Id</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see de.bund.bfr.knime.pmm.core.common.Unit#getId()
	 * @see #getUnit()
	 * @generated
	 */
	EAttribute getUnit_Id();

	/**
	 * Returns the meta object for the attribute '
	 * {@link de.bund.bfr.knime.pmm.core.common.Unit#getName <em>Name</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see de.bund.bfr.knime.pmm.core.common.Unit#getName()
	 * @see #getUnit()
	 * @generated
	 */
	EAttribute getUnit_Name();

	/**
	 * Returns the meta object for the attribute '
	 * {@link de.bund.bfr.knime.pmm.core.common.Unit#getConvertFrom
	 * <em>Convert From</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Convert From</em>'.
	 * @see de.bund.bfr.knime.pmm.core.common.Unit#getConvertFrom()
	 * @see #getUnit()
	 * @generated
	 */
	EAttribute getUnit_ConvertFrom();

	/**
	 * Returns the meta object for the attribute '
	 * {@link de.bund.bfr.knime.pmm.core.common.Unit#getConvertTo
	 * <em>Convert To</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Convert To</em>'.
	 * @see de.bund.bfr.knime.pmm.core.common.Unit#getConvertTo()
	 * @see #getUnit()
	 * @generated
	 */
	EAttribute getUnit_ConvertTo();

	/**
	 * Returns the meta object for the reference '
	 * {@link de.bund.bfr.knime.pmm.core.common.Unit#getQuantityType
	 * <em>Quantity Type</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Quantity Type</em>'.
	 * @see de.bund.bfr.knime.pmm.core.common.Unit#getQuantityType()
	 * @see #getUnit()
	 * @generated
	 */
	EReference getUnit_QuantityType();

	/**
	 * Returns the factory that creates the instances of the model. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	CommonFactory getCommonFactory();

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
		 * {@link de.bund.bfr.knime.pmm.core.common.impl.QuantityTypeImpl
		 * <em>Quantity Type</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see de.bund.bfr.knime.pmm.core.common.impl.QuantityTypeImpl
		 * @see de.bund.bfr.knime.pmm.core.common.impl.CommonPackageImpl#getQuantityType()
		 * @generated
		 */
		EClass QUANTITY_TYPE = eINSTANCE.getQuantityType();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute QUANTITY_TYPE__ID = eINSTANCE.getQuantityType_Id();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute QUANTITY_TYPE__NAME = eINSTANCE.getQuantityType_Name();

		/**
		 * The meta object literal for the '<em><b>Units</b></em>' reference
		 * list feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference QUANTITY_TYPE__UNITS = eINSTANCE.getQuantityType_Units();

		/**
		 * The meta object literal for the '<em><b>Default Unit</b></em>'
		 * reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference QUANTITY_TYPE__DEFAULT_UNIT = eINSTANCE
				.getQuantityType_DefaultUnit();

		/**
		 * The meta object literal for the '
		 * {@link de.bund.bfr.knime.pmm.core.common.impl.UnitImpl <em>Unit</em>}
		 * ' class. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @see de.bund.bfr.knime.pmm.core.common.impl.UnitImpl
		 * @see de.bund.bfr.knime.pmm.core.common.impl.CommonPackageImpl#getUnit()
		 * @generated
		 */
		EClass UNIT = eINSTANCE.getUnit();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute UNIT__ID = eINSTANCE.getUnit_Id();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute UNIT__NAME = eINSTANCE.getUnit_Name();

		/**
		 * The meta object literal for the '<em><b>Convert From</b></em>'
		 * attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute UNIT__CONVERT_FROM = eINSTANCE.getUnit_ConvertFrom();

		/**
		 * The meta object literal for the '<em><b>Convert To</b></em>'
		 * attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute UNIT__CONVERT_TO = eINSTANCE.getUnit_ConvertTo();

		/**
		 * The meta object literal for the '<em><b>Quantity Type</b></em>'
		 * reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference UNIT__QUANTITY_TYPE = eINSTANCE.getUnit_QuantityType();

	}

} // CommonPackage
