
package foorumi;

import foorumi.database.AlueDao;
import foorumi.database.Database;
import foorumi.database.KayttajaDao;
import foorumi.database.ViestiDao;
import foorumi.database.ViestiketjuDao;
import foorumi.domain.Alue;
import foorumi.domain.Kayttaja;
import foorumi.domain.Osiotieto;
import foorumi.domain.Viesti;
import foorumi.domain.Viestiketju;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // asetetaan portti jos heroku antaa PORT-ympäristömuuttujan
        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
           
        }
          // käytetään oletuksena paikallista sqlite-tietokantaa
        String jdbcOsoite = "jdbc:sqlite:foorumi.db";
        // jos heroku antaa käyttöömme tietokantaosoitteen, otetaan se käyttöön
        if (System.getenv("DATABASE_URL") != null) {
            jdbcOsoite = System.getenv("DATABASE_URL");
        } 

        ///Database db = new Database(jdbcOsoite);
        
        Database database = new Database(jdbcOsoite);
        KayttajaDao kayttajaDao = new KayttajaDao(database);
        AlueDao alueDao = new AlueDao(database);
        ViestiketjuDao viestiketjuDao = new ViestiketjuDao(database, alueDao);
        ViestiDao viestiDao = new ViestiDao(database, alueDao, viestiketjuDao, kayttajaDao);
        
        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("osiot", alueDao.FindAllInfo());

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        
        post("/newforum", (req, res) -> {
            alueDao.create(new Alue(0, req.queryParams("nimi")));
            
            res.redirect("/");
                    
            return null;
        });
        
        
        get("/forum/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            Alue alue = alueDao.findOne(Integer.parseInt(req.params(":id")));
            int sivu = (req.queryParams("page") == null) ? 1 : Integer.parseInt(req.queryParams("page"));
            List<Osiotieto> osiot = viestiketjuDao.findInfoFromAlue(alue, (sivu-1)*10, 11);
            if (osiot.size() > 10) {
                osiot.remove(10);
                map.put("seuraava", true);
            } else {
                map.put("seuraava", false);
            }
            map.put("sivu", sivu);
            map.put("alue", alue);
            map.put("osiot", osiot);
            
            return new ModelAndView(map, "forum");
        }, new ThymeleafTemplateEngine());
        
        get("/forum/:id/newtopic", (req, sys) -> {
            HashMap map = new HashMap<>();
            Alue alue = alueDao.findOne(Integer.parseInt(req.params(":id")));
            map.put("alue", alue);
        
            return new ModelAndView(map, "newtopic");
        }, new ThymeleafTemplateEngine());
        
        post("/forum/:id/newtopic", (req, res) -> {
            String nimimerkki = req.queryParams("nimimerkki");
            if (nimimerkki.trim().isEmpty()) { res.redirect("/topic/" + req.params(":id")); }
            
            Alue alue = alueDao.findOne(Integer.parseInt(req.params(":id")));
            viestiketjuDao.create(new Viestiketju(0, alue, req.queryParams("otsikko")));
            Viestiketju viestiketju = viestiketjuDao.findWithOtsikko(req.queryParams("otsikko"));
            Kayttaja kirjoittaja = kayttajaDao.findWithNimimerkki(nimimerkki);
            if (kirjoittaja == null) {
                kayttajaDao.create(new Kayttaja(0, nimimerkki));
                kirjoittaja = kayttajaDao.findWithNimimerkki(nimimerkki);
            }
            viestiDao.create(new Viesti(0, viestiketju, kirjoittaja, null, req.queryParams("viesti")));
            
            res.redirect("/topic/" + viestiketju.getViestiketju_id());
            
            return "";
        });
        
        
        get("/topic/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            Viestiketju viestiketju = viestiketjuDao.findOne(Integer.parseInt(req.params(":id")));
            int sivu = (req.queryParams("page") == null) ? 1 : Integer.parseInt(req.queryParams("page"));
            List<Viesti> viestit = viestiDao.findFromViestiketju(viestiketju, (sivu-1)*10, 11);
            int viesteja = viestit.size();
            if (viesteja > 10) {
                viestit.remove(10);
                map.put("seuraava", true);
            } else {
                map.put("seuraava", false);
            }
            map.put("sivu", sivu);
            map.put("viestiketju", viestiketju);
            map.put("viestit", viestit);
            
            return new ModelAndView(map, "topic");
        }, new ThymeleafTemplateEngine());
        
        post("/topic/:id/newpost", (req, res) -> {
            Viestiketju viestiketju = viestiketjuDao.findOne(Integer.parseInt(req.params(":id")));
            String nimimerkki = req.queryParams("nimimerkki");
            Kayttaja kirjoittaja = kayttajaDao.findWithNimimerkki(nimimerkki);
            if (kirjoittaja == null) {
                kayttajaDao.create(new Kayttaja(0, nimimerkki));
                kirjoittaja = kayttajaDao.findWithNimimerkki(nimimerkki);
            }
            viestiDao.create(new Viesti(0, viestiketju, kirjoittaja, null, req.queryParams("viesti")));
            
            res.redirect("/topic/" + req.params(":id") + "?page=" + req.queryParams("sivu"));
            
            return "";
        });
        
    }
}