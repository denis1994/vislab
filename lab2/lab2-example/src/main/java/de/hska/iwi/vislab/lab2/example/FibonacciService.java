package de.hska.iwi.vislab.lab2.example;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("fibonacci")
public class FibonacciService {
    private static int fibonacciNumber = 0;
    private static long fibonacci = 0;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getFibonacci() {
        String outJson = "{\"n\": " + this.fibonacciNumber + ", \"fibonacci\": " + this.fibonacci + "}";
        return outJson;
    }

    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public String calcNextFibonacci() {
        this.fibonacciNumber++;
        this.fibonacci = this.fib(this.fibonacciNumber);
        return "success";
    }

    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public String restoreState() {
        this.fibonacciNumber = 0;
        this.fibonacci = 0;
        return "success";
    }

    private long fib(int n) {
        if (n == 0) {
            return 0;
        } else if (n == 1) {
            return 1;
        } else {
            return  fib(n - 1) + fib(n - 2);
        }
    }
}
