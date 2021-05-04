package com.osa.openstreetart.dto;

import lombok.Data;

@Data
public class ChangePswDTO {
	private String oldPassword;
	private String newPassword;
}
