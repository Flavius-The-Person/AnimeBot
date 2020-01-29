
package sg4e.anime;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.math.BigDecimal;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Episode {
    private String seriesName;
    private String seriesUrl;
    private String episodeNumber;
    private String episodeUrl;
    private BigDecimal timestamp;
}
