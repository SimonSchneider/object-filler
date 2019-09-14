package org.simonschneider.test.core;

public class Utils {

  public static <R> R toUnchecked(ThrowableSupplier<R> supplier) {
    try {
      return supplier.get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void toUnchecked(ThrowableRunnable runnable) {
    try {
      runnable.run();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @FunctionalInterface
  public interface ThrowableSupplier<R> {
    R get() throws Exception;
  }

  @FunctionalInterface
  public interface ThrowableRunnable {
    void run() throws Exception;
  }
}
