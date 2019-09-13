package org.simonschneider.test;

public class Utils {

  public static <R> R safe(ThrowableSupplier<R> supplier) {
    try {
      return supplier.get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @FunctionalInterface
  public interface ThrowableSupplier<R> {
    R get() throws Exception;
  }
}
