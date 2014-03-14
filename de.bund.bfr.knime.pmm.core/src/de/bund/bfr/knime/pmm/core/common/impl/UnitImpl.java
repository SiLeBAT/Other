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

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Unit</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link de.bund.bfr.knime.pmm.core.common.impl.UnitImpl#getId <em>Id</em>}
 * </li>
 * <li>{@link de.bund.bfr.knime.pmm.core.common.impl.UnitImpl#getName <em>Name
 * </em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.common.impl.UnitImpl#getConvertFrom
 * <em>Convert From</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.common.impl.UnitImpl#getConvertTo <em>
 * Convert To</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.common.impl.UnitImpl#getQuantityType
 * <em>Quantity Type</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class UnitImpl extends EObjectImpl implements Unit {
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
	 * The default value of the '{@link #getConvertFrom() <em>Convert From</em>}
	 * ' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getConvertFrom()
	 * @generated
	 * @ordered
	 */
	protected static final String CONVERT_FROM_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getConvertFrom() <em>Convert From</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getConvertFrom()
	 * @generated
	 * @ordered
	 */
	protected String convertFrom = CONVERT_FROM_EDEFAULT;

	/**
	 * The default value of the '{@link #getConvertTo() <em>Convert To</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getConvertTo()
	 * @generated
	 * @ordered
	 */
	protected static final String CONVERT_TO_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getConvertTo() <em>Convert To</em>}'
	 * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getConvertTo()
	 * @generated
	 * @ordered
	 */
	protected String convertTo = CONVERT_TO_EDEFAULT;

	/**
	 * The cached value of the '{@link #getQuantityType()
	 * <em>Quantity Type</em>}' reference. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @see #getQuantityType()
	 * @generated
	 * @ordered
	 */
	protected QuantityType quantityType;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected UnitImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CommonPackage.Literals.UNIT;
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
					CommonPackage.UNIT__ID, oldId, id));
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
					CommonPackage.UNIT__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getConvertFrom() {
		return convertFrom;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setConvertFrom(String newConvertFrom) {
		String oldConvertFrom = convertFrom;
		convertFrom = newConvertFrom;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					CommonPackage.UNIT__CONVERT_FROM, oldConvertFrom,
					convertFrom));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public String getConvertTo() {
		return convertTo;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setConvertTo(String newConvertTo) {
		String oldConvertTo = convertTo;
		convertTo = newConvertTo;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					CommonPackage.UNIT__CONVERT_TO, oldConvertTo, convertTo));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public QuantityType getQuantityType() {
		if (quantityType != null && quantityType.eIsProxy()) {
			InternalEObject oldQuantityType = (InternalEObject) quantityType;
			quantityType = (QuantityType) eResolveProxy(oldQuantityType);
			if (quantityType != oldQuantityType) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE,
							CommonPackage.UNIT__QUANTITY_TYPE, oldQuantityType,
							quantityType));
			}
		}
		return quantityType;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public QuantityType basicGetQuantityType() {
		return quantityType;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setQuantityType(QuantityType newQuantityType) {
		QuantityType oldQuantityType = quantityType;
		quantityType = newQuantityType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					CommonPackage.UNIT__QUANTITY_TYPE, oldQuantityType,
					quantityType));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case CommonPackage.UNIT__ID:
			return getId();
		case CommonPackage.UNIT__NAME:
			return getName();
		case CommonPackage.UNIT__CONVERT_FROM:
			return getConvertFrom();
		case CommonPackage.UNIT__CONVERT_TO:
			return getConvertTo();
		case CommonPackage.UNIT__QUANTITY_TYPE:
			if (resolve)
				return getQuantityType();
			return basicGetQuantityType();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case CommonPackage.UNIT__ID:
			setId((String) newValue);
			return;
		case CommonPackage.UNIT__NAME:
			setName((String) newValue);
			return;
		case CommonPackage.UNIT__CONVERT_FROM:
			setConvertFrom((String) newValue);
			return;
		case CommonPackage.UNIT__CONVERT_TO:
			setConvertTo((String) newValue);
			return;
		case CommonPackage.UNIT__QUANTITY_TYPE:
			setQuantityType((QuantityType) newValue);
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
		case CommonPackage.UNIT__ID:
			setId(ID_EDEFAULT);
			return;
		case CommonPackage.UNIT__NAME:
			setName(NAME_EDEFAULT);
			return;
		case CommonPackage.UNIT__CONVERT_FROM:
			setConvertFrom(CONVERT_FROM_EDEFAULT);
			return;
		case CommonPackage.UNIT__CONVERT_TO:
			setConvertTo(CONVERT_TO_EDEFAULT);
			return;
		case CommonPackage.UNIT__QUANTITY_TYPE:
			setQuantityType((QuantityType) null);
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
		case CommonPackage.UNIT__ID:
			return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
		case CommonPackage.UNIT__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT
					.equals(name);
		case CommonPackage.UNIT__CONVERT_FROM:
			return CONVERT_FROM_EDEFAULT == null ? convertFrom != null
					: !CONVERT_FROM_EDEFAULT.equals(convertFrom);
		case CommonPackage.UNIT__CONVERT_TO:
			return CONVERT_TO_EDEFAULT == null ? convertTo != null
					: !CONVERT_TO_EDEFAULT.equals(convertTo);
		case CommonPackage.UNIT__QUANTITY_TYPE:
			return quantityType != null;
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
		result.append(", convertFrom: ");
		result.append(convertFrom);
		result.append(", convertTo: ");
		result.append(convertTo);
		result.append(')');
		return result.toString();
	}

} // UnitImpl
