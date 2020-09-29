package no.hvl.dat250.jpa.basicexample;

import com.google.gson.Gson;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import static spark.Spark.after;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.put;


import static spark.Spark.*;

public class SparkMain {
    private static final String PERSISTENCE_UNIT_NAME = "todos";
    private static EntityManagerFactory factory;

    public static void main(String[] args) {

        if (args.length > 0) {
            port(Integer.parseInt(args[0]));
        } else {
            port(8080);
        }

        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();
        // read the existing entries and write to console

        after((req, res) -> {
            res.type("application/json");
        });

        get("/hello", (req, res) -> "Hello World!");

        get("/todos", (req, res) -> {
            Gson todoListJson = new Gson();
            Query q = em.createQuery("select t from Todo t");
            return todoListJson.toJson(q.getResultList());
        });

        put("/todos", (req,res) -> {
            Gson gson = new Gson();
            em.getTransaction().begin();
            Todo todo = gson.fromJson(req.body(), Todo.class);
            em.persist(todo);
            em.getTransaction().commit();
            return todo.toJson();
        });
    }
}
