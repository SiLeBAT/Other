/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.bund.bfr.knime.pmm.core.models;

import org.eclipse.emf.common.util.EMap;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>Model</b></em>'. <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link de.bund.bfr.knime.pmm.core.models.Model#getId <em>Id</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.models.Model#getSse <em>Sse</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.models.Model#getMse <em>Mse</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.models.Model#getRmse <em>Rmse</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.models.Model#getR2 <em>R2</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.models.Model#getAic <em>Aic</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.models.Model#getDegreesOfFreedom <em>
 * Degrees Of Freedom</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.models.Model#getVariableRanges <em>
 * Variable Ranges</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.models.Model#getParamValues <em>Param
 * Values</em>}</li>
 * </ul>
 * </p>
 * 
 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getModel()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface Model extends EObject {
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
	 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getModel_Id()
	 * @model id="true"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.models.Model#getId <em>Id</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

	/**
	 * Returns the value of the '<em><b>Sse</b></em>' attribute. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sse</em>' attribute isn't clear, there really
	 * should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Sse</em>' attribute.
	 * @see #setSse(Double)
	 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getModel_Sse()
	 * @model
	 * @generated
	 */
	Double getSse();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.models.Model#getSse <em>Sse</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Sse</em>' attribute.
	 * @see #getSse()
	 * @generated
	 */
	void setSse(Double value);

	/**
	 * Returns the value of the '<em><b>Mse</b></em>' attribute. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mse</em>' attribute isn't clear, there really
	 * should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Mse</em>' attribute.
	 * @see #setMse(Double)
	 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getModel_Mse()
	 * @model
	 * @generated
	 */
	Double getMse();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.models.Model#getMse <em>Mse</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Mse</em>' attribute.
	 * @see #getMse()
	 * @generated
	 */
	void setMse(Double value);

	/**
	 * Returns the value of the '<em><b>Rmse</b></em>' attribute. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Rmse</em>' attribute isn't clear, there really
	 * should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Rmse</em>' attribute.
	 * @see #setRmse(Double)
	 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getModel_Rmse()
	 * @model
	 * @generated
	 */
	Double getRmse();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.models.Model#getRmse <em>Rmse</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Rmse</em>' attribute.
	 * @see #getRmse()
	 * @generated
	 */
	void setRmse(Double value);

	/**
	 * Returns the value of the '<em><b>R2</b></em>' attribute. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>R2</em>' attribute isn't clear, there really
	 * should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>R2</em>' attribute.
	 * @see #setR2(Double)
	 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getModel_R2()
	 * @model
	 * @generated
	 */
	Double getR2();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.models.Model#getR2 <em>R2</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>R2</em>' attribute.
	 * @see #getR2()
	 * @generated
	 */
	void setR2(Double value);

	/**
	 * Returns the value of the '<em><b>Aic</b></em>' attribute. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Aic</em>' attribute isn't clear, there really
	 * should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Aic</em>' attribute.
	 * @see #setAic(Double)
	 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getModel_Aic()
	 * @model
	 * @generated
	 */
	Double getAic();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.models.Model#getAic <em>Aic</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Aic</em>' attribute.
	 * @see #getAic()
	 * @generated
	 */
	void setAic(Double value);

	/**
	 * Returns the value of the '<em><b>Degrees Of Freedom</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Degrees Of Freedom</em>' attribute isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Degrees Of Freedom</em>' attribute.
	 * @see #setDegreesOfFreedom(Integer)
	 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getModel_DegreesOfFreedom()
	 * @model
	 * @generated
	 */
	Integer getDegreesOfFreedom();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.models.Model#getDegreesOfFreedom
	 * <em>Degrees Of Freedom</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Degrees Of Freedom</em>' attribute.
	 * @see #getDegreesOfFreedom()
	 * @generated
	 */
	void setDegreesOfFreedom(Integer value);

	/**
	 * Returns the value of the '<em><b>Variable Ranges</b></em>' map. The key
	 * is of type {@link java.lang.String}, and the value is of type
	 * {@link de.bund.bfr.knime.pmm.core.models.VariableRange}, <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Variable Ranges</em>' map isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Variable Ranges</em>' map.
	 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getModel_VariableRanges()
	 * @model mapType=
	 *        "de.bund.bfr.knime.pmm.core.models.StringToVariableRangeMapEntry<org.eclipse.emf.ecore.EString, de.bund.bfr.knime.pmm.core.models.VariableRange>"
	 * @generated
	 */
	EMap<String, VariableRange> getVariableRanges();

	/**
	 * Returns the value of the '<em><b>Param Values</b></em>' map. The key is
	 * of type {@link java.lang.String}, and the value is of type
	 * {@link de.bund.bfr.knime.pmm.core.models.ParameterValue}, <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Param Values</em>' map isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Param Values</em>' map.
	 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#getModel_ParamValues()
	 * @model mapType=
	 *        "de.bund.bfr.knime.pmm.core.models.StringToParameterValueMapEntry<org.eclipse.emf.ecore.EString, de.bund.bfr.knime.pmm.core.models.ParameterValue>"
	 * @generated
	 */
	EMap<String, ParameterValue> getParamValues();

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @model kind="operation"
	 * @generated
	 */
	ModelFormula getModelFormula();

} // Model
