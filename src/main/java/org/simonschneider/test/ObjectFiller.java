package org.simonschneider.test;

import java.lang.reflect.Type;

@FunctionalInterface
public interface ObjectFiller {
  <T> T createAndFill(Type type);
}
