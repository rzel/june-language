package june.engine;

import static june.engine.Helper.*;
import static org.objectweb.asm.Opcodes.*;
import june.tree.*;

import org.objectweb.asm.*;

public class Compiler {

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
		Label firstLabel = new Label();
		expressionsFor(block);
		Label lastLabel = new Label();
		method.visitLocalVariable(
				"this",
				"Lpkg/Cls;",
				null,
				firstLabel,
				lastLabel,
				0);
		method.visitInsn(RETURN);
		method.visitMaxs(0, 0);
		method.visitEnd();
	}

	private void expressionsFor(Block block) {
		for (Node kid: block.getKids()) {
			if (kid instanceof Expression) {
				expression((Expression)kid);
			}
		}
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
				"pkg/Cls",
				null,
				"java/lang/Object",
				null);
		writer.visitSource("TODO.june", null);
		for (Node kid: script.getKids()) {
			if (kid instanceof Block) {
				block((Block)kid, false);
			}
		}
		writer.visitEnd();
		final byte[] data = writer.toByteArray();
		System.out.println("Class size: " + data.length);
		ClassLoader loader = new ClassLoader(getClass().getClassLoader()) {
			@Override
			protected Class<?> findClass(String name)
					throws ClassNotFoundException {
				if (name.equals("pkg.Cls")) {
					return defineClass(name, data, 0, data.length);
				}
				throw new ClassNotFoundException();
			}
		};
		try {
			return loader.loadClass("pkg.Cls");
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
		// method.visitVarInsn(ALOAD, 0);
		// method.visitMethodInsn(
		// INVOKESPECIAL,
		// "java/lang/Object",
		// "<init>",
		// "()V");
		// Label firstLabel = new Label();
		for (Node kid: def.getKids()) {
			if (kid instanceof Block) {
				block((Block)kid, true);
			}
		}
		// Label lastLabel = new Label();
		// method.visitLocalVariable(
		// "this",
		// "Lpkg/Cls;",
		// null,
		// firstLabel,
		// lastLabel,
		// 0);
		method.visitInsn(RETURN);
		method.visitMaxs(0, 0);
		method.visitEnd();
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

}
