package controllers;

import api.JoinRequest;
import api.Reply;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import lib.BCrypt;
import models.Mybatis;
import models.User;
import models.Users;
import play.*;
import play.api.mvc.Request;
import play.libs.F.*;
import play.libs.Json;
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
        Logger.info("create user");
        Promise<User> promise = Promise.promise(() -> Users.createUser());
        return promise.map(user -> ok(createuser.render(user)));
    }

    public Promise<Result> users() {
        Logger.info("get users");
        Promise<List<User>> promise = Promise.promise(() -> Users.getUsers());
        return promise.map(users -> ok(usersv.render(users)));
    }

    public Result signin() {
        Logger.info("signin");
        return ok(signinv.render());
    }

    public Result join() {
        Logger.info("join");
        return ok(joinv.render());
    }

    public Result postJoin() {
        try {
            Logger.info("post join");

            JsonNode json = request().body().asJson();
            if (json == null)
                throw new Exception("bad request");
            String email = json.findPath("email").textValue();
            String password = json.findPath("password").textValue();
            if (email == null || password == null)
                throw new Exception("bad request");

            Logger.info("email=" + email + " password=" + password);

            User user = Users.joinUser(email, password);
            ObjectNode result = Json.newObject();
            if (user != null) {
                Logger.info("user created uid=" + user.getUid());
                result.put("result", "success");
                result.put("resultCode", 0);
                result.put("uid", user.getUid());
            } else {
                result.put("result", "join failed");
                result.put("resultCode", 1);
            }
            return ok(result);
        } catch (Exception e) {
            Logger.error("bad request", e);
            return badRequest();
        } finally {

        }
    }

    public Result postSignin() {
        try {
            Logger.info("post signin");

            JsonNode json = request().body().asJson();
            if (json == null)
                throw new Exception("bad request");
            String email = json.findPath("email").textValue();
            String password = json.findPath("password").textValue();
            if (email == null || password == null)
                throw new Exception("bad request");

            Logger.info("email=" + email + " password=" + password);

            User user = Users.getUserByEmail(email);
            ObjectNode result = Json.newObject();
            if (user == null) {
                result.put("result", "signin failed");
                result.put("resultCode", 1);
                return ok(result);
            }

            if (!BCrypt.checkpw(password, user.getHashp())) {
                result.put("result", "signin failed");
                result.put("resultCode", 1);
                return ok(result);
            }

            Logger.info("user signed in uid=" + user.getUid());
            result.put("result", "success");
            result.put("resultCode", 0);
            result.put("uid", user.getUid());
            return ok(result);
        } catch (Exception e) {
            Logger.error("bad request", e);
            return badRequest();
        } finally {

        }
    }

    public Result postSignout() {
        Logger.info("post signout");
        return badRequest();
    }

    public Result profile(long uid) {
        Logger.info("profile uid=" + uid);
        User user = Users.getUser(uid);
        if (user == null)
            return badRequest();
        return ok(profilev.render(user));
    }
}
