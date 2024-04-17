package com.avensys.rts.candidate.interceptor;

import com.avensys.rts.candidate.util.JwtUtil;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

//public class JwtTokenInterceptor implements RequestInterceptor {
//	@Override
//	 public void apply(RequestTemplate requestTemplate) {
//        requestTemplate.header("Authorization", "Bearer " + JwtUtil.getTokenFromContext());
//    }
//
//}

public class JwtTokenInterceptor implements RequestInterceptor {

	private static final ThreadLocal<String> threadLocalToken = new ThreadLocal<>();

	public JwtTokenInterceptor() {
	}

	@Override
	public void apply(RequestTemplate requestTemplate) {
		String token = threadLocalToken.get(); // Attempt to get the token from ThreadLocal first
		if (token == null) {
			// Fallback to fetching the token from RequestContextHolder if not found in ThreadLocal
			RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
			if (requestAttributes != null) {
				token = (String) requestAttributes.getAttribute("token", RequestAttributes.SCOPE_REQUEST);
			}
		}

		if (token != null) {
			requestTemplate.header("Authorization", "Bearer " + token);
		}

		System.out.println("Token 12345: " + token);
	}

	public static void setToken(String token) {
		threadLocalToken.set(token);
	}

	public static void clear() {
		threadLocalToken.remove();
	}
}
