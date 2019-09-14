package org.simonschneider.test.creator;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import org.simonschneider.test.ClassFactory;
import org.simonschneider.test.GenericTypeFactory;
import org.simonschneider.test.MapBackedClassFactory;
import org.simonschneider.test.MapBackedGenericTypeFactory;
import org.simonschneider.test.MapBackedGenericTypeFactory.GenericTypeCreator;

public class CoreCreators {
  public static Map<Class, Function<Random, Object>> getPrimitiveCreators() {
    return Map.of(
        Integer.TYPE, Random::nextInt,
        Integer.class, Random::nextInt,
        Long.TYPE, Random::nextLong,
        Long.class, Random::nextLong,
        Double.TYPE, Random::nextDouble,
        Double.class, Random::nextDouble,
        Float.TYPE, Random::nextFloat,
        Float.class, Random::nextFloat,
        Boolean.TYPE, Random::nextBoolean,
        Boolean.class, Random::nextBoolean);
  }

  public static Map<Class, Function<Random, Object>> getStringCreator() {
    return Map.of(String.class, r -> UUID.randomUUID().toString());
  }

  public static Map<Class, Function<Random, Object>> getDateTimeCreators() {
    return Map.of(
        Instant.class,
        r -> Instant.now(),
        Date.class,
        r -> Date.from(Instant.now()),
        OffsetDateTime.class,
        r -> OffsetDateTime.now());
  }

  public static Map<Type, GenericTypeCreator> collectionCreators() {
    return Map.of(
        Map.class,
        (b, t) -> Map.of(b.apply(t[0]), b.apply(t[1])),
        List.class,
        (b, t) -> List.of(b.apply(t[0])),
        Set.class,
        (b, t) -> Set.of(b.apply(t[0])));
  }

  public static ClassFactory defaultClassFactory() {
    Map<Class, Function<Random, Object>> initialCreators = new HashMap<>();
    initialCreators.putAll(getPrimitiveCreators());
    initialCreators.putAll(getStringCreator());
    initialCreators.putAll(getDateTimeCreators());
    return new MapBackedClassFactory(initialCreators);
  }

  public static GenericTypeFactory defaultGenericTypeFactory() {
    Map<Type, GenericTypeCreator> initialCreators = new HashMap<>();
    initialCreators.putAll(collectionCreators());
    return new MapBackedGenericTypeFactory(initialCreators);
  }
}
