package com.nlc.dataimporter.service.csv;

import com.nlc.dataimporter.service.DataValidator;
import com.nlc.dataimporter.utils.TestUtils;
import org.junit.Test;

public class SingleLineRowValidatorTest {

    @Test(expected=RuntimeException.class)
    public void givenWrongCSVRow_whenValidateIsCalled_thenExceptionThrown() throws Exception {
        //given
        String csvRow = "wrong";

        //when
        DataValidator classUnderTest = new SingleLineRowValidator();
        classUnderTest.validate(csvRow);
    }

    @Test(expected=RuntimeException.class)
    public void givenNullInput_whenValidateIsCalled_thenExceptionThrown() throws Exception {
        //given
        String csvRow = null;

        //when
        DataValidator classUnderTest = new SingleLineRowValidator();
        classUnderTest.validate(csvRow);
    }

    @Test(expected=RuntimeException.class)
    public void givenMoreThanOneLineInInput_whenValidateIsCalled_thenExceptionThrown() throws Exception {
        //given
        String csvRow = TestUtils.IMPORT_LINE_FOR_TEST + "\n" + TestUtils.IMPORT_LINE_FOR_TEST;

        //when
        DataValidator classUnderTest = new SingleLineRowValidator();
        classUnderTest.validate(csvRow);
    }

    @Test
    public void givenWorkingCSVRow_whenValidateIsCalled_thenNothingHappens() throws Exception {
        //given
        String csvRow = TestUtils.IMPORT_LINE_FOR_TEST;

        //when
        DataValidator classUnderTest = new SingleLineRowValidator();
        classUnderTest.validate(csvRow);

        //then
        //assumptions:
        // - the vin number is composed of alphanumeric characters and is long 17 chars.
        // - the second input is made of only alpha chars and must be 2 chars long.
        // - the third input should be 2 chars long and may be alphanumeric
        // - the last part can be everything potentially
    }

}