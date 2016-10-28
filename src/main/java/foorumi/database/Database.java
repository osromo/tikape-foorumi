
package foorumi.database;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private String databaseAddress;

    public Database(String databaseAddress) throws SQLException {
        this.databaseAddress = databaseAddress;

        init();
    }

    private void init() {
        List<String> lauseet = null;
        if (this.databaseAddress.contains("postgres")) {
            lauseet = postgreLauseet();
        } else {
            lauseet = sqliteLauseet();
        }

        // "try with resources" sulkee resurssin automaattisesti lopuksi
        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();

            // suoritetaan komennot
            for (String lause : lauseet) {
                System.out.println("Running command >> " + lause);
                st.executeUpdate(lause);
            }

        } catch (Throwable t) {
            // jos tietokantataulu on jo olemassa, ei komentoja suoriteta
            System.out.println("Error >> " + t.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        if (this.databaseAddress.contains("postgres")) {
            try {
                URI dbUri = new URI(databaseAddress);

                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

                return DriverManager.getConnection(dbUrl, username, password);
            } catch (Throwable t) {
                System.out.println("Error: " + t.getMessage());
                t.printStackTrace();
            }
        }

        return DriverManager.getConnection(databaseAddress);
    }

    private List<String> postgreLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
        lista.add("DROP TABLE Alue;");
        lista.add("DROP TABLE Kayttaja;");
        lista.add("DROP TABLE Viestiketju;");
        lista.add("DROP TABLE Viesti;");
        // heroku käyttää SERIAL-avainsanaa uuden tunnuksen automaattiseen luomiseen
        lista.add("CREATE TABLE Alue (alue_id SERIAL PRIMARY KEY, nimi varchar(128) UNIQUE NOT NULL CHECK(LENGTH(nimi) > 0));");
        lista.add("CREATE TABLE Kayttaja (kayttaja_id SERIAL PRIMARY KEY, nimimerkki varchar(128) NOT NULL UNIQUE CHECK(LENGTH(nimimerkki) > 0));");
        lista.add("CREATE TABLE Viestiketju (viestiketju_id SERIAL PRIMARY KEY, alue integer NOT NULL, otsikko varchar(128) UNIQUE NOT NULL CHECK(LENGTH(otsikko) > 0));");
        lista.add("CREATE TABLE Viesti (viesti_id SERIAL PRIMARY KEY, viestiketju integer NOT NULL, kirjoittaja integer NOT NULL, aikaleima timestamp NOT NULL DEFAULT(CURRENT_TIMESTAMP), viesti varchar(2048) UNIQUE NOT NULL CHECK(LENGTH(viesti) > 0), FOREIGN KEY(viestiketju) REFERENCES Viestiketju(viestiketju_id), FOREIGN KEY(kirjoittaja) REFERECES Kayttaja(kayttaja_id));");
        //lista.add("CREATE TABLE Tuote (id SERIAL PRIMARY KEY, nimi varchar(255));");
        //lista.add("INSERT INTO Tuote (nimi) VALUES ('postgresql-tuote');");

        return lista;
    }

    private List<String> sqliteLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
        lista.add("CREATE TABLE Alue (alue_id integer PRIMARY KEY, nimi varchar(128) UNIQUE NOT NULL CHECK(LENGTH(nimi) > 0));");
        lista.add("CREATE TABLE Kayttaja (kayttaja_id integer PRIMARY KEY, nimimerkki varchar(128) NOT NULL UNIQUE CHECK(LENGTH(nimimerkki) > 0));");
        lista.add("CREATE TABLE Viestiketju (viestiketju_id integer PRIMARY KEY, alue integer NOT NULL, otsikko varchar(128) UNIQUE NOT NULL CHECK(LENGTH(otsikko) > 0));");
        lista.add("CREATE TABLE Viesti (viesti_id integer PRIMARY KEY, viestiketju integer NOT NULL, kirjoittaja integer NOT NULL, aikaleima timestamp NOT NULL DEFAULT(CURRENT_TIMESTAMP), viesti varchar(2048) UNIQUE NOT NULL CHECK(LENGTH(viesti) > 0), FOREIGN KEY(viestiketju) REFERENCES Viestiketju(viestiketju_id), FOREIGN KEY(kirjoittaja) REFERECES Kayttaja(kayttaja_id));");
        //lista.add("CREATE TABLE Tuote (id integer PRIMARY KEY, nimi varchar(255));");
        //lista.add("INSERT INTO Tuote (nimi) VALUES ('sqlite-tuote');");

        return lista;
    }
}