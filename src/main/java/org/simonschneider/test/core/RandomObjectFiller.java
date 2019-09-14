package org.simonschneider.test.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Random;
import org.simonschneider.test.ClassFactory;
import org.simonschneider.test.FieldFilling;
import org.simonschneider.test.GenericTypeFactory;
import org.simonschneider.test.ObjectFiller;
import org.simonschneider.test.creators.Creators;

public class RandomObjectFiller implements ObjectFiller {
  private final Random random = new Random();
  private final ClassFactory classFactory;
  private final GenericTypeFactory genericTypeFactory;
  private final ConstructorInstantiator constructorInstantiator;

  public RandomObjectFiller(
      ClassFactory classFactory,
      GenericTypeFactory genericTypeFactory,
      ConstructorInstantiator constructorInstantiator) {
    this.classFactory = classFactory;
    this.genericTypeFactory = genericTypeFactory;
    this.constructorInstantiator = constructorInstantiator;
  }

  public <T> T createAndFill(Type type) {
    return createInstanceOfType(type);
  }

  @SuppressWarnings("unchecked")
  private <T> T createInstanceOfType(Type type) {
    if (type instanceof ParameterizedType) {
      return createInstanceOfParameterizedType((ParameterizedType) type);
    } else if (type instanceof Class) {
      return createInstanceOfClass((Class<T>) type);
    } else {
      throw new RuntimeException("unexpected type " + type.toString());
    }
  }

  private <T> T createInstanceOfParameterizedType(ParameterizedType type) {
    if (genericTypeFactory.canBuild(type)) {
      return genericTypeFactory.buildInstance(type, this);
    } else {
      throw new RuntimeException("unexpected generic type " + type.toString());
    }
  }

  private <T> T createInstanceOfClass(Class<T> clazz) {
    if (clazz.isEnum()) {
      return createInstanceOfEnum(clazz);
    } else if (classFactory.canBuild(clazz)) {
      return classFactory.buildInstance(random, clazz);
    } else {
      return Utils.toUnchecked(() -> constructorInstantiator.createAndFill(this, clazz));
    }
  }

  private <T> T createInstanceOfEnum(Class<T> clazz) {
    T[] enumValues = clazz.getEnumConstants();
    return enumValues[random.nextInt(enumValues.length)];
  }

  public static RandomObjectFiller simple() {
    return RandomObjectFiller.builder().build();
  }

  public static RandomObjectFiller.Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private ClassFactory classFactory = Creators.defaultClassFactory();
    private GenericTypeFactory genericTypeFactory = Creators.defaultGenericTypeFactory();
    private FieldFilling fieldFilling = (c, i) -> false;

    public Builder with(ClassFactory classFactory) {
      this.classFactory = classFactory;
      return this;
    }

    public Builder with(GenericTypeFactory genericTypeFactory) {
      this.genericTypeFactory = genericTypeFactory;
      return this;
    }

    public Builder with(FieldFilling fieldFilling) {
      this.fieldFilling = fieldFilling;
      return this;
    }

    public RandomObjectFiller build() {
      ConstructorInstantiator constructorInstantiator = new ConstructorInstantiator(fieldFilling);
      return new RandomObjectFiller(classFactory, genericTypeFactory, constructorInstantiator);
    }
  }
}
