package org.simonschneider.test.core;

import static java.util.Comparator.comparingInt;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.simonschneider.test.FieldFilling;
import org.simonschneider.test.ObjectFiller;

public class ConstructorInstantiator {
  private final FieldFilling fieldFilling;

  public ConstructorInstantiator(FieldFilling fieldFilling) {
    this.fieldFilling = fieldFilling;
  }

  public <T> T createAndFill(ObjectFiller objectFiller, Class<T> clazz) {
    return getSortedAccessibleConstructors(clazz).stream()
        .map(c -> constructClassFrom(objectFiller, c))
        .flatMap(Optional::stream)
        .findFirst()
        .orElseThrow(() -> new UnableToLocateSuitableConstructorException(clazz));
  }

  @SuppressWarnings("toUnchecked")
  private <T> List<Constructor<T>> getSortedAccessibleConstructors(Class<T> clazz) {
    return Arrays.stream(clazz.getDeclaredConstructors())
        .map(c -> (Constructor<T>) c)
        .sorted(comparingInt(c -> ((Constructor<T>) c).getParameterCount()).reversed())
        .filter(Constructor::trySetAccessible)
        .collect(Collectors.toList());
  }

  private <T> Optional<T> constructClassFrom(
      ObjectFiller objectFiller, Constructor<T> constructor) {
    try {
      Object[] constructorParameters =
          Arrays.stream(constructor.getGenericParameterTypes())
              .map(objectFiller::createAndFill)
              .toArray();
      T instance = constructor.newInstance(constructorParameters);
      if (fieldFilling.shouldFillFields(constructor, instance)) {
        fillFields(objectFiller, instance);
      }
      return Optional.of(instance);
    } catch (Exception e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

  private <T> void fillFields(ObjectFiller objectFiller, T instance) {
    for (Field field : instance.getClass().getDeclaredFields()) {
      field.setAccessible(true);
      Object value = objectFiller.createAndFill(field.getGenericType());
      Utils.toUnchecked(() -> field.set(instance, value));
    }
  }
}
