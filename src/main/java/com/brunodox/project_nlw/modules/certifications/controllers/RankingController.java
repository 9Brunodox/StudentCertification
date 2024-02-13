package com.brunodox.project_nlw.modules.certifications.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.brunodox.project_nlw.modules.certifications.UseCases.Top10RankingUseCase;
import com.brunodox.project_nlw.modules.students.entities.CertificationStudentEntity;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;



@RestController
@RequestMapping("/ranking")
public class RankingController {

  @Autowired
  private Top10RankingUseCase top10RankingUseCase;

  @GetMapping("/top10")
  public List<CertificationStudentEntity> top10(){
    return this.top10RankingUseCase.execute();

  }
}
