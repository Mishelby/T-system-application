package org.example.logisticapplication.domain.User;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record MainUserInfoWithOrders(

        UserMainInfoDto userInfoDto,

        @JsonInclude(value = JsonInclude.Include.NON_NULL, content = JsonInclude.Include.NON_EMPTY)
        List<UserOrderInfo> userOrderInfo,

        Boolean allowSystemDecide,

        Integer driversCount

) implements UserInfo {
    @Override
    @JsonIgnore
    public boolean getCurrentOrders() {
        return userOrderInfo == null;
    }

    @Override
    @JsonIgnore
    public Long getId() {
        return userInfoDto.id();
    }
}
