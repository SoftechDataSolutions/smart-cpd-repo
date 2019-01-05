package io.github.softech.dev.sgill.web.rest;

import com.codahale.metrics.annotation.Timed;
import io.github.softech.dev.sgill.domain.Customer;
import io.github.softech.dev.sgill.domain.Section;
import io.github.softech.dev.sgill.domain.SectionHistory;
import io.github.softech.dev.sgill.repository.CustomerRepository;
import io.github.softech.dev.sgill.repository.SectionHistoryRepository;
import io.github.softech.dev.sgill.service.CustomerService;
import io.github.softech.dev.sgill.service.SectionHistoryService;
import io.github.softech.dev.sgill.service.SectionService;
import io.github.softech.dev.sgill.web.rest.errors.BadRequestAlertException;
import io.github.softech.dev.sgill.web.rest.util.HeaderUtil;
import io.github.softech.dev.sgill.web.rest.util.PaginationUtil;
import io.github.softech.dev.sgill.service.dto.SectionHistoryCriteria;
import io.github.softech.dev.sgill.service.SectionHistoryQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing SectionHistory.
 */
@RestController
@RequestMapping("/api")
public class SectionHistoryResource {

    private final Logger log = LoggerFactory.getLogger(SectionHistoryResource.class);

    private static final String ENTITY_NAME = "sectionHistory";

    private final SectionHistoryService sectionHistoryService;

    private final SectionHistoryQueryService sectionHistoryQueryService;

    private final SectionHistoryRepository sectionHistoryRepository;

    private final CustomerService customerService;

    private final SectionService sectionService;

    public SectionHistoryResource(SectionHistoryService sectionHistoryService, SectionHistoryQueryService sectionHistoryQueryService,
                                  SectionHistoryRepository sectionHistoryRepository, CustomerService customerService, SectionService sectionService) {
        this.sectionHistoryService = sectionHistoryService;
        this.sectionHistoryQueryService = sectionHistoryQueryService;
        this.sectionHistoryRepository = sectionHistoryRepository;
        this.customerService = customerService;
        this.sectionService = sectionService;
    }

    /**
     * POST  /section-histories : Create a new sectionHistory.
     *
     * @param sectionHistory the sectionHistory to create
     * @return the ResponseEntity with status 201 (Created) and with body the new sectionHistory, or with status 400 (Bad Request) if the sectionHistory has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/section-histories")
    @Timed
    public ResponseEntity<SectionHistory> createSectionHistory(@RequestBody SectionHistory sectionHistory) throws URISyntaxException {
        log.debug("REST request to save SectionHistory : {}", sectionHistory);
        if (sectionHistory.getId() != null) {
            throw new BadRequestAlertException("A new sectionHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SectionHistory result = sectionHistoryService.save(sectionHistory);
        return ResponseEntity.created(new URI("/api/section-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /section-histories : Updates an existing sectionHistory.
     *
     * @param sectionHistory the sectionHistory to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated sectionHistory,
     * or with status 400 (Bad Request) if the sectionHistory is not valid,
     * or with status 500 (Internal Server Error) if the sectionHistory couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/section-histories")
    @Timed
    public ResponseEntity<SectionHistory> updateSectionHistory(@RequestBody SectionHistory sectionHistory) throws URISyntaxException {
        log.debug("REST request to update SectionHistory : {}", sectionHistory);
        if (sectionHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        SectionHistory result = sectionHistoryService.save(sectionHistory);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, sectionHistory.getId().toString()))
            .body(result);
    }

    /**
     * GET  /section-histories : get all the sectionHistories.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of sectionHistories in body
     */
    @GetMapping("/section-histories")
    @Timed
    public ResponseEntity<List<SectionHistory>> getAllSectionHistories(SectionHistoryCriteria criteria, Pageable pageable) {
        log.debug("REST request to get SectionHistories by criteria: {}", criteria);
        Page<SectionHistory> page = sectionHistoryQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/section-histories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{courseId}/section-histories/{customerId}")
    @Timed
    public ResponseEntity<SectionHistory> getLastCourseSectionHistories(@PathVariable Long courseId, @PathVariable Long customerId) {
        log.debug("REST request to get most recent Section by Course ID: {}", courseId);
        log.debug("REST request to get most recent Section by Customer ID: {}", customerId);
        return ResponseUtil.wrapOrNotFound(sectionHistoryRepository.getSectionByCourse(customerId, courseId));
    }

    @GetMapping("/{sectionId}/section-history/{customerId}")
    @Timed
    public ResponseEntity<SectionHistory> getByCustomerSectionHistories(@PathVariable Long sectionId, @PathVariable Long customerId) {
        log.debug("REST request to get most recent Section by Course ID: {}", sectionId);
        log.debug("REST request to get most recent Section by Customer ID: {}", customerId);
        return ResponseUtil.wrapOrNotFound(sectionHistoryRepository.getSectionHistoryByCustomerIdAndSectionId(customerId, sectionId));
    }

    /**
     * GET  /section-histories/:id : get the "id" sectionHistory.
     *
     * @param id the id of the sectionHistory to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the sectionHistory, or with status 404 (Not Found)
     */
    @GetMapping("/section-histories/{id}")
    @Timed
    public ResponseEntity<SectionHistory> getSectionHistory(@PathVariable Long id) {
        log.debug("REST request to get SectionHistory : {}", id);
        Optional<SectionHistory> sectionHistory = sectionHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sectionHistory);
    }

    @GetMapping("/customer/section-histories/{customerid}")
    @Timed
    public ResponseEntity<List<SectionHistory>> getCustomerSectionHistories(@PathVariable Long customerid) {
        log.debug("REST request to get SectionHistories by customer : {}", customerid);
        Customer reqdCustomer = customerService.findOne(customerid).orElse(null);
        return ResponseUtil.wrapOrNotFound(sectionHistoryRepository.getSectionHistoriesByCustomer(reqdCustomer));
    }

    @GetMapping("/recent/section-history/{customerid}")
    @Timed
    public ResponseEntity<Section> getRecentSectionHistory(@PathVariable Long customerid) {
        log.debug("REST request to get recent Section by customer : {}", customerid);
        SectionHistory temp = sectionHistoryRepository.getRecentSectionHistory(customerid).orElse(null);
        Optional<Section> tempSection = null;
        if (temp != null) {
            tempSection = sectionService.findOne(temp.getSection().getId());
        }
        return ResponseUtil.wrapOrNotFound(tempSection);
    }

    @GetMapping("/customer/{customerid}/section-history/{courseid}")
    @Timed
    public ResponseEntity<SectionHistory> getPersistanceSectionHistory(@PathVariable Long customerid, @PathVariable Long courseid) {
        log.debug("REST request to get SectionHistory by customer and Section : {}", customerid);
        return ResponseUtil.wrapOrNotFound(sectionHistoryRepository.getPersistanceSectionHistory(customerid, courseid));
    }

    /**
     * DELETE  /section-histories/:id : delete the "id" sectionHistory.
     *
     * @param id the id of the sectionHistory to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/section-histories/{id}")
    @Timed
    public ResponseEntity<Void> deleteSectionHistory(@PathVariable Long id) {
        log.debug("REST request to delete SectionHistory : {}", id);
        sectionHistoryService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/section-histories?query=:query : search for the sectionHistory corresponding
     * to the query.
     *
     * @param query the query of the sectionHistory search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/section-histories")
    @Timed
    public ResponseEntity<List<SectionHistory>> searchSectionHistories(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of SectionHistories for query {}", query);
        Page<SectionHistory> page = sectionHistoryService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/section-histories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
