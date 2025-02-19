package server.auth.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import server.auth.utils.ErrorResponder;
import server.auth.utils.JwtUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@Component
public class JwtParseInterceptor implements HandlerInterceptor {
    private final JwtUtils jwtUtils;
    private static final ThreadLocal<Long> authenticatedMemberId = new ThreadLocal<>();

    public JwtParseInterceptor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    public static long getAuthenticatedMemberId() {
        return authenticatedMemberId.get();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            Map<String, Object> claims = jwtUtils.getJwsClaimsFromRequest(request);
            Object memberId = claims.get("memberId");
            if (memberId != null) {
                authenticatedMemberId.set(Long.valueOf(memberId.toString()));
                return true;
            } else {
                // memberId가 null인 경우에 대한 예외 처리
                ErrorResponder.sendErrorResponse(response, HttpStatus.UNAUTHORIZED);
                return false;
            }
        } catch (Exception e) {
//            ErrorResponder.sendErrorResponse(response, HttpStatus.UNAUTHORIZED);
//            return false;
            authenticatedMemberId.set(-1L);  // 유효하지 않는 id 값을 설정하여 로그인이 필요한 기능 호출 시 진행되지 않도록 함
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        authenticatedMemberId.remove();
    }
}
