
package foorumi;

import foorumi.database.AlueDao;
import foorumi.database.Dao;
import foorumi.database.Database;
import foorumi.database.ViestiDao;
import foorumi.database.ViestiketjuDao;
import foorumi.domain.Alue;
import foorumi.domain.Viesti;
import java.sql.SQLException;
import java.util.HashMap;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        
        Database database = new Database("jdbc:sqlite:foorumi.db");
        AlueDao alueDao = new AlueDao(database);
        ViestiketjuDao viestiketjuDao = new ViestiketjuDao(database);
        ViestiDao viestiDao = new ViestiDao(database);
        
        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("alueet", alueDao.findAll());

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        
        
        get("/forum/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("alue", "Alue"); //alueDao.findOne(Integer.parseInt(req.params("id"))));
            map.put("viestiketjut", viestiketjuDao.findAll()); //.findAllWithValue("alue", req.params("id")));
            
            return new ModelAndView(map, "forum");
        }, new ThymeleafTemplateEngine());
        
        
        get("/topic/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("viestit", viestiDao.findAll());
            
            return new ModelAndView(map, "topic");
        }, new ThymeleafTemplateEngine());
        
    }
}