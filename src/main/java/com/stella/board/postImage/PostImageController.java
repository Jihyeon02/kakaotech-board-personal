package com.stella.board.postImage;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostImageController {

    private final PostImageService postImageService;

    public PostImageController(PostImageService postImageService) {
        this.postImageService = postImageService;
    }

    @PostMapping(
            value = "/{postId}/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<List<PostImageResponse>> uploadFiles(
            @PathVariable Long postId,
            @RequestParam("files") List<MultipartFile> files
    ) throws IOException {

        List<PostImageResponse> response =
                postImageService.uploadFiles(postId, files);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/{postId}/images/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long postId,
            @PathVariable Long imageId
    ) {
        postImageService.deleteImage(postId, imageId);

        return ResponseEntity.noContent().build();
    }
}
