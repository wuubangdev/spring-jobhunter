package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.LoginDTO;
import vn.hoidanit.jobhunter.domain.ResTokenDTO;
import vn.hoidanit.jobhunter.domain.user.User;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;

    @Value("${hoidanit.jwt.access-token-validity-in-second}")
    private Long jwtKeyRefreshExpiration;

    public AuthController(
            AuthenticationManagerBuilder authenticationManagerBuilder,
            SecurityUtil securityUtil,
            UserService userService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResTokenDTO> postLogin(@Valid @RequestBody LoginDTO loginDTO) {
        // Nap DTO vao security
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(), loginDTO.getPassword());
        // Xac thuc nguoi dung => Can viet lai ham loadUserByUsername

        // (UserDetailService)
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // create a acess token
        String access_token = this.securityUtil.createAccessToken(authentication);

        ResTokenDTO resTokenDTO = new ResTokenDTO();
        resTokenDTO.setAccess_token(access_token);
        User currentUserDB = this.userService.getUserByEmail(loginDTO.getEmail());
        if (currentUserDB != null) {
            ResTokenDTO.UserLogin userLogin = new ResTokenDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName());
            resTokenDTO.setUser(userLogin);

        }
        // Create refresh token
        String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getEmail(), resTokenDTO);
        // Update regresh token to user
        this.userService.updateUserToken(loginDTO.getEmail(), refresh_token);
        // set cookie
        ResponseCookie cookies = ResponseCookie.from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(jwtKeyRefreshExpiration)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookies.toString())
                .body(resTokenDTO);
    }

}
