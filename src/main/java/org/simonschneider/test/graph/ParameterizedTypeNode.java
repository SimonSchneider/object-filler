package org.simonschneider.test.graph;

import static java.util.stream.Collectors.toSet;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ParameterizedTypeNode<T> implements NodeWithChildren<T> {

  private final ParameterizedType type;
  private final Set<Type> childTypes;
  private final Map<Type, Node<?>> children = new HashMap<>();
  private final BiFunction<ParameterizedType, Function<Type, ?>, T> builder;

  ParameterizedTypeNode(
      ParameterizedType type, BiFunction<ParameterizedType, Function<Type, ?>, T> builder) {
    this.type = type;
    this.childTypes = Arrays.stream(type.getActualTypeArguments()).collect(toSet());
    this.builder = builder;
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public T create() {
    return builder.apply(type, t -> children.get(t).create());
  }

  @Override
  public Node<T> copy() {
    return new ParameterizedTypeNode<>(type, builder);
  }

  @Override
  public Set<Type> getChildTypes() {
    return childTypes;
  }

  @Override
  public void addChildNode(Node<?> child) {
    Arrays.stream(type.getActualTypeArguments())
        .filter(f -> f.equals(child.getType()))
        .forEach(f -> children.put(f, child));
  }

  @Override
  public Collection<Node<?>> getChildren() {
    return children.values();
  }
}
