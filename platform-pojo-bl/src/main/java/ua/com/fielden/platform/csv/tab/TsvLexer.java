package ua.com.fielden.platform.csv.tab;

// $ANTLR 3.2 Sep 23, 2009 12:02:23 /home/oleh/ANTLR/Tsv.g 2011-02-25 13:16:49

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.BaseRecognizer;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.DFA;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.IntStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RecognizerSharedState;

public class TsvLexer extends Lexer {
    public static final int NEWLINE = 4;
    public static final int QUOTED = 6;
    public static final int COMMA = 5;
    public static final int EOF = -1;
    public static final int UNQUOTED = 7;

    List<RecognitionException> exceptions = new ArrayList<RecognitionException>();

    public List<RecognitionException> getExceptions() {
	return exceptions;
    }

    @Override
    public void reportError(final RecognitionException e) {
	super.reportError(e);
	exceptions.add(e);
    }

    // delegates
    // delegators

    public TsvLexer() {
	;
    }

    public TsvLexer(final CharStream input) {
	this(input, new RecognizerSharedState());
    }

    public TsvLexer(final CharStream input, final RecognizerSharedState state) {
	super(input, state);

    }

    @Override
    public String getGrammarFileName() {
	return "/home/oleh/ANTLR/Tsv.g";
    }

    // $ANTLR start "NEWLINE"
    public final void mNEWLINE() throws RecognitionException {
	try {
	    final int _type = NEWLINE;
	    final int _channel = DEFAULT_TOKEN_CHANNEL;
	    // /home/oleh/ANTLR/Tsv.g:37:9: ( ( '\\r' )? '\\n' )
	    // /home/oleh/ANTLR/Tsv.g:37:11: ( '\\r' )? '\\n'
	    {
		// /home/oleh/ANTLR/Tsv.g:37:11: ( '\\r' )?
		int alt1 = 2;
		final int LA1_0 = input.LA(1);

		if ((LA1_0 == '\r')) {
		    alt1 = 1;
		}
		switch (alt1) {
		case 1:
		    // /home/oleh/ANTLR/Tsv.g:37:11: '\\r'
		{
		    match('\r');

		}
		    break;

		}

		match('\n');

	    }

	    state.type = _type;
	    state.channel = _channel;
	} finally {
	}
    }

