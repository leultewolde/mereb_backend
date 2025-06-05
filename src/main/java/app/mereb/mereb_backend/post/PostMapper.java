package app.mereb.mereb_backend.post;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class PostMapper {

    @Mapping(source = "repostOf", target = "repostOf_postID")
    @Mapping(target = "authorUsername", ignore = true)
    @Mapping(target = "repostOf_authorUsername", ignore = true)
    @Mapping(target = "repostOf_content", ignore = true)
    @Mapping(target = "repostOf_createdAt", ignore = true)
    @Mapping(target = "repostOf_updatedAt", ignore = true)
    @Mapping(target = "likedByCurrentUser", ignore = true)
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "allReposters", ignore = true)
    @Mapping(target = "repostedByCurrentUser", ignore = true)
    @Mapping(target = "repostCount", ignore = true)
    public abstract PostResponseDTO toDto(Post post);
}

