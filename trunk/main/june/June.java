package june;

import java.lang.annotation.*;

/**
 * Indicates that a class was compiled from June source.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface June {

	// Just a marker.

	// TODO We do need some way to mark non-nulls even in generic types - perhaps just the June source declaration.

}
