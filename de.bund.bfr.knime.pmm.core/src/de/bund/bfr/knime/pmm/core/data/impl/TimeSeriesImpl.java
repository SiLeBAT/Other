/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.bund.bfr.knime.pmm.core.data.impl;

import de.bund.bfr.knime.pmm.core.common.Unit;

import de.bund.bfr.knime.pmm.core.data.Condition;
import de.bund.bfr.knime.pmm.core.data.DataPackage;
import de.bund.bfr.knime.pmm.core.data.Matrix;
import de.bund.bfr.knime.pmm.core.data.Organism;
import de.bund.bfr.knime.pmm.core.data.TimeSeries;
import de.bund.bfr.knime.pmm.core.data.TimeSeriesPoint;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Time Series</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link de.bund.bfr.knime.pmm.core.data.impl.TimeSeriesImpl#getId <em>Id
 * </em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.data.impl.TimeSeriesImpl#getName <em>
 * Name</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.data.impl.TimeSeriesImpl#getPoints <em>
 * Points</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.data.impl.TimeSeriesImpl#getTimeUnit
 * <em>Time Unit</em>}</li>
 * <li>
 * {@link de.bund.bfr.knime.pmm.core.data.impl.TimeSeriesImpl#getConcentrationUnit
 * <em>Concentration Unit</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.data.impl.TimeSeriesImpl#getConditions
 * <em>Conditions</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.data.impl.TimeSeriesImpl#getOrganism
 * <em>Organism</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.data.impl.TimeSeriesImpl#getMatrix <em>
 * Matrix</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class TimeSeriesImpl extends EObjectImpl implements TimeSeries {
	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getPoints() <em>Points</em>}'
	 * containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getPoints()
	 * @generated
	 * @ordered
	 */
	protected EList<TimeSeriesPoint> points;

	/**
	 * The cached value of the '{@link #getTimeUnit() <em>Time Unit</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getTimeUnit()
	 * @generated
	 * @ordered
	 */
	protected Unit timeUnit;

	/**
	 * The cached value of the '{@link #getConcentrationUnit()
	 * <em>Concentration Unit</em>}' reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getConcentrationUnit()
	 * @generated
	 * @ordered
	 */
	protected Unit concentrationUnit;

	/**
	 * The cached value of the '{@link #getConditions() <em>Conditions</em>}'
	 * containment reference list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getConditions()
	 * @generated
	 * @ordered
	 */
	protected EList<Condition> conditions;

	/**
	 * The cached value of the '{@link #getOrganism() <em>Organism</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getOrganism()
	 * @generated
	 * @ordered
	 */
	protected Organism organism;

	/**
	 * The cached value of the '{@link #getMatrix() <em>Matrix</em>}' reference.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getMatrix()
	 * @generated
	 * @ordered
	 */
	protected Matrix matrix;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected TimeSeriesImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DataPackage.Literals.TIME_SERIES;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					DataPackage.TIME_SERIES__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					DataPackage.TIME_SERIES__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EList<TimeSeriesPoint> getPoints() {
		if (points == null) {
			points = new EObjectContainmentEList<TimeSeriesPoint>(
					TimeSeriesPoint.class, this,
					DataPackage.TIME_SERIES__POINTS);
		}
		return points;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Unit getTimeUnit() {
		if (timeUnit != null && timeUnit.eIsProxy()) {
			InternalEObject oldTimeUnit = (InternalEObject) timeUnit;
			timeUnit = (Unit) eResolveProxy(oldTimeUnit);
			if (timeUnit != oldTimeUnit) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							DataPackage.TIME_SERIES__TIME_UNIT, oldTimeUnit,
							timeUnit));
			}
		}
		return timeUnit;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Unit basicGetTimeUnit() {
		return timeUnit;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setTimeUnit(Unit newTimeUnit) {
		Unit oldTimeUnit = timeUnit;
		timeUnit = newTimeUnit;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					DataPackage.TIME_SERIES__TIME_UNIT, oldTimeUnit, timeUnit));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Unit getConcentrationUnit() {
		if (concentrationUnit != null && concentrationUnit.eIsProxy()) {
			InternalEObject oldConcentrationUnit = (InternalEObject) concentrationUnit;
			concentrationUnit = (Unit) eResolveProxy(oldConcentrationUnit);
			if (concentrationUnit != oldConcentrationUnit) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							DataPackage.TIME_SERIES__CONCENTRATION_UNIT,
							oldConcentrationUnit, concentrationUnit));
			}
		}
		return concentrationUnit;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Unit basicGetConcentrationUnit() {
		return concentrationUnit;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setConcentrationUnit(Unit newConcentrationUnit) {
		Unit oldConcentrationUnit = concentrationUnit;
		concentrationUnit = newConcentrationUnit;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					DataPackage.TIME_SERIES__CONCENTRATION_UNIT,
					oldConcentrationUnit, concentrationUnit));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EList<Condition> getConditions() {
		if (conditions == null) {
			conditions = new EObjectContainmentEList<Condition>(
					Condition.class, this, DataPackage.TIME_SERIES__CONDITIONS);
		}
		return conditions;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Organism getOrganism() {
		if (organism != null && organism.eIsProxy()) {
			InternalEObject oldOrganism = (InternalEObject) organism;
			organism = (Organism) eResolveProxy(oldOrganism);
			if (organism != oldOrganism) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							DataPackage.TIME_SERIES__ORGANISM, oldOrganism,
							organism));
			}
		}
		return organism;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Organism basicGetOrganism() {
		return organism;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setOrganism(Organism newOrganism) {
		Organism oldOrganism = organism;
		organism = newOrganism;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					DataPackage.TIME_SERIES__ORGANISM, oldOrganism, organism));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Matrix getMatrix() {
		if (matrix != null && matrix.eIsProxy()) {
			InternalEObject oldMatrix = (InternalEObject) matrix;
			matrix = (Matrix) eResolveProxy(oldMatrix);
			if (matrix != oldMatrix) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							DataPackage.TIME_SERIES__MATRIX, oldMatrix, matrix));
			}
		}
		return matrix;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Matrix basicGetMatrix() {
		return matrix;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setMatrix(Matrix newMatrix) {
		Matrix oldMatrix = matrix;
		matrix = newMatrix;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					DataPackage.TIME_SERIES__MATRIX, oldMatrix, matrix));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd,
			int featureID, NotificationChain msgs) {
		switch (featureID) {
		case DataPackage.TIME_SERIES__POINTS:
			return ((InternalEList<?>) getPoints()).basicRemove(otherEnd, msgs);
		case DataPackage.TIME_SERIES__CONDITIONS:
			return ((InternalEList<?>) getConditions()).basicRemove(otherEnd,
					msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case DataPackage.TIME_SERIES__ID:
			return getId();
		case DataPackage.TIME_SERIES__NAME:
			return getName();
		case DataPackage.TIME_SERIES__POINTS:
			return getPoints();
		case DataPackage.TIME_SERIES__TIME_UNIT:
			if (resolve)
				return getTimeUnit();
			return basicGetTimeUnit();
		case DataPackage.TIME_SERIES__CONCENTRATION_UNIT:
			if (resolve)
				return getConcentrationUnit();
			return basicGetConcentrationUnit();
		case DataPackage.TIME_SERIES__CONDITIONS:
			return getConditions();
		case DataPackage.TIME_SERIES__ORGANISM:
			if (resolve)
				return getOrganism();
			return basicGetOrganism();
		case DataPackage.TIME_SERIES__MATRIX:
			if (resolve)
				return getMatrix();
			return basicGetMatrix();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case DataPackage.TIME_SERIES__ID:
			setId((String) newValue);
			return;
		case DataPackage.TIME_SERIES__NAME:
			setName((String) newValue);
			return;
		case DataPackage.TIME_SERIES__POINTS:
			getPoints().clear();
			getPoints()
					.addAll((Collection<? extends TimeSeriesPoint>) newValue);
			return;
		case DataPackage.TIME_SERIES__TIME_UNIT:
			setTimeUnit((Unit) newValue);
			return;
		case DataPackage.TIME_SERIES__CONCENTRATION_UNIT:
			setConcentrationUnit((Unit) newValue);
			return;
		case DataPackage.TIME_SERIES__CONDITIONS:
			getConditions().clear();
			getConditions().addAll((Collection<? extends Condition>) newValue);
			return;
		case DataPackage.TIME_SERIES__ORGANISM:
			setOrganism((Organism) newValue);
			return;
		case DataPackage.TIME_SERIES__MATRIX:
			setMatrix((Matrix) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case DataPackage.TIME_SERIES__ID:
			setId(ID_EDEFAULT);
			return;
		case DataPackage.TIME_SERIES__NAME:
			setName(NAME_EDEFAULT);
			return;
		case DataPackage.TIME_SERIES__POINTS:
			getPoints().clear();
			return;
		case DataPackage.TIME_SERIES__TIME_UNIT:
			setTimeUnit((Unit) null);
			return;
		case DataPackage.TIME_SERIES__CONCENTRATION_UNIT:
			setConcentrationUnit((Unit) null);
			return;
		case DataPackage.TIME_SERIES__CONDITIONS:
			getConditions().clear();
			return;
		case DataPackage.TIME_SERIES__ORGANISM:
			setOrganism((Organism) null);
			return;
		case DataPackage.TIME_SERIES__MATRIX:
			setMatrix((Matrix) null);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case DataPackage.TIME_SERIES__ID:
			return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
		case DataPackage.TIME_SERIES__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT
					.equals(name);
		case DataPackage.TIME_SERIES__POINTS:
			return points != null && !points.isEmpty();
		case DataPackage.TIME_SERIES__TIME_UNIT:
			return timeUnit != null;
		case DataPackage.TIME_SERIES__CONCENTRATION_UNIT:
			return concentrationUnit != null;
		case DataPackage.TIME_SERIES__CONDITIONS:
			return conditions != null && !conditions.isEmpty();
		case DataPackage.TIME_SERIES__ORGANISM:
			return organism != null;
		case DataPackage.TIME_SERIES__MATRIX:
			return matrix != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (id: ");
		result.append(id);
		result.append(", name: ");
		result.append(name);
		result.append(')');
		return result.toString();
	}

} // TimeSeriesImpl
