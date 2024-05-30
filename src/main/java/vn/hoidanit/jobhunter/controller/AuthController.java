package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.LoginDTO;
import vn.hoidanit.jobhunter.domain.ResUserTokenLogin;
import vn.hoidanit.jobhunter.domain.user.User;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final SecurityUtil securityUtil;
        private final UserService userService;

        @Value("${hoidanit.jwt.refresh-token-validity-in-second}")
        private long jwtKeyRefreshExpiration;

        public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder,
                        SecurityUtil securityUtil,
                        UserService userService) {
                this.authenticationManagerBuilder = authenticationManagerBuilder;
                this.securityUtil = securityUtil;
                this.userService = userService;
        }

        @PostMapping("/auth/login")
        @ApiMessage("Login success")
        public ResponseEntity<ResUserTokenLogin> postLogin(@Valid @RequestBody LoginDTO loginDTO) {

                // Nạp input gồm username/password vào Security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                loginDTO.getEmail(), loginDTO.getPassword());

                // xác thực người dùng => cần viết hàm loadUserByUsername
                Authentication authentication = authenticationManagerBuilder.getObject()
                                .authenticate(authenticationToken);

                // nạp thông tin (nếu xử lý thành công) vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);

                ResUserTokenLogin resUserToken = new ResUserTokenLogin();

                User currentUserDB = this.userService.handleGetUserByEmail(loginDTO.getEmail());
                if (currentUserDB != null) {
                        ResUserTokenLogin.UserLogin userLogin = new ResUserTokenLogin.UserLogin(
                                        currentUserDB.getId(),
                                        currentUserDB.getEmail(),
                                        currentUserDB.getName());
                        resUserToken.setUserLogin(userLogin);

                }
                // Tao access token
                String access_token = this.securityUtil.createAccessToken(authentication.getName(), resUserToken);

                resUserToken.setAccess_token(access_token);
                // Create refresh token
                String refresh_token = this.securityUtil.createRefreshToken(authentication.getName(), resUserToken);
                this.userService.updateUserToken(refresh_token, loginDTO.getEmail());
                // Create ResponseCookie refresh token
                ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refresh_token)
                                .httpOnly(true)
                                .secure(true)
                                .maxAge(jwtKeyRefreshExpiration)
                                .path("/")
                                .build();

                return ResponseEntity
                                .ok()
                                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                                .body(resUserToken);
        }

        @GetMapping("/auth/account")
        @ApiMessage("fetch account")
        public ResponseEntity<ResUserTokenLogin.UserGetAccount> getAccount() {
                String email = SecurityUtil.getCurrentUserLogin().isPresent()
                                ? SecurityUtil.getCurrentUserLogin().get()
                                : "";
                User currentUserDB = this.userService.handleGetUserByEmail(email);
                ResUserTokenLogin.UserLogin userLogin = new ResUserTokenLogin.UserLogin();
                ResUserTokenLogin.UserGetAccount userGetAccount = new ResUserTokenLogin.UserGetAccount();
                if (currentUserDB != null) {
                        userLogin.setId(currentUserDB.getId());
                        userLogin.setEmail(currentUserDB.getEmail());
                        userLogin.setName(currentUserDB.getName());
                        userGetAccount.setUser(userLogin);
                }
                return ResponseEntity.ok().body(userGetAccount);
        }

        @GetMapping("/auth/refresh")
        @ApiMessage("fetch account")
        public ResponseEntity<ResUserTokenLogin> getNewToken(
                        @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token)
                        throws IdInvalidException {
                if (refresh_token.equals("abc")) {
                        throw new IdInvalidException("Ban khong co refresh token o cookie");
                }
                // check valid
                Jwt decodedToken = this.securityUtil.chekValidRefreshToken(refresh_token);
                String email = decodedToken.getSubject();
                // check user by token + email
                User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
                if (currentUser == null) {
                        throw new IdInvalidException("Refresh Token khong hop le!");
                }

                // issue new token/set refresh token as cookie
                ResUserTokenLogin resUserToken = new ResUserTokenLogin();

                User currentUserDB = this.userService.handleGetUserByEmail(email);
                if (currentUserDB != null) {
                        ResUserTokenLogin.UserLogin userLogin = new ResUserTokenLogin.UserLogin(
                                        currentUserDB.getId(),
                                        currentUserDB.getEmail(),
                                        currentUserDB.getName());
                        resUserToken.setUserLogin(userLogin);

                }
                // Tao access token
                String access_token = this.securityUtil.createAccessToken(email, resUserToken);

                resUserToken.setAccess_token(access_token);
                // Create refresh token
                String new_refresh_token = this.securityUtil.createRefreshToken(email, resUserToken);
                this.userService.updateUserToken(new_refresh_token, email);
                // Create ResponseCookie refresh token
                ResponseCookie responseCookie = ResponseCookie
                                .from("refresh_token", new_refresh_token)
                                .httpOnly(true)
                                .secure(true)
                                .maxAge(jwtKeyRefreshExpiration)
                                .path("/")
                                .build();

                return ResponseEntity
                                .ok()
                                .header(HttpHeaders.SET_COOKIE, responseCookie.toString())
                                .body(resUserToken);
        }

        @PostMapping("/auth/logout")
        @ApiMessage("Logout success")
        public ResponseEntity<Void> logout()
                        throws IdInvalidException {
                String email = SecurityUtil.getCurrentUserLogin().isPresent()
                                ? SecurityUtil.getCurrentUserLogin().get()
                                : "";
                if (email.equals("")) {
                        throw new IdInvalidException("Access token khong hop le");
                }
                // update refresh token = null
                this.userService.updateUserToken(null, email);
                // remove refresh token cookie
                ResponseCookie removeCookie = ResponseCookie
                                .from("refresh_token", null)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(0)
                                .build();
                return ResponseEntity
                                .ok()
                                .header(HttpHeaders.SET_COOKIE, removeCookie.toString())
                                .body(null);
        }

}
