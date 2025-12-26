package com.portfolio.acfs.mapper;

import com.portfolio.acfs.dto.SupportRequestResponse;
import com.portfolio.acfs.dto.UserSupportRequestResponse;
import com.portfolio.acfs.entity.SupportRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SupportRequestMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userEmail", source = "user.email")
    @Mapping(target = "answeredById", source = "answeredBy.id")
    @Mapping(target = "answeredByEmail", source = "answeredBy.email")
    @Mapping(target = "status", expression = "java(supportRequest.getStatus().name())")
    SupportRequestResponse toSupportRequestResponse(SupportRequest supportRequest);

    List<SupportRequestResponse> toSupportRequestResponseList(List<SupportRequest> supportRequests);

    @Mapping(target = "status", expression = "java(supportRequest.getStatus().name())")
    UserSupportRequestResponse toUserSupportRequestResponse(SupportRequest supportRequest);

    List<UserSupportRequestResponse> toUserSupportRequestResponseList(List<SupportRequest> supportRequests);
}