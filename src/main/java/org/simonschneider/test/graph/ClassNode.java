package org.simonschneider.test.graph;

import static java.util.Comparator.comparingInt;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.simonschneider.test.core.UnableToLocateSuitableConstructorException;
import org.simonschneider.test.core.Utils;

public class ClassNode<T> implements NodeWithChildren<T> {
  private final Class<T> clazz;
  private final Set<Type> childTypes;
  private final Map<Field, Node<?>> children = new HashMap<>();

  ClassNode(Class<T> clazz) {
    this.clazz = clazz;
    this.childTypes =
        Arrays.stream(clazz.getDeclaredFields())
            .map(Field::getGenericType)
            .collect(Collectors.toSet());
  }

  @Override
  public Type getType() {
    return clazz;
  }

  @Override
  public T create() {
    T instance = instantiateClassFromConstructor();
    children.forEach(
        (f, n) -> {
          f.setAccessible(true);
          Utils.toUnchecked(() -> f.set(instance, n.create()));
        });
    return instance;
  }

  @Override
  public Node<T> copy() {
    return new ClassNode<>(clazz);
  }

  @Override
  public void addChildNode(Node<?> child) {
    Arrays.stream(clazz.getDeclaredFields())
        .filter(f -> f.getGenericType().equals(child.getType()))
        .forEach(f -> children.put(f, child));
  }

  @Override
  public Collection<Node<?>> getChildren() {
    return children.values();
  }

  @Override
  public Set<Type> getChildTypes() {
    return childTypes;
  }

  private T instantiateClassFromConstructor() {
    return getSortedAccessibleConstructors(clazz).stream()
        .map(c -> constructClassFrom(c))
        .flatMap(Optional::stream)
        .findFirst()
        .orElseThrow(() -> new UnableToLocateSuitableConstructorException(clazz));
  }

  private List<Constructor<T>> getSortedAccessibleConstructors(Class<T> clazz) {
    return Arrays.stream(clazz.getDeclaredConstructors())
        .map(c -> (Constructor<T>) c)
        .sorted(comparingInt(c -> c.getParameterCount()))
        .filter(Constructor::trySetAccessible)
        .collect(Collectors.toList());
  }

  private Optional<T> constructClassFrom(Constructor<T> constructor) {
    try {
      Object[] constructorParameters =
          Arrays.stream(constructor.getGenericParameterTypes()).map(t -> null).toArray();
      T instance = constructor.newInstance(constructorParameters);
      return Optional.of(instance);
    } catch (Exception e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }
}
