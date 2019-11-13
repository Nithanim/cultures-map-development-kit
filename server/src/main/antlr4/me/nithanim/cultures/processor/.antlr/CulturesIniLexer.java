// Generated from /home/nithanim/NetBeansProjects/cultures-language-server2/server/src/main/antlr4/me/nithanim/cultures/processor/CulturesIni.g4 by ANTLR 4.7.1
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CulturesIniLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, LINE_COMMENT=5, METACHAR=6, DEBUGINFO=7, 
		DIGITS=8, STRING=9, CHARS=10, WHITESPACES=11, LINEEND=12;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "LINE_COMMENT", "METACHAR", "DEBUGINFO", 
		"DIGITS", "STRING", "CHARS", "WHITESPACES", "WHITESPACE", "LINEEND", "NEWLINE"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'include'", "'define'", "'['", "']'", null, "'#'", "'debuginfo'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, "LINE_COMMENT", "METACHAR", "DEBUGINFO", 
		"DIGITS", "STRING", "CHARS", "WHITESPACES", "LINEEND"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public CulturesIniLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CulturesIni.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\16z\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\6\3\6\7\6\67"+
		"\n\6\f\6\16\6:\13\6\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\3\t\3\t\3\t\7\tM\n\t\f\t\16\tP\13\t\5\tR\n\t\3\n\3\n\6\nV\n\n\r"+
		"\n\16\nW\3\n\3\n\3\13\6\13]\n\13\r\13\16\13^\3\f\6\fb\n\f\r\f\16\fc\3"+
		"\f\3\f\3\r\3\r\3\16\6\16k\n\16\r\16\16\16l\3\16\5\16p\n\16\3\17\5\17s"+
		"\n\17\3\17\3\17\6\17w\n\17\r\17\16\17x\2\2\20\3\3\5\4\7\5\t\6\13\7\r\b"+
		"\17\t\21\n\23\13\25\f\27\r\31\2\33\16\35\2\3\2\6\4\2\f\f\17\17\3\2$$\5"+
		"\2C\\aac|\4\2\13\13\"\"\2\u0082\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2"+
		"\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2"+
		"\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\33\3\2\2\2\3\37\3\2\2\2\5\'\3\2\2\2\7"+
		".\3\2\2\2\t\60\3\2\2\2\13\62\3\2\2\2\r=\3\2\2\2\17?\3\2\2\2\21Q\3\2\2"+
		"\2\23S\3\2\2\2\25\\\3\2\2\2\27a\3\2\2\2\31g\3\2\2\2\33o\3\2\2\2\35v\3"+
		"\2\2\2\37 \7k\2\2 !\7p\2\2!\"\7e\2\2\"#\7n\2\2#$\7w\2\2$%\7f\2\2%&\7g"+
		"\2\2&\4\3\2\2\2\'(\7f\2\2()\7g\2\2)*\7h\2\2*+\7k\2\2+,\7p\2\2,-\7g\2\2"+
		"-\6\3\2\2\2./\7]\2\2/\b\3\2\2\2\60\61\7_\2\2\61\n\3\2\2\2\62\63\7\61\2"+
		"\2\63\64\7\61\2\2\648\3\2\2\2\65\67\n\2\2\2\66\65\3\2\2\2\67:\3\2\2\2"+
		"8\66\3\2\2\289\3\2\2\29;\3\2\2\2:8\3\2\2\2;<\b\6\2\2<\f\3\2\2\2=>\7%\2"+
		"\2>\16\3\2\2\2?@\7f\2\2@A\7g\2\2AB\7d\2\2BC\7w\2\2CD\7i\2\2DE\7k\2\2E"+
		"F\7p\2\2FG\7h\2\2GH\7q\2\2H\20\3\2\2\2IR\4\62;\2JN\4\63;\2KM\4\62;\2L"+
		"K\3\2\2\2MP\3\2\2\2NL\3\2\2\2NO\3\2\2\2OR\3\2\2\2PN\3\2\2\2QI\3\2\2\2"+
		"QJ\3\2\2\2R\22\3\2\2\2SU\7$\2\2TV\n\3\2\2UT\3\2\2\2VW\3\2\2\2WU\3\2\2"+
		"\2WX\3\2\2\2XY\3\2\2\2YZ\7$\2\2Z\24\3\2\2\2[]\t\4\2\2\\[\3\2\2\2]^\3\2"+
		"\2\2^\\\3\2\2\2^_\3\2\2\2_\26\3\2\2\2`b\5\31\r\2a`\3\2\2\2bc\3\2\2\2c"+
		"a\3\2\2\2cd\3\2\2\2de\3\2\2\2ef\b\f\2\2f\30\3\2\2\2gh\t\5\2\2h\32\3\2"+
		"\2\2ik\5\35\17\2ji\3\2\2\2kl\3\2\2\2lj\3\2\2\2lm\3\2\2\2mp\3\2\2\2np\7"+
		"\2\2\3oj\3\2\2\2on\3\2\2\2p\34\3\2\2\2qs\7\17\2\2rq\3\2\2\2rs\3\2\2\2"+
		"st\3\2\2\2tw\7\f\2\2uw\7\17\2\2vr\3\2\2\2vu\3\2\2\2wx\3\2\2\2xv\3\2\2"+
		"\2xy\3\2\2\2y\36\3\2\2\2\16\28NQW^clorvx\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}