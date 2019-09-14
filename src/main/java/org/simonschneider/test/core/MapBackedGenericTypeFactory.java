package org.simonschneider.test.core;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.simonschneider.test.GenericTypeFactory;
import org.simonschneider.test.ObjectFiller;

public class MapBackedGenericTypeFactory implements GenericTypeFactory {
  private final Map<Type, GenericTypeCreator> typeCreators = new HashMap<>();

  public MapBackedGenericTypeFactory() {}

  public MapBackedGenericTypeFactory(Map<Type, GenericTypeCreator> initial) {
    this.putAll(initial);
  }

  @Override
  public boolean canBuild(Type type) {
    return typeCreators.containsKey(type);
  }

  @Override
  public <T> T buildInstance(Type type, ObjectFiller objectFiller, Type[] genericTypes) {
    return (T) typeCreators.get(type).apply(objectFiller, genericTypes);
  }

  public void put(Type type, GenericTypeCreator creator) {
    typeCreators.put(type, creator);
  }
}
