package ec.edu.uce.library;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import ec.edu.uce.library.lending.domain.CopyId;
import ec.edu.uce.library.lending.domain.CopyLent;
import ec.edu.uce.library.lending.domain.CopyReturned;
import ec.edu.uce.library.lending.domain.MemberId;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.belongToAnyOf;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Checks the rules of the modular monolith (Confluence page 12, ADR-001 and ADR-003) on every build.
 */
@AnalyzeClasses(packages = "ec.edu.uce.library", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule domainIsPlainJava = classes()
            .that().resideInAPackage("..domain..")
            .should().onlyDependOnClassesThat().resideInAnyPackage("java..", "..domain..")
            .because("ADR-003: the domain has no Spring, JPA or any framework import");

    @ArchTest
    static final ArchRule dependenciesPointInward = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .withOptionalLayers(true)
            .layer("Domain").definedBy("..domain..")
            .layer("Application").definedBy("..application..")
            .layer("Infrastructure").definedBy("..infrastructure..")
            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure")
            .because("ADR-003: dependencies point inward, infrastructure -> application -> domain");

    /**
     * Lending's published language: its domain events and the identifiers they carry (Context Map, page 8).
     */
    private static final DescribedPredicate<JavaClass> LENDING_PUBLISHED_LANGUAGE =
            belongToAnyOf(CopyLent.class, CopyReturned.class, CopyId.class, MemberId.class);

    @ArchTest
    static final ArchRule contextsDoNotShareClasses = slices()
            .matching("ec.edu.uce.library.(*)..")
            .should().notDependOnEachOther()
            .ignoreDependency(resideInAPackage("..infrastructure.."), LENDING_PUBLISHED_LANGUAGE)
            .because("ADR-001: no context uses another context's classes; only an infrastructure adapter may "
                    + "listen to Lending's domain events and read the identifiers they carry");
}
