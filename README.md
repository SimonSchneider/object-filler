# object-filler

This small lib is a testing lib to help with creating objects with random test data. 
The reasoning for why this is important is that in some tests the actual data in the 
objects are irrelevant, they simply need to be initialized with non-null data, this 
is where the ObjectFiller comes in handy, it will instantiate and fill any object with 
random data.

```java
Foo foo = ObjectFiller.simple().createAndFill(Foo.class)
```

Comment about how leaf classes need to be created and how we use classfactory 
and genericTypeFactories that can be extended by user additions to define any 
leaf node instance creation.
Comment about how all primitives and core types are supported but any additions 
to core java classes are appreciated.

We also appreciate any extensions that add non-java-core-leaf nodes