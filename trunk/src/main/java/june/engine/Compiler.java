package june.engine;

import static june.engine.Helper.*;
import static org.objectweb.asm.Opcodes.*;
import june.tree.*;

import org.objectweb.asm.*;

public class Compiler {

	private ClassWriter writer;

	private void block(Node kid) {
		// TODO Auto-generated method stub
	}

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
				block(kid);
			}
		}
		// for (Statement statement: script.blocks.get(0).statements) {
		// if (statement.begin().content instanceof Do) {
		// Multi doMulti = (Multi)statement;
		// MethodVisitor method =
		// writer.visitMethod(
		// ACC_PUBLIC,
		// "<init>",
		// "()V",
		// null,
		// null);
		// method.visitCode();
		// Label label0 = new Label();
		// method.visitLabel(label0);
		// method.visitLineNumber(doMulti.lineBegin(), label0);
		// method.visitVarInsn(ALOAD, 0);
		// method.visitMethodInsn(
		// INVOKESPECIAL,
		// "java/lang/Object",
		// "<init>",
		// "()V");
		// compileBlock(method, doMulti.blocks.get(0));
		// Label l3 = new Label();
		// method.visitLabel(l3);
		// method.visitLineNumber(14, l3);
		// method.visitInsn(RETURN);
		// Label l4 = new Label();
		// method.visitLabel(l4);
		// method.visitLocalVariable(
		// "this",
		// "Lpkg/Cls;",
		// null,
		// label0,
		// l4,
		// 0);
		// method.visitMaxs(0, 0);
		// method.visitEnd();
		// break;
		// }
		// }
		writer.visitEnd();
		final byte[] data = writer.toByteArray();
		ClassLoader loader = new ClassLoader(getClass().getClassLoader()) {
			@Override
			protected Class<?> findClass(String name)
					throws ClassNotFoundException {
				// TODO Auto-generated method stub
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

	// private void compileBlock(MethodVisitor method, Block block) {
	// for (Statement statement: block.statements) {
	// if (statement.begin().content instanceof Call) {
	// Call call = (Call)statement.begin().content;
	// if (call.method instanceof Token
	// && ((Token)call.method).text.equals("write")) {
	// Label label = new Label();
	// method.visitLabel(label);
	// method.visitLineNumber(statement.lineBegin(), label);
	// method.visitFieldInsn(
	// GETSTATIC,
	// "java/lang/System",
	// "out",
	// "Ljava/io/PrintStream;");
	// method
	// .visitLdcInsn(((Token)call.args.argItems.get(0).value).text);
	// method.visitMethodInsn(
	// INVOKEVIRTUAL,
	// "java/io/PrintStream",
	// "println",
	// "(Ljava/lang/String;)V");
	// }
	// }
	// }
	// }

}
