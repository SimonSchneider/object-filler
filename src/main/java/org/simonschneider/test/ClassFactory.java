package org.simonschneider.test;

import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ClassFactory {

  <T> boolean canBuild(Class<T> clazz);

  <T> T buildInstance(Random random, Class<T> clazz);

  void put(Class clazz, Function<Random, Object> creator);

  default void putSimple(Class clazz, Supplier<Object> creator) {
    put(clazz, r -> creator.get());
  }

  default void putAll(Map<? extends Class, ? extends Function<Random, Object>> other) {
    other.forEach(this::put);
  }

  default void putAllSimple(Map<? extends Class, ? extends Supplier<Object>> other) {
    other.forEach(this::putSimple);
  }
}
