package com.nlc.dataimporter.service.csv;

import com.nlc.dataimporter.model.CarInfo;
import com.nlc.dataimporter.repositories.CarInfoRepository;
import com.nlc.dataimporter.service.ImportFromTextStrategy;
import com.nlc.dataimporter.utils.TestUtils;
import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class ImportFromSingleLineStrategyTest {

    private CarInfoRepository mockedRepository;
    private SingleLineDataToCarInfo mockedSingleLineDataToCarInfo;

    class ImportFromSingleLineStrategyForTest extends  ImportFromSingleLineStrategy {

        @Override
        protected CarInfoRepository getCarInfoRepository() {
            return mockedRepository;
        }

        @Override
        protected SingleLineDataToCarInfo getSingleLineDataToCarInfoConverter() {
            return mockedSingleLineDataToCarInfo;
        }
    }

    @Before
    public void setUp() {
        mockedRepository = mock(CarInfoRepository.class);
        mockedSingleLineDataToCarInfo = mock(SingleLineDataToCarInfo.class);
    }

    @Test(expected=RuntimeException.class)
    public void givenNull_whenImportingData_thenRuntimeExceptionIsThrown() {
        //given
        ImportFromTextStrategy importFromSingleLine = new ImportFromSingleLineStrategyForTest();
        given(mockedSingleLineDataToCarInfo.importFromSource(null)).willThrow(new RuntimeException());

        //when
        importFromSingleLine.importData(null);
    }

    @Test
    public void givenCorrectImportData_whenImportingData_thenCollaboratorsAreCalled() {
        //given
        ImportFromTextStrategy importFromSingleLine = new ImportFromSingleLineStrategyForTest();
        CarInfo conversionResult = mock(CarInfo.class);
        given(mockedSingleLineDataToCarInfo.importFromSource(anyString())).willReturn(conversionResult);

        //when
        importFromSingleLine.importData(TestUtils.IMPORT_LINE_FOR_TEST);

        //then
        then(mockedSingleLineDataToCarInfo).should(times(1)).importFromSource(TestUtils.IMPORT_LINE_FOR_TEST);
        then(mockedRepository).should(times(1)).save(any(CarInfo.class));
    }

    @Test
    public void givenImportData_whenAskingForImportedData_thenCarInfoIsReturned() {
        //given
        ImportFromTextStrategy importFromSingleLine = new ImportFromSingleLineStrategyForTest();
        CarInfo conversionResult = mock(CarInfo.class);
        given(conversionResult.getVin()).willReturn("VF1KMS40A36042123");

        given(mockedSingleLineDataToCarInfo.importFromSource(anyString())).willReturn(conversionResult);
        given(mockedRepository.save(any())).willReturn(conversionResult);

        //when
        importFromSingleLine.importData(TestUtils.IMPORT_LINE_FOR_TEST);
        CarInfo result = (CarInfo) importFromSingleLine.getImportedData();

        //then
        then(mockedSingleLineDataToCarInfo).should(times(1)).importFromSource(TestUtils.IMPORT_LINE_FOR_TEST);
        then(mockedRepository).should(times(1)).save(any(CarInfo.class));

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getVin()).isEqualToIgnoringCase("VF1KMS40A36042123");
    }

}