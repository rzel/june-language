package june.engine;

import static june.tree.TokenType.*;

import java.util.*;

import june.tree.*;

@SuppressWarnings("unchecked")
public class Parser {

	private static final EnumSet<TokenType> ANY;

	private static final EnumSet<TokenType> ANY_BUT_FILL;

	private static final EnumSet<TokenType> ANY_BUT_LINEAR_FILL;

	private static final EnumSet<TokenType> ASSIGN_COLON_ID_OR_STRONG_END;

	private static final EnumSet<TokenType> ASSIGN_COLON_OR_ID;

	private static final EnumSet<TokenType> ASSIGN_COLON_OR_SOFT_END;

	private static final EnumSet<TokenType> ID_OPEN_BRACE_OPEN_PAREN_OR_STRONG_END;

	private static final EnumSet<TokenType> ID_OR_STRONG_END;

	private static final EnumSet<TokenType> OPEN_BRACE_OPEN_PAREN_OR_STRONG_END;

	private static final EnumSet<TokenType> SOFT_END;

	private static final EnumSet<TokenType> STRONG_END;

	private static final EnumSet<TokenType> WHITE;

	static {
		// First round.
		ANY = EnumSet.allOf(TokenType.class);
		STRONG_END = EnumSet.of(CLOSE_BRACE, END_FILE, SEMICOLON);
		WHITE = EnumSet.of(END_LINE, WHITESPACE);
		// Second round.
		ANY_BUT_FILL = EnumSet.complementOf(union(WHITE, EnumSet.of(COMMENT)));
		ASSIGN_COLON_OR_ID = EnumSet.of(ASSIGN, COLON, ID);
		OPEN_BRACE_OPEN_PAREN_OR_STRONG_END =
				union(EnumSet.of(OPEN_BRACE, OPEN_PAREN), STRONG_END);
		ID_OR_STRONG_END = union(EnumSet.of(ID), STRONG_END);
		SOFT_END = union(STRONG_END, EnumSet.of(END_LINE));
		// Third round.
		ANY_BUT_LINEAR_FILL = union(ANY_BUT_FILL, EnumSet.of(END_LINE));
		ASSIGN_COLON_OR_SOFT_END = union(SOFT_END, EnumSet.of(ASSIGN, COLON));
		ASSIGN_COLON_ID_OR_STRONG_END = union(ASSIGN_COLON_OR_ID, STRONG_END);
		ID_OPEN_BRACE_OPEN_PAREN_OR_STRONG_END =
				union(EnumSet.of(ID), OPEN_BRACE_OPEN_PAREN_OR_STRONG_END);
	}

	private static <Member extends Enum<Member>> EnumSet<Member> union(
			EnumSet<Member> first,
			EnumSet<Member>... rest) {
		EnumSet<Member> union = EnumSet.copyOf(first);
		for (Set<Member> set: rest) {
			union.addAll(set);
		}
		return union;
	}

	private int index;

	private Parent node;

	private List<Token> pending;

	private List<Token> pendingWhite;

	private Token token;

	private List<Token> tokens;

	private void addPending() {
		node.kids.addAll(pending);
		pending.clear();
	}

	private void addWhiteToPending() {
		pending.addAll(pendingWhite);
		pendingWhite.clear();
	}

	private void args() {
		push(new Args());
		try {
			next();
			while (!at(CLOSE_PAREN) && !at(END_FILE)) {
				push(new Arg());
				try {
					// TODO Do we really need Arg nodes? Leave for now for easier reading of results.
					if (!at(COMMA)) {
						// TODO Do unterminated strings parse as one expression until terminated?
						expression();
					}
					if (at(COMMA) || at(END_LINE)) {
						next();
					} else if (!at(CLOSE_PAREN)) {
						// TODO Mark error.
					}
				} finally {
					pop();
				}
			}
			// TODO Do end of file (except when expected) as an exception on purpose to simplify the logic?
			if (!at(END_FILE)) {
				next(ANY_BUT_LINEAR_FILL);
			}
		} finally {
			pop();
		}
	}

	private void assignment() {
		next();
		push(new AssignedValue());
		try {
			expression();
		} finally {
			pop();
		}
	}

	private boolean at(EnumSet<TokenType> types) {
		return types.contains(token.type);
	}

	private boolean at(TokenType type) {
		return type == token.type;
	}

	private void block() {
		push(new Block());
		try {
			TokenType end;
			if (at(OPEN_BRACE)) {
				end = CLOSE_BRACE;
				next();
			} else {
				end = END_FILE;
			}
			while (!at(end) && !at(END_FILE)) {
				statement();
			}
			if (at(end)) {
				if (!at(END_FILE)) {
					next(ANY);
				}
			} else {
				// TODO Mark error for unterminated block(s).
			}
		} finally {
			pop();
		}
	}

	private void call() {
		push(new Call());
		try {
			next(ANY_BUT_LINEAR_FILL);
			// TODO Support paren-less single arg?
			if (at(OPEN_PAREN)) {
				args();
			}
		} finally {
			pop();
		}
	}

