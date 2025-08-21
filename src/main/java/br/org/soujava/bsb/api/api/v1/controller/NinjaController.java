package br.org.soujava.bsb.api.api.v1.controller;

import static br.org.soujava.bsb.api.core.mapper.NinjaMapper.MAPPER;

import br.org.soujava.bsb.api.api.v1.request.NinjaQueryRequest;
import br.org.soujava.bsb.api.api.v1.request.NinjaRequest;
import br.org.soujava.bsb.api.api.v1.response.NinjaResponse;
import br.org.soujava.bsb.api.core.validation.Groups;
import br.org.soujava.bsb.api.domain.exception.ResourceNotFoundException;
import br.org.soujava.bsb.api.domain.service.NinjaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/ninjas")
public record NinjaController(NinjaService service) {

    private static final Logger LOGGER = LoggerFactory.getLogger(NinjaController.class);

    @PostMapping
    public ResponseEntity<NinjaResponse> create(@Validated(Groups.Create.class) @RequestBody NinjaRequest request) {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.debug("Function started 'create ninja'");
        final var response = MAPPER.toResponse(service.create(request));
        stopWatch.stop();
        LOGGER.debug("finished function with sucess 'create ninja {}' in {} ms", response, stopWatch.getTotalTimeMillis());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<NinjaResponse> getById(@PathVariable Integer id) throws ResourceNotFoundException {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.debug("Function started 'getById ninja' with id {}", id);
        final var ninja = service.findById(id);
        final var response = MAPPER.toResponse(ninja);
        stopWatch.stop();
        LOGGER.debug("finished function with sucess 'getById ninja' {} in {} ms", response, stopWatch.getTotalTimeMillis());
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<PagedModel<NinjaResponse>> search(@ModelAttribute NinjaQueryRequest query, Pageable page) {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.info("Function started 'find ninja'");
        final var pageEntity = service.search(query, page);
        stopWatch.stop();
        LOGGER.info("finished function with ninja 'find person' in {} ms", stopWatch.getTotalTimeMillis());

        return ResponseEntity.ok().body(new PagedModel<>(MAPPER.toPageResponse(pageEntity)));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<NinjaResponse> update(@PathVariable Integer id,
                                                @Validated(Groups.Update.class)
                                                @RequestBody NinjaRequest request) throws ResourceNotFoundException {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.debug("Function started 'update ninja'");
        final var ninja = service.update(id, request);
        stopWatch.stop();
        LOGGER.debug("finished function with sucess 'update ninja' {} in {} ms", ninja, stopWatch.getTotalTimeMillis());
        return ResponseEntity.status(HttpStatus.OK).body(MAPPER.toResponse(ninja));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) throws ResourceNotFoundException {
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        LOGGER.debug("Function started 'delete ninja' with id {}", id);
        service.delete(id);
        stopWatch.stop();
        LOGGER.debug("finished function with sucess 'delete person' in {} ms", stopWatch.getTotalTimeMillis());

        return ResponseEntity.noContent().build();
    }
}
