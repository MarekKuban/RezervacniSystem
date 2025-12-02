DROP DATABASE IF EXISTS RezervacniSystemJazyku;
CREATE DATABASE RezervacniSystemJazyku CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE RezervacniSystemJazyku;

-- Tabulka Jazyky: Přibyl sloupec 'trida_urceni'
CREATE TABLE jazyky (
    jazyk_id INT PRIMARY KEY AUTO_INCREMENT,
    nazev_jazyka VARCHAR(100) NOT NULL,
    trida_urceni VARCHAR(1) NOT NULL,
    max_kapacita INT NOT NULL,
    aktualni_pocet_registrovanych INT DEFAULT 0
);

-- Tabulka Studenti: Přibyl sloupec 'trida'
CREATE TABLE studenti (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    rodne_cislo VARCHAR(11) NOT NULL UNIQUE,
    jmeno VARCHAR(100) NOT NULL,
    prijmeni VARCHAR(100) NOT NULL,
    trida VARCHAR(1) NOT NULL
);

-- Tabulka Registrace: Beze změny
CREATE TABLE registrace (
    registrace_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    jazyk_id INT NOT NULL,
    datum_registrace TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES studenti(student_id),
    FOREIGN KEY (jazyk_id) REFERENCES jazyky(jazyk_id),
    UNIQUE (student_id, jazyk_id)
);

-- Triggery pro automatické počítání kapacity
DELIMITER //
CREATE TRIGGER tr_registrace_insert AFTER INSERT ON registrace
FOR EACH ROW UPDATE jazyky SET aktualni_pocet_registrovanych = aktualni_pocet_registrovanych + 1 WHERE jazyk_id = NEW.jazyk_id;
//
CREATE TRIGGER tr_registrace_delete AFTER DELETE ON registrace
FOR EACH ROW UPDATE jazyky SET aktualni_pocet_registrovanych = aktualni_pocet_registrovanych - 1 WHERE jazyk_id = OLD.jazyk_id;
//
DELIMITER ;

-- VLOŽENÍ DAT: 4 Jazyky pro každou z 5 tříd (Celkem 20)
INSERT INTO jazyky (nazev_jazyka, trida_urceni, max_kapacita) VALUES
('Španělština', 'A', 15), ('Němčina', 'A', 30), ('Ruština', 'A', 20), ('Francouzština', 'A', 10),
('Španělština', 'B', 15), ('Němčina', 'B', 30), ('Ruština', 'B', 20), ('Francouzština', 'B', 20),
('Španělština', 'C', 15), ('Němčina', 'C', 30), ('Ruština', 'C', 20), ('Francouzština', 'C', 20),
('Španělština', 'D', 15), ('Němčina', 'D', 30), ('Ruština', 'D', 20), ('Francouzština', 'D', 20),
('Španělština', 'E', 15), ('Němčina', 'E', 30), ('Ruština', 'E', 20), ('Francouzština', 'E', 20);

-- VLOŽENÍ DAT: 10 Studentů do různých tříd
INSERT INTO studenti (rodne_cislo, jmeno, prijmeni, trida) VALUES
('080000/0000', 'Jan', 'Novák', 'A'),
('080101/0001', 'Petr', 'Svoboda', 'A'),
('080102/0002', 'Jana', 'Dvořáková', 'A'),
('080103/0003', 'Tomáš', 'Novotný', 'B'),
('080104/0004', 'Lucie', 'Černá', 'B'),
('080105/0005', 'Martin', 'Procházka', 'C'),
('080106/0006', 'Eva', 'Kučerová', 'C'),
('080107/0007', 'Jakub', 'Veselý', 'D'),
('080108/0008', 'Anna', 'Horáková', 'D'),
('080109/0009', 'David', 'Němec', 'E'),
('080110/0010', 'Lenka', 'Marek', 'E');