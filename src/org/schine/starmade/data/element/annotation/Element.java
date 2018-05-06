package org.schine.starmade.data.element.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Element {
	boolean elementSet() default false;

	String collectionElementTag() default "";

	String collectionType() default "";

	boolean cubatom() default false;

	boolean cubatomRecipe() default false;

	boolean inventoryGroup() default false;

	boolean canBulkChange() default true;

	/**
	 * used for attributes (set false)
	 *
	 * @return if its handled as a tag
	 */
	boolean writeAsTag() default true;

	boolean factory() default false;

	int from() default -1;

	boolean level() default false;

	String[] states() default {};

	String[] stateDescs() default {};

	ElemType parser();
	
	boolean textArea() default false;

	int to() default -1;

	boolean type() default false;

	boolean updateTextures() default false;

	boolean vector3f() default false;

	boolean vector4f() default false;

	boolean consistence() default false;

	boolean cubatomConsistence() default false;

	boolean editable() default true;

	boolean lodShape() default false;

	boolean selectBlock() default false;

	boolean shortSet() default false;

	boolean stringSet() default false;

	boolean configGroupSet() default false;
}
