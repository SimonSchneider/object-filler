package org.simonschneider.test;

import java.lang.reflect.Type;
import java.util.function.Function;

public interface GenericTypeFactory {

  boolean canBuild(Type type);

  <T> T buildInstance(Type type, Function<Type, Object> delegate, Type[] genericTypes);
}
