import java.util.LinkedHashMap;
import java.util.Map;

public class PagesTable {
    Map<Integer, Integer> pagesTable;

    public PagesTable(int numberOfPages, int[] allocatedFrames) {
        this.pagesTable = new LinkedHashMap<>();

        for (int i = 0; i < numberOfPages; i++) {
            pagesTable.putIfAbsent(i, allocatedFrames[i]);
        }

    }

    public int getFrameByPage(int pageNumber) {
        return this.pagesTable.get(pageNumber);
    }
}
