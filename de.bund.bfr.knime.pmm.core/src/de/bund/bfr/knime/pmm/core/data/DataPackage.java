/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.bund.bfr.knime.pmm.core.data;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains
 * accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * 
 * @see de.bund.bfr.knime.pmm.core.data.DataFactory
 * @model kind="package"
 * @generated
 */
public interface DataPackage extends EPackage {
	/**
	 * The package name. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	String eNAME = "data";

	/**
	 * The package namespace URI. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	String eNS_URI = "http:///de/bund/bfr/knime/pmm/core/data.ecore";

	/**
	 * The package namespace name. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	String eNS_PREFIX = "de.bund.bfr.knime.pmm.core.data";

	/**
	 * The singleton instance of the package. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	DataPackage eINSTANCE = de.bund.bfr.knime.pmm.core.data.impl.DataPackageImpl
			.init();

	/**
	 * The meta object id for the '
	 * {@link de.bund.bfr.knime.pmm.core.data.impl.TimeSeriesImpl
	 * <em>Time Series</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @see de.bund.bfr.knime.pmm.core.data.impl.TimeSeriesImpl
	 * @see de.bund.bfr.knime.pmm.core.data.impl.DataPackageImpl#getTimeSeries()
	 * @generated
	 */
	int TIME_SERIES = 0;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int TIME_SERIES__ID = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int TIME_SERIES__NAME = 1;

	/**
	 * The feature id for the '<em><b>Points</b></em>' containment reference
	 * list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int TIME_SERIES__POINTS = 2;

	/**
	 * The feature id for the '<em><b>Time Unit</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int TIME_SERIES__TIME_UNIT = 3;

	/**
	 * The feature id for the '<em><b>Concentration Unit</b></em>' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int TIME_SERIES__CONCENTRATION_UNIT = 4;

	/**
	 * The feature id for the '<em><b>Conditions</b></em>' containment reference
	 * list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int TIME_SERIES__CONDITIONS = 5;

	/**
	 * The feature id for the '<em><b>Organism</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int TIME_SERIES__ORGANISM = 6;

	/**
	 * The feature id for the '<em><b>Matrix</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int TIME_SERIES__MATRIX = 7;

	/**
	 * The number of structural features of the '<em>Time Series</em>' class.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int TIME_SERIES_FEATURE_COUNT = 8;

	/**
	 * The meta object id for the '
	 * {@link de.bund.bfr.knime.pmm.core.data.impl.TimeSeriesPointImpl
	 * <em>Time Series Point</em>}' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see de.bund.bfr.knime.pmm.core.data.impl.TimeSeriesPointImpl
	 * @see de.bund.bfr.knime.pmm.core.data.impl.DataPackageImpl#getTimeSeriesPoint()
	 * @generated
	 */
	int TIME_SERIES_POINT = 1;

	/**
	 * The feature id for the '<em><b>Time</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int TIME_SERIES_POINT__TIME = 0;

	/**
	 * The feature id for the '<em><b>Concentration</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int TIME_SERIES_POINT__CONCENTRATION = 1;

	/**
	 * The number of structural features of the '<em>Time Series Point</em>'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int TIME_SERIES_POINT_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '
	 * {@link de.bund.bfr.knime.pmm.core.data.impl.OrganismImpl
	 * <em>Organism</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see de.bund.bfr.knime.pmm.core.data.impl.OrganismImpl
	 * @see de.bund.bfr.knime.pmm.core.data.impl.DataPackageImpl#getOrganism()
	 * @generated
	 */
	int ORGANISM = 2;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ORGANISM__ID = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ORGANISM__NAME = 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ORGANISM__DESCRIPTION = 2;

	/**
	 * The number of structural features of the '<em>Organism</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int ORGANISM_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '
	 * {@link de.bund.bfr.knime.pmm.core.data.impl.MatrixImpl <em>Matrix</em>}'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see de.bund.bfr.knime.pmm.core.data.impl.MatrixImpl
	 * @see de.bund.bfr.knime.pmm.core.data.impl.DataPackageImpl#getMatrix()
	 * @generated
	 */
	int MATRIX = 3;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MATRIX__ID = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MATRIX__NAME = 1;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MATRIX__DESCRIPTION = 2;

