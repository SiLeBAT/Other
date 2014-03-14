/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.bund.bfr.knime.pmm.core.models;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>Tertiary Model Formula</b></em>'. <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>
 * {@link de.bund.bfr.knime.pmm.core.models.TertiaryModelFormula#getIndepVars
 * <em>Indep Vars</em>}</li>
 * </ul>
 * </p>
 * 
 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getTertiaryModelFormula()
 * @model
 * @generated
 */
public interface TertiaryModelFormula extends ModelFormula {
	/**
	 * Returns the value of the '<em><b>Indep Vars</b></em>' containment
	 * reference list. The list contents are of type
	 * {@link de.bund.bfr.knime.pmm.core.models.Variable}. <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of the '<em>Indep Vars</em>' containment reference list
	 * isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Indep Vars</em>' containment reference
	 *         list.
	 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getTertiaryModelFormula_IndepVars()
	 * @model containment="true"
	 * @generated
	 */
	EList<Variable> getIndepVars();

} // TertiaryModelFormula
