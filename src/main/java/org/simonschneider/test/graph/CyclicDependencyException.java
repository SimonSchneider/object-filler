package org.simonschneider.test.graph;

import java.lang.reflect.Type;

public class CyclicDependencyException extends RuntimeException {
  public CyclicDependencyException(Type type) {
    super(String.format("Detected loop with %s", type));
  }
}
