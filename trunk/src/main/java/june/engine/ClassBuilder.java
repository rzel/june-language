package june.engine;

import static org.objectweb.asm.Type.*;

import java.util.*;

import june.tree.*;

import org.objectweb.asm.*;

class ClassBuilder implements ClassVisitor {

	public static JuneClass accessClass(
			Map<String, JuneClass> classCache,
			String className) {
		JuneClass $class = classCache.get(className);
		if ($class == null) {
			$class = new JuneClass();
			$class.name = className;
			classCache.put(className, $class);
		}
		return $class;
	}

	public JuneClass $class;

	private Map<String, JuneClass> classCache;

	public ClassBuilder(Map<String, JuneClass> classCache) {
		this.classCache = classCache;
	}

	private JuneClass accessClass(String className) {
		return accessClass(classCache, className);
	}

	/**
	 * TODO We need annotations for full type information. Also, use signatures not descriptors!
	 */
	private JuneType toJuneType(Type asmType) {
		JuneType juneType = null;
		switch (asmType.getSort()) {
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
