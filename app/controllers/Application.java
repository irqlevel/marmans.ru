package controllers;

import play.*;
import play.libs.F.*;
import play.mvc.*;
import play.Logger;
import views.html.*;

import java.math.BigInteger;

public class Application extends Controller {

    public Result index() {
        return ok(index.render());
    }

    public String factorialCalc(int number) {
        Logger.info("calc fac of " + number);
        if (number < 0)
            return "UNDEFINED";
        if (number == 0)
            return "0";

        BigInteger fac = BigInteger.valueOf(1);
        for (int i = 1; i <= number; i++) {
            fac = fac.multiply(BigInteger.valueOf(i));
        }
        return fac.toString();
    }

    public Promise<Result> factorial2(final Integer number) {
        Logger.info("request to calc fac of " + number);
        Promise<String> promise = Promise.promise(() -> factorialCalc(number));
        return promise.map(val -> ok(factorial.render(number.toString(), val)));
    }
}
