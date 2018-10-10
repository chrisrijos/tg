package ua.com.fielden.platform.sample.domain;

import ua.com.fielden.platform.dao.IEntityDao;

/**
 * DAO contract for {@link TgPerson}.
 *
 * @author TG Team
 *
 */
public interface ITgPerson extends IEntityDao<TgPerson> {

    /** Should provide a person with default user name and password. */
    TgPerson makeUser(final TgPerson person);

    /** Should remove values for user name and password properties from a person, which effectively revokes user privilege from it. */
    TgPerson unmakeUser(final TgPerson person);

    /** Should set user password to a default value. */
    TgPerson resetPassword(final TgPerson person);

    /** Creates a new person and resets its password. */
    TgPerson populateNew(final String givenName, final String surName, final String fullName, final String userName);

    /** Retrieves current person. */
    TgPerson currentPerson();
}
