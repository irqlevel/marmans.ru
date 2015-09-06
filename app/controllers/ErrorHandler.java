package controllers;

import play.http.HttpErrorHandler;
import play.mvc.*;
import play.mvc.Http.*;
import play.libs.F.*;

import models.*;
import play.libs.F.*;
import play.mvc.*;
import play.Logger;
import views.html.*;


public class ErrorHandler implements HttpErrorHandler {
    public Promise<Result> onClientError(RequestHeader request, int statusCode, String message) {
        Logger.error("onClientError uri=" + request.uri() + " statusCode=" + statusCode + " message=" + message);
        Promise<UserAuth> promise = Promise.promise(() -> Application.currUserAuth());
        return promise.map(userAuth -> Results.status(statusCode, error.render(userAuth, Integer.toString(statusCode))));
    }

    public Promise<Result> onServerError(RequestHeader request, Throwable exception) {
        Logger.error("onServerError uri=" + request.uri(), exception);
        Promise<UserAuth> promise = Promise.promise(() -> Application.currUserAuth());
        return promise.map(userAuth -> Results.status(500, error.render(userAuth, "Server Error")));
    }
}
