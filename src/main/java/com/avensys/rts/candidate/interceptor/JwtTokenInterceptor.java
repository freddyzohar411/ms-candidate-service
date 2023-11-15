package com.avensys.rts.candidate.interceptor;

import com.avensys.rts.candidate.util.JwtUtil;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class JwtTokenInterceptor implements RequestInterceptor {
	@Override
	 public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header("Authorization", "Bearer " + JwtUtil.getTokenFromContext());
    }

}
