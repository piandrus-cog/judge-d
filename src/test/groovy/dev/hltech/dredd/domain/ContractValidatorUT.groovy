package dev.hltech.dredd.domain

import dev.hltech.dredd.domain.environment.Environment
import dev.hltech.dredd.domain.environment.MockServiceDiscovery
import spock.lang.Specification

import static au.com.dius.pact.model.PactReader.loadPact
import static com.google.common.io.ByteStreams.toByteArray

class ContractValidatorUT extends Specification {

    private ContractValidator verifier

    void setup() {
        def environment = new Environment(MockServiceDiscovery.builder()
            .withProvider(
            "backend-provider",
            new String(toByteArray(getClass().getResourceAsStream("/backend-provider-swagger.json"))))
            .build())
        verifier = new ContractValidator(environment)
    }

    def 'should verify all interactions from pact file'() {
        given:
            def pact = loadPact(getClass().getResourceAsStream("/pact-frontend-to-backend-provider.json"))
        when:
            def validationReports = verifier.validate(pact)
        then:
            with(validationReports.get(0)) {
                status == ValidationStatus.FAILED
                name.equalsIgnoreCase("a request for details")
                errors.size() == 2
            }
    }

}
