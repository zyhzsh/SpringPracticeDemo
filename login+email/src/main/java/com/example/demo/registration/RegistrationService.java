package com.example.demo.registration;

import com.example.demo.appuser.AppUser;
import com.example.demo.appuser.AppUserRole;
import com.example.demo.appuser.AppUserService;
import com.example.demo.email.EmailSender;
import com.example.demo.registration.token.ConfirmationToken;
import com.example.demo.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final EmailValidator emailValidator;
    private final AppUserService appUserService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSender emailSender;

    public String register(RegistrationRequest request) {
        boolean is_validate_email=emailValidator
                .test(request.getEmail());
        if(!is_validate_email){throw new IllegalStateException("email not valid");}

        String token= appUserService.signUpUser(
                new AppUser(
                        request.getFirstName(),
                        request.getLastName(),
                        request.getEmail(),
                        request.getPassword(),
                        AppUserRole.USER
                )
        );
        String link="http://localhost:8080/api/v1/registration/confirm?token="+token;
        emailSender.send(request.getEmail(),
                buildEmail(request.getFirstName()+request.getLastName(),link));


        return token;
    }
    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(
                confirmationToken.getAppUser().getEmail());
        return "confirmed";
    }
    private String buildEmail(String name, String link) {
        return "\n" +
                "<p>&nbsp;</p>\n" +
                "<table border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\"><!-- start logo -->\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td align=\"center\" bgcolor=\"#e9ecef\">&nbsp;</td>\n" +
                "</tr>\n" +
                "<!-- end logo -->\n" +
                "<tr>\n" +
                "<td align=\"center\" bgcolor=\"#e9ecef\">\n" +
                "<table style=\"max-width: 600px;\" border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td style=\"padding: 36px 24px 0; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; border-top: 3px solid #d4dadf;\" align=\"left\" bgcolor=\"#ffffff\">\n" +
                "<h1 style=\"margin: 0; font-size: 32px; font-weight: bold; letter-spacing: -1px; line-height: 48px;\">Confirm Your Email Address</h1>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td align=\"center\" bgcolor=\"#e9ecef\">\n" +
                "<table style=\"max-width: 600px; height: 211px; width: 100%;\" border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                "<tbody>\n" +
                "<tr style=\"height: 78px;\">\n" +
                "<td style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px; height: 78px;\" align=\"left\" bgcolor=\"#ffffff\">\n" +
                "<p style=\"margin: 0;\">Hi ,"+name+"</p>\n" +
                "<pre>Thank you for registering.</pre>\n" +
                "<pre>Please click on the link below to activate your account:<br /><br /></pre>\n" +
                "<a href=\"" + link + "\">Activate Now</a>"+
                "</td>\n" +
                "</tr>\n" +
                "<tr style=\"height: 48px;\">\n" +
                "<td style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px; border-bottom: 3px solid #d4dadf; height: 48px;\" align=\"left\" bgcolor=\"#ffffff\">\n" +
                "<p style=\"margin: 0;\">GetAndGo Team</p>\n" +
                "</td>\n" +
                "</tr>\n" +
                "<tr>\n" +
                "<td style=\"padding: 24px;\" align=\"center\" bgcolor=\"#e9ecef\">&nbsp;</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>";
    }
}
