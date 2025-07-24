--liquibase formatted sql

-- changeset galya:1
create index student_name_index on student (name);

-- changeset galya:2
create index faculty_name_color_index on faculty(name,color);