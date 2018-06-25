package com.nlc.dataimporter.service.storage;

import com.nlc.dataimporter.service.DataValidator;
import com.nlc.dataimporter.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class FileSystemStorageServiceTest {

    private StorageProperties properties = new StorageProperties();
    private FileSystemStorageService service;
    private List<DataValidator> validatorsList;

    class FileSystemStorageServiceForTest extends FileSystemStorageService {

        FileSystemStorageServiceForTest(StorageProperties properties) {
            super(properties);
        }

        @Override
        protected List<DataValidator> getValidators() {
            return validatorsList;
        }
    }

    @Before
    public void startUp() {
        properties.setLocation("target/files/" + Math.abs(new Random().nextLong()));
        service = new FileSystemStorageServiceForTest(properties);
        service.init();

        validatorsList = new ArrayList<>();
    }

    @Test
    public void loadNonExistent() {
        //when
        Path loaded = service.load("foo.txt");

        //then
        assertThat(loaded).doesNotExist();
    }

    @Test
    public void saveAndLoad() {
        //when
        service.store(new MockMultipartFile("foo", "foo.txt", MediaType.TEXT_PLAIN_VALUE,
                "Hello World".getBytes()));

        Path loaded = service.load("foo.txt");

        //then
        assertThat(loaded).exists();
    }

    @Test(expected = StorageException.class)
    public void whenStoringWrongFile_exceptionIsThrown() throws IOException {
        //given
        MultipartFile file = mock(MultipartFile.class);
        given(file.getInputStream()).willThrow(new IOException());

        //when
        service.store(file);
    }

    @Test(expected = StorageException.class)
    public void saveNotPermitted() {
        //given
        DataValidator mockedValidator = mock(DataValidator.class);
        validatorsList.add(mockedValidator);

        doThrow(new StorageException("")).when(mockedValidator).validate(any());

        //when
        service.store(new MockMultipartFile("foo", "../foo.txt",
                MediaType.TEXT_PLAIN_VALUE, "Hello World".getBytes()));
    }

    @Test
    public void savePermitted() {
        //when
        service.store(new MockMultipartFile("foo", "bar/../foo.txt",
                MediaType.TEXT_PLAIN_VALUE, "Hello World".getBytes()));

        //then
        // no exceptions
    }

    @Test
    public void loadFileAsResource() throws IOException {
        //given
        MockMultipartFile multipartFile = new MockMultipartFile("foo", "bar/../foo.txt", MediaType.TEXT_PLAIN_VALUE, "Hello World".getBytes());

        //when
        service.store(multipartFile);
        Resource resource = service.loadAsResource("foo.txt");

        //then
        assertThat(resource.contentLength()).isEqualTo(multipartFile.getSize());
    }

    @Test
    public void loadFileContentAsString() throws IOException {
        //given
        String fileContent = TestUtils.IMPORT_LINE_FOR_TEST + System.lineSeparator() + TestUtils.IMPORT_LINE_FOR_TEST;
        MockMultipartFile multipartFile = new MockMultipartFile("foo", "foo.txt", MediaType.TEXT_PLAIN_VALUE, fileContent.getBytes());

        //when
        service.store(multipartFile);
        String content = service.loadFileContentAsString("foo.txt");

        //then
        assertThat(content).isEqualTo(fileContent);
    }

    @Test(expected = StorageException.class)
    public void givenStorageService_whenLoadFileContentAsString_thenThrowException() throws IOException {
        //given
        String fileContent = TestUtils.IMPORT_LINE_FOR_TEST + System.lineSeparator() + TestUtils.IMPORT_LINE_FOR_TEST;
        MockMultipartFile multipartFile = new MockMultipartFile("foo", "foo.txt", MediaType.TEXT_PLAIN_VALUE, fileContent.getBytes());
        service = new FileSystemStorageServiceForTest(properties) {
            @Override
            protected BufferedReader getBufferedReader(InputStream inputStream) throws IOException {
                throw new IOException();
            }
        };

        //when
        service.store(multipartFile);
        String content = service.loadFileContentAsString("foo.txt");

        //then
        assertThat(content).isEqualTo(fileContent);
    }

    @Test(expected = StorageException.class)
    public void givenStorageService_whenLoadFileContentAsString_thenFailsAtLoadAsResource() throws IOException {
        //given
        String fileContent = TestUtils.IMPORT_LINE_FOR_TEST + System.lineSeparator() + TestUtils.IMPORT_LINE_FOR_TEST;
        MockMultipartFile multipartFile = new MockMultipartFile("foo", "foo.txt", MediaType.TEXT_PLAIN_VALUE, fileContent.getBytes());
        Resource mockedResource = mock(Resource.class);

        given(mockedResource.getInputStream()).willThrow(new IOException());

        service = new FileSystemStorageServiceForTest(properties) {
            @Override
            public Resource loadAsResource(String filename) {
                return mockedResource;
            }
        };

        //when
        service.store(multipartFile);
        String content = service.loadFileContentAsString("foo.txt");

        //then
        assertThat(content).isEqualTo(fileContent);
    }
}