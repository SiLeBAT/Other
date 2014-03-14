/**
 */
package de.bund.bfr.knime.pmm.core.datatypes;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>EPair</b></em>'. <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link de.bund.bfr.knime.pmm.core.datatypes.EPair#getValue1 <em>Value1
 * </em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.datatypes.EPair#getValue2 <em>Value2
 * </em>}</li>
 * </ul>
 * </p>
 * 
 * @see de.bund.bfr.knime.pmm.core.datatypes.DatatypesPackage#getEPair()
 * @model
 * @generated
 */
public interface EPair extends EObject {
	/**
	 * Returns the value of the '<em><b>Value1</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Value1</em>' containment reference isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Value1</em>' containment reference.
	 * @see #setValue1(EObject)
	 * @see de.bund.bfr.knime.pmm.core.datatypes.DatatypesPackage#getEPair_Value1()
	 * @model containment="true"
	 * @generated
	 */
	EObject getValue1();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.datatypes.EPair#getValue1
	 * <em>Value1</em>}' containment reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Value1</em>' containment reference.
	 * @see #getValue1()
	 * @generated
	 */
	void setValue1(EObject value);

	/**
	 * Returns the value of the '<em><b>Value2</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Value2</em>' containment reference isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Value2</em>' containment reference.
	 * @see #setValue2(EObject)
	 * @see de.bund.bfr.knime.pmm.core.datatypes.DatatypesPackage#getEPair_Value2()
	 * @model containment="true"
	 * @generated
	 */
	EObject getValue2();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.datatypes.EPair#getValue2
	 * <em>Value2</em>}' containment reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Value2</em>' containment reference.
	 * @see #getValue2()
	 * @generated
	 */
	void setValue2(EObject value);

} // EPair
