package org.simonschneider.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

class ObjectFillerTest {

  @Test
  void shouldFillSimpleString() {
    String filledString = new ObjectFiller().createAndFill(String.class);
    assertThat(filledString, notNullValue());
  }
}
