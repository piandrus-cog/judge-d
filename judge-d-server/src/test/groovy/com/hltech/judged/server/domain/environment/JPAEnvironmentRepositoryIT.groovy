package com.hltech.judged.server.domain.environment

import com.hltech.judged.server.domain.ServiceVersion
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import static EnvironmentAggregate.builder
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT, properties = ["management.port=0"])
@ActiveProfiles("test-integration")
class JPAEnvironmentRepositoryIT extends Specification {

    @Autowired
    JPAEnvironmentRepository repository

    def 'should retrieve what was persisted'() {
        given:
            def serviceVersion = [new ServiceVersion('serviceName', 'serviceVersion')] as Set
        and:
            def environment = new EnvironmentAggregate('environmentName', serviceVersion)
        when:
            def persisted = repository.persist(environment)
        then:
            repository.get(environment.name).with {
                name == environment.name
                allServices.with {
                    name == serviceVersion.name
                    version == serviceVersion.version
                }
            }
        and:
            noExceptionThrown()
    }

    def 'should overwrite what was persisted before'() {
        given:
            def environment1 = repository.persist(new EnvironmentAggregate(
                'environmentName',
                [new ServiceVersion('serviceName1', 'serviceVersion1'), new ServiceVersion('serviceName2', 'serviceVersion2')] as Set
            ))
            def environment2 = new EnvironmentAggregate(
                environment1.name,
                [new ServiceVersion('serviceName1', 'serviceVersion1')] as Set
            )
        when:
            repository.persist(environment2)
        then:
            repository.get(environment1.name).with {
                name == environment1.name
                allServices.size() == 1
            }
    }

    def 'should retrieve names'() {
        given:
            def environment1 = repository.persist(new EnvironmentAggregate(
                randomAlphabetic(10),
                [new ServiceVersion('serviceName1', 'serviceVersion1'), new ServiceVersion('serviceName2', 'serviceVersion2')] as Set
            ))
        when:
            def names = repository.getNames()
        then:
            names.contains(environment1.name)
    }

    def 'should retrieve service versions of all spaces'(){
        given:
            def environment1 = builder(randomAlphabetic(10))
                .withServiceVersion("s1", "v1")
                .withServiceVersions("space", [new ServiceVersion("s2", "v2")] as Set)
                .build();
        when:
            repository.persist(environment1);
        then:
            repository.get(environment1.name).with {
                name == environment1.name
                allServices.size() == 2
            }
    }
}
