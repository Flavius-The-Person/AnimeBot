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

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.apache.commons.text.StringEscapeUtils;

public class ChatBot {
    
    private static final String CREDENTIAL_FILE = "creds.txt";
    private static final String CHAT_CHANNEL = "sg4e";
    private static final String BOT_TWITCH_NAME = "maikachan";
    private static final Random RANDOM = new Random();
    
    public static void main(String[] args) throws IOException {
        
        List<String> creds = Files.lines(Paths.get(CREDENTIAL_FILE)).collect(Collectors.toList());
        String clientId = creds.get(0);
        String clientSecret = creds.get(1);
        String oauthToken = creds.get(2);
                
        // chat credential
        OAuth2Credential credential = new OAuth2Credential("twitch", oauthToken);

        // twitch client
        TwitchClient twitchClient = TwitchClientBuilder.builder()
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .withEnableChat(true)
                .withChatAccount(credential)
                .withEventManager(null)
                .build();
        
        twitchClient.getChat().joinChannel(CHAT_CHANNEL);
        
        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class).subscribe(event -> {
            if(!BOT_TWITCH_NAME.equals(event.getUser().getName())) {
                String message = event.getMessage();
                if("AYAYA".equals(message)) {
                    twitchClient.getChat().sendMessage(CHAT_CHANNEL, "\\ AYAYA /");
                }
                else if(message.toLowerCase().startsWith("!anime ")) {
                    String secondPart = message.substring(7);
                    List<SearchResult> response = SonarMoe.getInstance().getApi().search(new Query(secondPart));
                    if(response.isEmpty()) {
                        twitchClient.getChat().sendMessage(CHAT_CHANNEL, "No results found FeelsAkariMan");
                    }
                    else {
                        SearchResult randomChoice = response.get(RANDOM.nextInt(response.size()));
                        twitchClient.getChat().sendMessage(CHAT_CHANNEL, String.format("From %s episode %s: %s",
                                clean(randomChoice.getEpisode().getSeriesName()),
                                clean(randomChoice.getEpisode().getEpisodeNumber()),
                                clean(randomChoice.getPlaintext())));
                    }
                }
            }
        });
    }
    
    private static String clean(String html) {
        return StringEscapeUtils.unescapeHtml4(html);
    }
}
