package ua.com.fielden.platform.user;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static ua.com.fielden.platform.entity.AbstractEntity.KEY;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.fetchAll;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.fetchOnly;

import java.util.Optional;

import org.junit.Test;

import ua.com.fielden.platform.entity.annotation.Title;
import ua.com.fielden.platform.entity.annotation.Unique;
import ua.com.fielden.platform.entity.meta.MetaProperty;
import ua.com.fielden.platform.entity.validation.UniqueValidator;
import ua.com.fielden.platform.property.validator.EmailValidator;
import ua.com.fielden.platform.property.validator.StringValidator;
import ua.com.fielden.platform.reflection.TitlesDescsGetter;
import ua.com.fielden.platform.security.exceptions.SecurityException;
import ua.com.fielden.platform.security.user.IUser;
import ua.com.fielden.platform.security.user.IUserProvider;
import ua.com.fielden.platform.security.user.User;
import ua.com.fielden.platform.security.user.validators.UserBaseOnUserValidator;
import ua.com.fielden.platform.test.ioc.UniversalConstantsForTesting;
import ua.com.fielden.platform.test_config.AbstractDaoTestCase;
import ua.com.fielden.platform.utils.IUniversalConstants;

/**
 * A test case to cover basic user validations.
 *
 * @author TG Team
 *
 */
public class UserTestCase extends AbstractDaoTestCase {

    private final IUser coUser = co$(User.class);

    @Test
    public void username_does_not_permit_password_reset_UUID_separator() {
        final User user = new_(User.class);
        final String value = format("USER%s1", User.SECRET_RESET_UUID_SEPERATOR);
        user.setKey(value);
        assertFalse(user.getProperty(KEY).isValid());
        assertEquals(format(StringValidator.validationErrorTemplate, user.getProperty(KEY).getTitle(), TitlesDescsGetter.getEntityTitleAndDesc(User.class).getKey()), user.getProperty("key").validationResult().getMessage());
    }

    @Test
    public void propety_email_in_user_defined_as_unique() {
        final User user1 = coUser.findByKey("USER1");
        final MetaProperty<String> emailProp = user1.getProperty("email");
        assertTrue(emailProp.getValidationAnnotations().stream().filter(a -> a instanceof Unique).count() > 0);
    }
    
    @Test
    public void users_have_unique_email_addresses() {
        final User user1 = coUser.findByKey("USER1");
        user1.setEmail("user1@company.com");
        assertNotNull(coUser.save(user1).getEmail());
        
        final User user2 = coUser.findByKey("USER2");
        user2.setEmail("user1@company.com");
        assertFalse(user2.isValid().isSuccessful());
        assertFalse(user2.getProperty("email").isValid());
        assertEquals(format(UniqueValidator.validationErrorTemplate, "user1@company.com", "email", User.class.getName()), user2.getProperty("email").getFirstFailure().getMessage());
    }

    @Test
    public void multiple_inactive_users_may_have_their_email_addresses_set_to_null() {
        final User user3 = coUser.findByKey("USER3").setActive(false).setEmail(null);
        assertTrue(user3.isValid().isSuccessful());
        assertNull(coUser.save(user3).getEmail());
        
        final User user4 = coUser.findByKey("USER4").setActive(false).setEmail(null);
        assertTrue(user4.isValid().isSuccessful());
        assertNull(coUser.save(user3).getEmail());
    }

    @Test
    public void email_address_value_has_bacis_validation() {
        
        final User user1 = coUser.findByKey("USER1");
        
        user1.setEmail("user1@");
        assertFalse(user1.getProperty("email").isValid());
        assertEquals(format(EmailValidator.validationErrorTemplate, "user1@", "email", "User") ,user1.getProperty("email").getFirstFailure().getMessage());
        
        user1.setEmail("@company.com");
        assertFalse(user1.getProperty("email").isValid());
        assertEquals(format(EmailValidator.validationErrorTemplate, "@company.com", "email", "User") ,user1.getProperty("email").getFirstFailure().getMessage());

        user1.setEmail("user1@company . com");
        assertFalse(user1.getProperty("email").isValid());
        assertEquals(format(EmailValidator.validationErrorTemplate, "user1@company . com", "email", "User") ,user1.getProperty("email").getFirstFailure().getMessage());
        
        user1.setEmail("user1@company@la.com");
        assertFalse(user1.getProperty("email").isValid());
        assertEquals(format(EmailValidator.validationErrorTemplate, "user1@company@la.com", "email", "User") ,user1.getProperty("email").getFirstFailure().getMessage());
        
        user1.setEmail("user1@company.com");
        assertTrue(user1.getProperty("email").isValid());
    }

