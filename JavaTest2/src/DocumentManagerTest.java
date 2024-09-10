import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DocumentManagerTest {

    @Test
    public void testSaveAndSearch() {
        DocumentManager documentManager = new DocumentManager();

        // Створюємо автора
        DocumentManager.Author author = DocumentManager.Author.builder()
                .id("author1")
                .name("John Doe")
                .build();

        // Створюємо документ
        DocumentManager.Document document = DocumentManager.Document.builder()
                .title("Document Title")
                .content("This is the content of the document.")
                .author(author)
                .build();

        // Зберігаємо документ
        DocumentManager.Document savedDocument = documentManager.save(document);

        // Перевіряємо, що ID було згенеровано
        assertNotNull(savedDocument.getId());

        // Створюємо запит на пошук
        DocumentManager.SearchRequest searchRequest = DocumentManager.SearchRequest.builder()
                .titlePrefixes(List.of("Document"))
                .containsContents(List.of("content"))
                .authorIds(List.of("author1"))
                .build();

        // Шукаємо документи
        List<DocumentManager.Document> searchResults = documentManager.search(searchRequest);

        // Перевіряємо, що знайдено 1 документ
        assertEquals(1, searchResults.size());
        assertEquals("Document Title", searchResults.get(0).getTitle());
    }
}