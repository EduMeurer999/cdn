package com.cdnspringboot.cdn.configs;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.cdnspringboot.cdn.repositories.UserRepository;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                var serveltPath = request.getServletPath();

                if (serveltPath.startsWith("/file")) {
                    // Pegar a autenticação
        
                    var auth = request.getHeader("Authorization");
                    System.out.println(auth);
        
                    var userPass = auth.substring("Basic".length()).trim();
        
                    byte[] authDecoded = Base64.getDecoder().decode(userPass);
        
                    var authString = new String(authDecoded);
        
                    System.out.println(userPass);
                    System.out.println(authString);
                    String[] credentials = authString.split(":");
                    String email = credentials[0];
                    String password = credentials[1];
        
                    System.out.println(email);
                    System.out.println(password);
                    // Validar usuário
        
                    var user = this.userRepository.findByEmail(email);
                    if (user == null) {
                        response.sendError(401, "Usuário sem autorização");
                    } else {
        
                        // Validar Senha
        
                        var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getSenha());
                        if (passwordVerify.verified) {
                            request.setAttribute("idUser", user.getId());
                            filterChain.doFilter(request, response);
                        } else {
                            response.sendError(401, "Usuário sem autorização");
                        }
                        // Segue viagem
                    }
                }else{
                    filterChain.doFilter(request, response);
                }
    }
    
}
