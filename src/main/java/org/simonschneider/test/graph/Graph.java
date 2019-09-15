package org.simonschneider.test.graph;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import org.simonschneider.test.ClassFactory;
import org.simonschneider.test.GenericTypeFactory;

class Graph {

  private final Map<Type, Node<?>> graph = new HashMap<>();
  private final Type type;
  private final Random random;
  private final ClassFactory classFactory;
  private final GenericTypeFactory genericTypeFactory;

  Graph(
      Random random, ClassFactory classFactory, GenericTypeFactory genericTypeFactory, Type type) {
    this.type = type;
    this.random = random;
    this.classFactory = classFactory;
    this.genericTypeFactory = genericTypeFactory;
  }

  void prepare() {
    getType(type);
  }

  void validate() {
    new GraphValidator(graph, type).validate();
  }

  @SuppressWarnings("unchecked")
  <T> T build() {
    return (T) graph.get(type).create();
  }

  @SuppressWarnings("unchecked")
  private <T> Node<T> getType(Type type) {
    if (graph.containsKey(type)) {
      return (Node<T>) graph.get(type);
    }
    addType(type);
    return (Node<T>) graph.get(type);
  }

  private void addType(Type type) {
    if (type instanceof Class<?>) {
      addLeafOrClassNode((Class<?>) type);
    } else if (type instanceof ParameterizedType) {
      addParameterizedNode((ParameterizedType) type);
    }
  }

  private <T> void addLeafOrClassNode(Class<T> clazz) {
    if (classFactory.canBuild(clazz)) {
      graph.put(clazz, new LeafNode<>(clazz, () -> classFactory.buildInstance(random, clazz)));
    } else {
      addClassNode(clazz);
    }
  }

  private <T> void addClassNode(Class<T> clazz) {
    ClassNode<T> node = new ClassNode<>(clazz);
    addNodeWithChildren(clazz, node);
  }

  private <T> void addParameterizedNode(ParameterizedType type) {
    if (genericTypeFactory.canBuild(type)) {
      ParameterizedTypeNode<T> node =
          new ParameterizedTypeNode<>(type, genericTypeFactory::buildInstance);
      addNodeWithChildren(type, node);
    } else {
      throw new RuntimeException("unexpected generic type " + type.toString());
    }
  }

  private <T> void addNodeWithChildren(Type type, NodeWithChildren<T> node) {
    graph.put(type, node);
    Set<Type> childTypes = node.getChildTypes();
    Set<Node<Object>> childNodes =
        childTypes.stream().map(this::getType).collect(Collectors.toSet());
    childNodes.forEach(node::addChildNode);
  }
}
