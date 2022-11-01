package com.allocator.resourcemanagementservice.exception;

/**
 * The Server not found exception is thrown when user tries to access non-existing server/severs.
 */
public class ServerNotFoundException extends Exception{

  /**
   * Instantiates a new Server-not-found exception.
   *
   * @param s the Error message string
   */
  public ServerNotFoundException(String s){
    super(s);
  }
}
