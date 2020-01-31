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
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import org.apache.commons.text.StringEscapeUtils;

public class ChatBot {
    
    private static final String CREDENTIAL_FILE = "credentials.yaml";
    private static final Random RANDOM = new Random();
    
    public static void main(String[] args) throws IOException {
        
        //parse config file
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.findAndRegisterModules();
        
        BotConfig config = mapper.readValue(new File(CREDENTIAL_FILE), BotConfig.class);
                
        // chat credential
        OAuth2Credential credential = new OAuth2Credential("twitch", config.getOauthKey());

        // twitch client
        TwitchClient twitchClient = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withChatAccount(credential)
                .withEventManager(null)
                .build();
        
        config.getChatChannels().forEach(twitchClient.getChat()::joinChannel);
        String commandPrefix = config.getCommandPrefix();
        Map<String, String> commandDictionary = config.getCommands().entrySet().stream().collect(Collectors.toMap(kv -> kv.getKey().toLowerCase(), kv -> kv.getValue()));
        
        twitchClient.getEventManager().onEvent(ChannelMessageEvent.class).subscribe(event -> {
            if(event.getMessage().length() > 0) {
                Responder res = new Responder(event);
                
                if(event.getMessage().startsWith(commandPrefix)) {
                    final String prefixless = event.getMessage().substring(commandPrefix.length());
                    final String[] messageParts = prefixless.split("\\s+", 2);
                    final String command = messageParts[0].toLowerCase();
                    final String everythingAfterTheCommand = (messageParts.length > 1 ? messageParts[1] : "");
                    if(commandDictionary.containsKey(command)) {
                        res.respond(commandDictionary.get(command));
                    }
                    else if("anime".equals(command)) {
                        List<SearchResult> response = SonarMoe.getInstance().getApi().search(new Query(everythingAfterTheCommand));
                        if(response.isEmpty()) {
                            res.respond("No results found FeelsAkariMan");
                        }
                        else {
                            SearchResult randomChoice = response.get(RANDOM.nextInt(response.size()));
                            res.respond(String.format("From %s episode %s: %s",
                                    clean(randomChoice.getEpisode().getSeriesName()),
                                    clean(randomChoice.getEpisode().getEpisodeNumber()),
                                    clean(randomChoice.getPlaintext())));
                        }
                    }
                }
            }
        });
    }
    
    private static String clean(String html) {
        return StringEscapeUtils.unescapeHtml4(html);
    }
    
    public static class Responder {
        private final TwitchChat chat;
        private final ChannelMessageEvent event;
        
        private Responder(ChannelMessageEvent event) {
            this.event = event;
            this.chat = event.getTwitchChat();
        }
        
        public void respond(String message) {
            chat.sendMessage(event.getChannel().getName(), message);
        }
    }
}
