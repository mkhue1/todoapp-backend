package com.angular_training.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "todo_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TodoItem{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column()
    private String title;
    @Column()
    private boolean completed;
    @Column()
    private LocalDate date;
    @Column()
    @Enumerated(EnumType.STRING)
    private Priority priority;

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }
}
