package vn.hoidanit.jobhunter.util;

import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import vn.hoidanit.jobhunter.domain.response.RestResponse;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;

@RestControllerAdvice
public class FormatResponseEntity implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        ServletServerHttpResponse servletServerHttpResponse = (ServletServerHttpResponse) response;
        int status = servletServerHttpResponse.getServletResponse().getStatus();

        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(status);

        if (body instanceof String || body instanceof Resource) {
            return body;
        }
        if (status >= 400) {// case error
            return body;
        } else {// case success
            ApiMessage message = returnType.getMethodAnnotation(ApiMessage.class);
            res.setMessage(message != null ? message.value() : "CALL API SUCCESS");
            res.setData(body);
        }
        return res;
    }

}
