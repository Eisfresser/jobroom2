package ch.admin.seco.jobroom.web.rest;


import ch.admin.seco.jobroom.security.UserPrincipal;
import ch.admin.seco.jobroom.service.dto.CurrentUserDTO;

public interface CurrentUserMapper {

    CurrentUserDTO toCurrentUserResource(UserPrincipal userPrincipal);

}
