package org.simonschneider.test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public interface GenericTypeFactory {

  default boolean canBuild(ParameterizedType type) {
    return canBuild(type.getRawType());
  }

  boolean canBuild(Type type);

  default <T> T buildInstance(ParameterizedType type, Function<Type, ?> typeBuilder) {
    return buildInstance(type.getRawType(), typeBuilder, type.getActualTypeArguments());
  }

  <T> T buildInstance(Type type, Function<Type, ?> typeBuilder, Type[] genericTypes);

  void put(Type type, GenericTypeCreator creator);

  default void putSimple(Type type, Supplier<Object> creator) {
    put(type, (f, i) -> creator.get());
  }

  default void putAll(Map<? extends Type, ? extends GenericTypeCreator> other) {
    other.forEach(this::put);
  }

  default void putAllSimple(Map<? extends Type, ? extends Supplier<Object>> other) {
    other.forEach(this::putSimple);
  }

  @FunctionalInterface
  interface GenericTypeCreator {
    Object apply(Function<Type, ?> typeBuilder, Type[] genericTypes);
  }
}
