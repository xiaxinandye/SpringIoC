package com.yunche.entity;

/**
 * @ClassName: Person
 * @Description:
 * @author: yunche
 * @date: 2018/08/30
 */
public class Person
{
    private Student student;
    private Teacher teacher;

    public Student getStudent()
    {
        return student;
    }

    public void setStudent(Student student)
    {
        this.student = student;
    }

    public Teacher getTeacher()
    {
        return teacher;
    }

    public void setTeacher(Teacher teacher)
    {
        this.teacher = teacher;
    }
}
