package com.github.braully.persistence;

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

    Map<Parameter, Object> parameters;
    String queryString;
//    Query query;
    List result;
    int page;
    int count;
    int size;
    String queryCountString;

    public PagedQueryResult() {
        result = new ArrayList();
        parameters = new HashMap<>();
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
}
