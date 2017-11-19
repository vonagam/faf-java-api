package com.faforever.api.avatar;

import com.faforever.api.error.ErrorResponse;
import com.faforever.api.security.OAuthScope;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(path = "/avatars")
public class AvatarController {

  private final AvatarService avatarService;

  public AvatarController(AvatarService avatarService) {
    this.avatarService = avatarService;
  }

  @ApiOperation(value = "Upload avatar", notes = "Avatar metadata - " +
    "{" +
    " \"tooltip\": \"String\"" +
    "}")
  @ApiResponses(value = {
    @ApiResponse(code = 201, message = "Success"),
    @ApiResponse(code = 422, message = "Invalid input", response = ErrorResponse.class),
    @ApiResponse(code = 500, message = "Failure", response = ErrorResponse.class)})
  @ResponseStatus(value = HttpStatus.CREATED)
  @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("#oauth2.hasScope('" + OAuthScope._UPLOAD_AVATAR + "') and hasRole('ROLE_MODERATOR')")
  public void uploadAvatar(
    @ApiParam(name = "metadata") @RequestPart("metadata") AvatarMetadata avatarMetaData,
    @ApiParam(name = "file") @RequestPart("file") MultipartFile avatarImageFile) throws IOException {
    avatarService.processUploadedAvatar(avatarMetaData.getTooltip(), avatarImageFile.getOriginalFilename(), avatarImageFile.getInputStream(), avatarImageFile.getSize());
  }

  @ApiOperation(value = "Delete avatar")
  @ApiResponses(value = {
    @ApiResponse(code = 204, message = "Success"),
    @ApiResponse(code = 422, message = "Invalid input", response = ErrorResponse.class),
    @ApiResponse(code = 500, message = "Failure", response = ErrorResponse.class)})
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  @PreAuthorize("#oauth2.hasScope('" + OAuthScope._UPLOAD_AVATAR + "') and hasRole('ROLE_MODERATOR')")
  public void deleteAvatar(
    @ApiParam(name = "metadata") @PathVariable("id") Integer avatarId) throws IOException {
    avatarService.deleteAvatar(avatarId);
  }

}