package org.simonschneider.test;

import java.lang.reflect.Type;
import java.util.Map;

public interface EmptyInstanceFactory {

  boolean containsEmptyInstanceFor(Type clazz);

  <T> T getEmptyInstanceFor(Type type);

  void put(Type type, Object creator);

  default void putAll(Map<Type, Object> other) {
    other.forEach(this::put);
  }
}
