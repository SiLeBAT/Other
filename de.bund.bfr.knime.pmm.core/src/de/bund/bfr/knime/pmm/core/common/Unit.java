/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.bund.bfr.knime.pmm.core.common;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>Unit</b></em>'. <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link de.bund.bfr.knime.pmm.core.common.Unit#getId <em>Id</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.common.Unit#getName <em>Name</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.common.Unit#getConvertFrom <em>Convert
 * From</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.common.Unit#getConvertTo <em>Convert To
 * </em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.common.Unit#getQuantityType <em>
 * Quantity Type</em>}</li>
 * </ul>
 * </p>
 * 
 * @see de.bund.bfr.knime.pmm.core.common.CommonPackage#getUnit()
 * @model
 * @generated
 */
public interface Unit extends EObject {
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
	 * @see de.bund.bfr.knime.pmm.core.common.CommonPackage#getUnit_Id()
	 * @model id="true"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.common.Unit#getId <em>Id</em>}'
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
	 * @see de.bund.bfr.knime.pmm.core.common.CommonPackage#getUnit_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.common.Unit#getName <em>Name</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Convert From</b></em>' attribute. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Convert From</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Convert From</em>' attribute.
	 * @see #setConvertFrom(String)
	 * @see de.bund.bfr.knime.pmm.core.common.CommonPackage#getUnit_ConvertFrom()
	 * @model
	 * @generated
	 */
	String getConvertFrom();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.common.Unit#getConvertFrom
	 * <em>Convert From</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Convert From</em>' attribute.
	 * @see #getConvertFrom()
	 * @generated
	 */
	void setConvertFrom(String value);

	/**
	 * Returns the value of the '<em><b>Convert To</b></em>' attribute. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Convert To</em>' attribute isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Convert To</em>' attribute.
	 * @see #setConvertTo(String)
	 * @see de.bund.bfr.knime.pmm.core.common.CommonPackage#getUnit_ConvertTo()
	 * @model
	 * @generated
	 */
	String getConvertTo();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.common.Unit#getConvertTo
	 * <em>Convert To</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Convert To</em>' attribute.
	 * @see #getConvertTo()
	 * @generated
	 */
	void setConvertTo(String value);

	/**
	 * Returns the value of the '<em><b>Quantity Type</b></em>' reference. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Quantity Type</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Quantity Type</em>' reference.
	 * @see #setQuantityType(QuantityType)
	 * @see de.bund.bfr.knime.pmm.core.common.CommonPackage#getUnit_QuantityType()
	 * @model
	 * @generated
	 */
	QuantityType getQuantityType();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.common.Unit#getQuantityType
	 * <em>Quantity Type</em>}' reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Quantity Type</em>' reference.
	 * @see #getQuantityType()
	 * @generated
	 */
	void setQuantityType(QuantityType value);

} // Unit
