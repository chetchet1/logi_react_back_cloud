package kr.co.seoulit.logistics.logiinfosvc.hr.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/hr/*")
public class MemberLogoutController {
	
	@RequestMapping(value="/logout", method=RequestMethod.GET)
    public ModelAndView LogOut(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession();
        session.invalidate();

        return new ModelAndView("/logiinfo/loginForm");
    }
}
