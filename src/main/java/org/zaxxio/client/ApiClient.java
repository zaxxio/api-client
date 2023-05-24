package org.zaxxio;

public class ApiClient {

    private String baseUrl;

    private ApiClient(Builder builder) {

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

    public <T> T create(Class<T> service){

    }

}
