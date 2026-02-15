package io.github.future0923.debug.tools.test.mapstruct.mapper;

import io.github.future0923.debug.tools.test.mapstruct.domain.UserEntity;
import io.github.future0923.debug.tools.test.mapstruct.model.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "id", target = "userId")
    @Mapping(source = "username", target = "loginName")
    @Mapping(source = "createTime", target = "createDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    UserVO entityToVo(UserEntity entity);
}
