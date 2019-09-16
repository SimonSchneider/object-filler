package org.simonschneider.test.graph;

import java.lang.reflect.Type;

interface Node<T> {

  Type getType();

  T create();

  Node<T> copy();
}
