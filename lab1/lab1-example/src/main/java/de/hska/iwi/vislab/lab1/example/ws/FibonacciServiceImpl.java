package de.hska.iwi.vislab.lab1.example.ws;

import javax.jws.WebService;

/** Dienstimplementierung */
@WebService(endpointInterface = "de.hska.iwi.vislab.lab1.example.ws.FibonacciService")
public class FibonacciServiceImpl implements FibonacciService {

	@Override
	public int getFibonacci(int n) {
		return fib(n);
    }
    
    private int fib(int n) {
        if (n == 0) {
            return 0;
        } else if (n == 1) {
            return 1;
        } else {
            return  fib(n - 1) + fib(n - 2);
        }
    }
}