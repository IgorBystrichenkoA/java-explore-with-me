package ru.practicum.ewm.data;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Objects;

public class OffsetBasedPageRequest implements Pageable {

    private final long offset;
    private final int pageNumber;
    private final int pageSize;

    private final Sort sort;

    public OffsetBasedPageRequest(long offset, int pageNumber, int pageSize, Sort sort) {
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must not be less than zero!");
        }

        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number must not be less than zero!");
        }

        if (pageSize < 1) {
            throw new IllegalArgumentException("Page size must not be less than one!");
        }

        this.offset = offset;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;

        this.sort = Objects.requireNonNullElseGet(sort, Sort::unsorted);
    }

    public OffsetBasedPageRequest(int offset, int limit, Sort sort) {
        this(offset, 0, limit, sort);
    }

    public OffsetBasedPageRequest(int offset, int limit) {
        this(offset, 0, limit, null);
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return this.sort;
    }

    @Override
    public Pageable next() {
        return new OffsetBasedPageRequest(this.getOffset(), this.getPageNumber() + 1, this.getPageSize(), this.getSort());
    }

    @Override
    public Pageable previousOrFirst() {
        return this.hasPrevious() ? this.previous() : this.first();
    }

    public Pageable previous() {
        return this.getPageNumber() == 0 ? this :
                new OffsetBasedPageRequest(this.getOffset(), this.getPageNumber() - 1, this.getPageSize(), this.getSort());
    }

    @Override
    public Pageable first() {
        return new OffsetBasedPageRequest(this.getOffset(), 0, this.getPageSize(), this.getSort());
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new OffsetBasedPageRequest(this.getOffset(), pageNumber, this.getPageSize(), this.getSort());
    }

    @Override
    public boolean hasPrevious() {
        return this.getPageNumber() > 0;
    }
}