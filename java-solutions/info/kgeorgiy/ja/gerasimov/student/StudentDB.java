package info.kgeorgiy.ja.gerasimov.student;

import info.kgeorgiy.java.advanced.student.GroupName;
import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentQuery;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StudentDB implements StudentQuery {
    private static final Comparator<Student> comparator = Comparator
            .comparing(Student::getLastName)
            .thenComparing(Student::getFirstName)
            .thenComparing(Comparator.comparing(Student::getId).reversed());

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return getSortedList(students, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return getSortedList(students, Student::getLastName);
    }

    @Override
    public List<GroupName> getGroups(List<Student> students) {
        return getSortedList(students, Student::getGroup);
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return getSortedList(students, student -> student.getFirstName() + " " + student.getLastName());
    }

    private static <T> List<T> getSortedList(List<Student> students, Function<Student, T> mapper) {
        return students.stream()
                .map(mapper)
                .toList();
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return students.stream()
                .map(Student::getFirstName)
                .collect(Collectors.toSet());
    }

    @Override
    public String getMaxStudentFirstName(List<Student> students) {
        return students.stream()
                .max(Comparator.comparing(Student::getId))
                .map(Student::getFirstName)
                .orElse("");
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return getSortedList(students, Comparator.comparing(Student::getId));
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return getSortedList(students, comparator);
    }

    private static List<Student> getSortedList(Collection<Student> students, Comparator<Student> comparing) {
        return students.stream()
                .sorted(comparing)
                .toList();
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return students.stream()
                .filter(student -> student.getFirstName().equals(name))
                .sorted(
                        Comparator
                                .comparing(Student::getLastName)
                                .thenComparing(Student::getId))
                .toList();
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return getStudentsBySth(
                students,
                student -> student.getLastName().equals(name),
                Comparator
                .comparing(Student::getFirstName)
                .thenComparing(Student::getId)
        );
    }

    private static List<Student> getStudentsBySth(Collection<Student> students, Predicate<Student> predicate, Comparator<Student> comparator1) {
        return students.stream()
                .filter(predicate)
                .sorted(comparator1)
                .toList();
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, GroupName group) {
        return getStudentsBySth(
                students,
                student -> student.getGroup() == group,
                comparator
        );
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, GroupName group) {
        return students.stream()
                .filter(student -> student.getGroup() == group)
                .collect(Collectors.toMap(Student::getLastName, Student::getFirstName, BinaryOperator.minBy(Comparator.naturalOrder())));
    }
}
