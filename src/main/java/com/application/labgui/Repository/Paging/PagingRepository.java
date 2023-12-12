package com.application.labgui.Repository.Paging;

import com.application.labgui.Domain.Entitate;
import com.application.labgui.Repository.Repository;

public interface PagingRepository<ID, E extends Entitate<ID>> extends Repository<ID, E> {
    Page<E> findAll(Pageable pageable);
}
