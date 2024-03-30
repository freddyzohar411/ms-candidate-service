package com.avensys.rts.candidate.util;

import com.avensys.rts.candidate.interceptor.JwtTokenInterceptor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class AsyncUtil {

	// Context-aware asynchronous utility methods
	public static <T> CompletableFuture<T> supplyAsyncWithContext(Supplier<T> supplier) {
		RequestAttributes context = RequestContextHolder.currentRequestAttributes();
		return CompletableFuture.supplyAsync(() -> {
			try {
				RequestContextHolder.setRequestAttributes(context);
				return supplier.get();
			} finally {
				RequestContextHolder.resetRequestAttributes();
			}
		});
	}

	public static <T> CompletableFuture<T> supplyAsyncWithContextManualAdd(Supplier<T> supplier, String token, RequestAttributes context ) {

		try {
			return CompletableFuture.supplyAsync(() -> {
				try {
					RequestContextHolder.setRequestAttributes(context);

					// Now the context is available in this thread
//					RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//					String TT = (String) requestAttributes.getAttribute("token", RequestAttributes.SCOPE_REQUEST);
//					System.out.println("Token TT: " + TT);
//					JwtTokenInterceptor.setToken(token);
					return supplier.get();
				} finally {
					RequestContextHolder.resetRequestAttributes();
				}
			});
		} finally {
			JwtTokenInterceptor.clear(); // Clear the token after setting up the CompletableFuture
		}
	}

}
