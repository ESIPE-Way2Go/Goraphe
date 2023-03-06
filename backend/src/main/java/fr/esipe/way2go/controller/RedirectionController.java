package fr.esipe.way2go.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@org.springframework.stereotype.Controller
public class RedirectionController implements ErrorController {

    private static final String PATH = "/error";

    /**
     * Redirects every requests that doesn't match any page to a error 404 page
     * @return
     */
    @RequestMapping(value = PATH, method = {RequestMethod.GET, RequestMethod.POST})
    public String error() {
        return "forward:/";
    }

}