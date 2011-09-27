package org.salespointframework.web.spring.interceptors;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.salespointframework.core.shop.Shop;
import org.salespointframework.core.user.User;
import org.salespointframework.core.user.UserIdentifier;
import org.salespointframework.core.user.UserManager;
import org.salespointframework.web.WebConstants;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 
 * @author Lars Kreisz
 * @author Uwe Schmidt
 * @author Paul Henke
 */
public class LoginInterceptor extends HandlerInterceptorAdapter
{
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView mav) throws Exception
	{
		Map<String, String[]> params = request.getParameterMap();

		if (!params.containsKey(WebConstants.SP_LOGIN_PARAM))
		{
			return;
		}

		String identifier = params.get(WebConstants.SP_LOGIN_PARAM_IDENTIFIER)[0];
		String password = params.get(WebConstants.SP_LOGIN_PARAM_PASSWORD)[0];

		UserIdentifier userIdentifier = new UserIdentifier(identifier);
		@SuppressWarnings("unchecked")
		UserManager<User> usermanager = (UserManager<User>) Shop.INSTANCE.getUserManager();

		User user = usermanager.get(User.class, userIdentifier);

		if (user != null)
		{
			if (user.verifyPassword(password))
			{
				usermanager.logOn(user, request.getSession());
			}
		}
	}
}
