package org.example;

import java.util.*;


public class Program {
    public static void main(String[] args) {
        List<String> subjects = Arrays.asList( "ВДУС", "АГ","Програмування", "Матаналiз", "Дискретка");
        List<String> teachers = Arrays.asList("Шевченко", "Франко", "Тарасенко", "Іваненко", "Миколенко");
        List<String> groups = Arrays.asList("КН-11", "КН-12", "КН-13", "КН-14", "ТТП-41", "ТТП-42");
        Map<String, List<String>> teacher_and_subjects = new HashMap<>();
        teacher_and_subjects.put("Шевченко", Arrays.asList("Матаналiз", "АГ"));
        teacher_and_subjects.put("Франко", Arrays.asList("Дискретка", "Програмування"));
        teacher_and_subjects.put("Тарасенко", Collections.singletonList("Програмування"));
        teacher_and_subjects.put("Іваненко", Arrays.asList("Матаналiз", "Дискретка"));
        teacher_and_subjects.put("Миколенко", Collections.singletonList("ВДУС"));

        Map<String, List<String>> groups_and_subjects = new HashMap<>();
        groups_and_subjects.put("КН-11", Arrays.asList("Матаналiз", "Програмування"));
        groups_and_subjects.put("КН-12", Arrays.asList("ВДУС", "АГ"));
        groups_and_subjects.put("КН-13", Arrays.asList("Програмування", "Матаналiз"));
        groups_and_subjects.put("КН-14", Collections.singletonList("Дискретка"));
        groups_and_subjects.put("ТТП-41", Arrays.asList("Програмування", "АГ"));
        groups_and_subjects.put("ТТП-42", Arrays.asList("Програмування", "ВДУС"));

        Map<String, Integer> teacher_max_hours = new HashMap<>();
        teacher_max_hours.put("Шевченко", 3);
        teacher_max_hours.put("Франко", 2);
        teacher_max_hours.put("Тарасенко", 3);
        teacher_max_hours.put("Іваненко", 4);
        teacher_max_hours.put("Миколенко", 1);

        List<String> audiences = Arrays.asList("1", "2");
        int classesPerDay = 5;

        GeneticAlgorithmRealization scheduler = new GeneticAlgorithmRealization(subjects, teachers, groups, classesPerDay, teacher_and_subjects, audiences, teacher_max_hours, groups_and_subjects);
        AbstractMap.SimpleEntry<List<Class>, Integer> result = scheduler.solve(500, 50);
        List<Class> bestSchedule = result.getKey();
        int fitness = result.getValue();

        System.out.println("Найкращий розпорядок:");
        for (Class lesson : bestSchedule) {
            System.out.println("Група: " + lesson.group + " Предмет: " + lesson.subject + " Викладач: " + lesson.teacher + " Пара: " + lesson.time + " Аудиторія: " + lesson.audience);
        }
        System.out.println("Рейтинг(Fitness): " + fitness);
    }
}
