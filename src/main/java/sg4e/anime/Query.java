
package sg4e.anime;

import lombok.Data;
import lombok.NonNull;

@Data
public class Query {
    @NonNull private final String query;
}
