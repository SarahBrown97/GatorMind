package com.example.onecare.initsdk;

public interface AuthConstants {

	// TODO Change it to your web domain
	public final static String WEB_DOMAIN = "zoom.us";

	/**
	 * We recommend that, you can generate jwttoken on your own server instead of hardcore in the code.
	 * We hardcore it here, just to run the demo.
	 *
	 * You can generate a jwttoken on the https://jwt.io/
	 * with this payload:
	 * {
	 *
	 *     "appKey": "string", // app key
	 *     "iat": long, // access token issue timestamp
	 *     "exp": long, // access token expire time
	 *     "tokenExp": long // token expire time
	 * }
	 */
	//public final static String SDK_JWTTOKEN = JWT_TOKEN;
	public final static String APP_KEY = "wu0hl6U4wkvjis0kqG8j2a0K9v2HubaFEh2M";
	public final static String APP_SECRET = "P1jMaERpAT2W5z0I1vG4ozFt9E2BWDeCxpLZ";

}
