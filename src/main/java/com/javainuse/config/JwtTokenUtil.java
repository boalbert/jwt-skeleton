package com.javainuse.config;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {

	@Serial
	private static final long serialVersionUID = -2550185165626007488L;

	@Value("${jwt.secret_key}")
	private String jwt_secret_key;

	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public Date getIssuedAtDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getIssuedAt);
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser()
				.setSigningKey(Base64.getEncoder().encode(jwt_secret_key.getBytes()))
				.parseClaimsJws(token)
				.getBody();

	}

	private boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	private boolean ignoreTokenExpiration(String token) {
		// here you specify tokens, for that the expiration is ignored
		return false;
	}

	public String generateToken(UserDetails userDetails) {

		return doGenerateToken(userDetails.getUsername());
	}

	private String doGenerateToken(String subject) {

		String tokenId= String.valueOf(new Random().nextInt(10000));

		return Jwts.builder()
				.setId(tokenId)
				.setSubject(subject)
				.setIssuer("ABC_Ltd")
				.setAudience("XYZ_Ltd")
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)))
				.signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encode(jwt_secret_key.getBytes()))
				.compact();
	}

	public boolean canTokenBeRefreshed(String token) {
		return (!isTokenExpired(token) || ignoreTokenExpiration(token));
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
}
