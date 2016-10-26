
package foorumi;

import foorumi.database.AlueDao;
import foorumi.database.Database;
import foorumi.database.KayttajaDao;
import foorumi.database.ViestiDao;
import foorumi.database.ViestiketjuDao;
import foorumi.domain.Alue;
import foorumi.domain.Kayttaja;
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
        
        Database database = new Database("jdbc:sqlite:foorumi.db");
        KayttajaDao kayttajaDao = new KayttajaDao(database);
        AlueDao alueDao = new AlueDao(database);
        ViestiketjuDao viestiketjuDao = new ViestiketjuDao(database, alueDao);
        ViestiDao viestiDao = new ViestiDao(database, alueDao, viestiketjuDao, kayttajaDao);
        
        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("alueet", alueDao.findAll());

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
            List<Viesti> viestit = viestiDao.findLatestViestitInAlue(alue);
            map.put("alue", alue);
            map.put("viestit", viestit);
            
            return new ModelAndView(map, "forum");
        }, new ThymeleafTemplateEngine());
        
        get("/forum/:id/newtopic", (req, sys) -> {
            HashMap map = new HashMap<>();
            Alue alue = alueDao.findOne(Integer.parseInt(req.params(":id")));
            map.put("alue", alue);
        
            return new ModelAndView(map, "newtopic");
        }, new ThymeleafTemplateEngine());
        
        post("/forum/:id/newtopic", (req, res) -> {
            Alue alue = alueDao.findOne(Integer.parseInt(req.params(":id")));
            viestiketjuDao.create(new Viestiketju(0, alue, req.queryParams("otsikko")));
            Viestiketju viestiketju = viestiketjuDao.findWithOtsikko(req.queryParams("otsikko"));
            String nimimerkki = req.queryParams("nimimerkki");
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
            map.put("viestiketju", viestiketju);
            map.put("viestit", viestiDao.findAllInViestiketju(viestiketju));
            
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
            
            res.redirect("/topic/" + req.params(":id"));
            
            return "";
        });
        
    }
}