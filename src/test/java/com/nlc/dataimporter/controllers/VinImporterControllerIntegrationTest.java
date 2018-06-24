package com.nlc.dataimporter.controllers;

import com.nlc.dataimporter.VinImporterApplication;
import com.nlc.dataimporter.utils.TestUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by sinanaj on 18/06/2018.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { VinImporterApplication.class })
@WebAppConfiguration
@SpringBootTest
public class VinImporterControllerIntegrationTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void verifyTestConfiguration() {
        ServletContext servletContext = wac.getServletContext();

        Assert.assertNotNull(servletContext);
        Assert.assertTrue(servletContext instanceof MockServletContext);
        Assert.assertNotNull(wac.getBean("vinImporterController"));
    }

    @Test
    public void givenCarInfoAsText_whenMockMVC_thenVerifyResponseIsCreated() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/carinfo")
                .content(TestUtils.IMPORT_LINE_FOR_TEST)
                .contentType("text/plain"))
                .andDo(print()).andExpect(status().isCreated())
                //.andExpect(jsonPath("$").isArray())
                .andReturn();

        Assert.assertNotNull(mvcResult.getResponse().getHeaderValue("Location"));
    }

    @Test
    public void givenEmptyCarInfoAsText_whenMockMVC_thenVerifyBadRequestError() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/carinfo")
                .contentType("text/plain"))
                .andDo(print()).andExpect(status().isBadRequest())
                //.andExpect(jsonPath("$").isArray())
                .andReturn();
    }

    @Test
    public void givenCarInfoAsText_whenMockMVC_thenVerifyIdOfImportedElementIsReturned() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(post("/carinfo")
                .content(TestUtils.IMPORT_LINE_FOR_TEST)
                .contentType("text/plain"))
                .andDo(print()).andExpect(status().isCreated())
                .andExpect(header().string("Location", is("http://localhost/carinfo/1")))
                //.andExpect(jsonPath("$").isArray())
                .andReturn();
    }
}
