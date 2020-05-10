package nl.gidsopenstandaarden.hti.testsuite.module.controller;

import nl.gidsopenstandaarden.hti.testsuite.module.service.HtiModuleService;
import nl.gidsopenstandaarden.hti.testsuite.module.valueobject.ValidationRequest;
import nl.gidsopenstandaarden.hti.testsuite.module.valueobject.ValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
@Controller
public class HtiModuleController {


	private HtiModuleService htiModuleService;

	@SuppressWarnings("SpringMVCViewInspection")
	@RequestMapping(value = "module_launch", method = RequestMethod.POST)
	public String launch(@RequestParam String token) {
		return "redirect:module.html?token=" + token;
	}

	@Autowired
	public void setHtiModuleService(HtiModuleService htiModuleService) {
		this.htiModuleService = htiModuleService;
	}

	@RequestMapping(value = "module_validate", method = RequestMethod.POST)
	public @ResponseBody ValidationResponse validate(@RequestBody ValidationRequest request, HttpServletRequest httpServletRequest) {

		return htiModuleService.validate(request, httpServletRequest);
	}
}
