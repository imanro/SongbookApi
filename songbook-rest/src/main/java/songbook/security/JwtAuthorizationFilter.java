package songbook.security;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import songbook.domain.user.entity.User;
import songbook.domain.user.port.in.UserByEmailQuery;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static songbook.security.SecurityConstants.*;

// the class that deals with getting user from token (The meaning of authorization - assigning of user identity)
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final UserByEmailQuery userByEmailQuery;

    public JwtAuthorizationFilter(AuthenticationManager authManager,
                                  UserByEmailQuery userByEmailQuery
    ) {
        super(authManager);
        this.userByEmailQuery = userByEmailQuery;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(SecurityConstants.TOKEN_HEADER);
        if (!token.equals("") && token.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            try {
                byte[] signingKey = SecurityConstants.JWT_SECRET.getBytes();

                JwtParser parser = Jwts.parserBuilder()
                        .requireAudience(TOKEN_AUDIENCE)
                        .setSigningKey(signingKey)
                        .build();

                Jws<Claims> parsedToken = parser.parseClaimsJws(token.replace("Bearer ", ""));

                String username = parsedToken
                        .getBody()
                        .getSubject();

                List<SimpleGrantedAuthority> authorities = ((List<?>) parsedToken.getBody()
                        .get("rol")).stream()
                        .map(authority -> new SimpleGrantedAuthority((String) authority))
                        .collect(Collectors.toList());

                if (!username.equals("")) {

                    User domainUser = userByEmailQuery.getUserByEmail(username).orElseThrow(() -> new Exception("The user was not found"));
                    return new UsernamePasswordAuthenticationToken(domainUser, null, authorities);
                }
            } catch (ExpiredJwtException exception) {
                // log.warn("Request to parse expired JWT : {} failed : {}", token, exception.getMessage());
                System.out.println(String.format("Request to parse expired JWT : %s failed : %s", token, exception.getMessage()));
            } catch (UnsupportedJwtException exception) {
                // log.warn("Request to parse unsupported JWT : {} failed : {}", token, exception.getMessage());
                System.out.println(String.format("Request to parse unsupported JWT : %s failed : %s", token, exception.getMessage()));
            } catch (MalformedJwtException exception) {
                // log.warn("Request to parse invalid JWT : {} failed : {}", token, exception.getMessage());
                System.out.println(String.format("Request to parse invalid JWT : %s failed : %s", token, exception.getMessage()));
            } catch (IllegalArgumentException exception) {
                // log.warn("Request to parse empty or null JWT : {} failed : {}", token, exception.getMessage());
                System.out.println(String.format("Request to parse empty null JWT : %s failed : %s", token, exception.getMessage()));
            } catch (Exception exception) {
                System.out.println(String.format("Unknown error : %s failed : %s", token, exception.getMessage()));
            }
        }

        return null;
    }

}
