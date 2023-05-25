package org.zaxxio.client;

import com.google.gson.Gson;
import org.zaxxio.http.*;

import javax.net.ssl.SSLContext;
import java.lang.reflect.*;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class ApiClient {

    private String baseUrl;

    private ApiClient(Builder builder) {
        this.baseUrl = builder.baseUrl;
    }

    public static class Builder{
        private String baseUrl;

        public Builder basePath(String baseUrl){
            this.baseUrl = baseUrl;
            return this;
        }

        public ApiClient build(){
            return new ApiClient(this);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> service){
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                // Http Request Builder
                HttpRequest.Builder builder = HttpRequest.newBuilder();

                // Headers Tag
                if (method.isAnnotationPresent(Headers.class)){
                    Headers headersTag = method.getAnnotation(Headers.class);
                    String[] headers = headersTag.value();
                    for (String header : headers) {
                        String[] headerChunks = header.split(":");
                        if (headerChunks.length == 2){
                            String headerName = headerChunks[0];
                            String headerValue = headerChunks[1];
                            builder.header(headerName, headerValue);
                        }
                    }
                }

                // Header Tag
                Parameter[] parameters = method.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];

                    if (parameter.isAnnotationPresent(Header.class)) {
                        Header header = parameter.getAnnotation(Header.class);
                        String headerName = header.value();
                        String headerValue = (String) args[i];
                        builder.header(headerName, headerValue);
                    }
                }

                // GET Request Method Mapping
                if (method.isAnnotationPresent(GET.class)){
                    GET get = method.getAnnotation(GET.class);
                    String url = baseUrl + get.value();

                    Map<String, String> pathParams = new HashMap<>();
                    StringBuilder queryParams = new StringBuilder();

                    parameters = method.getParameters();

                    for (int i = 0; i < parameters.length; i++) {
                        if (parameters[i].isAnnotationPresent(Path.class)) {
                            Path path = parameters[i].getAnnotation(Path.class);
                            pathParams.put(path.value(), String.valueOf(args[i]));
                        }
                        if (parameters[i].isAnnotationPresent(Query.class)) {
                            Query query = parameters[i].getAnnotation(Query.class);
                            if(queryParams.length() > 0){
                                queryParams.append("&");
                            } else {
                                queryParams.append("?");
                            }
                            queryParams.append(query.value()).append("=").append(args[i]);
                        }
                    }

                    for(Map.Entry<String, String> entry : pathParams.entrySet()){
                        url = url.replace("{" + entry.getKey() + "}", entry.getValue());
                    }

                    url += queryParams.toString();
                    URI uri = new URI(url);
                    builder.uri(uri);
                    HttpResponse<String> response = HttpClient
                            .newBuilder()
                            .proxy(ProxySelector.getDefault())
                            .build()
                            .send(builder.build(), HttpResponse.BodyHandlers.ofString());

                    Type returnType = method.getGenericReturnType();
                    return new Gson().fromJson(response.body(), returnType);
                }else if (method.isAnnotationPresent(POST.class)){
                    POST post = method.getAnnotation(POST.class);
                    String url = baseUrl + post.value();

                    builder.header("Content-type", "application/json; charset=UTF-8");

                    Map<String, String> pathParams = new HashMap<>();
                    StringBuilder queryParams = new StringBuilder();
                    String body = null;

                    // Handle @Path, @Query, and @Body annotations
                    parameters = method.getParameters();
                    for (int i = 0; i < parameters.length; i++) {
                        if (parameters[i].isAnnotationPresent(Path.class)) {
                            Path path = parameters[i].getAnnotation(Path.class);
                            pathParams.put(path.value(), String.valueOf(args[i]));
                        }
                        if (parameters[i].isAnnotationPresent(Query.class)) {
                            Query query = parameters[i].getAnnotation(Query.class);
                            if(queryParams.length() > 0){
                                queryParams.append("&");
                            } else {
                                queryParams.append("?");
                            }
                            queryParams.append(query.value()).append("=").append(args[i]);
                        }
                        if (parameters[i].isAnnotationPresent(Body.class)) {
                            body = new Gson().toJson(args[i]); // Assuming you have a 'converter' instance
                        }
                    }

                    for(Map.Entry<String, String> entry : pathParams.entrySet()){
                        url = url.replace("{" + entry.getKey() + "}", entry.getValue());
                    }

                    url += queryParams.toString();


                    URI uri = new URI(url);
                    builder.uri(uri);

                    if (body != null) {
                        builder.POST(HttpRequest.BodyPublishers.ofString(body));
                    } else {
                        builder.POST(HttpRequest.BodyPublishers.noBody());
                    }

                    HttpResponse<String> response = HttpClient.newBuilder()
                            .proxy(ProxySelector.getDefault())
                            .sslContext(SSLContext.getDefault())
                            .build()
                            .send(builder.build(), HttpResponse.BodyHandlers.ofString());

                    Type returnType = method.getGenericReturnType();

                    return new Gson().fromJson(response.body(), returnType);

                }else if (method.isAnnotationPresent(PATCH.class)){
                    PATCH patch = method.getAnnotation(PATCH.class);

                } else if (method.isAnnotationPresent(DELETE.class)) {
                    DELETE delete = method.getAnnotation(DELETE.class);

                }
                return null;
            }
        });
    }

}
