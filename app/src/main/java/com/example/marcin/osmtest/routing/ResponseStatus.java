package com.example.marcin.osmtest.routing;

public enum ResponseStatus
{
    OK(0),
    INVALID(-1),
    TECHNICAL(2);

    private int value;
    public int getValue()
    {
        return value;
    }
    ResponseStatus(int value)
    {
        this.value = value;
    }
}
