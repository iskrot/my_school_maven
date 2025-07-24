SELECT student.id, student.name, student.age, faculty.name INNER JOIN faculty ON student.faculty_id = faculty.id;

SELECT student.id, student.name, student.age FROM avatar INNER JOIN student ON avatar.id = student.id;
