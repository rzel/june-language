June is what ordinary Java would look like if it were simpler, more reliable, and disguised as a scripting language. Run from source - or compile to class files with no dependencies on June itself.

Here's Hello World:

```
out.println('Hello, world!')
```

Or put a shebang in front for fun:

```
#!/usr/bin/env june
out.println('Hello, world!')
```

Simple summary:

  * Statically typed.
  * Really almost just Java on the inside.
  * No checked exceptions.
  * Simplified generics.
  * Closures.
  * Compiles to Java classes.
  * Null and non-null handling.
  * Simplified handling of autoboxing and primitives as objects.
  * Hopefully possible to compile Java and June in one pass, allowing you to convert one file at a time.
  * Uses '#' for comments to ease Unix-like scripting expectations.

Very simple scripts work, but there's a long way to go on implementation.

I'm releasing June code as public domain.