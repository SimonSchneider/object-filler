package org.simonschneider.test;

import static java.util.Comparator.comparingInt;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import org.simonschneider.test.creator.CoreCreators;

public class ObjectFiller {
  private final Random random = new Random();
  private final boolean shouldFillFieldsOnNoArgsConstructor;
  private final boolean shouldFillFieldsOnAnyConstructor;
  private final ClassFactory classFactory;
  private final GenericTypeFactory genericTypeFactory;

  public ObjectFiller(
      boolean shouldFillFieldsOnNoArgsConstructor, boolean shouldFillFieldsOnAnyConstructor) {
    this.shouldFillFieldsOnNoArgsConstructor = shouldFillFieldsOnNoArgsConstructor;
    this.shouldFillFieldsOnAnyConstructor = shouldFillFieldsOnAnyConstructor;
    this.classFactory = CoreCreators.defaultClassFactory();
    this.genericTypeFactory = CoreCreators.defaultGenericTypeFactory();
  }

  public <T> T createAndFill(Type type) {
    return createInstanceOfType(type);
  }

  private <T> T createInstanceOfClass(Class<T> clazz) {
    if (clazz.isEnum()) {
      return createInstanceOfEnum(clazz);
    } else if (classFactory.canBuild(clazz)) {
      return classFactory.buildInstance(random, clazz);
    } else {
      return Utils.toUnchecked(() -> createAndFillComplexClass(clazz));
    }
  }

  private <T> T createInstanceOfEnum(Class<T> clazz) {
    T[] enumValues = clazz.getEnumConstants();
    return enumValues[random.nextInt(enumValues.length)];
  }

  private <T> T createAndFillComplexClass(Class<T> clazz) {
    return getSortedAccessibleConstructors(clazz).stream()
        .map(this::constructClassWith)
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

  private <T> Optional<T> constructClassWith(Constructor<T> constructor) {
    try {
      Object[] constructorParameters =
          Arrays.stream(constructor.getGenericParameterTypes())
              .map(this::createInstanceOfType)
              .toArray();
      T instance = constructor.newInstance(constructorParameters);
      if (shouldFillFieldsOnNoArgsConstructor && constructor.getParameterCount() == 0) {
        fillFieldsInInstance(instance);
      } else if (shouldFillFieldsOnAnyConstructor) {
        fillFieldsInInstance(instance);
      }
      return Optional.of(instance);
    } catch (Exception e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

  private <T> T createInstanceOfType(Type t) {
    if (t instanceof ParameterizedType) {
      ParameterizedType pt = (ParameterizedType) t;
      if (genericTypeFactory.canBuild(pt.getRawType())) {
        return genericTypeFactory.buildInstance(
            pt.getRawType(), this::createInstanceOfType, pt.getActualTypeArguments());
      } else {
        throw new RuntimeException("unexpected type " + t.toString());
      }
    } else if (t instanceof Class) {
      return createInstanceOfClass((Class<T>) t);
    } else {
      throw new RuntimeException("unexpected type " + t.toString());
    }
  }

  private <T> T fillFieldsInInstance(T instance) {
    for (Field field : instance.getClass().getDeclaredFields()) {
      field.setAccessible(true);
      Object value = createInstanceOfType(field.getGenericType());
      Utils.toUnchecked(() -> field.set(instance, value));
    }
    return instance;
  }
}
