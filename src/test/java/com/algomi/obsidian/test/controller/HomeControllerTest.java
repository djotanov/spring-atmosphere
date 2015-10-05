package com.algomi.obsidian.test.controller;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test suite for {@link HomeController}
 *
 */
public class HomeControllerTest {
  private HomeController controller() {
    return new HomeController();
  }

  /**
   * The home action should return the index page.
   */
  @Test
  public void homeServesIndexPage() {
    assertEquals("index", controller().home());
  }

}
