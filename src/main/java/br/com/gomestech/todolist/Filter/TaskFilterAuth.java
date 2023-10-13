package br.com.gomestech.todolist.Filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.gomestech.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TaskFilterAuth extends OncePerRequestFilter {

  @Autowired
  private IUserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    var serveletPath = request.getServletPath();

    if (serveletPath.startsWith("/tasks/")) {
      var authorization = request.getHeader("Authorization");

      var authEncoded = authorization.substring("Basic".length()).trim();

      byte[] authDecode = Base64.getDecoder().decode(authEncoded);

      var authString = new String(authDecode);

      String[] authArray = authString.split(":");

      String username = authArray[0];
      String password = authArray[1];

      var user = this.userRepository.findByUsername(username);

      if (user == null) {
        response.sendError(401, "User not found");
        return;
      } else {
        var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

        if (!passwordVerify.verified) {
          response.sendError(401, "Password incorrect");
          return;
        }

        request.setAttribute("userId", user.getId());
        filterChain.doFilter(request, response);
      }
    } else {
      filterChain.doFilter(request, response);
    }

  }

}
