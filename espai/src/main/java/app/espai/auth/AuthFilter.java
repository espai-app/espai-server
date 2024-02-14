/*
 * Copyright 2023 Saxon State and University Library Dresden (SLUB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.espai.auth;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author borowski
 */
public class AuthFilter implements Filter {

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "login.xhtml", "logout.xhtml", "forgotPassword.xhtml", "resetPassword.xhtml", "jakarta.faces.resource");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String currentPath = httpRequest.getRequestURI()
                .substring(httpRequest.getServletContext().getContextPath().length() + 1);

        // check if path is public
        for (String p : PUBLIC_PATHS) {
            if (currentPath.startsWith(p)) {
                chain.doFilter(request, response);
                return;
            }
        }

        // ignore CORS preflight requests
        if (httpRequest.getMethod().equals("OPTIONS")) {
            chain.doFilter(request, response);
            return;
        }

        // if not logged in or public user in private area, redirect to login page
        Principal principal = httpRequest.getUserPrincipal();
        if (principal == null) {
            redirectToLogin(httpRequest, (HttpServletResponse) response);
            return;
        }

        chain.doFilter(request, response);
    }

    public static void redirectToLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String fullRequestUrl = request.getRequestURL()
                .append((request.getQueryString() != null) ? "?" + request.getQueryString() : "")
                .toString();

        URL loginUrl = new URL(
                request.getScheme(),
                request.getServerName(),
                request.getServerPort(),
                request.getContextPath() + "/login.xhtml");

        String loginUrlString = loginUrl.toExternalForm();
        if (!loginUrlString.equals(fullRequestUrl)) {
            loginUrlString += "?returnUrl=" + URLEncoder.encode(
                    fullRequestUrl, StandardCharsets.UTF_8);
        }

        response.sendRedirect(loginUrlString);

    }

}
