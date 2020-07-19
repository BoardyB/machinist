package com.github.boardyb.machinist.machine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.boardyb.machinist.MachinistApplication;
import com.github.boardyb.restmodel.ErrorResponse;
import com.github.boardyb.restmodel.MachineTO;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {
        MachinistApplication.class,
        MachineController.class,
        IntegrationTestConfiguration.class,
        MachineControllerTest.MachineControllerTestConfiguration.class
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MachineControllerTest {

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    @Qualifier("machineRepositorySpy")
    private MachineRepository machineRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.applicationContext).build();
    }

    @Test
    void shouldFetchStoredMachinesFromDatabase() throws Exception {
        Machine machine1 = new Machine("testMachine1", "this is a test machine", 1999);
        Machine machine2 = new Machine("testMachine2", "this is a test machine as well", 2005);
        this.machineRepository.save(machine1);
        this.machineRepository.save(machine2);

        MvcResult mvcResult = mockMvc.perform(get("/api/machine/all").characterEncoding("utf-8")).andExpect(status().isOk()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();

        MachineTO[] machineTOs = objectMapper.readValue(contentAsString, MachineTO[].class);

        verify(machineRepository, times(1)).findAllByDeletedFalseOrderByUpdatedAtDesc();

        MachineTO machineTO1 = machineTOs[0];
        assertThat(machineTO1.getId(), Matchers.notNullValue());
        assertThat(machineTO1.getName(), Matchers.equalTo(machine2.getName()));
        assertThat(machineTO1.getDescription(), Matchers.equalTo(machine2.getDescription()));
        assertThat(machineTO1.getYearOfProduction(), Matchers.equalTo(machine2.getYearOfProduction()));
        assertThat(machineTO1.getCreatedAt(), Matchers.notNullValue());
        assertThat(machineTO1.getUpdatedAt(), Matchers.notNullValue());

        MachineTO machineTO2 = machineTOs[1];
        assertThat(machineTO2.getId(), Matchers.notNullValue());
        assertThat(machineTO2.getName(), Matchers.equalTo(machine1.getName()));
        assertThat(machineTO2.getDescription(), Matchers.equalTo(machine1.getDescription()));
        assertThat(machineTO2.getYearOfProduction(), Matchers.equalTo(machine1.getYearOfProduction()));
        assertThat(machineTO2.getCreatedAt(), Matchers.notNullValue());
        assertThat(machineTO2.getUpdatedAt(), Matchers.notNullValue());
    }

    @Test
    void shouldFetchSingleMachineByProvidedId() throws Exception {
        Machine machine1 = new Machine("testMachine1", "this is a test machine", 1999);
        Machine savedMachine = this.machineRepository.save(machine1);

        MvcResult mvcResult = mockMvc.perform(get("/api/machine/" + savedMachine.getId()).characterEncoding("utf-8")).andExpect(status().isOk()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        MachineTO machineTO = objectMapper.readValue(contentAsString, MachineTO.class);

        verify(machineRepository, times(1)).findByIdAndDeletedFalse(savedMachine.getId());

        assertThat(machineTO.getId(), Matchers.notNullValue());
        assertThat(machineTO.getName(), Matchers.equalTo(machine1.getName()));
        assertThat(machineTO.getDescription(), Matchers.equalTo(machine1.getDescription()));
        assertThat(machineTO.getYearOfProduction(), Matchers.equalTo(machine1.getYearOfProduction()));
        assertThat(machineTO.getCreatedAt(), Matchers.notNullValue());
        assertThat(machineTO.getUpdatedAt(), Matchers.notNullValue());
    }

    @Test
    void shouldFailToFetchSingleMachineIfMachineWithProvidedIdDoesNotExist() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/machine/testId").characterEncoding("utf-8")).andExpect(status().isNotFound()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(contentAsString, ErrorResponse.class);

        assertThat(errorResponse.getMessage(), Matchers.equalTo("Machine with id [testId] does not exist!"));
        assertThat(errorResponse.getStatus(), Matchers.equalTo(404));
        assertThat(errorResponse.getTimestamp(), Matchers.notNullValue());
    }

    @AfterEach
    void tearDown() {
        this.machineRepository.deleteAll();
    }

    protected static class MachineControllerTestConfiguration {

        @Primary
        @Bean(name = "machineRepositorySpy")
        public MachineRepository machineRepository(final MachineRepository repository) {
            // workaround for https://github.com/spring-projects/spring-boot/issues/7033
            return Mockito.mock(MachineRepository.class, AdditionalAnswers.delegatesTo(repository));
        }
    }

}