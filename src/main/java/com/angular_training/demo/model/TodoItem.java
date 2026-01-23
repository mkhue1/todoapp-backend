package com.angular_training.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "todo_items")
public class TodoItem{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "COMPLETED")
    private boolean completed;
    @Column(name = "DATE")
    private LocalDate date;
    @Column(name = "PRIORITY")
    @Enumerated(EnumType.STRING)
    private Priority priority;

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }
    public TodoItem() {}

    public TodoItem(String title, boolean completed, LocalDate date, Priority priority) {
        this.title = title;
        this.completed = completed;
        this.date = date;
        this.priority = priority;
    }
    // getters & setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}
