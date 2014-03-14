/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.bund.bfr.knime.pmm.core.data;

import de.bund.bfr.knime.pmm.core.common.QuantityType;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>Condition Parameter</b></em>'. <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link de.bund.bfr.knime.pmm.core.data.ConditionParameter#getId <em>Id
 * </em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.data.ConditionParameter#getName <em>
 * Name</em>}</li>
 * <li>
 * {@link de.bund.bfr.knime.pmm.core.data.ConditionParameter#getQuantityTypes
 * <em>Quantity Types</em>}</li>
 * </ul>
 * </p>
 * 
 * @see de.bund.bfr.knime.pmm.core.data.DataPackage#getConditionParameter()
 * @model
 * @generated
 */
public interface ConditionParameter extends EObject {
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
	 * @see de.bund.bfr.knime.pmm.core.data.DataPackage#getConditionParameter_Id()
	 * @model id="true"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.data.ConditionParameter#getId
	 * <em>Id</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
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
	 * @see de.bund.bfr.knime.pmm.core.data.DataPackage#getConditionParameter_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.data.ConditionParameter#getName
	 * <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Quantity Types</b></em>' reference list.
	 * The list contents are of type
	 * {@link de.bund.bfr.knime.pmm.core.common.QuantityType}. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Quantity Types</em>' reference list isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Quantity Types</em>' reference list.
	 * @see de.bund.bfr.knime.pmm.core.data.DataPackage#getConditionParameter_QuantityTypes()
	 * @model
	 * @generated
	 */
	EList<QuantityType> getQuantityTypes();

} // ConditionParameter
