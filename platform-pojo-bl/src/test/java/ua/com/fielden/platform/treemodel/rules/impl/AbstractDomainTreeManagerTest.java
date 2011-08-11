package ua.com.fielden.platform.treemodel.rules.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

import org.junit.Test;

import ua.com.fielden.platform.domain.tree.DomainTreeManagerAndEnhancer1;
import ua.com.fielden.platform.domain.tree.MasterEntity;
import ua.com.fielden.platform.serialisation.api.ISerialiser;
import ua.com.fielden.platform.treemodel.rules.ICalculatedProperty;
import ua.com.fielden.platform.treemodel.rules.ICalculatedProperty.CalculatedPropertyCategory;
import ua.com.fielden.platform.treemodel.rules.IDomainTreeManager.IDomainTreeManagerAndEnhancer;
import ua.com.fielden.platform.types.Money;

/**
 * A test for {@link AbstractDomainTreeManager}.
 *
 * @author TG Team
 *
 */
public class AbstractDomainTreeManagerTest extends AbstractDomainTreeTest {
    @Override
    protected IDomainTreeManagerAndEnhancer createManager(final ISerialiser serialiser, final Set<Class<?>> rootTypes) {
	final IDomainTreeManagerAndEnhancer dtm = new DomainTreeManagerAndEnhancer1(serialiser, rootTypes);
	allLevels(new IAction() {
	    public void action(final String name) {
		dtm.getSecondTick().check(MasterEntity.class, name, true);
	    }
	}, "checkedUntouchedProp");
	allLevels(new IAction() {
	    public void action(final String name) {
		dtm.getSecondTick().check(MasterEntity.class, name, true);
	    }
	}, "mutatedWithFunctionsProp");
	return dtm;
    }

    protected void checkIllegally(final String name, final String message) {
	try {
	    dtm().getFirstTick().check(MasterEntity.class, name, true);
	    fail(message);
	} catch (final IllegalArgumentException e) {
	}
    }

    protected void isCheck_equals_to_state(final String name, final String message, final boolean state) {
	assertEquals(message, state, dtm().getFirstTick().isChecked(MasterEntity.class, name));
    }

    protected void checkLegally(final String name, final String message) {
	try {
	    dtm().getFirstTick().check(MasterEntity.class, name, true);
	    dtm().getFirstTick().check(MasterEntity.class, name, false);
	} catch (final Exception e) {
	    fail(message);
	}
    }

    protected void isCheckIllegally(final String name, final String message) {
	try {
	    dtm().getFirstTick().isChecked(MasterEntity.class, name);
	    fail(message);
	} catch (final IllegalArgumentException e) {
	}
    }

    ////////////////////////////////////////////////////////////////
    ////////////////////// 1. Manage state legitimacy (CHECK) //////
    ////////////////////////////////////////////////////////////////
    @Test
    public void test_that_CHECK_state_managing_for_excluded_properties_is_not_permitted() {
	final String message = "Excluded property should cause illegal argument exception while changing its state.";
	allLevels(new IAction() {
	    public void action(final String name) {
		checkIllegally(name, message);
	    }
	}, "excludedManuallyProp");
    }

    @Test
    public void test_that_CHECK_state_managing_for_disabled_properties_is_not_permitted() { // (disabled == immutably checked or unchecked)
	final String message = "Immutably checked property (disabled) should cause illegal argument exception while changing its state.";
	allLevels(new IAction() {
	    public void action(final String name) {
		checkIllegally(name, message);
	    }
	}, "checkedManuallyProp");

	final String message1 = "Immutably unchecked property (disabled) should cause illegal argument exception while changing its state.";
	allLevels(new IAction() {
	    public void action(final String name) {
		checkIllegally(name, message1);
	    }
	}, "disabledManuallyProp");
    }

    @Test
    public void test_that_CHECK_state_managing_for_rest_properties_is_permitted() {
	final String message = "Not disabled and not excluded property should NOT cause any exception while changing its state.";
	allLevelsWithoutCollections(new IAction() {
	    public void action(final String name) {
		checkLegally(name, message);
	    }
	}, "mutablyCheckedProp");
    }

    ///////////////////////////////////////////////////////////////////////
    ////////////////////// 2. Ask state checking / legitimacy (CHECK) /////
    ///////////////////////////////////////////////////////////////////////
    @Test
    public void test_that_CHECK_state_asking_for_excluded_properties_is_not_permitted() {
	final String message = "Excluded property should cause illegal argument exception while asking its state.";
	allLevels(new IAction() {
	    public void action(final String name) {
		isCheckIllegally(name, message);
	    }
	}, "excludedManuallyProp");
    }

