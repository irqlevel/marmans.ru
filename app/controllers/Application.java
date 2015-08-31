package controllers;

import play.*;
import play.mvc.*;
import play.Logger;
import views.html.*;

import java.math.BigInteger;

public class Application extends Controller {

    public Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public Result home() {
        Logger.info("at home");
        return ok(home.render());
    }

    public Result factorial(Integer number) {
        if (number < 0)
            return ok(factorial.render(number.toString(), "UNDEFINED"));
        if (number == 0)
            return ok(factorial.render(number.toString(), "0"));
        BigInteger fac = BigInteger.valueOf(1);
        for (int i = 1; i <= number; i++) {
            fac = fac.multiply(BigInteger.valueOf(i));
        }

        return ok(factorial.render(number.toString(), fac.toString()));
    }
}
