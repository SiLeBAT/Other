/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.bund.bfr.knime.pmm.core.models;

import de.bund.bfr.knime.pmm.core.data.TimeSeries;

import org.eclipse.emf.common.util.EMap;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>Primary Model</b></em>'. <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link de.bund.bfr.knime.pmm.core.models.PrimaryModel#getModelFormula
 * <em>Model Formula</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.models.PrimaryModel#getData <em>Data
 * </em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.models.PrimaryModel#getAssignments <em>
 * Assignments</em>}</li>
 * </ul>
 * </p>
 * 
 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getPrimaryModel()
 * @model
 * @generated
 */
public interface PrimaryModel extends Model {
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
	 * @see #setModelFormula(PrimaryModelFormula)
	 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getPrimaryModel_ModelFormula()
	 * @model
	 * @generated
	 */
	@Override
	PrimaryModelFormula getModelFormula();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.models.PrimaryModel#getModelFormula
	 * <em>Model Formula</em>}' reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Model Formula</em>' reference.
	 * @see #getModelFormula()
	 * @generated
	 */
	void setModelFormula(PrimaryModelFormula value);

	/**
	 * Returns the value of the '<em><b>Data</b></em>' reference. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Data</em>' reference isn't clear, there really
	 * should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Data</em>' reference.
	 * @see #setData(TimeSeries)
	 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getPrimaryModel_Data()
	 * @model
	 * @generated
	 */
	TimeSeries getData();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.models.PrimaryModel#getData
	 * <em>Data</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Data</em>' reference.
	 * @see #getData()
	 * @generated
	 */
	void setData(TimeSeries value);

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
	 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getPrimaryModel_Assignments()
	 * @model mapType=
	 *        "de.bund.bfr.knime.pmm.core.models.StringToStringMapEntry<org.eclipse.emf.ecore.EString, org.eclipse.emf.ecore.EString>"
	 * @generated
	 */
	EMap<String, String> getAssignments();

} // PrimaryModel
