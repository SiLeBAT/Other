/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.bund.bfr.knime.pmm.core.common;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>Quantity Type</b></em>'. <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link de.bund.bfr.knime.pmm.core.common.QuantityType#getId <em>Id</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.common.QuantityType#getName <em>Name
 * </em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.common.QuantityType#getUnits <em>Units
 * </em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.common.QuantityType#getDefaultUnit <em>
 * Default Unit</em>}</li>
 * </ul>
 * </p>
 * 
 * @see de.bund.bfr.knime.pmm.core.common.CommonPackage#getQuantityType()
 * @model
 * @generated
 */
public interface QuantityType extends EObject {
	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear, there really
	 * should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see de.bund.bfr.knime.pmm.core.common.CommonPackage#getQuantityType_Id()
	 * @model id="true"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.common.QuantityType#getId <em>Id</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear, there really
	 * should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see de.bund.bfr.knime.pmm.core.common.CommonPackage#getQuantityType_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.common.QuantityType#getName
	 * <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Units</b></em>' reference list. The list
	 * contents are of type {@link de.bund.bfr.knime.pmm.core.common.Unit}. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Units</em>' reference list isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Units</em>' reference list.
	 * @see de.bund.bfr.knime.pmm.core.common.CommonPackage#getQuantityType_Units()
	 * @model
	 * @generated
	 */
	EList<Unit> getUnits();

	/**
	 * Returns the value of the '<em><b>Default Unit</b></em>' reference. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Default Unit</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Default Unit</em>' reference.
	 * @see #setDefaultUnit(Unit)
	 * @see de.bund.bfr.knime.pmm.core.common.CommonPackage#getQuantityType_DefaultUnit()
	 * @model
	 * @generated
	 */
	Unit getDefaultUnit();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.common.QuantityType#getDefaultUnit
	 * <em>Default Unit</em>}' reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Default Unit</em>' reference.
	 * @see #getDefaultUnit()
	 * @generated
	 */
	void setDefaultUnit(Unit value);

} // QuantityType
