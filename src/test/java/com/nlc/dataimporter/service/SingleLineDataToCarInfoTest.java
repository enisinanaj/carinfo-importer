package com.nlc.dataimporter.service;

import com.nlc.dataimporter.model.CarInfo;
import com.nlc.dataimporter.service.csv.SingleLineDataToCarInfo;
import com.nlc.dataimporter.service.csv.SingleLineRowValidator;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

/**
 * Created by sinanaj on 20/06/2018.
 */
public class SingleLineDataToCarInfoTest {

    private SingleLineRowValidator mockedValidator;

    class SingleLineDataToCarInfoForTest extends SingleLineDataToCarInfo {

        @Override
        protected SingleLineRowValidator getValidator() {
            return mockedValidator;
        }
    }

    @Before
    public void setUp() throws Exception {
        mockedValidator = mock(SingleLineRowValidator.class);
    }

    @Test
    public void importFromSource() throws Exception {
        //given
        SingleLineDataToCarInfo SingleLineDataToCarInfo = new SingleLineDataToCarInfoForTest();

        //when
        CarInfo carInfo = SingleLineDataToCarInfo.importFromSource("VF1KMS40A36042123,KB,H1,RENAULT");

        //then
        Assertions.assertThat(carInfo.getVin()).isEqualTo("VF1KMS40A36042123");
        Assertions.assertThat(carInfo.getCarMake()).isEqualTo("RENAULT");
        Assertions.assertThat(carInfo.getInput1()).isEqualTo("KB");
        Assertions.assertThat(carInfo.getInput2()).isEqualTo("H1");
    }

    @Test(expected=RuntimeException.class)
    public void ifSourceIsNullReturnEmptyList() throws Exception {
        //given
        SingleLineDataToCarInfo SingleLineDataToCarInfo = new SingleLineDataToCarInfoForTest();
        RuntimeException exception = new RuntimeException();
        doThrow(exception).when(mockedValidator).validate(null);

        //when
        CarInfo result = SingleLineDataToCarInfo.importFromSource(null);

        //then
        Assertions.assertThat(result).isNull();
    }


}