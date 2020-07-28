package ee.opngo.pms.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PmsRestError {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private String path;
}
