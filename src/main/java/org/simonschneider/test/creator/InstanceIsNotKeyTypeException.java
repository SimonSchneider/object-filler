package org.simonschneider.test.creator;

public class InstanceIsNotKeyTypeException extends RuntimeException {

  public InstanceIsNotKeyTypeException(Class key, Object instance) {
    super(
        String.format(
            "instance '%s of type %s' is not a instanceOf key type '%s'",
            instance, instance.getClass(), key));
  }
}
