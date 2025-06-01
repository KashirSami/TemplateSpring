package com.template.template.database;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class FirebaseRestLogin {

    /**
     * Intenta iniciar sesión contra Firebase REST.
     * @param email    Correo del usuario.
     * @param password Contraseña del usuario.
     * @return true si credenciales correctas.
     * @throws FirebaseRestLoginException si hubo un error de autenticación
     */
    public static boolean loginWithEmailPassword(String email, String password) throws FirebaseRestLoginException {
        try {
            String apiKey = "AIzaSyBiYqQFhr84FjuoXmdKNxe57AFuZNyV3Sk";
            URL url = new URL(
                    "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + apiKey
            );

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "proyectofinal-ca5b2.json");
            conn.setDoOutput(true);

            // Construir JSON de petición
            String body = String.format(
                    "{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}",
                    email, password
            );
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                // Login exitoso
                return true;
            }

            // Si no es 200, leer error para extraer el mensaje
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream())
            )) {

                StringBuilder responseError = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    responseError.append(line);
                }
                // Parsear JSON de error
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(responseError.toString());
                String message = root.path("error").path("message").asText();

                // Mapear el código de Firebase a nuestro mensaje amigable
                switch (message) {
                    case "EMAIL_NOT_FOUND":
                        throw new FirebaseRestLoginException("No existe ningún usuario con ese correo.");
                    case "INVALID_PASSWORD":
                        throw new FirebaseRestLoginException("La contraseña es incorrecta.");
                    case "USER_DISABLED":
                        throw new FirebaseRestLoginException("La cuenta de usuario ha sido deshabilitada.");
                    default:
                        // Otros posibles mensajes de error (TOO_MANY_ATTEMPTS_TRY_LATER, etc.)
                        throw new FirebaseRestLoginException("Error al iniciar sesión: " + message);
                }
            }

        } catch (FirebaseRestLoginException e) {
            // Propagamos nuestra excepción con mensaje claro
            throw e;
        } catch (Exception e) {
            // Cualquier otra excepción (p. ej. problemas de red o parseo)
            throw new FirebaseRestLoginException("Error inesperado al autenticar: " + e.getMessage());
        }
    }

    public static class FirebaseRestLoginException extends Exception {
        public FirebaseRestLoginException(String mensaje) {
            super(mensaje);
        }
    }
}