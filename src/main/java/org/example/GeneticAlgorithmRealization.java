package org.example;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class GeneticAlgorithmRealization {
    private final List<String> subjects;
    private final List<String> teachers;
    private final List<String> groups;
    private final List<String> audiences;
    private final int classes_per_day;
    private final Map<String, List<String>> teacher_and_subjects;
    private final Map<String, Integer> teacher_max_hours;
    private final Map<String, List<String>> groups_and_subjects;
    private static final Random random = new Random();

    public GeneticAlgorithmRealization(List<String> subjects, List<String> teachers, List<String> groups, int classes_per_day, Map<String, List<String>> teacher_and_subjects, List<String> audiences, Map<String, Integer> teacher_max_hours, Map<String, List<String>> groups_and_subjects) {
        this.subjects = subjects;
        this.teachers = teachers;
        this.groups = groups;
        this.classes_per_day = classes_per_day;
        this.teacher_and_subjects = teacher_and_subjects;
        this.audiences = audiences;
        this.teacher_max_hours = teacher_max_hours;
        this.groups_and_subjects = groups_and_subjects;
    }

    private Schedule randomizeSchedule() {
        List<Class> collect = groups.stream().map(group -> {
            Class c = new Class();
            c.subject = subjects.get(random.nextInt(subjects.size()));
            c.teacher = teachers.get(random.nextInt(teachers.size()));
            c.group = group;
            c.time = random.nextInt(classes_per_day) + 1;
            c.audience = audiences.get(random.nextInt(audiences.size()));
            return c;
        }).collect(Collectors.toList());
        Schedule scedule = new Schedule(collect, findFitness(collect, teacher_and_subjects,
            teacher_max_hours, groups_and_subjects));
        return scedule;
    }

    private List<Schedule> populationInit(int populationSize) {
        return IntStream.range(0, populationSize).mapToObj(i -> randomizeSchedule()).collect(Collectors.toList());
    }

    private static int findFitness(List<Class> schedule, Map<String, List<String>> teacherSubjects, Map<String, Integer> teacherMaxHours, Map<String, List<String>> groupsSubjects) {
        long conflicts = IntStream.range(0, schedule.size()).mapToObj(i -> new AbstractMap.SimpleEntry<>(schedule.get(i), i))
            .flatMap(pair1 -> schedule.stream().skip(pair1.getValue() + 1).map(c -> new AbstractMap.SimpleEntry<>(pair1.getKey(), c)))
            .filter(pair -> pair.getKey().time == pair.getValue().time && (pair.getKey().group.equals(pair.getValue().group) || pair.getKey().teacher.equals(pair.getValue().teacher) || pair.getKey().audience.equals(pair.getValue().audience)))
            .count();
        conflicts += schedule.stream().filter(c -> !teacherSubjects.getOrDefault(c.teacher, Collections.emptyList()).contains(c.subject)).count();
        conflicts += schedule.stream().filter(c -> !groupsSubjects.getOrDefault(c.group, Collections.emptyList()).contains(c.subject)).count();

        Map<String, Long> teachingHours = schedule.stream().collect(Collectors.groupingBy(c -> c.teacher, Collectors.summingLong(c -> c.time)));
        for (Map.Entry<String, Long> teacher : teachingHours.entrySet()) {
            if (teacherMaxHours.containsKey(teacher.getKey()) && teacher.getValue() > teacherMaxHours.get(teacher.getKey())) {
                conflicts++;
            }
        }

        return (int) ((1.0 / (1.0 + conflicts)) * 100);
    }

    private Schedule mutate(Schedule schedule) {
        List<Class> classes = schedule.classes.stream()
                .map(c -> random.nextDouble() < 0.1 ? randomizeSchedule().classes.get(0) : c).collect(Collectors.toList());
        return new Schedule(classes, findFitness(classes, teacher_and_subjects,
            teacher_max_hours, groups_and_subjects));
    }

    private AbstractMap.SimpleEntry<Schedule, Schedule> crossover(List<Class> schedule1, List<Class> schedule2) {
        int crossoverPoint1 = random.nextInt(groups.size() - 1);
        int crossoverPoint2 = random.nextInt(groups.size() - crossoverPoint1 - 1) + crossoverPoint1 + 1;

        List<Class> child1 = new ArrayList<>(schedule1.subList(0, crossoverPoint1));
        child1.addAll(schedule2.subList(crossoverPoint1, crossoverPoint2));
        child1.addAll(schedule1.subList(crossoverPoint2, groups.size()));
        Schedule childSchedule1 = new Schedule(child1, findFitness(child1, teacher_and_subjects,
            teacher_max_hours, groups_and_subjects));

        List<Class> child2 = new ArrayList<>(schedule2.subList(0, crossoverPoint1));
        child2.addAll(schedule1.subList(crossoverPoint1, crossoverPoint2));
        child2.addAll(schedule2.subList(crossoverPoint2, groups.size()));
        Schedule childSchedule2 = new Schedule(child2, findFitness(child2, teacher_and_subjects,
            teacher_max_hours, groups_and_subjects));

        return new AbstractMap.SimpleEntry<>(childSchedule1, childSchedule2);
    }

    public AbstractMap.SimpleEntry<List<Class>, Integer> solve(int populationSize, int generations) {
        List<Schedule> population = populationInit(populationSize);
        int optimized_fitness = 0;
        List<Class> optimized_schedule = null;

        double crossoverProbability = 0.8;
        double mutationProbability = 0.1;

        for (int generation = 0; generation < generations; generation++) {
            population.sort((o1, o2) -> -(o1.fitness_score - o2.fitness_score));
            optimized_schedule = population.get(0).classes;
            optimized_fitness = population.get(0).fitness_score;
            System.out.println("Покоління: " + (generation + 1) + " Найкращий рейтинг: " + optimized_fitness);

            List<Schedule> newPopulation = new ArrayList<>();
            newPopulation.addAll(population.subList(0, population.size() / 2));
            while (newPopulation.size() < populationSize) {
                Schedule mom = population.get(random.nextInt(population.size()));
                Schedule dad = population.get(random.nextInt(population.size()));

                if (random.nextDouble() < crossoverProbability) {
                    AbstractMap.SimpleEntry<Schedule, Schedule> children = crossover(mom.classes, dad.classes);
                    newPopulation.add(children.getKey());
                    newPopulation.add(children.getValue());
                }

                if (random.nextDouble() < mutationProbability) {
                    int i = random.nextInt(newPopulation.size());
                    newPopulation.set(i, mutate(newPopulation.get(i)));
                }
            }

            population = new ArrayList<>(newPopulation.subList(0, populationSize));
        }
        population.sort((o1, o2) -> -(o1.fitness_score - o2.fitness_score));
        optimized_schedule = population.get(0).classes;
        optimized_fitness = population.get(0).fitness_score;
        return new AbstractMap.SimpleEntry<>(optimized_schedule, optimized_fitness);
    }
}