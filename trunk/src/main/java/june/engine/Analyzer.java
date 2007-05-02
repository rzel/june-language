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

	// TODO Resolve names. Find higher-level bugs. More.

	// TODO Pass in a Resolver! (Into the constructor?)

	/**
	 * TODO Cache packages here too. Call it "globals"?
	 */
	private Map<String, JuneClass> classCache =
			new HashMap<String, JuneClass>();

	private JuneClass accessClass(String className) {
		return ClassBuilder.accessClass(classCache, className);
	}

	public void analyze(Script script) {
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
					System.out.println(call.entity);
				}
			} else if (kid instanceof Args) {
				args((Args)kid, usage);
			}
		}
		if (usage.name != null) {
			System.out.println("call " + usage + " at " + context);
			if (context instanceof Call) {
				Entity entity = ((Call)context).entity;
				if (entity instanceof JuneMember) {
					JuneClass $class = ((JuneClass)((JuneMember)entity).type);
					ensureClassLoaded($class);
					// TODO Do we need resolve the other types? Now or immediately when building the class?
					call.entity = $class.getMember(usage);
				}
			} else {
				call.entity =
						new Resolver.ImportResolver(
								DEFAULT_IMPORTS,
								classCache,
								null).findEntity(usage);
			}
			if (call.entity instanceof JuneMember) {
				call.type = ((JuneMember)call.entity).type;
				ensureClassLoaded((JuneClass)call.type);
			}
		}
	}

	/**
	 * Load class details on demand with this method. Maybe we should just aggressively load everything at referring class load time?
	 */
	private void ensureClassLoaded(JuneClass $class) {
		if (!$class.loaded) {
			String[] packageAndClass =
					Resolver.splitPackageAndClass($class.name);
			Resolver.loadClass(
					classCache,
					packageAndClass[0],
					packageAndClass[1]);
		}
	}

	private void expression(Expression expression) {
		if (expression instanceof StringNode) {
			JuneClass $class = accessClass("java.lang.String");
			ensureClassLoaded($class);
			expression.type = $class;
		}
		Node context = expression.parent;
		for (Node kid: expression.getKids()) {
			if (kid instanceof Call) {
				call((Call)kid, context);
				// TODO This should be any prior expression node (not just statement nor call) in a series of dot drills.
				context = kid;
			}
		}
	}

}