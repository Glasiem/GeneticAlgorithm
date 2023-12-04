package org.example;

import java.util.List;

public class Schedule {

  public List<Class> classes;

  public int fitness_score;

  public Schedule(List<Class> classes, int fitness_score) {
    this.classes = classes;
    this.fitness_score = fitness_score;
  }
}
