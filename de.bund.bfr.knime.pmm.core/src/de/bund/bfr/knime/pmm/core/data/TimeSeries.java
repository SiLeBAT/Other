/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.bund.bfr.knime.pmm.core.data;

import de.bund.bfr.knime.pmm.core.common.Unit;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>Time Series</b></em>'. <!-- end-user-doc -->
 * 
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link de.bund.bfr.knime.pmm.core.data.TimeSeries#getId <em>Id</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.data.TimeSeries#getName <em>Name</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.data.TimeSeries#getPoints <em>Points
 * </em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.data.TimeSeries#getTimeUnit <em>Time
 * Unit</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.data.TimeSeries#getConcentrationUnit
 * <em>Concentration Unit</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.data.TimeSeries#getConditions <em>
 * Conditions</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.data.TimeSeries#getOrganism <em>
 * Organism</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.data.TimeSeries#getMatrix <em>Matrix
 * </em>}</li>
 * </ul>
 * </p>
 * 
 * @see de.bund.bfr.knime.pmm.core.data.DataPackage#getTimeSeries()
 * @model
 * @generated
 */
public interface TimeSeries extends EObject {
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
	 * @see de.bund.bfr.knime.pmm.core.data.DataPackage#getTimeSeries_Id()
	 * @model id="true"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.data.TimeSeries#getId <em>Id</em>}'
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
	 * @see de.bund.bfr.knime.pmm.core.data.DataPackage#getTimeSeries_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.data.TimeSeries#getName <em>Name</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Points</b></em>' containment reference
	 * list. The list contents are of type
	 * {@link de.bund.bfr.knime.pmm.core.data.TimeSeriesPoint}. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Points</em>' containment reference list isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Points</em>' containment reference list.
	 * @see de.bund.bfr.knime.pmm.core.data.DataPackage#getTimeSeries_Points()
	 * @model containment="true"
	 * @generated
	 */
	EList<TimeSeriesPoint> getPoints();

	/**
	 * Returns the value of the '<em><b>Time Unit</b></em>' reference. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Time Unit</em>' reference isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Time Unit</em>' reference.
	 * @see #setTimeUnit(Unit)
	 * @see de.bund.bfr.knime.pmm.core.data.DataPackage#getTimeSeries_TimeUnit()
	 * @model
	 * @generated
	 */
	Unit getTimeUnit();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.data.TimeSeries#getTimeUnit
	 * <em>Time Unit</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @param value
	 *            the new value of the '<em>Time Unit</em>' reference.
	 * @see #getTimeUnit()
	 * @generated
	 */
	void setTimeUnit(Unit value);

	/**
	 * Returns the value of the '<em><b>Concentration Unit</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Concentration Unit</em>' reference isn't
	 * clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Concentration Unit</em>' reference.
	 * @see #setConcentrationUnit(Unit)
	 * @see de.bund.bfr.knime.pmm.core.data.DataPackage#getTimeSeries_ConcentrationUnit()
	 * @model
	 * @generated
	 */
	Unit getConcentrationUnit();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.data.TimeSeries#getConcentrationUnit
	 * <em>Concentration Unit</em>}' reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @param value
	 *            the new value of the '<em>Concentration Unit</em>' reference.
	 * @see #getConcentrationUnit()
	 * @generated
	 */
	void setConcentrationUnit(Unit value);

	/**
	 * Returns the value of the '<em><b>Conditions</b></em>' containment
	 * reference list. The list contents are of type
	 * {@link de.bund.bfr.knime.pmm.core.data.Condition}. <!-- begin-user-doc
	 * -->
	 * <p>
	 * If the meaning of the '<em>Conditions</em>' containment reference list
	 * isn't clear, there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Conditions</em>' containment reference
	 *         list.
	 * @see de.bund.bfr.knime.pmm.core.data.DataPackage#getTimeSeries_Conditions()
	 * @model containment="true"
	 * @generated
	 */
	EList<Condition> getConditions();

	/**
	 * Returns the value of the '<em><b>Organism</b></em>' reference. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Organism</em>' reference isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Organism</em>' reference.
	 * @see #setOrganism(Organism)
	 * @see de.bund.bfr.knime.pmm.core.data.DataPackage#getTimeSeries_Organism()
	 * @model
	 * @generated
	 */
	Organism getOrganism();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.data.TimeSeries#getOrganism
	 * <em>Organism</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @param value
	 *            the new value of the '<em>Organism</em>' reference.
	 * @see #getOrganism()
	 * @generated
	 */
	void setOrganism(Organism value);

	/**
	 * Returns the value of the '<em><b>Matrix</b></em>' reference. <!--
	 * begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Matrix</em>' reference isn't clear, there
	 * really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * 
	 * @return the value of the '<em>Matrix</em>' reference.
	 * @see #setMatrix(Matrix)
	 * @see de.bund.bfr.knime.pmm.core.data.DataPackage#getTimeSeries_Matrix()
	 * @model
	 * @generated
	 */
	Matrix getMatrix();

	/**
	 * Sets the value of the '
	 * {@link de.bund.bfr.knime.pmm.core.data.TimeSeries#getMatrix
	 * <em>Matrix</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @param value
	 *            the new value of the '<em>Matrix</em>' reference.
	 * @see #getMatrix()
	 * @generated
	 */
	void setMatrix(Matrix value);

} // TimeSeries
