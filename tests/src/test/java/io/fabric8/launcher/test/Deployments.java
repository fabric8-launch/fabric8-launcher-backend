package io.fabric8.launcher.test;

import java.io.File;

import io.fabric8.launcher.service.github.api.GitHubService;
import io.fabric8.launcher.service.github.api.GitHubServiceFactory;
import io.fabric8.launcher.service.github.impl.kohsuke.KohsukeGitHubServiceImpl;
import io.fabric8.launcher.service.github.spi.GitHubServiceSpi;
import io.fabric8.launcher.service.openshift.api.OpenShiftServiceFactory;
import io.fabric8.launcher.service.openshift.spi.OpenShiftServiceSpi;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.Resolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import io.fabric8.launcher.service.openshift.impl.OpenShiftProjectImpl;

/**
 * Obtains deployments for shared use in tests
 *
 * @author <a href="mailto:alr@redhat.com">Andrew Lee Rubinger</a>
 */
class Deployments {

    private Deployments() {
        // No instances
    }

    static WebArchive getMavenBuiltWar() {
        final WebArchive webArchive = ShrinkWrap.createFromZipFile(
                WebArchive.class,
                new File("../web/target/launcher-backend.war"));
        return webArchive;
    }

    /**
     * Test hooks so we can do some cleanupCreatedProject
     *
     * @return
     */
    static WebArchive getTestDeployment() {
        final WebArchive archive = ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(
                        true,
                        OpenShiftServiceSpi.class.getPackage(),
                        OpenShiftProjectImpl.class.getPackage(),
                        OpenShiftServiceFactory.class.getPackage(),
                        GitHubServiceSpi.class.getPackage(),
                        KohsukeGitHubServiceImpl.class.getPackage(),
                        GitHubServiceFactory.class.getPackage())
                .addPackage(TestSupport.class.getPackage());
        final File[] depsOpenshift = Resolvers.use(MavenResolverSystem.class).
                loadPomFromFile("../services/openshift-service-impl/pom.xml").
                importRuntimeAndTestDependencies().
                resolve().
                withTransitivity().
                asFile();
        final File[] depsGithub = Resolvers.use(MavenResolverSystem.class).
                loadPomFromFile("../services/github-service-impl/pom.xml").
                importRuntimeAndTestDependencies().
                resolve().
                withTransitivity().
                asFile();

        final File[] ourTestDeps = Resolvers.use(MavenResolverSystem.class).
                loadPomFromFile("pom.xml").
                importTestDependencies().
                resolve().
                withTransitivity().
                asFile();
        archive.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        archive.addAsLibraries(depsOpenshift);
        archive.addAsLibraries(depsGithub);
        archive.addAsLibraries(ourTestDeps);
        return archive;
    }
}