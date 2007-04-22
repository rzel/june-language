package june.engine;

import java.lang.reflect.*;
import java.net.*;
import java.util.*;

import june.tree.*;

public abstract class Resolver {

	/**
	 * Searches imports and the package hierarchy on the classpath.
	 */
	public static class ImportResolver extends Resolver {

		/**
		 * TODO Minimal VFS to support network, in-memory, and so on? Shouldn't use Java 6 APIs for Java 5 compatibility, however.
		 */
		private final List<String> classpath;

		private final Set<String> imports;

		/**
		 * @param imports
		 *            (or should this be a list?) - can't really accommodate current package, since that has a specific priority. And what about "import bob: java.sql"?
		 * @param classpath
		 */
		public ImportResolver(Set<String> imports, List<String> classpath) {
			this.imports = imports;
			this.classpath = classpath;
		}

		private void addMatches(
				Set<Entity> matches,
				String $import,
				Signature signature) {
			// TODO First check the last part of the imported package to see if it matches (if null argTypes). As in "sql" for "java.sql".
			// TODO Use the provided classpath, and manually search it instead of using Package#getPackage(String).
			String packageName = $import;
			Package $package = Package.getPackage(packageName);
			String baseClassName = null;
			while (packageName.length() > 0 && $package == null) {
				int lastDot = packageName.lastIndexOf('.');
				baseClassName =
						packageName.substring(lastDot + 1)
								+ (baseClassName == null ? "" : "."
										+ baseClassName);
				packageName =
						packageName.substring(0, lastDot < 0 ? 0 : lastDot);
				$package = Package.getPackage(packageName);
				// TODO Cache some of these search results?
			}
			String memberName = null;
			if (baseClassName == null) {
				// There was no class in the import, so the requested entity must be a class or package.
				String tempPackageName =
						packageName + (packageName.length() > 0 ? "." : "")
								+ signature.name;
				if (Package.getPackage(tempPackageName) != null) {
					matches.add(new JunePackage());
					return;
				}
				baseClassName = signature.name;
			} else {
				memberName = signature.name;
			}
			String resourceName =
					"/"
							+ (packageName.length() > 0 ? packageName.replace(
									'.',
									'/')
									+ "/" : "")
							+ baseClassName.replace('.', '$') + ".class";
			System.out.println(resourceName);
			URL url = getClass().getClassLoader().getResource(resourceName);
			System.out.println(url);
			if (url == null) {
				// TODO Don't do this if we can avoid it. We shouldn't be initializing classes. Just search the classpath.
				// TODO Is it okay to initialize for "java(x).**"?
				String className =
						(packageName.length() > 0 ? packageName + "." : "")
								+ baseClassName;
				System.out.println(className);
				Class<?> $class = null;
				try {
					$class = Class.forName(className);
					System.out.println($class);
				} catch (Exception e) {
					System.out.println("Like I care or something.");
					return;
				}
				if (memberName != null) {
					try {
						Field field = $class.getField(memberName);
						JuneField juneField = new JuneField(field.getName());
						juneField.declaringClass = className;
						matches.add(juneField);
					} catch (Exception e) {
						System.out.println("Like I care again.");
						return;
					}
				}
			}
		}

		@Override
		protected Entity findCurrentEntity(Signature signature) {
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
		protected Entity findCurrentEntity(Signature signature) {
			// TODO Auto-generated method stub
			return null;
		}

	}

	// TODO Keep abstract enough that it could apply other than with Java classpaths?

	// TODO How do we represent a signified entity (method, field, property, class, package)? Just saying "Object" for now.

	public Resolver parent;

	protected abstract Entity findCurrentEntity(Signature signature);

	public Entity findEntity(Signature signature) {
		Entity entity = findCurrentEntity(signature);
		if (entity == null) {
			entity = parent.findEntity(signature);
		}
		System.out.println(entity);
		return entity;
	}

}
