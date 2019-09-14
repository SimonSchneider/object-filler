package org.simonschneider.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.simonschneider.test.core.InstanceIsNotKeyTypeException;
import org.simonschneider.test.core.MapBackedClassFactory;
import org.simonschneider.test.creators.Creators;

class ClassFactoryTest {

  @Test
  void shouldBePossibleToPutAllPrimitiveTypesInFactoryAndCreateTypes() {
    ClassFactory classFactory = new MapBackedClassFactory(Creators.getPrimitiveCreators());

    Object generatedPrimitiveType = classFactory.buildInstance(new Random(), Integer.TYPE);
    Object generatedBoxedType = classFactory.buildInstance(new Random(), Integer.class);

    assertThat(generatedPrimitiveType, Matchers.isA(Integer.TYPE));
    assertThat(generatedBoxedType, Matchers.isA(Integer.class));
  }

  @Test
  void cannotPutNonMatchingKeyAndCreatorInFactory() {
    MapBackedClassFactory objectFactory = new MapBackedClassFactory();
    assertThrows(
        InstanceIsNotKeyTypeException.class,
        () -> objectFactory.put(Integer.class, Random::nextBoolean));
  }
}
