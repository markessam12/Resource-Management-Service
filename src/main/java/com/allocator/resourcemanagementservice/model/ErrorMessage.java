package com.allocator.resourcemanagementservice.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Error message is a class to send error message and error codes in the API response.
 */
@XmlRootElement
public class ErrorMessage {
  private String message;
  private int errorCode;

  public ErrorMessage(){}

  public ErrorMessage(String message, int errorCode){
    super();
    this.message = message;
    this.errorCode = errorCode;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

}