    @Test
    public void weak_user_passwords_are_identified() {
        assertFalse(coUser.isPasswordStrong("passW0rd?"));
        assertFalse(coUser.isPasswordStrong("today123"));
        assertFalse(coUser.isPasswordStrong("aaaaa aaaa"));
    }
    
    @Test
    public void strong_user_passwords_are_identified() {
        assertTrue(coUser.isPasswordStrong("today123A"));
        assertTrue(coUser.isPasswordStrong("My voice is my password. Verify!"));
        assertTrue(coUser.isPasswordStrong("Sentances are strong and memorable passwords"));
    }
    
    @Test 
    public void user_identified_by_username_can_have_correct_password_reset_UUID_generated() {
        final Optional<User> user = coUser.assignPasswordResetUuid("USER3");
        assertTrue(user.isPresent());
        assertNotNull(user.get().getResetUuid());
    }

    @Test 
    public void locating_user_by_name_during_password_reset_UUID_generation_is_case_insensitive() {
        final Optional<User> user = coUser.assignPasswordResetUuid("user3");
        assertTrue(user.isPresent());
        assertNotNull(user.get().getResetUuid());
    }
    
    @Test
    public void user_identified_by_email_can_have_correct_password_reset_UUID_generated() {
        final Optional<User> user = coUser.assignPasswordResetUuid("user3@company.com");
        assertTrue(user.isPresent());
        assertNotNull(user.get().getResetUuid());
    }
    
    @Test
    public void password_reset_UUID_assignment_returns_empty_value_for_unidentified_user() {
        final Optional<User> user = coUser.assignPasswordResetUuid("invalid@company.com");
        assertFalse(user.isPresent());
    }
    
    @Test
    public void password_reset_UUID_structure_is_valid() {
        final UniversalConstantsForTesting consts = (UniversalConstantsForTesting) getInstance(IUniversalConstants.class);
        consts.setNow(dateTime("2016-04-05 14:00:00"));
        
        final Optional<User> user = coUser.assignPasswordResetUuid("USER3");
        assertTrue(user.isPresent());
        
        final String[] uuidParts = user.get().getResetUuid().split(User.SECRET_RESET_UUID_SEPERATOR);
        assertEquals(3, uuidParts.length);
        assertEquals(user.get().getKey(), uuidParts[0]);
        assertEquals(consts.now().plusHours(24).getMillis(), Long.valueOf(uuidParts[2]).longValue());
    }

    @Test
    public void user_can_be_found_by_password_reset_UUID() {
        final UniversalConstantsForTesting consts = (UniversalConstantsForTesting) getInstance(IUniversalConstants.class);
        consts.setNow(dateTime("2016-04-05 14:00:00"));
        
        final String uuid = coUser.assignPasswordResetUuid("USER3").get().getResetUuid();
        final Optional<User> user = coUser.findUserByResetUuid(uuid);
        assertTrue(user.isPresent());
        assertEquals(uuid, user.get().getResetUuid());
    }

    @Test
    public void resetting_user_password_removes_reset_UUID() {
        final User user = coUser.assignPasswordResetUuid("USER3").get();
        final User updatedUser = coUser.resetPasswd(user, "new and strong password!");
        assertNull(updatedUser.getResetUuid());
    }
    
    @Test
    public void expired_password_reset_UUID_is_invalid() {
        final UniversalConstantsForTesting consts = (UniversalConstantsForTesting) getInstance(IUniversalConstants.class);
        final String now = "2016-04-05 14:00:00";
        consts.setNow(dateTime(now));
        
        final String uuid = coUser.assignPasswordResetUuid("USER3").get().getResetUuid();
        assertTrue(coUser.isPasswordResetUuidValid(uuid));
        // move the time forward
        consts.setNow(dateTime(now).plusDays(2));
        assertFalse(coUser.isPasswordResetUuidValid(uuid));
    }

    @Test
    public void password_reset_UUID_with_no_associated_user_is_invalid() {
        final UniversalConstantsForTesting consts = (UniversalConstantsForTesting) getInstance(IUniversalConstants.class);
        final String now = "2016-04-05 14:00:00";
        consts.setNow(dateTime(now));
        
        final User user = coUser.assignPasswordResetUuid("USER3").get();
        final String uuid = user.getResetUuid();
        coUser.save(user.setResetUuid(null));
        
        assertFalse(coUser.isPasswordResetUuidValid(uuid));
    }

    @Test
    public void incorrect_password_reset_UUID_associated_with_user_is_recognized_as_invalid() {
        final UniversalConstantsForTesting consts = (UniversalConstantsForTesting) getInstance(IUniversalConstants.class);
        final String now = "2016-04-05 14:00:00";
        consts.setNow(dateTime(now));

        final User user = coUser.findByKeyAndFetch(fetchAll(User.class), "USER3");
        final String uuid = "incorrect password reset UUID";
        coUser.save(user.setResetUuid(uuid));
        
        final Optional<User> foundUser = coUser.findUserByResetUuid(uuid);
        assertFalse(foundUser.isPresent());
        assertFalse(coUser.isPasswordResetUuidValid(uuid));
    }

