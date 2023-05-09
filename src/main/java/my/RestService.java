/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/WebServices/GenericResource.java to edit this template
 */
package my;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.*;
import com.mysql.cj.jdbc.exceptions.CommunicationsException;

import io.jsonwebtoken.Jwts;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author student
 */

//https://www.viralpatel.net/java-create-validate-jwt-token/
@Path("RestService")
public class RestService
{
    @Context
    private UriInfo context;

	private Connection con;
	private Statement statement;

	/**
     * Creates a new instance of RestService
	 * @throws java.sql.SQLException
     */
    public RestService() throws SQLException
    {
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException e)
		{

		}

		//con = DriverManager.getConnection("jdbc:mysql://169.254.188.8:3306/strona","root","");
		con = DriverManager.getConnection("jdbc:mysql://localhost/strona","root","");
		statement = con.createStatement();
    }

	private boolean sendCommand(String sql_command) throws SQLException
	{
		try
		{
			statement.executeUpdate(sql_command);
			return true;
		}
		catch (CommunicationsException c)
		{
			return false;
		}
	}

	private ResultSet recieveData(String sql_command) throws SQLException
	{
		try
		{
			ResultSet rs = statement.executeQuery(sql_command);
			return rs;
		}
		catch (CommunicationsException c)
		{
			return null;
		}
	}

    private static PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        String rsaPrivateKey = "" +
                "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDK7c0HtOvefMRM" +
                "s1tkdiJm+A16Df85lQlmXjQvMHNgY4P/znvl4kRON9DdBdo3K81OG7pR/0H9XvdB" +
                "TEojj/6vCVuMDeeIiBrgx0OJjhv0r8oUD4d52+1kXXITaniyZcbJ08s4sF7fUSCu" +
                "IZOhvvwQTd/tIwXGU1qqfg+bsomQ6h2czPSKXAux54vUiRO2IWf/Y6twyk8cy1PH" +
                "IOfelCVUJ4kzmP+CsOH7Rh3JMwZ0Mc4GAzndWpKwNXKjVM20/bKE9FgIiIjzmEQd" +
                "VpSdUz2MbAKM1kskdaHXQyuaHoHfPwESYuEwBld4vh9AGMF3jYMu8ggnAzVRIoWG" +
                "Mr5eCE2tAgMBAAECggEBAKBPXiKRdahMzlJ9elyRyrmnihX7Cr41k7hwAS+qSetC" +
                "kpu6RjykFCvqgjCpF+tvyf/DfdybF0mPBStrlkIj1iH29YBd16QPSZR7NkprnoAd" +
                "gzl3zyGgcRhRjfXyrajZKEJ281s0Ua5/i56kXdlwY/aJXrYabcxwOvbnIXNxhqWY" +
                "NSejZn75fcacSyvaueRO6NqxmCTBG2IO4FDc/xGzsyFKIOVYS+B4o/ktUOlU3Kbf" +
                "vwtz7U5GAh9mpFF+Dkr77Kv3i2aQUonja6is7X3JlA93dPu4JDWK8jrhgdZqY9p9" +
                "Q8odbKYUaBV8Z8CnNgz2zaNQinshzwOeGfFlsd6H7SECgYEA7ScsDCL7omoXj4lV" +
                "Mt9RkWp6wQ8WDu5M+OCDrcM1/lfyta2wf7+9hv7iDb+FwQnWO3W7eFngYUTwSw5x" +
                "YP2uvOL5qbe7YntKI4Q9gHgUd4XdRJJSIdcoY9/d1pavkYwOGk7KsUrmSeoJJ2Jg" +
                "54ypVzZlVRkcHjuwiiXKvHwj2+UCgYEA2w5YvWSujExREmue0BOXtypOPgxuolZY" +
                "pS5LnuAr4rvrZakE8I4sdYjh0yLZ6qXJHzVlxW3DhTqhcrhTLhd54YDogy2IT2ff" +
                "0GzAV0kX+nz+mRhw0/u+Yw6h0QuzH9Q04Wg3T/u/K9+rG335j/RU1Tnh7nxetfGb" +
                "EwJ1oOqcXikCgYEAqBAWmxM/mL3urH36ru6r842uKJr0WuhuDAGvz7iDzxesnSvV" +
                "5PKQ8dY3hN6xfzflZoXssUGgTc55K/e0SbP93UZNAAWA+i29QKY6n4x5lKp9QFch" +
                "dXHw4baIk8Z97Xt/kw07f6FAyijdC9ggLHf2miOmdEQzNQm/9mcJ4cFn+DECgYEA" +
                "gvOepQntNr3gsUxY0jcEOWE3COzRroZD0+tLFZ0ZXx/L5ygVZeD4PwMnTNrGvvmA" +
                "tAFt54pomdqk7Tm3sBQkrmQrm0+67w0/xQ9eJE/z37CdWtQ7jt4twHXc0mVWHa70" +
                "NdPhTRVIAWhil7rFWANOO3Gw2KrMy6O1erW7sAjQlZECgYBmjXWzgasT7JcHrP72" +
                "fqrEx4cg/jQFNlqODNb515tfXSBBoAFiaxWJK3Uh/60/I6cFL/Qoner4trNDWSNo" +
                "YENBqXLZnWGfIo0vAIgniJ6OD67+1hEQtbenhSfeE8Hou2BnFOTajUxmYgGm3+hx" +
                "h8TPOvfHATdiwIm7Qu76gHhpzQ==";

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(rsaPrivateKey));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privKey = kf.generatePrivate(keySpec);
        return privKey;
    }

    private String generateJWT(String username, String role) throws InvalidKeySpecException, NoSuchAlgorithmException
    {
        PrivateKey privateKey = getPrivateKey();

        Instant now = Instant.now();
        return Jwts.builder()
                .claim("role", role) //cltr d
                .setSubject(username)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(5l, ChronoUnit.MINUTES)))
                .signWith(privateKey)
                .compact();
    }

    public static Jws<Claims> parseJWT(String jwtString) throws InvalidKeySpecException, NoSuchAlgorithmException
    {
        PublicKey publicKey = getPublicKey();

        Jws<Claims> jwt = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(jwtString);

        return jwt;
    }

    private static PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        String rsaPublicKey = "" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyu3NB7Tr3nzETLNbZHYi" +
                "ZvgNeg3/OZUJZl40LzBzYGOD/8575eJETjfQ3QXaNyvNThu6Uf9B/V73QUxKI4/+" +
                "rwlbjA3niIga4MdDiY4b9K/KFA+HedvtZF1yE2p4smXGydPLOLBe31EgriGTob78" +
                "EE3f7SMFxlNaqn4Pm7KJkOodnMz0ilwLseeL1IkTtiFn/2OrcMpPHMtTxyDn3pQl" +
                "VCeJM5j/grDh+0YdyTMGdDHOBgM53VqSsDVyo1TNtP2yhPRYCIiI85hEHVaUnVM9" +
                "jGwCjNZLJHWh10Mrmh6B3z8BEmLhMAZXeL4fQBjBd42DLvIIJwM1USKFhjK+XghN" +
                "rQIDAQAB";

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(rsaPublicKey));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(keySpec);
        return publicKey;
    }

    private ResultSet checkDatabaseForUsername(String username)
    {
		String sql_command = "select `username` from users where `username` = '" + username + "'";
        ResultSet users = null;
		try
		{
			users = recieveData(sql_command);
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}

        return users;
    }

    private String verifyJWT(Jws<Claims> parsedJwt) //returns role on success
    {
		String stringJwt = parsedJwt.toString();
		int idx = stringJwt.indexOf("body");
		String body = stringJwt.substring(idx+5).replace("sub=", "sub:");
		body = body.replace("role=", "role:");
		body = body.replace("jti=", "jti:");
		body = body.replace("iat=", "iat:");
		body = body.replace("exp=", "exp:");
		body = body.replace("signature=", "signature:");
		body = body.replace("},", ",");
		body = body + "}";
        JSONObject jwtAsJson = new JSONObject(body);
        String username = (String) jwtAsJson.get("sub");
		ResultSet result = checkDatabaseForUsername(username);

		if (result == null)
		{
			return body;
		}
		else
		{
			return (String) jwtAsJson.get("role");
		}

    }


    /**
     * PUT method for updating or creating an instance of RestServiceResource
     * @param content representation for the resource
     * @return
     */

    @POST
    @Path("auth/signup")
    @Produces(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
    @Consumes(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
    public String signUp(String content)
    {
		JSONObject jsonResponse;

		try
		{
			JSONObject inputJson = new JSONObject(content);
			String username = (String) inputJson.get("username");
			String password = (String) inputJson.get("password");
			String email = (String) inputJson.get("email");

			String sqlCommand = "INSERT INTO `users`(`username`, `password`, `email`) VALUES ('" + username + "','" + password + "','" + email + "')";
			if (sendCommand(sqlCommand))
			{
				jsonResponse = new JSONObject("{\"Result\":\"Sign up Success\"}");
			}
			else
			{
				jsonResponse = new JSONObject("{\"Result\":\"Sign up Fail\"}");
			}

		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
			String resJson = "{\"Result\":\"" + e.getMessage() + "\"}";
			jsonResponse = new JSONObject(resJson);
		}

		return jsonResponse.toString();
	}


	@POST
    @Path("auth/signin")
    @Produces(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
    @Consumes(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
    public String signIn(String content)
    {

		JSONObject jsonResponse;
		ResultSet response;
		JSONObject inputJson = new JSONObject(content);

		try
		{

			String username = (String) inputJson.get("username");
			String password = (String) inputJson.get("password");

			String sqlCommand = "select `role` from `users` where `username` = '" + username + "' and `password` = '" + password + "'";
			response = recieveData(sqlCommand);
			if (response.next())
			{
				String role = response.getString(1);

				jsonResponse = new JSONObject("{\"Result\":\"Sign in Success\", \"Role\": \"" + role + "\"}");
				jsonResponse.put("JWT", generateJWT(username, role));
			}
			else
			{
				jsonResponse = new JSONObject("{\"Result\":\"Sign in Fail\"}");
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			String resJson = "{\"Result\":\"" + e.getMessage() + "\"}";
			jsonResponse = new JSONObject(resJson);
		}


		return jsonResponse.toString();
	}


	@GET
    @Path("resource/public")
    @Produces(MediaType.APPLICATION_JSON)
    public String publicResource(String content)
    {
		String rs = "{\"Result\":\"Public resource\"}";

       return new JSONObject(rs).toString();
	}


	@POST
    @Path("resource/public")
    @Produces(MediaType.APPLICATION_JSON)
	@Consumes(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
    public String publicResourceJson(String content)
    {
		String rs = "{\"Result\":\"Public resource\"}";

        return new JSONObject(rs).toString();
	}


	@POST
    @Path("resource/user")
    @Produces(MediaType.APPLICATION_JSON)
	@Consumes(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
    public String userResource(String content)
    {
		String rs = "";
		try
		{
			JSONObject inputJson = new JSONObject(content);
			String jwt = (String) inputJson.get("JWT");
			Jws<Claims> parsedJwt =  parseJWT(jwt);

			String role = verifyJWT(parsedJwt);

			if (role != null)
			{
				if (role.equals("pending"))
				{
					rs = "{\"Result\":\"Please have an admin activate your account\"}";
				}
				else
				{
					rs = "{\"Result\":\"User content\"}";
				}
			}
			else
			{
				rs = "{\"Result\":\"Incorrect user JWT\"}";
			}
		}
		catch (ExpiredJwtException p)
		{
			rs = "{\"Result\":\"JWT expired\"}";
		}
		catch (Exception e)
		{

			//rs = "{\"Result\":\"" + e.getMessage()+"\",\"error\":\"" + e.toString()+"\"}";
			rs = "{\"Result\":\"JWT error\"}";
		}

        return rs;
	}


	@POST
    @Path("resource/admin")
    @Produces(MediaType.APPLICATION_JSON)
	@Consumes(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
    public String adminResource(String content)
    {
		String rs;
		try
		{
			JSONObject inputJson = new JSONObject(content);
			String jwt = (String) inputJson.get("JWT");
			Jws<Claims> parsedJwt =  parseJWT(jwt);
			String role = verifyJWT(parsedJwt);

			if (role != null)
			{
				if (role.equals("admin"))
				{
					rs = "{\"Result\":\"Admin resource\"}";
				}
				else if (role.equals("pending"))
				{
					rs = "{\"Result\":\"Please have an admin activate your account\"}";
				}
				else
				{
					rs = "{\"Result\":\"insufficient privileges\"}";
				}

			}
			else
			{
				rs = "{\"Result\":\"Incorrect admin JWT\"}";
			}
		}
		catch (ExpiredJwtException p)
		{
			rs = "{\"Result\":\"JWT expired\"}";
		}
		catch (Exception e)
		{
			//rs = "{\"Result\":\"" + e.getMessage()+"\"}";
			rs = "{\"Result\":\"JWT error\"}";
		}

        return new JSONObject(rs).toString();
	}

}
