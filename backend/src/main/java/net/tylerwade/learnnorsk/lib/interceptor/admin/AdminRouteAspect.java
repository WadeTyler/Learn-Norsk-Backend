package net.tylerwade.learnnorsk.lib.interceptor.admin;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import net.tylerwade.learnnorsk.lib.exception.UnauthorizedException;
import net.tylerwade.learnnorsk.lib.util.AuthUtil;
import net.tylerwade.learnnorsk.model.auth.User;
import net.tylerwade.learnnorsk.repository.UserRepository;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
public class AdminRouteAspect {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private UserRepository userRepo;

    @Pointcut("@annotation(net.tylerwade.learnnorsk.lib.interceptor.admin.AdminRoute)")
    public void adminRouteMethods() {
        // empty
    }

    @Before("adminRouteMethods()")
    public void checkAdminRole() throws Exception {
        // Check for authToken cookie
        Cookie[] cookies = request.getCookies();
        String authToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("authToken")) {
                    authToken = cookie.getValue();
                    break;
                }
            }
        }

        String id = authUtil.getIdFromToken(authToken);

        if (authToken == null || id == null) {
            throw new UnauthorizedException("Unauthorized access.");
        }

        // Find user
        Optional<User> user = userRepo.findById(id);
        if (!user.isPresent()) throw new UnauthorizedException("Unauthorized access.");

        String role = user.get().getRole();
        if (!role.equals("admin")) throw new UnauthorizedException("Unauthorized access.");

        request.setAttribute("user", user.get());
    }

}
