package org.simonschneider.test.graph;

import java.lang.reflect.Type;
import java.util.function.Supplier;

public class LeafNode<T> implements Node<T> {
  private final Type type;
  private final Supplier<T> supplier;

  public LeafNode(Type type, Supplier<T> supplier) {
    this.type = type;
    this.supplier = supplier;
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public T create() {
    return supplier.get();
  }
}
