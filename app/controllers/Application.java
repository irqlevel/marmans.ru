package controllers;

import models.Mybatis;
import models.User;
import models.Users;
import play.*;
import play.libs.F.*;
import play.mvc.*;
import play.Logger;
import views.html.*;

import java.math.BigInteger;
import java.util.List;

public class Application extends Controller {

    private String dbSyncJob() {
        if (Mybatis.dbSync())
            return "Success";
        else
            return "Failure";
    }

    public Promise<Result> dbSync() {
        Logger.info("db sync");
        Promise<String> promise = Promise.promise(() -> dbSyncJob());
        return promise.map(val -> ok(dbsync.render(val)));
    }

    private String dbDropJob() {
        if (Mybatis.dbDrop())
            return "Success";
        else
            return "Failure";
    }

    public Promise<Result> dbDrop() {
        Logger.info("db drop");
        Promise<String> promise = Promise.promise(() -> dbDropJob());
        return promise.map(val -> ok(dbdrop.render(val)));
    }

    public Result index() {
        return ok(index.render("Hello world!"));
    }

    private String factorialCalc(int number) {
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

    public Promise<Result> createUser() {
        Promise<User> promise = Promise.promise(() -> Users.createUser());
        return promise.map(user -> ok(createuser.render(user)));
    }

    public Promise<Result> users() {
        Promise<List<User>> promise = Promise.promise(() -> Users.getUsers());
        return promise.map(users -> ok(usersv.render(users)));
    }
}
