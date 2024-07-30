package org.parser_vacancy;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "vacancy_reports")
public class VacancyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "area_name")
    private String areaName;

    @Column(name = "total_vacancies")
    private int totalVacancies;

    @Column(name = "experience_counts")
    private String experienceCounts;

    @Column(name = "schedule_counts")
    private String scheduleCounts;

    @Column(name = "report_date")
    private Date reportDate;

    // Конструкторы, геттеры и сеттеры
}