package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.dto.StudentRequestDTO;
import com.openclassrooms.etudiant.dto.StudentResponseDTO;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.mapper.StudentDtoMapper;
import com.openclassrooms.etudiant.repository.StudentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class StudentServiceTest {

    private static final Long ID = 1L;
    private static final String FIRST_NAME = "Marie";
    private static final String LAST_NAME = "Curie";
    private static final String EMAIL = "marie.curie@example.com";

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentDtoMapper studentDtoMapper;

    @InjectMocks
    private StudentService studentService;

    // ---- create ----

    @Test
    public void test_create_null_dto_throws_IllegalArgumentException() {
        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> studentService.create(null));
    }

    @Test
    public void test_create_student_successful() {
        // GIVEN
        StudentRequestDTO dto = buildRequestDTO();
        Student entity = buildStudent();
        StudentResponseDTO responseDTO = buildResponseDTO();

        when(studentDtoMapper.toEntity(dto)).thenReturn(entity);
        when(studentRepository.save(entity)).thenReturn(entity);
        when(studentDtoMapper.toDTO(entity)).thenReturn(responseDTO);

        // WHEN
        StudentResponseDTO result = studentService.create(dto);

        // THEN
        assertThat(result).isEqualTo(responseDTO);
        verify(studentRepository).save(entity);
    }

    // ---- findAll ----

    @Test
    public void test_findAll_returns_mapped_list() {
        // GIVEN
        Student entity = buildStudent();
        StudentResponseDTO responseDTO = buildResponseDTO();

        when(studentRepository.findAll()).thenReturn(List.of(entity));
        when(studentDtoMapper.toDTO(entity)).thenReturn(responseDTO);

        // WHEN
        List<StudentResponseDTO> result = studentService.findAll();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(responseDTO);
    }

    @Test
    public void test_findAll_empty_returns_empty_list() {
        // GIVEN
        when(studentRepository.findAll()).thenReturn(List.of());

        // WHEN
        List<StudentResponseDTO> result = studentService.findAll();

        // THEN
        assertThat(result).isEmpty();
    }

    // ---- findById ----

    @Test
    public void test_findById_null_id_throws_IllegalArgumentException() {
        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> studentService.findById(null));
    }

    @Test
    public void test_findById_not_found_throws_IllegalArgumentException() {
        // GIVEN
        when(studentRepository.findById(ID)).thenReturn(Optional.empty());

        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> studentService.findById(ID));
    }

    @Test
    public void test_findById_found_returns_dto() {
        // GIVEN
        Student entity = buildStudent();
        StudentResponseDTO responseDTO = buildResponseDTO();

        when(studentRepository.findById(ID)).thenReturn(Optional.of(entity));
        when(studentDtoMapper.toDTO(entity)).thenReturn(responseDTO);

        // WHEN
        StudentResponseDTO result = studentService.findById(ID);

        // THEN
        assertThat(result).isEqualTo(responseDTO);
    }

    // ---- update ----

    @Test
    public void test_update_null_id_throws_IllegalArgumentException() {
        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> studentService.update(null, buildRequestDTO()));
    }

    @Test
    public void test_update_not_found_throws_IllegalArgumentException() {
        // GIVEN
        when(studentRepository.findById(ID)).thenReturn(Optional.empty());

        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> studentService.update(ID, buildRequestDTO()));
    }

    @Test
    public void test_update_student_successful() {
        // GIVEN
        StudentRequestDTO dto = buildRequestDTO();
        Student existing = buildStudent();
        StudentResponseDTO responseDTO = buildResponseDTO();

        when(studentRepository.findById(ID)).thenReturn(Optional.of(existing));
        when(studentRepository.save(any(Student.class))).thenReturn(existing);
        when(studentDtoMapper.toDTO(existing)).thenReturn(responseDTO);

        // WHEN
        StudentResponseDTO result = studentService.update(ID, dto);

        // THEN
        assertThat(result).isEqualTo(responseDTO);
        verify(studentRepository).save(existing);
    }

    // ---- delete ----

    @Test
    public void test_delete_null_id_throws_IllegalArgumentException() {
        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> studentService.delete(null));
    }

    @Test
    public void test_delete_not_found_throws_IllegalArgumentException() {
        // GIVEN
        when(studentRepository.existsById(ID)).thenReturn(false);

        // THEN
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> studentService.delete(ID));
    }

    @Test
    public void test_delete_student_successful() {
        // GIVEN
        when(studentRepository.existsById(ID)).thenReturn(true);

        // WHEN
        studentService.delete(ID);

        // THEN
        verify(studentRepository).deleteById(ID);
    }

    // ---- helpers ----

    private StudentRequestDTO buildRequestDTO() {
        StudentRequestDTO dto = new StudentRequestDTO();
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setEmail(EMAIL);
        return dto;
    }

    private Student buildStudent() {
        Student student = new Student();
        student.setId(ID);
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setEmail(EMAIL);
        return student;
    }

    private StudentResponseDTO buildResponseDTO() {
        StudentResponseDTO dto = new StudentResponseDTO();
        dto.setId(ID);
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        dto.setEmail(EMAIL);
        return dto;
    }
}