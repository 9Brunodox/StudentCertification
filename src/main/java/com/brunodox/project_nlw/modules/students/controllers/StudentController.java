package com.brunodox.project_nlw.modules.students.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import com.brunodox.project_nlw.modules.students.dto.StudentCertificationAnswerDTO;
import com.brunodox.project_nlw.modules.students.dto.VerifyHasCertificationDTO;
import com.brunodox.project_nlw.modules.students.entities.CertificationStudentEntity;
import com.brunodox.project_nlw.modules.students.useCases.StudentCertificationAnswersUseCase;
import com.brunodox.project_nlw.modules.students.useCases.VerifyIfHasCertificationUseCase;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/students")

public class StudentController {

  @Autowired
  private VerifyIfHasCertificationUseCase verifyIfHasCertificationUseCase;

  @Autowired
  private StudentCertificationAnswersUseCase studentCertificationAnswersUseCase;

  @PostMapping("/verifyIfHasCertification")
  public String verifyIfHasCertification(@RequestBody VerifyHasCertificationDTO verifyHasCertificationDTO) {
    
    var result = this.verifyIfHasCertificationUseCase.execute(verifyHasCertificationDTO);
    if(result){
      return "Usuário já fez a prova";
    }
    return "Usuário pode fazer a prova";
  } 
  
  @PostMapping("/certification/answer")
  public ResponseEntity<Object> certificationAnswer(
    @RequestBody StudentCertificationAnswerDTO studentCertificationAnswerDTO){
      try{
        var result = this.studentCertificationAnswersUseCase.execute(studentCertificationAnswerDTO); 
        return ResponseEntity.ok().body(result);
      }catch(Exception e){
        return ResponseEntity.badRequest().body(e.getMessage());
      }
    }
}
