# RAF-LMS
RAF sistem za upravljanje učenjem, tj. RAF LMS, u svom osnovnom obliku, predstavlja platformu za kreiranje, izradu, pregledanje i ocenjivanje kolokvijuma i ispita iz predmeta koji se bave programiranjem.

Sistem ima serversku i klijentsku stranu. Serverska strana služi za skladištenje postavki, kao i urađenih zadataka koji dolaze na kolokvijumima ili ispitima, dok klijentska strana podržava integraciju sa Intellij razvojnim okruženjem.
Dva ključna tipa korisnika sistema su student i nastavnik.

## Mogući slučajevi upotrebe
- __Prijava studenta na sistem i preuzimanje zadataka__\
  Prilikom izrade kolokvijuma ili ispita, student se prijavljuje na sistem, unosi potrebne podatke za odabir zadataka, kao što je, na primer, grupa zadataka, ukoliko postoji više grupa, a zatim se sa serverske strane preuzima postavka zadataka.
- __Izrada zadataka__\
  Nakon prijave na sistem i preuzimanja zadataka, student počinje sa izradom zadatka, tj. kucanjem koda u Intellij razvojnom okruženju.
- __Predaja rada__\
  Po završetku izrade zadataka student može da preda rad pozivom odgovarajuće komande, koja studentski rad skladišti na serveru.
- __Konfigurisanje podataka za izradu kolokvijuma ili ispita__\
  Nastavnik ima mogućnost da konfiguriše podatke za izradu kolokvijuma ili ispita, da postavi tekstove zadataka i definiše grupe.
- __Preuzimanje studentskih radova i ocenjivanje__\
  Nakon što studenti predaju svoje radove, nastavnik može da preuzme sve zadatke ili samo deo zadataka na osnovu odabira određenih kriterijuma, npr. samo za jednu grupu, i da ih otvori u svom razvojnom okruženju, tj. kroz Intellij, iz kog može da pregleda zadatke i unese studentu poene za svaki pojedinačni zadatak.
- __Pregled radova i kolektivnih rezultata__\
  Prilikom unošenja poena za zadatke informacija o tome se čuva sa serverske strane u bazi podatadaka za ocenjivanje, na osnovu čega nastavnik može da zatraži da dobije zbirni prikaz osvojenih poena svih studenata, kao i da razlikuje pregledane radove od onih koji još uvek nisu pregledani.
- __Prosleđivanje rezultata studentima putem imejla__\
  Nastavnik, po završetku pregledanja studentskih radova i unošenja poena, može pozivom odgovarajuće metode da prosledi imejl sa rezultatima svim studentima.

## Dijagram arhitekture

![Početni dijagram arhitekture sistema](https://github.com/RAFSoftLab/RAF-LMS/assets/37117249/f6e4e5ee-6d56-477d-a4ed-0f7f70e09d5c)
