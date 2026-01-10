package service;

import dto.MediaWithRatingDto;
import models.Media;
import models.MediaFilter;
import models.MediaType;
import repository.MediaRepository;
import service.MediaService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    MediaRepository repo;

    @InjectMocks
    MediaService service;

    @Test
    void findById_shouldReturnDto() {
        UUID id = UUID.randomUUID();
        MediaWithRatingDto dto = mock(MediaWithRatingDto.class);

        when(repo.findByIdWithRating(id)).thenReturn(dto);

        MediaWithRatingDto result = service.findById(id);

        assertSame(dto, result);
        verify(repo).findByIdWithRating(id);
    }

    @Test
    void findAll_withMediaType() {
        List<Media> medias = List.of(mock(Media.class));

        when(repo.findAll(Optional.of(MediaType.movie))).thenReturn(medias);

        List<Media> result = service.findAll(Optional.of(MediaType.movie));

        assertEquals(medias, result);
    }

    @Test
    void findAll_withoutMediaType() {
        List<Media> medias = List.of(mock(Media.class));

        when(repo.findAll(Optional.empty())).thenReturn(medias);

        List<Media> result = service.findAll();

        assertEquals(medias, result);
    }

    @Test
    void findAllWithRating_shouldDelegateToRepo() {
        List<MediaWithRatingDto> dtos = List.of(mock(MediaWithRatingDto.class));

        when(repo.findAllWithRating()).thenReturn(dtos);

        List<MediaWithRatingDto> result = service.findAllWithRating();

        assertEquals(dtos, result);
        verify(repo).findAllWithRating();
    }

    @Test
    void findRecommendedMedias() {
        List<MediaWithRatingDto> dtos = List.of(mock(MediaWithRatingDto.class));

        when(repo.findReccommendedMedias("Action", 5)).thenReturn(dtos);

        List<MediaWithRatingDto> result =
                service.findReccommendedMedias("Action", 5);

        assertEquals(dtos, result);
    }

    @Test
    void save_newMedia_shouldSetCreatorAndSave() {
        UUID userId = UUID.randomUUID();
        Media media = new Media();

        boolean result = service.save(media, userId);

        assertTrue(result);
        assertEquals(userId, media.getCreatorId());
        verify(repo).save(media);
    }

    @Test
    void save_existingMedia_notFound_shouldReturnFalse() {
        UUID mediaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Media media = new Media();
        media.setId(mediaId);

        when(repo.findByIdWithRating(mediaId)).thenReturn(null);

        boolean result = service.save(media, userId);

        assertFalse(result);
        verify(repo, never()).save(any());
    }

    @Test
    void save_existingMedia_wrongUser_shouldReturnFalse() {
        UUID mediaId = UUID.randomUUID();
        UUID creatorId = UUID.randomUUID();
        UUID otherUser = UUID.randomUUID();

        Media existing = new Media();
        existing.setCreatorId(creatorId);

        MediaWithRatingDto dto = mock(MediaWithRatingDto.class);
        when(dto.getMedia()).thenReturn(existing);

        when(repo.findByIdWithRating(mediaId)).thenReturn(dto);

        Media media = new Media();
        media.setId(mediaId);

        boolean result = service.save(media, otherUser);

        assertFalse(result);
        verify(repo, never()).save(any());
    }

    @Test
    void save_existingMedia_correctUser_shouldSave() {
        UUID mediaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Media existing = new Media();
        existing.setCreatorId(userId);

        MediaWithRatingDto dto = mock(MediaWithRatingDto.class);
        when(dto.getMedia()).thenReturn(existing);

        when(repo.findByIdWithRating(mediaId)).thenReturn(dto);

        Media media = new Media();
        media.setId(mediaId);

        boolean result = service.save(media, userId);

        assertTrue(result);
        assertEquals(userId, media.getCreatorId());
        verify(repo).save(media);
    }

    @Test
    void filterMedia_shouldDelegateToRepo() {
        List<Media> medias = List.of(mock(Media.class));

        when(repo.findByFilter(MediaFilter.GENRE, "Action"))
                .thenReturn(medias);

        List<Media> result =
                service.filterMedia(MediaFilter.GENRE, "Action");

        assertEquals(medias, result);
    }

    @Test
    void delete_shouldReturnRepoResult() {
        UUID mediaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(repo.delete(mediaId, userId)).thenReturn(true);

        boolean result = service.delete(mediaId, userId);

        assertTrue(result);
        verify(repo).delete(mediaId, userId);
    }
}
