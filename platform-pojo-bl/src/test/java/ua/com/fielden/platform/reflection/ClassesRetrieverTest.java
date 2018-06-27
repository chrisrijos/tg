package ua.com.fielden.platform.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import ua.com.fielden.platform.reflection.test_entities.ForFindClass;
import ua.com.fielden.platform.reflection.test_entities.ForFindClass.InnerForFindClass;
import ua.com.fielden.platform.reflection.test_entities.ISomeInterface;
import ua.com.fielden.platform.reflection.testannotation.BaseClassAnnotation;
import ua.com.fielden.platform.reflection.testannotation.DerivedClassAnnotation;
import ua.com.fielden.platform.reflection.testannotation.MethodInBaseClassAnnotation;
import ua.com.fielden.platform.reflection.testannotation.MethodInDerivedClassAnnotation;
import ua.com.fielden.platform.reflection.testclasses.BaseClass;
import ua.com.fielden.platform.reflection.testclasses.DerivedClass;

/**
 * Test case for {@link ClassesRetriever}.
 * 
 * @author TG Team
 * 
 */
public class ClassesRetrieverTest {

    @Test
    public void testGetAllClassesInPackage() {
        try {
            List<Class<?>> classesInPackage = ClassesRetriever.getAllClassesInPackage("target/test-classes", "ua.com.fielden.platform.reflection.testclasses");
            assertEquals("incorrect number of classes in ua.com.fielden.platform.reflection.testclasses package", 9, classesInPackage.size());
            classesInPackage = ClassesRetriever.getAllClassesInPackage("src/test/resources/testjar.jar", "jartest");
            assertEquals("incorrect number of classes in jartest package", 7, classesInPackage.size());
            classesInPackage = ClassesRetriever.getAllClassesInPackage("src/test/resources", "ua.com.fielden.platform.reflection.testclasses");
            assertEquals("incorrect number of classes in ua.com.fielden.platform.reflection.testclasses package", 7, classesInPackage.size());
        } catch (final Exception e) {
            e.printStackTrace();
            fail("There suppose to be no exception.");
        }
    }

    @Test
    public void testGetAllClassesInPackageAnnotatedWith() {
        try {
            List<Class<?>> classesInPackage = ClassesRetriever.getAllClassesInPackageAnnotatedWith("target/test-classes", "ua.com.fielden.platform.reflection.testclasses", BaseClassAnnotation.class);
            assertEquals("incorrect number of classes in ua.com.fielden.platform.reflection.testclasses package annotated with BaseClassAnnotation", 6, classesInPackage.size());
            classesInPackage = ClassesRetriever.getAllClassesInPackageAnnotatedWith("target/test-classes", "ua.com.fielden.platform.reflection.testclasses", DerivedClassAnnotation.class);
            assertEquals("incorrect number of classes in ua.com.fielden.platform.reflection.testclasses package annotated with DerivedClassAnnotation", 4, classesInPackage.size());
            classesInPackage = ClassesRetriever.getAllClassesInPackageAnnotatedWith("src/test/resources/testjar.jar", "jartest", BaseClassAnnotation.class);
            assertEquals("incorrect number of classes in jartest package annotated with BaseClassAnnotation", 4, classesInPackage.size());
            classesInPackage = ClassesRetriever.getAllClassesInPackageAnnotatedWith("src/test/resources/testjar.jar", "jartest", DerivedClassAnnotation.class);
            assertEquals("incorrect number of classes in jartest package annotated with DerivedClassAnnotation", 4, classesInPackage.size());
            classesInPackage = ClassesRetriever.getAllClassesInPackageAnnotatedWith("src/test/resources", "ua.com.fielden.platform.reflection.testclasses", BaseClassAnnotation.class);
            assertEquals("incorrect number of classes in ua.com.fielden.platform.reflection.testclasses package annotated with BaseClassAnnotation", 4, classesInPackage.size());
            classesInPackage = ClassesRetriever.getAllClassesInPackageAnnotatedWith("src/test/resources", "ua.com.fielden.platform.reflection.testclasses", DerivedClassAnnotation.class);
            assertEquals("incorrect number of classes in ua.com.fielden.platform.reflection.testclasses package annotated with DerivedClassAnnotation", 4, classesInPackage.size());
        } catch (final Exception e) {
            fail("There suppose to be no exception.");
        }
    }

