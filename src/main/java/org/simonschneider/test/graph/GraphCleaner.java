package org.simonschneider.test.graph;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class GraphCleaner<R> {

  private final Node<R> rootNode;
  private final Map<Type, ?> emptyMap;

  GraphCleaner(Node<R> rootNode, Map<Type, ?> emptyMap) {
    this.rootNode = rootNode;
    this.emptyMap = emptyMap;
  }

  Node<R> clean() {
    return visitNode(new HashSet<>(), rootNode);
  }

  private <T> Node<T> visitNode(Set<Type> visitedTypes, Node<T> node) {
    if (node instanceof NodeWithChildren) {
      return visitNodeWithChildren(visitedTypes, (NodeWithChildren<T>) node);
    } else {
      return node.copy();
    }
  }

  private <T> Node<T> visitNodeWithChildren(Set<Type> visitedTypes, NodeWithChildren<T> node) {
    if (visitedTypes.contains(node.getType())) {
      return fixCycle(node);
    }
    visitedTypes.add(node.getType());
    Collection<Node<?>> childTypes = node.getChildren();
    NodeWithChildren<T> newNode = (NodeWithChildren<T>) node.copy();
    childTypes.stream()
        .map(n -> visitNode(new HashSet<>(visitedTypes), n))
        .forEach(newNode::addChildNode);
    return newNode;
  }

  private <T> Node<T> fixCycle(Node<T> node) {
    return visitNodeAndFixCycle(new HashSet<>(), node);
  }

  private <T> Node<T> visitNodeAndFixCycle(Set<Type> visitedTypes, Node<T> node) {
    if (node instanceof NodeWithChildren) {
      return visitNodeWithChildrenAndFixCycle(visitedTypes, (NodeWithChildren<T>) node);
    } else {
      return node.copy();
    }
  }

  @SuppressWarnings("unchecked")
  private <T> Node<T> visitNodeWithChildrenAndFixCycle(
      Set<Type> visitedTypes, NodeWithChildren<T> node) {
    if (emptyMap.containsKey(getType(node.getType()))) {
      return new LeafNode<>(node.getType(), () -> (T) emptyMap.get(getType(node.getType())));
    }
    if (visitedTypes.contains(node.getType())) {
      return new LeafNode<>(node.getType(), () -> null);
    }
    visitedTypes.add(node.getType());
    Collection<Node<?>> childTypes = node.getChildren();
    NodeWithChildren<T> newNode = (NodeWithChildren<T>) node.copy();
    childTypes.stream()
        .map(n -> visitNodeAndFixCycle(new HashSet<>(visitedTypes), n))
        .forEach(newNode::addChildNode);
    return newNode;
  }

  private Type getType(Type type) {
    if (type instanceof ParameterizedType) {
      return ((ParameterizedType) type).getRawType();
    } else {
      return type;
    }
  }
}
