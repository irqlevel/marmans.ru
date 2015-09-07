package controllers;

import api.*;
import com.fasterxml.jackson.databind.JsonNode;
import lib.BCrypt;

import models.*;

import models.dba.Posts;
import models.dba.UserSessions;
import models.dba.Users;
import play.libs.F.*;
import play.mvc.*;
import play.Logger;
import scala.App;
import views.html.*;

import java.util.List;

public class Application extends Controller {
    public Promise<Result> index() {
        Logger.info("index");
        Promise<UserAuth> promise = Promise.promise(() -> currUserAuth());
        return promise.map(userAuth -> ok(index.render(userAuth, "Hello world!")));
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
        return promise.map(result -> ok(usersv.render(result.userAuth, result.users)));
    }

    public Promise<Result> signin() {
        Logger.info("signin");
        Promise<UserAuth> promise = Promise.promise(() -> currUserAuth());
        return promise.map(userAuth -> ok(signinv.render(userAuth)));
    }

    public Promise<Result> join() {
        Logger.info("join");
        Promise<UserAuth> promise = Promise.promise(() -> currUserAuth());
        return promise.map(userAuth -> ok(joinv.render(userAuth)));
    }

    private AppResult postJoinJob(AppUserJoin join) {
        try {
            Logger.info("post join job");
            if (join.email == null || join.password == null)
                throw new AppException(AppResult.EINVAL);

            Logger.info("email=" + join.email + " password=" + join.password);

            User user = Users.join(join.email, join.password);
            if (user == null)
                throw new AppException(AppResult.EDB_UPDATE);

            AppResult result = AppResult.success();
            result.uid = user.getUid();
            return result;
        } catch (AppException e) {
            return e.getResult();
        } catch (Exception e) {
            Logger.error("exception", e);
            return AppResult.error(AppResult.EEXCEPT);
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
            return appResultToPromise(AppResult.error(AppResult.EINVAL));
        }
        AppUserJoin join = AppUserJoin.parseJson(json);
        Promise<AppResult> promise = Promise.promise(() -> postJoinJob(join));
        return promise.map(val -> ok(val.toJson()));
    }

    private AppResult postSigninJob(AppUserSignin signin) {
        try {
            Logger.info("post signin job");
            if (signin.email == null || signin.password == null)
                throw new AppException(AppResult.EINVAL);

            Logger.info("email=" + signin.email + " password=" + signin.password);

            User user = Users.getByEmail(signin.email);
            if (user == null)
                throw new AppException(AppResult.ENOTFOUND);

            if (!BCrypt.checkpw(signin.password, user.getHashp()))
                throw new AppException(AppResult.EAUTH);

            if (postUserAuth() != null)
                signout();
            UserSession session = UserSession.genSession(user.getUid(), 24*3600*1000);
            if (!UserSessions.insert(session))
                throw new AppException(AppResult.EDB_UPDATE);

            session().put("usersession", session.getValue());

            Logger.info("user signed in uid=" + user.getUid());
            AppResult result = AppResult.success();
            result.uid = user.getUid();
            return result;
        } catch (AppException e) {
            return e.getResult();
        } catch (Exception e) {
            Logger.error("exception", e);
            return AppResult.error(AppResult.EEXCEPT);
        } finally {

        }
    }

    public Promise<Result> postSignin() {
        Logger.info("post signin");
        JsonNode json = request().body().asJson();
        if (json == null) {
            return appResultToPromise(AppResult.error(AppResult.EINVAL));
        }
        AppUserSignin signin = AppUserSignin.parseJson(json);
        Promise<AppResult> promise = Promise.promise(() -> postSigninJob(signin));
        return promise.map(val -> ok(val.toJson()));
    }

    private void signout() {
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
    }

    private AppResult signoutJob() {
        UserAuth userAuth = postUserAuth();
        if (userAuth == null)
            return AppResult.error(AppResult.EPERM);
        signout();
        return AppResult.success();
    }

    public Promise<Result> postSignout() {
        Logger.info("post signout");
        Promise<AppResult> promise = Promise.promise(() -> signoutJob());
        return promise.map(val -> ok(val.toJson()));
    }