    @Test
    public void password_reset_for_non_existing_user_fails() {
        final UniversalConstantsForTesting consts = (UniversalConstantsForTesting) getInstance(IUniversalConstants.class);
        final String now = "2016-05-19 11:02:00";
        consts.setNow(dateTime(now));
        
        assertFalse(coUser.assignPasswordResetUuid("NON-EXISTING-USER").isPresent());
    }
    
    @Test
    public void password_reset_for_inactive_user_fails() {
        final UniversalConstantsForTesting consts = (UniversalConstantsForTesting) getInstance(IUniversalConstants.class);
        final String now = "2016-05-19 11:03:00";
        consts.setNow(dateTime(now));
        
        final User inactiveUser = coUser.findByKey("INACTIVE_USER");
        assertFalse(coUser.assignPasswordResetUuid(inactiveUser.getKey()).isPresent());
    }

    
    @Test 
    public void unit_test_user_cannot_be_persisted() {
        final IUser coUser = co$(User.class);
        final User user = new_(User.class, User.system_users.VIRTUAL_USER.name()).setBase(true);
        
        try {
            coUser.save(user);
            fail();
        } catch (final SecurityException ex) {
            assertEquals("VIRTUAL_USER cannot be persisted.", ex.getMessage());
        }
    }
    
    @Test
    public void self_modification_of_user_instance_result_in_correct_assignment_of_the_last_updated_by_group_of_properties() {
        final IUserProvider up = getInstance(IUserProvider.class);
        up.setUsername(up.getUser().getKey(), co$(User.class)); // refresh the user

        final User currUser = up.getUser();
        assertNotNull(currUser);
        assertTrue(currUser.isPersisted());
        assertEquals(1L, currUser.getVersion().longValue());
        
        final UniversalConstantsForTesting constants = (UniversalConstantsForTesting) getInstance(IUniversalConstants.class);
        constants.setNow(dateTime("2016-05-16 16:36:57"));
        
        // modify and save
        final String email = "new_email@company.com";
        final User savedUser = save(currUser.setEmail(email));
        assertEquals(2L, savedUser.getVersion().longValue());
        
        // refresh the user instance in the provider
        up.setUsername(currUser.getKey(), co$(User.class));
        
        final User user = up.getUser();
        assertTrue(user.isPersisted());
        assertEquals(2L, savedUser.getVersion().longValue());
        assertEquals(email, user.getEmail());
        
        assertNotNull(user.getLastUpdatedBy());
        assertEquals(user, user.getLastUpdatedBy());
        assertNotNull(user.getLastUpdatedDate());
        assertEquals(constants.now().toDate(), user.getLastUpdatedDate());
        assertNotNull(user.getLastUpdatedTransactionGuid());
    }
    
    @Test
    public void property_lastUpdatedBy_is_fetched_as_id_only_proxy_even_for_a_fetchOnly_strategy_for_owning_entity() {
        final User user = co$(User.class).findByKeyAndFetch(fetchOnly(User.class), "USER1");
        assertTrue(user.getLastUpdatedBy().isIdOnlyProxy());
    }
 
    @Test
    public void if_base_prop_is_set_to_false_then_prop_basedOnUser_becomes_required() {
        final User user1 = coUser.findByKeyAndFetch(co$(User.class).getFetchProvider().fetchModel(), "USER1");
        user1.setBase(false);
        
        assertTrue(user1.getProperty("basedOnUser").isRequired());
        
        final User user3 = coUser.findByKey("USER2");
        user1.setBasedOnUser(user3);
        
        final User user1saved = save(user1);
        assertEquals(user3, user1saved.getBasedOnUser());
    }


    @Test
    public void if_base_prop_is_set_to_true_then_prop_basedOnUser_becomes_not_required_and_its_value_is_removed() {
        final User user5 = coUser.findByKeyAndFetch(co$(User.class).getFetchProvider().fetchModel(), "USER5");
        assertTrue(user5.getProperty("basedOnUser").isRequired());
        assertNotNull(user5.getBasedOnUser());
        user5.setBase(true);
        assertFalse(user5.getProperty("basedOnUser").isRequired());
        assertNull(user5.getBasedOnUser());
        
        final User user5saved = save(user5);
        assertTrue(user5saved.isBase());
    }
    