	/**
	 * The number of structural features of the '<em>Matrix</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int MATRIX_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '
	 * {@link de.bund.bfr.knime.pmm.core.data.impl.ConditionParameterImpl
	 * <em>Condition Parameter</em>}' class. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see de.bund.bfr.knime.pmm.core.data.impl.ConditionParameterImpl
	 * @see de.bund.bfr.knime.pmm.core.data.impl.DataPackageImpl#getConditionParameter()
	 * @generated
	 */
	int CONDITION_PARAMETER = 4;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONDITION_PARAMETER__ID = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONDITION_PARAMETER__NAME = 1;

	/**
	 * The feature id for the '<em><b>Quantity Types</b></em>' reference list.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONDITION_PARAMETER__QUANTITY_TYPES = 2;

	/**
	 * The number of structural features of the '<em>Condition Parameter</em>'
	 * class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONDITION_PARAMETER_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '
	 * {@link de.bund.bfr.knime.pmm.core.data.impl.ConditionImpl
	 * <em>Condition</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see de.bund.bfr.knime.pmm.core.data.impl.ConditionImpl
	 * @see de.bund.bfr.knime.pmm.core.data.impl.DataPackageImpl#getCondition()
	 * @generated
	 */
	int CONDITION = 5;

	/**
	 * The feature id for the '<em><b>Parameter</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONDITION__PARAMETER = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONDITION__VALUE = 1;

	/**
	 * The feature id for the '<em><b>Unit</b></em>' reference. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONDITION__UNIT = 2;

	/**
	 * The number of structural features of the '<em>Condition</em>' class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 * @ordered
	 */
	int CONDITION_FEATURE_COUNT = 3;

	/**
	 * Returns the meta object for class '
	 * {@link de.bund.bfr.knime.pmm.core.data.TimeSeries <em>Time Series</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Time Series</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.TimeSeries
	 * @generated
	 */
	EClass getTimeSeries();

	/**
	 * Returns the meta object for the attribute '
	 * {@link de.bund.bfr.knime.pmm.core.data.TimeSeries#getId <em>Id</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.TimeSeries#getId()
	 * @see #getTimeSeries()
	 * @generated
	 */
	EAttribute getTimeSeries_Id();

	/**
	 * Returns the meta object for the attribute '
	 * {@link de.bund.bfr.knime.pmm.core.data.TimeSeries#getName <em>Name</em>}
	 * '. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.TimeSeries#getName()
	 * @see #getTimeSeries()
	 * @generated
	 */
	EAttribute getTimeSeries_Name();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link de.bund.bfr.knime.pmm.core.data.TimeSeries#getPoints
	 * <em>Points</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '
	 *         <em>Points</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.TimeSeries#getPoints()
	 * @see #getTimeSeries()
	 * @generated
	 */
	EReference getTimeSeries_Points();

	/**
	 * Returns the meta object for the reference '
	 * {@link de.bund.bfr.knime.pmm.core.data.TimeSeries#getTimeUnit
	 * <em>Time Unit</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Time Unit</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.TimeSeries#getTimeUnit()
	 * @see #getTimeSeries()
	 * @generated
	 */
	EReference getTimeSeries_TimeUnit();

	/**
	 * Returns the meta object for the reference '
	 * {@link de.bund.bfr.knime.pmm.core.data.TimeSeries#getConcentrationUnit
	 * <em>Concentration Unit</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @return the meta object for the reference '<em>Concentration Unit</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.TimeSeries#getConcentrationUnit()
	 * @see #getTimeSeries()
	 * @generated
	 */
	EReference getTimeSeries_ConcentrationUnit();

	/**
	 * Returns the meta object for the containment reference list '
	 * {@link de.bund.bfr.knime.pmm.core.data.TimeSeries#getConditions
	 * <em>Conditions</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the containment reference list '
	 *         <em>Conditions</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.TimeSeries#getConditions()
	 * @see #getTimeSeries()
	 * @generated
	 */
	EReference getTimeSeries_Conditions();

	/**
	 * Returns the meta object for the reference '
	 * {@link de.bund.bfr.knime.pmm.core.data.TimeSeries#getOrganism
	 * <em>Organism</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Organism</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.TimeSeries#getOrganism()
	 * @see #getTimeSeries()
	 * @generated
	 */
	EReference getTimeSeries_Organism();

