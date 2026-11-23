package ec.edu.uce.library;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Architecture rules (ADR-001, ADR-003, ADR-008). Do not weaken them to make code compile.
 */
@AnalyzeClasses(packages = "ec.edu.uce.library", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule domainIsPlainJava = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..", "jakarta..", "javax.persistence..", "org.hibernate..");

    @ArchTest
    static final ArchRule applicationIsPlainJava = noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..", "jakarta..", "javax.persistence..", "org.hibernate..");

    @ArchTest
    static final ArchRule dependenciesPointInward = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Domain").definedBy("..domain..")
            .layer("Application").definedBy("..application..")
            .layer("Infrastructure").definedBy("..infrastructure..")
            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure");

    @ArchTest
    static final ArchRule contextsDoNotShareClasses = slices()
            .matching("ec.edu.uce.library.(*)..")
            .should().notDependOnEachOther();
}
