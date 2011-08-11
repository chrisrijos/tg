package ua.com.fielden.platform.expression;

import java.util.ArrayList;
import java.util.List;

import ua.com.fielden.platform.expression.automata.SequenceRecognitionFailed;
import ua.com.fielden.platform.expression.lexer.BaseNonDeterministicAutomata;
import ua.com.fielden.platform.expression.lexer.avg.AvgTokenAutomata;
import ua.com.fielden.platform.expression.lexer.comma.CommaTokenAutomata;
import ua.com.fielden.platform.expression.lexer.date_constant.DateConstantTokenAutomata;
import ua.com.fielden.platform.expression.lexer.decimal.DecimalTokenAutomata;
import ua.com.fielden.platform.expression.lexer.div.DivTokenAutomata;
import ua.com.fielden.platform.expression.lexer.integer.IntegerTokenAutomata;
import ua.com.fielden.platform.expression.lexer.lparen.LparenTokenAutomata;
import ua.com.fielden.platform.expression.lexer.minus.MinusTokenAutomata;
import ua.com.fielden.platform.expression.lexer.mult.MultTokenAutomata;
import ua.com.fielden.platform.expression.lexer.name.NameTokenAutomata;
import ua.com.fielden.platform.expression.lexer.plus.PlusTokenAutomata;
import ua.com.fielden.platform.expression.lexer.rparen.RparenTokenAutomata;
import ua.com.fielden.platform.expression.lexer.string.StringTokenAutomata;
import ua.com.fielden.platform.expression.lexer.sum.SumTokenAutomata;


/**
 * A lexer to scan input for expression language tokens.
 */
public class ExpressionLexer {

    public static final char EOF = (char) -1; //  represent end of file char
    public static final int EOF_TYPE = 1; //  represent EOF token type
    protected String input; // input string
    protected int curPosition = 0; // index into input of current character
    protected char currChar; // current character

    private static final BaseNonDeterministicAutomata[] tokenLexers = {//
	new LparenTokenAutomata(), new RparenTokenAutomata(), new CommaTokenAutomata(), //
	new PlusTokenAutomata(), new MinusTokenAutomata(), new MultTokenAutomata(), new DivTokenAutomata(), //
	new AvgTokenAutomata(), new SumTokenAutomata(),//
	new NameTokenAutomata(),//
	new StringTokenAutomata(),//
	new DateConstantTokenAutomata(), new DecimalTokenAutomata(), new IntegerTokenAutomata()};

    public ExpressionLexer(final String input) {
	this.input = input;
    }

    /**
     * Produces the next in the input text token.
     *
     * @return
     * @throws SequenceRecognitionFailed
     */
    public Token nextToken() throws SequenceRecognitionFailed {
	while (currChar != EOF) {
	    return  predict(input.substring(curPosition));
	}
	return new Token(EgTokenCategory.EOF, "<EOF>", input.length(), input.length());
    }

    /**
     * Tokenizes the input into tokens (lexemes). Does not include the EOF token indicating the end of the input.
     *
     * @return
     * @throws SequenceRecognitionFailed
     */
    public Token[] tokenize() throws SequenceRecognitionFailed {
	final List<Token> tokens = new ArrayList<Token>();
	Token token = nextToken();
	while (token.category.getIndex() != EgTokenCategory.EOF.index) {
	    tokens.add(token);
	    token = nextToken();
	}
	return tokens.toArray(new Token[]{});
    }

    private Token predict(final String substring) throws SequenceRecognitionFailed {
	SequenceRecognitionFailed error = null;
	String lastPretendant = "";

	BaseNonDeterministicAutomata tokenLexer = null;
	int index = 0;
	while (index < tokenLexers.length) {
	    try {
		tokenLexer = tokenLexers[index];
		final String tokenText = tokenLexer.recognisePartiallyFromStart(substring, curPosition);
		final int prevPosition = curPosition;
		curPosition += tokenLexer.getCharsRecognised();
		if (curPosition >= input.length()) {
		    currChar = EOF;
		}
		return new Token(tokenLexer.lexemeCat, tokenText, prevPosition, curPosition); // prevPosition + tokenLexer.getCharsRecognised()
	    } catch (final SequenceRecognitionFailed e) {
		if (tokenLexer.getPretendantSequence().toString().length() > lastPretendant.length()) {
		    lastPretendant = tokenLexer.getPretendantSequence().toString();
		    error = e;
		}
	    } finally {
		index++;
	    }
	}
	throw (error != null ? error : new SequenceRecognitionFailed("Unrecognisable symbol at position " + curPosition, curPosition));
    }
 }