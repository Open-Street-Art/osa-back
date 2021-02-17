package com.osa.openstreetart.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "Informations nécessaires d'un nouvel utilisateur.")
@Data
public class UserRegisterDTO {
	@ApiModelProperty("L'adresse mail de l'utilisateur.")
	private String email;

	@ApiModelProperty("Le nom d'utilisateur du compte.")
	private String username;

	@ApiModelProperty("Le mot de passe de l'utilisateur.")
	private String password;

	@ApiModelProperty("Le rôle initial de l'utilisateur.")
	private String role;
}
