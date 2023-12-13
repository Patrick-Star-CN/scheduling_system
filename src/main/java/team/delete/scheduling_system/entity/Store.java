package team.delete.scheduling_system.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @author Devin100086
 * @version 1.0
 */

@Data
@Builder
public class Store {
    private String id;
    private String name;
    private String address;
    private float size;
}
