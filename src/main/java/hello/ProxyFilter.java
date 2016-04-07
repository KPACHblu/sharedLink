package hello;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;

public class ProxyFilter implements Filter {
    public static final String ATTR_TARGET_URL = "targetUrl";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String requestURI = req.getRequestURI();
        if (requestURI.equals("/") || requestURI.startsWith("/index.html") || requestURI.startsWith("/content")) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            String targetUrl = (String) req.getSession().getAttribute(ATTR_TARGET_URL);
            if (targetUrl != null) {
                String targetRequestUrl = targetUrl + requestURI;


                CloseableHttpClient httpclient = HttpClients.createDefault();
                HttpGet httpGet = new HttpGet(targetRequestUrl);
                CloseableHttpResponse response = httpclient.execute(httpGet);
                System.out.println(response.getStatusLine());
                HttpEntity entity = response.getEntity();

                OutputStream servletOutputStream = servletResponse.getOutputStream();
                entity.writeTo(servletOutputStream);
            }
        }
    }

    @Override
    public void destroy() {

    }
}
