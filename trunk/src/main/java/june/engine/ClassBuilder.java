package june.engine;

import june.tree.*;

import org.objectweb.asm.*;

class ClassBuilder implements ClassVisitor {

	public JuneClass $class = new JuneClass();

	public void visit(
			int version,
			int access,
			String name,
			String signature,
			String superName,
			String[] interfaces) {
		$class.name = name.replace('/', '.');
	}

	public AnnotationVisitor visitAnnotation(String description, boolean visible) {
		// TODO Auto-generated method stub
		return null;
	}

	public void visitAttribute(Attribute attribute) {
		// TODO Auto-generated method stub
	}

	public void visitEnd() {
		// TODO Auto-generated method stub
	}

	public FieldVisitor visitField(
			int access,
			String name,
			String description,
			String signature,
			Object value) {
		JuneField method = new JuneField();
		method.name = name;
		method.declaringClass = $class;
		$class.addMember(method);
		// TODO We'll need a field visitor to get annotations which we'll need for full (yet usually runtime erased) type information from June.
		return null;
	}

	public void visitInnerClass(
			String name,
			String outerName,
			String innerName,
			int access) {
		// TODO Auto-generated method stub
	}

	public MethodVisitor visitMethod(
			int access,
			String name,
			String description,
			String signature,
			String[] exceptions) {
		JuneMethod method = new JuneMethod();
		method.name = name;
		method.declaringClass = $class;
		$class.addMember(method);
		// TODO We'll need a method visitor to get annotations which we'll need for full (yet usually runtime erased) type information from June.
		return null;
	}

	public void visitOuterClass(String owner, String name, String description) {
		// TODO Auto-generated method stub
	}

	public void visitSource(String source, String debug) {
		// TODO Auto-generated method stub
	}

}
