package in.osop.messaging_platform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateStatisticsDto {
    private long totalTemplates;
    private long activeTemplates;
    private long defaultTemplates;
    private List<String> categories;
    private List<String> types;
}
