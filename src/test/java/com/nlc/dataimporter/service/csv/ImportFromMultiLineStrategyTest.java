package com.nlc.dataimporter.service.csv;

import com.nlc.dataimporter.model.CarInfo;
import com.nlc.dataimporter.repositories.CarInfoRepository;
import com.nlc.dataimporter.utils.TestUtils;
import org.fest.assertions.Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

public class ImportFromMultiLineStrategyTest {

    private SingleLineDataToCarInfo converterMocked;
    private CarInfoRepository carInfoRepositoryMocked;

    private class ImportFromMultiLineStrategyForTest extends ImportFromMultiLineStrategy {

        @Override
        protected SingleLineDataToCarInfo getSingleLineDataToCarInfoConverter() {
            return converterMocked;
        }

        @Override
        protected CarInfoRepository getRepository() {
            return carInfoRepositoryMocked;
        }
    }

    @Before
    public void setUp() {
        converterMocked = mock(SingleLineDataToCarInfo.class);
        carInfoRepositoryMocked = mock(CarInfoRepository.class);
    }

    @Test
    public void givenContent_whenImporting_thenEveryLineOfContentIsConvertedToCarInfo() throws Exception {
        //given
        String source = TestUtils.IMPORT_MULTI_LINE_FOR_TEST;

        //when
        new ImportFromMultiLineStrategyForTest().importData(source);

        //then
        then(converterMocked).should(times(2)).importFromSource(anyString());
    }

    @Test
    public void givenContent_whenImporting_thenEveryLineImportedIsSaved() throws Exception {
        //given
        String source = TestUtils.IMPORT_MULTI_LINE_FOR_TEST;

        //when
        new ImportFromMultiLineStrategyForTest().importData(source);

        //then
        then(carInfoRepositoryMocked).should(times(2)).save(any());
    }

    @Test(expected = RuntimeException.class)
    public void givenContentIsErroneous_whenImporting_thenNoElementIsSaved() throws Exception {
        //given
        String erroneousString = "error";
        String source = TestUtils.IMPORT_MULTI_LINE_FOR_TEST + "\n" + erroneousString;
        given(converterMocked.importFromSource(erroneousString)).willThrow(new RuntimeException());

        //when
        new ImportFromMultiLineStrategyForTest().importData(source);

        //then
        then(converterMocked).should(times(2)).importFromSource(anyString());
        then(carInfoRepositoryMocked).should(never()).save(any());
    }

    @Test
    public void getImportedData() throws Exception {
        //given
        ImportFromMultiLineStrategy importMultiLine = new ImportFromMultiLineStrategyForTest();
        String source = TestUtils.IMPORT_MULTI_LINE_FOR_TEST;

        //when
        importMultiLine.importData(source);
        List<CarInfo> result = (List<CarInfo>) importMultiLine.getImportedData();

        //then
        Assertions.assertThat(result).hasSize(2);
    }
}