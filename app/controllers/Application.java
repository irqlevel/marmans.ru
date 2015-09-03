package controllers;

import api.AppResult;
import api.AppUserJoin;
import api.AppUserProfile;
import api.AppUserSignin;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lib.BCrypt;

import models.*;

import play.libs.F.*;
import play.libs.Json;
import play.mvc.*;
import play.Logger;
import views.html.*;

import java.math.BigInteger;
import java.util.List;

public class Application extends Controller {
    public Promise<Result> index() {
        Logger.info("index");
        Promise<UserAuth> promise = Promise.promise(() -> currUserAuth());
        return promise.map(userAuth -> ok(index.render(userAuth.user, userAuth.csrfToken, "Hello world!")));
    }

    private UsersResult usersJob() {
        Logger.info("users job");
        UsersResult result = new UsersResult();
        result.users = Users.getAll();
        result.userAuth = currUserAuth();
        return result;
    }

    public Promise<Result> users() {
        Logger.info("users");
        Promise<UsersResult> promise = Promise.promise(() -> usersJob());
        return promise.map(result -> ok(usersv.render(result.userAuth.user, result.userAuth.csrfToken, result.users)));
    }

    public Promise<Result> signin() {
        Logger.info("signin");
        Promise<UserAuth> promise = Promise.promise(() -> currUserAuth());
        return promise.map(userAuth -> ok(signinv.render(userAuth.user, userAuth.csrfToken)));
    }

    public Promise<Result> join() {
        Logger.info("join");
        Promise<UserAuth> promise = Promise.promise(() -> currUserAuth());
        return promise.map(userAuth -> ok(joinv.render(userAuth.user, userAuth.csrfToken)));
    }

    private AppResult postJoinJob(AppUserJoin join) {
        try {
            Logger.info("post join job");
            if (join.email == null || join.password == null)
                throw new Exception("bad request");

            Logger.info("email=" + join.email + " password=" + join.password);

            User user = Users.join(join.email, join.password);
            if (user == null) {
                return AppResult.error(1, "join failed");
            }
            AppResult result = AppResult.success();
            result.uid = user.getUid();
            return result;
        } catch (Exception e) {
            Logger.error("bad request", e);
            return AppResult.error(1, "join failed");
        } finally {
        }
    }

    public Promise<Result> appResultToPromise(AppResult result) {
        Promise<AppResult> promise = Promise.promise(() -> result);
        return promise.map(val -> ok(val.toJson()));
    }

    public Promise<Result> postJoin() {
        Logger.info("post join");
        JsonNode json = request().body().asJson();
        if (json == null) {
            return appResultToPromise(AppResult.error(1, "invalid data"));
        }
        AppUserJoin join = AppUserJoin.parseJson(json);
        Promise<AppResult> promise = Promise.promise(() -> postJoinJob(join));
        return promise.map(val -> ok(val.toJson()));
    }

    private AppResult postSigninJob(AppUserSignin signin) {
        try {
            Logger.info("post signin job");
            if (signin.email == null || signin.password == null)
                throw new Exception("bad request");

            Logger.info("email=" + signin.email + " password=" + signin.password);

            User user = Users.getByEmail(signin.email);
            if (user == null) {
                return AppResult.error(1, "signin failed");
            }

            if (!BCrypt.checkpw(signin.password, user.getHashp())) {
                return AppResult.error(1, "signin failed");
            }

            UserSession session = UserSession.genSession(user.getUid(), 24*3600*1000);
            if (!UserSessions.insert(session)) {
                return AppResult.error(1, "signin failed");
            }
            session().put("usersession", session.getValue());

            Logger.info("user signed in uid=" + user.getUid());
            AppResult result = AppResult.success();
            result.uid = user.getUid();
            return result;
        } catch (Exception e) {
            Logger.error("bad request", e);
            return AppResult.error(1, "signin failed");
        } finally {

        }
    }

    public Promise<Result> postSignin() {
        Logger.info("post join");
        JsonNode json = request().body().asJson();
        if (json == null) {
            return appResultToPromise(AppResult.error(1, "invalid data"));
        }
        AppUserSignin signin = AppUserSignin.parseJson(json);
        Promise<AppResult> promise = Promise.promise(() -> postSigninJob(signin));
        return promise.map(val -> ok(val.toJson()));
    }

    private AppResult signoutJob() {
        String value = session().get("usersession");
        session().remove("usersession");

        UserSession session = UserSessions.get(value);
        if (session != null) {
            if (session.getExpires() > System.currentTimeMillis()) {
                UserSessions.deleteByUid(session.getUid());
            } else {
                UserSessions.delete(session.getValue());
            }
        }
        return AppResult.success();
    }

    public Promise<Result> postSignout() {
        Logger.info("post signout session=" + session().get("usersession"));

        Promise<AppResult> promise = Promise.promise(() -> signoutJob());
        return promise.map(val -> ok(val.toJson()));
    }

    private UserAuth currUserAuth() {
        UserAuth userAuth = new UserAuth();
        String value = session().get("usersession");
        if (value == null)
            return userAuth;
        UserSession session = UserSessions.get(value);
        if (session == null)
            return userAuth;
        if (session.getExpires() <= System.currentTimeMillis())
            return userAuth;
        userAuth.session = session;
        userAuth.csrfToken = session.getCsrfToken();
        User user = Users.get(session.getUid());
        if (user != null) {
            userAuth.user = user;
            Logger.info("auth user uid=" + user.getUid() + " email=" + user.getEmail());
        }
        return userAuth;
    }

    public ProfileResult profileJob(long uid) {
        Logger.info("profile job");
        ProfileResult result = new ProfileResult();
        result.userAuth = currUserAuth();
        result.user = Users.get(uid);
        return result;
    }

    public Promise<Result> profile(long uid) {
        Logger.info("profile uid=" + uid);
        Promise<ProfileResult> promise = Promise.promise(() -> profileJob(uid));
        return promise.map(result -> (result.user != null) ?
                ok(profilev.render(result.userAuth.user, result.userAuth.csrfToken, result.user)) : notFound());
    }

    private AppResult postProfileJob(AppUserProfile profile) {
        try {
            String csrfToken = request().getHeader("X-Csrf-Token");
            Logger.info("post profile job csrfToken=" + csrfToken);

            if (profile.name == null)
                throw new Exception("bad request");

            Logger.info("name=" + profile.name);
            UserAuth userAuth = currUserAuth();
            User user = userAuth.user;
            if (user == null)
                throw new Exception("bad request");
            if (!csrfToken.equals(userAuth.csrfToken))
                throw new Exception("bad request");
            if (!Users.updateName(user.getUid(), profile.name))
                return AppResult.error(1, "can't update");
            return AppResult.success();
        } catch (Exception e) {
            Logger.error("bad request", e);
            return AppResult.error(1, "bad request");
        } finally {

        }
    }

    public Promise<Result> postProfile() {
        Logger.info("post profile");
        JsonNode json = request().body().asJson();
        if (json == null) {
            return appResultToPromise(AppResult.error(1, "invalid data"));
        }
        AppUserProfile profile = AppUserProfile.parseJson(json);
        Promise<AppResult> promise = Promise.promise(() -> postProfileJob(profile));
        return promise.map(val -> ok(val.toJson()));
    }

    public Promise<Result> currentProfile() {
        Logger.info("profile");

        Promise<UserAuth> promise = Promise.promise(() -> currUserAuth());
        return promise.map(userAuth -> (userAuth.user == null) ? redirect(controllers.routes.Application.signin()) :
                ok(profilev.render(userAuth.user, userAuth.csrfToken, userAuth.user)));
    }
}
