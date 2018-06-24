package com.nlc.dataimporter.service.storage;

import org.junit.Test;

public class StorageFileNotFoundExceptionTest {

    @Test(expected = StorageFileNotFoundException.class)
    public void testException() {
        //given
        StorageFileNotFoundException exception = new StorageFileNotFoundException("");

        //when
        throw exception;
    }

    @Test(expected = StorageFileNotFoundException.class)
    public void testExceptionFromOtherException() {
        //given
        RuntimeException baseException = new RuntimeException();
        StorageFileNotFoundException exception = new StorageFileNotFoundException("", baseException);

        //when
        throw exception;
    }
}