package server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class SomeController {

    /**
     * Some example function
     * @return helloworld string
     */
    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "Hello world!";
    }
}