    // $ANTLR end "NEWLINE"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
	try {
	    final int _type = COMMA;
	    final int _channel = DEFAULT_TOKEN_CHANNEL;
	    // /home/oleh/ANTLR/Tsv.g:39:7: ( ( ( ' ' )* '\\t' ( ' ' )* ) )
	    // /home/oleh/ANTLR/Tsv.g:39:9: ( ( ' ' )* '\\t' ( ' ' )* )
	    {
		// /home/oleh/ANTLR/Tsv.g:39:9: ( ( ' ' )* '\\t' ( ' ' )* )
		// /home/oleh/ANTLR/Tsv.g:39:11: ( ' ' )* '\\t' ( ' ' )*
		{
		    // /home/oleh/ANTLR/Tsv.g:39:11: ( ' ' )*
		    loop2: do {
			int alt2 = 2;
			final int LA2_0 = input.LA(1);

			if ((LA2_0 == ' ')) {
			    alt2 = 1;
			}

			switch (alt2) {
			case 1:
			    // /home/oleh/ANTLR/Tsv.g:39:11: ' '
			{
			    match(' ');

			}
			    break;

			default:
			    break loop2;
			}
		    } while (true);

		    match('\t');
		    // /home/oleh/ANTLR/Tsv.g:39:21: ( ' ' )*
		    loop3: do {
			int alt3 = 2;
			final int LA3_0 = input.LA(1);

			if ((LA3_0 == ' ')) {
			    alt3 = 1;
			}

			switch (alt3) {
			case 1:
			    // /home/oleh/ANTLR/Tsv.g:39:21: ' '
			{
			    match(' ');

			}
			    break;

			default:
			    break loop3;
			}
		    } while (true);

		}

	    }

	    state.type = _type;
	    state.channel = _channel;
	} finally {
	}
    }

    // $ANTLR end "COMMA"

    // $ANTLR start "QUOTED"
    public final void mQUOTED() throws RecognitionException {
	try {
	    final int _type = QUOTED;
	    final int _channel = DEFAULT_TOKEN_CHANNEL;
	    // /home/oleh/ANTLR/Tsv.g:41:8: ( ( '\"' ( options {greedy=false; } : . )+ '\"' )+ )
	    // /home/oleh/ANTLR/Tsv.g:41:10: ( '\"' ( options {greedy=false; } : . )+ '\"' )+
	    {
		// /home/oleh/ANTLR/Tsv.g:41:10: ( '\"' ( options {greedy=false; } : . )+ '\"' )+
		int cnt5 = 0;
		loop5: do {
		    int alt5 = 2;
		    final int LA5_0 = input.LA(1);

		    if ((LA5_0 == '\"')) {
			alt5 = 1;
		    }

		    switch (alt5) {
		    case 1:
			// /home/oleh/ANTLR/Tsv.g:41:11: '\"' ( options {greedy=false; } : . )+ '\"'
		    {
			match('\"');
			// /home/oleh/ANTLR/Tsv.g:41:15: ( options {greedy=false; } : . )+
			int cnt4 = 0;
			loop4: do {
			    int alt4 = 2;
			    final int LA4_0 = input.LA(1);

			    if ((LA4_0 == '\"')) {
				alt4 = 2;
			    } else if (((LA4_0 >= '\u0000' && LA4_0 <= '!') || (LA4_0 >= '#' && LA4_0 <= '\uFFFF'))) {
				alt4 = 1;
			    }

			    switch (alt4) {
			    case 1:
				// /home/oleh/ANTLR/Tsv.g:41:42: .
			    {
				matchAny();

			    }
				break;

			    default:
				if (cnt4 >= 1)
				    break loop4;
				final EarlyExitException eee = new EarlyExitException(4, input);
				throw eee;
			    }
			    cnt4++;
			} while (true);

			match('\"');

		    }
			break;

		    default:
			if (cnt5 >= 1)
			    break loop5;
			final EarlyExitException eee = new EarlyExitException(5, input);
			throw eee;
		    }
		    cnt5++;
		} while (true);

		final StringBuffer txt = new StringBuffer(getText());
		// Remove first and last double-quote
		txt.deleteCharAt(0);
		txt.deleteCharAt(txt.length() - 1);
		// "" -> "
		int probe;
		while ((probe = txt.lastIndexOf("\"\"")) >= 0) {
		    txt.deleteCharAt(probe);
		}
		setText(txt.toString());

	    }

	    state.type = _type;
	    state.channel = _channel;
	} finally {
	}
    }

    // $ANTLR end "QUOTED"

    // $ANTLR start "UNQUOTED"
    public final void mUNQUOTED() throws RecognitionException {
	try {
	    final int _type = UNQUOTED;
	    final int _channel = DEFAULT_TOKEN_CHANNEL;
	    // /home/oleh/ANTLR/Tsv.g:57:2: ( (~ ( '\\r' | '\\n' | '\\t' ) )+ )
	    // /home/oleh/ANTLR/Tsv.g:57:4: (~ ( '\\r' | '\\n' | '\\t' ) )+
	    {
		// /home/oleh/ANTLR/Tsv.g:57:4: (~ ( '\\r' | '\\n' | '\\t' ) )+
		int cnt6 = 0;
		loop6: do {
		    int alt6 = 2;
		    final int LA6_0 = input.LA(1);

		    if (((LA6_0 >= '\u0000' && LA6_0 <= '\b') || (LA6_0 >= '\u000B' && LA6_0 <= '\f') || (LA6_0 >= '\u000E' && LA6_0 <= '\uFFFF'))) {
			alt6 = 1;
		    }

		    switch (alt6) {
		    case 1:
			// /home/oleh/ANTLR/Tsv.g:57:4: ~ ( '\\r' | '\\n' | '\\t' )
		    {
			if ((input.LA(1) >= '\u0000' && input.LA(1) <= '\b') || (input.LA(1) >= '\u000B' && input.LA(1) <= '\f')
				|| (input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF')) {
			    input.consume();

			} else {
			    final MismatchedSetException mse = new MismatchedSetException(null, input);
			    recover(mse);
			    throw mse;
			}

		    }
			break;

		    default:
			if (cnt6 >= 1)
			    break loop6;
			final EarlyExitException eee = new EarlyExitException(6, input);
			throw eee;
		    }
		    cnt6++;
		} while (true);

	    }

	    state.type = _type;
	    state.channel = _channel;
	} finally {
	}
    }

    // $ANTLR end "UNQUOTED"

    @Override
    public void mTokens() throws RecognitionException {
	// /home/oleh/ANTLR/Tsv.g:1:8: ( NEWLINE | COMMA | QUOTED | UNQUOTED )
	int alt7 = 4;
	alt7 = dfa7.predict(input);
	switch (alt7) {
	case 1:
	    // /home/oleh/ANTLR/Tsv.g:1:10: NEWLINE
	{
	    mNEWLINE();

	}
	    break;
	case 2:
	    // /home/oleh/ANTLR/Tsv.g:1:18: COMMA
	{
	    mCOMMA();

	}
	    break;
	case 3:
	    // /home/oleh/ANTLR/Tsv.g:1:24: QUOTED
	{
	    mQUOTED();

	}
	    break;
	case 4:
	    // /home/oleh/ANTLR/Tsv.g:1:31: UNQUOTED
	{
	    mUNQUOTED();

	}
	    break;

	}

    }

    protected DFA7 dfa7 = new DFA7(this);
    static final String DFA7_eotS = "\2\uffff\1\5\1\uffff\1\5\1\uffff\1\5\1\uffff\2\7";
    static final String DFA7_eofS = "\12\uffff";
    static final String DFA7_minS = "\1\0\1\uffff\1\11\1\uffff\1\0\1\uffff\1\0\1\uffff\2\0";
    static final String DFA7_maxS = "\1\uffff\1\uffff\1\40\1\uffff\1\uffff\1\uffff\1\uffff\1\uffff\2" + "\uffff";
    static final String DFA7_acceptS = "\1\uffff\1\1\1\uffff\1\2\1\uffff\1\4\1\uffff\1\3\2\uffff";
    static final String DFA7_specialS = "\1\2\3\uffff\1\3\1\uffff\1\0\1\uffff\1\4\1\1}>";
    static final String[] DFA7_transitionS = { "\11\5\1\3\1\1\2\5\1\1\22\5\1\2\1\5\1\4\uffdd\5", "", "\1\3\26\uffff\1\2", "", "\11\6\2\7\2\6\1\7\ufff2\6", "",
	    "\11\6\2\7\2\6\1\7\24\6\1\10\uffdd\6", "", "\11\6\2\uffff\2\6\1\uffff\24\6\1\11\uffdd\6", "\11\6\2\uffff\2\6\1\uffff\24\6\1\11\uffdd\6" };

    static final short[] DFA7_eot = DFA.unpackEncodedString(DFA7_eotS);
    static final short[] DFA7_eof = DFA.unpackEncodedString(DFA7_eofS);
    static final char[] DFA7_min = DFA.unpackEncodedStringToUnsignedChars(DFA7_minS);
    static final char[] DFA7_max = DFA.unpackEncodedStringToUnsignedChars(DFA7_maxS);
    static final short[] DFA7_accept = DFA.unpackEncodedString(DFA7_acceptS);
    static final short[] DFA7_special = DFA.unpackEncodedString(DFA7_specialS);
    static final short[][] DFA7_transition;

    static {
	final int numStates = DFA7_transitionS.length;
	DFA7_transition = new short[numStates][];
	for (int i = 0; i < numStates; i++) {
	    DFA7_transition[i] = DFA.unpackEncodedString(DFA7_transitionS[i]);
	}
    }

    class DFA7 extends DFA {

	public DFA7(final BaseRecognizer recognizer) {
	    this.recognizer = recognizer;
	    this.decisionNumber = 7;
	    this.eot = DFA7_eot;
	    this.eof = DFA7_eof;
	    this.min = DFA7_min;
	    this.max = DFA7_max;
	    this.accept = DFA7_accept;
	    this.special = DFA7_special;
	    this.transition = DFA7_transition;
	}

	@Override
	public String getDescription() {
	    return "1:1: Tokens : ( NEWLINE | COMMA | QUOTED | UNQUOTED );";
	}

	@Override
	public int specialStateTransition(int s, final IntStream _input) throws NoViableAltException {
	    final IntStream input = _input;
	    final int _s = s;
	    switch (s) {
	    case 0:
		final int LA7_6 = input.LA(1);

		s = -1;
		if ((LA7_6 == '\"')) {
		    s = 8;
		}

		else if (((LA7_6 >= '\u0000' && LA7_6 <= '\b') || (LA7_6 >= '\u000B' && LA7_6 <= '\f') || (LA7_6 >= '\u000E' && LA7_6 <= '!') || (LA7_6 >= '#' && LA7_6 <= '\uFFFF'))) {
		    s = 6;
		}

		else if (((LA7_6 >= '\t' && LA7_6 <= '\n') || LA7_6 == '\r')) {
		    s = 7;
		}

		else
		    s = 5;

		if (s >= 0)
		    return s;
		break;
	    case 1:
		final int LA7_9 = input.LA(1);

		s = -1;
		if ((LA7_9 == '\"')) {
		    s = 9;
		}

		else if (((LA7_9 >= '\u0000' && LA7_9 <= '\b') || (LA7_9 >= '\u000B' && LA7_9 <= '\f') || (LA7_9 >= '\u000E' && LA7_9 <= '!') || (LA7_9 >= '#' && LA7_9 <= '\uFFFF'))) {
		    s = 6;
		}

		else
		    s = 7;

		if (s >= 0)
		    return s;
		break;
	    case 2:
		final int LA7_0 = input.LA(1);

		s = -1;
		if ((LA7_0 == '\n' || LA7_0 == '\r')) {
		    s = 1;
		}

		else if ((LA7_0 == ' ')) {
		    s = 2;
		}

		else if ((LA7_0 == '\t')) {
		    s = 3;
		}

		else if ((LA7_0 == '\"')) {
		    s = 4;
		}

		else if (((LA7_0 >= '\u0000' && LA7_0 <= '\b') || (LA7_0 >= '\u000B' && LA7_0 <= '\f') || (LA7_0 >= '\u000E' && LA7_0 <= '\u001F') || LA7_0 == '!' || (LA7_0 >= '#' && LA7_0 <= '\uFFFF'))) {
		    s = 5;
		}

		if (s >= 0)
		    return s;
		break;
	    case 3:
		final int LA7_4 = input.LA(1);

		s = -1;
		if (((LA7_4 >= '\u0000' && LA7_4 <= '\b') || (LA7_4 >= '\u000B' && LA7_4 <= '\f') || (LA7_4 >= '\u000E' && LA7_4 <= '\uFFFF'))) {
		    s = 6;
		}

		else if (((LA7_4 >= '\t' && LA7_4 <= '\n') || LA7_4 == '\r')) {
		    s = 7;
		}

		else
		    s = 5;

		if (s >= 0)
		    return s;
		break;
	    case 4:
		final int LA7_8 = input.LA(1);

		s = -1;
		if ((LA7_8 == '\"')) {
		    s = 9;
		}

		else if (((LA7_8 >= '\u0000' && LA7_8 <= '\b') || (LA7_8 >= '\u000B' && LA7_8 <= '\f') || (LA7_8 >= '\u000E' && LA7_8 <= '!') || (LA7_8 >= '#' && LA7_8 <= '\uFFFF'))) {
		    s = 6;
		}

		else
		    s = 7;

		if (s >= 0)
		    return s;
		break;
	    }
	    final NoViableAltException nvae = new NoViableAltException(getDescription(), 7, _s, input);
	    error(nvae);
	    throw nvae;
	}
    }

}