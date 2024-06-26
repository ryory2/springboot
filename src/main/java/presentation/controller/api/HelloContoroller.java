package presentation.controller.api;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloContoroller {

  @RequestMapping(value = "/")
  public String index(Model model) {
    model.addAttribute("message", "Hello Thymeleaf!!");
    return "HelloWorld";
  }
}
