package org.simonschneider.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.simonschneider.test.core.RandomObjectFiller;
import org.simonschneider.test.graph.GraphObjectFiller;

class RandomObjectFillerTest {

  static Stream<Arguments> fillers() {
    return Stream.of(
        Arguments.of(RandomObjectFiller.builder().build()),
        Arguments.of(GraphObjectFiller.simple()));
  }

  @MethodSource("fillers")
  @ParameterizedTest
  void shouldFillSimpleString(ObjectFiller objectFiller) {
    String filledString = objectFiller.createAndFill(String.class);
    assertThat(filledString, notNullValue());
  }

  @MethodSource("fillers")
  @ParameterizedTest
  void shouldFillClassContainingMap(ObjectFiller objectFiller) {
    ClassWithMap classWithMap = objectFiller.createAndFill(ClassWithMap.class);

    assertThat(classWithMap, notNullValue());
    assertThat(classWithMap.map, notNullValue());
    assertThat(classWithMap.map.size(), is(1));
    assertThat(classWithMap.map, hasEntry(isA(String.class), isA(SimpleType.class)));
  }

  @MethodSource("fillers")
  @ParameterizedTest
  void shouldFillIfOnlyNoArgsConstructorAndFillingFields(ObjectFiller objectFiller) {
    NoArgsConstructorClass noArgsConstructorClass =
        objectFiller.createAndFill(NoArgsConstructorClass.class);

    assertThat(noArgsConstructorClass, notNullValue());
    assertThat(noArgsConstructorClass.test, notNullValue());
  }

  @MethodSource("fillers")
  @ParameterizedTest
  void shouldFillIfOnlySomeArgsConstructorIfFillingOnAnyArgsConstructorFields(
      ObjectFiller objectFiller) {
    ClassWithSomeArgsConstructor classWithSomeArgsConstructor =
        objectFiller.createAndFill(ClassWithSomeArgsConstructor.class);

    assertThat(classWithSomeArgsConstructor, notNullValue());
    assertThat(classWithSomeArgsConstructor.constructorField, notNullValue());
    assertThat(classWithSomeArgsConstructor.nonConstructorField, notNullValue());
  }

  @Test
  void shouldComplainWhenTryingToCreateObjectWithCyclicDependency() {
    ObjectFiller objectFiller = GraphObjectFiller.simple();
    CyclicA cyclicA = objectFiller.createAndFill(CyclicA.class);
    assertThat(cyclicA, notNullValue());
    assertThat(cyclicA.cyclicB, notNullValue());
    assertThat(cyclicA.cyclicB.cyclicA, notNullValue());
    assertThat(cyclicA.cyclicB.cyclicA.cyclicB, notNullValue());
    assertThat(cyclicA.cyclicB.cyclicA.cyclicB.cyclicA, nullValue());
  }

  @Test
  void shouldComplainWhenTryingToCreateObjectWithLargeCyclicDependency() {
    ObjectFiller objectFiller = GraphObjectFiller.simple();
    CyclicLargeA cyclicLargeA = objectFiller.createAndFill(CyclicLargeA.class);
    assertThat(cyclicLargeA, notNullValue());
    assertThat(cyclicLargeA.map.keySet(), hasSize(1));
  }

  static class CyclicA {
    private CyclicB cyclicB;
  }

  static class CyclicB {
    private CyclicA cyclicA;
    private String string;
  }

  static class CyclicLargeA {
    private Map<String, CyclicLargeB> map;
  }

  static class CyclicLargeB {
    private CyclicLargeC c;
  }

  static class CyclicLargeC {
    private Map<CyclicLargeA, String> map;
  }

  static class ClassWithMap {
    private final Map<String, SimpleType> map;

    public ClassWithMap(Map<String, SimpleType> map) {
      this.map = map;
    }
  }

  static class SimpleType {
    private final String string;

    public SimpleType(String string) {
      this.string = string;
    }
  }

  static class NoArgsConstructorClass {
    private String test;
  }

  static class ClassWithSomeArgsConstructor {
    private String nonConstructorField;
    private final String constructorField;

    public ClassWithSomeArgsConstructor(String constructorField) {
      this.constructorField = constructorField;
    }
  }
}