	/**
	 * Returns the meta object for the reference '
	 * {@link de.bund.bfr.knime.pmm.core.data.TimeSeries#getMatrix
	 * <em>Matrix</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Matrix</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.TimeSeries#getMatrix()
	 * @see #getTimeSeries()
	 * @generated
	 */
	EReference getTimeSeries_Matrix();

	/**
	 * Returns the meta object for class '
	 * {@link de.bund.bfr.knime.pmm.core.data.TimeSeriesPoint
	 * <em>Time Series Point</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @return the meta object for class '<em>Time Series Point</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.TimeSeriesPoint
	 * @generated
	 */
	EClass getTimeSeriesPoint();

	/**
	 * Returns the meta object for the attribute '
	 * {@link de.bund.bfr.knime.pmm.core.data.TimeSeriesPoint#getTime
	 * <em>Time</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Time</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.TimeSeriesPoint#getTime()
	 * @see #getTimeSeriesPoint()
	 * @generated
	 */
	EAttribute getTimeSeriesPoint_Time();

	/**
	 * Returns the meta object for the attribute '
	 * {@link de.bund.bfr.knime.pmm.core.data.TimeSeriesPoint#getConcentration
	 * <em>Concentration</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Concentration</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.TimeSeriesPoint#getConcentration()
	 * @see #getTimeSeriesPoint()
	 * @generated
	 */
	EAttribute getTimeSeriesPoint_Concentration();

	/**
	 * Returns the meta object for class '
	 * {@link de.bund.bfr.knime.pmm.core.data.Organism <em>Organism</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Organism</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.Organism
	 * @generated
	 */
	EClass getOrganism();

	/**
	 * Returns the meta object for the attribute '
	 * {@link de.bund.bfr.knime.pmm.core.data.Organism#getId <em>Id</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.Organism#getId()
	 * @see #getOrganism()
	 * @generated
	 */
	EAttribute getOrganism_Id();

	/**
	 * Returns the meta object for the attribute '
	 * {@link de.bund.bfr.knime.pmm.core.data.Organism#getName <em>Name</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.Organism#getName()
	 * @see #getOrganism()
	 * @generated
	 */
	EAttribute getOrganism_Name();

	/**
	 * Returns the meta object for the attribute '
	 * {@link de.bund.bfr.knime.pmm.core.data.Organism#getDescription
	 * <em>Description</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.Organism#getDescription()
	 * @see #getOrganism()
	 * @generated
	 */
	EAttribute getOrganism_Description();

	/**
	 * Returns the meta object for class '
	 * {@link de.bund.bfr.knime.pmm.core.data.Matrix <em>Matrix</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Matrix</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.Matrix
	 * @generated
	 */
	EClass getMatrix();

	/**
	 * Returns the meta object for the attribute '
	 * {@link de.bund.bfr.knime.pmm.core.data.Matrix#getId <em>Id</em>}'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.Matrix#getId()
	 * @see #getMatrix()
	 * @generated
	 */
	EAttribute getMatrix_Id();

	/**
	 * Returns the meta object for the attribute '
	 * {@link de.bund.bfr.knime.pmm.core.data.Matrix#getName <em>Name</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.Matrix#getName()
	 * @see #getMatrix()
	 * @generated
	 */
	EAttribute getMatrix_Name();

	/**
	 * Returns the meta object for the attribute '
	 * {@link de.bund.bfr.knime.pmm.core.data.Matrix#getDescription
	 * <em>Description</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Description</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.Matrix#getDescription()
	 * @see #getMatrix()
	 * @generated
	 */
	EAttribute getMatrix_Description();

	/**
	 * Returns the meta object for class '
	 * {@link de.bund.bfr.knime.pmm.core.data.ConditionParameter
	 * <em>Condition Parameter</em>}'. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @return the meta object for class '<em>Condition Parameter</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.ConditionParameter
	 * @generated
	 */
	EClass getConditionParameter();

	/**
	 * Returns the meta object for the attribute '
	 * {@link de.bund.bfr.knime.pmm.core.data.ConditionParameter#getId
	 * <em>Id</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.ConditionParameter#getId()
	 * @see #getConditionParameter()
	 * @generated
	 */
	EAttribute getConditionParameter_Id();

