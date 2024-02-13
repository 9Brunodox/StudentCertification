package com.brunodox.project_nlw.modules.questions.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brunodox.project_nlw.modules.questions.entities.AlternativesEntity;
import com.brunodox.project_nlw.modules.questions.entities.QuestionEntity;
import com.brunodox.project_nlw.modules.questions.repositories.QuestionRepository;
import com.brunodox.project_nlw.modules.questions.dto.AlternativesResultDTO;
import com.brunodox.project_nlw.modules.questions.dto.QuestionResultDTO;

@RestController
@RequestMapping("/questions")
public class QuestionController {

  @Autowired
  private QuestionRepository questionRepository;

  @GetMapping("/technology/{technology}")
  public List<QuestionResultDTO> findByTechnology(@PathVariable String technology) {
    System.out.println("TECH ===" + technology);
    var result = this.questionRepository.findByTechnology(technology);

    var toMap = result.stream().map(this::mapQuestionToDTO).collect(Collectors.toList());
    return toMap;
  }

  private QuestionResultDTO mapQuestionToDTO(QuestionEntity question) {
    var questionResultDTO = QuestionResultDTO.builder()
        .id(question.getId())
        .technology(question.getTechnology())
        .description(question.getDescription()).build();

    List<AlternativesResultDTO> alternativesResultDTOs = question.getAlternatives().stream()
        .map(alternative -> mapAlternativeDTO(alternative)).collect(Collectors.toList());

    questionResultDTO.setAlternatives(alternativesResultDTOs);

    return questionResultDTO;

  }

  private AlternativesResultDTO mapAlternativeDTO(AlternativesEntity alternativesResultDTO) {
    return AlternativesResultDTO.builder()
        .id(alternativesResultDTO.getId())
        .description(alternativesResultDTO.getDescription()).build();
  }
}
