package com.github.stephanenicolas.injectresource;

/**
 * An exception that can be thrown by the {@link InjectResourceProcessor}.
 */
public class InjectResourceException extends Exception {
  public InjectResourceException(String message) {
    super(message);
  }

  public InjectResourceException(String message, Throwable cause) {
    super(message, cause);
  }
}
