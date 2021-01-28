package com.ksh.crfi.windows.user;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;

@RestController
@CrossOrigin
public class UserController {

	@RequestMapping(value="/authentication", produces="application/javascript")
	public String getUserJsonp(Authentication auth, String callback) {
		String prefix = Strings.isNullOrEmpty(callback)?"_ng_jsonp_._req0.finished": callback;
		return String.format("%s([\"%s\"])", prefix, auth.getPrincipal().toString().replace("\\", "_"));
	}
}