    @Test
    public void testGetAllClassesInPackageDerivedFrom() {
            assertEquals("incorrect number of classes in ua.com.fielden.platform.reflection.testclasses package derived from BaseClass", 4, ClassesRetriever.getAllClassesInPackageDerivedFrom("target/test-classes", "ua.com.fielden.platform.reflection.testclasses", BaseClass.class).size());
            assertEquals("incorrect number of classes in ua.com.fielden.platform.reflection.testclasses package derived from DerivedClass", 2, ClassesRetriever.getAllClassesInPackageDerivedFrom("target/test-classes", "ua.com.fielden.platform.reflection.testclasses", DerivedClass.class).size());
            // let's test interface inheritance
            assertEquals("Incorrect number of classes in ua.com.fielden.platform.reflection package derived from ISomeInterface", 4, ClassesRetriever.getAllClassesInPackageDerivedFrom("target/test-classes", "ua.com.fielden.platform.reflection", ISomeInterface.class).size());
            assertEquals("incorrect number of classes in jartest package derived from BaseClass", 3, ClassesRetriever.getAllClassesInPackageDerivedFrom("src/test/resources/testjar.jar", "jartest", BaseClass.class).size());
            assertEquals("incorrect number of classes in jartest package derived from DerivedClass", 2, ClassesRetriever.getAllClassesInPackageDerivedFrom("src/test/resources/testjar.jar", "jartest", DerivedClass.class).size());
            assertEquals("incorrect number of classes in ua.com.fielden.platform.reflection.testclasses package derived from BaseClass", 3, ClassesRetriever.getAllClassesInPackageDerivedFrom("src/test/resources", "ua.com.fielden.platform.reflection.testclasses", BaseClass.class).size());
            assertEquals("incorrect number of classes in ua.com.fielden.platform.reflection.testclasses package derived from DerivedClass", 2, ClassesRetriever.getAllClassesInPackageDerivedFrom("src/test/resources", "ua.com.fielden.platform.reflection.testclasses", DerivedClass.class).size());
    }

    @Test
    public void testGetAllClassesInPackageWithAnnotatedMethod() {
        try {
            List<Class<?>> classesInPackage = ClassesRetriever.getAllClassInPackageWithAnnotatedMethods("target/test-classes", "ua.com.fielden.platform.reflection.testclasses", MethodInBaseClassAnnotation.class);
            assertEquals("incorrect number of classes in ua.com.fielden.platform.reflection.testclasses package those have method annotated with MethodInBaseClassAnnotation", 5, classesInPackage.size());
            classesInPackage = ClassesRetriever.getAllClassInPackageWithAnnotatedMethods("target/test-classes", "ua.com.fielden.platform.reflection.testclasses", MethodInDerivedClassAnnotation.class);
            assertEquals("incorrect number of classes in ua.com.fielden.platform.reflection.testclasses package those have method annotated with MethodInDerivedClassAnnotation", 3, classesInPackage.size());
            classesInPackage = ClassesRetriever.getAllClassInPackageWithAnnotatedMethods("src/test/resources/testjar.jar", "jartest", MethodInBaseClassAnnotation.class);
            assertEquals("incorrect number of classes in jartest package those have method annotated with MethodInBaseClassAnnotation", 3, classesInPackage.size());
            classesInPackage = ClassesRetriever.getAllClassInPackageWithAnnotatedMethods("src/test/resources/testjar.jar", "jartest", MethodInDerivedClassAnnotation.class);
            assertEquals("incorrect number of classes in jartest package those have method annotated with MethodInDerivedClassAnnotation", 3, classesInPackage.size());
            classesInPackage = ClassesRetriever.getAllClassInPackageWithAnnotatedMethods("src/test/resources", "ua.com.fielden.platform.reflection.testclasses", MethodInBaseClassAnnotation.class);
            assertEquals("incorrect number of classes in ua.com.fielden.platform.reflection.testclasses package those have method annotated with MethodInBaseClassAnnotation", 3, classesInPackage.size());
            classesInPackage = ClassesRetriever.getAllClassInPackageWithAnnotatedMethods("src/test/resources", "ua.com.fielden.platform.reflection.testclasses", MethodInDerivedClassAnnotation.class);
            assertEquals("incorrect number of classes in ua.com.fielden.platform.reflection.testclasses package those have method annotated with MethodInDerivedClassAnnotation", 3, classesInPackage.size());
        } catch (final Exception e) {
            fail("There suppose to be no exception.");
        }
    }

    @Test
    public void test_that_find_class_method_works() {
        assertEquals("Couldn't find ForFindClassTesting class", ClassesRetriever.findClass(ForFindClass.class.getName()), ForFindClass.class);
        assertEquals("Couldn't find innner class of ForFindClassTesting", ClassesRetriever.findClass(InnerForFindClass.class.getName()), InnerForFindClass.class);
    }

}
