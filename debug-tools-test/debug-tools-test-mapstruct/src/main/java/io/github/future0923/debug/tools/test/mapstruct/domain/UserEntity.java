package io.github.future0923.debug.tools.test.mapstruct.domain;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserEntity {
    private Long id;
    private String username;
    private String password;
    private String email;
    private LocalDateTime createTime;
}
