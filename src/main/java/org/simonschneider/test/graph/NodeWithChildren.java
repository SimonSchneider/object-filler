package org.simonschneider.test.graph;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Set;

interface NodeWithChildren<T> extends Node<T> {
  Set<Type> getChildTypes();

  void addChildNode(Node<?> child);

  Collection<Node<?>> getChildren();
}
