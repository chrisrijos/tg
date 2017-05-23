package ua.com.fielden.platform.dao;

import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.expr;
import static ua.com.fielden.platform.entity.query.fluent.EntityQueryUtils.select;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ua.com.fielden.platform.entity.AbstractEntity;
import ua.com.fielden.platform.entity.AbstractUnionEntity;
import ua.com.fielden.platform.entity.DynamicEntityKey;
import ua.com.fielden.platform.entity.annotation.Calculated;
import ua.com.fielden.platform.entity.meta.PropertyDescriptor;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.ICaseWhenFunctionWhen;
import ua.com.fielden.platform.entity.query.fluent.EntityQueryProgressiveInterfaces.IStandAloneExprOperationAndClose;
import ua.com.fielden.platform.entity.query.model.ExpressionModel;
import ua.com.fielden.platform.entity.query.model.PrimitiveResultQueryModel;
import ua.com.fielden.platform.expression.ExpressionText2ModelConverter;
import ua.com.fielden.platform.reflection.AnnotationReflector;
import ua.com.fielden.platform.reflection.Finder;
import ua.com.fielden.platform.reflection.Reflector;
import ua.com.fielden.platform.utils.EntityUtils;
import ua.com.fielden.platform.utils.Pair;

public class DomainMetadataExpressionsGenerator {

    ExpressionModel generateUnionEntityPropertyExpression(final Class<? extends AbstractUnionEntity> entityType, final String commonPropName) {
        final List<Field> props = AbstractUnionEntity.unionProperties(entityType);
        final Iterator<Field> iterator = props.iterator();
        final String firstUnionPropName = iterator.next().getName();
        ICaseWhenFunctionWhen<IStandAloneExprOperationAndClose, AbstractEntity<?>> expressionModelInProgress = expr().caseWhen().prop(firstUnionPropName).isNotNull().then().prop(firstUnionPropName
                + "." + commonPropName);

        for (; iterator.hasNext();) {
            final String unionPropName = iterator.next().getName();
            expressionModelInProgress = expressionModelInProgress.when().prop(unionPropName).isNotNull().then().prop(unionPropName + "." + commonPropName);
        }

        return expressionModelInProgress.otherwise().val(null).end().model();
    }

//    ExpressionModel getVirtualKeyPropForEntityWithCompositeKey(final Class<? extends AbstractEntity<DynamicEntityKey>> entityType, final List<Pair<Field, Boolean>> keyMembers) {
//        final Iterator<Pair<Field, Boolean>> iterator = keyMembers.iterator();
//        final Pair<Field, Boolean> firstMember = iterator.next();
//        IConcatFunctionWith<IStandAloneExprOperationAndClose, AbstractEntity<?>> expressionModelInProgress = firstMember.getValue() ? //
//        expr().concat().prop(getKeyMemberConcatenationExpression(firstMember.getKey()))
//                :
//                expr().concat().prop(getKeyMemberConcatenationExpression(firstMember.getKey()));
//        for (; iterator.hasNext();) {
//            expressionModelInProgress = expressionModelInProgress.with().val(Reflector.getKeyMemberSeparator(entityType));
//            expressionModelInProgress = expressionModelInProgress.with().prop(getKeyMemberConcatenationExpression(iterator.next().getKey()));
//        }
//        return expressionModelInProgress.end().model();
//    }

    ExpressionModel getVirtualKeyPropForEntityWithCompositeKey(final Class<? extends AbstractEntity<DynamicEntityKey>> entityType, final List<Pair<Field, Boolean>> keyMembers) {
        return composeExpression(keyMembers, Reflector.getKeyMemberSeparator(entityType));
    }

    private String getKeyMemberConcatenationExpression(final Field keyMember) {
        if (PropertyDescriptor.class != keyMember.getType() && EntityUtils.isEntityType(keyMember.getType())) {
            return keyMember.getName() + ".key";
        } else {
            return keyMember.getName();
        }
    }

    private ExpressionModel composeExpression(final List<Pair<Field, Boolean>> original, final String separator) {
        ExpressionModel currExp = null;
        Boolean currExpIsOptional = null;

        for (final Pair<Field, Boolean> originalField : original) {
            currExp = composeTwo(new Pair<ExpressionModel, Boolean>(currExp, currExpIsOptional), originalField, separator);
            currExpIsOptional = currExpIsOptional != null ? currExpIsOptional && originalField.getValue() : originalField.getValue();
        }

        return currExp;
    }

