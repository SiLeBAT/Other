/**
 */
package de.bund.bfr.knime.pmm.core.datatypes;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a
 * create method for each non-abstract class of the model. <!-- end-user-doc -->
 * 
 * @see de.bund.bfr.knime.pmm.core.datatypes.DatatypesPackage
 * @generated
 */
public interface DatatypesFactory extends EFactory {
	/**
	 * The singleton instance of the factory. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	DatatypesFactory eINSTANCE = de.bund.bfr.knime.pmm.core.datatypes.impl.DatatypesFactoryImpl
			.init();

	/**
	 * Returns a new object of class '<em>EPair</em>'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>EPair</em>'.
	 * @generated
	 */
	EPair createEPair();

	/**
	 * Returns a new object of class '<em>EString</em>'. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return a new object of class '<em>EString</em>'.
	 * @generated
	 */
	EString createEString();

	/**
	 * Returns the package supported by this factory. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @return the package supported by this factory.
	 * @generated
	 */
	DatatypesPackage getDatatypesPackage();

} // DatatypesFactory
