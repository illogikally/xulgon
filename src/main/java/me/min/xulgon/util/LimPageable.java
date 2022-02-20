package me.min.xulgon.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class LimPageable implements Pageable {
   private final Integer size;
   private final Long offset;

   public LimPageable(Integer size, Long offset) {
      offset = offset == null ? 0 : offset;
      int veryBigNumber = Integer.MAX_VALUE - 1;
      size = size == null ? veryBigNumber : size;
      if (offset < 0) {
         throw new IllegalArgumentException("Offset index must not be less than zero!");
      }

      if (size < 1) {
         throw new IllegalArgumentException("Limit must not be less than one!");
      }

      this.size = size;
      this.offset = offset;
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

   @Override
   public Pageable next() {
      return null;
   }

   @Override
   public Pageable previousOrFirst() {
      return null;
   }

   @Override
   public Pageable first() {
      return null;
   }

   @Override
   public Pageable withPage(int pageNumber) {
      return null;
   }

   @Override
   public boolean hasPrevious() {
      return false;
   }
}
