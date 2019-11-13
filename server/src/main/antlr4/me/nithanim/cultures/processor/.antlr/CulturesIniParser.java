// Generated from /home/nithanim/NetBeansProjects/cultures-language-server2/server/src/main/antlr4/me/nithanim/cultures/processor/CulturesIni.g4 by ANTLR 4.7.1
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CulturesIniParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, LINE_COMMENT=5, METACHAR=6, DEBUGINFO=7, 
		DIGITS=8, STRING=9, CHARS=10, WHITESPACES=11, LINEEND=12;
	public static final int
		RULE_fullfile = 0, RULE_line = 1, RULE_metaline = 2, RULE_includeline = 3, 
		RULE_defineline = 4, RULE_definename = 5, RULE_definenumber = 6, RULE_definestring = 7, 
		RULE_categoryline = 8, RULE_categoryname = 9, RULE_commandline = 10, RULE_debugline = 11, 
		RULE_commandname = 12, RULE_commandarg = 13, RULE_stringarg = 14, RULE_constantarg = 15, 
		RULE_constantname = 16, RULE_numberarg = 17;
	public static final String[] ruleNames = {
		"fullfile", "line", "metaline", "includeline", "defineline", "definename", 
		"definenumber", "definestring", "categoryline", "categoryname", "commandline", 
		"debugline", "commandname", "commandarg", "stringarg", "constantarg", 
		"constantname", "numberarg"
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

	@Override
	public String getGrammarFileName() { return "CulturesIni.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public CulturesIniParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class FullfileContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(CulturesIniParser.EOF, 0); }
		public List<LineContext> line() {
			return getRuleContexts(LineContext.class);
		}
		public LineContext line(int i) {
			return getRuleContext(LineContext.class,i);
		}
		public FullfileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fullfile; }
	}

	public final FullfileContext fullfile() throws RecognitionException {
		FullfileContext _localctx = new FullfileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_fullfile);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(37); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(36);
				line();
				}
				}
				setState(39); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__2) | (1L << METACHAR) | (1L << DEBUGINFO) | (1L << CHARS) | (1L << LINEEND))) != 0) );
			setState(41);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LineContext extends ParserRuleContext {
		public TerminalNode LINEEND() { return getToken(CulturesIniParser.LINEEND, 0); }
		public MetalineContext metaline() {
			return getRuleContext(MetalineContext.class,0);
		}
		public CategorylineContext categoryline() {
			return getRuleContext(CategorylineContext.class,0);
		}
		public DebuglineContext debugline() {
			return getRuleContext(DebuglineContext.class,0);
		}
		public CommandlineContext commandline() {
			return getRuleContext(CommandlineContext.class,0);
		}
		public LineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_line; }
	}

	public final LineContext line() throws RecognitionException {
		LineContext _localctx = new LineContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_line);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(47);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case METACHAR:
				{
				setState(43);
				metaline();
				}
				break;
			case T__2:
				{
				setState(44);
				categoryline();
				}
				break;
			case DEBUGINFO:
				{
				setState(45);
				debugline();
				}
				break;
			case CHARS:
				{
				setState(46);
				commandline();
				}
				break;
			case LINEEND:
				break;
			default:
				break;
			}
			setState(49);
			match(LINEEND);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MetalineContext extends ParserRuleContext {
		public TerminalNode METACHAR() { return getToken(CulturesIniParser.METACHAR, 0); }
		public IncludelineContext includeline() {
			return getRuleContext(IncludelineContext.class,0);
		}
		public DefinelineContext defineline() {
			return getRuleContext(DefinelineContext.class,0);
		}
		public MetalineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_metaline; }
	}

	public final MetalineContext metaline() throws RecognitionException {
		MetalineContext _localctx = new MetalineContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_metaline);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(51);
			match(METACHAR);
			setState(54);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				{
				setState(52);
				includeline();
				}
				break;
			case T__1:
				{
				setState(53);
				defineline();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IncludelineContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(CulturesIniParser.STRING, 0); }
		public IncludelineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_includeline; }
	}

	public final IncludelineContext includeline() throws RecognitionException {
		IncludelineContext _localctx = new IncludelineContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_includeline);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(56);
			match(T__0);
			setState(57);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DefinelineContext extends ParserRuleContext {
		public DefinenameContext definename() {
			return getRuleContext(DefinenameContext.class,0);
		}
		public DefinenumberContext definenumber() {
			return getRuleContext(DefinenumberContext.class,0);
		}
		public DefinestringContext definestring() {
			return getRuleContext(DefinestringContext.class,0);
		}
		public DefinelineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defineline; }
	}

	public final DefinelineContext defineline() throws RecognitionException {
		DefinelineContext _localctx = new DefinelineContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_defineline);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(59);
			match(T__1);
			setState(60);
			definename();
			setState(63);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case DIGITS:
				{
				setState(61);
				definenumber();
				}
				break;
			case STRING:
				{
				setState(62);
				definestring();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DefinenameContext extends ParserRuleContext {
		public TerminalNode CHARS() { return getToken(CulturesIniParser.CHARS, 0); }
		public DefinenameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_definename; }
	}

	public final DefinenameContext definename() throws RecognitionException {
		DefinenameContext _localctx = new DefinenameContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_definename);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(65);
			match(CHARS);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DefinenumberContext extends ParserRuleContext {
		public TerminalNode DIGITS() { return getToken(CulturesIniParser.DIGITS, 0); }
		public DefinenumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_definenumber; }
	}

	public final DefinenumberContext definenumber() throws RecognitionException {
		DefinenumberContext _localctx = new DefinenumberContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_definenumber);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(67);
			match(DIGITS);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DefinestringContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(CulturesIniParser.STRING, 0); }
		public DefinestringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_definestring; }
	}

	public final DefinestringContext definestring() throws RecognitionException {
		DefinestringContext _localctx = new DefinestringContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_definestring);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(69);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CategorylineContext extends ParserRuleContext {
		public CategorynameContext categoryname() {
			return getRuleContext(CategorynameContext.class,0);
		}
		public CategorylineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_categoryline; }
	}

	public final CategorylineContext categoryline() throws RecognitionException {
		CategorylineContext _localctx = new CategorylineContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_categoryline);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(71);
			match(T__2);
			setState(72);
			categoryname();
			setState(73);
			match(T__3);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CategorynameContext extends ParserRuleContext {
		public TerminalNode CHARS() { return getToken(CulturesIniParser.CHARS, 0); }
		public CategorynameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_categoryname; }
	}

	public final CategorynameContext categoryname() throws RecognitionException {
		CategorynameContext _localctx = new CategorynameContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_categoryname);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(75);
			match(CHARS);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CommandlineContext extends ParserRuleContext {
		public CommandnameContext commandname() {
			return getRuleContext(CommandnameContext.class,0);
		}
		public List<CommandargContext> commandarg() {
			return getRuleContexts(CommandargContext.class);
		}
		public CommandargContext commandarg(int i) {
			return getRuleContext(CommandargContext.class,i);
		}
		public CommandlineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_commandline; }
	}

	public final CommandlineContext commandline() throws RecognitionException {
		CommandlineContext _localctx = new CommandlineContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_commandline);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(77);
			commandname();
			setState(81);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << METACHAR) | (1L << DIGITS) | (1L << STRING))) != 0)) {
				{
				{
				setState(78);
				commandarg();
				}
				}
				setState(83);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DebuglineContext extends ParserRuleContext {
		public TerminalNode DEBUGINFO() { return getToken(CulturesIniParser.DEBUGINFO, 0); }
		public TerminalNode STRING() { return getToken(CulturesIniParser.STRING, 0); }
		public DebuglineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_debugline; }
	}

	public final DebuglineContext debugline() throws RecognitionException {
		DebuglineContext _localctx = new DebuglineContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_debugline);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			match(DEBUGINFO);
			setState(85);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CommandnameContext extends ParserRuleContext {
		public TerminalNode CHARS() { return getToken(CulturesIniParser.CHARS, 0); }
		public CommandnameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_commandname; }
	}

	public final CommandnameContext commandname() throws RecognitionException {
		CommandnameContext _localctx = new CommandnameContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_commandname);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(87);
			match(CHARS);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CommandargContext extends ParserRuleContext {
		public StringargContext stringarg() {
			return getRuleContext(StringargContext.class,0);
		}
		public NumberargContext numberarg() {
			return getRuleContext(NumberargContext.class,0);
		}
		public ConstantargContext constantarg() {
			return getRuleContext(ConstantargContext.class,0);
		}
		public CommandargContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_commandarg; }
	}

	public final CommandargContext commandarg() throws RecognitionException {
		CommandargContext _localctx = new CommandargContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_commandarg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(92);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING:
				{
				setState(89);
				stringarg();
				}
				break;
			case DIGITS:
				{
				setState(90);
				numberarg();
				}
				break;
			case METACHAR:
				{
				setState(91);
				constantarg();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StringargContext extends ParserRuleContext {
		public TerminalNode STRING() { return getToken(CulturesIniParser.STRING, 0); }
		public StringargContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringarg; }
	}

	public final StringargContext stringarg() throws RecognitionException {
		StringargContext _localctx = new StringargContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_stringarg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(94);
			match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstantargContext extends ParserRuleContext {
		public ConstantnameContext constantname() {
			return getRuleContext(ConstantnameContext.class,0);
		}
		public ConstantargContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constantarg; }
	}

	public final ConstantargContext constantarg() throws RecognitionException {
		ConstantargContext _localctx = new ConstantargContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_constantarg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(96);
			match(METACHAR);
			setState(97);
			constantname();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConstantnameContext extends ParserRuleContext {
		public TerminalNode CHARS() { return getToken(CulturesIniParser.CHARS, 0); }
		public ConstantnameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constantname; }
	}

	public final ConstantnameContext constantname() throws RecognitionException {
		ConstantnameContext _localctx = new ConstantnameContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_constantname);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			match(CHARS);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumberargContext extends ParserRuleContext {
		public TerminalNode DIGITS() { return getToken(CulturesIniParser.DIGITS, 0); }
		public NumberargContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_numberarg; }
	}

	public final NumberargContext numberarg() throws RecognitionException {
		NumberargContext _localctx = new NumberargContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_numberarg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(101);
			match(DIGITS);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\16j\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4"+
		"\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22\4\23"+
		"\t\23\3\2\6\2(\n\2\r\2\16\2)\3\2\3\2\3\3\3\3\3\3\3\3\5\3\62\n\3\3\3\3"+
		"\3\3\4\3\4\3\4\5\49\n\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\5\6B\n\6\3\7\3\7\3"+
		"\b\3\b\3\t\3\t\3\n\3\n\3\n\3\n\3\13\3\13\3\f\3\f\7\fR\n\f\f\f\16\fU\13"+
		"\f\3\r\3\r\3\r\3\16\3\16\3\17\3\17\3\17\5\17_\n\17\3\20\3\20\3\21\3\21"+
		"\3\21\3\22\3\22\3\23\3\23\3\23\2\2\24\2\4\6\b\n\f\16\20\22\24\26\30\32"+
		"\34\36 \"$\2\2\2a\2\'\3\2\2\2\4\61\3\2\2\2\6\65\3\2\2\2\b:\3\2\2\2\n="+
		"\3\2\2\2\fC\3\2\2\2\16E\3\2\2\2\20G\3\2\2\2\22I\3\2\2\2\24M\3\2\2\2\26"+
		"O\3\2\2\2\30V\3\2\2\2\32Y\3\2\2\2\34^\3\2\2\2\36`\3\2\2\2 b\3\2\2\2\""+
		"e\3\2\2\2$g\3\2\2\2&(\5\4\3\2\'&\3\2\2\2()\3\2\2\2)\'\3\2\2\2)*\3\2\2"+
		"\2*+\3\2\2\2+,\7\2\2\3,\3\3\2\2\2-\62\5\6\4\2.\62\5\22\n\2/\62\5\30\r"+
		"\2\60\62\5\26\f\2\61-\3\2\2\2\61.\3\2\2\2\61/\3\2\2\2\61\60\3\2\2\2\61"+
		"\62\3\2\2\2\62\63\3\2\2\2\63\64\7\16\2\2\64\5\3\2\2\2\658\7\b\2\2\669"+
		"\5\b\5\2\679\5\n\6\28\66\3\2\2\28\67\3\2\2\29\7\3\2\2\2:;\7\3\2\2;<\7"+
		"\13\2\2<\t\3\2\2\2=>\7\4\2\2>A\5\f\7\2?B\5\16\b\2@B\5\20\t\2A?\3\2\2\2"+
		"A@\3\2\2\2B\13\3\2\2\2CD\7\f\2\2D\r\3\2\2\2EF\7\n\2\2F\17\3\2\2\2GH\7"+
		"\13\2\2H\21\3\2\2\2IJ\7\5\2\2JK\5\24\13\2KL\7\6\2\2L\23\3\2\2\2MN\7\f"+
		"\2\2N\25\3\2\2\2OS\5\32\16\2PR\5\34\17\2QP\3\2\2\2RU\3\2\2\2SQ\3\2\2\2"+
		"ST\3\2\2\2T\27\3\2\2\2US\3\2\2\2VW\7\t\2\2WX\7\13\2\2X\31\3\2\2\2YZ\7"+
		"\f\2\2Z\33\3\2\2\2[_\5\36\20\2\\_\5$\23\2]_\5 \21\2^[\3\2\2\2^\\\3\2\2"+
		"\2^]\3\2\2\2_\35\3\2\2\2`a\7\13\2\2a\37\3\2\2\2bc\7\b\2\2cd\5\"\22\2d"+
		"!\3\2\2\2ef\7\f\2\2f#\3\2\2\2gh\7\n\2\2h%\3\2\2\2\b)\618AS^";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}