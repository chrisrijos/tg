package ua.com.fielden.platform.domaintree.centre.analyses.impl;

import java.nio.ByteBuffer;
import java.util.Set;

import ua.com.fielden.platform.domaintree.centre.analyses.ISentinelDomainTreeRepresentation;
import ua.com.fielden.platform.domaintree.impl.EnhancementLinkedRootsSet;
import ua.com.fielden.platform.domaintree.impl.EnhancementSet;
import ua.com.fielden.platform.entity.annotation.Calculated;
import ua.com.fielden.platform.reflection.AnnotationReflector;
import ua.com.fielden.platform.reflection.PropertyTypeDeterminator;
import ua.com.fielden.platform.serialisation.api.ISerialiser;
import ua.com.fielden.platform.utils.EntityUtils;
import ua.com.fielden.platform.utils.Pair;

/**
 * A domain tree representation for sentinel analyses.
 * 
 * @author TG Team
 * 
 */
public class SentinelDomainTreeRepresentation extends AnalysisDomainTreeRepresentation implements ISentinelDomainTreeRepresentation {
    public static final String COUNT_OF_SELF_DASHBOARD = "countOfSelfDashboard";
    public static final String RED = "RED";
    public static final String YELLOW = "YELLOW";
    public static final String GREEN = "GREEN";

    /**
     * A <i>representation</i> constructor for the first time instantiation.
     * 
     * @param serialiser
     * @param rootTypes
     */
    public SentinelDomainTreeRepresentation(final ISerialiser serialiser, final Set<Class<?>> rootTypes) {
        this(serialiser, rootTypes, createSet(), new SentinelAddToDistributionTickRepresentation(), new SentinelAddToAggregationTickRepresentation());
    }

    /**
     * A <i>representation</i> constructor. Initialises also children references on itself.
     */
    protected SentinelDomainTreeRepresentation(final ISerialiser serialiser, final Set<Class<?>> rootTypes, final Set<Pair<Class<?>, String>> excludedProperties, final SentinelAddToDistributionTickRepresentation firstTick, final SentinelAddToAggregationTickRepresentation secondTick) {
        super(serialiser, rootTypes, excludedProperties, firstTick, secondTick);
    }

    @Override
    public ISentinelAddToDistributionTickRepresentation getFirstTick() {
        return (ISentinelAddToDistributionTickRepresentation) super.getFirstTick();
    }

    @Override
    public ISentinelAddToAggregationTickRepresentation getSecondTick() {
        return (ISentinelAddToAggregationTickRepresentation) super.getSecondTick();
    }

    /**
     * Makes CountOfSelfDashboard property disabled for both ticks.
     */
    public void provideMetaStateForCountOfSelfDashboardProperty() {
        for (final Class<?> rootType : rootTypes()) {
            if (!isExcludedImmutably(rootType, SentinelDomainTreeRepresentation.COUNT_OF_SELF_DASHBOARD)) {
                getFirstTick().disableImmutably(rootType, SentinelDomainTreeRepresentation.COUNT_OF_SELF_DASHBOARD);
                getSecondTick().disableImmutably(rootType, SentinelDomainTreeRepresentation.COUNT_OF_SELF_DASHBOARD);
            }
        }
    }

    @Override
    public boolean isExcludedImmutably(final Class<?> root, final String property) {
        // inject an enhanced type into method implementation
        final Class<?> managedType = managedType(root);

        final boolean isEntityItself = "".equals(property); // empty property means "entity itself"
        return (super.isExcludedImmutably(managedType, property)) || // base TG analysis domain representation usage
                !(isSentinel(managedType, property) || isEntityItself || COUNT_OF_SELF_DASHBOARD.equals(property)); // exclude all properties except "sentinels" and special "countOfSelf" property
    }

    /**
     * Indicates whether the property represents so called "sentinel", which is by a contract a case-insensitive implementation of "case/when" string calculated property with
     * return values "GREEN" or "RED".
     * 
     * @param managedType
     * @param property
     * @return
     */
    private boolean isSentinel(final Class<?> managedType, final String property) {
        final boolean isEntityItself = "".equals(property); // empty property means "entity itself"
        final Class<?> propertyType = isEntityItself ? managedType : PropertyTypeDeterminator.determinePropertyType(managedType, property);
        final Calculated calculatedAnnotation = isEntityItself ? null : AnnotationReflector.getPropertyAnnotation(Calculated.class, managedType, property);
        final String upperCasedAndTrimmedExpr = calculatedAnnotation != null ? calculatedAnnotation.value().trim().toUpperCase() : null;
        return calculatedAnnotation != null && EntityUtils.isString(propertyType) && //
                upperCasedAndTrimmedExpr.startsWith("CASE WHEN ") && upperCasedAndTrimmedExpr.endsWith(" END") && //
                upperCasedAndTrimmedExpr.contains(" \"" + GREEN + "\" ") && upperCasedAndTrimmedExpr.contains(" \"" + RED + "\" ");
    }

    public static class SentinelAddToDistributionTickRepresentation extends AnalysisAddToDistributionTickRepresentation implements ISentinelAddToDistributionTickRepresentation {
        /**
         * Used for serialisation and for normal initialisation. IMPORTANT : To use this tick it should be passed into representation constructor, which should initialise "dtr"
         * field.
         */
        public SentinelAddToDistributionTickRepresentation() {
        }
    }

    public static class SentinelAddToAggregationTickRepresentation extends AnalysisAddToAggregationTickRepresentation implements ISentinelAddToAggregationTickRepresentation {
        /**
         * Used for serialisation and for normal initialisation. IMPORTANT : To use this tick it should be passed into representation constructor, which should initialise "dtr"
         * field.
         */
        public SentinelAddToAggregationTickRepresentation() {
        }
    }

    /**
     * A specific Kryo serialiser for {@link SentinelDomainTreeRepresentation}.
     * 
     * @author TG Team
     * 
     */
    public static class SentinelDomainTreeRepresentationSerialiser extends AbstractDomainTreeRepresentationSerialiser<SentinelDomainTreeRepresentation> {
        public SentinelDomainTreeRepresentationSerialiser(final ISerialiser serialiser) {
            super(serialiser);
        }

        @Override
        public SentinelDomainTreeRepresentation read(final ByteBuffer buffer) {
            final EnhancementLinkedRootsSet rootTypes = readValue(buffer, EnhancementLinkedRootsSet.class);
            final EnhancementSet excludedProperties = readValue(buffer, EnhancementSet.class);
            final SentinelAddToDistributionTickRepresentation firstTick = readValue(buffer, SentinelAddToDistributionTickRepresentation.class);
            final SentinelAddToAggregationTickRepresentation secondTick = readValue(buffer, SentinelAddToAggregationTickRepresentation.class);
            return new SentinelDomainTreeRepresentation(serialiser(), rootTypes, excludedProperties, firstTick, secondTick);
        }
    }
}
