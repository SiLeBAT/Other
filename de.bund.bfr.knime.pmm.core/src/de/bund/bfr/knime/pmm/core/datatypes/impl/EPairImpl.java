/**
 */
package de.bund.bfr.knime.pmm.core.datatypes.impl;

import de.bund.bfr.knime.pmm.core.datatypes.DatatypesPackage;
import de.bund.bfr.knime.pmm.core.datatypes.EPair;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>EPair</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link de.bund.bfr.knime.pmm.core.datatypes.impl.EPairImpl#getValue1 <em>
 * Value1</em>}</li>
 * <li>{@link de.bund.bfr.knime.pmm.core.datatypes.impl.EPairImpl#getValue2 <em>
 * Value2</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class EPairImpl extends EObjectImpl implements EPair {
	/**
	 * The cached value of the '{@link #getValue1() <em>Value1</em>}'
	 * containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getValue1()
	 * @generated
	 * @ordered
	 */
	protected EObject value1;

	/**
	 * The cached value of the '{@link #getValue2() <em>Value2</em>}'
	 * containment reference. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #getValue2()
	 * @generated
	 * @ordered
	 */
	protected EObject value2;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected EPairImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return DatatypesPackage.Literals.EPAIR;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EObject getValue1() {
		return value1;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetValue1(EObject newValue1,
			NotificationChain msgs) {
		EObject oldValue1 = value1;
		value1 = newValue1;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this,
					Notification.SET, DatatypesPackage.EPAIR__VALUE1,
					oldValue1, newValue1);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setValue1(EObject newValue1) {
		if (newValue1 != value1) {
			NotificationChain msgs = null;
			if (value1 != null)
				msgs = ((InternalEObject) value1)
						.eInverseRemove(this, EOPPOSITE_FEATURE_BASE
								- DatatypesPackage.EPAIR__VALUE1, null, msgs);
			if (newValue1 != null)
				msgs = ((InternalEObject) newValue1)
						.eInverseAdd(this, EOPPOSITE_FEATURE_BASE
								- DatatypesPackage.EPAIR__VALUE1, null, msgs);
			msgs = basicSetValue1(newValue1, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					DatatypesPackage.EPAIR__VALUE1, newValue1, newValue1));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EObject getValue2() {
		return value2;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public NotificationChain basicSetValue2(EObject newValue2,
			NotificationChain msgs) {
		EObject oldValue2 = value2;
		value2 = newValue2;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this,
					Notification.SET, DatatypesPackage.EPAIR__VALUE2,
					oldValue2, newValue2);
			if (msgs == null)
				msgs = notification;
			else
				msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public void setValue2(EObject newValue2) {
		if (newValue2 != value2) {
			NotificationChain msgs = null;
			if (value2 != null)
				msgs = ((InternalEObject) value2)
						.eInverseRemove(this, EOPPOSITE_FEATURE_BASE
								- DatatypesPackage.EPAIR__VALUE2, null, msgs);
			if (newValue2 != null)
				msgs = ((InternalEObject) newValue2)
						.eInverseAdd(this, EOPPOSITE_FEATURE_BASE
								- DatatypesPackage.EPAIR__VALUE2, null, msgs);
			msgs = basicSetValue2(newValue2, msgs);
			if (msgs != null)
				msgs.dispatch();
		} else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET,
					DatatypesPackage.EPAIR__VALUE2, newValue2, newValue2));
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
		case DatatypesPackage.EPAIR__VALUE1:
			return basicSetValue1(null, msgs);
		case DatatypesPackage.EPAIR__VALUE2:
			return basicSetValue2(null, msgs);
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
		case DatatypesPackage.EPAIR__VALUE1:
			return getValue1();
		case DatatypesPackage.EPAIR__VALUE2:
			return getValue2();
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
		case DatatypesPackage.EPAIR__VALUE1:
			setValue1((EObject) newValue);
			return;
		case DatatypesPackage.EPAIR__VALUE2:
			setValue2((EObject) newValue);
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
		case DatatypesPackage.EPAIR__VALUE1:
			setValue1((EObject) null);
			return;
		case DatatypesPackage.EPAIR__VALUE2:
			setValue2((EObject) null);
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
		case DatatypesPackage.EPAIR__VALUE1:
			return value1 != null;
		case DatatypesPackage.EPAIR__VALUE2:
			return value2 != null;
		}
		return super.eIsSet(featureID);
	}

} // EPairImpl
