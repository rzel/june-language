package june.engine;

import static june.tree.TokenType.*;

import java.util.*;

import june.tree.*;

@SuppressWarnings("unchecked")
public class Analyzer {

	public static final Set<String> DEFAULT_IMPORTS =
			Collections.unmodifiableSet(new LinkedHashSet(Arrays.asList(
					"java.io",
					"java.lang",
					"java.lang.System",
					"java.math",
					"java.text",
					"java.util",
					"java.util.regex")));

	// TODO Mark errors. Find higher-level bugs. More.

	// TODO Pass in a Resolver? (Into the constructor?)

	/**
	 * Cache of packages and classes.
	 */
	private Map<String, Entity> globals = new HashMap<String, Entity>();

	private JuneClass accessClass(String className) {
		return ClassBuilder.accessClass(globals, className);
	}

	public void analyze(Script script) {
		// TODO Multiple passes across multiple files.
		// TODO First find defs and vars (in all files), then find bindings to them.
		// TODO Do multiple passes on locals and privates to determine types?
		for (Node kid: script.getKids()) {
			if (kid instanceof Block) {
				block(kid);
			}
		}
	}

	private void args(Args args, Usage usage) {
		// We have some args. It's no longer null (unknown).
		usage.argTypes = new ArrayList<JuneType>();
		for (Node kid: args.getKids()) {
			if (kid instanceof Arg && !((Arg)kid).kids.isEmpty()) {
				Node grandkid = ((Arg)kid).kids.get(0);
				if (grandkid instanceof Expression) {
					expression((Expression)grandkid);
					usage.argTypes.add(((Expression)grandkid).type);
				} else {
					// TODO Should be an error.
				}
			}
		}
	}

	private void block(Node block) {
		for (Node kid: block.getKids()) {
			if (kid instanceof Expression) {
				expression((Expression)kid);
			} else if (kid instanceof Def) {
				def((Def)kid);
			}
		}
	}

	private void call(Call call, Node context) {
		Usage usage = new Usage();
		for (Node kid: call.getKids()) {
			if (kid instanceof Token) {
				Token token = (Token)kid;
				if (token.type == ID) {
					// TODO Do we need to know the arg types first?
					usage.name = token.text.toString();
					// System.out.println(call.entity);
				}
			} else if (kid instanceof Args) {
				args((Args)kid, usage);
			}
		}
		if (usage.name != null) {
			// System.out.println("call " + usage + " at " + context);
			if (context instanceof Call) {
				// The namespace is bound to a particular object and/or class (hierarchy).
				Entity contextEntity = ((Call)context).entity;
				if (contextEntity instanceof JuneMember) {
					JuneClass $class =
							((JuneClass)((JuneMember)contextEntity).type);
					ensureClassLoaded($class);
					// TODO Do we need resolve the other types? Now or immediately when building the class?
					call.entity = $class.getMember(usage);
				}
			} else {
				// Lexical scoping falling out to imports and global namespacing.
				if (context instanceof Block) {
					// TODO Search lexical contexts - and inherited members for explicit classes.
				}
				if (call.entity == null) {
					call.entity =
							new Resolver.ImportResolver(
									DEFAULT_IMPORTS,
									globals,
									null).findEntity(usage);
				}
			}
			if (call.entity instanceof JuneMember) {
				call.type = ((JuneMember)call.entity).type;
				ensureClassLoaded((JuneClass)call.type);
			}
		}
	}

	private void def(Def def) {
		for (Node kid: def.getKids()) {
			if (kid instanceof Token) {
				Token token = (Token)kid;
				if (token.type == ID) {
					def.method.name = token.text.toString();
					def.method.declaringClass = ((Block)def.parent).$class;
					def.method.declaringClass.addMember(def.method);
					// System.out.println(def.method);
				}
			} else if (kid instanceof Params) {
				params((Params)kid);
			}
		}
		// TODO Record explicit types (required for all but private or local).
		// TODO Mark unresolved types so we can complete in later pass.
	}

	/**
	 * Load class details on demand with this method. Maybe we should just aggressively load everything at referring class load time?
	 */
	private void ensureClassLoaded(JuneClass $class) {
		if (!$class.loaded) {
			String[] packageAndClass =
					Resolver.splitPackageAndClass(globals, $class.name);
			Resolver.loadClass(globals, packageAndClass[0], packageAndClass[1]);
		}
	}

	private void expression(Expression expression) {
		if (expression instanceof StringNode) {
			JuneClass $class = accessClass("java.lang.String");
			ensureClassLoaded($class);
			expression.type = $class;
		} else if (expression instanceof Call) {
			// TODO Should this really be here? How should a dot series really look?
			call(((Call)expression), expression.parentBlock());
		} else {
			// TODO Should this really be here? Should we have such arbitrary "expressions" or make Expression abstract?
			Node context = expression.parentBlock();
			for (Node kid: expression.getKids()) {
				if (kid instanceof Call) {
					call((Call)kid, context);
					// TODO This should be any prior expression node (not just statement nor call) in a series of dot drills.
					context = kid;
				}
			}
		}
	}

	private void params(Params params) {
		// TODO Auto-generated method stub
	}

}
