package com.nlc.dataimporter.controllers;

import com.nlc.dataimporter.model.CarInfo;
import com.nlc.dataimporter.service.ImportFromTextStrategy;
import com.nlc.dataimporter.service.ImportFromTextStrategyFactory;
import com.nlc.dataimporter.service.SourceType;
import com.nlc.dataimporter.service.StorageService;
import com.nlc.dataimporter.utils.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

public class VinImporterControllerTest {

    private static final String LOCATION_URL = "https://uri.com";
    private static final String NEW_CAR_INFO = "";
    private UriComponentsBuilder uriComponentBuilderMocked;
    private ImportFromTextStrategyFactory mockedImporter;
    private ServletUriComponentsBuilder servletUrilMocked;
    private UriComponents uriComponentsMocked;
    private ImportFromTextStrategy singleLineImporterMocked;
    private StorageService storageServiceMocked;
    private ImportFromTextStrategy multiLineImporterMocked;

    class VinImporterControllerForTest extends VinImporterController {

        public VinImporterControllerForTest(ImportFromTextStrategyFactory importFromTextStrategyFactory, StorageService fileSystemStorageService) {
            super(importFromTextStrategyFactory, fileSystemStorageService);
        }

        @Override
        protected ServletUriComponentsBuilder getCurrentContextPath() {
            return servletUrilMocked;
        }

        @Override
        protected StorageService getStorageService() {
            return storageServiceMocked;
        }
    }

    @Before
    public void setUp() throws Exception {
        servletUrilMocked = mock(ServletUriComponentsBuilder.class);
        uriComponentBuilderMocked = mock(UriComponentsBuilder.class);
        uriComponentsMocked = mock(UriComponents.class);
        mockedImporter = mock(ImportFromTextStrategyFactory.class);
        storageServiceMocked = mock(StorageService.class);

        given(servletUrilMocked.path(anyString())).willReturn(uriComponentBuilderMocked);
        given(uriComponentBuilderMocked.path(anyString())).willReturn(uriComponentBuilderMocked);
        given(uriComponentBuilderMocked.buildAndExpand(anyLong())).willReturn(uriComponentsMocked);
        given(uriComponentBuilderMocked.buildAndExpand()).willReturn(uriComponentsMocked);
        given(uriComponentsMocked.toUri()).willReturn(URI.create(LOCATION_URL));
        singleLineImporterMocked = mock(ImportFromTextStrategy.class);
        multiLineImporterMocked = mock(ImportFromTextStrategy.class);
        given(mockedImporter.createStrategy(SourceType.SINGLE_LINE)).willReturn(singleLineImporterMocked);
        given(mockedImporter.createStrategy(SourceType.MULTI_LINE)).willReturn(multiLineImporterMocked);
    }

    @Test(expected=RuntimeException.class)
    public void givenEmptyCarInfoToImport_whenImporting_thenBadRequestExceptionIsReturned() {
        //given
        VinImporterController controller = new VinImporterControllerForTest(mockedImporter, storageServiceMocked);
        doThrow(new RuntimeException()).when(singleLineImporterMocked).importData(anyString());

        //when
        ResponseEntity response = controller.saveNewCarInfo(NEW_CAR_INFO);

        //then
    }

    @Test
    public void givenCarInfoToImport_whenImporting_thenImportIsSuccessful() {
        //given
        VinImporterController controller = new VinImporterControllerForTest(mockedImporter, storageServiceMocked);
        CarInfo carInfoResult = mock(CarInfo.class);
        given(carInfoResult.getId()).willReturn(1L);
        given(singleLineImporterMocked.getImportedData()).willReturn(carInfoResult);

        //when
        ResponseEntity response = controller.saveNewCarInfo(TestUtils.IMPORT_LINE_FOR_TEST);

        //then
        Assert.assertEquals(201, response.getStatusCodeValue());
        Assert.assertEquals(LOCATION_URL, response.getHeaders().get("Location").get(0));

        InOrder inOrder = inOrder(mockedImporter, singleLineImporterMocked, servletUrilMocked, uriComponentBuilderMocked);

        inOrder.verify(mockedImporter).createStrategy(SourceType.SINGLE_LINE);
        inOrder.verify(singleLineImporterMocked).importData(anyString());
        inOrder.verify(servletUrilMocked).path("/carinfo");
        inOrder.verify(uriComponentBuilderMocked).path("/{id}");
    }

    @Test
    public void givenAFileToImport_whenImporting_thenImportIsSuccessful() {
        //given
        VinImporterController controller = new VinImporterControllerForTest(mockedImporter, storageServiceMocked);
        CarInfo carInfoResult = mock(CarInfo.class);
        given(carInfoResult.getId()).willReturn(1L);

        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", TestUtils.IMPORT_LINE_FOR_TEST.getBytes());

        //when
        ResponseEntity response = controller.importCarsFromFile(multipartFile);

        //then
        Assert.assertEquals(201, response.getStatusCodeValue());
    }

    @Test
    public void givenAFileToImport_whenImporting_thenTheControllerAsksStorageServiceToSaveTheFile() {
        //given
        VinImporterController controller = new VinImporterControllerForTest(mockedImporter, storageServiceMocked);
        CarInfo carInfoResult = mock(CarInfo.class);
        given(carInfoResult.getId()).willReturn(1L);

        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", TestUtils.IMPORT_LINE_FOR_TEST.getBytes());

        //when
        controller.importCarsFromFile(multipartFile);

        //then
        then(storageServiceMocked).should(times(1)).store(multipartFile);
    }

    @Test
    public void givenAFileToImport_whenImporting_thenTheControllerUsesTheMultilineStrategyToImport() {
        //given
        VinImporterController controller = new VinImporterControllerForTest(mockedImporter, storageServiceMocked);
        CarInfo carInfoResult = mock(CarInfo.class);
        given(carInfoResult.getId()).willReturn(1L);

        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", TestUtils.IMPORT_LINE_FOR_TEST.getBytes());

        //when
        controller.importCarsFromFile(multipartFile);

        //then
        then(mockedImporter).should(times(1)).createStrategy(SourceType.MULTI_LINE);
    }

    @Test
    public void givenAFileToImport_whenImporting_thenTheControllerCallsCollaboratorClasses() {
        //given
        VinImporterController controller = new VinImporterControllerForTest(mockedImporter, storageServiceMocked);
        CarInfo carInfoResult = mock(CarInfo.class);
        given(carInfoResult.getId()).willReturn(1L);
        given(storageServiceMocked.loadFileContentAsString(anyString())).willReturn(TestUtils.IMPORT_MULTI_LINE_FOR_TEST);

        MockMultipartFile multipartFile = new MockMultipartFile("file", "test.txt",
                "text/plain", TestUtils.IMPORT_MULTI_LINE_FOR_TEST.getBytes());

        //when
        controller.importCarsFromFile(multipartFile);

        //then
        InOrder inOrder = inOrder(storageServiceMocked, mockedImporter, multiLineImporterMocked, servletUrilMocked);

        inOrder.verify(storageServiceMocked).store(any());
        inOrder.verify(storageServiceMocked).loadFileContentAsString(anyString());
        inOrder.verify(mockedImporter).createStrategy(SourceType.MULTI_LINE);
        inOrder.verify(multiLineImporterMocked).importData(anyString());
        inOrder.verify(servletUrilMocked).path("/carinfo");
    }

}