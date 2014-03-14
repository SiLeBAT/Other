/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.bund.bfr.knime.pmm.core.models;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>Model Formula</b></em>'. <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link de.bund.bfr.knime.pmm.core.models.ModelFormula#getId <em>Id</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.models.ModelFormula#getName <em>Name
 * </em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.models.ModelFormula#getFormula <em>
 * Formula</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.models.ModelFormula#getDepVar <em>Dep
 * Var</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.models.ModelFormula#getParams <em>
 * Params</em>}</li>
 * </ul>
 * </p>
 * 
 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getModelFormula()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ModelFormula extends EObject {
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
	 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getModelFormula_Id()
	 * @model id="true"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.models.ModelFormula#getId <em>Id</em>}'
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
	 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getModelFormula_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.models.ModelFormula#getName
	 * <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Formula</b></em>' attribute. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Formula</em>' attribute isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Formula</em>' attribute.
	 * @see #setFormula(String)
	 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getModelFormula_Formula()
	 * @model
	 * @generated
	 */
	String getFormula();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.models.ModelFormula#getFormula
	 * <em>Formula</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @param value
	 *            the new value of the '<em>Formula</em>' attribute.
	 * @see #getFormula()
	 * @generated
	 */
	void setFormula(String value);

	/**
	 * Returns the value of the '<em><b>Dep Var</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Dep Var</em>' containment reference isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Dep Var</em>' containment reference.
	 * @see #setDepVar(Variable)
	 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getModelFormula_DepVar()
	 * @model containment="true"
	 * @generated
	 */
	Variable getDepVar();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.models.ModelFormula#getDepVar
	 * <em>Dep Var</em>}' containment reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Dep Var</em>' containment reference.
	 * @see #getDepVar()
	 * @generated
	 */
	void setDepVar(Variable value);

	/**
	 * Returns the value of the '<em><b>Params</b></em>' containment reference
	 * list. The list contents are of type
	 * {@link de.bund.bfr.knime.pmm.core.models.Parameter}. <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of the '<em>Params</em>' containment reference list isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Params</em>' containment reference list.
	 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getModelFormula_Params()
	 * @model containment="true"
	 * @generated
	 */
	EList<Parameter> getParams();

} // ModelFormula
