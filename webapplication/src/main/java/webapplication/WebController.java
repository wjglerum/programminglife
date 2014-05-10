package webapplication;

import javax.validation.Valid;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Controller
public class WebController extends WebMvcConfigurerAdapter {

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
      registry.addViewController("/results").setViewName("results");
      registry.addViewController("/").setViewName("index");
      registry.addViewController("/index").setViewName("index");
      registry.addViewController("/login").setViewName("login");
      registry.addViewController("/upload").setViewName("upload");
  }

  @RequestMapping(value="/upload", method=RequestMethod.GET)
  public String showForm(Person person) {
      return "upload";
  }

  @RequestMapping(value="/upload", method=RequestMethod.POST)
  public String checkPersonInfo(@Valid Person person, BindingResult bindingResult) {
      if (bindingResult.hasErrors()) {
          return "upload";
      }
      return "redirect:/results";
  }
}