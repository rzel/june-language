package june.engine;

import static org.objectweb.asm.Type.*;

import java.util.*;

import june.tree.*;

import org.objectweb.asm.*;

class ClassBuilder implements ClassVisitor {

	public static JuneClass accessClass(
			Map<String, Entity> globals,
			String className) {
		JuneClass $class = (JuneClass)globals.get(className);
		if ($class == null) {
			$class = new JuneClass();
			$class.name = className;
			globals.put(className, $class);
		}
		return $class;
	}

	public static JunePackage accessPackage(
			Map<String, Entity> globals,
			String packageName) {
		JunePackage $package = (JunePackage)globals.get(packageName);
		if ($package == null) {
			$package = new JunePackage();
			$package.name = packageName;
			globals.put(packageName, $package);
		}
		return $package;
	}

	public JuneClass $class;

	private Map<String, Entity> globals;

	public ClassBuilder(Map<String, Entity> globals) {
		this.globals = globals;
	}

	private JuneClass accessClass(String className) {
		return accessClass(globals, className);
	}

	private void applyModifiers(JuneMember member, int access) {
		member.setStatic((access & Opcodes.ACC_STATIC) != 0);
	}

	/**
	 * TODO We need annotations for full type information. Also, use signatures not descriptors!
	 */
	private JuneType toJuneType(Type asmType) {
		JuneType juneType = null;
		switch (asmType.getSort()) {
			case VOID:
				juneType = accessClass("java.lang.Void");
				break;
			case OBJECT:
				juneType = accessClass(asmType.getClassName());
				break;
		}
		return juneType;
	}

	public void visit(
			int version,
			int access,
			String name,
			String signature,
			String superName,
			String[] interfaces) {
		String className = name.replace('/', '.');
		$class = accessClass(className);
		$class.internalName = name;
		String packageName;
		if (name.indexOf('/') >= 0) {
			int lastSlash = name.lastIndexOf('/');
			packageName = name.substring(0, lastSlash).replace('/', '.');
			$class.baseName = name.substring(lastSlash + 1);
		} else {
			packageName = "";
			$class.baseName = className;
		}
		JunePackage $package = accessPackage(globals, packageName);
		$class.$package = $package;
	}

	public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
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
			String descriptor,
			String signature,
			Object value) {
		JuneField field = new JuneField();
		field.name = name;
		field.declaringClass = $class;
		field.type = toJuneType(Type.getType(descriptor));
		applyModifiers(field, access);
		$class.addMember(field);
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
			String descriptor,
			String signature,
			String[] exceptions) {
		JuneMethod method = new JuneMethod();
		method.name = name;
		method.declaringClass = $class;
		method.type = toJuneType(Type.getReturnType(descriptor));
		// TODO Use SignatureVisitor and MethodVisitor to get full type information.
		Type[] argTypes = Type.getArgumentTypes(descriptor);
		for (Type type: argTypes) {
			method.argTypes.add(toJuneType(type));
		}
		applyModifiers(method, access);
		$class.addMember(method);
		// TODO We'll need a method visitor to get annotations which we'll need for full (yet usually runtime erased) type information from June.
		return null;
	}

	public void visitOuterClass(String owner, String name, String descriptor) {
		// TODO Auto-generated method stub
	}

	public void visitSource(String source, String debug) {
		// TODO Auto-generated method stub
	}

}
