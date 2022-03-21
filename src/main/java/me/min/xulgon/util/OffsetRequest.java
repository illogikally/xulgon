package me.min.xulgon.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class OffsetRequest implements Pageable {
   private final Integer size;
   private final Long offset;
   private final Long after;
   private final Long before;

   public OffsetRequest(Integer size, Long offset, Long after, Long before) {
      offset = offset == null ? 0 : offset;
      final int VERY_BIG_NUMBER = Integer.MAX_VALUE - 1;
      size = size == null ? VERY_BIG_NUMBER : size;

      if (offset < 0) {
         throw new IllegalArgumentException("Offset must not less than 0");
      }

      if (size < 0L) {
         throw new IllegalArgumentException("Limit must not less than 0");
      }
      this.size = size;
      this.offset = offset;
      this.after = after == null ? 0L : after;
      this.before = before == null ? Long.MAX_VALUE : before;
   }



   public OffsetRequest sizePlusOne() {
      return new OffsetRequest(this.size + 1, this.offset, after, before);
   }

   public Long getAfter() {
      return after;
   }
   public Long getBefore() {
      return before;
   }

   @Override
   public int getPageNumber() {
      return (int) (offset / size);
   }

   @Override
   public int getPageSize() {
      return size;
   }

   @Override
   public long getOffset() {
      return offset;
   }

   @NotNull
   @Override
   public Sort getSort() {
      return Sort.unsorted();
   }

   @NotNull
   @Override
   public Pageable next() {
      return new OffsetRequest(size, offset + size, after, before);
   }

   @NotNull
   @Override
   public Pageable previousOrFirst() {
      final long offset = this.offset < size ? 0 : this.offset - size;
      return new OffsetRequest(size, offset, after, before);
   }

   @NotNull
   @Override
   public Pageable first() {
      return new OffsetRequest(size, 0L, after, before);
   }

   @NotNull
   @Override
   public Pageable withPage(int pageNumber) {
      return new OffsetRequest(size, (long) pageNumber * size, after, before);
   }

   @Override
   public boolean hasPrevious() {
      return offset > size;
   }

}
