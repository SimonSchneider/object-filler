package org.simonschneider.test;

import java.util.Random;

public interface ClassFactory {

  <T> boolean canBuild(Class<T> clazz);

  <T> T buildInstance(Random random, Class<T> clazz);
}
