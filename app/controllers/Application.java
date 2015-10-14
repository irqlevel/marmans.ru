package controllers;

import api.*;
import com.fasterxml.jackson.databind.JsonNode;
import lib.BCrypt;

import lib.DateFormat;
import models.*;

import models.dba.*;
import org.apache.ibatis.exceptions.PersistenceException;
import org.imgscalr.Scalr;
import play.libs.F.*;
import play.mvc.*;
import play.Logger;
import scala.App;
import views.html.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        for (User user : result.users) {
            userResolveAvatar(user);
        }
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
                throw new AppException(AppResult.EINVAL, "invalid email or password");

            Logger.info("email=" + join.email);

            User user = Users.join(join.email, join.password);
            if (user == null)
                throw new AppException(AppResult.EDB_UPDATE);

            signin(user.uid);
            AppResult result = AppResult.success();
            result.uid = user.uid;
            return result;
        } catch (PersistenceException e) {
            AppResult r = new AppResult(AppResult.EXISTS);
            r.setResultDesc("user already exists");
            return r;
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

    private void signin(long uid) throws AppException {
        UserSession session = UserSession.genSession(uid, 24 * 3600 * 1000);
        if (!UserSessions.insert(session))
            throw new AppException(AppResult.EDB_UPDATE);
        session().put("usersession", session.getValue());
    }

    private AppResult postSigninJob(AppUserSignin signin) {
        try {
            Logger.info("post signin job");
            if (signin.email == null || signin.password == null)
                throw new AppException(AppResult.EINVAL, "invalid email or password");

            Logger.info("email=" + signin.email);

            User user = Users.getByEmail(signin.email);
            if (user == null)
                throw new AppException(AppResult.ENOTFOUND, "user not found");

            if (!BCrypt.checkpw(signin.password, user.hashp))
                throw new AppException(AppResult.EAUTH, "wrong password");

            signout(user.uid);
            signin(user.uid);

            Logger.info("user signed in uid=" + user.uid);
            AppResult result = AppResult.success();
            result.uid = user.uid;
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

    private void signout(long uid) {
        String value = session().get("usersession");
        session().remove("usersession");
        if (value != null)
            UserSessions.delete(value);
        UserSessions.deleteByUid(uid);
    }

    private AppResult signoutJob() {
        UserAuth userAuth = postUserAuth();
        if (userAuth == null)
            return AppResult.error(AppResult.EPERM);
        signout(userAuth.user.uid);
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
            Logger.info("auth user uid=" + user.uid + " email=" + user.email);
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

    private User userResolveAvatar(User user) {
        Image avatar = Images.get(user.avatarId);
        if (avatar != null)
            user.avatarUrl = avatar.url;

        Image thumbnail = Images.get(user.thumbnailId);
        if (thumbnail != null)
            user.thumbnailUrl = thumbnail.url;
        return user;
    }

    public Promise<Result> profile(long uid) {
        Logger.info("profile uid=" + uid);
        Promise<ProfileResult> promise = Promise.promise(() -> profileJob(uid));
        return promise.map(result -> (result.user != null) ?
                ok(profilev.render(result.userAuth, userResolveAvatar(result.user))) : notFound());
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
            if (!Users.updateName(userAuth.user.uid, profile.name))
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
                ok(profilev.render(userAuth, userResolveAvatar(userAuth.user))));
    }

    public Promise<Result> postCreate() {
        Logger.info("post create");

        Promise<UserAuth> promise = Promise.promise(() -> currUserAuth());
        return promise.map(userAuth -> (userAuth.user == null) ? redirect(controllers.routes.Application.signin()) :
                ok(postcreatev.render(userAuth)));
    }

    private static String extractYTId(String ytUrl) {
        String vId = null;
        Pattern pattern = Pattern.compile(
                "^(http|https):\\/\\/www\\.youtube\\.com\\/(watch\\?v=|embed\\/)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()) {
            vId = matcher.group(3);
            if (vId.length() != 11)
                vId = null;
        }
        return vId;
    }

    private AppResult postCreateJob(AppPostCreate postCreate) {
        try {
            Logger.info("post create job");
            Logger.info("title=" + postCreate.title + " content=" + postCreate.content);

            if (postCreate.title == null || postCreate.content == null || postCreate.youtubeVideoLink == null)
                throw new AppException(AppResult.EINVAL);

            if (postCreate.title.length() == 0)
                throw new AppException(AppResult.EINVAL);

            Logger.info("post create title=" + postCreate.title + " youtubeVideoLink=" + postCreate.youtubeVideoLink);
            UserAuth userAuth = postUserAuth();
            if (userAuth == null)
                throw new AppException(AppResult.EAUTH);

            String youtubeLinkId = null;
            if (postCreate.youtubeVideoLink.length() > 0) {
                youtubeLinkId = extractYTId(postCreate.youtubeVideoLink);
                Logger.info("youtubeLinkId=" + youtubeLinkId);
                if (youtubeLinkId == null)
                    throw new AppException(AppResult.EBADLINK);
            }
            postCreate.title = Character.toUpperCase(postCreate.title.charAt(0)) + postCreate.title.substring(1);
            Post post = Posts.create(userAuth.user.uid, postCreate.title, postCreate.content, youtubeLinkId);
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

    private User uidToUser(long uid) {
        User user = Users.get(uid);
        if (user == null)
            return null;

        Image avatar = Images.get(user.avatarId);
        if (avatar != null)
            user.avatarUrl = avatar.url;

        Image thumbnail = Images.get(user.thumbnailId);
        if (thumbnail != null)
            user.thumbnailUrl = thumbnail.url;
        return user;
    }

    private PostsResult postsJob() {
        PostsResult result = new PostsResult();
        List<Post> posts = Posts.getAll();
        for (Post post: posts) {
            User user = uidToUser(post.uid);
            post.userName = user.name;
            post.userPicUrl = user.thumbnailUrl;
            post.date = DateFormat.timeToString(post.creationTime);
            post.imageUrl = getImageUrl(post.imageId);
            post.nrComments = Comments.countComments(post.postId);
        }
        result.posts = posts;
        result.userAuth = currUserAuth();
        return result;
    }

    public Promise<Result> posts() {
        Logger.info("posts");
        Promise<PostsResult> promise = Promise.promise(() -> postsJob());
        return promise.map(val -> ok(postsv.render(val.userAuth, val.posts)));
    }

    private String getImageUrl(long imageId) {
        try {
            if (imageId == -1)
                return null;
            Image image = Images.get(imageId);
            return image.url;
        } catch (Throwable t) {
            Logger.error("throwable", t);
        }
        return null;
    }

    private PostResult postJob(long postId) {
        PostResult result = new PostResult();
        result.post = Posts.get(postId);
        if (result.post != null) {
            User user = uidToUser(result.post.uid);
            result.post.userName = user.name;
            result.post.userPicUrl = user.thumbnailUrl;
            result.post.date = DateFormat.timeToString(result.post.creationTime);
            result.post.imageUrl = getImageUrl(result.post.imageId);
            result.post.nrComments = Comments.countComments(result.post.postId);
        }
        result.userAuth = currUserAuth();
        return result;
    }

    public Promise<Result> post(long postId) {
        Logger.info("post " + postId);
        Promise<PostResult> promise = Promise.promise(() -> postJob(postId));
        return promise.map(val -> (val.post != null) ? ok(postv.render(val.userAuth, val.post)) : notFound());
    }

    private AppPosts latestPostsJob(long offset, long limit) {
        try {
            if (offset < 0 || limit <= 0)
                throw new AppException(AppResult.EINVAL);

            List<Post> posts = Posts.getLatest(offset, limit);
            for (Post post: posts) {
                User user = uidToUser(post.uid);
                post.userName = user.name;
                post.date = DateFormat.timeToString(post.creationTime);
                post.userPicUrl = user.thumbnailUrl;
                post.imageUrl = getImageUrl(post.imageId);
                post.nrComments = Comments.countComments(post.postId);
            }
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

    private AppResult postCommentJob(long postId, AppCommentCreate createComment) {
        try {
            if (postId < 0 || createComment.postId != postId)
                throw new AppException(AppResult.EINVAL);

            UserAuth userAuth = postUserAuth();
            if (userAuth == null)
                throw new AppException(AppResult.EAUTH);

            Comment comment = Comments.create(userAuth.user.uid, postId, -1, createComment.content);
            if (comment == null)
                throw new AppException(AppResult.EDB_UPDATE);
            AppResult result = AppResult.success();
            result.id = comment.commentId;
            return result;
        } catch (AppException e) {
            return e.getResult();
        } catch (Exception e) {
            Logger.error("exception", e);
            return AppResult.error(AppResult.EEXCEPT);
        } finally {

        }
    }

    public Promise<Result> postComment(long postId) {
        Logger.info("post comment for post " + postId);

        JsonNode json = request().body().asJson();
        if (json == null) {
            return appResultToPromise(AppResult.error(AppResult.EINVAL));
        }
        AppCommentCreate comment = AppCommentCreate.parseJson(json);
        Promise<AppResult> promise = Promise.promise(() -> postCommentJob(postId, comment));
        return promise.map(val -> ok(val.toJson()));
    }

    private AppResult postCommentReplyJob(long commentId, AppCommentCreate createComment) {
        try {
            if (commentId < 0)
                throw new AppException(AppResult.EINVAL);

            UserAuth userAuth = postUserAuth();
            if (userAuth == null)
                throw new AppException(AppResult.EAUTH);

            Comment parent = Comments.get(commentId);
            if (parent == null)
                throw new AppException(AppResult.ENOTFOUND);

            Comment comment = Comments.create(userAuth.user.uid, parent.postId, parent.commentId,
                                              createComment.content);
            if (comment == null)
                throw new AppException(AppResult.EDB_UPDATE);
            AppResult result = AppResult.success();
            result.id = comment.commentId;
            return result;
        } catch (AppException e) {
            return e.getResult();
        } catch (Exception e) {
            Logger.error("exception", e);
            return AppResult.error(AppResult.EEXCEPT);
        } finally {

        }
    }

    public Promise<Result> postCommentReply(long commentId) {
        Logger.info("post comment reply for commentId " + commentId);

        JsonNode json = request().body().asJson();
        if (json == null) {
            return appResultToPromise(AppResult.error(AppResult.EINVAL));
        }
        AppCommentCreate comment = AppCommentCreate.parseJson(json);
        Promise<AppResult> promise = Promise.promise(() -> postCommentReplyJob(commentId, comment));
        return promise.map(val -> ok(val.toJson()));
    }

    private AppComments commentsJob(long postId) {
        try {
            if (postId < 0)
                throw new AppException(AppResult.EINVAL);
            List<Comment> comments = Comments.getCommentsByPostId(postId);
            for (Comment comment: comments) {
                User user = uidToUser(comment.uid);
                comment.userName = user.name;
                comment.userPicUrl = user.thumbnailUrl;
                comment.date = DateFormat.timeToString(comment.creationTime);
            }
            AppComments appComments = new AppComments(AppResult.success());
            appComments.setComments(comments);
            return appComments;
        } catch (AppException e) {
            return new AppComments(e.getResult());
        } catch (Exception e) {
            Logger.error("exception", e);
            return new AppComments(AppResult.error(AppResult.EEXCEPT));
        } finally {

        }
    }

    public Promise<Result> comments(long postId) {
        Promise<AppComments> promise = Promise.promise(() -> commentsJob(postId));
        return promise.map(appComments -> ok(appComments.toJson()));
    }

    public Result robots() {
        Logger.info("robots");
        return ok(robots.render()).as("text/plain");
    }

    private AppResult postAvatarJob() {
        File file = null;
        Image avatar = null, thumbnail = null;
        try {
            Logger.info("post avatar job");
            UserAuth userAuth = postUserAuth();
            if (userAuth == null)
                throw new AppException(AppResult.EAUTH);
            User user = userAuth.user;

            file = request().body().asRaw().asFile();
            String fileName = request().getHeader("X-File-Name");
            String fileType = request().getHeader("X-File-Type");
            Logger.info("file is " + file.getAbsolutePath() + " length=" + file.length());

            BufferedImage srcImg = ImageIO.read(file);
            BufferedImage avatarImg = Scalr.resize(srcImg, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, 200, Scalr.OP_ANTIALIAS);
            BufferedImage thumbnailImg = Scalr.resize(srcImg, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, 64, Scalr.OP_ANTIALIAS);

            ImageIO.write(avatarImg, "jpg", file);
            avatar = Images.create(user.uid, "avatar", "avatar", file, fileName, fileType);
            Logger.info("avatar created url=" + avatar.url + " id=" + avatar.imageId);
            Users.updateAvatar(user.uid, avatar.imageId);

            ImageIO.write(thumbnailImg, "jpg", file);
            thumbnail = Images.create(user.uid, "thumbnail", "thumbnail", file, fileName, fileType);
            Logger.info("thumbnail created url=" + thumbnail.url + " id=" + thumbnail.imageId);
            Users.updateThumbnail(user.uid, thumbnail.imageId);

            return AppResult.success();
        } catch (AppException e) {
            return e.getResult();
        } catch (Throwable e) {
            Logger.error("exception", e);
            return AppResult.error(AppResult.EEXCEPT);
        } finally {
            if (file != null)
                file.delete();
        }
    }

    public Promise<Result> postAvatar() {
        Logger.info("post avatar");
        Promise<AppResult> promise = Promise.promise(() -> postAvatarJob());
        return promise.map(val -> ok(val.toJson()));
    }

    private boolean isImageFileTypeSupported(String fileType) {
        if (fileType.equals("image/png") || fileType.equals("image/gif") || fileType.equals("image/jpeg"))
            return true;
        return false;
    }

    private String fileTypeToImgFormat(String fileType) {
        if (fileType.equals("image/png"))
            return "png";
        if (fileType.equals("image/gif"))
            return "gif";
        if (fileType.equals("image/jpeg"))
            return "jpg";
        return null;
    }

    private AppResult postUploadImageJob(long postId) {
        File file = null;
        Image image = null;
        final int maxImgWidth = 1024;
        try {
            Logger.info("post " + postId + " upload image job");
            UserAuth userAuth = postUserAuth();
            if (userAuth == null)
                throw new AppException(AppResult.EAUTH);
            User user = userAuth.user;
            Post post = Posts.get(postId);
            if (post == null)
                throw new AppException(AppResult.ENOTFOUND);
            if (post.uid != user.uid)
                throw new AppException(AppResult.ENOTFOUND);

            file = request().body().asRaw().asFile();
            String fileName = request().getHeader("X-File-Name");
            String fileType = request().getHeader("X-File-Type");
            if (fileName == null || fileType == null)
                throw new AppException(AppResult.EINVAL);
            Logger.info("file is " + file.getAbsolutePath() + " length=" + file.length() + " fileType=" + fileType);
            if (!isImageFileTypeSupported(fileType))
                throw new AppException(AppResult.EUNSUPFMT);

            BufferedImage srcImg = ImageIO.read(file);
            if (!fileType.equals("image/gif")) {
                if (srcImg.getWidth() > maxImgWidth) {
                    srcImg = Scalr.resize(srcImg, Scalr.Method.ULTRA_QUALITY,
                            Scalr.Mode.FIT_TO_WIDTH, maxImgWidth, Scalr.OP_ANTIALIAS);
                }
                ImageIO.write(srcImg, fileTypeToImgFormat(fileType), file);
            } else {
                if (srcImg.getWidth() > maxImgWidth)
                    throw new AppException(AppResult.EFILETOBIG);
            }

            image = Images.create(user.uid, "post image", "post image", file, fileName, fileType);
            Logger.info("image created url=" + image.url + " id=" + image.imageId);

            if (!Posts.setImage(post.postId, image.imageId))
                throw new AppException(AppResult.EDB_UPDATE);

            return AppResult.success();
        } catch (AppException e) {
            return e.getResult();
        } catch (Throwable e) {
            Logger.error("exception", e);
            return AppResult.error(AppResult.EEXCEPT);
        } finally {
            if (file != null)
                file.delete();
        }
    }

    public Promise<Result> postUploadImage(long postId) {
        Logger.info("post " + postId + " upload image");
        Promise<AppResult> promise = Promise.promise(() -> postUploadImageJob(postId));
        return promise.map(val -> ok(val.toJson()));
    }

    private AppResult postDeleteJob(long postId) {
        try {
            Logger.info("post " + postId + " delete job");
            UserAuth userAuth = postUserAuth();
            if (userAuth == null)
                throw new AppException(AppResult.EAUTH);
            User user = userAuth.user;
            Post post = Posts.get(postId);
            if (post == null)
                throw new AppException(AppResult.ENOTFOUND);
            if (post.uid != user.uid)
                throw new AppException(AppResult.ENOTFOUND);

            Posts.delete(postId);
            try {
                Images.delete(post.imageId);
            } catch (Throwable t) {
                Logger.error("exception", t);
            }
            return AppResult.success();
        } catch (AppException e) {
            return e.getResult();
        } catch (Throwable t) {
            Logger.error("exception", t);
            return AppResult.error(AppResult.EEXCEPT);
        } finally {
        }
    }

    public Promise<Result> postDelete(long postId) {
        Logger.info("post " + postId + " delete");
        Promise<AppResult> promise = Promise.promise(() -> postDeleteJob(postId));
        return promise.map(val -> ok(val.toJson()));
    }

    private AppResult postActivateJob(long postId) {
        try {
            Logger.info("post " + postId + " activate job");
            UserAuth userAuth = postUserAuth();
            if (userAuth == null)
                throw new AppException(AppResult.EAUTH);
            User user = userAuth.user;
            Post post = Posts.get(postId);
            if (post == null)
                throw new AppException(AppResult.ENOTFOUND);
            if (post.uid != user.uid)
                throw new AppException(AppResult.ENOTFOUND);

            Posts.setActive(postId, 1);
            return AppResult.success();
        } catch (AppException e) {
            return e.getResult();
        } catch (Throwable t) {
            Logger.error("exception", t);
            return AppResult.error(AppResult.EEXCEPT);
        } finally {
        }
    }

    public Promise<Result> postActivate(long postId) {
        Logger.info("post " + postId + " activate");
        Promise<AppResult> promise = Promise.promise(() -> postActivateJob(postId));
        return promise.map(val -> ok(val.toJson()));
    }
}
