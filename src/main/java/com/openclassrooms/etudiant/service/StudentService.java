package com.openclassrooms.etudiant.service;

import com.openclassrooms.etudiant.dto.StudentRequestDTO;
import com.openclassrooms.etudiant.dto.StudentResponseDTO;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.mapper.StudentDtoMapper;
import com.openclassrooms.etudiant.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentDtoMapper studentDtoMapper;

    public StudentResponseDTO create(StudentRequestDTO dto) {
        Assert.notNull(dto, "StudentRequestDTO must not be null");
        log.info("Creating new student");
        Student saved = studentRepository.save(studentDtoMapper.toEntity(dto));
        return studentDtoMapper.toDTO(saved);
    }

    public List<StudentResponseDTO> findAll() {
        log.info("Fetching all students");
        return studentRepository.findAll().stream()
                .map(studentDtoMapper::toDTO)
                .toList();
    }

    public StudentResponseDTO findById(Long id) {
        Assert.notNull(id, "Id must not be null");
        log.info("Fetching student with id {}", id);
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + id));
        return studentDtoMapper.toDTO(student);
    }

    public StudentResponseDTO update(Long id, StudentRequestDTO dto) {
        Assert.notNull(id, "Id must not be null");
        Assert.notNull(dto, "StudentRequestDTO must not be null");
        log.info("Updating student with id {}", id);

        Student existing = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + id));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());

        return studentDtoMapper.toDTO(studentRepository.save(existing));
    }

    public void delete(Long id) {
        Assert.notNull(id, "Id must not be null");
        log.info("Deleting student with id {}", id);

        if (!studentRepository.existsById(id)) {
            throw new IllegalArgumentException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }
}