    private ExpressionModel concatTwo(final ExpressionModel first, final String secondPropName, final String separator) {
        return expr().concat().expr(first).with().val(separator).with().prop(secondPropName).end().model();
    }

    private ExpressionModel composeTwo(final Pair<ExpressionModel, Boolean> first, final Pair<Field, Boolean> second, final String separator) {
        final ExpressionModel firstModel = first.getKey();
        final Boolean firstIsOptional = first.getValue();

        final String secondPropName = getKeyMemberConcatenationExpression(second.getKey());
        final boolean secondPropIsOptional = second.getValue();

        if (first.getKey() == null) {
            return expr().prop(secondPropName).model();
        } else {
            if (firstIsOptional) {
                if (secondPropIsOptional) {
                    return expr().caseWhen().expr(firstModel).isNotNull().and().prop(secondPropName).isNotNull().then().expr(concatTwo(firstModel, secondPropName, separator)). //
                    when().expr(firstModel).isNotNull().and().prop(secondPropName).isNull().then().expr(firstModel). //
                    when().prop(secondPropName).isNotNull().then().prop(secondPropName). //
                    otherwise().val(null).endAsStr(256).model();
                } else {
                    return expr().caseWhen().expr(firstModel).isNotNull().then().expr(concatTwo(firstModel, secondPropName, separator)). //
                    otherwise().prop(secondPropName).endAsStr(256).model();
                }
            } else {
                if (secondPropIsOptional) {
                    return expr().caseWhen().prop(secondPropName).isNotNull().then().expr(concatTwo(firstModel, secondPropName, separator)). //
                    otherwise().expr(firstModel).endAsStr(256).model();
                } else {
                    return concatTwo(firstModel, secondPropName, separator);
                }
            }
        }
    }

    ExpressionModel extractExpressionModelFromCalculatedProperty(final Class<? extends AbstractEntity<?>> entityType, final Field calculatedPropfield) throws Exception {
        final Calculated calcAnnotation = AnnotationReflector.getAnnotation(calculatedPropfield, Calculated.class);
        if (!"".equals(calcAnnotation.value())) {
            return createExpressionText2ModelConverter(entityType, calcAnnotation).convert().getModel();
        } else {
            try {
                final Field exprField = Finder.getFieldByName(entityType, calculatedPropfield.getName() + "_");
                exprField.setAccessible(true);
                return (ExpressionModel) exprField.get(null);
            } catch (final Exception e) {
                throw new IllegalStateException("Hard-coded expression model for prop [" + calculatedPropfield.getName() + "] is missing! ---" + e);
            }
        }
    }

    private ExpressionText2ModelConverter createExpressionText2ModelConverter(final Class<? extends AbstractEntity<?>> entityType, final Calculated calcAnnotation)
            throws Exception {
        if (AnnotationReflector.isContextual(calcAnnotation)) {
            return new ExpressionText2ModelConverter(getRootType(calcAnnotation), calcAnnotation.contextPath(), calcAnnotation.value());
        } else {
            return new ExpressionText2ModelConverter(entityType, calcAnnotation.value());
        }
    }

    public Class<? extends AbstractEntity<?>> getRootType(final Calculated calcAnnotation) throws ClassNotFoundException {
        return (Class<? extends AbstractEntity<?>>) ClassLoader.getSystemClassLoader().loadClass(calcAnnotation.rootTypeName());
    }

    private PrimitiveResultQueryModel getReferenceCountForSingleProp(final Class<? extends AbstractEntity<?>> entityType, final String propName) {
        return select(entityType).where().prop(propName).eq().extProp("id").yield().countAll().modelAsPrimitive();
    }

    ExpressionModel getReferencesCountPropForEntity(final Set<Pair<Class<? extends AbstractEntity<?>>, String>> references) {
        if (references.size() == 0) {
            return expr().val(0).model();
        }

        final Iterator<Pair<Class<? extends AbstractEntity<?>>, String>> iterator = references.iterator();
        final Pair<Class<? extends AbstractEntity<?>>, String> firstEntry = iterator.next();
        IStandAloneExprOperationAndClose expressionModelInProgress = expr().model(getReferenceCountForSingleProp(firstEntry.getKey(), firstEntry.getValue()));
        for (; iterator.hasNext();) {
            final Pair<Class<? extends AbstractEntity<?>>, String> entry = iterator.next();
            expressionModelInProgress = expressionModelInProgress.add().model(getReferenceCountForSingleProp(entry.getKey(), entry.getValue()));
        }
        return expressionModelInProgress.model();
    }

}