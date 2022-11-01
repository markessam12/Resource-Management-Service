package com.allocator.resourcemanagementservice.exception;

/**
 * The Refused request exception is thrown when the user request doesn't meet conditions
 * It's thrown when user request memory more than the server's maximum memory
 */
public class RefusedRequestException extends Exception{

  /**
   * Instantiates a new Refused-request exception.
   *
   * @param s the Error message string
   */
  public RefusedRequestException(String s){
    super(s);
  }
}
