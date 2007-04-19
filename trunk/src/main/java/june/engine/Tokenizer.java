package june.engine;

import java.util.*;

import june.tree.*;

import static june.tree.TokenType.*;

public class Tokenizer {

	private static final Map<String, TokenType> KEYWORDS = buildKeywords();

	private static Map<String, TokenType> buildKeywords() {
		Map<String, TokenType> keywords = new HashMap<String, TokenType>();
		keywords.put("annotation", ANNOTATION);
		keywords.put("class", CLASS);
		keywords.put("def", DEF);
		keywords.put("do", DO);
		keywords.put("import", IMPORT);
		keywords.put("interface", INTERFACE);
		keywords.put("return", RETURN);
		keywords.put("var", VAR);
		keywords.put("with", WITH);
		return Collections.unmodifiableMap(keywords);
	}

	private int charIndex;

	private int current;

	private int index;

	private int line;

	private int lineIndex;

	private CharSequence source;

	private int tokenCharIndex;

	private int tokenIndex;

	// private void back() {
	// if (index > 0) {
	// index--;
	// charIndex += Character.offsetByCodePoints(source, charIndex, -1);
	// }
	// }

	private ArrayList<Token> tokens;

	private void assignOrEquals() {
		if (next() == '=') {
			nextToken(EQUALS);
		} else {
			token(ASSIGN);
		}
	}

	private void comment() {
		COMMENT_CHARS: while (hasNext()) {
			next();
			if (isEndLine(current)) {
				break COMMENT_CHARS;
			}
			// Chew through.
		}
		token(COMMENT);
	}

	private void dot() {
		if (next() == '.') {
			if (next() == '.') {
				nextToken(ELLIPSIS);
			} else {
				token(ERROR);
			}
		} else {
			token(DOT);
		}
	}

	private void endLine() {
		if (next() == '\n') {
			next();
		}
	}

	private void error() {
		// TODO Group error chars together where possible.
		next();
		token(ERROR);
	}

	private boolean hasNext() {
		return charIndex < source.length();
	}

	private void identifier() {
		while (isIdentifierPart(next())) {
			// Just chew through
		}
		token(ID);
	}

	private boolean isEndLine(int c) {
		return c == '\r' || c == '\n';
	}

	private boolean isIdentifierPart(int i) {
		return ('0' <= i && i <= '9') || ('A' <= i && i <= 'Z')
				|| ('a' <= i && i <= 'z') || i == '_';
	}

	private boolean isWhitespace(int i) {
		return i == '\t' || i == ' ';
	}

	private int next() {
		if (index < 0) {
			index++;
			charIndex++;
		} else if (charIndex < source.length()) {
			index++;
			charIndex = Character.offsetByCodePoints(source, charIndex, 1);
		}
		current = hasNext() ? Character.codePointAt(source, charIndex) : -1;
		return current;
	}

	private void nextToken(TokenType type) {
		next();
		token(type);
	}

	/**
	 * TODO Support string interpolation by having different token types inside of strings?
	 */
	private void string() {
		int start = current;
		STRING_CHARS: while (hasNext()) {
			next();
			if (isEndLine(current)) {
				break STRING_CHARS;
			} else if (current == start) {
				next();
				break STRING_CHARS;
			}
			// Chew through.
			// TODO Support escapes.
		}
		token(STRING);
	}

	private void token(TokenType type) {
		CharSequence text = source.subSequence(tokenCharIndex, charIndex);
		if (type == ID) {
			// See if it's really a keyword.
			TokenType keyword = KEYWORDS.get(text.toString());
			if (keyword != null) {
				type = keyword;
			}
		}
		Token token = new Token(type, line, tokenIndex - lineIndex + 1, text);
		tokens.add(token);
		tokenIndex = index;
		tokenCharIndex = charIndex;
		if (type == END_LINE) {
			line++;
			lineIndex = index;
		}
	}

	/**
	 * Tokenize the source, and return the list of tokens. This method is not thread safe.
	 * 
	 * @param source
	 * @return
	 */
	public List<Token> tokenize(CharSequence source) {
		tokens = new ArrayList<Token>();
		this.source = source;
		index = -1;
		charIndex = -1;
		next();
		line = 1;
		lineIndex = tokenIndex = tokenCharIndex = 0;
		TOKENS: while (true) {
			switch (current) {
				case ' ':
				case '\t':
					whitespace();
					break;
				case '\n':
					nextToken(END_LINE);
					break;
				case '\r':
					endLine();
					break;
				case '\'':
					string();
					break;
				case '#':
					comment();
					break;
				case '.':
					dot();
					break;
				case ':':
					nextToken(COLON);
					break;
				case ';':
					nextToken(SEMICOLON);
					break;
				case ',':
					nextToken(COMMA);
					break;
				case '{':
					nextToken(OPEN_BRACE);
					break;
				case '}':
					nextToken(CLOSE_BRACE);
					break;
				case '[':
					nextToken(OPEN_BRACKET);
					break;
				case ']':
					nextToken(CLOSE_BRACKET);
					break;
				case '(':
					nextToken(OPEN_PAREN);
					break;
				case ')':
					nextToken(CLOSE_PAREN);
					break;
				case '=':
					assignOrEquals();
					break;
				case '$':
					next();
					identifier();
					break;
				default:
					if (!hasNext()) {
						break TOKENS;
					} else if (isIdentifierPart(current)) {
						identifier();
					} else {
						error();
					}
					break;
			}
		}
		if (index != tokenIndex) {
			token(ERROR);
		}
		token(END_FILE);
		return tokens;
	}

	private void whitespace() {
		while (isWhitespace(next())) {
			// Just chew through it.
		}
		token(WHITESPACE);
	}

}