	/**
	 * Just bogus to get through the file.
	 */
	private void chewStatement() {
		if (at(SOFT_END) && !at(END_FILE)) {
			next();
		} else {
			next(SOFT_END);
		}
	}

	private void def() {
		push(new Def());
		// TODO @annotations, ...
		next(ID_OPEN_BRACE_OPEN_PAREN_OR_STRONG_END);
		if (at(STRONG_END)) {
			return;
		}
		if (at(ID)) {
			// TODO Record ID.
			// If no ID, then it could be a constructor.
			next(OPEN_BRACE_OPEN_PAREN_OR_STRONG_END);
		}
		if (at(OPEN_PAREN)) {
			params();
		}
		// TODO ':', throws, @annotations, ...
		if (at(OPEN_BRACE)) {
			block();
			if (!at(SOFT_END)) {
				next(SOFT_END);
			}
		} else {
			chewStatement();
		}
	}

	private void expression() {
		switch (token.type) {
			case ID:
				call();
				break;
			case STRING:
				stringNode();
				break;
			default:
				next(ANY_BUT_LINEAR_FILL);
				break;
		}
		while (at(DOT)) {
			// TODO If inside of a list, we should look for COMMA or CLOSE_PAREN instead of normal ends.
			next(ID_OR_STRONG_END);
			if (at(ID)) {
				call();
			} else {
				// TODO Mark error.
				return;
			}
		}
	}

	private void expressionStatement() {
		push(new Expression());
		expression();
	}

	private boolean hasMoreTokens() {
		return index < tokens.size() - 1;
	}

	private void imports() {
		// TODO Auto-generated method stub
	}

	private void mainDeclaration() {
		// TODO Auto-generated method stub
	}

	private TokenType next() {
		return next(ANY_BUT_FILL);
	}

	private TokenType next(EnumSet<TokenType> stops) {
		while (hasMoreTokens()) {
			if (token != null) {
				// Add the previous token now.
				if (at(WHITE)) {
					pendingWhite.add(token);
				} else {
					addWhiteToPending();
					pending.add(token);
				}
			}
			index++;
			token = tokens.get(index);
			// TODO If stopping at END_LINEs, check also for ellipses and skip the next endline if that's next stop.
			if (at(stops)) {
				return token.type;
			}
		}
		throw new RuntimeException("no more tokens");
	}

	private void params() {
		// TODO Auto-generated method stub
	}

	/**
	 * Parses a complete compilation unit into a {@link Script} AST object.
	 * 
	 * @param source
	 * @return
	 */
	public Script parse(CharSequence source) {
		return parse(new Tokenizer().tokenize(source));
	}

	private Script parse(List<Token> tokens) {
		this.tokens = tokens;
		index = -1;
		pending = new ArrayList<Token>();
		pendingWhite = new ArrayList<Token>();
		Script script = push(new Script());
		next();
		imports();
		mainDeclaration();
		block();
		addWhiteToPending();
		addPending();
		return script;
	}

	private void pop() {
		addPending();
		node = node.parent;
		addWhiteToPending();
		addPending();
	}

	private <NodeType extends Parent> NodeType push(NodeType node) {
		node.parent = this.node;
		if (this.node != null) {
			addWhiteToPending();
			addPending();
			this.node.kids.add(node);
		}
		this.node = node;
		return node;
	}

	private void statement() {
		boolean advancedForSemi = false;
		try {
			switch (token.type) {
				case DEF:
					def();
					break;
				case VAR:
					var();
					break;
				default:
					expressionStatement();
					break;
			}
			if (!at(SOFT_END)) {
				next(SOFT_END);
			}
			if (at(SEMICOLON)) {
				advancedForSemi = true;
				next();
			}
		} finally {
			// Pop here for consistency after the semicolon.
			pop();
		}
		if (!advancedForSemi && !at(END_FILE)) {
			next();
		}
	}

	private void stringNode() {
		StringNode stringNode = push(new StringNode());
		try {
			do {
				String text = token.text.toString().substring(1);
				if (text.endsWith("'")) {
					text = text.substring(0, text.length() - 1);
				} else {
					// TODO Should this ever have "\r\n"?
					text += '\n';
				}
				stringNode.value += text;
				next(ANY_BUT_LINEAR_FILL);
			} while (at(STRING));
		} finally {
			pop();
		}
	}

	private void type() {
		if (next() == ID) {
			push(new TypeRef());
			try {
				next();
			} finally {
				pop();
			}
		}
	}

	private void typeDeclaration() {
		type();
		// TODO Allow assignment.
	}

	private void var() {
		Var statement = push(new Var());
		next(ASSIGN_COLON_ID_OR_STRONG_END);
		if (at(STRONG_END)) {
			return;
		}
		if (at(ID)) {
			statement.id = token;
			// TODO Are soft ends allowed or do we need a type or assignment?
			next(ASSIGN_COLON_OR_SOFT_END);
		} else {
			// TODO Mark error.
		}
		if (at(COLON)) {
			typeDeclaration();
		}
		// Might be an assignment following the type declaration.
		if (at(ASSIGN)) {
			assignment();
		}
	}

}
