import java.util.LinkedHashMap;
import java.util.Map;

public class PagesTable {
    Map<Integer, Integer> pagesTable;

    public PagesTable(int numberOfPages, int[] freeFramesToBeAlocated) {
        this.pagesTable = new LinkedHashMap<Integer, Integer>();

        for (int i = 0; i < numberOfPages; i++) {
            pagesTable.putIfAbsent(i, freeFramesToBeAlocated[i]);
        }

    }

    public int getFrameByPage(int pageNumber) {
        return this.pagesTable.get(pageNumber);
    }
}
