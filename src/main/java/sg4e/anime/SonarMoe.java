/*
 * Copyright 2020 sg4e.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