    @Test
    public void test_that_CHECK_state_for_disabled_properties_is_correct() { // (disabled == immutably checked or unchecked)
	final String message = "Immutably checked property (disabled) should return 'true' CHECK state.";
	allLevels(new IAction() {
	    public void action(final String name) {
		isCheck_equals_to_state(name, message, true);
	    }
	}, "checkedManuallyProp");

	final String message1 = "Immutably unchecked property (disabled) should return 'false' CHECK state.";
	allLevels(new IAction() {
	    public void action(final String name) {
		isCheck_equals_to_state(name, message1, false);
	    }
	}, "disabledManuallyProp");
    }

    @Test
    public void test_that_CHECK_state_for_untouched_properties_is_correct() { // correct state should be "unchecked"
	final String message = "Untouched property (also not muted in representation) should return 'false' CHECK state.";
	allLevels(new IAction() {
	    public void action(final String name) {
		isCheck_equals_to_state(name, message, false);
	    }
	}, "bigDecimalProp");
    }

    @Test
    public void test_that_CHECK_state_for_mutated_by_isChecked_method_properties_is_desired_and_after_manual_mutation_is_actually_mutated() {
	// checked properties, defined in isChecked() contract
	final String message = "Checked property, defined in isChecked() contract, should return 'true' CHECK state, and after manual mutation its state should be desired.";
	allLevels(new IAction() {
	    public void action(final String name) {
		isCheck_equals_to_state(name, message, true);
		dtm().getFirstTick().check(MasterEntity.class, name, false);
		isCheck_equals_to_state(name, message, false);
		dtm().getFirstTick().check(MasterEntity.class, name, true);
		isCheck_equals_to_state(name, message, true);
	    }
	}, "mutablyCheckedProp");
    }

    @Test
    public void test_that_CHECK_state_for_mutated_by_Check_method_properties_is_actually_mutated() {
	// checked properties, mutated_by_Check_method
	final String message = "Checked property, defined in isChecked() contract, should return 'true' CHECK state, and after manual mutation its state should be desired.";
	allLevels(new IAction() {
	    public void action(final String name) {
		dtm().getFirstTick().check(MasterEntity.class, name, false);
		isCheck_equals_to_state(name, message, false);
		dtm().getFirstTick().check(MasterEntity.class, name, true);
		isCheck_equals_to_state(name, message, true);
	    }
	}, "mutablyCheckedProp");
    }

