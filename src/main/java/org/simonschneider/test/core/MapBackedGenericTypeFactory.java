package org.simonschneider.test.core;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.simonschneider.test.GenericTypeFactory;

public class MapBackedGenericTypeFactory implements GenericTypeFactory {
  private final Map<Type, GenericTypeCreator> typeCreators = new HashMap<>();

  public MapBackedGenericTypeFactory(Map<Type, GenericTypeCreator> initial) {
    this.putAll(initial);
  }

  @Override
  public boolean canBuild(Type type) {
    return typeCreators.containsKey(type);
  }

  @Override
  public <T> T buildInstance(Type type, Function<Type, ?> typeBuilder, Type[] genericTypes) {
    return (T) typeCreators.get(type).apply(typeBuilder, genericTypes);
  }

  public void put(Type type, GenericTypeCreator creator) {
    typeCreators.put(type, creator);
  }
}