    @Test
    public void system_users_cannot_have_property_base_changed() {
        final User unitTestUser = coUser.findByKeyAndFetch(co$(User.class).getFetchProvider().fetchModel(), User.system_users.UNIT_TEST_USER);
        assertNotNull(unitTestUser);
        assertTrue(unitTestUser.isBase());
        assertTrue(unitTestUser.getProperty("base").isValid());
        unitTestUser.setBase(false);
        assertFalse(unitTestUser.getProperty("base").isValid());
        assertTrue(unitTestUser.isBase());
    }

    @Test
    public void the_use_of_self_as_basedOnUser_is_not_permitted() {
        final User user1 = coUser.findByKeyAndFetch(co$(User.class).getFetchProvider().fetchModel(), "USER1");
        assertTrue(user1.getProperty("basedOnUser").isValid());
        user1.setBasedOnUser(user1);
        assertFalse(user1.getProperty("basedOnUser").isValid());
        assertEquals(UserBaseOnUserValidator.SELF_REFERENCE_IS_NOT_PERMITTED, user1.getProperty("basedOnUser").getFirstFailure().getMessage());
    }

    @Test
    public void system_users_cannot_have_property_basedOnUser_changed() {
        final User unitTestUser = coUser.findByKeyAndFetch(co$(User.class).getFetchProvider().fetchModel(), User.system_users.UNIT_TEST_USER);
        assertNotNull(unitTestUser);
        assertNull(unitTestUser.getBasedOnUser());
        assertTrue(unitTestUser.getProperty("basedOnUser").isValid());
        final User user3 = coUser.findByKeyAndFetch(co$(User.class).getFetchProvider().fetchModel(), "USER3");
        unitTestUser.setBasedOnUser(user3);
        assertFalse(unitTestUser.getProperty("basedOnUser").isValid());
        assertNull(unitTestUser.getBasedOnUser());
        assertEquals(format(UserBaseOnUserValidator.SYSTEM_BUILT_IN_ACCOUNTS_CANNOT_HAVE_BASED_ON_USER, unitTestUser.getKey()), unitTestUser.getProperty("basedOnUser").getFirstFailure().getMessage());
    }

    @Test
    public void only_a_base_user_can_be_set_as_basedOnUser() {
        final User user5 = coUser.findByKeyAndFetch(co$(User.class).getFetchProvider().fetchModel(), "USER5");
        assertTrue(user5.getProperty("basedOnUser").isValid());
        assertEquals("USER1", user5.getBasedOnUser().getKey());
        
        final User nonbaseUser6 = coUser.findByKeyAndFetch(co$(User.class).getFetchProvider().fetchModel(), "USER6");
        user5.setBasedOnUser(nonbaseUser6);
        assertFalse(user5.getProperty("basedOnUser").isValid());
        assertEquals("USER1", user5.getBasedOnUser().getKey());
        assertEquals(format(UserBaseOnUserValidator.ONLY_BASE_USER_CAN_BE_USED_FOR_INHERITANCE, nonbaseUser6.getKey()), user5.getProperty("basedOnUser").getFirstFailure().getMessage());
        
        final User baseUser3 = coUser.findByKeyAndFetch(co$(User.class).getFetchProvider().fetchModel(), "USER3");
        user5.setBasedOnUser(baseUser3);
        final User savedUser5 = save(user5);
        assertTrue(savedUser5.getProperty("basedOnUser").isValid());
        assertEquals("USER3", savedUser5.getBasedOnUser().getKey());
    }

    @Test
    public void once_basedOnUser_is_assigned_to_a_base_user_then_its_property_base_becomes_false() {
        final User user3 = coUser.findByKeyAndFetch(co$(User.class).getFetchProvider().fetchModel(), "USER3");
        assertTrue(user3.isBase());

        final User baseUser4 = coUser.findByKeyAndFetch(co$(User.class).getFetchProvider().fetchModel(), "USER4");
        user3.setBasedOnUser(baseUser4);
        assertFalse(user3.isBase());
    }

    
    @Override
    protected void populateDomain() {
        super.populateDomain();
        
        // add inactive users with no email addresses
        coUser.save(new_(User.class, "INACTIVE_USER").setBase(true).setActive(false));
        final User user1 = coUser.save(coUser.save(new_(User.class, "USER1").setBase(true)).setEmail("USER1@company.com"));
        coUser.save(new_(User.class, "USER2").setBase(true));

        // add active users with email addresses
        coUser.save(new_(User.class, "USER3").setBase(true).setEmail("USER3@company.com").setActive(true));
        coUser.save(new_(User.class, "USER4").setBase(true).setEmail("USER4@company.com").setActive(true));
        
        // based on users
        coUser.save(new_(User.class, "USER5").setBasedOnUser(user1).setEmail("USER5@company.com"));
        coUser.save(new_(User.class, "USER6").setBasedOnUser(user1).setEmail("USER6@company.com"));
    }

}