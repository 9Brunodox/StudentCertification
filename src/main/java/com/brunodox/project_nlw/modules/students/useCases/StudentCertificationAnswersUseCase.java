package com.brunodox.project_nlw.modules.students.useCases;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brunodox.project_nlw.modules.questions.entities.QuestionEntity;
import com.brunodox.project_nlw.modules.questions.repositories.QuestionRepository;
import com.brunodox.project_nlw.modules.students.dto.StudentCertificationAnswerDTO;
import com.brunodox.project_nlw.modules.students.dto.VerifyHasCertificationDTO;
import com.brunodox.project_nlw.modules.students.entities.AnswersCertificationEntity;
import com.brunodox.project_nlw.modules.students.entities.CertificationStudentEntity;
import com.brunodox.project_nlw.modules.students.entities.StudentEntity;
import com.brunodox.project_nlw.modules.students.repositories.CertificationStudentRepository;
import com.brunodox.project_nlw.modules.students.repositories.StudentRepository;

@Service
public class StudentCertificationAnswersUseCase {

  @Autowired
  private StudentRepository studentRepository;

  @Autowired
  private CertificationStudentRepository certificationStudentRepository;

  @Autowired
  private QuestionRepository questionRepository;

  @Autowired
  private VerifyIfHasCertificationUseCase verifyIfHasCertificationUseCase;

  @SuppressWarnings("null")
  public CertificationStudentEntity execute(StudentCertificationAnswerDTO dto) throws Exception {

    var hasCertification = this.verifyIfHasCertificationUseCase.execute
    (new VerifyHasCertificationDTO(dto.getEmail(), dto.getTechnology()));

    if(hasCertification){
      throw new Exception("Você já tirou sua certificação");
    }

    List<QuestionEntity> questionsEntities = questionRepository.findByTechnology(dto.getTechnology());
    List<AnswersCertificationEntity> answersCertifications = new ArrayList<>();

    AtomicInteger correctAnswers = new AtomicInteger(0);

    dto.getQuestionsAnswers()
        .stream().forEach(questionAnswer -> {
          var question = questionsEntities.stream().filter(q -> q.getId().equals(questionAnswer.getQuestionID()))
              .findFirst().get();

          var findCorrectAlternative = question.getAlternatives().stream()
              .filter(alternative -> alternative.isCorrect()).findFirst().get();

          if (findCorrectAlternative.getId().equals(questionAnswer.getAlternativeID())) {
            questionAnswer.setCorrect(true);
            correctAnswers.incrementAndGet();
          } else {
            questionAnswer.setCorrect(false);
          }

          var answerrsCertificationsEntity = AnswersCertificationEntity.builder()
              .answerID(questionAnswer.getAlternativeID())
              .questionID(questionAnswer.getQuestionID())
              .isCorrect(questionAnswer.isCorrect()).build();

          answersCertifications.add(answerrsCertificationsEntity);

        });

    var student = studentRepository.findByEmail(dto.getEmail());
    UUID studentID;
    if (student.isEmpty()) {
      var studentCreated = StudentEntity.builder().email(dto.getEmail()).build();
      studentCreated = studentRepository.save(studentCreated);
      studentID = studentCreated.getId();
    } else {
      studentID = student.get().getId();
    }

    CertificationStudentEntity certificationStudentEntity = CertificationStudentEntity.builder()
        .technology(dto.getTechnology())
        .studentID(studentID)
        .grade(correctAnswers.get())
        .build();

    var certificationStudentCreated = certificationStudentRepository.save(certificationStudentEntity);

    answersCertifications.stream().forEach(answerCertification -> {
      answerCertification.setCertificationID(certificationStudentEntity.getId());
      answerCertification.setCertificationStudentEntity(certificationStudentEntity);
    });

    certificationStudentEntity.setAnswersCertificationEntities(answersCertifications);

    certificationStudentRepository.save(certificationStudentEntity);

    return certificationStudentCreated;

  }
}