	/**
	 * Returns the meta object for the attribute '
	 * {@link de.bund.bfr.knime.pmm.core.data.ConditionParameter#getName
	 * <em>Name</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.ConditionParameter#getName()
	 * @see #getConditionParameter()
	 * @generated
	 */
	EAttribute getConditionParameter_Name();

	/**
	 * Returns the meta object for the reference list '
	 * {@link de.bund.bfr.knime.pmm.core.data.ConditionParameter#getQuantityTypes
	 * <em>Quantity Types</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference list '<em>Quantity Types</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.ConditionParameter#getQuantityTypes()
	 * @see #getConditionParameter()
	 * @generated
	 */
	EReference getConditionParameter_QuantityTypes();

	/**
	 * Returns the meta object for class '
	 * {@link de.bund.bfr.knime.pmm.core.data.Condition <em>Condition</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for class '<em>Condition</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.Condition
	 * @generated
	 */
	EClass getCondition();

	/**
	 * Returns the meta object for the reference '
	 * {@link de.bund.bfr.knime.pmm.core.data.Condition#getParameter
	 * <em>Parameter</em>}'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Parameter</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.Condition#getParameter()
	 * @see #getCondition()
	 * @generated
	 */
	EReference getCondition_Parameter();

	/**
	 * Returns the meta object for the attribute '
	 * {@link de.bund.bfr.knime.pmm.core.data.Condition#getValue <em>Value</em>}
	 * '. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.Condition#getValue()
	 * @see #getCondition()
	 * @generated
	 */
	EAttribute getCondition_Value();

	/**
	 * Returns the meta object for the reference '
	 * {@link de.bund.bfr.knime.pmm.core.data.Condition#getUnit <em>Unit</em>}'.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the meta object for the reference '<em>Unit</em>'.
	 * @see de.bund.bfr.knime.pmm.core.data.Condition#getUnit()
	 * @see #getCondition()
	 * @generated
	 */
	EReference getCondition_Unit();

	/**
	 * Returns the factory that creates the instances of the model. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	DataFactory getDataFactory();

	/**
	 * <!-- begin-user-doc --> Defines literals for the meta objects that
	 * represent
	 * <ul>
	 * <li>each class,</li>
	 * <li>each feature of each class,</li>
	 * <li>each enum,</li>
	 * <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '
		 * {@link de.bund.bfr.knime.pmm.core.data.impl.TimeSeriesImpl
		 * <em>Time Series</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see de.bund.bfr.knime.pmm.core.data.impl.TimeSeriesImpl
		 * @see de.bund.bfr.knime.pmm.core.data.impl.DataPackageImpl#getTimeSeries()
		 * @generated
		 */
		EClass TIME_SERIES = eINSTANCE.getTimeSeries();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute TIME_SERIES__ID = eINSTANCE.getTimeSeries_Id();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute TIME_SERIES__NAME = eINSTANCE.getTimeSeries_Name();

		/**
		 * The meta object literal for the '<em><b>Points</b></em>' containment
		 * reference list feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference TIME_SERIES__POINTS = eINSTANCE.getTimeSeries_Points();

		/**
		 * The meta object literal for the '<em><b>Time Unit</b></em>' reference
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference TIME_SERIES__TIME_UNIT = eINSTANCE.getTimeSeries_TimeUnit();

		/**
		 * The meta object literal for the '<em><b>Concentration Unit</b></em>'
		 * reference feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference TIME_SERIES__CONCENTRATION_UNIT = eINSTANCE
				.getTimeSeries_ConcentrationUnit();

		/**
		 * The meta object literal for the '<em><b>Conditions</b></em>'
		 * containment reference list feature. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @generated
		 */
		EReference TIME_SERIES__CONDITIONS = eINSTANCE
				.getTimeSeries_Conditions();

		/**
		 * The meta object literal for the '<em><b>Organism</b></em>' reference
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference TIME_SERIES__ORGANISM = eINSTANCE.getTimeSeries_Organism();

		/**
		 * The meta object literal for the '<em><b>Matrix</b></em>' reference
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference TIME_SERIES__MATRIX = eINSTANCE.getTimeSeries_Matrix();

		/**
		 * The meta object literal for the '
		 * {@link de.bund.bfr.knime.pmm.core.data.impl.TimeSeriesPointImpl
		 * <em>Time Series Point</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see de.bund.bfr.knime.pmm.core.data.impl.TimeSeriesPointImpl
		 * @see de.bund.bfr.knime.pmm.core.data.impl.DataPackageImpl#getTimeSeriesPoint()
		 * @generated
		 */
		EClass TIME_SERIES_POINT = eINSTANCE.getTimeSeriesPoint();

