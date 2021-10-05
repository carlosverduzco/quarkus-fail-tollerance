package org.acme.controller;

import org.acme.model.Person;
import org.eclipse.microprofile.faulttolerance.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
public class PersonController {
    List<Person> personList = new ArrayList<>();
    Logger LOGGER = Logger.getLogger("Demologger");

    @GET
    @Timeout(value = 5000L)
    @Retry(maxRetries = 2)
    //@CircuitBreaker(failureRatio = 0.1, delay = 5000L)
    @Bulkhead(value = 100)
    @Fallback(fallbackMethod = "getPersonFallbackList")
    public List<Person> getPersonList(){
        LOGGER.info("Ejecutando person list");
        //doFail();
        doWait();
        return this.personList;
    }

    public List<Person> getPersonFallbackList(){
        var person = new Person(-1L,"Carlos","carloseverduzco@hotmail.com");
        return List.of(person);
    }

    public void doWait(){
        var random = new Random();
        try {
            LOGGER.warning("Haciendo un sleep");
            Thread.sleep((random.nextInt(10) + 4) * 1000L);
        }catch (Exception ex){

        }
    }

    public void doFail(){
        var random = new Random();
        if(random.nextBoolean()){
            LOGGER.warning("Se produjo una falla");
            throw new RuntimeException("Haciendo una falla en implementacion");
        }
    }
}
