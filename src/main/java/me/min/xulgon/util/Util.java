package me.min.xulgon.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class Util {
   public static Pageable limToPage(int limit, int offset) {
      final int page = offset / limit;
      return PageRequest.of(page, limit);
   }
}
