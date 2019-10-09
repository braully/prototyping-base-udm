package com.github.braully.persistence;

import com.github.braully.util.UtilCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Parameter;

/**
 *
 * @author braully
 */
public class PagedQueryResult {

    static int WINDOW_SIZE = 7;

    Map<Parameter, Object> parameters;
    String queryString;
//    Query query;
    List result;
    int page;
    int count;
    int size;
    Map infoExtra;
    String queryCountString;

    public PagedQueryResult() {
        result = new ArrayList();
        parameters = new HashMap<>();
        infoExtra = new HashMap<>();
    }

    PagedQueryResult(int size, int page) {
        this();
        this.size = size;
        this.page = page;
    }

    public List getResult() {
        return result;
    }

    public int getPage() {
        return page;
    }

    public int getCount() {
        return count;
    }

    public int getSize() {
        return size;
    }

    public int getTotalPages() {
        if (size == 0) {
            return 0;
        }
        float tpages = (float) count / (float) size;
        return Math.round(tpages);
    }

    public List<Integer> getWindowPages() {
        return UtilCollection.list(getWindowIni(), getWindowEnd());
    }

    public int getWindowIni() {
        if (getTotalPages() > WINDOW_SIZE) {
            int desloc = (this.page % (WINDOW_SIZE - 2));
            int pos = desloc * WINDOW_SIZE;
            return Math.min(this.page, pos);
        }
        return 0;
    }

    public int getWindowEnd() {
        int totalPages = this.getTotalPages();
        if (getTotalPages() > WINDOW_SIZE) {
            return Math.min(getWindowIni() + WINDOW_SIZE, totalPages);
        }
        return totalPages;
    }

    void newResults(Collection nwreslt) {
        result.clear();
        result.addAll(nwreslt);
    }

    public void setPage(int toPage) {
        this.page = toPage;
    }

    public void next() {
        if (!hasNext()) {
            throw new IllegalStateException("Page Query Result not found");
        }
        this.page++;
    }

    public void previous() {
        if (!hasPrevious()) {
            throw new IllegalStateException("Page Query Result not found");
        }
        this.page--;
    }

    public boolean hasNext() {
        return this.page < this.getTotalPages();
    }

    public boolean hasPrevious() {
        return page > 0;
    }

    public boolean hasWindowPageQueryResult() {
        //Se tem mais paginas que o tamanho da janela
        if (this.getTotalPages() > WINDOW_SIZE) {
            return true;
        }
        return false;
    }
}
