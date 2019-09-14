package org.simonschneider.test;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class MapBackedGenericTypeFactory implements GenericTypeFactory {
  private final HashMap<Type, GenericTypeCreator> typeCreators = new HashMap<>();

  public MapBackedGenericTypeFactory() {}

  public MapBackedGenericTypeFactory(Map<Type, GenericTypeCreator> initial) {
    this.putAll(initial);
  }

  @Override
  public boolean canBuild(Type type) {
    return typeCreators.containsKey(type);
  }

  @Override
  public <T> T buildInstance(Type type, Function<Type, Object> delegate, Type[] genericTypes) {
    return (T) typeCreators.get(type).apply(delegate, genericTypes);
  }

  public void put(Type type, GenericTypeCreator creator) {
    typeCreators.put(type, creator);
  }

  public void putSimple(Type type, Supplier<Object> creator) {
    put(type, (r, i) -> creator.get());
  }

  public void putAll(Map<? extends Type, ? extends GenericTypeCreator> other) {
    other.forEach(this::put);
  }

  public void putAllSimple(Map<? extends Type, ? extends Supplier<Object>> other) {
    other.forEach(this::putSimple);
  }

  @FunctionalInterface
  public interface GenericTypeCreator {
    Object apply(Function<Type, Object> typeBuilder, Type[] genericTypes);
  }
}
