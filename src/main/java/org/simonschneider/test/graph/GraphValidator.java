package org.simonschneider.test.graph;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class GraphValidator {

  private final Map<Type, Node<?>> graph;
  private final Type rootType;

  GraphValidator(Map<Type, Node<?>> graph, Type rootType) {
    this.graph = graph;
    this.rootType = rootType;
  }

  void validate() {
    Node<?> rootNode = graph.get(rootType);
    visitNode(new HashSet<>(), rootNode);
  }

  private void visitNode(Set<Type> visitedTypes, Node<?> node) {
    if (node instanceof NodeWithChildren) {
      visitNodeWithChildren(visitedTypes, (NodeWithChildren<?>) node);
    }
  }

  private void visitNodeWithChildren(Set<Type> visitedTypes, NodeWithChildren<?> node) {
    if (visitedTypes.contains(node.getType())) {
      throw new CyclicDependencyException(node.getType());
    }
    visitedTypes.add(node.getType());
    Collection<Node<?>> childTypes = node.getChildren();
    childTypes.forEach(n -> visitNode(new HashSet<>(visitedTypes), n));
  }
}
