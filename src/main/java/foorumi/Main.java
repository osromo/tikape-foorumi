
package foorumi;

import foorumi.database.AlueDao;
import foorumi.database.Dao;
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
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        
        Database database = new Database("jdbc:sqlite:foorumi.db");
        KayttajaDao kayttajaDao = new KayttajaDao(database);
        AlueDao alueDao = new AlueDao(database);
        ViestiketjuDao viestiketjuDao = new ViestiketjuDao(database, alueDao);
        ViestiDao viestiDao = new ViestiDao(database, viestiketjuDao, kayttajaDao);
        
        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("alueet", alueDao.findAll());

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        
        
        get("/forum/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("alue", alueDao.findOne(Integer.parseInt(req.params("id"))));
            map.put("viestiketjut", viestiketjuDao.findAllWithValue("alue", req.params("id")));
            
            return new ModelAndView(map, "forum");
        }, new ThymeleafTemplateEngine());
        
        
        get("/topic/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("viestiketju", req.params("id"));
            map.put("viestit", viestiDao.findAllWithValue("viestiketju", req.params("id")));
            
            return new ModelAndView(map, "topic");
        }, new ThymeleafTemplateEngine());
        
        post("/newpost/:id", (req, res) -> {
            viestiDao.save(req.params("id"), req.queryParams("nimimerkki"), req.queryParams("viesti"));
            
            res.redirect("/topic/" + req.params("id"));
            
            return "";
        });
    }
}