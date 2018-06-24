package com.nlc.dataimporter.service.storage;

import com.nlc.dataimporter.utils.TestUtils;
import org.junit.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class FileNameValidatorTest {

    @Test(expected=RuntimeException.class)
    public void givenErroneousFile_whenValidating_ExceptionIsReturned() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "../../file.txt",
                "text/plain", TestUtils.IMPORT_LINE_FOR_TEST.getBytes());

        //when
        new FileNameValidator().validate(file);
    }

    @Test(expected=RuntimeException.class)
    public void givenFileWithEmptyFilename_whenValidating_ExceptionIsReturned() throws Exception {
        //given
        MultipartFile file = mock(MultipartFile.class);
        given(file.getOriginalFilename()).willReturn(null);

        //when
        new FileNameValidator().validate(file);
    }

    @Test(expected=RuntimeException.class)
    public void givenNullObject_whenValidating_ExceptionIsReturned() throws Exception {
        //when
        new FileNameValidator().validate(null);
    }

    @Test
    public void givenNormalFile_whenValidating_ValidationIsPassed() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "file.txt",
                "text/plain", TestUtils.IMPORT_LINE_FOR_TEST.getBytes());

        //when
        new FileNameValidator().validate(file);
    }

}