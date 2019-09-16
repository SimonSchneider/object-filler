package org.simonschneider.test.creators;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Function;
import org.simonschneider.test.ClassFactory;
import org.simonschneider.test.EmptyInstanceFactory;
import org.simonschneider.test.GenericTypeFactory;
import org.simonschneider.test.GenericTypeFactory.GenericTypeCreator;
import org.simonschneider.test.core.MapBackedClassFactory;
import org.simonschneider.test.core.MapBackedEmptyInstanceFactory;
import org.simonschneider.test.core.MapBackedGenericTypeFactory;

public class Creators {
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
        (f, t) -> Map.of(f.apply(t[0]), f.apply(t[1])),
        List.class,
        (f, t) -> List.of(f.apply(t[0])),
        Set.class,
        (f, t) -> Set.of(f.apply(t[0])),
        Optional.class,
        (f, t) -> Optional.of(f.apply(t[0])),
        Future.class,
        (f, t) -> CompletableFuture.completedFuture(f.apply(t[0])));
  }

  public static Map<Type, Object> emptyInstances() {
    return Map.of(
        Map.class,
        emptyMap(),
        List.class,
        emptyList(),
        Set.class,
        emptySet(),
        Optional.class,
        Optional.empty());
  }

  public static ClassFactory defaultClassFactory() {
    Map<Class, Function<Random, Object>> initialCreators = new HashMap<>();
    initialCreators.putAll(getPrimitiveCreators());
    initialCreators.putAll(getStringCreator());
    initialCreators.putAll(getDateTimeCreators());
    return new MapBackedClassFactory(initialCreators);
  }

  public static GenericTypeFactory defaultGenericTypeFactory() {
    Map<Type, GenericTypeCreator> initialCreators = new HashMap<>(collectionCreators());
    return new MapBackedGenericTypeFactory(initialCreators);
  }

  public static EmptyInstanceFactory defaultEmptyInstanceFactory() {
    Map<Type, Object> initial = new HashMap<>(emptyInstances());
    return new MapBackedEmptyInstanceFactory(initial);
  }
}
