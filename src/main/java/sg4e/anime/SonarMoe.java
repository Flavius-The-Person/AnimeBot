
package sg4e.anime;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Feign;
import feign.Headers;
import feign.Logger;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import java.util.List;

public class SonarMoe {
    
    private static final SonarMoe INSTANCE = new SonarMoe();
    private static final String BASE_URL = "https://sonar.moe/api";
    
    private final Api api;
    
    public interface Api {
        @RequestLine("POST /search")
        @Headers("Content-Type: application/json")
        List<SearchResult> search(Query query);
    }
    
    private SonarMoe() {
        ObjectMapper mapper = new ObjectMapper();
        
        api = Feign.builder()
            .client(new OkHttpClient())
            .encoder(new JacksonEncoder(mapper))
            .decoder(new JacksonDecoder(mapper))
            .logger(new Logger.ErrorLogger())
            .target(Api.class, BASE_URL);
    }
    
    public Api getApi() {
        return api;
    }
    
    public static SonarMoe getInstance() {
        return INSTANCE;
    }
    
}
