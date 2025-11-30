-- 1. Úklid: Smažeme starou databázi, pokud existuje, ať začínáme s čistým štítem
DROP DATABASE IF EXISTS RezervacniSystemJazyku;

-- 2. Vytvoření databáze s podporou češtiny (UTF-8)
CREATE DATABASE RezervacniSystemJazyku CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE RezervacniSystemJazyku;

-- 3. Tabulka: JAZYKY
-- Uchovává seznam předmětů a jejich kapacitu
CREATE TABLE jazyky (
                        jazyk_id INT PRIMARY KEY AUTO_INCREMENT,
                        nazev_jazyka VARCHAR(100) NOT NULL UNIQUE,
                        max_kapacita INT NOT NULL,
                        aktualni_pocet_registrovanych INT DEFAULT 0
);

-- 4. Tabulka: STUDENTI
-- Whitelist studentů, kteří se mohou přihlásit
CREATE TABLE studenti (
                          student_id INT PRIMARY KEY AUTO_INCREMENT,
                          rodne_cislo VARCHAR(11) NOT NULL UNIQUE, -- Formát RRRRMM/XXXX
                          jmeno VARCHAR(100) NOT NULL,
                          prijmeni VARCHAR(100) NOT NULL,
                          trida VARCHAR(1) NOT NULL
);

-- 5. Tabulka: REGISTRACE
-- Propojovací tabulka (Kdo -> Kam)
CREATE TABLE registrace (
                            registrace_id INT PRIMARY KEY AUTO_INCREMENT,
                            student_id INT NOT NULL,
                            jazyk_id INT NOT NULL,
                            datum_registrace TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Cizí klíče (vazby na ostatní tabulky)
                            FOREIGN KEY (student_id) REFERENCES studenti(student_id),
                            FOREIGN KEY (jazyk_id) REFERENCES jazyky(jazyk_id),

    -- Unikátní klíč: Jeden student může mít jen jeden jazyk
                            UNIQUE (student_id, jazyk_id)
);

-- 6. TRIGGERY (Automatizace)
-- Tyto skripty automaticky počítají obsazenost v tabulce jazyky

DELIMITER //

-- Trigger: Když se student zapíše -> zvyšíme počet o 1
CREATE TRIGGER tr_registrace_insert
    AFTER INSERT ON registrace
    FOR EACH ROW
BEGIN
    UPDATE jazyky
    SET aktualni_pocet_registrovanych = aktualni_pocet_registrovanych + 1
    WHERE jazyk_id = NEW.jazyk_id;
END;
//

-- Trigger: Když se student odhlásí (smaže) -> snížíme počet o 1
CREATE TRIGGER tr_registrace_delete
    AFTER DELETE ON registrace
    FOR EACH ROW
BEGIN
    UPDATE jazyky
    SET aktualni_pocet_registrovanych = aktualni_pocet_registrovanych - 1
    WHERE jazyk_id = OLD.jazyk_id;
END;
//

DELIMITER ;

-- ==========================================
-- 7. VLOŽENÍ TESTOVACÍCH DAT
-- ==========================================

-- Jazyky a jejich kapacity
INSERT INTO jazyky (nazev_jazyka, max_kapacita) VALUES
                                                    ('Španělština', 15),
                                                    ('Němčina', 30),
                                                    ('Ruština', 20),
                                                    ('Francouzština', 20);

-- Testovací student
INSERT INTO studenti (rodne_cislo, jmeno, prijmeni, trida) VALUES
    ('111111/1111', 'Jan', 'Novák', 'D');