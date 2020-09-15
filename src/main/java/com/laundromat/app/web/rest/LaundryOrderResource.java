package com.laundromat.app.web.rest;

import com.laundromat.app.domain.LaundryOrder;
import com.laundromat.app.service.LaundryOrderService;
import com.laundromat.app.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.laundromat.app.domain.LaundryOrder}.
 */
@RestController
@RequestMapping("/api")
public class LaundryOrderResource {

    private final Logger log = LoggerFactory.getLogger(LaundryOrderResource.class);

    private static final String ENTITY_NAME = "laundryOrder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LaundryOrderService laundryOrderService;

    public LaundryOrderResource(LaundryOrderService laundryOrderService) {
        this.laundryOrderService = laundryOrderService;
    }

    /**
     * {@code POST  /laundry-orders} : Create a new laundryOrder.
     *
     * @param laundryOrder the laundryOrder to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new laundryOrder, or with status {@code 400 (Bad Request)} if the laundryOrder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/laundry-orders")
    public ResponseEntity<LaundryOrder> createLaundryOrder(@RequestBody LaundryOrder laundryOrder) throws URISyntaxException {
        log.debug("REST request to save LaundryOrder : {}", laundryOrder);
        if (laundryOrder.getId() != null) {
            throw new BadRequestAlertException("A new laundryOrder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LaundryOrder result = laundryOrderService.save(laundryOrder);
        return ResponseEntity.created(new URI("/api/laundry-orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /laundry-orders} : Updates an existing laundryOrder.
     *
     * @param laundryOrder the laundryOrder to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated laundryOrder,
     * or with status {@code 400 (Bad Request)} if the laundryOrder is not valid,
     * or with status {@code 500 (Internal Server Error)} if the laundryOrder couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/laundry-orders")
    public ResponseEntity<LaundryOrder> updateLaundryOrder(@RequestBody LaundryOrder laundryOrder) throws URISyntaxException {
        log.debug("REST request to update LaundryOrder : {}", laundryOrder);
        if (laundryOrder.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        LaundryOrder result = laundryOrderService.save(laundryOrder);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, laundryOrder.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /laundry-orders} : get all the laundryOrders.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of laundryOrders in body.
     */
    @GetMapping("/laundry-orders")
    public List<LaundryOrder> getAllLaundryOrders() {
        log.debug("REST request to get all LaundryOrders");
        return laundryOrderService.findAll();
    }

    /**
     * {@code GET  /laundry-orders/:id} : get the "id" laundryOrder.
     *
     * @param id the id of the laundryOrder to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the laundryOrder, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/laundry-orders/{id}")
    public ResponseEntity<LaundryOrder> getLaundryOrder(@PathVariable Long id) {
        log.debug("REST request to get LaundryOrder : {}", id);
        Optional<LaundryOrder> laundryOrder = laundryOrderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(laundryOrder);
    }

    /**
     * {@code DELETE  /laundry-orders/:id} : delete the "id" laundryOrder.
     *
     * @param id the id of the laundryOrder to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/laundry-orders/{id}")
    public ResponseEntity<Void> deleteLaundryOrder(@PathVariable Long id) {
        log.debug("REST request to delete LaundryOrder : {}", id);
        laundryOrderService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/laundry-orders?query=:query} : search for the laundryOrder corresponding
     * to the query.
     *
     * @param query the query of the laundryOrder search.
     * @return the result of the search.
     */
    @GetMapping("/_search/laundry-orders")
    public List<LaundryOrder> searchLaundryOrders(@RequestParam String query) {
        log.debug("REST request to search LaundryOrders for query {}", query);
        return laundryOrderService.search(query);
    }
}
