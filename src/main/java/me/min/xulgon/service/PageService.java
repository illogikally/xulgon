package me.min.xulgon.service;

import lombok.AllArgsConstructor;
import me.min.xulgon.exception.PageNotFoundException;
import me.min.xulgon.model.Page;
import me.min.xulgon.repository.PageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class PageService {

   private final PageRepository pageRepository;

   @Transactional(readOnly = true)
   public Long getPhotoSetId(Long pageId) {
      Page page = pageRepository.findById(pageId)
            .orElseThrow(PageNotFoundException::new);
      return page.getPagePhotoSet().getId();
   }
}