    @Test
    public void test_that_calculated_properties_work() {
	/////////////// ADDING & MANAGING ///////////////
	// enhance domain with new calculated property
	dtm().getEnhancer().addCalculatedProperty(new CalculatedProperty(MasterEntity.class, "calcProp1", CalculatedPropertyCategory.EXPRESSION, "integerProp", Integer.class, "1 * 2", "title", "desc"));
	dtm().getEnhancer().apply();

	dtm().getRepresentation().excludeImmutably(MasterEntity.class, "calcProp1");
	assertTrue("The brand new calculated property should be excluded.", dtm().getRepresentation().isExcludedImmutably(MasterEntity.class, "calcProp1"));

	// enhance domain with new calculated property
	dtm().getEnhancer().addCalculatedProperty(new CalculatedProperty(MasterEntity.class, "calcProp2", CalculatedPropertyCategory.EXPRESSION, "moneyProp", Money.class, "1 * 2.5", "title", "desc"));
	dtm().getEnhancer().apply();

	dtm().getRepresentation().getFirstTick().disableImmutably(MasterEntity.class, "calcProp2");
	assertTrue("The brand new calculated property should be excluded.", dtm().getRepresentation().isExcludedImmutably(MasterEntity.class, "calcProp1"));
	assertTrue("The brand new calculated property should be disabled.", dtm().getRepresentation().getFirstTick().isDisabledImmutably(MasterEntity.class, "calcProp2"));

	// enhance domain with new calculated property
	dtm().getEnhancer().addCalculatedProperty(new CalculatedProperty(MasterEntity.class, "calcProp3", CalculatedPropertyCategory.EXPRESSION, "moneyProp", Money.class, "1 * 2.5", "title", "desc"));
	dtm().getEnhancer().apply();

	dtm().getRepresentation().getSecondTick().checkImmutably(MasterEntity.class, "calcProp3");
	assertTrue("The brand new calculated property should be excluded.", dtm().getRepresentation().isExcludedImmutably(MasterEntity.class, "calcProp1"));
	assertTrue("The brand new calculated property should be disabled.", dtm().getRepresentation().getFirstTick().isDisabledImmutably(MasterEntity.class, "calcProp2"));
	assertTrue("The brand new calculated property should be immutable checked.", dtm().getRepresentation().getSecondTick().isCheckedImmutably(MasterEntity.class, "calcProp3"));

	// enhance domain with new calculated property
	dtm().getEnhancer().addCalculatedProperty(new CalculatedProperty(MasterEntity.class, "calcProp4", CalculatedPropertyCategory.EXPRESSION, "moneyProp", Money.class, "1 * 2.5", "title", "desc"));
	dtm().getEnhancer().apply();

	// enhance domain with new calculated property
	dtm().getEnhancer().addCalculatedProperty(new CalculatedProperty(MasterEntity.class, "calcProp5", CalculatedPropertyCategory.AGGREGATED_EXPRESSION, "bigDecimalProp", BigDecimal.class, "1 * 2.5", "title", "desc"));
	dtm().getEnhancer().apply();

	dtm().getSecondTick().check(MasterEntity.class, "calcProp5", true);
	assertTrue("The brand new calculated property should be excluded.", dtm().getRepresentation().isExcludedImmutably(MasterEntity.class, "calcProp1"));
	assertTrue("The brand new calculated property should be disabled.", dtm().getRepresentation().getFirstTick().isDisabledImmutably(MasterEntity.class, "calcProp2"));
	assertTrue("The brand new calculated property should be immutable checked.", dtm().getRepresentation().getSecondTick().isCheckedImmutably(MasterEntity.class, "calcProp3"));
	assertTrue("The brand new calculated property should be checked.", dtm().getSecondTick().isChecked(MasterEntity.class, "calcProp5"));

	/////////////// MODIFYING & MANAGING ///////////////
	dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp1").setTitle("new title");
	dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp1").setDesc("new title");
	dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp1").setExpression("56 * 78 / [integerProp]");

	dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp1").setResultType(BigInteger.class);
	dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp2").setResultType(BigDecimal.class);
	dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp3").setResultType(BigDecimal.class);
	dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp4").setResultType(BigDecimal.class);
	dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp5").setResultType(Money.class);
	dtm().getEnhancer().apply();

	assertTrue("The brand new calculated property should be excluded.", dtm().getRepresentation().isExcludedImmutably(MasterEntity.class, "calcProp1"));
	assertTrue("The brand new calculated property should be disabled.", dtm().getRepresentation().getFirstTick().isDisabledImmutably(MasterEntity.class, "calcProp2"));
	assertTrue("The brand new calculated property should be immutable checked.", dtm().getRepresentation().getSecondTick().isCheckedImmutably(MasterEntity.class, "calcProp3"));
	assertTrue("The brand new calculated property should be checked.", dtm().getSecondTick().isChecked(MasterEntity.class, "calcProp5"));

	/////////////// REMOVING & MANAGING ///////////////
	final ICalculatedProperty calc1 = dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp1");
	final ICalculatedProperty calc2 = dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp2");
	final ICalculatedProperty calc3 = dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp3");
	final ICalculatedProperty calc4 = dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp4");
	final ICalculatedProperty calc5 = dtm().getEnhancer().getCalculatedProperty(MasterEntity.class, "calcProp5");
	dtm().getEnhancer().removeCalculatedProperty(calc1);
	dtm().getEnhancer().removeCalculatedProperty(calc2);
	dtm().getEnhancer().removeCalculatedProperty(calc3);
	dtm().getEnhancer().removeCalculatedProperty(calc4);
	dtm().getEnhancer().removeCalculatedProperty(calc5);
	dtm().getEnhancer().apply();

	try {
	    dtm().getRepresentation().isExcludedImmutably(MasterEntity.class, "calcProp1");
	    fail("At this moment property 'calcProp1' should not exist and should cause exception.");
	} catch (final IllegalArgumentException e) {
	}
	try {
	    dtm().getRepresentation().getFirstTick().isDisabledImmutably(MasterEntity.class, "calcProp2");
	    fail("At this moment property 'calcProp2' should not exist and should cause exception.");
	} catch (final IllegalArgumentException e) {
	}
	try {
	    dtm().getRepresentation().getSecondTick().isCheckedImmutably(MasterEntity.class, "calcProp3");
	    fail("At this moment property 'calcProp3' should not exist and should cause exception.");
	} catch (final IllegalArgumentException e) {
	}
	try {
	    dtm().getSecondTick().isChecked(MasterEntity.class, "calcProp5");
	    fail("At this moment property 'calcProp5' should not exist and should cause exception.");
	} catch (final IllegalArgumentException e) {
	}

	dtm().getEnhancer().addCalculatedProperty(calc1);
	dtm().getEnhancer().addCalculatedProperty(calc2);
	dtm().getEnhancer().addCalculatedProperty(calc3);
	dtm().getEnhancer().addCalculatedProperty(calc4);
	dtm().getEnhancer().addCalculatedProperty(calc5);
	dtm().getEnhancer().apply();

	assertTrue("The brand new calculated property should be excluded.", dtm().getRepresentation().isExcludedImmutably(MasterEntity.class, "calcProp1"));
	assertTrue("The brand new calculated property should be disabled.", dtm().getRepresentation().getFirstTick().isDisabledImmutably(MasterEntity.class, "calcProp2"));
	assertTrue("The brand new calculated property should be immutable checked.", dtm().getRepresentation().getSecondTick().isCheckedImmutably(MasterEntity.class, "calcProp3"));
	assertTrue("The brand new calculated property should be checked.", dtm().getSecondTick().isChecked(MasterEntity.class, "calcProp5"));
    }
}
