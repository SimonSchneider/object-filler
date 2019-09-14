package org.simonschneider.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import org.simonschneider.test.creator.InstanceIsNotKeyTypeException;

public class MapBackedClassFactory implements ClassFactory {
  private final HashMap<Class, Function<Random, Object>> classCreators = new HashMap<>();

  public MapBackedClassFactory() {}

  public MapBackedClassFactory(Map<Class, Function<Random, Object>> initial) {
    this.putAll(initial);
  }

  @Override
  public <T> boolean canBuild(Class<T> clazz) {
    return classCreators.containsKey(clazz);
  }

  @Override
  @SuppressWarnings("toUnchecked")
  public <T> T buildInstance(Random random, Class<T> clazz) {
    return (T) classCreators.get(clazz).apply(random);
  }

  public void put(Class clazz, Function<Random, Object> creator) {
    Object instance = creator.apply(new Random());
    if (getComparable(clazz).isInstance(instance)) {
      classCreators.put(clazz, creator);
    } else {
      throw new InstanceIsNotKeyTypeException(clazz, instance);
    }
  }

  public void putSimple(Class clazz, Supplier<Object> creator) {
    put(clazz, r -> creator.get());
  }

  public void putAll(Map<? extends Class, ? extends Function<Random, Object>> other) {
    other.forEach(this::put);
  }

  public void putAllSimple(Map<? extends Class, ? extends Supplier<Object>> other) {
    other.forEach(this::putSimple);
  }

  private static Class<?> getComparable(Class<?> expectedClass) {
    if (boolean.class.equals(expectedClass)) return Boolean.class;
    if (byte.class.equals(expectedClass)) return Byte.class;
    if (char.class.equals(expectedClass)) return Character.class;
    if (double.class.equals(expectedClass)) return Double.class;
    if (float.class.equals(expectedClass)) return Float.class;
    if (int.class.equals(expectedClass)) return Integer.class;
    if (long.class.equals(expectedClass)) return Long.class;
    if (short.class.equals(expectedClass)) return Short.class;
    return expectedClass;
  }
}
