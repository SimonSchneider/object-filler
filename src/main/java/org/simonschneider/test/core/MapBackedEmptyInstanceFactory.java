package org.simonschneider.test.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.simonschneider.test.EmptyInstanceFactory;

public class MapBackedEmptyInstanceFactory implements EmptyInstanceFactory {
  private final Map<Type, Object> emptyTypes = new HashMap<>();

  public MapBackedEmptyInstanceFactory() {}

  public MapBackedEmptyInstanceFactory(Map<Type, Object> initial) {
    this.putAll(initial);
  }

  @Override
  public boolean containsEmptyInstanceFor(Type type) {
    return emptyTypes.containsKey(getType(type));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T getEmptyInstanceFor(Type type) {
    return (T) emptyTypes.get(getType(type));
  }

  @Override
  public void put(Type type, Object instance) {
    emptyTypes.put(type, instance);
  }

  private Type getType(Type type) {
    if (type instanceof ParameterizedType) {
      return ((ParameterizedType) type).getRawType();
    } else {
      return type;
    }
  }
}
