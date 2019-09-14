package org.simonschneider.test;

public class UnableToLocateSuitableConstructorException extends RuntimeException {

  public UnableToLocateSuitableConstructorException(Class<?> clazz) {
    super(String.format("Unable to locate a suitable constructor for %s", clazz));
  }
}
