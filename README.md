# Usage example of Arquillian (Drone, Warp) together with Vaadin TestBench

This is an example setup how you can use two cool WebDriver based testing tools, Arquillian Drone and Vaadin TestBench, together to make your browser level tests easier to write and maintain. Drone stuff add superb helpers to implement Page Object pattern and TestBench understands Vaadin communication better than anything else.

Also you'll gain all the goodies of basic Arquillian stuff as well and by using Arquillian Warp, you can pretty much fade away the border between client and the server in you integration tests. E.g. in the MixingClientAndServerUsingWarpIT example test, we first add a book into database via Vaadin UI and EJB and then we can, in the very same test, verify the book really exists in the backend by asserting that directly from the EJB.

*How cool is that!?*

### TODO: instructions & blog post
