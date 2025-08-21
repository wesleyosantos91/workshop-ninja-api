package br.org.soujava.bsb.api.domain.service;

import static br.org.soujava.bsb.api.core.mapper.NinjaMapper.MAPPER;
import static java.text.MessageFormat.format;

import br.org.soujava.bsb.api.api.v1.request.NinjaQueryRequest;
import br.org.soujava.bsb.api.api.v1.request.NinjaRequest;
import br.org.soujava.bsb.api.domain.entity.NinjaEntity;
import br.org.soujava.bsb.api.domain.exception.ResourceNotFoundException;
import br.org.soujava.bsb.api.domain.repository.NinjaRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NinjaService {

    private final NinjaRepository respository;

    public NinjaService(NinjaRepository respository) {
        this.respository = respository;
    }

    @Transactional
    public NinjaEntity create(NinjaRequest ninjaRequest) {
        return respository.save(MAPPER.toEntity(ninjaRequest));
    }

    @Transactional(readOnly = true)
    public NinjaEntity findById(Integer id) throws ResourceNotFoundException {
        return respository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(format("Not found regitstry with code {0}", id)));
    }

    @Transactional(readOnly = true)
    public Page<NinjaEntity> search(NinjaQueryRequest queryRequest, Pageable pageable) {

        final var ninjaEntityExample = Example.of(MAPPER.toEntity(queryRequest));
        return respository.findAll(ninjaEntityExample, pageable);
    }

    @Transactional
    public NinjaEntity update(Integer id, NinjaRequest request) throws ResourceNotFoundException {
        final var ninja = MAPPER.toEntity(request, findById(id));
        return respository.save(ninja);
    }

    @Transactional
    public void delete(Integer id) throws ResourceNotFoundException {
        final var ninjaEntity = findById(id);
        respository.delete(ninjaEntity);
    }

}
