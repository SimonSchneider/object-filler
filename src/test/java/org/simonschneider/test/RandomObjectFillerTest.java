package org.simonschneider.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.simonschneider.test.core.RandomObjectFiller;

class RandomObjectFillerTest {

  @Test
  void shouldFillSimpleString() {
    String filledString = RandomObjectFiller.simple().createAndFill(String.class);
    assertThat(filledString, notNullValue());
  }

  @Test
  void shouldFillClassContainingMap() {
    ClassWithMap classWithMap = RandomObjectFiller.simple().createAndFill(ClassWithMap.class);

    assertThat(classWithMap, notNullValue());
    assertThat(classWithMap.map, notNullValue());
    assertThat(classWithMap.map.size(), is(1));
    assertThat(classWithMap.map, hasEntry(isA(String.class), isA(SimpleType.class)));
  }

  @Test
  void shouldNotFillIfOnlyNoArgsConstructorAndNotFillingFields() {
    NoArgsConstructorClass noArgsConstructorClass =
        RandomObjectFiller.simple().createAndFill(NoArgsConstructorClass.class);

    assertThat(noArgsConstructorClass, notNullValue());
    assertThat(noArgsConstructorClass.test, nullValue());
  }

  @Test
  void shouldFillIfOnlyNoArgsConstructorAndFillingFields() {
    NoArgsConstructorClass noArgsConstructorClass =
        RandomObjectFiller.builder()
            .with((c, i) -> c.getParameterCount() == 0)
            .build()
            .createAndFill(NoArgsConstructorClass.class);

    assertThat(noArgsConstructorClass, notNullValue());
    assertThat(noArgsConstructorClass.test, notNullValue());
  }

  @Test
  void shouldFillIfOnlySomeArgsConstructorIfNotFillingFields() {
    ClassWithSomeArgsConstructor classWithSomeArgsConstructor =
        RandomObjectFiller.simple().createAndFill(ClassWithSomeArgsConstructor.class);

    assertThat(classWithSomeArgsConstructor, notNullValue());
    assertThat(classWithSomeArgsConstructor.constructorField, notNullValue());
    assertThat(classWithSomeArgsConstructor.nonConstructorField, nullValue());
  }

  @Test
  void shouldFillIfOnlySomeArgsConstructorIfFillingOnNoArgsConstructorFields() {
    ClassWithSomeArgsConstructor classWithSomeArgsConstructor =
        RandomObjectFiller.simple().createAndFill(ClassWithSomeArgsConstructor.class);

    assertThat(classWithSomeArgsConstructor, notNullValue());
    assertThat(classWithSomeArgsConstructor.constructorField, notNullValue());
    assertThat(classWithSomeArgsConstructor.nonConstructorField, nullValue());
  }

  @Test
  void shouldFillIfOnlySomeArgsConstructorIfFillingOnAnyArgsConstructorFields() {
    ClassWithSomeArgsConstructor classWithSomeArgsConstructor =
        RandomObjectFiller.builder()
            .with((c, i) -> true)
            .build()
            .createAndFill(ClassWithSomeArgsConstructor.class);

    assertThat(classWithSomeArgsConstructor, notNullValue());
    assertThat(classWithSomeArgsConstructor.constructorField, notNullValue());
    assertThat(classWithSomeArgsConstructor.nonConstructorField, notNullValue());
  }

  public static class ClassWithMap {
    private final Map<String, SimpleType> map;

    public ClassWithMap(Map<String, SimpleType> map) {
      this.map = map;
    }
  }

  public static class SimpleType {
    private final String string;

    public SimpleType(String string) {
      this.string = string;
    }
  }

  public static class NoArgsConstructorClass {
    private String test;
  }

  public static class ClassWithSomeArgsConstructor {
    private String nonConstructorField;
    private String constructorField;

    public ClassWithSomeArgsConstructor(String constructorField) {
      this.constructorField = constructorField;
    }
  }
}
