package com.example.demo.registration;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping(path = "api/v1/registration")
@AllArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping
    public String register(@RequestBody RegistrationRequest request){
        return registrationService.register(request);
    }

    @GetMapping(path = "confirm")
    public ModelAndView confirm(@RequestParam("token") String token){
    String result=registrationService.confirmToken(token);
    if(result=="confirmed") { return new ModelAndView("redirect:"+"http://localhost:8080/"); }
    return new  ModelAndView("Errors:+"+"http://localhost:8080/Error");
}

}
