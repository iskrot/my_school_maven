CREATE TABLE man {
    id INTEGER;
    age INTEGER;
    name VARCHAR(10);
    license BOOLEAN;
    carId INTEGER;
}

CREATE TABLE car {
    id INTEGER;
    make TEXT;
    model TEXT;
    price INTEGER;
}

SELECT man.id, man.name, man.age, car.id FROM man INNER JOIN car ON man.carId = car.id