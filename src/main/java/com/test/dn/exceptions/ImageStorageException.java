package com.test.dn.exceptions;

public class ImageStorageException extends Exception
{
    private int errorCode;
    private String errorMsg;

    public ImageStorageException(int errorCode, String errorMsg)
    {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public ImageStorageException(int errorCode, String errorMsg, Throwable throwable)
    {
        super(errorMsg, throwable);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public int getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(int errorCode)
    {
        this.errorCode = errorCode;
    }

    public String getErrorMsg()
    {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg)
    {
        this.errorMsg = errorMsg;
    }
}
