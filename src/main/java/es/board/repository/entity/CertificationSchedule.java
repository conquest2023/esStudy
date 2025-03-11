package es.board.repository.entity;

import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "certification_schedule")
public class CertificationSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private  String name;

    @Column(name = "exam_round", nullable = false)
    private String examRound;

    @Column(name = "written_reg_start")
    private LocalDate writtenRegStart;

    @Column(name = "written_reg_end")
    private LocalDate writtenRegEnd;

    @Column(name = "written_exam_start")
    private LocalDate writtenExamStart;

    @Column(name = "written_exam_end")
    private LocalDate writtenExamEnd;

    @Column(name = "written_pass_date")
    private LocalDate writtenPassDate;
    @Column(name = "doc_submit_start")
    private LocalDate docSubmitStart;

    @Column(name = "doc_submit_end")
    private LocalDate docSubmitEnd;

    @Column(name = "practical_reg_start")
    private LocalDate practicalRegStart;

    @Column(name = "practical_reg_end")
    private LocalDate practicalRegEnd;

    private  String category;

    @Column(name = "practical_exam_start")
    private LocalDate practicalExamStart;

    @Column(name = "practical_exam_end")
    private LocalDate practicalExamEnd;

    @Column(name = "practical_pass_start")
    private LocalDate practicalPassStart;

    @Column(name = "practical_pass_end")
    private LocalDate practicalPassEnd;
    @Column(name = "major_category")
    private String majorCategory;
    @Column(name = "sub_category")
    private String subCategory;


}
