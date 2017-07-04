package fr.ebiz.computerdatabase.ui.web.form;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface Form<T> {

    /**
     * Parse and validate HTTP request.
     *
     * @param request  The HTTP Request
     * @param response The HTTP Response
     * @return The map of errors
     * @throws IOException if an IO exception occurs
     */
    Map<String, String> parseAndValidate(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
