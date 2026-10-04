package com.stella.board.postImage;
import com.stella.board.post.Post;
import com.stella.board.post.exception.PostNotFoundException;
import com.stella.board.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PostImageService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final PostImageValidator postImageValidator;
    private final PostImageUploader postImageUploader;
    private final S3TransactionCompensator s3Compensator;

    public PostImageService(
            PostRepository postRepository,
            PostImageRepository postImageRepository,
            PostImageValidator postImageValidator,
            PostImageUploader postImageUploader,
            S3TransactionCompensator s3Compensator
    ) {
        this.postRepository = postRepository;
        this.postImageRepository = postImageRepository;
        this.postImageValidator = postImageValidator;
        this.postImageUploader = postImageUploader;
        this.s3Compensator = s3Compensator;
    }

    @Transactional
    public List<PostImageResponse> uploadFiles(
            Long postId,
            List<MultipartFile> files
    ) {
        postImageValidator.validate(files);

        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new PostNotFoundException(postId)
                );

        int nextSortOrder =
                postImageRepository.findMaxSortOrder(postId) + 1;

        List<PostImageUploader.UploadedImage> uploadedImages =
                postImageUploader.uploadAll(postId, files);

        List<String> uploadedKeys = uploadedImages.stream()
                .map(PostImageUploader.UploadedImage::imageKey)
                .toList();

        // 이후 DB 작업이 실패하면 업로드한 S3 객체 삭제
        s3Compensator.deleteAllOnRollback(uploadedKeys);

        List<PostImage> postImages = new ArrayList<>();

        for (int index = 0; index < uploadedImages.size(); index++) {
            PostImageUploader.UploadedImage uploaded =
                    uploadedImages.get(index);

            postImages.add(
                    PostImage.create(
                            post,
                            uploaded.imageKey(),
                            uploaded.imageUrl(),
                            uploaded.originalFileName(),
                            uploaded.fileSize(),
                            uploaded.contentType(),
                            nextSortOrder + index
                    )
            );
        }

        List<PostImage> savedImages =
                postImageRepository.saveAll(postImages);

        postImageRepository.flush();

        return savedImages.stream()
                .map(PostImageResponse::from)
                .toList();
    }

    @Transactional
    public void deleteImage(
            Long postId,
            Long imageId
    ) {
        PostImage postImage = postImageRepository
                .findByIdAndPost_PostId(imageId, postId)
                .orElseThrow(() ->
                        new PostImageNotFoundException(
                                postId,
                                imageId
                        )
                );

        String imageKey = postImage.getImageKey();

        postImageRepository.delete(postImage);
        postImageRepository.flush();

        s3Compensator.deleteAfterCommit(imageKey);
    }
}