package com.nlc.dataimporter.service.storage;

import com.nlc.dataimporter.service.DataValidator;
import com.nlc.dataimporter.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.Buffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class FileSystemStorageServiceTest {

    private StorageProperties properties = new StorageProperties();
    private FileSystemStorageService service;
    private List<DataValidator> validatorsList;
    private UrlResource mockedResource;
    private BufferedReader mockedBufferReader;
    private InputStreamReader mockedInputStream;

    class FileSystemStorageServiceForTest extends FileSystemStorageService {

        FileSystemStorageServiceForTest(StorageProperties properties) {
            super(properties);
        }

        @Override
        protected List<DataValidator> getValidators() {
            return validatorsList;
        }

        @Override
        protected UrlResource getResourceFromFile(Path file) throws MalformedURLException {
            return mockedResource;
        }

        @Override
        protected BufferedReader getBufferedReader(InputStream inputStream) throws IOException {
            return mockedBufferReader;
        }

        @Override
        protected InputStreamReader createInputStream(InputStream inputStream) {
            return mockedInputStream;
        }
    }

    @Before
    public void startUp() {
        properties.setLocation("target/files/" + Math.abs(new Random().nextLong()));
        service = new FileSystemStorageServiceForTest(properties);
        service.init();

        validatorsList = new ArrayList<>();

        mockedBufferReader = mock(BufferedReader.class);
        mockedResource = mock(UrlResource.class);
        mockedInputStream = mock(InputStreamReader.class);

        given(mockedResource.exists()).willReturn(true);
        given(mockedResource.isReadable()).willReturn(true);
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
        assertThat(resource).isEqualTo(mockedResource);
    }

    @Test(expected = StorageException.class)
    public void givenNullFile_wehnLoadFileAsResource_thenThrowException() throws IOException {
        //given
        MockMultipartFile multipartFile = new MockMultipartFile("foo", "bar/../foo.txt", MediaType.TEXT_PLAIN_VALUE, "Hello World".getBytes());
        given(mockedResource.exists()).willReturn(false);
        given(mockedResource.isReadable()).willReturn(false);

        //when
        service.store(multipartFile);
        Resource resource = service.loadAsResource("foo.txt");
    }

    @Test(expected = StorageException.class)
    public void givenThatExistsButIsNotReadable_wehnLoadFileAsResource_thenThrowException() throws IOException {
        //given
        MockMultipartFile multipartFile = new MockMultipartFile("foo", "bar/../foo.txt", MediaType.TEXT_PLAIN_VALUE, "Hello World".getBytes());
        given(mockedResource.exists()).willReturn(true);
        given(mockedResource.isReadable()).willReturn(false);

        //when
        service.store(multipartFile);
        Resource resource = service.loadAsResource("foo.txt");
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
        then(mockedBufferReader).should(times(1)).lines();
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