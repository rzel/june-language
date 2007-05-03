package june.engine;

import static june.engine.Helper.*;
import static org.objectweb.asm.Opcodes.*;
import june.tree.*;

import org.objectweb.asm.*;

public class Compiler {

	private MethodVisitor defaultConstructor;

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

	private void block(Node block) {
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
					defaultConstructor.visitFieldInsn(
							GETSTATIC,
							member.declaringClass.internalName,
							member.name,
							member.getDescriptor());
				}
			} else if (member instanceof JuneMethod) {
				defaultConstructor.visitMethodInsn(
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
		defaultConstructor =
				writer.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		defaultConstructor.visitVarInsn(ALOAD, 0);
		defaultConstructor.visitMethodInsn(
				INVOKESPECIAL,
				"java/lang/Object",
				"<init>",
				"()V");
		Label firstLabel = new Label();
		for (Node kid: script.getKids()) {
			if (kid instanceof Block) {
				block(kid);
			}
		}
		Label lastLabel = new Label();
		defaultConstructor.visitLocalVariable(
				"this",
				"Lpkg/Cls;",
				null,
				firstLabel,
				lastLabel,
				0);
		defaultConstructor.visitInsn(RETURN);
		defaultConstructor.visitMaxs(0, 0);
		defaultConstructor.visitEnd();
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

	private void expression(Expression expression) {
		if (expression instanceof StringNode) {
			// TODO Track current method.
			defaultConstructor.visitLdcInsn(((StringNode)expression).value);
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
