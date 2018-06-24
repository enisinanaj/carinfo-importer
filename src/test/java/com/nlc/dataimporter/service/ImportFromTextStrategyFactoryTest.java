package com.nlc.dataimporter.service;

import com.nlc.dataimporter.service.csv.ImportFromMultiLineStrategy;
import com.nlc.dataimporter.service.csv.ImportFromSingleLineStrategy;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ImportFromTextStrategyFactoryTest {

    private ImportFromTextStrategyFactory importFromTextStrategyFactory;
    private ImportFromTextStrategy mockedImportFromSingleLineStrategy;
    private ImportFromTextStrategy mockedImportFromMultiLineStrategy;

    private class ImportFromTextStrategyFactoryForTest extends ImportFromTextStrategyFactory {

        @Override
        protected ImportFromTextStrategy getImportFromSingleLineStrategy() {
            return mockedImportFromSingleLineStrategy;
        }

        @Override
        protected ImportFromTextStrategy getImportFromMultiLineStrategy() {
            return mockedImportFromMultiLineStrategy;
        }
    }

    @Before
    public void setUp() {
        importFromTextStrategyFactory = new ImportFromTextStrategyFactoryForTest();
        mockedImportFromSingleLineStrategy = mock(ImportFromSingleLineStrategy.class);
        mockedImportFromMultiLineStrategy = mock(ImportFromMultiLineStrategy.class);
    }

    @Test
    public void whenAnImportFromTextStrategyIsRequired_thenReturnesANewImportFromSingleLineStrategy() throws Exception {
        //given
        ImportFromTextStrategy strategy = importFromTextStrategyFactory.createStrategy(SourceType.SINGLE_LINE);

        //then
        Assert.assertThat("Is instance of single line import strategy",
                strategy,
                IsInstanceOf.instanceOf(ImportFromSingleLineStrategy.class));
    }

    @Test
    public void whenAnImportFromTextStrategyIsRequired_thenReturnesANewImportFromMultiLineStrategy() throws Exception {
        //given
        ImportFromTextStrategy strategy = importFromTextStrategyFactory.createStrategy(SourceType.MULTI_LINE);

        //then
        Assert.assertThat("Is instance of single line import strategy",
                strategy,
                IsInstanceOf.instanceOf(ImportFromMultiLineStrategy.class));
    }
}