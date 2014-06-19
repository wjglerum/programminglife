package controllers;

import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

/**
 * Secures the authentication.
 */
public class Secured extends Security.Authenticator {

	@Override
	public final String getUsername(final Context ctx) {
		return ctx.session().get("username");
	}

	@Override
	public final Result onUnauthorized(final Context ctx) {
		return redirect(routes.Authentication.login());
	}

}
