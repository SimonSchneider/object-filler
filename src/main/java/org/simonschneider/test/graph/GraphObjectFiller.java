package org.simonschneider.test.graph;

import java.lang.reflect.Type;
import java.util.Random;
import org.simonschneider.test.ClassFactory;
import org.simonschneider.test.GenericTypeFactory;
import org.simonschneider.test.ObjectFiller;
import org.simonschneider.test.creators.Creators;

public class GraphObjectFiller implements ObjectFiller {

  private final Random random = new Random();
  private final ClassFactory classFactory;
  private final GenericTypeFactory genericTypeFactory;

  public GraphObjectFiller(ClassFactory classFactory, GenericTypeFactory genericTypeFactory) {
    this.classFactory = classFactory;
    this.genericTypeFactory = genericTypeFactory;
  }

  @Override
  public <T> T createAndFill(Type type) {
    Graph graph = new Graph(random, classFactory, genericTypeFactory, type);
    graph.prepare();
    graph.validate();
    return graph.build();
  }

  public static GraphObjectFiller simple() {
    return GraphObjectFiller.builder().build();
  }

  public static GraphObjectFiller.Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private ClassFactory classFactory = Creators.defaultClassFactory();
    private GenericTypeFactory genericTypeFactory = Creators.defaultGenericTypeFactory();

    public Builder with(ClassFactory classFactory) {
      this.classFactory = classFactory;
      return this;
    }

    public Builder with(GenericTypeFactory genericTypeFactory) {
      this.genericTypeFactory = genericTypeFactory;
      return this;
    }

    public GraphObjectFiller build() {
      return new GraphObjectFiller(classFactory, genericTypeFactory);
    }
  }
}
