package com.portfolio.acfs.mapper;

import com.portfolio.acfs.dto.ResetPasswordRequest;
import com.portfolio.acfs.entity.PasswordResetToken;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResetPasswordMapper {

    PasswordResetToken fromDto(ResetPasswordRequest request);
}
