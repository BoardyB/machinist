package com.github.boardyb.machinist.machine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.boardyb.machinist.MachinistApplication;
import com.github.boardyb.restmodel.CreateMachineRequest;
import com.github.boardyb.restmodel.ErrorResponse;
import com.github.boardyb.restmodel.MachineTO;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {
        MachinistApplication.class,
        MachineController.class,
        IntegrationTestConfiguration.class
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MachineControllerTest {

    @Autowired
    private WebApplicationContext applicationContext;

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.applicationContext).build();
    }

    /**
     * - Given: there are machines stored in the database.
     * - When: a request is sent to the API endpoint which is responsible for
     * fetching all machines in the database.
     * - Then: the stored machines are fetched from the database and served in the response as DTOs.
     */
    @Test
    void shouldFetchStoredMachinesFromDatabase() throws Exception {
        Machine machine1 = new Machine("testMachine1", "this is a test machine", 1999);
        Machine machine2 = new Machine("testMachine2", "this is a test machine as well", 2005);
        this.machineRepository.save(machine1);
        this.machineRepository.save(machine2);

        MvcResult mvcResult = mockMvc.perform(get("/api/machine/all").characterEncoding("utf-8")).andExpect(status().isOk()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();

        MachineTO[] machineTOs = objectMapper.readValue(contentAsString, MachineTO[].class);

        MachineTO machineTO1 = machineTOs[0];
        Machine storedMachine1 = machineRepository.findById(machineTO1.getId()).get();
        assertThat(storedMachine1, Matchers.notNullValue());
        assertMachineFieldsWithDTO(storedMachine1, machineTO1);

        MachineTO machineTO2 = machineTOs[1];
        Machine storedMachine2 = machineRepository.findById(machineTO2.getId()).get();
        assertThat(storedMachine2, Matchers.notNullValue());
        assertMachineFieldsWithDTO(storedMachine2, machineTO2);
    }

    private void assertMachineFieldsWithDTO(Machine machine, MachineTO machineTO) {
        assertThat(machineTO.getId(), Matchers.equalTo(machine.getId()));
        assertThat(machineTO.getName(), Matchers.equalTo(machine.getName()));
        assertThat(machineTO.getDescription(), Matchers.equalTo(machine.getDescription()));
        assertThat(machineTO.getYearOfProduction(), Matchers.equalTo(machine.getYearOfProduction()));
        assertThat(machineTO.getCreatedAt(), Matchers.equalTo(machine.getCreatedAt()));
        assertThat(machineTO.getUpdatedAt(), Matchers.equalTo(machine.getUpdatedAt()));
    }

    /**
     * - Given: there is a machine stored in the database.
     * - When: a request with a valid ID path variable is sent to the API endpoint which is responsible for
     * fetching a single machine from the database.
     * - Then: the stored machine is fetched from the database and served in the response as a DTO.
     */
    @Test
    void shouldFetchSingleMachineByProvidedId() throws Exception {
        Machine machine1 = new Machine("testMachine1", "this is a test machine", 1999);
        Machine savedMachine = this.machineRepository.save(machine1);

        MvcResult mvcResult = mockMvc.perform(get("/api/machine/" + savedMachine.getId()).characterEncoding("utf-8")).andExpect(status().isOk()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        MachineTO machineTO = objectMapper.readValue(contentAsString, MachineTO.class);

        assertMachineFieldsWithDTO(savedMachine, machineTO);
    }

    /**
     * - Given: there are no machines stored in the database.
     * - When: a request with a valid ID path variable is sent to the API endpoint which is responsible for
     * fetching a single machine from the database.
     * - Then: an exception is being thrown and HTTP 404 response is being returned from the endpoint since the
     * queried machine does not exist.
     */
    @Test
    void shouldFailToFetchSingleMachineIfMachineWithProvidedIdDoesNotExist() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/machine/testId").characterEncoding("utf-8"))
                .andExpect(status().isNotFound())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(contentAsString, ErrorResponse.class);

        assertThat(errorResponse.getMessage(), Matchers.equalTo("Machine with id [testId] does not exist!"));
        assertThat(errorResponse.getStatus(), Matchers.equalTo(404));
        assertThat(errorResponse.getTimestamp(), Matchers.notNullValue());
    }

    /**
     * - Given: there are no machines stored in the database.
     * - When: a valid machine creation request is being sent to the API endpoint which is responsible for
     * creating a machine in the application.
     * - Then: the machine is being created with the provided fields.
     */
    @Test
    void shouldCreateMachine() throws Exception {
        CreateMachineRequest createMachineRequest = new CreateMachineRequest();
        createMachineRequest.setName("test machine");
        createMachineRequest.setDescription("test description");
        createMachineRequest.setYearOfProduction(2012);

        MvcResult mvcResult = mockMvc.perform(post("/api/machine/")
                .content(objectMapper.writeValueAsString(createMachineRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Machine machine = machineRepository.findAllByDeletedFalseOrderByUpdatedAtDesc().get(0);
        assertThat(machine.getId(), Matchers.notNullValue());
        assertThat(machine.getName(), Matchers.equalTo(createMachineRequest.getName()));
        assertThat(machine.getDescription(), Matchers.equalTo(createMachineRequest.getDescription()));
        assertThat(machine.getYearOfProduction(), Matchers.equalTo(createMachineRequest.getYearOfProduction()));
        assertThat(machine.getCreatedAt(), Matchers.notNullValue());
        assertThat(machine.getUpdatedAt(), Matchers.notNullValue());

        String location = mvcResult.getResponse().getHeader("Location");
        assertThat(location, Matchers.equalTo("/api/machine/" + machine.getId()));
    }

    /**
     * - Given: there are no machines stored in the database.
     * - When: a machine creation request without the name of the machine is being sent to
     * the API endpoint which is responsible for creating a machine in the application.
     * - Then: the machine creation fails and an HTTP 400 error is being sent to the client.
     */
    @Test
    void shouldFailToCreateNewMachineIfNameWasNotProvided() throws Exception {
        CreateMachineRequest createMachineRequest = new CreateMachineRequest();
        createMachineRequest.setDescription("test description");
        createMachineRequest.setYearOfProduction(2012);

        MvcResult mvcResult = mockMvc.perform(post("/api/machine/")
                .content(objectMapper.writeValueAsString(createMachineRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(contentAsString, ErrorResponse.class);

        assertThat(errorResponse.getMessage(), Matchers.containsString("must not be null"));
        assertThat(errorResponse.getStatus(), Matchers.equalTo(400));
        assertThat(errorResponse.getTimestamp(), Matchers.notNullValue());
    }

    /**
     * - Given: there are no machines stored in the database.
     * - When: a machine creation request with an invalid attribute is being sent to
     * the API endpoint which is responsible for creating a machine in the application.
     * - Then: the machine creation fails and an HTTP 400 error is being sent to the client.
     */
    @Test
    void shouldFailToCreateNewMachineIfNameIsProvidedButOtherFieldsAreInvalid() throws Exception {
        CreateMachineRequest createMachineRequest = new CreateMachineRequest();
        createMachineRequest.setName("test machine");
        createMachineRequest.setDescription("test description");
        createMachineRequest.setYearOfProduction(3003);

        MvcResult mvcResult = mockMvc.perform(post("/api/machine/")
                .content(objectMapper.writeValueAsString(createMachineRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(contentAsString, ErrorResponse.class);

        assertThat(errorResponse.getMessage(), Matchers.containsString("must be less than or equal to 2020"));
        assertThat(errorResponse.getStatus(), Matchers.equalTo(400));
        assertThat(errorResponse.getTimestamp(), Matchers.notNullValue());
    }

    /**
     * - Given: there is a machine saved in the database.
     * - When: a machine update request with a valid machine DTO is being sent to the server's endpoint
     * which is responsible for updating an existing machine
     * - Then: the machine is updated successfully with the provided fields, and the updatedAt field of the machine
     * is being updated with a new timestamp.
     */
    @Test
    void shouldUpdateMachine() throws Exception {
        Machine machine1 = new Machine("testMachine1", "this is a test machine", 1999);
        Machine savedMachine = machineRepository.save(machine1);

        MachineTO machineToUpdate = savedMachine.toDTO();
        machineToUpdate.setName("updatedName");
        machineToUpdate.setYearOfProduction(2013);
        machineToUpdate.setDescription("updatedDescription");

        MvcResult mvcResult = mockMvc.perform(put("/api/machine/")
                .content(objectMapper.writeValueAsString(machineToUpdate))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Machine updatedMachine = this.machineRepository.findById(machineToUpdate.getId()).get();
        assertThat(updatedMachine.getId(), Matchers.equalTo(savedMachine.getId()));
        assertThat(updatedMachine.getName(), Matchers.equalTo(machineToUpdate.getName()));
        assertThat(updatedMachine.getDescription(), Matchers.equalTo(machineToUpdate.getDescription()));
        assertThat(updatedMachine.getYearOfProduction(), Matchers.equalTo(machineToUpdate.getYearOfProduction()));
        assertThat(updatedMachine.getCreatedAt(), Matchers.equalTo(savedMachine.getCreatedAt()));
        assertTrue(updatedMachine.getUpdatedAt().isAfter(savedMachine.getUpdatedAt()));

        String location = mvcResult.getResponse().getHeader("Location");
        assertThat(location, Matchers.equalTo("/api/machine/" + updatedMachine.getId()));
    }

    /**
     * - Given: there are no machines stored in the database.
     * - When: a machine update request with a valid machine DTO is being sent to
     * the API endpoint which is responsible for updating an existing machine in the application.
     * - Then: the machine update fails and an HTTP 404 error is being sent to the client because the machine
     * does not exist.
     */
    @Test
    void shouldFailToUpdateMachineIfTheProvidedIdDoesNotMatchAnyStoredMachines() throws Exception {
        MachineTO machineToUpdate = new MachineTO();
        machineToUpdate.setId("notExistingId");
        machineToUpdate.setName("updatedName");

        MvcResult mvcResult = mockMvc.perform(put("/api/machine/")
                .content(objectMapper.writeValueAsString(machineToUpdate))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(contentAsString, ErrorResponse.class);

        assertThat(errorResponse.getMessage(), Matchers.equalTo("Machine with id [notExistingId] does not exist!"));
        assertThat(errorResponse.getStatus(), Matchers.equalTo(404));
        assertThat(errorResponse.getTimestamp(), Matchers.notNullValue());
    }

    /**
     * - Given: there are no machines stored in the database.
     * - When: a machine update request without a required attribute is being sent to
     * the API endpoint which is responsible for updating a machine in the application.
     * - Then: the machine update fails and an HTTP 400 error is being sent to the client.
     */
    @Test
    void shouldFailToUpdateMachineIfInvalidMachineWasProvidedInRequest() throws Exception {
        MachineTO machineToUpdate = new MachineTO();
        machineToUpdate.setId("testId");
        machineToUpdate.setDescription("test description");
        machineToUpdate.setYearOfProduction(2013);

        MvcResult mvcResult = mockMvc.perform(put("/api/machine/")
                .content(objectMapper.writeValueAsString(machineToUpdate))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(contentAsString, ErrorResponse.class);

        assertThat(errorResponse.getMessage(), Matchers.containsString("must not be null"));
        assertThat(errorResponse.getStatus(), Matchers.equalTo(400));
        assertThat(errorResponse.getTimestamp(), Matchers.notNullValue());
    }

    /**
     * - Given: there is a machine stored in the database.
     * - When: a request with a valid ID path variable is sent to the API endpoint which is responsible for
     * deleting a single machine from the database.
     * - Then: the stored machine's deleted field is set to true but the database record is not deleted
     * and querying the fetch API endpoint will not return the deleted machine.
     */
    @Test
    void shouldDeleteExistingMachineFromStore() throws Exception {
        Machine machine1 = new Machine("testMachine1", "this is a test machine", 1999);
        Machine savedMachine = this.machineRepository.save(machine1);

        mockMvc.perform(delete("/api/machine/" + savedMachine.getId())).andExpect(status().isOk()).andReturn();

        Machine deletedMachine = machineRepository.findById(savedMachine.getId()).get();
        assertThat(deletedMachine.isDeleted(), Matchers.equalTo(true));

        MvcResult mvcResult = mockMvc.perform(get("/api/machine/" + savedMachine.getId()).characterEncoding("utf-8")).andExpect(status().isNotFound()).andReturn();
    }

    /**
     * - Given: there are no machines stored in the database.
     * - When: a request with a valid ID path variable is sent to the API endpoint which is responsible for
     * deleting a single machine from the database.
     * - Then: an exception is being thrown and HTTP 404 response is being returned from the endpoint since the
     * machine does not exist.
     */
    @Test
    void shouldFailToDeleteMachineIfThereIsNoMachineMatchingTheProvidedId() throws Exception {
        MvcResult mvcResult = mockMvc.perform(delete("/api/machine/notExistingId")).andExpect(status().isNotFound()).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(contentAsString, ErrorResponse.class);

        assertThat(errorResponse.getMessage(), Matchers.equalTo("Machine with id [notExistingId] does not exist!"));
        assertThat(errorResponse.getStatus(), Matchers.equalTo(404));
        assertThat(errorResponse.getTimestamp(), Matchers.notNullValue());
    }

    @AfterEach
    void tearDown() {
        this.machineRepository.deleteAll();
    }

}