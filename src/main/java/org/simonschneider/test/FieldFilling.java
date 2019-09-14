package org.simonschneider.test;

import java.lang.reflect.Constructor;

@FunctionalInterface
public interface FieldFilling {
  boolean shouldFillFields(Constructor<?> constructor, Object instance);
}
