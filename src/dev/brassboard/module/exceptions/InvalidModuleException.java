package dev.brassboard.module.exceptions;

public class InvalidModuleException extends RuntimeException
{
    public InvalidModuleException(String message)
    {
        super(message);
    }

    public InvalidModuleException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
