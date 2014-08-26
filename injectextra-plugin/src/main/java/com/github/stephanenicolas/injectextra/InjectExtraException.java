package com.github.stephanenicolas.injectextra;

/**
 * An exception that can be thrown by the {@link InjectExtraProcessor}.
 */
public class InjectExtraException extends Exception {
  public InjectExtraException(String message) {
    super(message);
  }

  public InjectExtraException(String message, Throwable cause) {
    super(message, cause);
  }
}