    public static UserAuth currUserAuth() {
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
                ok(profilev.render(result.userAuth, result.user)) : notFound());
    }

    private UserAuth postUserAuth() {
        UserAuth userAuth = currUserAuth();
        String csrfToken = request().getHeader("X-Csrf-Token");
        if (userAuth.user == null || userAuth.csrfToken == null)
            return null;
        if (!csrfToken.equals(userAuth.csrfToken))
            return null;
        return userAuth;
    }

    private AppResult postProfileJob(AppUserProfile profile) {
        try {
            Logger.info("post profile job");

            if (profile.name == null)
                throw new AppException(AppResult.EINVAL);

            Logger.info("name=" + profile.name);
            UserAuth userAuth = postUserAuth();
            if (userAuth == null)
                throw new AppException(AppResult.EPERM);
            if (!Users.updateName(userAuth.user.getUid(), profile.name))
                return AppResult.error(AppResult.EDB_UPDATE);
            return AppResult.success();
        } catch (AppException e) {
            return e.getResult();
        } catch (Exception e) {
            return AppResult.error(AppResult.EEXCEPT);
        } finally {

        }
    }

    public Promise<Result> postProfile() {
        Logger.info("post profile");
        JsonNode json = request().body().asJson();
        if (json == null) {
            return appResultToPromise(AppResult.error(AppResult.EINVAL));
        }
        AppUserProfile profile = AppUserProfile.parseJson(json);
        Promise<AppResult> promise = Promise.promise(() -> postProfileJob(profile));
        return promise.map(val -> ok(val.toJson()));
    }

    public Promise<Result> currentProfile() {
        Logger.info("profile");

        Promise<UserAuth> promise = Promise.promise(() -> currUserAuth());
        return promise.map(userAuth -> (userAuth.user == null) ? redirect(controllers.routes.Application.signin()) :
                ok(profilev.render(userAuth, userAuth.user)));
    }

    public Promise<Result> postCreate() {
        Logger.info("post create");

        Promise<UserAuth> promise = Promise.promise(() -> currUserAuth());
        return promise.map(userAuth -> (userAuth.user == null) ? redirect(controllers.routes.Application.signin()) :
                ok(postcreatev.render(userAuth)));
    }

    private AppResult postCreateJob(AppPostCreate postCreate) {
        try {
            Logger.info("post create job");
            Logger.info("title=" + postCreate.title + " content=" + postCreate.content);

            if (postCreate.title == null || postCreate.content == null)
                throw new AppException(AppResult.EINVAL);

            UserAuth userAuth = postUserAuth();
            if (userAuth == null)
                throw new AppException(AppResult.EAUTH);

            postCreate.title = Character.toUpperCase(postCreate.title.charAt(0)) + postCreate.title.substring(1);
            Post post = Posts.create(userAuth.user.getUid(), postCreate.title, postCreate.content);
            if (post == null)
                throw new AppException(AppResult.EDB_UPDATE);
            AppResult result = AppResult.success();
            result.id = post.postId;
            Logger.info("post " + post.postId + " created");
            return result;
        } catch (AppException e) {
          return e.getResult();
        } catch (Exception e) {
            Logger.error("exception", e);
            return AppResult.error(AppResult.EEXCEPT);
        } finally {
        }
    }

    public Promise<Result> postPost() {
        Logger.info("post post");

        JsonNode json = request().body().asJson();
        if (json == null) {
            return appResultToPromise(AppResult.error(AppResult.EINVAL));
        }

        AppPostCreate postCreate = AppPostCreate.parseJson(json);
        Promise<AppResult> promise = Promise.promise(() -> postCreateJob(postCreate));
        return promise.map(val -> ok(val.toJson()));
    }

    private PostsResult postsJob() {
        PostsResult result = new PostsResult();
        result.posts = Posts.getAll();
        result.userAuth = currUserAuth();
        return result;
    }

    public Promise<Result> posts() {
        Logger.info("posts");
        Promise<PostsResult> promise = Promise.promise(() -> postsJob());
        return promise.map(val -> ok(postsv.render(val.userAuth, val.posts)));
    }

    private PostResult postJob(long postId) {
        PostResult result = new PostResult();
        result.post = Posts.get(postId);
        result.userAuth = currUserAuth();
        return result;
    }

    public Promise<Result> post(long postId) {
        Logger.info("post " + postId);
        Promise<PostResult> promise = Promise.promise(() -> postJob(postId));
        return promise.map(val -> ok(postv.render(val.userAuth, val.post)));
    }

    private AppPosts latestPostsJob(long offset, long limit) {
        try {
            if (offset < 0 || limit <= 0)
                throw new AppException(AppResult.EINVAL);

            List<Post> posts = Posts.getLatest(offset, limit);
            AppPosts appPosts = new AppPosts(AppResult.success());
            appPosts.setPosts(posts);
            return appPosts;
        } catch (AppException e) {
            return new AppPosts(e.getResult());
        } catch (Exception e) {
            Logger.error("exception", e);
            return new AppPosts(AppResult.error(AppResult.EEXCEPT));
        } finally {

        }
    }

    public Promise<Result> latestPosts(long offset, long limit) {
        Promise<AppPosts> promise = Promise.promise(() -> latestPostsJob(offset, limit));
        return promise.map(appPosts -> ok(appPosts.toJson()));
    }
}
