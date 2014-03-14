/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.bund.bfr.knime.pmm.core.models.impl;

import de.bund.bfr.knime.pmm.core.common.CommonPackage;

import de.bund.bfr.knime.pmm.core.common.impl.CommonPackageImpl;

import de.bund.bfr.knime.pmm.core.data.DataPackage;

import de.bund.bfr.knime.pmm.core.data.impl.DataPackageImpl;

import de.bund.bfr.knime.pmm.core.datatypes.DatatypesPackage;

import de.bund.bfr.knime.pmm.core.datatypes.impl.DatatypesPackageImpl;

import de.bund.bfr.knime.pmm.core.models.FormulaElement;
import de.bund.bfr.knime.pmm.core.models.Model;
import de.bund.bfr.knime.pmm.core.models.ModelFormula;
import de.bund.bfr.knime.pmm.core.models.ModelsFactory;
import de.bund.bfr.knime.pmm.core.models.ModelsPackage;
import de.bund.bfr.knime.pmm.core.models.Parameter;
import de.bund.bfr.knime.pmm.core.models.ParameterValue;
import de.bund.bfr.knime.pmm.core.models.PrimaryModel;
import de.bund.bfr.knime.pmm.core.models.PrimaryModelFormula;
import de.bund.bfr.knime.pmm.core.models.SecondaryModel;
import de.bund.bfr.knime.pmm.core.models.SecondaryModelFormula;
import de.bund.bfr.knime.pmm.core.models.TertiaryModel;
import de.bund.bfr.knime.pmm.core.models.TertiaryModelFormula;
import de.bund.bfr.knime.pmm.core.models.Variable;
import de.bund.bfr.knime.pmm.core.models.VariableRange;

import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!--
 * end-user-doc -->
 * 
 * @generated
 */