		/**
		 * The meta object literal for the '<em><b>Time</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute TIME_SERIES_POINT__TIME = eINSTANCE
				.getTimeSeriesPoint_Time();

		/**
		 * The meta object literal for the '<em><b>Concentration</b></em>'
		 * attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute TIME_SERIES_POINT__CONCENTRATION = eINSTANCE
				.getTimeSeriesPoint_Concentration();

		/**
		 * The meta object literal for the '
		 * {@link de.bund.bfr.knime.pmm.core.data.impl.OrganismImpl
		 * <em>Organism</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 * 
		 * @see de.bund.bfr.knime.pmm.core.data.impl.OrganismImpl
		 * @see de.bund.bfr.knime.pmm.core.data.impl.DataPackageImpl#getOrganism()
		 * @generated
		 */
		EClass ORGANISM = eINSTANCE.getOrganism();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute ORGANISM__ID = eINSTANCE.getOrganism_Id();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute ORGANISM__NAME = eINSTANCE.getOrganism_Name();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>'
		 * attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute ORGANISM__DESCRIPTION = eINSTANCE.getOrganism_Description();

		/**
		 * The meta object literal for the '
		 * {@link de.bund.bfr.knime.pmm.core.data.impl.MatrixImpl
		 * <em>Matrix</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 * 
		 * @see de.bund.bfr.knime.pmm.core.data.impl.MatrixImpl
		 * @see de.bund.bfr.knime.pmm.core.data.impl.DataPackageImpl#getMatrix()
		 * @generated
		 */
		EClass MATRIX = eINSTANCE.getMatrix();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute MATRIX__ID = eINSTANCE.getMatrix_Id();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute MATRIX__NAME = eINSTANCE.getMatrix_Name();

		/**
		 * The meta object literal for the '<em><b>Description</b></em>'
		 * attribute feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute MATRIX__DESCRIPTION = eINSTANCE.getMatrix_Description();

		/**
		 * The meta object literal for the '
		 * {@link de.bund.bfr.knime.pmm.core.data.impl.ConditionParameterImpl
		 * <em>Condition Parameter</em>}' class. <!-- begin-user-doc --> <!--
		 * end-user-doc -->
		 * 
		 * @see de.bund.bfr.knime.pmm.core.data.impl.ConditionParameterImpl
		 * @see de.bund.bfr.knime.pmm.core.data.impl.DataPackageImpl#getConditionParameter()
		 * @generated
		 */
		EClass CONDITION_PARAMETER = eINSTANCE.getConditionParameter();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute CONDITION_PARAMETER__ID = eINSTANCE
				.getConditionParameter_Id();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute CONDITION_PARAMETER__NAME = eINSTANCE
				.getConditionParameter_Name();

		/**
		 * The meta object literal for the '<em><b>Quantity Types</b></em>'
		 * reference list feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference CONDITION_PARAMETER__QUANTITY_TYPES = eINSTANCE
				.getConditionParameter_QuantityTypes();

		/**
		 * The meta object literal for the '
		 * {@link de.bund.bfr.knime.pmm.core.data.impl.ConditionImpl
		 * <em>Condition</em>}' class. <!-- begin-user-doc --> <!-- end-user-doc
		 * -->
		 * 
		 * @see de.bund.bfr.knime.pmm.core.data.impl.ConditionImpl
		 * @see de.bund.bfr.knime.pmm.core.data.impl.DataPackageImpl#getCondition()
		 * @generated
		 */
		EClass CONDITION = eINSTANCE.getCondition();

		/**
		 * The meta object literal for the '<em><b>Parameter</b></em>' reference
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference CONDITION__PARAMETER = eINSTANCE.getCondition_Parameter();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EAttribute CONDITION__VALUE = eINSTANCE.getCondition_Value();

		/**
		 * The meta object literal for the '<em><b>Unit</b></em>' reference
		 * feature. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		EReference CONDITION__UNIT = eINSTANCE.getCondition_Unit();

	}

} // DataPackage
