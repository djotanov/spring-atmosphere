package com.algomi.obsidian.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test suite for {@link Charsets}
 *
 */
public class CharsetsTest {

  @Test
  public void validateCharsetNameForUtf8() {
    assertEquals("UTF-8", Charsets.UTF_8);
  }

}
