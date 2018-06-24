package com.nlc.dataimporter.service.storage;

import org.junit.Test;

import static org.junit.Assert.*;

public class StorageExceptionTest {

    @Test(expected = StorageException.class)
    public void testException() {
        //given
        StorageException exception = new StorageException("");

        //when
        throw exception;
    }

    @Test(expected = StorageException.class)
    public void testExceptionFromOtherException() {
        //given
        RuntimeException baseException = new RuntimeException();
        StorageException exception = new StorageException("", baseException);

        //when
        throw exception;
    }
}