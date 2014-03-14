/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.bund.bfr.knime.pmm.core.common.impl;

import de.bund.bfr.knime.pmm.core.common.CommonPackage;
import de.bund.bfr.knime.pmm.core.common.QuantityType;
import de.bund.bfr.knime.pmm.core.common.Unit;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Quantity Type</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link de.bund.bfr.knime.pmm.core.common.impl.QuantityTypeImpl#getId <em>
 * Id</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.common.impl.QuantityTypeImpl#getName
 * <em>Name</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.common.impl.QuantityTypeImpl#getUnits
 * <em>Units</em>}</li>
 * <li>
 * {@link de.bund.bfr.knime.pmm.core.common.impl.QuantityTypeImpl#getDefaultUnit
 * <em>Default Unit</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class QuantityTypeImpl extends EObjectImpl implements QuantityType {
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
	 * The cached value of the '{@link #getUnits() <em>Units</em>}' reference
	 * list. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getUnits()
	 * @generated
	 * @ordered
	 */
	protected EList<Unit> units;

	/**
	 * The cached value of the '{@link #getDefaultUnit() <em>Default Unit</em>}'
	 * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getDefaultUnit()
	 * @generated
	 * @ordered
	 */
	protected Unit defaultUnit;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected QuantityTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CommonPackage.Literals.QUANTITY_TYPE;
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
					CommonPackage.QUANTITY_TYPE__ID, oldId, id));
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
					CommonPackage.QUANTITY_TYPE__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EList<Unit> getUnits() {
		if (units == null) {
			units = new EObjectResolvingEList<Unit>(Unit.class, this,
					CommonPackage.QUANTITY_TYPE__UNITS);
		}
		return units;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Unit getDefaultUnit() {
		if (defaultUnit != null && defaultUnit.eIsProxy()) {
			InternalEObject oldDefaultUnit = (InternalEObject) defaultUnit;
			defaultUnit = (Unit) eResolveProxy(oldDefaultUnit);
			if (defaultUnit != oldDefaultUnit) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							CommonPackage.QUANTITY_TYPE__DEFAULT_UNIT,
							oldDefaultUnit, defaultUnit));
			}
		}
		return defaultUnit;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Unit basicGetDefaultUnit() {
		return defaultUnit;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setDefaultUnit(Unit newDefaultUnit) {
		Unit oldDefaultUnit = defaultUnit;
		defaultUnit = newDefaultUnit;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					CommonPackage.QUANTITY_TYPE__DEFAULT_UNIT, oldDefaultUnit,
					defaultUnit));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case CommonPackage.QUANTITY_TYPE__ID:
			return getId();
		case CommonPackage.QUANTITY_TYPE__NAME:
			return getName();
		case CommonPackage.QUANTITY_TYPE__UNITS:
			return getUnits();
		case CommonPackage.QUANTITY_TYPE__DEFAULT_UNIT:
			if (resolve)
				return getDefaultUnit();
			return basicGetDefaultUnit();
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
		case CommonPackage.QUANTITY_TYPE__ID:
			setId((String) newValue);
			return;
		case CommonPackage.QUANTITY_TYPE__NAME:
			setName((String) newValue);
			return;
		case CommonPackage.QUANTITY_TYPE__UNITS:
			getUnits().clear();
			getUnits().addAll((Collection<? extends Unit>) newValue);
			return;
		case CommonPackage.QUANTITY_TYPE__DEFAULT_UNIT:
			setDefaultUnit((Unit) newValue);
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
		case CommonPackage.QUANTITY_TYPE__ID:
			setId(ID_EDEFAULT);
			return;
		case CommonPackage.QUANTITY_TYPE__NAME:
			setName(NAME_EDEFAULT);
			return;
		case CommonPackage.QUANTITY_TYPE__UNITS:
			getUnits().clear();
			return;
		case CommonPackage.QUANTITY_TYPE__DEFAULT_UNIT:
			setDefaultUnit((Unit) null);
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
		case CommonPackage.QUANTITY_TYPE__ID:
			return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
		case CommonPackage.QUANTITY_TYPE__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT
					.equals(name);
		case CommonPackage.QUANTITY_TYPE__UNITS:
			return units != null && !units.isEmpty();
		case CommonPackage.QUANTITY_TYPE__DEFAULT_UNIT:
			return defaultUnit != null;
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

} // QuantityTypeImpl
