package dev.sprikers.moustacheshop.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class UserModel {
    private String id;
    private String names;
    private String paternalSurname;
    private String maternalSurname;
    private String dni;
    private String email;
    private Integer phoneNumber;
    private List<String> roles;
    private String token;
    private boolean active;

    public String getSurnames() {
        return paternalSurname + " " + maternalSurname;
    }
}
