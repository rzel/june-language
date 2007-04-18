package june.tree;

import java.util.*;

public class Token extends Inline {

	public int line;

	public int offset;

	public CharSequence text;

	public TokenType type;

	public Token(TokenType type, int line, int offset, CharSequence text) {
		this.type = type;
		this.line = line;
		this.offset = offset;
		this.text = text;
	}

	private String escape(CharSequence text) {
		return text
				.toString()
				.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\n", "\\n")
				.replace("\r", "\\r")
				.replace("\t", "\\t");
	}

	/**
	 * Always returns an empty list. Tokens are leaves.
	 */
	@Override
	public Iterable<Node> getKids() {
		return Collections.emptyList();
	}

	@Override
	public int lineBegin() {
		return line;
	}

	@Override
	public int lineEnd() {
		return line;
	}

	@Override
	public int offsetBegin() {
		return offset;
	}

	@Override
	public int offsetEnd() {
		return offset + Character.codePointCount(text, 0, text.length());
	}

	@Override
	public String toString() {
		return type + "(" + line + ", " + offset + "): \"" + escape(text) + '"';
	}

}
