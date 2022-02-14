package me.min.xulgon.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.text.MessageFormat;

public class Util {
   private Util() {}

   public static Pageable limToPage(int limit, int offset) {
      final int page = offset / limit;
      return PageRequest.of(page, limit);
   }

   public static String PRIVACY_FILTER =
//        "        c.user_id = {0} \n" +
        "        EXISTS \n" +
        "        ( \n" +
        "            SELECT * \n" +
        "            FROM friendship f \n" +
        "            WHERE \n" +
        "                1 IN (f.usera_id, f.userb_id) \n" +
        "                AND c.user_id IN (f.usera_id, f.userb_id) \n" +
        "        ) \n";
//        "        OR p.privacy = ''PUBLIC'' \n";
}
