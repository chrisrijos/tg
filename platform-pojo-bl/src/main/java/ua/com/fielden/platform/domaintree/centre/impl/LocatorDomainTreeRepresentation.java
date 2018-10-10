package ua.com.fielden.platform.domaintree.centre.impl;

import java.nio.ByteBuffer;
import java.util.Set;

import ua.com.fielden.platform.domaintree.centre.ILocatorDomainTreeRepresentation;
import ua.com.fielden.platform.domaintree.impl.AbstractDomainTree;
import ua.com.fielden.platform.domaintree.impl.EnhancementLinkedRootsSet;
import ua.com.fielden.platform.domaintree.impl.EnhancementSet;
import ua.com.fielden.platform.serialisation.api.ISerialiser;
import ua.com.fielden.platform.utils.Pair;

/**
 * A domain tree representation for locators specific. A first tick means "include to locator criteria", second -- "include to locator result-set".<br>
 * 
 * @author TG Team
 * 
 */
public class LocatorDomainTreeRepresentation extends CentreDomainTreeRepresentation implements ILocatorDomainTreeRepresentation {
    /**
     * A <i>representation</i> constructor for the first time instantiation. Initialises also children references on itself.
     */
    public LocatorDomainTreeRepresentation(final ISerialiser serialiser, final Set<Class<?>> rootTypes) {
        this(serialiser, rootTypes, AbstractDomainTree.createSet(), new AddToCriteriaTick(), new AddToResultSetTick());
    }

    /**
     * A <i>representation</i> constructor. Initialises also children references on itself.
     */
    protected LocatorDomainTreeRepresentation(final ISerialiser serialiser, final Set<Class<?>> rootTypes, final Set<Pair<Class<?>, String>> excludedProperties, final AddToCriteriaTick firstTick, final AddToResultSetTick secondTick) {
        super(serialiser, rootTypes, excludedProperties, firstTick, secondTick);
    }

    /**
     * A specific Kryo serialiser for {@link LocatorDomainTreeRepresentation}.
     * 
     * @author TG Team
     * 
     */
    public static class LocatorDomainTreeRepresentationSerialiser extends AbstractDomainTreeRepresentationSerialiser<LocatorDomainTreeRepresentation> {
        public LocatorDomainTreeRepresentationSerialiser(final ISerialiser serialiser) {
            super(serialiser);
        }

        @Override
        public LocatorDomainTreeRepresentation read(final ByteBuffer buffer) {
            final EnhancementLinkedRootsSet rootTypes = readValue(buffer, EnhancementLinkedRootsSet.class);
            final EnhancementSet excludedProperties = readValue(buffer, EnhancementSet.class);
            final AddToCriteriaTick firstTick = readValue(buffer, AddToCriteriaTick.class);
            final AddToResultSetTick secondTick = readValue(buffer, AddToResultSetTick.class);
            return new LocatorDomainTreeRepresentation(serialiser(), rootTypes, excludedProperties, firstTick, secondTick);
        }
    }
}
