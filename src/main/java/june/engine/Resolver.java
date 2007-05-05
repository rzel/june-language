package june.engine;

import java.io.*;
import java.net.*;
import java.util.*;

import june.tree.*;

import org.objectweb.asm.*;

public abstract class Resolver {

	/**
	 * Searches imports and the package hierarchy on the classpath.
	 */
	public static class ImportResolver extends Resolver {

		/**
		 * TODO Minimal VFS to support network, in-memory, and so on? Shouldn't use Java 6 APIs for Java 5 compatibility, however.
		 */
		private final List<String> classpath;

		private final Map<String, Entity> globals;

		private final Set<String> imports;

		/**
		 * @param imports
		 *            (or should this be a list?) - can't really accommodate current package, since that has a specific priority. And what about "import bob: java.sql"?
		 * @param globals
		 * @param classpath
		 */
		public ImportResolver(
				Set<String> imports,
				Map<String, Entity> globals,
				List<String> classpath) {
			this.imports = imports;
			this.globals = globals;
			this.classpath = classpath;
		}

		private void addMatches(Set<Entity> matches, String $import, Usage usage) {
			try {
				// TODO First check the last part of the imported package to see if it matches (if null argTypes). As in "sql" for "java.sql".
				// TODO Use the provided classpath, and manually search it instead of using Package#getPackage(String).
				String[] packageAndClass =
						splitPackageAndClass(globals, $import);
				String packageName = packageAndClass[0];
				String baseClassName = packageAndClass[1];
				boolean expectClass = false;
				if (baseClassName == null) {
					// There was no class in the import, so the requested entity must be a class or package.
					String tempPackageName =
							packageName + (packageName.length() > 0 ? "." : "")
									+ usage.name;
					if (Package.getPackage(tempPackageName) != null) {
						matches.add(new JunePackage());
						return;
					}
					baseClassName = usage.name;
					expectClass = true;
				}
				JuneClass $class =
						loadClass(globals, packageName, baseClassName);
				if ($class != null) {
					// System.out.println($class);
					if (expectClass) {
						matches.add($class);
					} else {
						JuneMember member = $class.getMember(usage);
						// TODO Watch for visibility and static only.
						if (member != null) {
							matches.add(member);
						}
					}
				}
			} catch (Exception e) {
				throw Helper.throwAny(e);
			}
		}

		@Override
		protected Entity findCurrentEntity(Usage signature) {
			Set<Entity> matches = new HashSet<Entity>();
			// First, make sure to check the root.
			addMatches(matches, "", signature);
			// Now, check the each of the imports.
			for (String $import: imports) {
				addMatches(matches, $import, signature);
			}
			if (matches.isEmpty()) {
				return null;
			} else if (matches.size() > 1) {
				// TODO Find if there is a "best" match.
				// TODO Custom exception type so a correct error message can be given.
				throw new RuntimeException(matches.size() + " matches for "
						+ signature + " in " + imports);
			}
			return matches.iterator().next();
		}

	}

	/**
	 * Resolves members of the current class and superclasses, depending on visibility of members and so on. TODO How does this relate to locals and nested scopes?
	 */
	public static class MemberResolver extends Resolver {

		@Override
		protected Entity findCurrentEntity(Usage signature) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	public static JuneClass loadClass(
			Map<String, Entity> globals,
			String packageName,
			String baseClassName) {
		try {
			JuneClass $class = null;
			String resourceName =
					(packageName.length() > 0 ? packageName.replace('.', '/')
							+ "/" : "")
							+ baseClassName.replace('.', '$') + ".class";
			// TODO Just search the classpath manually where possible, but this might still be needed for system classes.
			URL url = Resolver.class.getClassLoader().getResource(resourceName);
			if (url != null) {
				// TODO Cache built classes by URL or by resourceName?
				// System.out.println(url);
				ClassBuilder builder = new ClassBuilder(globals);
				InputStream stream = url.openStream();
				try {
					new ClassReader(new BufferedInputStream(stream)).accept(
							builder,
							ClassReader.SKIP_CODE);
				} finally {
					stream.close();
				}
				$class = builder.$class;
				$class.loaded = true;
			}
			return $class;
		} catch (Exception e) {
			throw Helper.throwAny(e);
		}
	}

	public static String[] splitPackageAndClass(
			Map<String, Entity> globals,
			String qualifiedName) {
		Entity entity = globals.get(qualifiedName);
		if (entity instanceof JunePackage) {
			return new String[] {qualifiedName, null};
		} else if (entity instanceof JuneClass) {
			JuneClass $class = (JuneClass)entity;
			if ($class.$package != null) {
				return new String[] {$class.$package.name, $class.baseName};
			}
		}
		String packageName = qualifiedName;
		String baseClassName = null;
		Package $package = Package.getPackage(packageName);
		while (packageName.length() > 0 && $package == null) {
			int lastDot = packageName.lastIndexOf('.');
			baseClassName =
					packageName.substring(lastDot + 1)
							+ (baseClassName == null ? "" : "." + baseClassName);
			packageName = packageName.substring(0, lastDot < 0 ? 0 : lastDot);
			$package = Package.getPackage(packageName);
		}
		// TODO Cache intermediate search results?
		// TODO Cache not-founds to speed up even with lots of unresolved? Probably not.
		if (packageName.length() > 0) {
			// Might as well cache the package name for faster future reference.
			ClassBuilder.accessPackage(globals, packageName);
		}
		return new String[] {packageName, baseClassName};
	}

	// TODO Keep abstract enough that it could apply other than with Java classpaths?

	// TODO How do we represent a signified entity (method, field, property, class, package)? Just saying "Object" for now.

	public Resolver parent;

	protected abstract Entity findCurrentEntity(Usage signature);

	public Entity findEntity(Usage signature) {
		Entity entity = findCurrentEntity(signature);
		if (entity == null && parent != null) {
			entity = parent.findEntity(signature);
		}
		return entity;
	}

}
