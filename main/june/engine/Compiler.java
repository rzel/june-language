package june.engine;

import static june.engine.Helper.*;
import static org.objectweb.asm.Opcodes.*;
import june.tree.*;

import org.objectweb.asm.*;

public class Compiler {

	/**
	 * TODO Use a count or a random number or a different classloader for unnamed scripts?
	 */
	static int count;

	static {
		count++;
	}

	String className = "pkg.Cls" + count;

	String internalClassName = "pkg/Cls" + count;

	private MethodVisitor method;

	private ClassWriter writer;

	private void args(Args args) {
		for (Node kid: args.getKids()) {
			if (kid instanceof Arg && !((Arg)kid).kids.isEmpty()) {
				Node grandkid = ((Arg)kid).kids.get(0);
				if (grandkid instanceof Expression) {
					expression((Expression)grandkid);
				} else {
					// TODO Should be an error.
				}
			}
		}
	}

	private void block(Block block, boolean explicitDef) {
		if (explicitDef) {
			// In the case of an explicit def, just build the code.
			// The analyzer should be responsible for assigning it a unique name and a real declaring class by this point.
			expressionsFor(block);
		} else {
			// Otherwise, it's an implicit method. Assume constructor for now. Single-method interface callbacks will be different.
			buildDefaultConstructor(block);
		}
		// Even explicit defs can have subdefs. They'll belong to some class or another.
		for (Node kid: block.getKids()) {
			if (kid instanceof Def) {
				def((Def)kid);
			}
		}
	}

	private void buildDefaultConstructor(Block block) {
		// TODO Support pushing method on stack so we can nest them and so on.
		method = writer.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		method.visitVarInsn(ALOAD, 0);
		method.visitMethodInsn(
				INVOKESPECIAL,
				"java/lang/Object",
				"<init>",
				"()V");
		expressionsFor(block);
	}

	private void call(Call call) {
		for (Node kid: call.getKids()) {
			if (kid instanceof Args) {
				args((Args)kid);
			}
		}
		if (call.entity instanceof JuneMember) {
			JuneMember member = (JuneMember)call.entity;
			if (member instanceof JuneField) {
				if (member.isStatic()) {
					method.visitFieldInsn(
							GETSTATIC,
							member.declaringClass.internalName,
							member.name,
							member.getDescriptor());
				}
			} else if (member instanceof JuneMethod) {
				if (call.open && !member.isStatic()) {
					method.visitVarInsn(ALOAD, 0);
				}
				method.visitMethodInsn(
						member.isStatic() ? INVOKESTATIC : INVOKEVIRTUAL,
						member.declaringClass.internalName,
						member.name,
						member.getDescriptor());
			}
		}
	}

	/**
	 * @param script
	 *            a previously analyzed script.
	 * @return a class representing the script - if really a simple script then instantiating it will run it.
	 */
	public Class<?> compile(Script script) {
		writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		writer.visit(
				V1_5,
				ACC_PUBLIC + ACC_SUPER,
				internalClassName,
				null,
				"java/lang/Object",
				null);
		writer.visitSource("TODO.june", null);
		for (Node kid: script.getKids()) {
			if (kid instanceof Block) {
				// Should be only one top-level Block in a Script.
				// TODO Invent names in the Analyzer if missing?
				if (((Block)kid).$class.name == null) {
					((Block)kid).$class.name = className;
					((Block)kid).$class.internalName = internalClassName;
				}
				block((Block)kid, false);
			}
		}
		writer.visitEnd();
		final byte[] data = writer.toByteArray();
		// System.out.println("Class size: " + data.length);
		ClassLoader loader = new ClassLoader(getClass().getClassLoader()) {
			@Override
			protected Class<?> findClass(String name)
					throws ClassNotFoundException {
				if (name.equals(className)) {
					return defineClass(name, data, 0, data.length);
				}
				throw new ClassNotFoundException();
			}
		};
		try {
			return loader.loadClass(className);
		} catch (Exception e) {
			throw throwAny(e);
		}
	}

	private void def(Def def) {
		// TODO Support pushing method on stack so we can nest them and so on.
		method =
				writer.visitMethod(
						ACC_PRIVATE,
						def.method.name,
						"()V",
						null,
						null);
		for (Node kid: def.getKids()) {
			if (kid instanceof Block) {
				block((Block)kid, true);
			}
		}
	}

	private void expression(Expression expression) {
		if (expression instanceof StringNode) {
			// TODO Track current method.
			method.visitLdcInsn(((StringNode)expression).value);
		} else if (expression instanceof Call) {
			// TODO See Analyzer for a discussion of how I might want to handle Call.
			call((Call)expression);
		} else {
			for (Node kid: expression.getKids()) {
				if (kid instanceof Call) {
					call((Call)kid);
				}
			}
		}
	}

	private void expressionsFor(Block block) {
		// TODO Other locals.
		// TODO Don't define "this" if we don't need it.
		Label firstLabel = new Label();
		for (Node kid: block.getKids()) {
			if (kid instanceof Expression) {
				expression((Expression)kid);
			}
		}
		Label lastLabel = new Label();
		method.visitLocalVariable(
				"this",
				"L" + internalClassName + ";",
				null,
				firstLabel,
				lastLabel,
				0);
		method.visitInsn(RETURN);
		method.visitMaxs(0, 0);
		method.visitEnd();
	}

}
