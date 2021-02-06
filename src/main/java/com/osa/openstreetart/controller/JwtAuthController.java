package com.osa.openstreetart.controller;


import com.osa.openstreetart.entity;
import com.osa.openstreetart.service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@CrossOrigin
public class JwtAuthController {

    @Autowired
	private UserRepository userRepo;

    @Autowired
	private JwtService jwtService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> postRegister(@RequestBody UserDTD user) throws Exception {
        if (userRepo.findByEmail(user.getEmail()) != null)
			return new ResponseEntity<>("User already existing.", HttpStatus.BAD_REQUEST);
		if (user.getPassword().length() < UserEntity.PSW_MIN_LENGTH)
			return new ResponseEntity<>("Password too short.", HttpStatus.BAD_REQUEST);
		return ResponseEntity.ok(jwtService.save(user));
    }
    
}
