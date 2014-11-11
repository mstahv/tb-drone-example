package org.vaadin.tbtests;

import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.InitialPage;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.warp.Activity;
import org.jboss.arquillian.warp.Inspection;
import org.jboss.arquillian.warp.Warp;
import org.jboss.arquillian.warp.WarpTest;
import org.jboss.arquillian.warp.client.filter.http.HttpFilters;
import org.jboss.arquillian.warp.client.filter.http.HttpMethod;
import org.jboss.arquillian.warp.servlet.AfterServlet;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.vaadin.BookService;
import org.vaadin.entities.Book;
import org.vaadin.presentation.BookUI;

/**
 *
 */
@RunWith(Arquillian.class)
@WarpTest
@RunAsClient
public class MixingClientAndServerUsingWarpIT {

    @Deployment(testable = true)
    public static WebArchive createDeployment() {
        final PomEquippedResolveStage runtime = Maven.resolver().
                loadPomFromFile("pom.xml").importRuntimeDependencies();

        WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackage(BookUI.class.getPackage())
                .addClasses(BookService.class, Book.class)
                .addAsResource("META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsLibraries(runtime.resolve().withTransitivity().asFile());
        return war;
    }

    /*
     * tb-drone extension automatically modifies the injected WebDriver
     * to contain all TestBench goodies. So now the WebDriver has
     * all goodies of both Vaadin TestBench and Arquillian Graphene.
     * 
     * Note, as our test is using page object pattern with Graphene goodies, the 
     * WebDriver reference is not actually needed in this example.
     */
    @Drone
    WebDriver driver;

    /* 
     * In case you need to deployment url, you can get it like this. Not needed
     * in this example as using page object pattern together with Graphene.
     */
    @ArquillianResource
    private URL deploymentUrl;

    @Test
    public void testUIAndService(@InitialPage MainPage mainPage) {
        final String nameOfNewBook = "Book of Vaadin";

        Warp.initiate(new Activity() {

            EditorFragment editorFragment;

            @Override
            public void perform() {
                editorFragment = mainPage.clickNewBookButton();
                
                editorFragment.typeNameOfTheBook(nameOfNewBook + "\n"); // enter to autoclose window
 
                // Note, that no ajax savvy haxies niided. Even with implicit timeouts, 
                // this would fail without TestBench
                mainPage.assertBookInListing(nameOfNewBook);

            }

        })
         // Used as filter to only run Inspection on certain requests
         // Only do inspection afters saving, which is second XHR by vaadin 
         // "thin client"
        .observe(HttpFilters.request().method().equal(HttpMethod.POST).index(2))
        .inspect(new Inspection() {
            private static final long serialVersionUID = 1L;

            @Inject
            BookService service;

            @AfterServlet
            public void verifyFromEjb() {
                List<Book> findByTitle = service.findByTitle(nameOfNewBook);
                Assert.assertEquals(1, findByTitle.size());
            }
        });

    }

}
