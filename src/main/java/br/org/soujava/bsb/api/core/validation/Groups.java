package br.org.soujava.bsb.api.core.validation;

/**
 * Grupos de validação para Bean Validation.
 * Permite aplicar diferentes validações para diferentes contextos.
 */
public class Groups {

    /**
     * Grupo usado para validações na criação de recursos.
     */
    public interface Create {}

    /**
     * Grupo usado para validações na atualização de recursos.
     */
    public interface Update {}
}
