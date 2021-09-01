package jetty2.com.jetty2;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@SuppressWarnings("serial")
public class HelloSessionServlet2 extends HttpServlet {
  public HelloSessionServlet2() {}

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);
    response.addHeader("Cache-Control", "no-cache");

    HttpSession session = request.getSession();

    String greeting = request.getParameter("greeting");
    if (greeting != null) {
      session.setAttribute("greeting", greeting);
    } else {
      greeting = (String) session.getAttribute("greeting");
    }

    PrintWriter out = response.getWriter();
    out.println("<h1>" + " from HelloSessionServlet 2 </h1>");
    out.println("<pre>");
    out.println("session.getId() = " + session.getId());
    out.println("</pre>");

  }



}
