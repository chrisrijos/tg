package ua.com.fielden.platform.expression.ast.visitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;

import org.junit.Test;

import ua.com.fielden.platform.expression.EgTokenCategory;
import ua.com.fielden.platform.expression.ExpressionLexer;
import ua.com.fielden.platform.expression.ExpressionParser;
import ua.com.fielden.platform.expression.Token;
import ua.com.fielden.platform.expression.ast.AstNode;
import ua.com.fielden.platform.expression.ast.AstWalker;
import ua.com.fielden.platform.expression.ast.visitor.entities.EntityLevel1;
import ua.com.fielden.platform.expression.automata.SequenceRecognitionFailed;
import ua.com.fielden.platform.expression.exception.RecognitionException;
import ua.com.fielden.platform.expression.exception.semantic.SemanticException;
import ua.com.fielden.platform.expression.exception.semantic.UnsupportedTypeException;
import ua.com.fielden.platform.types.Money;

public class TypeEnforcementForDivOperationTest {

    @Test
    public void test_div_operation_with_literals_case_1() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("1 / 2").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	new AstWalker(ast, visitor).walk();
	assertEquals("Incorrect type.", BigDecimal.class, ast.getType());
	assertEquals("Incorrect value.", new BigDecimal("0.5000"), ast.getValue());
    }

    @Test
    public void test_div_operation_with_literals_case_2() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("1 / 2.6").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	new AstWalker(ast, visitor).walk();
	assertEquals("Incorrect type.", BigDecimal.class, ast.getType());
	assertEquals("Incorrect value.", new BigDecimal("0.3846"), ast.getValue());
    }

    @Test
    public void test_div_operation_with_literals_case_3() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("2.6 / 1").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	new AstWalker(ast, visitor).walk();
	assertEquals("Incorrect type.", BigDecimal.class, ast.getType());
	assertEquals("Incorrect value.", new BigDecimal("2.6000"), ast.getValue());
    }

    @Test
    public void test_div_operation_with_literals_case_4() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("2.6 / 1.4").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	new AstWalker(ast, visitor).walk();
	assertEquals("Incorrect type.", BigDecimal.class, ast.getType());
	assertEquals("Incorrect value.", new BigDecimal("1.8571"), ast.getValue());
    }

    @Test
    public void test_div_operation_with_literals_case_5() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("\"hello\" / \" world\"").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	try {
	    new AstWalker(ast, visitor).walk();
	} catch (final UnsupportedTypeException ex) {
	    assertEquals("Incorrect error message.", "Operands of string type are not applicable to operation " + EgTokenCategory.DIV, ex.getMessage());
	}
    }

    @Test
    public void test_div_operation_with_literals_case_6() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("1d / 3d").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	try {
	    new AstWalker(ast, visitor).walk();
	} catch (final UnsupportedTypeException ex) {
	    assertEquals("Incorrect error message.", "Operands of date literal type are not applicable to operation " + EgTokenCategory.DIV, ex.getMessage());
	}
    }

    @Test
    public void test_div_operation_with_literals_case_7() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("3m / 2m").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	try {
	    new AstWalker(ast, visitor).walk();
	} catch (final UnsupportedTypeException ex) {
	    assertEquals("Incorrect error message.", "Operands of date literal type are not applicable to operation " + EgTokenCategory.DIV, ex.getMessage());
	}
    }

    @Test
    public void test_div_operation_with_literals_case_8() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("3y / 2y").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	try {
	    new AstWalker(ast, visitor).walk();
	} catch (final UnsupportedTypeException ex) {
	    assertEquals("Incorrect error message.", "Operands of date literal type are not applicable to operation " + EgTokenCategory.DIV, ex.getMessage());
	}
    }

    @Test
    public void test_div_operation_with_literals_case_9() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("2 / (2 / 6.5)").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	new AstWalker(ast, visitor).walk();
	assertEquals("Incorrect type.", BigDecimal.class, ast.getType());
	assertEquals("Incorrect value.", new BigDecimal("6.4998"), ast.getValue());
    }

    @Test
    public void test_div_operation_with_literals_case_10() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("(2 / 2 / 1) / 5").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	new AstWalker(ast, visitor).walk();
	assertEquals("Incorrect type.", BigDecimal.class, ast.getType());
	assertEquals("Incorrect value.", new BigDecimal("0.2000"), ast.getValue());
    }

    @Test
    public void test_int_literal_div_int_property() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("2 / intProperty").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	new AstWalker(ast, visitor).walk();
	assertEquals("Incorrect type.", BigDecimal.class, ast.getType());
	assertNull("Incorrect value.", ast.getValue());
    }

    @Test
    public void test_int_property_div_int_literal() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("intProperty / 2").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	new AstWalker(ast, visitor).walk();
	assertEquals("Incorrect type.", BigDecimal.class, ast.getType());
	assertNull("Incorrect value.", ast.getValue());
    }

    @Test
    public void test_decimal_literal_div_int_property() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("2.5 / intProperty").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	new AstWalker(ast, visitor).walk();
	assertEquals("Incorrect type.", BigDecimal.class, ast.getType());
	assertNull("Incorrect value.", ast.getValue());
    }

    @Test
    public void test_int_property_div_decimal_literal() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("intProperty / 2.5").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	new AstWalker(ast, visitor).walk();
	assertEquals("Incorrect type.", BigDecimal.class, ast.getType());
	assertNull("Incorrect value.", ast.getValue());
    }

    @Test
    public void test_decimal_literal_div_decimal_property() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("2.5 / decimalProperty").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	new AstWalker(ast, visitor).walk();
	assertEquals("Incorrect type.", BigDecimal.class, ast.getType());
	assertNull("Incorrect value.", ast.getValue());
    }

    @Test
    public void test_decimal_property_div_decimal_literal() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("decimalProperty / 2.5").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	new AstWalker(ast, visitor).walk();
	assertEquals("Incorrect type.", BigDecimal.class, ast.getType());
	assertNull("Incorrect value.", ast.getValue());
    }

    @Test
    public void test_int_literal_div_money_property() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("2 / moneyProperty").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	new AstWalker(ast, visitor).walk();
	assertEquals("Incorrect type.", Money.class, ast.getType());
	assertNull("Incorrect value.", ast.getValue());
    }

    @Test
    public void test_money_property_div_int_literal() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("moneyProperty / 2").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	new AstWalker(ast, visitor).walk();
	assertEquals("Incorrect type.", Money.class, ast.getType());
	assertNull("Incorrect value.", ast.getValue());
    }

    @Test
    public void test_decimal_literal_div_money_property() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("2.5 / moneyProperty").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	new AstWalker(ast, visitor).walk();
	assertEquals("Incorrect type.", Money.class, ast.getType());
	assertNull("Incorrect value.", ast.getValue());
    }

    @Test
    public void test_money_property_div_decimal_literal() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("moneyProperty / 2.5").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	new AstWalker(ast, visitor).walk();
	assertEquals("Incorrect type.", Money.class, ast.getType());
	assertNull("Incorrect value.", ast.getValue());
    }

    @Test
    public void test_money_property_div_money_property() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("moneyProperty / selfProperty.moneyProperty").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	new AstWalker(ast, visitor).walk();
	assertEquals("Incorrect type.", BigDecimal.class, ast.getType());
	assertNull("Incorrect value.", ast.getValue());
    }

    @Test
    public void test_string_literal_div_string_property() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("\"word\" / strProperty").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	try {
	    new AstWalker(ast, visitor).walk();
	} catch (final UnsupportedTypeException ex) {
	    assertEquals("Incorrect error message.", "Operands of string type are not applicable to operation " + EgTokenCategory.DIV, ex.getMessage());
	}
    }

    @Test
    public void test_string_property_div_string_literal() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("strProperty / \"word\"").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	try {
	    new AstWalker(ast, visitor).walk();
	} catch (final UnsupportedTypeException ex) {
	    assertEquals("Incorrect error message.", "Operands of string type are not applicable to operation " + EgTokenCategory.DIV, ex.getMessage());
	}
    }

    @Test
    public void test_date_property_div_date_property() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("dateProperty / dateProperty").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	try {
	    new AstWalker(ast, visitor).walk();
	} catch (final UnsupportedTypeException ex) {
	    assertEquals("Incorrect error message.", "Operands of date type are not applicable to operation " + EgTokenCategory.DIV, ex.getMessage());
	}
    }

    @Test
    public void test_complex_expression_with_multple_parties_1() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("2 / (moneyProperty / decimalProperty) / 3.5").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	new AstWalker(ast, visitor).walk();
	assertEquals("Incorrect type.", Money.class, ast.getType());
	assertNull("Incorrect value.", ast.getValue());
    }

    @Test
    public void test_complex_expression_with_multple_parties_2() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("2.5 / (intProperty / decimalProperty) / 35").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	new AstWalker(ast, visitor).walk();
	assertEquals("Incorrect type.", BigDecimal.class, ast.getType());
	assertNull("Incorrect value.", ast.getValue());
    }

    @Test
    public void test_complex_expression_with_multple_parties_3() throws RecognitionException, SequenceRecognitionFailed, SemanticException {
	final Token[] tokens = new ExpressionLexer("2.5 / (intProperty / moneyProperty) / 35").tokenize();
	final ExpressionParser parser = new ExpressionParser(tokens);
	final AstNode ast = parser.parse();
	final TypeEnforcementVisitor visitor = new TypeEnforcementVisitor(EntityLevel1.class);
	new AstWalker(ast, visitor).walk();
	assertEquals("Incorrect type.", Money.class, ast.getType());
	assertNull("Incorrect value.", ast.getValue());
    }

}
