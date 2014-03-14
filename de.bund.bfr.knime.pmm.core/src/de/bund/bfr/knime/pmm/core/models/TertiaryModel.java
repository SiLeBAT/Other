/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.bund.bfr.knime.pmm.core.models;

import de.bund.bfr.knime.pmm.core.data.TimeSeries;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>Tertiary Model</b></em>'. <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link de.bund.bfr.knime.pmm.core.models.TertiaryModel#getModelFormula
 * <em>Model Formula</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.models.TertiaryModel#getData <em>Data
 * </em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.models.TertiaryModel#getAssignments
 * <em>Assignments</em>}</li>
 * </ul>
 * </p>
 * 
 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getTertiaryModel()
 * @model
 * @generated
 */
public interface TertiaryModel extends Model {
	/**
	 * Returns the value of the '<em><b>Model Formula</b></em>' reference. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Model Formula</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Model Formula</em>' reference.
	 * @see #setModelFormula(TertiaryModelFormula)
	 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getTertiaryModel_ModelFormula()
	 * @model
	 * @generated
	 */
	@Override
	TertiaryModelFormula getModelFormula();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.models.TertiaryModel#getModelFormula
	 * <em>Model Formula</em>}' reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Model Formula</em>' reference.
	 * @see #getModelFormula()
	 * @generated
	 */
	void setModelFormula(TertiaryModelFormula value);

	/**
	 * Returns the value of the '<em><b>Data</b></em>' reference list. The list
	 * contents are of type {@link de.bund.bfr.knime.pmm.core.data.TimeSeries}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Data</em>' reference list isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Data</em>' reference list.
	 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getTertiaryModel_Data()
	 * @model
	 * @generated
	 */
	EList<TimeSeries> getData();

	/**
	 * Returns the value of the '<em><b>Assignments</b></em>' map. The key is of
	 * type {@link java.lang.String}, and the value is of type
	 * {@link java.lang.String}, <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Assignments</em>' map isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Assignments</em>' map.
	 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getTertiaryModel_Assignments()
	 * @model mapType=
	 *        "de.bund.bfr.knime.pmm.core.models.StringToStringMapEntry<org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString>"
	 * @generated
	 */
	EMap<String, String> getAssignments();

} // TertiaryModel