public class ModelsPackageImpl extends EPackageImpl implements ModelsPackage {
	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass modelEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass modelFormulaEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass formulaElementEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass variableEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass variableRangeEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass parameterEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass parameterValueEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass primaryModelEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass secondaryModelEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass tertiaryModelEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass primaryModelFormulaEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass secondaryModelFormulaEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass tertiaryModelFormulaEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass stringToStringMapEntryEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass stringToDoubleMapEntryEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass stringToVariableRangeMapEntryEClass = null;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private EClass stringToParameterValueMapEntryEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the
	 * package package URI value.
	 * <p>
	 * Note: the correct way to create the package is via the static factory
	 * method {@link #init init()}, which also performs initialization of the
	 * package, or returns the registered package, if one already exists. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see de.bund.bfr.knime.pmm.core.models.ModelsPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private ModelsPackageImpl() {
		super(eNS_URI, ModelsFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model,
	 * and for any others upon which it depends.
	 * 
	 * <p>
	 * This method is used to initialize {@link ModelsPackage#eINSTANCE} when
	 * that field is accessed. Clients should not invoke it directly. Instead,
	 * they should simply access that field to obtain the package. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static ModelsPackage init() {
		if (isInited)
			return (ModelsPackage) EPackage.Registry.INSTANCE
					.getEPackage(ModelsPackage.eNS_URI);

		// Obtain or create and register package
		ModelsPackageImpl theModelsPackage = (ModelsPackageImpl) (EPackage.Registry.INSTANCE
				.get(eNS_URI) instanceof ModelsPackageImpl ? EPackage.Registry.INSTANCE
				.get(eNS_URI) : new ModelsPackageImpl());

		isInited = true;

		// Obtain or create and register interdependencies
		CommonPackageImpl theCommonPackage = (CommonPackageImpl) (EPackage.Registry.INSTANCE
				.getEPackage(CommonPackage.eNS_URI) instanceof CommonPackageImpl ? EPackage.Registry.INSTANCE
				.getEPackage(CommonPackage.eNS_URI) : CommonPackage.eINSTANCE);
		DataPackageImpl theDataPackage = (DataPackageImpl) (EPackage.Registry.INSTANCE
				.getEPackage(DataPackage.eNS_URI) instanceof DataPackageImpl ? EPackage.Registry.INSTANCE
				.getEPackage(DataPackage.eNS_URI) : DataPackage.eINSTANCE);
		DatatypesPackageImpl theDatatypesPackage = (DatatypesPackageImpl) (EPackage.Registry.INSTANCE
				.getEPackage(DatatypesPackage.eNS_URI) instanceof DatatypesPackageImpl ? EPackage.Registry.INSTANCE
				.getEPackage(DatatypesPackage.eNS_URI)
				: DatatypesPackage.eINSTANCE);

		// Create package meta-data objects
		theModelsPackage.createPackageContents();
		theCommonPackage.createPackageContents();
		theDataPackage.createPackageContents();
		theDatatypesPackage.createPackageContents();

		// Initialize created meta-data
		theModelsPackage.initializePackageContents();
		theCommonPackage.initializePackageContents();
		theDataPackage.initializePackageContents();
		theDatatypesPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theModelsPackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(ModelsPackage.eNS_URI, theModelsPackage);
		return theModelsPackage;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EClass getModel() {
		return modelEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getModel_Id() {
		return (EAttribute) modelEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getModel_Sse() {
		return (EAttribute) modelEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getModel_Mse() {
		return (EAttribute) modelEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getModel_Rmse() {
		return (EAttribute) modelEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getModel_R2() {
		return (EAttribute) modelEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getModel_Aic() {
		return (EAttribute) modelEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getModel_DegreesOfFreedom() {
		return (EAttribute) modelEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EReference getModel_VariableRanges() {
		return (EReference) modelEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EReference getModel_ParamValues() {
		return (EReference) modelEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EClass getModelFormula() {
		return modelFormulaEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getModelFormula_Id() {
		return (EAttribute) modelFormulaEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getModelFormula_Name() {
		return (EAttribute) modelFormulaEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getModelFormula_Formula() {
		return (EAttribute) modelFormulaEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EReference getModelFormula_DepVar() {
		return (EReference) modelFormulaEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EReference getModelFormula_Params() {
		return (EReference) modelFormulaEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EClass getFormulaElement() {
		return formulaElementEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getFormulaElement_Name() {
		return (EAttribute) formulaElementEClass.getEStructuralFeatures()
				.get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EClass getVariable() {
		return variableEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EReference getVariable_Unit() {
		return (EReference) variableEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EClass getVariableRange() {
		return variableRangeEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getVariableRange_Min() {
		return (EAttribute) variableRangeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getVariableRange_Max() {
		return (EAttribute) variableRangeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EClass getParameter() {
		return parameterEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getParameter_Min() {
		return (EAttribute) parameterEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getParameter_Max() {
		return (EAttribute) parameterEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EClass getParameterValue() {
		return parameterValueEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getParameterValue_Value() {
		return (EAttribute) parameterValueEClass.getEStructuralFeatures()
				.get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getParameterValue_Error() {
		return (EAttribute) parameterValueEClass.getEStructuralFeatures()
				.get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getParameterValue_T() {
		return (EAttribute) parameterValueEClass.getEStructuralFeatures()
				.get(2);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getParameterValue_P() {
		return (EAttribute) parameterValueEClass.getEStructuralFeatures()
				.get(3);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EReference getParameterValue_Correlations() {
		return (EReference) parameterValueEClass.getEStructuralFeatures()
				.get(4);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EClass getPrimaryModel() {
		return primaryModelEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EReference getPrimaryModel_ModelFormula() {
		return (EReference) primaryModelEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EReference getPrimaryModel_Data() {
		return (EReference) primaryModelEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EReference getPrimaryModel_Assignments() {
		return (EReference) primaryModelEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EClass getSecondaryModel() {
		return secondaryModelEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EReference getSecondaryModel_ModelFormula() {
		return (EReference) secondaryModelEClass.getEStructuralFeatures()
				.get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EReference getSecondaryModel_Data() {
		return (EReference) secondaryModelEClass.getEStructuralFeatures()
				.get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EReference getSecondaryModel_Assignments() {
		return (EReference) secondaryModelEClass.getEStructuralFeatures()
				.get(2);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EClass getTertiaryModel() {
		return tertiaryModelEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EReference getTertiaryModel_ModelFormula() {
		return (EReference) tertiaryModelEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EReference getTertiaryModel_Data() {
		return (EReference) tertiaryModelEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EReference getTertiaryModel_Assignments() {
		return (EReference) tertiaryModelEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EClass getPrimaryModelFormula() {
		return primaryModelFormulaEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EReference getPrimaryModelFormula_IndepVar() {
		return (EReference) primaryModelFormulaEClass.getEStructuralFeatures()
				.get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EClass getSecondaryModelFormula() {
		return secondaryModelFormulaEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EReference getSecondaryModelFormula_IndepVars() {
		return (EReference) secondaryModelFormulaEClass
				.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EClass getTertiaryModelFormula() {
		return tertiaryModelFormulaEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EReference getTertiaryModelFormula_IndepVars() {
		return (EReference) tertiaryModelFormulaEClass.getEStructuralFeatures()
				.get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EClass getStringToStringMapEntry() {
		return stringToStringMapEntryEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getStringToStringMapEntry_Key() {
		return (EAttribute) stringToStringMapEntryEClass
				.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getStringToStringMapEntry_Value() {
		return (EAttribute) stringToStringMapEntryEClass
				.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EClass getStringToDoubleMapEntry() {
		return stringToDoubleMapEntryEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getStringToDoubleMapEntry_Key() {
		return (EAttribute) stringToDoubleMapEntryEClass
				.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getStringToDoubleMapEntry_Value() {
		return (EAttribute) stringToDoubleMapEntryEClass
				.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EClass getStringToVariableRangeMapEntry() {
		return stringToVariableRangeMapEntryEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getStringToVariableRangeMapEntry_Key() {
		return (EAttribute) stringToVariableRangeMapEntryEClass
				.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EReference getStringToVariableRangeMapEntry_Value() {
		return (EReference) stringToVariableRangeMapEntryEClass
				.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EClass getStringToParameterValueMapEntry() {
		return stringToParameterValueMapEntryEClass;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EAttribute getStringToParameterValueMapEntry_Key() {
		return (EAttribute) stringToParameterValueMapEntryEClass
				.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public EReference getStringToParameterValueMapEntry_Value() {
		return (EReference) stringToParameterValueMapEntryEClass
				.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public ModelsFactory getModelsFactory() {
		return (ModelsFactory) getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package. This method is guarded to
	 * have no affect on any invocation but its first. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated)
			return;
		isCreated = true;

		// Create classes and their features
		modelEClass = createEClass(MODEL);
		createEAttribute(modelEClass, MODEL__ID);
		createEAttribute(modelEClass, MODEL__SSE);
		createEAttribute(modelEClass, MODEL__MSE);
		createEAttribute(modelEClass, MODEL__RMSE);
		createEAttribute(modelEClass, MODEL__R2);
		createEAttribute(modelEClass, MODEL__AIC);
		createEAttribute(modelEClass, MODEL__DEGREES_OF_FREEDOM);
		createEReference(modelEClass, MODEL__VARIABLE_RANGES);
		createEReference(modelEClass, MODEL__PARAM_VALUES);

		modelFormulaEClass = createEClass(MODEL_FORMULA);
		createEAttribute(modelFormulaEClass, MODEL_FORMULA__ID);
		createEAttribute(modelFormulaEClass, MODEL_FORMULA__NAME);
		createEAttribute(modelFormulaEClass, MODEL_FORMULA__FORMULA);
		createEReference(modelFormulaEClass, MODEL_FORMULA__DEP_VAR);
		createEReference(modelFormulaEClass, MODEL_FORMULA__PARAMS);

		formulaElementEClass = createEClass(FORMULA_ELEMENT);
		createEAttribute(formulaElementEClass, FORMULA_ELEMENT__NAME);

		variableEClass = createEClass(VARIABLE);
		createEReference(variableEClass, VARIABLE__UNIT);

		variableRangeEClass = createEClass(VARIABLE_RANGE);
		createEAttribute(variableRangeEClass, VARIABLE_RANGE__MIN);
		createEAttribute(variableRangeEClass, VARIABLE_RANGE__MAX);

		parameterEClass = createEClass(PARAMETER);
		createEAttribute(parameterEClass, PARAMETER__MIN);
		createEAttribute(parameterEClass, PARAMETER__MAX);

		parameterValueEClass = createEClass(PARAMETER_VALUE);
		createEAttribute(parameterValueEClass, PARAMETER_VALUE__VALUE);
		createEAttribute(parameterValueEClass, PARAMETER_VALUE__ERROR);
		createEAttribute(parameterValueEClass, PARAMETER_VALUE__T);
		createEAttribute(parameterValueEClass, PARAMETER_VALUE__P);
		createEReference(parameterValueEClass, PARAMETER_VALUE__CORRELATIONS);

		primaryModelEClass = createEClass(PRIMARY_MODEL);
		createEReference(primaryModelEClass, PRIMARY_MODEL__MODEL_FORMULA);
		createEReference(primaryModelEClass, PRIMARY_MODEL__DATA);
		createEReference(primaryModelEClass, PRIMARY_MODEL__ASSIGNMENTS);

		secondaryModelEClass = createEClass(SECONDARY_MODEL);
		createEReference(secondaryModelEClass, SECONDARY_MODEL__MODEL_FORMULA);
		createEReference(secondaryModelEClass, SECONDARY_MODEL__DATA);
		createEReference(secondaryModelEClass, SECONDARY_MODEL__ASSIGNMENTS);

		tertiaryModelEClass = createEClass(TERTIARY_MODEL);
		createEReference(tertiaryModelEClass, TERTIARY_MODEL__MODEL_FORMULA);
		createEReference(tertiaryModelEClass, TERTIARY_MODEL__DATA);
		createEReference(tertiaryModelEClass, TERTIARY_MODEL__ASSIGNMENTS);

		primaryModelFormulaEClass = createEClass(PRIMARY_MODEL_FORMULA);
		createEReference(primaryModelFormulaEClass,
				PRIMARY_MODEL_FORMULA__INDEP_VAR);

		secondaryModelFormulaEClass = createEClass(SECONDARY_MODEL_FORMULA);
		createEReference(secondaryModelFormulaEClass,
				SECONDARY_MODEL_FORMULA__INDEP_VARS);

		tertiaryModelFormulaEClass = createEClass(TERTIARY_MODEL_FORMULA);
		createEReference(tertiaryModelFormulaEClass,
				TERTIARY_MODEL_FORMULA__INDEP_VARS);

		stringToStringMapEntryEClass = createEClass(STRING_TO_STRING_MAP_ENTRY);
		createEAttribute(stringToStringMapEntryEClass,
				STRING_TO_STRING_MAP_ENTRY__KEY);
		createEAttribute(stringToStringMapEntryEClass,
				STRING_TO_STRING_MAP_ENTRY__VALUE);

		stringToDoubleMapEntryEClass = createEClass(STRING_TO_DOUBLE_MAP_ENTRY);
		createEAttribute(stringToDoubleMapEntryEClass,
				STRING_TO_DOUBLE_MAP_ENTRY__KEY);
		createEAttribute(stringToDoubleMapEntryEClass,
				STRING_TO_DOUBLE_MAP_ENTRY__VALUE);

		stringToVariableRangeMapEntryEClass = createEClass(STRING_TO_VARIABLE_RANGE_MAP_ENTRY);
		createEAttribute(stringToVariableRangeMapEntryEClass,
				STRING_TO_VARIABLE_RANGE_MAP_ENTRY__KEY);
		createEReference(stringToVariableRangeMapEntryEClass,
				STRING_TO_VARIABLE_RANGE_MAP_ENTRY__VALUE);

		stringToParameterValueMapEntryEClass = createEClass(STRING_TO_PARAMETER_VALUE_MAP_ENTRY);
		createEAttribute(stringToParameterValueMapEntryEClass,
				STRING_TO_PARAMETER_VALUE_MAP_ENTRY__KEY);
		createEReference(stringToParameterValueMapEntryEClass,
				STRING_TO_PARAMETER_VALUE_MAP_ENTRY__VALUE);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model. This
	 * method is guarded to have no affect on any invocation but its first. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized)
			return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		CommonPackage theCommonPackage = (CommonPackage) EPackage.Registry.INSTANCE
				.getEPackage(CommonPackage.eNS_URI);
		DataPackage theDataPackage = (DataPackage) EPackage.Registry.INSTANCE
				.getEPackage(DataPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		variableEClass.getESuperTypes().add(this.getFormulaElement());
		parameterEClass.getESuperTypes().add(this.getFormulaElement());
		primaryModelEClass.getESuperTypes().add(this.getModel());
		secondaryModelEClass.getESuperTypes().add(this.getModel());
		tertiaryModelEClass.getESuperTypes().add(this.getModel());
		primaryModelFormulaEClass.getESuperTypes().add(this.getModelFormula());
		secondaryModelFormulaEClass.getESuperTypes()
				.add(this.getModelFormula());
		tertiaryModelFormulaEClass.getESuperTypes().add(this.getModelFormula());

		// Initialize classes and features; add operations and parameters
		initEClass(modelEClass, Model.class, "Model", IS_ABSTRACT,
				IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getModel_Id(), ecorePackage.getEString(), "id", null, 0,
				1, Model.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getModel_Sse(), ecorePackage.getEDoubleObject(), "sse",
				null, 0, 1, Model.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				IS_ORDERED);
		initEAttribute(getModel_Mse(), ecorePackage.getEDoubleObject(), "mse",
				null, 0, 1, Model.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				IS_ORDERED);
		initEAttribute(getModel_Rmse(), ecorePackage.getEDoubleObject(),
				"rmse", null, 0, 1, Model.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				IS_ORDERED);
		initEAttribute(getModel_R2(), ecorePackage.getEDoubleObject(), "r2",
				null, 0, 1, Model.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				IS_ORDERED);
		initEAttribute(getModel_Aic(), ecorePackage.getEDoubleObject(), "aic",
				null, 0, 1, Model.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				IS_ORDERED);
		initEAttribute(getModel_DegreesOfFreedom(),
				ecorePackage.getEIntegerObject(), "degreesOfFreedom", null, 0,
				1, Model.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getModel_VariableRanges(),
				this.getStringToVariableRangeMapEntry(), null,
				"variableRanges", null, 0, -1, Model.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getModel_ParamValues(),
				this.getStringToParameterValueMapEntry(), null, "paramValues",
				null, 0, -1, Model.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		addEOperation(modelEClass, this.getModelFormula(), "getModelFormula",
				0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(modelFormulaEClass, ModelFormula.class, "ModelFormula",
				IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getModelFormula_Id(), ecorePackage.getEString(), "id",
				null, 0, 1, ModelFormula.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_UNSETTABLE, IS_ID, IS_UNIQUE, !IS_DERIVED,
				IS_ORDERED);
		initEAttribute(getModelFormula_Name(), ecorePackage.getEString(),
				"name", null, 0, 1, ModelFormula.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEAttribute(getModelFormula_Formula(), ecorePackage.getEString(),
				"formula", null, 0, 1, ModelFormula.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEReference(getModelFormula_DepVar(), this.getVariable(), null,
				"depVar", null, 0, 1, ModelFormula.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getModelFormula_Params(), this.getParameter(), null,
				"params", null, 0, -1, ModelFormula.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(formulaElementEClass, FormulaElement.class,
				"FormulaElement", IS_ABSTRACT, IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getFormulaElement_Name(), ecorePackage.getEString(),
				"name", null, 0, 1, FormulaElement.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);

		initEClass(variableEClass, Variable.class, "Variable", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getVariable_Unit(), theCommonPackage.getUnit(), null,
				"unit", null, 0, 1, Variable.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(variableRangeEClass, VariableRange.class, "VariableRange",
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getVariableRange_Min(), ecorePackage.getEDoubleObject(),
				"min", null, 0, 1, VariableRange.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEAttribute(getVariableRange_Max(), ecorePackage.getEDoubleObject(),
				"max", null, 0, 1, VariableRange.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);

		initEClass(parameterEClass, Parameter.class, "Parameter", !IS_ABSTRACT,
				!IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getParameter_Min(), ecorePackage.getEDoubleObject(),
				"min", null, 0, 1, Parameter.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameter_Max(), ecorePackage.getEDoubleObject(),
				"max", null, 0, 1, Parameter.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);

		initEClass(parameterValueEClass, ParameterValue.class,
				"ParameterValue", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getParameterValue_Value(),
				ecorePackage.getEDoubleObject(), "value", null, 0, 1,
				ParameterValue.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				IS_ORDERED);
		initEAttribute(getParameterValue_Error(),
				ecorePackage.getEDoubleObject(), "error", null, 0, 1,
				ParameterValue.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED,
				IS_ORDERED);
		initEAttribute(getParameterValue_T(), ecorePackage.getEDoubleObject(),
				"t", null, 0, 1, ParameterValue.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEAttribute(getParameterValue_P(), ecorePackage.getEDoubleObject(),
				"p", null, 0, 1, ParameterValue.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);
		initEReference(getParameterValue_Correlations(),
				this.getStringToDoubleMapEntry(), null, "correlations", null,
				0, -1, ParameterValue.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(primaryModelEClass, PrimaryModel.class, "PrimaryModel",
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getPrimaryModel_ModelFormula(),
				this.getPrimaryModelFormula(), null, "modelFormula", null, 0,
				1, PrimaryModel.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPrimaryModel_Data(), theDataPackage.getTimeSeries(),
				null, "data", null, 0, 1, PrimaryModel.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPrimaryModel_Assignments(),
				this.getStringToStringMapEntry(), null, "assignments", null, 0,
				-1, PrimaryModel.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(secondaryModelEClass, SecondaryModel.class,
				"SecondaryModel", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getSecondaryModel_ModelFormula(),
				this.getSecondaryModelFormula(), null, "modelFormula", null, 0,
				1, SecondaryModel.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSecondaryModel_Data(), this.getPrimaryModel(), null,
				"data", null, 0, -1, SecondaryModel.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSecondaryModel_Assignments(),
				this.getStringToStringMapEntry(), null, "assignments", null, 0,
				-1, SecondaryModel.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(tertiaryModelEClass, TertiaryModel.class, "TertiaryModel",
				!IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getTertiaryModel_ModelFormula(),
				this.getTertiaryModelFormula(), null, "modelFormula", null, 0,
				1, TertiaryModel.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getTertiaryModel_Data(), theDataPackage.getTimeSeries(),
				null, "data", null, 0, -1, TertiaryModel.class, !IS_TRANSIENT,
				!IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getTertiaryModel_Assignments(),
				this.getStringToStringMapEntry(), null, "assignments", null, 0,
				-1, TertiaryModel.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(primaryModelFormulaEClass, PrimaryModelFormula.class,
				"PrimaryModelFormula", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getPrimaryModelFormula_IndepVar(), this.getVariable(),
				null, "indepVar", null, 0, 1, PrimaryModelFormula.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
				IS_ORDERED);

		initEClass(secondaryModelFormulaEClass, SecondaryModelFormula.class,
				"SecondaryModelFormula", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getSecondaryModelFormula_IndepVars(),
				this.getVariable(), null, "indepVars", null, 0, -1,
				SecondaryModelFormula.class, !IS_TRANSIENT, !IS_VOLATILE,
				IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES,
				!IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(tertiaryModelFormulaEClass, TertiaryModelFormula.class,
				"TertiaryModelFormula", !IS_ABSTRACT, !IS_INTERFACE,
				IS_GENERATED_INSTANCE_CLASS);
		initEReference(getTertiaryModelFormula_IndepVars(), this.getVariable(),
				null, "indepVars", null, 0, -1, TertiaryModelFormula.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE,
				!IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED,
				IS_ORDERED);

		initEClass(stringToStringMapEntryEClass, Map.Entry.class,
				"StringToStringMapEntry", !IS_ABSTRACT, !IS_INTERFACE,
				!IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getStringToStringMapEntry_Key(),
				ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
				!IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getStringToStringMapEntry_Value(),
				ecorePackage.getEString(), "value", null, 0, 1,
				Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(stringToDoubleMapEntryEClass, Map.Entry.class,
				"StringToDoubleMapEntry", !IS_ABSTRACT, !IS_INTERFACE,
				!IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getStringToDoubleMapEntry_Key(),
				ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
				!IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getStringToDoubleMapEntry_Value(),
				ecorePackage.getEDoubleObject(), "value", null, 0, 1,
				Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				!IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(stringToVariableRangeMapEntryEClass, Map.Entry.class,
				"StringToVariableRangeMapEntry", !IS_ABSTRACT, !IS_INTERFACE,
				!IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getStringToVariableRangeMapEntry_Key(),
				ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
				!IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getStringToVariableRangeMapEntry_Value(),
				this.getVariableRange(), null, "value", null, 0, 1,
				Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);

		initEClass(stringToParameterValueMapEntryEClass, Map.Entry.class,
				"StringToParameterValueMapEntry", !IS_ABSTRACT, !IS_INTERFACE,
				!IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getStringToParameterValueMapEntry_Key(),
				ecorePackage.getEString(), "key", null, 0, 1, Map.Entry.class,
				!IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE,
				!IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getStringToParameterValueMapEntry_Value(),
				this.getParameterValue(), null, "value", null, 0, 1,
				Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE,
				IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE,
				!IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);
	}

} // ModelsPackageImpl
