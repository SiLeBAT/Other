/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.bund.bfr.knime.pmm.core.models;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>Primary Model Formula</b></em>'. <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link de.bund.bfr.knime.pmm.core.models.PrimaryModelFormula#getIndepVar
 * <em>Indep Var</em>}</li>
 * </ul>
 * </p>
 * 
 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getPrimaryModelFormula()
 * @model
 * @generated
 */
public interface PrimaryModelFormula extends ModelFormula {
	/**
	 * Returns the value of the '<em><b>Indep Var</b></em>' containment
	 * reference. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Indep Var</em>' containment reference isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Indep Var</em>' containment reference.
	 * @see #setIndepVar(Variable)
	 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getPrimaryModelFormula_IndepVar()
	 * @model containment="true"
	 * @generated
	 */
	Variable getIndepVar();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.models.PrimaryModelFormula#getIndepVar
	 * <em>Indep Var</em>}' containment reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Indep Var</em>' containment
	 *            reference.
	 * @see #getIndepVar()
	 * @generated
	 */
	void setIndepVar(Variable value);

} // PrimaryModelFormula
