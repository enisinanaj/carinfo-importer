package com.nlc.dataimporter.service;

import com.nlc.dataimporter.service.csv.ImportFromSingleLineStrategy;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class ImportFromTextStrategyFactoryTest {

    private ImportFromTextStrategyFactory importFromTextStrategyFactory;
    private ImportFromTextStrategy mockedImportFromSingleLineStrategy;

    private class ImportFromTextStrategyFactoryForTest extends ImportFromTextStrategyFactory {

        @Override
        protected ImportFromTextStrategy getImportFromSingleLineStrategy() {
            return mockedImportFromSingleLineStrategy;
        }
    }

    @Before
    public void setUp() {
        importFromTextStrategyFactory = new ImportFromTextStrategyFactoryForTest();
        mockedImportFromSingleLineStrategy = mock(ImportFromSingleLineStrategy.class);
    }

    @Test
    public void whenAnImportFromTextStrategyIsRequired_thenReturnesANewImportFromCSVRowStrategy() throws Exception {
        //given
        ImportFromTextStrategy strategy = importFromTextStrategyFactory.createStrategy();

        //then
        Assert.assertThat("Is instance of single line import strategy",
                strategy,
                IsInstanceOf.instanceOf(ImportFromSingleLineStrategy.class));
    }
}