
package sg4e.anime;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class SearchResult {
    @JsonProperty("event_id")
    private String eventId;
    private String plaintext;
    private BigDecimal score;
    private Episode episode;
    private String buttons;
}
