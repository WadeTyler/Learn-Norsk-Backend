package net.tylerwade.learnnorsk.controller;

import jakarta.servlet.http.HttpServletResponse;
import net.tylerwade.learnnorsk.lib.util.AuthUtil;
import net.tylerwade.learnnorsk.model.auth.SignupRequest;
import net.tylerwade.learnnorsk.model.auth.User;
import net.tylerwade.learnnorsk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest, HttpServletResponse response) {
        try {

            String firstName = signupRequest.getFirstName();
            String lastName = signupRequest.getLastName();
            String email = signupRequest.getEmail();
            String password = signupRequest.getPassword();

            // Check if any of the fields are empty
            if (firstName == null || lastName == null || email == null || password == null) {
                return new ResponseEntity<>("All fields are required", HttpStatus.BAD_REQUEST);
            }

            // Check valid email
            if (!signupRequest.getEmail().contains("@") || !signupRequest.getEmail().contains(".")) {
                return new ResponseEntity<>("Invalid email", HttpStatus.BAD_REQUEST);
            }

            // Check passwords match
            if (!signupRequest.getPassword().equals(signupRequest.getConfirmPassword())) {
                return new ResponseEntity<>("Passwords do not match", HttpStatus.BAD_REQUEST);
            }

            // Check password length
            if (signupRequest.getPassword().length() < 6) {
                return new ResponseEntity<>("Password must be at least 6 characters", HttpStatus.BAD_REQUEST);
            }

            // Check if user already exists with email
            if (!userRepo.findByEmail(signupRequest.getEmail()).isEmpty()) {
                return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
            }

            String hashedPassword = authUtil.encodePassword(password);

            // Create user
            User user = new User(firstName, lastName, email, hashedPassword);
            userRepo.save(user);

            // Add auth token cookie
            response.addCookie(authUtil.createAuthTokenCookie(user.getId()));

            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Exception in signup(): " + e.getMessage());
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
