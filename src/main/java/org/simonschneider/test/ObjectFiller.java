package org.simonschneider.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class ObjectFiller {
  private Random random = new Random();

  @SuppressWarnings("unchecked")
  public <T> T createAndFill(Class<T> clazz) {
    return (T) createRandomInstanceOfClass(clazz);
  }

  private <T> T createAndFillComplexClass(Class<T> clazz) {
    try {
      return createAndFillI(clazz);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  private <T> T createAndFillI(Class<T> clazz) throws Exception {
    Constructor<T> constructor =
        Arrays.stream(clazz.getConstructors())
            .map(c -> (Constructor<T>) c)
            .filter(c -> c.getParameterTypes().length == 0)
            .findAny()
            .orElseGet(
                () ->
                    Arrays.stream(clazz.getConstructors())
                        .map(c -> (Constructor<T>) c)
                        .findAny()
                        .get());
    constructor.setAccessible(true);
    T instance =
        constructor.newInstance(
            Arrays.stream(constructor.getParameterTypes())
                .map(this::createRandomInstanceOfClass)
                .toArray());
    for (Field field : clazz.getDeclaredFields()) {
      field.setAccessible(true);
      Object value = getRandomInstanceOfField(field);
      field.set(instance, value);
    }
    return instance;
  }

  private Object getRandomInstanceOfField(Field field) {
    if (List.class.isAssignableFrom(field.getType())) {
      return List.of(createAndFillComplexClass(field.getGenericType().getClass()));
    } else if (Map.class.isAssignableFrom(field.getType())) {
      Type[] types =
          Arrays.stream(field.getGenericType().getClass().getDeclaredFields())
              .filter(f -> "actualTypeArguments".equals(f.getName()))
              .peek(f -> f.setAccessible(true))
              .findAny()
              .map(f -> Utils.safe(() -> (Type[]) f.get(field.getGenericType())))
              .get();

      return Map.of(
          createRandomInstanceOfClass((Class<?>) types[0]),
          createRandomInstanceOfClass((Class<?>) types[1]));
    } else {
      return createRandomInstanceOfClass(field.getType());
    }
  }

  private Object createRandomInstanceOfClass(Class<?> clazz) {
    if (clazz.isEnum()) {
      Object[] enumValues = clazz.getEnumConstants();
      return enumValues[random.nextInt(enumValues.length)];
    } else if (clazz.equals(Integer.TYPE) || clazz.equals(Integer.class)) {
      return random.nextInt();
    } else if (clazz.equals(Long.TYPE) || clazz.equals(Long.class)) {
      return random.nextLong();
    } else if (clazz.equals(Double.TYPE) || clazz.equals(Double.class)) {
      return random.nextDouble();
    } else if (clazz.equals(Float.TYPE) || clazz.equals(Float.class)) {
      return random.nextFloat();
    } else if (clazz.equals(Boolean.TYPE) || clazz.equals(Boolean.class)) {
      return random.nextBoolean();
    } else if (clazz.equals(String.class)) {
      return UUID.randomUUID().toString();
    } else if (clazz.equals(Instant.class)) {
      return Instant.now();
    } else {
      return createAndFillComplexClass(clazz);
    }
  }
}
