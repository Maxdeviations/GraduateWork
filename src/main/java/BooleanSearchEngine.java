import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

public class BooleanSearchEngine implements SearchEngine {
    private final Map<String, List<PageEntry>> database = new HashMap<>();
    private String pdfName;

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        for (File pdf : Objects.requireNonNull(pdfsDir.listFiles())) {
            pdfName = pdf.getName();
            PdfDocument doc = new PdfDocument(new PdfReader(pdf));
            int pages = doc.getNumberOfPages();
            for (int i = 1; i <= pages; i++) {
                String text = PdfTextExtractor.getTextFromPage(doc.getPage(i));
                String[] words = text.split("\\P{IsAlphabetic}+");
                List<String> sort = Arrays.stream(words)
                        .map(String::toLowerCase)
                        .sorted()
                        .collect(Collectors.toList());
                int count = -1;
                String key = sort.get(0);
                for (int j = 0; j < sort.size(); j++) {
                    count++;
                    if (sort.get(j).equals(key)) {
                        if (j == sort.size() - 1) {
                            count++;
                            pageMapPut(i, count, key);
                        }
                        continue;
                    } else {
                        pageMapPut(i, count, key);
                        key = sort.get(j);
                    }
                    count = 0;
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        return database.get(word.toLowerCase());
    }

    public void pageMapPut(int page, int count, String key) {
        PageEntry pageEntry = new PageEntry(pdfName, page, count);
        List<PageEntry> pageEntryList;
        if (database.containsKey(key)) {
            pageEntryList = database.get(key);
        } else {
            pageEntryList = new ArrayList<>();
        }
        pageEntryList.add(pageEntry);
        Collections.sort(pageEntryList);
        database.put(key, pageEntryList);
    }
}
