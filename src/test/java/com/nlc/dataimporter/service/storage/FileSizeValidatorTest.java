package com.nlc.dataimporter.service.storage;

import com.nlc.dataimporter.utils.TestUtils;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.Assert.*;

public class FileSizeValidatorTest {

    @Test(expected=RuntimeException.class)
    public void givenEmptyFile_whenValidation_thenExceptionIsReturned() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "../../file.txt",
                "text/plain", new byte[0]);

        //when
        new FileSizeValidator().validate(file);
    }

    @Test
    public void givenFileWithContent_whenValidation_thenValidationIsPassed() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "../../file.txt",
                "text/plain", "content".getBytes());

        //when
        new FileSizeValidator().validate(file);
    }

}