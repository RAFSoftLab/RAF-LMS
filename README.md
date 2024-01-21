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

![image](https://github.com/RAFSoftLab/RAF-LMS/assets/43738975/a7d43947-c50b-4bc6-8909-bb9387a438f6)


## Uputstvo za postavljanje git http servera

### Šta će vam biti potrebno

Da biste uspešno pokrenuli HTTP Git Server, trebaće vam sledeće:

1. Funkcionalna instanca Ubuntu Server 18.04.
2. Korisnik sa sudo privilegijama.

### Ažuriranje i Nadogradnja

Prva stvar koju želite uraditi je ažurirati i nadograditi vašu instancu Ubuntu servera.
Međutim, zapamtite, ukoliko se u procesu ažuriranja menja jezgro (kernel),
moraćete ponovo pokrenuti server.
Zbog toga se pobrinite da izvršite ažuriranje/nadogradnju u trenutku kada je ponovno pokretanje moguće.

Prijavite se na svoj Ubuntu server i ažurirajte apt uz pomoć komande:

````
sudo apt-get update
````

Nakon što je apt ažuriran, nadogradite server uz pomoć komande:

````
sudo apt-get upgrade -y
````

Kada se ovaj proces završi, ponovo pokrenite server (ako je potrebno).

Instaliranje Zavisnosti
Sve što je potrebno za HTTP Git Server možete instalirati jednom komandom. Vratite se u terminal i izdajte:

````
sudo apt-get install nginx git nano fcgiwrap apache2-utils -y
````

To je sve što je potrebno za instalaciju softvera na vašem serveru.

Kreiranje Git Direktorijuma za Repozitorijume
Sada kada je sve instalirano, kreirajte direktorijum za smeštanje Git repozitorijuma pomoću komande:

````
sudo mkdir /srv/git
````

Dodelite odgovarajući vlasništvo tom direktorijumu uz pomoć komande:

````
sudo chown -R www-data:www-data /srv/git
````

Konfiguracija NGINX-a
NGINX sada mora biti konfigurisan tako da zna kako da posluži repozitorijume na serveru. Da biste to postigli,
otvorite podrazumevani NGINX konfiguracioni fajl sa komandom:

````
sudo nano /etc/nginx/sites-available/default
````

Potražite sledeći odeljak:

````
location / {
                # First attempt to serve request as file, then
                # as directory, then fall back to displaying a 404.
                try_files $uri $uri/ =404;
        }
````


U tom odeljku, unesite sledeće:


````
location ~ (/.*) {
    client_max_body_size 0; # Git pushes can be massive, just to make sure nginx doesn't suddenly cut the connection add this.
    auth_basic "Git Login"; # Whatever text will do.
    auth_basic_user_file "/srv/git/htpasswd";
    include /etc/nginx/fastcgi_params; # Include the default fastcgi configs
    fastcgi_param SCRIPT_FILENAME /usr/lib/git-core/git-http-backend; # Tells fastcgi to pass the request to the git http backend executable
    fastcgi_param GIT_HTTP_EXPORT_ALL "";
    fastcgi_param GIT_PROJECT_ROOT /srv/git; # /srv/git is the location of all of your git repositories.
    fastcgi_param REMOTE_USER $remote_user;
    fastcgi_param PATH_INFO $1; # Takes the capture group from our location directive and gives git that.
    fastcgi_pass  unix:/var/run/fcgiwrap.socket; # Pass the request to fastcgi
}
````

Sačuvajte i zatvorite fajl.

Pokrenite NGINX test konfiguracije komandom:

````
sudo nginx -t
````

Trebalo bi da vidite sledeće poruke:
````
nginx: the configuration file /etc/nginx/nginx.conf syntax is ok
nginx: configuration file /etc/nginx/nginx.conf test is successful
````

Ukoliko vidite greške, vratite se u konfiguracioni fajl i proverite da li je gornji kod unešen u odgovarajućem odeljku.

Kreirajte Korisnički Nalog
Sada treba da kreirate korisnika koji će imati pristup HTTP Git Serveru. To možete uraditi pomoću htpasswd komande. Demonstriraću kreiranje korisnika "jack". Naravno, trebalo bi da kreirate korisnika koji odgovara vašim potrebama.

Za kreiranje novog korisnika, izdajte komandu:
````
sudo htpasswd -c /srv/git/htpasswd foo
````

Biće vam zatraženo da unesete i potvrdite novu lozinku za korisnika. Kada to završite, ponovo pokrenite NGINX komandom:
````
sudo systemctl restart nginx
````

Kreirajte Prvi Repozitorijum
Vreme je da kreirate prvi repozitorijum. Pošto smo upravo kreirali korisnika "foo", držaćemo se toga.
Ipak, zapamtite da kreirate repozitorijum sa istim imenom koje ste koristili prilikom kreiranja novog korisničkog naloga.

Da biste kreirali novi repozitorijum, promenite se u git direktorijum komandom:
````
cd /srv/git
````

Sada kreirajte repozitorijum komandom:
````
sudo mkdir project1.git
````

Uđite u ovaj novi direktorijum komandom:
````
cd project1.git
````

Sada ćemo inicializovati repozitorijum komandom:
````
sudo git --bare init
````

Zatim želimo ažurirati Git server kako bi bio svestan promena. Izdajte komandu:
````
sudo git update-server-info
````

Promenite vlasništvo nad novim repozitorijumom komandom:
````
sudo chown -R www-data:www-data .
````

Promenite dozvole repozitorijuma komandom:
````
sudo chmod -R 755 .
````

Povežite se sa Repozitorijumom

Sada mozete klonirati repozitorijum sa servera sledecom komandom:
````
git clone foo@SERVER_IP:/srv/project1.git
````

Bićete upitani za lozinku korisnika koju ste kreirali.
Nakon uspešne autentifikacije, git će klonirati repozitorijum i trebalo bi da vidite novi direktorijum
(u ovom slučaju, jack) u vašem trenutnom radnom direktorijumu.

Kako biste mogli da commitujete na server, izvrsite sledecu komandu:
````
git remote add origin http://foo@SERVER_IP/project1.git
````




