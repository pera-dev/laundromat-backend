package com.laundromat.app.web.rest;

import com.laundromat.app.domain.LaundryItem;
import com.laundromat.app.repository.LaundryItemRepository;
import com.laundromat.app.repository.search.LaundryItemSearchRepository;
import com.laundromat.app.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.laundromat.app.domain.LaundryItem}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class LaundryItemResource {

    private final Logger log = LoggerFactory.getLogger(LaundryItemResource.class);

    private static final String ENTITY_NAME = "laundryItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LaundryItemRepository laundryItemRepository;

    private final LaundryItemSearchRepository laundryItemSearchRepository;

    public LaundryItemResource(LaundryItemRepository laundryItemRepository, LaundryItemSearchRepository laundryItemSearchRepository) {
        this.laundryItemRepository = laundryItemRepository;
        this.laundryItemSearchRepository = laundryItemSearchRepository;
    }

    /**
     * {@code POST  /laundry-items} : Create a new laundryItem.
     *
     * @param laundryItem the laundryItem to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new laundryItem, or with status {@code 400 (Bad Request)} if the laundryItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/laundry-items")
    public ResponseEntity<LaundryItem> createLaundryItem(@RequestBody LaundryItem laundryItem) throws URISyntaxException {
        log.debug("REST request to save LaundryItem : {}", laundryItem);
        if (laundryItem.getId() != null) {
            throw new BadRequestAlertException("A new laundryItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LaundryItem result = laundryItemRepository.save(laundryItem);
        laundryItemSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/laundry-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /laundry-items} : Updates an existing laundryItem.
     *
     * @param laundryItem the laundryItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated laundryItem,
     * or with status {@code 400 (Bad Request)} if the laundryItem is not valid,
     * or with status {@code 500 (Internal Server Error)} if the laundryItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/laundry-items")
    public ResponseEntity<LaundryItem> updateLaundryItem(@RequestBody LaundryItem laundryItem) throws URISyntaxException {
        log.debug("REST request to update LaundryItem : {}", laundryItem);
        if (laundryItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        LaundryItem result = laundryItemRepository.save(laundryItem);
        laundryItemSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, laundryItem.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /laundry-items} : get all the laundryItems.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of laundryItems in body.
     */
    @GetMapping("/laundry-items")
    public List<LaundryItem> getAllLaundryItems() {
        log.debug("REST request to get all LaundryItems");
        return laundryItemRepository.findAll();
    }

    /**
     * {@code GET  /laundry-items/:id} : get the "id" laundryItem.
     *
     * @param id the id of the laundryItem to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the laundryItem, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/laundry-items/{id}")
    public ResponseEntity<LaundryItem> getLaundryItem(@PathVariable Long id) {
        log.debug("REST request to get LaundryItem : {}", id);
        Optional<LaundryItem> laundryItem = laundryItemRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(laundryItem);
    }

    /**
     * {@code DELETE  /laundry-items/:id} : delete the "id" laundryItem.
     *
     * @param id the id of the laundryItem to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/laundry-items/{id}")
    public ResponseEntity<Void> deleteLaundryItem(@PathVariable Long id) {
        log.debug("REST request to delete LaundryItem : {}", id);
        laundryItemRepository.deleteById(id);
        laundryItemSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/laundry-items?query=:query} : search for the laundryItem corresponding
     * to the query.
     *
     * @param query the query of the laundryItem search.
     * @return the result of the search.
     */
    @GetMapping("/_search/laundry-items")
    public List<LaundryItem> searchLaundryItems(@RequestParam String query) {
        log.debug("REST request to search LaundryItems for query {}", query);
        return StreamSupport
            .stream(laundryItemSearchRepository.search(queryStringQuery(query)).spliterator(), false)
        .collect(Collectors.toList());
    }
}
