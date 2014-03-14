/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.bund.bfr.knime.pmm.core.data;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>Time Series Point</b></em>'. <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link de.bund.bfr.knime.pmm.core.data.TimeSeriesPoint#getTime <em>Time
 * </em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.data.TimeSeriesPoint#getConcentration
 * <em>Concentration</em>}</li>
 * </ul>
 * </p>
 * 
 * @see de.bund.bfr.knime.pmm.core.data.DataPackage#getTimeSeriesPoint()
 * @model
 * @generated
 */
public interface TimeSeriesPoint extends EObject {
	/**
	 * Returns the value of the '<em><b>Time</b></em>' attribute. The default
	 * value is <code>"NaN"</code>. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Time</em>' attribute isn't clear, there really
	 * should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Time</em>' attribute.
	 * @see #setTime(double)
	 * @see de.bund.bfr.knime.pmm.core.data.DataPackage#getTimeSeriesPoint_Time()
	 * @model default="NaN"
	 * @generated
	 */
	double getTime();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.data.TimeSeriesPoint#getTime
	 * <em>Time</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Time</em>' attribute.
	 * @see #getTime()
	 * @generated
	 */
	void setTime(double value);

	/**
	 * Returns the value of the '<em><b>Concentration</b></em>' attribute. The
	 * default value is <code>"NaN"</code>. <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Concentration</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Concentration</em>' attribute.
	 * @see #setConcentration(double)
	 * @see de.bund.bfr.knime.pmm.core.data.DataPackage#getTimeSeriesPoint_Concentration()
	 * @model default="NaN"
	 * @generated
	 */
	double getConcentration();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.data.TimeSeriesPoint#getConcentration
	 * <em>Concentration</em>}' attribute. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Concentration</em>' attribute.
	 * @see #getConcentration()
	 * @generated
	 */
	void setConcentration(double value);

} // TimeSeriesPoint
