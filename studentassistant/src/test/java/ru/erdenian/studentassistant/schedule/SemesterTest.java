package ru.erdenian.studentassistant.schedule;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Todo: описание класса
 *
 * @author Pavel Lavrikov
 *         *
 * @since Todo: текущая версия
 */
public class SemesterTest {
    private Semester semester;
    private String name;
    private LocalDate firstDay;
    private LocalDate lastDay;
    private long id;

    @Before
    public void initialize() {
        name = "5 семестр";
        firstDay = new LocalDate(2016, 9, 1);
        lastDay = new LocalDate(2016, 12, 10);
        id = System.nanoTime();
        semester = new Semester(name, firstDay, lastDay, id);
    }

    @Test
    public void getLength() {
        String tempName = "5 семестр";
        int fDay = 1;
        int lDay = 10;
        int difference = lDay - fDay + 1;
        LocalDate tempFirstDay = new LocalDate(2016, 10, fDay);
        LocalDate tempLastDay = new LocalDate(2016, 10, lDay);
        long tempId = System.nanoTime();
        Semester semesterTest = new Semester(tempName, tempFirstDay, tempLastDay, tempId);
        assertEquals("Возвращена неверная длина.", difference, semesterTest.getLength());

    }

    @Test
    public void compareToEquals() {
        String name = "5 семестр";
        LocalDate firstDay = new LocalDate(2016, 9, 1);
        LocalDate lastDay = new LocalDate(2016, 12, 10);
        long id = System.nanoTime();
        Semester semesterTest = new Semester(name, firstDay, lastDay, id);
        assertEquals("Сравнение с таким же объектом провалилось. Семестры не равны.", 0, semester.compareTo(semesterTest));
        //assertEquals("Семестры  не одинаковы.\nТест не пройден.",1,semester.compareTo(semesterTest));
        //assertTrue("Семестры одинаковы.\nТест пройден.",semester.compareTo(semesterTest)==0);
        //assertTrue("Семестры  не одинаковы.\nТест не пройден.",semester.compareTo(semesterTest)==1);
    }

    @Test
    public void compareToNotEquals() {
        String tempName = "6 семестр";
        LocalDate tempFirstDay = new LocalDate(2016, 10, 1);
        LocalDate tempLastDay = new LocalDate(2016, 12, 10);
        long tempId = System.nanoTime();
        Semester semesterTest = new Semester(tempName, tempFirstDay, tempLastDay, tempId);
        assertEquals("Сравнение с отличным объектом провалилось. Семестры равны.", -1, semester.compareTo(semesterTest));
        //assertEquals("Семестры  одинаковы.\nТест не пройден.",0,semester.compareTo(semesterTest));
        //assertTrue("Семестры  не одинаковы.\nТест пройден.",semester.compareTo(semesterTest)==1);
        //assertTrue("Семестры  одинаковы.\nТест не пройден.",semester.compareTo(semesterTest)==0);
    }

    @Test
    public void getName() {
        String tempName = semester.getName();
        assertEquals("Возвращено верное значение.\nТест пройден.", name, tempName);
    }

    @Test
    public void getFirstDay() {
        LocalDate tempFirstDay = semester.getFirstDay();
        assertEquals("Возвращено верное значение.\nТест пройден.", firstDay, tempFirstDay);
    }

    @Test
    public void getLastDay() {
        LocalDate tempLastDay = semester.getLastDay();
        assertEquals("Возвращено верное значение.\nТест пройден.", lastDay, tempLastDay);
    }

    @Test
    public void getId() {
        long tempId = semester.getId();
        assertEquals("Возвращено верное значение.\nТест пройден.", id, tempId);
    }

    @Test(expected = Exception.class)
    public void constructorEmptyName() {
        String tempName = "";
        LocalDate tempFirstDay = new LocalDate(2016, 10, 1);
        LocalDate tempLastDay = new LocalDate(2016, 12, 10);
        long tempId = System.nanoTime();
        Semester semesterTest = new Semester(tempName, tempFirstDay, tempLastDay, tempId);
    }

    @Test(expected = Exception.class)
    public void constructorIncorrectData() {
        String tempName = "";
        LocalDate tempFirstDay = new LocalDate(2016, 13, 1);
        LocalDate tempLastDay = new LocalDate(2016, 9, 10);
        long tempId = System.nanoTime();
        Semester semesterTest = new Semester(tempName, tempFirstDay, tempLastDay, tempId);
    }

}