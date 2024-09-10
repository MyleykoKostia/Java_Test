import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class DocumentManager {

    private final Map<String, Document> storage = new HashMap<>();

    /**
     * Upsert the document to storage.
     * If the document does not have an ID, generate one.
     * Don't change the [created] field if it exists.
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (document.getId() == null) {
            document.setId(UUID.randomUUID().toString());
            document.setCreated(Instant.now());
        } else if (document.getCreated() == null) {
            document.setCreated(Instant.now());
        }
        storage.put(document.getId(), document);
        return document;
    }

    /**
     * Find documents which match with the search request.
     *
     * @param request - search request, each field could be null
     * @return list of matched documents
     */
    public List<Document> search(SearchRequest request) {
        return storage.values().stream()
                .filter(document -> matchesRequest(document, request))
                .collect(Collectors.toList());
    }

    /**
     * Find document by ID.
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    private boolean matchesRequest(Document document, SearchRequest request) {
        if (request.getTitlePrefixes() != null && !request.getTitlePrefixes().isEmpty()) {
            boolean titleMatches = request.getTitlePrefixes().stream()
                    .anyMatch(prefix -> document.getTitle() != null && document.getTitle().startsWith(prefix));
            if (!titleMatches) return false;
        }

        if (request.getContainsContents() != null && !request.getContainsContents().isEmpty()) {
            boolean contentMatches = request.getContainsContents().stream()
                    .anyMatch(content -> document.getContent() != null && document.getContent().contains(content));
            if (!contentMatches) return false;
        }

        if (request.getAuthorIds() != null && !request.getAuthorIds().isEmpty()) {
            if (document.getAuthor() == null || !request.getAuthorIds().contains(document.getAuthor().getId())) {
                return false;
            }
        }

        if (request.getCreatedFrom() != null && document.getCreated().isBefore(request.getCreatedFrom())) {
            return false;
        }

        if (request.getCreatedTo() != null && document.getCreated().isAfter(request.getCreatedTo())) {
            return false;
        }

        return true;
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}
