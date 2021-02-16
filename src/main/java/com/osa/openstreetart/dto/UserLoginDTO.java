package com.osa.openstreetart.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Informations nécessaires pour l'authentification")
public class UserLoginDTO {
	@ApiModelProperty(notes = "Nom de l'utilisateur du compte")
	private String username;

	@ApiModelProperty(notes = "Mot de passe de l'utilisateur")
	private String password;
}
