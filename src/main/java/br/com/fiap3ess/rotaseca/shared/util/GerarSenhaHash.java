package br.com.fiap3ess.rotaseca.shared.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** Utilitário da aula: gera o hash BCrypt usado nas migrações de usuários. */
public class GerarSenhaHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String senhaHash = encoder.encode(args.length > 0 ? args[0] : "admin123");
        System.out.println(senhaHash);
    }
}
