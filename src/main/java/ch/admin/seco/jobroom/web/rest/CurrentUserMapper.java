package ch.admin.seco.jobroom.web.rest;


import ch.admin.seco.jobroom.security.UserPrincipal;

public interface CurrentUserMapper {

    CurrentUserResource.CurrentUserDTO toCurrentUserResource(UserPrincipal userPrincipal);

}
