DROP DATABASE IF EXISTS RezervacniSystemJazyku;
CREATE DATABASE RezervacniSystemJazyku CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE RezervacniSystemJazyku;

-- Tabulka Jazyky
CREATE TABLE jazyky (
                        jazyk_id INT PRIMARY KEY AUTO_INCREMENT,
                        nazev_jazyka VARCHAR(100) NOT NULL,
                        trida_urceni VARCHAR(1) NOT NULL,
                        max_kapacita INT NOT NULL,
                        aktualni_pocet_registrovanych INT DEFAULT 0
);

-- Tabulka Studenti (obsahuje login i heslo)
CREATE TABLE studenti (
                          student_id INT PRIMARY KEY AUTO_INCREMENT,
                          jmeno VARCHAR(100) NOT NULL,
                          prijmeni VARCHAR(100) NOT NULL,
                          trida VARCHAR(1) NOT NULL,
                          login VARCHAR(50) NOT NULL UNIQUE,
                          heslo VARCHAR(50) NOT NULL
);

-- Tabulka Registrace
CREATE TABLE registrace (
                            registrace_id INT PRIMARY KEY AUTO_INCREMENT,
                            student_id INT NOT NULL,
                            jazyk_id INT NOT NULL,
                            datum_registrace TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (student_id) REFERENCES studenti(student_id),
                            FOREIGN KEY (jazyk_id) REFERENCES jazyky(jazyk_id),
                            UNIQUE (student_id, jazyk_id)
);

-- Triggery
DELIMITER //
CREATE TRIGGER tr_registrace_insert AFTER INSERT ON registrace
    FOR EACH ROW UPDATE jazyky SET aktualni_pocet_registrovanych = aktualni_pocet_registrovanych + 1 WHERE jazyk_id = NEW.jazyk_id;
//
CREATE TRIGGER tr_registrace_delete AFTER DELETE ON registrace
    FOR EACH ROW UPDATE jazyky SET aktualni_pocet_registrovanych = aktualni_pocet_registrovanych - 1 WHERE jazyk_id = OLD.jazyk_id;
//
DELIMITER ;

-- DATA Jazyky
INSERT INTO jazyky (nazev_jazyka, trida_urceni, max_kapacita) VALUES
                                                                  ('Španělština', 'A', 15), ('Němčina', 'A', 30), ('Ruština', 'A', 20), ('Francouzština', 'A', 10),
                                                                  ('Španělština', 'B', 15), ('Němčina', 'B', 30), ('Ruština', 'B', 20), ('Francouzština', 'B', 20),
                                                                  ('Španělština', 'C', 15), ('Němčina', 'C', 30), ('Ruština', 'C', 20), ('Francouzština', 'C', 20),
                                                                  ('Španělština', 'D', 15), ('Němčina', 'D', 30), ('Ruština', 'D', 20), ('Francouzština', 'D', 20),
                                                                  ('Španělština', 'E', 15), ('Němčina', 'E', 30), ('Ruština', 'E', 20), ('Francouzština', 'E', 20);

-- DATA Studenti (Login/Heslo)
INSERT INTO studenti (jmeno, prijmeni, trida, login, heslo) VALUES
                                                                ('Jan', 'Novák', 'A', 'novakj', 'heslo123'),
                                                                ('Petr', 'Svoboda', 'A', 'svobodap', 'kocka99'),
                                                                ('Jana', 'Dvořáková', 'A', 'dvorakovaj', 'skola2025'),
                                                                ('Tomáš', 'Novotný', 'B', 'novotnyt', 'tomas1'),
                                                                ('Lucie', 'Černá', 'B', 'cernal', 'lucie1234'),
                                                                ('Martin', 'Procházka', 'C', 'prochazkam', 'auto55'),
                                                                ('Eva', 'Kučerová', 'C', 'kucerovae', 'eva.k'),
                                                                ('Jakub', 'Veselý', 'D', 'veselyj', 'kubajezu'),
                                                                ('Anna', 'Horáková', 'D', 'horakovaa', 'anna777'),
                                                                ('David', 'Němec', 'E', 'nemecd', 'david.n'),
                                                                ('Lenka', 'Marek', 'E', 'marekl', 'lenka88');