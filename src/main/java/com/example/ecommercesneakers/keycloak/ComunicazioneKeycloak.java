package com.example.ecommercesneakers.keycloak;

import com.example.ecommercesneakers.models.Utente;
import lombok.experimental.UtilityClass;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@UtilityClass
public class ComunicazioneKeycloak {
    private static String usernameAdmin = "admin";
    private static String passwordAdmin = "admin";
    private static String role = "utente";
    private static String serverUrl = "http://localhost:8080";
    private static String realm = "eCommerceSneakers";
    private static String clientId = "eCommerceSneakersClient";
    private static String clientSecret = "";

    public static void addUser(Utente utenteDaRegistrare, String password){
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(clientId)
                .username(usernameAdmin)
                .password(passwordAdmin)
                .build();

        UserRepresentation user = new UserRepresentation();

        user.setEnabled(true);
        user.setUsername(utenteDaRegistrare.getEmail());
        user.setEmail(utenteDaRegistrare.getEmail());
        user.setFirstName(utenteDaRegistrare.getNome());
        user.setLastName(utenteDaRegistrare.getCognome());
        user.setEmailVerified(true);

        user.setAttributes(Collections.singletonMap("origin", Arrays.asList("demo")));

        // Settiamo il realm
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();

        // Creiamo l'utente e ne estraiamo l'esito
        Response response = usersResource.create(user);
        //System.out.printf("Response: %s %s%n", response.getStatus(), response.getStatusInfo());
        //System.out.println(response.getLocation());
        String userId = CreatedResponseUtil.getCreatedId(response);
        //System.out.printf("User created with userId: %s%n", userId);

        // Impostiamo la password dell'utente creato
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);  //non vogliamo gestire il caso di password da aggiornare ogni tot mesi
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);

        UserResource userResource = usersResource.get(userId);

        userResource.resetPassword(passwordCred);

        // Assegniamo i ruoli all'utente che abbiamo creato
        ClientRepresentation app1Client = realmResource.clients().findByClientId(clientId).get(0);


        RoleRepresentation userClientRole = realmResource.clients().get(app1Client.getId()).roles().get(role).toRepresentation();


        userResource.roles().clientLevel(app1Client.getId()).add(Arrays.asList(userClientRole));


    }
}
