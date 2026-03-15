package com.openclassrooms.etudiant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.etudiant.dto.StudentRequestDTO;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.repository.StudentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
public class StudentControllerTest {

    private static final String URL = "/api/students";
    private static final String FIRST_NAME = "Marie";
    private static final String LAST_NAME = "Curie";
    private static final String EMAIL = "marie.curie@example.com";

    @Container
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0.33");

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private Student savedStudent;

    @DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> mySQLContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> mySQLContainer.getUsername());
        registry.add("spring.datasource.password", () -> mySQLContainer.getPassword());
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
    }

    @BeforeEach
    public void beforeEach() {
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);
        savedStudent = studentRepository.save(student);
    }

    @AfterEach
    public void afterEach() {
        studentRepository.deleteAll();
    }

    // ---- POST /api/students ----

    @Test
    public void createStudent_withoutAuth_returns401() throws Exception {
        // GIVEN
        StudentRequestDTO dto = buildRequestDTO("Ada", "Lovelace", "ada@example.com");

        // WHEN / THEN
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void createStudent_withAuth_invalidBody_returns400() throws Exception {
        // GIVEN — empty body (missing required fields)
        StudentRequestDTO dto = new StudentRequestDTO();

        // WHEN / THEN
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void createStudent_withAuth_duplicateEmail_returns409() throws Exception {
        // GIVEN — email already used by savedStudent
        StudentRequestDTO dto = buildRequestDTO("Other", "Name", EMAIL);

        // WHEN / THEN
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    @WithMockUser
    public void createStudent_withAuth_validBody_returns201() throws Exception {
        // GIVEN
        StudentRequestDTO dto = buildRequestDTO("Ada", "Lovelace", "ada@example.com");

        // WHEN / THEN
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Ada"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("ada@example.com"));
    }

    // ---- GET /api/students ----

    @Test
    public void getAllStudents_withoutAuth_returns401() throws Exception {
        // WHEN / THEN
        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void getAllStudents_withAuth_returns200() throws Exception {
        // WHEN / THEN
        mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

    // ---- GET /api/students/{id} ----

    @Test
    @WithMockUser
    public void getStudentById_withAuth_returns200() throws Exception {
        // WHEN / THEN
        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/" + savedStudent.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedStudent.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(EMAIL));
    }

    @Test
    @WithMockUser
    public void getStudentById_withAuth_notFound_returns400() throws Exception {
        // WHEN / THEN
        mockMvc.perform(MockMvcRequestBuilders.get(URL + "/9999"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    // ---- PUT /api/students/{id} ----

    @Test
    @WithMockUser
    public void updateStudent_withAuth_returns200() throws Exception {
        // GIVEN
        StudentRequestDTO dto = buildRequestDTO("Marie-Updated", LAST_NAME, EMAIL);

        // WHEN / THEN
        mockMvc.perform(MockMvcRequestBuilders.put(URL + "/" + savedStudent.getId())
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Marie-Updated"));
    }

    // ---- DELETE /api/students/{id} ----

    @Test
    public void deleteStudent_withoutAuth_returns401() throws Exception {
        // WHEN / THEN
        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/" + savedStudent.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void deleteStudent_withAuth_returns204() throws Exception {
        // WHEN / THEN
        mockMvc.perform(MockMvcRequestBuilders.delete(URL + "/" + savedStudent.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    // ---- helpers ----

    private StudentRequestDTO buildRequestDTO(String firstName, String lastName, String email) {
        StudentRequestDTO dto = new StudentRequestDTO();
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setEmail(email);
        return dto;
    }
}