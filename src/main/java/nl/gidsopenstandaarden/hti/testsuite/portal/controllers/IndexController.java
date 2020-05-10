package nl.gidsopenstandaarden.hti.testsuite.portal.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 */
@Controller
@RequestMapping("/")
public class IndexController {
	@SuppressWarnings({"SpringMVCViewInspection", "SameReturnValue"})
	@RequestMapping(method = RequestMethod.GET)
	public String get(){
		return "redirect:portal.html";
	}
}
